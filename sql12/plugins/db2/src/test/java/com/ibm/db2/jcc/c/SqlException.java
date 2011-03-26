package com.ibm.db2.jcc.c;

/*
 * Copyright (C) 2011 Rob Manning
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

public class SqlException extends Throwable
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;
	private int code;
	private int state;
	
	
	public SqlException(String message, int code, int state) {
		this.code = code;
		this.message = message;
		this.state = state;
	}
	
	public Sqlca getSqlca() {
		return new Sqlca();
	}
	
	public class Sqlca {
		
		public String getMessage() {
			return message;
		}
		
		public int getSqlCode() {
			return code;
		}
		
		public int getSqlState() {
			return state;
		}
	}
}
