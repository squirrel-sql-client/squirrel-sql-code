package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;

final class CreditsPanel extends JScrollPane
{
   private final static ILogger s_log = LoggerController.createLogger(CreditsPanel.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CreditsPanel.class);



   CreditsPanel(IApplication app)
   {
      setBorder(BorderFactory.createEmptyBorder());

      final JEditorPane credits = new JEditorPane();
      credits.setEditable(false);
      credits.setContentType("text/html");

      // Required with the first beta of JDK1.4.1 to stop
      // this scrollpane from being too tall.
      credits.setPreferredSize(new Dimension(200, 200));

      String creditsHtml = readCreditsHtml(app);

      StringBuffer pluginHtml = new StringBuffer();
      // Get list of all plugin developers names. Allow for multiple
      // developers for a plugin in the form "John Smith, James Brown".
      PluginInfo[] pi = app.getPluginManager().getPluginInformation();
      for (int i = 0; i < pi.length; ++i)
      {
         pluginHtml.append("<br><b>").append(pi[i].getDescriptiveName()).append(":</b>");

         String authors = pi[i].getAuthor();
         StringTokenizer strok = new StringTokenizer(authors, ",");
         while (strok.hasMoreTokens())
         {
            pluginHtml.append("<br>").append(strok.nextToken().trim());
         }
         String contribs = pi[i].getContributors();
         strok = new StringTokenizer(contribs, ",");
         while (strok.hasMoreTokens())
         {
            pluginHtml.append("<br>").append(strok.nextToken().trim());
         }


         pluginHtml.append("<br>");
      }

      creditsHtml = creditsHtml.replaceAll("@@replace", pluginHtml.toString());
      credits.setText(creditsHtml);

      setViewportView(credits);
      credits.setCaretPosition(0);
   }

   private String readCreditsHtml(IApplication app)
   {
      final URL url = app.getResources().getCreditsURL();
      StringBuffer buf = new StringBuffer(2048);

      if (url != null)
      {
         try
         {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(url.openStream()));
            try
            {
               String line = null;
               while ((line = rdr.readLine()) != null)
               {
                  String internationalizedLine =
                        Utilities.replaceI18NSpanLine(line, s_stringMgr);
                  buf.append(internationalizedLine);
               }
            }
            finally
            {
               rdr.close();
            }
         }
         catch (IOException ex)
         {
            // i18n[AboutBoxDialog.error.creditsfile=Error reading credits file]
            String errorMsg = s_stringMgr.getString("AboutBoxDialog.error.creditsfile");
            s_log.error(errorMsg, ex);
            buf.append(errorMsg + ": " + ex.toString());
         }
      }
      else
      {
         // i18n[AboutBoxDialog.error.creditsfileurl=Couldn't retrieve Credits File URL]
         String errorMsg = s_stringMgr.getString("AboutBoxDialog.error.creditsfileurl");
         s_log.error(errorMsg);
         buf.append(errorMsg);
      }
      return buf.toString();
   }

}
