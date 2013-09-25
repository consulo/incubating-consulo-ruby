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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter;

import com.intellij.formatting.FormattingDocumentModel;
import com.intellij.formatting.WrapType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.HtmlPolicy;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 3, 2007
 */
public class RHTMLPolicy extends HtmlPolicy {
    public RHTMLPolicy(CodeStyleSettings settings, FormattingDocumentModel documentModel) {
        super(settings, documentModel);
    }

    @Override
	public WrapType getWrappingTypeForTagEnd(@Nullable final XmlTag xmlTag) {
        if (xmlTag == null) {
            return WrapType.NORMAL;
        }
        return super.getWrappingTypeForTagEnd(xmlTag);
    }

    @Override
	public WrapType getWrappingTypeForTagBegin(@Nullable final XmlTag xmlTag) {
        if (xmlTag == null) {
            return WrapType.NORMAL;
        }
        return super.getWrappingTypeForTagBegin(xmlTag);
    }

    @Override
	public boolean insertLineBreakBeforeTag(@Nullable final XmlTag xmlTag) {
        return xmlTag != null && super.insertLineBreakBeforeTag(xmlTag);
    }

    @Override
	public boolean removeLineBreakBeforeTag(@Nullable final XmlTag xmlTag) {
        return xmlTag != null && super.removeLineBreakBeforeTag(xmlTag);
    }

   @Override
   public boolean keepWhiteSpacesInsideTag(@Nullable final XmlTag xmlTag) {
       return  xmlTag != null && super.keepWhiteSpacesInsideTag(xmlTag);
   }

   @Override
   public boolean indentChildrenOf(@Nullable final XmlTag xmlTag) {
       return xmlTag == null || super.indentChildrenOf(xmlTag);
   }

   @Override
   public boolean isTextElement(@Nullable final XmlTag xmlTag) {
       return xmlTag == null || super.isTextElement(xmlTag);
   }


   @Override
   public int getTextWrap(@Nullable final XmlTag xmlTag) {
       if (xmlTag != null && xmlTag.getContainingFile() instanceof RHTMLFile) {
           return CodeStyleSettings.DO_NOT_WRAP;
       }
       return getSettings().HTML_TEXT_WRAP;
   }
}
