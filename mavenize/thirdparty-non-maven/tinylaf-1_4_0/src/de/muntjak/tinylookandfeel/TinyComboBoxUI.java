/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxIcon;
import javax.swing.plaf.metal.MetalComboBoxUI;

/**
 * TinyComboBoxUI
 * 
 * @version 1.3
 * @author Hans Bickel
 */
public class TinyComboBoxUI extends BasicComboBoxUI {
	
	static final int COMBO_BUTTON_WIDTH = 18;
	
	// Flag for calculating the display size
    protected boolean isDisplaySizeDirty = true;
    
    // Cached the size that the display needs to render the largest item
    protected Dimension cachedDisplaySize = new Dimension(0, 0);
    
    private static final Insets DEFAULT_INSETS = new Insets(0, 0, 0, 0);
	

	public static ComponentUI createUI(JComponent c) {
		return new TinyComboBoxUI(); 
	}

	public void paint(Graphics g, JComponent c) {}

	protected ComboBoxEditor createEditor() {
		return new TinyComboBoxEditor.UIResource();
	}

	protected JButton createArrowButton() {
		JButton button = new TinyComboBoxButton(comboBox,
			null,
			comboBox.isEditable(),
			currentValuePane,
			listBox);

		button.setMargin(DEFAULT_INSETS);
		button.putClientProperty("isComboBoxButton", Boolean.TRUE);
		
		return button;
	}
	
	protected void installComponents() {
		super.installComponents();
		
		if(arrowButton != null) {
			arrowButton.setFocusable(false);
		}
	}

	public PropertyChangeListener createPropertyChangeListener() {
		return new TinyPropertyChangeListener();
	}

	/**
	 * This inner class is marked &quot;public&quot; due to a compiler bug.
	 * This class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of <FooUI>.
	 */
	public class TinyPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {
		public void propertyChange(PropertyChangeEvent e) {
			super.propertyChange(e);
			
			String propertyName = e.getPropertyName();

			if(propertyName.equals("editable")) {	
				TinyComboBoxButton button = (TinyComboBoxButton)arrowButton;
				button.setIconOnly(comboBox.isEditable());
				isMinimumSizeDirty = true;
                isDisplaySizeDirty = true;
                comboBox.revalidate();
			}
//			else if(propertyName.equals("font")) {	
//				isMinimumSizeDirty = true;
//                isDisplaySizeDirty = true;
//                comboBox.revalidate();
//			}
			else if(propertyName.equals("background")) {
				Color color = (Color) e.getNewValue();
				listBox.setBackground(color);
			}
			else if(propertyName.equals("foreground")) {
				Color color = (Color) e.getNewValue();
				listBox.setForeground(color);
			}
		}
	}

	/**
	 * As of Java 2 platform v1.4 this method is no longer used. Do not call or
	 * override. All the functionality of this method is in the
	 * MetalPropertyChangeListener.
	 *
	 * @deprecated As of Java 2 platform v1.4.
	 */
	protected void editablePropertyChanged(PropertyChangeEvent e) {
	}

	protected LayoutManager createLayoutManager() {
		return new TinyComboBoxLayoutManager();
	}

	/**
	 * This inner class is marked &quot;public&quot; due to a compiler bug.
	 * This class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of <FooUI>.
	 */
	public class TinyComboBoxLayoutManager implements LayoutManager {
		public void addLayoutComponent(String name, Component comp) {}

		public void removeLayoutComponent(Component comp) {}

		public Dimension preferredLayoutSize(Container parent) {
			JComboBox cb = (JComboBox) parent;
			return parent.getPreferredSize();
		}

		public Dimension minimumLayoutSize(Container parent) {
			JComboBox cb = (JComboBox) parent;
			return parent.getMinimumSize();
		}

		public void layoutContainer(Container parent) {
			JComboBox cb = (JComboBox) parent;
			int width = cb.getWidth();
			int height = cb.getHeight();

			Rectangle cvb;

			if(comboBox.isEditable()) {
				if(arrowButton != null) {
					arrowButton.setBounds(width -
						COMBO_BUTTON_WIDTH, 0,
						COMBO_BUTTON_WIDTH, height);
				}
				
				if(editor != null) {
					cvb = rectangleForCurrentValue2();
					editor.setBounds(cvb);
				}
			}
			else {
				arrowButton.setBounds(0, 0, width, height);				
			}
		}
	}

	protected Rectangle rectangleForCurrentValue2() {
		int width = comboBox.getWidth();
		int height = comboBox.getHeight();
		Insets insets = getInsets();
		int buttonSize = height - (insets.top + insets.bottom);
		
		if(arrowButton != null) {
			buttonSize = COMBO_BUTTON_WIDTH;
		}
		if(comboBox.getComponentOrientation().isLeftToRight()) {
			return new Rectangle(insets.left,
				insets.top,
				width - (insets.left + insets.right + buttonSize),
				height - (insets.top + insets.bottom));
		} else {
			return new Rectangle(
				insets.left + buttonSize,
				insets.top,
				width - (insets.left + insets.right + buttonSize),
				height - (insets.top + insets.bottom));
		}
	}

	/**
	 * As of Java 2 platform v1.4 this method is no
	 * longer used.
	 *
	 * @deprecated As of Java 2 platform v1.4.
	 */
	protected void removeListeners() {
		if(propertyChangeListener != null) {
			comboBox.removePropertyChangeListener(propertyChangeListener);
		}
	}

	/**
	 * @param c the combo box
	 */
	public Dimension getMinimumSize(JComponent c) {
		if(!isMinimumSizeDirty) {
			isDisplaySizeDirty = true;	// 1.3
			return new Dimension(cachedMinimumSize);
		}

		// changed in 1.3
		Insets insets = Theme.comboInsets;
		Dimension size = getDisplaySize();
		size.width += COMBO_BUTTON_WIDTH;
		size.width += insets.left + insets.right;
		size.height += insets.top + insets.bottom;

		cachedMinimumSize.setSize(size.width, size.height);
		isMinimumSizeDirty = false;

		return new Dimension(cachedMinimumSize);
	}

	/** 
	 * Copied from BasicComboBoxUI, because isDisplaySizeDirty was declared private!?
     * Returns the calculated size of the display area. The display area is the
     * portion of the combo box in which the selected item is displayed. This 
     * method will use the prototype display value if it has been set. 
     * <p>
     * For combo boxes with a non trivial number of items, it is recommended to
     * use a prototype display value to significantly speed up the display 
     * size calculation.
     * 
     * @return the size of the display area calculated from the combo box items
     * @see javax.swing.JComboBox#setPrototypeDisplayValue
     */
    protected Dimension getDisplaySize() {
        if(!isDisplaySizeDirty) {
            return new Dimension(cachedDisplaySize);
        }
        
		Dimension result = new Dimension();      
        ListCellRenderer renderer = comboBox.getRenderer();
        
        if(renderer == null)  {
            renderer = new DefaultListCellRenderer();
        }

        Object prototypeValue = comboBox.getPrototypeDisplayValue();
        if(prototypeValue != null)  {
            // Calculates the dimension based on the prototype value
            result = getSizeForComponent(renderer.getListCellRendererComponent(
            	listBox, prototypeValue, -1, false, false));
        }
        else {
            // Calculate the dimension by iterating over all the elements in the combo
            // box list.
            ComboBoxModel model = comboBox.getModel();
            int modelSize = model.getSize();
	    	Dimension d;

            Component cpn;
            
            if(modelSize > 0) {
                for (int i = 0; i < modelSize ; i++ ) {
                    // Calculates the maximum height and width based on the largest
                    // element
                    d = getSizeForComponent(renderer.getListCellRendererComponent(
                    	listBox, model.getElementAt(i), -1, false, false));
                    result.width = Math.max(result.width,d.width);
                    result.height = Math.max(result.height,d.height);
                }
            }
            else {
				result = getDefaultSize();
				
				if(comboBox.isEditable()) {
				    result.width = 100;
				}
            }
        }

		if(comboBox.isEditable()) {
		    Dimension d = editor.getPreferredSize();
		    result.width = Math.max(result.width, d.width);
		    result.height = Math.max(result.height, d.height);
		}
        
        // Set the cached value
        cachedDisplaySize.setSize(result.width, result.height);
        isDisplaySizeDirty = false;

        return result;
    }
    
    /*
     * Copied from BasicComboBoxUI.
     */
    private Dimension getSizeForComponent(Component comp) {
		currentValuePane.add(comp);
		comp.setFont(comboBox.getFont());
		Dimension d = comp.getPreferredSize();
		currentValuePane.remove(comp);
		
		return d;
    }
}
