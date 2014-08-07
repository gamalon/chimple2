package com.gamelanlabs.chimple2.solvers;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.core.MonkeyFactory;
import com.gamelanlabs.chimple2.util.MHUtils;

/**
 * Runs the generative model "forward" to obtain the prior distribution.
 * 
 * @author BenL
 *
 */
public class PriorSolver extends Solver {
	/**
	 * Constructor
	 * 
	 * @param	p
	 * @param	a
	 * @param	cf
	 */
	public PriorSolver(ChimpleProgram p, Object[] a, CostFunction cf) {
		super(p, a, null);
	}
	
	/**
	 * Returns the MonkeyFactory that this solver wants to give the
	 * ChimpleProgram.
	 * 
	 * @return	factory
	 */
	@Override
	protected MonkeyFactory makeMonkeyFactory() {
		return new MHUtils.NaiveMonkeyFactory(zookeeper);
	}
	
	/**
	 * Runs the program forward.
	 * 
	 * @param	samples
	 */
	public void solve(int samples) {
		for(int i = 0; i < samples; i++) {
			save(program.run(arguments));
		}
	}
	
	/**
	 * Runs the program forward for 1000 samples.
	 */
	@Override
	public void solve() {
		solve(1000);
	}

	/**
	 * Return the names of arguments to solve() that this solver takes.
	 * 
	 * @return	names			Friendly names of arguments
	 */
	public static String[] getArgumentNames() {
		return new String[] {"Samples"};
	}
	
	/**
	 * Return default arguments to solve() that this solver takes.
	 * 
	 * @return	defaultargs		Default arguments
	 */
	public static Object[] getDefaultArguments() {
		return new Object[] {1000};
	}
}
