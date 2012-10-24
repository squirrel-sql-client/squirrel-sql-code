package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

/**
 * Created with IntelliJ IDEA.
 * User: gerd
 * Date: 24.10.12
 * Time: 20:05
 */
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
