package ru.galtsov;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ComponentDraft {

	private Class highLevelType;
	private Class type;
	private String name;

	private List<DependentComponent> dependentComponents = new ArrayList<>();
}
