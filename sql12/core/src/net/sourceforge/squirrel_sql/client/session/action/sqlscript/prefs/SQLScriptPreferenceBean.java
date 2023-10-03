package net.sourceforge.squirrel_sql.client.session.action.sqlscript.prefs;
/*
 * Copyright (C) 2006 Rob Manning
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

/**
 * A bean class to store preferences for the SQLScript plugin.
 */
public class SQLScriptPreferenceBean
{
   public static final String ESCAPE_NEW_LINE_STRING_DEFAULT = "\\n";

   /**
    * whether or not to qualify table names with the schema when generating
    * scripts
    */
   private boolean qualifyTableNames = true;
   private boolean useDoubleQuotes;

   private boolean escapeNewLine = true;
   private String escapeNewLineString = ESCAPE_NEW_LINE_STRING_DEFAULT;


   public static final int NO_ACTION = 0;

   private int deleteAction = NO_ACTION;

   private int updateAction = NO_ACTION;

   /**
    * whether or not to override the delete referential action for FK defs.
    */
   private boolean deleteRefAction = false;

   /**
    * whether or not to override the update referential action for FK defs.
    */
   private boolean updateRefAction = false;


   /**
    * Sets whether or not to qualify table names with the schema when
    * generating scripts
    *
    * @param qualifyTableNames a boolean value
    */
   public void setQualifyTableNames(boolean qualifyTableNames)
   {
      this.qualifyTableNames = qualifyTableNames;
   }

   /**
    * Returns a boolean value indicating whether or not to qualify table names
    * with the schema when generating scripts
    *
    * @return Returns the value of qualifyTableNames.
    */
   public boolean isQualifyTableNames()
   {
      return qualifyTableNames;
   }

   public void setDeleteRefAction(boolean deleteRefAction)
   {
      this.deleteRefAction = deleteRefAction;
   }

   public boolean isDeleteRefAction()
   {
      return deleteRefAction;
   }

   public void setDeleteAction(int action)
   {
      this.deleteAction = action;
   }

   public int getDeleteAction()
   {
      return deleteAction;
   }

   public void setUpdateAction(int updateAction)
   {
      this.updateAction = updateAction;
   }

   public int getUpdateAction()
   {
      return updateAction;
   }

   public void setUpdateRefAction(boolean updateRefAction)
   {
      this.updateRefAction = updateRefAction;
   }

   public boolean isUpdateRefAction()
   {
      return updateRefAction;
   }

   public boolean isUseDoubleQuotes()
   {
      return useDoubleQuotes;
   }

   public void setUseDoubleQuotes(boolean b)
   {
      useDoubleQuotes = b;
   }

   public boolean isEscapeNewLine()
   {
      return escapeNewLine;
   }

   public void setEscapeNewLine(boolean escapeNewLine)
   {
      this.escapeNewLine = escapeNewLine;
   }

   public void setEscapeNewLineString(String escapeNewLineString)
   {
      this.escapeNewLineString = escapeNewLineString;
   }

   public String getEscapeNewLineString()
   {
      return escapeNewLineString;
   }
}

