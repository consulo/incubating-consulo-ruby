package com.intellij.facet;

import java.util.Set;

import com.intellij.openapi.module.Module;
import com.intellij.util.messages.Topic;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class FacetManager
{
	public static final Topic<FacetManagerAdapter> FACETS_TOPIC = Topic.create("", FacetManagerAdapter.class);

	public static FacetManager getInstance(Module module)
	{
		return null;
	}

	public <T extends Facet> T getFacetByType(FacetTypeId<T> id)
	{
		return null;
	}

	public <T extends Facet> Set<T> getFacetsByType(FacetTypeId<T> id)
	{
		return null;
	}
}
