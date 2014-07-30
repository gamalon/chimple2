package com.gamelanlabs.chimple2.solvers;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.core.MonkeyCage;

/**
 * Runs the generative model "forward" to obtain the prior distribution.
 * 
 * @author BenL
 *
 */
public class PriorSolver extends Solver {
	public PriorSolver(ChimpleProgram p, Object[] a, CostFunction cf) {
		super(p, a, null);
	}
	public void solve(int samples) {
		for(int i = 0; i < samples; i++) {
			save(program.run(arguments));
			zookeeper.cage = new MonkeyCage(); // reset all monkeys
		}
	}
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
