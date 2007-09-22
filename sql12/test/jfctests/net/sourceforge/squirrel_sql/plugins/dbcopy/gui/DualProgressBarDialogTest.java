/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Before;
import org.junit.Test;

public class DualProgressBarDialogTest extends BaseSQuirreLJUnit4TestCase {

    @Before
    public void setUp() {
    }
    
    @Test
    public void test() {
        
        final JFrame test = new JFrame();
        JButton showDialog = new JButton("Show Dialog");
        showDialog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                DualProgressBarDialog.getDialog(test, "test", true, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Cancel button pressed");
                        System.exit(0);
                    }
                });
            }
        });
        test.getContentPane().setLayout(new BorderLayout());
        test.getContentPane().add(showDialog, BorderLayout.CENTER);
        test.setSize(100,100);
        test.setVisible(true);
    }
    
        
}
