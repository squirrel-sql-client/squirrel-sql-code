package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

import java.awt.LayoutManager;
import javax.swing.JPanel;

public class AddressablePrefJPanel extends JPanel implements AddressablePrefComponent
{
   private PreferencesAddressBook _preferencesAddressBookEntry;

   public AddressablePrefJPanel(PreferencesAddressBook preferencesAddressBookEntry, LayoutManager layout)
   {
      super(layout);
      _preferencesAddressBookEntry = preferencesAddressBookEntry;
   }

   public AddressablePrefJPanel(PreferencesAddressBook preferencesAddressBookEntry)
   {
      _preferencesAddressBookEntry = preferencesAddressBookEntry;
   }

   @Override
   public PreferencesAddressBook getAddress()
   {
      return _preferencesAddressBookEntry;
   }
}
