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
import java.util.Stack;
import java.util.Vector;

import javax.swing.plaf.ColorUIResource;

import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.BooleanControl;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.ColorizeIconCheck;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.FontColorControl;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.FontPanel;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.HSBControl;
import de.muntjak.tinylookandfeel.controlpanel.IntControl;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.SpreadControl;
import de.muntjak.tinylookandfeel.util.SBReference;
import de.muntjak.tinylookandfeel.util.ColoredFont;
import de.muntjak.tinylookandfeel.util.HSBReference;

/**
 * UndoManager
 * @author Hans Bickel
 *
 */
public class UndoManager {
	
	// stack of UndoData
	private static final Stack undoStack = new Stack();
	private static final Stack redoStack = new Stack();
	private static String undoDescription, redoDescription;
	
	// undo items which must be activated by pressing
	// 'Apply Settings' are added to delayedUndoItems
	// until they are activated at activateDelayedUndoItems(ControlPanel)
	private static final Vector delayedUndoItems = new Vector();
	
	static void clear() {
		//new Exception("UndoManager.clear()").printStackTrace();
		undoStack.clear();
		redoStack.clear();
		delayedUndoItems.clear();
	}
	
	/**
	 * Called if user clicked 'Apply Settings'. Returns
	 * true if there was at least one delayed undo item,
	 * false otherwise.
	 * @param cp
	 * @return true if there was at least one delayed undo item,
	 * false otherwise
	 */
	static boolean activateDelayedUndoItems(ControlPanel cp) {
		if(delayedUndoItems.isEmpty()) return false;
		
		if(delayedUndoItems.size() == 1) {
			undoStack.push(delayedUndoItems.get(0));
		}
		else {
			// push vector of UndoData
			undoStack.push(delayedUndoItems.clone());
//			printVector(delayedUndoItems);
		}

		undoDescription = getDescription(undoStack.peek());
		
		delayedUndoItems.clear();
		redoStack.clear();
		cp.undoItemsActivated();
		
		return true;
	}
	
	private static void printVector(Vector v) {
		System.out.println("\n" + v.size() + " items:");
		Iterator ii = v.iterator();
		while(ii.hasNext()) {
			System.out.println("  " + ii.next());
		}
	}

	private static String getDescription(Object o) {
		if(o instanceof UndoData) {
			return ((UndoData)o).description;
		}
		else if(o instanceof Vector) {
			Vector v = (Vector)o;
			String description = null;

			Iterator ii = v.iterator();
			while(ii.hasNext()) {
				UndoData data = (UndoData)ii.next();

				if(description == null) {
					description = data.description;
				}
				else if(!description.equals(data.description)) {
					if(description.startsWith("Change Inset") &&
						data.description.startsWith("Change Inset"))
					{
						description = "Change Inset";
					}
					else {
						description = "Multiple Actions";
						break;
					}
				}
			}
			
			if("Change Inset".equals(description)) {
				description = "Change multiple Insets";
			}
			else if("Change Boolean".equals(description)) {
				description = "Change multiple Booleans";
			}
			else if("Change Spread".equals(description)) {
				description = "Change multiple Spreads";
			}
			else if("Change Color".equals(description)) {
				description = "Change multiple Colors";
			}
			else if("Change Font".equals(description)) {
				description = "Change multiple Fonts";
			}
			else if(!"Multiple Actions".equals(description)) {
				description = "Multiple " + description + "s";
			}
			
			return description;
		}
		
		return null;	// Error
	}
	
	static String getUndoDescription() {
		return undoDescription;
	}
	
	static String getRedoDescription() {
		return redoDescription;
	}

	static void storeUndoData(ColoredFont cf) {
		UndoData data = new UndoData(cf);
		
		if(delayedUndoItems.contains(data)) {
			int index = delayedUndoItems.indexOf(data);
			UndoData old = (UndoData)delayedUndoItems.get(index);
			delayedUndoItems.remove(data);
			
			data.cfData = old.cfData;
			delayedUndoItems.add(data);
		}
		else {
			delayedUndoItems.add(data);
		}
	}
	
	static void storeUndoData(SBControl sb) {
		UndoData data = new UndoData(sb);
		
		if(sb.forceUpdate) {
			if(delayedUndoItems.contains(data)) {
				int index = delayedUndoItems.indexOf(data);
				UndoData old = (UndoData)delayedUndoItems.get(index);
				delayedUndoItems.remove(data);
				
				data.sbData = old.sbData;
				delayedUndoItems.add(data);
			}
			else {
				delayedUndoItems.add(data);
			}
		}
		else {
			undoStack.push(data);
			redoStack.clear();
			undoDescription = data.description;
		}
	}
	
	static void storeUndoData(SpreadControl sc) {
		UndoData data = new UndoData(sc);
		undoDescription = data.description;
		
		undoStack.push(data);
		redoStack.clear();
	}
	
	static void storeUndoData(BooleanControl bc) {
		UndoData data = new UndoData(bc);
		
		if(bc.forceUpdate) {
			if(delayedUndoItems.contains(data)) {
				delayedUndoItems.remove(data);
			}
			else {
				delayedUndoItems.add(data);
			}
		}
		else {
			undoStack.push(data);
			redoStack.clear();
			undoDescription = data.description;
		}
	}
	
	static void storeUndoData(HSBControl hsb, ColorizeIconCheck cc) {
		UndoData data = new UndoData(hsb, cc);
		
		if(delayedUndoItems.contains(data)) {
			int index = delayedUndoItems.indexOf(data);
			UndoData old = (UndoData)delayedUndoItems.get(index);
			delayedUndoItems.remove(data);
			
			data.hsbData = old.hsbData;
			data.cc = old.cc;
			delayedUndoItems.add(data);
		}
		else {
			delayedUndoItems.add(data);
		}
	}
	
	static void storeUndoData(ColorizeIconCheck cc) {
		UndoData data = new UndoData(cc);
		
		if(delayedUndoItems.contains(data)) {
			delayedUndoItems.remove(data);
		}
		else {
			delayedUndoItems.add(data);
		}
	}
	
	static void storeUndoData(InsetsControl ic, int icValue) {
		UndoData data = new UndoData(ic, icValue);
		
		delayedUndoItems.add(data);
	}
	
	static void storeUndoData(IntControl ic, int icValue) {
		UndoData data = new UndoData(ic, icValue);
		
		delayedUndoItems.add(data);
	}
	
	public static void storeUndoData(ParameterSet params) {
		UndoData data = new UndoData(params);
		undoDescription = data.description;
		
		undoStack.push(data);
		redoStack.clear();
	}

	/**
	 * 
	 * @return true if the undo operation succeeded, false otherwise
	 */
	static boolean doUndo(ControlPanel cp) {
		if(undoStack.isEmpty()) return false;
		
		Object o = undoStack.pop();
		redoStack.push(o);

		if(o instanceof UndoData) {
			((UndoData)o).doUndo(cp, true);
		}
		else {
			Vector v = (Vector)o;
			int top = v.size() - 1;

			for(int i = top; i >= 0; i--) {
				((UndoData)v.get(i)).doUndo(cp, false);
			}
			
			cp.setTheme();
		}
		
		if(!undoStack.isEmpty()) {
			undoDescription = getDescription(undoStack.peek());
		}
		
		redoDescription = getDescription(redoStack.peek());
		
		return true;
	}
	
	/**
	 * 
	 * @return true if the redo operation succeeded, false otherwise
	 */
	static boolean doRedo(ControlPanel cp) {
		if(redoStack.isEmpty()) return false;

		Object o = redoStack.pop();
		
		undoStack.push(o);
		
		if(o instanceof UndoData) {
			((UndoData)o).doUndo(cp, true);
		}
		else {
			Vector v = (Vector)o;
			int top = v.size() - 1;

			for(int i = top; i >= 0; i--) {
				((UndoData)v.get(i)).doUndo(cp, false);
			}
			
			cp.setTheme();
		}

		if(!redoStack.isEmpty()) {
			redoDescription = getDescription(redoStack.peek());
		}
		
		undoDescription = getDescription(undoStack.peek());
		
		return true;
	}
	
	static boolean canUndo() {
		return !undoStack.isEmpty();
	}
	
	static boolean canRedo() {
		return !redoStack.isEmpty();
	}

	/**
	 * Overrides equals(Object). Two UndoData are supposed
	 * to be equal if their controls are equal.
	 */
	private static class UndoData {
		
		String description;
		SBControl sb;
		SBReference sbData;
		
		ColoredFont cf;
		ColoredFont cfData;
		
		BooleanControl bc;
		boolean bcValue;
		
		HSBControl hsb;
		HSBReference hsbData;
		
		SpreadControl sc;
		int spread;
		
		ColorizeIconCheck cc;
		boolean ccValue;
		
		IntControl intControl;
		int intControlValue;
		
		InsetsControl insetsControl;
		int insetsControlValue;
		
		ParameterSet params;
		ParameterSet paramData;

		UndoData(SBControl sb) {
			this.sb = sb;
			// copy current values
			sbData = new SBReference(sb.getSBReference());
			description = "Change Color";
//			System.out.println("Stored: " + sb + " / " + sbData);
		}
		
		UndoData(ColoredFont cf) {
			this.cf = cf;
			// copy current values
			cfData = new ColoredFont(cf);
			description = "Change Font";
		}
		
		UndoData(BooleanControl bc) {
			this.bc = bc;
			// Note: Because with BooleanControl undo data is
			// stored *after* value changed, the next undo value
			// is equal to current value
			bcValue = bc.ref.getValue();
			description = "Change Boolean";
		}
		
		UndoData(SpreadControl sc) {
			this.sc = sc;
			// copy current value
			spread = sc.spread;
			description = "Change Spread";
		}

		UndoData(HSBControl hsb, ColorizeIconCheck cc) {
			this.hsb = hsb;
			hsbData = new HSBReference(hsb.getUndoReference());
			description = "Colorize Icon Action";
			
			if(!cc.isSelected()) {
				this.cc = cc;
				// copy current value (before change)
				ccValue = cc.isSelected();
			}
		}
		
		UndoData(ColorizeIconCheck cc) {
			this.cc = cc;
			// Note: Because with ColorizeIconCheck undo data is
			// stored *after* value changed, the next undo value
			// is the reversed current value
			ccValue = !cc.isSelected();
			description = "Colorize Icon Action";
		}
		
		UndoData(InsetsControl insetsControl, int value) {
			this.insetsControl = insetsControl;
			this.insetsControlValue = value;
			description = "Change Insets." + insetsControl.getPositionString();
		}

		UndoData(IntControl intControl, int value) {
			this.intControl = intControl;
			this.intControlValue = value;
			description = "Change " + intControl.getDescription();
		}
		
		UndoData(ParameterSet params) {
			this.params = params;
			paramData = params.getGenerator().getParameterSet();
//			System.out.println("UndoManager.paramData: " + paramData);
			description = params.getUndoString();
		}
		
		public boolean equals(Object o) {
			if(o == null || !(o instanceof UndoData)) return false;
			
			UndoData other = (UndoData)o;
			
			if(cf != null) {
				return cf.equals(other.cf);
			}
			else if(sb != null) {
				return sb.equals(other.sb);
			}
			else if(sc != null) {
				return sc.equals(other.sc);
			}
			else if(bc != null) {
				return bc.equals(other.bc);
			}
			else if(hsb != null) {
				return hsb.equals(other.hsb);
			}
			else if(cc != null) {
				return cc.equals(other.cc);
			}
			else if(insetsControl != null) {
				return insetsControl.equals(other.insetsControl);
			}
			else if(intControl != null) {
				return intControl.equals(other.intControl);
			}
			else if(params != null) {
				return insetsControl.equals(other.params);
			}
			
			return false;
		}
		
		public String toString() {
			if(sb != null) {
				return "Current Cr:" + sb.getSBReference() +
					" Undo Cr:" + sbData;
			}
			
			return super.toString();
		}

		void doUndo(ControlPanel cp, boolean setTheme) {
//			System.out.println("doUndo: " + this);
			if(sb != null) {
				int bri = sbData.getBrightness();
				ColorUIResource c = sbData.getColor();
				int ref = sbData.getReference();
				int sat = sbData.getSaturation();
				
				// for redo
				SBReference colorRef = sb.getSBReference();
				sbData.setBrightness(colorRef.getBrightness());
				sbData.setReference(colorRef.getReference());
				sbData.setSaturation(colorRef.getSaturation());
				sbData.setColor(colorRef.getColor());
				// end for redo
				
				colorRef.setBrightness(bri);
				colorRef.setReference(ref);
				colorRef.setSaturation(sat);
				colorRef.setColor(c);
				sb.update();
				
				if(setTheme) {
					sb.updateTargets(false);
				}
			}
			else if(sc != null) {
				int mem = sc.spread;
				
				if(setTheme) {
					sc.update(spread, false);	// also updates targets
				}
				
				// for redo
				spread = mem;
			}
			else if(bc != null) {
				bc.ref.setValue(bcValue);
				bc.setSelected(bcValue);

				if(setTheme) {
					bc.updateTargets(false);
				}

				// for redo
				bcValue = !bcValue;
			}
			else if(hsb != null) {
				int bri = hsbData.getBrightness();
				int hue = hsbData.getHue();
				boolean preserveGrey = hsbData.isPreserveGrey();
				int ref = hsbData.getReference();
				int sat = hsbData.getSaturation();
				
				// for redo
				HSBReference r = hsb.getHSBReference();
				hsbData.setBrightness(r.getBrightness());
				hsbData.setHue(r.getHue());
				hsbData.setPreserveGrey(r.isPreserveGrey());
				hsbData.setReference(r.getReference());
				hsbData.setSaturation(r.getSaturation());
				// end for redo
				
				r.setBrightness(bri);
				r.setHue(hue);
				r.setPreserveGrey(preserveGrey);
				r.setReference(ref);
				r.setSaturation(sat);

				if(cc != null) {
					cc.setSelected(ccValue);
					cc.ref.setValue(ccValue);
					
					if(setTheme) {
						hsb.update();
						cp.colorizeIcon(hsb, ccValue);
					}
					
					// for redo
					ccValue = !ccValue;
				}
				else {
					if(setTheme) {
						hsb.update();
						cp.colorizeIcon(hsb, true);
					}
				}
				
				if(setTheme) cp.setTheme();
			}
			else if(cc != null) {
				cc.setSelected(ccValue);
				cc.ref.setValue(ccValue);
				
				if(setTheme) {
					cp.colorizeIcon(cc.hsb, ccValue);
					cp.setTheme();
				}
				
				// for redo
				ccValue = !ccValue;
			}
			else if(insetsControl != null) {
				int mem = ((Integer)insetsControl.getValue()).intValue();
				insetsControl.commitValue(insetsControlValue);
				
				if(setTheme) cp.setTheme();
				
				// for redo
				insetsControlValue = mem;
			}
			else if(intControl != null) {
				int mem = ((Integer)intControl.getValue()).intValue();
				intControl.commitValue(intControlValue);
				
				if(setTheme) cp.setTheme();
				
				// for redo
				intControlValue = mem;
			}
			else if(cf != null) {
				ColoredFont temp = new ColoredFont(cfData);
				// for redo
				cfData = new ColoredFont(cf);

				cf.update(temp);
				cp.initFonts();

				if(setTheme) cp.setTheme();
			}
			else if(params != null) {
				params.updateValues(paramData);
				
				// store current parameters for redo
				paramData.updateValues();
				
				params.pasteParameters(false);	// triggers setTheme()
				params.updateReferenceColors();
//				System.out.println("UndoManager.doUndo: " + params);
			}
		}
	}
}
