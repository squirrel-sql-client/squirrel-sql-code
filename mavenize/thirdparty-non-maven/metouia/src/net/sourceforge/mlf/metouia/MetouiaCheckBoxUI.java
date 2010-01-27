/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*        Metouia Look And Feel: a free pluggable look and feel for java        *
*                         http://mlf.sourceforge.net                           *
*          (C) Copyright 2002, by Taoufik Romdhane and Contributors.           *
*                                                                              *
*   This library is free software; you can redistribute it and/or modify it    *
*   under the terms of the GNU Lesser General Public License as published by   *
*   the Free Software Foundation; either version 2.1 of the License, or (at    *
*   your option) any later version.                                            *
*                                                                              *
*   This library is distributed in the hope that it will be useful,            *
*   but WITHOUT ANY WARRANTY; without even the implied warranty of             *
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                       *
*   See the GNU Lesser General Public License for more details.                *
*                                                                              *
*   You should have received a copy of the GNU General Public License along    *
*   with this program; if not, write to the Free Software Foundation, Inc.,    *
*   59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.                    *
*                                                                              *
*  MetouiaCheckBoxUI.java                                                      *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import javax.swing.JComponent;
import javax.swing.AbstractButton;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalCheckBoxUI;

/**
 * This class represents the UI delegate for the JCheckBox component.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaCheckBoxUI extends MetalCheckBoxUI
{

  /**
   * The Cached UI delegate.
   */
  private final static MetouiaCheckBoxUI checkBoxUI = new MetouiaCheckBoxUI();

  /**
   * Creates the UI delegate for the given component.
   *
   * @param c The component to create its UI delegate.
   * @return The UI delegate for the given component.
   */
  public static ComponentUI createUI(JComponent c)
  {
    return checkBoxUI;
  }

  /**
   * Installs some default values for the given button.
   * The button border is replaced by a metouia border.
   *
   * @param button The reference of the button to install its default values.
   */
  public void installDefaults(AbstractButton button)
  {
    super.installDefaults(button);
    icon = new MetouiaCheckBoxIcon();
  }
}