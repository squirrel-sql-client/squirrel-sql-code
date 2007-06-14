package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.IHibernateConnectionProvider;

import javax.swing.text.JTextComponent;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class HQLCompleteCodeAction extends SquirrelAction
{
	private ISQLEntryPanel _sqlEntryPanel;
   private IHibernateConnectionProvider _hibernateConnectionProvider;
   private Completor _cc;
   private HQLCodeCompletorModel _model;


   public HQLCompleteCodeAction(IApplication app,
                                HibernatePlugin plugin,
                                ISQLEntryPanel sqlEntryPanel,
                                IHibernateConnectionProvider hibernateConnectionProvider)
	{
		super(app, plugin.getResources());
		_sqlEntryPanel = sqlEntryPanel;
      _hibernateConnectionProvider = hibernateConnectionProvider;

      _model = new HQLCodeCompletorModel(hibernateConnectionProvider);
      _cc = new Completor((JTextComponent)_sqlEntryPanel.getTextComponent(), _model);

		_cc.addCodeCompletorListener
		(
			new CompletorListener()
			{
				public void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
				{
               performCompletionSelected(completion, replaceBegin, keyCode, modifiers);
            }
			}
		);
	}


	public void actionPerformed(ActionEvent evt)
	{
      if(null != _hibernateConnectionProvider.getHibernateConnection())
      {
         _cc.show();
      }
   }



	private void performCompletionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
	{
      if(KeyEvent.VK_SPACE == keyCode && modifiers == KeyEvent.CTRL_MASK)
      {
         // Code Completion has been done within Code Completion. Now just replace what all candidates have in common.

         CompletionCandidates completionCandidates = _model.getCompletionCandidates(_cc.getTextTillCarret());

         _sqlEntryPanel.setSelectionStart(replaceBegin);
         _sqlEntryPanel.setSelectionEnd(_sqlEntryPanel.getCaretPosition());
         _sqlEntryPanel.replaceSelection(completionCandidates.getAllCandidatesPrefix(true));

         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _cc.show();
            }
         });

      }
		else if(KeyEvent.VK_TAB == keyCode)
		{
			_sqlEntryPanel.setSelectionStart(replaceBegin);
			_sqlEntryPanel.setSelectionEnd(getNextWhiteSpacePos(_sqlEntryPanel.getCaretPosition()));
			_sqlEntryPanel.replaceSelection(completion.getCompletionString());
		}
		else
		{
			_sqlEntryPanel.setSelectionStart(replaceBegin);
			_sqlEntryPanel.setSelectionEnd(_sqlEntryPanel.getCaretPosition());
			_sqlEntryPanel.replaceSelection(completion.getCompletionString());
		}
	}

	private int getNextWhiteSpacePos(int startPos)
	{
		String text = _sqlEntryPanel.getText();

		int retPos = startPos;

		for(;retPos < text.length(); ++retPos)
		{
			if(Character.isWhitespace(text.charAt(retPos)))
			{
				return retPos;
			}
		}

		return retPos;
	}
}
