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

public class TextSelectAllAction extends BaseAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextSelectAllAction.class);
   private final TextActionHelper _helper;

   private JTextComponent _comp;

   TextSelectAllAction()
   {
      _helper = new TextActionHelper(this,
                                     s_stringMgr.getString("TextPopupMenu.selectAll"),
                                     KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK),
                                     DefaultEditorKit.selectAllAction);
   }

   public static KeyStroke getKeyStroke()
   {
      return TextActionHelper.getKeyStroke(DefaultEditorKit.selectAllAction, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
   }

   public void setComponent(JTextComponent comp)
   {
      _helper.initKeyStroke(_comp);
      _comp = comp;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if(_comp != null)
      {
         _comp.selectAll();
      }
   }
}
