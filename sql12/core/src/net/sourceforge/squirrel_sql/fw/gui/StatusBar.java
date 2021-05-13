package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This is a statusbar component with a text control for messages.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StatusBar extends JPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(StatusBar.class);


	/** Label showing the message in the statusbar. */
	private final JEditorPane _textLbl;

   private final JProgressBar _progressBar = new JProgressBar();

   private final JPanel _pnlLabelOrProgress = new JPanel();

   /** Constraints used to add new controls to this statusbar. */
	private final GridBagConstraints _gbc = new GridBagConstraints();

	private Font _font;

	private StatusBarHrefListener _statusBarHrefListener;
	private Object _hrefReferenceObject;

	private JMenuItem _additionalRightMouseMenuItem;

	/**
	 * Default ctor.
	 */
	public StatusBar()
	{
		super(new GridBagLayout());

		_textLbl = new JEditorPane();
		_textLbl.setEditable(false);

		// Makes sure font size doesn't change when HTML is displayed.
		_textLbl.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		_textLbl.setBackground(new JLabel().getBackground());

		_textLbl.addHyperlinkListener( e -> onTextLblHyperEvent(e));

		_textLbl.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e)
			{
				onTriggerTextLblRightMouseMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				onTriggerTextLblRightMouseMenu(e);
			}
		});

		createGUI();
	}

	private void onTriggerTextLblRightMouseMenu(MouseEvent me)
	{
		if(false == me.isPopupTrigger())
		{
			return;
		}
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem mnuCopyAll = new JMenuItem(s_stringMgr.getString("StatusBar.rightMouseMenu.copyAll"));
		mnuCopyAll.addActionListener(e -> ClipboardUtil.copyToClip(getTextWithoutHtmlTags(_textLbl), true));
		popupMenu.add(mnuCopyAll);

		if (false == StringUtilities.isEmpty(_textLbl.getSelectedText(), true))
		{
			JMenuItem mnuCopySelected = new JMenuItem(s_stringMgr.getString("StatusBar.rightMouseMenu.copySelection"));
			mnuCopySelected.addActionListener(e -> ClipboardUtil.copyToClip(_textLbl.getSelectedText(), true));
			popupMenu.add(mnuCopySelected);
		}

		if (null != _additionalRightMouseMenuItem)
		{
			popupMenu.add(_additionalRightMouseMenuItem);
		}

		popupMenu.show(_textLbl, me.getX(), me.getY());

	}

	private String getTextWithoutHtmlTags(JEditorPane textLbl)
	{
		try
		{
			return textLbl.getDocument().getText(0, textLbl.getDocument().getLength());
		}
		catch (BadLocationException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	private void onTextLblHyperEvent(HyperlinkEvent e)
	{
		if(null != _statusBarHrefListener)
		{
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
			{
				_statusBarHrefListener.hrefClicked(e.getDescription(), _hrefReferenceObject);
			}
		}
	}

	/**
	 * Set the font for controls in this statusbar.
	 *
	 * @param	font	The font to use.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if <TT>null</TT> <TT>Font</TT> passed.
	 */
	public void setFont(Font font)
	{
		if (font == null)
		{
			throw new IllegalArgumentException("Font == null");
		}
		super.setFont(font);
		_font = font;
		updateSubcomponentsFont(this);
	}

	/**
	 * Set the text to display in the message label.
	 *
	 * @param	text	Text to display in the message label.
	 */
	public void setText(String text)
	{
		setText(text, null);
	}

	public void setText(String text, Object hrefReferenceObject)
	{
		_hrefReferenceObject = hrefReferenceObject;

		String myText = null;
		if (text != null)
		{
			myText = text.trim();
		}

		if (myText != null && myText.length() > 0)
		{
			if (myText.toLowerCase().startsWith("<html>"))
			{
				_textLbl.setContentType("text/html");
			}
			else
			{
				_textLbl.setContentType("text/plain");
			}
			_textLbl.setText(myText);
		}
		else
		{
			clearText();
		}
	}

	public void clearText()
	{
		_hrefReferenceObject = null;
		_textLbl.setContentType("text/plain");
		_textLbl.setText("");
	}

	public synchronized void addJComponent(JComponent comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("JComponent == null");
		}
		comp.setBorder(createComponentBorder());
		if (_font != null)
		{
			comp.setFont(_font);
			updateSubcomponentsFont(comp);
		}
		GUIUtils.inheritBackground(comp);
		super.add(comp, _gbc);
	}

	public static Border createComponentBorder()
	{
		return BorderFactory.createCompoundBorder(
			thinLoweredBevel,
			BorderFactory.createEmptyBorder(0, 4, 0, 4));
	}

	private static Border thinLoweredBevel = new AbstractBorder()
	{
		@Override public boolean isBorderOpaque() { return true; }

		@Override public Insets getBorderInsets(Component c, Insets insets)
		{
			insets.top = insets.bottom = insets.left = insets.right = 1;
			return insets;
		}

		@Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			Color oldColor = g.getColor();
			int right = x + width - 1;
			int bottom = y + height - 1;

			g.translate(x, y);

			Color darker = c.getBackground().darker();
			Color brighter = c.getBackground().brighter();

			g.setColor(darker);
			g.drawLine(x, y, right, y);
			g.setColor(brighter);
			g.drawLine(x, bottom, right, bottom);

			g.setColor(darker);
			g.drawLine(x, y, x, bottom);
			g.setColor(brighter);
			g.drawLine(right, y, right, bottom);

			g.translate(-x, -y);
			g.setColor(oldColor);
		}
	};

	private void createGUI()
	{
		clearText();

      Dimension progSize = _progressBar.getPreferredSize();
      progSize.height = _textLbl.getPreferredSize().height;
      _progressBar.setPreferredSize(progSize);

      _progressBar.setStringPainted(true);

      _pnlLabelOrProgress.setLayout(new GridLayout(1,1));
      _pnlLabelOrProgress.add(_textLbl);

      // The message area is on the right of the statusbar and takes
		// up all available space.
		_gbc.anchor = GridBagConstraints.WEST;
		_gbc.weightx = 1.0;
		_gbc.fill = GridBagConstraints.BOTH;
		_gbc.gridy = 0;
		_gbc.gridx = 0;
		addJComponent(_pnlLabelOrProgress);

		// Any other components are on the right.
		_gbc.weightx = 0.0;
		_gbc.anchor = GridBagConstraints.CENTER;
		_gbc.gridx = GridBagConstraints.RELATIVE;
		_gbc.insets.left = 2;
	}

	private void updateSubcomponentsFont(Container cont)
	{
		Component[] comps = cont.getComponents();
		for (int i = 0; i < comps.length; ++i)
		{
			comps[i].setFont(_font);
			if (comps[i] instanceof Container)
			{
				updateSubcomponentsFont((Container)comps[i]);
			}
		}
	}

   public void setStatusBarProgress(String msg, int minimum, int maximum, int value)
   {
      if(false == _pnlLabelOrProgress.getComponent(0) instanceof JProgressBar)
      {
         _pnlLabelOrProgress.remove(0);
         _pnlLabelOrProgress.add(_progressBar);
         validate();
      }

      _progressBar.setMinimum(minimum);
      _progressBar.setMaximum(maximum);
      _progressBar.setValue(value);

      if(null != msg)
      {
         _progressBar.setString(msg);
      }
      else
      {
         _progressBar.setString("");
      }
   }

   public void setStatusBarProgressFinished()
   {
      if(_pnlLabelOrProgress.getComponent(0) instanceof JProgressBar)
      {
         _pnlLabelOrProgress.remove(0);
         _pnlLabelOrProgress.add(_textLbl);
         validate();
         repaint();
      }
   }

	/**
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color bg)
	{
		super.setBackground(bg);
		if (_pnlLabelOrProgress != null)
		{
			_pnlLabelOrProgress.setBackground(bg);
		}
		if (_progressBar != null)
		{
			_progressBar.setBackground(bg);
		}

		if (null != _textLbl)
		{
			_textLbl.setBackground(bg);
		}
	}

	public void setHrefListener(StatusBarHrefListener statusBarHrefListener)
	{
		_statusBarHrefListener = statusBarHrefListener;
	}

	public void setAdditionalRightMouseMenuItem(JMenuItem additionalRightMouseMenuItem)
	{
		_additionalRightMouseMenuItem = additionalRightMouseMenuItem;
	}

	public Object getHrefReferenceObject()
	{
		return _hrefReferenceObject;
	}
}
