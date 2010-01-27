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
*  MetouiaLookAndFeel.java                                                     *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Insets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import net.sourceforge.mlf.metouia.borders.MetouiaBorderUtilities;
import net.sourceforge.mlf.metouia.borders.MetouiaInternalFrameBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaMenuBarBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaMenuItemBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaOptionDialogBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaPaletteBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaPopupMenuBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaScrollPaneBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaTableHeaderBorder;
import net.sourceforge.mlf.metouia.borders.MetouiaToolBarBorder;

/**
 * This is the main class of the Metouia Look and Feel.
 * It registers the UI delegates for all swing widgets and installs the default
 * color theme.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaLookAndFeel extends MetalLookAndFeel
{
  /**
   * The current Metouia compatible theme.
   */
  protected static MetouiaDefaultTheme metouiaTheme;

  /**
   * The installation state of the Metouia Look and Feel.
   */
  private static boolean isInstalled = false;

  /**
   * The installation state of the Metouia Theme.
   */
  private static boolean themeHasBeenSet = false;

  /**
   * This constructor installs the Metouia Look and Feel with the default color
   * theme.
   */
  public MetouiaLookAndFeel()
  {
    if (!isInstalled)
    {
      isInstalled = true;
      UIManager.installLookAndFeel(
        new UIManager.LookAndFeelInfo(
          "Metouia",
          "net.sourceforge.mlf.metouia.MetouiaLookAndFeel"));
    }
  }

  /**
   * Return a string that identifies this look and feel.  This string
   * will be used by applications/services that want to recognize
   * well known look and feel implementations.  Presently
   * the well known names are "Motif", "Windows", "Mac", "Metal".  Note
   * that a LookAndFeel derived from a well known superclass
   * that doesn't make any fundamental changes to the look or feel
   * shouldn't override this method.
   *
   * @return The Metouia Look and Feel identifier.
   */
  public String getID()
  {
    return "Metouia";
  }

  /**
   * Return a short string that identifies this look and feel, e.g.
   * "CDE/Motif".  This string should be appropriate for a menu item.
   * Distinct look and feels should have different names, e.g.
   * a subclass of MotifLookAndFeel that changes the way a few components
   * are rendered should be called "CDE/Motif My Way"; something
   * that would be useful to a user trying to select a L&F from a list
   * of names.
   *
   * @return The look and feel short name.
   */
  public String getName()
  {
    return "Metouia";
  }

  /**
   * Return a one line description of this look and feel implementation,
   * e.g. "The CDE/Motif Look and Feel".   This string is intended for
   * the user, e.g. in the title of a window or in a ToolTip message.
   *
   * @return The look and feel short description.
   */
  public String getDescription()
  {
    return "Metouia Look and Feel";
  }

  /**
   * If the underlying platform has a "native" look and feel, and this
   * is an implementation of it, return true.  For example a CDE/Motif
   * look and implementation would return true when the underlying
   * platform was Solaris.
   */
  public boolean isNativeLookAndFeel()
  {
    return false;
  }

  /**
   * Return true if the underlying platform supports and or permits
   * this look and feel.  This method returns false if the look
   * and feel depends on special resources or legal agreements that
   * aren't defined for the current platform.
   */
  public boolean isSupportedLookAndFeel()
  {
    return true;
  }

  /**
   * Initializes the uiClassID to BasicComponentUI mapping.
   * The JComponent classes define their own uiClassID constants. This table
   * must map those constants to a BasicComponentUI class of the appropriate
   * type.
   *
   * @param table The ui defaults table.
   */
  protected void initClassDefaults(UIDefaults table)
  {
    super.initClassDefaults(table);
    table.putDefaults(new Object[]
    {
      "ButtonUI", "net.sourceforge.mlf.metouia.MetouiaButtonUI",
      "CheckBoxUI", "net.sourceforge.mlf.metouia.MetouiaCheckBoxUI",
      "TextFieldUI", "net.sourceforge.mlf.metouia.MetouiaTextFieldUI",
      "ListUI", "net.sourceforge.mlf.metouia.MetouiaListUI",
      "TreeUI", "net.sourceforge.mlf.metouia.MetouiaTreeUI",
      "ToolBarUI", "net.sourceforge.mlf.metouia.MetouiaToolBarUI",
      "MenuBarUI", "net.sourceforge.mlf.metouia.MetouiaMenuBarUI",
      "MenuUI", "net.sourceforge.mlf.metouia.MetouiaMenuUI",
      "ScrollBarUI", "net.sourceforge.mlf.metouia.MetouiaScrollBarUI",
      "TabbedPaneUI", "net.sourceforge.mlf.metouia.MetouiaTabbedPaneUI",
      "ToggleButtonUI", "net.sourceforge.mlf.metouia.MetouiaToggleButtonUI",
      "PasswordFieldUI", "net.sourceforge.mlf.metouia.MetouiaPasswordFieldUI",
      "ScrollPaneUI", "net.sourceforge.mlf.metouia.MetouiaScrollPaneUI",
      "ProgressBarUI", "net.sourceforge.mlf.metouia.MetouiaProgressBarUI",
      "TableHeaderUI", "net.sourceforge.mlf.metouia.MetouiaTableHeaderUI",
      "InternalFrameUI", "net.sourceforge.mlf.metouia.MetouiaInternalFrameUI",
    });
  }

  /**
   * Creates the default theme and installs it.
   * The MetouiaDefaultTheme is used as default.
   */
  protected void createDefaultTheme()
  {
    if (!themeHasBeenSet)
    {
      metouiaTheme = new MetouiaDefaultTheme();
      setCurrentTheme(metouiaTheme);
    }
  }

  /**
   * Sets the current color theme.
   * Warning: The them must be an instance of MetouiaDefaultTheme!
   *
   * @param theme The theme to install.
   */
  public static void setCurrentTheme(MetalTheme theme)
  {
    MetalLookAndFeel.setCurrentTheme(theme);
    themeHasBeenSet = true;
  }

  /**
   * Initializes the system colors.
   *
   * @param table The ui defaults table.
   */
  protected void initSystemColorDefaults(UIDefaults table)
  {
    super.initSystemColorDefaults(table);
    table.put("textHighlight", getTextHighlightColor());
  }

  /**
   * Initializes the default values for many ui widgets and puts them in the
   * given ui defaults table.
   * Here is the place where borders can be changed.
   *
   * @param table The ui defaults table.
   */
  protected void initComponentDefaults(UIDefaults table)
  {
    // Let Metal Look and Feel do the basic and complete initializations:
    super.initComponentDefaults(table);

    // Replace the Metal borders:
    Border border;
    table.put("Button.border", MetouiaBorderUtilities.getButtonBorder());
    table.put("ToggleButton.border", MetouiaBorderUtilities.getToggleButtonBorder());
    table.put("TextField.border", MetouiaBorderUtilities.getTextFieldBorder());
    table.put("ToolBar.border", new MetouiaToolBarBorder());
    table.put("MenuBar.border", new MetouiaMenuBarBorder());
    table.put("ScrollPane.border", new MetouiaScrollPaneBorder());
    table.put("InternalFrame.border", new MetouiaInternalFrameBorder());
    table.put("InternalFrame.paletteBorder", new MetouiaPaletteBorder());
    table.put("InternalFrame.optionDialogBorder", new MetouiaOptionDialogBorder());
    border = new MetouiaMenuItemBorder();
    table.put("Menu.border", border);
    table.put("MenuItem.border", border);
    table.put("CheckBoxMenuItem.border", border);
    table.put("RadioButtonMenuItem.border", border);
    table.put("PopupMenu.border", new MetouiaPopupMenuBorder());
    table.put("TableHeader.cellBorder", new MetouiaTableHeaderBorder());

    // Tweak some subtle values:
    table.put("SplitPane.dividerSize", new Integer(6));
    table.put("InternalFrame.paletteTitleHeight", new Integer(13));
    table.put("InternalFrame.frameTitleHeight", new Integer(21));
    table.put("TabbedPane.contentBorderInsets", new Insets(4, 4, 3,3));
//    "TabbedPane.contentBorderInsets", tabbedPaneContentBorderInsets,
    table.put("Button.select", metouiaTheme.getPressedBackground());
    table.put("RadioButton.select", metouiaTheme.getPressedBackground());
    table.put("ToggleButton.select", metouiaTheme.getPressedBackground());
    table.put("Checkbox.select", metouiaTheme.getPressedBackground());
    table.put("TabbedPane.unselected", metouiaTheme.getPressedBackground());

    // Change some icons:
    table.put("InternalFrame.icon", loadIcon("default.gif", this));
    table.put("InternalFrame.paletteCloseIcon", loadIcon("pclose.gif", this));
    table.put("InternalFrame.closeIcon", loadIcon("close.gif", this));
    table.put("InternalFrame.maximizeIcon", loadIcon("maximize.gif", this));
    table.put("InternalFrame.iconifyIcon", loadIcon("minimize.gif", this));
    table.put("InternalFrame.minimizeIcon", loadIcon("restore.gif", this));
  }

  /**
   * Loads an image icon.
   *
   * @param file The image file name.
   * @param invoker The refence of the invoking class, whose classloader will be
   *                used for loading the image.
   */
  static ImageIcon loadIcon(String file, Object invoker)
  {
    file = "/net/sourceforge/mlf/metouia/icons/" + file;
    try
    {
      return new ImageIcon(Toolkit.getDefaultToolkit().createImage(
        readStream(invoker.getClass().getResourceAsStream(file))));
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
      System.out.println("Error getting resource " + file);
      return null;
    }
  }

  /**
   * Reads from a given input stream.
   *
   * @param input The input stream.
   * @return The read data.
   * @exception IOException thrown when this method fails.
   */
  static final byte[] readStream(InputStream input) throws IOException
  {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    int read;
    byte[] buffer = new byte[256];
    try
    {
      while ((read = input.read(buffer, 0, 256)) != -1)
      {
        bytes.write(buffer, 0, read);
      }
    }
    catch (IOException exception)
    {
      throw (IOException)exception.fillInStackTrace();
    }
    return bytes.toByteArray();
  }

  /**
   * Gets the upper gradient color for components like JButton, JMenuBar and
   * JProgressBar.
   *
   * @return The gradient reflection color.
   */
  public static Color getGradientReflection()
  {
    return metouiaTheme.getGradientReflection();
  }

  /**
   * Gets the lower gradient color for components like JButton, JMenuBar and
   * JProgressBar.
   *
   * @return The gradient shadow color.
   */
  public static Color getGradientShadow()
  {
    return metouiaTheme.getGradientShadow();
  }

  /**
   * Gets the transluscent variation of the upper gradient color for components
   * like JButton, JMenuBar and JProgressBar.
   *
   * @return The transluscent gradient reflection color.
   */
  public static Color getGradientTranslucentReflection()
  {
    return metouiaTheme.getGradientTranslucentReflection();
  }

  /**
   * Gets the transluscent variation of the lower gradient color for components
   * like JButton, JMenuBar and JProgressBar.
   *
   * @return The transluscent gradient shadow color.
   */
  public static Color getGradientTranslucentShadow()
  {
    return metouiaTheme.getGradientTranslucentShadow();
  }
}