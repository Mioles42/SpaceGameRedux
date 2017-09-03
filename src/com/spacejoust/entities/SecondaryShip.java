package com.spacejoust.entities;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Global;
public class SecondaryShip extends PlayerShip {

	private long lastBulletShot = 0;
	private boolean[] pressed = new boolean[6]; //In order: forwards, backwards, LTurn, RTurn, fire, use item 1

	public SecondaryShip(double x, double y, double r, int playerNum) {
		super(x, y, r, playerNum);
		setMaxHealth(40); //more than average
		setBaseSprite(ships.crop(1, 64, 63, 63));
	}

	//Fires a bullet, if possible.
	//This ship fires a single bullet from its nose.
	public void fire() {
		if(System.currentTimeMillis() > lastBulletShot + getFireSpeed()) {
			//Recognize this? It's the tangent calculation from Ship.
			//We're going to use the effective top left and right of the rotated ship
			//as our firing locations. That puts the bullets in a pretty logical location.

			double tancalc = (50 * Math.tan(Math.toRadians(getPosR() % 90))) / (Math.tan(Math.toRadians(getPosR() % 90)) + 1);

			Point effectiveTopLeft = new Point((int)(getPosX() + tancalc), (int) getPosY());
			Point effectiveTopRight = new Point(effectiveTopLeft.x + 50 - (int) tancalc, effectiveTopLeft.y + (int)tancalc);
			if(getPosR() < 90) {
				effectiveTopLeft = new Point((int)(getPosX() + tancalc), (int) getPosY());
				effectiveTopRight = new Point(effectiveTopLeft.x + 50 - (int) tancalc, effectiveTopLeft.y + (int)tancalc);
			} else if(getPosR() >= 90 && getPosR() < 180) {
				effectiveTopLeft = new Point((int) (getPosX() + 50), (int) (getPosY() + tancalc));
				effectiveTopRight = new Point((int) (getPosX() + (50 - tancalc)), (int) (getPosY() + 50));
			} else if(getPosR() >= 180 && getPosR() < 270) {
				effectiveTopLeft = new Point((int) (getPosX() + (50 - tancalc)), (int) (getPosY() + 50));
				effectiveTopRight = new Point((int) getPosX(), (int) (getPosY() + 50 - tancalc));
			} else {
				effectiveTopLeft = new Point((int) getPosX(), (int) (getPosY() + 50 - tancalc));
				effectiveTopRight = new Point((int) (getPosX() + tancalc), (int) getPosY());
			}

			//We need the point between the effective top right and left.
			Point effectiveMiddle = new Point((effectiveTopRight.x + effectiveTopLeft.x) / 2, (effectiveTopRight.y + effectiveTopLeft.y) / 2);

			//The bullets will have a velocity in two directions that should be about (getBulletSpeed()). 
			//We can figure out how fast they go in each direction using the following:
			double bulletVelY = -(effectiveTopRight.x - effectiveTopLeft.x) * getBulletSpeed() / 50;
			double bulletVelX = (effectiveTopRight.y - effectiveTopLeft.y) * getBulletSpeed() / 50;

			//Create and spawn out bullet
			Bullet bullet1 = new Bullet(effectiveMiddle.x, effectiveMiddle.y, getPosR(), this, getBulletDamage());
			bullet1.setVelY(bulletVelY);
			bullet1.setVelX(bulletVelX);
			getHandler().addEntity(bullet1);
			lastBulletShot = System.currentTimeMillis();
			Global.playSound(Global.BULLET_SOUND);
		}
	}

	//Stats
	public double getMaxVel() { return 400; }
	public int getBulletDamage() { return 6; }
	public BufferedImage getBulletSprite() { return ships.crop(225, 32, 31, 31); }
	public Color getShipColor() { return new Color(100, 100, 255); }
	public double getMaxAcc() { return 1; }
	public double getBulletSpeed() { return 430; }
	public double getMaxVelR() { return 160; }
	public double getMaxAccR() { return 15; }
	public int getShipSize() { return 64; }
	public int getFireSpeed() { return 250; }
	public String getName() { return "Patrol Ship"; }
	public String getLore() { return "A speedy police spacevessel with a single powerful ion cannon mounted in its nose."; }
	public int getMaxItems() { return 1; }

	//Controls
	@Override
	public void press(int key) {
		if(key == KeyEvent.VK_NUMPAD5){
			double r = getPosR();
			double ratioX; //A ratio of x to y motion
			if(r < 90) ratioX = (r / 90.0);
			else if(r >= 90 && r < 180) ratioX = ((90.0 - (r % 90.0)) / 90.0);
			else if(r >= 180 && r < 270)  ratioX = -((r % 90.0) / 90.0);
			else ratioX = -((90.0 - (r % 90.0)) / 90.0);
			System.out.println("ratioX: " + ratioX);
			setAccX(ratioX * 1000);
			double ratioY = 1 - Math.abs(ratioX);
			if(r >= 270 || r < 90) ratioY *= -1;
			setAccY(ratioY * 1000);
			pressed[0] = true;
		}
		if(key == KeyEvent.VK_NUMPAD2){
			double r = getPosR();
			double ratioX; //A ratio of x to y motion
			if(r < 90) ratioX = (r / 90.0);
			else if(r >= 90 && r < 180) ratioX = ((90.0 - (r % 90.0)) / 90.0);
			else if(r >= 180 && r < 270)  ratioX = -((r % 90.0) / 90.0);
			else ratioX = -((90.0 - (r % 90.0)) / 90.0);
			System.out.println("ratioX: " + ratioX);
			setAccX(-ratioX * 1000);
			double ratioY = 1 - Math.abs(ratioX);
			if(r >= 270 || r < 90) ratioY *= -1;
			setAccY(-ratioY * 1000);
			pressed[1] = true;
		}
		
		if(key == KeyEvent.VK_NUMPAD4){
			setVelR(-getMaxVelR());
			pressed[2] = true;
		}
			if(key == KeyEvent.VK_NUMPAD6){
			setVelR(getMaxVelR());
			pressed[3] = true;
		}
		if(key == KeyEvent.VK_HOME){
			fire();
			pressed[4] = true;
		}
		if(key == KeyEvent.VK_NUMPAD7){
			useItem(0);
		}
	}

	@Override
	public void release(int key) {
		if(key == KeyEvent.VK_NUMPAD5){
			pressed[0] = false;
		}
		if(key == KeyEvent.VK_NUMPAD2){
			pressed[1] = false;
		}
		if(key == KeyEvent.VK_NUMPAD4){
			pressed[2] = false;
		}
		if(key == KeyEvent.VK_NUMPAD6){
			pressed[3] = false;
		}
		if(!pressed[2] && !pressed[3]){
			setVelR(0);
		}
		if(!pressed[0] && !pressed[1]){
			setAccY(0);
			setAccX(0);
		}
	}
}