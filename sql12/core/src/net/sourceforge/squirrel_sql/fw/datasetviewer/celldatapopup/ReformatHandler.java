package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.client.preferences.themes.ThemesEnum;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BinaryDisplayConverter;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeGeneral;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DisplayAsciiMode;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableRSyntaxTextArea;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

import javax.swing.JToggleButton;
import java.awt.Color;

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
               styleJson();
               _textArea.repaint();
            }
            else if(FormattingResultType.XML == formattingResult.getFormattingResultType())
            {
               _textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
               styleXml();
               _textArea.repaint();
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

   private void styleXml()
   {
      if(ThemesEnum.getCurrentTheme() == ThemesEnum.DARK)
      {
         _textArea.getSyntaxScheme().getStyle(TokenTypes.VARIABLE).foreground = Color.green;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(182, 118, 3).brighter().brighter();
         _textArea.getSyntaxScheme().getStyle(TokenTypes.IDENTIFIER).foreground = Color.lightGray;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.SEPARATOR).foreground = Color.white;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_TAG_ATTRIBUTE).foreground = new Color(196, 105, 248, 255);
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = Color.lightGray;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.OPERATOR).foreground = Color.white;

         Color simpleMarkupDark = new Color(11, 225, 232, 255);
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_TAG_DELIMITER).foreground = simpleMarkupDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_TAG_NAME).foreground = simpleMarkupDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_COMMENT).foreground = simpleMarkupDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_DTD).foreground = simpleMarkupDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_PROCESSING_INSTRUCTION).foreground = simpleMarkupDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.MARKUP_CDATA_DELIMITER).foreground = simpleMarkupDark;
      }
   }

   private void styleJson()
   {
      if( ThemesEnum.getCurrentTheme() == ThemesEnum.DARK )
      {
         _textArea.getSyntaxScheme().getStyle(TokenTypes.VARIABLE).foreground = Color.green;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(182, 118, 3).brighter().brighter();
         _textArea.getSyntaxScheme().getStyle(TokenTypes.IDENTIFIER).foreground = Color.lightGray;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.SEPARATOR).foreground = Color.white;


         Color simpleLiteralsDark = new Color(11, 225, 232, 255);
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_CHAR).foreground = simpleLiteralsDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_NUMBER_FLOAT).foreground = simpleLiteralsDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT).foreground = simpleLiteralsDark;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_BOOLEAN).foreground = simpleLiteralsDark;
      }
      else
      {
         _textArea.getSyntaxScheme().getStyle(TokenTypes.VARIABLE).foreground = Color.blue ;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(58, 46, 28);

         Color simpleLiteralsLight = new Color(207, 19, 211, 255);
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_CHAR).foreground = simpleLiteralsLight;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_NUMBER_FLOAT).foreground = simpleLiteralsLight;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT).foreground = simpleLiteralsLight;
         _textArea.getSyntaxScheme().getStyle(TokenTypes.LITERAL_BOOLEAN).foreground = simpleLiteralsLight;
      }
   }

   public boolean isReformatted()
   {
      return _btnReformat.isSelected();
   }
}
