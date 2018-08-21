package org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RRescueBlock;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 09.04.2008
 */
class ExceptionInfo
{
	RRescueBlock myRescue;
	List<InstructionImpl> myThrowers = new ArrayList<InstructionImpl>();

	public ExceptionInfo(RRescueBlock rescue)
	{
		myRescue = rescue;
	}
}
