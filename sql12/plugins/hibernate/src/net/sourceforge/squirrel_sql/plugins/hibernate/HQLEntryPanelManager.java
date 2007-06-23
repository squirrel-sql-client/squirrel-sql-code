package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.client.session.action.RedoAction;
import net.sourceforge.squirrel_sql.client.session.action.UndoAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SquirrelDefaultUndoManager;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import javax.swing.*;
import java.util.Properties;
import java.util.HashMap;

public class HQLEntryPanelManager implements IHqlEntryPanelManager
{
   private ISession _session;
   private ISQLEntryPanel _sqlEntry;
   private JComponent _component;

   public HQLEntryPanelManager(ISession session)
   {
      _session = session;

      HashMap props = new HashMap();
      props.put(IParserEventsProcessorFactory.class.getName(), null);

      _sqlEntry = _session.getApplication().getSQLEntryPanelFactory().createSQLEntryPanel(_session, props);


      _component = _sqlEntry.getTextComponent();
      if (false == _sqlEntry.getDoesTextComponentHaveScroller())
      {
         _component = new JScrollPane(_sqlEntry.getTextComponent());
         _component.setBorder(BorderFactory.createEmptyBorder());
      }

      if (!_sqlEntry.hasOwnUndoableManager())
      {
         SquirrelDefaultUndoManager undoManager = new SquirrelDefaultUndoManager();
         IApplication app = _session.getApplication();
         Resources res = app.getResources();
         UndoAction undoAction = new UndoAction(app, undoManager);
         RedoAction redoAction = new RedoAction(app, undoManager);

         JComponent comp = _sqlEntry.getTextComponent();
         comp.registerKeyboardAction(undoAction, res.getKeyStroke(undoAction),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         comp.registerKeyboardAction(redoAction, res.getKeyStroke(redoAction),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         _sqlEntry.setUndoActions(undoAction, redoAction);

         _sqlEntry.setUndoManager(undoManager);
      }

   }



   public JComponent  getComponent()
   {
      return _component;
   }

   public ISQLEntryPanel getEntryPanel()
   {
      return _sqlEntry;
   }

   public void requestFocus()
   {
      _sqlEntry.requestFocus();
   }
}
