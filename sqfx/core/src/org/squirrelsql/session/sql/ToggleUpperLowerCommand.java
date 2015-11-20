package org.squirrelsql.session.sql;

public class ToggleUpperLowerCommand
{
   public ToggleUpperLowerCommand(SQLTextAreaServices sqlTextAreaServices)
   {

      String selectedText = sqlTextAreaServices.getTextArea().getSelectedText();

      if(0 == selectedText.length())
      {
         return;
      }

      String replacement = selectedText;

      for (int i = 0; i < selectedText.length(); i++)
      {
         char c = selectedText.charAt(i);

         if(Character.isLowerCase(c) && Character.toUpperCase(c) != c)
         {
            replacement = replacement.toUpperCase();
            break;
         }

         if(Character.isUpperCase(c) && Character.toLowerCase(c) != c)
         {
            replacement = replacement.toLowerCase();
            break;
         }

      }

      sqlTextAreaServices.replaceSelection(replacement, true);

   }
}
