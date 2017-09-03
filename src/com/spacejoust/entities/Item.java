package com.spacejoust.entities;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Global;
import java.awt.Graphics;

public abstract class Item extends SolidEntity {
	
	final int ITEM_SIZE = 31;
	final int MAX_ITEM_SPEED = 300;
	final double ITEM_DRAG = .2;
	
	public Item(int x, int y, BufferedImage sprite) {
		super(x, y, (int) (Math.random() * 180), sprite);
		setVelX((int) (Math.random() * MAX_ITEM_SPEED) - MAX_ITEM_SPEED/2);
		setVelY((int) (Math.random() * MAX_ITEM_SPEED) - MAX_ITEM_SPEED/2);
		setVelR((int) (Math.random() * 180));
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle((int) getPosX(), (int) getPosY(), 16, 16);
	}

	@Override
	public void applyMotion() {
		super.applyMotion();
		if(getAccX() == 0 && getVelX() != 0) setVelX(getVelX() > 0? getVelX()*1000.0 - ITEM_DRAG : getVelX()*1000.0 + ITEM_DRAG);
		if(getAccY() == 0 && getVelY() != 0) setVelY(getVelY() > 0? getVelY()*1000.0 - ITEM_DRAG : getVelY()*1000.0 + ITEM_DRAG);
	}

	@Override
	public void render(Graphics g) {
		double tancalc = (ITEM_SIZE * Math.tan(Math.toRadians(getPosR() % 90))) / (Math.tan(Math.toRadians(getPosR() % 90)) + 1);
		Point effectiveTopLeft;
		Point effectiveTopRight;
		
		if(getPosR() < 90) {
			effectiveTopLeft = new Point((int)(getPosX() + tancalc), (int) getPosY());
			effectiveTopRight = new Point(effectiveTopLeft.x + ITEM_SIZE - (int) tancalc, effectiveTopLeft.y + (int)tancalc);
		} else if(getPosR() >= 90 && getPosR() < 180) {
			effectiveTopLeft = new Point((int) (getPosX() + ITEM_SIZE), (int) (getPosY() + tancalc));
			effectiveTopRight = new Point((int) (getPosX() + (ITEM_SIZE - tancalc)), (int) (getPosY() + ITEM_SIZE));
		} else if(getPosR() >= 180 && getPosR() < 270) {
			effectiveTopLeft = new Point((int) (getPosX() + (ITEM_SIZE - tancalc)), (int) (getPosY() + ITEM_SIZE));
			effectiveTopRight = new Point((int) getPosX(), (int) (getPosY() + ITEM_SIZE - (int) tancalc));
		} else {
			effectiveTopLeft = new Point((int) getPosX(), (int) (getPosY() + ITEM_SIZE - (int) tancalc));
			effectiveTopRight = new Point((int) (getPosX() + (int) tancalc), (int) getPosY());
		}
		
		/*
		 * aspectRatio - A ratio, measuring the actual size of the ship versus the drawn size of the ship.
		 * We can use it to resize so that those two are equal.
		 * amountChange - The difference between the sizes of the actual and rotated ships (before resize).
		 * This is used to make sure the ship rotates around its center.
		 */
		double aspectRatio = (ITEM_SIZE / effectiveTopLeft.distance(effectiveTopRight));
		int amountChange = (int) (ITEM_SIZE - effectiveTopLeft.distance(effectiveTopRight));
		
		//Finally, draw the image (aspectRatio) times larger than (SHIP_SIZE), because
		//when it's drawn it will be made (aspectRatio) times smaller.
		//Also, shift the image so it rotates in its center.
		g.drawImage(
				Global.rotate(getDrawSprite(), getPosR()),
				(int) (getPosX() - amountChange/2),
				(int) (getPosY() - amountChange/2),
				(int) (ITEM_SIZE * aspectRatio),
				(int) (ITEM_SIZE * aspectRatio),
				null);
		g.setColor(Color.RED);
		
	}
	
	public abstract void use(PlayerShip p);
}
