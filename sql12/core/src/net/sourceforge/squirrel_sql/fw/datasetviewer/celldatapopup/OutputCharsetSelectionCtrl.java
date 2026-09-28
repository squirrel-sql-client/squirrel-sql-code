package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class OutputCharsetSelectionCtrl
{
   public final static ILogger s_log = LoggerController.createLogger(OutputCharsetSelectionCtrl.class);


   private static final String PREF_OUTPUT_CHARSET = "OutputCharsetSelectionCtrl.output.charset";
   private final DefaultListModel<String> _listModel;

   OutputCharsetSelectionDlg _dlg;

   public OutputCharsetSelectionCtrl(Window owningWindow)
   {
      _dlg = new OutputCharsetSelectionDlg(owningWindow);

      _listModel = new DefaultListModel<>();
      _dlg.lstCharSets.setModel(_listModel);
      _dlg.lstCharSets.addListSelectionListener(e -> onListSelectionChanged(e));

      initList();

      _dlg.filterCboHandler.addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e) {initList();}

         @Override
         public void removeUpdate(DocumentEvent e) {initList();}

         @Override
         public void changedUpdate(DocumentEvent e) {initList();}
      });

      _dlg.filterCboHandler.selectFirstItemIfExists();
      _dlg.filterCboHandler.focus();

      _dlg.filterCboHandler.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyPressed(KeyEvent e)
         {
            GUIUtils.traverseListOnUpDownKeys(_dlg.lstCharSets, e);
         }
      });

      _dlg.lblSelectedCharset.setText(getSelectedCharset().name());

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      _dlg.getRootPane().setDefaultButton(_dlg.btnOk);

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 300, 400);
      _dlg.setVisible(true);
   }

   private void onOk()
   {
      Charset charset = Charset.forName(_dlg.lblSelectedCharset.getText());
      Props.putString(PREF_OUTPUT_CHARSET, charset.name());
      _dlg.filterCboHandler.saveCurrentItem();
      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }


   private void onListSelectionChanged(ListSelectionEvent e)
   {
      if(false == e.getValueIsAdjusting())
      {
         String selectedValue = _dlg.lstCharSets.getSelectedValue();

         if(StringUtils.isBlank(selectedValue))
         {
            return;
         }
         Charset charset = Charset.forName(selectedValue);

         _dlg.lblSelectedCharset.setText(charset.name());
      }
   }

   private void initList()
   {
      String filter = _dlg.filterCboHandler.getItem();

      List<String> availableCharsetNames = Charset.availableCharsets().keySet().stream().toList();

      ArrayList<String> list = new ArrayList<>(availableCharsetNames.stream().filter(e -> StringUtils.isBlank(filter) ? true : StringUtils.containsIgnoreCase(e, filter)).toList());
      Collections.sort(list);
      _listModel.clear();
      _listModel.addAll(list);

      _dlg.lstCharSets.setSelectedValue(getSelectedCharset().name(), true);

   }

   public static Charset getSelectedCharset()
   {
      Charset ret = StandardCharsets.UTF_8;

      String charsetName = Props.getString(PREF_OUTPUT_CHARSET, ret.name());

      try
      {
         ret = Charset.forName(charsetName);
      }
      catch(Exception e)
      {
         s_log.error("Failed to load character set %s falling back to %s".formatted(charsetName, ret.name()), e);
      }

      return ret;
   }
}
