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
*  MetouiaInternalFrameUI.java                                                 *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalInternalFrameUI;

/**
 * This class represents the UI delegate for the JInternalFrame component and
 * its derivates.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaInternalFrameUI extends MetalInternalFrameUI
{

  /**
   * The metouia version of the internal frame title pane.
   */
  private MetouiaInternalFrameTitlePane titlePane;

  /**
   * Creates the UI delegate for the given frame.
   *
   * @param frame The frame to create its UI delegate.
   */
  public MetouiaInternalFrameUI(JInternalFrame frame)
  {
    super(frame);
  }

  /**
   * Creates the UI delegate for the given component.
   *
   * @param c The component to create its UI delegate.
   * @return The UI delegate for the given component.
   */
  public static ComponentUI createUI(JComponent c)
  {
    return new MetouiaInternalFrameUI((JInternalFrame)c);
  }

  /**
   * Creates the north pane (the internal frame title pane) for the given frame.
   *
   * @param frame The frame to create its north pane.
   */
  protected JComponent createNorthPane(JInternalFrame frame)
  {
    super.createNorthPane(frame);
    titlePane = new MetouiaInternalFrameTitlePane(frame);
    return titlePane;
  }

  /**
   * Changes this internal frame mode from / to palette mode.
   * This affect only the title pane.
   *
   * @param isPalette The target palette mode.
   */
  public void setPalette(boolean isPalette)
  {
    super.setPalette(isPalette);
    titlePane.setPalette(isPalette);
  }
}