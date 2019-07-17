package edu.missouriwestern.csmp.gg.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Determines if an entity should be saved to the db when destroyed and searched for in the db
 * when created before assinging a brand new entity ID
 */
@Retention(RetentionPolicy.RUNTIME) // checked in order to determine if DB should be used
@Target(ElementType.TYPE)
public @interface Permanent {
}
