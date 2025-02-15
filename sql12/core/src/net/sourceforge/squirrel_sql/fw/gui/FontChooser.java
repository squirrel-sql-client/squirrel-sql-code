package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2004 Colin Bell
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * A dialog allow selection and a font and its associated info.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class FontChooser extends JDialog
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FontChooser.class);

	private final boolean _selectStyles;

	private JComboBox _fontNamesCmb;
	private final JComboBox _fontSizesCmb = new JComboBox(new String[] { "8", "9", "10", "12", "14", "16", "18", "20", "22", "24", "26", "28" });
	private final JCheckBox _boldChk = new JCheckBox(s_stringMgr.getString("FontChooser.bold"));
	private final JCheckBox _italicChk = new JCheckBox(s_stringMgr.getString("FontChooser.italic"));
	private final JLabel _previewLbl = new JLabel(s_stringMgr.getString("FontChooser.previewText"));

	private Font _font;

	private ActionListener _previewUpdater;
	private boolean _noSelection = false;

	/**
	 * Default ctor.
	 */
	public FontChooser()
	{
		this((Frame)null);
	}

	/**
	 * ctor specifying whether styles can be selected.
	 *
	 * @param	selectStyles	If <TT>true</TT> bold and italic checkboxes displayed.
	 */
	public FontChooser(boolean selectStyles)
	{
		this(null, selectStyles);
	}


	/**
	 * ctor specifying the parent dialog.
	 *
	 * @param	owner	Parent frame.
	 */
	public FontChooser(Window owner)
	{
		this(owner, true);
	}

	/**
	 * ctor specifying the parent dialog and whether styles can be selected.
	 *
	 * @param	owner	Parent frame.
	 * @param	selectStyles	If <TT>true</TT> bold and italic checkboxes displayed.
	 */
	public FontChooser(Window owner, boolean selectStyles)
	{
		this(owner,selectStyles, false);
	}

	public FontChooser(Window owner, boolean selectStyles, boolean allowNoSelection)
	{
		super(owner, s_stringMgr.getString("FontChooser.title"));
		setModal(true);
		_selectStyles = selectStyles;
		createUserInterface(allowNoSelection);

		GUIUtils.enableCloseByEscape(this);
	}

	/**
	 * Component is being added to its parent.
	 */
	public void addNotify()
	{
		super.addNotify();
		if (_previewUpdater == null)
		{
			_previewUpdater = new PreviewLabelUpdater();
			_fontNamesCmb.addActionListener(_previewUpdater);
			_fontSizesCmb.addActionListener(_previewUpdater);
			_boldChk.addActionListener(_previewUpdater);
			_italicChk.addActionListener(_previewUpdater);
		}
	}

	/**
	 * Component is being removed from its parent.
	 */
	public void removeNotify()
	{
		super.removeNotify();
		if (_previewUpdater != null)
		{
			_fontNamesCmb.removeActionListener(_previewUpdater);
			_fontSizesCmb.removeActionListener(_previewUpdater);
			_boldChk.removeActionListener(_previewUpdater);
			_italicChk.removeActionListener(_previewUpdater);
			_previewUpdater = null;
		}
	}

	public Font showDialog()
	{
		return showDialog(null);
	}

	/**
	 * Show dialog defaulting to the passed font.
	 *
	 * @param	font	The font to default to.
	 */
	public Font showDialog(Font font)
	{
		if (font != null)
		{
			_fontNamesCmb.setSelectedItem(FontFamilyWrapper.wrap(this, font.getName()));
			_fontSizesCmb.setSelectedItem("" + font.getSize());
			_boldChk.setSelected(_selectStyles && font.isBold());
			_italicChk.setSelected(_selectStyles && font.isItalic());
		}
		else
		{
			_fontNamesCmb.setSelectedIndex(0);
			_fontSizesCmb.setSelectedIndex(0);
			_boldChk.setSelected(false);
			_italicChk.setSelected(false);
		}
		setupPreviewLabel();
		setVisible(true);
		return _font;
	}

	public boolean isNoSelection()
	{
		return _noSelection;
	}

	private void setupFontFromDialog()
	{
		int size = 12;
		try
		{
			size = Integer.parseInt((String)_fontSizesCmb.getSelectedItem());
		}
		catch (Exception ignore)
		{
			// Ignore.
		}
		FontInfo fi = new FontInfo();
		fi.setFamily(((FontFamilyWrapper)_fontNamesCmb.getSelectedItem()).getFontFamilyName());
		fi.setSize(size);
		fi.setIsBold(_boldChk.isSelected());
		fi.setIsItalic(_italicChk.isSelected());
		_font = fi.createFont();
	}

	private void createUserInterface(boolean allowNoSelection)
	{
		final JPanel content = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();

		setContentPane(content);
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = gbc.gridy = 0;
		content.add(new JLabel(s_stringMgr.getString("FontChooser.font")), gbc);

		++gbc.gridx;
		content.add(new JLabel(s_stringMgr.getString("FontChooser.size")), gbc);

		if (_selectStyles)
		{
			++gbc.gridx;
			content.add(new JLabel(s_stringMgr.getString("FontChooser.style")), gbc);
		}

		++gbc.gridy;
		gbc.gridx = 0;
		_fontNamesCmb = new JComboBox(FontFamilyWrapper.createWrappers(this));
		content.add(_fontNamesCmb, gbc);

		++gbc.gridx;
		_fontSizesCmb.setEditable(true);
		content.add(_fontSizesCmb, gbc);

		if (_selectStyles)
		{
			++gbc.gridx;
			content.add(_boldChk, gbc);
			++gbc.gridy;
			content.add(_italicChk, gbc);
		}

		gbc.gridx = 0;
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		content.add(createPreviewPanel(), gbc);

		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		content.add(createButtonsPanel(allowNoSelection), gbc);

		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(true);
	}

	private JPanel createPreviewPanel()
	{
		final JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("FontChooser.previewTitle")));
		Dimension prefSize = _previewLbl.getPreferredSize();
		prefSize.height = 50;
		_previewLbl.setPreferredSize(prefSize);
		pnl.add(_previewLbl, BorderLayout.CENTER);
		setupPreviewLabel();

		return pnl;
	}

	private JPanel createButtonsPanel(boolean allowNoSelection)
	{

		JPanel pnl = new JPanel();

		JButton btnOk = new JButton(s_stringMgr.getString("FontChooser.ok"));
		btnOk.addActionListener(evt -> {setupFontFromDialog(); dispose();});

		JButton btnNoSelection = null;
		if(allowNoSelection)
		{
         btnNoSelection = new JButton(s_stringMgr.getString("FontChooser.no.selection"));
         btnNoSelection.addActionListener(evt -> {_noSelection = true; dispose();});
		}

		JButton btnCancel = new JButton(s_stringMgr.getString("FontChooser.cancel"));
		btnCancel.addActionListener(evt -> { FontChooser.this._font = null; dispose();});

		pnl.add(btnOk);
      if(allowNoSelection)
      {
         pnl.add(btnNoSelection);
      }
      pnl.add(btnCancel);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { btnOk, btnCancel });
		getRootPane().setDefaultButton(btnOk);

		return pnl;
	}

	private void setupPreviewLabel()
	{
		setupFontFromDialog();
		_previewLbl.setFont(_font);
	}

	private final class PreviewLabelUpdater implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			setupPreviewLabel();
		}
	}
}
