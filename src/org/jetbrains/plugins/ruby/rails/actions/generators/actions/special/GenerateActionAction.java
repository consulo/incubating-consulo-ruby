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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions.special;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.generators.GenerateDialogs;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.AnActionUtil;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorAction;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RBodyStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.RBodyStatementNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.RCompoundStatementNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RClassNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.ActionRunner;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 27.12.2006
 */
public class GenerateActionAction extends SimpleGeneratorAction
{
	protected static class ActionStubInserter implements Runnable
	{
		protected final static Logger LOG = Logger.getInstance(ActionStubInserter.class.getName());
		protected DataContext myDataContext;
		public String myMethodName;

		public ActionStubInserter(@NotNull final DataContext dataContext, @NotNull final String methodName)
		{
			myDataContext = dataContext;
			myMethodName = methodName;
		}

		@Override
		public void run()
		{
			final Editor editor = myDataContext.getData(CommonDataKeys.EDITOR);
			final PsiFile psiFile = myDataContext.getData(CommonDataKeys.PSI_FILE);
			if(psiFile == null || editor == null)
			{
				LOG.error("Psi element couldn't be found for action.");
				return;
			}

			final RClass rClass = determineControllerClass(myDataContext);
			if(rClass == null)
			{
				return;
			}
			final Project project = rClass.getProject();

			final Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
			assert document != null;

			//TODO Reimplement this Hack
			final String tip = RBundle.message("template.rails.action.implement.body");
			final String text = "def " + myMethodName + "\n  " + tip + "\nend";


			CommandProcessor.getInstance().executeCommand(project, new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						ActionRunner.runInsideWriteAction(new ActionRunner.InterruptibleRunnable()
						{
							@Override
							public void run() throws Exception
							{
								// insert method stub
								EditorModificationUtil.insertStringAtCaret(editor, text);
								PsiDocumentManager.getInstance(project).commitDocument(document);
							}
						});
						ActionRunner.runInsideWriteAction(new ActionRunner.InterruptibleRunnable()
						{
							@Override
							public void run() throws Exception
							{
								// insert method stub
								editor.getCaretModel().moveCaretRelatively(0, -1, false, false, true);

								// reformat method code
								final RMethod newMethod = RContainerUtil.getMethodByName(rClass, myMethodName);
								if(newMethod != null)
								{
									final TextRange myTextRange = newMethod.getTextRange();
									try
									{
										CodeStyleManager.getInstance(project).reformatText(psiFile, myTextRange.getStartOffset(), myTextRange.getEndOffset());
										CodeStyleManager.getInstance(project).adjustLineIndent(document, editor.getCaretModel().getOffset());
									}
									catch(IncorrectOperationException e)
									{
										LOG.error("Inserting method template for : " + myMethodName + ". Code formatting failed.", e);
									}
								}
							}
						});
					}
					catch(Exception e)
					{
						LOG.error(e);
					}
				}
			}, RBundle.message("popup.generate.action.command"), null);
			// insert tip
/*
			EditorModificationUtil.insertStringAtCaret(myEditor, tip);
            PsiDocumentManager.getInstance(project).commitDocument(document);
            myEditor.getCaretModel().moveCaretRelatively(-tip.length(),
                                                         0, false, false, true);
*/

            /* PsiElementFactory factory = rClass.getManager().getElementFactory();
            try {
                if (rClass.isWritable()) {
                    final PsiMethod psiMethod =
                            factory.createMethodFromText(methodName, currentElem);
                    psiMethod.getBody().delete();

                    TemplateBuilder builder = new TemplateBuilder(method);
                    final CmpFieldTypeExpression expression = new CmpFieldTypeExpression(psiManager);
                    builder.replaceElement(method.getReturnTypeElement(), expression);
                    TemplateGenerationInfo info = new TemplateGenerationInfo(builder.buildTemplate(), method);
                }
            }
            catch (IncorrectOperationException e) {
                LOG.error("Error inserting javadoc for method: " + psiMethod.getName(), e);
            }*/
		}
	}

	public GenerateActionAction()
	{
		this(null);
	}


	public GenerateActionAction(final String actionName)
	{
		this(actionName != null ? actionName : RBundle.message("popup.generate.action.text"), RBundle.message("popup.generate.action.description"), RailsIcons.RAILS_ACTION_NODE);
	}

	public GenerateActionAction(@NotNull final String actionName, @Nullable final String description, @Nullable final Icon icon)
	{
		super(GenerateControllerAction.GENERATOR_CONTROLLER, actionName, description, icon);
	}

	@Override
	public void actionPerformed(final AnActionEvent e)
	{
		final Module module = e.getData(CommonDataKeys.MODULE);

		invokeDialog(module, e.getDataContext());
	}

	public void invokeAction(final String scriptArguments, final String mainArgument, final Module myModule, final DataContext myDataContext)
	{
		insertMethodStub(myDataContext, mainArgument);
		invokeAction(scriptArguments, mainArgument, myModule);
	}

	public void insertMethodStub(final DataContext dataContext, final String methodName)
	{
		if(dataContext != null)
		{
			Project project = dataContext.getData(CommonDataKeys.PROJECT);
			CommandProcessor.getInstance().executeCommand(project, RModuleUtil.createWriteAction(new ActionStubInserter(dataContext, methodName)), "GenerateActionAction.insertMethodSub", null);
		}
	}


	@Override
	public void update(@NotNull final AnActionEvent event)
	{
		final DataContext dataContext = event.getDataContext();
		final Presentation presentation = event.getPresentation();

		// Check if module is Rails module, SDK is valid
		final Module module = event.getData(CommonDataKeys.MODULE);
		if(module == null || !RailsFacetUtil.hasRailsSupport(module) || !RubySdkUtil.isKindOfRubySDK(RModuleUtil.getModuleOrJRubyFacetSdk(module)))
		{

			AnActionUtil.updatePresentation(presentation, false, false);
			return;
		}

		// Check if file name corresponds to file with controller class
		final VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
		if(ControllersConventions.getControllerName(file) == null)
		{
			AnActionUtil.updatePresentation(presentation, false, false);
			return;
		}

		// Check if caret is situated in ruby class
		final RClass rClass = determineControllerClass(dataContext);
		if(rClass != null && file != null)
		{
			final Document document = PsiDocumentManager.getInstance(rClass.getProject()).getDocument(rClass.getContainingFile());
			if(document != null)
			{

				// Check if our ruby class is Controller
				final String controllerFullClassName = ControllersConventions.getControllerClassName(ControllersConventions.getControllerFullName(module, file));
				assert controllerFullClassName != null; // Controller must exist

				if(controllerFullClassName.equals(rClass.getFullName()))
				{
					AnActionUtil.updatePresentation(presentation, true, true);
					return;
				}
			}
		}

		AnActionUtil.updatePresentation(presentation, false, false);
	}

	protected ActionInputValidator createValidator(@NotNull final Module module, @Nullable final VirtualFile file, @NotNull final DataContext dataContext)
	{
		return new ActionInputValidator(this, module, file, dataContext);
	}

	@Override
	protected String getGenerateDialogTitle()
	{
		return RBundle.message("popup.generate.action.prompt.title");
	}

	@Override
	protected String getErrorTitle()
	{
		return RBundle.message("popup.generate.action.error.title");
	}

	@Nullable
	private static RClass determineControllerClass(final DataContext dataContext)
	{
		final Editor editor = dataContext.getData(CommonDataKeys.EDITOR);
		final PsiFile psiFile = dataContext.getData(CommonDataKeys.PSI_FILE);
		if(psiFile == null || editor == null)
		{
			return null;
		}
		PsiElement psiElem = dataContext.getData(CommonDataKeys.PSI_ELEMENT);
		if(psiElem == null)
		{
			psiElem = psiFile.findElementAt(editor.getCaretModel().getOffset());
		}
		if(psiElem != null && psiElem instanceof PsiWhiteSpace && psiElem.isWritable())
		{

			final RCompoundStatement st = RCompoundStatementNavigator.getByPsiElement(psiElem);
			if(st != null)
			{
				final RBodyStatement body = RBodyStatementNavigator.getByRCompoundStatement(st);
				return body == null ? null : RClassNavigator.getByRBodyStatement(body);
			}
			else
			{
				return RClassNavigator.getByPsiWhiteSpace((PsiWhiteSpace) psiElem);
			}
		}
		return null;
	}

	private void invokeDialog(final Module module, @NotNull final DataContext dataContext)
	{
		final VirtualFile file = dataContext.getData(CommonDataKeys.VIRTUAL_FILE);

		final ActionInputValidator validator = createValidator(module, file, dataContext);
		GenerateDialogs.showGenerateActionDialog(module, getGenerateDialogTitle(), validator);
	}
}
