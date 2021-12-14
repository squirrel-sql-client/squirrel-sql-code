package net.sourceforge.squirrel_sql.client.gui.pastefromhistory;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard.PasteHistory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;

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


      PasteHistory pasteHistory = Main.getApplication().getPasteHistory();

      String[] history = pasteHistory.getHistory();

      _dlg.lstHistoryItems.setListData(history);

      if (0 < history.length)
      {
         _dlg.lstHistoryItems.setSelectedIndex(0);
         _dlg.txtHistoryDetail.setText(history[0]);
      }


      _dlg.setSize(getDimension());


      _dlg.btnCancel.addActionListener(e -> close());

      _dlg.btnOk.addActionListener(e -> onOk());


      _dlg.lstHistoryItems.requestFocus();

      _dlg.lstHistoryItems.addListSelectionListener(e -> onSelectionChanged(e));

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

      SwingUtilities.invokeLater(() -> _dlg.lstHistoryItems.requestFocus());
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

      ///////////////////////////////////////////////////////////////////////////////
      // Put the pasted entry on top of the history and set it as clipboard content
      StringSelection contents = new StringSelection(selected);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, contents);

      Main.getApplication().getPasteHistory().addToPasteHistory(selected);
      //
      ///////////////////////////////////////////////////////////////////////////////

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
            Props.getInt(PREF_KEY_PASTE_HIST_DIALOG_WIDTH, 800),
            Props.getInt(PREF_KEY_PASTE_HIST_DIALOG_HEIGHT, 500)
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
      Props.putInt(PREF_KEY_PASTE_HIST_DIALOG_WIDTH, size.width);
      Props.putInt(PREF_KEY_PASTE_HIST_DIALOG_HEIGHT, size.height);
   }

}
