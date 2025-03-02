package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BinaryDisplayConverter;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeGeneral;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DisplayAsciiMode;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableRSyntaxTextArea;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.JToggleButton;

public class ReformatHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ReformatHandler.class);

   private final RestorableRSyntaxTextArea _textArea;
   private final ReformatHandlerListener _reformatHandlerListener;
   private final boolean _originalUnformattedTextIsBinaryBase16BinaryData;
   private final JToggleButton _btnReformat;
   private String _originalUnformattedText;
   private boolean _reformatSilently = false;

   public ReformatHandler(RestorableRSyntaxTextArea textAreaWithOriginalUnformattedText,
                          boolean originalUnformattedTextIsBinaryBase16BinaryData,
                          ReformatHandlerListener reformatHandlerListener)
   {
      _textArea = textAreaWithOriginalUnformattedText;
      _reformatHandlerListener = reformatHandlerListener;
      _originalUnformattedText = _textArea.getText();
      _originalUnformattedTextIsBinaryBase16BinaryData = originalUnformattedTextIsBinaryBase16BinaryData;

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
         String text = _textArea.getText();

         if(_originalUnformattedTextIsBinaryBase16BinaryData)
         {
            Byte[] bytes = BinaryDisplayConverter.convertToBytes(_originalUnformattedText, 16, false);
            text = BinaryDisplayConverter.convertToString(bytes, 16, DisplayAsciiMode.ASCII_NO_ADDITIONAL_SPACES);
         }

         FormattingResult formattingResult = CellDataPopupFormatter.format(text, _reformatSilently);
         if(formattingResult.isSuccess())
         {
            _textArea.setText(formattingResult.getResult());

            _textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
            if(FormattingResultType.JSON == formattingResult.getFormattingResultType())
            {
               _textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
            }
            else if(FormattingResultType.XML == formattingResult.getFormattingResultType())
            {
               _textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
            }
         }
         else
         {
            _btnReformat.setSelected(false);
         }
      }
      else
      {
         _textArea.setText(_originalUnformattedText);
         _reformatHandlerListener.originalTextWasRestored();
      }
   }

   public boolean isReformatted()
   {
      return _btnReformat.isSelected();
   }
}
