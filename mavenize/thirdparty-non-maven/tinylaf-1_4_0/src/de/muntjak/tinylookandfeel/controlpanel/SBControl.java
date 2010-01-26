/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.util.SBReference;

/**
 * SBControl is a controller component used to specify
 * a color by a reference color, a brightness value and
 * a saturation value.
 * @author Hans Bickel
 *
 */
public class SBControl extends JPanel implements Selectable {
	
	protected static final ControlPanel cp = ControlPanel.instance;
	protected final int cpSize = 10;
	protected boolean forceUpdate = false;
	protected Dimension size = new Dimension(64, 20);
	protected SBReference sbReference;
	protected int controlMode = ControlPanel.CONTROLS_ALL;
	private boolean selected = false;
	
	SBControl(SBReference ref, int controlMode) {
		this(ref, false, controlMode);
	}
	
	SBControl(SBReference ref, boolean forceUpdate, int controlMode) {
		this.sbReference = ref;
		this.forceUpdate = forceUpdate;
		this.controlMode = controlMode;

		if(ref == null) return;
		
		update();
		addMouseListener(new Mousey());
	}
	
	/**
	 * Constructor for the following fields:
	 * mainColor, backColor, disColor, frameColor
	 * @param ref
	 * @param height
	 */
	SBControl(SBReference ref) {
		this.sbReference = ref;
		forceUpdate = true;
		size.height = 24;

		if(ref == null) return;
		
		update();
		addMouseListener(new Mousey());
	}

	public SBReference getSBReference() {
		return sbReference;
	}

	public Color getColor() {
		return sbReference.getColor();
	}
	
	public boolean isLocked() {
		return (sbReference != null && sbReference.isLocked());
	}
	
	public void setBackground(Color bg) {
		if(sbReference == null) {
			super.setBackground(bg);
		}
		else {
			super.setBackground(sbReference.getColor());
		}
	}
	
	public void update() {
		if(sbReference != null) {
			setBackground(sbReference.update());
		}
		
		repaint();
		updateTTT();
	}

	public void updateTTT() {
		if(sbReference == null) {
			setToolTipText(null);
			return;
		} 
		
		Color c = sbReference.getColor();
		StringBuffer buff = new StringBuffer("<html>");
		
		if(sbReference.isAbsoluteColor()) {
			buff.append("R:" + c.getRed());
			buff.append(" G:" + c.getGreen());
			buff.append(" B:" + c.getBlue());
		}
		else {
			buff.append("S:" + sbReference.getSaturation());
			buff.append(" B:" + sbReference.getBrightness());
			buff.append(" (" + sbReference.getReferenceString() + ")");
			buff.append(" R:" + c.getRed());
			buff.append(" G:" + c.getGreen());
			buff.append(" B:" + c.getBlue());
		}
		
		if(sbReference.equals(Theme.mainColor)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.MAIN_COLOR));
		}
		else if(sbReference.equals(Theme.backColor)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.BACK_COLOR));
		}
		else if(sbReference.equals(Theme.disColor)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.DIS_COLOR));
		}
		else if(sbReference.equals(Theme.frameColor)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.FRAME_COLOR));
		}
		else if(sbReference.equals(Theme.sub1Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB1_COLOR));
		}
		else if(sbReference.equals(Theme.sub2Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB2_COLOR));
		}
		else if(sbReference.equals(Theme.sub3Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB3_COLOR));
		}
		else if(sbReference.equals(Theme.sub4Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB4_COLOR));
		}
		else if(sbReference.equals(Theme.sub5Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB5_COLOR));
		}
		else if(sbReference.equals(Theme.sub6Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB6_COLOR));
		}
		else if(sbReference.equals(Theme.sub7Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB7_COLOR));
		}
		else if(sbReference.equals(Theme.sub8Color)) {
			buff.append("<br>References: " +
				SBReference.getNumReferences(SBReference.SUB8_COLOR));
		}
		
		setToolTipText(buff.toString());
	}
	
	public void setSBReference(SBReference ref) {
		this.sbReference = ref;
		update();
	}
	
	public Dimension getPreferredSize() {
		return size;
	}
	
	public void paint(Graphics g) {
		// With colored fonts, sbReference can be null
		if(sbReference == null) {
			g.setColor(Theme.backColor.getColor());
			g.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		
		// fill with background
		g.setColor(getBackground());
		g.fillRect(2, 2, getWidth() - 3, getHeight() - 3);
		
		// paint border
		if(selected) {
			g.setColor(Color.DARK_GRAY);
			g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
			g.setColor(Color.RED);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
		else {
			g.setColor(Color.DARK_GRAY);
			g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
			g.setColor(Theme.backColor.getColor());
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
		
		if(sbReference == null || sbReference.isLocked()) return;

		// paint left rectangle
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(2, 2, cpSize, getHeight() - 4);
		g.setColor(Color.BLACK);
		g.fillRect(cpSize + 2, 2, 1, getHeight() - 4);
		
		// paint gradient boxes
		if(sbReference.isAbsoluteColor()) {
			int x = getWidth() - 20;
			float hue = 0.0f;
			
			g.drawLine(x - 1, 2, x - 1, getHeight() - 3);
			
			for(int i = 0; i < 18; i++) {
				g.setColor(Color.getHSBColor(hue, 0.5f, 1.0f));
				g.drawLine(x + i, 2, x + i, getHeight() - 3);
				hue += 1.0 / 19.0;
			}
		}
		else {
			int x = getWidth() - 20;
			int grey = 255;
			
			g.drawLine(x - 1, 2, x - 1, getHeight() - 3);
			
			for(int i = 0; i < 18; i++) {
				g.setColor(new Color(grey, grey, grey));
				g.drawLine(x + i, 2, x + i, getHeight() - 3);
				grey -= 255 / 18;
			}
		}
	}
	
	class Mousey extends MouseAdapter {
		
		public void mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger() && !sbReference.isLocked()) {
				if(e.getX() <= cpSize) {
					cp.showCPSBPopup(SBControl.this);
				}
				else {
					cp.showSBPopup(SBControl.this);
				}
			}
		}
		
		public void mousePressed(MouseEvent e) {
			if(sbReference == null) return;

			requestFocusInWindow();
			
			if(e.isControlDown()) {
				if(!selected) {
					cp.selection.add(SBControl.this);
				}
				return;
			}
			else if(e.isAltDown()) {
				if(selected) {
					cp.selection.remove(SBControl.this);
				}
				return;
			}

			if(e.getX() <= cpSize) {
				cp.showCPSBPopup(SBControl.this);
				return;
			}
			
			if(e.isPopupTrigger() && !sbReference.isLocked()) {
				cp.showSBPopup(SBControl.this);
				return;
			}
			
			if(e.getX() > getWidth() - 19 && !sbReference.isLocked()) {
				cp.showSBPopup(SBControl.this);
				return;
			}
			
			if(e.getButton() != MouseEvent.BUTTON1) return;

			Color newColor = null;
			
			if(sbReference.isAbsoluteColor()) {
				newColor =
					PSColorChooser.showColorChooser(cp.theFrame, getColor());
				
				if(newColor == null) return;	// cancelled
				if(newColor.equals(sbReference.getColor())) return;	// unchanged

				cp.storeUndoData(SBControl.this);
				sbReference.setColor(newColor);
			}
			else {
				newColor = SBChooser.showSBChooser(cp.theFrame, SBControl.this);
				
				if(newColor == null) return;	// cancelled
				if(sbReference.getBrightness() == SBChooser.getBrightness() &&
					sbReference.getSaturation() == SBChooser.getSaturation()) return;	// unchanged
				
				cp.storeUndoData(SBControl.this);
				sbReference.setColor(SBChooser.getSaturation(), SBChooser.getBrightness());
			}
			
			update();
			cp.initPanels();	// update all derived colors...
			updateTargets(true);
		}
	}
	
	void updateTargets(boolean activateApplyButton) {
		if(forceUpdate) {
			if(activateApplyButton) {
				cp.examplePanel.update(true);
			}
			else {
				cp.initPanels();	// update all derived colors...
				cp.setTheme();
			}
		}
		else {
			cp.repaintTargets(controlMode);
		}
	}
	
	public String toString() {
		return "SBField[ref=" + sbReference + "]";
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if(this.selected == selected) return;
		
		this.selected = selected;
		repaint();
	}
}
