package com.lord.solver;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private RandomAccessFile mmap;
	public MappedByteBuffer out;
	public WordArray array;
	
	public MainWindow(final String path, final String flag) {
		super("JWordle Solver");
		
		setup(path);
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Random Algorithm", null, new RandomPanel(600, 200, this), "Chooses a random word.");
		tabs.addTab("Information Theory Algorithm", null, new ShannonPanel(600, 200, this, ((flag != null && flag.equals("Y")) ? true : false)), "Chooses words based on their Shannon entropy.");
		tabs.addTab("Cumulative Product Algorithm", null, new ProductSolver(600, 200, this), "Chooses words based on their \"Score\".");
		this.setContentPane(tabs);
		
		this.setResizable(false);
		this.pack();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.requestFocus();
		
	}
	
	public void clean() {
		try {
			mmap.close();
			System.out.println("Pipe closed.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setup(final String path) {
		try {
			mmap = new RandomAccessFile(path, "rw");
			out = mmap.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 11);
			array = new WordArray("/list.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
