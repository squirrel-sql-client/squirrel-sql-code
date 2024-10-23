package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeGeneral;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JTextArea;
import javax.swing.JToggleButton;

public class ReformatHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ReformatHandler.class);

   private final JTextArea _textArea;
   private final JToggleButton _btnReformat;
   private String _originalUnformattedText;
   private boolean _reformatSilently = false;

   public ReformatHandler(JTextArea textAreaWithOriginalUnformatedText)
   {
      _textArea = textAreaWithOriginalUnformatedText;
      _originalUnformattedText = _textArea.getText();

      _btnReformat = new JToggleButton(s_stringMgr.getString("ReformatHandler.reformatXml"));
      //GUIUtils.setPreferredWidth(reformatButton, (int) (((double) reformatButton.getPreferredSize().width) * 1.2d));
      GUIUtils.setMinimumWidth(_btnReformat, (int) (((double) _btnReformat.getMinimumSize().width) * 1.1d));

      _btnReformat.addActionListener(e -> onReformat());
   }

   public void maybeDoInitialAutoReformat()
   {
      boolean formatXmlJsonWhenDisplayedInPopupPanel = DataTypeGeneral.isFormatXmlJsonWhenDisplayedInPopupPanel();
      if(formatXmlJsonWhenDisplayedInPopupPanel)
      {
         try
         {
            _reformatSilently = true;
            setReformated(formatXmlJsonWhenDisplayedInPopupPanel);
         }
         finally
         {
            _reformatSilently = false;
         }
         //_btnReformat.setSelected(formatXmlJsonWhenDisplayedInPopupPanel);
         //reformat(true, true);
      }

   }

   public JToggleButton getBtnReformat()
   {
      return _btnReformat;
   }

   public void setReformated(boolean reformated)
   {
      if(_btnReformat.isSelected() != reformated)
      {
         _btnReformat.doClick();
      }

      //_btnReformat.setSelected(reformated);
      //onReformat();
   }


   private void onReformat()
   {
      reformat();
   }


   private void reformat()
   {
      if(_btnReformat.isSelected())
      {
         FormattingResult formattingResult = CellDataPopupFormatter.format(_textArea.getText(), _reformatSilently);
         if(formattingResult.isSuccess())
         {
            _textArea.setText(formattingResult.getResult());
         }
         else
         {
            _btnReformat.setSelected(false);
         }
      }
      else
      {
         _textArea.setText(_originalUnformattedText);
      }
   }

}
