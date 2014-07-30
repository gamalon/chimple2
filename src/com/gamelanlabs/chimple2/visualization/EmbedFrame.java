package com.gamelanlabs.chimple2.visualization;

import javax.swing.JFrame;

import processing.core.PApplet;
import processing.core.PGraphics;

import com.gamelanlabs.chimple2.visualization.EmbeddedApplet;

/**
 * GUI window class for use with Processing.
 * 
 * @author BenL
 *
 */
public class EmbedFrame extends JFrame {
	private static final long serialVersionUID = 7356646222071084182L;
	public static PApplet embed = null;
	
	public EmbedFrame() {
		super("Processing");
		embed = new EmbeddedApplet();
		embed.init();
	}
	
	public PGraphics getEmbed(int w, int h) {
		return embed.createGraphics(w, h);
	}
}
