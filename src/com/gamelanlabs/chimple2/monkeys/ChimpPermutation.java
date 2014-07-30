package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.util.CombinatoricsUtils;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Permutation ERP.
 * 
 * @author BenL
 *
 */
public class ChimpPermutation extends Monkey<int[]> {
	public int n;
	
	/**
	 * Constructor
	 * 
	 * @param	z	The zookeeper
	 * @param	_n	Perutations of length n
	 */
	public ChimpPermutation(Zookeeper z, int _n) {
		super(z);
		n = _n;
	}
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public int[] generate() {
		setValue(getRandom().nextPermutation(n));
		return getValue();
	}
	
	/**
	 * Generates from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public int[] propose() {
		int[] value = getValue();
		int i = Math.abs(getRandom().nextInt())%(n);
		int j = Math.abs(getRandom().nextInt())%(n-1);
		if(j >= i) ++j;
		
		// Swap i and j
		int tmp = value[i];
		value[i] = value[j];
		value[j] = tmp;
		
		return value;
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
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpPermutation clone() {
		ChimpPermutation c = new ChimpPermutation(zookeeper, n);
		c.setValue(getValue().clone());
		return c;
	}
}
