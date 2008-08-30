package net.sourceforge.squirrel_sql.client.update.xmlbeans;
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

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ChangeListXmlBeanExternalTest extends BaseSQuirreLJUnit4TestCase {

    
      
   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testWriteChangeList() throws Exception  {
      ArtifactStatus status = new ArtifactStatus();
      status.setName("fw.jar");
      status.setType("core");
      status.setInstalled(true);
      status.setArtifactAction(ArtifactAction.INSTALL);
      
      ArrayList<ArtifactStatus> list = new ArrayList<ArtifactStatus>();
      list.add(status);
      
      ChangeListXmlBean bean = new ChangeListXmlBean();
      bean.setChanges(list);
      
      UpdateXmlSerializer serializer = new UpdateXmlSerializerImpl();
      serializer.write(bean, "/tmp/changeList.xml");
   }

}
