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
package org.jetbrains.plugins.ruby.ruby.lang.psi.dataFlow.types;

import com.intellij.util.containers.HashMap;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.dataFlow.Semilattice;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author oleg
 */
public class TypesSemilattice implements Semilattice<Map<String, RType>> {

    @Override
	public Map<String, RType> join(final ArrayList<Map<String, RType>> ins) {
        if (ins.size() == 0) return new HashMap<String, RType>();

        Map<String, RType> result = new HashMap<String, RType>(ins.get(0));

        for (int i = 1; i < ins.size(); i++) {
            Map<String, RType> map = ins.get(i);

            for (Map.Entry<String, RType> entry : map.entrySet()) {
                final String name = entry.getKey();
                final RType t1 = entry.getValue();
                if (result.containsKey(name)){
                    final RType t2 = result.get(name);
                    if (t1 != null && t2 != null) {
                        result.put(name, RTypeUtil.joinOr(t1, t2));
                    } else {
                        result.put(name, null);
                    }
                } else {
                    result.put(name, t1);
                }
            }
        }

        return result;
    }

    @Override
	public boolean eq(final Map<String, RType> e1, final Map<String, RType> e2) {
        if (e1.size() != e2.size()) {
            return false;
        }
        for (Map.Entry<String, RType> entry : e1.entrySet()) {
            final String name = entry.getKey();
            final RType value1 = entry.getValue();
            if (!e2.containsKey(name)) {
                return false;
            }
            RType value2 = e2.get(name);
            if (value1 == null && value2 != null ||
                    value2 == null && value1 != null) {
                return false;
            }
            if (value1 != null && value2 != null) {
                if (!RTypeUtil.equal(entry.getValue(), e2.get(entry.getKey()))) {
                    return false;
                }
            }
        }
        return true;
    }
}
