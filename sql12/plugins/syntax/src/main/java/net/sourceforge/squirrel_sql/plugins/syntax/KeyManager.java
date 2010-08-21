package net.sourceforge.squirrel_sql.plugins.syntax;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;


/**
 * Manages the ctrl left/right and ctrl shift left/right keys.
 */
public class KeyManager
{
   public static final char[] STOP_AT = new char[]{'.', '(', ')' , '\'', '\n', ',', '=', '<', '>'};

   private JTextComponent _textPane;


   public KeyManager(JTextComponent textPane)
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

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK | Event.SHIFT_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK | Event.SHIFT_MASK);
      _textPane.getKeymap().removeKeyStrokeBinding(keyStroke);

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Event.CTRL_MASK);
		_textPane.getKeymap().removeKeyStrokeBinding(keyStroke);
	}

   private void registerKeyStrokes()
   {
      KeyStroke keyStroke;
      Action act;

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRightCtrl();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK | Event.SHIFT_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRightCtrlShift();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onLeftCtrl();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

      keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK | Event.SHIFT_MASK);
      act  = new AbstractAction()
      {
         public void actionPerformed(ActionEvent e)
         {
            onLeftCtrlShift();
         }
      };
      _textPane.getKeymap().addActionForKeyStroke(keyStroke,act);

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, Event.CTRL_MASK);
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


      for(; pos > 0; --pos)
      {
         if(isToStopAt(text.charAt(pos-1), text.charAt(pos)))
         {
            break;
         }
      }

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


      for(; pos < text.length(); ++pos)
      {
         if(isToStopAt(text.charAt(pos), text.charAt(pos-1)))
         {
            break;
         }
      }

      if(select)
      {
         _textPane.moveCaretPosition(pos);
      }
      else
      {
         _textPane.setCaretPosition(pos);
      }
   }

   private boolean isToStopAt(char toCheck, char former)
   {
      if(isInStopAtArray(former) || isInStopAtArray(toCheck))
      {
         return true;
      }
      else if(false == Character.isWhitespace(former) && Character.isWhitespace(toCheck)  ||
              Character.isWhitespace(former) && false == Character.isWhitespace(toCheck)  )
 //     else if(Character.isWhitespace(former) && false == Character.isWhitespace(toCheck))
      {
         return true;
      }

      return false;
   }

   private boolean isInStopAtArray(char toCheck)
   {
      for (int i = 0; i < STOP_AT.length; i++)
      {
         if(toCheck == STOP_AT[i])
         {
            return true;
         }
      }

      return false;
   }


}
