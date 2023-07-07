package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.sql.PreparedStatement;
import java.util.HashMap;

abstract public class BaseSourcePanel extends JPanel
{

   private JTextComponent _textComponent;
   private ISession _session;

   public BaseSourcePanel(ISession session)
   {
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

      ISQLEntryPanel sqlPanel = getSession().getApplication().getSQLEntryPanelFactory().createSQLEntryPanel(getSession(), props);
      _textComponent = sqlPanel.getTextComponent();
      _textComponent.setEditable(false);

      //TextFindCtrl textFindCtrl = new TextFindCtrl(sqlPanel.getTextComponent(), sqlPanel.getTextAreaEmbeddedInScrollPane(), true);
      //add(textFindCtrl.getContainerPanel(), BorderLayout.NORTH);
      //sqlPanel.getTextAreaEmbeddedInScrollPane().setPreferredSize(new Dimension(1,1));
      //add(sqlPanel.getTextAreaEmbeddedInScrollPane(), BorderLayout.CENTER);
      add(sqlPanel.getTextComponent(), BorderLayout.CENTER);
   }

   public abstract void load(ISession session, PreparedStatement stmt);

   /**
    * @return the textArea
    */
   public JTextComponent getTextArea()
   {
      return _textComponent;
   }

   /**
    * @return the session
    */
   public ISession getSession()
   {
      return _session;
   }

   /**
    * @param session the session to set
    */
   private void setSession(ISession session)
   {
      if (session == null)
      {
         throw new IllegalArgumentException("session == null");
      }
      this._session = session;
   }
}
