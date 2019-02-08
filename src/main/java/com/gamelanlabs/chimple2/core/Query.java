package com.gamelanlabs.chimple2.core;

/**
 * Interface implemented by Solvers and its subclasses, as well as
 * other objects that can perform queries (ie. externally).
 * 
 * @author BenL
 *
 */
public interface Query {
	/**
	 * Return query response.
	 * 
	 * @return	response	The response (ie. ArrayList of samples,
	 * 						TopicVocab object, etc.)
	 */
	public Object get();
}
