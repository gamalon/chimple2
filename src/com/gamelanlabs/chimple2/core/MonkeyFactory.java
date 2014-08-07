package com.gamelanlabs.chimple2.core;

import com.gamelanlabs.chimple2.monkeys.Monkey;

/**
 * The interface for monkey-making.
 * 
 * @author BenL
 *
 */
public interface MonkeyFactory {
	/**
	 * Some of this code is MH-specific and should probably be moved out.
	 * 
	 * @param	type
	 * @param	name
	 * @param	params
	 * @return	banana
	 */
	public abstract <Banana> Banana makeMonkey(
			Class<? extends Monkey<Banana>> type,
			String name,
			Object... params);
}
