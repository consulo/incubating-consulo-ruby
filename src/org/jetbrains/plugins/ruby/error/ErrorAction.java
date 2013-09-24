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

package org.jetbrains.plugins.ruby.error;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 11, 2007
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class ErrorAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(ErrorAction.class.getName());

    public void actionPerformed(AnActionEvent e) {
        //throw new UnsupportedOperationException("Manually generated error");
        LOG.assertTrue(false, "I am stupid krevedko!");
    }
}
