package com.gamelanlabs.chimple2.monkeys;


/**
 * Uniform distribution ERP. Returns values from 0.0 to 1.0.
 * Has no parameters (really? re-scale the output yourself).
 * 
 * @author BenL
 *
 */
public class ChimpRand extends Monkey<Double> {	
	/**
	 * Generates a proposal from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double generate() {
		value = getRandom().nextDouble();
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
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	public Object[] getParams() {
		return new Object[] {};
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return false;
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
	public void setParams(Object... params) { }
}
