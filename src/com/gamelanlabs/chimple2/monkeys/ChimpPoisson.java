package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.stat.StatUtils;

import com.gamelanlabs.chimple2.core.Zookeeper;

/**
 * Beta distribution ERP.
 * 
 * @author BenL
 *
 */
public class ChimpPoisson extends Monkey<Integer> {
	public double lambda;
	public double jump;
	
	/**
	 * Constructor
	 * 
	 * @param	z		The zookeeper
	 * @param	_lambda	Parameter
	 */
	public ChimpPoisson(Zookeeper z, double _lambda, double _jump) {
		super(z);
		lambda = _lambda;
		jump = _jump;
	}
	
	/**
	 * Generates from the prior (basically directly
	 * from RandomPlus::nextDirichlet).
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer generate() {
		setValue(getRandom().nextPoisson(lambda));
		return getValue();
	}
	
	/**
	 * Generates from the proposal kernel.
	 * 
	 * @return	value	Output
	 */
	@Override
	public Integer propose() {
		// Use Bactrian kernel
		int var = (int) Math.ceil(lambda*jump);
		int value = getValue();
		double[] probs = new double[value+var];
		
		// Set up triangle discrete distribution
		for(int i = -var; i <= var; i++) {
			if(i == 0) continue;
			int index = value + i;
			if(index < 0) index = 1 - index;
			probs[index] += Math.abs(i);
		}
		
		// Draw from this distribution
		double rand = getRandom().nextDouble()*StatUtils.sum(probs);
		int newvalue = probs.length - 1;
		for(int i = 0; i < probs.length; i++) {
			rand -= probs[i];
			if (rand < 0) {
				newvalue = i;
				break;
			}
		}
		setValue(newvalue);
		return newvalue;
	}
	
	/**
	 * Negative log-likelihood of current value.
	 * 
	 * @return	energy	Negative log-likelihood
	 */
	@Override
	public double energy() {
		return -(new PoissonDistribution(lambda)).logProbability(getValue());
	}
	
	/**
	 * Negative log-likelihood of proposing current value
	 * from previous value.
	 * 
	 * @param	fromvalue	Previous value
	 */
	@Override
	public double transitionEnergy(Integer fromvalue) {
		// Proposal kernel is symmetric.
		return 0;
	}
	
	/**
	 * Clones the monkey
	 * 
	 * @return	clone	Cloned monkey
	 */
	@Override
	public ChimpPoisson clone() {
		ChimpPoisson c = new ChimpPoisson(zookeeper, lambda, jump);
		c.setValue(getValue());
		return c;
	}
}
