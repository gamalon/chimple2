package com.gamelanlabs.chimple2.demos;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import processing.core.PGraphics;

import com.gamelanlabs.chimple2.visualization.EmbedFrame;

/**
 * You must have Processing installed and added to your build path
 * to run this demo!
 * 
 * @author BenL
 * @author Matt Barr
 *
 */
public class Bezier extends Demo {
	private final int width = 1024;
	private final int height = 768;
	protected int numPoints = 100;
	//private final int eyePoints = 100;
	protected ScribbleDraw _gibberish;
	
	/**
	 * Constructor
	 * 
	 * @param	filename	Name of image file to fit curves to
	 */
	public Bezier(String filename) {
		_gibberish = new ScribbleDraw(filename);
	}
	
	public Bezier() {
		_gibberish = new ScribbleDraw("src/resources/benvigoda.jpg");

	}
	
	/**
	 * Resets the internal ScribbleDraw.
	 * 
	 * @param	filename	Name of image file to fit curves to
	 */
	public void reset(String filename) {
		_gibberish = new ScribbleDraw(filename);
	}
	
	/**
	 * Runs the probabilistic program.
	 * 
	 * @return	An array of points in the format (x1, y1, x2, y2)
	 * 			where (x1, y1) are anchor points and (x2, y2) are
	 * 			control points.
	 */
	@Override
	public Object run(Object ... args)  
	{
		int [][] points = new int[numPoints/*+eyePoints*/][4];
			
		for (int i=0; i<numPoints; i++) {
			// Anchor points of bezier - stay in window
			int x1 = (int) ((chimpRand("x1"+i) * (width + 1)));
		    int y1 = (int) ((chimpRand("y1"+i) * (height + 1)));
			
		    //Control points of bezier - larger area
		    //int x2 = (int) ((chimpRand("x2"+i) * 2*(width + 1)-width/2));
		    //int y2 = (int) ((chimpRand("y2"+i) * 2*(height + 1)-height/2));
		    int x2 = (int) ((chimpRand("x2"+i) * (width + 1)));
		    int y2 = (int) ((chimpRand("y2"+i) * (height + 1)));
		    
		    // Color and transparency - should be easy to turn on/off
		    //int c1 = (int) ((chimpRand("c1"+i) * 255));
		    //int c1 = 0;
		    //int alpha = (int) ((chimpRand("alpha"+i)*255));
		    //int alpha = 255;

		    points[i][0] = x1;
		    points[i][1] = y1;
		    points[i][2] = x2;
		    points[i][3] = y2;
		    //points[i][4] = c1;
		    //points[i][5] = alpha;
		}
			
		/*int noseWidth = (int) Math.abs(chimpRandn("noseWidth", (width/20), 0)); //really half a nose width
		int varSelfRelax = (int) Math.abs(chimpRandn("varSelfRelax", 0, width/20));
		int eyey = (int) ( chimpRandn("eyey", (double) (height/3), (double) (height/10)) );
		
		for (int i=numPoints; i<(numPoints+eyePoints); i++) {
				
			int eye1x = (int) (chimpRandn("eye1x"+i, (width/2)-noseWidth, varSelfRelax) );
			int eye2x = (int) (chimpRandn("eye2x"+i, (width/2)+noseWidth, varSelfRelax) );
			int eye1y = (int) (chimpRandn("eye1y"+i, eyey, varSelfRelax) );
			int eye2y = (int) (chimpRandn("eye2y"+i, eyey, varSelfRelax) );				
			
		    points[i][0] = eye1x;
		    points[i][1] = eye1y;
		    points[i][2] = eye2x;
		    points[i][3] = eye2y;
		}*/

		// Draw
		//_gibberish.draw(points, numPoints+eyePoints);
		_gibberish.draw(points, numPoints);
		
		double l2 = _gibberish.getl2();
		
		double selfInteraction = 0;
		double term=0;
		//int counter=0;
		double limit=3000;
		
		for (int j=0;j<numPoints;j++) {
			for (int k=(j+1);k<numPoints;k++) {
				term=limit/(Math.abs(Math.abs((points[j][0]-points[k][0]))+Math.abs((points[j][1]-points[k][1]))));
				term=Math.pow(term, 3);
				if (term<2000000000) {
					selfInteraction+=term;
					//counter++;
				}
			}
		}
		addEnergy((l2+selfInteraction)/100000000);
		
		//System.out.println("L2 "+l2);
		//System.out.println("Counter "+counter);
		//System.out.println("SI "+selfInteraction);

		return points;
	}
	
	/**
	 * Internal class to do the drawing.
	 * 
	 * @author Matt Barr
	 *
	 */
	protected class ScribbleDraw {
		protected int SQUARE = 0;

		protected EmbedFrame frameMain = new EmbedFrame();

		protected Canvas canvas = new Canvas();

		protected BufferStrategy buffer = null;
		
		protected GraphicsEnvironment ge = null;
		protected GraphicsDevice gd = null;
		protected GraphicsConfiguration gc = null;
		
		protected PGraphics g2d = null;
		protected Graphics graphicsContext = null;

		protected BufferedImage bi = null;
		protected BufferedImage targetImg = null;

		protected Color background = null;
		protected Random rand = null;

		// Variables for counting frames per seconds
		protected int fps = 0;
		protected int frames = 0;
		protected long totalTime = 0;
		protected long curTime = System.currentTimeMillis();
		protected long lastTime = curTime;
		
		public String filename;
		
		public int[] pixels;
		protected int[][] result;
		

		public ScribbleDraw(String f) {
			filename = f;

			frameMain.setIgnoreRepaint(true);
			frameMain.setDefaultCloseOperation(EmbedFrame.EXIT_ON_CLOSE);

			canvas.setIgnoreRepaint(true);
			canvas.setSize(1024, 768);

			frameMain.add(canvas);
			frameMain.pack();
			frameMain.setVisible(true);

			canvas.createBufferStrategy(2);
			buffer = canvas.getBufferStrategy();

			// Get graphics configuration...
			ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			gd = ge.getDefaultScreenDevice();
			gc = gd.getDefaultConfiguration();
			// Create off-screen drawing surface
			bi = gc.createCompatibleImage(1024, 768);
			
			// Objects needed for rendering...
			background = Color.WHITE;
			rand = new Random();

			BufferedImage tempImage = null;
			try {
				tempImage = ImageIO.read(new File(filename));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Using default image file.");
				try {
					tempImage = ImageIO.read(getClass().getResourceAsStream("/resources/benvigoda.jpg"));
				} catch(IOException e2) {
					JOptionPane.showMessageDialog(null, "Error loading default image file.");
					return;
				}
			}		
			targetImg = toCompatibleImage(tempImage);
		}

		public void draw(int[][] points, int numPoints) {

				g2d = frameMain.getEmbed(1024,768);

				// clear back buffer...
				g2d.fill(Color.WHITE.getRGB());;
				g2d.rect(0, 0, 1024, 768);
				
				
				int strokeSize = 6;
				
				
				g2d.beginDraw();
				g2d.strokeCap(SQUARE);
				

				for (int i = 1; i < numPoints; i++) {
					
					g2d.stroke(Color.black.getRGB(),40);
					g2d.strokeWeight(strokeSize);
					g2d.noFill();
					g2d.beginShape();
					//anchor point 1 prior
					int x10 = points[i-1][0];
					int y10 = points[i-1][1];
					//control point 1 prior
					int x20 = points[i-1][2];
					int y20 = points[i-1][3];
					//anchor point 2
					int x3 = points[i][0];
					int y3 = points[i][1];
					//control point 2
					int x4 = points[i][2];
					int y4 = points[i][3];
					
					int x1 = x10;int y1 = y10;
					int x2 = 2*x10 - x20; int y2 = 2*y10 - y20;
				
					g2d.bezier(x1,y1,x2,y2,x4,y4,x3,y3);
					g2d.endShape();
				}
				g2d.endDraw();

				// Put the result in the BufferedImage
				bi =  (BufferedImage) g2d.getNative();
				
				// Draw the bufferedImage into the canvas
					graphicsContext = buffer.getDrawGraphics();
					graphicsContext.drawImage(bi, 0, 0, null);
				if(tag != null) {
					graphicsContext.setFont(new Font("Consolas", Font.PLAIN, 72));
					graphicsContext.setColor(Color.RED);
					graphicsContext.drawString(tag, 25, 75);
				}

				if (!buffer.contentsLost())
					buffer.show();
				
				// count Frames per second...
				lastTime = curTime;
				curTime = System.currentTimeMillis();
				totalTime += curTime - lastTime;
				if (totalTime > 1000) {
					totalTime -= 1000;
					fps = frames;
					frames = 0;
					// display frames per second...
					System.out.printf("FPS %d\n", fps);
				}
				++frames;
				
				// Let the OS have a little time...
				Thread.yield();

		}
		
		public double getl2() {
			int width = bi.getWidth();
			int height = bi.getHeight();

			double l2 = 0;
			int[] arrowsBuf = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
			int[] targetBuf = ((DataBufferInt) targetImg.getRaster().getDataBuffer()).getData();
			int x, y;
			int r, g, b;
		     
			for (int i = 0; i < width * height; ++i) {
				x = arrowsBuf[i];
				y = targetBuf[i];
				b=Math.abs((x & 0xFF) - (y & 0xFF));
				g=Math.abs(((x & 0xFF00) >> 8) - ((y & 0xFF00) >> 8));
				r=Math.abs(((x & 0xFF0000) >> 16) - ((y & 0xFF0000) >> 16));        
				l2 += b*b + g*g +r*r;
			}
		   

			// NOW you can dump the graphics.
			if (g2d != null)
				g2d.dispose();
			if (graphicsContext != null)
				graphicsContext.dispose();
			return l2;
		}

		private BufferedImage toCompatibleImage(BufferedImage image)
		{
			// obtain the current system graphical settings
			GraphicsConfiguration gfx_config = GraphicsEnvironment.
				getLocalGraphicsEnvironment().getDefaultScreenDevice().
				getDefaultConfiguration();
	
			/*
			 * if image is already compatible and optimized for current system 
			 * settings, simply return it
			 */
			if (image.getColorModel().equals(gfx_config.getColorModel()))
				return image;
	
			// image is not optimized, so create a new image that is
			BufferedImage new_image = gfx_config.createCompatibleImage(
					image.getWidth(), image.getHeight(), image.getTransparency());
	
			// get the graphics context of the new image to draw the old image on
			Graphics2D g2d = (Graphics2D) new_image.getGraphics();
	
			// actually draw the image and dispose of context no longer needed
			g2d.drawImage(image, 0, 0, null);
			g2d.dispose();
	
			// return the new optimized image
			return new_image; 
		}
	}

	/**
	 * "Harness" function.
	 * 
	 * @param	args	Command-line arguments
	 */
	public static void main(String [] args) {
		Demo.runDemo(new Bezier(), args);
	}
	
	/**
	 * Display results output by a sequence of program outputs.
	 * 
	 * @param	results		Results to display
	 */
	@Override
	public void display(ArrayList<Object> results) { }

	/**
	 * Override clone() from superclass.
	 * 
	 * @return	clone
	 */
	@Override
	public Bezier clone() {
		Bezier p = (Bezier) super.clone();
		p.reset(_gibberish.filename);
		return p;
	}
	
	/**
	 * Perform cleanup (ie. close any GUI windows this program created, etc).
	 */
	@Override
	public void cleanup() {
		super.cleanup();
		_gibberish.frameMain.setVisible(false);
		_gibberish.frameMain.dispose();
	}
	
	/**
	 * Settings panel
	 * 
	 * @return	panel
	 */
	@Override
	public JPanel getSettingsPanel() {
		final JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		// Auto-margin, auto-padding
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		// File
		JLabel lblFile = new JLabel("File");
		final JTextField txtFile = new JTextField(_gibberish.filename, 20);
		txtFile.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) { }

			@Override
			public void focusLost(FocusEvent e) {
				String str = txtFile.getText();
				if(!new File(str).isFile()) {
					JOptionPane.showMessageDialog(panel, "\""+str+"\" is not a valid file!");
					txtFile.setText(_gibberish.filename);
				} else {
					_gibberish.frameMain.setVisible(false);
					_gibberish.frameMain.dispose();
					reset(str);
				}
			}
		});
		
		// Number of points
		JLabel lblNP = new JLabel("Num. points");
		final JTextField txtNP = new JTextField(Integer.toString(numPoints), 10);
		txtNP.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) { }

			@Override
			public void focusLost(FocusEvent e) {
				String str = txtNP.getText();
				try {
					numPoints = Integer.parseInt(str);
				} catch(NumberFormatException ex) {
					JOptionPane.showMessageDialog(panel, "\""+str+"\" is not a valid integer!");
					txtFile.setText(Integer.toString(numPoints));
				}
			}
		});
		
		panel.add(lblFile);
		panel.add(txtFile);
		panel.add(lblNP);
		panel.add(txtNP);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblFile)
						.addComponent(txtFile)
					)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lblNP)
						.addComponent(txtNP)
					)
			);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lblFile)
						.addComponent(lblNP)
					)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(txtFile)
						.addComponent(txtNP)
					)
			);
		
		return panel;
	}
}
