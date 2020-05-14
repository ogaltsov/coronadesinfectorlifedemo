package ru.galtsov;/**
 * @author Evgeny Borisov
 */

import java.lang.annotation.Retention;
import java.util.stream.Stream;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface Singleton {
	String componentName() default "";
}
