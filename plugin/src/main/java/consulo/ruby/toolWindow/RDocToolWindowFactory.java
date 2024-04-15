package consulo.ruby.toolWindow;

import org.jetbrains.plugins.ruby.ruby.ri.RDocPanel;
import org.jetbrains.plugins.ruby.ruby.ri.RDocSettings;
import consulo.project.Project;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.project.ui.wm.ToolWindowFactory;
import consulo.ui.ex.content.Content;
import consulo.ui.ex.content.ContentManager;

/**
 * @author VISTALL
 * @since 25.09.13.
 */
public class RDocToolWindowFactory implements ToolWindowFactory
{
	@Override
	public void createToolWindowContent(Project project, ToolWindow toolWindow)
	{
		ContentManager contentManager = toolWindow.getContentManager();

		Content content = contentManager.getFactory().createContent(new RDocPanel(project, RDocSettings.getInstance()).getPanel(), null, false);

		contentManager.addContent(content);
	}
}
