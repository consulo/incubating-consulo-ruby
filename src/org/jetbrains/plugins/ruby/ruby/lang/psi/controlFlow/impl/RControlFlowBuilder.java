package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.UsageAnalyzer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowOwner;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RElseBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RElsifBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modifierStatements.RIfModStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modifierStatements.RUnlessModStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modifierStatements.RUntilModStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modifierStatements.RWhileModStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RMultiAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.RCompoundStatementNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCommandCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RFunctionCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubySystemCallVisitor;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 07.04.2008
 */
public class RControlFlowBuilder extends RubySystemCallVisitor
{
	// Here we store all the instructions
	private List<InstructionImpl> myInstructions;

	// Here we store stack of processing instructions
	private Stack<InstructionImpl> myInstructionsStack;

	private Stack<ExceptionInfo> myExceptionInfos;

	private InstructionImpl myPrevInstruction;

	// Here we store all the pending instructions with their scope
	private List<Pair<InstructionImpl, RPsiElement>> myPending;

	private RPsiElement myStartElementInScope;
	private RPsiElement myEndElementInScope;

	private boolean isInScope;
	private int myInstructionNumber;

	private FileSymbol myFileSymbol;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Control flow builder staff
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Instruction[] buildControlFlow(@Nullable final FileSymbol fileSymbol, @NotNull final RPsiElement scope, @Nullable final RPsiElement startInScope, @Nullable final RPsiElement endInScope)
	{
		myInstructions = new ArrayList<InstructionImpl>();
		myInstructionsStack = new Stack<InstructionImpl>();
		myExceptionInfos = new Stack<ExceptionInfo>();
		myPending = new ArrayList<Pair<InstructionImpl, RPsiElement>>();
		myInstructionNumber = 0;
		myStartElementInScope = startInScope;
		myEndElementInScope = endInScope;
		isInScope = startInScope == null;
		myFileSymbol = fileSymbol;

		// create start pseudo node
		startNode(null);

		scope.acceptChildren(this);

		// create end pseudo node and close all pending edges
		checkPending(startNode(null));

		return myInstructions.toArray(new Instruction[myInstructions.size()]);
	}

	private InstructionImpl findInstructionByElement(PsiElement element)
	{
		for(int i = myInstructionsStack.size() - 1; i >= 0; i--)
		{
			InstructionImpl instruction = myInstructionsStack.get(i);
			if(element.equals(instruction.getElement()))
			{
				return instruction;
			}
		}
		return null;
	}

	/**
	 * Adds edge between 2 edges
	 *
	 * @param beginInstruction Begin of new edge
	 * @param endInstruction   End of new edge
	 */
	private void addEdge(InstructionImpl beginInstruction, InstructionImpl endInstruction)
	{
		if(!beginInstruction.mySucc.contains(endInstruction))
		{
			beginInstruction.mySucc.add(endInstruction);
		}

		if(!endInstruction.myPred.contains(beginInstruction))
		{
			endInstruction.myPred.add(beginInstruction);
		}
	}

	/**
	 * Add new node and set prev instruction pointing to this instruction
	 *
	 * @param instruction new instruction
	 */
	private void addNode(InstructionImpl instruction)
	{
		myInstructions.add(instruction);
		if(myPrevInstruction != null)
		{
			addEdge(myPrevInstruction, instruction);
		}
		myPrevInstruction = instruction;
	}

	/**
	 * Stops control flow, used for break, next, redo
	 */
	private void flowAbrupted()
	{
		myPrevInstruction = null;
	}

	/**
	 * Adds pending adge in pendingScope
	 *
	 * @param pendingScope Scope for instruction
	 * @param instruction  "Last" pending instruction
	 */
	private void addPendingEdge(RPsiElement pendingScope, InstructionImpl instruction)
	{
		if(instruction == null)
		{
			return;
		}

		int i = 0;
		// another optimization! Place pending before first scope, not contained in pendingScope
		// the same logic is used in checkPending
		if(pendingScope != null)
		{
			for(; i < myPending.size(); i++)
			{
				Pair<InstructionImpl, RPsiElement> pair = myPending.get(i);
				final RPsiElement currScope = pair.getSecond();
				if(currScope == null)
				{
					continue;
				}
				if(!PsiTreeUtil.isAncestor(currScope, pendingScope, true))
				{
					break;
				}
			}
		}
		myPending.add(i, new Pair<InstructionImpl, RPsiElement>(instruction, pendingScope));
	}

	private void checkPending(InstructionImpl instruction)
	{
		final PsiElement element = instruction.getElement();
		if(element == null)
		{
			// if element is null (fake element, we just process all pending)
			for(Pair<InstructionImpl, RPsiElement> pair : myPending)
			{
				addEdge(pair.getFirst(), instruction);
			}
			myPending.clear();
		}
		else
		{
			// else we just all the pending with scope containing in element
			// reverse order is just an optimization
			for(int i = myPending.size() - 1; i >= 0; i--)
			{
				final Pair<InstructionImpl, RPsiElement> pair = myPending.get(i);
				final PsiElement scopeWhenToAdd = pair.getSecond();
				if(scopeWhenToAdd == null)
				{
					continue;
				}
				if(!PsiTreeUtil.isAncestor(scopeWhenToAdd, element, false))
				{
					addEdge(pair.getFirst(), instruction);
					myPending.remove(i);
				}
				else
				{
					break;
				}
			}
		}
	}

	/**
	 * Creates instruction for given element, and adds it to myInstructionsStack
	 * Warning! Always call finishNode after startNode
	 *
	 * @param element Element to create instruction for
	 * @return new instruction
	 */
	private InstructionImpl startNode(RPsiElement element)
	{
		final InstructionImpl instruction = new InstructionImpl(element, myInstructionNumber++);
		addNode(instruction);
		checkPending(instruction);
		return myInstructionsStack.push(instruction);
	}

	/**
	 * Removes given instruction from myInstructionsStack
	 *
	 * @param instruction Instruction to process
	 */
	private void finishNode(InstructionImpl instruction)
	{
		assert instruction.equals(myInstructionsStack.pop());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///// Here we override visitors methods to implement functionality
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void visitElement(@NotNull final PsiElement element)
	{
		if(element == myStartElementInScope)
		{
			isInScope = true;
		}
		else if(element == myEndElementInScope)
		{
			isInScope = false;
		}

		if(isInScope)
		{
			// we shouldn`t get inside inner onwers
			if(!(element instanceof RControlFlowOwner))
			{
				// but we still want collect all the expressions
				if(element instanceof RExpression && RCompoundStatementNavigator.getByPsiElement(element) != null)
				{
					// Simple add expression instruction
					InstructionImpl instruction = new InstructionImpl(element, myInstructionNumber++);
					addNode(instruction);
					checkPending(instruction);
				}
				super.visitElement(element);
			}
		}
	}

	/// return statement handling
	@Override
	public void visitRReturnStatement(final RReturnStatement rReturnStatement)
	{
		final InstructionImpl instruction = startNode(rReturnStatement);
		for(RPsiElement element : rReturnStatement.getReturnValues())
		{
			element.accept(this);
		}
		finishNode(instruction);

		addPendingEdge(null, myPrevInstruction);
		flowAbrupted();
	}

	///// Function and command calls

	@Override
	public void visitRFunctionCall(final RFunctionCall rFunctionCall)
	{
		final InstructionImpl instruction = startNode(rFunctionCall);
		rFunctionCall.getCallArguments().accept(this);
		rFunctionCall.getPsiCommand().accept(this);
		finishNode(instruction);
	}

	@Override
	public void visitRCommandCall(final RCommandCall rCommandCall)
	{
		final InstructionImpl instruction = startNode(rCommandCall);
		rCommandCall.getCallArguments().accept(this);
		rCommandCall.getPsiCommand().accept(this);
		finishNode(instruction);
	}

	/////// visit assignments

	@Override
	public void visitRAssignmentExpression(final RAssignmentExpression assignmentExpression)
	{
		final InstructionImpl instruction = startNode(assignmentExpression);
		final RPsiElement value = assignmentExpression.getValue();
		if(value != null)
		{
			value.accept(this);
		}
		final RPsiElement object = assignmentExpression.getObject();
		object.accept(this);
		finishNode(instruction);
	}

	@Override
	public void visitRMultiAssignmentExpression(final RMultiAssignmentExpression multiAssignmentExpression)
	{
		// the same logic here
		visitRAssignmentExpression(multiAssignmentExpression);
	}

	/// Visit identifier
	@Override
	public void visitRIdentifier(final RIdentifier rIdentifier)
	{
		// we should handle it only if its a parameter or local variable
		if(rIdentifier.isParameter() || rIdentifier.isLocalVariable())
		{
			ReadWriteInstructionImpl instruction = new ReadWriteInstructionImpl(rIdentifier.getName(), myInstructionNumber++, UsageAnalyzer.createUsageAccess(rIdentifier));
			addNode(instruction);
			checkPending(instruction);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///// Control statements
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	///// If and If modifier statements

	@Override
	public void visitRIfStatement(final RIfStatement ifStatement)
	{
		InstructionImpl ifInstruction = startNode(ifStatement);
		final RCondition condition = ifStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		// Set the head as the last instruction of condition
		InstructionImpl head = myPrevInstruction;
		final RCompoundStatement thenBranch = ifStatement.getThenBlock();
		if(thenBranch != null)
		{
			final InstructionImpl thenInstruction = startNode(thenBranch);
			thenBranch.accept(this);
			addPendingEdge(ifStatement, myPrevInstruction);
			finishNode(thenInstruction);
		}

		for(RElsifBlock block : ifStatement.getElsifBlocks())
		{
			// restore head
			myPrevInstruction = head;
			final RCondition rCondition = block.getCondition();
			if(rCondition != null)
			{
				rCondition.accept(this);
			}
			// Set the head as the last instruction of condition
			head = myPrevInstruction;
			final InstructionImpl thenInstruction = startNode(thenBranch);
			block.accept(this);
			addPendingEdge(ifStatement, myPrevInstruction);
			finishNode(thenInstruction);
		}

		// restore head
		myPrevInstruction = head;
		final RElseBlock elseBranch = ifStatement.getElseBlock();
		if(elseBranch != null)
		{
			final InstructionImpl elseInstruction = startNode(elseBranch);
			elseBranch.accept(this);
			addPendingEdge(ifStatement, myPrevInstruction);
			finishNode(elseInstruction);
		}

		finishNode(ifInstruction);
	}

	@Override
	public void visitRIfModStatement(final RIfModStatement ifStatement)
	{
		InstructionImpl ifInstruction = startNode(ifStatement);
		final RCondition condition = ifStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		final InstructionImpl head = myPrevInstruction;
		// Set the head as the last instruction of condition
		final RPsiElement command = ifStatement.getCommand();
		if(command != null)
		{
			command.accept(this);
			addPendingEdge(ifStatement, myPrevInstruction);
		}
		// restore head
		myPrevInstruction = head;
		finishNode(ifInstruction);
	}

	///// Unless and Unless modifier statements

	@Override
	public void visitRUnlessStatement(final RUnlessStatement rUnlessStatement)
	{
		InstructionImpl unlessInstruction = startNode(rUnlessStatement);
		final RCondition condition = rUnlessStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		// Set the head as the last instruction of condition
		InstructionImpl head = myPrevInstruction;
		final RCompoundStatement thenBranch = rUnlessStatement.getThenBlock();
		if(thenBranch != null)
		{
			final InstructionImpl thenInstruction = startNode(thenBranch);
			thenBranch.accept(this);
			addPendingEdge(rUnlessStatement, myPrevInstruction);
			finishNode(thenInstruction);
		}

		// restore head
		myPrevInstruction = head;
		final RElseBlock elseBranch = rUnlessStatement.getElseBlock();
		if(elseBranch != null)
		{
			final InstructionImpl elseInstruction = startNode(elseBranch);
			elseBranch.accept(this);
			addPendingEdge(rUnlessStatement, myPrevInstruction);
			finishNode(elseInstruction);
		}

		finishNode(unlessInstruction);
	}

	@Override
	public void visitRUnlessModStatement(final RUnlessModStatement rUnlessModStatement)
	{
		InstructionImpl unlessModInstruction = startNode(rUnlessModStatement);
		final RCondition condition = rUnlessModStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		final InstructionImpl head = myPrevInstruction;
		// Set the head as the last instruction of condition
		final RPsiElement command = rUnlessModStatement.getCommand();
		if(command != null)
		{
			command.accept(this);
			addPendingEdge(rUnlessModStatement, myPrevInstruction);
		}
		// restore head
		myPrevInstruction = head;
		finishNode(unlessModInstruction);
	}

	///// While and While modifier statements

	@Override
	public void visitRWhileStatement(final RWhileStatement rWhileStatement)
	{
		final InstructionImpl instruction = startNode(rWhileStatement);
		final RCondition condition = rWhileStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		// if condition was false
		addPendingEdge(rWhileStatement, myPrevInstruction);

		final RPsiElement body = rWhileStatement.getLoopBody();
		if(body != null)
		{
			final InstructionImpl bodyInstruction = startNode(body);
			body.accept(this);
			finishNode(bodyInstruction);
		}
		checkPending(instruction); //check for breaks targeted here
		if(myPrevInstruction != null)
		{
			addEdge(myPrevInstruction, instruction); //loop
		}
		flowAbrupted();
		finishNode(instruction);
	}

	@Override
	public void visitRWhileModStatement(final RWhileModStatement rWhileModStatement)
	{
		final InstructionImpl instruction = startNode(rWhileModStatement);
		final RCondition condition = rWhileModStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		// if condition was false
		addPendingEdge(rWhileModStatement, myPrevInstruction);
		final RPsiElement command = rWhileModStatement.getLoopBody();
		if(command != null)
		{
			final InstructionImpl bodyInstruction = startNode(command);
			command.accept(this);
			finishNode(bodyInstruction);
		}
		checkPending(instruction); //check for breaks targeted here
		if(myPrevInstruction != null)
		{
			addEdge(myPrevInstruction, instruction); //loop
		}
		flowAbrupted();
		finishNode(instruction);
	}

	///// While and While modifier statements

	@Override
	public void visitRUntilStatement(final RUntilStatement rUntilStatement)
	{
		final InstructionImpl instruction = startNode(rUntilStatement);
		final RCondition condition = rUntilStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		// if condition was false
		addPendingEdge(rUntilStatement, myPrevInstruction);

		final RPsiElement body = rUntilStatement.getLoopBody();
		if(body != null)
		{
			final InstructionImpl bodyInstruction = startNode(body);
			body.accept(this);
			finishNode(bodyInstruction);
		}
		checkPending(instruction); //check for breaks targeted here
		if(myPrevInstruction != null)
		{
			addEdge(myPrevInstruction, instruction); //loop
		}
		flowAbrupted();
		finishNode(instruction);
	}

	@Override
	public void visitRUntilModStatement(final RUntilModStatement rUntilModStatement)
	{
		final InstructionImpl instruction = startNode(rUntilModStatement);
		final RCondition condition = rUntilModStatement.getCondition();
		if(condition != null)
		{
			condition.accept(this);
		}
		// if condition was false
		addPendingEdge(rUntilModStatement, myPrevInstruction);
		final RPsiElement command = rUntilModStatement.getLoopBody();
		if(command != null)
		{
			final InstructionImpl bodyInstruction = startNode(command);
			command.accept(this);
			finishNode(bodyInstruction);
		}
		checkPending(instruction); //check for breaks targeted here
		if(myPrevInstruction != null)
		{
			addEdge(myPrevInstruction, instruction); //loop
		}
		flowAbrupted();
		finishNode(instruction);
	}

	/// Case
	@Override
	public void visitRCaseStatement(final RCaseStatement rCaseStatement)
	{
		final InstructionImpl instruction = startNode(rCaseStatement);
		final RPsiElement expression = rCaseStatement.getExpression();
		if(expression != null)
		{
			expression.accept(this);
		}
		InstructionImpl prevCaseExpression = null;
		for(RWhenCase whenCase : rCaseStatement.getCases())
		{
			final InstructionImpl caseInstruction = startNode(whenCase);
			final RListOfExpressions caseExpr = whenCase.getCaseExpression();
			if(prevCaseExpression != null)
			{
				addEdge(prevCaseExpression, caseInstruction);
			}
			if(caseExpr != null)
			{
				caseExpr.accept(this);
				prevCaseExpression = myPrevInstruction;
			}

			final RCompoundStatement body = whenCase.getCaseBody();
			body.accept(this);
			finishNode(caseInstruction);
		}

		final RElseBlock elseBlock = rCaseStatement.getElseCase();
		if(elseBlock != null)
		{
			final InstructionImpl elseInstruction = startNode(elseBlock);
			if(prevCaseExpression != null)
			{
				addEdge(prevCaseExpression, elseInstruction);
			}
			elseBlock.accept(this);
			finishNode(elseInstruction);
			addPendingEdge(rCaseStatement, myPrevInstruction);
		}

		checkPending(instruction); //check for breaks targeted here
		flowAbrupted();
		finishNode(instruction);
	}

	/// For
	@Override
	public void visitRForStatement(final RForStatement rForStatement)
	{
		InstructionImpl instruction = startNode(rForStatement);
		final RPsiElement expr = rForStatement.getExpression();
		if(expr != null)
		{
			expr.accept(this);
		}
		addPendingEdge(rForStatement, myPrevInstruction);

		final RCompoundStatement body = rForStatement.getBody();
		if(body != null)
		{
			InstructionImpl bodyInstruction = startNode(body);
			body.accept(this);
			finishNode(bodyInstruction);

			if(myPrevInstruction != null)
			{
				addEdge(myPrevInstruction, bodyInstruction);  //loop
				addPendingEdge(rForStatement, myPrevInstruction); // exit
			}
		}

		checkPending(instruction); //check for breaks targeted here
		flowAbrupted();

		finishNode(instruction);
	}

	///// Loop statements handling

	@Override
	public void visitRBreakStatement(final RBreakStatement breakStatement)
	{
		addNode(new InstructionImpl(breakStatement, myInstructionNumber++));
		final RLoopStatement loop = breakStatement.getBreakedLoop();
		if(loop != null)
		{
			addPendingEdge(loop, myPrevInstruction);
		}
		flowAbrupted();
	}

	@Override
	public void visitRNextStatement(final RNextStatement rNextStatement)
	{
		addNode(new InstructionImpl(rNextStatement, myInstructionNumber++));
		final RLoopStatement loop = rNextStatement.getLoop();
		// case doesn`t support next
		if(loop != null && !(loop instanceof RCaseStatement))
		{
			final InstructionImpl instruction = findInstructionByElement(loop);
			if(instruction != null)
			{
				addEdge(myPrevInstruction, instruction);
			}
			flowAbrupted();
		}
	}

	@Override
	public void visitRRedoStatement(final RRedoStatement rRedoStatement)
	{
		addNode(new InstructionImpl(rRedoStatement, myInstructionNumber++));
		final RLoopStatement loop = rRedoStatement.getLoop();
		// case doesn`t support redo
		if(loop != null && !(loop instanceof RCaseStatement))
		{
			final RPsiElement body = loop.getLoopBody();
			final InstructionImpl instruction = findInstructionByElement(body);
			if(instruction != null)
			{
				addEdge(myPrevInstruction, instruction);
			}
			flowAbrupted();
		}
	}

/*
// Exceptions handling in ruby
// TODO[oleg]: add Exceptions in controlflow
*/

	/**
	 * Looks for exception info for handler, that can handle given type
	 * @param thrownType ThrownType
	 * @return ExceptionInfo
	 */
/*
	@Nullable
    private ExceptionInfo findCatch(@NotNull final RType thrownType) {
        for (int i = myExceptionInfos.size() - 1; i >= 0; i--) {
            final ExceptionInfo info = myExceptionInfos.get(i);
            RRescueBlock rescue = info.myRescue;
            RPsiElement exception = rescue.getException();
            if (exception != null) {
                final RType expressionType = exception instanceof RExpression ?
                        ((RExpression) exception).getType(myFileSymbol, TypeConstraints.createEmpty()) : RType.NOT_TYPED;
                return info;
//                if (expressionType.contains(thrownType).isEmpty()){
//                    return info;
//                }
            }
        }
        return null;
    }

    public void visitRaiseCall(@NotNull final RCall raiseCall) {
        final List<RPsiElement> elementList = raiseCall.getArguments();
        final RPsiElement exception = !elementList.isEmpty() ? elementList.get(0) : null;
        if (exception != null) {
            exception.accept(this);
            final InstructionImpl throwInstruction = startNode(raiseCall);
            flowAbrupted();
            final RType exceptionType = exception instanceof RExpression ?
                    ((RExpression) exception).getType(myFileSymbol, TypeConstraints.createEmpty()) : null;
            if (exceptionType!=null && exceptionType.isTyped()) {
                final ExceptionInfo info = findCatch(exceptionType);
                if (info != null) {
                    info.myThrowers.add(throwInstruction);
                } else {
                    addPendingEdge(null, throwInstruction);
                }
            } else {
                addPendingEdge(null, throwInstruction);
            }
            finishNode(throwInstruction);
        }
    }

    private PostCallInstructionImpl addCallNode(InstructionImpl finallyInstruction,
                                                RPsiElement scopeWhenAddPending,
                                                InstructionImpl src) {
        flowAbrupted();
        final CallInstructionImpl call = new CallInstructionImpl(myInstructionNumber++, finallyInstruction);
        addNode(call);
        addEdge(call, finallyInstruction);
        addEdge(src, call);
        PostCallInstructionImpl postCall = new PostCallInstructionImpl(myInstructionNumber++, call);
        addNode(postCall);
        addPendingEdge(scopeWhenAddPending, postCall);
        return postCall;
    }

    private void addEnsureEdges(InstructionImpl finallyInstruction, Set<PostCallInstructionImpl> calls) {
        final List<Pair<InstructionImpl, RPsiElement>> copy = myPending;
        myPending = new ArrayList<Pair<InstructionImpl, RPsiElement>>();
        for (Pair<InstructionImpl, RPsiElement> pair : copy) {
            calls.add(addCallNode(finallyInstruction, pair.getSecond(), pair.getFirst()));
        }
    }

    public void visitRBodyStatement(final RBodyStatement rBodyStatement) {
        final RCompoundStatement tryBlock = rBodyStatement.getBlock();
        // gather rescue blocks
        final List<RRescueBlock> rescueBlocks = rBodyStatement.getRescueBlocks();
        final REnsureBlock ensureBlock = rBodyStatement.getEnsureBlock();
        for (int i = rescueBlocks.size() - 1; i >= 0; i--) {
            myExceptionInfos.push(new ExceptionInfo(rescueBlocks.get(i)));
        }

        List<Pair<InstructionImpl, RPsiElement>> oldPending = null;
        if (ensureBlock != null) {
            //copy pending instructions
            oldPending = myPending;
            myPending = new ArrayList<Pair<InstructionImpl, RPsiElement>>();
        }

        InstructionImpl tryBeg = null;
        InstructionImpl tryEnd = null;
        if (tryBlock != null) {
            tryBeg = startNode(tryBlock);
            tryBlock.accept(this);
            tryEnd = myPrevInstruction;
            finishNode(tryBeg);
        }

        InstructionImpl[][] throwers = new InstructionImpl[rescueBlocks.size()][];
        for (int i = 0; i < rescueBlocks.size(); i++) {
            final List<InstructionImpl> list = myExceptionInfos.pop().myThrowers;
            throwers[i] = list.toArray(new InstructionImpl[list.size()]);
        }

        InstructionImpl[] catches = new InstructionImpl[rescueBlocks.size()];

        for (int i = 0; i < rescueBlocks.size(); i++) {
            flowAbrupted();
            final InstructionImpl catchBeg = startNode(rescueBlocks.get(i));
            for (InstructionImpl thrower : throwers[i]) {
                addEdge(thrower, catchBeg);
            }

            if (tryBeg != null) addEdge(tryBeg, catchBeg);
            if (tryEnd != null) addEdge(tryEnd, catchBeg);
            rescueBlocks.get(i).accept(this);
            catches[i] = myPrevInstruction;
            finishNode(catchBeg);
        }

        if (ensureBlock != null) {
            flowAbrupted();
            final InstructionImpl finallyInstruction = startNode(ensureBlock);
            Set<PostCallInstructionImpl> postCalls = new LinkedHashSet<PostCallInstructionImpl>();
            addEnsureEdges(finallyInstruction, postCalls);

            if (tryEnd == null) {
                tryEnd = startNode(null);
                finishNode(tryEnd);
            }

            postCalls.add(addCallNode(finallyInstruction, rBodyStatement, tryEnd));

            for (InstructionImpl catchEnd : catches) {
                if (catchEnd != null) {
                    postCalls.add(addCallNode(finallyInstruction, rBodyStatement, catchEnd));
                }
            }

            myPrevInstruction = finallyInstruction;
            ensureBlock.accept(this);
            final RetInstruction retInsn = new RetInstruction(myInstructionNumber++);
            for (PostCallInstructionImpl postCall : postCalls) {
                postCall.setReturnInstruction(retInsn);
                addEdge(retInsn, postCall);
            }
            addNode(retInsn);
            flowAbrupted();
            finishNode(finallyInstruction);

            assert oldPending != null;
            oldPending.addAll(myPending);
            myPending = oldPending;
        } else {
            if (tryEnd != null) {
                addPendingEdge(rBodyStatement, tryEnd);
            }
        }
    }

    public void visitRRetryStatement(final RRetryStatement rRetryStatement) {
        throw new UnsupportedOperationException("visitRRetryStatement isnot overriden correctly in org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl.RControlFlowBuilder");
    }

*/

}
