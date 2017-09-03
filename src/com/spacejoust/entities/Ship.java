package com.spacejoust.entities;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Global;

/*
 * Ship: A SolidEntity that is controlled manually
 * or autonomously that fires bullets and has basic, varying statistics
 * about bullet firing and motion.
 */
public abstract class Ship extends SolidEntity {
	private int MAX_PARTICLES = 16;
	private int MAX_PARTICLE_SPEED = 100;
	private int PARTICLE_SIZE = 3;
	private int PARTICLE_DURATION = 700;

	//Standard constructor. Subclasses are expected to set their own sprite.
	public Ship(double x, double y, double rot) {
		super(x, y, rot);
	}

	//Occurs every tick to move the ship.
	public void update() {
		if(getHealth() <= 0) {
			getHandler().removeEntity(this);
			Particle p;
			for(int i = (int) (Math.random() * (MAX_PARTICLES - 5)) + 5; i > 0; i--) {
				p = new Particle(this.getPosX(), this.getPosY(), PARTICLE_SIZE, MAX_PARTICLE_SPEED, PARTICLE_DURATION, getShipColor());
				getHandler().addEntity(p);
			}
		}
		
		applyMotion();
		
		//Crunch the position
		setPosX(Global.crunchX(getPosX()));
		setPosY(Global.crunchY(getPosY()));
		
		//Deal with max velocities when turned
		double r = this.getPosR();
		double ratioX; //A ratio of x to y motion
		if(r < 90) ratioX = (r / 90.0);
		else if(r >= 90 && r < 180) ratioX = ((90.0 - (r % 90.0)) / 90.0);
		else if(r >= 180 && r < 270)  ratioX = ((r % 90.0) / 90.0);
		else ratioX = ((90.0 - (r % 90.0)) / 90.0);
		double maxX = ratioX * getMaxVel();
		double ratioY = 1 - Math.abs(ratioX);
		double maxY = ratioY * getMaxVel();
		
  		//Crunch the velocity
		if(getVelX() > maxX / 1000.0) setVelX(maxX);
		if(getVelX() < -maxX / 1000.0) setVelX(-maxX);
		if(getVelY() > maxY / 1000.0) setVelY(maxY);
		if(getVelY() < -maxY / 1000.0) setVelY(-maxY);
		
		if(getVelX() < 9.9E-4 && getVelX() > -9.9E-4) setVelX(0);
		if(getVelY() < 9.9E-4 && getVelY() > -9.9E-4) setVelY(0);
		
		//Apply velocity drag - Ships aren't allowed to move infinitely
		if(getAccX() == 0 && getVelX() != 0) setVelX(getVelX() > 0? getVelX()*1000.0 - getMaxAcc() : getVelX()*1000.0 + getMaxAcc());
		if(getAccY() == 0 && getVelY() != 0) setVelY(getVelY() > 0? getVelY()*1000.0 - getMaxAcc() : getVelY()*1000.0 + getMaxAcc());
		
		//In the future we will apply effects here.
		//Until then the only effect is damage invulnerability.
		if(System.currentTimeMillis() < getLastDamage() + getDamageTime()) setDrawSprite(Global.tint(getBaseSprite(), new Color(200, 0, 0), 0.5f));
		else setDrawSprite(getBaseSprite());
	}

	//Occurs every tick to draw the ship.
	//Rotating the image causes it to appear smaller (the rotated 
	//square is required to fit inside the original square), so
	//some calculations are required to resize it when drawing.
	public void render(Graphics g) {
		//Ship size will be constant for a moment.
		final int SHIP_SIZE = getShipSize();
		
		/* tancalc - This important tangent calculation represents a particular segment on the bounding square.
		 * We can use it to figure out points on the new square
		 * When rendered in my lousy ASCII art skills:
		 *
		 *  
		 *  Tancalc (between the '='s)
		 *  v  
		 *=---=-----------.
		 *|   &***---____ |<--- Bounding image square (identical to the 
		 *|  /           &|     turned image when R = 90, 180, 270, 360, and 0)
		 *|!/           /!|
		 *|/___        /<------ Turned image square
		 *|    ----___/   |  ! - these are equal to R % 90 degrees.
		 *'-----------=---=  & - these are the "effective top left" and "effective top right" of the
		 *                       UNRESIZED ship. They can be used to resize the ship and find bullet firing locations.
		 * Note that Math.tan() uses RADIANS!
		 */
		double tancalc = (SHIP_SIZE * Math.tan(Math.toRadians(getPosR() % 90))) / (Math.tan(Math.toRadians(getPosR() % 90)) + 1);
		Point effectiveTopLeft;
		Point effectiveTopRight;
		
		//Depending on the orientation of the ship these don't always fall on the "top".
		//So it's necessary to figure in what quadrant of rotation the ship is in.
		if(getPosR() < 90) {
			effectiveTopLeft = new Point((int)(getPosX() + tancalc), (int) getPosY());
			effectiveTopRight = new Point(effectiveTopLeft.x + SHIP_SIZE - (int) tancalc, effectiveTopLeft.y + (int)tancalc);
		} else if(getPosR() >= 90 && getPosR() < 180) {
			effectiveTopLeft = new Point((int) (getPosX() + SHIP_SIZE), (int) (getPosY() + tancalc));
			effectiveTopRight = new Point((int) (getPosX() + (SHIP_SIZE - tancalc)), (int) (getPosY() + SHIP_SIZE));
		} else if(getPosR() >= 180 && getPosR() < 270) {
			effectiveTopLeft = new Point((int) (getPosX() + (SHIP_SIZE - tancalc)), (int) (getPosY() + SHIP_SIZE));
			effectiveTopRight = new Point((int) getPosX(), (int) (getPosY() + SHIP_SIZE - (int) tancalc));
		} else {
			effectiveTopLeft = new Point((int) getPosX(), (int) (getPosY() + SHIP_SIZE - (int) tancalc));
			effectiveTopRight = new Point((int) (getPosX() + (int) tancalc), (int) getPosY());
		}
		
		/*
		 * aspectRatio - A ratio, measuring the actual size of the ship versus the drawn size of the ship.
		 * We can use it to resize so that those two are equal.
		 * amountChange - The difference between the sizes of the actual and rotated ships (before resize).
		 * This is used to make sure the ship rotates around its center.
		 */
		double aspectRatio = (SHIP_SIZE / effectiveTopLeft.distance(effectiveTopRight));
		int amountChange = (int) (SHIP_SIZE - effectiveTopLeft.distance(effectiveTopRight));
		
		//Finally, draw the image (aspectRatio) times larger than (SHIP_SIZE), because
		//when it's drawn it will be made (aspectRatio) times smaller.
		//Also, shift the image so it rotates in its center.
		g.drawImage(
				Global.rotate(getDrawSprite(), getPosR()),
				(int) (getPosX() - amountChange/2),
				(int) (getPosY() - amountChange/2),
				(int) (SHIP_SIZE * aspectRatio),
				(int) (SHIP_SIZE * aspectRatio),
				null);
		g.setColor(Color.RED);
	}

	//Return the bounding box of the ship.
	//For rendering reasons, all Ships are square, even if their sprites
	//may be better bounded by a rectangle.
	public Rectangle getBounds() {
		return new Rectangle((int) getPosX(), (int) getPosY(), getShipSize(), getShipSize());
	}

	//Fires a bullet (or round of bullets) from this Ship.
	public abstract void fire();
	//Retrieves the unique Bullet sprite for this Ship (to be used by the bullet in its constructor).
	public abstract BufferedImage getBulletSprite();
	
	/* All Ships (both PlayerShips and EnemyShips) have stats.
	 * Most of them originate from Ship but some come from SolidEntity.
	 * The full list of all stats is as follows:
	 * [SolidEntity]
	 * Max Health | Armor | Damage Invulnerability Time
	 * [Ship]
	 * Acceleration | Velocity | Rotation Speed | Rotation Acceleration
	 * Bullet Damage | Bullet Speed | Size | Firing Speed
	 */
	public abstract double getMaxAcc();
	public abstract double getMaxVel();
	public abstract int getBulletDamage();
	public abstract double getBulletSpeed();
	public abstract double getMaxVelR();
	public abstract double getMaxAccR();
	public abstract int getShipSize();
	public abstract int getFireSpeed();
	
	//Return the color associated with this ship.
	public abstract Color getShipColor();
	//Return the display  name associated with this ship.
	public abstract String getName();
	//Return the lore associated with this ship.
	public abstract String getLore();
}
