/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

/**
 * This contains information to describe a record that was copied in relation
 * to other records of a table.
 * 
 */
public class RecordEvent extends AbstractCopyEvent {
    
    /** the number of the record.  This will always be >= 1 */
    private int recordNumber;
       
    /** 
     * the total number of records to be copied from the table this record 
     * belongs to
     */ 
    private int recordCount;
       
    /**
     * 
     * @param aNumber
     * @param aCount
     */
    public RecordEvent(SessionInfoProvider prov, int aNumber, int aCount) {
        super(prov);
        recordNumber = aNumber;
        recordCount = aCount;
    }

    /**
     * @param recordNumber The recordNumber to set.
     */
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    /**
     * @return Returns the recordNumber.
     */
    public int getRecordNumber() {
        return recordNumber;
    }

    /**
     * @param recordCount The recordCount to set.
     */
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * @return Returns the recordCount.
     */
    public int getRecordCount() {
        return recordCount;
    }
    
    /**
    * @see java.lang.Object#toString()
    */
   @Override
    public String toString() {
   	 StringBuilder result = new StringBuilder("Record ");
   	 result.append(recordNumber);
   	 result.append(" of "); 
   	 result.append(recordCount);
   	 result.append(" total records");
   	 return result.toString();
    }
}
