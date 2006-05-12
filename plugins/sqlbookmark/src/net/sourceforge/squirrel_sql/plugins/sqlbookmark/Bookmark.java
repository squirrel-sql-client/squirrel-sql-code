/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
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

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.io.Serializable;

/**
 * A class encapsulating an SQL Bookmark.
 *
 * @author Joseph Mocker
 */
public class Bookmark
{

   /**
    * The name of the bookmark
    */
   protected String _name;

   private String _description;
   /**
    * The SQL for the bookmark
    */
   protected String _sql;
   private String _toString;

   public Bookmark()
   {
      this(null, null, null);
   }

   public Bookmark(String name, String description, String sql)
   {
      _name = name;
      _description = description;
      setSql(sql);
      initToString();
   }

   private void initToString()
   {
      String name = null == _name ? "(missing name)" : _name;
      String description = null == _description ? "(missing description)" : _description;
      _toString = "(" + name + ")   " + description;
   }

   public String getName()
   {
      return _name;
   }

   public String getDescription()
   {
      return _description;
   }

   public String getSql()
   {
      return _sql;
   }

   public void setName(String name)
   {
      this._name = name;
      initToString();
   }

   public void setDescription(String description)
   {
      this._description = description;
      initToString();
   }

   public void setSql(String sql)
   {
      _sql = sql;
//      if(null == sql )
//      {
//         _sql = null;
//      }
//      else
//      {
//         _sql = "\n" + sql.trim();
//      }
   }

   public String toString()
   {
      return _toString;
   }
}
