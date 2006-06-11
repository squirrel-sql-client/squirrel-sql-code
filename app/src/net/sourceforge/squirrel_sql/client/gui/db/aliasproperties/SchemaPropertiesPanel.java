package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class SchemaPropertiesPanel extends JPanel
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SchemaPropertiesPanel.class);


   JRadioButton radLoadAllAndCacheNone;
   JRadioButton radLoadAndCacheAll;
   JRadioButton radSpecifySchemas;

   JButton btnUpdateSchemas;

   JTable tblSchemas;



   public SchemaPropertiesPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[SchemaPropertiesPanel.hint=Here you may pecify which schemas to be loaded and displayed in a Session's Object tree.
      // Code completion and Syntax highlighting will work only for loaded schemas.
      // If schemas take a long time to load you may cache them on your hard disk.
      // Then loading will take long only when you open a Session for the first time.
      // You can always refesh the cache either by using the Session's 'Refresh all' toolbar button
      // or by using the 'Refresh Item' right mouse menu on an Object tree node.]
      MultipleLineLabel lblHint = new MultipleLineLabel(s_stringMgr.getString("SchemaPropertiesPanel.hint"));
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      add(lblHint, gbc);

      // i18n[SchemaPropertiesPanel.loadAllAndCacheNone=Load all schemas, cache none]
      radLoadAllAndCacheNone = new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.loadAllAndCacheNone"));
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      add(radLoadAllAndCacheNone, gbc);

      // i18n[SchemaPropertiesPanel.loadAndCacheAll=Load all and cache all schemas]
      radLoadAndCacheAll= new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.loadAndCacheAll"));
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(radLoadAndCacheAll, gbc);

      // i18n[SchemaPropertiesPanel.specifySchemas=Specify schema loading and caching]
      radSpecifySchemas= new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.specifySchemas"));
      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(radSpecifySchemas, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radLoadAllAndCacheNone);
      bg.add(radLoadAndCacheAll);
      bg.add(radSpecifySchemas);


      // i18n[SchemaPropertiesPanel.refreshSchemas=Connect database to refresh schema table]
      btnUpdateSchemas = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.refreshSchemas"));
      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      add(btnUpdateSchemas, gbc);


      // i18n[SchemaPropertiesPanel.schemaTableTitle=Schema table]
      JLabel lblSchemaTableTitle = new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableTitle"));
      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      add(lblSchemaTableTitle, gbc);

      tblSchemas = new JTable();
      gbc = new GridBagConstraints(0,6,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5), 0,0);
      add(new JScrollPane(tblSchemas), gbc);



      // TODO do away
      JLabel lblDoAwy = new JLabel("Work in progress. GUI not yet functional.");
      lblDoAwy.setForeground(Color.red);
      gbc = new GridBagConstraints(0,7,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(lblDoAwy, gbc);


   }

}
