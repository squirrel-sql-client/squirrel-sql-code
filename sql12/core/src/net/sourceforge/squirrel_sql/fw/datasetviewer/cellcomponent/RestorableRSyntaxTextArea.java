package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * @author gwg
 * <p>
 * JTextField that saves and restores the original value.
 * The original value is saved when the table does a setText()
 * at the start of editing.  During editing, the key handlers
 * may restore the original value or update the contents
 * without changing the original value.
 */
public class RestorableRSyntaxTextArea extends RSyntaxTextArea implements IRestorableTextComponent
{
   /*
    * The original value set in this cell by the table
    */
   private String _originalValue = null;


   public RestorableRSyntaxTextArea()
   {
      setHighlightCurrentLine(false);
   }

   /*
    * When the table initiates editing and sets this field, remember the value as the
    * original value of the field
    */
   public void setText(String originalValue)
   {
		if(originalValue == null)
		{
			_originalValue = StringUtilities.NULL_AS_STRING;
		}
		else
		{
			_originalValue = originalValue;
		}
      super.setText(_originalValue);
      setCaretPosition(0);
   }

   /*
    * Used by editing operations to set textField value without
    * changing the original text saved in the class
    */
   public void updateText(String newText)
   {
      super.setText(newText);
      setCaretPosition(0);
   }

   /*
    * Used by editing operations to reset the field to its original value.
    */
   public void restoreText()
   {
      super.setText(_originalValue);
      setCaretPosition(0);
   }
}
