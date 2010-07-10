package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
/**
 * This sheet shows the contents of a HTML file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HtmlViewerSheet extends JFrame
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(HtmlViewerSheet.class);

	/** Application API. */
	private final IApplication _app;

	/** Toolbar for window. */
	private ToolBar _toolBar;

	/** Original URL (home). */
	private URL _documentURL;

	/** Current URL. */
	private URL _currentURL;
	
	/** Text area containing the HTML. */
	private JEditorPane _contentsTxt = new JEditorPane();

	/** <TT>JScrollPane</TT> for <TT>_contentsText</TT>. */
	private JScrollPane _contentsScrollPane;

	/** History of links. */
	private List _history = new LinkedList();

	/** Current index into <TT>_history</TT>. */
	private int _historyIndex = -1;

  	public HtmlViewerSheet(IApplication app, String title) throws IOException
	{
		this(app, title, null);
	}

  	public HtmlViewerSheet(IApplication app, String title, URL url)
		throws IOException
	{
		super(title);//, true, true, true, true);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		_app = app;
		createGUI();
		read(url);
	}

	public synchronized void read(URL url) throws IOException
	{
		if (url != null)
		{
			_documentURL = url;
	
			CursorChanger cursorChg = new CursorChanger(this);
			cursorChg.show();
			try
			{
	// Causes NPE in JDK 1.3.1
	//			_contentsTxt.setText("");
				displayURL(url);
				_history.add(url);
				_historyIndex = 0;
			}
			finally
			{
				cursorChg.restore();
			}
		}
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

	public void goBack()
	{
		if (_historyIndex > 0 && _historyIndex < _history.size())
		{
			displayURL((URL)_history.get(--_historyIndex));
		}
	}

	public void goForward()
	{
		if (_historyIndex > -1 && _historyIndex < _history.size() - 1)
		{
			displayURL((URL)_history.get(++_historyIndex));
		}
	}

	public void refreshPage()
	{
		final Point pos = _contentsScrollPane.getViewport().getViewPosition();
		displayURL(_currentURL);
		_contentsScrollPane.getViewport().setViewPosition(pos);
	}

	private void displayURL(URL url)
	{
		if (url != null)
		{
			try
			{
				_contentsTxt.setPage(url);
				_currentURL = url;
			}
			catch (Exception ex)
			{
				s_log.error(ex);
			}
		}
	}

	/**
	 * Create user interface.
	 */
	private void createGUI()
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		GUIUtils.makeToolWindow(this, true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(createMainPanel(), BorderLayout.CENTER);
		contentPane.add(createToolBar(), BorderLayout.NORTH);
		final SquirrelResources rsrc = _app.getResources();
		final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.APPLICATION_ICON);
		if (icon != null)
		{
			setIconImage(icon.getImage());
		}
		pack();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_contentsTxt.requestFocus();
			}
		});
	}

	private ToolBar createToolBar()
	{
		_toolBar = new ToolBar();
		_toolBar.setBorder(BorderFactory.createEtchedBorder());
		_toolBar.setUseRolloverButtons(true);
		_toolBar.setFloatable(false);
		_toolBar.add(new HomeAction(_app));
		_toolBar.add(new BackAction(_app));
		_toolBar.add(new ForwardAction(_app));
		_toolBar.add(new RefreshAction(_app));
		_toolBar.add(new CloseAction(_app));
		return _toolBar;
	}

	/**
	 * Create the main panel.
	 */
	private JPanel createMainPanel()
	{
		_contentsTxt.setEditable(false);
		_contentsTxt.setContentType("text/html");
		final TextPopupMenu pop = new TextPopupMenu();
		pop.setTextComponent(_contentsTxt);
		_contentsTxt.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		final JPanel pnl = new JPanel(new BorderLayout());
		_contentsTxt.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		_contentsTxt.addHyperlinkListener(createHyperLinkListener());
		_contentsScrollPane = new JScrollPane(_contentsTxt,
									JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pnl.add(_contentsScrollPane, BorderLayout.CENTER);

		return pnl;
	}

	private HyperlinkListener createHyperLinkListener()
	{
		return new HyperlinkListener()
		{
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					if (e instanceof HTMLFrameHyperlinkEvent)
					{
						((HTMLDocument)_contentsTxt.getDocument()).processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent)e);
					}
					else
					{
						try
						{
							final URL url = e.getURL();
							ListIterator it = _history.listIterator(_historyIndex + 1);
							while (it.hasNext())
							{
								it.next();
								it.remove();
							}
							_history.add(url);
							_historyIndex = _history.size() - 1;
							_contentsTxt.setPage(url);
							_currentURL = url;
						}
						catch (IOException ioe)
						{
							s_log.error(ioe);
						}
					}
				}
			}
		};
	}

	private final class CloseAction extends SquirrelAction
	{
		public CloseAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			dispose();
		}
	}

	private final class BackAction extends SquirrelAction
	{
		public BackAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			goBack();
		}
	}

	private final class ForwardAction extends SquirrelAction
	{
		public ForwardAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			goForward();
		}
	}

	private final class RefreshAction extends SquirrelAction
	{
		public RefreshAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			refreshPage();
		}
	}

	private final class HomeAction extends SquirrelAction
	{
		public HomeAction(IApplication app)
		{
			super(app);
			if (app == null)
			{
				throw new IllegalArgumentException("Null IApplication passed");
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			try
			{
				read(_documentURL);
			}
			catch (IOException ex)
			{
				s_log.error("Error reading URL", ex);
			}
		}
	}

}
	