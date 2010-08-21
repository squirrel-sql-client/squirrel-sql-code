/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.InsetsUIResource;

/**
 * InsetsControl is a JSpinner controlling one
 * side of an Insets object.
 * 
 * @author Hans Bickel
 */
public class InsetsControl extends JSpinner implements ChangeListener {

	public static final int TOP 	= 1;
	public static final int LEFT 	= 2;
	public static final int BOTTOM 	= 3;
	public static final int RIGHT 	= 4;
	
	private static final Vector armedControls = new Vector();
	private InsetsUIResource ref;
	private int position;
	private int oldValue;
	boolean changeState = true;
	
	public InsetsControl(SpinnerModel model, InsetsUIResource ref, int position) {
		super(model);

		this.ref = ref;
		this.position = position;
		oldValue = ((Integer)model.getValue()).intValue();

		addChangeListener(this);
	}
	
	public String getPositionString() {
		if(position == TOP) return "top";
		if(position == LEFT) return "left";
		if(position == BOTTOM) return "bottom";
		else return "right";
	}
	
	public int getIntValue() {
		return ((Integer)getValue()).intValue();
	}

	/**
	 * Sets the argument as the current value but doesn't
	 * update Insets reference.
	 * @param value
	 */
	public void setValue(int value) {
		changeState = false;
		oldValue = value;
		super.setValue(new Integer(value));
		changeState = true;
	}
	
	/**
	 * Sets the argument as the current value and immediately
	 * updates Insets reference.
	 * @param value
	 */
	public void commitValue(int value) {
		changeState = false;
		oldValue = value;
		super.setValue(new Integer(value));
		updateInsets();
		
		changeState = true;
	}
	
	/**
	 * Should be called after 'Apply Settings' button was clicked.
	 *
	 */
	static void confirmChanges() {
		if(armedControls.isEmpty()) return;
		
		Iterator ii = armedControls.iterator();
		while(ii.hasNext()) {
			((InsetsControl)ii.next()).confirmChange();
		}
		
		armedControls.clear();
	}

	private void confirmChange() {
		UndoManager.storeUndoData(this, oldValue);
		oldValue = ((Integer)getValue()).intValue();

		updateInsets();
	}
	
	private void updateInsets() {
		switch(position) {
			case TOP:
				ref.top = oldValue;
				break;
			case LEFT:
				ref.left = oldValue;
				break;
			case BOTTOM:
				ref.bottom = oldValue;
				break;
			case RIGHT:
				ref.right = oldValue;
				break;
		}
	}

// ChangeListener impl
	public void stateChanged(ChangeEvent e) {
		if(!changeState) return;
		
		if(!armedControls.contains(this)) {
			armedControls.add(this);
		}
		
		if(!ControlPanel.instance.applySettingsButton.isEnabled()) {
			ControlPanel.instance.applySettingsButton.setEnabled(true);
		}
	}
}
