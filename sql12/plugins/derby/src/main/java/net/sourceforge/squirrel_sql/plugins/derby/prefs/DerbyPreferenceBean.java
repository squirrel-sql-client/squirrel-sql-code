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
package net.sourceforge.squirrel_sql.plugins.derby.prefs;

import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean;

/**
 * A preference bean for the Derby plugin.
 * 
 * @author manningr
 */
public class DerbyPreferenceBean extends BaseQueryTokenizerPreferenceBean implements Cloneable, Serializable {

   static final long serialVersionUID = 5818886723165356478L;
   
   private boolean readClobsFully = true;

   public DerbyPreferenceBean() {
      super();
      statementSeparator = ";";
      procedureSeparator = "/";
      lineComment = "--";
      removeMultiLineComments = false;
      readClobsFully = true;
      installCustomQueryTokenizer = true;
   }

   /**
    * @return the readClobsFully
    */
   public boolean isReadClobsFully() {
      return readClobsFully;
   }

   /**
    * @param readClobsFully the readClobsFully to set
    */
   public void setReadClobsFully(boolean readClobsFully) {
      this.readClobsFully = readClobsFully;
   }

	/**
	 * @see net.sourceforge.squirrel_sql.fw.preferences.BaseQueryTokenizerPreferenceBean#clone()
	 */
	@Override
	protected DerbyPreferenceBean clone()
	{
		return (DerbyPreferenceBean)super.clone();
	}

   
}
