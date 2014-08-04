package com.gamelanlabs.chimple2.core;

import java.util.Arrays;
import java.util.LinkedList;

import com.gamelanlabs.chimple2.monkeys.ChimpBeta;
import com.gamelanlabs.chimple2.monkeys.ChimpDirichlet;
import com.gamelanlabs.chimple2.monkeys.ChimpDiscrete;
import com.gamelanlabs.chimple2.monkeys.ChimpFlip;
import com.gamelanlabs.chimple2.monkeys.ChimpNormal;
import com.gamelanlabs.chimple2.monkeys.ChimpPermutation;
import com.gamelanlabs.chimple2.monkeys.ChimpPoisson;
import com.gamelanlabs.chimple2.monkeys.ChimpRand;
import com.gamelanlabs.chimple2.solvers.MetropolisHastingsSolver;

public abstract class ChimpleProgram implements Cloneable {
	/**
	 * The zookeeper is unique to this program -- even when a ChimpleProgram is
	 * cloned they do not share a zookeeper.
	 * 
	 * This is generally the only Zookeeper instance that should ever exist
	 * (the only exception is if you are running multiple solvers in parallel).
	 */
	public Zookeeper zookeeper;
	
	/**
	 * The solver may clone() and run multiple instances of your ChimpleProgram
	 * in parallel, in which case it may write a solver-specific tag to each
	 * program.
	 */
	public String tag = null;

	/**
	 * To write a Chimple program, extend this class and implement the probabilistic
	 * program inside this function.
	 * 
	 * @param	args	Static parameters that the solver will pass to your probabilistic
	 * 					program every time it's run.
	 * @return	results Results that the solver will collect on every iteration.
	 */
	public abstract Object run(Object ... args);
	
	/**
	 * Perform cleanup (ie. close any GUI windows this program created, etc).
	 */
	public void cleanup() { }
	
	/**
	 * You may also write a main() function in the same class -- there is no need to use
	 * a separate harness class.
	 */
	
	
	/************************************************************
	 * The following methods implement the Cloneable interface. *
	 ************************************************************/
	
	/**
	 * Implements Cloneable interface.
	 * 
	 * @return	clone
	 */
	@Override
	public ChimpleProgram clone() {
		try {
			ChimpleProgram p = (ChimpleProgram) super.clone();
			
			// Wipe the zookeeper
			p.zookeeper = null;
			return p;
		} catch (CloneNotSupportedException e) {
			// No reason this should ever happen
			throw new RuntimeException(e);
		}
	}
	
	
	/************************************************************
	 * The following methods are wrappers for Monkey creation   *
	 * internal cost function definition, and other convenience *
	 * functions. They are placed in ChimpleProgram for ease of *
	 * use. You don't need to implement them if you are writing *
	 * a probabilistic program. Instead, you will call them     *
	 * from within the run() method you write.                  *
	 *                                                          *
	 * If you create a new Monkey, you should add a wrapper     *
	 * here.                                                    *
	 ************************************************************/
	
	/**
	 * Specify an internal cost function, from within the run()
	 * method of a ChimpleProgram.
	 * 
	 * @param	energy	How much energy to add to this trace
	 */
	
	public void addEnergy(double energy) {
		zookeeper.cage.internalenergy += energy;
	}
	
	private static LinkedList<Long> timers = new LinkedList<Long>();
	
	/**
	 * Start a timer. Timers are stored on a stack, they may be nested.
	 */
	public static void tic() {
		timers.push(System.nanoTime());
	}
	
	/**
	 * Stop a timer.
	 * 
	 * @return	elapsed		Time elapsed since tic() was called
	 */
	public static double toc() {
		long elapsed = timers.pop() - System.nanoTime();
		return -((double) elapsed)/1000000000;
	}
	
	/**
	 * Stop and print a timer, prefixed with the passed description.
	 * For example, if you pass "Iteration 0: ", then the output will
	 * be "Iteration 0: 0.41238 seconds."
	 * 
	 * @param	desc	Prefix for output
	 */
	public static void tocPrint(String desc) {
		System.out.printf("%s%f seconds\n", desc, toc());
	}
	
	public static void tocPrint() {
		tocPrint("Elapsed: ");
	}
	
	/**
	 * Trigger the solver's end condition, if it has one (solver-
	 * dependent).
	 */
	public void triggerEndCondition() {
		zookeeper.end = true;
	}
	
	/**
	 * Convenience function for running traceMH. Call this in your
	 * main() function (the harness), not your run() function (the
	 * probabilistic program)!
	 * 
	 * @param	burnin
	 * @param	samples
	 * @param	spacing
	 * @param	program
	 * @param	args
	 * @param	cf
	 * @return	results
	 */
	public static Object[] MHQuery(int burnin, int samples, int spacing,
			ChimpleProgram program, Object[] args, CostFunction cf) {
		MetropolisHastingsSolver s = new MetropolisHastingsSolver(program, args, cf);
		s.solve(burnin, samples, spacing);
		return s.getResults().toArray();
	}
	
	public static Object[] MHQuery(int burnin, int samples, int spacing,
			ChimpleProgram program, Object[] args) {
		return MHQuery(burnin, samples, spacing, program, args, null);
	}
	
	public static Object[] MHQuery(int burnin, int samples, int spacing,
			ChimpleProgram program) {
		return MHQuery(burnin, samples, spacing, program, new Object[] {}, null);
	}
	
	
	/******************************* MONKEYS ******************************/
	
	/**
	 * Bernoulli(p) ERP.
	 * 
	 * @param	name
	 * @param	p
	 * @return	value
	 */
	public int chimpFlip(String name, double p) {
		ChimpFlip m;
		
		try {
			m = (ChimpFlip) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpFlip(zookeeper, p);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		if(p != m.weight) {
			// An upstream monkey was regenerated and
			// the parameters for this monkey changed.
			// We should generate from the prior here.
			m.weight = p;
			m.generate();
		}
		
		return m.getValue();
	}

	/**
	 * Uniform(0, 1) ERP.
	 * 
	 * @param	name
	 * @return	value
	 */
	public double chimpRand(String name) {
		ChimpRand m;
		
		try {
			m = (ChimpRand) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpRand(zookeeper);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		return m.getValue();
	}
	
	/**
	 * Beta(alpha, beta) ERP.
	 * 
	 * @param	name
	 * @param	alpha
	 * @param	beta
	 * @return	value
	 */
	public double chimpBeta(String name, double alpha, double beta) {
		ChimpBeta m;
		
		try {
			m = (ChimpBeta) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpBeta(zookeeper, alpha, beta);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		if(alpha != m.alphas[0] || beta != m.alphas[1]) {
			// An upstream monkey was regenerated and
			// the parameters for this monkey changed.
			// We should generate from the prior here.
			m.alphas = new double[] {alpha, beta};
			m.generate();
		}
		
		return m.getValue();
	}

	/**
	 * Dirichlet(alpha[0], ...) ERP.
	 * 
	 * @param	name
	 * @param	alphas
	 * @return	value
	 */
	public double[] chimpDirichlet(String name, double[] alphas) {
		ChimpDirichlet m;
		
		try {
			m = (ChimpDirichlet) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpDirichlet(zookeeper, alphas);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		if(!Arrays.equals(alphas, m.alphas)) {
			// An upstream monkey was regenerated and
			// the parameters for this monkey changed.
			// We should generate from the prior here.
			m.alphas = alphas;
			m.generate();
		}
		
		return m.getValue().clone();
	}

	/**
	 * Categorical(probs[0], ...) ERP.
	 * 
	 * @param	name
	 * @param	probs
	 * @return	value
	 */
	public int chimpDiscrete(String name, double[] probs) {
		ChimpDiscrete m;
		
		try {
			m = (ChimpDiscrete) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpDiscrete(zookeeper, probs);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		if(!Arrays.equals(probs, m.probs)) {
			// An upstream monkey was regenerated and
			// the parameters for this monkey changed.
			// We should generate from the prior here.
			m.probs = probs;
			m.generate();
		}
		
		return m.getValue();
	}

	/**
	 * N(mean, variance) ERP with N(value, walk_variance) proposal kernel.
	 * 
	 * @param	name
	 * @param	mean
	 * @param	variance
	 * @param	walk_variance
	 * @return	value
	 */
	public double chimpNormal(String name, double mean, double variance, double walk_variance) {
		ChimpNormal m;
		try{
			m = (ChimpNormal) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpNormal(zookeeper, mean, variance, walk_variance);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		if(mean != m.mu || variance != m.sigma || walk_variance != m.walk_sigma) {
			// An upstream monkey was regenerated and
			// the parameters for this monkey changed.
			// We should generate from the prior here.
			m.mu = mean;
			m.sigma = variance;
			m.walk_sigma = walk_variance;
			m.generate();
		}
		
		return m.getValue();
	}

	/**
	 * N(mean, variance) ERP with N(value, variance/10) proposal kernel.
	 * 
	 * @param	name
	 * @param	mean
	 * @param	variance
	 * @return	value
	 */
	public double chimpNormal(String name, double mean, double variance) {
		// Default proposal kernel (random walk) variance is variance/10.
		return chimpNormal(name, mean, variance, variance/10);
	}

	/**
	 * Poisson(lambda) ERP with Bactrian(jump) proposal kernel.
	 * 
	 * @param	name
	 * @param	lambda
	 * @param	jump
	 * @return	value
	 */
	public int chimpPoisson(String name, double lambda, double jump) {
		ChimpPoisson m;
		
		try {
			m = (ChimpPoisson) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpPoisson(zookeeper, lambda, jump);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		if(lambda != m.lambda || jump != m.jump) {
			// An upstream monkey was regenerated and
			// the parameters for this monkey changed.
			// We should generate from the prior here.
			m.lambda = lambda;
			m.jump = jump;
			m.generate();
		}
		
		return m.getValue();
	}

	/**
	 * Permutation ERP with swap proposal kernel.
	 * 
	 * @param	name
	 * @param	n
	 * @return	value
	 */
	public int[] chimpPermutation(String name, int n) {
		ChimpPermutation m;
		
		try {
			m = (ChimpPermutation) zookeeper.cage.get(name);
		} catch(ClassCastException e) {
			m = null;
			zookeeper.cage.del(zookeeper.cage.get(name));
		}
		
		if(m == null) {
			m = new ChimpPermutation(zookeeper, n);
			zookeeper.cage.add(name, m);
			m.generate();
		}
		
		m.touched = true;
		
		if(n != m.n) {
			// An upstream monkey was regenerated and
			// the parameters for this monkey changed.
			// We should generate from the prior here.
			m.n = n;
			m.generate();
		}
		
		return m.getValue().clone();
	}
}
