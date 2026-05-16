/**
 * @author VISTALL
 * @since 2026-05-15
 */
open module consulo.jruby.rails
{
    requires consulo.ruby.api;
    requires consulo.ruby.rails.api;

    requires consulo.module.api;
    requires consulo.module.content.api;

    exports consulo.jruby.rails.module.extension;
}
