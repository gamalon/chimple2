package com.gamelanlabs.chimple2.monkeys;

import com.gamelanlabs.chimple2.core.Zookeeper;
import com.gamelanlabs.chimple2.util.RandomPlus;

/**
 * Abstract monkey. Bananas sold separately.
 * 
 * @author BenL
 *
 * @param	<Banana>	What a banana is
 *
 */
public abstract class Monkey<Banana> implements Cloneable {
	public Zookeeper zookeeper;
	public boolean touched; // used to keep track of which monkeys have been touched in a run
	private Banana value;
	
	/**
	 * Constructor
	 * 
	 * @param z		The Zookeeper that has all the bananas
	 */
	public Monkey(Zookeeper z) {
		zookeeper = z;
		touched = false;
	}
	
	/**
	 * Generates from the prior.
	 * 
	 * @return	value	An Object from the prior distribution
	 * 					of this Monkey.
	 */
	public abstract Banana generate();
	
	/**
	 * Generates from the proposal kernel (ie. possibly
	 * depending on the previous value).
	 * 
	 * @return	value
	 */
	public abstract Banana propose();
	
	/**
	 * Gets the banana.
	 * 
	 * @return	value	The Banana previously generated
	 * 					by generate() or regenerate();
	 */
	public Banana getValue() {
		return value;
	}
	
	/**
	 * Sets the banana.
	 * 
	 * @param	v	The Banana to set the value to.
	 */
	public void setValue(Banana v) {
		value = v;
	};
	
	/**
	 * Returns the negative log-likelihood of the current
	 * value of this Monkey.
	 * 
	 * @return	energy	The negative log-likelihood.
	 */
	public abstract double energy();
	
	/**
	 * Returns the negative log-likelihood of proposing the
	 * current value from a previous state fromvalue, given
	 * the current parameters.
	 * 
	 * @param	fromvalue	Previous value
	 * @return	energy		The negative log-likelihood
	 */
	public abstract double transitionEnergy(Banana fromvalue);
	
	/**
	 * Clones the monkey (value and parameters).
	 * 
	 * @return	cloned_monkey	Cloned monkey
	 */
	@Override
	public abstract Monkey<Banana> clone();
	
	/**
	 * Convenience function (for easier porting of Monkeys
	 * from old chimple).
	 * 
	 * @return	r	Random number generator
	 */
	public RandomPlus getRandom() {
		return zookeeper.random;
	};
}
