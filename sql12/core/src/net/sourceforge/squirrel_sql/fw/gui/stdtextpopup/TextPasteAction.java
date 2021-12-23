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

public class TextPasteAction extends BaseAction
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TextPasteAction.class);
   private final TextActionHelper _helper;

   private JTextComponent _comp;

   TextPasteAction()
   {
      _helper = new TextActionHelper(this,
                                     s_stringMgr.getString("TextPopupMenu.paste"),
                                     KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK),
                                     DefaultEditorKit.pasteAction);
   }

   public static KeyStroke getKeyStroke()
   {
      return TextActionHelper.getKeyStroke(DefaultEditorKit.pasteAction, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
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
         _comp.paste();
      }
   }
}
