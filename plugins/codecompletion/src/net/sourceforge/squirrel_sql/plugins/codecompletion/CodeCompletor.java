package net.sourceforge.squirrel_sql.plugins.codecompletion;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class CodeCompletor
{
	private Vector _listeners = new Vector();
	private CodeCompletorModel _model;
	private JPanel _completionPanel;
	private JList _completionList;

	private Rectangle _curCompletionPanelSize;
	private PopupManager _popupMan;
	private JTextComponent _txtComp;
	private String _currBegining;

	private MouseAdapter _listMouseAdapter;
	private KeyListener _listKeyListener;
	private FocusListener _listFocusListener;
	private Rectangle _currCaretBounds;
	private static final int MAX_ITEMS_IN_COMPLETION_LIST = 10;
	private JScrollPane _completionListScrollPane;
	private Timer _focusTimer;


	public CodeCompletor(JTextComponent txtComp, CodeCompletorModel model)
	{
		_txtComp = txtComp;
		_model = model;

		_completionPanel =
			new JPanel(new BorderLayout())
			{
				public void setSize(int width, int height)
				{
					// without this the completion pnels size will be weird
					super.setSize(_curCompletionPanelSize.width, _curCompletionPanelSize.height);
				}
			};

		_completionList = new JList(new DefaultListModel());
		_completionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_completionList.setBackground(new Color(255,255,204));

		_listMouseAdapter =
			new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)	{onMousClicked(e);}
			};

		_listKeyListener =
			new KeyAdapter()
			{
				public void keyPressed(KeyEvent e){onKeyPressedOnList(e);}
			};

		_listFocusListener = new FocusListener()
		{
			public void focusGained(FocusEvent e){onFocusGained(e);}

			public void focusLost(FocusEvent e){onFocusLosOnList(e);}
		};


		_completionListScrollPane = new JScrollPane(_completionList);
		_completionPanel.add(_completionListScrollPane, BorderLayout.CENTER);
		_completionPanel.setVisible(false);

		_popupMan = new PopupManager(txtComp);

		ActionListener timerListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				closePopup();
			}
		};

		_focusTimer = new Timer(300, timerListener);
		_focusTimer.setRepeats(false);

	}

	private void onFocusGained(FocusEvent e)
	{
		if(false == e.isTemporary())
		{
			_focusTimer.stop();
		}
	}

	private void onFocusLosOnList(FocusEvent e)
	{
		if(false == e.isTemporary())
		{
			_focusTimer.start();
		}
	}

	private void onKeyPressedOnList(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
		{
         completionSelected();
		}
		else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			closePopup();
		}
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			if(1 >= _currBegining.length())
			{
				closePopup();
			}
			else
			{
				_currBegining = _currBegining.substring(0, _currBegining.length() - 1);
				reInitList(false);
			}
			removeLastCharInTextComponent();
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			closePopup();
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			// don't send to text field
		}

		if(shouldBeSentToTextComponent(e))
		{
			_currBegining += e.getKeyChar();
			reInitList(true);
			_txtComp.replaceSelection("" + e.getKeyChar());

			DefaultListModel listModel = (DefaultListModel) _completionList.getModel();
			if(1 == listModel.size())
			{
				CodeCompletionInfo info = (CodeCompletionInfo) listModel.getElementAt(0);
				if(_currBegining.toUpperCase().startsWith(info.getCompletionString().toUpperCase()))
				{
					closePopup();
				}
			}
		}
	}

	boolean shouldBeSentToTextComponent(KeyEvent e)
	{
		// TODO find something better than this hard codening
		char[] allowedBesidesLettersOrDigits
		= new char[]{
			           '!', '"', '§', '$', '%', '&', '/', '(', ')', '=', '?','\'', '\\', '#', '~',
			           '_', '+', '-', '*', '{', '}', '[', ']', ',', ';', '.', ':',
						  'ä', 'Ä', 'ö', 'Ö', 'ü', 'Ü', 'ß'
		            };

		if(Character.isLetterOrDigit(e.getKeyChar()))
		{
			return true;
		}

		for (int i = 0; i < allowedBesidesLettersOrDigits.length; i++)
		{
			if(e.getKeyChar() == allowedBesidesLettersOrDigits[i])
			{
				return true;
			}
		}

		return false;


	}

	private void removeLastCharInTextComponent()
	{
		int caretPos = _txtComp.getCaretPosition();
		if(0 < caretPos)
		{
			_txtComp.setSelectionStart(caretPos -1);
			_txtComp.setSelectionEnd(caretPos);
			_txtComp.replaceSelection("");
		}

	}


	private void reInitList(boolean selectionNarrowed)
	{
		CodeCompletionInfo[] candidates = _model.getCompletionInfos(_currBegining);
		if(0 == candidates.length)
		{
			closePopup();
		}
		else
		{
			fillAndShowCompletionList(candidates);
		}


		/*
		As long as there are no performance problems, don't care for this
		if(selectionNarrowed)
		{

		}
		else
		{

		}
		*/
	}

	private void onMousClicked(MouseEvent e)
	{
		if(2 == e.getClickCount())
		{
			completionSelected();
		}
	}

	private void completionSelected()
	{
		Object selected = _completionList.getSelectedValue();
		if(null != selected && selected instanceof CodeCompletionInfo)
		{
			fireEvent( ( (CodeCompletionInfo)selected ).getCompletionString());
		}
		closePopup();
	}

	private void closePopup()
	{
		_completionList.removeMouseListener(_listMouseAdapter);
		_completionList.removeKeyListener(_listKeyListener);
		_completionList.removeFocusListener(_listFocusListener);
		_completionPanel.setVisible(false);
		_txtComp.requestFocus();
	}


	public void show(String beginning)
	{
		try
		{
			CodeCompletionInfo[] candidates = _model.getCompletionInfos(beginning);

			if(0 == candidates.length)
			{
				return;
			}
			if(1 == candidates.length)
			{
				fireEvent(candidates[0].getCompletionString());
				return;
			}

			_currBegining = beginning;

			int caretPos = _txtComp.getCaret().getDot();

			if(-1 < _currBegining.indexOf('.'))
			{
				_currCaretBounds = _txtComp.modelToView(caretPos - (_currBegining.length() - _currBegining.lastIndexOf('.') - 1));
			}
			else
			{
				_currCaretBounds = _txtComp.modelToView(caretPos - _currBegining.length());
			}


			_completionList.setFont(_txtComp.getFont());
			fillAndShowCompletionList(candidates);
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void fillAndShowCompletionList(CodeCompletionInfo[] candidates)
	{
		// needed to resize completion panle appropriately
		// see initializationof _curCompletionPanelSize
		_curCompletionPanelSize = getCurCompletionPanelSize(candidates);

//		if(MAX_ITEMS_IN_COMPLETION_LIST >= candidates.length)
//		{
//			_completionListScrollPane.setHorizontalScrollBar(null);
//		}
//		else
//		{
//			_completionListScrollPane.setHorizontalScrollBar(new JScrollBar());
//		}

		DefaultListModel model = (DefaultListModel) _completionList.getModel();
		model.removeAllElements();

		for (int i = 0; i < candidates.length; i++)
		{
			model.addElement(candidates[i]);
		}

		_completionList.setSelectedIndex(0);

		_popupMan.install(_completionPanel, _currCaretBounds, PopupManager.BelowPreferred);
		_completionPanel.setVisible(true);

		_completionList.removeMouseListener(_listMouseAdapter);
		_completionList.addMouseListener(_listMouseAdapter);
		_completionList.removeKeyListener(_listKeyListener);
		_completionList.addKeyListener(_listKeyListener);
		_completionList.removeFocusListener(_listFocusListener);
		_completionList.addFocusListener(_listFocusListener);

		_completionList.requestFocus();
	}

	private Rectangle getCurCompletionPanelSize(CodeCompletionInfo[] candidates)
	{
		FontMetrics fm = _txtComp.getGraphics().getFontMetrics(_txtComp.getFont());
		int width = getMaxSize(candidates, fm) + 30;
		int height = (int)(Math.min(candidates.length,  MAX_ITEMS_IN_COMPLETION_LIST) * (fm.getHeight() + 2.3) + 3);
		return new Rectangle(width, height);
	}

	private int getMaxSize(CodeCompletionInfo[] infos, FontMetrics fontMetrics)
	{
		int maxSize = 0;
		for (int i = 0; i < infos.length; i++)
		{
			int buf = fontMetrics.stringWidth(infos[i].toString());
			if(maxSize < buf)
			{
				maxSize = buf;
			}
		}
		return maxSize;

	}


	private void fireEvent(String completion)
	{
		Vector clone =(Vector) _listeners.clone();

		for (int i = 0; i < clone.size(); i++)
		{
			( (CodeCompletorListener)clone.elementAt(i) ).completionSelected(completion);
		}
	}

	public void addCodeCompletorListener(CodeCompletorListener l)
	{
		_listeners.add(l);
	}

	public void removeCodeCompletorListener(CodeCompletorListener l)
	{
		_listeners.remove(l);
	}

}
