package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.*;

import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ObjectTreeCellRenderer extends DefaultTreeCellRenderer
{
   private ObjectTreeModel _model;
   private ISession _session;

   private JPanel _pnlFilterRootNodeRendererComponent;
   private JLabel _filterHint;

   GridBagConstraints _gbcLeft = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
   GridBagConstraints _gbcRight = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);

   public ObjectTreeCellRenderer(ObjectTreeModel model, ISession session)
   {

      _model = model;
      _session = session;

      _session.getProperties().addPropertyChangeListener(new PropertyChangeListener()
      {
         public void propertyChange(PropertyChangeEvent evt)
         {
            onPropertyChanged(evt);
         }
      });

      initFilter();

      _pnlFilterRootNodeRendererComponent = new JPanel(new GridBagLayout());

   }


   private void onPropertyChanged(PropertyChangeEvent evt)
   {
      if(
         SessionProperties.IPropertyNames.OBJECT_FILTER_INCLUDE.equals(evt.getPropertyName()) ||
         SessionProperties.IPropertyNames.OBJECT_FILTER_EXCLUDE.equals(evt.getPropertyName()) 

         )
      {
         initFilter();
      }
   }

   private void initFilter()
   {
      String objectFilterInclude = _session.getProperties().getObjectFilterInclude();
      String objectFilterExclude = _session.getProperties().getObjectFilterExclude();

      String filterhint = "";

      if(false == StringUtils.isEmpty(objectFilterInclude))
      {
         filterhint += "Inc:" + objectFilterInclude;
      }

      if(false == StringUtils.isEmpty(objectFilterExclude))
      {
         if(0 == filterhint.length())
         {
            filterhint += "Exc:" + objectFilterExclude;
         }
         else
         {
            filterhint += "; Exc:" + objectFilterExclude;
         }
      }



      if(0 < filterhint.length())
      {

         _filterHint = new JLabel(filterhint);
         final SquirrelResources rsrc = _session.getApplication().getResources();
         final ImageIcon icon = rsrc.getIcon(SquirrelResources.IImageNames.FILTER);
         _filterHint.setIcon(icon);
      }
      else
      {
         _filterHint = null;
      }

      _model.nodeChanged(_model.getRootObjectTreeNode());
   }




   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
   {
      JLabel ret = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

      if(null != _filterHint && _model.isRootNode(value))
      {
         _pnlFilterRootNodeRendererComponent.removeAll();
         _pnlFilterRootNodeRendererComponent.setBackground(new Color(255,204,204));

         _pnlFilterRootNodeRendererComponent.add(ret, _gbcLeft);
         _pnlFilterRootNodeRendererComponent.add(ret);

         _pnlFilterRootNodeRendererComponent.add(_filterHint, _gbcRight);
         _pnlFilterRootNodeRendererComponent.add(_filterHint);

         return _pnlFilterRootNodeRendererComponent;

      }
      else
      {
         if(value instanceof ObjectTreeNode)
         {
            ObjectTreeNode otn = (ObjectTreeNode) value;
            if (null != otn.getIcon())
            {
               ret.setIcon(otn.getIcon());
            }
            else
            {
               setDefaultIcon(expanded, leaf, ret);
            }

         }
         else
         {
            setDefaultIcon(expanded, leaf, ret);
         }

         return ret;
      }
   }

   private void setDefaultIcon(boolean expanded, boolean leaf, JLabel lbl)
   {
      if (leaf)
      {
         lbl.setIcon(getLeafIcon());
      }
      else if (expanded)
      {
         lbl.setIcon(getDefaultOpenIcon());
      }
      else
      {
         lbl.setIcon(getDefaultClosedIcon());
      }
   }
}
