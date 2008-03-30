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
package net.sourceforge.squirrel_sql.client.update.xmlbeans;


import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class ChannelXmlBeanTest extends BaseSQuirreLJUnit4TestCase {

    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEquals() {
        ReleaseXmlBean releaseBean1 = new ReleaseXmlBean("snapshot", "Snapshot-20070922_1258");
        
        ReleaseXmlBean releaseBean2 = new ReleaseXmlBean("snapshot", "Snapshot-20070929_1300");
        
        ChannelXmlBean cb1 = new ChannelXmlBean();
        cb1.setName("snapshot");
        cb1.setCurrentRelease(releaseBean1);
        
        ChannelXmlBean cb2 = new ChannelXmlBean();
        cb2.setName("snapshot");
        cb2.setCurrentRelease(releaseBean1);        
        
        ChannelXmlBean cb3 = new ChannelXmlBean();
        cb3.setName("snapshot");
        cb3.setCurrentRelease(releaseBean2);
        
        ChannelXmlBean cb4 = new ChannelXmlBean() {
            private static final long serialVersionUID = 1L;
        };

        new EqualsTester(cb1, cb2, cb3, cb4);
    }
    
}
