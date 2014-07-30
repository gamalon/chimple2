package com.gamelanlabs.chimple2.demos;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.core.CostFunction;
import com.gamelanlabs.chimple2.solvers.MetropolisHastingsSolver;
import com.gamelanlabs.chimple2.solvers.PriorSolver;
import com.gamelanlabs.chimple2.solvers.Solver;


/**
 * Demos must implement this interface, so that they can be
 * automated.
 * 
 * @author BenL
 *
 */
public abstract class Demo extends ChimpleProgram {
	/**
	 * Display results output by a sequence of program outputs.
	 * 
	 * @param	results		Results to display
	 */
	public abstract void display(ArrayList<Object> results);
	
	/**
	 * Retrieve the default arguments with which this demo
	 * class should be instantiated. Can be null if the
	 * program does not use its arguments.
	 * 
	 * @return	args	Arguments
	 */
	public Object[] getDefaultArguments() {
		return new Object[] {};
	};
	
	/**
	 * Retrieve an instance of the default cost function.
	 * 
	 * @return	cf		Cost function
	 */
	public CostFunction getDefaultCostFunction() {
		return null;
	}
	
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
	public Solver getDefaultSolver(Demo program, Object[] args, CostFunction cf) {
		return new MetropolisHastingsSolver(program, args, cf);
	}
	
	/**
	 * Retrieve a set of parameters with which the
	 * demo prefers for use with the Solver class
	 * that s is an instance of.
	 * 
	 * @param	s
	 * @return	params
	 */
	public Object[] getDefaultsFor(Solver s) {
		return new Object[] {};
	}
	
	/**
	 * Uses Java reflection to call Solver.solve() with
	 * the passed array as arguments to the method call.
	 * 
	 * @param	s
	 * @param	args
	 * @return	results
	 */
	public static ArrayList<Object> callSolve(Solver s, Object[] args) {
		for(Method m : s.getClass().getMethods()) {
			if(m.getName() == "solve") {
				if(m.getParameterTypes().length != args.length) continue;
				try {
					m.invoke(s, args);
					return s.getResults();
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		throw new RuntimeException("callSolve failed!");
	}
	
	/**
	 * Instantiates a solver from the command line.
	 * 
	 * @param	program
	 * @param	args
	 * @return	solver
	 */
	public static Solver solverFromCommandLine(Demo program, String[] args) {
		CostFunction cf = null;
		if(args.length < 2 || !args[1].equals("none")) {
			cf = program.getDefaultCostFunction();
		}
		if(args.length > 0) {
			switch(args[0]) {
			case "forward":
				return new PriorSolver(program, program.getDefaultArguments(), cf);
			case "tracemh":
				return new MetropolisHastingsSolver(program, program.getDefaultArguments(), cf);
			default:
				System.err.println("Unknown solver '"+args[0]+"'.");
				System.exit(1);
			}
		}
		return program.getDefaultSolver(program, program.getDefaultArguments(), cf);
	}
	
	/**
	 * Runs the demo with the passed command line.
	 * 
	 * @param	args
	 * @return	results
	 */
	public static ArrayList<Object> runDemo(Demo program, String[] args) {
		Solver s = solverFromCommandLine(program, args);
		return callSolve(s, program.getDefaultsFor(s));
	}
	
	/**
	 * Returns a settings panel.
	 * 
	 * @return	panel
	 */
	public JPanel getSettingsPanel() {
		return new JPanel();
	}
}
