package net.sourceforge.squirrel_sql.fw.util.beanwrapper;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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

public class StringWrapper {

    public interface IPropertyNames {
        String STRINGS = "string";
    }

    private String _string;

    public StringWrapper() {
        this(null);
    }

    public StringWrapper(String string) {
        super();
        setString(string);
    }

    public String getString() {
        return _string;
    }

    public void setString(String value) {
        _string = value;
    }

//  public Rectangle createRectangle() {
//      return new Rectangle(_x, _y, _width, _height);
//  }
}