package ru.galtsov;

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationApplicationContextConfiguration implements ApplicationContextConfiguration {

	private String packageToScan;
	private static final AtomicInteger i = new AtomicInteger(0);

	public AnnotationApplicationContextConfiguration(Class mainEntryClass) {
		packageToScan = mainEntryClass.getPackageName();
	}

	@Override
	public List<ComponentDraft> scanComponentDrafts() {

		Reflections scanner = new Reflections(packageToScan);
		//todo: list of types
		Set<Class<?>> componentTypes = scanner.getTypesAnnotatedWith(Singleton.class);

		List<ComponentDraft> scannedDrafts = new ArrayList<>();

		for (Class<?> componentType : componentTypes) {
            Singleton annotation = componentType.getAnnotation(Singleton.class);

            String injectableComponentName = annotation.componentName().isEmpty() ?
                    "component:" + componentType.getName() + "-" + i.incrementAndGet()
                    : annotation.componentName();

            Optional<Class<?>> componentInterface = Stream.of(componentType.getInterfaces())
                    .filter(intrfc -> intrfc.getMethods().length != 0)
                    .findFirst();

            Class highLevelType = componentInterface.isPresent() ? componentInterface.get() : componentType;

            ComponentDraft componentDraft = new ComponentDraft();
            componentDraft.setHighLevelType(highLevelType);
            componentDraft.setType(componentType);
            componentDraft.setName(injectableComponentName);

			List<DependentComponent> dependentCopmponents = Stream.of(componentType.getDeclaredFields())
					.map(field -> {
						if (field.isAnnotationPresent(InjectByType.class)) {
							InjectByType injectAnnotation = field.getAnnotation(InjectByType.class);
							String dependentComponentName = injectAnnotation.componentName().isEmpty() ? field.getName() : injectAnnotation.componentName();

							return new DependentComponent(field.getType(), dependentComponentName);
						}
						return null;
					}).filter(Objects::nonNull)
					.collect(Collectors.toList());

			componentDraft.setDependentComponents(dependentCopmponents);
			scannedDrafts.add(componentDraft );
        }
		return scannedDrafts;
	}
}
