package org.itzstonlex.recon.sql.reqest.field.impl;

import org.itzstonlex.recon.sql.reqest.field.ReconSqlRequestField;

public class ValuedRequestField implements ReconSqlRequestField {

    public static ValuedRequestField create(String name, Object value) {
        return new ValuedRequestField(name, value);
    }

    public static ValuedRequestField create(String name) {
        return new ValuedRequestField(name, null);
    }

    private final String name;
    private Object value;

    public ValuedRequestField(String name, Object value) {
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
