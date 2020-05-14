package ru.galtsov;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DependentComponent {

	private Class type;
	private String name;
}
