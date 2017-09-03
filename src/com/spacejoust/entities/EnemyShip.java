package com.spacejoust.entities;

import java.awt.Color;

//EnemyShip: Any Ship not controlled by a player.
//They drop items sometimes.
public abstract class EnemyShip extends Ship {
	public EnemyShip(double x, double y, double rot) {
		super(x, y, rot);
    }
	
	@Override
	public void update() {
		if(getHealth() <= 0) {
			Item drop = getDrop();
			if(drop != null) getHandler().addEntity(drop);
		}
		super.update();
	}
	
	@Override
	public Color getShipColor() {
		return new Color(100, 10, 0);
	}
	
	public abstract Item getDrop();
}