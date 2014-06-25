package com.code2play.game;

import com.badlogic.gdx.physics.box2d.World;
import com.code2play.quickout.WorldView;

public interface IGameManager {

	public void init();
	public void update(float delta);
	public void setWorldRenderer(WorldView viewRenderer);
	public WorldView getWorldRenderer();
	public World getPhysicsWorld();
	public void saveGame();
	public void exitGame();
}
