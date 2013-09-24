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

package org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 14.08.2006
 */

/**
 * Ruby defined functions
 */
public enum RubyCallType {
    RAISE_CALL,
    REQUIRE_CALL, LOAD_CALL,
    INCLUDE_CALL, EXTEND_CALL,

    ATTR_READER_CALL, ATTR_WRITER_CALL, ATTR_ACCESSOR_CALL,
    ATTR_INTERNAL_CALL, CATTR_ACCESSOR_CALL,
    PRIVATE_CALL, PUBLIC_CALL, PROTECTED_CALL,
    IMPORT_CALL, INCLUDE_CLASS_CALL, INCLUDE_PACKAGE_CALL,
    REQUIRE_GEM_CALL, GEM_CALL,
    UNKNOWN;

    public boolean isAttributeCall(){
        return this == ATTR_ACCESSOR_CALL || 
                this == ATTR_READER_CALL ||
                this == ATTR_WRITER_CALL ||
                this == ATTR_INTERNAL_CALL ||
                this == CATTR_ACCESSOR_CALL;

    }

    public boolean isGemCall() {
        return this == REQUIRE_GEM_CALL || this == GEM_CALL;
    }

    public boolean isFileRef(){
        return this == REQUIRE_CALL || this == LOAD_CALL;
    }
}
