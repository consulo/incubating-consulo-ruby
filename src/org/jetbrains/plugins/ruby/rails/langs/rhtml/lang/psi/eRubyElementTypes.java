package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi;

import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.eRubyLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.impl.RubyDeclarationsInRHTMLTypeImpl;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.tree.IFileElementType;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public interface eRubyElementTypes
{
	IFileElementType RHTML_FILE = new IFileElementType("RHTML_FILE", eRubyLanguage.INSTANCE);

	TemplateDataElementType TEMPLATE_DATA = new TemplateDataElementType("ERUBY_TEMPLATE_DATA", eRubyLanguage.INSTANCE, eRubyTokenTypes.TEMPLATE_CHARACTERS_IN_RHTML, eRubyTokenTypes.RHTML_INJECTION_IN_HTML);

	//Ruby root of rhtml file
	IFileElementType RUBY_DECLARATIONS_IN_RHTML_ROOT = new RubyDeclarationsInRHTMLTypeImpl("RUBY_DECLARATIONS_IN_RHTML_ROOT");
}
