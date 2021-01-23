/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.guis.customComponents;

import emit.ipcv.engines.interfaces.observerPatterns.ObservableClass;
import emit.ipcv.engines.interfaces.observerPatterns.ObserverClass;
import emit.ipcv.engines.interfaces.observerPatterns.ZoomListener;
import emit.ipcv.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rinelfi
 */
public class ImageViewer extends JPanel implements ObservableClass<int[]>, MouseMotionListener, MouseListener, MouseWheelListener {
	
	private ImageLoader imageLoader;
	private Integer xMargin,
		yMargin,
		scaledWidth,
		scaledHeight,
		imageWidth,
		imageHeight,
		userScale,
		xMouseMargin,
		yMouseMargin,
		zoomSize;
	final int zoomMin = 12, zoomMax = 1500;
	private final List<ObserverClass<int[]>> observers;
	private final List<ZoomListener> zoomListeners;
	private boolean readyToMove;
	private String imagePreviewMessage;
	
	public ImageViewer() {
		imagePreviewMessage = "Aperçu d'image";
		observers = new ArrayList<>();
		zoomListeners = new ArrayList<>();
		xMargin = null;
		yMargin = null;
		scaledWidth = null;
		scaledHeight = null;
		imageWidth = null;
		imageHeight = null;
		xMouseMargin = null;
		yMouseMargin = null;
		zoomSize = 4;
		readyToMove = false;
		userScale = 100;
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setColor(Color.decode("#4f4f4f"));
		graphics2d.fillRect(0, 0, getWidth(), getHeight());
		graphics2d.setColor(Color.WHITE);
		BufferedImage bufferedImage = null;
		if (imageLoader != null) {
			bufferedImage = imageLoader.getBufferedImage();
		}
		int myWidth = getWidth(), myHeight = getHeight(), leftOffset = 0, rightOffset = 0;
		imageWidth = bufferedImage != null ? bufferedImage.getWidth() : null;
		imageHeight = bufferedImage != null ? bufferedImage.getHeight() : null;
		
		if (imageLoader == null) {
			String defaultFontName = new JLabel().getFont().getName();
			Font stringFont = new Font(defaultFontName, Font.PLAIN, 20);
			FontMetrics metrics = graphics2d.getFontMetrics(stringFont);
			// Determine the X coordinate for the text
			int x = (myWidth - metrics.stringWidth(imagePreviewMessage)) / 2;
			// Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
			int y = (myHeight - metrics.getHeight()) / 2 + metrics.getAscent();
			// Set the font
			graphics2d.setFont(stringFont);
			graphics2d.drawString(imagePreviewMessage, x, y);
		} else {
			
			// Resizing
			if (imageWidth < myWidth && imageHeight < myHeight) {
				scaledWidth = imageWidth;
				scaledHeight = imageHeight;
				leftOffset = myWidth / 2 - imageWidth / 2;
				rightOffset = myHeight / 2 - imageHeight / 2;
			} else if (myWidth > myHeight) {
				if (imageWidth <= imageHeight) {
					scaledWidth = imageWidth * myHeight / imageHeight;
					scaledHeight = myHeight;
					rightOffset = 0;
					leftOffset = (myWidth - scaledWidth) / 2;
					if (leftOffset < 0) {
						scaledWidth = myWidth;
						scaledHeight = imageHeight * myWidth / imageWidth;
						rightOffset = (myHeight - scaledHeight) / 2;
						leftOffset = 0;
					}
				} else if (imageWidth > imageHeight) {
					scaledWidth = myWidth;
					scaledHeight = imageHeight * myWidth / imageWidth;
					rightOffset = (myHeight - scaledHeight) / 2;
					leftOffset = 0;
					if (rightOffset < 0) {
						scaledWidth = imageWidth * myHeight / imageHeight;
						scaledHeight = myHeight;
						leftOffset = (myWidth - scaledWidth) / 2;
						rightOffset = 0;
					}
				}
			} else {
				if (imageWidth - imageHeight <= 0) {
					scaledWidth = imageWidth * myHeight / imageHeight;
					scaledHeight = myHeight;
					leftOffset = (myWidth - scaledWidth) / 2;
				} else if (imageWidth - imageHeight > 0) {
					scaledWidth = myWidth;
					scaledHeight = imageHeight * myWidth / imageWidth;
					rightOffset = (myHeight - scaledHeight) / 2;
				}
			}
			// Resizing
			
			int lostWidth = scaledWidth - scaledWidth * userScale / 100;
			int lostHeight = scaledHeight - scaledHeight * userScale / 100;
			scaledWidth = scaledWidth * userScale / 100;
			scaledHeight = scaledHeight * userScale / 100;
			xMargin = readyToMove ? xMargin : (int) (leftOffset + lostWidth / (float) 2);
			yMargin = readyToMove ? yMargin : (int) (rightOffset + lostHeight / (float) 2);
			graphics2d.drawImage(bufferedImage, xMargin, yMargin, scaledWidth, scaledHeight, null);
		}
	}
	
	public void setImageLoader(final ImageLoader imageHelper) {
		this.imageLoader = imageHelper;
		repaint();
	}
	
	public ImageLoader getImageLoader() {
		return this.imageLoader;
	}
  
  public void setImagePreviewMessage(String imagePreviewMessage) {
    this.imagePreviewMessage = imagePreviewMessage;
    repaint();
  }
  
  @Override
	public void addObserver(ObserverClass<int[]> observer) {
		observers.add(observer);
	}
	
	@Override
	public void clearObserver() {
		observers.clear();
	}
	
	@Override
	public void update(int[] data) {
		observers.forEach(observer -> observer.callback(data));
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (imageLoader != null) {
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
		boolean imageExiste = scaledWidth != null && scaledHeight != null;
		boolean deplacementGauche = xMouseMargin != null && (e.getX() - xMouseMargin) >= (getWidth() - scaledWidth) - (userScale / 100),
			deplacementDroite = xMouseMargin != null && (e.getX() - xMouseMargin) - (userScale / 100) <= 0,
			deplacementHaut = yMouseMargin != null && (e.getY() - yMouseMargin) >= (getHeight() - scaledHeight) - (userScale / 100),
			deplacementBas = yMouseMargin != null && (e.getY() - yMouseMargin) - (userScale / 100) <= 0;
		if (imageExiste && readyToMove) {
			if (scaledWidth > getWidth() && deplacementGauche && deplacementDroite) {
				xMargin = e.getX() - xMouseMargin;
			}
			if (scaledHeight > getHeight() && deplacementHaut && deplacementBas) {
				yMargin = e.getY() - yMouseMargin;
			}
			this.repaint();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		boolean imageExists = scaledWidth != null && scaledHeight != null,
			validX = xMargin != null && e.getX() >= xMargin && e.getX() <= (scaledWidth + xMargin),
			validY = yMargin != null && e.getY() >= yMargin && e.getY() <= (scaledHeight + yMargin);
		if (imageExists && validX && validY) {
			int x = (e.getX() - xMargin) * imageWidth / scaledWidth,
				y = (e.getY() - yMargin) * imageHeight / scaledHeight;
			update(new int[]{x, y});
		}
	}
	
	public void setUserScale(int userScale) {
		this.userScale = userScale;
		updateZoom(userScale);
		this.repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		readyToMove = true;
		boolean imageExists = scaledWidth != null && scaledHeight != null;
		if (imageExists) {
			xMouseMargin = scaledWidth > getWidth() ? e.getX() - xMargin : null;
			yMouseMargin = scaledHeight > getHeight() ? e.getY() - yMargin : null;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		readyToMove = false;
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		userScale -= e.getWheelRotation() * zoomSize;
		userScale = userScale < zoomMin || userScale > zoomMax ? (userScale < zoomMin ? zoomMin : zoomMax) : userScale;
		updateZoom(userScale);
		this.repaint();
	}
	
	public void zoomIn() {
		userScale += zoomSize;
		userScale = userScale < zoomMin || userScale > zoomMax ? (userScale < zoomMin ? zoomMin : zoomMax) : userScale;
		updateZoom(userScale);
		this.repaint();
	}
	
	public void zoomOut() {
		userScale -= zoomSize;
		userScale = userScale < zoomMin || userScale > zoomMax ? (userScale < zoomMin ? zoomMin : zoomMax) : userScale;
		updateZoom(userScale);
		this.repaint();
	}
	
	public void addZoomListener(ZoomListener e) {
		zoomListeners.add(e);
	}
	
	private void updateZoom(int value) {
		zoomListeners.forEach(zoomListener -> {
			zoomListener.update(value);
		});
	}
}
