package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001 Colin Bell
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
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class JeditPreferences implements Serializable, Cloneable {
	public interface IPropertyNames {
		String BLINK_CARET = "blinkCaret";
		String BLOCK_CARET_ENABLED = "blockCaretEnabled";
		String BRACKET_HIGHLIGHTING = "bracketHighlighting";
		String CURRENT_LINE_HIGHLIGHTING = "currentLineHighlighting";
		String EOL_MARKERS = "eolMarkers";
		String USE_JEDIT_CONTROL = "useJeditControl";
		String KEYWORD1_COLOR = "keyword1Color";
		String KEYWORD2_COLOR = "keyword2Color";
		String KEYWORD3_COLOR = "keyword3Color";
	}

	/** Object to handle property change events. */
	private PropertyChangeReporter _propChgReporter = new PropertyChangeReporter(this);

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

	private int _keyword1RGB = Color.black.getRGB();
	private int _keyword2RGB = Color.magenta.getRGB();
	private int _keyword3RGB = Color.red.getRGB();
	
	public JeditPreferences() {
		super();
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_propChgReporter.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_propChgReporter.removePropertyChangeListener(listener);
	}

	public boolean getUseJeditTextControl() {
		return _useJeditTextControl;
	}

	public void setUseJeditTextControl(boolean data) {
		if (_useJeditTextControl != data) {
			final boolean oldValue = _useJeditTextControl;
			_useJeditTextControl = data;
			_propChgReporter.firePropertyChange(IPropertyNames.USE_JEDIT_CONTROL,
								oldValue, _useJeditTextControl);
		}
	}

	public boolean getBracketHighlighting() {
		return _bracketHighlighting;
	}

	public void setBracketHighlighting(boolean data) {
		if (_bracketHighlighting != data) {
			final boolean oldValue = _bracketHighlighting;
			_bracketHighlighting = data;
			_propChgReporter.firePropertyChange(IPropertyNames.BRACKET_HIGHLIGHTING,
								oldValue, _bracketHighlighting);
		}
	}

	public boolean isBlockCaretEnabled() {
		return _blockCaretEnabled;
	}

	public void setBlockCaretEnabled(boolean data) {
		if (_blockCaretEnabled != data) {
			final boolean oldValue = _blockCaretEnabled;
			_blockCaretEnabled = data;
			_propChgReporter.firePropertyChange(IPropertyNames.BLOCK_CARET_ENABLED,
								oldValue, _blockCaretEnabled);
		}
	}

	public boolean getEolMarkers() {
		return _showEndOfLineMarkers;
	}

	public void setEolMarkers(boolean data) {
		if (_showEndOfLineMarkers != data) {
			final boolean oldValue = _showEndOfLineMarkers;
			_showEndOfLineMarkers = data;
			_propChgReporter.firePropertyChange(IPropertyNames.EOL_MARKERS,
								oldValue, _showEndOfLineMarkers);
		}
	}

	public boolean getCurrentLineHighlighting() {
		return _currentLineHighlighting;
	}

	public void setCurrentLineHighlighting(boolean data) {
		if (_currentLineHighlighting != data) {
			final boolean oldValue = _currentLineHighlighting;
			_currentLineHighlighting = data;
			_propChgReporter.firePropertyChange(IPropertyNames.CURRENT_LINE_HIGHLIGHTING,
								oldValue, _currentLineHighlighting);
		}
	}

	public boolean getBlinkCaret() {
		return _blinkCaret;
	}

	public void setBlinkCaret(boolean data) {
		if (_blinkCaret != data) {
			final boolean oldValue = _blinkCaret;
			_blinkCaret = data;
			_propChgReporter.firePropertyChange(IPropertyNames.BLINK_CARET,
								oldValue, _blinkCaret);
		}
	}

	public int getKeyword1RGB() {
		return _keyword1RGB;
	}

	public void setKeyword1RGB(int data) {
		if (_keyword1RGB != data) {
			final int oldValue = _keyword1RGB;
			_keyword1RGB = data;
			_propChgReporter.firePropertyChange(IPropertyNames.KEYWORD1_COLOR,
								oldValue, _keyword1RGB);
		}
	}

	public int getKeyword2RGB() {
		return _keyword2RGB;
	}

	public void setKeyword2RGB(int data) {
		if (_keyword2RGB != data) {
			final int oldValue = _keyword2RGB;
			_keyword2RGB = data;
			_propChgReporter.firePropertyChange(IPropertyNames.KEYWORD2_COLOR,
								oldValue, _keyword2RGB);
		}
	}

	public int getKeyword3RGB() {
		return _keyword3RGB;
	}


	public void setKeyword3RGB(int data) {
		if (_keyword3RGB != data) {
			final int oldValue = _keyword3RGB;
			_keyword3RGB = data;
			_propChgReporter.firePropertyChange(IPropertyNames.KEYWORD3_COLOR,
								oldValue, _keyword3RGB);
		}
	}
}
