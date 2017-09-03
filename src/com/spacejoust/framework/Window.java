package com.spacejoust.framework;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class Window extends JFrame implements WindowListener, ComponentListener {

	//A type of Game that is currently playing.
	private Game game;
	
	//Info about the last Game that was processed. state could be any information about that game; the Object[]
	//must be expanded into useable variables according to its state schema.
	private Game lastMode;
	private Object[] state;

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new Window(Global.WIDTH, Global.HEIGHT, "SpaceJoust");
	}

	public Window(int width, int height, String title) {
		super(title);

		this.getContentPane().setBackground(Color.BLACK);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(0, 0);
		this.setSize(width,  height);
		this.addWindowListener(this);
		this.addComponentListener(this);
		
		SpriteSheet ships = new SpriteSheet(ImageLoader.loadImage("/res/ships.png"));
		this.setIconImage(ships.crop(1, 0, 63, 63));

		game = new MainMenuGame(this);
		game.requestFocusInWindow();
		game.setIgnoreRepaint(true);
		game.setSize(width, height);
		this.add(game);
		game.start(null);

		this.setVisible(true);
	}

	public void switchMode(Game mode, Object[] state) {
		game.stop();
		lastMode = game;
		this.remove(game);
		game = mode;
		game.requestFocusInWindow();
		game.setIgnoreRepaint(true);
		game.setSize(Global.WIDTH, Global.HEIGHT);
		this.add(game);
		game.start(state);
		repaint();
	}
	
	public void componentResized(ComponentEvent e) {
		Global.setWidth(this.getWidth());
		Global.setHeight(this.getHeight());
		game.setSize(Global.WIDTH, Global.HEIGHT);
		repaint();
	}
	public void windowClosing(WindowEvent e) {
		game.stop();
		System.exit(0);
	}

	//Extra doNothing() interface methods
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
}
