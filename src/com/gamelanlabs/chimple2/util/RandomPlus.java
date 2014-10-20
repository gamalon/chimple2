package com.gamelanlabs.chimple2.util;

import java.util.Random;

/**
 * This class was a dependency from Dimple, but is standalone and
 * thus has been moved directly into the Chimple codebase. Some
 * changes have been made by BenL.
 * 
 * @author BenL
 * @author jakeneely0
 * 
 *
 */
public class RandomPlus extends Random 
{
	private static final long serialVersionUID = 1L;

	public int nextPoisson(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		do {
			k++;
			p *= nextDouble();
		} while (p > L);

		return k - 1;
	}
	
	public double nextExponential(double lambda) {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int j = 0;
		
		do{
			j++;
			p *= nextDouble();
		}
		while (p>L);
		return j-1;
	}
	
	
	public int[] nextPermutation(int n)
	{
		int[] answer = new int[n];

		for(int i = 0; i < n; i++) {
			answer[i] = i+1;
		}
		
		// Modern Fisher-Yates shuffle
		for (int i = n-1; i >= 0; i--) {
			int j = Math.abs(nextInt())%(i+1);
			int tmp = answer[i];
			answer[i] = answer[j];
			answer[j] = tmp;
		}
		
		return answer;
	}
	
	public double nextGamma(double alpha, double theta)
	{

		int k = (int) Math.floor(alpha);
		double delta = alpha - k;
		double g = 0;
		
		if (delta > 0)
		{
			double nu = Math.exp(1)/(Math.exp(1)+delta);
			while (true)
			{
				double v0 = nextDouble();
				double v1 = nextDouble();
				double xi = 0;
				double eta = 0;
				
				if (v0 <= nu)
				{
					xi = Math.pow((v0/nu),(1.0/delta));
					eta = v1 * Math.pow(xi,delta-1);
				}
				else
				{
					xi = 1.0 - Math.log((v0-nu)/(1-nu));
					eta = v1*Math.exp(-xi);
				}
				if (eta <= Math.pow(xi,delta-1) * Math.exp(-xi))
				{
					g = xi;
					break;
				}
			}
		}
		double un = 0;
		for (int i = 0; i < k; i++)
			un += Math.log(nextDouble());
		g = theta * (g-un);
		
		return g;
	}
	
	public double [] nextDirichlet(double [] a)
	{
		int n = a.length;
		double [] r = new double[n];
		double sum = 0;
		for (int i = 0; i < n; i++)
		{
			r[i] = nextGamma(a[i], 1);
			sum += r[i];
		}
		
		for (int i = 0; i < n; i++)
			r[i]=r[i]/sum;

		return r;
	}
}
