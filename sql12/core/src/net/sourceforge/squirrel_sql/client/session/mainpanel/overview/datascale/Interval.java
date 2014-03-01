package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.util.List;


public class Interval
{

   private IndexedColumn _indexedColumn;
   private int _firstIx;
   private int _lastIx;
   private Object _beginData;
   private Object _endData;
   private String _label;
   private String _tooltip;
   private String _report;

   public Interval(IndexedColumn indexedColumn, int firstIx, int lastIx, Object beginData, Object endData)
   {
      _indexedColumn = indexedColumn;
      _firstIx = firstIx;
      _lastIx = lastIx;
      _beginData = beginData;
      _endData = endData;

      Object firstVal = _indexedColumn.get(firstIx);
      Object lastVal = _indexedColumn.get(lastIx);
      String firstValRendered = _indexedColumn.renderObject(firstVal);
      String lastValRendered = _indexedColumn.renderObject(lastVal);

      if (hasDifferentValues())
      {
         _label = firstValRendered + " - " + lastValRendered;
      }
      else
      {
         _label = firstValRendered;
      }


      _tooltip = "<html>" +
            escapeHtmlChars(_label) + "<br><br>" +
            "row count = " + getLen() + "; percentage = " + String.format("%.3f",100d * getWight()) + "%<br>" +
            "first value = " + escapeHtmlChars(firstValRendered) + "<br>" +
            "last value = " + escapeHtmlChars(lastValRendered) + "<br>" +
            "first index = " + _firstIx + "; last index = " + _lastIx + "<br>" +
            "data interval = "    + getIntervalOpeningBracket() + escapeHtmlChars(_indexedColumn.renderObject(beginData)) +
            ", " + escapeHtmlChars(_indexedColumn.renderObject(endData)) + getIntervalClosingBracket() + "<br>" +
            "complete row count = " + _indexedColumn.size() +
            "</html>";

      _report = "<html>" +
            //"<table border=\"1\">" +
            "<table>" +
            "<tr><th>" +
            escapeHtmlChars(_label) +
            "</th></tr>" +
            "<tr><td>" +
            //"<table border=\"1\">" +
            "<table>" +
            "<tr>" +
            "<td>row count</td><td>" + getLen() + "</td>" +
            "</tr>" +
            "<tr>" +
            "<td>percentage</td><td>" + String.format("%.3f",100d * getWight()) + "%</td>" +
            "</tr>" +
            "<tr>" +
            "<td>from-to index</td><td>" + _firstIx + " - " + _lastIx + "</td>" +
            "</tr>" +
            "</table>" +
            "</tr></td>" +
            "</table>" +
            "</html>";



   }

   private String getIntervalOpeningBracket()
   {
      if(0 == _indexedColumn.compareObjects(_indexedColumn.get(_firstIx), _beginData))
      {
         return "[";
      }
      else
      {
         return "]";
      }
   }

   private String getIntervalClosingBracket()
   {
      if(0 == _indexedColumn.compareObjects(_indexedColumn.get(_lastIx), _endData))
      {
         return "]";
      }
      else
      {
         return "[";
      }
   }


   public int getLen()
   {
      return (_lastIx - _firstIx + 1);
   }

   private String escapeHtmlChars(String sql)
   {
      String buf = sql.replaceAll("&", "&amp;");
      buf = buf.replaceAll("<", "&lt;");
      buf = buf.replaceAll("<", "&gt;");
      buf = buf.replaceAll("\"", "&quot;");
      return buf;
   }

   private boolean hasDifferentValues()
   {
      Object firstVal = _indexedColumn.get(_firstIx);
      Object lastVal = _indexedColumn.get(_lastIx);
      return false == Utilities.equalsRespectNull(firstVal, lastVal);
   }


   public double getWight()
   {
      return (double) getLen() / (double) _indexedColumn.size();
   }

   public String getLabel()
   {
      return _label;
   }

   public String getToolTip()
   {
      return _tooltip;
   }

   public String getReport()
   {
      return _report;
   }

   public List<Object[]> getResultRows()
   {
      return _indexedColumn.getResultRows(_firstIx, _lastIx);
   }

   public boolean containsAllRows()
   {
      return getLen() == _indexedColumn.size();
   }

   public String getWidth()
   {
      return _indexedColumn.calculateDist(_beginData, _endData);
   }

   public Object get(int i)
   {
      return _indexedColumn.get(_firstIx + i);
   }

   public int getDataSetRowIndex(int intervalIx)
   {
      return _indexedColumn.getRowIx(_firstIx + intervalIx);
   }

   public IndexedColumn getIndexedColumn()
   {
      return _indexedColumn;
   }
}
