package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

public class CliProgressAbortCallback implements ProgressAbortCallback
{
   @Override
   public void setTaskStatus(String status)
   {

   }

   @Override
   public void setFinished()
   {

   }

   @Override
   public boolean isUserCanceled()
   {
      return false;
   }

   @Override
   public boolean isVisible()
   {
      return false;
   }

   @Override
   public void currentlyLoading(String simpleName)
   {

   }

   @Override
   public void setLoadingPrefix(String loadingPrefix)
   {

   }

   @Override
   public void setVisible(boolean b)
   {

   }

   @Override
   public void setTotalItems(int totalItems)
   {

   }

   @Override
   public boolean finishedLoading()
   {
      return false;
   }

   @Override
   public void dispose()
   {

   }
}
