package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

/**
 * @author gwg
 *
 * Class that extends DefaultCellEditor by replacing the
 * default approach to creating the editable text with
 * using the renderer from the appropriate DataType class.
 */
public class CellEditorUsingRenderer extends DefaultCellEditor {

	// place to save reference to DataType object needed
	// for calling the appropriate renderer on the object value
	private IDataTypeComponent _dataTypeObject;
	
    /**
     * Constructs a <code>DefaultCellEditor</code> that uses a 
     * restorable text field and fills in the value using the same
     * renderer as is used to fill in the non-editable cell.
     *
     * @param x  a <code>JTextField</code> object
     */
    public CellEditorUsingRenderer(final JTextField textField,
    	IDataTypeComponent dataTypeObject) {
    	
    	super(textField);
    	
    	// save pointer to object needed to render value for use
    	// in the inner class delegate
    	_dataTypeObject = dataTypeObject;
    	
        editorComponent = textField;
		this.clickCountToStart = 2;
        delegate = new EditorDelegate() {
            public void setValue(Object value) {

				// If the cell is editable, we really must have a valid
				// DataType object, so the 'else' clause should never
				// be used.  It is just there as defensive programming.
				if (CellEditorUsingRenderer.this._dataTypeObject != null)
					textField.setText(_dataTypeObject.renderObject(value));
				else textField.setText((value != null) ? value.toString() : "<null>");
            }

	    	public Object getCellEditorValue() {
			return textField.getText();
			}
        };
		textField.addActionListener(delegate);
    }

}
