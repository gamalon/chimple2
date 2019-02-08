package com.gamelanlabs.chimple2.core;

/**
 * Abstract cost function class -- this object basically just
 * a function.
 * 
 * This method of setting a cost function will be referred to
 * as the "external" cost function.
 * 
 * You may also call ChimpleProgram::addCost() within your
 * probabilistic program to add "internal" cost functions.
 * This method may be more convenient for most applications.
 * 
 * @author BenL
 *
 */
public abstract class CostFunction {
	/**
	 * Computes the cost function.
	 * 
	 * @param	result	A result set returned by run() on your ChimpleProgram
	 * @param	cage	In addition to the actual result set, your cost function may also reference
	 * 					"hidden" Monkeys that weren't returned by the program.
	 * 					A proper cost function should only depend on the observables, but the cost
	 * 					function can also be used for other purposes besides the usual "cost/negative
	 * 					utility" paradigm (ie. express exponential family log-likelihoods -- see
	 * 					the RandomCoin demo's BetaBinCostFunction).
	 * @return	cost	This is added to the energy of this sample.
	 */
	public abstract double call(Object result, MonkeyCage cage);
}
