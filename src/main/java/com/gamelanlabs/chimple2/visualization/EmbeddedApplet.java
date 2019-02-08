package com.gamelanlabs.chimple2.visualization;

import processing.core.PApplet;

/**
 * Wrapper class for Processing.
 * 
 * @author BenL
 *
 */
public class EmbeddedApplet extends PApplet {
	private static final long serialVersionUID = -7800180921794962864L;

	@Override
	public void draw() { }
	
	public void setup(int x, int y) {
		size(1024, 768);
		// Prevent thread from starving everything else
		noLoop();
	}
}
