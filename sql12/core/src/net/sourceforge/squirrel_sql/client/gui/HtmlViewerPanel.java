package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2003 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
/**
 * This panel shows the contents of a HTML file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class HtmlViewerPanel extends JPanel
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(HtmlViewerPanel.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(HtmlViewerPanel.class);    
    
	/** Text area containing the HTML. */
	private final JEditorPane _contentsTxt = new JEditorPane()
	{
		@Override public void paint(Graphics g)
		{
			if (g instanceof Graphics2D)
			{
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				super.paint(g2);
				g2.dispose();
			}
			else
			{
				super.paint(g);
			}
		}
	};

	/** <TT>JScrollPane</TT> for <TT>_contentsText</TT>. */
	private JScrollPane _contentsScrollPane;

	/** Original URL (home). */
	private URL _homeURL;

	/** Current URL. */
	private URL _currentURL;

	/** History of links. */
	private final List<URL> _history = new LinkedList<URL>();

	/** Current index into <TT>_history</TT>. */
	private int _historyIndex = -1;

	/**
	 * Collection of listeners for events in this object.
	 */
	private EventListenerList _listenerList = new EventListenerList();

	private StyleSheet _styleSheet;

	/**
	 * Ctor.
	 * 
	 * @param	url	URL to home document.
	 */
	public HtmlViewerPanel(URL url)
	{
		super();
		createGUI();
		setHomeURL(url);
		setURL(url);
	}

	/**
	 * Retrieve the current URL.
	 * 
	 * @return	The current URL.
	 */
	public URL getURL()
	{
		return _currentURL;
	}

	/**
	 * Retrieve the home URL.
	 * 
	 * @return	The home URL.
	 */
	public URL getHomeURL()
	{
		return _homeURL;
	}

	/**
	 * Specify the URL that is to the consider the &quot;Home&quot; URL. THis
	 * does <EM>not</EM> change the current URL for this viewer.
	 * 
	 * @param	homeURL		The new home URL.
	 */
	public void setHomeURL(URL homeURL)
	{
		_homeURL = homeURL;
		fireHomeURLChanged();
	}

	public void setStyleURL(String urlSpec)
	{
		if (urlSpec == null)
		{
			_styleSheet = null;
			return;
		}

		try
		{
			URL baseURL = getHomeURL();
			URL styleURL = (baseURL != null)
			               ? baseURL.toURI().resolve(urlSpec).toURL()
			               : new URL(urlSpec);

			_styleSheet = new StyleSheet();
			try (InputStream in = styleURL.openStream();
					Reader rules = new InputStreamReader(in))
			{
				_styleSheet.loadRules(rules, styleURL);
			}
		}
		catch (URISyntaxException | IOException e)
		{
			s_log.warn("Problem loading style sheet: " + urlSpec, e);
		}
	}

	public int getFontSize()
	{
		return _contentsTxt.getFont().getSize();
	}

	public void setFontSize(int size)
	{
		_contentsTxt.setFont(_contentsTxt.getFont().deriveFont((float) size));
	}

	/**
	 * Adds a listener to this object.
	 *
	 * @param	lis		Listener to be added.
	 */
	public void addListener(IHtmlViewerPanelListener lis)
	{
		_listenerList.add(IHtmlViewerPanelListener.class, lis);
	}

	/**
	 * Removes a listener from this object.
	 *
	 * @param	lis	Listener to be removed.
	 */
	void removeListener(IHtmlViewerPanelListener lis)
	{
		_listenerList.remove(IHtmlViewerPanelListener.class, lis);
	}

	public synchronized void gotoURL(URL url) throws IOException
	{
		if (url == null)
		{
			throw new IllegalArgumentException("URL == null");
		}
		if (!url.equals(_currentURL))
		{
			ListIterator<URL> it = _history.listIterator(_historyIndex + 1);
			while (it.hasNext())
			{
				it.next();
				it.remove();
			}
			_history.add(url);
			_historyIndex = _history.size() - 1;
			_contentsTxt.setPage(url);
			_currentURL = url;
			fireURLChanged();
		}
		else if (url.getRef() != null)
		{
			// Scroll to fragment.
			_contentsTxt.setPage(url);
		}
	}

	public synchronized void goBack()
	{
		if (_historyIndex > 0 && _historyIndex < _history.size())
		{
			displayURL(_history.get(--_historyIndex));
		}
	}

	public synchronized void goForward()
	{
		if (_historyIndex > -1 && _historyIndex < _history.size() - 1)
		{
			displayURL(_history.get(++_historyIndex));
		}
	}

	public synchronized void goHome()
	{
		_historyIndex = 0;
		displayURL(_homeURL);
	}

	public void refreshPage()
	{
		_contentsScrollPane.putClientProperty("savedScrollPosition", _contentsScrollPane.getViewport().getViewPosition());
		_contentsTxt.getDocument().putProperty(Document.StreamDescriptionProperty, null);
		displayURL(_currentURL);
	}

	/**
	 * Displayed the passed URL in this panel.
	 * 
	 * @param	url		URL to be displayed.
	 */
	private synchronized void setURL(URL url)
	{
		if (url != null)
		{
			CursorChanger cursorChg = new CursorChanger(this);
			cursorChg.show();
			try
			{
				// Causes NPE in JDK 1.3.1
				//_contentsTxt.setText("");
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
	 * Display the passed URL. This method does not affect the URL history, it
	 * merely displays the URL.
	 * 
	 * @param	url	The URL to display.
	 */
	private void displayURL(URL url)
	{
		if (url != null)
		{
			try
			{
				_contentsTxt.setPage(url);
				_currentURL = url;
				fireURLChanged();
			}
			catch (Exception ex)
			{
                // i18n[HtmlViewerPanel.error.displayurl=Error displaying URL]
				s_log.error(s_stringMgr.getString("HtmlViewerPanel.error.displayurl"), ex);
			}
		}
	}

	/**
	 * Fire a "URL changed" event to all listeners.
	 */
	private void fireURLChanged()
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		HtmlViewerPanelListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IHtmlViewerPanelListener.class)
			{
				// Lazily create the event.
				if (evt == null)
				{
					evt = new HtmlViewerPanelListenerEvent(this);
				}
				((IHtmlViewerPanelListener)listeners[i + 1]).currentURLHasChanged(evt);
			}
		}
	}

	/**
	 * Fire a "Home URL changed" event to all listeners.
	 */
	private void fireHomeURLChanged()
	{
		// Guaranteed to be non-null.
		Object[] listeners = _listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event.
		HtmlViewerPanelListenerEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IHtmlViewerPanelListener.class)
			{
				// Lazily create the event.
				if (evt == null)
				{
					evt = new HtmlViewerPanelListenerEvent(this);
				}
				((IHtmlViewerPanelListener)listeners[i + 1]).homeURLHasChanged(evt);
			}
		}
	}

	/**
	 * Create user interface.
	 */
	private void createGUI()
	{
		setLayout(new BorderLayout());
		add(createMainPanel(), BorderLayout.CENTER);
	}

	/**
	 * Create the main panel.
	 */
	private JPanel createMainPanel()
	{
		Runnable keepEditableColors = () -> {
			_contentsTxt.setEditable(true);
			Color bg = _contentsTxt.getBackground();
			Color fg = _contentsTxt.getForeground();
			_contentsTxt.setEditable(false);
			_contentsTxt.setBackground(bg);
			_contentsTxt.setForeground(fg);
		};
		keepEditableColors.run();
		_contentsTxt.addPropertyChangeListener("UI", evt -> keepEditableColors.run());
		_contentsTxt.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		//_contentsTxt.putClientProperty(JEditorPane.W3C_LENGTH_UNITS, true);
		_contentsTxt.setContentType("text/html");
		_contentsTxt.setEditorKitForContentType("text/html", new HTMLEditorKit()
		{
			@Override public Document createDefaultDocument()
			{
				HTMLDocument doc = (HTMLDocument) super.createDefaultDocument();
				if (_styleSheet != null)
				{
					doc.getStyleSheet().addStyleSheet(_styleSheet);
					doc.getStyleSheet().addRule("body { font-size: " + getFontSize() + "}");
				}
				return doc;
			}
		});
		_contentsTxt.addPropertyChangeListener("font", evt ->
		{
			Object newFont = evt.getNewValue();
			if (newFont instanceof Font)
			{
				Font oldFont = (Font) evt.getOldValue();
				int fontSize = ((Font) newFont).getSize();
				firePropertyChange("fontSize",
						oldFont == null ? -1 : oldFont.getSize(), fontSize);
				Document document = _contentsTxt.getDocument();
				if (document instanceof HTMLDocument)
				{
					((HTMLDocument) document).getStyleSheet()
							.addRule("body { font-size: " + fontSize + " }");
				}
			}
		});
		_contentsTxt.addPropertyChangeListener("page", evt ->
		{
			Object pos = _contentsScrollPane.getClientProperty("savedScrollPosition");
			if (pos instanceof Point)
			{
				_contentsScrollPane.getViewport().setViewPosition((Point) pos);
				_contentsScrollPane.putClientProperty("savedScrollPosition", null);
			}
		});
		final TextPopupMenu pop = new TextPopupMenu();
		pop.setTextComponent(_contentsTxt);
		_contentsTxt.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
			public void mouseReleased(MouseEvent evt)
			{
				if (evt.isPopupTrigger())
				{
					pop.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		final JPanel pnl = new JPanel(new BorderLayout());
		_contentsTxt.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
		_contentsTxt.addHyperlinkListener(createHyperLinkListener());
		_contentsScrollPane = new JScrollPane(_contentsTxt,
									JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
									JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pnl.add(_contentsScrollPane, BorderLayout.CENTER);

		initKeyBindings();

		return pnl;
	}

	private void initKeyBindings()
	{
		InputMap scrollKeys = new InputMap();
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "scrollDown");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.SHIFT_DOWN_MASK), "scrollUp");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "scrollDown");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "scrollUp");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "scrollHome");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "scrollEnd");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "unitScrollDown");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "unitScrollUp");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "unitScrollRight");
		scrollKeys.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "unitScrollLeft");

		InputMap scrollInput = _contentsScrollPane.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		scrollKeys.setParent(scrollInput.getParent());
		scrollInput.setParent(scrollKeys);

		InputMap editKeys = _contentsTxt.getInputMap();
		for (KeyStroke keyStroke : scrollKeys.keys())
		{
			removeKey(editKeys, keyStroke);
		}
	}

	private static void removeKey(InputMap inputMap, KeyStroke keyStroke)
	{
		Object command = inputMap.get(keyStroke);
		if (command == null)
			return;

		InputMap current = inputMap;
		while (current != null) {
			current.remove(keyStroke);
			current = current.getParent();
		}
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
							gotoURL(e.getURL());
						}
						catch (IOException ex)
						{
                            // i18n[HtmlViewerPanel.error.processhyperlink=Error processing hyperlink]
							s_log.error(s_stringMgr.getString("HtmlViewerPanel.error.processhyperlink"), ex);
						}
					}
				}
			}
		};
	}
}
