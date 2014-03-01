package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Class responsible for renaming selected session title.
 * When it is invoked on main session tab, the session itself with all session tabs are renamed. 
 * When is is invoked on the second, third, ... tab, only selected tab is renamed.
 * If one session tab is renamed and then the session itself is renamed, all tabs including the renamed one are renamed.
 * @author Vladislav Vavra
 */
public class RenameSessionAction  extends SquirrelAction
									implements ISessionAction
{
	
	private ISession _session;

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameSessionAction.class);	
	
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

      String newTitle = JOptionPane.showInputDialog(_app.getMainFrame(),
            s_stringMgr.getString("RenameSessionAction.label"),
            s_stringMgr.getString("RenameSessionAction.title"),
            JOptionPane.QUESTION_MESSAGE);

      
      if(null == newTitle)
      {
         // Dialog was canceled.
         return;
      }
      

      if(!_session.getActiveSessionWindow().equals(_app.getWindowManager().getAllFramesOfSession(_session.getIdentifier())[0])) 
		{
			_session.getActiveSessionWindow().setTitle(newTitle);
		}
		else
		{
			_session.setTitle(newTitle);
			updateGui();
		}
	}
	
	/**
	 * Method for propagating new session title into gui.
	 */
	private void updateGui() {
		_app.getMainFrame().repaint();
		ISessionWidget[] sessionSheets = _app.getWindowManager().getAllFramesOfSession(_session.getIdentifier());
		if(sessionSheets.length==0) return;
			
		sessionSheets[0].setTitle(_session.getTitle());
		for(int i=1;i<sessionSheets.length;i++) {
			sessionSheets[i].setTitle(_session.getTitle()+" (" + (i + 1) + ")");
		}		
	}
}
