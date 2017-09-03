package com.spacejoust.entities;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Global;

public class BasicShip extends PlayerShip {
	
	private long lastBulletShot = 0;
	private boolean[] pressed = new boolean[6]; //In order: forwards, backwards, LTurn, RTurn, fire, use item 1
	
    public BasicShip(double x, double y, double r, int playerNum) {
        super(x, y, r, playerNum);
        setMaxHealth(50); //more than average
        setBaseSprite(ships.crop(1, 0, 63, 63));
        setVelR(0.01);
        addItem(new BatteryItem(0, 0));
        update();
    }
    
    //Fire bullets, if possible.
    //This ship fires two bullets on either side of itself.
    public void fire() {
		//System.out.println("<" + getVelX() + ", " + getVelY() + "> and <<" + getAccX() + ", " + getAccY() + ">>");
    	//If we are able to fire...
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
    	
    		//The bullets will have a velocity in two directions that should be about (getBulletSpeed()). 
    		//We can figure out how fast they go in each direction using the following:
    		double bulletVelY = -(effectiveTopRight.x - effectiveTopLeft.x) * getBulletSpeed() / 50;
    		double bulletVelX = (effectiveTopRight.y - effectiveTopLeft.y) * getBulletSpeed() / 50;
    		
    		//Finally, create the bullets and add them to the world (or handler).
    		Bullet bullet1 = new Bullet(effectiveTopLeft.x, effectiveTopLeft.y, getPosR(), this, getBulletDamage());
    		Bullet bullet2 = new Bullet(effectiveTopRight.x, effectiveTopRight.y, getPosR(), this, getBulletDamage());
    		bullet1.setVelY(bulletVelY);
    		bullet2.setVelY(bulletVelY);
    		bullet1.setVelX(bulletVelX);
    		bullet2.setVelX(bulletVelX);
    		getHandler().addEntity(bullet1);
    		getHandler().addEntity(bullet2);
    		lastBulletShot = System.currentTimeMillis();
    		Global.playSound(Global.BULLET_SOUND);
    	}
    }
    
    //Stats
	public double getMaxVel() { return 300; }
	public int getBulletDamage() { return 4; }
	public BufferedImage getBulletSprite() { return ships.crop(225, 0, 31, 31); }
	public Color getShipColor() { return new Color(255, 100, 100); }
	public double getMaxAcc() { return 1; }
	public double getBulletSpeed() { return 400; }
	public double getMaxVelR() { return 180; }
	public double getMaxAccR() { return 10; }
	public int getShipSize() { return 64; }
	public int getFireSpeed() { return 270; }
	public String getName() { return "Common Ship"; }
	public String getLore() { return "A spacevessel usually owned by civilians, with a couple laser cannons installed."; }
  	public int getMaxItems() { return 1; }

  	//Controls
  	@Override
  	public void press(int key) {
  		if(key == KeyEvent.VK_W){
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
  		if(key == KeyEvent.VK_S){
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
  		
  		if(key == KeyEvent.VK_Q){
  			setVelR(-getMaxVelR());
  			pressed[2] = true;
  		}
  			if(key == KeyEvent.VK_E){
  			setVelR(getMaxVelR());
  			pressed[3] = true;
  		}
  		if(key == KeyEvent.VK_G){
  			fire();
  			pressed[4] = true;
  		}
  		if(key == KeyEvent.VK_F){
			useItem(0);
		}
  	}

  	@Override
  	public void release(int key) {
  		if(key == KeyEvent.VK_W){
  			pressed[0] = false;
  		}
  		if(key == KeyEvent.VK_S){
  			pressed[1] = false;
  		}
  		if(key == KeyEvent.VK_Q){
  			pressed[2] = false;
  		}
  		if(key == KeyEvent.VK_E){
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