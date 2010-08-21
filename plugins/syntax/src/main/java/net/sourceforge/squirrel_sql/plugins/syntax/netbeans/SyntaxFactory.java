package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.StatusBar;
import org.netbeans.editor.Syntax;

import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;


/**
 * This class is used in the SQLKit.createSyntax() method.
 * In this method the right SQLSyntax object initialized
 * with the right session must be returned because
 * usually different sessions have different key words, tables, ...
 *
 * The only key we are given in SQLKit.createSyntax() is the document
 * of the JEditorPane of the session. This class provides the mapping
 * of document to session. This mapping is initialized through the
 * calls of putEditorPane() and putDocument() in the constructor of
 * NetbeansSQLEditorPane.
 */
public class SyntaxFactory
{
	private Hashtable<Document, DocumentAssignedObjects> _documentAssignedObjectsByDocument = new Hashtable<Document, DocumentAssignedObjects>();
	private Hashtable<IIdentifier, ArrayList<NetbeansSQLEditorPane>> _editorPanesBySessionID = new Hashtable<IIdentifier, ArrayList<NetbeansSQLEditorPane>>();
	private Hashtable<IIdentifier, Vector<SQLSyntax>> _syntaxesBySessionID = new Hashtable<IIdentifier, Vector<SQLSyntax>>();
	private Hashtable<IIdentifier ,Vector<SQLTokenListener>> _sqlTokenListenersBySession = new Hashtable<IIdentifier ,Vector<SQLTokenListener>>();
	private Hashtable<IIdentifier, ArrayList<Document>> _documentsBySessionID = new Hashtable<IIdentifier, ArrayList<Document>>();


	public SyntaxFactory()
	{

		final ResourceBundle bundle = ResourceBundle.getBundle("org.netbeans.editor.Bundle");

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
					return bundle.getString(key);
				}
			}
		});
	}


	public Syntax getSyntax(Document doc)
	{
      DocumentAssignedObjects docAssigendObjs = _documentAssignedObjectsByDocument.get(doc);

      if(null == docAssigendObjs)
		{
			// Once and again the Netbeans editor calls createSyntax() after
			// sessionEnding() was called. Then sess is null and the code below
			// would break.
			return new Syntax();
		}


		ArrayList<NetbeansSQLEditorPane> editors = _editorPanesBySessionID.get(docAssigendObjs.getSession().getIdentifier());


      NetbeansSQLEditorPane editorMatchingDocument = null;
      for (NetbeansSQLEditorPane editor : editors)
      {
         if(doc.equals(editor.getDocument()))
         {
            editorMatchingDocument = editor;
            break;
         }
      }

      if(null == editorMatchingDocument)
      {
         throw new IllegalStateException("No Editor matching document found");
      }


      SQLSyntax syntax = new SQLSyntax(docAssigendObjs.getSession(), editorMatchingDocument, docAssigendObjs.getProperties());

		Vector<SQLTokenListener> tokenListeners = _sqlTokenListenersBySession.get(docAssigendObjs.getSession().getIdentifier());

		if(null != tokenListeners)
		{
			for (int i = 0; i < tokenListeners.size(); i++)
			{
				SQLTokenListener tl = tokenListeners.elementAt(i);
				syntax.addSQLTokenListener(tl);
			}
		}


		Vector<SQLSyntax> syntaxes = _syntaxesBySessionID.get(docAssigendObjs.getSession().getIdentifier());
		if(null == syntaxes)
		{
			syntaxes = new Vector<SQLSyntax>();
			_syntaxesBySessionID.put(docAssigendObjs.getSession().getIdentifier(), syntaxes);
		}
		syntaxes.add(syntax);

		return syntax;

	}

	public void addSQLTokenListeners(ISession sess, SQLTokenListener tl)
	{
		Vector<SQLTokenListener> tokenListeners = _sqlTokenListenersBySession.get(sess.getIdentifier());

		if(null == tokenListeners)
		{
			tokenListeners = new Vector<SQLTokenListener>();
			_sqlTokenListenersBySession.put(sess.getIdentifier(), tokenListeners);
		}
		tokenListeners.add(tl);

		Vector<SQLSyntax> syntaxes = _syntaxesBySessionID.get(sess.getIdentifier());

		if(null != syntaxes)
		{
			for (int i = 0; i < syntaxes.size(); i++)
			{
				SQLSyntax syntax = syntaxes.elementAt(i);
				syntax.addSQLTokenListener(tl);
			}
		}
	}

	public void removeSQLTokenListeners(ISession sess, SQLTokenListener tl)
	{
		Vector<SQLTokenListener> tokenListeners = _sqlTokenListenersBySession.get(sess.getIdentifier());

		if(null == tokenListeners)
		{
			return;
		}

		tokenListeners.remove(tl);

		Vector<SQLSyntax> syntaxes = _syntaxesBySessionID.get(sess.getIdentifier());

		for (int i = 0; i < syntaxes.size(); i++)
		{
			SQLSyntax syntax = syntaxes.elementAt(i);
			syntax.removeSQLTokenListener(tl);
		}
	}

	public void putEditorPane(ISession sess, NetbeansSQLEditorPane editor)
	{
      ArrayList<NetbeansSQLEditorPane> buf = _editorPanesBySessionID.get(sess.getIdentifier());

      if(null == buf)
      {
         buf = new ArrayList<NetbeansSQLEditorPane>();
         _editorPanesBySessionID.put(sess.getIdentifier(), buf);

      }

      buf.remove(editor);
      buf.add(editor);
	}

	public void putDocument(ISession session, NetbeansPropertiesWrapper wrp, Document document)
	{
		_documentAssignedObjectsByDocument.put(document, new DocumentAssignedObjects(session, wrp));

		ArrayList<Document> docs = _documentsBySessionID.get(session.getIdentifier());

		if(null == docs)
		{
			docs = new ArrayList<Document>();
			_documentsBySessionID.put(session.getIdentifier(), docs);
		}
		docs.add(document);
	}

	public void sessionEnding(ISession sess)
	{
		ArrayList<Document> docs = _documentsBySessionID.remove(sess.getIdentifier());

		if(null != docs)
		{
         for (Document doc : docs)
         {
            _documentAssignedObjectsByDocument.remove(doc);
         }
      }

		_editorPanesBySessionID.remove(sess.getIdentifier());
		_syntaxesBySessionID.remove(sess.getIdentifier());
		_sqlTokenListenersBySession.remove(sess.getIdentifier());
	}


   private static class DocumentAssignedObjects
   {
      private ISession _session;
      private NetbeansPropertiesWrapper _wrp;

      public DocumentAssignedObjects(ISession session, NetbeansPropertiesWrapper wrp)
      {
         _session = session;
         _wrp = wrp;
      }


      public ISession getSession()
      {
         return _session;
      }

      public NetbeansPropertiesWrapper getProperties()
      {
         return _wrp;
      }
   }
}
