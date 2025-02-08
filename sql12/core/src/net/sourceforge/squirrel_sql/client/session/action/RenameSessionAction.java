package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.ResizableTextEditDialog;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.event.ActionEvent;

/**
 * Class responsible for renaming selected session title.
 * When it is invoked on main session tab, the session itself with all session tabs are renamed. 
 * When is is invoked on the second, third, ... tab, only selected tab is renamed.
 * If one session tab is renamed and then the session itself is renamed, all tabs including the renamed one are renamed.
 * @author Vladislav Vavra
 */
public class RenameSessionAction  extends SquirrelAction implements ISessionAction
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RenameSessionAction.class);

	private ISession _session;

	public RenameSessionAction(IApplication app)
	{
		super(app);
	}

	public void setSession(ISession session)
	{
		_session = session;
	}
	
	/**
	 * Method for renaming a session.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		setSession(_app.getSessionManager().getActiveSession());

        String oldTitle;

        if(!_session.getActiveSessionWindow().equals(_app.getWindowManager().getAllFramesOfSession(_session.getIdentifier())[0]))
        {
            oldTitle = _session.getActiveSessionWindow().getTitle();
        }
        else
        {
            oldTitle = _session.getTitle();
        }

		ResizableTextEditDialog textEditDialog = new ResizableTextEditDialog(_app.getMainFrame(),
																									getClass().getName(),
																									s_stringMgr.getString("RenameSessionAction.title"),
																									s_stringMgr.getString("RenameSessionAction.label"),
																									oldTitle,
																									null);

      if(false == textEditDialog.isOk())
      {
         return;
      }
      

      if(!_session.getActiveSessionWindow().equals(_app.getWindowManager().getAllFramesOfSession(_session.getIdentifier())[0])) 
		{
			_session.getActiveSessionWindow().setTitle(textEditDialog.getEditedText());
		}
		else
		{
			_session.setUserChangedTitle(textEditDialog.getEditedText());
			updateGui();
		}

		Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();
	}
	
	/**
	 * Method for propagating new session title into gui.
	 */
	private void updateGui()
	{
		_app.getMainFrame().repaint();
		ISessionWidget[] sessionSheets = _app.getWindowManager().getAllFramesOfSession(_session.getIdentifier());
		if (sessionSheets.length == 0) return;

		sessionSheets[0].setTitle(_session.getTitle());
		for (int i = 1; i < sessionSheets.length; i++)
		{
			sessionSheets[i].setTitle(_session.getTitle() + " (" + (i + 1) + ")");
		}
	}
}
