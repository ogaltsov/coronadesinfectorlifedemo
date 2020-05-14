package ru.galtsov;

/**
 * @author Evgeny Borisov
 */
public interface ProxyConfigurator {
    Object replaceWithProxyIfNeeded(Object t, Class implClass);
}
