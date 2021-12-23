package net.sourceforge.squirrel_sql.fw.gui.stdtextpopup;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class TextCutAction extends BaseAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextCutAction.class);
   private final TextActionHelper _helper;

   private JTextComponent _comp;

   TextCutAction()
   {
      _helper = new TextActionHelper(this,
                                     s_stringMgr.getString("TextPopupMenu.cut"),
                                     KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK),
                                     DefaultEditorKit.cutAction);
   }

   public static KeyStroke getKeyStroke()
   {
      return TextActionHelper.getKeyStroke(DefaultEditorKit.cutAction, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
   }

   public void setComponent(JTextComponent comp)
   {
      _comp = comp;
      _helper.initKeyStroke(_comp);
   }

   public void actionPerformed(ActionEvent evt)
   {
      if(_comp != null)
      {
         TextActionUtil.wrapCutActionToSelectLineOnEmptySelection(_comp, evt, () ->_comp.cut());
      }
   }
}
