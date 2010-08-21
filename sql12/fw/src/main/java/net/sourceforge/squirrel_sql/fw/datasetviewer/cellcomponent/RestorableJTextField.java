package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.JTextField;

/**
 * @author gwg
 *
 * JTextField that saves and restores the original value.
 * The original value is saved when the table does a setText()
 * at the start of editing.  During editing, the key handlers
 * may restore the original value or update the contents
 * without changing the original value.
 * 
 */
public class RestorableJTextField extends JTextField 
	implements IRestorableTextComponent
{

	/*
	 * The original value set in this cell by the table
	 */
	 private String _originalValue = null;
	 

	/*
	 * When the table initiates editing and sets this field, remember the value as the
	 * original value of the field
	 */
	public void setText(String originalValue) {
		if (originalValue == null)
			_originalValue = "<null>";
		else _originalValue = originalValue;
		super.setText(_originalValue);
	}

	/*
	 * Used by editing operations to set textField value without
	 * changing the original text saved in the class
	 */
	public void updateText(String newText) {
		super.setText(newText);
	}
	
	/*
	 * Used by editing operations to reset the field to its original value.
	 */
	 public void restoreText() {
	 	super.setText(_originalValue);
	 }
}
