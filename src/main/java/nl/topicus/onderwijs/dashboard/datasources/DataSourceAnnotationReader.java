package nl.topicus.onderwijs.dashboard.datasources;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.modules.DataSourceSettings;
import nl.topicus.onderwijs.dashboard.modules.KeyProperty;

public final class DataSourceAnnotationReader {
	private DataSourceAnnotationReader() {
	}

	public static DataSourceSettings getSettings(
			Class<? extends DataSource<?>> dataSource) {
		return findAnnotation(dataSource, DataSourceSettings.class);
	}

	public static KeyProperty getKeyProperty(
			Class<? extends DataSource<?>> dataSource) {
		return findAnnotation(dataSource, KeyProperty.class);
	}

	private static <T extends Annotation> T findAnnotation(
			Class<? extends DataSource<?>> dataSource, Class<T> annotationClass) {
		Set<Class<?>> todo = new HashSet<Class<?>>();
		todo.add(dataSource);
		while (!todo.isEmpty()) {
			Class<?> curClass = todo.iterator().next();
			todo.remove(curClass);
			if (curClass.isAnnotationPresent(annotationClass))
				return curClass.getAnnotation(annotationClass);
			if (curClass.getSuperclass() != null)
				todo.add(curClass.getSuperclass());
			todo.addAll(Arrays.asList(curClass.getInterfaces()));
		}
		return null;
	}
}
