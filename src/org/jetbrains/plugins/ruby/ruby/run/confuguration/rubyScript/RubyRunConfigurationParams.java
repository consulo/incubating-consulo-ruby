package org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript;

import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfigurationParams;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 22, 2008
 */
public interface RubyRunConfigurationParams extends AbstractRubyRunConfigurationParams
{
	public String getScriptArgs();

	public String getScriptPath();

	public void setScriptPath(final String scriptPath);

	public void setScriptArgs(final String myScriptArgs);
}
