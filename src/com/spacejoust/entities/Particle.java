package com.spacejoust.entities;

import java.awt.Color;
import java.awt.Graphics;

public class Particle extends Entity {
	
	int size;
	int duration;
	final double PARTICLE_DRAG = .2;
	Color color;

	public Particle(double x, double y, int size, int duration, int speed, Color color) {
		super(x, y, 0);
		this.size = size;
		this.duration = duration;
		this.color = color;
		setVelX((int) (Math.random() * speed) - speed/2);
		setVelY((int) (Math.random() * speed) - speed/2);
	}
	
	@Override
	public void update() {
		applyMotion();
		if((int) (Math.random() * duration) == 0) getHandler().removeEntity(this);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(color);
		g.fillRect((int) getPosX(), (int) getPosY(), size, size);
	}

	@Override
	public void applyMotion() {
		super.applyMotion();
		if(getAccX() == 0 && getVelX() != 0) setVelX(getVelX() > 0? getVelX()*1000.0 - PARTICLE_DRAG : getVelX()*1000.0 + PARTICLE_DRAG);
		if(getAccY() == 0 && getVelY() != 0) setVelY(getVelY() > 0? getVelY()*1000.0 - PARTICLE_DRAG : getVelY()*1000.0 + PARTICLE_DRAG);
	}
}
