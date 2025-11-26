package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class PasswordEncryption
{
   private static final String UNICODE_FORMAT = "UTF8";
   public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

   static String encrypt(String toEncrypt)
   {
      return encrypt(toEncrypt, getKeyPassword());
   }

   static String encrypt(String toEncrypt, String keyPassword)
   {
      try
      {
         Cipher cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME);
         cipher.init(Cipher.ENCRYPT_MODE, createKey(keyPassword));
         byte[] plainText = toEncrypt.getBytes(UNICODE_FORMAT);
         byte[] encryptedText = cipher.doFinal(plainText);
         return new String(Base64.encodeBase64(encryptedText));
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   static String decrypt(String encryptedString)
   {
      try
      {
         return decrypt(encryptedString, getKeyPassword());
      }
      catch(BadPaddingException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   static String decryptKeyTrusted(String encryptedString, String keyPassword)
   {
      try
      {
         return decrypt(encryptedString, keyPassword);
      }
      catch(BadPaddingException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   static String decrypt(String encryptedString, String keyPassword) throws BadPaddingException
   {
      try
      {
         Cipher cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME);
         cipher.init(Cipher.DECRYPT_MODE, createKey(keyPassword));
         byte[] encryptedStringAsBase64Bytes = Base64.decodeBase64(encryptedString);
         byte[] plainText = cipher.doFinal(encryptedStringAsBase64Bytes);
         return new String(plainText);
      }
      catch (BadPaddingException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static SecretKey createKey(String keyPassword)
   {
      try
      {
         byte[] arrayBytes = makeValidDesEdeKey(keyPassword).getBytes("UTF8");
         KeySpec ks = new DESedeKeySpec(arrayBytes);
         return  SecretKeyFactory.getInstance("DESede").generateSecret(ks);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static String getKeyPassword()
   {
      if(Main.getApplication().getAliasKeyPasswordManager().isUseKeyPassword())
      {
         return Main.getApplication().getAliasKeyPasswordManager().getKeyPassword();
      }
      else
      {
         return k();
      }
   }

   public static String k()
   {
      return "IntorducdeInVersion422ForStabillityRaesons";
   }

   private static String makeValidDesEdeKey(String key)
   {
      return StringUtils.rightPad(StringUtils.abbreviate(key, "", DESedeKeySpec.DES_EDE_KEY_LEN), DESedeKeySpec.DES_EDE_KEY_LEN, '#');
   }


   public static void main(String args[]) throws BadPaddingException
   {
      String target = "imparator";
      String encrypted = encrypt(target, k());
      String decrypted = decrypt(encrypted, k());

      System.out.println("String To Encrypt: " + target);
      System.out.println("Encrypted String:" + encrypted);
      System.out.println("Decrypted String:" + decrypted);
   }
}

