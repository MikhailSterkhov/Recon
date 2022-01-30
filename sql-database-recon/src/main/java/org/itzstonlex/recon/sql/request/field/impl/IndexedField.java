package org.itzstonlex.recon.sql.request.field.impl;

import org.itzstonlex.recon.sql.request.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.request.field.ReconSqlRequestField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class IndexedField implements ReconSqlRequestField {

    public static IndexedField create(ReconSqlFieldType type, String name) {
        return new IndexedField(type, name);
    }

    public static IndexedField createUnique(ReconSqlFieldType type, String name) {
        return new IndexedField(type, name).index(IndexType.UNIQUE);
    }

    public static IndexedField createNotNull(ReconSqlFieldType type, String name) {
        return new IndexedField(type, name).index(IndexType.NOT_NULL);
    }

    public static IndexedField createPrimary(ReconSqlFieldType type, String name) {
        return new IndexedField(type, name).index(IndexType.PRIMARY);
    }

    public static IndexedField createPrimaryNotNull(ReconSqlFieldType type, String name) {
        return new IndexedField(type, name).indexes(IndexType.PRIMARY, IndexType.NOT_NULL);
    }

    private final ReconSqlFieldType type;
    private final String name;

    private final Collection<IndexType> indexTypes = new ArrayList<>();
    
    public IndexedField(ReconSqlFieldType type, String name) {
        this.type = type;
        this.name = name;
    }

    public IndexedField index(IndexType index) {
        indexTypes.add(index);
        return this;
    }

    public IndexedField indexes(IndexType... indexes) {
        indexTypes.addAll(Arrays.asList(indexes));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(String.format("`%s`", name));

        stringBuilder.append(" ");
        stringBuilder.append(type.getFormattedName());

        for (IndexType indexType : IndexType.values()) {
            if (!indexTypes.contains(indexType))
                continue;

            stringBuilder.append(" ");
            stringBuilder.append(indexType.getFormattedName());
        }

        return stringBuilder.toString();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object value() {
        return null;
    }

    public enum IndexType {

        NOT_NULL("NOT NULL"),
        PRIMARY("PRIMARY KEY"),
        KEY("KEY"),
        UNIQUE("UNIQUE"),
        FULLTEXT("FULLTEXT"),
        SPATIAL("SPATIAL"),
        AUTO_INCREMENT("AUTO_INCREMENT"),
        ;

        private final String formattedName;

        IndexType(String formattedName) {
            this.formattedName = formattedName;
        }

        public String getFormattedName() {
            return formattedName;
        }
    }
}
