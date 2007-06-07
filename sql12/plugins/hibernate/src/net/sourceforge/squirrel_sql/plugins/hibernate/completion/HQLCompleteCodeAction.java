package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.completion.Completor;
import net.sourceforge.squirrel_sql.fw.completion.CompletorListener;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;

import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class HQLCompleteCodeAction extends SquirrelAction
{
	private ISQLEntryPanel _sqlEntryPanel;
	private Completor _cc;



	public HQLCompleteCodeAction(IApplication app,
                             HibernatePlugin plugin,
                             ISQLEntryPanel sqlEntryPanel,
                             ISession session,
                             HibernateConnection con)
	{
		super(app, plugin.getResources());
		_sqlEntryPanel = sqlEntryPanel;

		HQLCodeCompletorModel model = new HQLCodeCompletorModel(session, plugin, new HQLCompletionInfos(con), sqlEntryPanel.getIdentifier());
		_cc = new Completor((JTextComponent)_sqlEntryPanel.getTextComponent(), model);

		_cc.addCodeCompletorListener
		(
			new CompletorListener()
			{
				public void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode)
				{
               performCompletionSelected((HQLCompletionInfo) completion, replaceBegin, keyCode);
            }
			}
		);
	}


	public void actionPerformed(ActionEvent evt)
	{
		_cc.show();
	}



	private void performCompletionSelected(HQLCompletionInfo completion, int replaceBegin, int keyCode)
	{
		if(KeyEvent.VK_TAB == keyCode)
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
