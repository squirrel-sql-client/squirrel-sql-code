package net.sourceforge.squirrel_sql.fw.gui.action.makeeditable;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTab;
import net.sourceforge.squirrel_sql.client.session.properties.DataSetViewerType;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultMetaDataTable;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.action.ShowReferencesUtil;
import net.sourceforge.squirrel_sql.fw.gui.action.UndoMakeEditableCommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MakeEditableToolbarCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MakeEditableToolbarCtrl.class);


   private final ResultTab _resultTab;
   private final ISession _session;
   private JToggleButton _button;

   public MakeEditableToolbarCtrl(ResultTab resultTab, ISession session)
   {
      _resultTab = resultTab;
      _session = session;
   }

   public JToggleButton getTabButton()
   {

      if (null == _button)
      {
         if(_resultTab.allowsEditing())
         {
            if (isSqlResultTableEditableBySessionProperties() && false == isTextOutput(_resultTab))
            {
               _button = GUIUtils.styleAsTabButton(new JToggleButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT)));
               _button.setSelected(true);
            }
            else
            {
               _button = GUIUtils.styleAsTabButton(new JToggleButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT)));
            }

            if (false == isTextOutput(_resultTab))
            {
               _resultTab.getSQLResultDataSetViewer().getUpdateableModelReference().addListener(mode -> _button.setSelected(mode));
            }
         }
         else
         {
            _button = GUIUtils.styleAsTabButton(new JToggleButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT_RED_DOT)));
         }

         _button.addActionListener(e -> onButtonClicked());

         _button.setToolTipText(s_stringMgr.getString("MakeEditableToolbarCtrl.toggle.editable.tooltip"));
      }

      return _button;
   }

   private boolean isSqlResultTableEditableBySessionProperties()
   {
      return DataSetViewerType.EDITABLE_TABLE.getDataSetViewerClass().getName().equals(_session.getProperties().getSQLResultsOutputClassName());
   }

   private void onButtonClicked()
   {
      try
      {
         if(isTextOutput(_resultTab))
         {
            _button.setSelected(false);
            Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("MakeEditableToolbarCtrl.text.output"));
            return;
         }


         _resultTab.selectResultTab();

         if(false == _resultTab.allowsEditing())
         {
            List<ResultMetaDataTable> tables = new ArrayList<>();

            if (_resultTab.getSQLResultDataSetViewer().getComponent() instanceof JTable)
            {
               tables = ShowReferencesUtil.findTables((JTable) _resultTab.getSQLResultDataSetViewer().getComponent(), _session, true);
            }

            if (false == tables.isEmpty())
            {
               JOptionPane.showMessageDialog(GUIUtils.getOwningWindow(_button), s_stringMgr.getString("MakeEditableToolbarCtrl.uneditable.use.show.refs"));
            }
            else
            {
               JOptionPane.showMessageDialog(GUIUtils.getOwningWindow(_button), s_stringMgr.getString("MakeEditableToolbarCtrl.uneditable"));
            }

            _button.setSelected(false);
         }
         else
         {
            if (_button.isSelected())
            {
               new MakeEditableCommand(_resultTab.getSQLResultDataSetViewer().getUpdateableModelReference()).execute();
               if (_session.getSQLConnection().getAutoCommit())
               {
                  Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("MakeEditableToolbarCtrl.was.made.editable.autocommit.true"));
               }
               else
               {
                  Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("MakeEditableToolbarCtrl.was.made.editable.autocommit.false"));
               }
            }
            else
            {
               if (isSqlResultTableEditableBySessionProperties())
               {
                  JOptionPane.showMessageDialog(GUIUtils.getOwningWindow(_button), s_stringMgr.getString("MakeEditableToolbarCtrl.cannot.make.uneditable.see.session.properties"));
                  _button.setSelected(true);
               }
               else
               {
                  new UndoMakeEditableCommand(_resultTab.getSQLResultDataSetViewer().getUpdateableModelReference()).execute();
                  Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("MakeEditableToolbarCtrl.was.made.uneditable"));
               }
            }
         }
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private boolean isTextOutput(ResultTab resultTab)
   {
      return DataSetViewerType.TEXT.getDataSetViewerClass().equals(resultTab.getSQLResultDataSetViewer().getClass());
   }
}
