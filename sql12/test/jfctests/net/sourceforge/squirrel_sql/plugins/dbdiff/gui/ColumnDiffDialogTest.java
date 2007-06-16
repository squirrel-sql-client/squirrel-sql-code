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
package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import java.awt.Frame;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ColumnDiffDialogTest extends BaseSQuirreLJUnit4TestCase {

    ColumnDiffDialog dialogUnderTest = null;
    
    @Before
    public void setUp() throws Exception {
        dialogUnderTest = new ColumnDiffDialog(createMainFrame(), false);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = IllegalArgumentException.class)
    public void setSession1Label() {
        dialogUnderTest.setSession1Label(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void setSession2Label() {
        dialogUnderTest.setSession2Label(null);
    }
    
    // Helpers
    
    private Frame createMainFrame() {
        return new JFrame();
    }
    
}
