/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
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
 *
 * created by cse, 02.10.2002 13:54:25
 */
package net.sourceforge.jcomplete.ui;

import java.awt.Point;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import javax.swing.JFrame;
import net.sourceforge.jcomplete.Completion;
import net.sourceforge.jcomplete.CompletionHandler;
import net.sourceforge.jcomplete.SQLSchema;
import net.sourceforge.jcomplete.completions.SQLColumn;
import net.sourceforge.jcomplete.completions.SQLTable;

/**
 *
 */
public class SQLCompletionAdapter extends CompletionAdapter
{
    public SQLCompletionAdapter(
          JTextComponent textComponent, CompletionHandler completor, int popupMask, int popupKey)
    {
        super(textComponent, completor, popupMask, popupKey);
    }

    protected void showCompletionUI(Completion completion)
    {
        if(completion instanceof SQLColumn) {
            SQLColumn column = (SQLColumn)completion;
            if(column.hasTable(m_textComponent.getCaretPosition())) {
                String[] cols = column.getCompletions(m_textComponent.getCaretPosition());
                if(cols.length > 1)
                    showPopupList(column, cols, column.isRepeatable());
                else if(cols.length == 1)
                    completionRequested(column, cols);
            }
            else {
                showTableColumnDialog(column);
            }
        }
        else if(completion instanceof SQLTable) {
            SQLTable table = (SQLTable)completion;
            SQLSchema.Table[] tables = table.getCompletions(m_textComponent.getCaretPosition());
            if(tables.length > 1)
                showPopupList(table, tables, table.isRepeatable());
            else if(tables.length == 1 && !tables[0].equals(table.catalog, table.schema, table.name))
                completionRequested(table, tables);
        }
    }

    private void showTableColumnDialog(SQLColumn column)
    {
        JFrame frame = getFrame();
        int dot = m_textComponent.getCaret().getDot();
        try {
            TableColumnChooser chooser = new TableColumnChooser(frame, column, this);

            Rectangle rect = m_textComponent.modelToView(dot);
            Point pos = m_textComponent.getLocationOnScreen();
            pos.translate(rect.x, rect.y+rect.height);
            chooser.setLocation(pos);
            chooser.show();
        }
        catch (BadLocationException e) {}
    }

    private JFrame getFrame()
    {
        Component comp = m_textComponent;
        do {
            comp = comp.getParent();
        }
        while(!(comp instanceof JFrame) && comp != null);
        return comp != null ? (JFrame)comp : new JFrame();
    }
}
