/*
 * Copyright (C) 2003 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.codecompletion;


import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
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

	private MouseAdapter _listMouseAdapter;
	private KeyListener _listKeyListener;
	private static final int MAX_ITEMS_IN_COMPLETION_LIST = 10;
	private JScrollPane _completionListScrollPane;

   private CodeCompletionCandidates _currCandidates;

	private KeyStroke[] keysToDisableWhenPopUpOpen = new KeyStroke[]
	{
		KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, false)
	};


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

		_completionListScrollPane = new JScrollPane(_completionList);
		_completionPanel.add(_completionListScrollPane, BorderLayout.CENTER);
		_completionPanel.setVisible(false);

		_popupMan = new PopupManager(txtComp);
	}

	private void onKeyPressedOnList(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_TAB)
		{
         completionSelected();
		}
		else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			closePopup();
		}
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			if(1 >= _currCandidates.getStringToReplace().length())
			{
				closePopup();
			}
			else
			{
				reInitList(false);
			}
			//removeLastCharInTextComponent();
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_TAB)
		{
			// do nothing
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			if(0 < _completionList.getSelectedIndex())
			{
				int newSelIx = _completionList.getSelectedIndex() - 1;
				_completionList.setSelectionInterval(newSelIx, newSelIx);
				_completionList.ensureIndexIsVisible(newSelIx);
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			if(_completionList.getSelectedIndex() + 1 < _completionList.getModel().getSize())
			{
				int newSelIx = _completionList.getSelectedIndex() + 1;
				_completionList.setSelectionInterval(newSelIx, newSelIx);
				_completionList.ensureIndexIsVisible(newSelIx);
			}
		}
      else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
      {
         if(0 < _completionList.getSelectedIndex() - MAX_ITEMS_IN_COMPLETION_LIST)
         {
            int newSelIx = _completionList.getSelectedIndex() - MAX_ITEMS_IN_COMPLETION_LIST;
            _completionList.setSelectionInterval(newSelIx, newSelIx);
            _completionList.ensureIndexIsVisible(newSelIx);
         }
         else
         {
            _completionList.setSelectionInterval(0, 0);
            _completionList.ensureIndexIsVisible(0);
         }
      }
      else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
      {
         if(_completionList.getSelectedIndex() + MAX_ITEMS_IN_COMPLETION_LIST < _completionList.getModel().getSize())
         {
            int newSelIx = _completionList.getSelectedIndex() + MAX_ITEMS_IN_COMPLETION_LIST;
            _completionList.setSelectionInterval(newSelIx, newSelIx);
            _completionList.ensureIndexIsVisible(newSelIx);
         }
         else
         {
            int lastIndex = _completionList.getModel().getSize() - 1;
            _completionList.setSelectionInterval(lastIndex, lastIndex);
            _completionList.ensureIndexIsVisible(lastIndex);
         }

      }
		else
		{
			reInitList(true);

			DefaultListModel listModel = (DefaultListModel) _completionList.getModel();
			if(1 == listModel.size())
			{
				CodeCompletionInfo info = (CodeCompletionInfo) listModel.getElementAt(0);
				if(_currCandidates.getStringToReplace().toUpperCase().startsWith(info.getCompareString().toUpperCase()))
				{
					closePopup();
				}
			}
		}
	}

	private void reInitList(boolean selectionNarrowed)
	{
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            // Needs to be done later because when reInitList is called,
            // the text componetes model is not yet up to date.
            // E.g. the last character is missing.
            reInitListLater();
         }
      });



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

   private void reInitListLater()
   {
      try
      {
         _currCandidates = _model.getCompletionCandidates(getTextTillCarret());

         if(0 == _currCandidates.getCandidates().length)
         {
            closePopup();
         }
         else
         {
            fillAndShowCompletionList(_currCandidates.getCandidates());
         }
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }

   private String getTextTillCarret()
      throws BadLocationException
   {
      return _txtComp.getText(0, _txtComp.getCaretPosition());
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
			fireEvent( (CodeCompletionInfo)selected);
		}
		closePopup();
	}

	private void closePopup()
	{
		_txtComp.requestFocus();

		_completionList.removeMouseListener(_listMouseAdapter);
		_txtComp.removeKeyListener(_listKeyListener);
		_completionPanel.setVisible(false);


		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Keymap km = _txtComp.getKeymap();
				for (int i = 0; i < keysToDisableWhenPopUpOpen.length; i++)
				{
					km.removeKeyStrokeBinding(keysToDisableWhenPopUpOpen[i]);
				}
			}
		});
	}


	public void show()
	{
		try
		{
			_currCandidates = _model.getCompletionCandidates(getTextTillCarret());

			if(0 == _currCandidates.getCandidates().length)
			{
				return;
			}
			if(1 == _currCandidates.getCandidates().length)
			{
				fireEvent(_currCandidates.getCandidates()[0]);
				return;
			}

         _txtComp.modelToView(_currCandidates.getReplacementStart());

			_completionList.setFont(_txtComp.getFont());
			fillAndShowCompletionList(_currCandidates.getCandidates());
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}

   private int getCarretLineBeg()
   {
      String textTillCarret = _txtComp.getText().substring(0, _txtComp.getCaretPosition());

      int lineFeedIndex = textTillCarret.lastIndexOf('\n');
      if(-1 == lineFeedIndex)
      {
         return 0;
      }
      else
      {
         return lineFeedIndex;
      }
   }

   private void fillAndShowCompletionList(CodeCompletionInfo[] candidates)
	{
      try
      {
         // needed to resize completion panle appropriately
         // see initializationof _curCompletionPanelSize
         _curCompletionPanelSize = getCurCompletionPanelSize(candidates);

         DefaultListModel model = (DefaultListModel) _completionList.getModel();
         model.removeAllElements();

         for (int i = 0; i < candidates.length; i++)
         {
            model.addElement(candidates[i]);
         }

         _completionList.setSelectedIndex(0);

         Rectangle caretBounds = _txtComp.modelToView(_currCandidates.getReplacementStart());

         _popupMan.install(_completionPanel, caretBounds, PopupManager.BelowPreferred);
         _completionPanel.setVisible(true);

         _completionList.removeMouseListener(_listMouseAdapter);
         _completionList.addMouseListener(_listMouseAdapter);
         _txtComp.removeKeyListener(_listKeyListener);
         _txtComp.addKeyListener(_listKeyListener);

         Action doNothingAction = new AbstractAction("doNothingAction")
         {
            public void actionPerformed(ActionEvent e)
            {
            }
         };

         Keymap km = _txtComp.getKeymap();
         for (int i = 0; i < keysToDisableWhenPopUpOpen.length; i++)
         {
            km.addActionForKeyStroke(keysToDisableWhenPopUpOpen[i], doNothingAction);
         }
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
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


	private void fireEvent(CodeCompletionInfo completion)
	{
		Vector clone =(Vector) _listeners.clone();

		for (int i = 0; i < clone.size(); i++)
		{
			( (CodeCompletorListener)clone.elementAt(i) ).completionSelected(completion, _currCandidates.getReplacementStart());
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
