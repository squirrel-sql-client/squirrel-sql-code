package net.sourceforge.squirrel_sql.client.gui.db.recentalias;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasWindowManager;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.FindAliasListCellRenderer;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.AliasSearchWrapper;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.AliasesUtil;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAction;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class RecentAliasesListCtrl
{
   private static ILogger s_log = LoggerController.createLogger(RecentAliasesListCtrl.class);
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RecentAliasesListCtrl.class);

   private final RecentAliasesListDockWidget _widget;

   public RecentAliasesListCtrl()
   {
      _widget = new RecentAliasesListDockWidget();
      _widget.lstAliases.setModel(new DefaultListModel<>());
      _widget.lstAliases.setCellRenderer(new FindAliasListCellRenderer());
      _widget.lstAliases.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


      File jsonBeanFile = new ApplicationFiles().getRecentAliasesJsonBeanFile();

      RecentAliasesJsonBean jsonBean = new RecentAliasesJsonBean();
      if(jsonBeanFile.exists())
      {
         try
         {
            jsonBean = JsonMarshalUtil.readObjectFromFile(jsonBeanFile, RecentAliasesJsonBean.class);
         }
         catch (Exception e)
         {
            s_log.error("Failed to read RecentAliasesJsonBean", e);
         }
      }

      _widget.txtMaxNumRecent.setInt(jsonBean.getMaxRecentAliases());

      DefaultListModel<AliasSearchWrapper> model = (DefaultListModel<AliasSearchWrapper>) _widget.lstAliases.getModel();

      for (String recentAliasIdentifier : jsonBean.getRecentAliasesIdentifier())
      {
         SQLAlias alias = findAlias(recentAliasIdentifier);
         if(null != alias)
         {
            model.addElement(new AliasSearchWrapper(alias));
         }
      }
      _widget.lstAliases.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }
      });


      _widget.btnClearList.addActionListener(e -> onClearList(model));

      _widget.btnRemoveSelected.addActionListener(e -> onRemoveSelected());

      _widget.toolBar.add(proxy(ConnectToAliasAction.class, e -> onConnect()));
      _widget.toolBar.add(proxy(ModifyAliasAction.class, e -> onModifyAlias()));
      _widget.toolBar.add(proxy(AliasPropertiesAction.class, e -> onAliasProperties()));
      _widget.toolBar.add(proxy(FindAliasAction.class, e -> onFindAlias()));
      _widget.toolBar.add(proxy(ViewInAliasesAction.class, e -> onViewInAliases()));
   }

   private RecentAliasListActionProxy proxy(Class actionClass, ActionListener actionListener)
   {
      return new RecentAliasListActionProxy(Main.getApplication().getActionCollection().get(actionClass), actionListener);
   }


   private void onViewInAliases()
   {
      SQLAlias selectedAlias = getSelectedAliasChecked();
      if (selectedAlias == null)
      {
         return;
      }

      AliasesUtil.viewInAliasesDockWidget(selectedAlias);
   }

   private void onFindAlias()
   {
      Main.getApplication().getActionCollection().get(FindAliasAction.class).actionPerformed(new ActionEvent(_widget.toolBar, 0, "Dummy"));
   }

   private void onAliasProperties()
   {
      SQLAlias selectedAlias = getSelectedAliasChecked();
      if (selectedAlias == null)
      {
         return;
      }

      new AliasPropertiesCommand(selectedAlias, Main.getApplication()).execute();
   }

   private void onModifyAlias()
   {
      SQLAlias selectedAlias = getSelectedAliasChecked();
      if (selectedAlias == null)
      {
         return;
      }

      AliasWindowManager.showModifyAliasInternalFrame(selectedAlias);


   }


   private void onListClicked(MouseEvent e)
   {
      if(2 == e.getClickCount())
      {
         onConnect();
      }
   }

   private void onConnect()
   {
      SQLAlias selectedAlias = getSelectedAliasChecked();
      if (selectedAlias == null)
      {
         return;
      }

      new ConnectToAliasCommand(Main.getApplication(), selectedAlias).execute();
   }

   private SQLAlias getSelectedAliasChecked()
   {
      AliasSearchWrapper selectedAliasSearchWrapper = _widget.lstAliases.getSelectedValue();

      if(selectedAliasSearchWrapper == null)
      {
         String msg = s_stringMgr.getString("RecentAliasesListCtrl.no.selected.alias");
         JOptionPane.showMessageDialog(_widget.getContentPane(), msg);
         return null;
      }

      SQLAlias orgAlias = findAlias(selectedAliasSearchWrapper.getAlias().getIdentifier().toString());

      if(null == orgAlias)
      {
         String msg = s_stringMgr.getString("RecentAliasesListCtrl.alias.doesnt.exist");
         JOptionPane.showMessageDialog(_widget.getContentPane(), msg);

         ((DefaultListModel<AliasSearchWrapper>)_widget.lstAliases.getModel()).removeElement(selectedAliasSearchWrapper);
         return null;
      }
      return (SQLAlias) selectedAliasSearchWrapper.getAlias();
   }

   private void onClearList(DefaultListModel model)
   {
      String msg = s_stringMgr.getString("RecentAliasesListCtrl.clearList.msg");

      if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(_widget.getContentPane(), msg))
      {
         model.clear();
      }
   }

   private void onRemoveSelected()
   {
      DefaultListModel<AliasSearchWrapper> model = (DefaultListModel<AliasSearchWrapper>) _widget.lstAliases.getModel();

      for (AliasSearchWrapper aliasSearchWrapper : _widget.lstAliases.getSelectedValuesList())
      {
         model.removeElement(aliasSearchWrapper);
      }
   }

   private SQLAlias findAlias(String aliasIdentifierString)
   {
      return (SQLAlias) Main.getApplication().getAliasesAndDriversManager().getAlias(new UidIdentifier(aliasIdentifierString));
   }

   public RecentAliasesListDockWidget getWidget()
   {
      return _widget;
   }

   public void startingCreateSession(SQLAlias sqlAlias)
   {
      DefaultListModel<AliasSearchWrapper> model = (DefaultListModel<AliasSearchWrapper>) _widget.lstAliases.getModel();

      final AliasSearchWrapper aliasSearchWrapper = new AliasSearchWrapper(sqlAlias);

      int curIx = model.indexOf(aliasSearchWrapper);
      while(-1 < curIx)
      {
         model.remove(curIx);
         curIx = model.indexOf(aliasSearchWrapper);
      }

      model.add(0, aliasSearchWrapper);

      while ( _widget.txtMaxNumRecent.getInt() < model.size())
      {
         model.removeElementAt(model.size() -1);
      }
   }

   public void saveRecentAliases()
   {
      RecentAliasesJsonBean jsonBean = new RecentAliasesJsonBean();

      jsonBean.setMaxRecentAliases(_widget.txtMaxNumRecent.getInt());

      DefaultListModel<AliasSearchWrapper> model = (DefaultListModel<AliasSearchWrapper>) _widget.lstAliases.getModel();

      for (Object aliasSearchWrapper : model.toArray())
      {
         final SQLAlias alias = findAlias(((AliasSearchWrapper) aliasSearchWrapper).getAlias().getIdentifier().toString());
         if(null != alias)
         {
            jsonBean.getRecentAliasesIdentifier().add(alias.getIdentifier().toString());
         }
      }

      File jsonBeanFile = new ApplicationFiles().getRecentAliasesJsonBeanFile();

      JsonMarshalUtil.writeObjectToFile(jsonBeanFile, jsonBean);

   }

   public void aliasChanged(ISQLAlias sqlAlias)
   {
      _widget.lstAliases.repaint();
   }
}
