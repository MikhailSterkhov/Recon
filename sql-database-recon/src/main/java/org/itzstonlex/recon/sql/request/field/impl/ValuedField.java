package org.itzstonlex.recon.sql.request.field.impl;

import org.itzstonlex.recon.sql.request.field.ReconSqlRequestField;

public class ValuedField implements ReconSqlRequestField {

    public static ValuedField create(String name, Object value) {
        return new ValuedField(name, value);
    }

    public static ValuedField create(String name) {
        return new ValuedField(name, null);
    }

    private final String name;
    private Object value;

    public ValuedField(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Object value() {
        return value;
    }

    public void set(Object value) {
        this.value = value;
    }

}
