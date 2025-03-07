package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.PrioritizedCaretMouseListener;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.action.syntax.SyntaxKeyManager;
import net.sourceforge.squirrel_sql.client.session.action.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.action.SquirrelCopyAsRtfAction;
import net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.search.SquirrelRSyntaxSearchEngine;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintHandler;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rtextarea.RTextAreaUI;

import javax.swing.InputMap;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SquirrelRSyntaxTextArea extends RSyntaxTextArea
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SquirrelRSyntaxTextArea.class);

   private ISession _session;
   private SyntaxPreferences _prefs;
   private RSyntaxPropertiesWrapper _propertiesWrapper;
   private IIdentifier _sqlEntryPanelIdentifier;
   private RSyntaxHighlightTokenMatcherProxy _rSyntaxHighlightTokenMatcherProxy;
   private IUndoHandler _undoHandler;
   private SquirrelSyntaxScheme _squirrelSyntaxScheme;

   private List<ErrorInfo> _currentErrorInfos = new ArrayList<>();
   private boolean _parsingInitialized;
   private SquirrelRSyntaxSearchEngine _squirrelRSyntaxSearchEngine;
   private TextAreaPaintHandler _textAreaPaintHandler;

   public SquirrelRSyntaxTextArea(ISession session, SyntaxPreferences prefs, RSyntaxPropertiesWrapper propertiesWrapper, IIdentifier sqlEntryPanelIdentifier)
   {
      _session = session;
      _prefs = prefs;
      _propertiesWrapper = propertiesWrapper;
      _sqlEntryPanelIdentifier = sqlEntryPanelIdentifier;

      _rSyntaxHighlightTokenMatcherProxy.setDelegate(_propertiesWrapper.getSyntaxHighlightTokenMatcher(session, this, sqlEntryPanelIdentifier));

      SquirrelRSyntaxCaretWithPrioritizedMouseListener caret = new SquirrelRSyntaxCaretWithPrioritizedMouseListener();
      caret.setBlinkRate(getCaret().getBlinkRate());
      getCaret().deinstall(this);
      setCaret(caret);

      modifyKeystrokesFromPreferences(prefs);


      updateFromPreferences();


      new SyntaxKeyManager(this);

      _squirrelRSyntaxSearchEngine = new SquirrelRSyntaxSearchEngine(_session, this);

      setToolTipText("Just to make getToolTiptext() to be called");


      // DAR001
      Color bg = new Color(prefs.getWhiteSpaceStyle().getBackgroundRGB());
      setBackground(bg);

      if (SyntaxPreferences.NO_COLOR != prefs.getCaretColorRGB())
      {
         setCaretColor(new Color(prefs.getCaretColorRGB()));
      }

      setMarginLineEnabled(prefs.isTextLimitLineVisible());
      setMarginLinePosition(prefs.getTextLimitLineWidth());

      setHighlightCurrentLine(false);
      if(SyntaxPreferences.NO_COLOR != prefs.getCurrentLineHighlightColorRGB())
      {
         setHighlightCurrentLine(true);
         setCurrentLineHighlightColor(new Color(prefs.getCurrentLineHighlightColorRGB()));
      }

      setCurrentLineHighlightColor(new Color(prefs.getCurrentLineHighlightColorRGB()));

      if(null != System.getProperty("os.name") && System.getProperty("os.name").toUpperCase().startsWith("WINDOWS"))
      {
         if(null == System.getProperty("sun.java2d.noddraw") || false == "true".equals(System.getProperty("sun.java2d.noddraw")))
         {
            session.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("syntax.useNoDDrawOnWIn32"));
         }
      }

      _textAreaPaintHandler = new TextAreaPaintHandler(this, b -> onPauseInsertPairedCharacters(b), session);
      setInsertPairedCharacters(_prefs.isInsertPairedQuotes());
   }

   private void onPauseInsertPairedCharacters(boolean b)
   {
      if(b)
      {
         setInsertPairedCharacters(false);
      }
      else
      {
         setInsertPairedCharacters(_prefs.isInsertPairedQuotes());
      }
   }

   /**
    * Modifies the current {@link InputMap} of this component according to the actual preferences.
    * Within the preferences of the syntax plugin, the user can change the behavior of some key strokes. e.g. the copy action.
    * Because we have no access to the syntax preferences at the moment, the UI is created, we have to modify them after the creation. 
    * @param prefs Preferences to use.
    */
   private void modifyKeystrokesFromPreferences(SyntaxPreferences prefs)
   {
      InputMap shared = getInputMap();
      if (false == prefs.isUseCopyAsRtf())
      {
         return;
      }

      // Replace the default copy behavior for all key bindings
      KeyStroke[] allKeys = shared.allKeys();
      for (KeyStroke keyStroke : allKeys)
      {
         Object object = shared.get(keyStroke);
         if (DefaultEditorKit.copyAction.equals(object))
         {
            shared.put(keyStroke, Main.getApplication().getActionCollection().get(SquirrelCopyAsRtfAction.class));
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
      _squirrelSyntaxScheme.initStyles(_prefs, _session.getProperties().getFontInfo());
      new RSyntaxTextAreaEditorKit.IncreaseFontSizeAction().actionPerformedImpl(new ActionEvent(this, 1, "foo"), this);
      new RSyntaxTextAreaEditorKit.DecreaseFontSizeAction().actionPerformedImpl(new ActionEvent(this, 1, "bar"), this);
      repaint();
   }

   public void addSQLTokenListeners(SQLTokenListener tl)
   {
      _rSyntaxHighlightTokenMatcherProxy.addSQLTokenListener(tl);
   }

   public void removeSQLTokenListeners(SQLTokenListener tl)
   {
      _rSyntaxHighlightTokenMatcherProxy.removeSQLTokenListener(tl);
   }

   public String getToolTipText(MouseEvent event)
   {
      int pos = viewToModel2D(event.getPoint());

      initParsing();

      for (int i = 0; i < _currentErrorInfos.size(); i++)
      {
         if(_currentErrorInfos.get(i).beginPos-1 <= pos && pos <= _currentErrorInfos.get(i).endPos)
         {
            return _currentErrorInfos.get(i).message;
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
            @Override
            public void errorsFound(List<ErrorInfo> errorInfos)
            {
               onErrorsFound(errorInfos);
            }
         });
      }
   }

   private void onErrorsFound(List<ErrorInfo> errorInfos)
   {
      _currentErrorInfos = errorInfos;
   }

   public void showFindDialog()
   {
      _squirrelRSyntaxSearchEngine.find();
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
      _squirrelRSyntaxSearchEngine.replace();
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

   public void paint(Graphics g)
   {
      super.paint(g);

      _textAreaPaintHandler.onPaint(g);
   }

   @Override
   protected void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      _textAreaPaintHandler.onPaintComponent(g);
   }

   //   public void copyAsStyledText()
//   {
//      RtfFix.copyAsStyledText(this);
//   }

   public void addTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintHandler.addTextAreaPaintListener(textAreaPaintListener);
   }

   public void removeTextAreaPaintListener(TextAreaPaintListener textAreaPaintListener)
   {
      _textAreaPaintHandler.removeTextAreaPaintListener(textAreaPaintListener);
   }

   public TextAreaPaintHandler getTextAreaPaintHandler()
   {
      return _textAreaPaintHandler;
   }

   /**
    * This method is called, when text is pasted to the editor.
    * We replace non-breaking spaces by ordinary spaces because
    * non-breaking spaces keep reformatting from working.
    * See https://stackoverflow.com/questions/28295504/how-to-trim-no-break-space-in-java
    *
    * To reproduce the problem: Libre Office Writer allows to edit non-breaking spaces be ctrl+shift+space.
    */
   @Override
   public void replaceSelection(String text)
   {
      super.replaceSelection(StringUtilities.replaceNonBreakingSpacesBySpaces(text));
   }

   public void setPrioritizedCaretMouseListener(PrioritizedCaretMouseListener prioritizedCaretMouseListener)
   {
      ((SquirrelRSyntaxCaretWithPrioritizedMouseListener)getCaret()).setPrioritizedCaretMouseListener(prioritizedCaretMouseListener);
   }
}
