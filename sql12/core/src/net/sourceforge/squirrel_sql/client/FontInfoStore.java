package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001-2006 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
/**
 * A store of <TT>FontInfo</TT> objects.
 */
public class FontInfoStore
{
	/** Default one. */
	private FontInfo _defaultFontInfo = new FontInfo();

	/** For statusbars. */
	private FontInfo _statusBarFontInfo;


	/**
	 * Default ctor.
	 */
	public FontInfoStore()
	{
		PropertyChangeListener listener = evt ->
		{
			switch (evt.getPropertyName())
			{
				case "lookAndFeel":
					updateDefaultFontInfo();
					break;

				case "Label.font":
					updateDefaultFontInfo();
					break;
			}
		};
		UIManager.addPropertyChangeListener(listener);
		UIManager.getDefaults().addPropertyChangeListener(listener);
		updateDefaultFontInfo();
	}

	private void updateDefaultFontInfo()
	{
		Font tmp = (Font) UIManager.get("Label.font");
		if (tmp != null)
		{
			FontInfo oldValue = getStatusBarFontInfo();
			double smallerSize = tmp.getSize() * 0.85;
			Font font = tmp.deriveFont(Font.BOLD, Math.max(Math.round(smallerSize), 10f));
			_defaultFontInfo = new FontInfo(font);

			if (_changeSupport != null)
				_changeSupport.fireIndexedPropertyChange("statusBarFontInfo", 0, oldValue, getStatusBarFontInfo());
		}
	}

	/**
	 * Gets the FontInfo for status bars.
	 * 
	 * @return	Returns FontInfo for statusbars
	 */
	public FontInfo getStatusBarFontInfo()
	{
		return _statusBarFontInfo != null ? _statusBarFontInfo : _defaultFontInfo;
	}

	/**
	 * Sets the FontInfo for status bars.
	 * 
	 * @param fi	The new FontInfo for status bars
	 */
	public void setStatusBarFontInfo(FontInfo fi)
	{
		FontInfo oldValue = getStatusBarFontInfo();
		_statusBarFontInfo = fi;

		if (_changeSupport != null)
			_changeSupport.firePropertyChange("statusBarFontInfo", oldValue, getStatusBarFontInfo());
	}

	private PropertyChangeSupport _changeSupport;

	public void setUpStatusBarFont(Component comp)
	{
		comp.setFont(getStatusBarFontInfo().createFont());
		addPropertyChangeListener(new StatusBarFontListener(comp));
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if (_changeSupport == null)
			_changeSupport = new PropertyChangeSupport(this);

		_changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		if (_changeSupport == null)
			return;

		_changeSupport.removePropertyChangeListener(listener);
	}
	
	ReferenceQueue<Component> _discardQueue = new ReferenceQueue<>();

	/*
	 * Avoid memory leaks with components no longer in use.
	 */
	private class StatusBarFontListener
			extends WeakReference<Component>
			implements PropertyChangeListener
	{
		StatusBarFontListener(Component comp)
		{
			super(comp, _discardQueue);
			removeDiscarded();
		}

		private void removeDiscarded()
		{
			StatusBarFontListener ref = (StatusBarFontListener) _discardQueue.poll();
			while (ref != null)
			{
				removePropertyChangeListener(ref);
				ref = (StatusBarFontListener) _discardQueue.poll();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			Component c = get();
			if (c == null)
			{
				removePropertyChangeListener(this);
				return;
			}

			if ("statusBarFontInfo".equals(evt.getPropertyName()))
			{
				c.setFont(getStatusBarFontInfo().createFont());
			}
		}
	}

}

