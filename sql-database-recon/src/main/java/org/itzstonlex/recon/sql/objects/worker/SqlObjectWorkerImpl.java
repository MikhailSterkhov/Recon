package org.itzstonlex.recon.sql.objects.worker;

import org.itzstonlex.recon.sql.ReconSqlConnection;
import org.itzstonlex.recon.sql.ReconSqlTable;
import org.itzstonlex.recon.sql.exception.ReconSqlException;
import org.itzstonlex.recon.sql.objects.SqlObjectDescription;
import org.itzstonlex.recon.sql.objects.SqlObjectWorker;
import org.itzstonlex.recon.sql.objects.annotation.FieldSql;
import org.itzstonlex.recon.sql.objects.annotation.InjectionSql;
import org.itzstonlex.recon.sql.request.ReconSqlResponse;
import org.itzstonlex.recon.sql.request.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.request.field.impl.IndexedField;
import org.itzstonlex.recon.sql.util.GsonUtils;
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
        int idByFirstField = this.executeWithResponse(description, "SELECT * FROM ${rtable} WHERE ${0}")
                .thenApply(response -> response.next() ? response.getInt("id") : -1)
                .join();

        if (idByFirstField < 0) {
            return this.executeWithResponse(description, String.format("SELECT * FROM ${rtable} WHERE %s", description.propertiesListToRequest(" AND ")))
                    .thenApply(response -> response.next() ? response.getInt("id") : -1)
                    .join();
        }

        return idByFirstField;
    }

    @Override
    public boolean contains(SqlObjectDescription<?> description) {
        return this.getID(description) > 0;
    }

    @Override
    public void insert(SqlObjectDescription<?> description) {
        this.execute(description, String.format("INSERT INTO `${rtable}` (%s)", description.propertiesListToRequest(", ", ") VALUES (")));
    }

    @Override
    public void delete(SqlObjectDescription<?> description) {
        if (this.contains(description)) {
            this.execute(description, String.format("DELETE FROM `${rtable}` WHERE `id`=%s", this.getID(description)));
        }
    }

    @Override
    public void update(SqlObjectDescription<?> description) {
        if (this.contains(description)) {
            this.execute(description, String.format("UPDATE `${rtable}` SET %s WHERE `id`=%d", description.propertiesListToRequest(", "), this.getID(description)));
        }
    }

    @Override
    public void execute(SqlObjectDescription<?> description, String sql) {
        connection.getExecution().update(true, description.remakeRequest(sql));
    }

    @Override
    public CompletableFuture<ReconSqlResponse> executeWithResponse(SqlObjectDescription<?> description, String sql) {
        return connection.getExecution().getResponse(true, description.remakeRequest(sql));
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

                    if (ReconSqlFieldType.fromAttachment(value.getClass()) != ReconSqlFieldType.UNKNOWN) {
                        fieldsProperty.setProperty(name, value);
                    }
                    else {
                        fieldsProperty.setProperty(name, GsonUtils.toJsonString(value));
                    }

                } catch (Exception ignored) {
                } finally {
                    field.setAccessible(false);
                }
            }
        }

        return fieldsProperty;
    }

    private Map<String, FieldSql> getFieldsAnnotationsMap(Class<?> instanceType) {
        Map<String, FieldSql> map = new HashMap<>();

        for (Field field : instanceType.getDeclaredFields()) {
            FieldSql fieldAnnotation = field.getDeclaredAnnotation(FieldSql.class);

            if (fieldAnnotation != null) {
                field.setAccessible(true);

                try {
                    String name = fieldAnnotation.name().isEmpty() ? field.getName() : fieldAnnotation.name();
                    map.put(name, fieldAnnotation);

                } catch (Exception ignored) {
                } finally {
                    field.setAccessible(false);
                }
            }
        }

        return map;
    }

    private ReconSqlTable createSqlTable(Map<String, FieldSql> fieldsAnnotationsMap,
                                         PropertyMap<Object> fieldProperties, String table) {

        return connection.createOrGetTable(table, request -> {

            request.push(IndexedField.createPrimaryNotNull(ReconSqlFieldType.INT, "id")
                    .index(IndexedField.IndexType.AUTO_INCREMENT));

            fieldProperties.keys()
                    .stream()
                    .distinct()
                    .forEach(field -> {

                if (field.equalsIgnoreCase("id")) {
                    throw new ReconSqlException("Field `id` already is exists!");
                }

                Object value = fieldProperties.getProperty(field);
                ReconSqlFieldType fieldType = ReconSqlFieldType.fromAttachment(value.getClass());

                if (fieldType != ReconSqlFieldType.UNKNOWN) {

                    IndexedField indexedField = IndexedField.create(fieldType, field);
                    FieldSql annotation = fieldsAnnotationsMap.get(field);

                    if (annotation != null) {
                        indexedField.indexes(annotation.indexes());
                    }

                    request.push(indexedField);
                }
            });
        });
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

        ReconSqlTable table = this.createSqlTable(this.getFieldsAnnotationsMap(instanceType),
                fieldsProperty, injectionAnnotation.table());

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
