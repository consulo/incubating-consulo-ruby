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

package org.jetbrains.plugins.ruby.rails.nameConventions;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.support.AbstractRORTestCase;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 27, 2007
 */
public class ViewConventionsTest extends AbstractRORTestCase {

    public void testFindViewsInFolder() {
        final VirtualFile dir = getFileByRelativePath("");
        assertNotNull(dir);
        final List<VirtualFile> list = ViewsConventions.findViewsInFolder("store", dir.getUrl());
        assertEquals(4, list.size());
    }

    public void testFindLayoutsInFolder() {
        final VirtualFile dir = getFileByRelativePath("");
        assertNotNull(dir);
        final List<VirtualFile> list = ViewsConventions.findLayoutsInFolder("store", dir.getUrl());
        assertEquals(3, list.size());
    }

    public void testGetViewsFolderName() {
        assertEquals("admin",  ViewsConventions.getViewsFolderName("AdminController"));
        assertEquals("my_admin",  ViewsConventions.getViewsFolderName("MyAdminController"));
        assertEquals("application",  ViewsConventions.getViewsFolderName("ApplicationController"));
        assertEquals(TextUtil.EMPTY_STRING, ViewsConventions.getViewsFolderName(null));
    }

    public void testGetActionMethodNameByView() {
        VirtualFile file;

        file = getFileByRelativePath("/store.html.erb");
        assertNotNull(file);
        assertEquals("store", ViewsConventions.getActionMethodNameByView(file));

        file = getFileByRelativePath("/store1.html.erb");
        assertNotNull(file);
        assertEquals("store1", ViewsConventions.getActionMethodNameByView(file));

        file = getFileByRelativePath("/store2.rhtml");
        assertNotNull(file);
        assertEquals("store2", ViewsConventions.getActionMethodNameByView(file));

        file = getFileByRelativePath("/list.atom.builder");
        assertNotNull(file);
        assertEquals("list", ViewsConventions.getActionMethodNameByView(file));

        file = getFileByRelativePath("/list2.xml.builder");
        assertNotNull(file);
        assertEquals("list2", ViewsConventions.getActionMethodNameByView(file));

        file = getFileByRelativePath("/list2.rxml");
        assertNotNull(file);
        assertEquals("list2", ViewsConventions.getActionMethodNameByView(file));

        //incorrect examples
        file = getFileByRelativePath("/wrong1.xml.rb");
        assertNotNull(file);
        assertEquals("wrong1", ViewsConventions.getActionMethodNameByView(file));

        file = getFileByRelativePath("/wrong2.wrong3.xml.builder");
        assertNotNull(file);
        assertEquals("wrong2.wrong3", ViewsConventions.getActionMethodNameByView(file));
    }
    public void testIsRHTMLFile() {
        assertTrue(ViewsConventions.isRHTMLFile("file.rhtml"));
        assertTrue(ViewsConventions.isRHTMLFile("file.erb"));
        assertTrue(ViewsConventions.isRHTMLFile("file.html.erb"));
        assertTrue(ViewsConventions.isRHTMLFile("file.xxx.erb"));

        assertFalse(ViewsConventions.isRHTMLFile("file"));
        assertFalse(ViewsConventions.isRHTMLFile("file.html"));
        assertFalse(ViewsConventions.isRHTMLFile("file.xml"));
        assertFalse(ViewsConventions.isRHTMLFile("file.builder"));
    }

    public void testIsRXMLFile() {
        assertTrue(ViewsConventions.isRXMLFile("file.rxml"));
        assertTrue(ViewsConventions.isRXMLFile("file.builder"));
        assertTrue(ViewsConventions.isRXMLFile("file.xxx.builder"));

        assertFalse(ViewsConventions.isRXMLFile("file"));
        assertFalse(ViewsConventions.isRXMLFile("file.rhtml"));
        assertFalse(ViewsConventions.isRXMLFile("file.xml"));
    }

    public void testIsRJSFile() {
        assertTrue(ViewsConventions.isRJSFile("file.rjs"));

        assertFalse(ViewsConventions.isRJSFile("file"));
        assertFalse(ViewsConventions.isRJSFile("file.rhtml"));
        assertFalse(ViewsConventions.isRJSFile("file.xml"));
        assertFalse(ViewsConventions.isRJSFile("file.builder"));
    }

    public void testIsApplicationLayoutFile() {
        VirtualFile file;
        file = getFileByRelativePath("/store2.rhtml");
        assertNotNull(file);
        assertFalse(ViewsConventions.isApplicationLayoutFile(file));

        file = getFileByRelativePath("/application.rhtml");
        assertNotNull(file);
        assertTrue(ViewsConventions.isApplicationLayoutFile(file));

        file = getFileByRelativePath("/application.html.erb");
        assertNotNull(file);
        assertTrue(ViewsConventions.isApplicationLayoutFile(file));
    }

    public void testIsValidLayoutFileName() {
        assertTrue(ViewsConventions.isValidLayoutFileName("file.rhtml"));
        assertTrue(ViewsConventions.isValidLayoutFileName("file.erb"));
        assertTrue(ViewsConventions.isValidLayoutFileName("file.html.erb"));
        assertTrue(ViewsConventions.isValidLayoutFileName("file.xxx.erb"));
        assertTrue(ViewsConventions.isValidLayoutFileName("file.builder"));

        assertFalse(ViewsConventions.isValidLayoutFileName("file"));
        assertFalse(ViewsConventions.isValidLayoutFileName("file.html"));
        assertFalse(ViewsConventions.isValidLayoutFileName("file.xml"));
    }

    public void testIsLayoutFile() {
        VirtualFile file;

        file = getFileByRelativePath("/store.html.erb");
        assertNotNull(file);
        assertTrue(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/store1.html.erb");
        assertNotNull(file);
        assertTrue(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/store2.rhtml");
        assertNotNull(file);
        assertTrue(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/list.atom.builder");
        assertNotNull(file);
        assertTrue(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/list2.xml.builder");
        assertNotNull(file);
        assertTrue(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/list2.rxml");
        assertNotNull(file);
        assertTrue(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/list.rjs");
        assertNotNull(file);
        assertFalse(ViewsConventions.isLayoutFile(file));

        //incorrect examples
        file = getFileByRelativePath("/wrong1.xml.rb");
        assertNotNull(file);
        assertFalse(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/folder.rhtml");
        assertNotNull(file);
        assertFalse(ViewsConventions.isLayoutFile(file));

        file = getFileByRelativePath("/wrong2.wrong3.xml.builder");
        assertNotNull(file);
        assertTrue(ViewsConventions.isLayoutFile(file));
    }

    public void testIsValidViewFileName() {
        assertTrue(ViewsConventions.isValidViewFileName("file.rhtml"));
        assertTrue(ViewsConventions.isValidViewFileName("file.erb"));
        assertTrue(ViewsConventions.isValidViewFileName("file.xxx.erb"));

        assertTrue(ViewsConventions.isValidViewFileName("file.rjs"));

        assertTrue(ViewsConventions.isValidViewFileName("file.rxml"));
        assertTrue(ViewsConventions.isValidViewFileName("file.builder"));
        assertTrue(ViewsConventions.isValidViewFileName("file.xxx.builder"));

        assertFalse(ViewsConventions.isValidViewFileName("file"));
        assertFalse(ViewsConventions.isValidViewFileName("file.html"));
        assertFalse(ViewsConventions.isValidViewFileName("file.xml"));
        assertFalse(ViewsConventions.isValidViewFileName("file.rb"));
    }

    public void testIsPartialViewName() {
        assertTrue(ViewsConventions.isPartialViewName("_file.rhtml"));
        assertTrue(ViewsConventions.isPartialViewName("_file.erb"));
        assertTrue(ViewsConventions.isPartialViewName("_file.xxx.erb"));
        assertTrue(ViewsConventions.isPartialViewName("_file.rjs"));
        assertTrue(ViewsConventions.isPartialViewName("_file.rxml"));
        assertTrue(ViewsConventions.isPartialViewName("_file.builder"));
        assertTrue(ViewsConventions.isPartialViewName("_file.xxx.builder"));

        assertFalse(ViewsConventions.isPartialViewName("file.xxx.builder"));
    }

    public void testIsViewFile() {
        VirtualFile file;

        file = getFileByRelativePath("/store.html.erb");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/store1.html.erb");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/store2.rhtml");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/list.atom.builder");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/list2.xml.builder");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/list2.rxml");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/list.rjs");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));

        //incorrect examples
        file = getFileByRelativePath("/wrong1.xml.rb");
        assertNotNull(file);
        assertFalse(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/folder.rhtml");
        assertNotNull(file);
        assertFalse(ViewsConventions.isViewFile(file));

        file = getFileByRelativePath("/wrong2.wrong3.xml.builder");
        assertNotNull(file);
        assertTrue(ViewsConventions.isViewFile(file));
    }
}
