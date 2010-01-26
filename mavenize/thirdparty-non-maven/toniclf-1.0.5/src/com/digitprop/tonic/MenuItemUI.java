package com.digitprop.tonic;


import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.View;


/**	UI delegate for JMenuItems.
 * 
 * 	@author	Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class MenuItemUI extends BasicMenuItemUI
{
	/**	Used for painting and preferred size calculations */
	static Rectangle zeroRect= new Rectangle(0, 0, 0, 0);
	
	/**	Used for painting and preferred size calculations */
	static Rectangle iconRect= new Rectangle();
	
	/**	Used for painting and preferred size calculations */
	static Rectangle textRect= new Rectangle();
	
	/**	Used for painting and preferred size calculations */
	static Rectangle acceleratorRect= new Rectangle();
	
	/**	Used for painting and preferred size calculations */
	static Rectangle checkIconRect= new Rectangle();
	
	/**	Used for painting and preferred size calculations */
	static Rectangle arrowIconRect= new Rectangle();
	
	/**	Used for painting and preferred size calculations */
	static Rectangle viewRect= new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
	
	/**	Used for painting and preferred size calculations */
	static Rectangle r= new Rectangle();

	/**	The associated menu item */
	protected JMenuItem menuItem= null;
	
	/**	Color of the selected background */
	protected Color selectionBackground;
	
	/**	Foreground color for selected items */
	protected Color selectionForeground;
	
	/**	Foreground color for disabled items */
	protected Color disabledForeground;
	
	/**	Foreground color for accelerators */
	protected Color acceleratorForeground;
	
	/**	Foreground color for selected accelerators */
	protected Color acceleratorSelectionForeground;
	
	/**	Delimiting string for accelerators */
	private String acceleratorDelimiter;

	/**	Gap between text and icon */
	protected int defaultTextIconGap;
	
	/**	Font for accelerators */
	protected Font acceleratorFont;

	/**	Associated mouse input listener */
	protected MouseInputListener mouseInputListener;
	
	/**	Associated mouse listener */
	protected MenuDragMouseListener menuDragMouseListener;
	
	/**	Associated key listener */
	protected MenuKeyListener menuKeyListener;
	
	/**	Associated property change listener */
	private PropertyChangeListener propertyChangeListener;

	/**	Arrow icon */
	protected Icon arrowIcon= null;
	
	/**	Inverted arrow icon (for selected menu items) */
	protected Icon invArrowIcon= null;
	
	/**	Checked icon (for checkbox menu items) */
	protected Icon checkIcon= null;
	
	/**	Unchecked icon (for checkbox menu items) */
	protected Icon uncheckIcon=null;

	/**	? */
	protected boolean oldBorderPainted;

	/** 	Used for accelerator binding, lazily created. */
	InputMap windowInputMap;

	/**	Diagnostic aids -- should be false for production builds. */
	private static final boolean TRACE= false; // trace creates and disposes

	/**	Diagnostic aids -- should be false for production builds. */
	private static final boolean VERBOSE= false; // show reuse hits/misses
	
	/**	Diagnostic aids -- should be false for production builds. */
	private static final boolean DEBUG= false; // show bad params, misc.

	/**	Client Property keys for text and accelerator text widths */
	static final String MAX_TEXT_WIDTH= "maxTextWidth";
	
	/**	Client Property keys for text and accelerator text widths */
	static final String MAX_ACC_WIDTH= "maxAccWidth";


	/**	Creates and returns the UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new MenuItemUI();
	}


	/**	Installs the UI delegate for the specified component */
	public void installUI(JComponent c)
	{
		menuItem= (JMenuItem) c;

		installDefaults();
		installComponents(menuItem);
		installListeners();
		installKeyboardActions();
	}


	/**	Installs defaults settings for the associated menu item */
	protected void installDefaults()
	{
		String prefix= getPropertyPrefix();

		acceleratorFont= UIManager.getFont("MenuItem.acceleratorFont");

		menuItem.setOpaque(true);
		if (menuItem.getMargin() == null
			|| (menuItem.getMargin() instanceof UIResource))
		{
			menuItem.setMargin(UIManager.getInsets(prefix + ".margin"));
		}

		defaultTextIconGap= 4; // Should be from table

		LookAndFeel.installBorder(menuItem, prefix + ".border");
		oldBorderPainted= menuItem.isBorderPainted();
		menuItem.setBorderPainted(
			((Boolean) (UIManager.get(prefix + ".borderPainted"))).booleanValue());
		LookAndFeel.installColorsAndFont(
			menuItem,
			prefix + ".background",
			prefix + ".foreground",
			prefix + ".font");

		// MenuItem specific defaults
		if (selectionBackground == null
			|| selectionBackground instanceof UIResource)
		{
			selectionBackground=
				UIManager.getColor(prefix + ".selectionBackground");
		}
		if (selectionForeground == null
			|| selectionForeground instanceof UIResource)
		{
			selectionForeground=
				UIManager.getColor(prefix + ".selectionForeground");
		}
		if (disabledForeground == null
			|| disabledForeground instanceof UIResource)
		{
			disabledForeground= UIManager.getColor(prefix + ".disabledForeground");
		}
		if (acceleratorForeground == null
			|| acceleratorForeground instanceof UIResource)
		{
			acceleratorForeground=
				UIManager.getColor(prefix + ".acceleratorForeground");
		}
		if (acceleratorSelectionForeground == null
			|| acceleratorSelectionForeground instanceof UIResource)
		{
			acceleratorSelectionForeground=
				UIManager.getColor(prefix + ".acceleratorSelectionForeground");
		}
		// Get accelerator delimiter
		acceleratorDelimiter=
			UIManager.getString("MenuItem.acceleratorDelimiter");
		if (acceleratorDelimiter == null)
		{
			acceleratorDelimiter= "+";
		}
		// Icons
		if (arrowIcon == null || arrowIcon instanceof UIResource)
		{
			arrowIcon= UIManager.getIcon(prefix + ".arrowIcon");
		}
		if (invArrowIcon == null || invArrowIcon instanceof UIResource)
		{
			invArrowIcon= UIManager.getIcon(prefix + ".invArrowIcon");
		}		
		if (checkIcon == null || checkIcon instanceof UIResource)
		{
			checkIcon= UIManager.getIcon(prefix + ".checkIcon");
		}
		if (uncheckIcon == null || uncheckIcon instanceof UIResource)
		{
			uncheckIcon= UIManager.getIcon(prefix + ".uncheckIcon");
		}
		
	}

	
	/**	Installs components to the specified menu item */
	protected void installComponents(JMenuItem menuItem)
	{
		BasicHTML.updateRenderer(menuItem, menuItem.getText());
	}


	/**	Returns the property prefix for menu items */
	protected String getPropertyPrefix()
	{
		return "MenuItem";
	}


	/**	Installs listeners for the associated menu item */
	protected void installListeners()
	{
		mouseInputListener= createMouseInputListener(menuItem);
		menuDragMouseListener= createMenuDragMouseListener(menuItem);
		menuKeyListener= createMenuKeyListener(menuItem);
		propertyChangeListener= createPropertyChangeListener(menuItem);

		menuItem.addMouseListener(mouseInputListener);
		menuItem.addMouseMotionListener(mouseInputListener);
		menuItem.addMenuDragMouseListener(menuDragMouseListener);
		menuItem.addMenuKeyListener(menuKeyListener);
		menuItem.addPropertyChangeListener(propertyChangeListener);
	}


	/**	Installs keyboard actions for the associated menu item */
	protected void installKeyboardActions()
	{
		ActionMap actionMap= getMyActionMap();

		SwingUtilities.replaceUIActionMap(menuItem, actionMap);
		updateMyAcceleratorBinding();
	}


	/**	Uninstalls the UI delegate for the specified component */
	public void uninstallUI(JComponent c)
	{
		menuItem= (JMenuItem) c;
		uninstallDefaults();
		uninstallComponents(menuItem);
		uninstallListeners();
		uninstallKeyboardActions();

		//Remove the textWidth and accWidth values from the parent's Client Properties.
		Container parent= menuItem.getParent();
		if ((parent != null && parent instanceof JComponent)
			&& !(menuItem instanceof JMenu && ((JMenu) menuItem).isTopLevelMenu()))
		{
			JComponent p= (JComponent) parent;
			p.putClientProperty(MAX_ACC_WIDTH, null);
			p.putClientProperty(MAX_TEXT_WIDTH, null);
		}

		menuItem= null;
	}


	/**	Uninstalls the defaults from the associated menu item */
	protected void uninstallDefaults()
	{
		LookAndFeel.uninstallBorder(menuItem);
		menuItem.setBorderPainted(oldBorderPainted);
		if (menuItem.getMargin() instanceof UIResource)
			menuItem.setMargin(null);
		if (arrowIcon instanceof UIResource)
			arrowIcon= null;
		if (checkIcon instanceof UIResource)
			checkIcon= null;
	}


	/**	Uninstalls additional components added to the menu item upon installation */
	protected void uninstallComponents(JMenuItem menuItem)
	{
		BasicHTML.updateRenderer(menuItem, "");
	}


	/**	Uninstalls any registered listeners from the associated menu item */
	protected void uninstallListeners()
	{
		menuItem.removeMouseListener(mouseInputListener);
		menuItem.removeMouseMotionListener(mouseInputListener);
		menuItem.removeMenuDragMouseListener(menuDragMouseListener);
		menuItem.removeMenuKeyListener(menuKeyListener);
		menuItem.removePropertyChangeListener(propertyChangeListener);

		mouseInputListener= null;
		menuDragMouseListener= null;
		menuKeyListener= null;
		propertyChangeListener= null;
	}


	/**	Uninstalls any keyboard actions from the associated menu item */
	protected void uninstallKeyboardActions()
	{
		SwingUtilities.replaceUIActionMap(menuItem, null);
		if (windowInputMap != null)
		{
			SwingUtilities.replaceUIInputMap(
				menuItem,
				JComponent.WHEN_IN_FOCUSED_WINDOW,
				null);
			windowInputMap= null;
		}
	}


	/**	Creates and returns a mouse input listener for the specified component */
	protected MouseInputListener createMouseInputListener(JComponent c)
	{
		return new MouseInputHandler();
	}


	/**	Creates and returns a mouse listener for the specified component */
	protected MenuDragMouseListener createMenuDragMouseListener(JComponent c)
	{
		return new MenuDragMouseHandler();
	}


	/**	Creates and returns a key listener for the specified component */
	protected MenuKeyListener createMenuKeyListener(JComponent c)
	{
		return new MenuKeyHandler();
	}

	/**	Creates and returns a property change listener for the specified component */
	private PropertyChangeListener createPropertyChangeListener(JComponent c)
	{
		return new PropertyChangeHandler();
	}


	/**	Returns the action map to be associated with the underlying menu item */
	ActionMap getMyActionMap()
	{
		String propertyPrefix= getPropertyPrefix();
		String uiKey= propertyPrefix + ".actionMap";
		ActionMap am= (ActionMap) UIManager.get(uiKey);
		if (am == null)
		{
			am= createMyActionMap();
			UIManager.getLookAndFeelDefaults().put(uiKey, am);
		}
		return am;
	}


	/**	Creates an action map for associated menu items */
	ActionMap createMyActionMap()
	{
		ActionMap map= new ActionMapUIResource();
		map.put("doClick", new ClickAction());

		return map;
	}


	/**	Creates an input map for associated menu items */
	InputMap createMyInputMap(int condition)
	{
		if (condition == JComponent.WHEN_IN_FOCUSED_WINDOW)
		{
			return new ComponentInputMapUIResource(menuItem);
		}
		return null;
	}


	/**	Updates the accelerator bindings for the associated menu item 
	 * 	(in the case that they have changed).
	 */
	void updateMyAcceleratorBinding()
	{
		KeyStroke accelerator= menuItem.getAccelerator();

		if (windowInputMap != null)
		{
			windowInputMap.clear();
		}
		if (accelerator != null)
		{
			if (windowInputMap == null)
			{
				windowInputMap= createMyInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				SwingUtilities.replaceUIInputMap(
					menuItem,
					JComponent.WHEN_IN_FOCUSED_WINDOW,
					windowInputMap);
			}
			windowInputMap.put(accelerator, "doClick");
		}
	}

	
	/**	Returns the minimum size of the specified component */
	public Dimension getMinimumSize(JComponent c)
	{
		Dimension d= null;
		View v= (View) c.getClientProperty(BasicHTML.propertyKey);
		if (v != null)
		{
			d= getPreferredSize(c);
			d.width -= v.getPreferredSpan(View.X_AXIS)
				- v.getMinimumSpan(View.X_AXIS);
		}
		return d;
	}

	
	/**	Returns the preferred size of the specified component */
	public Dimension getPreferredSize(JComponent c)
	{
		return getPreferredMenuItemSize(
			c,
			checkIcon,
			arrowIcon,
			defaultTextIconGap);
	}


	/**	Returns the maximum size of the specified component */
	public Dimension getMaximumSize(JComponent c)
	{
		Dimension d= null;
		View v= (View) c.getClientProperty(BasicHTML.propertyKey);
		if (v != null)
		{
			d= getPreferredSize(c);
			d.width += v.getMaximumSpan(View.X_AXIS)
				- v.getPreferredSpan(View.X_AXIS);
		}
		return d;
	}


	/**	Reset static rectangles for positioning of the contained elements */
	private void resetRects()
	{
		iconRect.setBounds(zeroRect);
		textRect.setBounds(zeroRect);
		acceleratorRect.setBounds(zeroRect);
		checkIconRect.setBounds(zeroRect);
		arrowIconRect.setBounds(zeroRect);
		viewRect.setBounds(0, 0, Short.MAX_VALUE, Short.MAX_VALUE);
		r.setBounds(zeroRect);
	}


	/**	Returns the preferred menu item size
	 * 
	 * 	@param	c							The component for which to return the preferred size
	 * 	@param	checkIcon				The check icon (if any)
	 * 	@param	arrowIcon				The arrow icon (if any)
	 * 	@param	defaultTextIconGap	The default gap in pixels between icon and text
	 */
	protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon, Icon arrowIcon, int defaultTextIconGap)
	{
		JMenuItem b=(JMenuItem)c;
		int w=0;
		int h=0;
		
		Icon icon= (Icon) b.getIcon();
		String text= b.getText();
		KeyStroke accelerator= b.getAccelerator();
		String acceleratorText= "";

		if (accelerator != null)
		{
			int modifiers= accelerator.getModifiers();
			if (modifiers > 0)
			{
				acceleratorText= KeyEvent.getKeyModifiersText(modifiers);
				//acceleratorText += "-";
				acceleratorText += acceleratorDelimiter;
			}
			int keyCode= accelerator.getKeyCode();
			if (keyCode != 0)
			{
				acceleratorText += KeyEvent.getKeyText(keyCode);
			}
			else
			{
				acceleratorText += accelerator.getKeyChar();
			}
		}

		Font font= b.getFont();
		FontMetrics fm= b.getToolkit().getFontMetrics(font);
		FontMetrics fmAccel= b.getToolkit().getFontMetrics(acceleratorFont);

		resetRects();

		int maxTextWidth=calcTextWidth(b);
		int maxAccWidth=calcAcceleratorWidth(b);
		boolean arrowUsed=isArrowUsed(b);
		boolean iconUsed=isIconUsed(b);
		
		layoutMenuItem(
			fm,
			text,
			fmAccel,
			acceleratorText,
			icon,
			checkIcon,
			arrowIcon,
			b.getVerticalAlignment(),
			b.getHorizontalAlignment(),
			b.getVerticalTextPosition(),
			b.getHorizontalTextPosition(),
			viewRect,
			iconRect,
			textRect,
			acceleratorRect,
			checkIconRect,
			arrowIconRect,
			text == null ? 0 : defaultTextIconGap,
			defaultTextIconGap,
			maxTextWidth,
			maxAccWidth,
			arrowUsed,
			iconUsed);
			
		w=viewRect.width;
		h=viewRect.height;
		
		return new Dimension(w,h);
	}


	/**	Updates the visual appearance of the specified component. 
	 * 	Invokes paint().
	 * 
	 * 	@see	#paint(Graphics, JComponent)
	 */
	public void update(Graphics g, JComponent c)
	{
		paint(g, c);
	}


	/**	Returns true if any icon is used in this menu item or other menu items
	 * 	at the same hierarchy level in the menu tree.
	 */
	private boolean isIconUsed(JMenuItem item)
	{
		Object p=item.getParent();
	
		if(p!=null)
		{
			if(p instanceof JMenu)
			{
				JMenu menu=(JMenu)p;
				for(int i=0; i<menu.getMenuComponentCount(); i++)
				{
					JMenuItem currItem=(JMenuItem)menu.getMenuComponent(i);
					if(currItem.getIcon()!=null || (currItem instanceof JCheckBoxMenuItem) ||
							(currItem instanceof JRadioButtonMenuItem))
						return true;
				}
			}
			else if(p instanceof JPopupMenu)
			{
				JPopupMenu menu=(JPopupMenu)p;
				for(int i=0; i<menu.getComponentCount(); i++)
				{
					Object o=menu.getComponent(i);
					if(o instanceof JMenuItem)
					{
						JMenuItem currItem=(JMenuItem)o;
						if(currItem.getIcon()!=null || (currItem instanceof JCheckBoxMenuItem) ||
								(currItem instanceof JRadioButtonMenuItem))
							return true;
					}
				}				
			}
		}
		
		return false;			
	}
	
	
	/**	Returns true, if any arrow is used in this item or any item on the
	 * 	same hierarchy level in the menu tree.
	 */
	private boolean isArrowUsed(JMenuItem item)
	{
		Object p=item.getParent();
		
		if(p!=null)
		{
			if(p instanceof JMenu)
			{
				JMenu menu=(JMenu)p;
				for(int i=0; i<menu.getMenuComponentCount(); i++)
				{
					JMenuItem currItem=(JMenuItem)menu.getMenuComponent(i);
					if(currItem instanceof JMenu)
						return true;
				}
			}
			else if(p instanceof JPopupMenu)
			{
				JPopupMenu menu=(JPopupMenu)p;
				for(int i=0; i<menu.getComponentCount(); i++)
				{
					Object o=menu.getComponent(i);
					if(o instanceof JMenu)
						return true;
				}				
			}
		}
		
		return false;			
	}
	
	
	/**	Calculates and returns the width of the text displayed on this
	 * 	menu item.
	 */
	private int calcTextWidth(JMenuItem item)
	{
		Object p=item.getParent();
			
		FontMetrics fm=item.getFontMetrics(item.getFont());
		int maxTextWidth=fm.stringWidth(item.getText());
		
		if(p!=null)
		{
			if(p instanceof JMenu)
			{
				JMenu menu=(JMenu)p;
				for(int i=0; i<menu.getMenuComponentCount(); i++)
				{
					JMenuItem currItem=(JMenuItem)menu.getMenuComponent(i);
					fm=currItem.getFontMetrics(currItem.getFont());
					maxTextWidth=Math.max(maxTextWidth, fm.stringWidth(currItem.getText()));		
				}
			}
			else if(p instanceof JPopupMenu)
			{
				JPopupMenu menu=(JPopupMenu)p;
				for(int i=0; i<menu.getComponentCount(); i++)
				{
					Object o=menu.getComponent(i);
					if(o instanceof JMenuItem)
					{
						JMenuItem currItem=(JMenuItem)o;
						fm=currItem.getFontMetrics(currItem.getFont());
						maxTextWidth=Math.max(maxTextWidth, fm.stringWidth(currItem.getText()));
					}		
				}				
			}
		}
		
		return maxTextWidth;
	}
	
	
	/**	Returns the width of the accelerator text displayed on the specified
	 * 	menu item.
	 */
	private int calcAcceleratorWidth(JMenuItem item)
	{
		Object p=item.getParent();
			
		FontMetrics fm=item.getFontMetrics(acceleratorFont);
		int maxTextWidth=fm.stringWidth(getAcceleratorText(item.getAccelerator()));
		
		if(p!=null)
		{
			if(p instanceof JMenu)
			{
				JMenu menu=(JMenu)p;
				for(int i=0; i<menu.getMenuComponentCount(); i++)
				{
					JMenuItem currItem=(JMenuItem)menu.getMenuComponent(i);
					maxTextWidth=Math.max(maxTextWidth, fm.stringWidth(getAcceleratorText(item.getAccelerator())));		
				}
			}
			else if(p instanceof JPopupMenu)
			{
				JPopupMenu menu=(JPopupMenu)p;
				for(int i=0; i<menu.getComponentCount(); i++)
				{
					Object o=menu.getComponent(i);
					if(o instanceof JMenuItem)
					{
						JMenuItem currItem=(JMenuItem)o;
						maxTextWidth=Math.max(maxTextWidth, fm.stringWidth(getAcceleratorText(currItem.getAccelerator())));
					}		
				}				
			}
		}
		
		return maxTextWidth;
	}
	
	
	/**	Paints the specified component */
	public void paint(Graphics g, JComponent c)
	{
		JMenuItem item=(JMenuItem)c;
		
		int maxTextWidth=calcTextWidth(item);
		int maxAccWidth=calcAcceleratorWidth(item);
		boolean arrowUsed=isArrowUsed(item);
		boolean iconUsed=isIconUsed(item);
		
		paintMenuItem(
			g,
			c,
			checkIcon,
			arrowIcon,
			selectionBackground,
			selectionForeground,
			defaultTextIconGap, 
			maxTextWidth, 
			maxAccWidth,
			arrowUsed,
			iconUsed);
	}


	/**	Returns the accelerator text for the specified key stroke */
	protected String getAcceleratorText(KeyStroke accelerator)
	{
		String acceleratorText= "";
		if (accelerator != null)
		{
			int modifiers= accelerator.getModifiers();
			if (modifiers > 0)
			{
				acceleratorText= KeyEvent.getKeyModifiersText(modifiers);
				//acceleratorText += "-";
				acceleratorText += acceleratorDelimiter;
			}

			int keyCode= accelerator.getKeyCode();
			if (keyCode != 0)
			{
				acceleratorText += KeyEvent.getKeyText(keyCode);
			}
			else
			{
				acceleratorText += accelerator.getKeyChar();
			}
		}
		
		return acceleratorText;		
	}
	
	
	/**	Paints the specified menu item.
	 * 
	 * 	@param	g							The graphics context into which to paint
	 * 	@param	c							The component which to paint
	 * 	@param	checkIcon				The check mark icon (if any)
	 * 	@param	arrowIcon				The arrow icon (if any)
	 * 	@param	background				The background color to be used
	 * 	@param	foreground				The foreground color to be used
	 * 	@param	defaultTextIconGap	The default width in pixels between the
	 * 											icon and the text
	 *		@param	maxTextWidth			The maximum width in pixels of the text
	 *		@param	maxAccWidth				The maximum width in pixels of the accelerator
	 *		@param	arrowUsed				If true, the arrow will be painted
	 *		@param	iconUsed					If true, the icon will be painted
	 */
	protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon,
		Icon arrowIcon, Color background, Color foreground, int defaultTextIconGap, int maxTextWidth, 
		int maxAccWidth, boolean arrowUsed, boolean iconUsed)
	{
		JMenuItem b= (JMenuItem) c;
		ButtonModel model= b.getModel();

		//   Dimension size = b.getSize();
		int menuWidth= b.getWidth();
		int menuHeight= b.getHeight();
		Insets i= c.getInsets();

		resetRects();

		viewRect.setBounds(0, 0, menuWidth, menuHeight);

		// Add a left inset if menu item is not top-level menu
//		Container parentC= menuItem.getParent();
//		if (parentC != null && parentC instanceof JComponent
//				&& !(menuItem instanceof JMenu && ((JMenu) menuItem).isTopLevelMenu()))
//		{
//			viewRect.x += 16;
//		}
			
		viewRect.x += i.left;
		viewRect.y += i.top;
		viewRect.width -= (i.right + viewRect.x);
		viewRect.height -= (i.bottom + viewRect.y);

		Font holdf= g.getFont();
		Font f= c.getFont();
		g.setFont(f);
		FontMetrics fm= g.getFontMetrics(f);
		FontMetrics fmAccel= g.getFontMetrics(acceleratorFont);

		// get Accelerator text
		KeyStroke accelerator= b.getAccelerator();
		String acceleratorText=getAcceleratorText(accelerator);

		// layout the text and icon
		String text=
			layoutMenuItem(
				fm,
				b.getText(),
				fmAccel,
				acceleratorText,
				b.getIcon(),
				checkIcon,
				arrowIcon,
				b.getVerticalAlignment(),
				b.getHorizontalAlignment(),
				b.getVerticalTextPosition(),
				b.getHorizontalTextPosition(),
				viewRect,
				iconRect,
				textRect,
				acceleratorRect,
				checkIconRect,
				arrowIconRect,
				b.getText() == null ? 0 : defaultTextIconGap,
				defaultTextIconGap,
				maxTextWidth, maxAccWidth, arrowUsed, iconUsed);

		// Paint background
		paintBackground(g, b, background);

		Color holdc= g.getColor();

		// Paint the Check
		if (checkIcon != null)
		{
			if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
			{
				g.setColor(foreground);
			}
			else
			{
				g.setColor(holdc);
			}
			if (useCheckAndArrow())
			{
				if(b.isSelected())
					checkIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
				else
					uncheckIcon.paintIcon(c, g, checkIconRect.x, checkIconRect.y);
			}
			g.setColor(holdc);
		}

		// Paint the Icon
		if (b.getIcon() != null)
		{
			Icon icon;
			if (!model.isEnabled())
			{
				icon= (Icon) b.getDisabledIcon();
			}
			else if (model.isPressed() && model.isArmed())
			{
				icon= (Icon) b.getPressedIcon();
				if (icon == null)
				{
					// Use default icon
					icon= (Icon) b.getIcon();
				}
			}
			else
			{
				icon= (Icon) b.getIcon();
			}

			if (icon != null)
				icon.paintIcon(c, g, iconRect.x, iconRect.y);
		}

		// Draw the Text
		if (text != null)
		{
			View v= (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null)
			{
				v.paint(g, textRect);
			}
			else
			{
				paintText(g, b, textRect, text);
			}
		}

		// Draw the Accelerator Text
		if (acceleratorText != null && !acceleratorText.equals(""))
		{

			//Get the maxAccWidth from the parent to calculate the offset.
			int accOffset= 0;
			Container parent= menuItem.getParent();
			if (parent != null && parent instanceof JComponent)
			{
				JComponent p= (JComponent) parent;
				Integer maxValueInt= (Integer) p.getClientProperty(MAX_ACC_WIDTH);
				int maxValue=
					maxValueInt != null
						? maxValueInt.intValue()
						: acceleratorRect.width;

				//Calculate the offset, with which the accelerator texts will be drawn with.
				accOffset= maxValue - acceleratorRect.width;
			}

			g.setFont(acceleratorFont);
			if (!model.isEnabled())
			{
				// *** paint the acceleratorText disabled
				if (disabledForeground != null)
				{
					g.setColor(disabledForeground);
					BasicGraphicsUtils.drawString(
						g,
						acceleratorText,
						0,
						acceleratorRect.x - accOffset,
						acceleratorRect.y + fmAccel.getAscent());
				}
				else
				{
					g.setColor(b.getBackground().brighter());
					BasicGraphicsUtils.drawString(
						g,
						acceleratorText,
						0,
						acceleratorRect.x - accOffset,
						acceleratorRect.y + fmAccel.getAscent());
					g.setColor(b.getBackground().darker());
					BasicGraphicsUtils.drawString(
						g,
						acceleratorText,
						0,
						acceleratorRect.x - accOffset - 1,
						acceleratorRect.y + fmAccel.getAscent() - 1);
				}
			}
			else
			{
				// *** paint the acceleratorText normally
				if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
				{
					g.setColor(acceleratorSelectionForeground);
				}
				else
				{
					g.setColor(acceleratorForeground);
				}
				BasicGraphicsUtils.drawString(
					g,
					acceleratorText,
					0,
					acceleratorRect.x - accOffset,
					acceleratorRect.y + fmAccel.getAscent());
			}
		}

		// Paint the Arrow
		if (arrowIcon != null)
		{
				g.setColor(Color.RED);
			if (useCheckAndArrow())
			{
				if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
				{
					if(invArrowIcon!=null)
						invArrowIcon.paintIcon(c, g, arrowIconRect.x, arrowIconRect.y);
				}
				else
				{
					if(arrowIcon!=null)
						arrowIcon.paintIcon(c, g, arrowIconRect.x, arrowIconRect.y);
				}
			}
		}
		g.setColor(holdc);
		g.setFont(holdf);
	}


	/**	Draws the background of the menu item 
	 * 
	 * 	@param	g				The paint graphics
	 * 	@param	menuItem		Menu item to be painted
	 * 	@param	bgColor		Selection background color
	 */
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor)
	{
		ButtonModel model= menuItem.getModel();
		Color oldColor= g.getColor();
		int menuWidth= menuItem.getWidth();
		int menuHeight= menuItem.getHeight();

		if (menuItem.isOpaque())
		{
			g.setColor(menuItem.getBackground());
			g.fillRect(0, 0, menuWidth, menuHeight);

			if (model.isArmed()
				|| (menuItem instanceof JMenu && model.isSelected()))
			{
				g.setColor(bgColor);
				g.fillRect(1, 1, menuWidth-2, menuHeight-2);
			}
			g.setColor(oldColor);
		}
	}

	
	/**	Renders the text of the current menu item.
	 * 
	 * 	@param	g			Graphics context
	 * 	@param	menuItem	Menu item to render
	 * 	@param	textRect	Bounding rectangle for rendering the text
	 * 	@param 	text		String to render
	 */
	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text)
	{
		ButtonModel model= menuItem.getModel();
		FontMetrics fm= g.getFontMetrics();
		int mnemIndex= menuItem.getDisplayedMnemonicIndex();

		if (!model.isEnabled())
		{
			// *** paint the text disabled
			if (UIManager.get("MenuItem.disabledForeground") instanceof Color)
			{
				g.setColor(UIManager.getColor("MenuItem.disabledForeground"));
				BasicGraphicsUtils.drawStringUnderlineCharAt(
					g,
					text,
					mnemIndex,
					textRect.x,
					textRect.y + fm.getAscent());
			}
			else
			{
				g.setColor(menuItem.getBackground().brighter());
				BasicGraphicsUtils.drawStringUnderlineCharAt(
					g,
					text,
					mnemIndex,
					textRect.x,
					textRect.y + fm.getAscent());
				g.setColor(menuItem.getBackground().darker());
				BasicGraphicsUtils.drawStringUnderlineCharAt(
					g,
					text,
					mnemIndex,
					textRect.x - 1,
					textRect.y + fm.getAscent() - 1);
			}
		}
		else
		{
			// *** paint the text normally
			if (model.isArmed()
				|| (menuItem instanceof JMenu && model.isSelected()))
			{
				g.setColor(selectionForeground); // Uses protected field.
			}
			BasicGraphicsUtils.drawStringUnderlineCharAt(
				g,
				text,
				mnemIndex,
				textRect.x,
				textRect.y + fm.getAscent());
		}
	}


	/** 
	 * Compute and return the location of the icons origin, the 
	 * location of origin of the text baseline, and a possibly clipped
	 * version of the compound labels string.  Locations are computed
	 * relative to the viewRect rectangle. 
	 */
	private String layoutMenuItem(FontMetrics fm, String text, FontMetrics fmAccel, String acceleratorText,
		Icon icon, Icon checkIcon, Icon arrowIcon, int verticalAlignment, int horizontalAlignment,
		int verticalTextPosition, int horizontalTextPosition, Rectangle viewRect, Rectangle iconRect,
		Rectangle textRect, Rectangle acceleratorRect, Rectangle checkIconRect, Rectangle arrowIconRect,
		int textIconGap, int menuItemGap, int maxTextWidth, int maxAccWidth, boolean arrowUsed,
		boolean iconUsed)
	{
		
		viewRect.x=0;
		viewRect.y=0;
		viewRect.width=10;
		viewRect.height=20;
		
		if(icon!=null)
		{
			iconRect.x=6+(16-icon.getIconWidth())/2;
			iconRect.y=(viewRect.height-icon.getIconHeight())/2;
			iconRect.width=16;
			iconRect.height=icon.getIconHeight();
		}
		
		if(checkIcon!=null)
		{
			checkIconRect.x=6+(16-checkIcon.getIconWidth())/2;
			checkIconRect.y=(viewRect.height-checkIcon.getIconHeight())/2;;
			checkIconRect.width=16;
			checkIconRect.height=checkIcon.getIconHeight();
		}
						
		if(iconUsed || !(menuItem.getParent() instanceof JMenuBar))
			textRect.x=26;
		else
			textRect.x=6;
			
		textRect.y=(viewRect.height-fm.getAscent()-fm.getDescent())/2;
		textRect.width=fm.stringWidth(text);
		textRect.height=fm.getAscent()+fm.getDescent();
		
		if(iconUsed || !(menuItem.getParent() instanceof JMenuBar))
			acceleratorRect.x=20+maxTextWidth+10;
		else
			acceleratorRect.x=maxTextWidth+10;
			
		if(maxAccWidth==0)
			acceleratorRect.x-=10;
			
		acceleratorRect.y=(viewRect.height-fmAccel.getAscent()-fmAccel.getDescent())/2;
		acceleratorRect.width=fmAccel.stringWidth(acceleratorText);
		acceleratorRect.height=fmAccel.getAscent()+fmAccel.getDescent();

		if(arrowIcon!=null && arrowUsed)
		{		
			arrowIconRect.x=acceleratorRect.x+maxAccWidth+14-arrowIcon.getIconWidth();
			arrowIconRect.y=(viewRect.height-arrowIcon.getIconHeight())/2;
			arrowIconRect.width=arrowIcon.getIconWidth();
			arrowIconRect.height=arrowIcon.getIconHeight();
		}
		else
		{
			arrowIconRect.x=acceleratorRect.x+maxAccWidth;
			if(!(menuItem instanceof JMenu))
				arrowIconRect.x+=24;
				
			arrowIconRect.y=0;
			arrowIconRect.width=0;
			arrowIconRect.height=viewRect.height;
		}		
					
		viewRect.width=arrowIconRect.x+arrowIconRect.width+10;
		
		return text;
	}
	
	
	/*
	 * Returns false if the component is a JMenu and it is a top
	 * level menu (on the menubar).
	 */
	private boolean useCheckAndArrow()
	{
		boolean b= true;
		if ((menuItem instanceof JMenu) && (((JMenu) menuItem).isTopLevelMenu()))
		{
			b= false;
		}
		return b;
	}


	/**	Returns the menu tree path to the associated menu item */
	public MenuElement[] getPath()
	{
		MenuSelectionManager m= MenuSelectionManager.defaultManager();
		MenuElement oldPath[]= m.getSelectedPath();
		MenuElement newPath[];
		int i= oldPath.length;
		if (i == 0)
			return new MenuElement[0];
		Component parent= menuItem.getParent();
		if (oldPath[i - 1].getComponent() == parent)
		{
			// The parent popup menu is the last so far
			newPath= new MenuElement[i + 1];
			System.arraycopy(oldPath, 0, newPath, 0, i);
			newPath[i]= menuItem;
		}
		else
		{
			// A sibling menuitem is the current selection
			// 
			//  This probably needs to handle 'exit submenu into 
			// a menu item.  Search backwards along the current
			// selection until you find the parent popup menu,
			// then copy up to that and add yourself...
			int j;
			for (j= oldPath.length - 1; j >= 0; j--)
			{
				if (oldPath[j].getComponent() == parent)
					break;
			}
			newPath= new MenuElement[j + 2];
			System.arraycopy(oldPath, 0, newPath, 0, j + 1);
			newPath[j + 1]= menuItem;
			/*
			System.out.println("Sibling condition -- ");
			System.out.println("Old array : ");
			printMenuElementArray(oldPath, false);
			System.out.println("New array : ");
			printMenuElementArray(newPath, false);
			*/
		}
		return newPath;
	}

	void printMyMenuElementArray(MenuElement path[], boolean dumpStack)
	{
		System.out.println("Path is(");
		int i, j;
		for (i= 0, j= path.length; i < j; i++)
		{
			for (int k= 0; k <= i; k++)
				System.out.print("  ");
			MenuElement me= (MenuElement) path[i];
			if (me instanceof JMenuItem)
				System.out.println(((JMenuItem) me).getText() + ", ");
			else if (me == null)
				System.out.println("NULL , ");
			else
				System.out.println("" + me + ", ");
		}
		System.out.println(")");

		if (dumpStack == true)
			Thread.dumpStack();
	}
	
	
	protected class MouseInputHandler implements MouseInputListener
	{
		public void mouseClicked(MouseEvent e)
		{
		}
		public void mousePressed(MouseEvent e)
		{
		}
		public void mouseReleased(MouseEvent e)
		{
			MenuSelectionManager manager= MenuSelectionManager.defaultManager();
			Point p= e.getPoint();
			if (p.x >= 0
				&& p.x < menuItem.getWidth()
				&& p.y >= 0
				&& p.y < menuItem.getHeight())
			{
				doClick(manager);
			}
			else
			{
				manager.processMouseEvent(e);
			}
		}
		public void mouseEntered(MouseEvent e)
		{
			MenuSelectionManager manager= MenuSelectionManager.defaultManager();
			int modifiers= e.getModifiers();
			// 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2	    
			if ((modifiers
				& (InputEvent.BUTTON1_MASK
					| InputEvent.BUTTON2_MASK
					| InputEvent.BUTTON3_MASK))
				!= 0)
			{
				MenuSelectionManager.defaultManager().processMouseEvent(e);
			}
			else
			{
				manager.setSelectedPath(getPath());
			}
		}
		public void mouseExited(MouseEvent e)
		{
			MenuSelectionManager manager= MenuSelectionManager.defaultManager();

			int modifiers= e.getModifiers();
			// 4188027: drag enter/exit added in JDK 1.1.7A, JDK1.2
			if ((modifiers
				& (InputEvent.BUTTON1_MASK
					| InputEvent.BUTTON2_MASK
					| InputEvent.BUTTON3_MASK))
				!= 0)
			{
				MenuSelectionManager.defaultManager().processMouseEvent(e);
			}
			else
			{

				MenuElement path[]= manager.getSelectedPath();
				if (path.length > 1)
				{
					MenuElement newPath[]= new MenuElement[path.length - 1];
					int i, c;
					for (i= 0, c= path.length - 1; i < c; i++)
						newPath[i]= path[i];
					manager.setSelectedPath(newPath);
				}
			}
		}

		public void mouseDragged(MouseEvent e)
		{
			MenuSelectionManager.defaultManager().processMouseEvent(e);
		}
		public void mouseMoved(MouseEvent e)
		{
		}
	}

	private class MenuDragMouseHandler implements MenuDragMouseListener
	{
		public void menuDragMouseEntered(MenuDragMouseEvent e)
		{
		}
		public void menuDragMouseDragged(MenuDragMouseEvent e)
		{
			MenuSelectionManager manager= e.getMenuSelectionManager();
			MenuElement path[]= e.getPath();
			manager.setSelectedPath(path);
		}
		public void menuDragMouseExited(MenuDragMouseEvent e)
		{
		}
		public void menuDragMouseReleased(MenuDragMouseEvent e)
		{
			MenuSelectionManager manager= e.getMenuSelectionManager();
			MenuElement path[]= e.getPath();
			Point p= e.getPoint();
			if (p.x >= 0
				&& p.x < menuItem.getWidth()
				&& p.y >= 0
				&& p.y < menuItem.getHeight())
			{
				doClick(manager);
			}
			else
			{
				manager.clearSelectedPath();
			}
		}
	}

	private class MenuKeyHandler implements MenuKeyListener
	{
		public void menuKeyTyped(MenuKeyEvent e)
		{
			if (DEBUG)
			{
				System.out.println(
					"in BasicMenuItemUI.menuKeyTyped for " + menuItem.getText());
			}
			int key= menuItem.getMnemonic();
			if (key == 0)
				return;
			if (lower(key) == lower((int) (e.getKeyChar())))
			{
				MenuSelectionManager manager= e.getMenuSelectionManager();
				doClick(manager);
				e.consume();
			}
		}
		public void menuKeyPressed(MenuKeyEvent e)
		{
			if (DEBUG)
			{
				System.out.println(
					"in BasicMenuItemUI.menuKeyPressed for " + menuItem.getText());
			}
		}
		public void menuKeyReleased(MenuKeyEvent e)
		{
		}

		private int lower(int ascii)
		{
			if (ascii >= 'A' && ascii <= 'Z')
				return ascii + 'a' - 'A';
			else
				return ascii;
		}

	}

	private class PropertyChangeHandler implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			String name= e.getPropertyName();

			if (name.equals("labelFor")
				|| name.equals("displayedMnemonic")
				|| name.equals("accelerator"))
			{
				updateMyAcceleratorBinding();
			}
			else if (
				name.equals("text")
					|| "font".equals(name)
					|| "foreground".equals(name))
			{
				// remove the old html view client property if one
				// existed, and install a new one if the text installed
				// into the JLabel is html source.
				JMenuItem lbl= ((JMenuItem) e.getSource());
				String text= lbl.getText();
				BasicHTML.updateRenderer(lbl, text);
			}
		}
	}

	private static class ClickAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			JMenuItem mi= (JMenuItem) e.getSource();
			MenuSelectionManager.defaultManager().clearSelectedPath();
			mi.doClick();
		}
	}

	/**
	 * Call this method when a menu item is to be activated.
	 * This method handles some of the details of menu item activation
	 * such as clearing the selected path and messaging the 
	 * JMenuItem's doClick() method.
	 *
	 * @param msm  A MenuSelectionManager. The visual feedback and 
	 *             internal bookkeeping tasks are delegated to 
	 *             this MenuSelectionManager. If <code>null</code> is
	 *             passed as this argument, the 
	 *             <code>MenuSelectionManager.defaultManager</code> is
	 *             used.
	 * @see MenuSelectionManager
	 * @see JMenuItem#doClick(int)
	 * @since 1.4
	 */
	protected void doClick(MenuSelectionManager msm)
	{
		// Auditory cue
		if (!isInternalFrameSystemMenu())
		{
			ActionMap map= menuItem.getActionMap();
			if (map != null)
			{
				Action audioAction= map.get(getPropertyPrefix() + ".commandSound");
				//		if (audioAction != null) {
				//			 // pass off firing the Action to a utility method
				//			 BasicLookAndFeel lf = (BasicLookAndFeel)
				//									 UIManager.getLookAndFeel();
				//			 lf.playSound(audioAction);
				//		}
			}
		}
		// Visual feedback
		if (msm == null)
		{
			msm= MenuSelectionManager.defaultManager();
		}
		msm.clearSelectedPath();
		menuItem.doClick(0);
	}

	/** 
	 * This is to see if the menu item in question is part of the 
	 * system menu on an internal frame.
	 * The Strings that are being checked can be found in 
	 * MetalInternalFrameTitlePaneUI.java,
	 * WindowsInternalFrameTitlePaneUI.java, and
	 * MotifInternalFrameTitlePaneUI.java.
	 *
	 * @since 1.4
	 */
	private boolean isInternalFrameSystemMenu()
	{
		String actionCommand= menuItem.getActionCommand();
		if ((actionCommand == "Close")
			|| (actionCommand == "Minimize")
			|| (actionCommand == "Restore")
			|| (actionCommand == "Maximize"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**	Convenience function for determining ComponentOrientation.  Helps us
	 * 	avoid having Munge directives throughout the code.
	 */
	static boolean isLeftToRight(Component c)
	{
		return c.getComponentOrientation().isLeftToRight();
	}
}
