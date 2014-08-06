package com.gamelanlabs.chimple2.demos;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.SwingChart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Highly-correlated bivariate normal distribution (cf. FHLG13).
 * 
 * @author BenL
 *
 */
public class Anisotropic extends Demo {
	protected double epsilon;
	protected boolean gui = false;
	protected JFrame frame;
	protected Chart chart;
	protected JLabel pct;
	protected Point lastpoint;
	protected int good;
	protected int bad;
	
	/**
	 * Constructor
	 * 
	 * @param	_epsilon
	 */
	public Anisotropic(double _epsilon) {
		epsilon = _epsilon;
	}
	
	public Anisotropic() {
		this(0.01);
	}
	
	/**
	 * Shows GUI.
	 */
	public void enableGUI() {
		if(gui) return;
		gui = true;
		System.out.println("Loading 3D plot...");
		
		// Create surface of PDF
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return Math.exp(-pdf(x, y));
			}
		};
		Range range = new Range(-2, 2);
		int steps = 10;
		Shape surface = Builder.buildOrthonormal(
				new OrthonormalGrid(range, steps, range, steps), mapper);
		ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(),
				surface.getBounds().getZmin(), surface.getBounds().getZmax(),
				new Color(1, 1, 1, .5f));
		surface.setColorMapper(colorMapper);
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
		surface.setWireframeColor(Color.GRAY);
		
		// Create chart
		chart = new SwingChart(Quality.Advanced);
		chart.getScene().getGraph().add(surface);
		chart.addController(new AWTCameraKeyController());
		
		// Create JLabels
		Font bigfont = new Font("Sans-Serif", Font.BOLD, 36);
		Font font = new Font("Sans-Serif", Font.PLAIN, 24);
		pct = new JLabel("---");
		pct.setFont(font);
		pct.setOpaque(false);
		
		JLabel title = new JLabel("Anisotropic");
		title.setFont(bigfont);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setOpaque(false);
		
		JLabel eps = new JLabel("Epsilon: "+Double.toString(epsilon));
		eps.setFont(font);
		eps.setHorizontalAlignment(SwingConstants.RIGHT);
		eps.setOpaque(false);
		
		// Create panel
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel header = new JPanel();
		header.setLayout(new GridLayout(1, 3, 0, 0));
		header.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
						BorderFactory.createEmptyBorder(10, 30, 10, 30)));
		header.add(pct);
		header.add(title);
		header.add(eps);
		panel.add(header, BorderLayout.NORTH);
		panel.add((Component) chart.getCanvas(), BorderLayout.CENTER);
		
		// Create frame
		frame = new JFrame("Anisotropic");
		frame.getContentPane().add(panel);
		frame.setSize(768, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		System.out.println("Done loading 3D plot!");
	}

	/**
	 * Implement Demo interface
	 * 
	 * @param	results
	 */
	@Override
	public void display(ArrayList<Object> results) { }
	
	/**
	 * Returns a settings panel.
	 * 
	 * @return	panel
	 */
	@Override
	public JPanel getSettingsPanel() {
		final JPanel p = new JPanel();
		
		JLabel lblGUI = new JLabel("GUI");
		final JCheckBox cbGUI = new JCheckBox("Enable");
		p.add(lblGUI, cbGUI);
		
		JLabel lblE = new JLabel("Epsilon");
		final JTextField txtE = new JTextField(Double.toString(epsilon));
		
		cbGUI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cbGUI.isSelected()) {
					new Thread() {
						@Override
						public void run() {
							enableGUI();
						}
					}.start();
					cbGUI.setEnabled(false);
					txtE.setEnabled(false);
				}
			}
		});
		txtE.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent arg0) { }

			@Override
			public void focusLost(FocusEvent arg0) {
				try {
					epsilon = Double.parseDouble(txtE.getText());
				} catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(null, "Invalid double value!");
					txtE.setText(Double.toString(epsilon));
				}
			}
		});
		p.add(lblE, txtE);
		
		// Create layout
		GroupLayout layout = new GroupLayout(p);
		p.setLayout(layout);
		
		// Auto-margin, auto-padding
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblGUI)
								.addComponent(cbGUI)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblE)
								.addComponent(txtE)
					)
			);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(lblGUI)
								.addComponent(lblE)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(cbGUI)
								.addComponent(txtE)
					)
			);
		
		return p;
	}

	/**
	 * The probabilistic program.
	 * 
	 * @param	args
	 * @return	output
	 */
	@Override
	public Object run(Object... args) {
		double[] x = new double[2];
		x[0] = chimpRand("u0")*4 - 2;
		x[1] = chimpRand("u1")*4 - 2;
		addEnergy(pdf(x));
		updatePosition(x);
		return x;
	}

	/**
	 * Updates the position of the current trace on the 3D plot.
	 * 
	 * @param	x
	 */
	protected void updatePosition(double... x) {
		if(!gui) return;
		
		Coord3d coords = new Coord3d(x[0], x[1], Math.exp(-pdf(x)));
		
		if(lastpoint != null) {
			lastpoint.setWidth(2.0f);
			if(lastpoint.getCoord().z > 0.1) {
				lastpoint.setColor(Color.BLACK);
				++good;
			} else {
				lastpoint.setColor(Color.WHITE);
				++bad;
			}
		}
		lastpoint = new Point(coords);
		lastpoint.setWidth(5.0f);
		lastpoint.setColor(Color.RED);
		chart.getScene().getGraph().add(lastpoint);
		if(good+bad > 0) {
			pct.setText(String.format("Blk pts: %.2f%%", ((double) (100*good))/(good+bad)));
		}
	}

	/**
	 * PDF
	 * 
	 * @param	x			Value
	 * @return	energy		Negative log-likelihood + C
	 */
	protected double pdf(double... x) {
		double sum = x[0] + x[1];
		double diff = x[0] - x[1];
		return (diff*diff/epsilon + sum*sum)/2;
	}
	
	/**
	 * Command-line harness.
	 * 
	 * @param	args
	 */
	public static void main(String[] args) {
		Anisotropic p = new Anisotropic();
		p.enableGUI();
		Demo.runDemo(p, args);
	}
	
	/**
	 * Clean up GUI objects.
	 */
	@Override
	public void cleanup() {
		super.cleanup();
		if(gui) {
			frame.setVisible(false);
			chart.dispose();
			frame.dispose();
		}
	}
}
