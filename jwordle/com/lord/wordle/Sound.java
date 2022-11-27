package com.lord.wordle;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
	
	public static Sound type = loadSound("/type.wav");
	public static Sound altt = loadSound("/altt.wav");
	
	private Clip clip;
	
	public void play() {
		try {
			
			if(clip != null) {
				
				clip.stop();
				clip.setFramePosition(0);
				clip.start();
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setVolume(float value) {
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(20.f * (float) Math.log10(value));
	}
	
	public static Sound loadSound(final String path) {
		Sound sound = new Sound();
		
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getResource(path));
			Clip _clip = AudioSystem.getClip();
			_clip.open(ais);
			sound.clip = _clip;
			sound.setVolume(0.05f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sound;
	}
	
}
