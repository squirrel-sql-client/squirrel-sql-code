package net.sourceforge.squirrel_sql.client.session.parser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParserThread;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ParsingFinishedListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.TableAliasInfo;
import net.sourceforge.squirrel_sql.fw.util.BaseRuntimeException;

public class ParserEventsProcessor implements IParserEventsProcessor
{
	private Timer _parserTimer;
	private ParserThread _parserThread;
	private Vector<ParserEventsListener> _listeners = 
	    new Vector<ParserEventsListener>();
	private ISession _session;
   private ISQLPanelAPI _sqlPanelApi;
	private KeyAdapter _triggerParserKeyListener;
   private boolean _processingEnded;

   public ParserEventsProcessor(ISQLPanelAPI sqlPanelApi, ISession session)
   {
      _session = session;
      _sqlPanelApi = sqlPanelApi;

      ActionListener al = new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onTimerStart();
         }
      };


      _triggerParserKeyListener = new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            onKeyTyped(e);
         }
      };


      _parserTimer = new Timer(500, al);
      _parserTimer.start();
   }


   private void onParserExitedOnException(final Throwable e)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				throw new BaseRuntimeException(e);
			}
		});

	}

	public void addParserEventsListener(ParserEventsListener l) {
        if (_listeners != null && l != null) {
            _listeners.add(l);
        }
    }
    
	public void removeParserEventsListener(ParserEventsListener l) {
        if (_listeners != null && l != null) {
            _listeners.add(l);
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
      _parserTimer.restart();
   }

	private void onParsingFinished()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				fireParsingFinished();
			}
		});
	}

	private void fireParsingFinished()
	{
      if(_processingEnded)
      {
         return;
      }

      ParserEventsListener[] clone = 
          _listeners.toArray(new ParserEventsListener[_listeners.size()]);

		TableAliasInfo[] aliasInfos = _parserThread.getTableAliasInfos();
		ErrorInfo[] errorInfos = _parserThread.getErrorInfos();

		for (int i = 0; i < clone.length; i++)
		{
			clone[i].aliasesFound( aliasInfos);
			clone[i].errorsFound(errorInfos);
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

		initParserThread();
		_parserThread.notifyParser(_sqlPanelApi.getSQLEntryPanel().getText());
	}

	private void initParserThread()
	{
		if(null != _parserThread)
		{
			return;
		}

		_parserThread = new ParserThread(new SQLSchemaImpl(_session));

		_sqlPanelApi.getSQLEntryPanel().getTextComponent().addKeyListener(_triggerParserKeyListener);

      // No more automatic restarts because
      // key events will restart the parser from now on.
      _parserTimer.setRepeats(false);

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
