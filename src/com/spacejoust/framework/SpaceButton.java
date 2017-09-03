package com.spacejoust.framework;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;

public class SpaceButton extends JComponent implements MouseListener {

	private static final long serialVersionUID = -2860716427191235910L;
	public BufferedImage spriteHovering;
	public BufferedImage spriteUnpressed;
	public BufferedImage spritePressed;
	public BufferedImage spritePressedHovering;

	private boolean mouseInside = false;
	private boolean isBeingPressed = false;

	private String text = "";
	private Font font = new Font("Arial", Font.PLAIN, 12);

	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public SpaceButton() {
		super();
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/res/components.png"));
		spriteHovering = sheet.crop(1, 1, 95, 31);
		spritePressed = sheet.crop(1, 33, 95, 31);
		spriteUnpressed = sheet.crop(1, 65, 95, 31);
		spritePressedHovering = sheet.crop(1, 97, 95, 31);
	}
	public SpaceButton(String text) {
		super();
		setOpaque(false);
		this.text = text;
		this.addMouseListener(this);
		SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage("/res/components.png"));
		spriteHovering = sheet.crop(1, 1, 95, 31);
		spritePressed = sheet.crop(1, 33, 95, 31);
		spriteUnpressed = sheet.crop(1, 65, 95, 31);
		spritePressedHovering = sheet.crop(1, 97, 95, 31);
	}
	public SpaceButton(int x, int y, int width, int height) {
		
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();

		BufferedImage sprite = spriteUnpressed;
		if(mouseInside) sprite = spriteHovering;
		if(isBeingPressed) sprite = spritePressed;
		if(mouseInside && isBeingPressed)  sprite = spritePressedHovering;
		g2.drawImage(sprite, 0, 0, getSize().width, getSize().height, null);

		FontMetrics metrics = g.getFontMetrics(font);
		int hgt = metrics.getHeight();
		int adv = metrics.stringWidth(text);

		int textLocY = getHeight()/2 + hgt/3;
		int textLocX = getWidth()/2 - adv/2;
		g2.drawString(text, textLocX, textLocY);
		g2.dispose();
	}

	public void addActionListener(ActionListener a) {
		actionListeners.add(a);
	}

	public void removeActionListener(ActionListener a) {
		actionListeners.remove(a);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("press");
		ActionEvent k = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SpaceButton pressed");
		for(ActionListener a: actionListeners) a.actionPerformed(k);
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseInside = true;
		repaint();
	}
	@Override
	public void mouseExited(MouseEvent e) {
		mouseInside = false;
		repaint();
	}
	@Override
	public void mousePressed(MouseEvent e) {
		isBeingPressed = true;
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		isBeingPressed = false;
		repaint();
	}

	public void setText(String text) {
		this.text = text;
	}
	public void setFont(Font font) {
		this.font = font;
	}

}
