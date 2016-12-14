/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.ticketing.service.EntryTypeService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage TicketInputs features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTicketInputs.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = TicketInputsJspBean.RIGHT_MANAGETICKETINPUTS )
public class TicketInputsJspBean extends MVCAdminJspBean
{
    /**
     * Right to manage ticketing inputs
     */
    public static final String RIGHT_MANAGETICKETINPUTS = "TICKETING_MANAGEMENT";
    private static final long serialVersionUID = 1L;

    // templates
    private static final String TEMPLATE_MANAGE_TICKETINPUTS = TicketingConstants.TEMPLATE_ADMIN_TICKETINPUTS_FEATURE_PATH +
        "manage_ticketinputs.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETINPUTS = "ticketing.manage_ticketinputs.pageTitle";

    // Markers
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";
    private static final String MARK_LOCALE = "language";
    private static final String MARK_LOCALE_TINY = "locale";

    // Jsp
    private static final String JSP_MANAGE_TICKETINPUTS = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH +
        "ManageTicketInputs.jsp";

    // Properties

    // Views
    private static final String VIEW_MANAGE_TICKETINPUTS = "manageTicketInputs";

    /**
     * Default constructor
     */
    public TicketInputsJspBean(  )
    {
    }

    /**
     * Returns the form to update info about a ticketing form
     * @param request The HTTP request
     * @return The HTML form to update info
     * @throws AccessDeniedException If the user is not authorized to modify
     *             this ticketing form
     */
    @View( value = VIEW_MANAGE_TICKETINPUTS, defaultView = true )
    public String getManageTicketInputs( HttpServletRequest request )
        throws AccessDeniedException
    {
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        Map<String, Object> model = getModel(  );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance(  ).getEntryTypeReferenceList(  ) );
        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_LOCALE_TINY, getLocale(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETINPUTS, TEMPLATE_MANAGE_TICKETINPUTS, model );
    }

    /**
     * Get the URL to manage ticketing inputs
     *
     * @param request
     *            The request
     * @return The URL to manage ticketing inputs
     */
    public static String getURLManageTicketInputs( HttpServletRequest request )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_TICKETINPUTS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_TICKETINPUTS );

        return urlItem.getUrl(  );
    }
}
