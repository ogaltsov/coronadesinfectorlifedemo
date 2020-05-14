package ru.galtsov;

import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApplicationContext {

	private ApplicationContextConfiguration contextConfiguration;
	@Setter
	private ObjectFactory objectFactory;

	private Map<Class, Map<String, ComponentDraft>> componentDrafts = new HashMap<>();
	private Map<Class, Map<String, Object>> componentByNameByType = new HashMap<>();


	public ApplicationContext(ApplicationContextConfiguration contextConfiguration) {
		Assert.notNull(contextConfiguration, "contextConfiguration is null");
		this.contextConfiguration = contextConfiguration;
	}

	public <T> T getObject(Class type, String name) {
		Map<String, Object> orDefault = componentByNameByType.getOrDefault(type, Collections.emptyMap());

		if (orDefault.containsKey(name)) {
			return (T) orDefault.get(name);
		}
		return getObject(type);
	}

	public <T> T getObject(Class type) {

		Map<String, Object> orDefault = componentByNameByType.getOrDefault(type, Collections.emptyMap());
		if (orDefault.size() == 1) {
			return (T) orDefault.values().stream().findFirst().orElseThrow();
		}
		return null;
	}

	public void start() {
		List<ComponentDraft> draftList = contextConfiguration.scanComponentDrafts();

		draftList.forEach(draft -> {
			Map<String, ComponentDraft> componentDraftNameMap = componentDrafts.computeIfAbsent(draft.getHighLevelType(), k -> new HashMap<>());
			if (componentDraftNameMap.containsKey(draft.getName())) {
				throw new IllegalStateException("There ara components with same name: '" + draft.getName() + "' and type: " + draft.getHighLevelType());
			}
			componentDraftNameMap.put(draft.getName(), draft);
		});

		//проверяем что все необходимые компоненты существуют(раньше всего)

		//проверяем циклические зависимости

		//расставляем порядки(уровень вложенности) и создаем
		buildComponentTree(draftList);

	}

	private void buildComponentTree(List<ComponentDraft> draftList) {

		List<ComponentDependencyTreeNode> dependencyNodeList = new ArrayList<>();
		draftList.forEach(draft -> buildNode(draft, 0, dependencyNodeList));

		Map<ComponentDraft, ComponentDependencyTreeNode> collect = dependencyNodeList.stream()
				.collect(Collectors.toMap(
						ComponentDependencyTreeNode::getParentComponent,
						Function.identity(),
						(d1, d2) -> Integer.compare(d1.getLevel(), d2.getLevel()) == 1 ? d1 : d2
				));
		List<ComponentDraft> componentCandidatesInOrder = collect.entrySet().stream()
				.sorted(Comparator.comparing(entry -> entry.getValue().getLevel(), Comparator.reverseOrder()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		componentCandidatesInOrder.forEach(component -> {
			Object buildedComponent = objectFactory.createObject(component.getType());
			Map<String, Object> map = componentByNameByType.computeIfAbsent(component.getHighLevelType(), k -> new HashMap<>());
			if (map.containsKey(component.getName())) {
				throw new IllegalStateException("There ara components with same name: '" + component.getName() + "' and type: " + component.getHighLevelType());
			}
			map.put(component.getName(), buildedComponent);
		});
	}

	private ComponentDependencyTreeNode buildNode(ComponentDraft parentComponentDraft, int level, List<ComponentDependencyTreeNode> nodeResultList) {
		ComponentDependencyTreeNode treeNode = new ComponentDependencyTreeNode();
		treeNode.setParentComponent(parentComponentDraft);
		treeNode.setLevel(level++);

		int finalLevel = level;
		List<ComponentDependencyTreeNode> childComponentNodes = parentComponentDraft.getDependentComponents().stream()
				.map(this::getSuitableComponentDraft)
				.map(draft -> buildNode(draft, finalLevel, nodeResultList))
				.collect(Collectors.toList());

		treeNode.setChildComponentNodes(childComponentNodes);
		nodeResultList.add(treeNode);
		return treeNode;
	}

	private ComponentDraft getSuitableComponentDraft(DependentComponent draft) {
		Map<String, ComponentDraft> componentMap = componentDrafts.getOrDefault(draft.getType(), Collections.emptyMap());
		if (componentMap.containsKey(draft.getName())) {
			return componentMap.get(draft.getName());
		}
		if (componentMap.values().size() == 1) {
			return componentMap.values().stream().findFirst().orElseThrow();
		}
		throw new IllegalStateException("no suitable component for " + draft.getType().toString());
	}
}
