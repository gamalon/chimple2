package com.gamelanlabs.chimple2.demos;

import java.util.ArrayList;

import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.core.MonkeyCage;
import com.gamelanlabs.chimple2.monkeys.ChimpRand;
import com.gamelanlabs.chimple2.solvers.MetropolisHastingsSolver;
import com.gamelanlabs.chimple2.solvers.Solver;

/**
 * This probabilistic program generates a random weight
 * uniformly (ie. with a prior Beta(1,1)) and flips a
 * coin 20 times.
 * 
 * With BetaBinCostFunction, we condition on the fact
 * that previously, we observed 15 heads and 5 tails.
 * 
 * The closed-form posterior is the distribution
 * BetaBin(16, 6, 20). (A function that calculates the BetaBin
 * PMF is included in CompareSamplers.)
 *  
 * @author BenL
 *
 */

/*
In Church:
	(define (program) (
	  mh-query 1000 10
	  (define weight (uniform 0 1))
	  (define doflip (lambda () (if (flip weight) 1 0)))
	  (define numheads (apply + (repeat 20 doflip)))
	  numheads
	                   
	  (condition (= (apply + (repeat 20 doflip)) 15))
	))
	
	(hist (program) "Random coin")
*/

public class RandomCoin extends Demo
{
	public static final int num_flips = 20; // Each observation is 20 flips.
	public static final int sum_val = 15; // We observed sum2 == 15.

	/**
	 * This is the probabilistic program.
	 * 
	 * @param	args	A one-element Object[] that contains
	 * 					the actual number of heads.
	 * @return	r		A two-element int[] that contains the
	 * 					generated number of heads, along with
	 * 					the actual number of heads. Both
	 * 					pieces of information are needed by
	 * 					the cost function.
	 */
	@Override
	public Object run(Object... args) 
	{
		// Generate a random weight for the coin
		double weight = chimpRand("weight");
		
		// Flip the coin 20 times and count the number of heads
		int sumout = 0;
		for (int i = 0; i < num_flips; i++) {
			sumout += chimpFlip("X" + i, weight);
		}

		// Return the generated number of heads
		return new int[] {sumout};
	}
	
	/**
	 * This is the cost function. Although it is not the
	 * Bayesian one, it is a proper cost function (in that
	 * it only looks at the result of the program,
	 * and doesn't peek inside the cage).
	 */
	public static class AbsDistanceCostFunction extends CostFunction
	{
		@Override
		public double call(Object result, MonkeyCage cage) 
		{
			int[] r = (int[]) result;
			int sum = r[0];

			// This is left exactly the same as the original chimple version.
			// Why do we take the exponential? This is supposed to be an energy.
			//return Math.exp(Math.abs(sum_val - sum));
			
			// Better idea :)
			return Math.abs(sum_val - sum);
		}
	}
	
	/**
	 * This is another cost function (which will
	 * produce the correct Beta-Binomial posterior).
	 * It is equivalent to doing 20 extra flips and
	 * calling chimpConst on sum2 == 15 in the old
	 * Chimple. Also equivalent to Church's
	 * "condition" statement.
	 * 
	 * Of course, the closed-form solution saves us
	 * the 20 flips and high rejection rate, but usually we
	 * won't have a closed-form solution for our generative
	 * model so this is the only thing we can do.
	 */
	public static class RejectionCostFunction extends CostFunction
	{
		@Override
		public double call(Object result, MonkeyCage cage) {
			double weight = ((ChimpRand) cage.get("weight")).getValue();
		
			// Flip the coin 20 more times and count the number of heads
			int sum = 0;
			for (int i = 0; i < num_flips; i++) {
				sum += (Math.random() <= weight)?1:0;
			}

			// Reject here, to form the posterior for weight.
			// (We know it to be Beta(16, 6).)
			if(sum == sum_val) {
				return 0;
			} else {
				return Double.POSITIVE_INFINITY;
			}
			// Unfortunately, this causes a very low acceptance rate.
		}
	}
	
	/**
	 * This is will produce the correct Beta-Binomial posterior
	 * much faster than RejectionCostFunction.
	 */
	public static class BetaBinCostFunction extends CostFunction
	{
		@Override
		public double call(Object result, MonkeyCage cage) {
			// This is the exponential dispersion family energy/
			// negative-log-likelihood for the binomial distribution.
			double weight = ((ChimpRand) cage.get("weight")).getValue();
			return -(sum_val*Math.log(weight)+(num_flips-sum_val)*Math.log(1-weight));
		}
	}
	
	/**
	 * This is the main function (or "harness" classically
	 * in the Chimple literature) of the Java program.
	 * Keep in mind that it is a static function, and it
	 * is placed here only for convenience (ie. you may
	 * write your harness elsewhere).
	 * 
	 * @param	args	command-line arguments
	 */
	public static void main(String[] args)
	{
		RandomCoin program = new RandomCoin();
		ArrayList<Object> results = Demo.runDemo(program, args);
		program.display(results);
	}

	/* IMPLEMENTATION OF DEMO INTERFACE */
	
	/**
	 * Retrieve an instance of the default solver, and
	 * pass the program, arguments, and cost function
	 * to the constructor.
	 * 
	 * @param	program
	 * @param	args
	 * @param	cf
	 * @return	solver
	 */
	@Override
	public Solver getDefaultSolver(Demo program,
			Object[] args, CostFunction cf) {
		return new MetropolisHastingsSolver(program, args, cf);
	}
	
	/**
	 * Retrieve an instance of the default cost function.
	 * 
	 * @return	cf		Cost function
	 */
	@Override
	public CostFunction getDefaultCostFunction() {
		//return new RejectionCostFunction();
		return new BetaBinCostFunction();
	}
	
	/**
	 * Retrieve a set of parameters with which the
	 * demo prefers for use with the Solver class
	 * that s is an instance of.
	 * 
	 * @param	s
	 * @return	params
	 */
	@Override
	public Object[] getDefaultsFor(Solver s) {
		// These parameters are common to all solvers
		int samples = 2000;
		int burnin = 1000;
		int spacing = 3;
		
		if(s.getClass().equals(MetropolisHastingsSolver.class)) {
			spacing = 5;
			
			return new Object[] {burnin, samples, spacing};
		} else return new Object[] {};
	}

	/**
	 * Display results output by a sequence of program outputs.
	 * 
	 * @param	results		Results to display
	 */
	@Override
	public void display(ArrayList<Object> results) {
		double average = 0;
		int length = results.size();
		for (int i = 0; i < length; i++)
		{
			int[] result = (int[]) results.get(i);
			average += result[0];
		}
		average /= length;
		System.out.printf("Average was %f.\n", average);
	}
}
