package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases.SQLAliasPropType;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;

public class SchemaPropertiesPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SchemaPropertiesPanel.class);

   private static final String PREF_KEY_SHOW_SCHEMA_FILTER = "SchemaPropertiesPanel.show.schema.filter";


   JRadioButton radLoadAllAndCacheNone;
   JRadioButton radLoadAndCacheAll;

   JRadioButton radSpecifySchemasByLikeString;
   JTextField txtSchemasByLikeStringInclude;
   JTextField txtSchemasByLikeStringExclude;

   JRadioButton radSpecifySchemas;
   JButton btnUpdateSchemasTable;
   JButton btnClearSchemasTable;
   JTable tblSchemas;

   JComboBox cboSchemaTableUpdateWhat;
   private JPanel schemaFilterPanel;
   SmallTabButton btnShowSchemaFilter;
   JTextField txtSchemaFilter = new JTextField();
   JComboBox cboSchemaTableUpdateTo;
   JButton btnSchemaTableUpdateApply;


   JCheckBox chkCacheSchemaIndepndentMetaData;

   JButton btnPrintCacheFileLocation;
   JButton btnDeleteCache;


   public SchemaPropertiesPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[SchemaPropertiesPanel.hint=Here you may pecify which Schemas to be loaded and displayed in a Session's Object tree.
      // Code completion and Syntax highlighting will work only for loaded schemas.
      // If Schemas take a long time to load you may cache them on your hard disk.
      // Then loading will take long only when you open a Session for the first time.
      // You can always refresh the cache either by using the Session's 'Refresh all' toolbar button
      // or by using the 'Refresh Item' right mouse menu on an Object tree node.]
      MultipleLineLabel lblHint = new MultipleLineLabel(s_stringMgr.getString("SchemaPropertiesPanel.hint"));
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      add(lblHint, gbc);

      // i18n[SchemaPropertiesPanel.loadAllAndCacheNone=Load all Schemas, cache none]
      radLoadAllAndCacheNone = new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.loadAllAndCacheNone"));
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      add(radLoadAllAndCacheNone, gbc);

      // i18n[SchemaPropertiesPanel.loadAndCacheAll=Load all and cache all Schemas]
      radLoadAndCacheAll= new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.loadAndCacheAll"));
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(radLoadAndCacheAll, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,2,5), 0,0);
      add(createSpecifySchemasByLikeStringPanel(), gbc);

      // i18n[SchemaPropertiesPanel.specifySchemas=Specify Schema loading and caching]
      radSpecifySchemas= new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.specifySchemas"));
      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      add(radSpecifySchemas, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radLoadAllAndCacheNone);
      bg.add(radLoadAndCacheAll);
      bg.add(radSpecifySchemasByLikeString);
      bg.add(radSpecifySchemas);


      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      add(createSchemaTableUpdateButtonPanel(), gbc);


      // i18n[SchemaPropertiesPanel.schemaTableTitle=Schema table]
      JLabel lblSchemaTableTitle = new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableTitle"));
      gbc = new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      add(lblSchemaTableTitle, gbc);

      tblSchemas = new JTable();
      tblSchemas.setAutoCreateRowSorter(true);
      gbc = new GridBagConstraints(0,7,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5), 0,0);
      add(new JScrollPane(tblSchemas), gbc);

      gbc = new GridBagConstraints(0,8,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5), 0,0);
      add(createSchemaTableUpdatePanel(), gbc);


      // i18n[SchemaPropertiesPanel.CacheSchemaIndependentMetaData=Cache Schema independent meta data (Catalogs, Keywords, Data types, Global functions)]
      chkCacheSchemaIndepndentMetaData = new JCheckBox(SQLAliasPropType.schemaProp_cacheSchemaIndependentMetaData.getI18nString());
      gbc = new GridBagConstraints(0,9,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      add(chkCacheSchemaIndepndentMetaData, gbc);


      gbc = new GridBagConstraints(0,10,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5), 0,0);
      add(createCacheFilePanel(), gbc);

   }

   private JPanel createSchemaTableUpdateButtonPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2,5,5));

      btnUpdateSchemasTable = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.refreshSchemas"));
      ret.add(btnUpdateSchemasTable);

      btnClearSchemasTable = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.clearSchemas"));
      ret.add(btnClearSchemasTable);

      return ret;
   }

   private JPanel createSpecifySchemasByLikeStringPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,0), 0,0);
      radSpecifySchemasByLikeString = new JRadioButton(s_stringMgr.getString("SchemaPropertiesPanel.specifySchemasByLikeString"));
      radSpecifySchemasByLikeString.setToolTipText(s_stringMgr.getString("SchemaPropertiesPanel.specifySchemasByLikeString.tooltip"));
      ret.add(radSpecifySchemasByLikeString, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(new SmallToolTipInfoButton(s_stringMgr.getString("SchemaPropertiesPanel.specifySchemasByLikeString.tooltip.long.html")).getButton(), gbc);


      JPanel pnlRight = new JPanel(new GridLayout(1,2,5,5));

      JPanel pnlInclude = new JPanel(new BorderLayout(5,5));
      pnlInclude.add(new JLabel(SQLAliasPropType.schemaProp_byLikeStringInclude.getI18nString()), BorderLayout.WEST);

      txtSchemasByLikeStringInclude = new JTextField();
      pnlInclude.add(txtSchemasByLikeStringInclude, BorderLayout.CENTER);

      pnlRight.add(pnlInclude);


      JPanel pnlExclude = new JPanel(new BorderLayout(5,5));
      pnlExclude.add(new JLabel(SQLAliasPropType.schemaProp_byLikeStringExclude.getI18nString()), BorderLayout.WEST);

      txtSchemasByLikeStringExclude = new JTextField();
      pnlExclude.add(txtSchemasByLikeStringExclude, BorderLayout.CENTER);

      pnlRight.add(pnlExclude);

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      ret.add(pnlRight, gbc);

      return ret;
   }


   private JPanel createCacheFilePanel()
   {
      JPanel ret = new JPanel();

      ret.setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);
      JLabel label = new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.deleteCacheNote"));
      label.setForeground(Color.red);
      ret.add(label, gbc);


      // i18n[SchemaPropertiesPanel.printCacheFileLocation=Print cache file path to message panel]
      btnPrintCacheFileLocation = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.printCacheFileLocation"));
      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,5,5), 0,0);
      ret.add(btnPrintCacheFileLocation, gbc);

      // i18n[SchemaPropertiesPanel.deleteCache=Delete cache file]
      btnDeleteCache = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.deleteCache"));
      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      ret.add(btnDeleteCache, gbc);

      return ret;
   }


   private JPanel createSchemaTableUpdatePanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      // i18n[SchemaPropertiesPanel.schemaTableUpdateLable1=Set]
      ret.add(new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateLable1")), gbc);

      cboSchemaTableUpdateWhat = new JComboBox();
      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      ret.add(cboSchemaTableUpdateWhat, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,0), 0,0);
      // i18n[SchemaPropertiesPanel.schemaTableUpdateLable2=in all Schemas to]
      schemaFilterPanel = createSchemaFilterPanel();
      ret.add(schemaFilterPanel, gbc);

      btnShowSchemaFilter = createSchemaFilterButton();
      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnShowSchemaFilter.addActionListener(a -> onBtnSmallPlusMinus());
      ret.add(btnShowSchemaFilter, gbc);

      cboSchemaTableUpdateTo = new JComboBox();
      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      ret.add(cboSchemaTableUpdateTo, gbc);

      // i18n[SchemaPropertiesPanel.schemaTableUpdateApply=Apply]
      btnSchemaTableUpdateApply = new JButton(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateApply"));
      gbc = new GridBagConstraints(5,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      ret.add(btnSchemaTableUpdateApply, gbc);


      return ret;
   }

   private JPanel createSchemaFilterPanel()
   {
      JPanel ret = new JPanel();
      updateSchemaFilterPanel(ret);
      return ret;
   }

   private void updateSchemaFilterPanel(JPanel ret)
   {
      ret.removeAll();
      if (isShowSchemaFilter())
      {
         ret.setLayout(new GridBagLayout());

         GridBagConstraints gbc;
         String toolTip = s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateLable2._begin.SchemaFilter.tooltip");

         gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
         JLabel lblBegin = new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateLable2._begin.SchemaFilter"));
         lblBegin.setToolTipText(toolTip);
         ret.add(lblBegin, gbc);

         gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,5),0,0);
         txtSchemaFilter.setPreferredSize(new Dimension(100, txtSchemaFilter.getPreferredSize().height));
         txtSchemaFilter.setToolTipText(toolTip);
         ret.add(txtSchemaFilter, gbc);

         gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
         ret.add(new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateLable2._end.SchemaFilter")), gbc);

         //ret.setBorder(BorderFactory.createLineBorder(Color.RED));
      }
      else
      {
         ret.setLayout(new GridLayout(1,1));
         ret.add(new JLabel(s_stringMgr.getString("SchemaPropertiesPanel.schemaTableUpdateLable2")));
      }
   }

   private void onBtnSmallPlusMinus()
   {
      Main.getApplication().getPropsImpl().put(PREF_KEY_SHOW_SCHEMA_FILTER, !isShowSchemaFilter());
      btnShowSchemaFilter.setIcon(getSmallPlusMinusIcon());
      updateSchemaFilterPanel(schemaFilterPanel);
      validate();
   }

   private SmallTabButton createSchemaFilterButton()
   {
      SmallTabButton ret = new SmallTabButton(s_stringMgr.getString("SchemaPropertiesPanel.clickToToggle.SchemaFilter"), getSmallPlusMinusIcon());
      Dimension size = new Dimension(16, 16);
      ret.setPreferredSize(size);
      ret.setMinimumSize(size);
      ret.setMaximumSize(size);
      return ret;
   }

   private ImageIcon getSmallPlusMinusIcon()
   {
      if (isShowSchemaFilter())
      {
         return Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_MINUS);
      }
      else
      {
         return Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SMALL_PLUS);
      }
   }

   private boolean isShowSchemaFilter()
   {
      return Main.getApplication().getPropsImpl().getBoolean(PREF_KEY_SHOW_SCHEMA_FILTER, false);
   }

   public String getSchemaFilter()
   {
      if (isShowSchemaFilter() && false == StringUtilities.isEmpty(txtSchemaFilter.getText(), false))
      {
         return txtSchemaFilter.getText();
      }

      return null;
   }
}
