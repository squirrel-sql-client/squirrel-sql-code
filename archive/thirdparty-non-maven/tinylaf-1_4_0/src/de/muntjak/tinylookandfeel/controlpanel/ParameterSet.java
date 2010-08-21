/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.Insets;
import java.util.Vector;

import javax.swing.plaf.InsetsUIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.HSBControl;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.SpreadControl;
import de.muntjak.tinylookandfeel.util.BooleanReference;
import de.muntjak.tinylookandfeel.util.ColoredFont;
import de.muntjak.tinylookandfeel.util.HSBReference;
import de.muntjak.tinylookandfeel.util.IntReference;
import de.muntjak.tinylookandfeel.util.SBReference;

/**
 * ParameterSet
 * @author Hans Bickel
 *
 */
public class ParameterSet {

	static ControlPanel controlPanel;
	private ParameterSetGenerator generator;
	private Vector references;
	private Vector values;
	private Vector referenceColors;
	private String name;
	
	/**
	 * Creates a new ParameterSet, either from the current
	 * selection or from an entire CP.
	 * @param generator
	 * @param name
	 */
	ParameterSet(ParameterSetGenerator generator, String name) {
		this.generator = generator;
		this.name = name;
		values = new Vector();
		references = new Vector();
		referenceColors = getReferenceColors();
	}
	
	/**
	 * Gets vector of ColorUIResources.
	 * @return
	 */
	private Vector getReferenceColors() {

		Vector v = new Vector();
		v.add(Theme.mainColor.getColor());
		v.add(Theme.backColor.getColor());
		v.add(Theme.disColor.getColor());
		v.add(Theme.frameColor.getColor());
		v.add(Theme.sub1Color.getColor());
		v.add(Theme.sub2Color.getColor());
		v.add(Theme.sub3Color.getColor());
		v.add(Theme.sub4Color.getColor());
		v.add(Theme.sub5Color.getColor());
		v.add(Theme.sub6Color.getColor());
		v.add(Theme.sub7Color.getColor());
		v.add(Theme.sub8Color.getColor());
		
		return v;
	}
	
	/**
	 * Stores current reference colors.
	 *
	 */
	public void updateReferenceColors() {
		referenceColors = getReferenceColors();
	}
	
	/**
	 * Copy constructor.
	 * @param ps
	 */
	public ParameterSet(ParameterSet ps) {
		generator = ps.generator;
		name = ps.name;
		references = (Vector)ps.references.clone();
		values = (Vector)ps.values.clone();
		// Note: It's essential to retrieve the
		// current colors (and not copy from argument)
		referenceColors = getReferenceColors();
	}
	
	public ParameterSetGenerator getGenerator() {
		return generator;
	}

	public String getUndoString() {
		return "Paste " + name + " Parameters";
	}
	
	/**
	 * Clones argument's value vector.
	 * @param other
	 */
	public void updateValues(ParameterSet other) {
		values = (Vector)other.values.clone();
	}
	
	/**
	 * Sets all values to the current value of the reference.
	 * This allows for doing a redo after an undo
	 * (or vice versa). Changes values only.
	 *
	 */
	public void updateValues() {
		Vector temp = new Vector(values.size());
		int end = values.size();
		
		for(int i = 0; i < end; i++) {
			Object value = values.get(i);
			Object reference = references.get(i);
			
			if(reference instanceof BooleanReference) {
				// value is Boolean
				temp.add(new Boolean(((BooleanReference)reference).getValue()));
			}
			// because HSBReference *is a* SBReference,
			// it must come before SBReference
			else if(reference instanceof HSBReference) {
				// value is HSBReference
				temp.add(new HSBReference((HSBReference)reference));
			}
			else if(reference instanceof SBReference) {
				// value is SBReference
				temp.add(new SBReference((SBReference)reference));
			}
			else if(reference instanceof IntReference) {
				// value is Integer
				temp.add(new Integer(((IntReference)reference).getValue()));
			}
			else if(reference instanceof InsetsUIResource) {
				// value is Insets
				InsetsUIResource r = (InsetsUIResource)reference;

				temp.add(new Insets(r.top, r.left, r.bottom, r.right));
			}
			else if(reference instanceof ColoredFont) {
				// value is ColoredFont
				temp.add(new ColoredFont((ColoredFont)reference));
			}
		}
		
		values = temp;
	}

	void addParameter(boolean value, BooleanReference reference) {
		values.add(new Boolean(value));
		// reference is BooleanReference
		references.add(reference);
	}
	
	void addParameter(SBControl control) {
		if(control.getSBReference().isReferenceColor()) {
			// reference colors must be inserted at the beginning
			if(control.getSBReference().isAbsoluteColor()) {
				values.add(0, new SBReference(control.getSBReference()));
				// reference is SBReference
				references.add(0, control.getSBReference());
			}
			else {	// not an absolute color
				// find index to insert
				int end = values.size();
				int index = 0;
				
				for(int i = 0; i < end; i++) {
					Object value = values.get(i);
					
					if(value instanceof SBReference) {
						if(!((SBReference)value).isReferenceColor()) {
							index = i;
							break;
						}
					}
					else {
						index = i;
						break;
					}
				}
				
				values.add(index, new SBReference(control.getSBReference()));
				// reference is SBReference
				references.add(index, control.getSBReference());
			}
		}
		else {
			values.add(new SBReference(control.getSBReference()));
			// reference is SBReference
			references.add(control.getSBReference());
		}
	}
	
	void addParameter(SpreadControl control) {
		values.add(new Integer(control.getValue()));
		// reference is IntReference
		references.add(control.getIntReference());
	}
	
	void addParameter(IntControl control) {
		values.add(control.getValue());
		// reference is IntReference
		references.add(control.getIntReference());
	}
	
	void addParameter(Insets value, InsetsUIResource reference) {
		values.add(value);
		// reference is InsetsUIResource
		references.add(reference);
	}
	
	void addParameter(HSBControl control) {
		values.add(new HSBReference(control.getHSBReference()));
		// reference is HSBReference
		references.add(control.getHSBReference());
	}
	
	void addParameter(ColoredFont cf) {
		values.add(new ColoredFont(cf));
		// reference is ColoredFont
		references.add(cf);
	}

	/**
	 * Sets the values of all references to the current
	 * values stored.
	 * @param storeUndoData
	 */
	public void pasteParameters(boolean storeUndoData) {
		if(storeUndoData) ControlPanel.instance.storeUndoData(this);

		// set all references to stored value
		int end = values.size();
		
		for(int i = 0; i < end; i++) {
			Object value = values.get(i);
			Object reference = references.get(i);
			
			if(reference instanceof BooleanReference) {
				// value is Boolean
				((BooleanReference)reference).setValue(
					((Boolean)value).booleanValue());
			}
			// because HSBReference *is a* SBReference,
			// it must come before SBReference
			else if(reference instanceof HSBReference) {
				// value is HSBReference
				((HSBReference)reference).update(
					(HSBReference)value, referenceColors);
			}
			else if(reference instanceof SBReference) {
				// value is SBReference
				((SBReference)reference).update(
					(SBReference)value, referenceColors);
			}
			else if(reference instanceof IntReference) {
				// value is Integer
				((IntReference)reference).setValue(
					((Integer)value).intValue());
			}
			else if(reference instanceof InsetsUIResource) {
				// value is Insets
				InsetsUIResource r = (InsetsUIResource)reference;
				Insets v = (Insets)value;
				
				r.top = v.top;
				r.left = v.left;
				r.bottom = v.bottom;
				r.right = v.right;
			}
			else if(reference instanceof ColoredFont) {
				// value is SBReference
				((ColoredFont)reference).update(
					(ColoredFont)value, referenceColors);
			}
		}
		
		generator.init(true);	// not called from setTheme() sequence
		ControlPanel.instance.initPanels();
		ControlPanel.instance.setTheme();
		
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer("ParameterSet:");
		
		int end = values.size();

		for(int i = 0; i < end; i++) {
			Object value = values.get(i);
			Object reference = references.get(i);
			
			buff.append("\n  reference: " + reference);
			buff.append("\n      value: " + value);
		}
		
		return buff.toString();
	}
}
