package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;


/**
 * @author gwg
 *
 * This interface allows sections of the DataType objects to operate on
 * both RestorableJTextField and RestorableJTextArea components.
 * 
 * In those components,the original contents of the cell is saved whenever
 * the setText() method is called.  The original text is restored to the
 * component by calling restoreText(), and the contents of the cell is
 * updated without changing the original text by calling updateText().
 * 
 * This is the only way that I could find to handle saving/restoring the
 * initial data in the cell.  The problem is that the CellEditor component is created
 * without any data, then the same component is reused for every cell
 * in the column.
 */
public interface IRestorableTextComponent {
	
	/*
	 * Restore the contents to the original value.
	 */
	 public void restoreText();

	/*
	 * Set the contents of the component without resetting the
	 * original value.
	 */
	 public void updateText(String text);
}
