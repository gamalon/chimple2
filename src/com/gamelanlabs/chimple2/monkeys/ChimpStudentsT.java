/**
 * 
 */
package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.distribution.TDistribution;

/**
 * @author nileshtrip
 *
 */
public class ChimpStudentsT extends Monkey<Double> {

	protected double nu;
	protected double mu;
	protected double sigma;
	protected double walk_sigma;
	protected TDistribution t_dist;
	protected TDistribution walk_t;
	
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
		double _nu = (double) newparams[2];
		double _walk_sigma = (double) newparams[3];
		return mu != _mu || sigma != _sigma ||nu != _nu || walk_sigma != _walk_sigma;
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
		nu = (double) params[2];
		walk_sigma = (double) params[3];
		t_dist = new TDistribution(nu);
	}

	
	/**
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	protected Object[] getParams() {

		return new Object[] {mu, sigma, nu, walk_sigma};
	}
	/**
	 * Generates a proposal from the prior.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double generate() {

		//double z = getRandom().nextGaussian();
		//double chi_2 = getRandom().nextGamma(nu*.5, .5);
		//value = mu + sigma*z*Math.sqrt(nu/chi_2);
		value = t_dist.sample()*sigma + mu;
		return getValue();	
	}

	/**
	 * Generates a proposal from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Double propose() {
		
		//value = getRandom().nextGaussian()*walk_sigma + getValue();
		value = t_dist.sample()*walk_sigma + getValue();
		return getValue();
	}

	/**
	 * Returns negative log-likelihood of the current state.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double energy() {
		
		return (double) -Math.log(t_dist.density((getValue()-mu)/sigma));
	}

	/**
	 * Returns the negative log of the probability of moving from
	 * the passed previous value to the current value.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double transitionEnergy(Double fromvalue) {
		
		return 0;
	}

}
