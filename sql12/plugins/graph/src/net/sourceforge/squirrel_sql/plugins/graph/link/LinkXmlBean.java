package net.sourceforge.squirrel_sql.plugins.graph.link;

public class LinkXmlBean
{
   private String _linkName;
   private String _nameOfLinkedGraph;
   private String _filePathOfLinkedGraph;

   public LinkXmlBean()
   {
   }

   public LinkXmlBean(String linkName, String nameOfLinkedGraph, String filePathOfLinkedGraph)
   {
      _linkName = linkName;
      _nameOfLinkedGraph = nameOfLinkedGraph;
      _filePathOfLinkedGraph = filePathOfLinkedGraph;
   }


   public String getLinkName()
   {
      return _linkName;
   }

   public void setLinkName(String linkName)
   {
      _linkName = linkName;
   }

   public String getNameOfLinkedGraph()
   {
      return _nameOfLinkedGraph;
   }

   public void setNameOfLinkedGraph(String nameOfLinkedGraph)
   {
      _nameOfLinkedGraph = nameOfLinkedGraph;
   }

   public String getFilePathOfLinkedGraph()
   {
      return _filePathOfLinkedGraph;
   }

   public void setFilePathOfLinkedGraph(String filePathOfLinkedGraph)
   {
      _filePathOfLinkedGraph = filePathOfLinkedGraph;
   }
}
