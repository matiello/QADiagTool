package com.insightx.tools.diagnostic;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

public class AppGUI {

	private JFrame frmQntDiagnostics;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppGUI window = new AppGUI();
					window.frmQntDiagnostics.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQntDiagnostics = new JFrame();
		frmQntDiagnostics.setTitle("QNT Diagnostics");
		frmQntDiagnostics.setBounds(100, 100, 800, 600);
		frmQntDiagnostics.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmQntDiagnostics.getContentPane().setLayout(null);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 537, 764, 14);
		frmQntDiagnostics.getContentPane().add(progressBar);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Save Scenario");
		chckbxNewCheckBox.setBounds(10, 443, 97, 23);
		frmQntDiagnostics.getContentPane().add(chckbxNewCheckBox);
		
		JTextArea txtrScenarioName = new JTextArea();
		txtrScenarioName.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtrScenarioName.setText("<Scenario Name>");
		txtrScenarioName.setBounds(113, 442, 267, 23);
		frmQntDiagnostics.getContentPane().add(txtrScenarioName);
		
		JButton btnRun = new JButton("Run");
		btnRun.setBounds(10, 476, 89, 23);
		frmQntDiagnostics.getContentPane().add(btnRun);
		
		JMenuBar menuBar = new JMenuBar();
		frmQntDiagnostics.setJMenuBar(menuBar);
		
		JMenu mnDiagnostic = new JMenu("Diagnostic");
		menuBar.add(mnDiagnostic);
		
		JMenuItem mntmStartANew = new JMenuItem("Start a new diagnostic");
		mnDiagnostic.add(mntmStartANew);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		
		JMenuItem mntmPluginsLoaded = new JMenuItem("Plugins Loaded");
		mnHelp.add(mntmPluginsLoaded);
	}
}
