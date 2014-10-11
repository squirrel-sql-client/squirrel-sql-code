package org.squirrelsql.workaround;

import org.fxmisc.richtext.CodeArea;

public class CodeAreaRepaintWA
{
   public static void avoidRepaintProblemsAfterTextModification(CodeArea sqlTextArea)
   {
      sqlTextArea.requestLayout();
   }
}
