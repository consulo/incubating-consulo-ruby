package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl;

import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.*;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.ReadWriteInstruction;

class ReadWriteInstructionImpl extends InstructionImpl implements ReadWriteInstruction
{
	public String myName;
	private Access myAccess;

	public ReadWriteInstructionImpl(final String name, final int number, final Access access)
	{
		super(access.getElement(), number);
		myName = name;
		myAccess = access;
	}

	@Override
	public String getVariableName()
	{
		return myName;
	}

	@Override
	public Access getAccess()
	{
		return myAccess;
	}

	@Override
	protected String getElementPresentation()
	{
		if(myAccess instanceof MethodParameterAccess)
		{
			return "METHOD PARAMETER " + myName;
		}
		if(myAccess instanceof BlockParameterAccess)
		{
			return "BLOCK PARAMETER " + myName;
		}
		if(myAccess instanceof AssignAccess)
		{
			return "ASSIGN " + myName;
		}
		if(myAccess instanceof CallAccess)
		{
			return "CALL " + myName;
		}
		if(myAccess instanceof ConstantAccess)
		{
			return "CONSTANT " + myName;
		}
		if(myAccess instanceof FieldWriteAccess)
		{
			return "FIELD_ASSIGN " + myName;
		}
		if(myAccess instanceof JavaTypedAccess)
		{
			return "JAVA_TYPED " + myName;
		}
		if(myAccess instanceof RescueBlockAccess)
		{
			return "RESCUE_VALUE " + myName;
		}
		if(myAccess instanceof ReadAccess)
		{
			return "READ " + myName;
		}
		throw new IllegalArgumentException("Wrong access: " + myAccess);
	}
}
