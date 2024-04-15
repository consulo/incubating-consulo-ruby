package com.intellij.facet;

import consulo.module.Module;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
@Deprecated
public class Facet<T>
{
	public Facet(Module module, String name, T configuration, Facet underlyingFacet)
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
