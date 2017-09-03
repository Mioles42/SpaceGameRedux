package com.spacejoust.entities;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.spacejoust.framework.Handler;
import com.spacejoust.framework.ImageLoader;
import com.spacejoust.framework.SpriteSheet;

/*
 * Entity: Any object with some graphical representation
 * that may move, accelerate, and be destroyed.
 * Entities must keep track of their positions (in bounds),
 * movement, display, rotation, and removal.
 * The width and height of an entity are left
 * to the discretion of subclasses (mostly for use in
 * rendering)
 */
public abstract class Entity {
	
	//X-axis (X) position and motion
	private double posX;
	private double velX;
	private double accX;
	
	//Y-axis (Y) position and motion
	private double posY;
	private double velY;
	private double accY;
	
	//Rotation (R) position and motion
	private double posR;
	private double velR;
	private double accR;
	
	//The Handler that this Entity belongs to
	//Entities must remove themselves or create new Entities 
	//using this Handler.
	private Handler handler;
	
	//Entities may have two sprites:
	//The base sprite, directly from a sprite sheet,
	//and the draw sprite, the actual appearance of the Entity
	//after operations have been made on the base sprite.
	private BufferedImage baseSprite;
	private BufferedImage drawSprite;
	
	//For convenience: References to the sprite sheets
	//containing all of the game sprites.
	static SpriteSheet ships = new SpriteSheet(ImageLoader.loadImage("/res/ships.png"));
	static SpriteSheet enemies = new SpriteSheet(ImageLoader.loadImage("/res/enemies.png"));
	static SpriteSheet environment = new SpriteSheet(ImageLoader.loadImage("/res/environment.png"));
	static SpriteSheet items = new SpriteSheet(ImageLoader.loadImage("/res/items.png"));
	
	//The default decrease in acceleration over time.
	final static double DRAG = 0.0001;
	final static double R_DRAG= -0.01;
	
	//Default constructor, with values 0 and references null.
	public Entity() {
	}
	
	//Positions constructor (including rotation)
	public Entity(double x, double y, double r) {
		posX = x;
		posY = y;
		posR = r;
	}
	
	//Sprite constructor
	public Entity(BufferedImage sprite) {
		baseSprite = sprite;
	}
	
	//Positions and sprite constructor
	public Entity(double x, double y, double r, BufferedImage sprite) {
		posX = x;
		posY = y;
		posR = r;
		baseSprite = sprite;
	}
	
	
	//Any Entity is responsible for moving itself every game tick.
	//A tick is meant to be 1 millisecond, but there is no assurance that this is true.
	public abstract void update();
	
	//Entities are also responsible for drawing themselves
	//onto Graphics. It is not necessary for them to incorporate rotation
	//or even position, though they should use the draw sprite.
	public abstract void render(Graphics g);
	
	//The following is a utility method to apply physics to 
	//the motion variables.
	public void applyMotion() {
		//Position gains velocity
		posX += velX;
		posY += velY;
		posR += velR;
		
		//Velocity gains acceleration
		velX += accX;
		velY += accY;
		velR += accR;
		
		//Recalculate the rotation so it falls in the degree scale (0-359)
		if(posR <= 0) posR += 360;
		if(posR > 360) posR -= 360;
	}
	
	//Getters: Position and motion information
	public double getPosX() { return posX; }
	public double getVelX() { return velX; }
	public double getAccX() { return accX; }
	public double getPosY() { return posY; }
	public double getVelY() { return velY; }
	public double getAccY() { return accY; }
	public double getPosR() { return posR; }
	public double getVelR() { return velR; }
	public double getAccR() { return accR; }
	
	//Getters: Render and update information
	public BufferedImage getBaseSprite() { return baseSprite; }
	public BufferedImage getDrawSprite() { if(drawSprite == null) return baseSprite; else return drawSprite; }
	public Handler getHandler() { return handler; }
	
	//Setters: Position and motion information
	public void setPosX(double value) { posX = value; }
	public void setVelX(double value) { velX = value / 1000.0; }
	public void setAccX(double value) { accX = value / 1000.0; }
	public void setPosY(double value) { posY = value; }
	public void setVelY(double value) { velY = value / 1000.0; }
	public void setAccY(double value) { accY = value / 1000.0; }
	public void setPosR(double value) { posR = value; }
	public void setVelR(double value) { velR = value / 1000.0;; }
	public void setAccR(double value) { accR = value / 1000.0;;}
	
	//Setters: Render and update information
	public void setBaseSprite(BufferedImage value) { baseSprite = value; }
	public void setDrawSprite(BufferedImage value) { drawSprite = value; }
	public void setHandler(Handler value) { handler = value; }
}