package com.spacejoust.framework;

import java.awt.Component;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;

public abstract class Game extends JPanel implements Runnable {
	
	//The Thread this Game will run on.
	private Thread thread;
	//Whether the is running
	private boolean running;
	//Keep Eclipse from being annoying
	private static final long serialVersionUID = 1098234752002793743L;
	//The Window this Game is running in.
	private Window window;
	//State information from the last Game.
	private Object[] state;

	public Game(Window window) {
		this.window = window;
		super.setLayout(null);
	}
	
	public void run() {
		//Game loop
		long timer1MSec = System.currentTimeMillis();
		int ticks = 0;
		while(running){
			long now = System.currentTimeMillis();
			
			if(now > timer1MSec) {
				ticks++;
				window.repaint();
				tick();
				//System.out.println(ticks);
				timer1MSec = now;
			}
		}
		endGame();
	}
	
	public synchronized void start(Object[] state){
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	private synchronized void endGame(){
		try{
			thread.join();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		renderEntities(g);
		paintChildren(g);
		g.dispose();
	}
	
	public synchronized void stop() {
		running = false;
	}
	
	protected void switchMode(Class<?> mode) {
		try {
			window.switchMode((Game) (mode.getDeclaredConstructor(Window.class).newInstance(window)), getEndState());
		} catch (Exception e) {
			System.err.println("Error instantiating new game mode");
			e.printStackTrace();
		}
	}
	
	public Object[] getState() {
		return state;
	}
	
	public abstract void renderEntities(Graphics g);
	public abstract void tick();
	public abstract Object[] getEndState();
}
