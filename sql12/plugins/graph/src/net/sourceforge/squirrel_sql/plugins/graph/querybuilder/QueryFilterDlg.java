package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.GraphPluginResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import net.sourceforge.squirrel_sql.fw.props.Props;

public class QueryFilterDlg extends JDialog
{

   private static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(QueryFilterDlg.class);
   private  static final int MIN_HEIGHT = 270;

   JTextField _txtFilter;
   JButton _btnOk;
   JButton _btnCancel;
   JButton _btnClearFilter;
   JCheckBox _chkApplyQuotes;
   JComboBox _cboOperator;
   JButton _btnEscapeDate;


   private static final String PREF_KEY_QUERY_FILTER_WIDTH = "Squirrel.queryFilterDlg.widht";
   private static final String PREF_KEY_QUERY_FILTER_HEIGHT = "Squirrel.queryFilterDlg.height";


   public QueryFilterDlg(Window parent, GraphPluginResources rsrc, String qualifiedColumn)
   {
      super(parent, s_stringMgr.getString("QueryFilterDlg.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15,5,20,5),0,0);
      JLabel lbl = new JLabel(s_stringMgr.getString("QueryFilterDlg.column", qualifiedColumn));
      lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
      getContentPane().add(lbl, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("QueryFilterDlg.operator")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _cboOperator = new JComboBox();
      getContentPane().add(_cboOperator, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("QueryFilterDlg.filterValue")), gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      getContentPane().add(createFilterPanel(rsrc), gbc);


      gbc = new GridBagConstraints(0,3,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(30,5,5,5),0,0);
      _chkApplyQuotes = new JCheckBox(s_stringMgr.getString("QueryFilterDlg.AlwaysAppendQuotes"));
      JPanel pnl = new JPanel(new GridLayout(1,1));
      pnl.add(_chkApplyQuotes);
      pnl.setBorder(BorderFactory.createLoweredBevelBorder());
      getContentPane().add(pnl, gbc);

      gbc = new GridBagConstraints(0,4,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      getContentPane().add(createButtonPanel(), gbc);

      gbc = new GridBagConstraints(0,5,2,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      getContentPane().add(new JPanel(), gbc);

      GUIUtils.enableCloseByEscape(this);

      getRootPane().setDefaultButton(_btnOk);

      int width = Props.getInt(PREF_KEY_QUERY_FILTER_WIDTH, 500);
      int height = Props.getInt(PREF_KEY_QUERY_FILTER_HEIGHT, MIN_HEIGHT);


      setSize(new Dimension(width, height));

   }

   private JPanel createFilterPanel(GraphPluginResources rsrc)
   {
      JPanel ret = new JPanel(new BorderLayout(5, 0));

      _txtFilter = new JTextField();
      ret.add(_txtFilter, BorderLayout.CENTER);

      _btnEscapeDate = new JButton(rsrc.getIcon(GraphPluginResources.IKeys.ESCAPE_DATE));
      _btnEscapeDate.setToolTipText(s_stringMgr.getString("QueryFilterDlg.btnEscapeDate"));
      _btnEscapeDate.setBorder(BorderFactory.createEmptyBorder());
      ret.add(_btnEscapeDate, BorderLayout.EAST);

      return ret;
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnOk = new JButton(s_stringMgr.getString("QueryFilterDlg.btnOK"));
      ret.add(_btnOk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnClearFilter = new JButton(s_stringMgr.getString("QueryFilterDlg.btnClearFilter"));
      ret.add(_btnClearFilter, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      _btnCancel = new JButton(s_stringMgr.getString("QueryFilterDlg.btnCancel"));
      ret.add(_btnCancel, gbc);
      return ret;
   }

   void saveCurrentSize()
   {
      Props.putInt(PREF_KEY_QUERY_FILTER_WIDTH, getSize().width);
      Props.putInt(PREF_KEY_QUERY_FILTER_HEIGHT, Math.max(getSize().height, MIN_HEIGHT));
   }

}
