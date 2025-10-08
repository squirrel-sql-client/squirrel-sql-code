package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.util.Properties;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This dialog allows the user to review and maintain
 * the properties for a JDBC driver.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverPropertiesController implements IAliasPropertiesPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DriverPropertiesController.class);

   private DriverPropertiesPanel _propsPnl;

   private SQLAlias _alias;
   String _errMsg;
   private Color _origTblColor;

   public DriverPropertiesController(SQLAlias alias)
   {
      _alias = alias;
      String aliasUrl = alias.getUrl();

      IIdentifier driverIdentifier = alias.getDriverIdentifier();
      if (driverIdentifier == null)
      {
         // I18n[DriverPropertiesController.noDriverSelected=No driver available in this Alias.\nCan not load driver properties tab.]
         _errMsg = s_stringMgr.getString("DriverPropertiesController.noDriverSelected");
         Main.getApplication().getMessageHandler().showErrorMessage(_errMsg);
         return;
      }
      final Driver jdbcDriver = Main.getApplication().getSQLDriverManager().getJDBCDriver(driverIdentifier);
      if (jdbcDriver == null)
      {
         // I18n[DriverPropertiesController.loadingDriverFailed=Loading JDBC driver "{0}" failed.\nCan not load driver properties tab.]
         _errMsg = s_stringMgr.getString("DriverPropertiesController.loadingDriverFailed", Main.getApplication().getAliasesAndDriversManager().getDriver(driverIdentifier).getName());
         Main.getApplication().getMessageHandler().showErrorMessage(_errMsg);
         return;
      }
      else
      {
         try
         {
            if (!jdbcDriver.acceptsURL(aliasUrl))
            {
               String driverName = Main.getApplication().getAliasesAndDriversManager().getDriver(driverIdentifier).getName();
               //I18n[DriverPropertiesController.invalidUrl=According to
               //the driver "{0}", the url "{1}" is invalid.]
               _errMsg = s_stringMgr.getString("DriverPropertiesController.invalidUrl",new String[]{driverName, aliasUrl});
               Main.getApplication().getMessageHandler().showErrorMessage(_errMsg);
               return;
            }
         }
         catch (Exception e)
         {
            // I18n[DriverPropertiesController.loadingDriverFailed=Loading JDBC driver "{0}" failed.\nCan not load driver properties tab.]
            _errMsg = s_stringMgr.getString("DriverPropertiesController.loadingDriverFailed", Main.getApplication().getAliasesAndDriversManager().getDriver(driverIdentifier).getName());
            Main.getApplication().getMessageHandler().showErrorMessage(_errMsg);
            return;
         }
      }

      DriverPropertyInfo[] infoAr = new DriverPropertyInfo[0];
      try
      {
         infoAr = jdbcDriver.getPropertyInfo(alias.getUrl(), new Properties());
      }
      catch (Exception e)
      {
         // I18n[DriverPropertiesController.gettingDriverPropetiesFailed=Loading the properties from the JDBC driver failed.\nCan not load driver properties tab.]
         _errMsg = s_stringMgr.getString("DriverPropertiesController.gettingDriverPropetiesFailed");
         Main.getApplication().getMessageHandler().showErrorMessage(_errMsg);
         //return;
      }

      SQLDriverPropertyCollection driverPropertiesClone = alias.getDriverPropertiesClone(true);
      driverPropertiesClone.applyDriverPropertyInfo(infoAr);
      _propsPnl = new DriverPropertiesPanel(driverPropertiesClone);

      _propsPnl.chkUseDriverProperties.setSelected(alias.getUseDriverProperties());
      updateTableEnabled();

      _propsPnl.chkUseDriverProperties.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            updateTableEnabled();
         }
      });


   }

   private void updateTableEnabled()
   {
      if(null == _origTblColor)
      {
         _origTblColor = _propsPnl.tblDriverProperties.getForeground();
      }

      _propsPnl.tblDriverProperties.setEnabled(_propsPnl.chkUseDriverProperties.isSelected());

      if(_propsPnl.chkUseDriverProperties.isSelected())
      {
         _propsPnl.tblDriverProperties.setForeground(_origTblColor);
      }
      else
      {
         _propsPnl.tblDriverProperties.setForeground(Color.lightGray);
      }
   }

   public Component getPanelComponent()
   {
      if(null == _propsPnl)
      {
         return new MultipleLineLabel(_errMsg);
      }
      else
      {
         return _propsPnl;
      }
   }


   public void applyChanges()
   {
      if (null != _propsPnl)
      {
         _alias.setDriverProperties(_propsPnl.getSQLDriverProperties());
         _alias.setUseDriverProperties(_propsPnl.chkUseDriverProperties.isSelected());
      }
   }


   public String getTitle()
   {
      return s_stringMgr.getString("DriverPropertiesController.title");
   }

   public String getHint()
   {
      return s_stringMgr.getString("DriverPropertiesController.Hint");
   }
}
