package com.spacejoust.framework;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.spacejoust.entities.Entity;
import com.spacejoust.entities.PlayerShip;
import com.spacejoust.entities.Ship;
import com.spacejoust.entities.SolidEntity;


public class KeyInput extends KeyAdapter implements KeyListener {

	private Handler handler;

	private boolean[] keyPressedp1 = new boolean[6];
	private boolean[] keyPressedp2 = new boolean[6];

	public KeyInput(Handler handler){
		this.handler = handler;
		for(@SuppressWarnings("unused") boolean b: keyPressedp1) b = false;
		for(@SuppressWarnings("unused") boolean b: keyPressedp2) b = false;
	}

	@Override
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		for(int i = 0; i < handler.entities.size(); i++){
			Entity temp = handler.entities.get(i);
			if(temp instanceof PlayerShip) ((PlayerShip) temp).press(key);
			
		}
		if(key == KeyEvent.VK_ESCAPE) {
				handler.removeAll();
				handler.initialSpawns();
			}
	}

	@Override
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();

		for(int i = 0; i < handler.entities.size(); i++){
			Entity temp = handler.entities.get(i);
			if(temp instanceof PlayerShip) ((PlayerShip) temp).release(key);
		}
	}
}
