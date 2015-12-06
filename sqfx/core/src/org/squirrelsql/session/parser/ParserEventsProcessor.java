package org.squirrelsql.session.parser;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.parser.kernel.ErrorInfo;
import org.squirrelsql.session.parser.kernel.ParserThread;
import org.squirrelsql.session.parser.kernel.ParsingFinishedListener;
import org.squirrelsql.session.parser.kernel.TableAliasInfo;
import org.squirrelsql.session.sql.SQLTextAreaServices;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ParserEventsProcessor implements IParserEventsProcessor
{
	private Timer _parserTimer;
	private ParserThread _parserThread;
	private Vector<ParserEventsListener> _listeners = 
	    new Vector<ParserEventsListener>();
	private Session _session;
   private SQLTextAreaServices _sqlTextAreaServices;
	private ChangeListener<String> _triggerParserKeyListener;
   private boolean _processingEnded;

   public ParserEventsProcessor(SQLTextAreaServices sqlTextAreaServices, Session session)
   {
      _session = session;
      _sqlTextAreaServices = sqlTextAreaServices;

      ActionListener al = new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onTimerStart();
         }
      };


      _triggerParserKeyListener = new ChangeListener<String>()
      {
         @Override
         public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
         {
            onTextChanged();
         }
      };


      _parserTimer = new Timer(500, al);
      _parserTimer.start();
   }


   private void onParserExitedOnException(final Throwable e)
	{
		Platform.runLater(() -> { throw new RuntimeException(e); });
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
		if(null == _session)
		{
			return;
		}

		_processingEnded = true;

		_sqlTextAreaServices.getTextArea().textProperty().removeListener(_triggerParserKeyListener);

		if (_parserTimer != null)
		{
			_parserTimer.stop();
		}

		if (_parserThread != null)
		{
			_parserThread.exitThread();
		}


		_session = null;
		_sqlTextAreaServices = null;
		_listeners = null;
		_triggerParserKeyListener = null;
	}

	public void triggerParser()
   {
      _parserTimer.restart();
   }

	private void onParsingFinished()
	{
		Platform.runLater(new Runnable()
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
		initParserThread();
		_parserThread.notifyParser(_sqlTextAreaServices.getTextArea().getText());
	}

	private void initParserThread()
	{
		if(null != _parserThread)
		{
			return;
		}

		_parserThread = new ParserThread(new SQLSchemaImpl(_session));

		_sqlTextAreaServices.getTextArea().textProperty().addListener(_triggerParserKeyListener);

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

   private void onTextChanged()
   {
      _parserTimer.restart();
   }



}
