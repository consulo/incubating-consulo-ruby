/**
 * @author VISTALL
 * @since 2026-05-15
 */
open module consulo.ruby.impl
{
    requires consulo.ruby.api;

    requires consulo.file.template.api;
    requires consulo.localize.api;
    requires consulo.project.api;
    requires consulo.project.ui.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;

    exports consulo.ruby.toolWindow;
    exports consulo.ruby.impl.template;
}
