package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * http://sourceforge.net/projects/squirrel-sql
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.util.NumberDocument;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A dialog to add a SEQUENCE.
 */
public class AddModifySequenceDialog extends AbstractRefactoringDialog
{

	private static final long serialVersionUID = 1L;

	/**
	 * Internationalized strings for this class
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddModifySequenceDialog.class);

	static interface i18n
	{
		String TITLEADD = s_stringMgr.getString("AddSequenceDialog.titleAdd");

		String TITLEMODIFY = s_stringMgr.getString("AddSequenceDialog.titleModify");

		String SEQUENCE_NAME_LABEL = s_stringMgr.getString("AddSequenceDialog.sequenceNameLabel");

		String LAST_VALUE_LABEL = s_stringMgr.getString("AddSequenceDialog.lastValueLabel");
		
		String INCREMENT_LABEL = s_stringMgr.getString("AddSequenceDialog.incrementLabel");

		String START_LABEL = s_stringMgr.getString("AddSequenceDialog.startLabel");

		String RESTART_LABEL = s_stringMgr.getString("AddSequenceDialog.reStartLabel");

		String MINIMUM_LABEL = s_stringMgr.getString("AddSequenceDialog.minimumLabel");

		String MAXIMUM_LABEL = s_stringMgr.getString("AddSequenceDialog.maximumLabel");

		String CACHE_LABEL = s_stringMgr.getString("AddSequenceDialog.cacheLabel");

		String CYCLED_LABEL = s_stringMgr.getString("AddSequenceDialog.cycledLabel");
	}

	/**
	 * Dialog mode to act as sequence modifier.
	 */
	public static final int MODIFY_MODE = 1;

	/**
	 * Dialog mode to act as new sequence adder.
	 */
	public static final int ADD_MODE = 2;

	private JTextField _nameField;
	
	private JTextField _lastValueField;
	
	private JTextField _incrementField;

	private JTextField _startField;

	private JTextField _minimumField;

	private JTextField _maximumField;

	private JTextField _cacheField;

	private JCheckBox _cycledField;

	private int _dialogMode = -1;

	/**
	 * Constructor of AddModifySequenceDialog.
	 * 
	 * @param dialogMode
	 *           mode in which the dialog should be run.
	 */
	public AddModifySequenceDialog(int dialogMode)
	{
		if (dialogMode == MODIFY_MODE)
		{
			setTitle(i18n.TITLEMODIFY);
		} else if (dialogMode == ADD_MODE)
		{
			setTitle(i18n.TITLEADD);
		}
		_dialogMode = dialogMode;

		init();
	}

	/**
	 * Constructor for the MODIFY mode.
	 * 
	 * @param dialogMode
	 *           mode in which the dialog should be run.
	 * @param sequenceName
	 *           name of the sequence.
	 * @param lastValue
	 *           the last value handed out by the sequence.
	 * @param increment
	 *           increment value of the sequence.
	 * @param minimum
	 *           minimum value of the sequence.
	 * @param maximum
	 *           maximum value of the sequence.
	 * @param cache
	 *           cache value of the sequence.
	 * @param cylced
	 *           cycled value of the sequence.
	 */
	public AddModifySequenceDialog(int dialogMode, String sequenceName, String lastValue, String increment,
		String minimum, String maximum, String cache, boolean cylced)
	{
		this(dialogMode);

		_nameField.setText(sequenceName);
		_lastValueField.setText(lastValue);
		_incrementField.setText(increment);
		_minimumField.setText(minimum);
		_maximumField.setText(maximum);
		_cacheField.setText(cache);
		_cycledField.setSelected(cylced);
	}

	/**
	 * Initialize the dialog.
	 */
	protected void init()
	{

		// Name
		JLabel nameLabel = getBorderedLabel(i18n.SEQUENCE_NAME_LABEL + " ", emptyBorder);
		pane.add(nameLabel, getLabelConstraints(c));

		_nameField = new JTextField();
		_nameField.setPreferredSize(mediumField);
		if (_dialogMode == MODIFY_MODE)
			_nameField.setEnabled(false);
		pane.add(_nameField, getFieldConstraints(c));

		_nameField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent keyEvent)
			{
				if (AddModifySequenceDialog.this._nameField.getText().equals(""))
				{
					enableAllButtons(false);
					return;
				}

				enableAllButtons(true);
			}
		});

		// Last Value
		JLabel lastValueLabel = getBorderedLabel(i18n.LAST_VALUE_LABEL + " ", emptyBorder);
		pane.add(lastValueLabel, getLabelConstraints(c));
		
		_lastValueField = new JTextField();
		_lastValueField.setEnabled(false);
		_lastValueField.setPreferredSize(mediumField);
		pane.add(_nameField, getFieldConstraints(c));

		// Increment
		JLabel incrementLabel = getBorderedLabel(i18n.INCREMENT_LABEL + " ", emptyBorder);
		pane.add(incrementLabel, getLabelConstraints(c));

		_incrementField = new JTextField();
		_incrementField.setPreferredSize(mediumField);
		_incrementField.setDocument(new NumberDocument());
		pane.add(_incrementField, getFieldConstraints(c));

		// Start
		JLabel startLabel;
		if (_dialogMode == MODIFY_MODE)
		{
			startLabel = getBorderedLabel(i18n.RESTART_LABEL + " ", emptyBorder);
		} else
		{
			startLabel = getBorderedLabel(i18n.START_LABEL + " ", emptyBorder);
		}
		pane.add(startLabel, getLabelConstraints(c));

		_startField = new JTextField();
		_startField.setPreferredSize(mediumField);
		_startField.setDocument(new NumberDocument());
		pane.add(_startField, getFieldConstraints(c));

		// Minimum
		JLabel minimumLabel = getBorderedLabel(i18n.MINIMUM_LABEL + " ", emptyBorder);
		pane.add(minimumLabel, getLabelConstraints(c));

		_minimumField = new JTextField();
		_minimumField.setPreferredSize(mediumField);
		_minimumField.setDocument(new NumberDocument());
		pane.add(_minimumField, getFieldConstraints(c));

		// Maximum
		JLabel maximumLabel = getBorderedLabel(i18n.MAXIMUM_LABEL + " ", emptyBorder);
		pane.add(maximumLabel, getLabelConstraints(c));

		_maximumField = new JTextField();
		_maximumField.setPreferredSize(mediumField);
		_maximumField.setDocument(new NumberDocument());
		pane.add(_maximumField, getFieldConstraints(c));

		// Cache
		JLabel cacheLabel = getBorderedLabel(i18n.CACHE_LABEL + " ", emptyBorder);
		pane.add(cacheLabel, getLabelConstraints(c));

		_cacheField = new JTextField();
		_cacheField.setPreferredSize(mediumField);
		_cacheField.setDocument(new NumberDocument());
		pane.add(_cacheField, getFieldConstraints(c));

		// Cycled
		JLabel cycledLabel = getBorderedLabel(i18n.CYCLED_LABEL + " ", emptyBorder);
		pane.add(cycledLabel, getLabelConstraints(c));

		_cycledField = new JCheckBox();
		pane.add(_cycledField, getFieldConstraints(c));

		enableAllButtons(_dialogMode == MODIFY_MODE);
		super.executeButton.setRequestFocusEnabled(true);
	}

	public String getSequenceName()
	{
		return _nameField.getText();
	}

	public String getIncrement()
	{
		return _incrementField.getText();
	}

	public String getStart()
	{
		return _startField.getText();
	}

	public String getMaximum()
	{
		return _maximumField.getText();
	}

	public String getMinimum()
	{
		return _minimumField.getText();
	}

	public String getCache()
	{
		return _cacheField.getText();
	}

	public boolean isCycled()
	{
		return _cycledField.isSelected();
	}

	public static void main(String[] args)
	{
		new AddModifySequenceDialog(ADD_MODE).setVisible(true);
	}
}
