package com.gamelanlabs.chimple2.monkeys;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.stat.StatUtils;


/**
 * exponential distribution ERP
 * @author jakeneely
 *
 */

public class ChimpExponential extends Monkey<Double> {
	protected double lambda;
	
	

	
	@Override
	public Double generate() {
		value = getRandom().nextExponential(lambda);
		return (Double) getValue();
	}
	

	@Override
	public Double propose() {
		//value = getRandom().nextExponential(lambda) + getValue();
		//value = getRandom().nextPoisson(lambda) + getValue();
		//return getValue();
		value = Math.abs(getRandom().nextGaussian()*2 + getValue());
		return getValue();
		
	}
	
	@Override
	public double energy() {
//		return -(new ExponentialDistribution(lambda)).logDensity(value);
//		return -1*Math.log(lambda * Math.exp(-1*lambda*value));
		return lambda*value - Math.log(lambda);
	}
	
	@Override
	public double transitionEnergy(Double fromvalue) {
		// Proposal kernel is symmetric.
		return 0;
	}
	
	@Override
	public Object[] getParams() {
		return new Object[] {lambda};
	}
	
	@Override
	public boolean paramsChanged(Object... newparams) {
		return lambda != (double) newparams[0];
	}
	
	@Override
	public void setParams(Object... params) {
		lambda = (double) params[0];
	}
	


}
