package com.lord.wordle;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Art {
	
	public static Bitmap font = loadBitmap("/font.png");
	public static Bitmap tiles = loadBitmap("/tiles.png");
	
	public static Bitmap loadBitmap(String path) {
		try {
			BufferedImage image = ImageIO.read(Art.class.getResource(path));
			Bitmap ans = new Bitmap(image.getWidth(), image.getHeight());
			image.getRGB(0, 0, ans.width, ans.height, ans.pixels, 0, ans.width);
			return ans;
		} catch (Exception ie) {
			ie.printStackTrace();
		}
		
		return null;
	}
}
