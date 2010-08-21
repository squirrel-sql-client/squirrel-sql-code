package com.digitprop.tonic;


import javax.swing.*;


/**	Button specifically designed for toolbars. This button is flat and has
 * 	no border until the mouse rolls over. When it does, a dark blue, single pixel
 * 	border appears, and the button is filled with a light blue background.
 * 
 * 	@author Markus Fischer
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
public class ToolButton extends JButton
{
	private static final String uiClassID="ToolButtonUI";
	
	
   /**	Creates a button with no set text or icon. */
   public ToolButton() 
	{
       super();
   }
   
   
   /**	Creates a button with an icon.
    * 
    * 	@param icon  the Icon image to display on the button
    */
   public ToolButton(Icon icon) 
	{
       super(icon);
   }
   
   
   /**	Creates a button with text.
    *
    * 	@param text  the text of the button
    */
   public ToolButton(String text) 
	{
       super(text);
   }
   
   
   /**	Creates a button where properties are taken from the
    * 	<code>Action</code> supplied.
    * 
    * 	@param a the <code>Action</code> used to specify the new button
    */
   public ToolButton(Action a) 
	{
   	super(a);
   }


   /**	Creates a button with initial text and an icon.
    *
    * 	@param text  the text of the button
    * 	@param icon  the Icon image to display on the button
    */
   public ToolButton(String text, Icon icon) 
	{
   	super(text, icon);
   }
   
   
   /**
    * Returns a string that specifies the name of the L&F class
    * that renders this component.
    *
    * @return the string "ButtonUI"
    * @see JComponent#getUIClassID
    * @see UIDefaults#getUI
    * @beaninfo
    *        expert: true
    *   description: A string that specifies the name of the L&F class.
    */
   public String getUIClassID() 
	{
       return uiClassID;
   }   
}
