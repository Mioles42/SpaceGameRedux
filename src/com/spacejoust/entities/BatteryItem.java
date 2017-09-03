package com.spacejoust.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Global;

public class BatteryItem extends Item {

	public BatteryItem(int x, int y) {
		super(x, y, items.crop(33, 64, 31, 31));
	}

	@Override
	public void use(PlayerShip p) {
		Global.playSound(Global.BATTERY_SOUND);
		p.heal(7);
		if(p.getMaxHealth() < p.getHealth()) p.setHealth(p.getMaxHealth());
	}

	@Override
	public void update() {
		applyMotion();

		if(! Global.isInBounds(this)) getHandler().removeEntity(this);

		for(Entity entity: getHandler().getEntities()) {
			if(entity instanceof PlayerShip && getBounds().intersects(((PlayerShip) entity).getBounds())) {
				PlayerShip ship = (PlayerShip) entity;
				if (ship.addItem(this)) {
					Global.playSound(Global.ITEM_SOUND);
					getHandler().removeEntity(this); //If the item is successfully picked up
				}
			}
			else continue;
		}
	}
}
