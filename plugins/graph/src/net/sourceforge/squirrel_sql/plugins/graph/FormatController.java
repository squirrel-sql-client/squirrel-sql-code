package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FormatXmlBean;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;


public class FormatController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FormatController.class);


	private FormatDlg _dlg;
   private FormatXmlBean[] _formats;
   private ISession _session;
   private GraphPlugin _plugin;
   private String FORMAT_XML_FILE_NAME = "formats.xml";

   private FormatControllerListener _listener;
   private JPopupMenu m_lstPopup;
   private Unit m_currentUnit;

   public FormatController(ISession session, GraphPlugin plugin, FormatControllerListener listener)
   {
      try
      {
         _plugin = plugin;
         _session = session;
         _listener = listener;

         String userSettingsFolder = _plugin.getPluginUserSettingsFolder().getPath();

         File f = new File(userSettingsFolder + File.separator + FORMAT_XML_FILE_NAME);

         if(f.exists())
         {
            XMLBeanReader br = new XMLBeanReader();
            br.load(userSettingsFolder + File.separator + FORMAT_XML_FILE_NAME, this.getClass().getClassLoader());

            Vector<FormatXmlBean> buf = new Vector<FormatXmlBean>();

            for(Iterator<?> i=br.iterator(); i.hasNext();)
            {
               buf.add((FormatXmlBean)i.next());
            }

            _formats = buf.toArray(new FormatXmlBean[buf.size()]);
         }

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onSave()
   {


      if(null == _dlg.txtName.getText() || "".equals(_dlg.txtName.getText().trim()))
      {
			// i18n[graph.invalidName=Invalid name]
			JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("graph.invalidName"));
         return;
      }
      String name = _dlg.txtName.getText().trim();

      Unit selUnit = (Unit) _dlg.cboUnit.getSelectedItem();

      double height;
      double width;

      String buf;
      buf = _dlg.txtHeight.getText();
      try
      {
         height = Double.parseDouble(buf) * selUnit.getInCm();
      }
      catch (NumberFormatException e)
      {
			// i18n[graph.invalidHeight=Invalid height]
			JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("graph.invalidHeight"));
         return;
      }

      buf = _dlg.txtWidth.getText();
      try
      {
         width = Double.parseDouble(buf) * selUnit.getInCm();
      }
      catch (NumberFormatException e)
      {
			// i18n[graph.invalidWidth=Invalid width]
			JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("graph.invalidWidth"));
         return;
      }

      boolean isLandscape = _dlg.chkIsLandscape.isSelected();

      FormatXmlBean selBean = (FormatXmlBean) _dlg.lstFormats.getSelectedValue();

      if(null == selBean)
      {
         selBean = new FormatXmlBean(name, width, height, false,isLandscape);
         Vector<FormatXmlBean> v = new Vector<FormatXmlBean>();
         v.addAll(Arrays.asList(_formats));
         v.add(selBean);
         _formats = v.toArray(new FormatXmlBean[v.size()]);

         _dlg.lstFormats.setListData(_formats);
         _dlg.lstFormats.setSelectedValue(selBean, true);
      }
      else
      {
         selBean.setName(name);
         selBean.setWidth(width);
         selBean.setHeight(height);
         selBean.setLandscape(isLandscape);
         _dlg.lstFormats.repaint();
      }

      _listener.formatsChanged((FormatXmlBean)_dlg.lstFormats.getSelectedValue());
      saveFormats();
   }

   private void saveFormats()
   {
      try
      {
         String userSettingsFolder = _plugin.getPluginUserSettingsFolder().getPath();
         XMLBeanWriter bw = new XMLBeanWriter();
         for (int i = 0; i < _formats.length; i++)
         {
            bw.addToRoot(_formats[i]);
         }
         bw.save(userSettingsFolder + File.separator + FORMAT_XML_FILE_NAME);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onNew()
   {
      _dlg.lstFormats.clearSelection();
   }

   private void onListSelectionChanged(ListSelectionEvent e)
   {
      if(false == e.getValueIsAdjusting())
      {
         updateRightSideControls();
      }
   }

   private void updateRightSideControls()
   {
      FormatXmlBean selBean =(FormatXmlBean) _dlg.lstFormats.getSelectedValue();

      if(null == selBean)
      {
         _dlg.txtName.setText(null);
         _dlg.txtHeight.setText(null);
         _dlg.txtWidth.setText(null);
      }
      else
      {
         _dlg.txtName.setText(selBean.getName());
         _dlg.chkIsLandscape.setSelected(selBean.isLandscape());

         Unit unit = (Unit) _dlg.cboUnit.getSelectedItem();


         if(_dlg.cboUnit.getSelectedItem() == Unit.UNIT_CM)
         {
            _dlg.txtHeight.setText("" + selBean.getHeight());
            _dlg.txtWidth.setText("" + selBean.getWidth());
         }
         else
         {
            _dlg.txtHeight.setText("" +  selBean.getHeight() / unit.getInCm());
            _dlg.txtWidth.setText("" + selBean.getWidth() / unit.getInCm());
         }
      }
   }

   private FormatXmlBean[] getDefaultFormats()
   {
      return new
         FormatXmlBean[]
      {
			// i18n[graph.dina3=Din A 3]
			new FormatXmlBean(s_stringMgr.getString("graph.dina3"), 29.7, 42.0, false, false),
			// i18n[graph.dina4=Din A 4]
         new FormatXmlBean(s_stringMgr.getString("graph.dina4"), 21.0, 29.7, false, false),
			// i18n[graph.dina5=Din A 5]
         new FormatXmlBean(s_stringMgr.getString("graph.dina5"), 14.8, 21.0, true, false)
      };
   }

   public void setVisible(boolean b)
   {
      if (null == _dlg)
      {
         _dlg = new FormatDlg(_session.getApplication().getMainFrame());

         if(null == _formats)
         {
            _formats = getDefaultFormats();
         }

         _dlg.lstFormats.setListData(_formats);


         _dlg.cboUnit.addItem(Unit.UNIT_CM);
         _dlg.cboUnit.addItem(Unit.UNIT_INCH);
          m_currentUnit = Unit.UNIT_CM;
         _dlg.cboUnit.setSelectedItem(Unit.UNIT_CM);

         _dlg.lstFormats.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

         _dlg.lstFormats.addListSelectionListener(new ListSelectionListener()
         {
            public void valueChanged(ListSelectionEvent e)
            {
               onListSelectionChanged(e);
            }
         });



         _dlg.lstFormats.addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent e)
            {
               maybeShowListPopUp(e);
            }

            public void mouseReleased(MouseEvent e)
            {
               maybeShowListPopUp(e);
            }
         });

         m_lstPopup = new JPopupMenu();

			// i18n[graph.mnuDelete=delete]
			JMenuItem mnuDeleteFomat = new JMenuItem(s_stringMgr.getString("graph.mnuDelete"));
         mnuDeleteFomat.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onDeleteSeletedListItems();
            }
         });
         m_lstPopup.add(mnuDeleteFomat);

			// i18n[graph.landscape=landscape]
			JMenuItem mnuLandscape = new JMenuItem(s_stringMgr.getString("graph.landscape"));
         mnuLandscape.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onLandscape();
            }
         });
         m_lstPopup.add(mnuLandscape);

         _dlg.btnNew.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onNew();
            }
         });

         _dlg.btnSave.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onSave();
            }
         });

         _dlg.addWindowListener(new WindowAdapter()
         {
            public void windowClosing(WindowEvent e)
            {
               onWindowClosing();
            }
         });

         _dlg.cboUnit.addItemListener(new ItemListener()
         {
            public void itemStateChanged(ItemEvent e)
            {
               onUnitChanged(e);
            }
         });

      }

      GUIUtils.centerWithinParent(_dlg);

      _dlg.setVisible(b);
   }

   private void onLandscape()
   {
      FormatXmlBean selBean = (FormatXmlBean) _dlg.lstFormats.getSelectedValue();

      if(null == selBean)
      {
         return;
      }

      FormatXmlBean lsBean = new FormatXmlBean(selBean.getName() + " (LS)", selBean.getHeight(), selBean.getWidth(), false, true);

      Vector<FormatXmlBean> v = new Vector<FormatXmlBean>();
      v.addAll(Arrays.asList(_formats));
      v.add(lsBean);
      _formats = v.toArray(new FormatXmlBean[v.size()]);

      _dlg.lstFormats.setListData(_formats);
      _dlg.lstFormats.setSelectedValue(lsBean, true);

      _listener.formatsChanged((FormatXmlBean)_dlg.lstFormats.getSelectedValue());

      _dlg.lstFormats.repaint();

   }

   private void onDeleteSeletedListItems()
   {
      Object[] selFormats = _dlg.lstFormats.getSelectedValues();

      Vector<FormatXmlBean> remainFormats = new Vector<FormatXmlBean>();
      for (int i = 0; i < _formats.length; i++)
      {
         boolean found = false;
         for (int j = 0; j < selFormats.length; j++)
         {
            if(_formats[i] == selFormats[j])
            {
               found = true;
               break;
            }
         }
         if(false == found)
         {
            remainFormats.add(_formats[i]);
         }
      }


      if(0 == remainFormats.size())
      {
         _formats = getDefaultFormats();
      }
      else
      {
         _formats = remainFormats.toArray(new FormatXmlBean[remainFormats.size()]);
      }

      saveFormats();

      _dlg.lstFormats.setListData(_formats);


   }

   private void maybeShowListPopUp(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {
         m_lstPopup.show(e.getComponent(), e.getX(), e.getY());
      }
   }

   private void onUnitChanged(ItemEvent e)
   {
      if(ItemEvent.SELECTED == e.getStateChange())
      {
         double widht;
         double height;

         Unit selUnit = (Unit) _dlg.cboUnit.getSelectedItem();

         if(selUnit == m_currentUnit)
         {
            return;
         }


         try
         {
            widht = Double.parseDouble(_dlg.txtWidth.getText());
            height = Double.parseDouble(_dlg.txtHeight.getText());
         }
         catch (NumberFormatException e1)
         {
				// i18n[graph.invalidNumberFormat=Invalid number format. Can not calculate new unit.]
				JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("graph.invalidNumberFormat"));

            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  _dlg.cboUnit.setSelectedItem(m_currentUnit);
               }
            });

            return;
         }


         widht *=  m_currentUnit.getInCm() / selUnit.getInCm();
         height *=  m_currentUnit.getInCm() / selUnit.getInCm();
         _dlg.txtWidth.setText("" + widht);
         _dlg.txtHeight.setText("" + height);

         m_currentUnit = selUnit;
      }
   }

   private void onWindowClosing()
   {
      _listener.formatsChanged((FormatXmlBean)_dlg.lstFormats.getSelectedValue());
      saveFormats();
   }

   public FormatXmlBean[] getFormats()
   {
      if(null == _formats)
      {
        _formats = getDefaultFormats();
      }

      return _formats;
   }

   public void close()
   {
      if(null != _dlg)
      {
         saveFormats();
         _dlg.setVisible(false);
         _dlg.dispose();
      }

   }


   public static class Unit
   {
		// i18n[graph.cm=cm]
		public static final Unit UNIT_CM = new Unit(s_stringMgr.getString("graph.cm"), 1);
		// i18n[graph.inch=inch]
      public static final Unit UNIT_INCH = new Unit(s_stringMgr.getString("graph.inch"), 2.54);

      private String _name;
      private double _inCm;

      private Unit(String name, double inCm)
      {
         _name = name;
         _inCm = inCm;
      }

      public String getName()
      {
         return _name;
      }

      public double getInCm()
      {
         return _inCm;
      }
      
      public String toString()
      {
         return _name;
      }
   }


}
