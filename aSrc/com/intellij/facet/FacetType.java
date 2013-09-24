package com.intellij.facet;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public class FacetType<T extends Facet<?>, F extends FacetConfiguration>
{
	public FacetType(FacetTypeId<T> id, String s, String message)
	{

	}

	public FacetType(FacetTypeId<T> baseRailsFacetFacetTypeId, String stringId, String presentableName, FacetTypeId underlyingFacetType)
	{

	}

	public F createDefaultConfiguration()
	{

		return null;
	}

	public FacetTypeId<T> getId()
	{
		return null;
	}
}
