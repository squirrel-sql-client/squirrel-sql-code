package net.sourceforge.squirrel_sql.fw.gui;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;

public class EditableComboBoxHandler
{
   private final JComboBox _cbo;
   private final String _prefKeyPrefix;
   private final int _maxItemCount;
   private String _defaultString;

   public EditableComboBoxHandler(JComboBox cbo, String prefKeyPrefix)
   {
      this(cbo, prefKeyPrefix, 10, null);
   }
   public EditableComboBoxHandler(JComboBox cbo, String prefKeyPrefix, int maxItemCount)
   {
      this(cbo, prefKeyPrefix, maxItemCount, null);
   }
   public EditableComboBoxHandler(JComboBox cbo, String prefKeyPrefix, int maxItemCount, String defaultString)
   {
      _cbo = cbo;
      _cbo.setEditable(true);

      _prefKeyPrefix = prefKeyPrefix;
      _maxItemCount = maxItemCount;
      _defaultString = defaultString;

      loadComboBox();
   }

   private void loadComboBox()
   {
      for (int i = 0; ; i++)
      {
         String item = Props.getString(_prefKeyPrefix + i, null);
         if (null == item)
         {
            break;
         }
         _cbo.addItem(item);
      }

      if(false == StringUtilities.isEmpty(_defaultString, true))
      {
         _cbo.getEditor().setItem(_defaultString);
         _cbo.getEditor().selectAll();
      }
      else
      {
         _cbo.getEditor().setItem(null);
      }
   }

   public void addOrReplaceCurrentItem(String newItem)
   {
      for (int i = 0; i < _cbo.getItemCount(); i++)
      {
         if(newItem.equals(_cbo.getItemAt(i)))
         {
            _cbo.removeItemAt(i);
         }
      }
      ((DefaultComboBoxModel)_cbo.getModel()).insertElementAt(newItem, 0);


      ArrayList itemsToRemove = new ArrayList();
      for (int i = 0; i <  _cbo.getItemCount(); i++)
      {
         final String item = (String) _cbo.getItemAt(i);
         if (_maxItemCount > i && false == StringUtilities.isEmpty(item))
         {
            Props.putString(_prefKeyPrefix + i, item);
         }
         else
         {
            itemsToRemove.add(item);
         }
      }

      for (Object item : itemsToRemove)
      {
         _cbo.removeItem(item);
      }

      _cbo.setSelectedIndex(0);

   }

   public void focus()
   {
      GUIUtils.forceFocus(_cbo.getEditor().getEditorComponent());
   }

   public String getItem()
   {
      return "" + _cbo.getEditor().getItem();
   }

   public void addDocumentListener(DocumentListener documentListener)
   {
      if(_cbo.getEditor().getEditorComponent() instanceof JTextComponent)
      {
         ((JTextComponent)_cbo.getEditor().getEditorComponent()).getDocument().addDocumentListener(documentListener);
      }
      else
      {
         throw new UnsupportedOperationException("Failed to add DocumentListener to editable Combobox as the ComboBoxes editor isn't an instance of JTextComponent");
      }
   }

   public void saveCurrentItem()
   {
      addOrReplaceCurrentItem(getItem());
   }
}
