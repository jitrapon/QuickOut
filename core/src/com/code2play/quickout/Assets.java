package com.code2play.quickout;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/**
 * Asset manager handles texture loading, animation, and models. This class initializes all models' constraints.
 * @author Jitrapon
 *
 */
public class Assets {
	
	public static Array<Texture> textures;
	private static final int DEFAULT_TEXTURE_LOAD_SIZE = 10;

	/**
	 * Call this method to load all necessary texture files and etc.
	 */
	public static void load() {
		loadTextures();
	}
	
	/**
	 * Load all textures here
	 */
	private static void loadTextures() {
		textures = new Array<Texture>(DEFAULT_TEXTURE_LOAD_SIZE);
		textures.add( new Texture("textures/ball/blue.png") );
		textures.add( new Texture("textures/ball/green.png") );
		textures.add( new Texture("textures/ball/red.png") );
		textures.add( new Texture("textures/ball/yellow.png") );
	}
}
