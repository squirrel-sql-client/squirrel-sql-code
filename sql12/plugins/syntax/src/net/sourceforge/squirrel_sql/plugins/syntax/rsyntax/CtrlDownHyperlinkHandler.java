package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.Main;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CtrlDownHyperlinkHandler
{
   private SquirrelRSyntaxTextArea _squirrelRSyntaxTextArea;
   private Timer _hyperlinkDelayTimer;
   private boolean _showTablesAsHyperlink;

   public CtrlDownHyperlinkHandler(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea)
   {
      if(false == Main.getApplication().getSquirrelPreferences().getSessionProperties().getAllowCtrlMouseClickJumpToObjectTree())
      {
         return;
      }


      _squirrelRSyntaxTextArea = squirrelRSyntaxTextArea;

      _hyperlinkDelayTimer = new Timer(200, e -> onDelayTimer());
      _hyperlinkDelayTimer.setRepeats(false);

      _squirrelRSyntaxTextArea.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e)
         {
            onKeyPressed(e);
         }

         @Override
         public void keyReleased(KeyEvent e)
         {
            onKeyReleased(e);
         }
      });

      _squirrelRSyntaxTextArea.addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(FocusEvent e)
         {
            onFocusLost();
         }
      });
   }

   private void onDelayTimer()
   {
      setShowTablesAsHyperlink(true);
   }

   private void setShowTablesAsHyperlink(boolean b)
   {
      boolean oldVal = _showTablesAsHyperlink;
      _showTablesAsHyperlink = b;

      if(oldVal != b)
      {
         RSyntaxUtil.forceHighlightUpdate(_squirrelRSyntaxTextArea);
      }
   }

   private void onFocusLost()
   {
      clearHyperlink();
   }

   private void clearHyperlink()
   {
      _hyperlinkDelayTimer.stop();
      SwingUtilities.invokeLater(() -> setShowTablesAsHyperlink(false));
   }

   private void onKeyPressed(KeyEvent e)
   {
      if(e.getExtendedKeyCode() == KeyEvent.VK_CONTROL || e.getExtendedKeyCode() == KeyEvent.VK_ALT || e.getExtendedKeyCode() == KeyEvent.VK_SHIFT)
      {
         if(   (0 == (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK))
               && (0 == (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK)))
         {
            _hyperlinkDelayTimer.start();
            return;
         }
      }

      clearHyperlink();
   }

   private void onKeyReleased(KeyEvent e)
   {
      if(e.getExtendedKeyCode() == KeyEvent.VK_CONTROL)
      {
         clearHyperlink();
      }
   }

   public boolean isShowTablesAsHyperlink()
   {
      return _showTablesAsHyperlink;
   }
}
