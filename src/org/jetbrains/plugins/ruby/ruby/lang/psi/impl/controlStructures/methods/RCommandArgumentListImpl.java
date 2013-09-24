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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 19.05.2005
 */
public class RCommandArgumentListImpl extends RPsiElementBase implements RArgumentList {
    public RCommandArgumentListImpl(ASTNode astNode) {
        super(astNode);
    }

    @NotNull
    public List<RArgument> getArguments() {
        return RubyPsiUtil.getChildrenByType(this, RArgument.class);
    }

    @NotNull
    public List<ArgumentInfo> getArgumentInfos(final boolean includeDefaultArgs) {
        List<ArgumentInfo> myInfos = new ArrayList<ArgumentInfo>();
        for (RArgument argument : getArguments()) {
            myInfos.add(getArgumentInfo(argument, includeDefaultArgs));
        }
        return myInfos;
    }

    @NotNull
    public List<ArgumentInfo> getArgumentInfos() {
        return getArgumentInfos(false);
    }

    private ArgumentInfo getArgumentInfo(@NotNull final RArgument argument, final boolean includeDefaultArgs) {
        if (argument instanceof RPredefinedArgument && includeDefaultArgs){
            final String value = ((RPredefinedArgument) argument).getValueText();
            return new RArgumentInfoWithDefaultArgs(argument.getName(),
                    value!=null ? value : "",
                    argument.getType());
        }
        return new ArgumentInfo(argument.getName(), argument.getType());
    }

    public int getArgNumber(@NotNull final RArgument arg) {
        for (int i = 0; i < getArguments().size(); i++) {
            final RArgument argument = getArguments().get(i);
            if (argument == arg){
                return i;
            }
        }
        return -1;
    }

    @NotNull
    public String getPresentableName(boolean includeDefaultArgs) {
        return getPresentableName(getArgumentInfos(includeDefaultArgs));
    }

    public static String getPresentableName(List<ArgumentInfo> argList) {
        final StringBuilder buff = new StringBuilder();
        for (int i = 0; i < argList.size(); i++){
            if (i != 0){
                buff.append(", ");
            }
            buff.append(argList.get(i).getPresentableName());
        }
        return buff.toString();
    }
}