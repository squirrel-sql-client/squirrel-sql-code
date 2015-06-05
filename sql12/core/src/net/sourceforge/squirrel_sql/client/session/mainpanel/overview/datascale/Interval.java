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

   private String _tooltipSmall;
   private final String _tooltipBig;
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


      int distinctValsForColumn = _indexedColumn.countDistinctValsForColumn();
      int distinctValsInInterval = _indexedColumn.countDistinctValsForInterval(_firstIx, _lastIx);


      _tooltipSmall = createToolTip("<small>", beginData, endData, firstValRendered, lastValRendered, distinctValsForColumn, distinctValsInInterval);
      _tooltipBig = createToolTip("", beginData, endData, firstValRendered, lastValRendered, distinctValsForColumn, distinctValsInInterval);

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

   private String createToolTip(String size, Object beginData, Object endData, String firstValRendered, String lastValRendered, int distinctValsForColumn, int distinctValsInInterval)
   {
      String tooltip = "<html> " +
            size + "<b>Interval details for column <i>" + _indexedColumn.getColumnName() + "</i></b><br>" +

            "<table BORDER=1>" +
            "<tr>" +
            "<td>" + size + "Interval bounds (first/last data value)</td><td>" + size +  escapeHtmlChars(_label) + "</td>" +
            "</tr>" +
            "<tr>" +
            "<td>" + size + "Interval bounds (calculated)</td><td>" + size + getIntervalOpeningBracket() + escapeHtmlChars(_indexedColumn.renderObject(beginData)) + ", " + escapeHtmlChars(_indexedColumn.renderObject(endData)) + getIntervalClosingBracket()+ "</td>" +
            "</tr>" +
            "<tr>" +
            "<td>" + size + "Number of values in Interval</td><td>" + size + getLen() + " (percentage = " + String.format("%.3f",100d * getWight()) + "%)</td>" +
            "</tr>" +
            "<tr>" +
            "<td>" + size + "Number of distinct values in Interval</td><td>" + size + distinctValsInInterval + "</td>" +
            "</tr>" +
//            "<tr>" +
//            "<td>"+ size + "First data value</td><td>" + size + escapeHtmlChars(firstValRendered) + "</td>" +
//            "</tr>" +
//            "<tr>" +
//            "<td>" + size + "Last data value</td><td>" + size + escapeHtmlChars(lastValRendered) + "</td>" +
//            "</tr>" +
            "<tr>" +
            "<td>" + size + "First index = " + _firstIx + "</td><td>"+ size + "Last Index = " + _lastIx + "<br>" +
            "</tr>" +
            "</table>" +

            "<br>" + size + "<b>Details of complete Overview</b><br>" +
            size + "<table BORDER=1>" + size +
            "<tr>" +
            "<td>" + size + "Number of distinct values for column <i>" + _indexedColumn.getColumnName() + "</i> in Overview</td><td>" + size + distinctValsForColumn + "</td>" +
            "</tr>" +
            "<tr>" +
            "<td>" + size + "Complete row count of underlying data in Overview</td><td>" + size + _indexedColumn.size() + "</td>" +
            "</tr>" +
            "</table>" +
            "</html>";

      return tooltip;
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

   public String getSmallToolTipHtml()
   {
      return _tooltipSmall;
   }
   public String getBigToolTipHtml()
   {
      return _tooltipBig;
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
