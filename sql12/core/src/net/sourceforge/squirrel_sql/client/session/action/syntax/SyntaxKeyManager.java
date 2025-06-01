package net.sourceforge.squirrel_sql.client.session.action.syntax;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;



/**
 * Manages the ctrl left/right and ctrl shift left/right keys.
 */
public class SyntaxKeyManager
{

   private JTextComponent _textPane;


   public SyntaxKeyManager(JTextComponent textPane)
   {
      _textPane = textPane;

      // One may ask why we don't register the key strokes permanently.
      // When we did so with two internal frames open, the key stroke event
      // sometimes went to the wrong frame. This doesn't happen if we procede
      // like we do.
      // The question is, why?

      _textPane.addFocusListener(new FocusListener()
      {
         public void focusGained(FocusEvent e)
         {
            registerKeyStrokes();
         }

         public void focusLost(FocusEvent e)
         {
            unregisterKeyStrokes();
         }
      });


   }

   private void unregisterKeyStrokes()
   {
      KeyStroke keyStroke;

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_DOWN_MASK);
		_textPane.getKeymap().removeKeyStrokeBinding(keyStroke);
	}

   private void registerKeyStrokes()
   {
      KeyStroke keyStroke;
      Action act;

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRightCtrl();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRightCtrlShift();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onLeftCtrl();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onLeftCtrlShift();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, KeyEvent.CTRL_DOWN_MASK);
		act  = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				onCtrlBackSpace();
			}

		};
		_textPane.getKeymap().addActionForKeyStroke(keyStroke,act);
   }

	private void onCtrlBackSpace()
	{
		moveCtrlLeft(true);
		_textPane.replaceSelection("");
	}


	private void onLeftCtrlShift()
   {
      moveCtrlLeft(true);
   }

   private void onLeftCtrl()
   {
      moveCtrlLeft(false);
   }

   private void moveCtrlLeft(boolean select)
   {
      String text = _textPane.getText();
      int pos = _textPane.getCaretPosition() - 1;

      if(pos < 0 )
      {
         return;
      }


      pos = CtrlLeftRightStopUtil.getStopToTheLeftPos(pos, text);

      if(select)
      {
         _textPane.moveCaretPosition(pos);
      }
      else
      {
         _textPane.setCaretPosition(pos);
      }
   }

   private void onRightCtrlShift()
   {
      moveCtrlRight(true);
   }

   private void onRightCtrl()
   {
      moveCtrlRight(false);
   }

   private void moveCtrlRight(boolean select)
   {
      String text = _textPane.getText();
      int pos = _textPane.getCaretPosition() + 1;

      if(pos > text.length())
      {
         return;
      }


      pos = CtrlLeftRightStopUtil.getStopToTheRightPos(pos, text);

      if(select)
      {
         _textPane.moveCaretPosition(pos);
      }
      else
      {
         _textPane.setCaretPosition(pos);
      }
   }


}
