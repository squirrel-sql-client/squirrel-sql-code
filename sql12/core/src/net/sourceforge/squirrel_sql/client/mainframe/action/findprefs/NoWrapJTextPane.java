package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.Color;
import java.awt.Dimension;

/**
 * See https://stackoverflow.com/questions/7156038/how-to-turn-off-jtextpane-line-wrapping
 */
public class NoWrapJTextPane extends JTextPane
{
   private final SimpleAttributeSet _matchAttributeSet;
   private final AttributeSet _defaultAttributeSet;
   private boolean _lineWrap;

   public NoWrapJTextPane()
   {
      final StyleContext defaultStyleContext = StyleContext.getDefaultStyleContext();
      new SimpleAttributeSet(defaultStyleContext.getEmptySet());

      _matchAttributeSet = new SimpleAttributeSet();
      StyleConstants.setBackground(_matchAttributeSet, Color.green);

      StyleContext sc = StyleContext.getDefaultStyleContext();
      _defaultAttributeSet = new SimpleAttributeSet();

   }

   @Override
   public boolean getScrollableTracksViewportWidth()
   {
      if(false == _lineWrap)
      {
         // Only track viewport width when the viewport is wider than the preferred width
         return getUI().getPreferredSize(this).width <= getParent().getSize().width;
      }
      else
      {
         return super.getScrollableTracksViewportWidth();
      }
   }
   @Override
   public Dimension getPreferredSize()
   {
      if(false == _lineWrap)
      {
         // Avoid substituting the minimum width for the preferred width when the viewport is too narrow
         return getUI().getPreferredSize(this);
      }
      else
      {
         return super.getPreferredSize();
      }
   }

   public void setLineWrap(boolean lineWrap)
   {
      _lineWrap = lineWrap;
      repaint();
   }


   public void appendToPane(String text, boolean match)
   {
      try
      {
         if(match)
         {
            getStyledDocument().insertString(getStyledDocument().getLength(), text, _matchAttributeSet);
         }
         else
         {
            getStyledDocument().insertString(getStyledDocument().getLength(), text, _defaultAttributeSet);
         }
      }
      catch (BadLocationException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}

