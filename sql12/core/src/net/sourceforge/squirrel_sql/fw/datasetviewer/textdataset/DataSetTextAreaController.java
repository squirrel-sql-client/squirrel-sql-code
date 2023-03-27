package net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset;

import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

public class DataSetTextAreaController
{
	private DataSetTextArea _outText;
	private ResultAsText _resultAsText;

	public DataSetTextAreaController()
	{
      _outText = new DataSetTextArea();
   }

   public void init(ColumnDisplayDefinition[] colDefs, boolean showHeadings)
   {
		ResultAsTextLineCallback resultAsTextLineCallback = line -> _outText.append(line);

		_resultAsText = new ResultAsText(colDefs, showHeadings, isShowRowNumberInTextLayout(),resultAsTextLineCallback);
   }

	private boolean isShowRowNumberInTextLayout()
	{
		boolean showRowNumberInTextLayout = Main.getApplication().getSquirrelPreferences().getSessionProperties().getShowRowNumberInTextLayout();

		final ISession activeSession = Main.getApplication().getSessionManager().getActiveSession();
		if (null != activeSession)
		{
			showRowNumberInTextLayout = activeSession.getProperties().getShowRowNumberInTextLayout();
		}

		return showRowNumberInTextLayout;
	}

	public void clear()
	{
		_outText.setText("");
		if (null != _resultAsText)
		{
			_resultAsText.clear();
		}
	}

	public void addRow(Object[] row)
	{
		_resultAsText.addRow(row);
	}

	public void close()
	{
		_resultAsText.close();
	}

	
	public void moveToTop()
	{
		_outText.select(0, 0);
	}

   /**
	 * Get the component for this viewer.
	 *
	 * @return	The component for this viewer.
	 */
	public JComponent getComponent()
	{
		return _outText;
	}

	/*
	 * @see IDataSetViewer#getRowCount()
	 */
	public int getRowCount()
	{
		if(null == _resultAsText)
		{
			return 0;
		}
		return _resultAsText.getRowCount();
	}
}
