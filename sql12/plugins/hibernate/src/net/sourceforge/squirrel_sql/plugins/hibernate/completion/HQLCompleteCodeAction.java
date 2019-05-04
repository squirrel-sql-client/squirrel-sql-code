package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SimpleSessionListener;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;
import net.sourceforge.squirrel_sql.plugins.hibernate.HQLEntryPanelManager;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateChannel;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;
import net.sourceforge.squirrel_sql.plugins.hibernate.HqlSyntaxHighlightTokenMatcherProxy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class HQLCompleteCodeAction extends SquirrelAction
{
	private ISQLEntryPanel _hqlEntryPanel;
   private HibernateChannel _hibernateChannel;
   private Completor _cc;
   private HQLCodeCompletorModel _model;


   public HQLCompleteCodeAction(IApplication app,
                                HibernatePluginResources resources,
                                HQLEntryPanelManager hqlEntryPanelManager,
                                HibernateChannel hibernateChannel,
                                HqlSyntaxHighlightTokenMatcherProxy hqlSyntaxHighlightTokenMatcherProxy,
                                ISession session)
	{
		super(app, resources);
		_hqlEntryPanel = hqlEntryPanelManager.getEntryPanel();
      _hibernateChannel = hibernateChannel;

      _model = new HQLCodeCompletorModel(hibernateChannel, new HQLAliasFinder(_hqlEntryPanel), hqlSyntaxHighlightTokenMatcherProxy);


      CompletorListener completorListener = new CompletorListener()
      {
         public void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
         {
            performCompletionSelected(completion, replaceBegin, keyCode, modifiers);
         }
      };

      _cc = new Completor(_hqlEntryPanel.getTextComponent(), _model, completorListener);

      session.addSimpleSessionListener(new SimpleSessionListener()
      {
         public void sessionClosed()
         {
            _cc.disposePopup();
         }
      });


   }


	public void actionPerformed(ActionEvent evt)
	{
      if(null != _hibernateChannel.getHibernateConnection())
      {
         _cc.show();
      }
   }



	private void performCompletionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers)
	{
      if(KeyEvent.VK_SPACE == keyCode && modifiers == KeyEvent.CTRL_MASK)
      {
         // Code Completion has been done within Code Completion. Now just replace what all candidates have in common.

         CompletionCandidates completionCandidates = _model.getCompletionCandidates(_cc.getTextTillCaret());

         _hqlEntryPanel.setSelectionStart(replaceBegin);
         _hqlEntryPanel.setSelectionEnd(_hqlEntryPanel.getCaretPosition());
         _hqlEntryPanel.replaceSelection(completionCandidates.getAllCandidatesPrefix(true));

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
			_hqlEntryPanel.setSelectionStart(replaceBegin);
			_hqlEntryPanel.setSelectionEnd(getNextWhiteSpacePos(_hqlEntryPanel.getCaretPosition()));
			_hqlEntryPanel.replaceSelection(completion.getCompletionString());
		}
		else
		{
			_hqlEntryPanel.setSelectionStart(replaceBegin);
			_hqlEntryPanel.setSelectionEnd(_hqlEntryPanel.getCaretPosition());
			_hqlEntryPanel.replaceSelection(completion.getCompletionString());
		}
	}

	private int getNextWhiteSpacePos(int startPos)
	{
		String text = _hqlEntryPanel.getText();

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
