package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2004 Johan Compagner
 * jcompagner@j-com.nl
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.client.session.action.ReturnResultTabAction;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.ReRunChooserCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum.RowColAndSumController;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetMetaDataDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableState;
import net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates.MarkDuplicatesChooserController;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * JASON: Rename to ResultInternalFrame
 * Torn off frame that contains SQL results.
 *
 * @author <A HREF="mailto:jcompagner@j-com.nl">Johan Compagner</A>
 */
public class ResultFrame extends SessionDialogWidget
{
	private static ILogger s_log = LoggerController.createLogger(ResultFrame.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultFrame.class);

   private ISession _session;
   /** SQL Results. */
	private IResultTab _resultTab;
   private ResultTabFactory _resultTabFactory;
   private ResultFrameListener _resultFrameListener;
   private JButton _btnReturnToTab;
   private TabButton _btnToggleFind;
   private TabButton _btnFindColumn;
   private MarkDuplicatesChooserController _markDuplicatesChooserController;
   private JCheckBox _chkOnTop;
   private JPanel _centerPanel;
   private RowColAndSumController _rowColAndSumController = new RowColAndSumController();
   private final ResultTabListener _originalResultTabListener;

   private ReRunChooserCtrl _reRunChooserCtrl;

   public ResultFrame(final ISession session, IResultTab resultTab, ResultTabFactory resultTabFactory, ResultFrameListener resultFrameListener, boolean checkStayOnTop, boolean isOnRerun)
   {
      super(getFrameTitle(session, resultTab), true, true, true, true, session);
      _session = session;
      _resultTab = resultTab;
      _originalResultTabListener = _resultTab.replaceResultTabListener((sql, resultTabToReplace) -> onRerun());
      _resultTabFactory = resultTabFactory;
      _resultFrameListener = resultFrameListener;
      _reRunChooserCtrl = new ReRunChooserCtrl(_resultTab);

      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      getContentPane().setLayout(new BorderLayout());
      final IApplication app = session.getApplication();


      getContentPane().add(createTopPanel(session, app, checkStayOnTop), BorderLayout.NORTH);
      _centerPanel = new JPanel(new GridLayout(1,1));
      getContentPane().add(_centerPanel, BorderLayout.CENTER);
      _centerPanel.add(_resultTab.getTabbedPaneOfResultTabs());
      _resultTab.setParentWindow(getParentWindow());

      _chkOnTop.addActionListener(e -> onStayOnTopChanged());

      _btnToggleFind.addActionListener(e -> onFind());

      _btnFindColumn.addActionListener(e -> onFindColumn());

      if (false == isOnRerun)
      {
         showFrame(this, false);
      }
   }

   private void onFind()
   {
      _resultTab.toggleShowFindPanel();
   }

   private void onFindColumn()
   {
      _resultTab.findColumn();
   }

   private void onRerun()
   {
      _btnReturnToTab.setEnabled(false);
      //_reRunChooserCtrl.setEnabled(false);
      _centerPanel.removeAll();
      new SQLExecutionHandler(_resultTab, _session, _resultTab.getSqlString(), createSQLExecutionHandlerListener(), new ISQLExecutionListener[0]);
   }

   private ISQLExecutionHandlerListener createSQLExecutionHandlerListener()
   {
      return new ISQLExecutionHandlerListener()
      {
         @Override
         public void addResultsTab(SQLExecutionInfo info, ResultSetDataSet rsds, ResultSetMetaDataDataSet rsmdds, IDataSetUpdateableTableModel creator, IResultTab resultTabToReplace)
         {
            onAddResultsTab(info, rsds, rsmdds, creator, resultTabToReplace);
         }

         @Override
         public void removeCancelPanel(CancelPanelCtrl cancelPanelCtrl, IResultTab resultTabToReplace)
         {
            SwingUtilities.invokeLater(() -> onRemoveCancelPanel(cancelPanelCtrl, resultTabToReplace));
         }

         @Override
         public void setCancelPanel(CancelPanelCtrl cancelPanelCtrl)
         {
            onSetCancelPanel(cancelPanelCtrl);
         }

         @Override
         public void displayErrors(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
         {
            onDisplayErrors(sqlExecErrorMsgs, lastExecutedStatement);
         }
      };
   }

   private void onDisplayErrors(final ArrayList<String> sqlExecErrorMsgs, final String lastExecutedStatement)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _centerPanel.removeAll();
            ErrorPanel errorPanel = _resultTabFactory.createErrorPanel(sqlExecErrorMsgs, lastExecutedStatement);
            errorPanel.hideCloseButton();
            _centerPanel.add(errorPanel);
            //_reRunChooserCtrl.setEnabled(true);
         }
      });
   }

   private void onSetCancelPanel(final CancelPanelCtrl cancelPanelCtrl)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _centerPanel.removeAll();
            _centerPanel.add(cancelPanelCtrl.getPanel());
         }
      });
   }

   private void onRemoveCancelPanel(final CancelPanelCtrl cancelPanelCtrl, IResultTab resultTabToReplace)
   {
      _centerPanel.removeAll();
      cancelPanelCtrl.wasRemoved();
   }

   private void onAddResultsTab(final SQLExecutionInfo info, final ResultSetDataSet rsds, final ResultSetMetaDataDataSet rsmdds, final IDataSetUpdateableTableModel creator, IResultTab resultTabToReplace)
   {
      // We start a new frame here because reusing the current one for the new result led to repaint problems
      SwingUtilities.invokeLater(() -> showRerunResult(info, creator, rsds, rsmdds));
   }

   private void showRerunResult(SQLExecutionInfo info, IDataSetUpdateableTableModel creator, ResultSetDataSet rsds, ResultSetMetaDataDataSet rsmdds)
   {
      try
      {
         _centerPanel.removeAll();

         TableState tableState = null;

         if(null != _resultTab)
         {
            tableState = _resultTab.getResultSortableTableState();
         }

         ResultTab newResultTab = _resultTabFactory.createResultTab(info, creator, rsds, rsmdds);
         _resultTab.aboutToBeReplacedBy(newResultTab, _reRunChooserCtrl);

         _resultTab = newResultTab;
         _resultTab.replaceResultTabListener((sql, resultTabToReplace) -> onRerun());

         JTabbedPane tabbedPaneOfResultTabs = _resultTab.getTabbedPaneOfResultTabs();
         GUIUtils.unconventionallyAddToParentWithRepaint(_centerPanel, tabbedPaneOfResultTabs);
         _markDuplicatesChooserController.init(_resultTab);
         _reRunChooserCtrl.setResultTab((ResultTab) _resultTab);

         if(null != tableState)
         {
            _resultTab.applyResultSortableTableState(tableState);
         }

         _btnReturnToTab.setEnabled(true);
         //_reRunChooserCtrl.setEnabled(true);
      }
      catch (Throwable t)
      {
         Main.getApplication().getMessageHandler().showErrorMessage(t);
      }
   }

   private void showFrame(ResultFrame frame, boolean isOnRerun)
   {
      Main.getApplication().getMainFrame().addWidget(frame);
      if (isOnRerun)
      {
         frame.setBounds(getBounds());
      }
      else
      {
         frame.pack();
         DialogWidget.centerWithinDesktop(frame);
      }

      frame.setVisible(true);
      frame.toFront();
      frame.requestFocus();
   }

   private JPanel createTopPanel(ISession session, IApplication app, boolean checkStayOnTop)
   {
      JPanel ret = new JPanel(new BorderLayout(15, 0));

      JPanel pnlButtons = createLeftButtonsPanel(session, app, checkStayOnTop);
      ret.add(pnlButtons, BorderLayout.WEST);

      ret.add(new JPanel(), BorderLayout.CENTER);

      ret.add(createRightUpperPanel(), BorderLayout.EAST);
      return ret;
   }

   private JPanel createLeftButtonsPanel(ISession session, IApplication app, boolean checkStayOnTop)
   {
      JPanel pnlButtons = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      _btnReturnToTab = new JButton(new ReturnResultTabAction(app, this));
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      pnlButtons.add(_btnReturnToTab, gbc);

      _chkOnTop = new JCheckBox(s_stringMgr.getString("resultFrame.stayOnTop"));
      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      pnlButtons.add(_chkOnTop, gbc);
      _chkOnTop.setSelected(checkStayOnTop);
      initLayer();

      _chkOnTop.setVisible(session.getApplication().getDesktopStyle().supportsLayers());
      return pnlButtons;
   }

   private JPanel createRightUpperPanel()
   {
      JPanel ret = new JPanel(new BorderLayout(15,0));

      ret.add(createRightButtonsPanel(), BorderLayout.EAST);

      _rowColAndSumController.setDataSetViewer(_resultTab.getSQLResultDataSetViewer());

      ret.add(_rowColAndSumController.getPanel(), BorderLayout.CENTER);
      _rowColAndSumController.setRowColSumLayoutListener(() -> updateRightUpperPanelLayout(ret));
      updateRightUpperPanelLayout(ret);

      return ret;
   }

   public void updateRightUpperPanelLayout(JPanel panel)
   {
      panel.revalidate();
      if (null != panel.getParent())
      {
         panel.getParent().revalidate();
      }
   }


   private JPanel createRightButtonsPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,4));
      ret.add(_reRunChooserCtrl.getComponent());

      _markDuplicatesChooserController = new MarkDuplicatesChooserController(_resultTab);
      _markDuplicatesChooserController.copyStateFrom(_resultTab.getMarkDuplicatesChooserController());
      ret.add(_markDuplicatesChooserController.getComponent());

      ImageIcon iconFindColumn = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND_COLUMN);
      _btnFindColumn = new TabButton(iconFindColumn);
      ret.add(_btnFindColumn);

      ImageIcon iconFind = Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND);
      _btnToggleFind = new TabButton(iconFind);
      ret.add(_btnToggleFind);

      return ret;
   }

   private void onStayOnTopChanged()
   {
      initLayer();
      toFront();
   }

   private void initLayer()
   {
      if(_chkOnTop.isSelected())
      {
         setLayer(JLayeredPane.PALETTE_LAYER.intValue());
      }
      else
      {
         setLayer(JLayeredPane.DEFAULT_LAYER.intValue());
      }
   }

   /**
	 * Close this window.
	 */
	public void dispose()
	{
		if (_resultTab != null)
		{
			_resultTab.disposeTab();
			_resultTab = null;
		}
		super.dispose();
	}

	public void returnToTabbedPane()
	{
		s_log.debug("ResultFrame.returnToTabbedPane()");
		getContentPane().remove(_resultTab.getTabbedPaneOfResultTabs());
      _resultTab.replaceResultTabListener(_originalResultTabListener);
		_resultTab.returnToTabbedPane();
		_resultTab = null;
		dispose();
	}

	private static String getFrameTitle(ISession session, IResultTab tab)
		throws IllegalArgumentException
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		return session.getTitle() + " - " + tab.getViewableSqlString();
	}

   public IResultTab getResultTab()
   {
      return _resultTab;
   }

   public MarkDuplicatesChooserController getMarkDuplicatesChooserController()
   {
      return _markDuplicatesChooserController;
   }
}
