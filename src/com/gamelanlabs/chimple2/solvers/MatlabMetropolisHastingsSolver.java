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
	 * Overrides MetropolisHastingsSolver::accept
	 */
	@Override
	public void accept(boolean save) {
		if(!save) return;
		try {
			// Save last result in MATLAB
			Matlab.mtEval("chimplify_internal_results = [chimplify_internal_results, lastresult];");
			
			// Push last energy to MATLAB
			if(lastenergy == Double.POSITIVE_INFINITY) {
				Matlab.mtEval("chimplify_internal_likelihoods = [chimplify_internal_likelihoods, Inf]");
			} else if(lastenergy == Double.NEGATIVE_INFINITY) {
				Matlab.mtEval("chimplify_internal_likelihoods = [chimplify_internal_likelihoods, -Inf]");
			} else if(lastenergy == Double.NaN) {
				Matlab.mtEval("chimplify_internal_likelihoods = [chimplify_internal_likelihoods, NaN];");
			} else {
				Matlab.mtEval(String.format("chimplify_internal_likelihoods = [chimplify_internal_likelihoods, %f];",lastenergy));
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Overrides MetropolisHastingsSolver::reject
	 */
	@Override
	public void reject(boolean save) {
		if(!save) return;
		try {
			Matlab.mtEval(
					"chimplify_internal_results{end+1} = chimplify_internal_results{end};");
			Matlab.mtEval(
					"chimplify_internal_likelihoods{end+1} = chimplify_internal_likelihoods{end};");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
