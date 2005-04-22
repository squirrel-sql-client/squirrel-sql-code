package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.KeyManager;
import net.sourceforge.squirrel_sql.plugins.syntax.KeyManager;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.text.Document;
import javax.swing.text.Keymap;

import org.netbeans.editor.*;
import org.netbeans.editor.ext.ExtSettingsInitializer;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;


public class NetbeansSQLEditorPane extends JEditorPane
{
   private boolean _parsingInitialized;
   private ISession _session;
   private ErrorInfo[] _currentErrorInfos = new ErrorInfo[0];
   private SyntaxPreferences _prefs;
   private SyntaxFactory _syntaxFactory;
   private SyntaxPugin _plugin;

   private IIdentifier _sqlEntryPanelIdentifier;

   public NetbeansSQLEditorPane(ISession session, SyntaxPreferences prefs, SyntaxFactory syntaxFactory, SyntaxPugin plugin, IIdentifier sqlEntryPanelIdentifier)
   {
      _session = session;
      _prefs = prefs;
      _syntaxFactory = syntaxFactory;
      _plugin = plugin;
      _sqlEntryPanelIdentifier = sqlEntryPanelIdentifier;

      _syntaxFactory.putEditorPane(_session, this);

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
      Settings.addInitializer(new SQLSettingsInitializer(SQLKit.class, _prefs, _plugin));


      //////////////////////////////////////////////////////////////////////////////////////////
      //Needs to be done at this moment. That's why we can't call updateFromPreferences() here.
      setEditorKit(new SQLKit(syntaxFactory));
      //
      //////////////////////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // The ctrl enter short cut in the Netbeans editor is set in org.netbeans.editor.ext.BaseKit
      // to the org.netbeans.editor.ext.BaseKit.SplitLineAction.
      // Since the ctrl enter shorcut is a basic SQuirreL short cut and is defined via the main menu action
      // we must remove this binding here.
      KeyStroke ctrlEnterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK);
      getKeymap().removeKeyStrokeBinding(ctrlEnterStroke);
      //
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////

      Document doc = getDocument();
      _syntaxFactory.putDocument(_session, doc);


      setToolTipText("Just to make getToolTiptext() to be called");

      new KeyManager(this);
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
      Settings.addInitializer(new SQLSettingsInitializer(SQLKit.class, _prefs, _plugin));


      //////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // The ctrl enter short cut in the Netbeans editor is set in org.netbeans.editor.ext.BaseKit
      // to the org.netbeans.editor.ext.BaseKit.SplitLineAction.
      // Since the ctrl enter shorcut is a basic SQuirreL short cut and is defined via the main menu action
      // we must remove this binding here.
      KeyStroke ctrlEnterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK);
      getKeymap().removeKeyStrokeBinding(ctrlEnterStroke);
      //
      ///////////////////////////////////////////////////////////////////////////////////////////////////////////

      Document doc = getDocument();
      _syntaxFactory.putDocument(_session, doc);

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
      if(false == _parsingInitialized && null != _session.getParserEventsProcessor(_sqlEntryPanelIdentifier))
      {
         _parsingInitialized = true;
         _session.getParserEventsProcessor(_sqlEntryPanelIdentifier).addParserEventsListener(new ParserEventsAdapter()
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

   public void setUndoManager(UndoableEditListener um)
   {
      getDocument().addUndoableEditListener(um);
      getDocument().putProperty( BaseDocument.UNDO_MANAGER_PROP, um );
   }

   public IIdentifier getSqlEntryPanelIdentifier()
   {
      return _sqlEntryPanelIdentifier;
   }

}
