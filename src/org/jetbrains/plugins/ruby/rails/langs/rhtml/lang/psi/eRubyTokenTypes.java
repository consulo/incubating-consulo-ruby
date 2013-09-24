package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi;

import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IRHTMLElementType;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 24.09.13.
 */
public interface eRubyTokenTypes
{
	IElementType TEMPLATE_CHARACTERS_IN_RHTML = new IRHTMLElementType("TEMPLATE_CHARACTERS_IN_RHTML");

	IElementType RHTML_INJECTION_IN_HTML = new IRHTMLElementType("RHTML_INJECTION_IN_HTML");
}
