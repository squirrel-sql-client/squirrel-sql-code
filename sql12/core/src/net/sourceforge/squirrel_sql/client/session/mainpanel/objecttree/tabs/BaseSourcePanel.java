package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLEntryPanelUtil;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

abstract public class BaseSourcePanel extends JPanel {

	private JTextComponent textArea;
	private ISession session;
	
    public BaseSourcePanel(ISession session) {
    	super(new BorderLayout());
    	setSession(session);
		createUserInterface();
    }
    
    /**
     * Create the user interface.
     * The created {@link JTextComponent} depends on the {@link ISQLEntryPanelFactory}.
     * This enables support for Syntax-Highlighting, if the syntax plugin is loaded.
     */
    protected void createUserInterface()
    {
    	
    	HashMap<String, Object> props = new HashMap<String, Object>();
    	props.put(IParserEventsProcessorFactory.class.getName(), null);
    	
		ISQLEntryPanel sqlPanel = getSession().getApplication().getSQLEntryPanelFactory().createSQLEntryPanel(getSession(), props );
		JTextComponent textComponent = sqlPanel.getTextComponent();
		textComponent.setEditable(false);
		setTextArea(textComponent);
		add(getTextArea(), BorderLayout.CENTER);
    }
    
    public abstract void load(ISession session, PreparedStatement stmt);

	/**
	 * @return the textArea
	 */
	public JTextComponent getTextArea() {
		return textArea;
	}

	/**
	 * @param textArea the textArea to set
	 */
	private void setTextArea(JTextComponent textArea) {
		if(textArea == null){
			throw new IllegalArgumentException("textArea == null");
		}
		this.textArea = textArea;
	}

	/**
	 * @return the session
	 */
	public ISession getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	private void setSession(ISession session) {
		if(session == null){
			throw new IllegalArgumentException("session == null");
		}
		this.session = session;
	}
}
