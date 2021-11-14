package net.sourceforge.squirrel_sql.plugins.syntax;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.plugins.syntax.theme.SyntaxTheme;
import net.sourceforge.squirrel_sql.plugins.syntax.theme.SyntaxThemeFactory;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * This JavaBean class represents the user specific
 * preferences for this plugin.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SyntaxPreferences implements Serializable
{
   public static final int NO_COLOR = -1;

   public interface IPropertyNames
   {
      //		String BLINK_CARET = "blinkCaret";
//		String BLOCK_CARET_ENABLED = "blockCaretEnabled";
//		String BRACKET_HIGHLIGHTING = "bracketHighlighting";
//		String BRACKET_HIGHLIGHT_COLOR = "bracketHighlightColor";
//		String CARET_COLOR = "caretColor";
      String COLUMN_STYLE = "columnStyle";
      String COMMENT_STYLE = "commentStyle";
      //		String CURRENT_LINE_HIGHLIGHTING = "currentLineHighlighting";
//		String CURRENT_LINE_HIGHLIGHT_COLOR = "currentLineHighlightColor";
      String DATA_TYPE_STYLE = "dataTypeStyle";
      //		String EOL_MARKERS = "eolMarkers";
//		String EOL_MARKER_COLOR = "eolMarkerColor";
      String ERROR_STYLE = "errorStyle";
      String FUNCTION_STYLE = "functionStyle";
      String IDENTIFIER_STYLE = "identifierStyle";
      String LITERAL_STYLE = "literalStyle";
      String OPERATOR_STYLE = "operatorStyle";
      String RESERVED_WORD_STYLE = "reservedWordStyle";
      String SEPARATOR_STYLE = "separatorStyle";
      //		String LINE_NUMBER_COLOR = "lineNumberColor";
//		String SELECTION_COLOR = "selectionColor";
//		String SHOW_LINE_NBRS = "showLineNumbers";
      String TABLE_STYLE = "tableStyle";
      String USE_RSYNTAX_CONTROL = "useRSyntaxControl";
      String USE_PLAIN_CONTROL = "usePlainControl";
      String WHITE_SPACE_STYLE = "whiteSpaceStyle";
      String TEXT_LIMIT_LINE_VISIBLE = "textLimitLineVisible";
      String TAB_LENGTH = "tabLength";
      String REPLACE_TABS_BY_SPACES = "replaceTabsBySpaces";
      String TEXT_LIMIT_LINE_WIDTH = "textLimitLineWidth";
      String HIGHLIGHT_CURRENT_LINE = "highlightCurrentLine";
      String CURRENT_LINE_HIGHLIGHT_COLOR_RGB = "currentLineHighlightColorRGB";
      String LINE_NUMBERS_ENABLED = "lineNumbersEnabled";
      String CARET_COLOR_RGB = "caretColorRGB";
   }

   /**
    * Object to handle property change events.
    */
   private transient PropertyChangeReporter _propChgReporter;

   private boolean _usePlainTextControl = false;
   private boolean _useRSyntaxTextArea = true;


   private boolean _textLimitLineVisible = false;
   private int _textLimitLineWidth = 80;

   private int _tabLength = 5;
   private boolean _replaceTabsBySpaces = false;

   private boolean _highlightCurrentLine = true;


   private boolean _lineNumbersEnabled = false;

   /**
    * Indicates, that copy in rich text format should be used as default copy action (if possible)
    */
   private boolean _useCopyAsRtf = false;


   private SyntaxTheme _syntaxTheme;


   public SyntaxPreferences()
   {
      initSyntaxTheme(SyntaxThemeFactory.createDefaultLightTheme());
   }

   public void initSyntaxTheme(SyntaxTheme syntaxTheme)
   {
      _syntaxTheme = syntaxTheme;
   }

   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      getPropertyChangeReporter().addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      getPropertyChangeReporter().removePropertyChangeListener(listener);
   }


   public boolean getUseRSyntaxTextArea()
   {
      return _useRSyntaxTextArea;
   }

   public void setUseRSyntaxTextArea(boolean data)
   {
      if (_useRSyntaxTextArea != data)
      {
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.USE_RSYNTAX_CONTROL, _useRSyntaxTextArea, data);
         _useRSyntaxTextArea = data;
      }
   }

   public boolean getUsePlainTextControl()
   {
      return _usePlainTextControl;
   }

   public void setUsePlainTextControl(boolean data)
   {
      if (_usePlainTextControl != data)
      {
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.USE_PLAIN_CONTROL, _usePlainTextControl, data);
         _usePlainTextControl = data;
      }
   }


   public boolean isTextLimitLineVisible()
   {
      return _textLimitLineVisible;
   }

   public void setTextLimitLineVisible(boolean data)
   {
      if (_textLimitLineVisible != data)
      {
         final Boolean oldValue = Boolean.valueOf(_textLimitLineVisible);
         _textLimitLineVisible = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.TEXT_LIMIT_LINE_VISIBLE, oldValue, Boolean.valueOf(_textLimitLineVisible));
      }
   }

   public boolean isReplaceTabsBySpaces()
   {
      return _replaceTabsBySpaces;
   }

   public void setReplaceTabsBySpaces(boolean data)
   {
      if (_replaceTabsBySpaces != data)
      {
         final Boolean oldValue = Boolean.valueOf(_replaceTabsBySpaces);
         _replaceTabsBySpaces = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.REPLACE_TABS_BY_SPACES, oldValue, Boolean.valueOf(_replaceTabsBySpaces));
      }

   }

   public int getTabLength()
   {
      return _tabLength;
   }

   public void setTabLength(int data)
   {
      if (_tabLength != data)
      {
         final Integer oldValue = _tabLength;
         _tabLength = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.TAB_LENGTH, oldValue, (Integer) _tabLength);
      }
   }


   public int getTextLimitLineWidth()
   {
      return _textLimitLineWidth;
   }

   public void setTextLimitLineWidth(int data)
   {
      if (_textLimitLineWidth != data)
      {
         final Integer oldValue = Integer.valueOf(_textLimitLineWidth);
         _textLimitLineWidth = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.TEXT_LIMIT_LINE_WIDTH, oldValue, Integer.valueOf(_textLimitLineWidth));
      }

   }


   public boolean isHighlightCurrentLine()
   {
      return _highlightCurrentLine;
   }


   public void setHighlightCurrentLine(boolean data)
   {
      if (_highlightCurrentLine != data)
      {
         final Boolean oldValue = Boolean.valueOf(_highlightCurrentLine);
         _highlightCurrentLine = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.HIGHLIGHT_CURRENT_LINE, oldValue, Boolean.valueOf(_highlightCurrentLine));
      }

   }

   public boolean isLineNumbersEnabled()
   {
      return _lineNumbersEnabled;
   }

   public int getCurrentLineHighlightColorRGB()
   {
      return _syntaxTheme.getCurrentLineHighlightColorRGB();
   }

   public void setCurrentLineHighlightColorRGB(int data)
   {
      if (_syntaxTheme.getCurrentLineHighlightColorRGB() != data)
      {
         final int oldValue = _syntaxTheme.getCurrentLineHighlightColorRGB();
         _syntaxTheme.setCurrentLineHighlightColorRGB(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.CURRENT_LINE_HIGHLIGHT_COLOR_RGB, oldValue, _syntaxTheme.getCurrentLineHighlightColorRGB());
      }
   }


   public void setLineNumbersEnabled(boolean data)
   {
      if (_lineNumbersEnabled != data)
      {
         final Boolean oldValue = Boolean.valueOf(_lineNumbersEnabled);
         _lineNumbersEnabled = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.LINE_NUMBERS_ENABLED, oldValue, Boolean.valueOf(_lineNumbersEnabled));
      }

   }

   public int getCaretColorRGB()
   {
      return _syntaxTheme.getCaretColorRGB();
   }

   public void setCaretColorRGB(int caretColorRGB)
   {
      final int oldValue = _syntaxTheme.getCaretColorRGB();
      _syntaxTheme.setCaretColorRGB(caretColorRGB);
      getPropertyChangeReporter().firePropertyChange(IPropertyNames.CARET_COLOR_RGB, oldValue, _syntaxTheme.getCaretColorRGB());
   }

   public SyntaxStyle getCommentStyle()
   {
      return _syntaxTheme.getCommentStyle();
   }

   public void setCommentStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getCommentStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getCommentStyle();
         _syntaxTheme.setCommentStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.COMMENT_STYLE, oldValue, _syntaxTheme.getCommentStyle());
      }
   }

   public SyntaxStyle getDataTypeStyle()
   {
      return _syntaxTheme.getDataTypeStyle();
   }

   public void setDataTypeStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getDataTypeStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getDataTypeStyle();
         _syntaxTheme.setDataTypeStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.DATA_TYPE_STYLE, oldValue, _syntaxTheme.getDataTypeStyle());
      }
   }

   public SyntaxStyle getErrorStyle()
   {
      return _syntaxTheme.getErrorStyle();
   }

   public void setErrorStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getErrorStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getErrorStyle();
         _syntaxTheme.setErrorStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.ERROR_STYLE, oldValue, _syntaxTheme.getErrorStyle());
      }
   }

   public SyntaxStyle getFunctionStyle()
   {
      return _syntaxTheme.getFunctionStyle();
   }

   public void setFunctionStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getFunctionStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getFunctionStyle();
         _syntaxTheme.setFunctionStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.FUNCTION_STYLE, oldValue, _syntaxTheme.getFunctionStyle());
      }
   }

   public SyntaxStyle getIdentifierStyle()
   {
      return _syntaxTheme.getIdentifierStyle();
   }

   public void setIdentifierStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getIdentifierStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getIdentifierStyle();
         _syntaxTheme.setIdentifierStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.IDENTIFIER_STYLE, oldValue, _syntaxTheme.getIdentifierStyle());
      }
   }


   public SyntaxStyle getLiteralStyle()
   {
      return _syntaxTheme.getLiteralStyle();
   }

   public void setLiteralStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getLiteralStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getLiteralStyle();
         _syntaxTheme.setLiteralStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.LITERAL_STYLE, oldValue, _syntaxTheme.getLiteralStyle());
      }
   }

   public SyntaxStyle getTableStyle()
   {
      return _syntaxTheme.getTableStyle();
   }

   public void setTableStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getTableStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getTableStyle();
         _syntaxTheme.setTableStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.TABLE_STYLE, oldValue, _syntaxTheme.getTableStyle());
      }
   }

   public SyntaxStyle getColumnStyle()
   {
      return _syntaxTheme.getColumnStyle();
   }

   public void setColumnStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getColumnStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getColumnStyle();
         _syntaxTheme.setColumnStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.COLUMN_STYLE, oldValue, _syntaxTheme.getColumnStyle());
      }
   }

   public SyntaxStyle getOperatorStyle()
   {
      return _syntaxTheme.getOperatorStyle();
   }

   public void setOperatorStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getOperatorStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getOperatorStyle();
         _syntaxTheme.setOperatorStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.OPERATOR_STYLE, oldValue, _syntaxTheme.getOperatorStyle());
      }
   }

   public SyntaxStyle getReservedWordStyle()
   {
      return _syntaxTheme.getReservedWordStyle();
   }

   public void setReservedWordStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getReservedWordStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getReservedWordStyle();
         _syntaxTheme.setReservedWordStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.RESERVED_WORD_STYLE, oldValue, _syntaxTheme.getReservedWordStyle());
      }
   }

   public SyntaxStyle getSeparatorStyle()
   {
      return _syntaxTheme.getSeparatorStyle();
   }

   public void setSeparatorStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getSeparatorStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getSeparatorStyle();
         _syntaxTheme.setSeparatorStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.SEPARATOR_STYLE, oldValue, _syntaxTheme.getSeparatorStyle());
      }
   }

   public SyntaxStyle getWhiteSpaceStyle()
   {
      return _syntaxTheme.getWhiteSpaceStyle();
   }

   public void setWhiteSpaceStyle(SyntaxStyle data)
   {
      if (data == null)
      {
         throw new IllegalArgumentException("SyntaxStyle==null");
      }

      if (_syntaxTheme.getWhiteSpaceStyle() != data)
      {
         final SyntaxStyle oldValue = _syntaxTheme.getWhiteSpaceStyle();
         _syntaxTheme.setWhiteSpaceStyle(data);
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.WHITE_SPACE_STYLE, oldValue, _syntaxTheme.getWhiteSpaceStyle());
      }
   }


   private synchronized PropertyChangeReporter getPropertyChangeReporter()
   {
      if (_propChgReporter == null)
      {
         _propChgReporter = new PropertyChangeReporter(this);
      }

      return _propChgReporter;
   }

   /**
    * @return the useCopyAsRtf
    */
   public boolean isUseCopyAsRtf()
   {
      return _useCopyAsRtf;
   }

   /**
    * @param _useCopyAsRtf the useCopyAsRtf to set
    */
   public void setUseCopyAsRtf(boolean useCopyAsRtf)
   {
      this._useCopyAsRtf = useCopyAsRtf;
   }

}
