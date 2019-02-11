package com.gamelanlabs.chimple2.monkeys;


/**
 * Normal (Gaussian) distribution ERP, with Normal proposal
 * kernel.
 * 
 * @author BenL
 *
 */
public class ChimpNormal extends Monkey<Double> {
	protected double mu;
	protected double sigma;
	protected double walk_sigma;

	/**
	 * Generates a proposal from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double generate() {
		value = getRandom().nextGaussian()*sigma + mu;
		return getValue();
	}
	
	/**
	 * Generates a proposal from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double propose() {
		value = getRandom().nextGaussian()*walk_sigma + getValue();
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
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	public Object[] getParams() {
		return new Object[] {mu, sigma, walk_sigma};
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		double _mu = (double) newparams[0];
		double _sigma = (double) newparams[1];
		double _walk_sigma = (double) newparams[2];
		return mu != _mu || sigma != _sigma || walk_sigma != _walk_sigma;
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
		mu = (double) params[0];
		sigma = (double) params[1];
		walk_sigma = (double) params[2];
	}
}
