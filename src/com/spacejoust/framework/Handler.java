package com.spacejoust.framework;
import java.awt.Graphics;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import com.spacejoust.entities.*;

public class Handler {

	LinkedList<Entity> entities = new LinkedList<Entity>();
	LinkedList<Entity> trash = new LinkedList<Entity>();

	public void update() {
		try  {
			while(trash.size() > 0) {
				entities.remove(trash.get(0));
				trash.remove(0);
			}
			for(Entity tempObject: entities) {
				tempObject.update();
			}
		}
		catch (ConcurrentModificationException e) {
			System.out.println("caught e");
		}
	}

	public void render(Graphics g){
		for(int i = 0; i < entities.size(); i++) entities.get(i).render(g);
		for(int i = 0; i < entities.size(); i++) if(entities.get(i) instanceof SolidEntity){
			entities.get(i).render(g);
		}
	}

	public void addEntity(Entity entity){
		entities.add(entity);
		entity.setHandler(this);
	}

	public void removeEntity(Entity entity){
		trash.add(entity);
	}

	public LinkedList<Entity> getEntities() {
		return entities;
	}
	
	public void removeAll() {
		System.out.println("Removing all...");
		while(entities.size() > 0) entities.remove();
	}
	
	public void initialSpawns(){
		PlayerShip ship = new BasicShip((Global.WIDTH / 2.0) - 74, (Global.HEIGHT / 2.0) + 42, 0.0, 1);
		this.addEntity(ship);
		PlayerShip ship1 = new SecondaryShip((Global.WIDTH / 2.0) + 74 , (Global.HEIGHT / 2.0) - 42, 180.0, 2);
		ship1.setPlayerNum(2);
		this.addEntity(ship1);
		EnemyScout scout = new EnemyScout(100.0, 100.0, 0.0);
		this.addEntity(scout);
	}
}
