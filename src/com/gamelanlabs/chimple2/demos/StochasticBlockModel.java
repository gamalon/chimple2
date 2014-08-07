package com.gamelanlabs.chimple2.demos;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Stochastic block model in Chimple, ported from Allen Chen's
 * MATLAB code.
 * 
 * @author BenL
 * @author Allen Chen
 *
 */
public class StochasticBlockModel extends Demo {
	// SBM parameters
	protected int N = 128;
	protected double eps = 0.1;
	protected int d = 16;
	protected int q = 4;
	
	/**
	 * Constructor
	 * 
	 * @param	_N
	 * @param	_eps
	 * @param	_d
	 * @param	_q
	 */
	public StochasticBlockModel(int _N, double _eps, int _d, int _q) {
		setParams(_N, _eps, _d, _q);
	}
	
	public StochasticBlockModel() {}
	
	/**
	 * Set parameters
	 * 
	 * @param	_N
	 * @param	_eps
	 * @param	_d
	 * @param	_q
	 */
	public void setParams(int _N, double _eps, int _d, int _q) {
		N = _N;
		eps = _eps;
		d = _d;
		q = _q;
	}
	
	/**
	 * The probabilistic program
	 */
	@Override
	public Object run(Object ... args) {
		// Define variables
		int[][] truth = (int[][]) args[0];
		double[] n_a = new double[q];
		double[][] pmatrix = new double[q][q];
		double c_in = d*q/(eps*q-eps+1-q/N);
		double c_out = eps*c_in;
		
		// Initialize n_a and pmatrix
		for(int i = 0; i < q; i++) {
			n_a[i] = 1./q;
			for(int j = 0; j < q; j++) {
				if(i == j) {
					pmatrix[i][j] = c_in/N;
				} else {
					pmatrix[i][j] = c_out/N;
				}
			}
		}
		
		// Decide node types (from 0 to q-1)
		int[] nodetype = new int[N];
		for(int i = 0; i < N; i++) {
			nodetype[i] = chimpDiscrete("nodetypes"+i, n_a);
		}
		
		// Cost function
		double difference = 0;
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(truth[i][j] == 1) {
					difference -= Math.log(pmatrix[nodetype[i]][nodetype[j]]);
				} else {
					difference -= Math.log(1-pmatrix[nodetype[i]][nodetype[j]]);
				}
			}
		}
		addEnergy(difference);
		
		return difference;
	}
	
	/**
	 * Display results output by a sequence of program outputs.
	 * 
	 * @param	results		Results to display
	 */
	@Override
	public void display(ArrayList<Object> results) {
		System.out.println(results.get(0));
		double lastdifference = (double) results.get(results.size()-1);
		System.out.printf("Final logloss: %f\n", lastdifference);
	}
	
	/**
	 * Harness
	 * 
	 * @param	args
	 */
	public static void main(String[] args) {
		StochasticBlockModel program = new StochasticBlockModel();
		ArrayList<Object> results = Demo.runDemo(program, args);
		program.display(results);
	}

	/**
	 * Retrieve the default arguments with which this demo
	 * class should be instantiated. Can be null if the
	 * program does not use its arguments.
	 * 
	 * @return	args	Arguments
	 */
	@Override
	public Object[] getDefaultArguments() {
		// Argument is the adjacency matrix
		InputStream is = getClass().getResourceAsStream("/resources/StochasticBlockModelMatrix.ser");
		try {
			return new Object[] { (int[][]) new ObjectInputStream(is).readObject() };
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Override clone() from the superclass.
	 * 
	 * @return	clone
	 */
	@Override
	public StochasticBlockModel clone() {
		StochasticBlockModel p = (StochasticBlockModel) super.clone();
		p.setParams(N, eps, d, q);
		return p;
	}
	
	/**
	 * Returns a settings panel.
	 * 
	 * @return	panel
	 */
	@Override
	public JPanel getSettingsPanel() {
		// Outer panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		// Info
		panel.add(new JLabel("<html><body style='width: 125px'>" +
				"<b>Note</b>: The (huge) adjacency matrix that " +
				"this model conditions on is hard-coded.<br />" +
				"</html></body>"));
		
		// Inner panel
		JPanel innerPanel = new JPanel();
		JPanel middlePanel = new JPanel();
		middlePanel.add(innerPanel);
		panel.add(middlePanel);
		GroupLayout layout = new GroupLayout(innerPanel);
		innerPanel.setLayout(layout);
		
		// Auto-margin, auto-padding
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		// Setting pairs
		JLabel lblN = new JLabel("n");
		final JTextField txtN = new JTextField(Integer.toString(N), 10);
		txtN.setEnabled(false);
		
		JLabel lblE = new JLabel("epsilon");
		final JTextField txtE = new JTextField(Double.toString(eps), 10);
		txtE.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent arg0) { }
			@Override public void focusLost(FocusEvent arg0) {
				eps = Double.parseDouble(txtE.getText());
			}
		});
		
		JLabel lblD = new JLabel("d");
		final JTextField txtD = new JTextField(Integer.toString(d), 10);
		txtD.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent arg0) { }
			@Override public void focusLost(FocusEvent arg0) {
				d = Integer.parseInt(txtD.getText());
			}
		});
		
		JLabel lblQ = new JLabel("q");
		final JTextField txtQ = new JTextField(Integer.toString(q), 10);
		txtQ.addFocusListener(new FocusListener() {
			@Override public void focusGained(FocusEvent arg0) { }
			@Override public void focusLost(FocusEvent arg0) {
				q = Integer.parseInt(txtQ.getText());
			}
		});
		
		innerPanel.add(lblN);
		innerPanel.add(txtN);
		innerPanel.add(lblE);
		innerPanel.add(txtE);
		innerPanel.add(lblD);
		innerPanel.add(txtD);
		innerPanel.add(lblQ);
		innerPanel.add(txtQ);
		
		// Configure layout
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblN)
								.addComponent(txtN)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblE)
								.addComponent(txtE)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblD)
								.addComponent(txtD)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblQ)
								.addComponent(txtQ)
					)
			);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(lblN)
								.addComponent(lblE)
								.addComponent(lblD)
								.addComponent(lblQ)
					)
				.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(txtN)
								.addComponent(txtE)
								.addComponent(txtD)
								.addComponent(txtQ)
					)
			);
		
		return panel;
	}
}
