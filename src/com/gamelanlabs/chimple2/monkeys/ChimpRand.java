package com.gamelanlabs.chimple2.monkeys;


/**
 * Uniform distribution ERP. Returns values from 0.0 to 1.0.
 * Optional random walk parameter
 * 
 * @author BenL
 * @author jake.neely
 *
 */
public class ChimpRand extends Monkey<Double> {	
	protected Double walk_sigma;
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
		if (walk_sigma == null){
			return generate();
		}
		else{
			value = Math.abs(getRandom().nextGaussian()*walk_sigma + getValue());
			return getValue();
		}
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
     * Sets parameters
     *
     * @param	pars	The pair of parameters
     */
    public void setParams(Object...pars) {
        walk_sigma = (Double)pars[0];
    }
	
	/**
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	protected Object[] getParams() {
		return new Object[] {walk_sigma};
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return walk_sigma != (Double) newparams[0];
	}

}
