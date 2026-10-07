package net.sourceforge.squirrel_sql.fw.gui.switchablemenu;

@FunctionalInterface
public interface SwitchableMenuCtrlListener<OPT extends SwitchableMenuOption>
{
   void selected(OPT option);
}
