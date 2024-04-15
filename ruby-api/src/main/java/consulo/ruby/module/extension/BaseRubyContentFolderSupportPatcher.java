package consulo.ruby.module.extension;

import consulo.annotation.component.ExtensionImpl;
import consulo.content.ContentFolderTypeProvider;
import consulo.language.content.ProductionContentFolderTypeProvider;
import consulo.language.content.TestContentFolderTypeProvider;
import consulo.module.content.layer.ContentFolderSupportPatcher;
import consulo.module.content.layer.ModifiableRootModel;

import jakarta.annotation.Nonnull;
import java.util.Set;

/**
 * @author VISTALL
 * @since 13.02.15
 */
@ExtensionImpl
public class BaseRubyContentFolderSupportPatcher implements ContentFolderSupportPatcher
{
	@Override
	public void patch(@Nonnull ModifiableRootModel model, @Nonnull Set<ContentFolderTypeProvider> set)
	{
		BaseRubyModuleExtension extension = model.getExtension(BaseRubyModuleExtension.class);
		if(extension != null)
		{
			set.add(ProductionContentFolderTypeProvider.getInstance());
			set.add(TestContentFolderTypeProvider.getInstance());
		}
	}
}
