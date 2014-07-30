package com.gamelanlabs.chimple2.monkeys;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Normal (Gaussian) distribution ERP, with Normal proposal
 * kernel.
 * 
 * @author BenL
 *
 */
public class ChimpNormal extends Monkey<Double> {
	public double mu;
	public double sigma;
	public double walk_sigma;
	
	/**
	 * Constructor
	 * 
	 * @param	z				The zookeeper
	 * @param	mean			The mean of the prior
	 * @param	variance		The variance of the prior
	 * @param	walk_variance	The variance of the proposal kernel
	 */
	public ChimpNormal(Zookeeper z, double mean, double variance, double walk_variance) {
		super(z);
		mu = mean;
		sigma = variance;
		walk_sigma = walk_variance;
		generate();
	}

	/**
	 * Generates a proposal from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double generate() {
		setValue(getRandom().nextGaussian()*sigma + mu);
		return getValue();
	}
	
	/**
	 * Generates a proposal from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double propose() {
		setValue(getRandom().nextGaussian()*walk_sigma + getValue());
		return getValue();
	}

	/**
	 * Returns negative log-likelihood of the current state.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double energy() {
		return Math.log(Math.sqrt(2*Math.PI*sigma*sigma)) + (mu-getValue())*(mu-getValue())/(2*sigma*sigma);
	}
	
	/**
	 * Returns the negative log of the probability of moving from
	 * the passed previous value to the current value.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double transitionEnergy(Double fromvalue) {
		// Proposal kernel is symmetric, so we skip calculating this.
		return 0;
	}
	
	/**
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpNormal clone() {
		ChimpNormal c = new ChimpNormal(zookeeper, mu, sigma, walk_sigma);
		c.setValue(getValue());
		return c;
	}
}
