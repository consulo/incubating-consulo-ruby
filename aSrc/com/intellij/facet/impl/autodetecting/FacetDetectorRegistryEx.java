package com.intellij.facet.impl.autodetecting;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiFile;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public interface FacetDetectorRegistryEx<T extends FacetConfiguration>
{
	void registerOnTheFlyDetector(FileType ruby, VirtualFileFilter jrubyFacetFilter, Condition<PsiFile> condition, FacetDetector<?, ? extends FacetConfiguration> facetDetector);

	void registerDetectorForWizard(FileType ruby, VirtualFileFilter jrubyFacetFilter, FacetDetector<?, ? extends FacetConfiguration> facetDetector);

}
