package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.TableFrameXmlBean;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.net.URL;


public class TableFrame extends JInternalFrame
{
   GraphTextAreaFactory txtColumsFactory;
   JScrollPane scrollPane;
   private MyUI _myUI;
   private Zoomer _zoomer;
   private ZoomerListener _zoomerListener;


   public TableFrame(String tableName, TableFrameXmlBean xmlBean, TableToolTipProvider toolTipProvider, Zoomer zoomer, DndCallback dndCallback, ISession session)
   {
      _zoomer = zoomer;

      scrollPane = new JScrollPane();
      scrollPane.setBorder(null);

      getContentPane().add(scrollPane);

      setMaximizable(false);
      setClosable(true);
      setIconifiable(false);

      setTitle(tableName);
      setBackground(new Color(255,255,204));

      setResizable(true);


      setFrameIcon(null);

      _myUI = new MyUI(this);
      setUI(_myUI);

      txtColumsFactory = new GraphTextAreaFactory(toolTipProvider, zoomer, dndCallback, session);
      scrollPane.setViewportView(txtColumsFactory.getComponent(zoomer.isEnabled()));
      
      if(null != xmlBean)
      {
         double zoom = _zoomer.getZoom();

         Rectangle r = new Rectangle();
         r.x = (int)(zoom*xmlBean.getX() + 0.5);
         r.y = (int)(zoom*xmlBean.getY() + 0.5);
         r.width = (int)(zoom*xmlBean.getWidht() + 0.5);
         r.height = (int)(zoom*xmlBean.getHeight() + 0.5);
         setBounds(r);
         setClosable(!_zoomer.isEnabled());
      }

      _zoomerListener = new ZoomerListener()
      {
         public void zoomChanged(double newZoom, double oldZoom, boolean adjusting)
         {
         }

         public void zoomEnabled(boolean b)
         {
            onZoomEnabled(b);
         }

         public void setHideScrollBars(boolean b)
         {
         }
      };


      setBorder(new LineBorder(Color.BLACK));
   }

   public void setVisible(boolean b)
   {
      if (null != _zoomer)
      {
         if (b)
         {
            _zoomer.addZoomListener(_zoomerListener);
            onZoomEnabled(_zoomer.isEnabled());
         }
         else
         {
            _zoomer.removeZoomListener(_zoomerListener);
         }
      }

      super.setVisible(b);
   }




   private void onZoomEnabled(boolean b)
   {
      scrollPane.setViewportView(txtColumsFactory.getComponent(_zoomer.isEnabled()));
      setClosable(!b);
   }

   public TableFrame.MyTitlePaneUI getTitlePane()
   {
      return _myUI.getTitlePane();
   }

   public TableFrameXmlBean getXmlBean()
   {
      TableFrameXmlBean ret = new TableFrameXmlBean();

      double zoom = _zoomer.getZoom();

      Rectangle bounds = getBounds();
      ret.setX((int)(bounds.x/zoom + 0.5));
      ret.setY((int)(bounds.y/zoom + 0.5));
      ret.setWidht((int)(bounds.width/zoom + 0.5));
      ret.setHeight((int)(bounds.height/zoom + 0.5));

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
      public static final int UNZOOMED_PREF_HEIGHT = 18;

      public MyTitlePaneUI(JInternalFrame f)
      {
         super(f);
      }


      protected void installDefaults()
      {
         super.installDefaults();
         URL resource = TableFrame.class.getResource("/net/sourceforge/squirrel_sql/plugins/graph/images/win_bigclose-rollover.gif");
         closeIcon = new ImageIcon(resource);
         selectedTextColor = Color.black;
         notSelectedTextColor = Color.black;
         setFont(new Font(getFont().getFontName(), Font.BOLD, getFont().getSize()));
      }

      protected void paintTitleBackground(Graphics g)
      {
         g.setColor(new Color(255,255,220));
         g.fillRect(0, 0, getWidth(), getHeight());
      }

      public void paintComponent(Graphics g)
      {
         paintTitleBackground(g);

         if (frame.getTitle() != null)
         {
            boolean isSelected = frame.isSelected();
            Font f = g.getFont();
            g.setFont(getFont());
            if (isSelected)
               g.setColor(selectedTextColor);
            else
               g.setColor(notSelectedTextColor);

            // Center text vertically.
            FontMetrics fm = g.getFontMetrics();

            double s = _zoomer.getZoom();
            int baseline = ((int)(getHeight()/s) + fm.getAscent() - fm.getLeading() - fm.getDescent()) / 2;

            int titleX;
            Rectangle r = new Rectangle(0, 0, 0, 0);
            if (frame.isIconifiable())
               r = iconButton.getBounds();
            else if (frame.isMaximizable())
               r = maxButton.getBounds();
            else if (frame.isClosable()) r = closeButton.getBounds();
            int titleW;

            String title = frame.getTitle();

            if (r.x == 0) r.x = frame.getWidth() - frame.getInsets().right;
            titleX = menuBar.getX() + menuBar.getWidth() + 2;
            titleW = (int)(  (r.x - titleX - 3)/_zoomer.getZoom() + 0.5  );
            title = getTitle(frame.getTitle(), fm, titleW);

            Graphics2D g2d = (Graphics2D) g;
            AffineTransform origTrans = g2d.getTransform();

            AffineTransform at = new AffineTransform(origTrans);
            at.scale(_zoomer.getZoom(), _zoomer.getZoom());
            g2d.setTransform(at);

            g.drawString(title, titleX, baseline);

            g2d.setTransform(origTrans);

            g.setFont(f);
         }
      }

      protected LayoutManager createLayout()
      {
         return new MyTitlePaneLayout();
      }


      public Dimension getPreferredSize()
      {
         Dimension ret = super.getPreferredSize();


         ret.height = (int) (UNZOOMED_PREF_HEIGHT * _zoomer.getZoom() + 0.5);
         return ret;
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

      class MyTitlePaneLayout extends BasicInternalFrameTitlePane.TitlePaneLayout
      {

         public Dimension minimumLayoutSize(Container c)
         {
            Dimension ret = super.minimumLayoutSize(c);
            ret.width *= _zoomer.getZoom();
            ret.height *= _zoomer.getZoom();
            return ret;
         }
      }

   }
}
