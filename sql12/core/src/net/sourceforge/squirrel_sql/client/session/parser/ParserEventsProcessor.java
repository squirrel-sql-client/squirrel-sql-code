package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserThread;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParsingFinishedListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAndAliasParseResult;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ParserEventsProcessor implements IParserEventsProcessor
{
	private Timer _parserTimer;
	private ParserThread _parserThread;
	private ArrayList<ParserEventsListener> _listeners = new ArrayList<>();
	private ISession _session;
   private ISQLPanelAPI _sqlPanelApi;
	private KeyAdapter _triggerParserKeyListener;
   private boolean _processingEnded;
	private long _timerLastRestarted = 0;

	public ParserEventsProcessor(ISQLPanelAPI sqlPanelApi, ISession session)
   {
      _session = session;
      _sqlPanelApi = sqlPanelApi;


		_triggerParserKeyListener = new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            onKeyTyped(e);
         }
      };


      _parserTimer = new Timer(250, e -> onTimerStart());

		// Repeats is set to true to make sure the parser gets correctly initialized.
		// once all loading is done.
		// After that it is set back to false and keyboard events only will trigger the parser.
		// For details see method onTimerStart().
      _parserTimer.setRepeats(true);

      _parserTimer.start();
   }


   private void onParserExitedOnException(final Throwable e)
	{
		SwingUtilities.invokeLater(() -> {throw Utilities.wrapRuntime(e);});
	}

	public void addParserEventsListener(ParserEventsListener l)
	{
		if (_listeners != null && l != null)
		{
			_listeners.remove(l);
			_listeners.add(l);
		}
	}

	public void removeParserEventsListener(ParserEventsListener l)
	{
		if (_listeners != null && l != null)
		{
			_listeners.remove(l);
		}
	}

	public void endProcessing()
	{
      _processingEnded = true;

      _sqlPanelApi.getSQLEntryPanel().getTextComponent().removeKeyListener(_triggerParserKeyListener);

		if (_parserTimer != null)
		{
			_parserTimer.stop();
		}

		if (_parserThread != null)
		{
			_parserThread.exitThread();
		}


		_session = null;
		_sqlPanelApi = null;
		_listeners = null;


	}

	public void triggerParser()
   {
		long currentTimeMillis = System.currentTimeMillis();
		// Without this if and quick typing the _parserTimer only gets restarted but never fires.
		if(currentTimeMillis - _timerLastRestarted > _parserTimer.getDelay() + 10)
		{
			_timerLastRestarted = currentTimeMillis;
			_parserTimer.restart();
		}
   }

	private void onParsingFinished()
	{
		SwingUtilities.invokeLater(() -> fireParsingFinished());
	}

	private void fireParsingFinished()
	{
      if(_processingEnded)
      {
         return;
      }

		TableAndAliasParseResult tableAndAliasParseResult = _parserThread.getTableAndAliasParseResult();
		List<ErrorInfo> errorInfos = _parserThread.getErrorInfos();

		for(ParserEventsListener parserEventsListener : _listeners.toArray(new ParserEventsListener[0]))
		{
			parserEventsListener.tableAndAliasParseResultFound(tableAndAliasParseResult);
			parserEventsListener.errorsFound(errorInfos);
		}
	}


	private void onTimerStart()
	{
		if(null == _sqlPanelApi.getSQLEntryPanel() || null == _session.getSchemaInfo() || false == _session.getSchemaInfo().isLoaded())
		{
			// Entry panel or schema info not yet available, try again next time.
			//System.out.println("ParserEventsProcessor.onTimerStart entry panel not yet set");
			return;
		}

		// Now that initialization is done, it is enough to trigger the parser by key events.
		// That is why we set Repeats back to false.
		// See also comment in constructor.
		_parserTimer.setRepeats(false);

		initParserThread();
		_parserThread.parseInBackground(_sqlPanelApi.getSQLEntryPanel().getText());
	}

	private void initParserThread()
	{
 		if(null != _parserThread)
		{
			return;
		}

		_parserThread = new ParserThread(_session);

		_sqlPanelApi.getSQLEntryPanel().getTextComponent().addKeyListener(_triggerParserKeyListener);

		_parserThread.setParsingFinishedListener(new ParsingFinishedListener()
		{
			public void parsingFinished()
			{
				onParsingFinished();
			}

			public void parserExitedOnException(Throwable e)
			{
				onParserExitedOnException(e);
			}
		});
	}

   private void onKeyTyped(KeyEvent e)
   {
      if(false == e.isActionKey())
      {
         _parserTimer.restart();
      }
   }



}
