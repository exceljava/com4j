package com4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Dispatch NAME of the method.
 *
 * <p>
 * Java method naming convention is to use mixed case with the first letter
 * lowercase, with the first letter of each internal word capitalized. The com4j
 * type library importer tool therefore camelize method names if necessary. The
 * information of original method name is required for COM event handling to
 * find correct Java side method. So we have to annotate that information
 * explicitly.
 *
 * @author Maximilian Gerhard (mgerhard@gk-software.com)
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface DISPNAME {
    /**
     * DISPNAME used by {@code EventProxy}.
     */
    String value();
}
