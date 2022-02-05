package org.itzstonlex.recon.sql.objects.annotation;

import org.itzstonlex.recon.sql.request.field.impl.IndexedField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSql {

    String name() default "";

    IndexedField.IndexType[] indexes() default {};
}
