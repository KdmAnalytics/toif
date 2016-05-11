/**
 *	Copyright (c) 2015 Vör Security Inc.
 *	All rights reserved.
 *	
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met:
 *	    * Redistributions of source code must retain the above copyright
 *	      notice, this list of conditions and the following disclaimer.
 *	    * Redistributions in binary form must reproduce the above copyright
 *	      notice, this list of conditions and the following disclaimer in the
 *	      documentation and/or other materials provided with the distribution.
 *	    * Neither the name of the <organization> nor the
 *	      names of its contributors may be used to endorse or promote products
 *	      derived from this software without specific prior written permission.
 *	
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *	DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ossindex.eclipse.common.impl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import net.ossindex.eclipse.common.ICUtils;

/** Stub utilities
 * 
 * @author Ken Duck
 *
 */
public class CUtilsStub implements ICUtils {

	@Override
	public void setProject(IProject project) {
	}

	@Override
	public boolean isCppCompilationUnit(IResource resource) {
		return false;
	}

	@Override
	public String[] getIncludePaths(IResource resource) {
		return new String[0];
	}

	@Override
	public String[] getMacros(IResource resource) {
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.ICUtils#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.ICUtils#getResource(java.lang.Object)
	 */
	@Override
	public Object getResource(Object object) {
		return object;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.eclipse.common.ICUtils#clean(java.lang.String)
	 */
	@Override
	public void clean(String[] builderIds) {
	}
}
