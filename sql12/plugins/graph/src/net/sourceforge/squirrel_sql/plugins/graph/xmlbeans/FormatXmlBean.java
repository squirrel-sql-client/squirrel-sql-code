package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;


public class FormatXmlBean
{
   private String name;
   private double width;
   private double height;
   private boolean selected;

   public FormatXmlBean(String name, double width, double height, boolean selected)
   {
      this.setName(name);
      this.setWidth(width);
      this.setHeight(height);
      this.setSelected(selected);
    }

   /**
    * Needed by XML Serializer
    */
   public FormatXmlBean()
   {
   }

   public String toString()
   {
      return getName();
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public double getWidth()
   {
      return width;
   }

   public void setWidth(double width)
   {
      this.width = width;
   }

   public double getHeight()
   {
      return height;
   }

   public void setHeight(double height)
   {
      this.height = height;
   }

   public boolean isSelected()
   {
      return selected;
   }

   public void setSelected(boolean selected)
   {
      this.selected = selected;
   }

}
