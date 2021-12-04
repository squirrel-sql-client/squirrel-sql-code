package net.sourceforge.squirrel_sql.client.session.messagepanel;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

import java.awt.Color;

public class MessagePanelStylePreferenceWrapper
{
   private SquirrelPreferences _prefs;

   public MessagePanelStylePreferenceWrapper(SquirrelPreferences prefs)
   {
      _prefs = prefs;
   }

   public boolean isSetMessageBackground()
   {
      return isSetBackground(_prefs.getMessagePanelMessageBackground());
   }

   public Color getMessageBackground()
   {
      return new Color(_prefs.getMessagePanelMessageBackground());
   }

   public boolean isSetMessageForeground()
   {
      return isSetForeground(_prefs.getMessagePanelMessageForeground());
   }

   public Color getMessageForeground()
   {
      return new Color(_prefs.getMessagePanelMessageForeground());
   }

   public boolean isSetMessageHistoryBackground()
   {
      return isSetBackground(_prefs.getMessagePanelMessageHistoryBackground());
   }

   public Color getMessageHistoryBackground()
   {
      return new Color(_prefs.getMessagePanelMessageHistoryBackground());
   }

   public boolean isSetMessageHistoryForeground()
   {
      return isSetForeground(_prefs.getMessagePanelMessageHistoryForeground());
   }

   public Color getMessageHistoryForeground()
   {
      return new Color(_prefs.getMessagePanelMessageHistoryForeground());
   }

   public boolean isSetWarningBackground()
   {
      return isSetBackground(_prefs.getMessagePanelWarningBackground());
   }

   public Color getWarningBackground()
   {
      return new Color(_prefs.getMessagePanelWarningBackground());
   }

   public boolean isSetWarningForeground()
   {
      return isSetForeground(_prefs.getMessagePanelWarningForeground());
   }

   public Color getWarningForeground()
   {
      return new Color(_prefs.getMessagePanelWarningForeground());
   }

   public boolean isSetWarningHistoryBackground()
   {
      return isSetBackground(_prefs.getMessagePanelWarningHistoryBackground());
   }

   public Color getWarningHistoryBackground()
   {
      return new Color(_prefs.getMessagePanelWarningHistoryBackground());
   }

   public boolean isSetWarningHistoryForeground()
   {
      return isSetForeground(_prefs.getMessagePanelWarningHistoryForeground());
   }

   public Color getWarningHistoryForeground()
   {
      return new Color(_prefs.getMessagePanelWarningHistoryForeground());
   }

   public boolean isSetErrorBackground()
   {
      return isSetBackground(_prefs.getMessagePanelErrorBackground());
   }

   public Color getErrorBackground()
   {
      return new Color(_prefs.getMessagePanelErrorBackground());
   }

   public boolean isSetErrorForeground()
   {
      return isSetForeground(_prefs.getMessagePanelErrorForeground());
   }

   public Color getErrorForeground()
   {
      return new Color(_prefs.getMessagePanelErrorForeground());
   }

   public boolean isSetErrorHistoryBackground()
   {
      return isSetBackground(_prefs.getMessagePanelErrorHistoryBackground());
   }

   public Color getErrorHistoryBackground()
   {
      return new Color(_prefs.getMessagePanelErrorHistoryBackground());
   }

   public boolean isSetErrorHistoryForeground()
   {
      return isSetForeground(_prefs.getMessagePanelErrorHistoryForeground());
   }

   public Color getErrorHistoryForeground()
   {
      return new Color(_prefs.getMessagePanelErrorHistoryForeground());
   }

   private boolean isSetBackground(int rgb)
   {
      return false == (_prefs.isMessagePanelWhiteBackgroundAsUIDefault() && Color.white.getRGB() == rgb);
   }

   private boolean isSetForeground(int rgb)
   {
      return false == (_prefs.isMessagePanelBlackForegroundAsUIDefault() && Color.black.getRGB() == rgb);
   }

}
