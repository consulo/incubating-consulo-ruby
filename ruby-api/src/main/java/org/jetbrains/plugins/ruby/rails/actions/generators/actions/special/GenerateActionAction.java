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

import consulo.application.WriteAction;
import consulo.dataContext.DataContext;
import consulo.document.Document;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiWhiteSpace;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.ui.ex.action.Presentation;
import consulo.undoRedo.CommandProcessor;
import consulo.codeEditor.Editor;
import consulo.ide.impl.idea.openapi.editor.EditorModificationUtil;
import consulo.module.Module;
import consulo.util.lang.function.ThrowableRunnable;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiFile;
import consulo.language.codeStyle.CodeStyleManager;
import consulo.util.lang.IncorrectOperationException;
import consulo.language.editor.CommonDataKeys;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.image.Image;

import jakarta.annotation.Nullable;
import jakarta.annotation.Nonnull;
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

		public ActionStubInserter(@Nonnull final DataContext dataContext, @Nonnull final String methodName)
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
						WriteAction.run(new ThrowableRunnable<Exception>()
						{
							@Override
							public void run() throws Exception
							{
								// insert method stub
								consulo.ide.impl.idea.openapi.editor.EditorModificationUtil.insertStringAtCaret(editor, text);
								PsiDocumentManager.getInstance(project).commitDocument(document);
							}
						});
						WriteAction.run(new consulo.util.lang.function.ThrowableRunnable<Exception>()
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

	public GenerateActionAction(@Nonnull final String actionName, @Nullable final String description, @Nullable final Image icon)
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
	public void update(@Nonnull final AnActionEvent event)
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

	protected ActionInputValidator createValidator(@Nonnull final Module module, @Nullable final VirtualFile file, @Nonnull final DataContext dataContext)
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

	private void invokeDialog(final Module module, @Nonnull final DataContext dataContext)
	{
		final VirtualFile file = dataContext.getData(CommonDataKeys.VIRTUAL_FILE);

		final ActionInputValidator validator = createValidator(module, file, dataContext);
		GenerateDialogs.showGenerateActionDialog(module, getGenerateDialogTitle(), validator);
	}
}
