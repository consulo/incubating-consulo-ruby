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

package org.jetbrains.plugins.ruby.ruby.lang.formatter.processors;

import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.spacing.SpacingTokens;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RCondition;

public class RubySpacingProcessor {
    private static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    private static final Spacing SINGLE_SPACING = Spacing.createSpacing(1,1000,0,true, 1000);
    private static final Spacing NO_EOL_SPACING = Spacing.createSpacing(1,1000, 0, false,0);
    /**
     * Calculates spacing between two ambigiuous children
     *
     * @param leftChild  Left child node
     * @param rightChild Right child node
     * @param settings   Current code style settings
     * @return Spacing object
     */
    @Nullable
    public static Spacing getSpacing(@NotNull final ASTNode leftChild, @NotNull final ASTNode rightChild,
                                     final CodeStyleSettings settings) {
        if (SpacingTokens.SPACING_AFTER.contains(leftChild.getElementType())){
            return SINGLE_SPACING;
        }
        if (SpacingTokens.NO_SPACING_BEFORE.contains(rightChild.getElementType())){
            return NO_SPACING;
        }
        if (rightChild.getPsi() instanceof RCondition){
            return NO_EOL_SPACING;
        }
        return null;
    }
}