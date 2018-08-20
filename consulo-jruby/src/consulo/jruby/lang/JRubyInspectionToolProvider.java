package consulo.jruby.lang;

import org.jetbrains.plugins.ruby.jruby.inspections.JRubyImplementInterfaceInspection;
import org.jetbrains.plugins.ruby.jruby.inspections.WrongTopLevelPackageInspection;
import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author VISTALL
 * @since 2018-08-21
 */
public class JRubyInspectionToolProvider implements InspectionToolProvider
{
	@Override
	public Class[] getInspectionClasses()
	{
		return new Class[]{
				JRubyImplementInterfaceInspection.class,
				WrongTopLevelPackageInspection.class
		};
	}
}
