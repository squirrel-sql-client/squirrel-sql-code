package net.sourceforge.squirrel_sql.fw.completion;

import javax.swing.*;
import java.awt.event.*;

/**
 * This tries to handle the focus of the completion popup
 * and tell its listener when the completion popup lost focus.
 * <p/>
 * Note: this class does not send focus gained events.
 */
public class CompletionFocusHandler
{
	private TextComponentProvider _txtComp;
	private JList _completionList;
	private FocusListener _completionFocusListener;
	private Timer _timer;
	private ActionListener _timerActionListener;
	private FocusEvent _lastFocusEvent;


	public CompletionFocusHandler(TextComponentProvider txtComp, JList completionList)
	{
		_txtComp = txtComp;
		_completionList = completionList;

		_timerActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onTimerAction();
			}
		};

		_timer = new Timer(200, null);
		_timer.setRepeats(false);


		_completionList.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				//System.out.println("################_completionList.focusGained");
				onCompletionListFocusGained(e);
				onFocusGained(e);
			}

			public void focusLost(FocusEvent e)
			{
				//System.out.println("################_completionList.focusLost");
				onFocusLost(e);
			}
		});

		if (_txtComp.editorEqualsFilter())
		{
			_txtComp.getEditor().addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent e)
				{
					//System.out.println("################editor.focusGained " + _txtComp.getEditor());
					onFocusGained(e);
				}

				public void focusLost(FocusEvent e)
				{
					//System.out.println("################editor.focusLost " + _txtComp.getEditor());
					onFocusLost(e);
				}
			});
		}
		else
		{
			_txtComp.getFilter().addFocusListener(new FocusListener()
			{
				public void focusGained(FocusEvent e)
				{
					//System.out.println("################filter.focusGained");
					onFocusGained(e);
				}

				public void focusLost(FocusEvent e)
				{
					//System.out.println("################filter.focusLost");
					onFocusLost(e);
				}
			});
		}

	}

	private void onFocusGained(FocusEvent e)
	{
		// Temporary events need to be included
		// in case a modla dialog opens.
		_timer.stop();
		_lastFocusEvent = e;
	}

	private void onFocusLost(FocusEvent e)
	{
		// Note: Temporary events need to be included
		// in case a modla dialog opens.
		_lastFocusEvent = e;
		_timer.start();
	}

	private void onTimerAction()
	{
		_timer.stop();
		if (null != _completionFocusListener)
		{
			//System.out.println("CompletionFocusHandler.onTimerAction");
			_completionFocusListener.focusLost(_lastFocusEvent);
		}
	}


	/**
	 * This method has nothing to do with the focus handling outside the
	 * popup it is needed to keep the focus on the filter, if the popup has
	 * its own filter, for example the tools popup.
	 */
	private void onCompletionListFocusGained(FocusEvent e)
	{
		if (false == e.isTemporary() && false == _txtComp.editorEqualsFilter())
		{
			_txtComp.getFilter().requestFocusInWindow();
		}
	}


	public void setFocusListener(FocusListener completionFocusListener)
	{
		_completionFocusListener = completionFocusListener;

		_timer.removeActionListener(_timerActionListener);
		if (null != _completionFocusListener)
		{
			_timer.addActionListener(_timerActionListener);
		}
	}
}
