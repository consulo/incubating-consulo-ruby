package consulo.ruby.lang;

import org.jetbrains.plugins.ruby.ruby.inspections.ducktype.RubyDuckTypeInspection;
import org.jetbrains.plugins.ruby.ruby.inspections.resolve.RubyResolveInspection;
import org.jetbrains.plugins.ruby.ruby.inspections.scopes.RubyScopesInspection;
import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author VISTALL
 * @since 2018-08-21
 */
public class RubyInspectionToolProvider implements InspectionToolProvider
{
	@Override
	public Class[] getInspectionClasses()
	{
		return new Class[]{
				RubyDuckTypeInspection.class,
				RubyResolveInspection.class,
				RubyScopesInspection.class
		};
	}
}
