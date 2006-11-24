package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2003 -2006 Colin Bell
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
import java.util.EventObject;
/**
 * This class is an event fired for changes in the HtmlViewerPanel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HtmlViewerPanelListenerEvent extends EventObject
{
	/** The <CODE>HtmlViewerPanel</CODE> involved. */
	private HtmlViewerPanel _pnl;
	/**
	 * Ctor.
	 *
	 * @param	source	The <CODE>HtmlViewerPanel</CODE> that change has
	 *					happened to.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT>HtmlViewerPanel/TT> passed.
	 */
	HtmlViewerPanelListenerEvent(HtmlViewerPanel source)
	{
		super(checkParams(source));
		_pnl = source;
	}
	/**
	 * Return the <CODE>HtmlViewerPanel</CODE>.
	 */
	public HtmlViewerPanel getHtmlViewerPanel()
	{
		return _pnl;
	}
	private static HtmlViewerPanel checkParams(HtmlViewerPanel source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("HtmlViewerPanel == null");
		}
		return source;
	}
}