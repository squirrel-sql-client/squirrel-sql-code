package net.sourceforge.squirrel_sql.plugins.i18n;

public class I18nBundle
{
   private I18nProps _defaultProps;
   private I18nProps _localizedProps;

   public I18nBundle(I18nProps defaultProps)
   {
      _defaultProps = defaultProps;
   }

   public void setLocalizedProp(I18nProps localizedProps)
   {
      _localizedProps = localizedProps;
   }

   public String toString()
   {
      return _defaultProps.getPackage();
   }

   public String getName()
   {
      return _defaultProps.getPackage();
   }

   public String getTranslationState()
   {
      return "incomplete";
   }
}
