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

package org.jetbrains.plugins.ruby.ruby.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import consulo.awt.TargetAWT;
import consulo.ide.IconDescriptorUpdaters;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RExpressionSubstitution;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpressionInParens;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import org.jetbrains.plugins.ruby.ruby.presentation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 24.07.2006
 */
public class RubyPsiUtil
{
	private static final Logger LOG = Logger.getInstance(RubyPsiUtil.class.getName());
	@NonNls
	private static final String TEMP_FILE_NAME = "ruby_temp_file_" + RubyPsiUtil.class.hashCode() + "." + RubyFileType.INSTANCE.getDefaultExtension();

	/**
	 * Creates Ruby File with text as content
	 *
	 * @param fileName  name of file
	 * @param directory psiDirectory, where file will be created
	 * @param text      Text
	 * @return file
	 * @throws com.intellij.util.IncorrectOperationException
	 *
	 */
	@SuppressWarnings({"JavaDoc"})
	public static RFile createFileFromText(final String fileName, final PsiDirectory directory, final String text) throws IncorrectOperationException
	{
		PsiFile file = directory.createFile(fileName);
		PsiFileFactory factory = getPsiElementFactory(directory.getProject());
		PsiFile templateFile = factory.createFileFromText(TEMP_FILE_NAME, text);
		file.addRange(templateFile.getFirstChild(), templateFile.getLastChild());
		return (RFile) file;
	}

	public static RFile createDummyRubyFile(final Project project, @NotNull final String text)
	{
		final FileType type = RubyFileType.INSTANCE;
		return (RFile) createFileFromText(project, TEMP_FILE_NAME, type, text);
	}


	public static List<RPsiElement> getTopLevelElements(final Project project, @NotNull final String text)
	{
		final RFile rFile = createDummyRubyFile(project, text);
		return rFile.getCompoundStatement().getStatements();
	}

	@NotNull
	private static PsiFile createFileFromText(@NotNull final Project project, @NotNull final String name, @NotNull final FileType fileType, @NotNull final String text)
	{
		return getPsiElementFactory(project).createFileFromText(name, fileType, text);
	}

	private static PsiFileFactory getPsiElementFactory(final Project project)
	{
		return PsiFileFactory.getInstance(project);
	}

	/**
	 * Returns elements in the psi tree
	 *
	 * @param filter  Types of expected child
	 * @param element tree parent node
	 * @return PsiElement - child psiElement
	 */
	@NotNull
	public static List<PsiElement> getChildrenByFilter(@NotNull final PsiElement element, @NotNull final TokenSet filter)
	{
		ArrayList<PsiElement> list = new ArrayList<PsiElement>();
		final ASTNode node = element.getNode();
		if(node != null)
		{
			for(ASTNode childNode : node.getChildren(filter))
			{
				list.add(childNode.getPsi());
			}
		}
		return list;
	}

	/**
	 * Returns child elements in the psi tree
	 *
	 * @param filter  Types of expected child
	 * @param element tree parent node
	 * @return PsiElement - child psiElement
	 */

	@NotNull
	public static List<PsiElement> getChildrenByFilter(@NotNull final PsiElement element, final IElementType filter)
	{
		return getChildrenByFilter(element, TokenSet.create(filter));
	}

	/**
	 * Returns child element in the psi tree
	 *
	 * @param filter  Types of expected child
	 * @param number  number
	 * @param element tree parent node
	 * @return PsiElement - child psiElement
	 */
	@Nullable
	public static PsiElement getChildByFilter(@NotNull final PsiElement element, final @NotNull TokenSet filter, final int number)
	{
		List<PsiElement> list = getChildrenByFilter(element, filter);
		return (0 <= number && number < list.size()) ? list.get(number) : null;
	}

	/**
	 * Returns child element in the psi tree
	 *
	 * @param filter  Types of expected child
	 * @param number  number
	 * @param element tree parent node
	 * @return PsiElement - child psiElement
	 */
	@Nullable
	public static PsiElement getChildByFilter(@NotNull final PsiElement element, final IElementType filter, final int number)
	{
		return getChildByFilter(element, TokenSet.create(filter), number);
	}

	/**
	 * Returns first element in psiTree left to element of type c
	 *
	 * @param c       object of required type
	 * @param element anchor node
	 * @return RElement object if found, null otherwise
	 */
	@Nullable
	public static <T extends PsiElement> T getElementToLeftWithSameParent(@NotNull final PsiElement element, final Class<T> c)
	{
		PsiElement left = element.getPrevSibling();
		while(left != null && !(c.isInstance(left)))
		{
			left = left.getPrevSibling();
		}
		//noinspection unchecked
		return left != null ? (T) left : null;
	}

	/**
	 * Returns  first element in psiTree right to element of type c
	 *
	 * @param c       object of required type
	 * @param element anchor node
	 * @return RElement object if found, null otherwise
	 */
	@Nullable
	public static <T extends PsiElement> T getElementToRightWithSameParent(@NotNull final PsiElement element, final Class<T> c)
	{
		PsiElement right = element.getNextSibling();
		while(right != null && !(c.isInstance(right)))
		{
			right = right.getNextSibling();
		}
		//noinspection unchecked
		return right != null ? (T) right : null;
	}

	/**
	 * Run formatter for minimal formatting block, containing given element
	 *
	 * @param element PsiElement to reformat
	 */
	public static void reformat(@NotNull final PsiElement element)
	{
		final Project project = element.getProject();
		final PsiFile file = element.getContainingFile();
		final Document document = PsiDocumentManager.getInstance(project).getDocument(file);
		assert document != null;
		// Commit document before formatting!
		PsiDocumentManager.getInstance(project).commitDocument(document);
		try
		{
			CodeStyleManager.getInstance(project).reformat(element);
		}
		catch(IncorrectOperationException e)
		{
			LOG.error(e);
		}
	}

	@NotNull
	public static <T extends PsiElement> List<T> getChildrenByType(RPsiElement rPsiElement, Class<T> c)
	{
		ArrayList<T> list = new ArrayList<T>();
		for(PsiElement psiElement : rPsiElement.getChildren())
		{
			if(c.isInstance(psiElement))
			{
				//noinspection unchecked
				list.add((T) psiElement);
			}
		}

		return list;
	}

	@Nullable
	public static <T extends PsiElement> T getChildByType(RPsiElement rPsiElement, Class<T> c, int number)
	{
		List<T> list = getChildrenByType(rPsiElement, c);
		return list.size() > number ? list.get(number) : null;
	}

	public static boolean isBefore(final PsiElement element1, final PsiElement element2)
	{
		return element1.getTextOffset() < element2.getTextOffset();
	}

	/**
	 * Returns presentable name for each rPsiElement, used in Folding, Structure view
	 *
	 * @param element element to get presentable name for
	 * @return String - name
	 */
	public static String getPresentableName(@NotNull final PsiElement element)
	{
		if(element instanceof RFile)
		{
			return ((RFile) element).getName();
		}
		// RObjectClass
		if(element instanceof RObjectClass)
		{
			return ((RObjectClass) element).getPresentableName();
		}
		// RModule
		if(element instanceof RModule)
		{
			return ((RModule) element).getFullName();
		}
		// RClass
		if(element instanceof RClass)
		{
			return ((RClass) element).getFullName();
		}
		// RMethod, RSingletonMethod
		if(element instanceof RMethod)
		{
			return ((RMethod) element).getPresentableName();
		}
		// Field
		if(element instanceof RField)
		{
			return element.getText();
		}
		// Constants
		if(element instanceof RConstant)
		{
			return ((RConstant) element).getName();
		}
		// Global variable
		if(element instanceof RGlobalVariable)
		{
			return element.getText();
		}
		// Alias
		if(element instanceof RAliasStatement)
		{
			return ((RAliasStatement) element).getPresentableText();
		}
		// PsiFile
		if(element instanceof PsiFile)
		{
			return ((PsiFile) element).getName();
		}
		// defaults
		return element.getText();
	}

	/**
	 * Returns icon for each rPsiElement
	 *
	 * @param element element to get icon for
	 * @return Icon
	 */
	public static Image getIcon(final PsiElement element)
	{
		if(element instanceof RFile)
		{
			return RFilePresentationUtil.getIconByRFile((RFile) element);
		}

		if(element instanceof RObjectClass)
		{
			return IconDescriptorUpdaters.getIcon(element, Iconable.ICON_FLAG_VISIBILITY);
		}

		if(element instanceof RContainer)
		{
			return RContainerPresentationUtil.getIconWithModifiers((RContainer) element);
		}

		if(element instanceof RField)
		{
			return RFieldPresentationUtil.getIconByRField((RField) element);
		}

		if(element instanceof RAliasStatement)
		{
			return RubyIcons.RUBY_ALIAS_NODE;
		}

		if(element instanceof RCall)
		{
			final StructureType type = ((RCall) element).getType();
			if(type == StructureType.CALL_REQUIRE)
			{
				return RubyIcons.RUBY_REQUIRE_NODE;
			}
			if(type == StructureType.CALL_INCLUDE)
			{
				return RubyIcons.RUBY_INCLUDE_NODE;
			}
			if(type == StructureType.CALL_EXTEND)
			{
				return RubyIcons.RUBY_INCLUDE_NODE;
			}
			if(type == StructureType.CALL_INCLUDE_CLASS || type == StructureType.CALL_INCLUDE_PACKAGE)
			{
				return RubyIcons.RUBY_INCLUDE_NODE;
			}
			if(type == StructureType.FIELD_ATTR_CALL)
			{
				return RFieldAttrPresentationUtil.getAttrIcon(((RCallBase) element).getFieldAttrType());
			}
		}

		// For constants
		if(element instanceof RConstant && ((RConstant) element).isInDefinition())
		{
			return RConstantPresentationUtil.getIcon();
		}

		// For global variables
		if(element instanceof RGlobalVariable && ((RGlobalVariable) element).isInDefinition())
		{
			return RGlobalVariablePresentationUtil.getIcon();
		}

		return null;
	}

	@Nullable
	public static RFile getRFile(@NotNull final PsiElement element)
	{
		final PsiFile containingFile = element.getContainingFile();
		return containingFile instanceof RFile ? (RFile) containingFile : null;
	}

	public static void replaceInParent(@NotNull final PsiElement oldElement, @NotNull final PsiElement... newElements)
	{
		addBeforeInParent(oldElement, newElements);
		removeElements(oldElement);
	}

	public static void addToEnd(@NotNull final PsiElement psiElement, @NotNull final PsiElement... newElements)
	{
		final ASTNode psiNode = psiElement.getNode();
		LOG.assertTrue(psiNode != null);
		for(PsiElement newElement : newElements)
		{
			//noinspection ConstantConditions
			psiNode.addChild(newElement.getNode());
		}
	}

	public static void addBeforeInParent(@NotNull final PsiElement anchor, @NotNull final PsiElement... newElements)
	{
		final PsiElement psiParent = anchor.getParent();
		LOG.assertTrue(psiParent != null);
		final ASTNode parentNode = psiParent.getNode();
		final ASTNode anchorNode = anchor.getNode();
		LOG.assertTrue(parentNode != null);
		LOG.assertTrue(anchorNode != null);
		for(PsiElement newElement : newElements)
		{
			//noinspection ConstantConditions
			parentNode.addChild(newElement.getNode(), anchorNode);
		}
	}

	public static void removeElements(@NotNull final PsiElement... elements)
	{
		final ASTNode parentNode = elements[0].getParent().getNode();
		LOG.assertTrue(parentNode != null);
		for(PsiElement element : elements)
		{
			//noinspection ConstantConditions
			parentNode.removeChild(element.getNode());
		}
	}

	@Nullable
	public static RPsiElement getCoveringRPsiElement(@NotNull final PsiElement psiElement)
	{
		PsiElement current = psiElement;
		while(current != null)
		{
			if(current instanceof RPsiElement)
			{
				return (RPsiElement) current;
			}
			current = current.getParent();
		}
		return null;
	}

	/**
	 * Search the most top level RClass (in RFile) that contains given element.
	 * Returns given element if it is such top level class.
	 *
	 * @param rContainer given element
	 * @return Null if element hasn't any class that contains him
	 */
	@Nullable
	public static RClass getContainingUpperRClass(@NotNull final RContainer rContainer)
	{
		RClass upperRClass = rContainer instanceof RClass ? (RClass) rContainer : null;
		RClass curr = getContainingRClassByContainer(rContainer);

		while(curr != null)
		{
			upperRClass = curr;
			curr = getContainingRClassByContainer(curr);
		}
		return upperRClass;
	}

	@Nullable
	public static RClass getContainingRClassByContainer(@NotNull final RContainer rContainer)
	{
		RContainer current = rContainer.getParentContainer();
		while(current != null)
		{
			if(current instanceof RClass)
			{
				return (RClass) current;
			}
			current = current.getParentContainer();
		}
		return null;
	}

	@Nullable
	public static RVirtualContainer getParentVContainer(@NotNull final PsiElement psiElement)
	{
		PsiElement element = psiElement;
		while(element != null)
		{
			if(element instanceof RVirtualContainer)
			{
				return ((RVirtualContainer) element).getVirtualParentContainer();
			}
			element = element.getParent();
		}
		return null;
	}

	@Nullable
	public static RClass getContainingRClass(@NotNull final PsiElement psiElement)
	{
		PsiElement element = psiElement;
		while(element != null)
		{
			if(element instanceof RContainer)
			{
				break;
			}
			element = element.getParent();
		}

		if(element == null)
		{
			return null;
		}

		RContainer current = (RContainer) element;
		while(current != null)
		{
			if(current instanceof RClass)
			{
				return (RClass) current;
			}
			current = current.getParentContainer();
		}
		return null;
	}

	/**
	 * Gets ruby method for given psi element. If element is method return element
	 *
	 * @param psiElement Given psi element
	 * @return null or method
	 */
	@Nullable
	public static RMethod getContainingRMethod(@NotNull final PsiElement psiElement)
	{
		PsiElement element = psiElement;
		while(element != null)
		{
			if(element instanceof RContainer)
			{
				break;
			}
			element = element.getParent();
		}

		if(element == null)
		{
			return null;
		}

		RContainer current = (RContainer) element;
		while(current != null)
		{
			if(current instanceof RMethod)
			{
				return (RMethod) current;
			}
			current = current.getParentContainer();
		}
		return null;
	}

	/*
	 * Looks for statement, containing this PsiElement
	 */
	@Nullable
	public static RPsiElement getStatement(PsiElement element)
	{
		PsiElement statement = element;
		// RUBY-1605. We want real statement, but not expressions in parens or expression subtitutions in strings
		do
		{
			statement = PsiTreeUtil.getParentOfType(statement, RCompoundStatement.class);
		}
		while(statement != null && (statement.getParent() instanceof RExpressionInParens || statement.getParent() instanceof RExpressionSubstitution));
		return statement instanceof RCompoundStatement ? getStatement((RCompoundStatement) statement, element) : null;
	}

	/*
	 * Looks for statement, containing this PsiElement
	 */
	@Nullable
	public static RPsiElement getStatement(@NotNull final RPsiElement compoundStatement, PsiElement element)
	{
		while(element != null && element.getParent() != compoundStatement)
		{
			element = element.getParent();
		}
		return element instanceof RPsiElement ? (RPsiElement) element : null;
	}

	@NotNull
	public static String evaluate(@NotNull final PsiElement element)
	{
		if(element instanceof RSymbol)
		{
			return evaluate(((RSymbol) element).getObject());
		}
		// String like processing
		if(element instanceof RStringLiteral && !((RStringLiteral) element).hasExpressionSubstitutions())
		{
			return ((RStringLiteral) element).getContent();
		}
		return element.getText();
	}

}