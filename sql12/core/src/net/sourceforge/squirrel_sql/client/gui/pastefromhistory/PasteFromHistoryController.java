package net.sourceforge.squirrel_sql.client.gui.pastefromhistory;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard.PasteHistory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class PasteFromHistoryController
{
   private static final String PREF_KEY_PASTE_HIST_DIALOG_WIDTH = "Squirrel.paste.history.dialog.width";
   private static final String PREF_KEY_PASTE_HIST_DIALOG_HEIGHT = "Squirrel.paste.history.dialog.height";

   private final PasteFromHistoryDialog _dlg;
   private ISQLPanelAPI _sqlPanelAPI;

   public PasteFromHistoryController(ISQLPanelAPI sqlPanelAPI)
   {
      _sqlPanelAPI = sqlPanelAPI;
      _dlg = new PasteFromHistoryDialog();

      _dlg.txtHistoryDetail.setEditable(false);


      PasteHistory pasteHistroy = Main.getApplication().getPasteHistroy();

      String[] histroy = pasteHistroy.getHistroy();

      _dlg.lstHistoryItems.setListData(histroy);

      if (0 < histroy.length)
      {
         _dlg.lstHistoryItems.setSelectedIndex(0);
         _dlg.txtHistoryDetail.setText(histroy[0]);
      }


      _dlg.setSize(getDimension());


      _dlg.btnCancel.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            close();
         }
      });

      _dlg.btnOk.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onOk();
         }
      });


      _dlg.lstHistoryItems.requestFocus();

      _dlg.lstHistoryItems.addListSelectionListener(new ListSelectionListener()
      {
         @Override
         public void valueChanged(ListSelectionEvent e)
         {
            onSelectionChanged(e);
         }
      });

      _dlg.lstHistoryItems.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListMouseClicked(e);
         }
      });

      _dlg.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            onWindowClosing();
         }
      });

      _dlg.setVisible(true);

      GUIUtils.centerWithinParent(_dlg);



   }

   private void onListMouseClicked(MouseEvent e)
   {
      if (e.getClickCount() == 2)
      {
         onOk();
      }
   }

   private void onOk()
   {
      String selected = (String) _dlg.lstHistoryItems.getSelectedValue();

      if (null != selected)
      {
         _sqlPanelAPI.getSQLEntryPanel().replaceSelection(selected);
      }

      close();
   }

   private void close()
   {
      onWindowClosing();
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   private Dimension getDimension()
   {
      return new Dimension(
            Preferences.userRoot().getInt(PREF_KEY_PASTE_HIST_DIALOG_WIDTH, 800),
            Preferences.userRoot().getInt(PREF_KEY_PASTE_HIST_DIALOG_HEIGHT, 500)
      );
   }


   private void onSelectionChanged(ListSelectionEvent e)
   {
      if(e.getValueIsAdjusting())
      {
         return;
      }

      String selectedValue = (String) _dlg.lstHistoryItems.getSelectedValue();

      if(null == selectedValue)
      {
         _dlg.txtHistoryDetail.setText("");
      }
      else
      {
         _dlg.txtHistoryDetail.setText(selectedValue);
      }
   }

   private void onWindowClosing()
   {
      Dimension size = _dlg.getSize();
      Preferences.userRoot().putInt(PREF_KEY_PASTE_HIST_DIALOG_WIDTH, size.width);
      Preferences.userRoot().putInt(PREF_KEY_PASTE_HIST_DIALOG_HEIGHT, size.height);
   }

}
