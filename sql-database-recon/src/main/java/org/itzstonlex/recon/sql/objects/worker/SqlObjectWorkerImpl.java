package org.itzstonlex.recon.sql.objects.worker;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlTable;
import org.itzstonlex.recon.sql.objects.SqlObjectDescription;
import org.itzstonlex.recon.sql.objects.SqlObjectWorker;
import org.itzstonlex.recon.sql.objects.annotation.FieldSql;
import org.itzstonlex.recon.sql.objects.annotation.InjectionSql;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;
import org.itzstonlex.recon.sql.util.propertymap.PropertyMap;
import org.itzstonlex.recon.sql.util.propertymap.type.ObjectPropertyMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SqlObjectWorkerImpl implements SqlObjectWorker {
    private final ReconSqlConnection connection;

    public SqlObjectWorkerImpl(ReconSqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public int getID(SqlObjectDescription<?> description) {
        return executeWithResponse(description, "SELECT * FROM ${rtable} WHERE `name`=${name}")
                .thenApply(response -> response.next() ? response.getInt("id") : -1)
                .join();
    }

    @Override
    public boolean contains(SqlObjectDescription<?> description) {
        return this.executeWithResponse(description, String.format("SELECT * FROM `${rtable}` WHERE %s", description.propertiesListToRequest(" AND ")))
                .thenApply(ReconSqlResponse::next)
                .join();
    }

    @Override
    public void insert(SqlObjectDescription<?> description) {
        this.execute(description, String.format("INSERT INTO `${rtable}` (%s)", description.propertiesListToRequest(", ", ") VALUES (")));
    }

    @Override
    public void delete(SqlObjectDescription<?> description) {
        this.execute(description, "DELETE FROM `${rtable}` WHERE ${0}");
    }

    @Override
    public void update(SqlObjectDescription<?> description) {
        this.execute(description, String.format("UPDATE `${rtable}` SET %s WHERE ${0}", description.propertiesListToRequest(", ")));
    }

    @Override
    public void execute(SqlObjectDescription<?> description, String sql) {
        connection.getExecution().update(true, description.remakeRequest(sql));
    }

    @Override
    public CompletableFuture<ReconSqlResponse> executeWithResponse(SqlObjectDescription<?> description, String sql) {
        return connection.getExecution().getResponse(true, description.remakeRequest(sql));
    }

    private ReconSqlTable createSqlTable(PropertyMap<Object> fieldProperties, String table) {
        return connection.createOrGetTable(table, request -> {

            request.push(IndexedField.createPrimaryNotNull(ReconSqlFieldType.INT, "id")
                    .index(IndexedField.IndexType.AUTO_INCREMENT));

            fieldProperties.keys().forEach(field -> {
                if (field.equalsIgnoreCase("id")) {
                    return;
                }

                Object value = fieldProperties.getProperty(field);

                ReconSqlFieldType fieldType = ReconSqlFieldType.fromAttachment(value.getClass());
                if (fieldType == ReconSqlFieldType.UNKNOWN) {
                    return;
                }

                IndexedField indexedField = IndexedField.create(fieldType, field);
                request.push(indexedField);
            });
        });
    }

    private PropertyMap<Object> createFieldProperties(Object instance, Class<?> instanceType) {
        PropertyMap<Object> fieldsProperty = new ObjectPropertyMap();

        for (Field field : instanceType.getDeclaredFields()) {
            FieldSql fieldAnnotation = field.getDeclaredAnnotation(FieldSql.class);

            if (fieldAnnotation != null) {
                field.setAccessible(true);

                try {
                    String name = fieldAnnotation.name().isEmpty() ? field.getName() : fieldAnnotation.name();
                    Object value = field.get(instance);

                    fieldsProperty.setProperty(name, value);

                } catch (Exception ignored) {
                } finally {
                    field.setAccessible(false);
                }
            }
        }

        return fieldsProperty;
    }

    private final Map<Integer, SqlObjectDescription<?>> memoryCachedObjectsDescriptionsMap
            = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <V> SqlObjectDescription<V> injectObject(V instance) {
        Class<?> instanceType = instance.getClass();
        InjectionSql injectionAnnotation = instanceType.getDeclaredAnnotation(InjectionSql.class);

        if (injectionAnnotation == null) {
            return null;
        }

        PropertyMap<Object> fieldsProperty = this.createFieldProperties(instance, instanceType);
        ReconSqlTable table = this.createSqlTable(fieldsProperty, injectionAnnotation.table());

        SqlObjectDescription<V> description;
        if (!memoryCachedObjectsDescriptionsMap.containsKey(instance.hashCode())) {

            description = SqlObjectDescription.create(instance, table, fieldsProperty);
            memoryCachedObjectsDescriptionsMap.put(instance.hashCode(), description);
        }
        else {
            description = (SqlObjectDescription<V>) memoryCachedObjectsDescriptionsMap.get(instance.hashCode());

            description.asProperty().reset();
            description.asProperty().setProperties(fieldsProperty);
        }

        return description;
    }

}
