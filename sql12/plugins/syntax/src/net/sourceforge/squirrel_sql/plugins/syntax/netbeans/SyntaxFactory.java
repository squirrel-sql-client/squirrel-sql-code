package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.StatusBar;

import javax.swing.text.Document;
import java.util.Hashtable;
import java.util.Vector;


public class SyntaxFactory
{
   private Hashtable _sessionByDocument = new Hashtable();
   private Hashtable _editorPaneBySessionID = new Hashtable();
   private Hashtable _syntaxesBySessionID = new Hashtable();
   private Hashtable _sqlTokenListenersBySession = new Hashtable();
   private Hashtable _documentBySessionID = new Hashtable();


   public SyntaxFactory()
   {
      LocaleSupport.addLocalizer(new LocaleSupport.Localizer()
      {
         public String getString(String key)
         {
            if(StatusBar.INSERT_LOCALE.equals(key))
            {
               return "INS";
            }
            else if(StatusBar.OVERWRITE_LOCALE.equals(key))
            {
               return "OVR";
            }
            else
            {
               return "Gerd";
            }
         }
      });
   }


   public SQLSyntax getSyntax(Document doc)
   {
      ISession sess = (ISession) _sessionByDocument.get(doc);

      NetbeansSQLEditorPane editor = (NetbeansSQLEditorPane) _editorPaneBySessionID.get(sess.getIdentifier());

      SQLSyntax syntax = new SQLSyntax(sess, editor);

      Vector tokenListeners = (Vector) _sqlTokenListenersBySession.get(sess.getIdentifier());

      if(null != tokenListeners)
      {
         for (int i = 0; i < tokenListeners.size(); i++)
         {
            SQLTokenListener tl = (SQLTokenListener) tokenListeners.elementAt(i);
            syntax.addSQLTokenListener(tl);
         }
      }


      Vector syntaxes = (Vector) _syntaxesBySessionID.get(sess);
      if(null == syntaxes)
      {
         syntaxes = new Vector();
         _syntaxesBySessionID.put(sess.getIdentifier(), syntaxes);
      }
      syntaxes.add(syntax);

      return syntax;

   }

   public void addSQLTokenListeners(ISession sess, SQLTokenListener tl)
   {
      Vector tokenListeners = (Vector) _sqlTokenListenersBySession.get(sess.getIdentifier());

      if(null == tokenListeners)
      {
         tokenListeners = new Vector();
         _sqlTokenListenersBySession.put(sess.getIdentifier(), tokenListeners);
      }
      tokenListeners.add(tl);

      Vector syntaxes = (Vector) _syntaxesBySessionID.get(sess.getIdentifier());

      if(null != syntaxes)
      {
         for (int i = 0; i < syntaxes.size(); i++)
         {
            SQLSyntax syntax = (SQLSyntax) syntaxes.elementAt(i);
            syntax.addSQLTokenListener(tl);
         }
      }
   }

   public void removeSQLTokenListeners(ISession sess, SQLTokenListener tl)
   {
      Vector tokenListeners = (Vector) _sqlTokenListenersBySession.get(sess.getIdentifier());

      if(null == tokenListeners)
      {
         return;
      }

      tokenListeners.remove(tl);

      Vector syntaxes = (Vector) _syntaxesBySessionID.get(sess.getIdentifier());

      for (int i = 0; i < syntaxes.size(); i++)
      {
         SQLSyntax syntax = (SQLSyntax) syntaxes.elementAt(i);
         syntax.removeSQLTokenListener(tl);
      }
   }

   public void putEditorPane(ISession sess, NetbeansSQLEditorPane editor)
   {
      _editorPaneBySessionID.put(sess.getIdentifier(), editor);
   }

   public void putDocument(ISession session, Document document)
   {
      _sessionByDocument.put(document, session);
      _documentBySessionID.put(session.getIdentifier(), document);
   }

   public void sessionEnding(ISession sess)
   {
      Document doc = (Document) _documentBySessionID.remove(sess.getIdentifier());

      if(null != doc)
      {
         _sessionByDocument.remove(doc);
      }

      _editorPaneBySessionID.remove(sess.getIdentifier());
      _syntaxesBySessionID.remove(sess.getIdentifier());
      _sqlTokenListenersBySession.remove(sess.getIdentifier());
   }
}
