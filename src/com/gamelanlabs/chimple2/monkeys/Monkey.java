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
	/**
	 * The local banana broker.
	 */
	private Zookeeper zookeeper = null;
	
	/**
	 * Make sure to pet your monkeys regularly.
	 */
	public boolean touched = false;
	
	/**
	 * The banana.
	 */
	protected Banana value = null;
	
	/**
	 * Shows the monkey who's boss.
	 * 
	 * @param	z		The Zookeeper that has all the bananas
	 */
	public void setZookeeper(Zookeeper z) {
		zookeeper = z;
	}
	
	/**
	 * Gets the banana, safely (ie. not by reference).
	 * 
	 * Please override this function if Banana is not
	 * immutable. Otherwise, unexpected behavior may
	 * occur and monkey anarchy will ensue.
	 * 
	 * @return	value	The Banana previously generated
	 * 					by generate() or regenerate();
	 */
	public Banana getValue() {
		return value;
	}
	
	/**
	 * Retrieves the banana plantation.
	 * 
	 * @return	r	Random number generator
	 */
	public RandomPlus getRandom() {
		return zookeeper.random;
	}
	
	/**
	 * Asks the monkey if the Banana recipe changed.
	 * 
	 * @param	newparams
	 * @return	changed
	 */
	public abstract boolean paramsChanged(Object... newparams);
	
	/**
	 * Tells the monkey how to make Bananas.
	 * 
	 * Makes a safe copy of the instructions (ie. not
	 * by reference).
	 * 
	 * @param	params		Parameters
	 */
	public abstract void setParams(Object... params);
	
	/**
	 * Asks the monkey about how to make Bananas.
	 * 
	 * Returns an UNSAFE copy of the instructions (ie.
	 * possibly by reference).
	 * 
	 * @return	params		Parameters
	 */
	protected abstract Object[] getParams();
	
	/**
	 * Generates from the prior.
	 * 
	 * @return	value	A Banana from the prior distribution
	 * 					of this Monkey.
	 */
	public abstract Banana generate();
	
	/**
	 * Generates from the proposal kernel (ie. possibly
	 * depending on the previous value).
	 * 
	 * @return	value	A proposal Banana
	 */
	public abstract Banana propose();
	
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
	public Monkey<Banana> clone() {
		Monkey<Banana> dolly;
		
		// Have to cast from Class<? extends Monkey> to
		// Class<? extends Monkey<Banana>> here -- if
		// raw types are used this will throw
		// an exception.
		@SuppressWarnings("unchecked")
		Class<? extends Monkey<Banana>> type =
				(Class<? extends Monkey<Banana>>)
				getClass();
		
		try {
			dolly = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		dolly.setZookeeper(zookeeper);
		dolly.setParams(getParams());
		dolly.value = getValue();
		return dolly;
	}
}
