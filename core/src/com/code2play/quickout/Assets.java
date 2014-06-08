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
		textures.add( new Texture("textures/ball/blue.png") );
		textures.add( new Texture("textures/ball/green.png") );
		textures.add( new Texture("textures/ball/red.png") );
		textures.add( new Texture("textures/ball/yellow.png") );
	}
	
	private static void loadAnimations() {
		textureAtlas = new TextureAtlas(Gdx.files.internal("textures/face/sally.pack"));
		animationList = new Array<Array<Animation>>();
		
		// There are three animations to load, namely IDLE, PROVOKED, and SCARED
		Array<Animation> anim = new Array<Animation>(3);
		Animation temp = new Animation( 1/15f, textureAtlas.findRegions("idle") );
		temp.setPlayMode(PlayMode.LOOP);
		anim.add(temp);

		temp = new Animation( 1/15f, textureAtlas.findRegions("poked") );
		temp.setPlayMode(PlayMode.LOOP);
		anim.add(temp);
		
		temp = new Animation( 1/15f, textureAtlas.findRegions("scared") );
		temp.setPlayMode(PlayMode.LOOP);
		anim.add(temp);
		
		animationList.add(anim);
	}
	
	public static void dispose() {
		for (Texture t : textures) {
			t.dispose();
		}
		textures.clear();
		textureAtlas.dispose();
	}
	
}
