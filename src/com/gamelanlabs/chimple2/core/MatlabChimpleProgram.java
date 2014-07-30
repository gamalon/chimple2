package com.gamelanlabs.chimple2.core;

import com.mathworks.jmi.Matlab;

/**
 * MATLAB interface. Requires JMI.
 * 
 * @author BenL
 *
 */
public class MatlabChimpleProgram extends ChimpleProgram {
	private static final byte PLACEHOLDER_RESULT = 0;
	private final String matlabcallback;
	
	/**
	 * This class is initialized by the thin MATLAB wrapper
	 * with the name of the MATLAB callback to run as the
	 * probabilistic program.
	 * 
	 * @param	call	MATLAB callback (function name only)
	 */
	public MatlabChimpleProgram(String call) {
		super();
		matlabcallback = call;
	}
	
	/**
	 * Function that runs the probabilistic program. In our
	 * case, we call the MATLAB JMI library's eval function.
	 * This is calls (in MATLAB)
	 * 		eval("<functionname>(chimplify_internal_args{:})")
	 * which is the same as (again, in MATLAB)
	 * 		<functionname>(arg1, arg2, ..., argN)
	 * and sets the return value to lastresult, a local variable
	 * that MatlabMetropolisHastingsSolver will later add to
	 * chimplify_internal_results.
	 * 
	 * If something goes wrong, JMI will throw a MatlabException
	 * (which we will re-throw so that it shows up in the
	 * MATLAB console).
	 * 
	 * Unfortunately, the MATLAB console error dump will not tell
	 * you what line the MATLAB error is on, so debugging your
	 * probabilistic program may be annoying.
	 * 
	 * To circumvent this, test your probabilistic program by
	 * directly calling (in MATLAB) <functionname>(<arg1>, ...,
	 * <argN>) instead of solver.solve() until you have fixed
	 * all of your bugs.
	 * 
	 * @param	args	Arguments (ignored)
	 * @return	r		A placeholder (the results never
	 * 					actually cross the MATLAB-Java
	 * 					membrane)
	 */
	@Override
	public Object run(Object ... args) {
		try {
			String call = String.format(
					"lastresult = %s(chimplify_internal_args{:});",
					matlabcallback);
			Matlab.mtEval(call);
			return PLACEHOLDER_RESULT;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
