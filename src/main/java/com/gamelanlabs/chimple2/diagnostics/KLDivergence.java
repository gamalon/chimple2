package com.gamelanlabs.chimple2.diagnostics;

/**
 * Performs (natural logarithm) Kullback-Leiber divergence calculations.
 * 
 * @author BenL
 *
 */
public final class KLDivergence {
	/**
	 * Calculates KL divergence on two discrete probability distributions.
	 * 
	 * D_{KL}(P||Q) = \sum_i \ln(P(i)/Q(i)) P(i)
	 * 
	 * @param	pmf1	The first distribution P ("divergence from ___...")
	 * @param	pmf2	The second distribution Q ("of ___")
	 * @return	kl		Natural logarithm KL divergence
	 */
	public static double discreteKL(double[] pmf1, double[] pmf2) {
		if(pmf1.length != pmf2.length) {
			throw new RuntimeException(
					"The domains of the two PMFs passed to "
					+"KLDivergence::discreteKL are not the same!");
		}
		double kl = 0;
		for(int i = 0; i < pmf1.length; i++) {
			if(Math.abs(pmf1[i]) < 0.0001) continue;
			if(Math.abs(pmf2[i]) < 0.0001) continue;
			kl += Math.log(pmf1[i]/pmf2[i])*pmf1[i];
		}
		return kl;
	}
}
