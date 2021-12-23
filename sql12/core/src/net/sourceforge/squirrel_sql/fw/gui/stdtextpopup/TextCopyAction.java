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

public class TextCopyAction extends BaseAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextCopyAction.class);

   private TextActionHelper _helper;
   private JTextComponent _comp;

   TextCopyAction()
   {
      _helper = new TextActionHelper(this,
                                     s_stringMgr.getString("TextPopupMenu.copy"),
                                     KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK),
                                     DefaultEditorKit.copyAction);
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
         TextActionUtil.wrapCopyActionToSelectLineOnEmptySelection(_comp, evt, () ->_comp.copy());
      }
   }

   public static KeyStroke getKeyStroke()
   {
      return TextActionHelper.getKeyStroke(DefaultEditorKit.copyAction, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
   }

}
