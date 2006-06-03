package net.sourceforge.squirrel_sql.fw.gui;

import javax.swing.*;

public interface IToggleAction extends Action
{
   /**
    * The framework will call this getter and add toggleable components (JButton, JCheckedMenuItem, ... )
    * to the holder. All the implementing action needs to do is to keep a member of type ToggleComponentHolder
    * and return it here.
    *
    * Through ToggleComponentHolder.setSelected(boolean) the implementing action is able toggle selection
    * of the toggleable components kept inside the holder.
    */
   ToggleComponentHolder getToggleComponentHolder();
}
