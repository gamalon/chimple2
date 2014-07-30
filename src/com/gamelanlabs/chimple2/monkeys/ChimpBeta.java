package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.special.Beta;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Beta distribution ERP.
 * 
 * @author BenL
 *
 */
public class ChimpBeta extends Monkey<Double> {
	public double[] alphas;
	
	/**
	 * Constructor
	 * 
	 * @param	z		The zookeeper
	 * @param	alpha	First alpha
	 * @param	beta	Second alpha
	 */
	public ChimpBeta(Zookeeper z, double alpha, double beta) {
		super(z);
		alphas = new double[] {alpha, beta};
	}
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double generate() {
		setValue(getRandom().nextDirichlet(alphas)[0]);
		return getValue();
	}
	
	/**
	 * Generates from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double propose() {
		setValue(1-getValue());
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
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpBeta clone() {
		ChimpBeta c = new ChimpBeta(zookeeper, alphas[0], alphas[1]);
		c.setValue(getValue());
		return c;
	}
}
