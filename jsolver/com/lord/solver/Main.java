package com.lord.solver;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
	
	public static void main(String args[]) {
		
		if(args.length < 1 || args.length > 2) {
			System.out.println("Usage: -jar solver.jar <Connect File Path> (Y)<Optional Optimize Flag>\n\n" +
			"If you don't have a file, just give a name and one will be created.\n" +
			"Just make sure JWordle and Solver are in the same directory.\n\n" +
			"Example: -jar solver.jar file.txt Y");
			return;
		}
		
		System.out.println("Connected to MMFile: " + args[0]);
		
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(() -> {
			MainWindow window = new MainWindow(args[0], (args.length == 2) ? args[1] : null);
			window.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent windowEvent) {
					window.clean();
					System.out.println("Solver closed.");
				}
			});
		});

		
	}
	
}
