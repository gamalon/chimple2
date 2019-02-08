package com.gamelanlabs.chimple2.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gamelanlabs.chimple2.demos.CompareSamplers;
import com.gamelanlabs.chimple2.demos.Demo;
import com.gamelanlabs.chimple2.demos.RandomCoin;
import com.gamelanlabs.chimple2.diagnostics.KLDivergence;

/**
 * Some random tests, piggybacking on the RandomCoin demo.
 * Run with JUnit4 in Eclipse.
 * 
 * @author BenL
 *
 */
public class RandomCoinTests {
	/**
	 * We run forward on RandomCoin and make sure the output is
	 * approximately uniform.
	 * 
	 * The KL divergence is usually <0.01, so to be safe we
	 * compare it to 0.02.
	 */
	@Test
	public void forwardHistogramIsUniform() {
		double[] histogram = CompareSamplers.tallyResults(
				Demo.runDemo(new RandomCoin(), new String[] {
					"forward"}));
		double[] uniform = new double[] {
				1./21, 1./21, 1./21, 1./21, 1./21, 
				1./21, 1./21, 1./21, 1./21, 1./21, 
				1./21, 1./21, 1./21, 1./21, 1./21, 
				1./21, 1./21, 1./21, 1./21, 1./21, 1./21
		};
		double kl = KLDivergence.discreteKL(uniform, histogram);
		System.out.printf("+ FWD KL divergence: %f\n", kl);
		assertTrue(kl < 0.02);
	}

	/**
	 * We run traceMH on RandomCoin but no cost function and
	 * make sure the output is approximately uniform.
	 * 
	 * The KL divergence is usually <0.1, so to be safe we
	 * compare it to 0.2.
	 */
	@Test
	public void MHWithNoCostIsUniform() {
		double[] histogram = CompareSamplers.tallyResults(
				Demo.runDemo(new RandomCoin(), new String[] {
				"tracemh", "none"}));
		double[] uniform = new double[] {
				1./21, 1./21, 1./21, 1./21, 1./21, 
				1./21, 1./21, 1./21, 1./21, 1./21, 
				1./21, 1./21, 1./21, 1./21, 1./21, 
				1./21, 1./21, 1./21, 1./21, 1./21, 1./21
		};
		double kl = KLDivergence.discreteKL(uniform, histogram);
		System.out.printf("+ MH+null KL divergence: %f\n", kl);
		assertTrue(kl < 0.2);
	}

	/**
	 * We run traceMH on RandomCoin with BetaBinCostFunction and
	 * make sure the output is approximately BetaBin(20, 16, 6).
	 * 
	 * The KL divergence is usually <0.1, so to be safe we
	 * compare it to 0.2.
	 */
	@Test
	public void MHWithBetaBinIsBetaBin() {
		double[] histogram = CompareSamplers.tallyResults(
				Demo.runDemo(new RandomCoin(), new String[] {
				"tracemh"}));
		double[] betabin = new double[21];
		for(int i = 0; i < 21; i++) {
			betabin[i] = CompareSamplers.betabin(20, i, 16, 6);
		}
		double kl = KLDivergence.discreteKL(betabin, histogram);
		System.out.printf("+ MH+BB KL divergence: %f\n", kl);
		assertTrue(kl < 0.2);
	}

}
