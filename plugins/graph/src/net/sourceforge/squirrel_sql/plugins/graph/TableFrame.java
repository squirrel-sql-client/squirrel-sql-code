package net.sourceforge.squirrel_sql.plugins.graph;

import org.w3c.dom.css.CSS2Properties;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.TableFrameXmlBean;


public class TableFrame extends JInternalFrame
{
   JTextArea txtColums;
   JScrollPane scrollPane;
   private MyUI _myUI;
   private TableToolTipProvider _toolTipProvider;


   public TableFrame(String tableName, TableFrameXmlBean xmlBean, TableToolTipProvider toolTipProvider)
   {
      _toolTipProvider = toolTipProvider;
      txtColums = new JTextArea()
      {
         public String getToolTipText(MouseEvent event)
         {
            return _toolTipProvider.getToolTipText(event);
         }
      };


      txtColums.setToolTipText("Just to make getToolTiptext() to be called");


      scrollPane = new JScrollPane(txtColums);
      getContentPane().add(scrollPane);
      txtColums.setEditable(false);

      setMaximizable(false);
      setClosable(true);
      setIconifiable(false);

      setTitle(tableName);
      setBackground(new Color(255,255,204));

      setResizable(true);

      txtColums.setBackground(new Color(255,255,204));

      setFrameIcon(null);

      _myUI = new MyUI(this);
      setUI(_myUI);

      if(null != xmlBean)
      {
         Rectangle r = new Rectangle();
         r.x = xmlBean.getX();
         r.y = xmlBean.getY();
         r.width = xmlBean.getWidht();
         r.height = xmlBean.getHeight();
         setBounds(r);
      }

   }

   public TableFrame.MyTitlePaneUI getTitlePane()
   {
      return _myUI.getTitlePane();
   }

   public TableFrameXmlBean getXmlBean()
   {
      TableFrameXmlBean ret = new TableFrameXmlBean();

      Rectangle bounds = getBounds();
      ret.setX(bounds.x);
      ret.setY(bounds.y);
      ret.setWidht(bounds.width);
      ret.setHeight(bounds.height);

      return ret;

   }

   class MyUI extends BasicInternalFrameUI
   {
      public MyUI(JInternalFrame frame)
      {
         super(frame);
      }


      protected JComponent createNorthPane(JInternalFrame w)
      {
         titlePane = new MyTitlePaneUI(w);
         return titlePane;
      }

      public TableFrame.MyTitlePaneUI getTitlePane()
      {
         return (MyTitlePaneUI) _myUI.titlePane;
      }


   }

   class MyTitlePaneUI extends BasicInternalFrameTitlePane
   {
      public MyTitlePaneUI(JInternalFrame f)
      {
         super(f);
      }


      protected void installDefaults()
      {
         super.installDefaults();

         selectedTextColor = Color.black;
         notSelectedTextColor = Color.black;
         //setFont(new Font("Tahoma", Font.BOLD, 11));
         setFont(new Font(getFont().getFontName(), Font.BOLD, getFont().getSize()));
      }

      protected void paintTitleBackground(Graphics g)
      {
         g.setColor(new Color(255,255,220));
         g.fillRect(0, 0, getWidth(), getHeight());
      }


      /**
       * This removes the system menu
       * @return
       */
      protected JMenuBar createSystemMenuBar()
      {
         menuBar = new JMenuBar()
         {
            public void setSize(int width, int height)
            {
               super.setSize(0,0);
            }

            public void setBounds(int x, int y, int width, int height)
            {
               super.setBounds(0, 0, 0, 0);
            }
         };
         menuBar.setBorderPainted(false);
         menuBar.setSize(0,0);
         menuBar.setBounds(0,0,0,0);
         return menuBar;
      }


   }
}
