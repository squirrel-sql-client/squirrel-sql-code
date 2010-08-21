/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.metal.MetalComboBoxEditor;

import de.muntjak.tinylookandfeel.controlpanel.*;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyComboBoxEditor
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyComboBoxEditor extends MetalComboBoxEditor {

	public TinyComboBoxEditor() {
		super();

		editor = new JTextField("", 9) {

			// workaround for 4530952
			public void setText(String s) {
				if(getText().equals(s)) {
					return;
				}

				super.setText(s);
			}

			// Note: The following code was introduced with Java 1.5 in
			// class javax.swing.plaf.metal.MetalComboBoxEditor.
			// With TinyLaF this isn't a good idea, so we create our
			// own editor (since 1.3.8).

			// The preferred and minimum sizes are overriden and padded by
			// 4 to keep the size as it previously was. Refer to bugs
			// 4775789 and 4517214 for details.
			// public Dimension getPreferredSize() {
			// Dimension pref = super.getPreferredSize();
			// pref.height += 4;
			// return pref;
			// }
			// public Dimension getMinimumSize() {
			// Dimension min = super.getMinimumSize();
			// min.height += 4;
			// return min;
			// }
		};

		editor.setBorder(new EditorBorder());
	}

	class EditorBorder extends AbstractBorder {

		/**
		 * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
		 */
		public Insets getBorderInsets(Component c) {
			// Note: Just adjusted insets until editable
			// and non-editable combo boxes look equal
			return new Insets(1, Theme.comboInsets.left + 1, 1, 0);
		}

		/**
		 * @see javax.swing.border.Border#paintBorder(java.awt.Component,
		 *      java.awt.Graphics, int, int, int, int)
		 */
		public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
			JComponent combo = (JComponent)editor.getParent();
		
			// New in 1.4.0: Added a check for combo being null.
			if(combo != null && combo.getBorder() == null) return;
		
			drawXpBorder(c, g, x, y, w, h);
		}
	}

	private void drawXpBorder(Component c, Graphics g, int x, int y, int w, int h) {
		// paint border background - next parent is combo box,
        // grandparent is the container containing the combo box
		
		// New in 1.4.0 Added NPE checks
		if(c != null && c.getParent() != null && c.getParent().getParent() != null) {
			g.setColor(c.getParent().getParent().getBackground());
		}
		
		g.drawLine(x, y, x + w - 1, y); // top
		g.drawLine(x, y, x, y + h - 1); // left
		g.drawLine(x, y + h - 1, x + w - 1, y + h - 1); // bottom

		if(!c.isEnabled()) {
			DrawRoutines.drawEditableComboBorder(g,
				Theme.comboBorderDisabledColor.getColor(), 0, 0, w, h);
		}
		else {
			DrawRoutines.drawEditableComboBorder(g,
				Theme.comboBorderColor.getColor(), 0, 0, w, h);
		}
	}

	/**
	 * A subclass of BasicComboBoxEditor that implements UIResource.
	 * BasicComboBoxEditor doesn't implement UIResource directly so that
	 * applications can safely override the cellRenderer property with
	 * BasicListCellRenderer subclasses.
	 * <p>
	 * <strong>Warning:</strong> Serialized objects of this class will not be
	 * compatible with future Swing releases. The current serialization support
	 * is appropriate for short term storage or RMI between applications running
	 * the same version of Swing. As of 1.4, support for long term storage of
	 * all JavaBeans<sup><font size="-2">TM</font></sup> has been added to
	 * the <code>java.beans</code> package. Please see
	 * {@link java.beans.XMLEncoder}.
	 */
	public static class UIResource extends TinyComboBoxEditor implements javax.swing.plaf.UIResource {
	}
}
