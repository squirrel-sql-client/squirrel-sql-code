package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcherFactory;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

import java.util.HashMap;

public class NetbeansPropertiesWrapper
{
   private HashMap<String, IParserEventsProcessorFactory> _props;

   public NetbeansPropertiesWrapper(HashMap<String, IParserEventsProcessorFactory> props)
   {
      _props = props;
   }

   public IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier, ISession sess)
   {
      if(false == _props.containsKey(IParserEventsProcessorFactory.class.getName()))
      {
         return sess.getParserEventsProcessor(sqlEntryPanelIdentifier);
      }
      else if(null == _props.get(IParserEventsProcessorFactory.class.getName()))
      {
         return null;
      }
      else
      {
         IParserEventsProcessorFactory fact = _props.get(IParserEventsProcessorFactory.class.getName());
         return fact.getParserEventsProcessor(sqlEntryPanelIdentifier, sess);
      }
   }

   public ISyntaxHighlightTokenMatcher getSyntaxHighlightTokenMatcher(ISession sess, NetbeansSQLEditorPane editorPane)
   {
      if(false == _props.containsKey(ISyntaxHighlightTokenMatcherFactory.class.getName()))
      {
         return new SqlSyntaxHighlightTokenMatcher(sess, editorPane);
      }
      else
      {
         ISyntaxHighlightTokenMatcherFactory fact = (ISyntaxHighlightTokenMatcherFactory) _props.get(ISyntaxHighlightTokenMatcherFactory.class.getName());
         return fact.getSyntaxHighlightTokenMatcher(sess, editorPane);
      }
   }
}
