/*
 * Copyright (C) 2002 Christian Sell
 * csell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * created by cse, 13.09.2002 23:35:35
 *
 * @version $Id: CompletionAdapter.java,v 1.5 2002-10-14 19:13:56 csell Exp $
 */
package net.sourceforge.jcomplete.ui;

import java.awt.event.*;
import java.awt.Rectangle;

import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import net.sourceforge.jcomplete.CompletionHandler;
import net.sourceforge.jcomplete.Completion;

/**
 * this class mediates between a JTextComponent and the completion parser by
 * implementing the KeyListener interface. It should display the appropriate
 * completion UI whenever requested
 */
public abstract class CompletionAdapter extends KeyAdapter implements CompletionListener
{
    protected JTextComponent m_textComponent;
    protected CompletionHandler m_completionHandler;

    protected PopupManager m_popupManager;
    protected CompletionListPopup m_popup;

    private int m_popupKey = KeyEvent.VK_SPACE;
    private int m_popupMask = KeyEvent.CTRL_MASK;

    public CompletionAdapter(
          JTextComponent textComponent, CompletionHandler completor, int popupMask, int popupKey)
    {
        m_popupKey = popupKey;
        m_popupMask = popupMask;
        m_textComponent = textComponent;
        m_completionHandler = completor;
    }

    public void keyPressed(KeyEvent e)
    {
        // check for auto-jcomplete sequence
        if ((e.getModifiers() & m_popupMask) > 0 && e.getKeyCode() == m_popupKey) {
            Completion comp = m_completionHandler.getCompletion(m_textComponent.getCaretPosition());
            if(comp != null) {
                showCompletionUI(comp);
            }
        }
    }

    /**
     * show the UI for the given completion. The UI should call back
     * to this object via the {@link CompletionListener} interface.
     * @param completion the completion object at the current caret position
     */
    protected abstract void showCompletionUI(Completion completion);

    protected boolean showPopupList(Completion completion, Object[] options, boolean multiple)
    {
        if(m_popupManager == null)
            m_popupManager = new PopupManager(m_textComponent);
        if(m_popup == null)
            m_popup = new CompletionListPopup(this, multiple);

        int dot = m_textComponent.getCaret().getDot();
        try {
            Rectangle rect = m_textComponent.modelToView(dot);
            m_popupManager.install(m_popup, rect, PopupManager.BelowPreferred);
            m_popup.show(completion, options);
        }
        catch (BadLocationException e) {}
        return true;
    }

    /**
     * process a completion event, triggered by a UI which provides an array of
     * objects representing the selected completion options. The selected objects are
     * translated into strings by the accompanying completion object, and inserted into
     * the underlying document at the caret position, separated by commas.
     * @param completion the object which handles the completion
     * @param selectedOptions the options to use for completion
     * @return either <em>completion</em>, or a substitute created during a reparse
     */
    public Completion completionRequested(Completion completion, Object[] selectedOptions)
    {
        try {
            // insert the text
            Document doc = m_textComponent.getDocument();
            boolean needsSeparator = false;

            if(completion.mustReplace(m_textComponent.getCaretPosition())) {
                doc.remove(completion.getStart(), completion.getLength());
            }
            for(int i=0; i<selectedOptions.length; i++) {
                if(needsSeparator)
                    doc.insertString(m_textComponent.getCaretPosition(), ", ", null);
                String text = completion.getText(
                      m_textComponent.getCaretPosition(), selectedOptions[i].toString());
                doc.insertString(m_textComponent.getCaretPosition(), text, null);
                needsSeparator = true;
            }
            m_textComponent.requestFocus();

            //possibly, a reparse was triggered and a new completion tree created
            return m_completionHandler.getCompletion(completion.getStart());
        }
        catch (BadLocationException e1) {
            throw new RuntimeException(e1.getMessage());
        }
    }

    /**
     * process a completion event, triggered by a UI which provides preprocessed completions
     * through the event object
     * @param event event object describing the completion requested
     * @return either <em>event.completion</em>, or a substitute created during a reparse
     */
    public Completion completionRequested(CompletionListener.Event event)
    {
        try {
            // insert the text
            Document doc = m_textComponent.getDocument();

            if(event.completion.mustReplace(m_textComponent.getCaretPosition())) {
                doc.remove(event.completion.getStart(), event.completion.getLength());
            }
            while(event.hasNext()) {
                if(event.needsSeparator())
                    doc.insertString(m_textComponent.getCaretPosition(), ", ", null);

                Completion comp = event.next();
                String text = comp.getText(m_textComponent.getCaretPosition());
                doc.insertString(m_textComponent.getCaretPosition(), text, null);
            }
            //possibly, a reparse was triggered and a new completion tree created
            return m_completionHandler.getCompletion(event.completion.getStart());
        }
        catch (BadLocationException e1) {
            throw new RuntimeException(e1.getMessage());
        }
    }

    /**
     * callback used if the user aborted the completion request
     */
    public void completionAborted()
    {
        m_textComponent.requestFocus();
    }
}