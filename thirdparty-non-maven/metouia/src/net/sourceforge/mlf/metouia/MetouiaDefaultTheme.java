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
*  MetouiaDefaultTheme.java                                                    *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Font;
import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * This class represents the default Metouia theme.
 * Extend this class in order to creates themes compatible to the Metouia Look
 * and Feel.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaDefaultTheme extends DefaultMetalTheme
{

  /**
   * Primary Color 1, used for the following:
   *  Active internal window borders.
   *  Shadows of activated items.
   *  System text (for example, labels).
   */
  private final ColorUIResource primary1 = new ColorUIResource(0, 0, 0);

  /**
   * Primary Color 2, used for the following:
   *  Highlighting to indicate activation (for example, of menu titles and menu
   *  items); indication of keyboard focus.
   *  Shadows (color).
   *  Scrollbars.
   */
  private final ColorUIResource primary2 = new ColorUIResource(213, 208, 198);

  /**
   * Primary Color 3, used for the following:
   *  Large colored areas (for example, the active title bar).
   *  Text selection.
   *  Tooltips background.
   *  InternalFrame TitleBar.
   */
  private final ColorUIResource primary3 = new ColorUIResource(213, 208, 198);


  /**
   * Secondary Color 2, used for the following:
   *  Dark border for flush 3D style.
   */
  private final ColorUIResource secondary1 = new ColorUIResource(133, 124, 93);

  /**
   * Secondary Color 2, used for the following:
   *  Inactive internal window borders; dimmed button borders.
   *  Shadows; highlighting of toolbar buttons upon mouse button down.
   *  Dimmed text (for example, inactive menu items or labels).
   */
  private final ColorUIResource secondary2 = new ColorUIResource(171, 171, 159);

  /**
   * Secondary Color 3, used for the following:
   *  Canvas color (that is, normal background color); inactive title bar.
   *  Background for noneditable text fields.
   */
  private final ColorUIResource secondary3 = new ColorUIResource(238, 238, 230);

  /**
   * The background color of a pressed button.
   * Intrduced by Metouia Look And Feel.
   */
  private final ColorUIResource secondary4 = new ColorUIResource(190, 190, 170);

  /**
   * The upper gradient color for components like JButton, JMenuBar and
   * JProgressBar.
   */
  private final Color gradientReflection = new Color(255, 255, 255, 86);

  /**
   * The lower gradient color for components like JButton, JMenuBar and
   * JProgressBar.
   */
  private final Color gradientShadow = new Color(188, 188, 180, 100);

  /**
   * The transluscent variation of the upper gradient color for components
   * like JButton, JMenuBar and JProgressBar.
   */
  private final Color gradientTranslucentReflection =
    new Color(gradientReflection.getRGB() & 0x00FFFFFF, true);

  /**
   * The transluscent variation of the lower gradient color for components
   * like JButton, JMenuBar and JProgressBar.
   */
  private final Color gradientTranslucentShadow =
    new Color(gradientShadow.getRGB() & 0x00FFFFFF, true);

  /**
   * A plain sans serif font used troughout the Metouia Default Theme.
   */
  private FontUIResource plainFont =
    new FontUIResource("SansSerif", Font.PLAIN, 12);;

  /**
   * A bold sans serif font used troughout the Metouia Default Theme.
   */
  private FontUIResource boldFont =
    new FontUIResource("SansSerif", Font.PLAIN, 12);

  /**
   * Gets the upper gradient color for components like JButton, JMenuBar and
   * JProgressBar.
   *
   * @return The gradient reflection color.
   */
  public Color getGradientReflection()
  {
    return gradientReflection;
  }

  /**
   * Gets the lower gradient color for components like JButton, JMenuBar and
   * JProgressBar.
   *
   * @return The gradient shadow color.
   */
  public Color getGradientShadow()
  {
    return gradientShadow;
  }

  /**
   * Gets the transluscent variation of the upper gradient color for components
   * like JButton, JMenuBar and JProgressBar.
   *
   * @return The transluscent gradient reflection color.
   */
  public Color getGradientTranslucentReflection()
  {
    return gradientTranslucentReflection;
  }

  /**
   * Gets the transluscent variation of the lower gradient color for components
   * like JButton, JMenuBar and JProgressBar.
   *
   * @return The transluscent gradient shadow color.
   */
  public Color getGradientTranslucentShadow()
  {
    return gradientTranslucentShadow;
  }

  /**
   * Gets the Font of Labels in many cases.
   *
   * @return The Font of Labels in many cases.
   */
  public FontUIResource getControlTextFont()
  {
    return plainFont;
  }

  /**
   * Gets the Font of Menus and MenuItems.
   *
   * @return The Font of Menus and MenuItems.
   */
  public FontUIResource getMenuTextFont()
  {
    return plainFont;
  }

  /**
   * Gets the Font of Nodes in JTrees.
   *
   * @return The Font of Nodes in JTrees.
   */
  public FontUIResource getSystemTextFont()
  {
    return plainFont;
  }

  /**
   * Gets the Font in TextFields, EditorPanes, etc.
   *
   * @return The Font in TextFields, EditorPanes, etc.
   */
  public FontUIResource getUserTextFont()
  {
    return plainFont;
  }

  /**
   * Gets the Font of the Title of JInternalFrames.
   *
   * @return The Font of the Title of JInternalFrames.
   */
  public FontUIResource getWindowTitleFont()
  {
    return boldFont;
  }

  /**
   * Adds some custom values to the defaults table.
   * Only some fonts are changed here.
   *
   * @param table The UI defaults table.
   */
  public void addCustomEntriesToTable(UIDefaults table)
  {
    super.addCustomEntriesToTable(table);
    UIManager.getDefaults().put("PasswordField.font", plainFont);
    UIManager.getDefaults().put("TextArea.font", plainFont);
    UIManager.getDefaults().put("TextPane.font", plainFont);
    UIManager.getDefaults().put("EditorPane.font", plainFont);
    UIManager.getDefaults().put("InternalFrame.font", plainFont);
  }

  /**
   * Gets the background color of a selected menu item.
   * Pending!
   *
   * @return The background color of a selected menu item.
   */
  public ColorUIResource getMenuSelectedBackground()
  {
    return new ColorUIResource(231, 231, 219);
  }

  /**
   * Gets the foreground color of a separator (in menues etc.).
   *
   * @return The foreground color of a separator
   */
  public ColorUIResource getSeparatorForeground()
  {
    return getPrimary2();
  }

  /**
   * Gets the name of this theme.
   *
   * @return A string describing this theme.
   */
  public String getName()
  {
    return "Metouia Default Theme";
  }

  /**
   * Gets the first primary color.
   *
   * @return The first primary color. See field declaration for more details.
   */
  protected ColorUIResource getPrimary1()
  {
    return primary1;
  }

  /**
   * Gets the second primary color.
   *
   * @return The second primary color. See field declaration for more details.
   */
  protected ColorUIResource getPrimary2()
  {
    return primary2;
  }

  /**
   * Gets the third primary color.
   *
   * @return The third primary color. See field declaration for more details.
   */
  protected ColorUIResource getPrimary3()
  {
    return primary3;
  }

  /**
   * Gets the first secondary color.
   *
   * @return The first secondary color. See field declaration for more details.
   */
  protected ColorUIResource getSecondary1()
  {
    return secondary1;
  }

  /**
   * Gets the second secondary color.
   *
   * @return The second secondary color. See field declaration for more details.
   */
  protected ColorUIResource getSecondary2()
  {
    return secondary2;
  }

  /**
   * Gets the third secondary color.
   *
   * @return The third secondary color. See field declaration for more details.
   */
  protected ColorUIResource getSecondary3()
  {
    return secondary3;
  }

  /**
   * Gets the background color of a pressed button.
   * Introduced by Metouia Look And Feel.
   *
   * @return The pressed background color.
   */
  public ColorUIResource getPressedBackground()
  {
    return secondary4;
  }
}