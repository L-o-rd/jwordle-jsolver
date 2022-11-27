package com.lord.wordle;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class JCanvas extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH = 320;
	public static final int HEIGHT = 180;
	public static final int SCALE = 4;
	public static final String TITLE = "JWordle";
	
	public int ticks, frames;
	
	private boolean running;
	private Thread thread;
	
	private BufferedImage image;
	private int pixels[];
	
	private Game game;
	private Screen screen;
	
	private InputHandler input;
	public RandomAccessFile mmap;
	public boolean playing;
	
	public JCanvas(final String path) {
		
		playing = true;
		
		if(path != null) {
			try {
				mmap = new RandomAccessFile(path, "rw");
				playing = false;
				System.out.println("MMFile opened.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Dimension dim = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		
		this.setMinimumSize(dim);
		this.setPreferredSize(dim);
		this.setMaximumSize(dim);
		
		input = new InputHandler();
		game = new Game(input, playing, mmap);
		screen = new Screen(WIDTH, HEIGHT);
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		
		addKeyListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
	}
	
	public synchronized void start() {
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void tick() {
		if(playing) {
			if(hasFocus()) {
				game.tick();
				if(game.finished && screen.shouldClear) {
					Arrays.fill(screen.pixels, 0);
					screen.shouldClear = false;
				}
				else if(!game.finished) screen.shouldClear = true;
			}
		} else {
			game.tick();
			if(game.finished && screen.shouldClear) {
				Arrays.fill(screen.pixels, 0);
				screen.shouldClear = false;
			}
			else if(!game.finished) screen.shouldClear = true;
		}
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		
		if(bs == null) {
			createBufferStrategy(3);
			requestFocus();
			return;
		}
		
		if(screen.shouldClear) Arrays.fill(screen.pixels, 0);
		
		screen.render(game);
		
		System.arraycopy(screen.pixels, 0, pixels, 0, pixels.length);
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		
		g.dispose();
		bs.show();
	}
	
	public void run() {
		long now, then, last = System.currentTimeMillis();
		double unprocessed = 0.0;
		final double ns_per_tick = 1000000000.0 / 60.0;
		then = System.nanoTime();
		boolean shouldRender = false;
		
		while(this.running) {
			shouldRender = false;
			now = System.nanoTime();
			unprocessed += (now - then);
			then = now;
			
			while(unprocessed >= ns_per_tick) {
				tick();
				shouldRender = true;
				++ticks;
				unprocessed -= ns_per_tick;
			}
			
			if(shouldRender) {
				render();
				++frames;
			}
			
			if(System.currentTimeMillis() > last + 1000) {
				ticks = frames = 0;
				last += 1000;
			}
		}
	}
	
	public synchronized void stop() {
		if(!running) return;
		running = false;
		
		try {
			thread.join();
			if(mmap != null) {
				mmap.close();
				System.out.println("MMFile closed.");
			}
		} catch(InterruptedException | IOException ie) {
			ie.printStackTrace();
		}
	}
}
