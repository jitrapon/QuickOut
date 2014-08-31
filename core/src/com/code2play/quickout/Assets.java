package com.code2play.quickout;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Asset manager handles texture loading, animation, and models. This class initializes all models' constraints.
 * @author Jitrapon
 *
 */
public class Assets {
	
	public static Array<Texture> textures;
	private static TextureAtlas ballAtlas;
	private static TextureAtlas lSceneAtlas;							// LEVEL SCENE PACK
	private static final int DEFAULT_TEXTURE_LOAD_SIZE = 10;
	public static Array<Array<Animation>> animationList;
	
	private static Map<String, TextureRegion> textureMap;
	
	/* ALL TEXTUREREGION IMAGE NAME REFERENCE */
	public static final String LEVEL_BACKGROUND_HILL = "hill";
	public static final String LEVEL_HUD_GROUND = "ground";
	public static final String LEVEL_BACKGROUND_CLOUD1 = "cloud1";
	public static final String LEVEL_BACKGROUND_CLOUD2 = "cloud2";
	public static final String LEVEL_BACKGROUND_CLOUD3 = "cloud3";
	public static final String LEVEL_BACKGROUND_CLOUD4 = "cloud4";
	public static final String LEVEL_BACKGROUND_CLOUD5 = "cloud5";
	public static final String LEVEL_BACKGROUND_CLOUD6 = "cloud6";
	public static final String LEVEL_BACKGROUND_CLOUD7 = "cloud7";
	public static final String LEVEL_BACKGROUND_CLOUD8 = "cloud8";
	public static final String LEVEL_BACKGROUND_CLOUD9 = "cloud9";
	
	private static Array<Sound> ballCorrectPlopEffectSounds;
	private static Array<Sound> ballWrongPlopEffectSounds;
	

	/**
	 * Call this method to load all necessary texture files and etc.
	 */
	public static void load() {
		ballAtlas = new TextureAtlas(Gdx.files.internal("textures/level/ball.pack"));
		lSceneAtlas = new TextureAtlas(Gdx.files.internal("textures/level/scene.pack"));
		loadTextures();
		loadAnimations();
		loadSounds();
	}
	
	/**
	 * Load all textures here
	 */
	private static void loadTextures() {
		textures = new Array<Texture>(DEFAULT_TEXTURE_LOAD_SIZE);
		textureMap = new HashMap<String, TextureRegion>();
		
		// load level background
		textures.add(new Texture(Gdx.files.internal("textures/level/GameBackground.png")));
		
		// load level background layers
		textureMap.put( LEVEL_BACKGROUND_HILL, lSceneAtlas.findRegion(LEVEL_BACKGROUND_HILL) );
		textureMap.put( LEVEL_HUD_GROUND, lSceneAtlas.findRegion(LEVEL_HUD_GROUND) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD1, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD1) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD2, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD2) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD3, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD3) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD4, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD4) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD5, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD5) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD6, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD6) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD7, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD7) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD8, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD8) );
		textureMap.put( LEVEL_BACKGROUND_CLOUD9, lSceneAtlas.findRegion(LEVEL_BACKGROUND_CLOUD9) );
	}
	
	public static TextureRegion getTextureRegion(String regionName) {
		return textureMap.get(regionName);
	}
	
	public static Texture getLevelBackground() {
		return textures.first();
	}
	
	private static void loadAnimations() {
		animationList = new Array<Array<Animation>>();
		
		// There are three animations to load, namely IDLE, PROVOKED, and SCARED
		Array<String> prefixes = new Array<String>(4);
		prefixes.addAll("Blue", "Green", "Red", "Yellow");							// order of type is specified here
		Animation animation = null;
		for (String name : prefixes) {
			// Idle animation
			Array<Animation> anim = new Array<Animation>(3);
			animation = new Animation( 1/15f, ballAtlas.findRegions(name + "Idle") );
			animation.setPlayMode(PlayMode.LOOP);
			anim.add(animation);
			
			// Dragged animation
			animation = new Animation( 1/15f, ballAtlas.findRegions(name + "Poked") );
			animation.setPlayMode(PlayMode.LOOP);
			anim.add(animation);
			
			// Flinged animation
			animation = new Animation( 1/15f, ballAtlas.findRegions(name + "Scared") );
			animation.setPlayMode(PlayMode.LOOP);
			anim.add(animation);
			
			animationList.add(anim);
		}
	}
	
	private static void loadSounds() {
		ballCorrectPlopEffectSounds  = new Array<Sound>();
		ballWrongPlopEffectSounds = new Array<Sound>();
		String prefix = "sound/level/ball/plop_";
		for (int i = 1; i <= 6 ; i++) {
			ballCorrectPlopEffectSounds.add(Gdx.audio.newSound(Gdx.files.internal(prefix + i + ".wav")));
		}
		
		for (int i = 7; i <= 8 ; i++) {
			ballWrongPlopEffectSounds.add(Gdx.audio.newSound(Gdx.files.internal(prefix + i + ".wav")));
		}
	}
	
	public static Sound getCorrectBallPlopSoundEffect() {
		return ballCorrectPlopEffectSounds.random();
	}
	
	public static Sound getWrongBallPlopSoundEffect() {
		return ballWrongPlopEffectSounds.random();
	}
	
	public static void dispose() {
		for (Texture t : textures) {
			t.dispose();
		}
		for (Sound s : ballCorrectPlopEffectSounds) {
			s.dispose();
		}
		for (Sound s : ballWrongPlopEffectSounds) {
			s.dispose();
		}
		textures.clear();
		textureMap.clear();
		ballAtlas.dispose();
		lSceneAtlas.dispose();
	}
	
}
