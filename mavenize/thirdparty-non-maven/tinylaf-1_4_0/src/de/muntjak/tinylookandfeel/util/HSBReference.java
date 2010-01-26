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
import java.util.Vector;

import javax.swing.plaf.ColorUIResource;


/**
 * HSBReference describes a (mutable) color, specified
 * by a reference color and values for brightness, hue
 * and saturation.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class HSBReference extends SBReference {
	
	protected int hue;
	protected boolean preserveGrey;

	/**
	 * Constructor for icon colorizers.
	 * @param hue
	 * @param sat
	 * @param bri
	 * @param ref
	 */
	public HSBReference(int hue, int sat, int bri, int ref) {
		super();
		
		this.hue = hue;
		this.sat = sat;
		this.bri = bri;
		this.ref = ref;
		preserveGrey = true;
	}
	
	/**
	 * Copy-constructor.
	 * @param other
	 */
	public HSBReference(HSBReference other) {
		super(false);
		
		color = new ColorUIResource(other.color);
		hue = other.hue;
		sat = other.sat;
		bri = other.bri;
		ref = other.ref;
		preserveGrey = other.preserveGrey;
	}
	
	public void update(HSBReference other) {
		color = new ColorUIResource(other.color);
		hue = other.hue;
		sat = other.sat;
		bri = other.bri;
		ref = other.ref;
		preserveGrey = other.preserveGrey;
	}
	
	public void update(HSBReference other, Vector referenceColors) {
		color = new ColorUIResource(other.color);
		hue = other.hue;
		sat = other.sat;
		bri = other.bri;
		ref = other.ref;
		preserveGrey = other.preserveGrey;
	}
	
	public int getHue() {
		return hue;
	}
	
	public void setHue(int newHue) {
		hue = newHue;
	}

	public void load(DataInputStream in) throws IOException {
		try {
			hue = in.readInt();
			sat = in.readInt();
			bri = in.readInt();
			ref = in.readInt();
			preserveGrey = in.readBoolean();
		} catch(Exception ex) {
			throw new IOException("HSBReference.load() : " + ex.getMessage());
		}
	}
	
	public void save(DataOutputStream out) throws IOException {
		out.writeInt(hue);
		out.writeInt(sat);
		out.writeInt(bri);
		out.writeInt(ref);
		out.writeBoolean(preserveGrey);
	}

	public boolean isPreserveGrey() {
		return preserveGrey;
	}

	public void setPreserveGrey(boolean b) {
		preserveGrey = b;
	}
	
	public String toString() {
		return "HSBReference[bri=" + bri + ",sat=" + sat +
		",hue=" + hue + ",ref=" + ref + ",c=(" + color.getRed() + "," +
			color.getGreen() + "," + color.getBlue() + ")]";
	}
}
