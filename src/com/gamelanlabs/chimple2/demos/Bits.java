package com.gamelanlabs.chimple2.demos;

import java.util.ArrayList;

/**
 * This demo performs a series of flips to determine N bits (0 or 1)
 * and converts the binary string into a decimal number.
 * 
 * @author BenL
 *
 */
public class Bits extends Demo {
	/**
	 * Number of bits
	 */
	public static final int N = 8;
	
	/**
	 * Run the probabilistic program.
	 * 
	 * @param	args	Arguments (one element: the XYSeries)
	 * @return	sum		The decimal value of the bits.
	 */
	@Override
	public Object run(Object ... args) {
		// Do N flips and convert the bytes to a decimal
		int[] a = new int[N];
		int sum = 0;
		for(int i = 0; i < N; i++) {
			a[i] = chimpFlip("a"+i, 0.5);
			sum += a[i] << i;
		}
		
		// Squared numeric difference from "01111... 1"
		//addCost(Math.pow((sum - (1<<(N-1))-1), 2));
		
		// XOR cost function (has desired nonsmooth/chaotic behavior)
		addEnergy((sum - (1<<(N-1))-1) ^ 2);
		
		return (double) sum;
	}
	
	/**
	 * Harness
	 * 
	 * @param	args
	 */
	public static void main(String[] args) {
		Demo.runDemo(new Bits(), args);
	}
	
	/**
	 * Display results output by a sequence of program outputs.
	 * 
	 * @param	results		Results to display
	 */
	@Override
	public void display(ArrayList<Object> results) { }
}
