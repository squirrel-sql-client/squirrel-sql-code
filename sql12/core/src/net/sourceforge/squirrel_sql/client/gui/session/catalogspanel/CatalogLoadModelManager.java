package net.sourceforge.squirrel_sql.client.gui.session.catalogspanel;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;

import java.io.File;

public class CatalogLoadModelManager
{
   private CatalogLoadModelJsonBean _catalogLoadModelJsonBean = new CatalogLoadModelJsonBean();

   public AliasCatalogLoadModel createAliasCatalogLoadModel(ISession session)
   {
      AliasCatalogLoadModelJsonBean aliasCatalogLoadModel = getAliasCatalogLoadModelJsonBean(session.getAlias());

      return new AliasCatalogLoadModel(aliasCatalogLoadModel, session);
   }

   public AliasCatalogLoadModelJsonBean getAliasCatalogLoadModelJsonBean(SQLAlias alias)
   {
      return _catalogLoadModelJsonBean.getAliasIdToAliasCatalogLoadModelBeans().computeIfAbsent(alias.getIdentifier().toString(), s -> new AliasCatalogLoadModelJsonBean());
   }

   public void load()
   {
      File file = new ApplicationFiles().getCatalogLoadModelJsonFile();
      if(null != file && file.exists())
      {
         _catalogLoadModelJsonBean = JsonMarshalUtil.readObjectFromFile(file, CatalogLoadModelJsonBean.class);
      }
   }

   public void save()
   {
      JsonMarshalUtil.writeObjectToFile(new ApplicationFiles().getCatalogLoadModelJsonFile(), _catalogLoadModelJsonBean);
   }
}
