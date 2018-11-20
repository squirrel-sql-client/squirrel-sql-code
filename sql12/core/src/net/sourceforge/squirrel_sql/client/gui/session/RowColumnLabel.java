package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.border.Border;
import java.awt.*;

class RowColumnLabel extends JLabel
{
	private RowColumnLabelSQLEntryPanelHandler _rowColumnLabelSqlEntryPanelHandler;
	private StringBuffer _msg = new StringBuffer();

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RowColumnLabel.class);
   private Dimension _dim;


   RowColumnLabel(ISQLEntryPanel sqlEntryPanel)
   {
      super(" ", JLabel.CENTER);
      init(new RowColumnLabelSQLEntryPanelHandler(sqlEntryPanel, e -> onCaretUpdate(e)));
   }

   RowColumnLabel(MainPanel mainPanel)
   {
      super(" ", JLabel.CENTER);
      init(new RowColumnLabelSQLEntryPanelHandler(mainPanel, e -> onCaretUpdate(e)));
   }

   private void init(RowColumnLabelSQLEntryPanelHandler rowColumnLabelSqlEntryPanelHandler)
   {
      _rowColumnLabelSqlEntryPanelHandler = rowColumnLabelSqlEntryPanelHandler;

      writePosition(0,0, 0);

      setToolTipText(s_stringMgr.getString("RowColumnLabel.tooltip"));
   }

   private void onCaretUpdate(CaretEvent e)
	{
		int caretLineNumber = _rowColumnLabelSqlEntryPanelHandler.getCaretLineNumber();
		int caretLinePosition = _rowColumnLabelSqlEntryPanelHandler.getCaretLinePosition();
      int caretPosition = _rowColumnLabelSqlEntryPanelHandler.getCaretPosition();

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
