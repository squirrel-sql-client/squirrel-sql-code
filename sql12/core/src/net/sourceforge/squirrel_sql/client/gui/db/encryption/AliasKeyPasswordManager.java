package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import javax.crypto.BadPaddingException;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.AliasInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.modifyaliases.AliasesBackUp;
import net.sourceforge.squirrel_sql.client.gui.db.modifyaliases.AliasesBackupCallback;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetAdapter;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.WidgetEvent;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

public class AliasKeyPasswordManager
{
   private static ILogger s_log = LoggerController.createLogger(AliasKeyPasswordManager.class);
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasKeyPasswordManager.class);


   public static final String PREF_USE_KEY_PASSWORD = "db.encryption.AliasKeyPasswordManager.PREF_USE_KEY_PASSWORD";

   /**
    * Never change the content of this constant
    */
   public static final String KEY_PASSWORD_CORRECTNESS_CHECK_STRING = "correctnessVerificationString";
   public static final String PREF_CURRENT_ALIAS_KEY_PASSWORD_ENCRYPTED_CHECK_STRING = "db.encryption.AliasKeyPasswordManager.PREF_CURRENT_ALIAS_KEY_PASSWORD_ENCRYPTED_CHECK_STRING";

   private String _keyPassword;

   private HashSet<AliasInternalFrame> _openAliasFrames = new HashSet<>();
   private WidgetAdapter _aliasInternalFrameListener;

   public AliasKeyPasswordManager()
   {
      _aliasInternalFrameListener = new WidgetAdapter()
      {
         @Override
         public void widgetClosed(WidgetEvent evt)
         {
            _openAliasFrames.remove(evt.getWidget());
         }
      };
   }

   public boolean isUseKeyPassword()
   {
      return Props.getBoolean(PREF_USE_KEY_PASSWORD, false);
   }

   public String getKeyPassword()
   {
      if(false == isUseKeyPassword())
      {
         throw new IllegalStateException("Key password is not active. Call isUseKeyPassword() before calling this method");
      }

      if(null == _keyPassword)
      {
         MissingKeyPasswordException missingKeyPasswordException = new MissingKeyPasswordException();
         Main.getApplication().getMessageHandler().showErrorMessage(missingKeyPasswordException.getMessage());
         throw missingKeyPasswordException;
      }

      return _keyPassword;
   }

   public boolean isLoggedIn()
   {
      return isUseKeyPassword() && null != _keyPassword;
   }

   public void logout()
   {
      _keyPassword = null;
   }

   public void setKeyPassword(String keyPassword)
   {
      _keyPassword = keyPassword;
   }

   public void initKeyPassword(String keyPassword)
   {
      File backupFile = _changeKeyPassword(PasswordEncryption.k(), keyPassword);

      Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasKeyPasswordManager.alias.passwords.key.password.encrypted", backupFile.getAbsolutePath()));
      _keyPassword = keyPassword;
   }

   public void changeKeyPassword(String currentKeyPassword, String newKeyPassword)
   {
      File backupFile = _changeKeyPassword(currentKeyPassword, newKeyPassword);

      Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("AliasKeyPasswordManager.alias.passwords.key.password.encrypted", backupFile.getAbsolutePath()));
      _keyPassword = newKeyPassword;
   }


   private static File _changeKeyPassword(String fromKeyPassword, String toKeyPassword)
   {
      File backupFile = backupAliases();

      Props.putBoolean(PREF_USE_KEY_PASSWORD, true);
      Props.putString(PREF_CURRENT_ALIAS_KEY_PASSWORD_ENCRYPTED_CHECK_STRING, PasswordEncryption.encrypt(KEY_PASSWORD_CORRECTNESS_CHECK_STRING, toKeyPassword));

      List<? extends SQLAlias> aliasList = Main.getApplication().getAliasesAndDriversManager().getAliasList();

      aliasList.stream()
               .filter(a -> a.isEncryptPassword() && false == StringUtils.isBlank(a.getPassword()))
               .forEach(a -> a.setPassword(PasswordEncryption.encrypt(PasswordEncryption.decryptKeyTrusted(a.getPassword(), fromKeyPassword), toKeyPassword)));

      Main.getApplication().saveAliases();
      return backupFile;
   }


   public void removeKeyPassword(String keyPassword)
   {
      File backupFile = backupAliases();

      List<? extends SQLAlias> aliasList = Main.getApplication().getAliasesAndDriversManager().getAliasList();

      aliasList.stream()
               .filter(a -> a.isEncryptPassword() && false == StringUtils.isBlank(a.getPassword()))
               .forEach(a -> a.setPassword(PasswordEncryption.encrypt(PasswordEncryption.decryptKeyTrusted(a.getPassword(), keyPassword), PasswordEncryption.k())));

      Props.putBoolean(PREF_USE_KEY_PASSWORD, false);
      Props.putString(PREF_CURRENT_ALIAS_KEY_PASSWORD_ENCRYPTED_CHECK_STRING, null);

      Main.getApplication().saveAliases();
      Main.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("AliasKeyPasswordManager.alias.passwords.key.password.encryption.removed", backupFile.getAbsolutePath()));

   }

   public boolean verifyKeyPassword(String keyPassword)
   {
      if(false == isUseKeyPassword())
      {
         throw new IllegalStateException("Key password is not active. Call isUseKeyPassword() before calling this method");
      }

      String encryptedCheckString = Props.getString(PREF_CURRENT_ALIAS_KEY_PASSWORD_ENCRYPTED_CHECK_STRING, null);

      if(null == encryptedCheckString)
      {
         throw new IllegalStateException("How can the decrypted check String be empty when isUseKeyPassword() == true?");
      }

      try
      {
         String decryptedCheckString = PasswordEncryption.decrypt(encryptedCheckString, keyPassword);
         return StringUtils.equals(decryptedCheckString, KEY_PASSWORD_CORRECTNESS_CHECK_STRING);
      }
      catch(BadPaddingException e)
      {
         s_log.warn("Wrong password cause password decryptor to raise exception", e);
         return false;
      }
   }

   public void aliasFrameOpened(AliasInternalFrame aliasInternalFrame)
   {
      _openAliasFrames.add(aliasInternalFrame);
      aliasInternalFrame.addWidgetListener(_aliasInternalFrameListener);
   }

   public boolean hasOpenAliasFrames()
   {
      return ! _openAliasFrames.isEmpty();
   }

   private static File backupAliases()
   {
      return AliasesBackUp.backupAliases(new AliasesBackupCallback()
      {
         @Override
         public void setStatus(String status)
         {
            s_log.info(status);
         }

         @Override
         public void cleanUp() {}
      });
   }
}
