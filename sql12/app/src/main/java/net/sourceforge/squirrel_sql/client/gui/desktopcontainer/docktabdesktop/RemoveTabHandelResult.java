package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

public class RemoveTabHandelResult
{
   private ButtonTabComponent _removedButtonTabComponent;
   private TabHandle _tabHandle;

   public void setRemovedButtonTabComponent(ButtonTabComponent removedButtonTabComponent)
   {
      _removedButtonTabComponent = removedButtonTabComponent;
   }

   public void setRemovedTabHandle(TabHandle tabHandle)
   {
      _tabHandle = tabHandle;
   }

   public ButtonTabComponent getRemovedButtonTabComponent()
   {
      return _removedButtonTabComponent;
   }

   public TabHandle getTabHandle()
   {
      return _tabHandle;
   }
}
