package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;

public class ShowRowNumbersCommand
{
	private DataSetViewerTablePanel _viewer;
	private boolean _showRowNumbers;

	public ShowRowNumbersCommand(DataSetViewerTablePanel table, boolean showRowNumbers)
	{
		_viewer = table;
		_showRowNumbers = showRowNumbers;
	}

	public void execute()
	{
		_viewer.setShowRowNumbers(_showRowNumbers);
	}
}
