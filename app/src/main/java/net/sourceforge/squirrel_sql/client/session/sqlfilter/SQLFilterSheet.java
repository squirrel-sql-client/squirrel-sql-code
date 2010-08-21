package net.sourceforge.squirrel_sql.client.session.sqlfilter;
/*
 * Copyright (C) 2003-2004 Maury Hammel
 * mjhammel@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
 *
 * Adapted from SessionPropertiesSheet.java by Colin Bell.
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

import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table.ContentsTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.List;
/**
 * SQLFilter dialog gui.
 * JASON: Rename to SQLFilterInternalFrame
 *
 * @author <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 */
public class SQLFilterSheet extends SessionDialogWidget
{
    private static final long serialVersionUID = 1L;

    /** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(SQLFilterSheet.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLFilterSheet.class);
    
    private static final String TITLE = 
        s_stringMgr.getString("SQLFilterSheet.title");
    
	/** The object tree we are filtering. */
	private final IObjectTreeAPI _objectTree;

	/** A reference to a class containing information about the database metadata. */
	transient private final IDatabaseObjectInfo _objectInfo;

	/** A list of panels that make up this sheet. */
	private List<ISQLFilterPanel> _panels = new ArrayList<ISQLFilterPanel>();

	/** A variable that contains a value that indicates which tab currently has focus. */
	private int _tabSelected;

	/** Frame title. */
	private JLabel _titleLbl = new JLabel();

	/** A button used to trigger the clearing of SQL Filter information. */
	private JButton _clearFilter = new JButton();

	/** A reference to a panel for the SQL Where Clause. */
	transient private WhereClausePanel _whereClausePanel = null;

	/** A reference to a panel for the SQL Order By Clause. */
	transient private OrderByClausePanel _orderByClausePanel = null;

	/**
	 * Creates a new instance of SQLFilterSheet
	 *
	 * @param	objectTree
	 * @param	objectInfo	The object we are filtering within the object
	 *						tree.
	 */
	public SQLFilterSheet(IObjectTreeAPI objectTree,
							IDatabaseObjectInfo objectInfo)
	{
		super(TITLE, true, objectTree.getSession());
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
		_objectTree = objectTree;
		_objectInfo = objectInfo;

		createGUI();
	}

	/**
	 * Position and display the sheet.
	 *
	 * @param	show	A boolean that determines whether the sheet is shown
	 * 					or hidden.
	 */
	public synchronized void setVisible(boolean show)
	{
		boolean reallyShow = true;

		if (show)
		{
			if (!isVisible())
			{
				ContentsTab tab =
					(ContentsTab)_objectTree.getTabbedPaneIfSelected(
											_objectInfo.getDatabaseObjectType(),
											ContentsTab.getContentsTabTitle());

            if (tab == null)
				{
					reallyShow = false;
                    // i18n[SQLFilterSheet.contentsMsg=You must have the Contents Tab selected to activate the SQL Filter]
                    String msg =
                        s_stringMgr.getString("SQLFilterSheet.contentsMsg");
					_objectTree.getSession().showMessage(msg);
				}
				else
				{
					final boolean isDebug = s_log.isDebugEnabled();
					long start = 0;
					for (Iterator<ISQLFilterPanel> it = _panels.iterator(); it.hasNext();)
					{
						ISQLFilterPanel pnl = it.next();
						if (isDebug)
						{
							start = System.currentTimeMillis();
						}

						pnl.initialize(tab.getSQLFilterClauses());
						if (isDebug)
						{
							s_log.debug("Panel " + pnl.getTitle()
									+ " initialized in "
									+ (System.currentTimeMillis() - start) + "ms");
						}
					}
					pack();
					/*
					 * TODO: Find out why
					 * KLUDGE: For some reason, I am not able to get the sheet to
					 * size correctly. It always displays with a size that causes
					 * the sub-panels to have their scrollbars showing. Add a bit
					 * of an increase in the size of the panel so the scrollbars
					 * are not displayed.
					 */
					Dimension d = getSize();
					d.width += 5;
					d.height += 5;
					setSize(d);
					/*
					 * END-KLUDGE
					 */
					DialogWidget.centerWithinDesktop(this);
					moveToFront();
				}
			}
		}

		if (!show || reallyShow)
		{
			super.setVisible(show);
		}
	}

	/**
	 * Set title of this frame. Ensure that the title label matches the frame title.
	 *
	 * @param	title	New title text.
	 */
	public void setTitle(String title)
	{
      if(null != _titleLbl)
      {
         // this method is called from the super class's constructor
         // therfore _titleLbl is null for that call. 
		   _titleLbl.setText(title + ": " + _objectInfo.getSimpleName());
      }
	}

	/**
	 * Dispose of the sheet.
	 */
	private void performClose()
	{
		dispose();
	}

	public IDatabaseObjectInfo getDatabaseObjectInfo()
	{
		return _objectInfo;
	}

	public IObjectTreeAPI getObjectTree()
	{
		return _objectTree;
	}

	/**
	 * OK button pressed. Edit data and if ok save to aliases model and
	 * then close dialog.
	 */
	private void performOk()
	{
		final boolean isDebug = s_log.isDebugEnabled();
		long start = 0;
		for (Iterator<ISQLFilterPanel> it = _panels.iterator(); it.hasNext();)
		{
			ISQLFilterPanel pnl = it.next();
			if (isDebug)
			{
				start = System.currentTimeMillis();
			}
			pnl.applyChanges();
			if (isDebug)
			{
				s_log.debug("Panel " + pnl.getTitle() + " applied changes in "
						+ (System.currentTimeMillis() - start) + "ms");
			}
		}
		try
		{
			ContentsTab cTab =
				(ContentsTab)_objectTree.getTabbedPaneIfSelected(
											_objectInfo.getDatabaseObjectType(),
											 ContentsTab.getContentsTabTitle());
         if (cTab != null)
			{
				cTab.refreshComponent();
			}
		}
		catch (DataSetException ex)
		{
			getSession().showErrorMessage(ex);
		}

		dispose();
	}

	/**
	 * Create the GUI elements for the sheet.
	 */
	private void createGUI()
	{
		SortedSet<String> columnNames = new TreeSet<String>();
		Map<String, Boolean> textColumns = new TreeMap<String, Boolean>();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(getTitle());

		// This is a tool window.
		makeToolWindow(true);

		try
		{
			ISQLConnection sqlConnection = getSession().getSQLConnection();
            SQLDatabaseMetaData md = sqlConnection.getSQLMetaData();
            TableColumnInfo[] infos = md.getColumnInfo((ITableInfo)_objectInfo);
            for (int i = 0; i < infos.length; i++) {
                String columnName = infos[i].getColumnName();
                int dataType = infos[i].getDataType();
                columnNames.add(columnName);
                if ((dataType == Types.CHAR)
                        || (dataType == Types.CLOB)
                        || (dataType == Types.LONGVARCHAR)
                        || (dataType == Types.VARCHAR))
                {
                    textColumns.put(columnName, Boolean.TRUE);
                }
                
            }
		}
		catch (SQLException ex)
		{
            // i18n[SQLFilterSheet.error.columnList=Unable to get list of columns {0}]
            String msg = 
                s_stringMgr.getString("SQLFilterSheet.error.columnList",
                                      ex);
			getSession().getApplication().showErrorDialog(msg);
		}

		_whereClausePanel =
		    new WhereClausePanel(columnNames, textColumns, _objectInfo.getQualifiedName());
		_orderByClausePanel =
			new OrderByClausePanel(columnNames, _objectInfo.getQualifiedName());
		_panels.add(_whereClausePanel);
		_panels.add(_orderByClausePanel);

		JTabbedPane tabPane = UIFactory.getInstance().createTabbedPane();
		for (Iterator<ISQLFilterPanel> it = _panels.iterator(); it.hasNext();)
		{
			ISQLFilterPanel pnl = it.next();
			String pnlTitle = pnl.getTitle();
			String hint = pnl.getHint();
			tabPane.addTab(pnlTitle, null, pnl.getPanelComponent(), hint);
		}

		tabPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				setButtonLabel(
					((JTabbedPane)event.getSource()).getSelectedIndex());
			}
		});

		final JPanel contentPane = new JPanel(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 1;

		gbc.gridx = 0;
		gbc.gridy = 0;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		contentPane.add(_titleLbl, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = GridBagConstraints.REMAINDER;
		setButtonLabel(0);
		_tabSelected = 0;
		_clearFilter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				clearFilter();
			}
		});
		contentPane.add(_clearFilter);

		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.weighty = 1;
		contentPane.add(tabPane, gbc);

		++gbc.gridy;
		gbc.gridwidth = 2;
		gbc.weighty = 0;
		contentPane.add(createButtonsPanel(), gbc);


      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            performClose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);
      
	}

	/**
	 * Clear out the SQL Filter information for the appropriate tab.
	 */
	private void clearFilter()
	{
		if (_tabSelected == 0)
		{
			_whereClausePanel.clearFilter();
		}
		else
		{
			_orderByClausePanel.clearFilter();
		}
	}

	/**
	 * Create a panel that contains the buttons that control the closing
	 * of the sheet.
	 *
	 * @return An instance of a JPanel.
	 */
	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();
        // i18n[SQLFilterSheet.okButtonLabel=OK]
		String okLabel = s_stringMgr.getString("SQLFilterSheet.okButtonLabel");
		JButton okBtn = new JButton(okLabel);
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
        // i18n[SQLFilterSheet.closeButtonLabel=Close]
        String closeLabel = 
            s_stringMgr.getString("SQLFilterSheet.closeButtonLabel");
		JButton closeBtn = new JButton(closeLabel);
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	/**
	 * Change the text of the 'clear' button depending on which
	 * clause panel has focus.
	 *
	 * @param	tabSelected	An integer indicating which panel has focus
	 */
	private void setButtonLabel(int tabSelected)
	{
        String title = null;
        if (tabSelected == 0) {
            title = _whereClausePanel.getTitle();
        } else {
            title = _orderByClausePanel.getTitle();
        }
        // i18n[SQLFilterSheet.clearButtonLabel=Clear {0}]
        String label = 
            s_stringMgr.getString("SQLFilterSheet.clearButtonLabel", title);
        _clearFilter.setText(label);
        _tabSelected = tabSelected;
	}

   public static SQLFilterSheet createSheet(IObjectTreeAPI objectTree, IDatabaseObjectInfo objectInfo)
   {
      return null;  //To change body of created methods use File | Settings | File Templates.
   }
}
