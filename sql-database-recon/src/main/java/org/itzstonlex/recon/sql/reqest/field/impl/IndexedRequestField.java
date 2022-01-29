package org.itzstonlex.recon.sql.reqest.field.impl;

import org.itzstonlex.recon.sql.reqest.field.ReconSqlFieldType;
import org.itzstonlex.recon.sql.reqest.field.ReconSqlRequestField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class IndexedRequestField implements ReconSqlRequestField {

    public static IndexedRequestField create(ReconSqlFieldType type, String name) {
        return new IndexedRequestField(type, name);
    }

    public static IndexedRequestField createUnique(ReconSqlFieldType type, String name) {
        return new IndexedRequestField(type, name).putIndex(IndexType.UNIQUE);
    }

    public static IndexedRequestField createNotNull(ReconSqlFieldType type, String name) {
        return new IndexedRequestField(type, name).putIndex(IndexType.NOT_NULL);
    }

    public static IndexedRequestField createPrimary(ReconSqlFieldType type, String name) {
        return new IndexedRequestField(type, name).putIndex(IndexType.PRIMARY);
    }

    public static IndexedRequestField createPrimaryNotNull(ReconSqlFieldType type, String name) {
        return new IndexedRequestField(type, name).putIndexes(IndexType.PRIMARY, IndexType.NOT_NULL);
    }

    private final ReconSqlFieldType type;
    private final String name;

    private final Collection<IndexType> indexTypes = new ArrayList<>();
    
    public IndexedRequestField(ReconSqlFieldType type, String name) {
        this.type = type;
        this.name = name;
    }

    public IndexedRequestField putIndex(IndexType index) {
        indexTypes.add(index);
        return this;
    }

    public IndexedRequestField putIndexes(IndexType... indexes) {
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
