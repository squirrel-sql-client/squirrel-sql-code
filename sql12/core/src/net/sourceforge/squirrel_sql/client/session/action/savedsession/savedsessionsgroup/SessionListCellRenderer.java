package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import javax.swing.*;
import java.awt.*;

public class SessionListCellRenderer implements ListCellRenderer<GroupDlgSessionWrapper>
{
   @Override
   public Component getListCellRendererComponent(JList<? extends GroupDlgSessionWrapper> list, GroupDlgSessionWrapper value, int index, boolean isSelected, boolean cellHasFocus)
   {
      return value.initAndGetSessionListCellPanel(isSelected, cellHasFocus);
   }
}
