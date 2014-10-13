package com.gamelanlabs.chimple2.solvers;

import java.util.ArrayList;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.core.MonkeyCage;
import com.gamelanlabs.chimple2.core.MonkeyFactory;
import com.gamelanlabs.chimple2.core.Query;
import com.gamelanlabs.chimple2.core.Zookeeper;
import com.gamelanlabs.chimple2.monkeys.Monkey;

/**
 * All solvers should extend this base class.
 *
 * This class describes a probabilistic program solver. If you
 * are looking into implementing a model-specific solver, you
 * should look at the Query interface instead (see LDAQuery for
 * an implementation example).
 *
 * @author BenL
 *
 */
public abstract class Solver implements Query {
	public ChimpleProgram program;
	public Object[] arguments;
	public CostFunction costfunction;
	public Zookeeper zookeeper = new Zookeeper();
	protected final ArrayList<Object> results;

	/**
	 * Constructor
	 *
	 * @param	p	ChimpleProgram whose posterior will be sampled
	 * @param	a	Arguments to the program
	 * @param	cf	Cost function
	 */
	public Solver(ChimpleProgram p, Object[] a, CostFunction cf) {
		program = p;
		arguments = a;
		costfunction = cf;
		p.zookeeper = zookeeper;
		p.factory = makeMonkeyFactory();
		results = new ArrayList<Object>();
	}

	/**
	 * Returns the MonkeyFactory that this solver wants to give the
	 * ChimpleProgram.
	 *
	 * @return	factory
	 */
	protected abstract MonkeyFactory makeMonkeyFactory();

	/**
	 * Computes the energy of a state.
	 *
	 * @param	result	A result set returned from the probabilistic program.
	 * @param	cage	The trace that the probabilistic program went through.
	 * @return	energy	The energy of this state.
	 */
	public double energy(Object result, MonkeyCage cage) {
		double energy = 0;

		// Gather energy from the external cost function
		if(costfunction != null) {
			energy += costfunction.call(result, cage);
		}

		// Gather energy from internal cost functions
		energy += cage.internalenergy;

		// Gather energies from the monkeys
		for(Monkey<?> m : cage.getList()) {
			energy += m.energy();
		}

		return energy;
	}

	/**
	 * Saves a resultset.
	 *
	 * @param	result		Resultset to save.
	 */
	public void save(Object result) {
		results.add(result);
	}

	/**
	 * Returns resultsets.
	 *
	 * @return	results		All saved resultsets
	 */
	public ArrayList<Object> getResults() {
		return results;
	}

	/**
	 * Returns resultsets as an Object (in order to implement QueryInterface).
	 *
	 * @return	results		All saved resultsets.
	 */
	@Override
	public Object get() {
		return results;
	}

	/**
	 * The solver is invoked by calling this method. This abstract class requires
	 * that all subclasses must implement a no-argument version (ie. with default
	 * values for burnin, samples, etc.), but generally a with-arguments version
	 * will be called.
	 */
	public abstract void solve();

	/**
	 * Return the names of arguments to solve() that this solver takes.
	 *
	 * @return	names			Friendly names of arguments
	 */
	public static String[] getArgumentNames() {
		return new String[] {};
	}

	/**
	 * Return default arguments to solve() that this solver takes.
	 *
	 * @return	defaultargs		Default arguments
	 */
	public static Object[] getDefaultArguments() {
		return new Object[] {};
	}

	/**
	 * Perform cleanup: destroy all objects created by this solver
	 * (but not the objects that were passed in).
	 */
	public void cleanup() {
		program.zookeeper = null;
	}
}

