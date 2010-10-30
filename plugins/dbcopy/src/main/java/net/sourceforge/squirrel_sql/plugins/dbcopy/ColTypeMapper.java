/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

import org.hibernate.MappingException;

/**
 * This class uses column type defintions from the source session table column
 * and uses that information to determine the correct column definition in the 
 * destination database using Hibernate.
 */
public class ColTypeMapper {

    /** Logger for this class. */
    private final static ILogger s_log = 
        LoggerController.createLogger(ColTypeMapper.class);
            
    /**
     * Returns null if the user cancelled picking the dialect.
     * 
     * @param sourceSession
     * @param destSession
     * @param TableColumnInfo
     * @param sourceTableName the name of the table we are copying from.  This
     *                        might include the schema prefix
     * @param destTableName the name of the table we are copying to.  This 
     *                      might include the schema prefix                      
     * @return
     */
    public static String mapColType(ISession sourceSession, 
                                    ISession destSession,
                                    TableColumnInfo colInfo,
                                    String sourceTableName,
                                    String destTableName) 
        throws UserCancelledOperationException, MappingException  
    {
        int colJdbcType = colInfo.getDataType();

        // If source column is type 1111 (OTHER), try to use the 
        // column type name to find a type that isn't 1111.        
        colJdbcType = DBUtil.replaceOtherDataType(colInfo, sourceSession);
        
        // If the source column is DISTINCT and the session is PostgreSQL, try to get the underlying type.
        colJdbcType = DBUtil.replaceDistinctDataType(colJdbcType, colInfo, sourceSession);
        
        // Oracle can only store DECIMAL type numbers.  Since regular non-decimal
        // numbers appear as "decimal", Oracle's decimal numbers can be rather 
        // large compared to other databases (precision up to 38).  Other 
        // databases can only handle this large precision in BIGINT fields, not
        // decimal, so try to figure out if Oracle is really storing a BIGINT
        // and claiming it is a DECIMAL.  If so, convert the type to BIGINT before
        // going any further.
        if (DialectFactory.isOracle(sourceSession.getMetaData())
                && colJdbcType ==Types.DECIMAL) 
        {
            // No decimal digits strongly suggests an INTEGER of some type.
            // Since it's not real easy to tell what kind of int (int2, int4, int8)
            // just make it an int8 (i.e. BIGINT)
            if (colInfo.getDecimalDigits() == 0) {
                colJdbcType = Types.BIGINT;
            }
        }
        // For char or date types this is the maximum number of characters, for 
        // numeric or decimal types this is precision.
        int size = getColumnLength(sourceSession, colInfo, sourceTableName);
                        
        if (DialectFactory.isPointbase(destSession.getMetaData()) && size <= 0) {
            if (DBUtil.isBinaryType(colInfo)) { 
                // For PointBase, if type maps to Pointbase "BLOB", and the size
                // isn't valid (PB requires size for BLOBS) then set it to something
                // reasonably large, like 16MB. 1 is the default size if no size
                // is specified.  That's practically useless :)
                size = 16777215; 
            } else {
                size = 20; // Numbers and such.
            }
        }
        if (DialectFactory.isFirebird(destSession.getMetaData())) {
            if (colJdbcType == java.sql.Types.DECIMAL) {
                if (size > 18) {
                    size = 18;
                }
            }
        }
        String result = null;
        JFrame mainFrame = destSession.getApplication().getMainFrame();
        HibernateDialect destDialect = 
            DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                      mainFrame, 
                                      destSession.getMetaData());

        if (s_log.isDebugEnabled()) {
            s_log.debug(
                    "ColTypeMapper.mapColType: using dialect type: "+
                    destDialect.getClass().getName()+" to find name for column "+
                    colInfo.getColumnName()+" in table "+destTableName+
                    " with type id="+colJdbcType+" ("+
                    JDBCTypeMapper.getJdbcTypeName(colJdbcType)+")");
        }
        if (destDialect != null) {
            HibernateDialect sourceDialect = 
                DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, 
                                          mainFrame, 
                                          sourceSession.getMetaData());
            
            int precision = sourceDialect.getPrecisionDigits(size, colJdbcType);
            
            if (precision > destDialect.getMaxPrecision(colJdbcType)) {
                precision = destDialect.getMaxPrecision(colJdbcType);
            }
            int scale = colInfo.getDecimalDigits();
            if (scale > destDialect.getMaxScale(colJdbcType)) {
                scale = destDialect.getMaxScale(colJdbcType);
            }
            // OK, this is a hack.  Currently, when precision == scale, I have 
            // no way to determine if this is valid for the actual data.  The
            // problem comes when the source db's precision/scale are greater - 
            // or reported to be greater - than the precision/scale of the 
            // destination db.  In this case, it maximimizes both for the 
            // destination, causing a definition that allows 0 digits to the 
            // left of the decimal.  Trouble is, without looking at the actual
            // data, there is no way to tell if this is valid - in some cases
            // it will be ok (0.0000000789) in others it will not be ok (100.123).
            // So for now, make the scale be approx. one-half of the precision
            // to accomodate the most digits to the left and right of the decimal
            // and hopefully that covers the majority of cases.
            if (precision <= scale) {
                if (precision < scale) {
                    precision = scale;
                }
                scale = precision / 2;
                s_log.debug(
                    "Precision == scale ("+precision+") for the destination " +
                    "database column def.  This is most likely incorrect, so " +
                    "setting the scale to a more reasonable value: "+scale);
                
            }
            // Some dbs (like McKoi) make -1 the default for scale.  Apply the 
            // same hack as above.
            if (scale < 0) {
                scale = precision / 2;
                s_log.debug(
                        "scale is less than 0 for the destination " +
                        "database column def.  This is most likely incorrect, so " +
                        "setting the scale to a more reasonable value: "+scale);                
            }
            result = destDialect.getTypeName(colJdbcType, size, precision, scale);
        } 
        return result;
    }
    
    /**
     * Gets the declared length of the column, or if the length is less than or
     * equal to 0, get the max length of the actual data in the column from the
     * database.  In the case of Firebird with certain BLOB types it always 
     * reports 0, so 2GB is hard-coded.  In the case of Oracle for CLOBs always
     * use the maximum value of the column or 4000 whichever is greatest.(Oracle
     * BLOBs/CLOBs always report 4000 as the column size, even when column 
     * values exceed this length) 
     *  
     * @param sourceSession
     * @param colInfo
     * @param tableName
     * @return
     */
    public static int getColumnLength(ISession sourceSession, 
                                      TableColumnInfo colInfo,
                                      String tableName) 
        throws UserCancelledOperationException
    {
        if (colInfo.getDataType() == Types.TIMESTAMP
                || colInfo.getDataType() == Types.DATE
                || colInfo.getDataType() == Types.TIME) 
        {
            // Date/Time types never declare a length.  Just return something
            // larger than 0 so we bypass other checks above.
            return 10;
        }
        // Oracle declares the column size to be 4000, regardless of the maximum
        // length of the CLOB field.  So if the Oracle BLOB/CLOB column contains 
        // values that exceed 4000 chars and we use colInfo.getColumnSize() we 
        // might create a destination column that is too small for the data 
        // that will be copied from Oracle.  We specify a default value of 4000
        // in case the table has no records or if the BLOB/CLOB column contains 
        // only null values.
        if (DialectFactory.isOracle(sourceSession.getMetaData())
                && (colInfo.getDataType() == Types.CLOB 
                        || colInfo.getDataType() == Types.BLOB))
        {
            return getColumnLengthBruteForce(sourceSession, colInfo, tableName, 4000);
        }
        int length = getColumnLength(sourceSession, colInfo);
        // As a last resort, get the length of the longest value in the 
        // specified column.
        if (length <= 0) {
            length = getColumnLengthBruteForce(sourceSession, colInfo, tableName, 10);            
        }
        return length;
    }
    
    private static int getColumnLength(ISession sourceSession, 
                                       TableColumnInfo colInfo) 
        throws UserCancelledOperationException
    {
        HibernateDialect dialect = 
            DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, 
                                      sourceSession.getApplication().getMainFrame(), 
                                      sourceSession.getMetaData());
        int length = colInfo.getColumnSize();
        int type = colInfo.getDataType();
        length = dialect.getColumnLength(length, type); 
        return length;
    }
    
    private static int getColumnLengthBruteForce(ISession sourceSession, 
                                                 TableColumnInfo colInfo,
                                                 String tableName,
                                                 int defaultLength) 
        throws UserCancelledOperationException
    {
        int length = defaultLength;
        String sql = 
            DBUtil.getMaxColumnLengthSQL(sourceSession, 
                                         colInfo, 
                                         tableName, 
                                         true);
        ResultSet rs = null;
        try {
            rs = DBUtil.executeQuery(sourceSession, sql);
            if (rs.next()) {
                length = rs.getInt(1);
            }
            if (length <= 0) {
                length = defaultLength;
            }
        } catch (SQLException e) {
            s_log.error("ColTypeMapper.getColumnLengthBruteForce: encountered " +
                        "unexpected SQLException - "+e.getMessage());
        } finally {
            SQLUtilities.closeResultSet(rs);
        }        
        return length;
    }
}
