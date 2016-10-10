package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FkPropsPersistence
{
   private List<FoldingPointPersistence> _foldingPointPersistences = new ArrayList<>();
   private String _fkName;
   private String _joinConfigValue = JoinConfig.INNER_JOIN.toString();


   public List<FoldingPointPersistence> getFoldingPointPersistences()
   {
      return _foldingPointPersistences;
   }

   public void setFoldingPointPersistences(List<FoldingPointPersistence> foldingPointPersistences)
   {
      _foldingPointPersistences = foldingPointPersistences;
   }

   public void setFkName(String fkName)
   {
      _fkName = fkName;
   }

   public String getFkName()
   {
      return _fkName;
   }


   public static HashMap<String, FkProps> toFkProps(HashMap<String, FkPropsPersistence> persistentFkPropsPersistenceByFkName)
   {
      HashMap<String, FkProps> ret = new HashMap<>();

      for (Map.Entry<String, FkPropsPersistence> entry : persistentFkPropsPersistenceByFkName.entrySet())
      {
         FkProps fkProps = new FkProps(entry.getKey(), JoinConfig.valueOf(entry.getValue().getJoinConfigValue()));

         for (FoldingPointPersistence fpp : entry.getValue().getFoldingPointPersistences())
         {
            fkProps.addFoldingPoint(new Point2D(fpp.getX(), fpp.getY()));
         }

         ret.put(entry.getKey(), fkProps);
      }

      return ret;
   }

   public static HashMap<String, FkPropsPersistence> toFkPropsPersitences(HashMap<String, FkProps> fkPropsByFkName)
   {
      HashMap<String, FkPropsPersistence> ret = new HashMap<>();

      for (Map.Entry<String, FkProps> entry : fkPropsByFkName.entrySet())
      {
         ret.put(entry.getKey(), entry.getValue().toPersistence());
      }

      return ret;
   }

   public void setJoinConfigValue(String joinConfigValue)
   {
      _joinConfigValue = joinConfigValue;
   }

   public String getJoinConfigValue()
   {
      return _joinConfigValue;
   }
}
