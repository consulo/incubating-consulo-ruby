/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.Access;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.ImplicitTypeAccess;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.MethodParameterAccess;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.WriteAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 17, 2008
 */
public class RControlFlowUtil {
    private static final Logger LOG = Logger.getInstance(RControlFlowUtil.class.getName());

    public static int[] postorder(Instruction[] flow) {
        int[] result = new int[flow.length];
        boolean[] visited = new boolean[flow.length];
        Arrays.fill(visited, false);

        int N = flow.length;
        for (int i = 0; i < flow.length; i++) { //graph might not be connected
            if (!visited[i]) N = doVisitForPostorder(flow[i], N, result, visited);
        }

        LOG.assertTrue(N == 0);
        return result;
    }

    private static int doVisitForPostorder(Instruction curr, int currN, int[] postorder, boolean[] visited) {
        visited[curr.num()] = true;
        for (Instruction succ : curr.allSucc()) {
            if (!visited[succ.num()]) {
                currN = doVisitForPostorder(succ, currN, postorder, visited);
            }
        }
        postorder[curr.num()] = --currN;
        return currN;
    }

    public static int[] accessesBeforeWrites(Instruction[] flow) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        boolean[] visited = new boolean[flow.length];
        Arrays.fill(visited, false);

        accessesBeforeWritesRec(flow[0], result, visited);

        final int[] nums = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            nums[i] = result.get(i);
        }
        return nums;
    }

    private static void accessesBeforeWritesRec(final Instruction instruction,
                                                final List<Integer> result,
                                                final boolean[] visited) {
        final int num = instruction.num();
        if (visited[num]){
            return;
        }
        visited[num] = true;
        if (instruction instanceof ReadWriteInstruction){
            final Access access = ((ReadWriteInstruction) instruction).getAccess();
            if (!(access instanceof MethodParameterAccess)){
                if (access instanceof WriteAccess || access instanceof ImplicitTypeAccess){
                    return;
                }
                result.add(num);
            }
        }
        for (Instruction succ : instruction.allSucc()) {
            accessesBeforeWritesRec(succ, result, visited);
        }
    }

}
