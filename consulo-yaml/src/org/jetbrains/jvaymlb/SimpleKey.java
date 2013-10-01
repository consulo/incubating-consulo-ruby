/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */
package org.jetbrains.jvaymlb;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 */
class SimpleKey
{
	private int tokenNumber;
	private boolean required;
	private int index;
	private int line;
	private int column;

	public SimpleKey(final int tokenNumber, final boolean required, final int index, final int line, final int column)
	{
		this.tokenNumber = tokenNumber;
		this.required = required;
		this.index = index;
		this.line = line;
		this.column = column;
	}

	public boolean isRequired()
	{
		return required;
	}

	public int getTokenNumber()
	{
		return this.tokenNumber;
	}

	public int getColumn()
	{
		return this.column;
	}
}// SimpleKey
