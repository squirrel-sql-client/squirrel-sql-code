package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import javax.swing.text.Document;
import javax.swing.text.Keymap;

import org.netbeans.editor.*;
import org.netbeans.editor.ext.ExtSettingsInitializer;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class NetbeansSQLEditorPane extends JEditorPane
{
   private SQLKit _sqlKit;
   private boolean _parsingInitialized;
   private ISession _session;
   private ErrorInfo[] _currentErrorInfos = new ErrorInfo[0];
   private SyntaxPreferences _prefs;

   public NetbeansSQLEditorPane(ISession session, SyntaxPreferences prefs, SyntaxFactory syntaxFactory)
   {
      _session = session;
      _prefs = prefs;
      syntaxFactory.putEditorPane(_session, this);


      Settings.removeInitializer(BaseSettingsInitializer.NAME);
      Settings.addInitializer(new BaseSettingsInitializer(), Settings.CORE_LEVEL);
      /////////////////////////////////////////////////////////////////////////////////
      // There are a lot of goodies in the ExtSettingsInitializer
      // that might be interesting in th future.
      // Unfortunately some conflicts with some of Squirrels shortcuts
      // are in ExtSettingsInitializer
      //Settings.removeInitializer(ExtSettingsInitializer.NAME);
      //Settings.addInitializer(new ExtSettingsInitializer(), Settings.CORE_LEVEL);
      //
      /////////////////////////////////////////////////////////////////////////////
      Settings.removeInitializer(SQLSettingsInitializer.NAME);
      Settings.addInitializer(new SQLSettingsInitializer(SQLKit.class, prefs));

      _sqlKit = new SQLKit(syntaxFactory);

      setEditorKit(_sqlKit);

      Document doc = getDocument();
      syntaxFactory.putDocument(session, doc);
      



//      final JComponent c = (getUI() instanceof BaseTextUI) ?
//      Utilities.getEditorUI(this).getExtComponent() : new JScrollPane( this );
//      Document doc = getDocument();

      //doc.addDocumentListener( new MarkingDocumentListener( c ) );


      UndoManager um = new UndoManager();
      doc.addUndoableEditListener( um );
      doc.putProperty( BaseDocument.UNDO_MANAGER_PROP, um );

      //new NetbeansKeyManager(this);


      //////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // The ctrl enter short cut in the Netbeans editor is set in org.netbeans.editor.ext.BaseKit
      // to the org.netbeans.editor.ext.BaseKit.SplitLineAction.
      // Since the ctrl enter shorcut is a basic SQuirreL short cut and is defined via the main menu action
      // we must remove this binding here.
      KeyStroke ctrlEnterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK);
      getKeymap().removeKeyStrokeBinding(ctrlEnterStroke);
      //
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////

      setToolTipText("Just to make getToolTiptext() to be called");


   }

   public void updateFromPreferences()
   {
      Settings.removeInitializer(BaseSettingsInitializer.NAME);
      Settings.addInitializer(new BaseSettingsInitializer(), Settings.CORE_LEVEL);
      /////////////////////////////////////////////////////////////////////////////////
      // There are a lot of goodies in the ExtSettingsInitializer
      // that might be interesting in th future.
      // Unfortunately some conflicts with some of Squirrels shortcuts
      // are in ExtSettingsInitializer
      //Settings.removeInitializer(ExtSettingsInitializer.NAME);
      //Settings.addInitializer(new ExtSettingsInitializer(), Settings.CORE_LEVEL);
      //
      /////////////////////////////////////////////////////////////////////////////
      Settings.removeInitializer(SQLSettingsInitializer.NAME);
      Settings.addInitializer(new SQLSettingsInitializer(SQLKit.class, _prefs));
   }


   public String getToolTipText(MouseEvent event)
   {
      int pos = viewToModel(event.getPoint());

      initParsing();

      for (int i = 0; i < _currentErrorInfos.length; i++)
      {
         if(_currentErrorInfos[i].beginPos-1 <= pos && pos <= _currentErrorInfos[i].endPos)
         {
            return _currentErrorInfos[i].message;
         }
      }

      return null;
   }


   private void initParsing()
   {
      if(false == _parsingInitialized && null != _session.getParserEventsProcessor())
      {
         _parsingInitialized = true;
         _session.getParserEventsProcessor().addParserEventsListener(new ParserEventsAdapter()
         {
            public void errorsFound(ErrorInfo[] errorInfos)
            {
               onErrorsFound(errorInfos);
            }
         });
      }
   }

   private void onErrorsFound(ErrorInfo[] errorInfos)
   {
      _currentErrorInfos = errorInfos;
   }

   public String getText()
   {
      return super.getText().replaceAll("\r\n", "\n");
   }

}
