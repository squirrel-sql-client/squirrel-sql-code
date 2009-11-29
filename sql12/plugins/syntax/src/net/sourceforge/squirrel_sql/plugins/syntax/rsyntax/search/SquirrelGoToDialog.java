package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

/*
 * 11/14/2003
 *
 * GoToDialog.java - A dialog allowing you to skip to a specific line number
 * in a document in RText.
 * Copyright (C) 2003 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://rtext.fifesoft.com
 *
 * This file is a part of RText.
 *
 * RText is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * RText is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.fife.ui.EscapableDialog;
import org.fife.ui.RButton;
import org.fife.ui.ResizableFrameContentPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A "Go To" dialog allowing you to go to a specific line number in a document
 * in RText.
 *
 * @author Robert Futrell
 * @version 1.0
 *
 * Had to be copied because it worked with the RText class
 *
 */
public class SquirrelGoToDialog extends EscapableDialog
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SquirrelGoToDialog.class);


   private JButton okButton;
   private JButton cancelButton;
   private JTextField lineNumberField;
   private int maxLineNumberAllowed;   // Number of lines in the document.
   private int lineNumber;         // The line to go to, or -1 for Cancel.

   /**
    * Creates a new <code>GoToDialog</code>.
    *
    * @param owner          The rtext window that owns this dialog.
    */
   public SquirrelGoToDialog(JFrame parent)
   {

      // Let it be known who the owner of this dialog is.
      super(parent);

      ComponentOrientation orientation = ComponentOrientation.
         getOrientation(getLocale());

      lineNumber = 1;
      maxLineNumberAllowed = 1; // Empty document has 1 line.
      GoToListener listener = new GoToListener();

      // Set the main content pane for the "GoTo" dialog.
      JPanel contentPane = new ResizableFrameContentPane(new BorderLayout());
      contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      setContentPane(contentPane);

      // Make a panel containing the "Line Number" edit box.
      JPanel enterLineNumberPane = new JPanel();
      BoxLayout box = new BoxLayout(enterLineNumberPane, BoxLayout.LINE_AXIS);
      enterLineNumberPane.setLayout(box);
      lineNumberField = new JTextField(16);
      lineNumberField.setText("" + lineNumber);
      AbstractDocument doc = (AbstractDocument) lineNumberField.getDocument();
      doc.addDocumentListener(listener);
      doc.setDocumentFilter(new GoToDocumentFilter());
      enterLineNumberPane.add(new JLabel(s_stringMgr.getString("syntax.SquirrelGoToDialog.LineNumber")));
      enterLineNumberPane.add(lineNumberField);

      // Make a panel containing the OK and Cancel buttons.
      JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
      okButton = createRButton(s_stringMgr.getString("syntax.SquirrelGoToDialog.OKButtonLabel"), s_stringMgr.getString("syntax.SquirrelGoToDialog.OKButtonMnemonic"));
      okButton.setActionCommand("OK");
      okButton.addActionListener(listener);
      cancelButton = createRButton(s_stringMgr.getString("syntax.SquirrelGoToDialog.Cancel"), s_stringMgr.getString("syntax.SquirrelGoToDialog.CancelMnemonic"));
      cancelButton.setActionCommand("Cancel");
      cancelButton.addActionListener(listener);
      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);

      // Put everything into a neat little package.
      contentPane.add(enterLineNumberPane, BorderLayout.NORTH);
      JPanel temp = new JPanel();
      temp.add(buttonPanel);
      contentPane.add(temp, BorderLayout.SOUTH);
      JRootPane rootPane = getRootPane();
      rootPane.setDefaultButton(okButton);
      setTitle(s_stringMgr.getString("syntax.SquirrelGoToDialog.GotoDialogTitle"));
      setModal(true);
      applyComponentOrientation(orientation);
      pack();
      setLocationRelativeTo(parent);

   }

   public static final RButton createRButton(String textKey, String mnemonicKey)
   {
      RButton b = new RButton(textKey);
      b.setMnemonic((int) mnemonicKey.charAt(0));
      return b;
   }


   /**
    * Called when they've clicked OK or pressed Enter; check the line number
    * they entered for validity and if it's okay, close this dialog.  If it
    * isn't okay, display an error message.
    */
   private void attemptToGetGoToLine()
   {

      try
      {

         lineNumber = Integer.parseInt(lineNumberField.getText());

         if (lineNumber < 1 || lineNumber > maxLineNumberAllowed)
            throw new NumberFormatException();

         // If we have a valid line number, close the dialog!
         setVisible(false);

      }
      catch (NumberFormatException nfe)
      {
         JOptionPane.showMessageDialog(this,
            s_stringMgr.getString("syntax.SquirrelGoToDialog.LineNumberRange", maxLineNumberAllowed),
            s_stringMgr.getString("syntax.SquirrelGoToDialog.ErrorDialogTitle"),
            JOptionPane.ERROR_MESSAGE);
         return;
      }

   }


   /**
    * Called when the user clicks Cancel or hits the Escape key.  This
    * hides the dialog.
    */
   protected void escapePressed()
   {
      lineNumber = -1;
      super.escapePressed();
   }


   /**
    * Gets the line number the user entered to go to.
    *
    * @return The line number the user decided to go to, or <code>-1</code>
    *         if the dialog was canceled.
    */
   public int getLineNumber()
   {
      return lineNumber;
   }


   /**
    * Sets the maximum line number for them to enter.
    *
    * @param max The new maximum line number value.
    */
   public void setMaxLineNumberAllowed(int max)
   {
      this.maxLineNumberAllowed = max;
   }


   /**
    * Overrides <code>JDialog</code>'s <code>setVisible</code> method; decides
    * whether or not buttons are enabled if the user is enabling the dialog.
    */
   public void setVisible(boolean visible)
   {
      if (visible)
      {
         lineNumber = -1;
         okButton.setEnabled(lineNumberField.getDocument().getLength() > 0);
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               lineNumberField.requestFocusInWindow();
               lineNumberField.selectAll();
            }
         });
      }
      super.setVisible(visible);
   }


   /**
    * A document filter that only allows numbers.
    */
   static class GoToDocumentFilter extends DocumentFilter
   {

      private final String cleanse(String text)
      {
         boolean beep = false;
         if (text != null)
         {
            int length = text.length();
            for (int i = 0; i < length; i++)
            {
               if (!Character.isDigit(text.charAt(i)))
               {
                  text = text.substring(0, i) + text.substring(i + 1);
                  i--;
                  length--;
                  beep = true;
               }
            }
         }
         if (beep)
            UIManager.getLookAndFeel().provideErrorFeedback(null);
         return text;
      }

      public void insertString(DocumentFilter.FilterBypass fb,
                               int offset, String text, AttributeSet attr)
         throws BadLocationException
      {
         fb.insertString(offset, cleanse(text), attr);
      }

      public void remove(DocumentFilter.FilterBypass fb,
                         int offset, int length)
         throws BadLocationException
      {
         fb.remove(offset, length);
      }

      public void replace(DocumentFilter.FilterBypass fb,
                          int offset, int length, String text, AttributeSet attr)
         throws BadLocationException
      {
         fb.replace(offset, length, cleanse(text), attr);
      }

   }


   /**
    * Listens for events in this dialog.
    */
   private class GoToListener implements ActionListener, DocumentListener
   {

      public void actionPerformed(ActionEvent e)
      {
         String command = e.getActionCommand();
         if (command.equals("OK"))
            attemptToGetGoToLine();
         else if (command.equals("Cancel"))
            escapePressed();
      }

      public void changedUpdate(DocumentEvent e)
      {
      }

      public void insertUpdate(DocumentEvent e)
      {
         okButton.setEnabled(lineNumberField.getDocument().getLength() > 0);
      }

      public void removeUpdate(DocumentEvent e)
      {
         okButton.setEnabled(lineNumberField.getDocument().getLength() > 0);
      }

	}


}