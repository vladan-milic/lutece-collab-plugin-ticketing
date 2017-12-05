/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.ticketing.web.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.viewing.Viewing;
import fr.paris.lutece.plugins.ticketing.business.viewing.ViewingHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * This class provides the user interface to manage Viewing features ( manage,
 * modify, remove )
 */
@Controller(controllerJsp = "ManageViewing.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT")
public class ViewingJspBean extends ManageAdminTicketingJspBean {

	private static final long serialVersionUID = -3983945855376772508L;

	// //////////////////////////////////////////////////////////////////////////
	// Constants

	// templates
	private static final String TEMPLATE_CREATE_VIEWING = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH
			+ "create_viewing.html";
	private static final String TEMPLATE_MANAGE_VIEWING = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH
			+ "manage_viewing.html";

	// Properties for page titles
	private static final String PROPERTY_PAGE_TITLE_CREATE_VIEWING = "ticketing.manage_viewing.pageTitle";
	private static final String PROPERTY_PAGE_TITLE_MANAGE_VIEWING = "ticketing.manage_viewing.pageTitle";

	// Markers
	private static final String MARK_VIEWING = "viewing";

	// Views
	private static final String VIEW_MANAGE_VIEWING = "manageViewing";

	// Actions
	private static final String ACTION_CREATE_VIEWING = "createViewing";
	private static final String ACTION_MODIFY_VIEWING = "modifyViewing";

	// Infos
	private static final String INFO_VIEWING_UPDATED = "ticketing.info.viewing.updated";
	private static final String INFO_VIEWING_CREATED = "ticketing.info.viewing.created";

	// Session variable to store working values
	private Viewing _viewing;

	/**
	 * Build the Manage View
	 * 
	 * @param request
	 *            The HTTP request
	 * @return The page
	 */
	@View(value = VIEW_MANAGE_VIEWING, defaultView = true)
	public String getManageViewing(HttpServletRequest request) {

		int nb = ViewingHome.getCountViewing();
		if (nb > 0) {
			_viewing = ViewingHome.getViewing();
			Map<String, Object> model = getModel();
			model.put(MARK_VIEWING, _viewing);
			ModelUtils.storeRichText(request, model);
			return getPage(PROPERTY_PAGE_TITLE_MANAGE_VIEWING, TEMPLATE_MANAGE_VIEWING, model);
		} else {
			_viewing = (_viewing != null) ? _viewing : new Viewing();

			Map<String, Object> model = getModel();
			model.put(MARK_VIEWING, _viewing);

			ModelUtils.storeRichText(request, model);

			return getPage(PROPERTY_PAGE_TITLE_CREATE_VIEWING, TEMPLATE_CREATE_VIEWING, model);
		}

	}

	/**
	 * Process the data capture form of a viewing
	 *
	 * @param request
	 *            The Http Request
	 * @return The Jsp URL of the process result
	 */
	@Action(ACTION_MODIFY_VIEWING)
	public String doModifyViewing(HttpServletRequest request) {
		populate(_viewing, request);
		ViewingHome.update(_viewing);
		addInfo(INFO_VIEWING_UPDATED, getLocale());
		return redirectView(request, VIEW_MANAGE_VIEWING);
	}

	/**
	 * Process the data capture form of a new viewing
	 *
	 * @param request
	 *            The Http Request
	 * @return The Jsp URL of the process result
	 */
	@Action(ACTION_CREATE_VIEWING)
	public String doCreateViewing(HttpServletRequest request) {
		populate(_viewing, request);
		ViewingHome.create(_viewing);
		addInfo(INFO_VIEWING_CREATED, getLocale());
		return redirectView(request, VIEW_MANAGE_VIEWING);
	}

}
