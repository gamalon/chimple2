package com.gamelanlabs.chimple2.monkeys;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Uniform distribution ERP. Returns values from 0.0 to 1.0.
 * Has no parameters (really? re-scale the output yourself).
 * 
 * @author BenL
 *
 */
public class ChimpRand extends Monkey<Double> {
	/**
	 * Constructor
	 * 
	 * @param	z				The zookeeper
	 */
	public ChimpRand(Zookeeper z) {
		super(z);
		generate();
	}
	
	/**
	 * Generates a proposal from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double generate() {
		setValue(getRandom().nextDouble());
		return getValue();
	}
	
	/**
	 * Generates a proposal from the posterior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double propose() {
		// Conditional proposal kernel is the same as independent proposal kernel.
		return generate();
	}

	/**
	 * Returns negative log-likelihood of the current state.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double energy() {
		return 0;
	}
	
	/**
	 * Returns the negative log of the probability of moving from
	 * the passed previous value to the current value.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double transitionEnergy(Double v) {
		// Proposal kernel is symmetric, so we skip calculating this.
		return 0;
	}
	
	/**
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpRand clone() {
		ChimpRand c = new ChimpRand(zookeeper);
		c.setValue(getValue());
		return c;
	}
}
