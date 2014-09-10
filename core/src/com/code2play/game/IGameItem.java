package com.code2play.game;

public interface IGameItem {

	/**
	 * Applies specific item effect. Override this method to achieve desired item effect.
	 * Effect lasts for as long as the item's max duration.
	 */
	public void applyEffect(float delta);
	
	/**
	 * Called when the item effect is finished.
	 * @param delta
	 */
	public abstract void onEffectFinished(float delta);
	
	/**
	 * Called when the item effect begins to be active.
	 * @param delta
	 */
	public abstract void onEffectStarted(float delta);
}
