package consulo.ruby.toolWindow;

import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.project.ui.wm.ToolWindowFactory;
import consulo.ui.ex.content.Content;
import consulo.ui.ex.content.ContentManager;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.ui.ex.toolWindow.ToolWindowAnchor;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.ri.RDocPanel;
import org.jetbrains.plugins.ruby.ruby.ri.RDocSettings;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class RDocToolWindowFactory implements ToolWindowFactory
{
	@Nonnull
	@Override
	public String getId()
	{
		return "RDoc";
	}

	@Nonnull
	@Override
	public LocalizeValue getDisplayName()
	{
		return LocalizeValue.localizeTODO("RDoc");
	}

	@Override
	public Image getIcon()
	{
		return RubyIcons.RUBY_ICON;
	}

	@Nonnull
	@Override
	public ToolWindowAnchor getAnchor()
	{
		return ToolWindowAnchor.RIGHT;
	}

	@Override
	public void createToolWindowContent(Project project, ToolWindow toolWindow)
	{
		ContentManager contentManager = toolWindow.getContentManager();

		Content content = contentManager.getFactory().createContent(new RDocPanel(project, RDocSettings.getInstance()).getPanel(), null, false);

		contentManager.addContent(content);
	}
}
