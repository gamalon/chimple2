package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;

/**
 * Permutation ERP.
 * 
 * @author BenL
 *
 */
public class ChimpPermutation extends Monkey<int[]> {
	protected int n;
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public int[] generate() {
		value = getRandom().nextPermutation(n);
		return getValue();
	}
	
	/**
	 * Generates from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public int[] propose() {
		int i = Math.abs(getRandom().nextInt())%(n);
		int j = Math.abs(getRandom().nextInt())%(n-1);
		if(j >= i) ++j;
		
		// Swap i and j
		int tmp = value[i];
		value[i] = value[j];
		value[j] = tmp;
		
		return getValue();
	}
	
	/**
	 * Negative log-likelihood of current value.
	 * 
	 * @return	energy	Negative log-likelihood
	 */
	@Override
	public double energy() {
		return CombinatoricsUtils.factorialLog(n);
	}
	
	/**
	 * Negative log-likelihood of proposing current value
	 * from previous value.
	 * 
	 * @param	fromvalue	Previous value
	 */
	@Override
	public double transitionEnergy(int[] fromvalue) {
		// Proposal kernel is symmetric.
		return 0;
	}
	
	/**
	 * Override default getter, in order to return a
	 * clone of the array, instead of a reference to the
	 * original array.
	 * 
	 * @return	safevalue
	 */
	@Override
	public int[] getValue() {
		return ArrayUtils.clone(value);
	}
	
	/**
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	public Object[] getParams() {
		return new Object[] {n};
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return n != (int) newparams[0];
	}

	/**
	 * Tells the monkey how to make Bananas.
	 * 
	 * Makes a safe copy of the instructions (ie. not
	 * by reference).
	 * 
	 * @param	params		Parameters
	 */
	@Override
	public void setParams(Object... params) {
		n = (int) params[0];
	}
}
