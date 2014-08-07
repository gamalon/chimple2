package com.gamelanlabs.chimple2.monkeys;

import java.util.Arrays;

/**
 * Discrete distribution ERP. Returns values from 0 to
 * probs.length-1 (the MATLAB wrapper changes this range
 * to 1:probs.length by, well, adding one).
 * 
 * @author BenL
 *
 */
public class ChimpDiscrete extends Monkey<Integer> {
	protected double[] probs;
	
	/**
	 * Helper function that sums probs[].
	 * 
	 * @return	total	The sum of all entries in probs[]
	 */
	protected double sum() {
		double total = 0;
		for(double prob : probs) {
			total += prob;
		}
		return total;
	}
	
	/**
	 * Generates a value from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer generate() {
		double rand = getRandom().nextDouble()*sum();
		value = probs.length - 1;
		for(int i = 0; i < probs.length; i++) {
			rand -= probs[i];
			if (rand < 0) {
				value = i;
				break;
			}
		}
		return value;
	}
	
	/**
	 * Generates a value from the proposal kernel (ie. from
	 * the given array of weights, minus the weight of
	 * the current value).
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer propose() {
		double sum = sum() - probs[value];
		double rand = getRandom().nextDouble()*sum;
		
		// Initialize with the last element
		if(value != probs.length - 1) {
			value = probs.length - 1;
		} else {
			value = probs.length - 2;
		}
		
		// Draw from the discrete distribution,
		// skipping the current value.
		for(int i = 0; i < probs.length; i++) {
			if(i == getValue()) {
				continue;
			}
			rand -= probs[i];
			if (rand < 0) {
				value = i;
				break;
			}
		}
		return value;
	}

	/**
	 * Returns negative log-likelihood of the current state.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double energy() {
		return -Math.log(probs[value]/sum());
	}
	
	/**
	 * Returns the negative log of the probability of moving from
	 * the passed previous value to the current value.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double transitionEnergy(Integer fromvalue) {
		double total = sum() - probs[fromvalue];
		return -Math.log(probs[value]/total);
	}
	
	/**
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	protected Object[] getParams() {
		return new Object[] {probs};
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return !Arrays.equals(probs, (double[]) newparams[0]);
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
		probs = (double[]) params[0];
	}
}
