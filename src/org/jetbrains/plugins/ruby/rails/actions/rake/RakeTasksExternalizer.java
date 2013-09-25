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

package org.jetbrains.plugins.ruby.rails.actions.rake;

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
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorsUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTaskSerializableImpl;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import java.io.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 01.12.2006
 */

/**
 * Serializes and deserializes rake tasks tree in xml form.
 * For example:
 * <RakeGroup description="" fullCmd="" taksId="rake">
 *     <RakeGroup description="" fullCmd="" taksId="db">
 *         <RakeGroup description="" fullCmd="" taksId="fixtures">
 *             <RakeTask
 *                     description="Load fixtures into the current environment's database.  Load specific fixtures using FIXTURES=x,y"
 *                     fullCmd="db:fixtures:load" taksId="load"/>
 *         </RakeGroup>
 *         <RakeTask
 *                 description="Migrate the database through scripts in db/migrate. Target specific version with VERSION=x"
 *                 fullCmd="db:migrate" taksId="migrate"/>
 *         <RakeGroup description="" fullCmd="" taksId="schema">
 *             <RakeTask
 *                     description="Create a db/schema.rb file that can be portably used against any DB supported by AR"
 *                     fullCmd="db:schema:dump" taksId="dump"/>
 *         </RakeGroup>
 *     </RakeGroup>
 * </RakeGroup>
 */
public class RakeTasksExternalizer extends SettingsExternalizer {

    protected final static Logger LOG = Logger.getInstance(RakeTasksExternalizer.class.getName());
    @NonNls
    private static final String RAKE_GROUP = "RakeGroup";
    @NonNls
    private static final String RAKE_TASK = "RakeTask";
    @NonNls
    private static final String RAKE_TASK_ID = "taksId";
    @NonNls
    private static final String RAKE_TASK_DESCRIPTION = "description";
    @NonNls
    private static final String RAKE_TASK_FULL_CMD = "fullCmd";
    @NonNls
    private static final String RAKE_TASKS_FILE_NAME = ".rakeTasks";
    @NonNls
    private final String SETTINGS = "Settings";

    @Nullable
    public static File getDataFile(@NotNull final String railsApplicHomeDirPath) {
        return new File(railsApplicHomeDirPath + File.separator + RAKE_TASKS_FILE_NAME);
    }

    @Override
	public String getID() {
        return TextUtil.EMPTY_STRING;
    }

    /**
     * Loads rake tasks from input stream.
     * @param reader data source
     * @return rake tasks tree
     */
    @Nullable
    public RakeTask loadRakeTasksTree(final Reader reader) {
        final SAXBuilder parser = new SAXBuilder();
        try {
            final Document doc = parser.build(reader);
            final Element root = doc.getRootElement();
            if (SETTINGS.equals(root.getName())) {
                for (Object o : root.getChildren()) {
                    if (o instanceof Element) {
                        Element content = (Element)o;
                        if (RAKE_GROUP.equals(content.getName())) {
                            final List roots = root.getChildren();
                            assert roots.size()==1;
                            return readExternal((Element)roots.get(0), null);
                        }
                    }
                }
            }
        } catch (JDOMException e) {
            LOG.warn(e);
        } catch (IOException e) {
            LOG.warn(e);
        }
        return null;
    }

    /**
     * Loads list of rake tasks for rails application from file system.
     * Uses loadRakeTasksTree(final InputStream inputStream) and getDataFile(final Module module).
     * @param railsApplicHomeDirPath Rails Application Home directory
     * @return rake tasks tree
     */
    @Nullable
    public RakeTask loadRakeTasksTree(@NotNull final String railsApplicHomeDirPath) {
        final File cachedTasks = getDataFile(railsApplicHomeDirPath);
        try {
            if (cachedTasks != null && cachedTasks.exists() && isUpToDate(cachedTasks, railsApplicHomeDirPath)) {
                FileReader inputStream = null;
                try {
                    inputStream = new FileReader(cachedTasks);
                    return loadRakeTasksTree(inputStream);
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

     private boolean isUpToDate(@NotNull final File cachedTasks,
                                @NotNull final String railsApplicHomeDirPath) {
         if (!cachedTasks.exists()) {
             return false;
         }

         final VirtualFile rootFile = VirtualFileUtil.findFileByLocalPath(railsApplicHomeDirPath);
         if (rootFile == null) {
             return false;
         }
         final VirtualFile libDir = rootFile.findChild(RakeUtil.LIB);
         final VirtualFile libTasksDir =
                 libDir != null ? libDir.findChild(RakeUtil.TASKS) : null;
         final VirtualFile rakeFile = rootFile.findChild(RakeUtil.RAKE_FILE);
         final VirtualFile cachedTasksVF = LocalFileSystem.getInstance().findFileByIoFile(cachedTasks);
         
         if (cachedTasksVF == null) {
             return false;
         }

         final long tStamp = cachedTasksVF.getTimeStamp();
         return !(GeneratorsUtil.existsNewerThanTimeStamp(rakeFile, tStamp)
                 || GeneratorsUtil.existsNewerThanTimeStamp(libTasksDir, tStamp));
    }

    /**
     * Saves rake tasks tree in file system. Uses xml presentation.
     * @param rootTask the root of tasks tree
     * @param railsApplicHomeDirPath Rails Application Home Directory Path
     */
    public void saveRakeTasksTree(@NotNull final RakeTask rootTask,
                                  @NotNull final String railsApplicHomeDirPath) {
        final File dataFile =  getDataFile(railsApplicHomeDirPath);
        if (dataFile == null) {
            //module without content roots
            return;
        }

        try {
            if (dataFile.exists()) {
                if (!dataFile.delete()) {
                    LOG.warn(RBundle.message("settings.raketasks.cant.save.message", dataFile.getPath()));
                    return;
                }
            }
            dataFile.createNewFile();

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(dataFile);
                saveRakeTasksTree(rootTask, fileWriter);
            } catch (FileNotFoundException e) {
                // shouldn't be thrown
            } finally {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
        } catch (IOException e) {
            LOG.warn(e);
        }
    }

    /**
     * Saves rake tasks tree in OuptutStream. Uses xml presentation.
     * @param rootTask the root of tasks tree
     * @param writer output stream
     */
    public void saveRakeTasksTree(final RakeTask rootTask,
                              final Writer writer) {
        final Element content = writeExternal(rootTask, null);
        final Element root = new Element(SETTINGS);
        final String text = RBundle.message("settings.raketasks.externalizer.info");
        root.addContent(new Comment(text));
        root.addContent(content);

        final Document doc = new Document(root);
        final XMLOutputter outputter = new XMLOutputter();
        try {
            outputter.output(doc, writer);
        }
        catch (IOException e) {
            LOG.warn(e);
        }
    }

    @NotNull
    protected RakeTask readExternal(final Element element, final RakeTaskSerializableImpl parent) {
        final List list = element.getChildren();
        final RakeTaskSerializableImpl task =
                new RakeTaskSerializableImpl(getAttributeFromElement(RAKE_TASK_ID, element),
                                             getAttributeFromElement(RAKE_TASK_DESCRIPTION, element),
                                             getAttributeFromElement(RAKE_TASK_FULL_CMD, element),
                                             RAKE_GROUP.equals(element.getName()),
                                             parent);

        for (Object o : list) {
            if (o instanceof Element) {
                final Element childElement = (Element)o;
                if (RAKE_TASK.equals(childElement.getName()) ||
                    RAKE_GROUP.equals(childElement.getName())) {
                    task.addSubTask(readExternal(childElement, task));
                }
            }
        }
        return task;
    }

    protected Element writeExternal(@NotNull final RakeTask rakeTask,
                                   @Nullable final Element parent) {
        final Element element = new Element(rakeTask.isGroup() ? RAKE_GROUP : RAKE_TASK);
        storeAttributeInElement(RAKE_TASK_DESCRIPTION, rakeTask.getDescription(),
                                element);
        storeAttributeInElement(RAKE_TASK_FULL_CMD, rakeTask.getFullCommand(),
                                element);
        storeAttributeInElement(RAKE_TASK_ID, rakeTask.getId(),
                                element);
        if (parent != null) {
            parent.addContent(element);
        }

        final List<? extends RakeTask> subTasks = rakeTask.getSubTasks();
        for (RakeTask subTask : subTasks) {
            writeExternal(subTask, element);
        }
        return element;
    }
}
