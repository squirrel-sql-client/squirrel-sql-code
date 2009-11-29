package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcherFactory;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.HashMap;

public class RSyntaxPropertiesWrapper
{
   private HashMap<String, Object> _props;

   public RSyntaxPropertiesWrapper(HashMap<String, Object> props)
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
         IParserEventsProcessorFactory fact = (IParserEventsProcessorFactory) _props.get(IParserEventsProcessorFactory.class.getName());
         return fact.getParserEventsProcessor(sqlEntryPanelIdentifier, sess);
      }
   }

   public ISyntaxHighlightTokenMatcher getSyntaxHighlightTokenMatcher(ISession sess, SquirrelRSyntaxTextArea rSyntaxTextArea, IIdentifier sqlEntryPanelIdentifier)
   {
      if(false == _props.containsKey(ISyntaxHighlightTokenMatcherFactory.class.getName()))
      {
         return new RSyntaxHighlightTokenMatcher(sess, rSyntaxTextArea, sqlEntryPanelIdentifier, this);
      }
      else
      {
         ISyntaxHighlightTokenMatcherFactory fact = (ISyntaxHighlightTokenMatcherFactory) _props.get(ISyntaxHighlightTokenMatcherFactory.class.getName());
         return fact.getSyntaxHighlightTokenMatcher(sess, rSyntaxTextArea);
      }
   }
}