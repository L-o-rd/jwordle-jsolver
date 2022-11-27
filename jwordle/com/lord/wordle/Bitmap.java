package com.lord.wordle;

public class Bitmap {
	
	public final int width, height, pixels[];
	public static final int key = 0xff8112a0;
	
	public Bitmap(final int width, final int height) {
		this.width = width; this.height = height;
		this.pixels = new int[width * height];
	}
	
	public void draw(Bitmap bitmap, int xo, int yo) {
		for(int y = 0; y < bitmap.height; ++y) {
			int starty = yo + y;
			if(starty < 0 || starty >= height) continue;
			for(int x = 0; x < bitmap.width; ++x) {
				int startx = xo + x;
				if(startx < 0 || startx >= width) continue;
				
				int pix = bitmap.pixels[x + y * bitmap.width];
				if(pix != key) pixels[startx + starty * width] = pix;
			}
		}
	}
	
	public void scaledDraw(Bitmap bitmap, int scale, int xoff, int yoff, int xo, int yo, int w, int h, int col)
	{
		for(int y = 0; y < h * scale; ++y) {
			int starty = yoff + y;
			if(starty < 0 || starty >= height) continue;
			for(int x = 0; x < w * scale; ++x) {
				int startx = xoff + x;
				if(startx < 0 || startx >= width) continue;
				
				int pix = bitmap.pixels[(x / scale + xo) + (y / scale + yo) * bitmap.width];
				if(pix != key) {
					pixels[startx + starty * width] = pix & col;
				}
			}
		}
	}
	
	public void draw(Bitmap bitmap, int xoff, int yoff, int xo, int yo, int w, int h) {
		for(int y = 0; y < h; ++y) {
			int starty = yoff + y;
			if(starty < 0 || starty >= height) continue;
			for(int x = 0; x < w; ++x) {
				int startx = xoff + x;
				if(startx < 0 || startx >= width) continue;
				
				int pix = bitmap.pixels[(x + xo) + (y + yo) * bitmap.width];
				if(pix != key) {
					pixels[startx + starty * width] = pix;
				}
			}
		}
	}
	
	public void coloredDraw(Bitmap bitmap, int xoff, int yoff, int xo, int yo, int w, int h, int col) {
		for(int y = 0; y < h; ++y) {
			int starty = yoff + y;
			if(starty < 0 || starty >= height) continue;
			for(int x = 0; x < w; ++x) {
				int startx = xoff + x;
				if(startx < 0 || startx >= width) continue;
				
				int pix = bitmap.pixels[(x + xo) + (y + yo) * bitmap.width];
				if(pix != key) {
					pixels[startx + starty * width] = pix & col;
				}
			}
		}
	}
	
}
