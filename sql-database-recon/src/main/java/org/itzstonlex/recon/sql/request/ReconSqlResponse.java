package org.itzstonlex.recon.sql.request;

import org.itzstonlex.recon.sql.util.GsonUtils;
import org.itzstonlex.recon.sql.util.ThrowableConsumer;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.function.Consumer;

public class ReconSqlResponse implements ResultSet {
    
    private final ResultSet impl;
    
    public ReconSqlResponse(ResultSet impl) {
        this.impl = impl;
    }

    @Override
    public boolean next() {
        try {
            return impl.next();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public void close() {
        try {
            impl.close();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean wasNull() {
        try {
            return impl.wasNull();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public String getString(int columnIndex) {
        try {
            return impl.getString(columnIndex);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean getBoolean(int columnIndex) {
        try {
            return impl.getBoolean(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public byte getByte(int columnIndex) {
        try {
            return impl.getByte(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public short getShort(int columnIndex) {
        try {
            return impl.getShort(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getInt(int columnIndex) {
        try {
            return impl.getInt(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public long getLong(int columnIndex) {
        try {
            return impl.getLong(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public float getFloat(int columnIndex) {
        try {
            return impl.getFloat(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public double getDouble(int columnIndex) {
        try {
            return impl.getDouble(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) {
        try {
            return impl.getBigDecimal(columnIndex, scale);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] getBytes(int columnIndex) {
        try {
            return impl.getBytes(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Date getDate(int columnIndex) {
        try {
            return impl.getDate(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Time getTime(int columnIndex) {
        try {
            return impl.getTime(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) {
        try {
            return impl.getTimestamp(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) {
        try {
            return impl.getAsciiStream(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) {
        try {
            return impl.getUnicodeStream(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) {
        try {
            return impl.getBinaryStream(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public String getString(String columnLabel) {
        try {
            return impl.getString(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean getBoolean(String columnLabel) {
        try {
            return impl.getBoolean(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public byte getByte(String columnLabel) {
        try {
            return impl.getByte(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public short getShort(String columnLabel) {
        try {
            return impl.getShort(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getInt(String columnLabel) {
        try {
            return impl.getInt(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public long getLong(String columnLabel) {
        try {
            return impl.getLong(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public float getFloat(String columnLabel) {
        try {
            return impl.getFloat(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public double getDouble(String columnLabel) {
        try {
            return impl.getDouble(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) {
        try {
            return impl.getBigDecimal(columnLabel, scale);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public byte[] getBytes(String columnLabel) {
        try {
            return impl.getBytes(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Date getDate(String columnLabel) {
        try {
            return impl.getDate(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Time getTime(String columnLabel) {
        try {
            return impl.getTime(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) {
        try {
            return impl.getTimestamp(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) {
        try {
            return impl.getAsciiStream(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) {
        try {
            return impl.getUnicodeStream(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) {
        try {
            return impl.getBinaryStream(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public SQLWarning getWarnings() {
        try {
            return impl.getWarnings();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void clearWarnings() {
        try {
            impl.clearWarnings();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public String getCursorName() {
        try {
            return impl.getCursorName();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public ResultSetMetaData getMetaData() {
        try {
            return impl.getMetaData();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getObject(int columnIndex) {
        try {
            return impl.getObject(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getObject(String columnLabel) {
        try {
            return impl.getObject(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public int findColumn(String columnLabel) {
        try {
            return impl.findColumn(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public Reader getCharacterStream(int columnIndex) {
        try {
            return impl.getCharacterStream(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(String columnLabel) {
        try {
            return impl.getCharacterStream(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) {
        try {
            return impl.getBigDecimal(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) {
        try {
            return impl.getBigDecimal(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isBeforeFirst() {
        try {
            return impl.isBeforeFirst();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isAfterLast() {
        try {
            return impl.isAfterLast();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return impl.isFirst();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isLast() {
        try {
            return impl.isLast();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public void beforeFirst() {
        try {
            impl.beforeFirst();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void afterLast() {
        try {
            impl.afterLast();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean first() {
        try {
            return impl.first();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean last() {
        try {
            return impl.last();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public int getRow() {
        try {
            return impl.getRow();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean absolute(int row) {
        try {
            return impl.absolute(row);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean relative(int rows) {
        try {
            return impl.relative(rows);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean previous() {
        try {
            return impl.previous();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public void setFetchDirection(int direction) {
        try {
            impl.setFetchDirection(direction);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public int getFetchDirection() {
        try {
            return impl.getFetchDirection();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return FETCH_UNKNOWN;
        }
    }

    @Override
    public void setFetchSize(int rows) {
        try {
            impl.setFetchSize(rows);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public int getFetchSize() {
        try {
            return impl.getFetchSize();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getType() {
        try {
            return impl.getType();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return TYPE_FORWARD_ONLY;
        }
    }

    @Override
    public int getConcurrency() {
        try {
            return impl.getConcurrency();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return CONCUR_READ_ONLY;
        }
    }

    @Override
    public boolean rowUpdated() {
        try {
            return impl.rowUpdated();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean rowInserted() {
        try {
            return impl.rowInserted();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean rowDeleted() {
        try {
            return impl.rowDeleted();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public void updateNull(int columnIndex) {
        try {
            impl.updateNull(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBoolean(int columnIndex, boolean value) {
        try {
            impl.updateBoolean(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateByte(int columnIndex, byte value) {
        try {
            impl.updateByte(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateShort(int columnIndex, short value) {
        try {
            impl.updateShort(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateInt(int columnIndex, int value) {
        try {
            impl.updateInt(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateLong(int columnIndex, long value) {
        try {
            impl.updateLong(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateFloat(int columnIndex, float value) {
        try {
            impl.updateFloat(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDouble(int columnIndex, double value) {
        try {
            impl.updateDouble(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal value) {
        try {
            impl.updateBigDecimal(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateString(int columnIndex, String value) {
        try {
            impl.updateString(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBytes(int columnIndex, byte[] value) {
        try {
            impl.updateBytes(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDate(int columnIndex, Date value) {
        try {
            impl.updateDate(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateTime(int columnIndex, Time value) {
        try {
            impl.updateTime(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp value) {
        try {
            impl.updateTimestamp(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream inputStream, int length) {
        try {
            impl.updateAsciiStream(columnIndex, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream inputStream, int length) {
        try {
            impl.updateBinaryStream(columnIndex, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader reader, int length) {
        try {
            impl.updateCharacterStream(columnIndex, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateObject(int columnIndex, Object value, int scaleOrLength) {
        try {
            impl.updateObject(columnIndex, value, scaleOrLength);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateObject(int columnIndex, Object value) {
        try {
            impl.updateObject(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNull(String columnLabel) {
        try {
            impl.updateNull(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBoolean(String columnLabel, boolean value) {
        try {
            impl.updateBoolean(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateByte(String columnLabel, byte value) {
        try {
            impl.updateByte(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateShort(String columnLabel, short value) {
        try {
            impl.updateShort(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateInt(String columnLabel, int value) {
        try {
            impl.updateInt(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateLong(String columnLabel, long value) {
        try {
            impl.updateLong(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateFloat(String columnLabel, float value) {
        try {
            impl.updateFloat(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDouble(String columnLabel, double value) {
        try {
            impl.updateDouble(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal value) {
        try {
            impl.updateBigDecimal(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateString(String columnLabel, String value) {
        try {
            impl.updateString(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBytes(String columnLabel, byte[] value) {
        try {
            impl.updateBytes(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDate(String columnLabel, Date value) {
        try {
            impl.updateDate(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateTime(String columnLabel, Time value) {
        try {
            impl.updateTime(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp value) {
        try {
            impl.updateTimestamp(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream inputStream, int length) {
        try {
            impl.updateAsciiStream(columnLabel, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream inputStream, int length) {
        try {
            impl.updateBinaryStream(columnLabel, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) {
        try {
            impl.updateCharacterStream(columnLabel, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateObject(String columnLabel, Object value, int scaleOrLength) {
        try {
            impl.updateObject(columnLabel, value, scaleOrLength);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateObject(String columnLabel, Object value) {
        try {
            impl.updateObject(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void insertRow() {
        try {
            impl.insertRow();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateRow() {
        try {
            impl.updateRow();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void deleteRow() {
        try {
            impl.deleteRow();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void refreshRow() {
        try {
            impl.refreshRow();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void cancelRowUpdates() {
        try {
            impl.cancelRowUpdates();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void moveToInsertRow() {
        try {
            impl.moveToInsertRow();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void moveToCurrentRow() {
        try {
            impl.moveToCurrentRow();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Statement getStatement() {
        try {
            return impl.getStatement();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) {
        try {
            return impl.getObject(columnIndex, map);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Ref getRef(int columnIndex) {
        try {
            return impl.getRef(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Blob getBlob(int columnIndex) {
        try {
            return impl.getBlob(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Clob getClob(int columnIndex) {
        try {
            return impl.getClob(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Array getArray(int columnIndex) {
        try {
            return impl.getArray(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) {
        try {
            return impl.getObject(columnLabel, map);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Ref getRef(String columnLabel) {
        try {
            return impl.getRef(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Blob getBlob(String columnLabel) {
        try {
            return impl.getBlob(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Clob getClob(String columnLabel) {
        try {
            return impl.getClob(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Array getArray(String columnLabel) {
        try {
            return impl.getArray(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) {
        try {
            return impl.getDate(columnIndex, cal);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) {
        try {
            return impl.getDate(columnLabel, cal);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) {
        try {
            return impl.getTime(columnIndex, cal);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) {
        try {
            return impl.getTime(columnLabel, cal);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) {
        try {
            return impl.getTimestamp(columnIndex, cal);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) {
        try {
            return impl.getTimestamp(columnLabel, cal);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public URL getURL(int columnIndex) {
        try {
            return impl.getURL(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public URL getURL(String columnLabel) {
        try {
            return impl.getURL(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateRef(int columnIndex, Ref value) {
        try {
            impl.updateRef(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateRef(String columnLabel, Ref value) {
        try {
            impl.updateRef(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBlob(int columnIndex, Blob value) {
        try {
            impl.updateBlob(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBlob(String columnLabel, Blob value) {
        try {
            impl.updateBlob(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateClob(int columnIndex, Clob value) {
        try {
            impl.updateClob(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateClob(String columnLabel, Clob value) {
        try {
            impl.updateClob(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateArray(int columnIndex, Array value) {
        try {
            impl.updateArray(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateArray(String columnLabel, Array value) {
        try {
            impl.updateArray(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public RowId getRowId(int columnIndex) {
        try {
            return impl.getRowId(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public RowId getRowId(String columnLabel) {
        try {
            return impl.getRowId(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateRowId(int columnIndex, RowId value) {
        try {
            impl.updateRowId(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateRowId(String columnLabel, RowId value) {
        try {
            impl.updateRowId(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public int getHoldability() {
        try {
            return impl.getHoldability();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return CLOSE_CURSORS_AT_COMMIT;
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return impl.isClosed();
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return true;
        }
    }

    @Override
    public void updateNString(int columnIndex, String nString) {
        try {
            impl.updateNString(columnIndex, nString);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNString(String columnLabel, String nString) {
        try {
            impl.updateNString(columnLabel, nString);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) {
        try {
            impl.updateNClob(columnIndex, nClob);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) {
        try {
            impl.updateNClob(columnLabel, nClob);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public NClob getNClob(int columnIndex) {
        try {
            return impl.getNClob(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public NClob getNClob(String columnLabel) {
        try {
            return impl.getNClob(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) {
        try {
            return impl.getSQLXML(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) {
        try {
            return impl.getSQLXML(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) {
        try {
            impl.updateSQLXML(columnIndex, xmlObject);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) {
        try {
            impl.updateSQLXML(columnLabel, xmlObject);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public String getNString(int columnIndex) {
        try {
            return impl.getNString(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public String getNString(String columnLabel) {
        try {
            return impl.getNString(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) {
        try {
            return impl.getNCharacterStream(columnIndex);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) {
        try {
            return impl.getNCharacterStream(columnLabel);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader reader, long length) {
        try {
            impl.updateNCharacterStream(columnIndex, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) {
        try {
            impl.updateNCharacterStream(columnLabel, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream inputStream, long length) {
        try {
            impl.updateAsciiStream(columnIndex, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream inputStream, long length) {
        try {
            impl.updateBinaryStream(columnIndex, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader reader, long length) {
        try {
            impl.updateCharacterStream(columnIndex, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream inputStream, long length) {
        try {
            impl.updateAsciiStream(columnLabel, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream inputStream, long length) {
        try {
            impl.updateBinaryStream(columnLabel, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) {
        try {
            impl.updateCharacterStream(columnLabel, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) {
        try {
            impl.updateBlob(columnIndex, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) {
        try {
            impl.updateBlob(columnLabel, inputStream, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) {
        try {
            impl.updateClob(columnIndex, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) {
        try {
            impl.updateClob(columnLabel, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) {
        try {
            impl.updateNClob(columnIndex, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) {
        try {
            impl.updateNClob(columnLabel, reader, length);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader value) {
        try {
            impl.updateNCharacterStream(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) {
        try {
            impl.updateNCharacterStream(columnLabel, reader);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream value) {
        try {
            impl.updateAsciiStream(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream value) {
        try {
            impl.updateBinaryStream(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader value) {
        try {
            impl.updateCharacterStream(columnIndex, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream value) {
        try {
            impl.updateAsciiStream(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream value) {
        try {
            impl.updateBinaryStream(columnLabel, value);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) {
        try {
            impl.updateCharacterStream(columnLabel, reader);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) {
        try {
            impl.updateBlob(columnIndex, inputStream);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) {
        try {
            impl.updateBlob(columnLabel, inputStream);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) {
        try {
            impl.updateClob(columnIndex, reader);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) {
        try {
            impl.updateClob(columnLabel, reader);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) {
        try {
            impl.updateNClob(columnIndex, reader);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) {
        try {
            impl.updateNClob(columnLabel, reader);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) {
        try {
            return impl.getObject(columnIndex, type);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) {
        try {
            return impl.getObject(columnLabel, type);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> T unwrap(Class<T> typeClass) {
        try {
            return impl.unwrap(typeClass);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> typeClass) {
        try {
            return impl.isWrapperFor(typeClass);
        } 
        catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public <R> R getJsonObject(String columnLabel, Class<R> returnType) {
        return GsonUtils.fromJsonString(this.getString(columnLabel), returnType);
    }

    public <R> R getJsonObject(int columnIndex, Class<R> returnType) {
        return GsonUtils.fromJsonString(this.getString(columnIndex), returnType);
    }

    public void forEachOrdered(ThrowableConsumer<ReconSqlResponse> loopHandler) {
        while (next()) {
            loopHandler.accept(this);
        }
    }

    public void doForEachOrdered(ThrowableConsumer<ReconSqlResponse> loopHandler) {
        do {
            loopHandler.accept(this);
        } while (next());
    }

}
