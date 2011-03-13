package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupAccessor;
import net.sourceforge.squirrel_sql.client.session.mainpanel.UndoHandlerImpl;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

import javax.swing.*;
import java.util.HashMap;

public class EntryPanelManager
{
   private ISession _session;
   private ISQLEntryPanel _entry;
   private JComponent _component;

   public EntryPanelManager(ISession session)
   {
      _session = session;
   }

   public void init(ISyntaxHighlightTokenMatcherFactory syntaxHighlightTokenMatcherFactory, ToolsPopupAccessor tpa)
   {
      HashMap props = new HashMap();
      props.put(IParserEventsProcessorFactory.class.getName(), null);

      if(null != syntaxHighlightTokenMatcherFactory)
      {
         props.put(ISyntaxHighlightTokenMatcherFactory.class.getName(), syntaxHighlightTokenMatcherFactory);
      }

      if(null != tpa)
      {
         props.put(ToolsPopupAccessor.class.getName(), tpa);
      }


      _entry = _session.getApplication().getSQLEntryPanelFactory().createSQLEntryPanel(_session, props);


      _component = _entry.getTextComponent();
      if (false == _entry.getDoesTextComponentHaveScroller())
      {
         _component = new JScrollPane(_entry.getTextComponent());
         _component.setBorder(BorderFactory.createEmptyBorder());
      }

      new UndoHandlerImpl(_session.getApplication(), _entry);
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
