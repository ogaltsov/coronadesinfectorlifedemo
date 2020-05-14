package ru.galtsov;

import lombok.Data;

import java.util.List;

@Data
public class ComponentDependencyTreeNode {
	private int level;
	private ComponentDraft parentComponent;
	private List<ComponentDependencyTreeNode> childComponentNodes;
}
