package net.sourceforge.squirrel_sql.fw.gui.stdtextpopup;

import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class TextEndLineAction extends BaseAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextEndLineAction.class);
   private final TextActionHelper _helper;

   private JTextComponent _comp;

   TextEndLineAction()
   {
      _helper = new TextActionHelper(this,
                                     s_stringMgr.getString("TextPopupMenu.gotoEndLine"),
                                     KeyStroke.getKeyStroke(KeyEvent.VK_END, 0),
                                     DefaultEditorKit.endLineAction);
   }

   public static KeyStroke getKeyStroke()
   {
      return TextActionHelper.getKeyStroke(DefaultEditorKit.endLineAction, KeyStroke.getKeyStroke(KeyEvent.VK_END,0));
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
