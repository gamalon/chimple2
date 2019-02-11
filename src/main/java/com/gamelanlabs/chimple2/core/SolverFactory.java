package com.gamelanlabs.chimple2.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.gamelanlabs.chimple2.solvers.MetropolisHastingsSolver;
import com.gamelanlabs.chimple2.solvers.PriorSolver;
import com.gamelanlabs.chimple2.solvers.Solver;

/**
 * Creates solvers from strings.
 * 
 * We also allow external solver factories to be registered.
 * 
 * @author BenL
 *
 */
public abstract class SolverFactory {
	/**
	 * Set of registered solver factories.
	 */
	private static final Set<SolverFactory> factories
			= new HashSet<SolverFactory>();
	
	/**
	 * List of friendly solver names (mapped to short names).
	 */
	@SuppressWarnings("serial")
	private static final Map<String, String> friendlynames
			= new HashMap<String, String>() {{
				put("traceMH", "tracemh");
				put("Forward", "forward");
			}};
	
	/**
	 * This function must be called in order to have your external
	 * solvers available in any software that uses solver factories.
	 */
	public static void registerSolverFactory(SolverFactory factory) {
		factories.add(factory);
		friendlynames.putAll(factory.getExternalFriendlyNames());
	}
	
	/**
	 * Creates solver from string.
	 * 
	 * @param	str
	 * @param	p
	 * @param	args
	 * @param	cf
	 * @return	solver
	 */
	public static Solver solverFromString(String str,
			ChimpleProgram p, Object[] args, CostFunction cf) {
		
		// Try built-in solvers
		switch(str) {
		case "tracemh":
			return new MetropolisHastingsSolver(p, args, cf);
		case "forward":
			return new PriorSolver(p, args, cf);
		}
		
		// Try list of registered solver factories
		for(SolverFactory f : factories) {
			Solver s = f.externalSolverFromString(str, p, args, cf);
			if(s != null) return s;
		}
		
		return null;
	}
	
	/**
	 * Gets the list of friendly names.
	 * 
	 * @return	array
	 */
	public static String[] getFriendlyNames() {
		return friendlynames.keySet().toArray(new String[0]);
	}
	
	/**
	 * Make solver from friendly name (convenience function).
	 * 
	 * @param	str
	 * @param	p
	 * @param	args
	 * @param	cf
	 * @return	solver
	 */
	public static Solver solverFromFriendlyName(String str,
			ChimpleProgram p, Object[] args, CostFunction cf) {
		return solverFromString(friendlynames.get(str), p, args, cf);
	}
	
	/**
	 * Creates solver from string. Returns null if this factory
	 * doesn't make that kind of solver. (Abstract method to
	 * be implemented by external solver factories.)
	 * 
	 * @param	str
	 * @param	p
	 * @param	args
	 * @param	cf
	 * @return	solver
	 */
	protected abstract Solver externalSolverFromString(String str,
			ChimpleProgram p, Object[] args, CostFunction cf);
	
	/**
	 * Returns a list of friendly names of solvers that this
	 * factory makes.
	 * 
	 * @return	collection
	 */
	protected abstract Map<String, String> getExternalFriendlyNames();
}
