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

package org.jetbrains.plugins.ruby.ruby.lang.commenter.block;

import com.intellij.codeInsight.generation.actions.CommentByBlockCommentAction;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.actions.BaseEditorActionTestCase;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 13, 2006
 */
public class BlockCommenterTest extends BaseEditorActionTestCase {
    @NonNls private static final String DATA_PATH = PathUtil.getDataPath(BlockCommenterTest.class);

    public BlockCommenterTest() {
        super(DATA_PATH);
    }


    protected void performAction() {
        performBlockCommentAction();
    }

    private void performBlockCommentAction() {
      CommentByBlockCommentAction action = new CommentByBlockCommentAction();
      action.actionPerformedImpl(myProject, myEditor);
    }

    public static Test suite(){
        return new BlockCommenterTest();
    }


/*
    public String getSearchPattern() {
        return ".*bc2\\.txt";
    }
*/
}
