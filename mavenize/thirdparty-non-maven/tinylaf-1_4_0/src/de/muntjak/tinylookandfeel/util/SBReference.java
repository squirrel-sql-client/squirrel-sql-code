/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.util;

import java.awt.*;
import java.io.*;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel;
import de.muntjak.tinylookandfeel.controlpanel.SBChooser;
import de.muntjak.tinylookandfeel.controlpanel.SBControl;

/**
 * SBReference describes a (mutable) color, specified
 * by a reference color and values for brightness and
 * saturation.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class SBReference {

	// ref values
	public static final int ABS_COLOR 		= 1;
	public static final int MAIN_COLOR 		= 2; 
	public static final int BACK_COLOR 		= 3;
	public static final int DIS_COLOR 		= 4;
	public static final int FRAME_COLOR 	= 5;
	public static final int SUB1_COLOR 		= 6;
	public static final int SUB2_COLOR 		= 7;
	public static final int SUB3_COLOR 		= 8;
	public static final int SUB4_COLOR 		= 9;
	public static final int SUB5_COLOR 		= 10;
	public static final int SUB6_COLOR 		= 11;
	public static final int SUB7_COLOR 		= 12;
	public static final int SUB8_COLOR 		= 13;
	
	// instances stores all created SBReferences so
	// we can calculate the number of references for
	// derived colors
	private static final Vector instances = new Vector();

	protected ColorUIResource color;
	protected ColorUIResource referenceColor;
	protected int sat = 0, bri = 0;
	protected int ref;
	protected boolean locked;
	protected ColorIcon icon;
	protected static ColorIcon absoluteIcon;
	
	/**
	 * Constructor for font colors and super constructor for
	 * HSBReferences. SBReference will be added to
	 * instances vector.
	 *
	 */
	public SBReference() {
		color = new ColorUIResource(Color.BLACK);
		ref = ABS_COLOR;
		instances.add(this);
	}
	
	/**
	 * Super constructor for HSBReference copy constructor.
	 * This SBReference will not be added to instances vector.
	 *
	 * @param dummy ignored
	 */
	public SBReference(boolean dummy) {
		color = new ColorUIResource(Color.BLACK);
		ref = ABS_COLOR;
	}

	/**
	 * Called from Theme.initData()
	 * @param c
	 * @param sat
	 * @param bri
	 * @param ref
	 */
	public SBReference(Color c, int sat, int bri, int ref) {
		color = new ColorUIResource(c);
		this.sat = sat;
		this.bri = bri;
		this.ref = ref;
		instances.add(this);
	}
	
	/**
	 * Constructor for our 4 locked color references.
	 * @param c
	 * @param sat
	 * @param bri
	 * @param ref
	 * @param locked
	 */
	public SBReference(Color c, int sat, int bri, int ref, boolean locked) {
		color = new ColorUIResource(c);
		this.sat = sat;
		this.bri = bri;
		this.ref = ref;
		this.locked = locked;
		// cannot be a reference, so don't add to instances
	}
	
	/**
	 * Copy constructor.
	 * @param other
	 */
	public SBReference(SBReference other) {
		color = new ColorUIResource(other.color);
		sat = other.sat;
		bri = other.bri;
		ref = other.ref;
		locked = other.locked;
		// ! do not add to instances !
	}
	
	/**
	 * Returns a copy of this SBReference and additionally
	 * stores the color reference (if any).
	 * Called if user copies parameters via Copy-Paste popup.
	 * @return
	 */
	public SBReference copy() {
		SBReference retVal = new SBReference(this);
		
		if(!isAbsoluteColor()) {
			retVal.referenceColor = getReferenceColor();
		}

		return retVal;
	}
	
	public static int getNumReferences(int ref) {
		int n = 0;
		
		Iterator ii = instances.iterator();
		while(ii.hasNext()) {
			if(ref == ((SBReference)ii.next()).ref) {
				n ++;
			}
		}
//		System.out.println("instances=" + instances.size() +
//			" references=" + n);
		return n;
	}
	
	public static void printReferences(int ref) {
		Iterator ii = instances.iterator();
		while(ii.hasNext()) {
			SBReference sb = (SBReference)ii.next();
			
			if(ref == sb.ref) {
				System.out.println(sb);
			}
		}
	}

	public void update(Color c, int sat, int bri, int ref) {
		color = new ColorUIResource(c);
		this.sat = sat;
		this.bri = bri;
		this.ref = ref;
	}
	
	/**
	 * Called if user pastes parameters via Copy-Paste popup.
	 * @param cr
	 */
	public void update(SBReference sb) {
//		System.out.println("update: " + sb + "\n  " + sb.referenceColor);
		if(sb.isAbsoluteColor() || sb.referenceColor == null) {
			color = new ColorUIResource(sb.color);
			sat = sb.sat;
			bri = sb.bri;
			ref = sb.ref;
		}
		else {
			update(sb, sb.referenceColor);
		}		
	}
	
	/**
	 * Updates this SBReference from values of <code>other</code>
	 * argument. If the current reference color differs from the
	 * one stored in <code>referenceColors</code> vector, this
	 * SBReference will switch to absolute color mode.
	 * @param other
	 * @param referenceColors
	 */
	public void update(SBReference other, Vector referenceColors) {
		if(other.isAbsoluteColor()) {
			color = new ColorUIResource(other.color);
			sat = other.sat;
			bri = other.bri;
			ref = other.ref;
		}
		else {
			update(other, (ColorUIResource)referenceColors.get(other.ref - 2));
		}
	}
	
	/**
	 * 
	 * @param sb
	 * @param cr
	 */
	private void update(SBReference sb, ColorUIResource cr) {
		// sb is not an absolute color and cr is non-null
//		System.out.println("update: " + sb + "\n  " + cr);
		
		if(!sb.getReferenceColor().equals(cr)) {
			// first look for another reference color which is
			// equal to the stored reference color
			int newRef = getRefForColor(cr);
//			System.out.println("1) newRef=" + newRef + " (" + ref + ")");
			
			if(newRef != -1) {
				ref = newRef;
				sat = sb.sat;
				bri = sb.bri;
				return;	// we are through
			}
			
			// now look for a reference color without references
			// (so it can be changed) - start with last color
			newRef = getEmptyReferenceColor();
//			System.out.println("2) newRef=" + newRef + " (" + ref + ")");
			
			if(newRef != -1) {
				ref = newRef;
				SBControl control = ControlPanel.instance.getSBControlFromRef(ref);

				control.getSBReference().setReference(ABS_COLOR);
				control.getSBReference().setSaturation(0);
				control.getSBReference().setBrightness(0);
				control.getSBReference().setColor(cr);
				control.update();
				
				sat = sb.sat;
				bri = sb.bri;
				return;	// we are through
			}
			
			// change to absolute color
			color = new ColorUIResource(sb.getColor());
			sat = 0;
			bri = 0;
			ref = ABS_COLOR;
//			System.out.println("Change to absolute: " + this);
			return;
		}

		color = new ColorUIResource(sb.color);
		sat = sb.sat;
		bri = sb.bri;
		ref = sb.ref;
//		System.out.println("Simply updated: " + this);
	}
	
	/**
	 * If one of reference colors sub1 to sub8 has
	 * no references, returns its ref value, returns -1 if
	 * no empty reference color was found.
	 * @return
	 */
	private int getEmptyReferenceColor() {
		for(int i = SUB1_COLOR; i <= SUB8_COLOR; i++) {
			if(getNumReferences(i) == 0) {
				return i;
			}
		}
		
		return -1;
	}
	
	private int getRefForColor(ColorUIResource cr) {
		if(Theme.mainColor.getColor().equals(cr)) {
			return MAIN_COLOR;
		}
		else if(Theme.backColor.getColor().equals(cr)) {
			return BACK_COLOR;
		}
		else if(Theme.disColor.getColor().equals(cr)) {
			return DIS_COLOR;
		}
		else if(Theme.frameColor.getColor().equals(cr)) {
			return FRAME_COLOR;
		}
		else if(Theme.sub1Color.getColor().equals(cr)) {
			return SUB1_COLOR;
		}
		else if(Theme.sub2Color.getColor().equals(cr)) {
			return SUB2_COLOR;
		}
		else if(Theme.sub3Color.getColor().equals(cr)) {
			return SUB3_COLOR;
		}
		else if(Theme.sub4Color.getColor().equals(cr)) {
			return SUB4_COLOR;
		}
		else if(Theme.sub5Color.getColor().equals(cr)) {
			return SUB5_COLOR;
		}
		else if(Theme.sub6Color.getColor().equals(cr)) {
			return SUB6_COLOR;
		}
		else if(Theme.sub7Color.getColor().equals(cr)) {
			return SUB7_COLOR;
		}
		else if(Theme.sub8Color.getColor().equals(cr)) {
			return SUB8_COLOR;
		}
		
		return -1;
	}

	public void update(Color c) {
		color = new ColorUIResource(c);
		sat = 0;
		bri = 0;
		ref = ABS_COLOR;
	}
	
	public void reset() {
		sat = 0;
		bri = 0;
	}
	
	public ColorUIResource getColor() {
		return color;
	}
	
	public int getSaturation() { return sat; }
	
	public int getBrightness() { return bri; }
	
	public int getReference() { return ref; }
	
	public ColorUIResource getReferenceColor() {
		return getReferencedColor(ref);
	}
	
	public static ColorUIResource getReferencedColor(int ref) {
		switch(ref) {
			case MAIN_COLOR:
				return Theme.mainColor.getColor();
			case BACK_COLOR:
				return Theme.backColor.getColor();
			case DIS_COLOR:
				return Theme.disColor.getColor();
			case FRAME_COLOR:
				return Theme.frameColor.getColor();
			case SUB1_COLOR:
				return Theme.sub1Color.getColor();
			case SUB2_COLOR:
				return Theme.sub2Color.getColor();
			case SUB3_COLOR:
				return Theme.sub3Color.getColor();
			case SUB4_COLOR:
				return Theme.sub4Color.getColor();
			case SUB5_COLOR:
				return Theme.sub5Color.getColor();
			case SUB6_COLOR:
				return Theme.sub6Color.getColor();
			case SUB7_COLOR:
				return Theme.sub7Color.getColor();
			case SUB8_COLOR:
				return Theme.sub8Color.getColor();
			default:
				return null;
		}
	}
	
	public String getReferenceString() {
		switch(ref) {
			case MAIN_COLOR:
				return "Main Color";
			case BACK_COLOR:
				return "Back Color";
			case DIS_COLOR:
				return "Disabled Color";
			case FRAME_COLOR:
				return "Frame Color";
			case SUB1_COLOR:
				return "Sub1 Color";
			case SUB2_COLOR:
				return "Sub2 Color";
			case SUB3_COLOR:
				return "Sub3 Color";
			case SUB4_COLOR:
				return "Sub4 Color";
			case SUB5_COLOR:
				return "Sub5 Color";
			case SUB6_COLOR:
				return "Sub6 Color";
			case SUB7_COLOR:
				return "Sub7 Color";
			case SUB8_COLOR:
				return "Sub8 Color";
			default:
				return "";
		}
	}
	
	public void setColor(Color newColor) {
		color = new ColorUIResource(newColor);
	}

	public void setSaturation(int newSat) {
		sat = newSat;
	}
	
	public void setBrightness(int newBri) {
		bri = newBri;
	}
	
	public void setReference(int newRef) {
		ref = newRef;
	}
	
	public void setColor(int sat, int bri) {
		if(isAbsoluteColor()) return;
		
		this.sat = sat;
		this.bri = bri;
		
		updateColor();
	}
	
	private void updateColor() {
		switch(ref) {
			case MAIN_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
					Theme.mainColor.getColor(), sat, bri));
				break;
			case BACK_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
					Theme.backColor.getColor(), sat, bri));
				break;
			case DIS_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
					Theme.disColor.getColor(), sat, bri));
				break;
			case FRAME_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
					Theme.frameColor.getColor(), sat, bri));
				break;
			case SUB1_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
					Theme.sub1Color.getColor(), sat, bri));
				break;
			case SUB2_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
					Theme.sub2Color.getColor(), sat, bri));
				break;
			case SUB3_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
						Theme.sub3Color.getColor(), sat, bri));
				break;
			case SUB4_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
						Theme.sub4Color.getColor(), sat, bri));
				break;
			case SUB5_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
						Theme.sub5Color.getColor(), sat, bri));
				break;
			case SUB6_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
						Theme.sub6Color.getColor(), sat, bri));
				break;
			case SUB7_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
						Theme.sub7Color.getColor(), sat, bri));
				break;
			case SUB8_COLOR:
				color = new ColorUIResource(
					ColorRoutines.getAdjustedColor(
						Theme.sub8Color.getColor(), sat, bri));
				break;
		}
	}
	
	public ColorUIResource update() {
		if(isAbsoluteColor()) return color;
		
		updateColor();
		
		return color;
	}
	
	public boolean isAbsoluteColor() {
		return (ref == ABS_COLOR);
	}
	
	public boolean isReferenceColor() {
		return locked ||
			this.equals(Theme.sub1Color) ||
			this.equals(Theme.sub2Color) ||
			this.equals(Theme.sub3Color) ||
			this.equals(Theme.sub4Color) ||
			this.equals(Theme.sub5Color) ||
			this.equals(Theme.sub6Color) ||
			this.equals(Theme.sub7Color) ||
			this.equals(Theme.sub8Color);
	}
	
	public void setLocked(boolean newLocked) {
		locked = newLocked;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public String toString() {
		return "SBReference[bri=" + bri + ",sat=" + sat +
			",ref=" + ref + ",c=(" + color.getRed() + "," +
			color.getGreen() + "," + color.getBlue() + ")]";
	}
	
	public Icon getIcon() {
		if(icon == null) {
			icon = new ColorIcon(false);
		}
		
		return icon;
	}
	
	public Icon getAbsoluteIcon() {		
		if(absoluteIcon == null) {
			absoluteIcon = new ColorIcon(true);
		}
		
		return absoluteIcon;
	}
	
	public void save(DataOutputStream out) throws IOException {
		out.writeInt(color.getRGB());
		out.writeInt(sat);
		out.writeInt(bri);
		out.writeInt(ref);
		out.writeBoolean(locked);
	}
	
	public void load(DataInputStream in) throws IOException {
		try {
			if(Theme.fileID >= Theme.FILE_ID_3A) {
				color = new ColorUIResource(in.readInt());
			}
			else {
				color = new ColorUIResource(in.readInt(), in.readInt(), in.readInt());
			}

			sat = in.readInt();
			bri = in.readInt();
			ref = in.readInt();
			locked = in.readBoolean();
		} catch(Exception ex) {
			throw new IOException("SBReference.load() : " + ex);
		}
	}
	
	public static void loadDummyData(DataInputStream in) throws IOException {
		try {
			if(Theme.fileID >= Theme.FILE_ID_3A) {
				in.readInt();
			}
			else {
				in.readInt();
				in.readInt();
				in.readInt();
			}

			in.readInt();
			in.readInt();
			in.readInt();
			in.readBoolean();
		} catch(Exception ex) {
			throw new IOException("SBReference.loadDummyData() : " + ex.getMessage());
		}
	}
	
	class ColorIcon implements Icon {

		private boolean paintGradients;
		
		ColorIcon(boolean paintGradients) {
			this.paintGradients = paintGradients;
		}
		
		public int getIconHeight() {
			return 16;
		}

		public int getIconWidth() {
			return 16;
		}
		
		public void paintIcon(Component comp, Graphics g, int x, int y) {
			Color tempCol = g.getColor();

			g.setColor(Color.GRAY);
			g.drawRect(x, y, getIconWidth(), getIconHeight());
			
			if(paintGradients) {
				float hue = 0.0f;

				for(int i = 0; i < 15; i++) {
					g.setColor(Color.getHSBColor(hue, 0.5f, 1.0f));
					g.drawLine(x + 1 + i, y + 1, x + 1 + i, y + getIconHeight() - 1);
					hue += 1.0 / 16.0;
				}
			}
			else {
				g.setColor(color);
				g.fillRect(x + 1, y + 1, getIconWidth() - 1, getIconHeight() - 1);
			}
			
			// draw arrow
			if(comp instanceof AbstractButton) {
				if(((AbstractButton)comp).isSelected()) {
					g.setColor(Color.WHITE);
					drawArrow(g, x + 1, y + 1);
					
					g.setColor(Color.BLACK);
					drawArrow(g, x, y);
				}
			}
			
			g.setColor(tempCol);
		}
		
		private void drawArrow(Graphics g, int x, int y) {
			g.drawLine(x + 3, y + 5, x + 3, y + 7);
			g.drawLine(x + 4, y + 6, x + 4, y + 8);
			g.drawLine(x + 5, y + 7, x + 5, y + 9);
			g.drawLine(x + 6, y + 6, x + 6, y + 8);
			g.drawLine(x + 7, y + 5, x + 7, y + 7);
			g.drawLine(x + 8, y + 4, x + 8, y + 6);
			g.drawLine(x + 9, y + 3, x + 9, y + 5);
		}
	}
}
