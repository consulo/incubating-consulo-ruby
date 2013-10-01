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

package org.jetbrains.plugins.ruby.support;

import java.awt.Component;
import java.awt.Cursor;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.jetbrains.annotations.NotNull;
import com.intellij.ide.BrowserUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 14, 2007
 */
public class OpenLinkInBrowserHyperlinkListener implements HyperlinkListener
{
	private final Component myComponent;

	public OpenLinkInBrowserHyperlinkListener(@NotNull final Component component)
	{
		myComponent = component;
	}

	@Override
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		final HyperlinkEvent.EventType eventType = e.getEventType();
		if(eventType.equals(HyperlinkEvent.EventType.ACTIVATED))
		{
			BrowserUtil.launchBrowser(e.getURL().toExternalForm());
		}
		else if(eventType.equals(HyperlinkEvent.EventType.ENTERED))
		{
			myComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		else if(eventType.equals(HyperlinkEvent.EventType.EXITED))
		{
			myComponent.setCursor(Cursor.getDefaultCursor());
		}
	}
}
