package com.gamelanlabs.chimple2.monkeys;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.special.Gamma;

/**
 * Dirichlet distribution ERP.
 * 
 * @author BenL
 *
 */
public class ChimpDirichlet extends Monkey<double[]> {
	protected double[] alphas;
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public double[] generate() {
		value = getRandom().nextDirichlet(alphas);
		return getValue();
	}
	
	/**
	 * Generates a proposal.
	 * 
	 * @return	value	Output
	 */
	@Override
	public double[] propose() {
		// Uniformly pick two random indices
		int ii = getRandom().nextInt(value.length);
		int jj = getRandom().nextInt(value.length-1);
		if(jj >= ii) ++jj;
		
		// Fetch the values and total them
		double a = value[ii];
		double b = value[jj];
		double total = a+b;
		
		// Split the total on a uniformly random point
		double ap = getRandom().nextDouble()*total;
		double bp = total-ap;
		
		// Set the pieces as the new values
		value[ii] = ap;
		value[jj] = bp;

		return value;
	}
	
	/**
	 * Returns negative log-likelihood of the current state.
	 * 	energy = sum(gammaln(alphas)) - gammaln(sum(alphas))
	 * 		- sum((alphas-1).*log(value))
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double energy() {
		double energy = 0;
		double sum = 0;
		for(int i = 0; i < alphas.length; i++) {
			energy -= (alphas[i]-1)*Math.log(value[i]);
			energy += Gamma.logGamma(alphas[i]);
			sum += alphas[i];
		}
		energy -= Gamma.logGamma(sum); 
		return energy;
	}
	
	/**
	 * Returns the negative log of the probability of moving from
	 * the passed previous value to the current value.
	 * 
	 * @return	energy	Energy
	 */
	@Override
	public double transitionEnergy(double[] v) {
		// Proposal kernel is symmetric, so we skip calculating this.
		return 0;
	}
	
	/**
	 * Override default getter, in order to return a
	 * clone of the array, instead of a reference to the
	 * original array.
	 * 
	 * @return	safevalue
	 */
	@Override
	public double[] getValue() {
		return ArrayUtils.clone(value);
	}
	
	/**
	 * Returns a safe copy of the parameters of this monkey.
	 * 
	 * @return	params
	 */
	@Override
	protected Object[] getParams() {
		return new Object[] {alphas};
	}

	/**
	 * Sets params
	 * 
	 * @param	params
	 */
	@Override
	public void setParams(Object... params) {
		alphas = ArrayUtils.clone((double[]) params[0]);
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return Arrays.equals(alphas, (double[]) newparams[0]);
	}
}
