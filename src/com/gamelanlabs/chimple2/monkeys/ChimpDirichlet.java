package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.special.Gamma;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Dirichlet distribution ERP.
 * 
 * @author BenL
 *
 */
public class ChimpDirichlet extends Monkey<double[]> {
	public double[] alphas;
	
	/**
	 * Constructor
	 * 
	 * @param	z	The zookeeper
	 * @param	a	An array of parameters
	 */
	public ChimpDirichlet(Zookeeper z, double[] a) {
		super(z);
		alphas = a;
		generate();
	}
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public double[] generate() {
		setValue(getRandom().nextDirichlet(alphas));
		return getValue();
	}
	
	/**
	 * Generates a proposal.
	 * 
	 * @return	value	Output
	 */
	@Override
	public double[] propose() {
		double[] value = getValue();

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
		double[] value = getValue();
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
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpDirichlet clone() {
		ChimpDirichlet c = new ChimpDirichlet(zookeeper, alphas);
		c.setValue(getValue().clone());
		return c;
	}
}
