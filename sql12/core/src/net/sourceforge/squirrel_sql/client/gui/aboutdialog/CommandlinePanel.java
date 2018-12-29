package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

public class CommandlinePanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreditsPanel.class);

   public CommandlinePanel()
   {
      try
      {
         setLayout(new GridBagLayout());

         ArrayList<Object[]> rows = new ArrayList<>();

         for (String[] option : ApplicationArguments.IOptions.ALL_OPTIONS)
         {
            rows.add(option);
         }

         ColumnDisplayDefinition [] columnDisplayDefinitions = new ColumnDisplayDefinition[]
               {
                  new ColumnDisplayDefinition(15, s_stringMgr.getString("CommandlinePanel.column.short.option")),
                  new ColumnDisplayDefinition(40, s_stringMgr.getString("CommandlinePanel.column.long.option")),
                  new ColumnDisplayDefinition(250, s_stringMgr.getString("CommandlinePanel.column.option.description"))
               };


         SimpleDataSet simpleDataSet = new SimpleDataSet(rows, columnDisplayDefinitions);

         DataSetViewerTablePanel table = new DataSetViewerTablePanel();
         table.init(null, null);
         table.show(simpleDataSet);


         GridBagConstraints gbc;

         gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,5,0), 0,0);
         add(new JScrollPane(table.getComponent()), gbc);

         gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
         MultipleLineLabel lblCurrent = new MultipleLineLabel(s_stringMgr.getString("CommandlinePanel.current.command.line", getArgumentsString()));
         add(lblCurrent, gbc);

      }
      catch (DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private String getArgumentsString()
   {
      String ret = null;
      String[] rawArguments = ApplicationArguments.getInstance().getRawArguments();

      for (int i = 0; i < rawArguments.length; i++)
      {
         if(ret == null)
         {
            ret = rawArguments[i];
         }
         else if (rawArguments[i].startsWith("-"))
         {
            ret += "\n" + rawArguments[i];
         }
         else
         {
            ret += " " + rawArguments[i];
         }

      }

      return ret;
   }
}
