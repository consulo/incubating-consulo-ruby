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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.ui;

import consulo.execution.ui.awt.BrowseModuleValueActionListener;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.RCacheUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestsRunConfigurationForm;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */
public class TestCaseClassBrowser extends BrowseModuleValueActionListener
{
	private final String myTitle;
	public GlobalSearchScope mySearchScope;
	public RTestsRunConfigurationForm myForm;
	//public final TestCachingFilter myClassCachingFilter;

	public TestCaseClassBrowser(final Project project, final RTestsRunConfigurationForm form)
	{
		super(project);
		myForm = form;

		myTitle = RBundle.message("choose.test.class.dialog.title");
		mySearchScope = GlobalSearchScope.projectScope(getProject());
		//myClassCachingFilter = new TestCachingFilter();
	}

	@Override
	@Nullable
	protected String showDialog()
	{
		// TODO reuse TreeClassChooserFactory.getInstance(getProject()).newChooser(RClass.class)

//		configureDialog(dialog);
//
//		// get result
//		if(!dialog.showDialog())
//		{
//			//on cancel
//			return null;
//		}
//		// on ok
//		final RClass rClass = dialog.getSelectedClass();
//		onClassChoosen(rClass);
//		if(rClass == null)
//		{
//			return RBundle.message("run.configuration.tests.no.data");
//		}
//		final String qualifiedName = RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(rClass, null);
//		// Here assertion is more correct, but I don't believe, that
//		// somebody will be able to post all necessary about his project in error submiter.
//		// maybe such error message induce him to ask us for help
//		if(TextUtil.isEmpty(qualifiedName))
//		{
//			return RBundle.message("run.configuration.tests.no.qualified.name");
//		}
//		return qualifiedName;
		return null;
	}

	protected void onClassChoosen(@Nullable final RClass psiClass)
	{
		String testScriptPath;
		if(psiClass == null)
		{
			testScriptPath = RBundle.message("run.configuration.tests.no.data");
		}
		else
		{
			final VirtualFile file = psiClass.getVirtualFile();
			assert file != null;
			testScriptPath = file.getPath();
		}
		myForm.setTestScriptPath(testScriptPath);
	}

//	private void configureDialog(final TreeRClassChooserDialog dialog)
//	{
//		final String qualifiedName = getText();
//
//		final String path = myForm.getTestScriptPath();
//		final VirtualFile file = TextUtil.isEmpty(path) ? null : LocalFileSystem.getInstance().findFileByPath(path);
//
//		if(file == null)
//		{
//			return;
//		}
//
//		final RClass rClass = findClass(qualifiedName, file);
//		if(rClass != null)
//		{
//			dialog.selectClass(rClass);
//		}
//
//		if(file.isDirectory())
//		{
//			dialog.selectFile(PsiManager.getInstance(getProject()).findDirectory(file));
//		}
//		else
//		{
//			dialog.selectFile(PsiManager.getInstance(getProject()).findFile(file));
//		}
//	}

	@Nullable
	protected RClass findClass(@Nonnull final String qualifiedNameClassName, @Nonnull final VirtualFile scriptFile)
	{

		final Project project = getProject();
		final RClass rVClass = RCacheUtil.getClassByNameInScriptInRubyTestMode(qualifiedNameClassName, project, mySearchScope, scriptFile, null);
		if(rVClass != null)
		{
			final RPsiElement psiElem = RVirtualPsiUtil.findPsiByVirtualElement(rVClass, project);
			if(psiElem instanceof RClass)
			{
				return (RClass) psiElem;
			}
		}
		return null;
	}

	//    protected TreeClassChooser.ClassFilterWithScope getFilter() throws NoFilterException {
	//        final ConfigurationModuleSelector moduleSelector = getModuleSelector();
	//        final Module module = moduleSelector.getModule();
	//        if (module == null) {
	//            throw NoFilterException.moduleDoesntExist(moduleSelector);
	//        }
	//        final TreeClassChooser.ClassFilterWithScope classFilter;
	//        try {
	//            final JUnitConfiguration configurationCopy = new JUnitConfiguration(ExecutionBundle.message("default.junit.configuration.name"), getProject(), JUnitConfigurationType.getInstance().getConfigurationFactories()[0]);
	//            applyEditorTo(configurationCopy);
	//            classFilter = TestClassFilter.create(configurationCopy.getTestObject().getSourceScope(), configurationCopy.getConfigurationModule().getModule());
	//        }
	//        catch (JUnitUtil.NoJUnitException e) {
	//            throw NoFilterException.noJUnitInModule(module);
	//        }
	//        return classFilter;
	//    }


	// Use this filter only if PsiElements can't be changed at that time
	// e.g. in modal select smth. dialog
//	private static class TestCachingFilter implements TreeRClassChooserDialog.ClassFilter
//	{
//		private HashMap<Integer, Pair<SoftReference<RClass>, Boolean>> processedElements = new HashMap<Integer, Pair<SoftReference<RClass>, Boolean>>();
//
//		@Override
//		public boolean isAccepted(@Nonnull final RClass rVClass)
//		{
//			final VirtualFile virtualFile = rVClass.getVirtualFile();
//			if(virtualFile == null)
//			{
//				return false;
//			}
//
//			final int key = rVClass.hashCode();
//			final Pair<SoftReference<RClass>, Boolean> pair = processedElements.get(key);
//			boolean isTestCase;
//			if(pair == null || pair.first.get() != rVClass)
//			{
//				isTestCase = RTestUnitUtil.isClassUnitTestCase(rVClass, null);
//				//TODO replace rVClass with its path..
//				processedElements.put(key, new Pair<SoftReference<RClass>, Boolean>(new SoftReference<RClass>(rVClass), isTestCase));
//
//			}
//			else
//			{
//				isTestCase = pair.second;
//			}
//			return isTestCase;
//		}
//	}
}
