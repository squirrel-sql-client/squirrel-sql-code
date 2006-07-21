/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

public class ErrorEvent extends AbstractCopyEvent {
    
    public static final int SETUP_AUTO_COMMIT_TYPE = 0;
    
    public static final int RESTORE_AUTO_COMMIT_TYPE = 1;
    
    public static final int SQL_EXCEPTION_TYPE = 2;
    
    public static final int MAPPING_EXCEPTION_TYPE = 3;
    
    public static final int USER_CANCELLED_EXCEPTION_TYPE = 4;
    
    public static final int GENERIC_EXCEPTION = 5;
    
    private int type = -1;
    
    private Exception exception = null;
    
    public ErrorEvent(SessionInfoProvider provider, int aType) {
        super(provider);
        type = aType;
    }

    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param exception The exception to set.
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * @return Returns the exception.
     */
    public Exception getException() {
        return exception;
    }
    
}
