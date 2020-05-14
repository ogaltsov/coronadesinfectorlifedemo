package ru.galtsov;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class InjectByTypeAnnotationObjectConfigurator implements ObjectConfigurator {
    @Override
    @SneakyThrows
    public void configure(Object t, ApplicationContext context) {
        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectByType.class)) {

                InjectByType annotation = field.getAnnotation(InjectByType.class);
                String injectableComponentName = annotation.componentName().isEmpty() ? field.getName() : annotation.componentName();

                field.setAccessible(true);
                Object object = context.getObject(field.getType(), injectableComponentName);
                field.set(t, object);
            }
        }
    }
}
