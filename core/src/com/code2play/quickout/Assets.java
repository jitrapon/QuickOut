package com.code2play.quickout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

/**
 * Asset manager handles texture loading, animation, and models. This class initializes all models' constraints.
 * @author Jitrapon
 *
 */
public class Assets {
	
	public static Array<Texture> textures;
	private static TextureAtlas textureAtlas;
	private static final int DEFAULT_TEXTURE_LOAD_SIZE = 10;
	
	public static Array<Array<Animation>> animationList;

	/**
	 * Call this method to load all necessary texture files and etc.
	 */
	public static void load() {
		loadTextures();
		loadAnimations();
	}
	
	/**
	 * Load all textures here
	 */
	private static void loadTextures() {
		textures = new Array<Texture>(DEFAULT_TEXTURE_LOAD_SIZE);
	}
	
	private static void loadAnimations() {
		textureAtlas = new TextureAtlas(Gdx.files.internal("textures/ball/ball.pack"));
		animationList = new Array<Array<Animation>>();
		
		// There are three animations to load, namely IDLE, PROVOKED, and SCARED
		Array<String> prefixes = new Array<String>(4);
		prefixes.addAll("Blue", "Green", "Red", "Yellow");							// order of type is specified here
		Animation animation = null;
		for (String name : prefixes) {
			// Idle animation
			Array<Animation> anim = new Array<Animation>(3);
			animation = new Animation( 1/15f, textureAtlas.findRegions(name + "Idle") );
			animation.setPlayMode(PlayMode.LOOP);
			anim.add(animation);
			
			// Dragged animation
			animation = new Animation( 1/15f, textureAtlas.findRegions(name + "Poked") );
			animation.setPlayMode(PlayMode.LOOP);
			anim.add(animation);
			
			// Flinged animation
			animation = new Animation( 1/15f, textureAtlas.findRegions(name + "Scared") );
			animation.setPlayMode(PlayMode.LOOP);
			anim.add(animation);
			
			animationList.add(anim);
		}
		Gdx.app.log("ANIMATION", "Done loading animation with size " + animationList.size);
	}
	
	public static void dispose() {
		for (Texture t : textures) {
			t.dispose();
		}
		textures.clear();
		textureAtlas.dispose();
	}
	
}
