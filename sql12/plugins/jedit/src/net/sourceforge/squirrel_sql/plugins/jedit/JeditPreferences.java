package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.syntax.TextAreaDefaults;
import org.gjt.sp.jedit.syntax.Token;

import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
/**
 * This JavaBean class represents the user specific
 * preferences for this plugin.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JeditPreferences implements Serializable, Cloneable
{
	public interface IPropertyNames
	{
		String BLINK_CARET = "blinkCaret";
		String BLOCK_CARET_ENABLED = "blockCaretEnabled";
		String BRACKET_HIGHLIGHTING = "bracketHighlighting";
		String BRACKET_HIGHLIGHT_COLOR = "bracketHighlightColor";
		String CARET_COLOR = "caretColor";
		String COLUMN_STYLE = "columnStyle";
		String COMMENT_STYLE = "commentStyle";
		String CURRENT_LINE_HIGHLIGHTING = "currentLineHighlighting";
		String CURRENT_LINE_HIGHLIGHT_COLOR = "currentLineHighlightColor";
		String EOL_MARKERS = "eolMarkers";
		String EOL_MARKER_COLOR = "eolMarkerColor";
		String KEYWORD1_STYLE = "keyword1Style";
		String KEYWORD2_STYLE = "keyword2Style";
		String KEYWORD3_STYLE = "keyword3Style";
		String LABEL_STYLE = "labelStyle";
		String LINE_NUMBER_COLOR = "lineNumberColor";
		String LITERAL_STYLE = "literalStyle";
		String OPERATOR_STYLE = "operatorStyle";
		String OTHER_STYLE = "otherStyle";
		String SELECTION_COLOR = "selectionColor";
		String SHOW_LINE_NBRS = "showLineNumbers";
		String TABLE_STYLE = "tableStyle";
		String USE_JEDIT_CONTROL = "useJeditControl";
	}

	/** Object to handle property change events. */
	private transient PropertyChangeReporter _propChgReporter;

	/** If <TT>true</TT> use the jEdit text control else use the standard Java control. */
	private boolean _useJeditTextControl = true;

	/** If <TT>true</TT> use the block caret. */
	private boolean _blockCaretEnabled = false;

	/** If <TT>true</TT> caret should blink. */
	private boolean _blinkCaret = true;

	/** If <TT>true</TT> show EOL markers. */
	private boolean _showEndOfLineMarkers = false;

	/** If <TT>true</TT> show matching brackets. */
	private boolean _bracketHighlighting = true;

	/** If <TT>true</TT> the current line should be highlighted. */
	private boolean _currentLineHighlighting = true;

	/** If <TT>true</TT> line numbers should be displayed. */
	private boolean _showLineNumbers = false;

	private SyntaxStyle _columnStyle;
	private SyntaxStyle _commentStyle;
	private SyntaxStyle _keyword1Style;
	private SyntaxStyle _keyword2Style;
	private SyntaxStyle _keyword3Style;
	private SyntaxStyle _labelStyle;
	private SyntaxStyle _literalStyle;
	private SyntaxStyle _operatorStyle;
	private SyntaxStyle _otherStyle;
	private SyntaxStyle _tableStyle;

	private int _caretRGB = Color.red.getRGB();
	private int _selectionRGB = 0xccccff;
	private int _lineHighlightRGB = Color.lightGray.getRGB();
	private int _eolMarkerRGB = 0x009999;
	private int _bracketHighlightRGB = Color.black.getRGB();
	private int _lineNumberRGB = Color.black.getRGB();

	public JeditPreferences()
	{
		super();

		final TextAreaDefaults dfts = TextAreaDefaults.getDefaults();
		_columnStyle = dfts.styles[Token.COLUMN];
		_commentStyle = dfts.styles[Token.COMMENT1];
		_keyword1Style = dfts.styles[Token.KEYWORD];
		_keyword2Style = dfts.styles[Token.DATA_TYPE];
		_keyword3Style = dfts.styles[Token.FUNCTION];
		_labelStyle = dfts.styles[Token.LABEL];
		_literalStyle = dfts.styles[Token.LITERAL1];
		_operatorStyle = dfts.styles[Token.OPERATOR];
		_otherStyle = dfts.styles[Token.NULL];
		_tableStyle = dfts.styles[Token.TABLE];
	}

	public Object clone() throws CloneNotSupportedException
	{
		try
		{
			JeditPreferences prefs = (JeditPreferences)super.clone();
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

	public boolean getUseJeditTextControl()
	{
		return _useJeditTextControl;
	}

	public void setUseJeditTextControl(boolean data)
	{
		if (_useJeditTextControl != data)
		{
			final boolean oldValue = _useJeditTextControl;
			_useJeditTextControl = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.USE_JEDIT_CONTROL,
				oldValue, _useJeditTextControl);
		}
	}

	public boolean getBracketHighlighting()
	{
		return _bracketHighlighting;
	}

	public void setBracketHighlighting(boolean data)
	{
		if (_bracketHighlighting != data)
		{
			final boolean oldValue = _bracketHighlighting;
			_bracketHighlighting = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.BRACKET_HIGHLIGHTING,
				oldValue, _bracketHighlighting);
		}
	}

	public boolean isBlockCaretEnabled()
	{
		return _blockCaretEnabled;
	}

	public void setBlockCaretEnabled(boolean data)
	{
		if (_blockCaretEnabled != data)
		{
			final boolean oldValue = _blockCaretEnabled;
			_blockCaretEnabled = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.BLOCK_CARET_ENABLED,
				oldValue, _blockCaretEnabled);
		}
	}

	public boolean getEOLMarkers()
	{
		return _showEndOfLineMarkers;
	}

	public void setEOLMarkers(boolean data)
	{
		if (_showEndOfLineMarkers != data)
		{
			final boolean oldValue = _showEndOfLineMarkers;
			_showEndOfLineMarkers = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.EOL_MARKERS,
				oldValue, _showEndOfLineMarkers);
		}
	}

	public boolean getCurrentLineHighlighting()
	{
		return _currentLineHighlighting;
	}

	public void setCurrentLineHighlighting(boolean data)
	{
		if (_currentLineHighlighting != data)
		{
			final boolean oldValue = _currentLineHighlighting;
			_currentLineHighlighting = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.CURRENT_LINE_HIGHLIGHTING,
				oldValue, _currentLineHighlighting);
		}
	}

	public boolean getBlinkCaret()
	{
		return _blinkCaret;
	}

	public void setBlinkCaret(boolean data)
	{
		if (_blinkCaret != data)
		{
			final boolean oldValue = _blinkCaret;
			_blinkCaret = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.BLINK_CARET,
				oldValue, _blinkCaret);
		}
	}

	public boolean getShowLineNumbers()
	{
		return _showLineNumbers;
	}

	public void setShowLineNumbers(boolean data)
	{
		if (_showLineNumbers != data)
		{
			final boolean oldValue = _showLineNumbers;
			_showLineNumbers = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SHOW_LINE_NBRS,
				oldValue, _showLineNumbers);
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

	public SyntaxStyle getKeyword1Style()
	{
		return _keyword1Style;
	}

	public void setKeyword1Style(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_keyword1Style != data)
		{
			final SyntaxStyle oldValue = _keyword1Style;
			_keyword1Style = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.KEYWORD1_STYLE,
				oldValue, _keyword1Style);
		}
	}

	public SyntaxStyle getKeyword2Style()
	{
		return _keyword2Style;
	}

	public void setKeyword2Style(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_keyword2Style != data)
		{
			final SyntaxStyle oldValue = _keyword2Style;
			_keyword2Style = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.KEYWORD2_STYLE,
				oldValue, _keyword2Style);
		}
	}

	public SyntaxStyle getKeyword3Style()
	{
		return _keyword3Style;
	}

	public void setKeyword3Style(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_keyword3Style != data)
		{
			final SyntaxStyle oldValue = _keyword3Style;
			_keyword3Style = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.KEYWORD3_STYLE,
				oldValue, _keyword3Style);
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

	public SyntaxStyle getLabelStyle()
	{
		return _labelStyle;
	}

	public void setLabelStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_labelStyle != data)
		{
			final SyntaxStyle oldValue = _labelStyle;
			_labelStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.LABEL_STYLE,
				oldValue, _labelStyle);
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

	public SyntaxStyle getOtherStyle()
	{
		return _otherStyle;
	}

	public void setOtherStyle(SyntaxStyle data)
	{
		if (data == null)
		{
			throw new IllegalArgumentException("SyntaxStyle==null");
		}

		if (_otherStyle != data)
		{
			final SyntaxStyle oldValue = _otherStyle;
			_otherStyle = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.OTHER_STYLE,
				oldValue, _otherStyle);
		}
	}

	public int getCaretRGB()
	{
		return _caretRGB;
	}

	public void setCaretRGB(int data)
	{
		if (_caretRGB != data)
		{
			final int oldValue = _caretRGB;
			_caretRGB = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.CARET_COLOR,
				oldValue, _caretRGB);
		}
	}

	public int getSelectionRGB()
	{
		return _selectionRGB;
	}

	public void setSelectionRGB(int data)
	{
		if (_selectionRGB != data)
		{
			final int oldValue = _selectionRGB;
			_selectionRGB = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.SELECTION_COLOR,
				oldValue, _selectionRGB);
		}
	}

	public int getCurrentLineHighlightRGB()
	{
		return _lineHighlightRGB;
	}

	public void setCurrentLineHighlightRGB(int data)
	{
		if (_lineHighlightRGB != data)
		{
			final int oldValue = _lineHighlightRGB;
			_lineHighlightRGB = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.CURRENT_LINE_HIGHLIGHT_COLOR,
				oldValue, _lineHighlightRGB);
		}
	}

	public int getEOLMarkerRGB()
	{
		return _eolMarkerRGB;
	}

	public void setEOLMarkerRGB(int data)
	{
		if (_eolMarkerRGB != data)
		{
			final int oldValue = _eolMarkerRGB;
			_eolMarkerRGB = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.EOL_MARKER_COLOR,
				oldValue, _eolMarkerRGB);
		}
	}

	public int getBracketHighlightRGB()
	{
		return _bracketHighlightRGB;
	}

	public void setBracketHighlightRGB(int data)
	{
		if (_bracketHighlightRGB != data)
		{
			final int oldValue = _bracketHighlightRGB;
			_bracketHighlightRGB = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.BRACKET_HIGHLIGHT_COLOR,
				oldValue, _bracketHighlightRGB);
		}
	}

	public int getLineNumberRGB()
	{
		return _lineNumberRGB;
	}

	public void setLineNumberRGB(int data)
	{
		if (_lineNumberRGB != data)
		{
			final int oldValue = _lineNumberRGB;
			_lineNumberRGB = data;
			getPropertyChangeReporter().firePropertyChange(IPropertyNames.LINE_NUMBER_COLOR,
				oldValue, _lineNumberRGB);
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
}
