package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import java.awt.*;

class DataScaleLayout extends GridLayout
{

   private Dimension buf = new Dimension();

   private double[] _renormedWeights;

   public DataScaleLayout(double[] weights)
   {
      super(1, weights.length);
      createRenormedWeights(weights);
   }

   /**
    * Renorming is done to make sure very small intervals become visible. 
    */
   private void createRenormedWeights(double[] weights)
   {
      double[] buf = new double[weights.length];


      double sumRenormed = 0;
      for (int i = 0; i < weights.length; i++)
      {
         //System.out.println("weight = " + weights[i]);

         buf[i] = Math.max(weights[i], 1d/60d);
         sumRenormed += buf[i];
      }

      _renormedWeights = new double[buf.length];
      for (int i = 0; i < buf.length; i++)
      {
         _renormedWeights[i] = buf[i] / sumRenormed;

         //System.out.println("_renormedWeight = " + _renormedWeights[i]);
      }
   }


   @Override
   public void layoutContainer(Container parent)
   {
      synchronized (parent.getTreeLock())
      {
         Insets insets = parent.getInsets();

         if (_renormedWeights.length == 0)
         {
            return;
         }

         Dimension pSize = parent.getSize(buf);

         int pw = pSize.width - (insets.left + insets.right);
         int h = pSize.height - (insets.top + insets.bottom);

         int y = insets.top;
         int x = insets.left;


         int pwLeftOver = pw;
         for (int i = 0; i < _renormedWeights.length; i++)
         {
            if ( i < _renormedWeights.length - 1)
            {
               int width = (int) (_renormedWeights[i] * (double) pw + 0.5);
               parent.getComponent(i).setBounds(x, y, width, h);
               x += width;

               pwLeftOver -= width;
            }
            else
            {
               parent.getComponent(i).setBounds(x, y, pwLeftOver, h);
            }
         }
      }
   }
}
