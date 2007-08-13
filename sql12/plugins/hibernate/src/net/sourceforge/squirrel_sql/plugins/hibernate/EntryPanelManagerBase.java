package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcherFactory;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SquirrelDefaultUndoManager;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import java.util.HashMap;

public class EntryPanelManagerBase
{
   private ISession _session;
   private ISQLEntryPanel _entry;
   private JComponent _component;

   public EntryPanelManagerBase(ISession session)
   {
      _session = session;
   }

   public void init(ISyntaxHighlightTokenMatcherFactory syntaxHighlightTokenMatcherFactory)
   {
      HashMap props = new HashMap();
      props.put(IParserEventsProcessorFactory.class.getName(), null);

      if(null != syntaxHighlightTokenMatcherFactory)
      {
         props.put(ISyntaxHighlightTokenMatcherFactory.class.getName(), syntaxHighlightTokenMatcherFactory);
      }


      _entry = _session.getApplication().getSQLEntryPanelFactory().createSQLEntryPanel(_session, props);


      _component = _entry.getTextComponent();
      if (false == _entry.getDoesTextComponentHaveScroller())
      {
         _component = new JScrollPane(_entry.getTextComponent());
         _component.setBorder(BorderFactory.createEmptyBorder());
      }

      if (!_entry.hasOwnUndoableManager())
      {
         SquirrelDefaultUndoManager undoManager = new SquirrelDefaultUndoManager();
         IApplication app = _session.getApplication();
         Resources res = app.getResources();
         UndoAction undoAction = new UndoAction(app, undoManager);
         RedoAction redoAction = new RedoAction(app, undoManager);

         JComponent comp = _entry.getTextComponent();
         comp.registerKeyboardAction(undoAction, res.getKeyStroke(undoAction),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         comp.registerKeyboardAction(redoAction, res.getKeyStroke(redoAction),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         _entry.setUndoActions(undoAction, redoAction);

         _entry.setUndoManager(undoManager);
      }
   }

   public JComponent getComponent()
   {
      checkInit();
      return _component;
   }

   private void checkInit()
   {
      if(null == _component)
      {
         throw new IllegalStateException("Call init() before using this object");
      }
   }

   public ISQLEntryPanel getEntryPanel()
   {
      checkInit();
      return _entry;
   }

   public void requestFocus()
   {
      checkInit();
      _entry.requestFocus();
   }

   protected ISession getSession()
   {
      return _session;
   }


}
