package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.Component;
import java.awt.event.MouseListener;

public interface ISQLEntryPanel {

	Component getComponent();
	
	String getText();
	String getSelectedText();

	int getCaretPosition();
	void setCaretPosition(int pos);

	void setRows(int rowCount);
	void setTabSize(int tabSize);

	void setText(String text);
	void appendText(String text);
	
	void addMouseListener(MouseListener lis);
	void removeMouseListener(MouseListener lis);
}

