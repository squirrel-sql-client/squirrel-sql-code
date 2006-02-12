package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

public class RowNumberTableColumn extends TableColumn
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RowNumberTableColumn.class);

	public static final int ROW_NUMBER_MODEL_INDEX = -42;

	// i18n[RowNumberTableColumn.rowNumber=Row Number]
	public static final String ROW_NUMBER_HEADER = s_stringMgr.getString("RowNumberTableColumn.rowNumber");


	public static final Object ROW_NUMBER_COL_IDENTIFIER = new Object();

	public RowNumberTableColumn()
	{
		super(ROW_NUMBER_MODEL_INDEX, 100);

		DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
		rend.setBackground(Color.lightGray);

		setCellRenderer(rend);

		setCellEditor(new DefaultCellEditor(new RestorableJTextField()));
		setHeaderValue(ROW_NUMBER_HEADER);

		setIdentifier(ROW_NUMBER_COL_IDENTIFIER);
	}

}
