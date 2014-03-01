/*
 * Copyright (C) 2008 Dieter Engelhardt
 * dieter@ew6.org
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
package net.sourceforge.squirrel_sql.plugins.sqlreplace;

/**
 * @author Dieter
 *
 */
public class Replacement {
	   /**
	    * The name of the replacement
	    */
	   protected String _variable;

	   private String _value;

	   public Replacement()
	   {
	      this(null, null);
	   }

	   public Replacement(String variable, String value)
	   {
		   _variable = variable;
	      _value = value;
	   }

	public String getVariable() {
		return _variable;
	}

	public void setVariable(String _variable) {
		this._variable = _variable;
	}

	public String getRegexVariable() {
		return _variable.replace("$", "\\$");
	}
	
	public String getValue() {
		return _value;
	}

	public void setValue(String _value) {
		this._value = _value;
	}

	public String toString() {
		return getVariable() + " = " + getValue();
	}

	   
}
