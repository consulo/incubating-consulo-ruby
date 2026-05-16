/**
 * @author VISTALL
 * @since 2026-05-15
 */
open module consulo.ruby.rails
{
    requires consulo.ruby.api;
    requires consulo.ruby.rails.api;

    requires consulo.disposer.api;
    requires consulo.module.api;
    requires consulo.module.content.api;
    requires consulo.ui.api;

    exports consulo.ruby.rails.impl.module.extension;
}
