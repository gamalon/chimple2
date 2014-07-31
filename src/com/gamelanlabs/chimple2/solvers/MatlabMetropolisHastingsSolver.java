package com.gamelanlabs.chimple2.solvers;

import com.gamelanlabs.chimple2.core.MatlabChimpleProgram;
import com.mathworks.jmi.Matlab;

/**
 * MATLAB interface to the solver. Requires JMI.
 * 
 * @author BenL
 *
 */
public class MatlabMetropolisHastingsSolver extends MetropolisHastingsSolver {
	/**
	 * Constructor (external cost functions are not supported).
	 * 
	 * @param	p		MATLAB probabilistic program (wrapped in a MatlabChimpleProgram)
	 */
	public MatlabMetropolisHastingsSolver(MatlabChimpleProgram p) {
		super(p, new Object[] {}, null);
		try {
			Matlab.mtEval("global chimplify_internal_results;");
			Matlab.mtEval("global chimplify_internal_likelihoods;");
			Matlab.mtEval("global chimplify_internal_args;");
			Matlab.mtEval("chimplify_internal_results = {};");
			Matlab.mtEval("chimplify_internal_likelihoods = {};");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Overrides MetropolisHastingsSolver::accept.
	 */
	@Override
	public void accept(boolean save) {
		try {
			Matlab.mtEval("last_accepted_result = last_result;");
			Matlab.mtEval("last_accepted_likelihood = " +
					doubleToMatlab(lastenergy) + ";");
		} catch(Exception e) {}
		
		if(save) saveLastAcceptedResult();
	}
	
	/**
	 * Overrides MetropolisHastingsSolver::reject.
	 */
	@Override
	public void reject(boolean save) {
		if(save) saveLastAcceptedResult();
	}
	
	/**
	 * Saves the last accepted result in MATLAB.
	 */
	protected void saveLastAcceptedResult() {
		try {
			Matlab.mtEval(
					"chimplify_internal_results{end+1} = last_accepted_result;");
			Matlab.mtEval(
					"chimplify_internal_likelihoods{end+1} = last_accepted_likelihood;");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts a double to a MATLAB double string.
	 * 
	 * @param	d
	 * @return	str
	 */
	protected String doubleToMatlab(double d) {
		if(d == Double.POSITIVE_INFINITY) {
			return "Inf";
		} else if(d == Double.NEGATIVE_INFINITY) {
			return "-Inf";
		} else if(d == Double.NaN) {
			return "NaN";
		} else return Double.toString(d);
	}

}
