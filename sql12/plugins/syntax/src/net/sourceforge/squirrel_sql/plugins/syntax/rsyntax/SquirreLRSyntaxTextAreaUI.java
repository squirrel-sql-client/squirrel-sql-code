package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.SQLEntryPanelUtil;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;
import net.sourceforge.squirrel_sql.fw.gui.stdtextpopup.TextActionUtil;
import net.sourceforge.squirrel_sql.fw.gui.stdtextpopup.TextCopyAction;
import net.sourceforge.squirrel_sql.fw.gui.stdtextpopup.TextCutAction;
import net.sourceforge.squirrel_sql.fw.gui.stdtextpopup.TextPasteAction;
import net.sourceforge.squirrel_sql.fw.gui.stdtextpopup.TextSelectAllAction;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaDefaultInputMap;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextAreaEditorKit;

import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SquirreLRSyntaxTextAreaUI extends RSyntaxTextAreaUI
{
   private static final KeyStroke RS_KEY_STROKE_TO_UPPER_CASE = KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
   private static final KeyStroke RS_KEY_STROKE_TO_LOWER_CASE = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);

   private static final KeyStroke RS_KEY_STROKE_LINE_UP = KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
   private static final KeyStroke RS_KEY_STROKE_LINE_DOWN = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);

   private static final EditorKit _squirrel_defaultKit =
      new RSyntaxTextAreaEditorKit()
      {
         @Override
         public Action[] getActions()
         {
            return TextAction.augmentList(super.getActions(),
                                          new Action[]
                                                {
                                                      new SQuirrelSelectWordAction(),
                                                      new SQuirrelCopyAction(),
                                                      new SQuirrelCutAction()
                                                });
         }
      };


   private SquirrelRSyntaxTextArea _squirrelRSyntaxTextArea;

   public SquirreLRSyntaxTextAreaUI(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      super(squirrelRSyntaxTextArea);
      _squirrelRSyntaxTextArea = squirrelRSyntaxTextArea;
   }

   protected InputMap getRTextAreaInputMap()
   {
      // Except from modifiyKeystrokes() this is copied from RSyntaxTextAreaUI.
      // Not too nice

      InputMap map = new InputMapUIResource();
      InputMap shared = (InputMap)UIManager.get("RSyntaxTextAreaUI.inputMap");

      if (shared == null)
      {
         shared = new RSyntaxTextAreaDefaultInputMap();
         modifyKeystrokes(shared);
         UIManager.put("RSyntaxTextAreaUI.inputMap", shared);
      }

      //KeyStroke[] keys = shared.allKeys();
      //for (int i=0; i<keys.length; i++)
      //	System.err.println(keys[i] + " -> " + shared.get(keys[i]));

      map.setParent(shared);
      return map;
   }

   /**
    * For information on RSyntax's keystrokes see {@link org.fife.ui.rtextarea.RTADefaultInputMap}
    */
   private void modifyKeystrokes(InputMap sharedIM)
   {
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK));

      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK)); // ChangeTrackAction

      // Removes RTextAreaEditorKit.rtaDeleteRestOfLineAction in favour of DeleteCurrentLineAction.
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK));

      /*
       *       Remove this Keystroke, because it triggers auto-complete of the current word with a matching most recent word,
       *       as long the shift and space key are pressed.
       *       See RTADefaultInputMap()
       *       put(KeyStroke.getKeyStroke(' '), RTextAreaEditorKit.rtaDumbCompleteWordAction); 
       */
      sharedIM.remove(KeyStroke.getKeyStroke(' '));

      sharedIM.put(getToUpperCaseKeyStroke(), RTextAreaEditorKit.rtaUpperSelectionCaseAction);
      sharedIM.put(getToLowerCaseKeyStroke(), RTextAreaEditorKit.rtaLowerSelectionCaseAction);

      sharedIM.put(getLineUpKeyStroke(), RTextAreaEditorKit.rtaLineUpAction);
      sharedIM.put(getLineDownKeyStroke(), RTextAreaEditorKit.rtaLineDownAction);

      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
      // This enables users to redefine to the default ctrl+DELETE for DeleteCurrentLineAction by the popular alternative ctrl+Y
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));


      // These standard Actions need to be removed and put again
      // because sharedIM is able to store more than one action
      // Note: All RTextAreaEditorKit....Action attributes are statically inherited from DefaultEditorKit.
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
      sharedIM.put(TextCopyAction.getKeyStroke(), RTextAreaEditorKit.copyAction);
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
      sharedIM.put(TextCutAction.getKeyStroke(), RTextAreaEditorKit.cutAction);
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
      sharedIM.put(TextPasteAction.getKeyStroke(), RTextAreaEditorKit.pasteAction);
      sharedIM.remove(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
      sharedIM.put(TextSelectAllAction.getKeyStroke(), RTextAreaEditorKit.selectAllAction);
   }

   public IUndoHandler createUndoHandler()
   {
      return new IUndoHandler()
      {
         @Override
         public Action getUndoAction()
         {
            return onGetUndoAction();
         }

         @Override
         public Action getRedoAction()
         {
            return onGetRedoAction();
         }
      };
   }

   private Action onGetUndoAction()
   {
      return getActionForName(_squirrelRSyntaxTextArea, RTextAreaEditorKit.rtaUndoAction);
   }

   private Action onGetRedoAction()
   {
      return getActionForName(_squirrelRSyntaxTextArea, RTextAreaEditorKit.rtaRedoAction);
   }

   public static Action getActionForName(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea, String actionName)
   {
      Action[] actions = squirrelRSyntaxTextArea.getUI().getEditorKit(squirrelRSyntaxTextArea).getActions();
      for (Action action : actions)
      {
         if(actionName.equals(action.getValue(Action.NAME)))
         {
            return action;
         }
      }
      throw new IllegalStateException("Action " + actionName + "not found");
   }

   public static KeyStroke getToUpperCaseKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaUpperSelectionCaseAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_TO_UPPER_CASE));
   }

   public static KeyStroke getToLowerCaseKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaLowerSelectionCaseAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_TO_LOWER_CASE));
   }

   public static KeyStroke getLineUpKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaLineUpAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_LINE_UP));
   }

   public static KeyStroke getLineDownKeyStroke()
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(RTextAreaEditorKit.rtaLineDownAction, SquirreLRSyntaxTextAreaUI.RS_KEY_STROKE_LINE_DOWN));
   }

   @Override
   public EditorKit getEditorKit(JTextComponent tc)
   {
      return _squirrel_defaultKit;
   }



   private static class SQuirrelSelectWordAction extends RSyntaxTextAreaEditorKit.SelectWordAction
   {
      public void actionPerformedImpl(ActionEvent e, RTextArea textArea)
      {
         int[] wordBoundsAtCursor = SQLEntryPanelUtil.getWordBoundsAtCursor(textArea, false);
         textArea.setSelectionStart(wordBoundsAtCursor[0]);
         textArea.setSelectionEnd(wordBoundsAtCursor[1]);
      }
   }

   private static class SQuirrelCopyAction extends RSyntaxTextAreaEditorKit.CopyAction
   {
      public void actionPerformedImpl(ActionEvent e, RTextArea textArea)
      {
         TextActionUtil.wrapCopyActionToSelectLineOnEmptySelection(textArea, e, () -> SQuirrelCopyAction.super.actionPerformedImpl(e, textArea));
      }
   }

   private static class SQuirrelCutAction extends RSyntaxTextAreaEditorKit.CutAction
   {
      public void actionPerformedImpl(ActionEvent e, RTextArea textArea)
      {
         TextActionUtil.wrapCutActionToSelectLineOnEmptySelection(textArea, e, () -> SQuirrelCutAction.super.actionPerformedImpl(e, textArea));
      }
   }
}
