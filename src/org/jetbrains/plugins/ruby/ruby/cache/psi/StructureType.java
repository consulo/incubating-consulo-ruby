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

package org.jetbrains.plugins.ruby.ruby.cache.psi;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 20, 2007
 */
public class StructureType {
    public static StructureType ALL = new StructureType();

    public static StructureType FAKE = new StructureType();

    public static StructureType FILE = new StructureType();
    public static StructureType MODULE = new StructureType();
    public static StructureType CLASS = new StructureType();
    public static StructureType OBJECT_CLASS = new StructureType();
    public static StructureType METHOD = new StructureType();
    public static StructureType SINGLETON_METHOD = new StructureType();

    public static StructureType CALL_REQUIRE = new StructureType();
    public static StructureType CALL_LOAD = new StructureType();

    public static StructureType CALL_INCLUDE = new StructureType();
    public static StructureType CALL_EXTEND = new StructureType();


    public static StructureType ALIAS = new StructureType();

    public static StructureType FIELD_ATTR_CALL = new StructureType();

// JRuby specific
    public static StructureType CALL_IMPORT = new StructureType();
    public static StructureType CALL_INCLUDE_CLASS = new StructureType();
    public static StructureType CALL_INCLUDE_PACKAGE = new StructureType();


    private StructureType() {
    }

    public boolean isContainer(){
        return this == FILE || this == MODULE || this == CLASS || this == OBJECT_CLASS || this == METHOD || this == SINGLETON_METHOD;
    }

    public boolean isStructureCall(){
        return this == CALL_INCLUDE || this == CALL_EXTEND ||
                this == CALL_REQUIRE || this == CALL_LOAD ||
                this == FIELD_ATTR_CALL ||
                this == CALL_IMPORT || this == CALL_INCLUDE_CLASS || this == CALL_INCLUDE_PACKAGE;
    }

    public boolean isMethod() {
        return this == METHOD || this == SINGLETON_METHOD;
    }
}
