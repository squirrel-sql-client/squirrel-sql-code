package net.sourceforge.squirrel_sql.client.mainframe.action.findaliases;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.*;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.FindAliasListCellRenderer;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewAliasesAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.CloseByEscapeListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.prefs.Preferences;

public class FindAliasesCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindAliasesCtrl.class);

   public static final String PREF_KEY_FIND_ALIAS_SHEET_WIDTH = "Squirrel.findAliasSheet.width";
   public static final String PREF_KEY_FIND_ALIAS_SHEET_HEIGHT = "Squirrel.findAliasSheet.height";

   public static final String PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN = "Squirrel.findAliasSheet.check.leave.open";

   public static final String PREF_KEY_FIND_ALIAS_REMEMBER_LAST_SEARCH = "Squirrel.findAliasSheet.remember.last.search";

   public static final String PREF_KEY_FIND_ALIAS_LAST_SEARCH_STRING = "Squirrel.findAliasSheet.last.search.string";


   private FindAliasesDialog _dlg = new FindAliasesDialog();

   public FindAliasesCtrl(final IAliasesList al)
   {
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

      _dlg.chkLeaveOpen.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN, false));


      _dlg.btnConnect.addActionListener(e -> onConnect());

      _dlg.btnGoto.addActionListener(e -> onGoto(al));

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


      _dlg.chkRememberLastSearch.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_FIND_ALIAS_REMEMBER_LAST_SEARCH, false));

      if (_dlg.chkRememberLastSearch.isSelected())
      {
         _dlg.txtToSearch.setText(Preferences.userRoot().get(PREF_KEY_FIND_ALIAS_LAST_SEARCH_STRING, null));
      }

      _dlg.txtToSearch.addKeyListener(new KeyAdapter() {
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
      ISQLAlias selectedAlias = (ISQLAlias) _dlg.lstResult.getSelectedValue();

      if(null ==  selectedAlias)
      {
         return;
      }

      ICompletionCallback completionCallback = null;

      if (_dlg.chkLeaveOpen.isSelected())
      {
         completionCallback = new ConnectToAliasCallBack(Main.getApplication(), (SQLAlias) selectedAlias)
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


      new ConnectToAliasCommand(Main.getApplication(), (SQLAlias) selectedAlias, true, completionCallback).execute();

      if(false == _dlg.chkLeaveOpen.isSelected())
      {
         onClosing();
         _dlg.setVisible(false);
         _dlg.dispose();
      }

   }

   private void onGoto(IAliasesList al)
   {
      ISQLAlias selectedAlias = (ISQLAlias) _dlg.lstResult.getSelectedValue();

      if(null == selectedAlias)
      {
         return;
      }

      IApplication app = Main.getApplication();
      AliasesListInternalFrame aliasesFrame = Main.getApplication().getWindowManager().getAliasesListInternalFrame();

      new ViewAliasesAction(app, aliasesFrame).actionPerformed(new ActionEvent(_dlg, 1, "Dummy"));

      al.goToAlias(selectedAlias);


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

      Object formerSelectedValue = _dlg.lstResult.getSelectedValue();

      Vector<ISQLAlias> allAliases = new Vector<>(Main.getApplication().getDataCache().getAliasList());


      Vector<ISQLAlias> matchingAliases = new Vector<>();

      for (ISQLAlias alias : allAliases)
      {
         if(matches(alias, filterText))
         {
            matchingAliases.add(alias);
         }
      }


      Collections.sort(matchingAliases, new Comparator<ISQLAlias>() {
         @Override
         public int compare(ISQLAlias o1, ISQLAlias o2)
         {
            return o1.getName().compareTo(o2.getName());
         }
      });

      _dlg.lstResult.setListData(matchingAliases);
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


   private boolean matches(ISQLAlias alias, String filterText)
   {
      if(StringUtilities.isEmpty(filterText, true))
      {
         return true;
      }

      return    (null != alias.getName() && -1 < alias.getName().toLowerCase().indexOf(filterText.toLowerCase()))
             || (null != alias.getUrl() && -1 < alias.getUrl().toLowerCase().indexOf(filterText.toLowerCase()))
             || (null != alias.getUserName() && -1 < alias.getUserName().toLowerCase().indexOf(filterText.toLowerCase()));

   }



   private void onClosing()
   {
      Preferences.userRoot().putInt(PREF_KEY_FIND_ALIAS_SHEET_WIDTH, _dlg.getWidth());
      Preferences.userRoot().putInt(PREF_KEY_FIND_ALIAS_SHEET_HEIGHT, _dlg.getHeight());

      Preferences.userRoot().putBoolean(PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN, _dlg.chkLeaveOpen.isSelected());


      Preferences.userRoot().putBoolean(PREF_KEY_FIND_ALIAS_REMEMBER_LAST_SEARCH, _dlg.chkRememberLastSearch.isSelected());

      if (_dlg.chkRememberLastSearch.isSelected())
      {
         Preferences.userRoot().put(PREF_KEY_FIND_ALIAS_LAST_SEARCH_STRING, _dlg.txtToSearch.getText());
      }

   }


   private Dimension getDimension()
   {
      return new Dimension(
            Preferences.userRoot().getInt(PREF_KEY_FIND_ALIAS_SHEET_WIDTH, 600),
            Preferences.userRoot().getInt(PREF_KEY_FIND_ALIAS_SHEET_HEIGHT, 400)
      );
   }

}
