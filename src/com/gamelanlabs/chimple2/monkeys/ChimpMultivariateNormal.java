package com.gamelanlabs.chimple2.monkeys;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
/**
 * 
 * Sweet Multivariate Gaussian Monkey
 * 
 * Implements a multivariate Gaussian based on a mean vector and a covariance matrix
 * 
 * @author mattbarr
 * @author nileshtrip
 */
public class ChimpMultivariateNormal extends Monkey<double[]> {
	protected double[] mus;
	protected double[][] sigmas;
	protected double[][] walksigmas;
	protected MultivariateNormalDistribution sampler;
	protected MultivariateNormalDistribution walker;
	
	/**
	 * 
	 * 
	 */
	@Override
	public boolean paramsChanged(Object... newparams) {
		return (!Arrays.equals(mus, (double[]) newparams[0]))||(!Arrays.equals(sigmas,(double[][])newparams[1]));
	}

	@Override
	public void setParams(Object... params) {
		mus = ArrayUtils.clone((double[])params[0]);
		sigmas = ArrayUtils.clone((double[][])params[1]);
		walksigmas = ArrayUtils.clone((double[][])params[2]);
		sampler = new MultivariateNormalDistribution(mus,sigmas);
		walker = new MultivariateNormalDistribution(new double[mus.length],walksigmas);
	}

	@Override
	protected Object[] getParams() {
		return new Object[]{mus, sigmas};
	}

	/**
	 * Generate will draw from the prior of the Gaussian process
	 */
	@Override
	public double[] generate() {
		value = sampler.sample();
		return getValue();
	}

	@Override
	public double[] propose() {
		double[] addition=walker.sample();
		double[] newvalue = ArrayUtils.clone(value);
		for (int i = 0; i<mus.length;i++)
			newvalue[i]=newvalue[i]+addition[i];
		return newvalue;
	}

	/**
	 * For the negative log likelihood, the energy should simply be (1/2) * (value - my)^T.inverse(covariance).(value - mu)
	 */
	@Override
	public double energy() {
		return -Math.log(sampler.density(value));	
	}


	/**
	 * The transition energy for a multivariate proposal distribution is zero by definition 
	 */
	@Override
	public double transitionEnergy(double[] fromvalue) {
		return 0;
	}
	
	@Override
	public ChimpMultivariateNormal clone() {
		ChimpMultivariateNormal copy = new ChimpMultivariateNormal();
		copy.setZookeeper(this.zookeeper);
		copy.setParams(getParams());
		return copy;
	}
	
	
}
