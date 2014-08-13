package com.gamelanlabs.chimple2.demos;

import java.util.ArrayList;
import java.util.Random;

import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.solvers.MetropolisHastingsSolver;
import com.gamelanlabs.chimple2.solvers.PriorSolver;
import com.gamelanlabs.chimple2.solvers.Solver;

/**
 * Bayesian Linear Regression Example
 *
 * @author dicai
 * @author jneely
 *
 */

public class BayesLinearRegress extends Demo
{
    private final int N;
    private double X[];

    public BayesLinearRegress(int N, double[] X)
    {
        this.N = N;
        this.X = X;
    }

	@Override
	public Object run(Object... args) 
	{
        // Draw sigma
        double invSigma = chimpGamma("sigma", 1, 1);
        double sigma = 1 / invSigma;

        // Draw beta
        double beta = chimpNormal("beta", 0, sigma);

        // Draw num_obs data
        double[] data = new double[N];

        for (int i = 0; i < N; i++)
            data[i] = chimpNormal("data"+i, 0, 0.1)*sigma + beta*X[i];

        return data;
	}
	
	public static void main(String[] args)
	{

        int N = 100;
        double[] X = new double[N];
        Random random = new Random();

        for (int i = 0; i < N; i++)
            X[i] = random.nextDouble();

        BayesLinearRegress program = new BayesLinearRegress(N, X);
        ArrayList<Object> results = Demo.runDemo(program, args);
        program.display(results);
	}

	/* IMPLEMENTATION OF DEMO INTERFACE */
	
	/**
	 * Retrieve an instance of the default solver, and
	 * pass the program, arguments, and cost function
	 * to the constructor.
	 * 
	 * @param	program
	 * @param	args
	 * @param	cf
	 * @return	solver
	 */
	@Override
	public Solver getDefaultSolver(Demo program, Object[] args, CostFunction cf) {
		return new MetropolisHastingsSolver(program, args, cf);
//        return new PriorSolver(program, args, cf);
	}
	
	/**
	 * Retrieve a set of parameters with which the
	 * demo prefers for use with the Solver class
	 * that s is an instance of.
	 * 
	 * @param	s
	 * @return	params
	 */
	@Override
	public Object[] getDefaultsFor(Solver s) {
		// These parameters are common to all solvers
		int samples = 2000;
		int burnin = 1000;
		int spacing = 3;
		
		if(s.getClass().equals(MetropolisHastingsSolver.class)) {
			spacing = 5;
			
			return new Object[] {burnin, samples, spacing};
		} else return new Object[] {};
	}

	/**
	 * Display results output by a sequence of program outputs.
	 * 
	 * @param	results		Results to display
	 */
	@Override
	public void display(ArrayList<Object> results) {
        int length = results.size();
        double[] temp = (double[])results.get(0);

        for (int i = 0; i < temp.length; i++)
            System.out.println(temp[i]);

        System.out.println("Program finished");
	}
}
