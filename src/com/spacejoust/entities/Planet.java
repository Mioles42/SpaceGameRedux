package com.spacejoust.entities;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Global;

/*
 * Planet: An environment Entity
 * that does not interact with other Entities.
 * It just floats down the screen, like a Star.
 */
public class Planet extends Entity {
	
	//Size of this Planet
	private int diameter;
	
	//Plenets are created 
	public Planet() {
		super(0, 0, 0);
		
		setPosX(Math.random() * Global.WIDTH);
		setPosY(-19);
		setVelY(Math.random() * 300 + 1.0);
		setVelX(0);
		setBaseSprite(getSprite());
		diameter = ((int) Math.random() * 128) + 128;
	}
	public BufferedImage getSprite() {
		switch((int)(Math.random() * 4)) {
		case 0: return environment.crop(1, 160, 63, 63);
		case 1: return environment.crop(65, 160, 31, 31);
		case 2: return environment.crop(65, 192, 31, 31);
		case 3: return environment.crop(97, 160, 31, 31);
		case 4: return environment.crop(97, 192, 31, 31);
		default: return environment.crop(1, 160, 63, 63);
		}
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(getBaseSprite(), (int) (getPosX() - diameter/2), (int) (getPosY() - diameter/2), diameter, diameter, null);
	}
	@Override
	public void update() {
		applyMotion();
		if(! Global.isInBounds(this)) getHandler().removeEntity(this);
	}

}
