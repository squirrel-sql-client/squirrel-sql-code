package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import javax.swing.*;
import java.awt.*;

public class SessionListCellRenderer implements ListCellRenderer<GroupDlgSessionWrapper>
{
   @Override
   public Component getListCellRendererComponent(JList<? extends GroupDlgSessionWrapper> list, GroupDlgSessionWrapper wrapperToRender, int index, boolean isSelected, boolean cellHasFocus)
   {
      return wrapperToRender.initAndGetListCellPanelForRendering(isSelected, cellHasFocus);
   }
}
