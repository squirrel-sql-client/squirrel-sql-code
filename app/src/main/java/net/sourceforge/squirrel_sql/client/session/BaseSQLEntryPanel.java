package net.sourceforge.squirrel_sql.client.session;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.action.ViewObjectAtCursorInObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;

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
public abstract class BaseSQLEntryPanel implements ISQLEntryPanel
{
	protected final static String LINE_SEPARATOR = "\n";

	protected final static String SQL_STMT_SEP = LINE_SEPARATOR + LINE_SEPARATOR;

   public static final IntegerIdentifierFactory ENTRY_PANEL_IDENTIFIER_FACTORY = new IntegerIdentifierFactory();
   private IIdentifier _entryPanelIdentifier;

	private TextPopupMenu _textPopupMenu;
	private IApplication _app;

	private MouseListener _sqlEntryMouseListener = new MyMouseListener();


	protected BaseSQLEntryPanel(IApplication app)
	{
		_entryPanelIdentifier = ENTRY_PANEL_IDENTIFIER_FACTORY.createIdentifier();
		_textPopupMenu = new TextPopupMenu();
		_app = app;

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				getTextComponent().addMouseListener(_sqlEntryMouseListener);
			}
		});

	}

   public IIdentifier getIdentifier()
   {
      return _entryPanelIdentifier;
   }

	public String getSQLToBeExecuted()
	{
		String sql = getSelectedText();
		if (sql == null || sql.trim().length() == 0)
		{
         sql = getText();
         int[] bounds = getBoundsOfSQLToBeExecuted();

         if(bounds[0] >= bounds[1])
         {
            sql = "";
         }
         else
         {
            sql = sql.substring(bounds[0], bounds[1]).trim();
         }
		}
		return sql != null ? sql : "";
	}

   public int[] getBoundsOfSQLToBeExecuted()
   {
      int[] bounds = new int[2];
      bounds[0] = getSelectionStart();
      bounds[1] = getSelectionEnd();

      if(bounds[0] == bounds[1])
      {
         bounds = getSqlBoundsBySeparatorRule(getCaretPosition());
      }

      return bounds;
   }

   /**
    * The non selection separator is two new lines. Two new lines with white spaces in between
    * is counted as separator too. 
    * @return
    */
   private int[] getSqlBoundsBySeparatorRule(int iCaretPos)
   {
      int[] bounds = new int[2];

      String sql = getText();

      bounds[0] = lastIndexOfStateSep(sql, iCaretPos);
      bounds[1] = indexOfStateSep(sql, iCaretPos);

      return bounds;

   }
   
   private static int indexOfStateSep(String sql, int pos)
   {
      int ix = pos;

      int newLinteCount = 0;
      for(;;)
      {
         if(sql.length() == ix)
         {
            return sql.length();
         }

         if(false == Character.isWhitespace(sql.charAt(ix)))
         {
            newLinteCount = 0;
         }

         if('\n' == sql.charAt(ix))
         {
            ++newLinteCount;
            if(2 == newLinteCount)
            {
               return ix-1;
            }
         }

         ++ix;
      }
   }

   private static int lastIndexOfStateSep(String sql, int pos)
   {
      int ix = pos;

      int newLinteCount = 0;
      for(;;)
      {

         if (ix == sql.length())
         {
            if(ix == 0)
            {
               return ix;
            }
            else
            {
               ix--;
            }
         }


         
         if(false == Character.isWhitespace(sql.charAt(ix)))
         {
            newLinteCount = 0;
         }


         if('\n' == sql.charAt(ix))
         {
            ++newLinteCount;
            if(2 == newLinteCount)
            {
               return ix+newLinteCount;
            }
         }

         if(0 == ix)
         {
            return 0 + newLinteCount;
         }


         --ix;
      }
   }


   public void moveCaretToPreviousSQLBegin()
   {
      String sql = getText();

      int iCaretPos = getCaretPosition() - 1;
      int iLastIndex = sql.lastIndexOf(SQL_STMT_SEP, iCaretPos);

      if(-1 == iLastIndex)
      {
         return;
      }

      iLastIndex = sql.lastIndexOf(SQL_STMT_SEP, iLastIndex - getWhiteSpaceCountBackwards(iLastIndex, sql));

      if(-1 == iLastIndex)
      {
         iLastIndex = 0;
      }

      char c = sql.charAt(iLastIndex);
      while(Character.isWhitespace(c) && iLastIndex < sql.length())
      {
         ++iLastIndex;
         c = sql.charAt(iLastIndex);

      }
      setCaretPosition(iLastIndex);
   }

	private int getWhiteSpaceCountBackwards(int iStartIx, String sql)
	{

		int count = 0;
		while(0 < iStartIx && Character.isWhitespace(sql.charAt(iStartIx)))
		{
			--iStartIx;
			++count;
		}

		return count;

	}

	public void moveCaretToNextSQLBegin()
	{
		String sql = getText();

		int iCaretPos = getCaretPosition();
		int iNextIndex = sql.indexOf(SQL_STMT_SEP, iCaretPos);

		if(-1 == iNextIndex)
		{
			return;
		}

		while(iNextIndex < sql.length() && Character.isWhitespace(sql.charAt(iNextIndex)))
		{
			++iNextIndex;
		}

		if(iNextIndex < sql.length())
		{
			setCaretPosition(iNextIndex);
		}
	}

   public void selectCurrentSql()
   {
      int[] boundsOfSQLToBeExecuted = 
          getSqlBoundsBySeparatorRule(getCaretPosition());

      if(boundsOfSQLToBeExecuted[0] != boundsOfSQLToBeExecuted[1])
      {
         setSelectionStart(boundsOfSQLToBeExecuted[0]);
         setSelectionEnd(boundsOfSQLToBeExecuted[1]);
      }
   }


   /**
	 * Add a hierarchical menu to the SQL Entry Area popup menu.
	 *
	 * @param	menu	The menu that will be added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Menu</TT> passed.
	 */
	public void addToSQLEntryAreaMenu(JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("Menu == null");
		}

		_textPopupMenu.add(menu);
	}

	/**
	 * Add an <TT>Action</TT> to the SQL Entry Area popup menu.
	 *
	 * @param	action	The action to be added.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>Action</TT> passed.
	 */
	public JMenuItem addToSQLEntryAreaMenu(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}

		return _textPopupMenu.add(action);
	}

   /**
	 * @see ISQLEntryPanel#addRedoUndoActionsToSQLEntryAreaMenu(javax.swing.Action, javax.swing.Action)
	 */
	public void addRedoUndoActionsToSQLEntryAreaMenu(Action undo, Action redo)
	{
		_textPopupMenu.addSeparator();

		JMenuItem buf;

		buf = addToSQLEntryAreaMenu(undo);
		_app.getResources().configureMenuItem(undo, buf);

		buf = addToSQLEntryAreaMenu(redo);
		_app.getResources().configureMenuItem(redo, buf);

		_textPopupMenu.addSeparator();
	}

   @Override
   public boolean hasOwnUndoableManager()
   {
      return false;
   }

   @Override
   public IUndoHandler createUndoHandler()
   {
      return null;
   }


	public void dispose()
	{
		_textPopupMenu.dispose();
	}

   @Override
   public String getWordAtCursor()
   {
      int[] beginAndEndPos = SQLEntryPanelUtil.getWordBoundsAtCursor(getTextComponent(), true);
      return getTextComponent().getText().substring(beginAndEndPos[0], beginAndEndPos[1]).trim();
   }

   @Override
   public JScrollPane createScrollPane(JTextComponent textComponent)
   {
      JScrollPane sqlEntryScroller = new JScrollPane(textComponent);
      sqlEntryScroller.setBorder(BorderFactory.createEmptyBorder());
      return sqlEntryScroller;
   }


   private final class MyMouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				displayPopupMenu(evt);
			}
		}

		public void mouseReleased(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				displayPopupMenu(evt);
			}
		}

        /**
         * This provides the feature which is like in Eclipse when you control
         * click on an identifier it takes you to the file that contains the 
         * identifier (method, class, etc) definition.  Similarly, ctrl-clicking 
         * on an identifier in the SQL editor will invoke the view object at
         * cursor in object tree action. 
         */
		@Override
        public void mouseClicked(MouseEvent e) {
            if (e.isControlDown() && e.getClickCount() == 1) {
                
                final Action a = 
                    _app.getActionCollection().get(ViewObjectAtCursorInObjectTreeAction.class);
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        a.actionPerformed(new ActionEvent(this, 1, "ViewObjectAtCursorInObjectTreeAction"));
                    }
                });
            }
        }

        private void displayPopupMenu(MouseEvent evt)
		{
			_textPopupMenu.setTextComponent(getTextComponent());
			_textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}



}
