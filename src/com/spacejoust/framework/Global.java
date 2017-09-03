package com.spacejoust.framework;

import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.spacejoust.entities.Entity;
public class Global {
	public static int WIDTH = 800;
	public static int HEIGHT = 800;
	public static final int BOUNDING = 42;

	public static URL BULLET_SOUND;
	public static URL LOW_BULLET_SOUND;
	public static URL EXPLOSION_SOUND;
	public static URL ITEM_SOUND;
	public static URL BATTERY_SOUND;

	static {
		BULLET_SOUND = Global.class.getResource("Global.class");
		try {
			//Use BULLET_SOUND as a temporary variable to hold the root
			BULLET_SOUND = getParentURL(getParentURL(getParentURL(getParentURL(getParentURL((BULLET_SOUND))))));
			LOW_BULLET_SOUND = getChildURL(BULLET_SOUND, "sound/littlebang.wav");
			EXPLOSION_SOUND = getChildURL(BULLET_SOUND, "sound/explosion.wav");
			ITEM_SOUND = getChildURL(BULLET_SOUND, "sound/item.wav");
			BATTERY_SOUND = getChildURL(BULLET_SOUND, "sound/battery.wav");
			BULLET_SOUND = getChildURL(BULLET_SOUND, "sound/lasergun1.wav");
			
		} catch (MalformedURLException e) { //MURLEs!
			e.printStackTrace();
		} catch (URISyntaxException e) { 
			e.printStackTrace();
		}
	}

	public static URL getParentURL(URL url) throws URISyntaxException, MalformedURLException {
		URI uri = url.toURI();
		URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
		return parent.toURL();
	}
	public static URL getChildURL(URL url, String child) throws MalformedURLException {
		return new URL(url.toString() + child);
	}

	public static void playSound(URL url) {
		Clip clip;
		try { 
			clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(url);
			clip.open(inputStream);
			clip.start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage rotate(BufferedImage src, double angle) {
		int w = src.getWidth();
		int h = src.getHeight();

		AffineTransform t = new AffineTransform();
		double ang = Math.toRadians(angle);
		t.setToRotation(ang, w / 2d, h / 2d);

		// source image rectangle
		Point[] points = {
				new Point(0, 0),
				new Point(w, 0),
				new Point(w, h),
				new Point(0, h)
		};

		// transform to destination rectangle
		t.transform(points, 0, points, 0, 4);

		// get destination rectangle bounding box
		Point min = new Point(points[0]);
		Point max = new Point(points[0]);
		for (int i = 1, n = points.length; i < n; i ++) {
			Point p = points[i];
			double pX = p.getX(), pY = p.getY();

			// update min/max x
			if (pX < min.getX()) min.setLocation(pX, min.getY());
			if (pX > max.getX()) max.setLocation(pX, max.getY());

			// update min/max y
			if (pY < min.getY()) min.setLocation(min.getX(), pY);
			if (pY > max.getY()) max.setLocation(max.getX(), pY);
		}

		// determine new width, height
		w = (int) (max.getX() - min.getX());
		h = (int) (max.getY() - min.getY());

		// determine required translation
		double tx = min.getX();
		double ty = min.getY();

		// append required translation
		AffineTransform translation = new AffineTransform();
		translation.translate(-tx, -ty);
		t.preConcatenate(translation);

		AffineTransformOp op = new AffineTransformOp(t, null);
		BufferedImage dst = new BufferedImage(w, h, src.getType());
		op.filter(src, dst);

		return dst;
	}
	public static BufferedImage tint(BufferedImage image, Color color, float alpha) {
		BufferedImage mask = generateMask(image, color, alpha);
		return tint(image, mask);
	}
	private static BufferedImage tint(BufferedImage master, BufferedImage tint) {
		int imgWidth = master.getWidth();
		int imgHeight = master.getHeight();

		BufferedImage tinted = createCompatibleImage(imgWidth, imgHeight, Transparency.TRANSLUCENT);
		Graphics2D g2 = tinted.createGraphics();
		applyQualityRenderingHints(g2);
		g2.drawImage(master, 0, 0, null);
		g2.drawImage(tint, 0, 0, null);
		g2.dispose();

		return tinted;
	}
	private static BufferedImage createCompatibleImage(int width, int height, int transparency) {
		BufferedImage image = getGraphicsConfiguration().createCompatibleImage(width, height, transparency);
		image.coerceData(true);
		return image;
	}
	private static GraphicsConfiguration getGraphicsConfiguration() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}
	private static void applyQualityRenderingHints(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
	}
	private static BufferedImage generateMask(BufferedImage imgSource, Color color, float alpha) {
		int imgWidth = imgSource.getWidth();
		int imgHeight = imgSource.getHeight();

		BufferedImage imgMask = createCompatibleImage(imgWidth, imgHeight, Transparency.TRANSLUCENT);
		Graphics2D g2 = imgMask.createGraphics();
		applyQualityRenderingHints(g2);

		g2.drawImage(imgSource, 0, 0, null);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
		g2.setColor(color);

		g2.fillRect(0, 0, imgSource.getWidth(), imgSource.getHeight());
		g2.dispose();

		return imgMask;
	}

	public static boolean isInBounds(Entity gameObject) {
		if(gameObject.getPosX() < -BOUNDING || gameObject.getPosX() > WIDTH + BOUNDING) return false;
		if(gameObject.getPosY() < -BOUNDING || gameObject.getPosY() > HEIGHT + BOUNDING) return false;
		return true;
	}
	public static boolean isInBounds(int x, int y) {
		if(x < -BOUNDING || x > WIDTH + BOUNDING) return false;
		if(y < -BOUNDING || y > HEIGHT + BOUNDING) return false;
		return true;
	}
	public static double crunchX(double x) {
		if(x > WIDTH - BOUNDING) return WIDTH - BOUNDING;
		if(x < -BOUNDING) return -BOUNDING;
		return x;
	}
	public static double crunchY(double y) {
		if(y > HEIGHT - BOUNDING) return HEIGHT - BOUNDING;
		if(y < -BOUNDING) return -BOUNDING;
		return y;
	}
	public static void setWidth(int wIDTH) {
		WIDTH = wIDTH;
	}
	public static void setHeight(int hEIGHT) {
		HEIGHT = hEIGHT;
	}
}