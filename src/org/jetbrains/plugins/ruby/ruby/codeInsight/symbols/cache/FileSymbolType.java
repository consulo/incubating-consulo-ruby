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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache;

/**
 * Created by IntelliJ IDEA.
*
* @author: oleg
* @date: Oct 7, 2007
*/
public enum FileSymbolType {
    MODIFIABLE,

// Built in types
    BUILT_IN,
    RAILS_BUILT_IN,

// Module layers
    MODULE_LAYER,
    RAILS_MODULE_LAYER,

// Rails specific layers
    VENDOR_LAYER,
    LIBS_LAYER,
    MODELS_LAYER,
    CONTROLLERS_AND_HELPERS_LAYER,
    MAILERS_LAYER,
    WEBSERVICES_LAYER
}
