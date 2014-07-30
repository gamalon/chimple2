package com.gamelanlabs.chimple2.demos;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import com.gamelanlabs.chimple2.core.ChimpleProgram;
import com.gamelanlabs.chimple2.solvers.Solver;
import com.gamelanlabs.chimple2.util.StreamCapture;

/**
 * Demo chooser GUI. Launched by default when you double-click the
 * chimplecrowd JAR file.
 * 
 * @author BenL
 *
 */
public class DemoChooser extends JFrame {
	private static final long serialVersionUID = 8678828390748152321L;
	
	// Swing components
	protected JButton btnRun;
	protected JTextField[] txtFields;
	protected JComboBox<String> solverName;
	protected JComboBox<String> programName;
	protected JPanel pnlProgramOptions;
	protected JPanel pnlSolverOptions;
	
	// Active program and solver
	protected Demo activeProgram;
	protected Solver activeSolver;
	
	/**
	 * Constructor
	 */
	public DemoChooser() {
		super("CHIMPLE | Demos");
		getContentPane().setPreferredSize(new Dimension(640, 480));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel pnlSettings = new JPanel();
		pnlSettings.setBorder(new EmptyBorder(20, 20, 20, 20));
		getContentPane().add(pnlSettings, BorderLayout.NORTH);
		pnlSettings.setLayout(new BoxLayout(pnlSettings, BoxLayout.X_AXIS));
		
		JPanel pnlProgram = new JPanel();
		pnlProgram.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new EmptyBorder(5, 5, 5, 5)));
		pnlSettings.add(pnlProgram);
		
		programName = new JComboBox<String>();
		programName.setMaximumSize(new Dimension(32767, 20));
		programName.setAlignmentY(Component.TOP_ALIGNMENT);
		programName.setModel(new DefaultComboBoxModel<String>(new String[] {
				"Sudoku",
				"CompareSamplers",
				"Bezier",
				"RandomCoin",
				"LatentDirichletAllocation",
				"StochasticBlockModel",
				"Bits"
			}));
		programName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateProgramPanel();
					}
				});
			}  
		});
		pnlProgram.setLayout(new BoxLayout(pnlProgram, BoxLayout.Y_AXIS));
		
		pnlProgram.add(programName);
		
		Component verticalStrut_2 = Box.createVerticalStrut(10);
		pnlProgram.add(verticalStrut_2);
		
		pnlProgramOptions = new JPanel();
		pnlProgram.add(pnlProgramOptions);
		
		// JPanel at bottom to suck up space
		pnlProgram.add(new JPanel());
		
		updateProgramPanel();
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		pnlSettings.add(rigidArea);
		
		JPanel pnlSolver = new JPanel();
		pnlSolver.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new EmptyBorder(5, 5, 5, 5)));
		pnlSettings.add(pnlSolver);
		pnlSolver.setLayout(new BoxLayout(pnlSolver, BoxLayout.Y_AXIS));
		
		solverName = new JComboBox<String>();
		solverName.setMaximumSize(new Dimension(32767, 20));
		solverName.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlSolver.add(solverName);
		solverName.setModel(new DefaultComboBoxModel<String>(new String[] {
				"MetropolisHastingsSolver",
				"PriorSolver"
			}));
		solverName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateSolverPanel();
					}
				});
			}  
		});
		
		Component verticalStrut = Box.createVerticalStrut(10);
		pnlSolver.add(verticalStrut);
		
		pnlSolverOptions = new JPanel();
		pnlSolver.add(pnlSolverOptions);
		
		// JPanel at bottom to suck up space
		pnlSolver.add(new JPanel());
		
		updateSolverPanel();
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		pnlSettings.add(rigidArea_1);
		btnRun = new JButton("Run");
		btnRun.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlSettings.add(btnRun);
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new SwingWorker<Void, Void>() {
					@Override
					public Void doInBackground() {
						chimplify();
						return null;
					}
				}).execute();
			}
		});
		getRootPane().setDefaultButton(btnRun);
		
		// Make STDOUT/STDERR also output to a GUI panel
		JTextArea log = new JTextArea();
		log.setEditable(false);
		JScrollPane scroll = new JScrollPane(log);
		getContentPane().add(scroll);
		
		// Hook STDOUT/STDERR
		System.setOut(new PrintStream(new StreamCapture("STDOUT", log, System.out)));
		System.setErr(new PrintStream(new StreamCapture("STDERR", log, System.err)));
		
		pack();
		setVisible(true);
	}
	
	/**
	 * Updates the solver settings panel.
	 */
	public void updateSolverPanel() {
		String solverStr = (String) solverName.getSelectedItem();
		String[] optionNames = new String[] {};
		String[] optionDefaults = new String[] {};
		try {
			Class<?> solverClass = Class.forName(
					"com.gamelanlabs.chimple2.solvers." + solverStr);
			
			Method method = solverClass.getMethod("getArgumentNames");
			optionNames = (String[]) method.invoke(null);
			
			method = solverClass.getMethod("getDefaultArguments");
			Object[] objOptionDefaults = (Object[]) method.invoke(null);
			optionDefaults = new String[objOptionDefaults.length];
			for(int i = 0; i < objOptionDefaults.length; i++) {
				optionDefaults[i] = objOptionDefaults[i].toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Set up panel
		pnlSolverOptions.removeAll();
		JPanel panel = new JPanel();
		pnlSolverOptions.add(panel);
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		
		// Auto-margin, auto-padding
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		// Create alignment groups
		GroupLayout.Group v = layout.createSequentialGroup();
		GroupLayout.Group h = layout.createSequentialGroup();
		GroupLayout.Group left = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.Group right = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		// Clear list of JTextFields
		txtFields = new JTextField[optionNames.length];
		
		// Add a label and text field for each option
		for(int i = 0; i < optionNames.length; i++) {
			String s = optionNames[i];
			
			JLabel lbl = new JLabel(s);
			panel.add(lbl);
			
			JTextField txt = new JTextField(optionDefaults[i], 10);
			panel.add(txt);
			txtFields[i] = txt;
			
			v.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(lbl).addComponent(txt));
			left.addComponent(lbl);
			right.addComponent(txt);
		}
		
		// Set alignment groups
		h.addGroup(left).addGroup(right);
		layout.setVerticalGroup(v);
		layout.setHorizontalGroup(h);

		// Redraw
		revalidate();
		repaint();
	}
	
	/**
	 * Updates the program settings panel.
	 */
	public void updateProgramPanel() {
		// Clear the previous options panel
		pnlProgramOptions.removeAll();
		
		// Cleanup the previous program
		if(activeProgram != null) {
			activeProgram.cleanup();
		}
		
		// Tell the program to manufacture its options panel
		String programStr = (String) programName.getSelectedItem();
		JPanel panel;
		try {
			Class<?> programClass = Class.forName(
					"com.gamelanlabs.chimple2.demos." + programStr);
			activeProgram = (Demo) programClass.newInstance();
			Method method = programClass.getMethod("getSettingsPanel");
			panel = (JPanel) method.invoke(activeProgram, new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// Add the options panel
		pnlProgramOptions.add(panel);
		revalidate();
		repaint();
	}
	
	/**
	 * Fetches (and type-converts) the solver arguments from the
	 * solver settings panel.
	 * 
	 * @return	arguments
	 */
	public Object[] getSolverArguments() {
		String solverStr = (String) solverName.getSelectedItem();
		Object[] r = new Object[txtFields.length];
		
		// Get types
		Class<?>[] paramTypes = new Class<?>[0];
		try {
			Class<?> solverClass = Class.forName(
					"com.gamelanlabs.chimple2.solvers." + solverStr);
			boolean success = false;
			for(Method m : solverClass.getMethods()) {
				paramTypes = ClassUtils.primitivesToWrappers(m.getParameterTypes());
				if(paramTypes.length == txtFields.length) {
					success = true;
					break;
				}
			}
			if(!success) {
				throw new RuntimeException("Could not find a solve() " +
						"with the correct number of arguments!");
			}
		
			for(int i = 0; i < txtFields.length; i++) {
				if(paramTypes[i].equals(Integer.class)) {
					r[i] = Integer.parseInt(txtFields[i].getText());
				} else if(paramTypes[i].equals(Double.class)) {
					r[i] = Double.parseDouble(txtFields[i].getText());
				} else if(paramTypes[i].equals(Boolean.class)) {
					r[i] = Boolean.parseBoolean(txtFields[i].getText());
				} else if(paramTypes[i].equals(Byte.class)) {
					r[i] = Byte.parseByte(txtFields[i].getText());
				} else throw new RuntimeException(
						"Unsupported parameter type <" +
						paramTypes[i] + "> for solver " +
						solverStr + "!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
	
	/**
	 * Runs the selected solver with the selected program and the settings
	 * entered for both.
	 */
	public void chimplify() {
		if(activeSolver != null) {
			activeSolver.cleanup();
		}
		String programStr = (String) programName.getSelectedItem();
		String solverStr = (String) solverName.getSelectedItem();
		System.out.printf("Chimplifying %s with %s...\n\n",
				programStr, solverStr);
		btnRun.setEnabled(false);
		try {
			Class<?> solverClass = Class.forName(
					"com.gamelanlabs.chimple2.solvers." + (String)
					solverName.getSelectedItem());
			activeSolver = (Solver) ConstructorUtils.invokeConstructor(solverClass,
					new Object[] {activeProgram, activeProgram.getDefaultArguments(),
					activeProgram.getDefaultCostFunction()});
			ChimpleProgram.tic();
			ArrayList<Object> results = Demo.callSolve(activeSolver,
					getSolverArguments());
			ChimpleProgram.tocPrint();
			activeProgram.display(results);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.print("Done!\n\n");
		btnRun.setEnabled(true);
	}
	
	/**
	 * Program entry point.
	 * 
	 * @param	args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {}
		new DemoChooser();
	}
}
