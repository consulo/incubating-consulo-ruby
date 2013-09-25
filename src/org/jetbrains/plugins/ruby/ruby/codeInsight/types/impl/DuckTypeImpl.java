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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types.impl;

import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.DuckType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 25, 2007
 */
public class DuckTypeImpl implements DuckType {
    private Map<String, Collection<Message>> myMessages = new HashMap<String, Collection<Message>>();

    public void clear(){
        myMessages.clear();
    }

    public void addMessage(@NotNull final Message message){
        // Add messsage to all message
        addForName(message, null);
        // Add message to map by it`s name
        addForName(message, message.getName());
    }

    private void addForName(@NotNull final Message message, final String name) {
        Collection<Message> messages = myMessages.get(name);
        if (messages == null){
            messages = new ArrayList<Message>();
            myMessages.put(name, messages);
        }
        messages.add(message);
    }

    @Override
	@NotNull
    public Collection<Message> getMessages() {
        return getMessagesForName(null);
    }

    @Override
	public Collection<Message> contains(@NotNull final DuckType type) {
        final List<Message> messages = new ArrayList<Message>();
        for (Message typeMessage : type.getMessages()) {
            boolean found = false;
            for (Message message : getMessages()) {
                found = found || message.matchesMessage(typeMessage);
            }
            if (!found){
                messages.add(typeMessage);
            }
        }
        return messages;
    }

    @Override
	public Collection<Message> getMessagesForName(@Nullable String name) {
        final Collection<Message> messages = myMessages.get(name);
        return messages!=null ? messages : Collections.<Message>emptyList();
    }

    public void addMessages(final Collection<Message> messages) {
        for (Message message : messages) {
            addMessage(message);
        }
    }
}
