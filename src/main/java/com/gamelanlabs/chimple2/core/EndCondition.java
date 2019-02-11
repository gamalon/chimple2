package com.gamelanlabs.chimple2.core;

/**
 * Signals an end condition to the solver ("stop sampling!")
 * for whatever reason (we found the correct or a good enough
 * solution, or our marginal improvement has fallen below
 * a certain rate, etc.)
 * 
 * @author BenL
 *
 */
public class EndCondition extends Exception {
	private static final long serialVersionUID = 2928085271110244476L;
	protected final Object _sample;
	
	/**
	 * Constructor
	 * 
	 * @param	sample	Last sample
	 */
	public EndCondition(Object sample) {
		_sample = sample;
	}
	
	/**
	 * Getter function
	 * 
	 * @return	sample
	 */
	public Object getSample() {
		return _sample;
	}
}
