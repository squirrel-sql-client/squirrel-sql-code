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
*  MetouiaTreeUI.java                                                          *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

/**
 * This class represents the UI delegate for the JTree component.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaTreeUI extends BasicTreeUI
{

  /**
   * The expand control image.
   */
  protected static ImageIcon expanded;

  /**
   * The collapse control image.
   */
  protected static ImageIcon collapsed;

  /**
   * Creates the UI delegate for the given tree.
   *
   * @param tree The tree to create its UI delegate.
   */
  public MetouiaTreeUI(JComponent tree)
  {
    expanded = MetouiaLookAndFeel.loadIcon("treeex.gif", this);
    collapsed = MetouiaLookAndFeel.loadIcon("treecol.gif", this);
  }

  /**
   * Creates the UI delegate for the given component.
   *
   * @param tree The component to create its UI delegate.
   * @return The UI delegate for the given component.
   */
  public static ComponentUI createUI(JComponent tree)
  {
    return new MetouiaTreeUI(tree);
  }

  /**
   * Paints the expand (toggle) part of a row. The reciever should
   * NOT modify <code>clipBounds</code>, or <code>insets</code>.
   *
   * @param  g The graphics context to use.
   * @param  bounds The expand control bounds.
   * @param  isExpanded The target state of the expand control.
   * @param  clipBounds Unused argument.
   * @param  insets Unused argument.
   * @param  path Unused argument.
   * @param  row Unused argument.
   * @param  hasBeenExpanded Unused argument.
   * @param  isLeaf Unused argument.
   */
  protected void paintExpandControl(Graphics g, Rectangle clipBounds,
    Insets insets, Rectangle bounds, TreePath path, int row,
    boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
  {
    if (isExpanded)
    {
      g.drawImage(expanded.getImage(), bounds.x - 17, bounds.y + 4, null);
    }
    else
    {
      g.drawImage(collapsed.getImage(), bounds.x - 17, bounds.y + 4, null);
    }
  }
}