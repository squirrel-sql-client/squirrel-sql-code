package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FontChooser extends JDialog
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "Font Chooser";
		String OK = "OK";
	}

//	private static int[] FONT_STYLES = { Font.PLAIN, Font.BOLD, Font.ITALIC };
	private JComboBox _fontNamesCmb;
	private JComboBox _fontSizesCmb =
		new JComboBox(new String[] { "8", "9", "10", "12", "14" });
	private JCheckBox _boldChk = new JCheckBox("Bold");
	private JCheckBox _italicChk = new JCheckBox("Italic");
	private JLabel _previewLbl =
		new JLabel("The quick brown fox jumped over the lazy dog");

	private Font _font;

	public FontChooser()
	{
		this((Frame) null);
	}

	public FontChooser(Frame owner)
	{
		super(owner, i18n.TITLE, true);
		createUserInterface();
	}

	public FontChooser(Dialog owner)
	{
		super(owner, i18n.TITLE, true);
		createUserInterface();
	}

	public Font showDialog()
	{
		return showDialog(null);
	}

	public Font showDialog(Font font)
	{
		if (font != null)
		{
			_fontNamesCmb.setSelectedItem(font.getName());
			_fontSizesCmb.setSelectedItem("" + font.getSize());
			_boldChk.setSelected(font.isBold());
			_italicChk.setSelected(font.isItalic());
		}
		else
		{
			_fontNamesCmb.setSelectedIndex(0);
			_fontSizesCmb.setSelectedIndex(0);
			_boldChk.setSelected(false);
			_italicChk.setSelected(false);
		}
		setupPreviewLabel();
		show();
		return _font;
	}

	public Font getSelectedFont()
	{
		return _font;
	}

	protected void setupFontFromDialog()
	{
		int size = 12;
		try
		{
			size = Integer.parseInt((String) _fontSizesCmb.getSelectedItem());
		}
		catch (Exception ignore)
		{
		}
		FontInfo fi = new FontInfo();
		fi.setFamily((String) _fontNamesCmb.getSelectedItem());
		fi.setSize(size);
		fi.setIsBold(_boldChk.isSelected());
		fi.setIsItalic(_italicChk.isSelected());
		_font = fi.createFont();
	}

	private void createUserInterface()
	{
		final JPanel content = new JPanel();
		setContentPane(content);
		final GridBagLayout gbl = new GridBagLayout();
		final GridBagConstraints gbc = new GridBagConstraints();
		content.setLayout(gbl);

		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);

		gbc.gridx = gbc.gridy = 0;
		content.add(new JLabel("Font"), gbc);

		++gbc.gridx;
		content.add(new JLabel("Size"), gbc);

		++gbc.gridx;
		content.add(new JLabel("Style"), gbc);

		++gbc.gridy;
		gbc.gridx = 0;
		_fontNamesCmb = new JComboBox(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		_fontNamesCmb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setupPreviewLabel();
			}
		});
		content.add(_fontNamesCmb, gbc);

		++gbc.gridx;
		_fontSizesCmb.setEditable(true);
		_fontSizesCmb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setupPreviewLabel();
			}
		});
		content.add(_fontSizesCmb, gbc);

		++gbc.gridx;
		_boldChk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setupPreviewLabel();
			}
		});
		content.add(_boldChk, gbc);
		++gbc.gridy;
		_italicChk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setupPreviewLabel();
			}
		});
		content.add(_italicChk, gbc);

		Dimension prefSize = _previewLbl.getPreferredSize();
		prefSize.height = 50;
		_previewLbl.setPreferredSize(prefSize);
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		content.add(_previewLbl, gbc);

		++gbc.gridy;
		gbc.anchor = GridBagConstraints.CENTER;
		content.add(createButtonsPanel(), gbc);

		setupPreviewLabel();

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(false);
	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("OK");
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setupFontFromDialog();
				dispose();
			}
		});
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				_font = null;
				dispose();
			}
		});

		pnl.add(okBtn);
		pnl.add(cancelBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, cancelBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private void setupPreviewLabel()
	{
		setupFontFromDialog();
		_previewLbl.setFont(_font);
	}
}
