package com.spacejoust.entities;
import java.awt.Graphics;
import java.util.Random;

import com.spacejoust.framework.Global;

/*
 * Star: An environment Entity that
 * has no direct impact on the game; it just
 * travels down the window at a random speed.
 */
public class Star extends Entity {
	
	//An instance of Random for random number generation.
	Random random = new Random();
	
	//Stars appear in random locations, so the constructor
	//does not take position parameters.
	public Star() {
		super(0, 0, 0);
		
		setPosX(random.nextInt(Global.WIDTH));
		setPosY(0);
		setVelY(random.nextDouble() * 1000 + 505.0);
		setVelX(0);
		setPosR(random.nextInt(360));
		setBaseSprite(environment.crop(random.nextInt(7) * 32 + 1, 32 * 7, 3, 3));
	}
	
	//Draw this Star
	public void render(Graphics g) {
		g.drawImage(getBaseSprite(), (int) getPosX(), (int) getPosY(), 3, 3, null);
	}
	//Move this Star
	public void update() {
		applyMotion();
		if(! Global.isInBounds(this)) getHandler().removeEntity(this);
	}
}
