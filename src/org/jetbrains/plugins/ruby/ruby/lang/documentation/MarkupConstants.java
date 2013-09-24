/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.ruby.lang.documentation;

import org.jetbrains.annotations.NonNls;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 11, 2007
 */
public interface MarkupConstants {
    @NonNls
    public final String ELEMENT = "ELEMENT";
    @NonNls
    public final String SYMBOL = "SYMBOL";
    @NonNls
    public final String COMMA = ",";

    @NonNls
    public final String LT = "&lt;";
    @NonNls
    public final String GT = "&gt;";
    @NonNls
    public final String PRIME = "&Prime;";
    @NonNls
    public final String BR = "<br>";
    @NonNls
    public final String HR = "<hr>";
    @NonNls
    public final String SPACE = "&nbsp;";

    @NonNls
    final String DIRECTIVE_PREFIX = "<font color=#0000FF>";
    @NonNls
    final String DIRECTIVE_SUFFIX = "</font>";
    @NonNls
    final String TODO_PREFIX = "<b><em><font color=#0000FF>";
    @NonNls
    final String TODO_SUFFIX = "</font></em></b>";
    @NonNls
    final String BOLD_PREFIX = "<b>";
    @NonNls
    final String BOLD_SUFFIX = "</b>";
    @NonNls
    final String ITALIC_PREFIX = "<em>";
    @NonNls
    final String ITALIC_SUFFIX = "</em>";
    @NonNls
    final String CODE_PREFIX = "<code>";
    @NonNls
    final String CODE_SUFFIX = "</code>";
}
