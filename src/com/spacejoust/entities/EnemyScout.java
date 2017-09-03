package com.spacejoust.entities;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Global;

/*
 * EnemyScout: A basic EnemyShip with a BasicShip-like
 * firing pattern and simple AI.
 */
public class EnemyScout extends EnemyShip {

	private long lastBulletShot = 0;

	//Variables for random ship motion
	private long lastDirectionChange = 0;
	private long maxDirectionDuration = 30000;


	//Erraticness of AI (constant):
	private static final double ERRATIC_FACTOR = 0.01;

	public EnemyScout(double x, double y, double rot) {
		super(x, y, rot);
		setMaxHealth(10);
		setBaseSprite(enemies.crop(1, 0, 31, 31));
	}

	/*
	 * In addition to moving this EnemyScout, we must apply its AI.
	 * This EnemyShip's AI is as follows:
	 * 1. Find the nearest PlayerShip
	 * 2. Move to the target, but at a minimum 200px away from itw
	 * 3. Rotate to face the target
	 * 4. Fire if possible.
	 */
	public void update() {
		super.update();

		//Part 1: Acquire target
		Point here = new Point((int) getPosX(), (int) getPosY());
		Point there;
		double distance = Double.MAX_VALUE;
		PlayerShip nearestShip = null;
		for(Entity g: getHandler().getEntities()) if(g instanceof PlayerShip) {
			there = new Point((int) g.getPosX(), (int) g.getPosY());
			if(here.distance(there) < distance) {
				nearestShip = ((PlayerShip) g);
				distance = here.distance(there);
			}
		}

		if(nearestShip == null) return;

		//Part 2: Move to target
		int distanceX = (int) (nearestShip.getPosX() - getPosX());
		int distanceY = (int) (nearestShip.getPosY() - getPosY());
		if(distance > 300) {
			setAccX(distanceX / (double) Global.WIDTH * getMaxAcc() + (Math.random() * ERRATIC_FACTOR - ERRATIC_FACTOR/2));
			setAccY(distanceY / (double) Global.HEIGHT * getMaxAcc() + (Math.random() * ERRATIC_FACTOR - ERRATIC_FACTOR/2));
			//System.out.println(accX + ", " + accY);
		} else if(System.currentTimeMillis() > lastDirectionChange + Math.random() * maxDirectionDuration){
			double random = Math.random() * getMaxAcc();
			setAccX(random - getMaxAcc()/2);
			setAccY(getMaxAcc() - random - getMaxAcc()/2);
			lastDirectionChange = System.currentTimeMillis();
		}

		//Part 3: Rotate to target
		double tancalc = Math.toDegrees(Math.atan((double) Math.abs(distanceX) / Math.abs(distanceY)));
		int targetRot = 0;
		if(distanceY <= 0 && distanceX > 0) targetRot = (int) (tancalc);
		if(distanceY > 0 && distanceX > 0) targetRot = 90 + (int) Math.abs(tancalc - 90);
		if(distanceY > 0 && distanceX <= 0) targetRot = (int) (tancalc + 180);
		if(distanceY <= 0 && distanceX <= 0) targetRot = 270 + (int) Math.abs(tancalc - 90);

		if(Math.abs(targetRot - getPosR()) > 6) {
			if((getPosR() - targetRot + 360) % 360 > 180) setVelR(getMaxVelR());
			else setVelR(-getMaxVelR());
		} else setVelR(0);

		//Part 4: Fire in the hole(s)!
		if(System.currentTimeMillis() > lastBulletShot + getFireSpeed()) fire();
	}

	//EnemyScouts use the same kind of firing technique as BasicShips.
	public void fire() {
		//System.out.println(getVelR());
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
			Global.playSound(Global.LOW_BULLET_SOUND);
		}
	}
	//Stats
	public double getMaxVel() { return 120; }
	public int getBulletDamage() { return 2; }
	public BufferedImage getBulletSprite() { return enemies.crop(225, 0, 31, 31); }
	public double getMaxAcc() { return 80; }
	public double getBulletSpeed() { return 400; }
	public double getMaxVelR() { return 180; }
	public double getMaxAccR() { return 90; }
	public int getShipSize() { return 48; }
	public int getFireSpeed() { return 3000; }
	public String getName() { return "Enemy Scout"; }
	public String getLore() { return "A small but burly-looking alien vessel."; }

	@Override
	public Item getDrop() {
		if((int) (Math.random() * 3) == 0) return new BatteryItem((int) getPosX(), (int) getPosY());
		else return null;
	}
}
