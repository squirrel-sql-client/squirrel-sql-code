package net.sourceforge.squirrel_sql.fw.gui.buttonchooser;

import javax.swing.AbstractButton;

@FunctionalInterface
public interface ButtonSelectedListener
{
   void buttonSelected(AbstractButton newSelectedButton, AbstractButton formerSelectedButton);
}
