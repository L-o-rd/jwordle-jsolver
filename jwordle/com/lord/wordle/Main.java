package com.lord.wordle;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
	
	public static void main(String args[]) {
		
		if(args.length > 1) {
			System.out.println("Usage: -jar jwordle.jar <MMFile path/Optional>\n"
					+ "If a file name is given, one will be created, if it does not\n"
					+ "already exist. Otherwise, you can freely play on your own.");
			return;
		}
		
		SwingUtilities.invokeLater(() -> {
			String path = (args.length > 0) ? args[0] : null;
			JCanvas canvas = new JCanvas(path);
			JFrame frame = new JFrame(JCanvas.TITLE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent windowEvent) {
					canvas.stop();
					System.out.println("JWordle closed.");
				}
			});
			frame.add(canvas);
			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			canvas.start();
		});
		
	}
	
}
