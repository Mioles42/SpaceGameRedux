package com.spacejoust.framework;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.spacejoust.entities.EnemyScout;
import com.spacejoust.entities.Entity;
import com.spacejoust.entities.Planet;
import com.spacejoust.entities.PlayerShip;
import com.spacejoust.entities.Star;

public class MainMenuGame extends Game implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private Handler handler;
	private java.util.Random random = new java.util.Random();
	private BufferedImage title;
	
	private SpaceButton versus;


	public MainMenuGame(Window w) {
		super(w);
		
		//1. Create a new Handler for this game and link it to the keyboard
		handler = new Handler();
		KeyInput input = new KeyInput(handler);
		
		//2. Listen for keyboard input
		this.addKeyListener(input);
		w.addKeyListener(input);
		
		//3. Set up other components
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/res/components.png"));
		addComponents(sheet);
	}
	
	public void tick(){
		handler.update();
		if(random.nextInt(300) == 0) handler.addEntity(new Star());
		if(random.nextInt(100000) == 0) handler.addEntity(new Planet());
	}

	public void renderEntities(Graphics g){
		this.setBackground(Color.black);
		
		handler.render(g);
		g.drawImage(title, (Global.WIDTH / 2) - 225, (Global.HEIGHT / 4) - 112, 450, 225, null);
	}

	private void addComponents(SpriteSheet sheet) {
		BufferedImage versusImage = sheet.crop(1, 193, 63, 63);
		versus = new SpaceButton("");
		versus.spriteUnpressed = versusImage;
		versus.spritePressed = Global.tint(versusImage, new Color(0, 0, 0), 0.3f);
		versus.spritePressedHovering = Global.tint(versusImage, new Color(0, 0, 0), 0.3f);
		versus.spriteHovering = Global.tint(versusImage, new Color(255, 255, 255), 0.3f);
		versus.setLocation(Global.WIDTH / 5 - 32, Global.HEIGHT * 5 / 8);
		versus.setSize(63, 63);
		versus.setVisible(true);
		versus.addActionListener(this);
		title = sheet.crop(1, 129, 127, 63);
		this.add(versus);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		switchMode(VersusGame.class);
	}

	@Override
	public Object[] getEndState() {
		// TODO Auto-generated method stub
		return null;
	}
}
