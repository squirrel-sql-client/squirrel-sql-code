package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.syntax.KeyManager;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search.SquirrelRSyntaxSearchEngine;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rtextarea.RTextAreaUI;

import javax.swing.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class SquirrelRSyntaxTextArea extends RSyntaxTextArea
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SquirrelRSyntaxTextArea.class);



   private ISession _session;
   private SyntaxPreferences _prefs;
   private RSyntaxPropertiesWrapper _propertiesWrapper;
   private IIdentifier _sqlEntryPanelIdentifier;
   private RSyntaxHighlightTokenMatcherProxy _rSyntaxHighlightTokenMatcherProxy;
   private IUndoHandler _undoHandler;
   private SquirrelSyntaxScheme _squirrelSyntaxScheme;

   private ErrorInfo[] _currentErrorInfos = new ErrorInfo[0];
   private boolean _parsingInitialized;
   private SquirrelRSyntaxSearchEngine _squirrelRSyntaxSearchEngine;


   public SquirrelRSyntaxTextArea(ISession session, SyntaxPreferences prefs, RSyntaxPropertiesWrapper propertiesWrapper, IIdentifier sqlEntryPanelIdentifier)
   {
      _session = session;
      _prefs = prefs;
      _propertiesWrapper = propertiesWrapper;
      _sqlEntryPanelIdentifier = sqlEntryPanelIdentifier;

      _rSyntaxHighlightTokenMatcherProxy.setDelegate(_propertiesWrapper.getSyntaxHighlightTokenMatcher(session, this, sqlEntryPanelIdentifier));

      updateFromPreferences();

      new KeyManager(this);

      _squirrelRSyntaxSearchEngine = new SquirrelRSyntaxSearchEngine(_session, this);

      setToolTipText("Just to make getToolTiptext() to be called");

      setMarginLineEnabled(prefs.isTextLimitLineVisible());
      setMarginLinePosition(prefs.getTextLimitLineWidth());
      setHighlightCurrentLine(prefs.isHighlightCurrentLine());

      if(null != System.getProperty("os.name") && System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
      {
         if(null == System.getProperty("sun.java2d.noddraw") || false == "true".equals(System.getProperty("sun.java2d.noddraw")))
         {
            session.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("syntax.useNoDDrawOnWIn32"));            
         }
      }
   }


   protected RTextAreaUI createRTextAreaUI()
   {
      // Will be called from the super class constructor
      SquirreLRSyntaxTextAreaUI ret = new SquirreLRSyntaxTextAreaUI(this);
      _undoHandler = ret.createUndoHandler();
      return ret;
   }


   @Override
   protected JPopupMenu createPopupMenu()
   {
      // SQuirreL creates its own popup menu.
      return null;
   }


   protected Document createDefaultModel()
   {
      // Is called from the super class constructor.
      // That is why initialization takes place here.
      _rSyntaxHighlightTokenMatcherProxy = new RSyntaxHighlightTokenMatcherProxy();
      RSyntaxDocument ret = new RSyntaxDocument(new SquirrelTokenMarkerFactory(this, _rSyntaxHighlightTokenMatcherProxy), SYNTAX_STYLE_SQL);
      return ret;
   }

   public SyntaxScheme getDefaultSyntaxScheme()
   {
      // Is called from the super class constructor.
      // That is why initialization takes place here.
      _squirrelSyntaxScheme = new SquirrelSyntaxScheme();
      return _squirrelSyntaxScheme;
   }

   public IUndoHandler createUndoHandler()
   {
      return _undoHandler;
   }

   public void addUndoableEditListener(UndoableEditListener um)
   {
      getDocument().addUndoableEditListener(um);
   }

   public void updateFromPreferences()
   {
      setFont(_session.getProperties().getFontInfo().createFont());
      _squirrelSyntaxScheme.initSytles(_prefs, _session.getProperties().getFontInfo());
      new RSyntaxTextAreaEditorKit.IncreaseFontSizeAction().actionPerformedImpl(new ActionEvent(this, 1, "foo"), this);
      new RSyntaxTextAreaEditorKit.DecreaseFontSizeAction().actionPerformedImpl(new ActionEvent(this, 1, "bar"), this);
      repaint();
   }

   public void addSQLTokenListeners(ISession session, SQLTokenListener tl)
   {
      _rSyntaxHighlightTokenMatcherProxy.addSQLTokenListener(tl);
   }

   public void removeSQLTokenListeners(ISession session, SQLTokenListener tl)
   {
      _rSyntaxHighlightTokenMatcherProxy.removeSQLTokenListener(tl);
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
      IParserEventsProcessor parserEventsProcessor = _propertiesWrapper.getParserEventsProcessor(_sqlEntryPanelIdentifier, _session);
      if(false == _parsingInitialized && null != parserEventsProcessor)
      {
         _parsingInitialized = true;
         parserEventsProcessor.addParserEventsListener(new ParserEventsAdapter()
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

   public void showFindDialog(ActionEvent evt)
   {
      _squirrelRSyntaxSearchEngine.find(evt);
   }

   public void findSelected(ActionEvent evt)
   {
      _squirrelRSyntaxSearchEngine.findSelected(evt);
   }

   public void repeatLastFind(ActionEvent evt)
   {
      _squirrelRSyntaxSearchEngine.repeatLastFind(evt);
   }

   public void markSelected(ActionEvent evt)
   {
      _squirrelRSyntaxSearchEngine.markSelected(evt);
   }

   public void unmarkAll()
   {
      _squirrelRSyntaxSearchEngine.unmarkAll();
   }

   public void showReplaceDialog(ActionEvent evt)
   {
      _squirrelRSyntaxSearchEngine.replace(evt);
   }

   public void showGoToLineDialog(ActionEvent evt)
   {
      _squirrelRSyntaxSearchEngine.goToLine();
   }

   public void sessionEnding()
   {
      ////////////////////////////////////////////
      // Better GCing
      getCaret().deinstall(this);
      if(getCaret() instanceof DefaultCaret)
      {
         ChangeListener[] changeListeners = ((DefaultCaret) getCaret()).getChangeListeners();

         for (ChangeListener changeListener : changeListeners)
         {
            getCaret().removeChangeListener(changeListener);
         }
      }
      //
      ////////////////////////////////////////////
   }
}
