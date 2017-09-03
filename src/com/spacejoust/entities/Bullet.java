package com.spacejoust.entities;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.spacejoust.framework.Global;

/*
 * Bullet: A SolidEntity spawned from a Ship source that deals damage
 * to another SolidEntity that it intersects, then destroys itself.
 */
public class Bullet extends SolidEntity {

	//The amount of damage the bullet can deal
	private int damage;
	
	//The Ship that the Bullet was fired from
	private Ship origin;
	
	//Particle information
	private int MAX_PARTICLES = 14;
	private int MAX_PARTICLE_SPEED = 100;
	private int PARTICLE_SIZE = 2;
	private int PARTICLE_DURATION = 700;

	//Position and origin constructor
	public Bullet(double x, double y, double rot, Ship origin, int damage) {
		super(x, y, rot);
		this.origin = origin;
		this.damage = damage;
		setBaseSprite(origin.getBulletSprite());
		setDrawSprite(origin.getBulletSprite());
		setHealth(1);
	}

	//Update the position of the bullet.
	//Uses standard motion plus support for damage.
	@Override
	public void update() {
		applyMotion();
		
		if(! Global.isInBounds(this)) getHandler().removeEntity(this);
		
		for(Entity entity: getHandler().getEntities()) {
			SolidEntity g;
			if(entity instanceof SolidEntity) g = (SolidEntity) entity;
			else continue;
			if(getBounds().intersects(g.getBounds()) && g != this && g != origin) {
				g.damage(damage);
				if(g instanceof Ship) {
					Ship ship = (Ship) g;
					Particle p;
					for(int i = (int) (Math.random() * (MAX_PARTICLES - 5)) + 5; i > 0; i--) {
						p = new Particle(this.getPosX(), this.getPosY(), PARTICLE_SIZE, MAX_PARTICLE_SPEED, PARTICLE_DURATION, ship.getShipColor());
						getHandler().addEntity(p);
					}
				}
				getHandler().removeEntity(this);
			}
		}
	}
	
	//Draw the bullet onto the Graphics.
	//With large objects we can use tangent calculations to deal with size variation
	//but that slows stuff down
	//and bullets are small anyway
	//so there is not much point in doing it for bullets.
	@Override
	public void render(Graphics g) {
		g.drawImage(Global.rotate(getDrawSprite(), getPosR()), (int) getPosX(), (int) getPosY(), 10, 10, null);
	}

	//Return the bounds of this Bullet.
	@Override
	public Rectangle getBounds() {
		return new Rectangle((int) getPosX(), (int) getPosY(), 2, 7);
	}
	
	//Getters and setters: Damage
	public int getDamage() { return damage; }
	public void setDamage(int damage) { this.damage = damage; }
}
