package org.itzstonlex.recon.sql.objects;

import org.itzstonlex.recon.sql.ReconSqlTable;
import org.itzstonlex.recon.sql.util.propertymap.PropertyMap;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class SqlObjectDescription<V> {

    public static <V> SqlObjectDescription<V> create(V instance, ReconSqlTable table, PropertyMap<Object> fieldsProperty) {
        return new SqlObjectDescription<>(instance, table, fieldsProperty);
    }

    private static final String PLACEHOLDER_FORMAT = ("${%s}");

    private int cachedID = -1;

    private final V instance;

    private final ReconSqlTable table;
    private final PropertyMap<Object> fieldsProperty;

    private SqlObjectDescription(V instance, ReconSqlTable table, PropertyMap<Object> fieldsProperty) {
        this.instance = instance;
        this.table = table;
        this.fieldsProperty = fieldsProperty;
    }

    public int getCachedID() {
        return cachedID;
    }

    public void setCachedID(int cachedID) {
        this.cachedID = cachedID;
    }

    public ReconSqlTable getTable() {
        return table;
    }

    public V asObject() {
        return instance;
    }

    public PropertyMap<Object> asProperty() {
        return fieldsProperty;
    }

    public void setProperty(String field, Object value) {
        fieldsProperty.setProperty(field, value);
    }

    public void reinject(SqlObjectWorker worker) {
        fieldsProperty.reset();
        fieldsProperty.setProperties(worker.injectObject(instance).fieldsProperty);
    }

    private String formatValueObject(Object value) {
        return (value instanceof CharSequence || value instanceof Date ? String.format("'%s'", value) : value.toString());
    }

    public String remakeRequest(String sql) {
        if (table != null) {
            sql = sql.replace(String.format(PLACEHOLDER_FORMAT, "rtable"), table.getName());
        }

        int counter = 0;
        for (String key : fieldsProperty.keys()) {
            Object value = fieldsProperty.getProperty(key);

            if (value != null) {
                String valueReplacementString = this.formatValueObject(value);

                sql = sql.replace(String.format(PLACEHOLDER_FORMAT, key), valueReplacementString);
                sql = sql.replace(String.format(PLACEHOLDER_FORMAT, counter++), String.format("`%s`=", key) + valueReplacementString);
            }
        }

        return sql;
    }

    public String propertiesKeysJoining(String delimiter) {
        return fieldsProperty.keys()
                .stream()
                .map(f -> String.format("`%s`", f))
                .collect(Collectors.joining(delimiter));
    }

    public String propertiesValuesJoining(String delimiter) {
        return fieldsProperty.values()
                .stream()
                .map(this::formatValueObject)
                .collect(Collectors.joining(delimiter));
    }

    public String propertiesListToRequest(String delimiter) {
        return fieldsProperty.keys()
                .stream()
                .map(this::fieldToRequest)
                .collect(Collectors.joining(delimiter));
    }

    public String propertiesListToRequest(String delimiter, String separator) {
        List<String> keysList = new ArrayList<>(fieldsProperty.keys());
        List<String> valuesList = new ArrayList<>();

        for (String key : keysList) {
            valuesList.add( this.formatValueObject(fieldsProperty.getProperty(key)) );
        }

        return String.join(delimiter, keysList) + separator + String.join(delimiter, valuesList);
    }

    public String fieldToRequest(String field) {
        Object value = fieldsProperty.getProperty(field);
        if (value == null) {
            return null;
        }

        return String.format("`%s`=", field) + this.formatValueObject(value);
    }

}
