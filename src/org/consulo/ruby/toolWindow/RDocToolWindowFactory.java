package org.consulo.ruby.toolWindow;

import org.jetbrains.plugins.ruby.ruby.ri.RDocPanel;
import org.jetbrains.plugins.ruby.ruby.ri.RDocSettings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;

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
