package com.lord.wordle;

public class Font {

	public static final String chars = " !'#$%&'()*+,-. 01" +
									   "23456789:;<=>?@ABC" +
									   "DEFGHIJKLMNOPQRSTU" +
									   "VWXYZ[ ]^_`abcdefg" +
									   "hijklmnopqrstuvwxy" +
									   "z{ }~             ";
	
	public static void render(Screen screen, String msg, int x, int y, int col) {
		for(int i = 0; i < msg.length(); ++i) {
			int chr = chars.indexOf(msg.charAt(i));
			if(chr >= 0) {
				int px = chr % 18, py = chr / 18;
				screen.coloredDraw(Art.font, x + i * 7, y, px * 7, py * 9, 7, 9, col);
			}
		}
	}
	
	public static void render(Screen screen, Character _chr, int x, int y, int col) {
		int chr = chars.indexOf(_chr);
		if(chr >= 0) {
			int px = chr % 18, py = chr / 18;
			screen.coloredDraw(Art.font, x, y, px * 7, py * 9, 7, 9, col);
		}
	}
	
	public static void render(Screen screen, Character _chr, int x, int y, int scale, int col) {
		int chr = chars.indexOf(_chr);
		if(chr >= 0) {
			int px = chr % 18, py = chr / 18;
			screen.scaledDraw(Art.font, scale, x, y, px * 7, py * 9, 7, 9, col);
		}
	}
	
	public static void render(Screen screen, String msg, int x, int y, int scale, int col) {
		for(int i = 0; i < msg.length(); ++i) {
			int chr = chars.indexOf(msg.charAt(i));
			if(chr >= 0) {
				int px = chr % 18, py = chr / 18;
				screen.scaledDraw(Art.font, scale, x + i * 7 * scale, y, px * 7, py * 9, 7, 9, col);
			}
		}
	}
	
}
