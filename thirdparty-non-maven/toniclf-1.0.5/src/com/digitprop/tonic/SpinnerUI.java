package com.digitprop.tonic;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;


/**	UI delegate for JSpinners.
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
public class SpinnerUI extends BasicSpinnerUI
{
	/**	Creates and returns a UI delegate for the specified component */
	public static ComponentUI createUI(JComponent c)
	{
		return new SpinnerUI();
	}


	/**	Replaces the old editor of the associated JSpinner with the 
	 * 	specified new editor
	 */
	protected void replaceEditor(JComponent oldEditor, JComponent newEditor)
	{
		spinner.remove(oldEditor);
		spinner.add(newEditor, "Editor");
	}

	
	/* Create a component that will replace the spinner models value
	 * with the object returned by <code>spinner.getPreviousValue</code>.
	 * By default the <code>previousButton</code> is a JButton
	 * who's <code>ActionListener</code> updates it's <code>JSpinner</code>
	 * ancestors model.  If a previousButton isn't needed (in a subclass)
	 * then override this method to return null.
	 *
	 * @return a component that will replace the spinners model with the
	 *     next value in the sequence, or null
	 * @see #installUI
	 * @see #createNextButton
	 */
	protected Component createPreviousButton()
	{
		Component tmpButton= super.createPreviousButton();

		if (tmpButton instanceof JButton)
		{
			JButton result= new ArrowButton(SwingConstants.SOUTH);
			ActionListener al[]= ((JButton) tmpButton).getActionListeners();
			for (int i= 0; i < al.length; i++)
				result.addActionListener(al[i]);

			MouseListener ml[]= ((JButton) tmpButton).getMouseListeners();
			for (int i= 0; i < ml.length; i++)
				result.addMouseListener(ml[i]);

			return result;
		}
		else
			return tmpButton;
	}

	
	/**
	 * Create a component that will replace the spinner models value
	 * with the object returned by <code>spinner.getNextValue</code>.
	 * By default the <code>nextButton</code> is a JButton
	 * who's <code>ActionListener</code> updates it's <code>JSpinner</code>
	 * ancestors model.  If a nextButton isn't needed (in a subclass)
	 * then override this method to return null.
	 *
	 * @return a component that will replace the spinners model with the
	 *     next value in the sequence, or null
	 * @see #installUI
	 * @see #createPreviousButton
	 */
	protected Component createNextButton()
	{
		Component tmpButton= super.createNextButton();

		if (tmpButton instanceof JButton)
		{
			JButton result= new ArrowButton(SwingConstants.NORTH);
			((ArrowButton)result).setDrawBottomBorder(false);
			
			result.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, UIManager.getColor("Button.borderColor")));
			ActionListener al[]= ((JButton) tmpButton).getActionListeners();
			for (int i= 0; i < al.length; i++)
				result.addActionListener(al[i]);

			MouseListener ml[]= ((JButton) tmpButton).getMouseListeners();
			for (int i= 0; i < ml.length; i++)
				result.addMouseListener(ml[i]);

			return result;
		}
		else
			return tmpButton;
	}
}
