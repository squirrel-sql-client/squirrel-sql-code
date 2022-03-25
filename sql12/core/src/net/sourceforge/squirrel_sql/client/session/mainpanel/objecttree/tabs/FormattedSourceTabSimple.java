package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

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

import java.sql.PreparedStatement;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfig;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.client.util.codereformat.ICodeReformator;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

/**
 * Simple version of {@link FormattedSourceTab}.
 * Just implement {@link #getSourceCode(ISession, IDatabaseObjectInfo)}
 */
public abstract class FormattedSourceTabSimple extends BaseSourceTab
{
   private FormattedSourceTabCore _core = new FormattedSourceTabCore();

   public FormattedSourceTabSimple(String tooltip, ISession session)
   {
      super(tooltip);

      CodeReformatorConfig config = CodeReformatorConfigFactory.createConfig(session);
      CodeReformator codeReformator = new CodeReformator(CodeReformatorConfigFactory.createConfig(config.getStatementSeparator(), config.getCommentSpecs()));

      _core.setupFormatter(codeReformator, config.getStatementSeparator(), config.getCommentSpecs());
   }

   /**
    * Implement this method to provide databaseObjectInfo source code
    *
    * @param databaseObjectInfo the object to read the source code for.
    */
   protected abstract String getSourceCode(ISession session, IDatabaseObjectInfo databaseObjectInfo);


   /**
    * Optional!
    *
    * Sets up the formatter which formats the source after retrieving it from the ResultSet. If this is not
    * setup prior to loading, then the formatter will not be used - only whitespace compressed if so enabled.
    *
    * @param stmtSep      the formatter needs to know what the statement separator is.
    * @param commentSpecs the types of comments that can be found in the source code. This can be null, and if so, the
    *                     standard comment styles are used (i.e. -- and c-style comments)
    */
   public void setupFormatter(String stmtSep, CommentSpec[] commentSpecs)
   {
      _core.setupFormatter(stmtSep, commentSpecs);
   }

   /**
    * Optional!
    *
    * Sets up a custom formatter implementation which is used to format the source after retrieving it from
    * the ResultSet. If this is not setup prior to loading, then the formatter will not be used - only
    * whitespace compressed if so enabled.
    *
    * @param codeReformator
    * @param stmtSep        the formatter needs to know what the statement separator is.
    * @param commentSpecs   the types of comments that can be found in the source code. This can be null, and if so, the
    *                       standard comment styles are used (i.e. -- and c-style comments)
    */
   public void setupFormatter(ICodeReformator codeReformator, String stmtSep, CommentSpec[] commentSpecs)
   {
      _core.setupFormatter(codeReformator, stmtSep, commentSpecs);
   }


   /**
    * Optional!
    *
    * Whether or not to convert multiple consecutive spaces into a single space.
    *
    * @param compressWhitespace
    */
   protected void setCompressWhitespace(boolean compressWhitespace)
   {
      _core.setCompressWhitespace(compressWhitespace);
   }

   /**
    * Optional!
    */
   protected void setAppendSeparator(boolean b)
   {
      _core.setAppendSeparator(b);
   }

   protected String getStatementSeparator()
   {
      return _core.getStatementSeparator();
   }

   /**
    * @see BaseSourceTab#createSourcePanel()
    */
   @Override
   protected BaseSourcePanel createSourcePanel()
   {
      return new BaseSourcePanel(getSession())
      {
         @Override
         public void load(ISession session, PreparedStatement stmt)
         {
            _core.loadTextArea(getTextArea(), getSourceCode(session, stmt));
         }
      };
   }

   /**
    * PRIVATE
    * DO NOT override for this simple implementation.
    * If you wish to override this method extend {@link FormattedSourceTab} instead.
    */
   protected final String getSourceCode(ISession session, PreparedStatement stmt)
   {
      return getSourceCode(session, getDatabaseObjectInfo());
   }
}