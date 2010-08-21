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
*  MetouiaInternalFrameTitlePane.java                                          *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):   Christian Walker                                         *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;
import javax.swing.plaf.metal.MetalLookAndFeel;
import net.sourceforge.mlf.metouia.borders.MetouiaDots;

/**
 * This class represents the title pane for the JInternalFrame components.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaInternalFrameTitlePane extends MetalInternalFrameTitlePane
  implements LayoutManager
{

  /**
   * The frame's title height, read from the UIDefaults table.
   */
  protected int frameTitleHeight;

  /**
   * The buttons width, calculated at runtime.
   */
  private int buttonsWidth;

  /**
   * The shared instance of the Metouia dots.
   */
  protected MetouiaDots dots = new MetouiaDots(0, 0);

  /**
   * The shared instance of the Metouia dots for palette, initialized when
   * needed.
   */
  protected MetouiaDots paletteDots;

  /**
   * Installs some default values.
   * Reads the internalframe title height from the ui defaults table.
   */
  protected void installDefaults()
  {
    super.installDefaults();
    frameTitleHeight = UIManager.getInt("InternalFrame.frameTitleHeight");
  }

  /**
   * This constructor creates a title pane for the given internal frame
   * instance.
   *
   * @param frame The internal frame that needs a title pane.
   */
  public MetouiaInternalFrameTitlePane(JInternalFrame frame)
  {
    super(frame);
  }

  /**
   * Paints this component.
   *
   * @param g The graphics context to use.
   */
  public void paintComponent(Graphics g)
  {
    if (isPalette)
    {
      paintPalette(g);
      return;
    }

    boolean leftToRight = frame.getComponentOrientation().isLeftToRight();
    boolean isSelected = frame.isSelected();

    int width = getWidth();
    int height = getHeight();

    Color background = MetalLookAndFeel.getWindowTitleInactiveBackground();
    Color foreground = MetalLookAndFeel.getWindowTitleInactiveForeground();
    Color darkShadow = MetalLookAndFeel.getControlDarkShadow();

    g.setColor(background);
    g.fillRect(0, 0, width, height);

    g.setColor(darkShadow);
    g.drawLine(0, height - 2, width, height - 2);
    g.setColor(Color.white);
    g.drawLine(0, height - 1, width, height - 1);


    int titleLength = 0;
    int xOffset = leftToRight ? 2 : width - 2;
    String frameTitle = frame.getTitle();

    Icon icon = frame.getFrameIcon();
    if (icon != null)
    {
      if (!leftToRight)
        xOffset -= icon.getIconWidth();
      int iconY = ((height / 2) - (icon.getIconHeight() / 2));
      icon.paintIcon(frame, g, xOffset, iconY);
      xOffset += leftToRight ? icon.getIconWidth() + 2 : -2;
    }

    if (frameTitle != null)
    {
      Font f = getFont();
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics();
      titleLength = fm.stringWidth(frameTitle);

      g.setColor(foreground);

      int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();
      if (!leftToRight)
        xOffset -= titleLength;
      g.drawString(frameTitle, xOffset, yOffset);
      xOffset += leftToRight ? titleLength + 2  : -2;
    }

    int bumpXOffset;
    int bumpLength;

    if (leftToRight)
    {
      bumpLength = width - buttonsWidth - xOffset - 2;
      bumpXOffset = xOffset;
    }
    else
    {
      bumpLength = xOffset - buttonsWidth - 2;
      bumpXOffset = buttonsWidth + 2;
    }
    int bumpYOffset = 6;
    int bumpHeight = getHeight() - (2 * bumpYOffset) + 1;
    dots.setDotsArea(bumpLength, bumpHeight);
    if (isSelected)
    {
      dots.paintIcon(this, g, bumpXOffset, bumpYOffset);
    }

    // Paint the horizontal highlight gradient:
    MetouiaGradients.drawHorizontalHighlight(g, this);

    // Paint the horizontal shadow gradient:
    MetouiaGradients.drawShadow(
      g, new Rectangle(0, getHeight() / 2, getWidth(), getHeight() / 2 - 1),
      true, false);
  }

  /**
   * Creates the layout manager for the title pane.
   *
   * @return The layout manager for the title pane.
   */
  protected LayoutManager createLayout()
  {
    return this;
  }

  /**
   * Creates the buttons of the title pane and initilizes their actions.
   */
  protected void createButtons()
  {
    iconButton = new JButton();
    iconButton.addActionListener(iconifyAction);
    iconButton.setRolloverEnabled(true);
    iconButton.setContentAreaFilled(false);

    maxButton = new JButton();
    maxButton.addActionListener(maximizeAction);
    maxButton.setRolloverEnabled(true);
    maxButton.setContentAreaFilled(false);

    closeButton = new JButton();
    closeButton.addActionListener(closeAction);
    closeButton.setRolloverEnabled(true);
    closeButton.setContentAreaFilled(false);

    setButtonIcons();

    iconButton.getAccessibleContext().setAccessibleName(
      UIManager.getString(
        "InternalFrameTitlePane.iconifyButtonAccessibleName"));

    maxButton.getAccessibleContext().setAccessibleName(
      UIManager.getString(
        "InternalFrameTitlePane.maximizeButtonAccessibleName"));

    closeButton.getAccessibleContext().setAccessibleName(
      UIManager.getString(
        "InternalFrameTitlePane.closeButtonAccessibleName"));
  }

  /**
   * Paints the title pane for a palette.
   *
   * @param g The graphics context to use.
   */
  public void paintPalette(Graphics g)
  {
    boolean leftToRight = frame.getComponentOrientation().isLeftToRight();

    int width = getWidth();
    int height = getHeight();

    if (paletteDots == null)
    {
      paletteDots
        = new MetouiaDots(0, 0);
    }

    Color background = MetalLookAndFeel.getPrimaryControlShadow();
    Color darkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();

    background = MetalLookAndFeel.getWindowTitleInactiveBackground();
//		foreground = MetalLookAndFeel.getWindowTitleInactiveForeground();
    darkShadow = MetalLookAndFeel.getControlDarkShadow();

    g.setColor(background);
    g.fillRect(0, 0, width, height);

    g.setColor(darkShadow);
    g.drawLine(0, height - 1, width, height - 1);

    int xOffset = leftToRight ? 2 : buttonsWidth + 2;
    int bumpLength = width - buttonsWidth - 1;
    int bumpHeight = getHeight() - 2;
    paletteDots.setDotsArea(bumpLength, bumpHeight);
    paletteDots.paintIcon(this, g, xOffset, 2);

    // Paint the horizontal highlight gradient:
    MetouiaGradients.drawHorizontalHighlight(g, this);

    // Paint the horizontal shadow gradient:
    MetouiaGradients.drawHorizontalShadow(g, this);
  }

  /**
   * Adds the specified component with the specified name to the layout.
   *
   * @param name the component name
   * @param c the component to be added
   */
  public void addLayoutComponent(String name, Component c)
  {
  }

  /**
   * Removes the specified component from the layout.
   *
   * @param c the component to be removed
   */
  public void removeLayoutComponent(Component c)
  {
  }

  /**
   * Calculates the preferred size dimensions for the specified
   * panel given the components in the specified parent container.
   *
   * @param c the component to be laid out
   */
  public Dimension preferredLayoutSize(Container c)
  {
    return getPreferredSize(c);
  }

  /**
   * Gets the preferred size of the given container.
   *
   * @param c The container to gets its preferred size.
   * @return The preferred size of the given container.
   */
  public Dimension getPreferredSize(Container c)
  {
    return
      new Dimension(
      c.getSize().width,
      (isPalette ? paletteTitleHeight : frameTitleHeight));
  }

  /**
   * The minimum size of the frame.
   * This is used, for example, during resizing to
   * find the minimum allowable size.
   * Providing at least some minimum size fixes a bug
   * which breaks horizontal resizing.
   * <b>Note</b>: the Motif plaf allows for a 0,0 min size,
   * but we provide a reasonable minimum here.
   * <b>Future</b>: calculate min size based upon contents.
   */
  public Dimension getMinimumSize() {
    return new Dimension(70,20);
  }

  /**
   * Calculates the minimum size dimensions for the specified
   * panel given the components in the specified parent container.
   *
   * @param c the component to be laid out
   */
  public Dimension minimumLayoutSize(Container c)
  {
    return preferredLayoutSize(c);
  }

  /**
   * Lays out the container in the specified panel.
   *
   * @param c the component which needs to be laid out
   */
  public void layoutContainer(Container c)
  {
    boolean leftToRight = frame.getComponentOrientation().isLeftToRight();

    int w = getWidth();
    int x = leftToRight ? w : 0;
    int y = 2;
    int spacing;

    // assumes all buttons have the same dimensions
    // these dimensions include the borders
    int buttonHeight = closeButton.getIcon().getIconHeight();
    int buttonWidth = closeButton.getIcon().getIconWidth();

    if (frame.isClosable())
    {
      if (isPalette)
      {
        spacing = 0;
        x += leftToRight ? -spacing - (buttonWidth) : spacing;
        closeButton.setBounds(x, y - 1, buttonWidth, getHeight() - 2);
        if (!leftToRight)
          x += (buttonWidth);
      }
      else
      {
        spacing = 0;
        x += leftToRight ? -spacing - buttonWidth : spacing;
        closeButton.setBounds(x, y, buttonWidth, buttonHeight);
        if (!leftToRight)
          x += buttonWidth;
      }
    }

    if (frame.isMaximizable() && !isPalette)
    {
      spacing = frame.isClosable() ? 0 : 2;
      x += leftToRight ? -spacing - buttonWidth : spacing;
      maxButton.setBounds(x, y, buttonWidth, buttonHeight);
      if (!leftToRight)
        x += buttonWidth;
    }

    if (frame.isIconifiable() && !isPalette)
    {
      spacing = frame.isMaximizable() ? 0
        : (frame.isClosable() ? 0 : 2);
      x += leftToRight ? -spacing - buttonWidth : spacing;
      iconButton.setBounds(x, y, buttonWidth, buttonHeight);
      if (!leftToRight)
        x += buttonWidth;
    }

    buttonsWidth = leftToRight ? w - x : x;
  }
}