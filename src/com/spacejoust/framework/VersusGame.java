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

public class VersusGame extends Game implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private Handler handler;
	private java.util.Random random = new java.util.Random();
	
	private SpaceButton home;
	private SpaceButton pause;
	private SpaceButton settings;

	public VersusGame(Window w) {
		super(w);
		
		//1. Create a new Handler for this game and link it to the keyboard
		handler = new Handler();
		handler.initialSpawns();
		KeyInput input = new KeyInput(handler);
		
		//2. Listen for keyboard input
		this.addKeyListener(input);
		w.addKeyListener(input);
		
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/res/components.png"));
		addComponents(sheet);
	}
	
	public void tick(){
		handler.update();
		if(random.nextInt(300) == 0) handler.addEntity(new Star());
		if(random.nextInt(100000) == 0) handler.addEntity(new Planet());
		if(random.nextInt(10000) == 0) handler.addEntity(new EnemyScout(
				random.nextInt(Global.WIDTH), 
				random.nextInt(Global.HEIGHT),
				random.nextInt(180)));
	}

	public void renderEntities(Graphics g){
		this.setBackground(Color.black);
		handler.render(g);

		//g.setColor(Color.YELLOW);
		//g.drawString(frames, 10, 20);
		//g.drawString(score, 10, 40);
		//g.drawString(level, 10, 60);

		//render health bars
		g.setFont(new Font("Arial", Font.BOLD, 16));
		int numPlayers = 0;
		int player = 0;
		int size = handler.getEntities().size();
		for(int i = 0; i < size; i++) {
			if(i >= handler.getEntities().size()) break;
			Entity o = handler.getEntities().get(i);
			PlayerShip ship;
			if(o instanceof PlayerShip) {
				ship = (PlayerShip)o;
				numPlayers++;
				player = ship.getPlayerNum();
				String playerName = "P" + player;
				Point displayCorner;

				if(numPlayers % 4 == 1) displayCorner = new Point(30, 10);
				else if(numPlayers % 4 == 2) displayCorner = new Point(Global.WIDTH - 100, 10);
				else if(numPlayers % 4 == 3) displayCorner = new Point(30, Global.HEIGHT - 60);
				else displayCorner = new Point(10, Global.HEIGHT - 60);

				FontMetrics metrics = g.getFontMetrics(new Font("Arial", Font.BOLD, 16));
				Rectangle healthBar = new Rectangle(displayCorner.x, displayCorner.y + 30 * (numPlayers / 4), ship.getHealth(), metrics.getHeight());
				g.setColor(new Color(100, 100, 100));
				g.drawRect(healthBar.x, healthBar.y, healthBar.width, healthBar.height);
				g.setColor(ship.getShipColor());
				g.fillRect(healthBar.x, healthBar.y, healthBar.width, healthBar.height);
				g.drawString(playerName, healthBar.x - metrics.stringWidth(playerName) - 3, healthBar.y + metrics.getAscent());
				
				Point item = new Point(healthBar.x + healthBar.width + 5, healthBar.y);
				for(int j = 0; j < ship.getMaxItems(); j++) {
					if(ship.getItem(j) != null) g.drawImage(ship.getItem(j).getBaseSprite(), item.x, item.y, metrics.getHeight(), metrics.getHeight(), null);
					g.drawRect(item.x, item.y, metrics.getHeight(), metrics.getHeight());
					item.x += metrics.getHeight() + 5;
				}
			}
		}
		if(numPlayers == 1) {
			g.setFont(new Font("Arial", Font.BOLD, 55));
			g.drawString("P" + player + " wins! Press Esc to restart.", 20, Global.HEIGHT / 2);
		} else if(numPlayers == 0) {
			g.setColor(new Color(150, 150, 150));
			g.setFont(new Font("Arial", Font.BOLD, 55));
			g.drawString("Tie! Press Esc to restart.", 20, Global.HEIGHT / 2);
		}
		
	}
	
	private void addComponents(SpriteSheet sheet) {
		BufferedImage homeImage = sheet.crop(193, 0, 63, 63);
		home = new SpaceButton("");
		home.spriteUnpressed = homeImage;
		home.spritePressed = Global.tint(homeImage, new Color(0, 0, 0), 0.3f);
		home.spritePressedHovering = Global.tint(homeImage, new Color(0, 0, 0), 0.3f);
		home.spriteHovering = Global.tint(homeImage, new Color(255, 255, 255), 0.3f);
		home.setLocation(10, Global.HEIGHT * 3/8);
		home.setSize(25, 25);
		home.setVisible(true);
		home.addActionListener(this);
		this.add(home);
		
		BufferedImage pauseImage = sheet.crop(193, 64, 63, 63);
		pause = new SpaceButton("");
		pause.spriteUnpressed = pauseImage;
		pause.spritePressed = Global.tint(pauseImage, new Color(0, 0, 0), 0.3f);
		pause.spritePressedHovering = Global.tint(pauseImage, new Color(0, 0, 0), 0.3f);
		pause.spriteHovering = Global.tint(pauseImage, new Color(255, 255, 255), 0.3f);
		pause.setLocation(10, Global.HEIGHT * 7/16);
		pause.setSize(25, 25);
		pause.setVisible(true);
		pause.addActionListener(this);
		this.add(pause);
		
		BufferedImage settingsImage = sheet.crop(193 - 64, 0, 63, 63);
		settings = new SpaceButton("");
		settings.spriteUnpressed = settingsImage;
		settings.spritePressed = Global.tint(settingsImage, new Color(0, 0, 0), 0.3f);
		settings.spritePressedHovering = Global.tint(settingsImage, new Color(0, 0, 0), 0.3f);
		settings.spriteHovering = Global.tint(settingsImage, new Color(255, 255, 255), 0.3f);
		settings.setLocation(10, Global.HEIGHT * 1/2);
		settings.setSize(25, 25);
		settings.setVisible(true);
		settings.addActionListener(this);
		this.add(settings);
		
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == home) switchMode(MainMenuGame.class);
	}

	@Override
	public Object[] getEndState() {
		// TODO Auto-generated method stub
		return null;
	}
	
}