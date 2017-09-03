package com.spacejoust.entities;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/*
 * SolidEntity: Any Entity that has a health (and may be damaged),
 * and may intersect with other SolidEntities or bounds.
 * SolidEntities are not just part of the environment, and must have
 * defined bounds (by extension, size).
 * Entities that are not Solid are considered part of the environment
 * (decorations), and not part of the game itself.
 */
public abstract class SolidEntity extends Entity {
	//Health information: Current health, max health, and damage mitigation with defaults
	private int health = 30; 
	private int maxHealth = 30;
	private int armor = 0;
	
	//Damage information: Acceptable time between damages and last damage time with defaults
	private long lastDamage = 0;
	private long damageTime = 400; //milliseconds
	
	//Default constructor
	public SolidEntity() {
		super();
	}
	
	//Positions constructor (including rotation)
	public SolidEntity(double x, double y, double r) {
		super(x, y, r);
	}

	//Sprite constructor
	public SolidEntity(BufferedImage sprite) {
		super(sprite);
	}

	//Positions and sprite constructor
	public SolidEntity(double x, double y, double r, BufferedImage sprite) {
		super(x, y, r, sprite);
	}
	
	//SolidEntities have defined bounds by necessity
	public abstract Rectangle getBounds();

	//Getters: Health information
	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	public int getArmor() { return armor; }
	
	//Setters: Health information
	public void setHealth(int value) { this.health = value; }
	public void setMaxHealth(int value) { this.maxHealth = value; health = value; }
	public void setArmor(int value) { this.armor = value; }

	//Getters: Damage information
	public long getLastDamage() { return lastDamage; }
	public long getDamageTime() { return damageTime; }
	
	//Setters: Damage information
	public void setLastDamage(long value) { this.lastDamage = value; }
	public void setDamageTime(long value) { this.damageTime = value; }
	
	//Mutators: Health and damage
	//Damage is mitigated by armor and can only occur every (lastDamage) milliseconds
	public void damage(int amount) {
//		amount -= armor;
//		if(amount < 0) amount = 0; //armor cannot heal you
		if(System.currentTimeMillis() > lastDamage + damageTime) {
			health -= amount;
			System.out.println(health);
			System.out.println("ouch!");
			lastDamage = System.currentTimeMillis();
		}
	}
	//Healing has no default cooldown and is unaffected by armor.
	public void heal(int amount) {
		health += amount;
	}
}
