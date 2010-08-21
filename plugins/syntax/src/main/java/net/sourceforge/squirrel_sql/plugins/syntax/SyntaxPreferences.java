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
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
/**
 * This JavaBean class represents the user specific
 * preferences for this plugin.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SyntaxPreferences implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;


   public interface IPropertyNames {
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
      String USE_OSTER_CONTROL = "useOsterControl";
      String USE_NETBEANS_CONTROL = "useNetbeansControl";
      String USE_RSYNTAX_CONTROL = "useRSyntaxControl";
      String USE_PLAIN_CONTROL = "usePlainControl";
      String WHITE_SPACE_STYLE = "whiteSpaceStyle";
      String TEXT_LIMIT_LINE_VISIBLE = "textLimitLineVisible";
      String TEXT_LIMIT_LINE_WIDTH = "textLimitLineWidth";
      String HIGHLIGHT_CURRENT_LINE = "highlightCurrentLine";
      String LINE_NUMBERS_ENABLED = "lineNumbersEnabled";
    }

	/** Object to handle property change events. */
	private transient PropertyChangeReporter _propChgReporter;

	/** If <TT>true</TT> use the Oster text control else use the standard Java control. */
	private boolean _useOsterTextControl = false;
   private boolean _useNetbeansTextControl = false;
   private boolean _usePlainTextControl = false;
   private boolean _useRSyntaxTextArea = true;

	/** If <TT>true</TT> use the block caret. */
//	private boolean _blockCaretEnabled = false;

	/** If <TT>true</TT> caret should blink. */
//	private boolean _blinkCaret = true;

	/** If <TT>true</TT> show EOL markers. */
//	private boolean _showEndOfLineMarkers = false;

	/** If <TT>true</TT> show matching brackets. */
//	private boolean _bracketHighlighting = true;

	/** If <TT>true</TT> the current line should be highlighted. */
//	private boolean _currentLineHighlighting = true;

	/** If <TT>true</TT> line numbers should be displayed. */
//	private boolean _showLineNumbers = false;

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

//	private int _caretRGB = Color.red.getRGB();
//	private int _selectionRGB = 0xccccff;
//	private int _lineHighlightRGB = Color.lightGray.getRGB();
//	private int _eolMarkerRGB = 0x009999;
//	private int _bracketHighlightRGB = Color.black.getRGB();
//	private int _lineNumberRGB = Color.black.getRGB();

   private boolean _textLimitLineVisible = false;
   private int _textLimitLineWidth = 80;

   private boolean _highlightCurrentLine = true;

   private boolean _lineNumbersEnabled = false;


   public SyntaxPreferences()
	{
		super();

		_columnStyle.setName(IConstants.IStyleNames.COLUMN);
		_columnStyle.setBackgroundRGB(Color.white.getRGB());
		_columnStyle.setTextRGB(-10066432);
		_columnStyle.setBold(false);
		_columnStyle.setItalic(false);

		_commentStyle.setName(IConstants.IStyleNames.COMMENT);
		_commentStyle.setBackgroundRGB(Color.white.getRGB());
		_commentStyle.setTextRGB(Color.lightGray.darker().getRGB());
		_commentStyle.setBold(false);
		_commentStyle.setItalic(false);

		_dataTypeStyle.setName(IConstants.IStyleNames.DATA_TYPE);
		_dataTypeStyle.setBackgroundRGB(Color.white.getRGB());
		_dataTypeStyle.setTextRGB(Color.yellow.darker().getRGB());
		_dataTypeStyle.setBold(false);
		_dataTypeStyle.setItalic(false);

		_errorStyle.setName(IConstants.IStyleNames.ERROR);
		_errorStyle.setBackgroundRGB(Color.white.getRGB());
		_errorStyle.setTextRGB(Color.red.getRGB());
		_errorStyle.setBold(false);
		_errorStyle.setItalic(false);

		_functionStyle.setName(IConstants.IStyleNames.FUNCTION);
		_functionStyle.setBackgroundRGB(Color.white.getRGB());
		_functionStyle.setTextRGB(Color.black.getRGB());
		_functionStyle.setBold(false);
		_functionStyle.setItalic(false);

		_identifierStyle.setName(IConstants.IStyleNames.IDENTIFIER);
		_identifierStyle.setBackgroundRGB(Color.white.getRGB());
		_identifierStyle.setTextRGB(Color.black.getRGB());
		_identifierStyle.setBold(false);
		_identifierStyle.setItalic(false);

		_literalStyle.setName(IConstants.IStyleNames.LITERAL);
		_literalStyle.setBackgroundRGB(Color.white.getRGB());
		_literalStyle.setTextRGB(11546720);
		_literalStyle.setBold(false);
		_literalStyle.setItalic(false);

		_operatorStyle.setName(IConstants.IStyleNames.OPERATOR);
		_operatorStyle.setBackgroundRGB(Color.white.getRGB());
		_operatorStyle.setTextRGB(Color.black.getRGB());
		_operatorStyle.setBold(true);
		_operatorStyle.setItalic(false);

		_reservedWordStyle.setName(IConstants.IStyleNames.RESERVED_WORD);
		_reservedWordStyle.setBackgroundRGB(Color.white.getRGB());
		_reservedWordStyle.setTextRGB(Color.blue.getRGB());
		_reservedWordStyle.setBold(false);
		_reservedWordStyle.setItalic(false);

		_separatorStyle.setName(IConstants.IStyleNames.SEPARATOR);
		_separatorStyle.setBackgroundRGB(Color.white.getRGB());
		_separatorStyle.setTextRGB(0x000080); // Navy.
		_separatorStyle.setBold(false);
		_separatorStyle.setItalic(false);

		_tableStyle.setName(IConstants.IStyleNames.TABLE);
		_tableStyle.setBackgroundRGB(Color.white.getRGB());
		_tableStyle.setTextRGB(-16738048);
		_tableStyle.setBold(false);
		_tableStyle.setItalic(false);

		_whiteSpaceStyle.setName(IConstants.IStyleNames.WHITESPACE);
		_whiteSpaceStyle.setBackgroundRGB(Color.white.getRGB());
		_whiteSpaceStyle.setTextRGB(Color.black.getRGB());
		_whiteSpaceStyle.setBold(false);
		_whiteSpaceStyle.setItalic(false);

//		final TextAreaDefaults dfts = TextAreaDefaults.getDefaults();
//		_columnStyle = dfts.styles[Token.COLUMN];
//		_commentStyle = dfts.styles[Token.COMMENT1];
//		_keyword1Style = dfts.styles[Token.KEYWORD];
//		_keyword2Style = dfts.styles[Token.DATA_TYPE];
//		_keyword3Style = dfts.styles[Token.FUNCTION];
//		_labelStyle = dfts.styles[Token.LABEL];
//		_literalStyle = dfts.styles[Token.LITERAL1];
//		_operatorStyle = dfts.styles[Token.OPERATOR];
//		_otherStyle = dfts.styles[Token.NULL];
//		_tableStyle = dfts.styles[Token.TABLE];
	}

	public Object clone() throws CloneNotSupportedException
	{
		try
		{
			SyntaxPreferences prefs = (SyntaxPreferences)super.clone();
			prefs._propChgReporter = null;

			return prefs;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().removePropertyChangeListener(listener);
	}

	public boolean getUseOsterTextControl()
	{
		return _useOsterTextControl;
	}

	public void setUseOsterTextControl(boolean data)
	{
		if (_useOsterTextControl != data)
		{
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.USE_OSTER_CONTROL, _useOsterTextControl, data);
         _useOsterTextControl = data;
      }
	}


   public boolean getUseNetbeansTextControl()
   {
      return _useNetbeansTextControl;
   }

   public void setUseNetbeansTextControl(boolean data)
   {
      if (_useNetbeansTextControl != data)
      {
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.USE_NETBEANS_CONTROL, _useNetbeansTextControl, data);
         _useNetbeansTextControl = data;
      }
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
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.TEXT_LIMIT_LINE_VISIBLE,
            oldValue, Boolean.valueOf(_textLimitLineVisible));
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
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.TEXT_LIMIT_LINE_WIDTH,
            oldValue, Integer.valueOf(_textLimitLineWidth));
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
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.HIGHLIGHT_CURRENT_LINE,
            oldValue, Boolean.valueOf(_highlightCurrentLine));
      }

   }

   public boolean isLineNumbersEnabled()
   {
      return _lineNumbersEnabled;
   }

   public void setLineNumbersEnabled(boolean data)
   {
      if (_lineNumbersEnabled != data)
      {
         final Boolean oldValue = Boolean.valueOf(_lineNumbersEnabled);
         _lineNumbersEnabled = data;
         getPropertyChangeReporter().firePropertyChange(IPropertyNames.LINE_NUMBERS_ENABLED,
            oldValue, Boolean.valueOf(_lineNumbersEnabled));
      }

   }


   public SyntaxStyle getCommentStyle()
	{
		return _commentStyle;
	}

	public void setCommentStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_commentStyle != data)
		{
			final SyntaxStyle oldValue = _commentStyle;
			_commentStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.COMMENT_STYLE,
				oldValue, _commentStyle);
		}
	}

	public SyntaxStyle getDataTypeStyle()
	{
		return _dataTypeStyle;
	}

	public void setDataTypeStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_dataTypeStyle != data)
		{
			final SyntaxStyle oldValue = _dataTypeStyle;
			_dataTypeStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.DATA_TYPE_STYLE,
				oldValue, _dataTypeStyle);
		}
	}

	public SyntaxStyle getErrorStyle()
	{
		return _errorStyle;
	}

	public void setErrorStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_errorStyle != data)
		{
			final SyntaxStyle oldValue = _errorStyle;
			_errorStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.ERROR_STYLE,
				oldValue, _errorStyle);
		}
	}

	public SyntaxStyle getFunctionStyle()
	{
		return _functionStyle;
	}

	public void setFunctionStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_functionStyle != data)
		{
			final SyntaxStyle oldValue = _functionStyle;
			_functionStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.FUNCTION_STYLE,
				oldValue, _functionStyle);
		}
	}

	public SyntaxStyle getIdentifierStyle()
	{
		return _identifierStyle;
	}

	public void setIdentifierStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_identifierStyle != data)
		{
			final SyntaxStyle oldValue = _identifierStyle;
			_identifierStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.IDENTIFIER_STYLE,
				oldValue, _identifierStyle);
		}
	}


	public SyntaxStyle getLiteralStyle()
	{
		return _literalStyle;
	}

	public void setLiteralStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_literalStyle != data)
		{
			final SyntaxStyle oldValue = _literalStyle;
			_literalStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.LITERAL_STYLE,
				oldValue, _literalStyle);
		}
	}

	public SyntaxStyle getTableStyle()
	{
		return _tableStyle;
	}

	public void setTableStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_tableStyle != data)
		{
			final SyntaxStyle oldValue = _tableStyle;
			_tableStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.TABLE_STYLE,
				oldValue, _tableStyle);
		}
	}

	public SyntaxStyle getColumnStyle()
	{
		return _columnStyle;
	}

	public void setColumnStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_columnStyle != data)
		{
			final SyntaxStyle oldValue = _columnStyle;
			_columnStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.COLUMN_STYLE,
				oldValue, _columnStyle);
		}
	}

	public SyntaxStyle getOperatorStyle()
	{
		return _operatorStyle;
	}

	public void setOperatorStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_operatorStyle != data)
		{
			final SyntaxStyle oldValue = _operatorStyle;
			_operatorStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.OPERATOR_STYLE,
				oldValue, _operatorStyle);
		}
	}

	public SyntaxStyle getReservedWordStyle()
	{
		return _reservedWordStyle;
	}

	public void setReservedWordStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_reservedWordStyle != data)
		{
			final SyntaxStyle oldValue = _reservedWordStyle;
			_reservedWordStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.RESERVED_WORD_STYLE,
				oldValue, _reservedWordStyle);
		}
	}

	public SyntaxStyle getSeparatorStyle()
	{
		return _separatorStyle;
	}

	public void setSeparatorStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_separatorStyle != data)
		{
			final SyntaxStyle oldValue = _separatorStyle;
			_separatorStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SEPARATOR_STYLE,
				oldValue, _separatorStyle);
		}
	}

	public SyntaxStyle getWhiteSpaceStyle()
	{
		return _whiteSpaceStyle;
	}

	public void setWhiteSpaceStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_whiteSpaceStyle != data)
		{
			final SyntaxStyle oldValue = _whiteSpaceStyle;
			_whiteSpaceStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.WHITE_SPACE_STYLE,
				oldValue, _whiteSpaceStyle);
		}
	}




//	public int getCaretRGB()
//	{
//		return _caretRGB;
//	}
//
//	public void setCaretRGB(int data)
//	{
//		if (_caretRGB != data)
//		{
//			final int oldValue = _caretRGB;
//			_caretRGB = data;
//			getPropertyChangeReporter().firePropertyChange(IPropertyNames.CARET_COLOR,
//				oldValue, _caretRGB);
//		}
//	}
//
//	public int getSelectionRGB()
//	{
//		return _selectionRGB;
//	}
//
//	public void setSelectionRGB(int data)
//	{
//		if (_selectionRGB != data)
//		{
//			final int oldValue = _selectionRGB;
//			_selectionRGB = data;
//			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SELECTION_COLOR,
//				oldValue, _selectionRGB);
//		}
//	}
//
//	public int getCurrentLineHighlightRGB()
//	{
//		return _lineHighlightRGB;
//	}
//
//	public void setCurrentLineHighlightRGB(int data)
//	{
//		if (_lineHighlightRGB != data)
//		{
//			final int oldValue = _lineHighlightRGB;
//			_lineHighlightRGB = data;
//			getPropertyChangeReporter().firePropertyChange(IPropertyNames.CURRENT_LINE_HIGHLIGHT_COLOR,
//				oldValue, _lineHighlightRGB);
//		}
//	}
//
//	public int getEOLMarkerRGB()
//	{
//		return _eolMarkerRGB;
//	}
//
//	public void setEOLMarkerRGB(int data)
//	{
//		if (_eolMarkerRGB != data)
//		{
//			final int oldValue = _eolMarkerRGB;
//			_eolMarkerRGB = data;
//			getPropertyChangeReporter().firePropertyChange(IPropertyNames.EOL_MARKER_COLOR,
//				oldValue, _eolMarkerRGB);
//		}
//	}
//
//	public int getBracketHighlightRGB()
//	{
//		return _bracketHighlightRGB;
//	}
//
//	public void setBracketHighlightRGB(int data)
//	{
//		if (_bracketHighlightRGB != data)
//		{
//			final int oldValue = _bracketHighlightRGB;
//			_bracketHighlightRGB = data;
//			getPropertyChangeReporter().firePropertyChange(IPropertyNames.BRACKET_HIGHLIGHT_COLOR,
//				oldValue, _bracketHighlightRGB);
//		}
//	}
//
//	public int getLineNumberRGB()
//	{
//		return _lineNumberRGB;
//	}
//
//	public void setLineNumberRGB(int data)
//	{
//		if (_lineNumberRGB != data)
//		{
//			final int oldValue = _lineNumberRGB;
//			_lineNumberRGB = data;
//			getPropertyChangeReporter().firePropertyChange(IPropertyNames.LINE_NUMBER_COLOR,
//				oldValue, _lineNumberRGB);
//		}
//	}

	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this);
		}

		return _propChgReporter;
	}
}
