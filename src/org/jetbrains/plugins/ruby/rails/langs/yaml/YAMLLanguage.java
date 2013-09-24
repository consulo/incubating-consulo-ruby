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

package org.jetbrains.plugins.ruby.rails.langs.yaml;

import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 11, 2008
 */
public class YAMLLanguage extends Language {
    public static final YAMLLanguage INSTANCE = new YAMLLanguage();

    protected NotNullLazyValue<SyntaxHighlighter> mySyntaxHighlighter;
    protected NotNullLazyValue<ParserDefinition> myParserDefinition;
    protected NotNullLazyValue<Commenter> myCommenter;

    private YAMLLanguage() {
        super("YAML", "yaml");
        mySyntaxHighlighter = new NotNullLazyValue<SyntaxHighlighter>() {
            @NotNull
            protected SyntaxHighlighter compute() {
                return new YAMLSyntaxHighlighter();
            }
        };
         myParserDefinition = new NotNullLazyValue<ParserDefinition>(){
            @NotNull
            protected ParserDefinition compute() {
                return new YAMLParserDefinition();
            }
        };
        myCommenter = new NotNullLazyValue<Commenter>(){
            @NotNull
            protected Commenter compute() {
                return new YAMLCommenter();
            }
        };
    }

    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(final Project project, final VirtualFile virtualFile) {
        return mySyntaxHighlighter.getValue();
    }

    @Nullable
    public ParserDefinition getParserDefinition() {
        return myParserDefinition.getValue();
    }

    @Nullable
    public Commenter getCommenter() {
        return myCommenter.getValue();
    }
}
