package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-20064 Colin Bell
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
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.Serializable;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.util.beanwrapper.RectangleWrapper;
import net.sourceforge.squirrel_sql.fw.xml.IXMLAboutToBeWritten;
/**
 * This bean will store the state of a window or an internal frame object.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class WindowState implements IXMLAboutToBeWritten, Serializable
{
	private static final long serialVersionUID = 2664203798124718385L;

	/**
	 * Window whose state is being stored. Only one of <TT>_window</TT>,
	 * <TT>_frame</tt> and <TT>_internalFrame</TT> can be non-null.
	 */
	private Window _window;

	/**
	 * JInternalFrame whose state is being stored. Only one of <TT>_window</TT>,
	 * <TT>_frame</tt> and <TT>_internalFrame</TT> can be non-null.
	 */
	private JInternalFrame _internalFrame;

	/**
	 * Frame whose state is being stored. Only one of <TT>_window</TT>,
	 * <TT>_frame</tt> and <TT>_internalFrame</TT> can be non-null.
	 */
	private Frame _frame;

	/** Window bounds. */
	private RectangleWrapper _bounds = new RectangleWrapper(new Rectangle(600, 400));

	/** Was the window visible. */
	private boolean _visible = true;
	
	/** Extended state for frame only. */
	private int _frameExtendedState = 0;

	public interface IPropertyNames
	{
		String BOUNDS = "bounds";
		String FRAME_EXTENDED_STATE = "frameExtendedState";
		String VISIBLE = "visible";
	}

	/**
	 * Default ctor.
	 */
	public WindowState()
	{
		super();
	}

	/**
	 * Ctor storing the state of the passed <CODE>Window</CODE>.
	 *
	 * @param	window	Window to store the state of.
	 */
	public WindowState(Window window)
	{
		super();
		_window = window;
	}

	/**
	 * Ctor storing the state of the passed <CODE>JInternalFrame</CODE>.
	 *
	 * @param	internalFrame	JInternalFrame to store the state of.
	 */
	public WindowState(JInternalFrame internalFrame)
	{
		super();
		_internalFrame = internalFrame;
	}

	/**
	 * Ctor storing the state of the passed <CODE>Frame</CODE>.
	 *
	 * @param	frame	frame to store the state of.
	 */
	public WindowState(Frame frame)
	{
		super();
		_frame = frame;
	}

	/**
	 * Set this objects state to that of the passed object. Think of this as
	 * being like an assignment operator
	 *
	 * @param	obj		Object to copy state from
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>WindowState</tt> passed.
	 */
	public void copyFrom(WindowState obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("WindowState == null");
		}

		setBounds(obj.getBounds());
		setVisible(obj.isVisible());
		setFrameExtendedState(obj.getFrameExtendedState());
	}

	/**
	 * This bean is about to be written out to XML so load its values from its
	 * window.
	 */
	public void aboutToBeWritten()
	{
		refresh();
	}

	public RectangleWrapper getBounds()
	{
		refresh();
		return _bounds;
	}

	public void setBounds(RectangleWrapper value)
	{
		_bounds = value;
		_window = null;
		_internalFrame = null;
	}

	public boolean isVisible()
	{
		refresh();
		return _visible;
	}

	public void setVisible(boolean value)
	{
		_visible = value;
	}

	public int getFrameExtendedState()
	{
		refresh();
		return _frameExtendedState;
	}

	public void setFrameExtendedState(int value)
	{
		_frameExtendedState = value;
	}

	private void refresh()
	{
		Rectangle windRc = null;
		if (_window != null)
		{
			windRc = _window.getBounds();
			_visible = _window.isVisible();
		}
		else if (_internalFrame != null)
		{
			windRc = _internalFrame.getBounds();
			_visible = _internalFrame.isVisible();
		}
		else if (_frame != null)
		{
			windRc = _frame.getBounds();
			_visible = _frame.isVisible();
			_frameExtendedState = _frame.getExtendedState();
		}

		if (windRc != null)
		{
			if (_bounds == null)
			{
				_bounds = new RectangleWrapper();
			}
			_bounds.setFrom(windRc);
		}
	}
}
