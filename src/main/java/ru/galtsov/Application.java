package ru.galtsov;

import org.reflections.Reflections;

public class Application {
    public static ApplicationContext run(Class applicationClass) {

        ApplicationContext context = new ApplicationContext(new AnnotationApplicationContextConfiguration(applicationClass));

        Reflections reflections = new Reflections(applicationClass.getPackageName());
        ObjectFactory objectFactory = new ObjectFactory(context, reflections);
        context.setObjectFactory(objectFactory);

        context.start();
        return context;
    }
}
