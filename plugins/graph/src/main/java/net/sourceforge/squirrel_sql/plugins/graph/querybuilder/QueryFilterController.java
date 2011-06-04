package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.plugins.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.prefs.Preferences;

public class QueryFilterController
{
   private QueryFilterDlg _queryFilterDlg;
   private String _tableName;
   private ColumnInfo _columnInfo;
   private ISession _session;
   private QueryFilterListener _queryFilterListener;

   private static final String PREF_KEY_QUERY_ALWAYS_APPEND_QUOTES = "Squirrel.queryFilterDlgCtrl.AlwaysAppendQuotes";
   private SessionAdapter _sessionAdapter;


   public QueryFilterController(Window parent, String tableName, ColumnInfo columnInfo, GraphPlugin graphPlugin, ISession session, QueryFilterListener queryFilterListener)
   {
      _tableName = tableName;
      _columnInfo = columnInfo;
      _session = session;

      _sessionAdapter = new SessionAdapter()
      {
         @Override
         public void sessionClosing(SessionEvent evt)
         {
            onSessionClosing(evt);
         }
      };


      _session.getApplication().getSessionManager().addSessionListener(_sessionAdapter);

      _queryFilterListener = queryFilterListener;
      _queryFilterDlg = new QueryFilterDlg(parent, new GraphPluginResources(graphPlugin), tableName + "." + _columnInfo.toString());


      boolean applyQuotes = Preferences.userRoot().getBoolean(PREF_KEY_QUERY_ALWAYS_APPEND_QUOTES, false);
      _queryFilterDlg._chkApplyQuotes.setSelected(applyQuotes);


      _queryFilterDlg._btnOk.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onOK();
         }
      });

      _queryFilterDlg._btnClearFilter.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onClearFilter();
         }
      });

      _queryFilterDlg._btnCancel.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            close();
         }
      });

      _queryFilterDlg._cboOperator.setModel(new DefaultComboBoxModel(QueryFilterOperators.values()));

      _queryFilterDlg._cboOperator.addItemListener(new ItemListener()
      {
         @Override
         public void itemStateChanged(ItemEvent e)
         {
            onOperatorChanged(e);
         }
      });

      _queryFilterDlg._btnEscapeDate.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onEscapeDate();
         }
      });



      if (null == columnInfo.getQueryData().getOperator())
      {
         _queryFilterDlg._cboOperator.setSelectedItem(QueryFilterOperators.EQUAL);
      }
      else
      {
         _queryFilterDlg._cboOperator.setSelectedItem(columnInfo.getQueryData().getOperator());
      }



      _queryFilterDlg._txtFilter.setText(_columnInfo.getQueryData().getFilterValue());

      GUIUtils.centerWithinParent(_queryFilterDlg);
      _queryFilterDlg.setVisible(true);
      _queryFilterDlg._txtFilter.requestFocus();
   }

   private void onEscapeDate()
   {
      String dateEscape = EditExtrasAccessor.getDateEscape(_queryFilterDlg, _session);

      if (null != dateEscape && 0 < dateEscape.trim().length())
      {
         _queryFilterDlg._txtFilter.setText(dateEscape);
      }
   }

   private void onSessionClosing(SessionEvent evt)
   {
      if (evt.getSession().getIdentifier().equals(_session.getIdentifier()))
      {
         close();
      }

      // To prevent memory leaks
      _session.getApplication().getSessionManager().removeSessionListener(_sessionAdapter);
   }

   private void onOperatorChanged(ItemEvent e)
   {
      if(ItemEvent.DESELECTED == e.getStateChange())
      {
         return;
      }

      QueryFilterOperators selectedOp = (QueryFilterOperators) _queryFilterDlg._cboOperator.getSelectedItem();
      if(QueryFilterOperators.isNoArgOperator(selectedOp))
      {
         _queryFilterDlg._txtFilter.setEnabled(false);
         _queryFilterDlg._btnEscapeDate.setEnabled(false);
      }
      else
      {
         _queryFilterDlg._txtFilter.setEnabled(true);
         _queryFilterDlg._btnEscapeDate.setEnabled(true);
      }

   }

   private void onClearFilter()
   {
      _columnInfo.getQueryData().clearFilter();
      _queryFilterListener.filterChanged();

      _queryFilterDlg.saveCurrentSize();

      close();
      _columnInfo.getColumnInfoModelEventDispatcher().fireChanged(TableFramesModelChangeType.COLUMN_WHERE);

   }

   private void onOK()
   {

      _columnInfo.getQueryData().setOperator((QueryFilterOperators) _queryFilterDlg._cboOperator.getSelectedItem());

      _columnInfo.getQueryData().setFilterValue(null);
      String text = _queryFilterDlg._txtFilter.getText();
      if (null != text && 0 < text.length())
      {
         ExtendedColumnInfo ec = getExtendedColumnInfo();

         if (_queryFilterDlg._chkApplyQuotes.isSelected() && null != ec && ec.isCharacterType())
         {
            _columnInfo.getQueryData().setFilterValue(getQuotedText(text));
         }
         else
         {
            _columnInfo.getQueryData().setFilterValue(text);
         }
      }

      _queryFilterListener.filterChanged();
      _queryFilterDlg.saveCurrentSize();

      Preferences.userRoot().putBoolean(PREF_KEY_QUERY_ALWAYS_APPEND_QUOTES, _queryFilterDlg._chkApplyQuotes.isSelected());
      close();

      _columnInfo.getColumnInfoModelEventDispatcher().fireChanged(TableFramesModelChangeType.COLUMN_WHERE);
   }

   private String getQuotedText(String text)
   {
      if(false == text.trim().startsWith("'") || false == text.trim().endsWith("'"))
      {
         return  "'" + text.trim().replaceAll("'", "''") + "'";
      }
      return text;
   }

   private ExtendedColumnInfo getExtendedColumnInfo()
   {
      ExtendedColumnInfo[] extendedColumnInfos = _session.getSchemaInfo().getExtendedColumnInfos(_tableName);
      for (ExtendedColumnInfo extendedColumnInfo : extendedColumnInfos)
      {
         if (extendedColumnInfo.getColumnName().equalsIgnoreCase(_columnInfo.getName()))
         {
            return extendedColumnInfo;
         }
      }
      return null;
   }


   private void close()
   {
      _queryFilterDlg.setVisible(false);
      _queryFilterDlg.dispose();

      // To prevent memory leaks
      _session.getApplication().getSessionManager().addSessionListener(_sessionAdapter);
   }
}
