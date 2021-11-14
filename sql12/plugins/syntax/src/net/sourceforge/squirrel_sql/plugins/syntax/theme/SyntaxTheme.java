package net.sourceforge.squirrel_sql.plugins.syntax.theme;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;
import org.fife.ui.rtextarea.RTextAreaBase;

import java.io.Serializable;

public class SyntaxTheme implements Serializable
{
   private SyntaxStyle _columnStyle = new SyntaxStyle();
   private SyntaxStyle _commentStyle = new SyntaxStyle();
   private SyntaxStyle _dataTypeStyle = new SyntaxStyle();
   private SyntaxStyle _errorStyle = new SyntaxStyle();
   private SyntaxStyle _functionStyle = new SyntaxStyle();
   private SyntaxStyle _identifierStyle = new SyntaxStyle();
   private SyntaxStyle _literalStyle = new SyntaxStyle();
   private SyntaxStyle _operatorStyle = new SyntaxStyle();
   private SyntaxStyle _reservedWordStyle = new SyntaxStyle();
   private SyntaxStyle _separatorStyle = new SyntaxStyle();
   private SyntaxStyle _tableStyle = new SyntaxStyle();
   private SyntaxStyle _whiteSpaceStyle = new SyntaxStyle();

   /**
    * Default ist the same as {@link RTextAreaBase#DEFAULT_CURRENT_LINE_HIGHLIGHT_COLOR}
    * which unfortunately isn't public and thus can't be used here.
    */
   private int _currentLineHighlightColorRGB;

   private int _caretColorRGB;

   public SyntaxStyle getColumnStyle()
   {
      return _columnStyle;
   }

   public void setColumnStyle(SyntaxStyle columnStyle)
   {
      _columnStyle = columnStyle;
   }

   public SyntaxStyle getCommentStyle()
   {
      return _commentStyle;
   }

   public void setCommentStyle(SyntaxStyle commentStyle)
   {
      _commentStyle = commentStyle;
   }

   public SyntaxStyle getDataTypeStyle()
   {
      return _dataTypeStyle;
   }

   public void setDataTypeStyle(SyntaxStyle dataTypeStyle)
   {
      _dataTypeStyle = dataTypeStyle;
   }

   public SyntaxStyle getErrorStyle()
   {
      return _errorStyle;
   }

   public void setErrorStyle(SyntaxStyle errorStyle)
   {
      _errorStyle = errorStyle;
   }

   public SyntaxStyle getFunctionStyle()
   {
      return _functionStyle;
   }

   public void setFunctionStyle(SyntaxStyle functionStyle)
   {
      _functionStyle = functionStyle;
   }

   public SyntaxStyle getIdentifierStyle()
   {
      return _identifierStyle;
   }

   public void setIdentifierStyle(SyntaxStyle identifierStyle)
   {
      _identifierStyle = identifierStyle;
   }

   public SyntaxStyle getLiteralStyle()
   {
      return _literalStyle;
   }

   public void setLiteralStyle(SyntaxStyle literalStyle)
   {
      _literalStyle = literalStyle;
   }

   public SyntaxStyle getOperatorStyle()
   {
      return _operatorStyle;
   }

   public void setOperatorStyle(SyntaxStyle operatorStyle)
   {
      _operatorStyle = operatorStyle;
   }

   public SyntaxStyle getReservedWordStyle()
   {
      return _reservedWordStyle;
   }

   public void setReservedWordStyle(SyntaxStyle reservedWordStyle)
   {
      _reservedWordStyle = reservedWordStyle;
   }

   public SyntaxStyle getSeparatorStyle()
   {
      return _separatorStyle;
   }

   public void setSeparatorStyle(SyntaxStyle separatorStyle)
   {
      _separatorStyle = separatorStyle;
   }

   public SyntaxStyle getTableStyle()
   {
      return _tableStyle;
   }

   public void setTableStyle(SyntaxStyle tableStyle)
   {
      _tableStyle = tableStyle;
   }

   public SyntaxStyle getWhiteSpaceStyle()
   {
      return _whiteSpaceStyle;
   }

   public void setWhiteSpaceStyle(SyntaxStyle whiteSpaceStyle)
   {
      _whiteSpaceStyle = whiteSpaceStyle;
   }

   public int getCaretColorRGB()
   {
      return _caretColorRGB;
   }

   public void setCaretColorRGB(int caretColorRGB)
   {
      _caretColorRGB = caretColorRGB;
   }

   public int getCurrentLineHighlightColorRGB()
   {
      return _currentLineHighlightColorRGB;
   }

   public void setCurrentLineHighlightColorRGB(int currentLineHighlightColorRGB)
   {
      _currentLineHighlightColorRGB = currentLineHighlightColorRGB;
   }
}
