package org.springframework.springfaces.template.ui;

import java.util.List;

import javax.faces.component.EditableValueHolder;

public interface ComponentInfo {

	EditableValueHolder getComponent();

	List<EditableValueHolder> getComponents();

	boolean isValid();

	boolean isRequired();

	String getId();

	String getClientId();

	String getLabel();

}
