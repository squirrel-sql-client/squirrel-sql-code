package net.sourceforge.squirrel_sql.plugins.jedit;
/*
 * Copyright (C) 2001-2002 Colin Bell
 * colbell@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.plugins.jedit.textarea.DefaultInputHandler;

public class JeditInputHandler extends DefaultInputHandler
{

	JeditInputHandler()
	{
		super();
		addKeyBinding("BACK_SPACE", BACKSPACE);
		addKeyBinding("C+BACK_SPACE", BACKSPACE_WORD);
		addKeyBinding("DELETE", DELETE);
		addKeyBinding("C+DELETE", DELETE_WORD);

		addKeyBinding("ENTER", INSERT_BREAK);
		addKeyBinding("TAB", INSERT_TAB);

		addKeyBinding("INSERT", OVERWRITE);
		addKeyBinding("C+\\", TOGGLE_RECT);

		addKeyBinding("HOME", HOME);
		addKeyBinding("END", END);
		addKeyBinding("S+HOME", SELECT_HOME);
		addKeyBinding("S+END", SELECT_END);
		addKeyBinding("C+HOME", DOCUMENT_HOME);
		addKeyBinding("C+END", DOCUMENT_END);
		addKeyBinding("CS+HOME", SELECT_DOC_HOME);
		addKeyBinding("CS+END", SELECT_DOC_END);

		addKeyBinding("PAGE_UP", PREV_PAGE);
		addKeyBinding("PAGE_DOWN", NEXT_PAGE);
		addKeyBinding("S+PAGE_UP", SELECT_PREV_PAGE);
		addKeyBinding("S+PAGE_DOWN", SELECT_NEXT_PAGE);

		addKeyBinding("LEFT", PREV_CHAR);
		addKeyBinding("S+LEFT", SELECT_PREV_CHAR);
		addKeyBinding("C+LEFT", PREV_WORD);
		addKeyBinding("CS+LEFT", SELECT_PREV_WORD);
		addKeyBinding("RIGHT", NEXT_CHAR);
		addKeyBinding("S+RIGHT", SELECT_NEXT_CHAR);
		addKeyBinding("C+RIGHT", NEXT_WORD);
		addKeyBinding("CS+RIGHT", SELECT_NEXT_WORD);
		addKeyBinding("UP", PREV_LINE);
		addKeyBinding("S+UP", SELECT_PREV_LINE);
		addKeyBinding("DOWN", NEXT_LINE);
		addKeyBinding("S+DOWN", SELECT_NEXT_LINE);

		//		addKeyBinding("C+ENTER",REPEAT);
	}

}