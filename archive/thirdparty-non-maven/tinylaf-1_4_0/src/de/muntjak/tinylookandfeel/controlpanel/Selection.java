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

import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.HSBControl;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.SpreadControl;

/**
 * Selection manages a selection of controls.
 * @author Hans Bickel
 *
 */
public class Selection implements ParameterSetGenerator {

	private Vector selection = new Vector();
	private Vector storedSelection;
	private static ControlPanel controlPanel;
	private static Selection onlyInstance;
	private Selection() {}
	
	public static Selection getSelection(ControlPanel cp) {
		controlPanel = cp;
		
		if(onlyInstance == null) {
			onlyInstance = new Selection();
		}
		
		return onlyInstance;
	}
	
	/**
	 * Adds the specified control to the selection and
	 * selects the control.
	 * @param control
	 */
	public void add(Selectable control) {
		selection.add(control);
		
		control.setSelected(true);
		
		if(!controlPanel.copyItem.isEnabled()) {
			controlPanel.copyItem.setEnabled(true);
		}
	}
	
	/**
	 * Removes the specified control from the selection
	 * and deselects the control.
	 * @param control
	 * @return
	 */
	public boolean remove(Selectable control) {
		control.setSelected(false);
		
		boolean retVal = selection.remove(control);
		
		if(selection.isEmpty()) {
			if(controlPanel.copyItem.isEnabled()) {
				controlPanel.copyItem.setEnabled(false);
			}
		}
		
		// pasteItem can still be enabled
		
		return retVal;
	}
	
	public void clearSelection() {
		if(selection.isEmpty()) return;
		
		Iterator ii = selection.iterator();
		while(ii.hasNext()) {
			((Selectable)ii.next()).setSelected(false);
		}
		
		selection.clear();
		
		if(controlPanel.copyItem.isEnabled()) {
			controlPanel.copyItem.setEnabled(false);
		}
		
		// pasteItem can still be enabled
	}
	
	/**
	 * Creates a parameter set from all currently selected
	 * controls and copies the vector of controls.
	 * @return
	 */
	public ParameterSet createParameterSet() {
		// We must store the selection because if it
		// is cleared, copied parameters can still be pasted
		storedSelection = (Vector)selection.clone();

		// Note: The empty string will resolve to
		// "Undo/Redo Paste Parameters"
		ParameterSet ps = new ParameterSet(this, "");
		
		Iterator ii = storedSelection.iterator();
		while(ii.hasNext()) {
			Object control = ii.next();
			
			if(control instanceof HSBControl) {
				ps.addParameter((HSBControl)control);
			}
			else if(control instanceof SBControl) {
				ps.addParameter((SBControl)control);
			}
			else if(control instanceof SpreadControl) {
				ps.addParameter((SpreadControl)control);
			}
		}
		
//		System.out.println("Selection.createParameterSet: " + ps);
		return ps;
	}

	public ParameterSet getParameterSet() {
		ParameterSet ps = new ParameterSet(this, "Selection");
		
		Iterator ii = storedSelection.iterator();	// ! use storedSelection
		while(ii.hasNext()) {
			Object control = ii.next();
			
			if(control instanceof HSBControl) {
				ps.addParameter((HSBControl)control);
			}
			else if(control instanceof SBControl) {
				ps.addParameter((SBControl)control);
			}
			else if(control instanceof SpreadControl) {
				ps.addParameter((SpreadControl)control);
			}
		}
		
		return ps;
	}

	public void init(boolean ignored) {
//		System.out.println("Updating " + storedSelection.size() + " controls");
		
		Iterator ii = storedSelection.iterator();
		while(ii.hasNext()) {
			Object control = ii.next();
			
			if(control instanceof HSBControl) {
				((HSBControl)control).update();
			}
			else if(control instanceof SBControl) {
				((SBControl)control).update();
			}
			else if(control instanceof SpreadControl) {
				((SpreadControl)control).init();
			}
		}
	}
}
