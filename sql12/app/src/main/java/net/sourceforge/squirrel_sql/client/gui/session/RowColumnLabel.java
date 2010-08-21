package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.border.Border;
import java.awt.*;

class RowColumnLabel extends JLabel
{
	private ISQLEntryPanel _sqlEntryPanel;
	private StringBuffer _msg = new StringBuffer();

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RowColumnLabel.class);
   private Dimension _dim;


   RowColumnLabel(ISQLEntryPanel sqlEntryPanel)
	{
		super(" ", JLabel.CENTER);

		_sqlEntryPanel = sqlEntryPanel;

		sqlEntryPanel.addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent e)
			{
				onCaretUpdate(e);
			}

		});

		writePosition(0,0, 0);

      setToolTipText(s_stringMgr.getString("RowColumnLabel.tooltip"));
	}

	private void onCaretUpdate(CaretEvent e)
	{
		int caretLineNumber = _sqlEntryPanel.getCaretLineNumber();
		int caretLinePosition = _sqlEntryPanel.getCaretLinePosition();
      int caretPosition = _sqlEntryPanel.getCaretPosition();

		writePosition(caretLineNumber, caretLinePosition, caretPosition);
	}

	private void writePosition(int caretLineNumber, int caretLinePosition, int caretPosition)
	{
		_msg.setLength(0);
		_msg.append(caretLineNumber + 1).append(",").append(caretLinePosition + 1).append(" / ").append(caretPosition + 1);
		setText(_msg.toString());
	}

	/**
	 * Return the preferred size of this component.
	 *
	 * @return	the preferred size of this component.
	 */
	public Dimension getPreferredSize()
	{
      if (null == _dim)
      {
         _dim = calcPrefSize();
      }
      return _dim;
	}

   private Dimension calcPrefSize()
   {
      Dimension dim = super.getPreferredSize();
      FontMetrics fm = getFontMetrics(getFont());
      dim.width = fm.stringWidth("000,000 / 00000000");
      Border border = getBorder();
      if (border != null)
      {
         Insets ins = border.getBorderInsets(this);
         if (ins != null)
         {
            dim.width += (ins.left + ins.right);
         }
      }
      Insets ins = getInsets();
      if (ins != null)
      {
         dim.width += (ins.left + ins.right);
      }
      return dim;
   }


}
