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

package org.jetbrains.plugins.ruby.ruby.lang.commenter;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;


public class RubyCommenter implements Commenter {
    @NonNls
    private static final String LINE_COMMENT_PREFIX = "#";
    @NonNls
    private static final String BLOCK_COMMENT_PREFIX = "\n=begin\n";
    @NonNls
    private static final String BLOCK_COMMAND_SUFFIX = "\n=end\n";

    @Nullable
    public String getLineCommentPrefix() {
        return LINE_COMMENT_PREFIX;
    }

    @Nullable
    public String getBlockCommentPrefix() {
        return BLOCK_COMMENT_PREFIX;
    }

    @Nullable
    public String getBlockCommentSuffix() {
        return BLOCK_COMMAND_SUFFIX;
    }
}
