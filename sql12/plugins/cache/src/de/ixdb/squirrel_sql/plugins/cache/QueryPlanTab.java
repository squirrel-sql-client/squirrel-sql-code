package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.*;


public class QueryPlanTab implements IMainPanelTab
{
   private QueryPlanPanel _queryPlanPanel;
   private QueryPlanTabListener _queryPlanTabListener;

   QueryPlanTab(String executionPlanHtml, QueryPlanTabListener queryPlanTabListener)
   {
      _queryPlanTabListener = queryPlanTabListener;


      _queryPlanPanel = new QueryPlanPanel();
      _queryPlanPanel.txtQueryPlan.setContentType("text/html");
      _queryPlanPanel.txtQueryPlan.addHyperlinkListener(new HyperlinkListener()
      {
         public void hyperlinkUpdate(HyperlinkEvent e)
         {
            onHyperlinkUpdate(e);
         }
      });
      _queryPlanPanel.txtQueryPlan.setEditable(false);
      _queryPlanPanel.txtQueryPlan.setText(executionPlanHtml);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            _queryPlanPanel.txtQueryPlan.scrollRectToVisible(new Rectangle(0,0,1,1));
         }
      });

   }

   private void onHyperlinkUpdate(HyperlinkEvent e)
   {
      if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
      {
         if(ShowQueryPlanAction.HREF_CLOSE_QUERY_PLAN.equals(e.getDescription()))
         {
            _queryPlanTabListener.closeRequested();

         }
         else
         {
            _queryPlanPanel.txtQueryPlan.scrollToReference(e.getDescription());
         }
      }
   }

   public String getTitle()
   {
      return "Query plan";
   }

   public String getHint()
   {
      return getTitle();
   }

   public Component getComponent()
   {
      return _queryPlanPanel;
   }

   public void setSession(ISession session)
   {
   }

   public void sessionClosing(ISession session)
   {
   }

   public void select()
   {
   }


}
