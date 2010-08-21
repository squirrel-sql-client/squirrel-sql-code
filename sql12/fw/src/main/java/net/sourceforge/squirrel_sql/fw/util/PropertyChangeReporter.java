package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class PropertyChangeReporter implements Serializable
{
    private static final long serialVersionUID = 1L;

    private boolean _notify = true;
	private Object _srcBean;
	private PropertyChangeSupport _propChgNotifier;

	public PropertyChangeReporter(Object srcBean)
		throws IllegalArgumentException
	{
		super();
		if (srcBean == null)
		{
			throw new IllegalArgumentException("Null srcBean passed");
		}
		_srcBean = srcBean;
	}

	public void setNotify(boolean value)
	{
		_notify = value;
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeNotifier().addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeNotifier().removePropertyChangeListener(listener);
	}

	public void firePropertyChange(
		String propName,
		Object oldValue,
		Object newValue)
	{
		if (_notify)
		{
			getPropertyChangeNotifier().firePropertyChange(
				propName,
				oldValue,
				newValue);
		}
	}

	public void firePropertyChange(
		String propName,
		boolean oldValue,
		boolean newValue)
	{
		if (_notify)
		{
			getPropertyChangeNotifier().firePropertyChange(
				propName,
				oldValue,
				newValue);
		}
	}

	public void firePropertyChange(String propName, int oldValue, int newValue)
	{
		if (_notify)
		{
			getPropertyChangeNotifier().firePropertyChange(
				propName,
				oldValue,
				newValue);
		}
	}

	private PropertyChangeSupport getPropertyChangeNotifier()
	{
		if (_propChgNotifier == null)
		{
			_propChgNotifier = new PropertyChangeSupport(_srcBean);
		}
		return _propChgNotifier;
	}
}
