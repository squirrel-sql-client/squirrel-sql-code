package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003 Gerd Wagner
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
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
/**
 * This command will &quot;quote&quot; an SQL string.
 *
 * @author  Gerd Wagner
 */
class FormatSQLCommand implements ICommand
{
	private final ISession _session;
   private ISQLPanelAPI _panel;

   FormatSQLCommand(ISession session, ISQLPanelAPI panel)
	{
		super();
		_session = session;
      _panel = panel;
   }

	public void execute() throws BaseException
	{

      int[] bounds = _panel.getSQLEntryPanel().getBoundsOfSQLToBeExecuted();

      if(bounds[0] == bounds[1])
      {
         return;
      }

      String textToReformat = _panel.getSQLEntryPanel().getSQLToBeExecuted();

		if (null == textToReformat)
		{
			return;
		}

		CommentSpec[] commentSpecs =
		  new CommentSpec[]
		  {
			  new CommentSpec("/*", "*/"),
			  new CommentSpec("--", StringUtilities.getEolStr())
		  };

		String statementSep = _session.getQueryTokenizer().getSQLStatementSeparator();
		
		CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(_session));

		String reformatedText = cr.reformat(textToReformat);

      _panel.getSQLEntryPanel().setSelectionStart(bounds[0]);
      _panel.getSQLEntryPanel().setSelectionEnd(bounds[1]);
      _panel.getSQLEntryPanel().replaceSelection(reformatedText);

	}
}
