package org.esa.s2tbx.fcc.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Razvan Dumitrascu
 * @since 5.0.6
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ParameterGroup {

    /**
     * @return An alias name for the parameter.
     *         Defaults to the empty string (= not set).
     */
    String alias() default "";

}