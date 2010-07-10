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
 * created by cse, 01.10.2002 11:57:26
 */
package net.sourceforge.jcomplete.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import net.sourceforge.jcomplete.Completion;

/**
 * a popup window with a scrollable list
 */
public class CompletionListPopup extends JPanel
{
    private JList list;
    private JScrollPane scroller;
    private CompletionListener completor;
    private Completion completion;
    private Dimension size;                 //cached size to avoid recalculation

    public CompletionListPopup(CompletionListener completor, boolean multiple)
    {
        this(completor, null, multiple);
    }

    public CompletionListPopup(CompletionListener completor, Object[] listData, boolean multiple)
    {
        super(new BorderLayout());
        this.completor = completor;
        initComponents(listData, multiple);
    }

    private void initComponents(Object[] listData, boolean multiple)
    {
        list = new JList();
        if(listData != null)
            list.setListData(listData);

        list.setVisibleRowCount(10);
        list.setSelectionMode(multiple ?
              ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);

        list.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    closeWindow();
                    completor.completionRequested(completion, list.getSelectedValues());
                }
                else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeWindow();
                    e.consume();
                    completor.completionAborted();
                }
            }
        });
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)
            {
                if(e.getClickCount() == 2) {
                    closeWindow();
                    completion = completor.completionRequested(completion, list.getSelectedValues());
                }
            }
        });
        list.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e)
            {
                closeWindow();
                completor.completionAborted();
            }
        });
        scroller = new JScrollPane();
        scroller.setViewportView(list);
        add(scroller, BorderLayout.CENTER);
    }

    private void closeWindow()
    {
        setVisible(false);
    }

    public Dimension getPreferredSize()
    {
        return scroller.getPreferredSize();
    }

    public Dimension getMaximumSize()
    {
        return scroller.getMaximumSize();
    }

    public Dimension getMinimumSize()
    {
        return scroller.getMinimumSize();
    }

    /**
     * we must override this, because the PopupManager will suggest a maximum
     * size which is unacceptable.
     * @param width suggested width
     * @param height suggested height
     */
    public void setSize(int width, int height)
    {
        if(size == null)
            size = scroller.getPreferredSize();
        super.setSize(Math.min(width, size.width), Math.min(height, size.height));
    }

    public void show(Completion completion, Object[] options)
    {
        this.completion = completion;
        list.setListData(options);
        list.setSelectedIndex(0);
        setVisible(true);
        list.requestFocus();
    }
}
