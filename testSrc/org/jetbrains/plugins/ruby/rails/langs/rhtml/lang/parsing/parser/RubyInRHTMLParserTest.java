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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser;

import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.support.utils.DebugUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 30, 2007
 */
public class RubyInRHTMLParserTest extends AbstractRHTMLParserTest {
    @NonNls private static final String DATA_PATH =
            PathUtil.getDataPath(AbstractRHTMLParserTest.class) + "/ruby_root";

    public RubyInRHTMLParserTest() {
        super(DATA_PATH);
    }

    protected String dump(final RHTMLFile psiFile) {
        final RHTMLFileViewProvider viewProvider = psiFile.getViewProvider();

        return DebugUtil.psiToString(viewProvider.getPsi(RubyLanguage.INSTANCE), false, false);
    }

    public static Test suite() {
        return new RubyInRHTMLParserTest();
    }
}