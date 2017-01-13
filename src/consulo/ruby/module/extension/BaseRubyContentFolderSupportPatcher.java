package consulo.ruby.module.extension;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.roots.ModifiableRootModel;
import consulo.roots.ContentFolderSupportPatcher;
import consulo.roots.ContentFolderTypeProvider;
import consulo.roots.impl.ProductionContentFolderTypeProvider;
import consulo.roots.impl.TestContentFolderTypeProvider;

;

/**
 * @author VISTALL
 * @since 13.02.15
 */
public class BaseRubyContentFolderSupportPatcher implements ContentFolderSupportPatcher
{
	@Override
	public void patch(@NotNull ModifiableRootModel model, @NotNull Set<ContentFolderTypeProvider> set)
	{
		BaseRubyModuleExtension extension = model.getExtension(BaseRubyModuleExtension.class);
		if(extension != null)
		{
			set.add(ProductionContentFolderTypeProvider.getInstance());
			set.add(TestContentFolderTypeProvider.getInstance());
		}
	}
}
