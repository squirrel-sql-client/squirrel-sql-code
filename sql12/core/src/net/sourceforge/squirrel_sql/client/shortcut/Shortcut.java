package net.sourceforge.squirrel_sql.client.shortcut;

import org.jfree.chart.util.ObjectUtils;

import javax.swing.KeyStroke;

public class Shortcut
{
   private final String _actionName;
   private final KeyStroke _defaultKeyStroke;
   private KeyStroke _userKeyStroke;
   private boolean _userKeyStrokeEmpty;

   public Shortcut(String actionName, KeyStroke defaultKeyStroke)
   {
      _actionName = actionName;
      _defaultKeyStroke = defaultKeyStroke;
   }

   /**
    * Used via reflection by JavabeanArrayDataSet
    */
   public String getActionName()
   {
      return _actionName;
   }

   /**
    * Used via reflection by JavabeanArrayDataSet
    */
   public String getDefaultKeyStroke()
   {
      return ShortcutUtil.getKeystrokeString(_defaultKeyStroke);
   }


   /**
    * Used via reflection by JavabeanArrayDataSet
    */
   public String getValidKeyStroke()
   {
      return ShortcutUtil.getKeystrokeString(validKeyStroke());
   }

   public KeyStroke validKeyStroke()
   {
      if(null != _userKeyStroke)
      {
         return _userKeyStroke;
      }

      if(_userKeyStrokeEmpty)
      {
         return null;
      }

      return _defaultKeyStroke;
   }


   public void setUserKeyStroke(KeyStroke userKeyStroke)
   {
      if(ObjectUtils.equal(userKeyStroke, _defaultKeyStroke))
      {
         restoreDefault();
      }
      else
      {
         _userKeyStroke = userKeyStroke;
         _userKeyStrokeEmpty = false;
      }
   }

   public boolean hasUserKeyStroke()
   {
      return null != _userKeyStroke || _userKeyStrokeEmpty;
   }

   public void restoreDefault()
   {
      _userKeyStroke = null;
      _userKeyStrokeEmpty = false;
   }

   public String generateKey()
   {
      return ShortcutUtil.generateKey(_actionName, _defaultKeyStroke);
   }

   public String generateUserKeyStrokeString()
   {
      if (_userKeyStrokeEmpty)
      {
         return null;
      }
      else
      {
         return _userKeyStroke.toString();
      }
   }

   public void setUserKeyStrokeEmpty()
   {
      _userKeyStroke = null;
      _userKeyStrokeEmpty = true;

   }


   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Shortcut shortcut = (Shortcut) o;

      return _actionName != null ? _actionName.equals(shortcut._actionName) : shortcut._actionName == null;
   }

   @Override
   public int hashCode()
   {
      return _actionName != null ? _actionName.hashCode() : 0;
   }
}
