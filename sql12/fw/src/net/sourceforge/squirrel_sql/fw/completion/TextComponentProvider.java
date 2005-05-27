package net.sourceforge.squirrel_sql.fw.completion;

import javax.swing.text.JTextComponent;
import javax.swing.*;


public class TextComponentProvider
{
   private JTextComponent _txtEditor;
   private JTextComponent _txtFilter;

   public TextComponentProvider(JTextComponent txtEditor, boolean useOwnFilterTextField)
   {
      _txtEditor = txtEditor;

      if(useOwnFilterTextField)
      {
         _txtFilter = new JTextField();
         _txtFilter.setFont(_txtEditor.getFont());
      }
   }


   JTextComponent getEditor()
   {
      return _txtEditor;
   }

   JTextComponent getFilter()
   {
      if(null != _txtFilter)
      {
         return _txtFilter;
      }
      else
      {
         return _txtEditor;
      }
   }


   public boolean editorEqualsFilter()
   {
      return null == _txtFilter;
   }
}
