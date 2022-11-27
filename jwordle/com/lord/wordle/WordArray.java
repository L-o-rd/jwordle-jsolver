package com.lord.wordle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordArray {
	
	public final List<String> words;
	
	public WordArray(final String path) {
		words = new ArrayList<String>();
		
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(WordArray.class.getResourceAsStream(path))
				);
			
			String line = "";
			
			while((line = reader.readLine()) != null) {
				words.add(line.toUpperCase());
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
