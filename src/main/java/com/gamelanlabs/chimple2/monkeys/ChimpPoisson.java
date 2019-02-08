package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Beta distribution ERP.
 * 
 * @author BenL
 *
 */
public class ChimpPoisson extends Monkey<Integer> {
	protected double lambda;
	protected double jump;
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer generate() {
		value = getRandom().nextPoisson(lambda);
		return getValue();
	}
	
	/**
	 * Generates from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer propose() {
		// Use Bactrian kernel
		int var = (int) Math.ceil(lambda*jump);
		double[] probs = new double[value+2*var];
		
		// Set up triangle discrete distribution
		for(int i = -var; i <= var; i++) {
			if(i == 0) continue;
			int index = value + i;
			if(index < 0) index = 1 - index;
			probs[index] += Math.abs(i);
		}
		
		// Draw from this distribution
		double rand = getRandom().nextDouble()*StatUtils.sum(probs);
		int newvalue = probs.length - 1;
		for(int i = 0; i < probs.length; i++) {
			rand -= probs[i];
			if (rand < 0) {
				newvalue = i;
				break;
			}
		}
		value = newvalue;
		return newvalue;

	}
	
	/**
	 * Negative log-likelihood of current value.
	 * 
	 * @return	energy	Negative log-likelihood
	 */
	@Override
	public double energy() {
		return -(new PoissonDistribution(lambda)).logProbability(value);
	}
	
	/**
	 * Negative log-likelihood of proposing current value
	 * from previous value.
	 * 
	 * @param	fromvalue	Previous value
	 */
	@Override
	public double transitionEnergy(Integer fromvalue) {
		// Proposal kernel is symmetric.
		return 0;
	}
	
	/**
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	public Object[] getParams() {
		return new Object[] {lambda, jump};
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return lambda != (double) newparams[0] ||
				jump != (double) newparams[1];
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
		lambda = (double) params[0];
		jump = (double) params[1];
	}
}
