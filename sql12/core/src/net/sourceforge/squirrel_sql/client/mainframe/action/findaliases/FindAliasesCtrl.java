package net.sourceforge.squirrel_sql.client.mainframe.action.findaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasFolder;
import net.sourceforge.squirrel_sql.client.gui.db.ConnectToAliasCallBack;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.ICompletionCallback;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.FindAliasListCellRenderer;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class FindAliasesCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindAliasesCtrl.class);

   public static final String PREF_KEY_FIND_ALIAS_SHEET_WIDTH = "Squirrel.findAliasSheet.width";
   public static final String PREF_KEY_FIND_ALIAS_SHEET_HEIGHT = "Squirrel.findAliasSheet.height";

   public static final String PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN = "Squirrel.findAliasSheet.check.leave.open";

   public static final String PREF_KEY_FIND_ALIAS_REMEMBER_LAST_SEARCH = "Squirrel.findAliasSheet.remember.last.search";

   public static final String PREF_KEY_FIND_ALIAS_INCLUDE_ALIAS_FOLDERS = "Squirrel.findAliasSheet.include.alias.folders";

   public static final String PREF_KEY_FIND_ALIAS_LAST_SEARCH_STRING = "Squirrel.findAliasSheet.last.search.string";


   private FindAliasesDialog _dlg = new FindAliasesDialog();
   private IAliasesList _aliasesList;

   public FindAliasesCtrl(final IAliasesList aliasesList)
   {
      _aliasesList = aliasesList;
      _dlg.setSize(getDimension());

      GUIUtils.centerWithinParent(_dlg);

      GUIUtils.enableCloseByEscape(_dlg, dialog -> onClosing());


      _dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClosing();
         }
      });

      _dlg.lstResult.setCellRenderer(new FindAliasListCellRenderer());

      _dlg.lstResult.setFocusable(false);

      _dlg.lstResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      _dlg.lstResult.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }
      });


      _dlg.chkLeaveOpen.setFocusable(false);

      _dlg.chkLeaveOpen.setSelected(Props.getBoolean(PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN, false));


      _dlg.btnConnect.addActionListener(e -> onConnect());

      _dlg.btnGoto.addActionListener(e -> onGoto());

      _dlg.btnClose.addActionListener(e -> onClose());



      _dlg.txtToSearch.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            updateList();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            updateList();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            updateList();
         }
      });

      _dlg.chkRememberLastSearch.setFocusable(false);
      _dlg.chkRememberLastSearch.setSelected(Props.getBoolean(PREF_KEY_FIND_ALIAS_REMEMBER_LAST_SEARCH, false));

      _dlg.chkIncludeAliasFolders.setFocusable(false);
      _dlg.chkIncludeAliasFolders.setSelected(Props.getBoolean(PREF_KEY_FIND_ALIAS_INCLUDE_ALIAS_FOLDERS, false));
      _dlg.chkIncludeAliasFolders.addActionListener(e -> updateList());

      if (_dlg.chkRememberLastSearch.isSelected())
      {
         String lastSearchString = Props.getString(PREF_KEY_FIND_ALIAS_LAST_SEARCH_STRING, null);
         _dlg.txtToSearch.setText(lastSearchString);

         if(null != lastSearchString)
         {
            _dlg.txtToSearch.selectAll();
         }
      }

      _dlg.txtToSearch.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });

      SwingUtilities.invokeLater(() -> _dlg.txtToSearch.requestFocus());



      _dlg.getRootPane().setDefaultButton(_dlg.btnConnect);


      updateList();

      _dlg.setVisible(true);

   }

   private void onClose()
   {
      onClosing();

      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private void onKeyPressed(KeyEvent e)
   {
      if(e.getKeyCode() == KeyEvent.VK_UP)
      {
         int selIx = _dlg.lstResult.getSelectedIndex();

         if(0 < selIx)
         {
            _dlg.lstResult.setSelectedIndex(selIx - 1);
            _dlg.lstResult.ensureIndexIsVisible(selIx - 1);
         }
      }
      else if(e.getKeyCode() == KeyEvent.VK_DOWN)
      {
         int selIx = _dlg.lstResult.getSelectedIndex();

         if(_dlg.lstResult.getModel().getSize() - 1 > selIx)
         {
            _dlg.lstResult.setSelectedIndex(selIx + 1);
            _dlg.lstResult.ensureIndexIsVisible(selIx + 1);
         }
      }


   }

   private void onConnect()
   {
      AliasSearchWrapper selectedWrapperAlias = _dlg.lstResult.getSelectedValue();

      if(null ==  selectedWrapperAlias)
      {
         return;
      }

      if (null != selectedWrapperAlias.getAlias())
      {
         ICompletionCallback completionCallback = null;

         if (_dlg.chkLeaveOpen.isSelected())
         {
            completionCallback = new ConnectToAliasCallBack((SQLAlias) selectedWrapperAlias.getAlias())
            {
               @Override
               public void sessionCreated(ISession session)
               {
                  _dlg.requestFocus();
                  _dlg.txtToSearch.requestFocus();
                  super.sessionCreated(session);
               }

            };
         }


         new ConnectToAliasCommand(Main.getApplication(), (SQLAlias) selectedWrapperAlias.getAlias(), true, completionCallback).execute();

         if(false == _dlg.chkLeaveOpen.isSelected())
         {
            onClosing();
            _dlg.setVisible(false);
            _dlg.dispose();
         }
      }
      else
      {
         onGoto();
      }
   }

   private void onGoto()
   {
      AliasSearchWrapper selectedAliasWrapper = _dlg.lstResult.getSelectedValue();

      if(null == selectedAliasWrapper)
      {
         return;
      }

      AliasesUtil.viewInAliasesDockWidget(selectedAliasWrapper, _aliasesList, _dlg);


      if(false == _dlg.chkLeaveOpen.isSelected())
      {
         onClosing();
         _dlg.setVisible(false);
         _dlg.dispose();
      }
      else
      {
         _dlg.txtToSearch.requestFocus();
      }
   }

   private void updateList()
   {

      String filterText = _dlg.txtToSearch.getText();

      java.lang.Object formerSelectedValue = _dlg.lstResult.getSelectedValue();

      Vector<AliasSearchWrapper> allAliasSearchWrappers = new Vector<>();

      allAliasSearchWrappers.addAll(AliasSearchWrapper.wrapAliases(Main.getApplication().getAliasesAndDriversManager().getAliasList()));

      if (_dlg.chkIncludeAliasFolders.isSelected())
      {
         List<AliasFolder> allAliasFolders =
               Main.getApplication().getWindowManager().getAliasesListInternalFrame().getAliasesList().getAliasTreeInterface().getAllAliasFolders();

         allAliasSearchWrappers.addAll(AliasSearchWrapper.wrapAliasFolders(allAliasFolders));
      }


      Vector<AliasSearchWrapper> matchingAliasSearchWrappers = new Vector<>();

      for (AliasSearchWrapper aliasSearchWrapper : allAliasSearchWrappers)
      {
         if(matches(aliasSearchWrapper, filterText))
         {
            matchingAliasSearchWrappers.add(aliasSearchWrapper);
         }
      }

      Collections.sort(matchingAliasSearchWrappers, Comparator.comparing(AliasSearchWrapper::getName));

      _dlg.lstResult.setListData(matchingAliasSearchWrappers);
      _dlg.lstResult.setSelectedValue(formerSelectedValue, true);

      if(-1 == _dlg.lstResult.getSelectedIndex())
      {
         _dlg.lstResult.setSelectedIndex(0);
         _dlg.lstResult.ensureIndexIsVisible(0);
      }
   }

   private void onListClicked(MouseEvent e)
   {
      if(2 == e.getClickCount())
      {
         onConnect();
      }
   }


   private boolean matches(AliasSearchWrapper aliasSearchWrapper, String filterText)
   {
      if(StringUtilities.isEmpty(filterText, true))
      {
         return true;
      }

      if (null != aliasSearchWrapper.getAlias())
      {
         return    (null != aliasSearchWrapper.getAlias().getName() && -1 < aliasSearchWrapper.getAlias().getName().toLowerCase().indexOf(filterText.toLowerCase()))
                || (null != aliasSearchWrapper.getAlias().getUrl() && -1 < aliasSearchWrapper.getAlias().getUrl().toLowerCase().indexOf(filterText.toLowerCase()))
                || (null != aliasSearchWrapper.getAlias().getUserName() && -1 < aliasSearchWrapper.getAlias().getUserName().toLowerCase().indexOf(filterText.toLowerCase()));
      }
      else
      {
         return  null != aliasSearchWrapper.getAliasFolder().getFolderName() && -1 < aliasSearchWrapper.getAliasFolder().getFolderName().toLowerCase().indexOf(filterText.toLowerCase());
      }

   }




   private void onClosing()
   {
      Props.putInt(PREF_KEY_FIND_ALIAS_SHEET_WIDTH, _dlg.getWidth());
      Props.putInt(PREF_KEY_FIND_ALIAS_SHEET_HEIGHT, _dlg.getHeight());

      Props.putBoolean(PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN, _dlg.chkLeaveOpen.isSelected());


      Props.putBoolean(PREF_KEY_FIND_ALIAS_REMEMBER_LAST_SEARCH, _dlg.chkRememberLastSearch.isSelected());

      Props.putBoolean(PREF_KEY_FIND_ALIAS_INCLUDE_ALIAS_FOLDERS, _dlg.chkIncludeAliasFolders.isSelected());

      if (_dlg.chkRememberLastSearch.isSelected())
      {
         Props.putString(PREF_KEY_FIND_ALIAS_LAST_SEARCH_STRING, _dlg.txtToSearch.getText());
      }

   }


   private Dimension getDimension()
   {
      return new Dimension(
            Props.getInt(PREF_KEY_FIND_ALIAS_SHEET_WIDTH, 600),
            Props.getInt(PREF_KEY_FIND_ALIAS_SHEET_HEIGHT, 400)
      );
   }

}
