package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.spec.KeySpec;

public class PasswordEncryption
{
   private static final String UNICODE_FORMAT = "UTF8";
   public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

   static String encrypt(String unencryptedString)
   {
      try
      {
         Cipher cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME);
         cipher.init(Cipher.ENCRYPT_MODE, createKey());
         byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
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
         Cipher cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME);
         cipher.init(Cipher.DECRYPT_MODE, createKey());
         byte[] encryptedText = Base64.decodeBase64(encryptedString);
         byte[] plainText = cipher.doFinal(encryptedText);
         return new String(plainText);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static SecretKey createKey()
   {
      try
      {
         String k = "IntorducdeInVersion422ForStabillityRaesons";
         byte[] arrayBytes = k.getBytes("UTF8");
         KeySpec ks = new DESedeKeySpec(arrayBytes);
         return  SecretKeyFactory.getInstance("DESede").generateSecret(ks);
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   public static void main(String args[])
   {
      String target = "imparator";
      String encrypted = encrypt(target);
      String decrypted = decrypt(encrypted);

      System.out.println("String To Encrypt: " + target);
      System.out.println("Encrypted String:" + encrypted);
      System.out.println("Decrypted String:" + decrypted);
   }
}

