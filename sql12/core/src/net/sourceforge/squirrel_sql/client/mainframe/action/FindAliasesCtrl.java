package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.*;
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
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(FindAliasesCtrl.class);

   public static final String PREF_KEY_FIND_ALIAS_SHEET_WIDTH = "Squirrel.findAliasSheet.width";
   public static final String PREF_KEY_FIND_ALIAS_SHEET_HEIGHT = "Squirrel.findAliasSheet.height";

   public static final String PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN = "Squirrel.findAliasSheet.check.leave.open";


   private final JDialog _dlg;
   private final JTextField _txtToSearch = new JTextField();
   private final JList _lstResult = new JList();

   private JCheckBox _chkLeaveOpen = new JCheckBox(s_stringMgr.getString("FindAliasesCtrl.leave.open"));


   private JButton _btnConnect = new JButton(s_stringMgr.getString("FindAliasesCtrl.connect"));
   private JButton _btnGoto = new JButton(s_stringMgr.getString("FindAliasesCtrl.goto"));
   private JButton _btnClose = new JButton(s_stringMgr.getString("FindAliasesCtrl.close"));


   public FindAliasesCtrl(final IAliasesList al)
   {
      _dlg = createGui();

      GUIUtils.centerWithinParent(_dlg);

      GUIUtils.enableCloseByEscape(_dlg, new CloseByEscapeListener() {
         @Override
         public void willCloseByEcape(JDialog dialog)
         {
            onClosing();
         }
      });


      _dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onClosing();
         }
      });

      _lstResult.setCellRenderer(new FindAliasListCellRenderer());

      _lstResult.setFocusable(false);

      _lstResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      _lstResult.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }
      });


      _chkLeaveOpen.setFocusable(false);

      _chkLeaveOpen.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN, false));


      _btnConnect.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onConnect();
         }
      });

      _btnGoto.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onGoto(al);
         }
      });

      _btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onClose();
         }
      });



      _txtToSearch.getDocument().addDocumentListener(new DocumentListener()
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

      _txtToSearch.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }
      });



      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            _txtToSearch.requestFocus();
         }
      });



      _dlg.getRootPane().setDefaultButton(_btnConnect);


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
         int selIx = _lstResult.getSelectedIndex();

         if(0 < selIx)
         {
            _lstResult.setSelectedIndex(selIx - 1);
            _lstResult.ensureIndexIsVisible(selIx - 1);
         }
      }
      else if(e.getKeyCode() == KeyEvent.VK_DOWN)
      {
         int selIx = _lstResult.getSelectedIndex();

         if(_lstResult.getModel().getSize() - 1 > selIx)
         {
            _lstResult.setSelectedIndex(selIx + 1);
            _lstResult.ensureIndexIsVisible(selIx + 1);
         }
      }


   }

   private void onConnect()
   {
      ISQLAlias selectedAlias = (ISQLAlias) _lstResult.getSelectedValue();

      if(null ==  selectedAlias)
      {
         return;
      }

      ICompletionCallback completionCallback = null;

      if (_chkLeaveOpen.isSelected())
      {
         completionCallback = new ConnectToAliasCallBack(Main.getApplication(), (SQLAlias) selectedAlias)
         {
            @Override
            public void sessionCreated(ISession session)
            {
               _dlg.requestFocus();
               _txtToSearch.requestFocus();
               super.sessionCreated(session);
            }

         };
      }


      new ConnectToAliasCommand(Main.getApplication(), (SQLAlias) selectedAlias, true, completionCallback).execute();

      if(false == _chkLeaveOpen.isSelected())
      {
         onClosing();
         _dlg.setVisible(false);
         _dlg.dispose();
      }

   }

   private void onGoto(IAliasesList al)
   {
      ISQLAlias selectedAlias = (ISQLAlias) _lstResult.getSelectedValue();

      if(null == selectedAlias)
      {
         return;
      }

      IApplication app = Main.getApplication();
      AliasesListInternalFrame aliasesFrame = Main.getApplication().getWindowManager().getAliasesListInternalFrame();

      new ViewAliasesAction(app, aliasesFrame).actionPerformed(new ActionEvent(_dlg, 1, "Dummy"));

      al.goToAlias(selectedAlias);


      if(false == _chkLeaveOpen.isSelected())
      {
         onClosing();
         _dlg.setVisible(false);
         _dlg.dispose();
      }
      else
      {
         _txtToSearch.requestFocus();
      }
   }

   private void updateList()
   {

      String filterText = _txtToSearch.getText();

      Object formerSelectedValue = _lstResult.getSelectedValue();

      Vector<ISQLAlias> allAliases = new Vector<ISQLAlias>(Main.getApplication().getDataCache().getAliasList());


      Vector<ISQLAlias> matchingAliases = new Vector<ISQLAlias>();

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

      _lstResult.setListData(matchingAliases);
      _lstResult.setSelectedValue(formerSelectedValue, true);

      if(-1 == _lstResult.getSelectedIndex())
      {
         _lstResult.setSelectedIndex(0);
         _lstResult.ensureIndexIsVisible(0);
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

   private JDialog createGui()
   {
      JDialog _dlg;
      _dlg = new JDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("FindAliasesCtrl.find.alias"), false);

      Container pane = _dlg.getContentPane();
      pane.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      pane.add(new JLabel(s_stringMgr.getString("FindAliasesCtrl.enter.text")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pane.add(_txtToSearch, gbc);

      gbc = new GridBagConstraints(0,2,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      pane.add(new JScrollPane(_lstResult), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pane.add(_chkLeaveOpen, gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pane.add(createButtonPanel(), gbc);


      _dlg.setSize(getDimension());
      return _dlg;
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(_btnConnect, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(_btnGoto, gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(_btnClose, gbc);

      return ret;
   }

   private void onClosing()
   {
      Preferences.userRoot().putInt(PREF_KEY_FIND_ALIAS_SHEET_WIDTH, _dlg.getWidth());
      Preferences.userRoot().putInt(PREF_KEY_FIND_ALIAS_SHEET_HEIGHT, _dlg.getHeight());

      Preferences.userRoot().putBoolean(PREF_KEY_FIND_ALIAS_CHECK_LEAVE_OPEN, _chkLeaveOpen.isSelected());
   }


   private Dimension getDimension()
   {
      return new Dimension(
            Preferences.userRoot().getInt(PREF_KEY_FIND_ALIAS_SHEET_WIDTH, 600),
            Preferences.userRoot().getInt(PREF_KEY_FIND_ALIAS_SHEET_HEIGHT, 400)
      );
   }

}
