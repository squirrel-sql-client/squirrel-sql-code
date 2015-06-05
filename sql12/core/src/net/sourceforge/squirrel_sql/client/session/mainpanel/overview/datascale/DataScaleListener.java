package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import javax.swing.*;
import java.awt.*;

public interface DataScaleListener
{
   void intervalSelected(Interval interval, JButton intervalButtonClicked);

   void showInTableWin(Interval interval);

   void showInTable(Interval interval);

   void showIntervalDetails(String intervalDetailsHtml, Point dialogLocation);
}
