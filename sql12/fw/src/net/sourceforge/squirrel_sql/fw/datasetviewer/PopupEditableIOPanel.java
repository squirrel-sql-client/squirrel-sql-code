package net.sourceforge.squirrel_sql.fw.datasetviewer;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JButton;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;


/**
 * @author gwg
 *
 * Class to handle IO between user and editable text, text and object,
 * and text/object to/from file.
 */
public class PopupEditableIOPanel extends JPanel {
	
	// The text area displaying the object contents
	private final JTextArea _ta;
	
	// Description needed to handle conversion of data to/from Object
	private final ColumnDisplayDefinition _colDef;
	
	private MouseAdapter _lis;
	
	private final TextPopupMenu _popupMenu;

//private final JScrollPane sp;

	/**
	 * Constructor
	 */
	public PopupEditableIOPanel(ColumnDisplayDefinition colDef,
		Object value, boolean isEditable) {
		
		_popupMenu = new TextPopupMenu();
		
		_colDef = colDef;
		_ta = CellComponentFactory.getJTextArea(colDef, value);
		
		if (isEditable) {
			_ta.setEditable(true);
			_ta.setBackground(Color.YELLOW);	// tell user it is editable
		}
		else {
			_ta.setEditable(false);
		}

		
		_ta.setLineWrap(true);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(_ta), BorderLayout.CENTER);


		
		_popupMenu.add(new LineWrapAction());
		_popupMenu.add(new WordWrapAction());
		_popupMenu.setTextComponent(_ta);
		
	}
	
	
	/**
	 * Return the contents of the editable text area as an Object
	 * converted by the correct DataType function.  Errors are reported
	 * through the messageBuffer.
	 */
	public Object getObject(StringBuffer messageBuffer) {
		return CellComponentFactory.validateAndConvertInPopup(_colDef,
					_ta.getText(), messageBuffer);
	}
	
	/**
	 * When focus is passed to this panel, automatically pass it
	 * on to the text area.
	 */
	public void requestFocus() {
		_ta.requestFocus();
	}
	
	public void addNotify()
		{
			super.addNotify();
			if (_lis == null)
			{
				_lis = new MouseAdapter()
				{
					public void mousePressed(MouseEvent evt)
					{
						if (evt.isPopupTrigger())
						{
							_popupMenu.show(evt);
						}
					}
					public void mouseReleased(MouseEvent evt)
					{
							if (evt.isPopupTrigger())
						{
							_popupMenu.show(evt);
						}
					}
				};
				_ta.addMouseListener(_lis);
			}
		}

		public void removeNotify()
		{
			super.removeNotify();
			if (_lis != null)
			{
				_ta.removeMouseListener(_lis);
				_lis = null;
			}
		}

		private class LineWrapAction extends BaseAction
		{
			LineWrapAction()
			{
				super("Wrap Lines on/off");
			}

			public void actionPerformed(ActionEvent evt)
			{
				if (_ta != null)
				{
					_ta.setLineWrap(!_ta.getLineWrap());
				}
			}
		}

		private class WordWrapAction extends BaseAction
		{
			WordWrapAction()
			{
				super("Wrap on Word on/off");
			}

			public void actionPerformed(ActionEvent evt)
			{
				if (_ta != null)
				{
					_ta.setWrapStyleWord(!_ta.getWrapStyleWord());
				}
			}
		}

}
