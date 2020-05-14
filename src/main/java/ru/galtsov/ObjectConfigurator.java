package ru.galtsov;

/**
 * @author Evgeny Borisov
 */
public interface ObjectConfigurator {
    void configure(Object t, ApplicationContext context);
}
