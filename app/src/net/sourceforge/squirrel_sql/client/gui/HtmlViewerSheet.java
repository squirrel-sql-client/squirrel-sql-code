package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.awt.BorderLayout;
import java.awt.Container;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
/**
 * This sheet shows the contents of a HTML file.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HtmlViewerSheet extends JFrame
{
	/** Application API. */
	private final IApplication _app;

	/** Original URL (home). */
	private URL _documentURL;

 	public HtmlViewerSheet(IApplication app, String title)
	{
		this(app, title, null);
	}

 	public HtmlViewerSheet(IApplication app, String title, URL url)
	{
		super(title);//, true, true, true, true);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		_app = app;
		_documentURL = url;
		createGUI();
	}

	/**
	 * Return the URL being displayed.
	 *
	 * @return	URL being displayed.
	 */
	public URL getURL()
	{
		return _documentURL;
	}

	/**
	 * Create user interface.
	 */
	private void createGUI() //throws IOException
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new HtmlViewerPanel(_documentURL), BorderLayout.CENTER);
		final SquirrelResources rsrc = _app.getResources();
		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);
		if (icon != null)
		{
			setIconImage(icon.getImage());
		}
		pack();
	}
}

