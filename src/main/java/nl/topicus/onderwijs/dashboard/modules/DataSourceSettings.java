package nl.topicus.onderwijs.dashboard.modules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataSourceSettings {
	String label();

	String unit() default "";

	String conversion() default "identity";

	String htmlClass();

	Class<?> type();

	boolean list() default false;
}
