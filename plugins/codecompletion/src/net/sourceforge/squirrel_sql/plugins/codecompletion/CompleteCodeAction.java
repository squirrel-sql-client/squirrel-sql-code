package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CompleteCodeAction extends SquirrelAction
{
   private ISQLEntryPanel _sqlEntryPanel;
   private CodeCompletor _cc;

   private static final char[] SEPARATORS = {' ', '\t', '\n' ,  ',', '('};


	public CompleteCodeAction(IApplication app, PluginResources rsrc, ISQLEntryPanel sqlEntryPanel, ISession session, CodeCompletionInfoCollection codeCompletionInfos)
   {
      super(app, rsrc);
      _sqlEntryPanel = sqlEntryPanel;

		CodeCompletorModel model = new CodeCompletorModel(session, codeCompletionInfos);
      _cc = new CodeCompletor((JTextComponent)_sqlEntryPanel.getTextComponent(), model);
		_sqlEntryPanel.addSQLTokenListener(model.getSQLTokenListener());

      _cc.addCodeCompletorListener
      (
         new CodeCompletorListener()
         {
            public void completionSelected(String completion)
            {performCompletionSelected(completion);}
         }
      );
   }


	public void actionPerformed(ActionEvent evt)
   {
		try
		{
			String lineTillCaret = getLineTillCaret();

			String beginning = "";
			if(0 != lineTillCaret.trim().length() && !Character.isWhitespace(lineTillCaret.charAt(lineTillCaret.length() - 1)) )
			{
				String trimmedLineTillCaret = lineTillCaret.trim();

				int lastSeparatorIndex = getLastSeparatorIndex(trimmedLineTillCaret);
				if(-1 == lastSeparatorIndex)
				{
					beginning = trimmedLineTillCaret;
				}
				else
				{
					beginning = trimmedLineTillCaret.substring(lastSeparatorIndex + 1, trimmedLineTillCaret.length());
				}
			}

			int caretPos = _sqlEntryPanel.getCaretPosition();
			Rectangle caretCoords = ((JTextComponent)_sqlEntryPanel.getTextComponent()).modelToView(caretPos);
			_cc.show(beginning);
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}

   private int getLastSeparatorIndex(String str)
   {
      int lastSeparatorIndex = -1;
      for(int i=0; i < SEPARATORS.length; ++i)
      {
         int buf = str.lastIndexOf(SEPARATORS[i]);
         if(buf > lastSeparatorIndex)
         {
            lastSeparatorIndex = buf;
         }
      }
      return lastSeparatorIndex;
   }

   private boolean isSeparator(char c)
   {
      for(int i=0; i < SEPARATORS.length; ++i)
      {
         if(SEPARATORS[i] == c)
         {
            return true;
         }
      }
      return false;
   }


   private String getLineTillCaret()
   {
		String textTillCarret = _sqlEntryPanel.getText().substring(0, _sqlEntryPanel.getCaretPosition());

		int lineFeedIndex = textTillCarret.lastIndexOf('\n');
		if(- 1 == lineFeedIndex)
		{
			return textTillCarret;
		}
		else
		{
			return textTillCarret.substring(lineFeedIndex + 1);
		}
//			int  caretXDist = _sqlEntryPanel.getCaretLinePosition();
//			//System.out.println("line till carr: " + ((JTextPane)_sqlEntryPanel.getTextComponent()).getText(_sqlEntryPanel.getCaretPosition() - _sqlEntryPanel.getCaretLinePosition(), caretXDist));
//			return ((JTextPane)_sqlEntryPanel.getTextComponent()).getText(_sqlEntryPanel.getCaretPosition() - _sqlEntryPanel.getCaretLinePosition(), caretXDist);
	}



   private void performCompletionSelected(String completion)
   {
		boolean completionDone = false;
		int lineBegin = _sqlEntryPanel.getCaretLineNumber();
		int curPos = _sqlEntryPanel.getCaretPosition() - 1;
		while(curPos >= lineBegin)
		{
			char c = _sqlEntryPanel.getText().substring(curPos, curPos + 1).charAt(0);
			//char c = ((JTextPane)_sqlEntryPanel.getTextComponent()).getText(curPos, 1).charAt(0);
			if('.' == c || isSeparator(c))
			{
				_sqlEntryPanel.setSelectionStart(curPos + 1);
				_sqlEntryPanel.setSelectionEnd(_sqlEntryPanel.getCaretPosition());

				_sqlEntryPanel.replaceSelection(completion);
				completionDone = true;
				break;
			}
			--curPos;
		}

		if(!completionDone)
		{
			_sqlEntryPanel.setSelectionStart(curPos + 1);
			_sqlEntryPanel.setSelectionEnd(_sqlEntryPanel.getCaretPosition());
			_sqlEntryPanel.replaceSelection(completion);
		}
	}
}