/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.net.URL;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import de.muntjak.tinylookandfeel.*;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * PSColorChooser
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class PSColorChooser extends JDialog {
	
	private Color inColor, outColor;
	private static PSColorChooser instance;
	private ColorSelector colorSelector;
	private HueSelector hueSelector;
	private TwoColorField twoColorField;
	private NumericTextField redField, greenField, blueField;
	private NumericTextField satField, briField, hueField;
	private JButton ok;
	private boolean spinnerUpdate = false;
	
	private static Cursor cs_cursor;
	private static BufferedImage brightmask;
	private static GraphicsConfiguration conf;

	static {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		conf = ge.getDefaultScreenDevice().getDefaultConfiguration();
		brightmask = loadBrightmask();
		cs_cursor = loadCursor();
	}
	
	private static Cursor loadCursor() {
		ImageIcon img = null;
      	Cursor c = null;
      	Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(16, 16);
      		
      	if(size.width == 32) {
      		img = loadImageIcon("/de/muntjak/tinylookandfeel/cp_icons/cs32.gif");
      		c = Toolkit.getDefaultToolkit().createCustomCursor(
      			img.getImage(), new Point(15, 15), "cs_cursor");
      	}
      	else if(size.width == 16) {
      		img = loadImageIcon("/de/muntjak/tinylookandfeel/cp_icons/cs16.gif");
      		c = Toolkit.getDefaultToolkit().createCustomCursor(
      			img.getImage(), new Point(7, 7), "cs_cursor");
      	}
      		
      	return c;
	}
	
	private static BufferedImage loadBrightmask() {
		ImageIcon img = loadImageIcon("/de/muntjak/tinylookandfeel/cp_icons/brightmask.png");
      	BufferedImage bimg = conf.createCompatibleImage(256, 256, Transparency.TRANSLUCENT);
		Graphics g = bimg.getGraphics();
		g.drawImage(img.getImage(), 0, 0, 256, 256, 0, 0, 1, 256, null);
		
		return bimg;
	}
	
	protected static ImageIcon loadImageIcon(String fn) {
		return new ImageIcon(PSColorChooser.class.getResource(fn));
	}

	private PSColorChooser(Frame frame, Color inColor) {
		super(frame, "Color Chooser", true);		
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		this.inColor = inColor;
		outColor  = inColor;
		
		setupUI(frame, inColor);
	}
	
	public static Color showColorChooser(Frame frame, Color inColor) {
		if(instance == null) {
			instance = new PSColorChooser(frame, inColor);
		}
		
		instance.setColor(inColor);
		instance.setVisible(true);
		return instance.outColor;
	}
	
	public static void deleteInstance() {
		instance = null;
	}
	
	public void setColor(Color c) {
		spinnerUpdate = true;
		inColor = c;
		outColor  = inColor;
		int hue = ColorRoutines.getHue(c);
		int sat = ColorRoutines.getSaturation(c);
		int bri = ColorRoutines.getBrightness(c);

		satField.setValue(sat);
		briField.setValue(bri);
		hueField.setValue(hue);
		redField.setValue(c.getRed());
		greenField.setValue(c.getGreen());
		blueField.setValue(c.getBlue());
		
		colorSelector.setColor(c);
		hueSelector.setHue(hue);
		twoColorField.setUpperColor(c);
		twoColorField.setLowerColor(c);
		spinnerUpdate = false;
	}
	
	private void colorChanged(Color c) {
		spinnerUpdate = true;
		int hue = ColorRoutines.getHue(c);
		int sat = ColorRoutines.getSaturation(c);
		int bri = ColorRoutines.getBrightness(c);

		satField.setValue(sat);
		briField.setValue(bri);
		hueField.setValue(hue);
		redField.setValue(c.getRed());
		greenField.setValue(c.getGreen());
		blueField.setValue(c.getBlue());
		
		twoColorField.setUpperColor(c);
		spinnerUpdate = false;
	}
	
	private void hueChanged(int hue) {
		spinnerUpdate = true;
		int sat = satField.getValue();
		int bri = briField.getValue();

		hueField.setValue(hue);
		
		Color c = Color.getHSBColor(
			(float)(hue / 360.0f), (float)(sat / 100.0f), (float)(bri / 100.0f));
		
		redField.setValue(c.getRed());
		greenField.setValue(c.getGreen());
		blueField.setValue(c.getBlue());
		
		twoColorField.setUpperColor(c);
		colorSelector.setColor(c);
		spinnerUpdate = false;
	}
	
	private void setupUI(Frame frame, Color inColor) {
		JPanel p1 = new JPanel(new BorderLayout());
		JPanel p2 = new JPanel(new BorderLayout());

		
		// ColorSelector
		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
		int hue = ColorRoutines.getHue(inColor);
		colorSelector = new ColorSelector(inColor);
		p3.add(colorSelector);
		
		p1.add(p3, BorderLayout.WEST);
		
		// HueSelector
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
		hueSelector = new HueSelector(hue);
		p3.add(hueSelector);
		
		p1.add(p3, BorderLayout.CENTER);
		p2.add(p1, BorderLayout.CENTER);
		
		// TwoColorField
		p1 = new JPanel(new BorderLayout());
		
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
		twoColorField = new TwoColorField(inColor);
		p3.add(twoColorField);
		p1.add(p3, BorderLayout.NORTH);

		// Spinners
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
		p3.add(createNumericTextFields());
		
		p1.add(p3, BorderLayout.CENTER);
		
		p2.add(p1, BorderLayout.EAST);
		
		getContentPane().add(p2, BorderLayout.NORTH);
		
		// buttons
		p3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		p3.setBorder(new EtchedBorder());
		
		JButton b = new JButton("Cancel");
		b.setMnemonic(KeyEvent.VK_C);
		b.addActionListener(new CancelAction());
		p3.add(b);
		
		b = new JButton("OK");
		b.setMnemonic(KeyEvent.VK_O);
		b.addActionListener(new OKAction());
		getRootPane().setDefaultButton(b);
		p3.add(b);
		
		getContentPane().add(p3, BorderLayout.SOUTH);
		
		pack();
		
		Dimension size = getSize();
		setLocation(frame.getLocationOnScreen().x + 
			(frame.getWidth() - getSize().width) / 2,
			frame.getLocationOnScreen().y + 
			(frame.getHeight() - getSize().height) / 2);
	}
	
	private JPanel createNumericTextFields() {
		ActionListener rgbAction = new RGBAction();
		ActionListener hsbAction = new HSBAction();
      	
      	JPanel p2 = new JPanel(new GridBagLayout());
      	GridBagConstraints gc = new GridBagConstraints();
      	gc.insets = new Insets(0, 0, 4, 2);
      	gc.anchor = GridBagConstraints.WEST;
      	gc.gridx = 0;
      	gc.gridy = 0;

      	p2.add(new JLabel("H:"), gc);
      	gc.gridx ++;
      	hueField = new NumericTextField(3, 0, 0, 360);
      	hueField.addActionListener(hsbAction);
      	p2.add(hueField, gc);
      	
      	gc.gridx = 0;
      	gc.gridy ++;
      	p2.add(new JLabel("S:"), gc);
      	gc.gridx ++;
      	satField = new NumericTextField(3, 0, 0, 100);
      	satField.addActionListener(hsbAction);
      	p2.add(satField, gc);
      	
      	gc.gridx = 0;
      	gc.gridy ++;
      	p2.add(new JLabel("B:"), gc);
      	gc.gridx ++;
      	briField = new NumericTextField(3, 0, 0, 100);
      	briField.addActionListener(hsbAction);
      	p2.add(briField, gc);
      	
      	gc.insets = new Insets(8, 0, 4, 2);
      	gc.gridx = 0;
      	gc.gridy ++;
      	p2.add(new JLabel("R:"), gc);
      	gc.gridx ++;
      	redField = new NumericTextField(3, 0, 0, 255);
      	redField.addActionListener(rgbAction);
      	p2.add(redField, gc);

      	gc.gridx = 0;
      	gc.gridy ++;
      	gc.insets = new Insets(0, 0, 4, 2);
      	p2.add(new JLabel("G:"), gc);
      	gc.gridx ++;
      	greenField = new NumericTextField(3, 0, 0, 255);
      	greenField.addActionListener(rgbAction);
      	p2.add(greenField, gc);
      	
      	gc.gridx = 0;
      	gc.gridy ++;
      	p2.add(new JLabel("B:"), gc);
      	gc.gridx ++;
      	blueField = new NumericTextField(3, 0, 0, 255);
      	blueField.addActionListener(rgbAction);
      	p2.add(blueField, gc);
      	
      	return p2;
	}
	
	class ColorSelector extends JPanel {
		
		private Dimension size = new Dimension(258, 258);
		private float hue;
		private int h, s, b;
		private int circleX, circleY;
		private Vector listeners;
		private Color theColor;
		private boolean mousePressed = false;
		
		ColorSelector(Color c) {
			h = ColorRoutines.getHue(c);
			s = ColorRoutines.getSaturation(c) * 255 / 100;
			b = ColorRoutines.getBrightness(c) * 255 / 100;
			hue = (float)(h / 360.0);
			theColor = c;
			
			setBorder(new LineBorder(Color.BLACK, 1));
			addMouseListener(new Mousey());
			addMouseMotionListener(new MouseyDrag());
			setCursor(cs_cursor);
		}

		void setColor(Color c) {
			h = hueField.getValue();
			s = satField.getValue() * 255 / 100;
			b = briField.getValue() * 255 / 100;
			hue = (float)(h / 360.0);
			
			repaint(0);
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		
		public void paint(Graphics g) {
			super.paintBorder(g);

			if(mousePressed) {
				g.setClip(circleX, circleY, 11, 11);
			}
			
			
			Color c;
			float sat;
			
			for(int x = 0; x < 256; x++) {
				sat = (float)(x / 255.0);
				c = Color.getHSBColor(hue, sat, 1.0f);					
				g.setColor(c);
				g.drawLine(x + 1, 1, x + 1, 256);
			}
			
			g.drawImage(brightmask, 1, 1, this);
			
			
			circleX = s - 4;		// 0 => 0, 100 => 255
			circleY = 255 - b - 4;	// 100 => 0, 0 => 255
			
			if(mousePressed) {
				g.setClip(circleX, circleY, 11, 11);
			}
			
			if(b < 160) {
				g.setColor(Color.WHITE);
			}
			else {
				g.setColor(Color.BLACK);
			}

			g.drawLine(circleX + 3, circleY, circleX + 7, circleY);
			g.drawLine(circleX + 3, circleY + 10, circleX + 7, circleY + 10);
			g.drawLine(circleX, circleY + 3, circleX, circleY + 7);
			g.drawLine(circleX + 10, circleY + 3, circleX + 10, circleY + 7);
			
			g.drawLine(circleX + 2, circleY + 1, circleX + 2, circleY + 1);
			g.drawLine(circleX + 8, circleY + 1, circleX + 8, circleY + 1);
			g.drawLine(circleX + 1, circleY + 2, circleX + 1, circleY + 2);
			g.drawLine(circleX + 9, circleY + 2, circleX + 9, circleY + 2);
			
			g.drawLine(circleX + 1, circleY + 8, circleX + 1, circleY + 8);
			g.drawLine(circleX + 9, circleY + 8, circleX + 9, circleY + 8);
			g.drawLine(circleX + 2, circleY + 9, circleX + 2, circleY + 9);
			g.drawLine(circleX + 8, circleY + 9, circleX + 8, circleY + 9);
		}
		
		public void update(Graphics g) {
			paint(g);
		}
		
		class Mousey extends MouseAdapter {

			public void mousePressed(MouseEvent e) {
				if(e.getX() < 1 || e.getX() > 256) return;
				if(e.getY() < 1 || e.getY() > 256) return;
				
				s = e.getX() - 1;
				if(s < 0) s = 0;
				else if(s > 255) s = 255;
				
				int y = e.getY() - 1;
				if(y < 0) y = 0;
				else if(y > 255) y = 255;
				
				mousePressed = true;
				
				b = 255 - y;
				paint(ColorSelector.this.getGraphics());
				theColor = Color.getHSBColor(hue, (float)(s / 255.0), (float)(b / 255.0));				
				colorChanged(theColor);
			}
			
			public void mouseReleased(MouseEvent e) {
				mousePressed = false;
			}
		}
		
		class MouseyDrag extends MouseMotionAdapter {

			public void mouseDragged(MouseEvent e) {
				s = e.getX() - 1;
				if(s < 0) s = 0;
				else if(s > 255) s = 255;
				
				int y = e.getY() - 1;
				if(y < 0) y = 0;
				else if(y > 255) y = 255;
				
				b = 255 - y;
				paint(ColorSelector.this.getGraphics());
				theColor = Color.getHSBColor(hue, (float)(s / 255.0), (float)(b / 255.0));				
				colorChanged(theColor);
			}
		}
	}
	
	class TwoColorField extends JPanel {
		
		private Dimension size = new Dimension(60, 68);
		private Color upperColor, lowerColor;
		
		TwoColorField(Color c) {
			setBorder(new LineBorder(Color.BLACK, 1));
			
			upperColor = c;
			lowerColor = c;
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		
		void setUpperColor(Color c) {
			upperColor = c;
			outColor = c;
			repaint(0);
		}
		
		void setLowerColor(Color c) {
			lowerColor = c;
			repaint(0);
		}
		
		public void paint(Graphics g) {
			super.paintBorder(g);
			
			g.setColor(upperColor);
			g.fillRect(1, 1, 58, 33);
			
			g.setColor(lowerColor);
			g.fillRect(1, 34, 58, 33);
		}
	}
	
	class HueSelector  extends JPanel {
		
		private Color darkColor = new Color(128, 128, 128);
		private Dimension size = new Dimension(35, 266);
		private float hue;
		private int arrowY;
		private Vector listeners;
		
		HueSelector(int hue) {
			this.hue = (float)(hue / 360.0);
			addMouseListener(new Mousey());
			addMouseMotionListener(new MouseyDrag());
		}
		
		void setHue(int hue) {
			this.hue = (float)(hue / 360.0);
			repaint(0);
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		
		public void paint(Graphics g) {
			g.setColor(Theme.backColor.getColor());
			g.fillRect(0, 0, 35, 266);
			
			drawArrows(g);
					
			// border
			g.setColor(darkColor);
			g.drawLine(6, 3, 27, 3);
			g.drawLine(6, 3, 6, 261);
			
			g.setColor(Color.WHITE);
			g.drawLine(6, 262, 28, 262);
			g.drawLine(28, 3, 28, 261);
			
			g.setColor(Color.BLACK);
			g.drawRect(7, 4, 20, 257);
			
			// gradients
			int x1 = 8;
			int x2 = 26;
			int y1 = 5;
			
			for(int y = 0; y < 256; y++) {
				float h = (float)((255 - y) / 255.0);
				g.setColor(Color.getHSBColor(h, 1.0f, 1.0f));
				g.drawLine(x1, y + y1, x2, y + y1);
			}
		}
		
		private void drawArrows(Graphics g) {
			arrowY = 255 + 5 - (int)(hue * 255.0);
			
			g.setColor(Color.BLACK);
			g.drawLine(0, arrowY - 5, 0, arrowY + 5);
			g.drawLine(1, arrowY - 4, 1, arrowY - 4);
			g.drawLine(2, arrowY - 3, 2, arrowY - 3);
			g.drawLine(3, arrowY - 2, 3, arrowY - 2);
			g.drawLine(4, arrowY - 1, 4, arrowY - 1);
			g.drawLine(5, arrowY, 5, arrowY);
			g.drawLine(0, arrowY + 5, 0, arrowY + 5);
			g.drawLine(1, arrowY + 4, 1, arrowY + 4);
			g.drawLine(2, arrowY + 3, 2, arrowY + 3);
			g.drawLine(3, arrowY + 2, 3, arrowY + 2);
			g.drawLine(4, arrowY + 1, 4, arrowY + 1);
			
			g.drawLine(34, arrowY - 5, 34, arrowY + 5);
			g.drawLine(33, arrowY - 4, 33, arrowY - 4);
			g.drawLine(32, arrowY - 3, 32, arrowY - 3);
			g.drawLine(31, arrowY - 2, 31, arrowY - 2);
			g.drawLine(30, arrowY - 1, 30, arrowY - 1);
			g.drawLine(29, arrowY, 29, arrowY);
			g.drawLine(34, arrowY + 5, 34, arrowY + 5);
			g.drawLine(33, arrowY + 4, 33, arrowY + 4);
			g.drawLine(32, arrowY + 3, 32, arrowY + 3);
			g.drawLine(31, arrowY + 2, 31, arrowY + 2);
			g.drawLine(30, arrowY + 1, 30, arrowY + 1);
		}
		
		class Mousey extends MouseAdapter {

			public void mousePressed(MouseEvent e) {
				if(e.getY() < 5 || e.getY() > 260) return;

				hue = (float)((255 - (e.getY() - 5)) / 255.0);
				
				repaint();				
				hueChanged((int)(hue * 360.0));
			}
		}
		
		class MouseyDrag extends MouseMotionAdapter {

			public void mouseDragged(MouseEvent e) {
				int y = e.getY() - 5;
				if(y < 0) y = 0;
				else if(y > 255) y = 255;
				
				hue = (float)((255 - y) / 255.0);
				repaint();
				hueChanged((int)(hue * 360.0));
			}
		}
	}
	
	class RGBAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if(spinnerUpdate) return;
					
			int r = redField.getValue();
			int g = greenField.getValue();
			int b = blueField.getValue();
			
			Color c = new Color(r, g, b);
			twoColorField.setUpperColor(c);
			
			spinnerUpdate = true;
			int hue = ColorRoutines.getHue(c);
			hueField.setValue(hue);
			satField.setValue(ColorRoutines.getSaturation(c));
  			briField.setValue(ColorRoutines.getBrightness(c));
  			spinnerUpdate = false;
  			
  			hueSelector.setHue(hue);
  			colorSelector.setColor(c);
		}
	}
	
	class HSBAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if(spinnerUpdate) return;
			
			int h = hueField.getValue();
			int s = satField.getValue();
			int b = briField.getValue();
			
			Color c = Color.getHSBColor(
				(float)(h / 360.0f), (float)(s / 100.0f), (float)(b / 100.0f));
			twoColorField.setUpperColor(c);
			
			spinnerUpdate = true;
			redField.setValue(c.getRed());
			greenField.setValue(c.getGreen());
  			blueField.setValue(c.getBlue());
  			spinnerUpdate = false;
  			
  			hueSelector.setHue(h);
  			colorSelector.setColor(c);
		}
	}
	
	class OKAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
	
	class CancelAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			outColor = null;
			setVisible(false);
		}
	}
}
