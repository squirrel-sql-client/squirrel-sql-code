package net.sourceforge.squirrel_sql.plugins.syntax;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;

import net.sourceforge.squirrel_sql.plugins.syntax.prefspanel.StyleMaintenancePanel;
import net.sourceforge.squirrel_sql.plugins.syntax.prefspanel.StylesList;
/**
 * New Session and Current Session preferences panel for this plugin.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SyntaxPreferencesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SyntaxPreferencesPanel.class);

	/** Plugin preferences object. */
	private final SyntaxPreferences _prefs;

	/** Component to display in the preferences dialog. */
	private final MyPanel _myPanel;

	/**
	 * Ctor.
	 *
	 * @param	prefs	The preferences to be maintained.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>prefs</TT> is <TT>null</TT>.
	 */
	public SyntaxPreferencesPanel(SyntaxPreferences prefs, SyntaxPluginResources rsrc)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("Null SyntaxPreferences passed");
		}
		_prefs = prefs;

		// Create the actual panel that will be displayed in dialog.
		_myPanel = new MyPanel(prefs, rsrc);
	}

	/**
	 * Panel is being loaded for the Application Preferences. This means that
	 * the settings are for newly created sessions.
	 *
	 * @param	app	 Application API.
	 */
	public void initialize(IApplication app)
	{
		_myPanel.loadData(_prefs);
	}

	/**
	 * Panel is being loaded for the Session Properties. This means that
	 * the settings are for the current session only.
	 *
	 * @param	app	 Application API.
	 *
	 * @throws	IllegalArgumentException
	 *			if <TT>IApplication</TT> is <TT>null</TT>.
	 */
	public void initialize(IApplication app, ISession session)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_myPanel.loadData(_prefs);
	}

	/**
	 * Return the component to be displayed in the Preferences dialog.
	 *
	 * @return	the component to be displayed in the Preferences dialog.
	 */
	public Component getPanelComponent()
	{
		return _myPanel;
	}

	/**
	 * User has pressed OK or Apply in the dialog so save data from
	 * panel.
	 */
	public void applyChanges()
	{
		_myPanel.applyChanges(_prefs);
	}

	/**
	 * Return the title for this panel.
	 *
	 * @return	the title for this panel.
	 */
	public String getTitle()
	{
		return MyPanel.i18n.TAB_TITLE;
	}

	/**
	 * Return the hint for this panel.
	 *
	 * @return	the hint for this panel.
	 */
	public String getHint()
	{
		return MyPanel.i18n.TAB_HINT;
	}

	/**
	 * Component to be displayed in the preferences dialog.
	 */
	private final static class MyPanel extends JPanel
	{
      /**
		 * This interface defines locale specific strings. This should be
		 * replaced with a property file.
		 */
		interface i18n
		{
			String TAB_TITLE = "Syntax";
			String TAB_HINT = "Syntax Highlighting";
         String NETBEANS = "Use Netbeans editor (recommended)";
			String OSTER = "Use Ostermiller editor";
         String PLAIN = "Use plain editor";
      }

      private final JRadioButton _netbeansActiveOpt  = new JRadioButton(i18n.NETBEANS);
		private final JRadioButton _osterActiveOpt = new JRadioButton(i18n.OSTER);
      private final JRadioButton _plainActiveOpt  = new JRadioButton(i18n.PLAIN);


		private StylesListSelectionListener _listLis;


		private final StylesList _stylesList = new StylesList();

		private StyleMaintenancePanel _styleMaintPnl;

		MyPanel(SyntaxPreferences prefs, SyntaxPluginResources rsrc)
		{
			super();
			createUserInterface(prefs, rsrc);
		}

		/**
		 * Component has been added to its parent so setup listeners etc.
		 */
		public void addNotify()
		{
			super.addNotify();

			if (_listLis == null)
			{
				_listLis = new StylesListSelectionListener();
				_stylesList.addListSelectionListener(_listLis);
			}
		}

		/**
		 * Component has been removed from its parent so remove listeners etc.
		 */
		public void removeNotify()
		{
			super.removeNotify();
			if (_listLis != null)
			{
				_stylesList.removeListSelectionListener(_listLis);
				_listLis = null;
			}
		}

		void loadData(SyntaxPreferences prefs)
		{
			_osterActiveOpt.setSelected(prefs.getUseOsterTextControl());
         _netbeansActiveOpt.setSelected(prefs.getUseNetbeansTextControl());
         _plainActiveOpt.setSelected(prefs.getUsePlainTextControl());

			_stylesList.loadData(prefs);
			_styleMaintPnl.setStyle(_stylesList.getSelectedSyntaxStyle());

			updateControlStatus();
		}

		void applyChanges(SyntaxPreferences prefs)
		{
         boolean oldUseNetbeansTextControl = prefs.getUseNetbeansTextControl();
         boolean oldUseOsterTextControl = prefs.getUseOsterTextControl();
         boolean oldUsePlainTextControl = prefs.getUsePlainTextControl();

         try
         {
            prefs.setUseNetbeansTextControl(_netbeansActiveOpt.isSelected());
            prefs.setUseOsterTextControl(_osterActiveOpt.isSelected());
            prefs.setUsePlainTextControl(_plainActiveOpt.isSelected());
         }
         catch (SyntaxPrefChangeNotSupportedException e)
         {
            prefs.setUseNetbeansTextControl(oldUseNetbeansTextControl);
            prefs.setUseOsterTextControl(oldUseOsterTextControl);
            prefs.setUsePlainTextControl(oldUsePlainTextControl);
         }

         prefs.setColumnStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.COLUMNS));
			prefs.setCommentStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.COMMENTS));
			prefs.setErrorStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.ERRORS));
			prefs.setFunctionStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.FUNCTIONS));
			prefs.setIdentifierStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.IDENTIFIERS));
			prefs.setLiteralStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.LITERALS));
			prefs.setOperatorStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.OPERATORS));
			prefs.setReservedWordStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.RESERVED_WORDS));
			prefs.setSeparatorStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.SEPARATORS));
			prefs.setTableStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.TABLES));
			prefs.setWhiteSpaceStyle(_stylesList.getSyntaxStyleAt(StylesList.IStylesListIndices.WHITE_SPACE));
		}

		private void updateControlStatus()
		{
			final boolean useOsterControl = _osterActiveOpt.isSelected();
         final boolean useNetbeansControl = _netbeansActiveOpt.isSelected();
         final boolean usePlainControl = _plainActiveOpt.isSelected();

			_stylesList.setEnabled(useOsterControl || useNetbeansControl);
			_styleMaintPnl.setEnabled(useOsterControl || useNetbeansControl);
		}

		private void createUserInterface(SyntaxPreferences prefs,
											SyntaxPluginResources rsrc)
		{
			setLayout(new GridBagLayout());
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

         ButtonGroup bg = new ButtonGroup();
         bg.add(_netbeansActiveOpt);
         bg.add(_osterActiveOpt);
         bg.add(_plainActiveOpt);

			_osterActiveOpt.addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent evt)
				{
					updateControlStatus();
				}
			});

         _netbeansActiveOpt.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent evt)
            {
               updateControlStatus();
            }
         });

         _plainActiveOpt.addChangeListener(new ChangeListener()
         {
            public void stateChanged(ChangeEvent evt)
            {
               updateControlStatus();
            }
         });

         JPanel pnlLeft = new JPanel(new BorderLayout(4,0));

         String[] text =
            {
            "Note:",
            "The preferable editor is the",
            "Netbeans editor.",
            "The Netbeans editor",
            "- is less memory consuming,",
            "- its highlightning is more exact,",
            "- can handle many lines well.",
            "The Oster editor is still there",
            "because it can handle",
            "very long lines better than the",
            "Netbeans editor.",
            "This is due to a known bug in",
            "the Netbeans editor (Issue #41241).",
            "As soon as this bug is fixed",
            "the Oster editor will be removed."
            };

         JPanel multiLineLabel = new JPanel(new GridLayout(text.length + 1,1));

         for (int i = 0; i < text.length; i++)
         {
            multiLineLabel.add(new JLabel(text[i]));
         }
         multiLineLabel.add(new JLabel());
         pnlLeft.add(multiLineLabel);


         JPanel pnlOpt = new JPanel(new GridLayout(3,1,4,0));
         pnlOpt.add(_netbeansActiveOpt);
         pnlOpt.add(_osterActiveOpt);
         pnlOpt.add(_plainActiveOpt);
         pnlLeft.add(pnlOpt, BorderLayout.SOUTH);


			gbc.gridx = 0;
			gbc.gridy = 0;
			add(pnlLeft, gbc);

         gbc.gridy = 0;
			++gbc.gridx;
			add(createStylePanel(rsrc), gbc);
		}


		private JPanel createStylePanel(SyntaxPluginResources rsrc)
		{
			JPanel pnl = new JPanel(new BorderLayout());
			pnl.setBorder(BorderFactory.createTitledBorder("Syntax Styles"));

			_styleMaintPnl = new StyleMaintenancePanel(_stylesList, rsrc);

			pnl.add(_styleMaintPnl, BorderLayout.NORTH);
			pnl.add(_stylesList, BorderLayout.CENTER);

			return pnl;
		}


		/**
		 * Selection listener for the Styles List. As selection changes in the
		 * list then update the maintenance panel to reflect the current
		 * selected style.
		 */
		private class StylesListSelectionListener implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				_styleMaintPnl.setStyle(((StylesList)evt.getSource()).getSelectedSyntaxStyle());
			}
		}
	}
}
