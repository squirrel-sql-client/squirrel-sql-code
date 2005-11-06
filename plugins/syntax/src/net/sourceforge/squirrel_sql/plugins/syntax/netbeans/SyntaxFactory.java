package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.StatusBar;
import org.netbeans.editor.Syntax;

import javax.swing.text.Document;
import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.ArrayList;


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
	private Hashtable _sessionByDocument = new Hashtable();
	private Hashtable _editorPaneBySessionID = new Hashtable();
	private Hashtable _syntaxesBySessionID = new Hashtable();
	private Hashtable _sqlTokenListenersBySession = new Hashtable();
	private Hashtable _documentsBySessionID = new Hashtable();


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
		ISession sess = (ISession) _sessionByDocument.get(doc);

		if(null == sess)
		{
			// Once and again the Netbeans editor calls createSyntax() after
			// sessionEnding() was called. Then sess is null and the code below
			// would break.
			return new Syntax();
		}


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


		Vector syntaxes = (Vector) _syntaxesBySessionID.get(sess.getIdentifier());
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

		ArrayList docs = (ArrayList) _documentsBySessionID.get(session.getIdentifier());

		if(null == docs)
		{
			docs = new ArrayList();
			_documentsBySessionID.put(session.getIdentifier(), docs);
		}
		docs.add(document);
	}

	public void sessionEnding(ISession sess)
	{
		ArrayList docs = (ArrayList) _documentsBySessionID.remove(sess.getIdentifier());

		if(null != docs)
		{
			for (int i = 0; i < docs.size(); i++)
			{
				_sessionByDocument.remove(docs.get(i));
			}
		}

		_editorPaneBySessionID.remove(sess.getIdentifier());
		_syntaxesBySessionID.remove(sess.getIdentifier());
		_sqlTokenListenersBySession.remove(sess.getIdentifier());
	}
}
