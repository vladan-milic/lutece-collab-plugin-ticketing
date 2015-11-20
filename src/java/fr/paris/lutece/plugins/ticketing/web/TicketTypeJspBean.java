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
package fr.paris.lutece.plugins.ticketing.web;

import fr.paris.lutece.plugins.ticketing.business.TicketType;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage TicketType features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTicketTypes.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "TICKETING_MANAGEMENT" )
public class TicketTypeJspBean extends ManageAdminTicketingJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETTYPES = "/admin/plugins/ticketing/manage_tickettypes.html";
    private static final String TEMPLATE_CREATE_TICKETTYPE = "/admin/plugins/ticketing/create_tickettype.html";
    private static final String TEMPLATE_MODIFY_TICKETTYPE = "/admin/plugins/ticketing/modify_tickettype.html";

    // Parameters
    private static final String PARAMETER_ID_TICKETTYPE = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETTYPES = "ticketing.manage_tickettypes.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETTYPE = "ticketing.modify_tickettype.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKETTYPE = "ticketing.create_tickettype.pageTitle";

    // Markers
    private static final String MARK_TICKETTYPE_LIST = "tickettype_list";
    private static final String MARK_TICKETTYPE = "tickettype";
    private static final String JSP_MANAGE_TICKETTYPES = "jsp/admin/plugins/ticketing/ManageTicketTypes.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKETTYPE = "ticketing.message.confirmRemoveTicketType";
    private static final String PROPERTY_DEFAULT_LIST_TICKETTYPE_PER_PAGE = "ticketing.listTicketTypes.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.tickettype.attribute.";

    // Views
    private static final String VIEW_MANAGE_TICKETTYPES = "manageTicketTypes";
    private static final String VIEW_CREATE_TICKETTYPE = "createTicketType";
    private static final String VIEW_MODIFY_TICKETTYPE = "modifyTicketType";

    // Actions
    private static final String ACTION_CREATE_TICKETTYPE = "createTicketType";
    private static final String ACTION_MODIFY_TICKETTYPE = "modifyTicketType";
    private static final String ACTION_REMOVE_TICKETTYPE = "removeTicketType";
    private static final String ACTION_CONFIRM_REMOVE_TICKETTYPE = "confirmRemoveTicketType";

    // Infos
    private static final String INFO_TICKETTYPE_CREATED = "ticketing.info.tickettype.created";
    private static final String INFO_TICKETTYPE_UPDATED = "ticketing.info.tickettype.updated";
    private static final String INFO_TICKETTYPE_REMOVED = "ticketing.info.tickettype.removed";

    // Session variable to store working values
    private TicketType _tickettype;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETTYPES, defaultView = true )
    public String getManageTicketTypes( HttpServletRequest request )
    {
        _tickettype = null;

        List<TicketType> listTicketTypes = (List<TicketType>) TicketTypeHome.getTicketTypesList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_TICKETTYPE_LIST, listTicketTypes,
                JSP_MANAGE_TICKETTYPES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETTYPES, TEMPLATE_MANAGE_TICKETTYPES, model );
    }

    /**
     * Returns the form to create a tickettype
     *
     * @param request The Http request
     * @return the html code of the tickettype form
     */
    @View( VIEW_CREATE_TICKETTYPE )
    public String getCreateTicketType( HttpServletRequest request )
    {
        _tickettype = ( _tickettype != null ) ? _tickettype : new TicketType(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKETTYPE, _tickettype );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKETTYPE, TEMPLATE_CREATE_TICKETTYPE, model );
    }

    /**
     * Process the data capture form of a new tickettype
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKETTYPE )
    public String doCreateTicketType( HttpServletRequest request )
    {
        populate( _tickettype, request );

        // Check constraints
        if ( !validateBean( _tickettype, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TICKETTYPE );
        }

        TicketTypeHome.create( _tickettype );
        addInfo( INFO_TICKETTYPE_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETTYPES );
    }

    /**
     * Manages the removal form of a tickettype whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKETTYPE )
    public String getConfirmRemoveTicketType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETTYPE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKETTYPE ) );
        url.addParameter( PARAMETER_ID_TICKETTYPE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKETTYPE,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a tickettype
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage tickettypes
     */
    @Action( ACTION_REMOVE_TICKETTYPE )
    public String doRemoveTicketType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETTYPE ) );
        TicketTypeHome.remove( nId );
        addInfo( INFO_TICKETTYPE_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETTYPES );
    }

    /**
     * Returns the form to update info about a tickettype
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKETTYPE )
    public String getModifyTicketType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETTYPE ) );

        if ( ( _tickettype == null ) || ( _tickettype.getId(  ) != nId ) )
        {
            _tickettype = TicketTypeHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKETTYPE, _tickettype );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETTYPE, TEMPLATE_MODIFY_TICKETTYPE, model );
    }

    /**
     * Process the change form of a tickettype
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKETTYPE )
    public String doModifyTicketType( HttpServletRequest request )
    {
        populate( _tickettype, request );

        // Check constraints
        if ( !validateBean( _tickettype, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TICKETTYPE, PARAMETER_ID_TICKETTYPE, _tickettype.getId(  ) );
        }

        TicketTypeHome.update( _tickettype );
        addInfo( INFO_TICKETTYPE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETTYPES );
    }
}
