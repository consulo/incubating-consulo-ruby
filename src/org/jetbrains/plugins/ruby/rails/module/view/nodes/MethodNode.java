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

package org.jetbrains.plugins.ruby.rails.module.view.nodes;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 10.10.2006
 */
public class MethodNode extends RailsNode {
    private final RVirtualMethod myMethod;

    public MethodNode(final Module module, final RVirtualMethod method,
                      final String fileUrl) {
        super(module);
        myMethod = method;

        final ItemPresentation presentation = method.getPresentation();
        assert presentation != null;
        
        init(generateNodeId(method), presentation);
        assert getVirtualFileUrl().equals(fileUrl);
    }

    @NotNull
    public static NodeId generateNodeId(final RVirtualMethod method) {
        return NodeIdUtil.createForVirtualContainer(method);
    }

    @Override
	@NotNull
    public RailsProjectNodeComparator.NodeType getType() {
        return RailsProjectNodeComparator.NodeType.METHOD;
    }

    public RVirtualMethod getMethod() {
        return myMethod;
    }
}
