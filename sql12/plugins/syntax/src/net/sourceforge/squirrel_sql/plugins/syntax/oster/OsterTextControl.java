package net.sourceforge.squirrel_sql.plugins.syntax.oster;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This is based on the text editor demonstration class that comes with
 * the Ostermiller Syntax Highlighter Copyright (C) 2001 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Syntax+Highlighting
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
import java.awt.Component;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.Ostermiller.Syntax.Lexer.Lexer;
import com.Ostermiller.Syntax.Lexer.SQLLexer;
import com.Ostermiller.Syntax.Lexer.Token;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.SchemaInfo;

import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;

class OsterTextControl extends JTextPane
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(OsterTextControl.class);

	/** Current session. */
	private final ISession _session;

	/**
	 * A lock for modifying the document, or for
	 * actions that depend on the document not being
	 * modified.
	 */
	private Object doclock = new Object();

	/**
	 * The styled document that is the model for
	 * the textPane.
	 */
	private HighLightedDocument document;

	/**
	 * A reader wrapped around the document
	 * so that the document can be fed into
	 * the lexer.
	 */
	private DocumentReader documentReader;

	/**
	 * The lexer that tells us what colors different
	 * words should be.
	 */
	private Lexer syntaxLexer;

	/**
	 * A thread that handles the actual coloring.
	 */
	private Colorer colorer;

	/**
	 * A hash table containing the text styles.
	 * Simple attribute sets are hashed by name (String)
	 */
	private Hashtable styles = new Hashtable();

	/** Preferences for this plugin. */
	private final SyntaxPreferences _syntaxPrefs;

	private Vector _sqlTokenListeners = new Vector();

	OsterTextControl(ISession session, SyntaxPreferences prefs)
	{
		super();
		_session = session;
		_syntaxPrefs = prefs;

		document = new HighLightedDocument();
		setDocument(document);

		// Start the thread that does the coloring
		colorer = new Colorer();
		colorer.start();

		// Set up the hash table that contains the styles.
		initStyles();

		// create the new document.
		documentReader = new DocumentReader(document);

		// Put the initial text into the text pane and
		// set it's initial coloring style.
		initDocument();

		updateFromPreferences();
	}

	// This stops the text control from line wrapping.
	public boolean getScrollableTracksViewportWidth()
	{
		final Component parent = getParent();
		final ComponentUI ui = getUI();

		if (parent != null)
		{
			return (ui.getPreferredSize(this).width <= parent.getSize().width);
		}
		return true;
	}

	void updateFromPreferences()
	{
		synchronized (doclock)
		{
			final FontInfo fi = _session.getProperties().getFontInfo();
			SyntaxStyle style;
			SimpleAttributeSet attribs;

			style = _syntaxPrefs.getColumnStyle();
			attribs = getMyStyle(IConstants.IStyleNames.COLUMN);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getCommentStyle();
			attribs = getMyStyle(IConstants.IStyleNames.COMMENT);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getDataTypeStyle();
			attribs = getMyStyle(IConstants.IStyleNames.DATA_TYPE);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getErrorStyle();
			attribs = getMyStyle(IConstants.IStyleNames.ERROR);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getFunctionStyle();
			attribs = getMyStyle(IConstants.IStyleNames.FUNCTION);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getIdentifierStyle();
			attribs = getMyStyle(IConstants.IStyleNames.IDENTIFIER);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getLiteralStyle();
			attribs = getMyStyle(IConstants.IStyleNames.LITERAL);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getOperatorStyle();
			attribs = getMyStyle(IConstants.IStyleNames.OPERATOR);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getReservedWordStyle();
			attribs = getMyStyle(IConstants.IStyleNames.RESERVED_WORD);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getSeparatorStyle();
			attribs = getMyStyle(IConstants.IStyleNames.SEPARATOR);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getTableStyle();
			attribs = getMyStyle(IConstants.IStyleNames.TABLE);
			applyStyle(attribs, style, fi);

			style = _syntaxPrefs.getWhiteSpaceStyle();
			attribs = getMyStyle(IConstants.IStyleNames.WHITESPACE);
			applyStyle(attribs, style, fi);

			colorAll();
		}
	}

	/**
	 * Color or recolor the entire document
	 */
	public void colorAll()
	{
		color(0, document.getLength());
	}

	/**
	 * Color a section of the document.
	 * The actual coloring will start somewhere before
	 * the requested position and continue as long
	 * as needed.
	 *
	 * @param position		the starting point for the coloring.
	 * @param adjustment	amount of text inserted or removed
	 *						at the starting point.
	 */
	public void color(int position, int adjustment)
	{
		colorer.color(position, adjustment);
	}

	/**
	 * retrieve the style for the given type of text.
	 *
	 * @param styleName	the label for the type of text ("tag" for example)
	 *					or null if the styleName is not known.
	 * @return the style
	 */
	private SimpleAttributeSet getMyStyle(String styleName)
	{
		return ((SimpleAttributeSet)styles.get(styleName));
	}

	private Token getNextToken(SchemaInfo si) throws IOException
	{
		return syntaxLexer.getNextToken();
	}

	private void applyStyle(SimpleAttributeSet attribs, SyntaxStyle style,
								FontInfo fi)
	{
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, new Color(style.getBackgroundRGB()));
		StyleConstants.setForeground(attribs, new Color(style.getTextRGB()));
		StyleConstants.setBold(attribs, style.isBold());
		StyleConstants.setItalic(attribs, style.isItalic());
	}

	/**
	 * Set the initial type of syntax highlighting.
	 */
	private void initDocument()
	{
		syntaxLexer = new SQLLexer(documentReader);
		try
		{
			document.insertString(document.getLength(), "", getMyStyle("text"));
		}
		catch (BadLocationException ex)
		{
			s_log.error("Error setting initial document style", ex);
		}
	}

	/**
	 * Create the styles and place them in the hash table.
	 */
	private void initStyles()
	{
		final FontInfo fi = _session.getProperties().getFontInfo();

		SyntaxStyle style;
		SimpleAttributeSet attribs;

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, Color.black);
		StyleConstants.setBold(attribs, false);
		StyleConstants.setItalic(attribs, false);
		styles.put("body", attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, Color.blue);
		StyleConstants.setBold(attribs, true);
		StyleConstants.setItalic(attribs, false);
		styles.put("tag", attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, Color.blue);
		StyleConstants.setBold(attribs, false);
		StyleConstants.setItalic(attribs, false);
		styles.put("endtag", attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, Color.black);
		StyleConstants.setBold(attribs, false);
		StyleConstants.setItalic(attribs, false);
		styles.put("reference", attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, new Color(0xB03060)/*Color.maroon*/);
		StyleConstants.setBold(attribs, true);
		StyleConstants.setItalic(attribs, false);
		styles.put("name", attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, new Color(0xB03060)/*Color.maroon*/);
		StyleConstants.setBold(attribs, false);
		StyleConstants.setItalic(attribs, true);
		styles.put("value", attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, Color.black);
		StyleConstants.setBold(attribs, true);
		StyleConstants.setItalic(attribs, false);
		styles.put("text", attribs);

		style = _syntaxPrefs.getColumnStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.COLUMN, attribs);

		style = _syntaxPrefs.getCommentStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.COMMENT, attribs);

		style = _syntaxPrefs.getDataTypeStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.DATA_TYPE, attribs);

		style = _syntaxPrefs.getErrorStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.ERROR, attribs);

		style = _syntaxPrefs.getFunctionStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.FUNCTION, attribs);

		style = _syntaxPrefs.getIdentifierStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.IDENTIFIER, attribs);

		style = _syntaxPrefs.getLiteralStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.LITERAL, attribs);

		style = _syntaxPrefs.getOperatorStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.OPERATOR, attribs);

		style = _syntaxPrefs.getReservedWordStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.RESERVED_WORD, attribs);

		style = _syntaxPrefs.getSeparatorStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.SEPARATOR, attribs);

		style = _syntaxPrefs.getTableStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.TABLE, attribs);

		style = _syntaxPrefs.getWhiteSpaceStyle();
		attribs = new SimpleAttributeSet();
		applyStyle(attribs, style, fi);
		styles.put(IConstants.IStyleNames.WHITESPACE, attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, new Color(0xA020F0).darker());
		StyleConstants.setBold(attribs, false);
		StyleConstants.setItalic(attribs, false);
		styles.put("preprocessor", attribs);

		// TODO: Do we need this one. */
		attribs = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attribs, fi.getFamily());
		StyleConstants.setFontSize(attribs, fi.getSize());
		StyleConstants.setBackground(attribs, Color.white);
		StyleConstants.setForeground(attribs, Color.orange);
		StyleConstants.setBold(attribs, false);
		StyleConstants.setItalic(attribs, false);
		styles.put("unknown", attribs);
	}

	private class Colorer extends Thread
	{

		/**
		 * Keep a list of places in the file that it is safe to restart the
		 * highlighting. This happens whenever the lexer reports that it has
		 * returned to its initial state. Since this list needs to be sorted
		 * and we need to be able to retrieve ranges from it, it is stored in a
		 * balanced tree.
		 */
		private TreeSet iniPositions =
			new TreeSet(new DocPositionComparator());

		/**
		 * As we go through and remove invalid positions we will also be finding
		 * new valid positions.
		 * Since the position list cannot be deleted from and written to at the same
		 * time, we will keep a list of the new positions and simply add it to the
		 * list of positions once all the old positions have been removed.
		 */
		private HashSet newPositions = new HashSet();

		/**
		 * A simple wrapper representing something that needs to be colored.
		 * Placed into an object so that it can be stored in a Vector.
		 */
		private class RecolorEvent
		{
			public int position;
			public int adjustment;
			public RecolorEvent(int position, int adjustment)
			{
				this.position = position;
				this.adjustment = adjustment;
			}
		}

		/**
		 * Vector that stores the communication between the two threads.
		 */
		private volatile Vector v = new Vector();

		/**
		 * The amount of change that has occurred before the place in the
		 * document that we are currently highlighting (lastPosition).
		 */
		private volatile int change = 0;

		/**
		 * The last position colored
		 */
		private volatile int lastPosition = -1;

		private volatile boolean asleep = false;

		/**
		 * When accessing the vector, we need to create a critical section.
		 * we will synchronize on this object to ensure that we don't get
		 * unsafe thread behavior.
		 */
		private Object lock = new Object();

		/**
		 * Tell the Syntax Highlighting thread to take another look at this
		 * section of the document. It will process this as a FIFO.
		 * This method should be done inside a doclock.
		 */
		public void color(int position, int adjustment)
		{
			// figure out if this adjustment effects the current run.
			// if it does, then adjust the place in the document
			// that gets highlighted.
			if (position < lastPosition)
			{
				if (lastPosition < position - adjustment)
				{
					change -= lastPosition - position;
				}
				else
				{
					change += adjustment;
				}
			}
			synchronized (lock)
			{
				v.add(new RecolorEvent(position, adjustment));
				if (asleep)
				{
					this.interrupt();
				}
			}
		}

		/**
		 * The colorer runs forever and may sleep for long
		 * periods of time. It should be interrupted every
		 * time there is something for it to do.
		 */
		public void run()
		{
			int position = -1;
			int adjustment = 0;
			// if we just finish, we can't go to sleep until we
			// ensure there is nothing else for us to do.
			// use try again to keep track of this.
			boolean tryAgain = false;
			for (;;)
			{ // forever
				synchronized (lock)
				{
					if (v.size() > 0)
					{
						RecolorEvent re = (RecolorEvent) (v.elementAt(0));
						v.removeElementAt(0);
						position = re.position;
						adjustment = re.adjustment;
					}
					else
					{
						tryAgain = false;
						position = -1;
						adjustment = 0;
					}
				}
				if (position != -1)
				{
					SortedSet workingSet;
					Iterator workingIt;
					DocPosition startRequest =
						new DocPosition(position);
					DocPosition endRequest =
						new DocPosition(
							position
								+ ((adjustment >= 0) ? adjustment : -adjustment));
					DocPosition dp;
					DocPosition dpStart = null;
					DocPosition dpEnd = null;

					// find the starting position. We must start at least one
					// token before the current position
					try
					{
						// all the good positions before
						workingSet = iniPositions.headSet(startRequest);
						// the last of the stuff before
						dpStart = ((DocPosition) workingSet.last());
					}
					catch (NoSuchElementException x)
					{
						// if there were no good positions before the requested start,
						// we can always start at the very beginning.
						dpStart = new DocPosition(0);
					}

					// if stuff was removed, take any removed positions off the list.
					if (adjustment < 0)
					{
						workingSet =
							iniPositions.subSet(startRequest, endRequest);
						workingIt = workingSet.iterator();
						while (workingIt.hasNext())
						{
							workingIt.next();
							workingIt.remove();
						}
					}

					// adjust the positions of everything after the insertion/removal.
					workingSet = iniPositions.tailSet(startRequest);
					workingIt = workingSet.iterator();
					while (workingIt.hasNext())
					{
						((DocPosition) workingIt.next()).adjustPosition(
							adjustment);
					}

					// now go through and highlight as much as needed
					workingSet = iniPositions.tailSet(dpStart);
					workingIt = workingSet.iterator();
					dp = null;
					if (workingIt.hasNext())
					{
						dp = (DocPosition) workingIt.next();
					}
					try
					{
						final SchemaInfo si = _session.getSchemaInfo();
						Token t;
						boolean done = false;
						dpEnd = dpStart;
						synchronized (doclock)
						{
							// we are playing some games with the lexer for efficiency.
							// we could just create a new lexer each time here, but instead,
							// we will just reset it so that it thinks it is starting at the
							// beginning of the document but reporting a funny start position.
							// Reseting the lexer causes the close() method on the reader
							// to be called but because the close() method has no effect on the
							// DocumentReader, we can do this.
							syntaxLexer.reset(
								documentReader,
								0,
								dpStart.getPosition(),
								0);
							// After the lexer has been set up, scroll the reader so that it
							// is in the correct spot as well.
							documentReader.seek(dpStart.getPosition());
							// we will highlight tokens until we reach a good stopping place.
							// the first obvious stopping place is the end of the document.
							// the lexer will return null at the end of the document and wee
							// need to stop there.
							t = getNextToken(si);
						}
						newPositions.add(dpStart);
						while (!done && t != null)
						{
							// this is the actual command that colors the stuff.
							// Color stuff with the description of the style matched
							// to the hash table that has been set up ahead of time.
							synchronized (doclock)
							{
								if (t.getCharEnd() <= document.getLength())
								{
									String type = t.getDescription();
									if (type.equals(IConstants.IStyleNames.IDENTIFIER))
									{
										final String data = t.getContents();
										if (si.isTable(data))
										{
											type = IConstants.IStyleNames.TABLE;
											fireTableOrViewFound(t.getContents());
										}
										else if (si.isColumn(data))
										{
											type = IConstants.IStyleNames.COLUMN;
										}
										else if (si.isDataType(data))
										{
											type = IConstants.IStyleNames.DATA_TYPE;
										}
										else if (si.isKeyword(data))
										{
											type = IConstants.IStyleNames.RESERVED_WORD;
										}
									}

									document.setCharacterAttributes(
										t.getCharBegin() + change,
										t.getCharEnd() - t.getCharBegin(),
										getMyStyle(type), true);
									// record the position of the last bit of text that we colored
									dpEnd =
										new DocPosition(t.getCharEnd());
								}
								lastPosition = (t.getCharEnd() + change);
							}
							// The other more complicated reason for doing no more highlighting
							// is that all the colors are the same from here on out anyway.
							// We can detect this by seeing if the place that the lexer returned
							// to the initial state last time we highlighted is the same as the
							// place that returned to the initial state this time.
							// As long as that place is after the last changed text, everything
							// from there on is fine already.
							if (t.getState() == Token.INITIAL_STATE)
							{
								//System.out.println(t);
								// look at all the positions from last time that are less than or
								// equal to the current position
								while (dp != null
									&& dp.getPosition() <= t.getCharEnd())
								{
									if (dp.getPosition() == t.getCharEnd()
										&& dp.getPosition()
											>= endRequest.getPosition())
									{
										// we have found a state that is the same
										done = true;
										dp = null;
									}
									else if (workingIt.hasNext())
									{
										// didn't find it, try again.
										dp =
											(DocPosition) workingIt.next();
									}
									else
									{
										// didn't find it, and there is no more info from last
										// time. This means that we will just continue
										// until the end of the document.
										dp = null;
									}
								}
								// so that we can do this check next time, record all the
								// initial states from this time.
								newPositions.add(dpEnd);
							}
							synchronized (doclock)
							{
								t = getNextToken(si);
							}
						}

						// remove all the old initial positions from the place where
						// we started doing the highlighting right up through the last
						// bit of text we touched.
						workingIt =
							iniPositions.subSet(dpStart, dpEnd).iterator();
						while (workingIt.hasNext())
						{
							workingIt.next();
							workingIt.remove();
						}

						// Remove all the positions that are after the end of the file.:
						workingIt =
							iniPositions
								.tailSet(
									new DocPosition(document.getLength()))
								.iterator();
						while (workingIt.hasNext())
						{
							workingIt.next();
							workingIt.remove();
						}

						// and put the new initial positions that we have found on the list.
						iniPositions.addAll(newPositions);
						newPositions.clear();

						/*workingIt = iniPositions.iterator();
						while (workingIt.hasNext()){
							System.out.println(workingIt.next());
						}

						System.out.println("Started: " + dpStart.getPosition() + " Ended: " + dpEnd.getPosition());*/
					}
					catch (IOException x)
					{
					}
					synchronized (doclock)
					{
						lastPosition = -1;
						change = 0;
					}
					// since we did something, we should check that there is
					// nothing else to do before going back to sleep.
					tryAgain = true;
				}
				asleep = true;
				if (!tryAgain)
				{
					try
					{
						sleep(0xffffff);
					}
					catch (InterruptedException x)
					{
					}

				}
				asleep = false;
			}
		}
	}

	public void addSQLTokenListener(SQLTokenListener l)
	{
		_sqlTokenListeners.add(l);
	}

	public void removeSQLTokenListener(SQLTokenListener l)
	{
		_sqlTokenListeners.remove(l);
	}

	private void fireTableOrViewFound(String name)
	{
		Vector buf;
		synchronized(_sqlTokenListeners)
		{
			buf = (Vector)_sqlTokenListeners.clone();
		}

		for(int i=0; i < buf.size(); ++i)
		{
			((SQLTokenListener)buf.get(i)).tableOrViewFound(name);
		}
	}

	/**
	 * Just like a DefaultStyledDocument but intercepts inserts and
	 * removes to color them.
	 */
	private class HighLightedDocument extends DefaultStyledDocument
	{
		public HighLightedDocument()
		{
			super();
			putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		}

		public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
		{
//			synchronized (doclock)
//			{
				super.insertString(offs, str, a);
				color(offs, str.length());
				documentReader.update(offs, str.length());
//			}
		}

		public void remove(int offs, int len) throws BadLocationException
		{
//			synchronized (doclock)
//			{
				super.remove(offs, len);
				color(offs, -len);
				documentReader.update(offs, -len);
//			}
		}
	}

	class DocumentReader extends Reader
	{

		/**
		 * Modifying the document while the reader is working is like
		 * pulling the rug out from under the reader. Alerting the
		 * reader with this method (in a nice thread safe way, this
		 * should not be called at the same time as a read) allows
		 * the reader to compensate.
		 */
		public void update(int position, int adjustment)
		{
			if (position < this.position)
			{
				if (this.position < position - adjustment)
				{
					this.position = position;
				}
				else
				{
					this.position += adjustment;
				}
			}
		}

		/**
		 * Current position in the document. Incremented
		 * whenever a character is read.
		 */
		private long position = 0;

		/**
		 * Saved position used in the mark and reset methods.
		 */
		private long mark = -1;

		/**
		 * The document that we are working with.
		 */
		private AbstractDocument document;

		/**
		 * Construct a reader on the given document.
		 *
		 * @param document the document to be read.
		 */
		public DocumentReader(AbstractDocument document)
		{
			this.document = document;
		}

		/**
		 * Has no effect. This reader can be used even after
		 * it has been closed.
		 */
		public void close()
		{
		}

		/**
		 * Save a position for reset.
		 *
		 * @param readAheadLimit ignored.
		 */
		public void mark(int readAheadLimit)
		{
			mark = position;
		}

		/**
		 * This reader support mark and reset.
		 *
		 * @return true
		 */
		public boolean markSupported()
		{
			return true;
		}

		/**
		 * Read a single character.
		 *
		 * @return the character or -1 if the end of the document has been reached.
		 */
		public int read()
		{
			if (position < document.getLength())
			{
				try
				{
					char c = document.getText((int) position, 1).charAt(0);
					position++;
					return c;
				}
				catch (BadLocationException x)
				{
					return -1;
				}
			}
			else
			{
				return -1;
			}
		}

		/**
		 * Read and fill the buffer.
		 * This method will always fill the buffer unless the end of the document is reached.
		 *
		 * @param cbuf the buffer to fill.
		 * @return the number of characters read or -1 if no more characters are available in the document.
		 */
		public int read(char[] cbuf)
		{
			return read(cbuf, 0, cbuf.length);
		}

		/**
		 * Read and fill the buffer.
		 * This method will always fill the buffer unless the end of the document is reached.
		 *
		 * @param cbuf the buffer to fill.
		 * @param off offset into the buffer to begin the fill.
		 * @param len maximum number of characters to put in the buffer.
		 * @return the number of characters read or -1 if no more characters are available in the document.
		 */
		public int read(char[] cbuf, int off, int len)
		{
			if (position < document.getLength())
			{
				int length = len;
				if (position + length >= document.getLength())
				{
					length = document.getLength() - (int) position;
				}
				if (off + length >= cbuf.length)
				{
					length = cbuf.length - off;
				}
				try
				{
					String s = document.getText((int) position, length);
					position += length;
					for (int i = 0; i < length; i++)
					{
						cbuf[off + i] = s.charAt(i);
					}
					return length;
				}
				catch (BadLocationException x)
				{
					return -1;
				}
			}
			else
			{
				return -1;
			}
		}

		/**
		 * @return true
		 */
		public boolean ready()
		{
			return true;
		}

		/**
		 * Reset this reader to the last mark, or the beginning of the document if a mark has not been set.
		 */
		public void reset()
		{
			if (mark == -1)
			{
				position = 0;
			}
			else
			{
				position = mark;
			}
			mark = -1;
		}

		/**
		 * Skip characters of input.
		 * This method will always skip the maximum number of characters unless
		 * the end of the file is reached.
		 *
		 * @param n number of characters to skip.
		 * @return the actual number of characters skipped.
		 */
		public long skip(long n)
		{
			if (position + n <= document.getLength())
			{
				position += n;
				return n;
			}
			else
			{
				long oldPos = position;
				position = document.getLength();
				return (document.getLength() - oldPos);
			}
		}

		/**
		 * Seek to the given position in the document.
		 *
		 * @param n the offset to which to seek.
		 */
		public void seek(long n)
		{
			if (n <= document.getLength())
			{
				position = n;
			}
			else
			{
				position = document.getLength();
			}
		}
	}

}
