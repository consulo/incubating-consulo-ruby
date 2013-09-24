package com.intellij.facet;

import com.intellij.openapi.module.Module;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class Facet<T extends FacetConfiguration>
{
	public Facet(FacetType facetType, Module module, String name, T configuration, Facet underlyingFacet)
	{

	}

	public Module getModule()
	{
		return null;
	}

	public void initFacet()
	{


	}

	public T getConfiguration()
	{
		return null;
	}

	public void disposeFacet()
	{


	}
}
