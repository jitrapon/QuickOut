package com.code2play.quickout;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * This class extends the functionality of image actor, but allows animation from sprites as 
 * texture regions.
 * @author Jitrapon
 *
 */
public class AnimatedImage extends Image {
	
	private Animation animation = null;
	private float stateTime = 0;

	public AnimatedImage(Animation animation) {
		super(animation.getKeyFrame(0));
		this.animation = animation;
	}
	
	/**
	 * This gets called when animation changes occur.
	 * @param animation
	 */
	public void setAnimation(Animation animation) {
		if (this.animation != animation) {
			this.animation = animation;
			stateTime = 0;
		}
	}
	
	public void setAnimation(Animation animation, float stateTime) {
		if (this.animation != animation) {
			this.animation = animation;
			this.stateTime = stateTime;
		}
	}
	
	public Animation getAnimation() {
		return animation;
	}
	
	public float getStateTime() {
		return stateTime;
	}
	
	@Override
	public void act(float delta) {
		((TextureRegionDrawable)getDrawable()).setRegion(animation.getKeyFrame(stateTime+=delta, true));
		super.act(delta);
	}
}
