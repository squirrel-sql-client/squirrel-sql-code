package net.sourceforge.squirrel_sql.plugins.sqlparam.gui;
/*
 * Copyright (C) 2007 Thorsten Mürell
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

import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;


/**
 * The dialog to ask the user for a value.
 *
 * @author Thorsten Mürell
 */
public class AskParamValueDialog extends JDialog
{
   private static final StringManager stringMgr = StringManagerFactory.getStringManager(AskParamValueDialog.class);

   private static final String PREF_KEY_ALWAYS_QUOTE_VALUES = "SqlparamPlugin.always.quote.values";


   private OkClosePanel _btnsPnl = new OkClosePanel();
   private JTextField _txtValue = new JTextField();
   private JCheckBox _chkQuote = new JCheckBox();
   private JCheckBox _chkQuoteAlways = new JCheckBox();
   private String _parameter;

   private boolean _done = false;
   private boolean _cancelled = false;

   /**
    * Creates the dialog.
    *  @param application
    * @param owningFrame
    * @param parameter   The name of the parameter to replace.
    * @param oldValue    The old value of the parameter to provide as a default.
    */
   public AskParamValueDialog(Frame owningFrame, String parameter, String oldValue)
   {
      super(owningFrame, stringMgr.getString("sqlparam.inputParameterValues"), true);
      _parameter = parameter;

      setContentPane(createMainPanel());

      _txtValue.setText(oldValue);
      _txtValue.getDocument().addDocumentListener(new DocumentListener()
      {
         public void changedUpdate(DocumentEvent e)
         {
            updateCheckbox();
         }

         public void insertUpdate(DocumentEvent e)
         {
            updateCheckbox();
         }

         public void removeUpdate(DocumentEvent e)
         {
            updateCheckbox();
         }
      });

      _chkQuoteAlways.setSelected(Props.getBoolean(PREF_KEY_ALWAYS_QUOTE_VALUES, false));
      _chkQuoteAlways.addActionListener(e -> onQuoteAlways());


      updateCheckbox();
      _btnsPnl.addListener(new MyOkClosePanelListener());

      _btnsPnl.makeOKButtonDefault();
      _btnsPnl.getRootPane().setDefaultButton(_btnsPnl.getOKButton());


      pack();

      GUIUtils.centerWithinParent(this);
      GUIUtils.enableCloseByEscape(this, d -> _cancelled = true);
   }

   private void onQuoteAlways()
   {
      Props.putBoolean(PREF_KEY_ALWAYS_QUOTE_VALUES, _chkQuoteAlways.isSelected());
   }

   /**
    * @return <code>true</code> if the dialog is done
    */
   public boolean isDone()
   {
      return _done;
   }

   /**
    * If the user doesn't want to input a value, he hits the close
    * button. The this method returns true.
    *
    * @return <code>true</code> if the dialog was cancelled by the user.
    */
   public boolean isCancelled()
   {
      return _cancelled;
   }

   /**
    * Sets the value of the dialog.
    *
    * @param defaultValue the value to set as a default in this dialog.
    */
   public void setValue(String defaultValue)
   {
      _txtValue.setText(defaultValue);
   }

   /**
    * Gets the value of the input field in this dialog.
    *
    * @return The value for the parameter.
    */
   public String getValue()
   {
      return _txtValue.getText();
   }

   /**
    * Returns if quotes around the value are needed.
    *
    * @return <code>true</code> if quoting is needed.
    */
   public boolean isQuotingNeeded()
   {
      return _chkQuote.isSelected();
   }

   private void updateCheckbox()
   {
      boolean isNumber;

      try
      {
         Float.parseFloat(_txtValue.getText());
         isNumber = true;
      }
      catch (NumberFormatException nfe)
      {
         isNumber = false;
      }

      if (isNumber)
      {
         _chkQuote.setSelected(_chkQuoteAlways.isSelected());
         _chkQuote.setEnabled(true);
      }
      else
      {
         _chkQuote.setSelected(true);
         _chkQuote.setEnabled(false);
      }
   }

   private JPanel createMainPanel()
   {
      _txtValue.setColumns(20);

      JPanel panel = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      panel.add(new JLabel(getTitle()), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      panel.add(new JLabel(stringMgr.getString("sqlparam.valueFor", _parameter)), gbc);

      gbc = new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      panel.add(_txtValue, gbc);


      gbc = new GridBagConstraints(0,2,2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		_chkQuote.setText(stringMgr.getString("sqlparam.quoteValues"));
      panel.add(_chkQuote, gbc);


      gbc = new GridBagConstraints(0,3,2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		_chkQuoteAlways.setText(stringMgr.getString("sqlparam.quoteValues.always"));
      panel.add(_chkQuoteAlways, gbc);

      gbc = new GridBagConstraints(0,4,2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      panel.add(_btnsPnl, gbc);

      return panel;
   }

   /**
    * Cancels the dialog.
    */
   public void onCancel()
   {
      _done = true;
      _cancelled = true;
      setVisible(false);
      dispose();
   }

   /**
    * Confirms the dialog.
    */
   public void onOk()
   {
      _done = true;
      setVisible(false);
      dispose();
   }

   public void requestFocusForInputField()
   {
      _txtValue.requestFocusInWindow();
      _txtValue.requestFocus();
   }


   private final class MyOkClosePanelListener implements IOkClosePanelListener
   {
      /**
       * Callback for the ok key.
       *
       * @param evt the event
       */
      public void okPressed(OkClosePanelEvent evt)
      {
         onOk();
      }

      /**
       * Callback for the close key.
       *
       * @param evt the event
       */
      public void closePressed(OkClosePanelEvent evt)
      {
         onCancel();
      }

      /**
       * Callback for the cancel key.
       *
       * @param evt the event
       */
      public void cancelPressed(OkClosePanelEvent evt)
      {
         onCancel();
      }
   }

}
