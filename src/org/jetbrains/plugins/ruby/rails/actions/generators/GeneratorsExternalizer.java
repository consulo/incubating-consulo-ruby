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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.12.2006
 */
public class GeneratorsExternalizer extends SettingsExternalizer {

    protected final static Logger LOG = Logger.getInstance(GeneratorsExternalizer.class.getName());

    @NonNls
    private static final String GENERATORS_GROUP = "GeneratorsGroup";
    @NonNls
    private static final String GENERATOR = "Generator";
    @NonNls
    private static final String GENERATOR_NAME = "name";
    @NonNls
    private static final String GENERATORS_FILE_NAME = ".generators";
    @NonNls
    private final String SETTINGS = "Settings";

    @Nullable
    public static File getDataFile(@NotNull final String railsApplicHomeDirPath) {
        return new File(railsApplicHomeDirPath + File.separator + GENERATORS_FILE_NAME);

    }

    @Override
	public String getID() {
        return TextUtil.EMPTY_STRING;
    }

    /**
     * Loads generators list from input stream.
     * @param inputStream data source
     * @return generators list
     */
    @Nullable
    public String[] loadGeneratorList(final InputStream inputStream) {
        final SAXBuilder parser = new SAXBuilder();
        try {
            final Document doc = parser.build(inputStream);
            final Element root = doc.getRootElement();
            if (SETTINGS.equals(root.getName())) {
                for (Object o : root.getChildren()) {
                    if (o instanceof Element) {
                        Element content = (Element)o;
                        if (GENERATORS_GROUP.equals(content.getName())) {
                            return readExternal(content);
                        }
                    }
                }
            }
            return new String[0];
        } catch (JDOMException e) {
            LOG.warn(e);
        } catch (IOException e) {
            LOG.warn(e);
        }
        return null;
    }

    /**
     * Loads list of generators for rails application from file system.
     * Uses loadGeneratorList(final InputStream inputStream) and getDataFile(@NotNull final String railsApplicHomeDirPath).
     * @param railsApplicHomeDirPath Rails Application Home Directory Path
     * @return generators list or null
     */
    @Nullable
    public String[] loadGeneratorList(@NotNull final String railsApplicHomeDirPath) {
        final File cachedList = getDataFile(railsApplicHomeDirPath);
        try {
            if (cachedList != null && cachedList.exists() && isUpToDate(cachedList, railsApplicHomeDirPath)) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(cachedList);
                    return loadGeneratorList(inputStream);
                } catch (FileNotFoundException e) {
                    // shouldn't be thrown
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            // shouldn't be thrown
        }
        return null;
    }

    private boolean isUpToDate(@NotNull final File cachedList,
                               @NotNull final String railsApplicHomeDirPath) {
        if (!cachedList.exists()) {
            return false;
        }
        final VirtualFile rootFile = LocalFileSystem.getInstance().findFileByPath(railsApplicHomeDirPath);
        assert rootFile != null;
        final VirtualFile generatorsDir = rootFile.findChild(GeneratorsUtil.GENERATORS_DIR);
        final VirtualFile cachedListVF = rootFile.findChild(GENERATORS_FILE_NAME);

        return cachedListVF != null
               && !GeneratorsUtil.existsNewerThanTimeStamp(generatorsDir,
                                                            cachedListVF.getTimeStamp());
    }

    /**
     * Saves generators list in file system. Uses xml presentation.
     * @param generators generators names
     * @param railsApplicHomeDirPath Rails Application Home Dir Path
     */
    public void saveGeneratorList(final String[] generators,
                                  @NotNull final String railsApplicHomeDirPath) {
        final File dataFile = getDataFile(railsApplicHomeDirPath);
        try {
            if (dataFile != null && dataFile.exists()) {
                if (dataFile.exists()) {
                    if (!dataFile.delete()) {
                        LOG.warn(RBundle.message("settings.generators.cant.save.message", dataFile.getPath()));
                        return;
                    }
                }
                dataFile.createNewFile();
            }
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(dataFile);
                saveGenratosList(generators, outputStream);
            } catch (FileNotFoundException e) {
                // shouldn't be thrown
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            LOG.warn(e);
        }
    }

    /**
     * Saves generators list in OuptutStream. Uses xml presentation.
     * @param generators generators list
     * @param outStream output stream
     */
    public void saveGenratosList(final String[] generators,
                              final OutputStream outStream) {
        final Element content = writeExternal(generators);
        final Element root = new Element(SETTINGS);
//        final String text = "This file was automatically generated by Ruby plugin.\n"
//                + "           You are allowed to: \n"
//                + "               1. Reorder generators\n"
//                + "               2. Remove generators\n"
//                + "               3. Add installed generators\n"
//                + "           To add new installed generators automatically delete this file and reload the project.\n";
        final String text = RBundle.message("settings.generators.externalizer.info");
        root.addContent(new Comment(text));
        root.addContent(content);
        
        final Document doc = new Document(root);
        final XMLOutputter outputter = new XMLOutputter();
        try {
            outputter.output(doc, outStream);
        }
        catch (IOException e) {
            LOG.warn(e);
        }
    }

    @NotNull
    protected String[] readExternal(final Element element) {
        ArrayList<String> generators = new ArrayList<String>();
        if (GENERATORS_GROUP.equals(element.getName())) {
            final List list = element.getChildren();
            for (Object o : list) {
                if (o instanceof Element) {
                    final Element generator = (Element)o;
                    if (GENERATOR.equals(generator.getName())) {
                        generators.add(getAttributeFromElement(GENERATOR_NAME, generator));
                    }
                }
            }
        }
        return generators.toArray(new String[generators.size()]);
    }

    protected Element writeExternal(@NotNull final String[] generators
    ) {
        final Element groupElement = new Element(GENERATORS_GROUP);
        for (String name : generators) {
            final Element generatorElement = new Element(GENERATOR);
            groupElement.addContent(generatorElement);
            storeAttributeInElement(GENERATOR_NAME, name, generatorElement);
        }
        return groupElement;
    }
}