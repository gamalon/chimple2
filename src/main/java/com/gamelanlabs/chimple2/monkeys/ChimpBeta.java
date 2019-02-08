package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.special.Beta;

/**
 * Beta distribution ERP.
 * 
 * @author BenL
 *
 */
public class ChimpBeta extends Monkey<Double> {
	protected double[] alphas;
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double generate() {
		value = getRandom().nextDirichlet(alphas)[0];
		return getValue();
	}
	
	/**
	 * Generates from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double propose() {
		value = 1 - getValue();
		return getValue();
	}
	
	/**
	 * Negative log-likelihood of current value.
	 * 
	 * @return	energy	Negative log-likelihood
	 */
	@Override
	public double energy() {
		double energy = 0;
		energy -= (alphas[0]-1)*Math.log(getValue());
		energy -= (alphas[1]-1)*Math.log(1-getValue());
		energy += Beta.logBeta(alphas[0], alphas[1]);
		return energy;
	}
	
	/**
	 * Negative log-likelihood of proposing current value
	 * from previous value.
	 * 
	 * @param	fromvalue	Previous value
	 */
	@Override
	public double transitionEnergy(Double fromvalue) {
		// Proposal kernel is deterministic, so likelihood is
		// always 1.
		return 0;
	}
	
	/**
	 * Sets parameters
	 * 
	 * @param	params	The pair of parameters
	 */
	@Override
	public void setParams(Object... params) {
		alphas = ArrayUtils.toPrimitive((Double[]) params);
	}
	
	/**
	 * Returns an unsafe safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	protected Object[] getParams() {
		return ArrayUtils.toObject(alphas);
	}
	
	/**
	 * Compares parameters
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return alphas[0] != (double) newparams[0] ||
				alphas[1] != (double) newparams[1];
	}
}
