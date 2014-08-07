package com.gamelanlabs.chimple2.util;

import com.gamelanlabs.chimple2.core.MonkeyFactory;
import com.gamelanlabs.chimple2.core.Zookeeper;
import com.gamelanlabs.chimple2.monkeys.Monkey;

/**
 * Provides some common functions for Metropolis-Hastings
 * based solvers.
 * 
 * @author BenL
 *
 */
public abstract class MHUtils {
	/**
	 * Monkey factory that implements the "weak stochastic reuse"
	 * memoization strategy.
	 * 
	 * @author BenL
	 *
	 */
	public static class WeakMonkeyFactory implements MonkeyFactory {
		/**
		 * The factory manager who (spoiler!) is also secretly
		 * the zookeeper. In the climax it turns out that he's
		 * having an affair with the solver and after a wild car
		 * chase he gets arrested for animal abuse (but comes back
		 * in the sequel with evil superpowers).
		 */
		protected final Zookeeper zookeeper;
		
		/**
		 * Constructor
		 * 
		 * @param	z
		 */
		public WeakMonkeyFactory(Zookeeper z) {
			zookeeper = z;
		}
		
		/**
		 * Uses the "weak stochastic reuse" memoization strategy.
		 * 
		 * @param	type
		 * @param	name
		 * @param	params
		 * @return	banana
		 */
		@Override
		public <Banana> Banana makeMonkey(
				Class<? extends Monkey<Banana>> type,
				String name, Object... params) {
			Monkey<Banana> m = null;
			
			try {
				m = type.cast(zookeeper.cage.get(name));
			} catch(ClassCastException e) {
				zookeeper.cage.del(zookeeper.cage.get(name));
			}
			
			if(m == null || m.paramsChanged(params)) {
				// This monkey needs to be regenerated from the prior,
				// either because it didn't exist before, or because
				// an upstream monkey was proposed, causing the parameter
				// of this monkey to be changed.
				try {
					m = type.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
				m.setZookeeper(zookeeper);
				m.setParams(params);
				m.touched = true;
				zookeeper.cage.add(name, m);
				return m.generate();
			} else {
				// Use the memoized value.
				m.touched = true;
				return m.getValue();
			}
		}
	}
	
	
	/**
	 * Monkey factory that implements the "strong stochastic reuse"
	 * memoization strategy.
	 * 
	 * @author BenL
	 *
	 */
	public static class StrongMonkeyFactory implements MonkeyFactory {
		/**
		 * See above.
		 */
		protected final Zookeeper zookeeper;
		
		/**
		 * Constructor
		 * 
		 * @param	z
		 */
		public StrongMonkeyFactory(Zookeeper z) {
			zookeeper = z;
		}
		
		/**
		 * Uses the "strong stochastic reuse" memoization strategy.
		 * 
		 * @param	type
		 * @param	name
		 * @param	params
		 * @return	banana
		 */
		@Override
		public <Banana> Banana makeMonkey(
				Class<? extends Monkey<Banana>> type,
				String name, Object... params) {
			Monkey<Banana> m = null;
			
			try {
				m = type.cast(zookeeper.cage.get(name));
			} catch(ClassCastException e) {
				zookeeper.cage.del(zookeeper.cage.get(name));
			}
			
			if(m == null) {
				// This monkey needs to be regenerated from the prior,
				// because it didn't exist before.
				try {
					m = type.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
				m.setZookeeper(zookeeper);
				m.setParams(params);
				m.touched = true;
				zookeeper.cage.add(name, m);
				return m.generate();
			} else {
				// Use the memoized value.
				m.touched = true;
				return m.getValue();
			}
		}
	}
	
	
	/**
	 * Monkey factory that always generates from the prior & ignores the cage.
	 * 
	 * @author BenL
	 *
	 */
	public static class NaiveMonkeyFactory implements MonkeyFactory {
		/**
		 * See above.
		 */
		protected final Zookeeper zookeeper;
		
		/**
		 * Constructor
		 * 
		 * @param	z
		 */
		public NaiveMonkeyFactory(Zookeeper z) {
			zookeeper = z;
		}
		
		/**
		 * Ignores the cage.
		 * 
		 * @param	type
		 * @param	name
		 * @param	params
		 * @return	banana
		 */
		@Override
		public <Banana> Banana makeMonkey(
				Class<? extends Monkey<Banana>> type,
				String name, Object... params) {
			Monkey<Banana> m;
			try {
				m = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
			m.setZookeeper(zookeeper);
			m.setParams(params);
			return m.generate();
		}
	}
}
