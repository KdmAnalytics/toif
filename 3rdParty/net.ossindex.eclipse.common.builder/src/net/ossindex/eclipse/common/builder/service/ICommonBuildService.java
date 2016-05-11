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
package net.ossindex.eclipse.common.builder.service;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.ImageDescriptor;

/** Allows extensions to register their builders and natures to provide
 * customized functionality.
 * 
 * @author Ken Duck
 *
 */
public interface ICommonBuildService
{

	/** Registering a nature allows us to enable/disable builder UI elements
	 * when projects of the expected nature are selected; for example the
	 * ManualBuild button
	 * 
	 * @param natureId
	 */
	void registerNature(String natureId);

	/** Registering a builder allows us to target specific behaviour at specific
	 * builders, such as the custom manual build button.
	 * 
	 * @param builderId
	 */
	void registerBuilder(String builderId);

	/** Return true if the supplied builderId is one of the registered builders.
	 * 
	 * @param builderId
	 * @return
	 */
	boolean containsBuilder(String builderId);

	/** Returns true if the provided project has a registered nature
	 * 
	 * @param project
	 * @return
	 */
	boolean supportsProject(IProject project);

	/**
	 * Brand the menu items and build buttons. The text and icons will depend
	 * on the particular build application.
	 * @param text
	 */
	void brandManualBuild(String text, ImageDescriptor icon);

	/** Get the menu text
	 * 
	 * @return
	 */
	String getMenuText();

	/** Get the menu/button icon
	 * 
	 * @return
	 */
	ImageDescriptor getIcon();
}
