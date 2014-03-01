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

import java.io.Serializable;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;

/**
 * This bean is a javabean representation of the data in changeList.xml which is captured from the user's 
 * changes that are made using the update feature. 
 * 
 * @author manningr
 */
public class ChangeListXmlBean implements Serializable {

	private static final long serialVersionUID = -5388272506303651886L;

	/** the changes to make */
   private List<ArtifactStatus> changes = null;
   
   /**
    * @return the _changes
    */
   public List<ArtifactStatus> getChanges() {
      return changes;
   }

   /**
    * @param _changes the _changes to set
    */
   public void setChanges(List<ArtifactStatus> changes) {
      this.changes = changes;
   }
   
   
   
}
