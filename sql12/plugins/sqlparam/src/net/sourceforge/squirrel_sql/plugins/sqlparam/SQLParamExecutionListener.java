package net.sourceforge.squirrel_sql.plugins.sqlparam;
/*
 * Copyright (C) 2007 Thorsten Mürell
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

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.MenuComponent;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SelectWidgetCommand;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SQLExecutionAdapter;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlparam.gui.AskParamValueDialog;

/**
 * This listener listens for SQL execution.
 * 
 * @author Thorsten Mürell
 */
public class SQLParamExecutionListener extends SQLExecutionAdapter
{

	private final static ILogger log = LoggerController.createLogger(SQLParamPlugin.class);
	private ISession session = null;
	private SQLParamPlugin plugin = null;
	private AskParamValueDialog dialog = null;

	/**
	 * The constructor
	 * 
	 * @param plugin
	 * @param session
	 */
	public SQLParamExecutionListener(SQLParamPlugin plugin, ISession session) {
		this.session = session;
		this.plugin = plugin;
	}

	/**
	 * This method is called when the SQL was executed.
	 * 
	 * @param sql
	 */
	@Override
	public void statementExecuted(String sql) {
		// log.info("SQL executed: " + sql);
	}

	/**
	 * Called prior to an individual statement being executed. If you modify the
	 * script remember to return it so that the caller knows about the
	 * modifications.
	 *
	 * @param	sql	The SQL to be executed.
	 *
	 * @return	The SQL to be executed. If <TT>null</TT> returned then the
	 *			statement will not be executed.
	 */
	@Override
	public String statementExecuting(String sql) {
		// log.info("SQL starting to execute: " + sql);

		// Removes -- comments
		sql = removeComments(sql);

		//log.info("Removed comments: " + sql);

		// Removes /*  */ comments
		sql = sql.replaceAll("/\\*(.|\\s)*\\*/", "");

		StringBuffer buffer = new StringBuffer(sql);
		Map<String, String> cache = plugin.getCache();
		Map<String, String> currentCache = new HashMap<String, String>();
		Pattern p = Pattern.compile("[\\ \\(]:[a-zA-Z]\\w+");
		
		Matcher m = p.matcher(buffer);


		boolean parametersWhereReplaced = false;
		while (m.find()) {
			if (isQuoted(buffer, m.start()))
				continue;
			final String var = m.group();
			String value = null;
			if (currentCache.containsKey(var)) {
				value = currentCache.get(var);
			} else {
				final String oldValue = cache.get(var);
				if (SwingUtilities.isEventDispatchThread()) {
					createParameterDialog(var, oldValue);
					while (!dialog.isDone()) {
						try {
							AWTEvent event = Toolkit.getDefaultToolkit().getSystemEventQueue().getNextEvent();
							Object source = event.getSource();
							if (event instanceof ActiveEvent) {
								((ActiveEvent)event).dispatch();
							} else if (source instanceof Component) {
								((Component)source).dispatchEvent(
										event);
							} else if (source instanceof MenuComponent) {
								((MenuComponent)source).dispatchEvent(
										event);
							} else {
								System.err.println(
										"Unable to dispatch: " + event);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								createParameterDialog(var, oldValue);
							}
						});
						while (!dialog.isDone()) {
							wait();
						}
					} catch (InvocationTargetException ite) {
						ite.printStackTrace();
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}
				if (dialog.isCancelled()) {
					dialog = null;
					return null;
				}
				value = sanitizeValue(dialog.getValue(), dialog.isQuotingNeeded());
				cache.put(var, dialog.getValue());
				currentCache.put(var, value);
				dialog = null;
			}
			buffer.replace(m.start(), m.end(), value);
			parametersWhereReplaced = true;
			m.reset();
		}

		GUIUtils.processOnSwingEventThread(new Runnable() {
			@Override
			public void run() {
				new SelectWidgetCommand(session.getActiveSessionWindow()).execute();
			}
		});
		// log.info("SQL passing to execute: " + buffer.toString());

		//////////////////////////////////////////////////////////////////
		// This is a workaround to avoid bug #1206 "SQuirrel detects single line comment inside string literals"
		// That means at least when no parameters are used bug #1206 is avoided.
		// The right way would be to do parsing like in QueryTokenizer instead of using Regular Expressions.
		// Regular Expressions is not able to really cope with literals.
		if (parametersWhereReplaced)
		{
			return buffer.toString();
		}
		else
		{
			return sql;
		}
		//
		//////////////////////////////////////////////////////////////////
	}

	private static String removeComments(String sql) {
        List<Integer> literalsStartPositions = new ArrayList<Integer>();
        List<Integer> literalsEndPositions = new ArrayList<Integer>();
        Matcher lm = Pattern.compile("('(('')|[^'])*')").matcher(sql);
        while (lm.find()) {
            literalsStartPositions.add(lm.start());
            literalsEndPositions.add(lm.end());
        }

        List<Integer> commentsStartPositions = new ArrayList<Integer>();
        List<Integer> commentsEndPositions = new ArrayList<Integer>();
        Matcher commentsm = Pattern.compile("--(.*?)\r?\n").matcher(sql);
        while (commentsm.find()) {
            if (isNotInsideOfAnyLiteral(commentsm.start(), literalsStartPositions, literalsEndPositions)) {
                commentsStartPositions.add(commentsm.start());
                commentsEndPositions.add(commentsm.end());
            }
        }

        // it's possible that there's no \r\n after the last line comment
        StringBuffer strippedSql = new StringBuffer(sql);
        int commentStartIdx = -1;
        if ((commentStartIdx = strippedSql.lastIndexOf("--")) != -1) {
            // commentStartIdx can NOT be before end of last matched comment [start, end)
            boolean uniqueComment = true;
            if (commentsEndPositions.size() > 0) {
                if (commentsEndPositions.get(commentsEndPositions.size() - 1) > commentStartIdx) {
                    uniqueComment = false;
                }
            }
            if (uniqueComment && isNotInsideOfAnyLiteral(commentStartIdx, literalsStartPositions, literalsEndPositions)) {
                commentsStartPositions.add(commentStartIdx);
                commentsEndPositions.add(strippedSql.length());
            }
        }

        for (int i = commentsStartPositions.size() - 1; i >= 0; --i) {
            strippedSql = strippedSql.replace(commentsStartPositions.get(i), commentsEndPositions.get(i), "");
        }

        return strippedSql.toString();
	}

	// comment can not start in any of the literals
	private static boolean isNotInsideOfAnyLiteral(int spos, List<Integer> literalsStartPositions, List<Integer> literalsEndPositions) {
        for (int i = 0; i < literalsStartPositions.size(); i++) {
            if (literalsStartPositions.get(i) <= spos && spos < literalsEndPositions.get(i)) {
                return false;
            }
        }
	    return true;
	}

	public static void main(String[] args) {
	    String testCase1 = "-- sql \n INSERT INTO code (txt) VALUES -- haha\n 'for(int i = e-1; i >= 0; --i)') -- nice test";
	    System.out.println(SQLParamExecutionListener.removeComments(testCase1));
    }

	private void createParameterDialog(String parameter, String oldValue) {
		dialog = new AskParamValueDialog(parameter, oldValue, session.getApplication());
		session.getApplication().getMainFrame().addWidget(dialog);
		dialog.setLayer(JLayeredPane.MODAL_LAYER);
		dialog.moveToFront();
		DialogWidget.centerWithinDesktop(dialog);
		dialog.setVisible(true);
		dialog.requestFocusForInputField();
	}

	private String sanitizeValue(String value, boolean quoting) {
		String retValue = value;
		boolean quotesNeeded = quoting;

		try {
			Float.parseFloat(value);
		} catch (NumberFormatException nfe) {
			quotesNeeded = true;
		}

		if (quotesNeeded) {
			retValue = "'" + value + "'";
		}
		return retValue;
	}

	private boolean isQuoted(StringBuffer buffer, int position) {
		String part = buffer.substring(0, position);
		if (searchAllOccurences(part, "\"") % 2 != 0) 
			return true;
		if (searchAllOccurences(part, "'") % 2 != 0)
			return true;
		return false;
	}

	private int searchAllOccurences(String haystack, String needle) {
		int i = 0;
		int pos = 0;
		while ((pos = haystack.indexOf(needle, pos + 1)) > -1) {
			i++;
		}
		return i;
	}

}
