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
 * created 24.09.2002 12:27:12
 */
package net.sourceforge.jcomplete.test;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.text.BadLocationException;

import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Map;
import java.util.HashMap;

import net.sourceforge.jcomplete.SQLCompletionHandler;
import net.sourceforge.jcomplete.CompletionHandler;
import net.sourceforge.jcomplete.SQLSchema;
import net.sourceforge.jcomplete.ui.DocumentAdapter;
import net.sourceforge.jcomplete.ui.SQLCompletionAdapter;

public class PopUpCompletionTest extends JFrame
{
    public static Map s_TestTables = new HashMap();
    static {
        SQLSchema.Table t = new SQLSchema.Table("TESTTABLE1");
        t.setColumns(new String[]{"column11", "column12", "column13", "column14"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TESTTABLE2");
        t.setColumns(new String[]{"column21", "column22", "column23", "column24"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TABLE1_Data_Data");
        t.setColumns(new String[]{"column1", "column2"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TABLE2");
        t.setColumns(new String[]{"column11", "column12"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TABLE3");
        t.setColumns(new String[]{"column311", "column312", "column321", "column322"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TABLE4");
        t.setColumns(new String[]{"column411", "column412"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TABLE5");
        t.setColumns(new String[]{"column511", "column512"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TABLE6");
        t.setColumns(new String[]{"column611", "column612"});
        s_TestTables.put(t.getCompositeName(), t);

        t = new SQLSchema.Table("TABLE7");
        t.setColumns(new String[]{"column711", "column712"});
        s_TestTables.put(t.getCompositeName(), t);
    }
    public PopUpCompletionTest()
    {
        setSize(800, 400);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        final JLabel posLabel = new JLabel("Pos: 0");
        JPanel labelArea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelArea.add(new JLabel("hit Ctrl-Enter for auto-completion on column and table references | "));
        labelArea.add(posLabel);

        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(15);
        textArea.setColumns(50);
        textArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e)
            {
                posLabel.setText("Pos: "+e.getDot());
            }
        });

        final JTextArea errorArea = new JTextArea();
        errorArea.setRows(5);
        errorArea.setColumns(50);
        errorArea.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if(e.getClickCount() == 2)
                    errorArea.setText("");
            }
        });

        JScrollPane textScroller = new JScrollPane(textArea);
        JScrollPane errScroller = new JScrollPane(errorArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(textScroller);
        splitPane.setBottomComponent(errScroller);
        splitPane.setDividerSize(5);
        splitPane.setResizeWeight(0.8);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(labelArea, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        //create the parser interface handler
        SQLCompletionHandler handler = new SQLCompletionHandler(new ErrorListener(errorArea), s_TestTables);

        //connect the handler to the document
        textArea.getDocument().addDocumentListener(
              new DocumentAdapter(handler, textArea.getDocument()));

        //connect the GUI to the handler
        textArea.addKeyListener(new SQLCompletionAdapter(
              textArea, handler, KeyEvent.CTRL_MASK, KeyEvent.VK_ENTER));
    }

    private class ErrorListener implements CompletionHandler.ErrorListener
    {
        private JTextArea textArea;

        public ErrorListener(JTextArea textArea)
        {
            this.textArea = textArea;
        }
        public void errorDetected(String message, int line, int column)
        {
            try {
                textArea.getDocument().insertString(
                      textArea.getDocument().getLength(),
                      "["+line+":"+column+"] "+message+"\n",
                      null);
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        PopUpCompletionTest test = new PopUpCompletionTest();
        test.pack();
        test.show();
    }
}