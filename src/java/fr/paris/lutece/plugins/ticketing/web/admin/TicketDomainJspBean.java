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

import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.service.TicketDomainResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage TicketDomain features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTicketDomains.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class TicketDomainJspBean extends ManageAdminTicketingJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETDOMAINS = "/admin/plugins/ticketing/admin/manage_ticket_domains.html";
    private static final String TEMPLATE_CREATE_TICKETDOMAIN = "/admin/plugins/ticketing/admin/create_ticket_domain.html";
    private static final String TEMPLATE_MODIFY_TICKETDOMAIN = "/admin/plugins/ticketing/admin/modify_ticket_domain.html";

    // Parameters
    private static final String PARAMETER_ID_TICKETDOMAIN = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETDOMAINS = "ticketing.manage_ticketdomains.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETDOMAIN = "ticketing.modify_ticketdomain.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKETDOMAIN = "ticketing.create_ticketdomain.pageTitle";

    // Markers
    private static final String MARK_TICKETDOMAIN_LIST = "ticketdomain_list";
    private static final String MARK_TICKETDOMAIN = "ticketdomain";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String JSP_MANAGE_TICKETDOMAINS = "jsp/admin/plugins/ticketing/ManageTicketDomains.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKETDOMAIN = "ticketing.message.confirmRemoveTicketDomain";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticketdomain.attribute.";

    // Views
    private static final String VIEW_MANAGE_TICKETDOMAINS = "manageTicketDomains";
    private static final String VIEW_CREATE_TICKETDOMAIN = "createTicketDomain";
    private static final String VIEW_MODIFY_TICKETDOMAIN = "modifyTicketDomain";

    // Actions
    private static final String ACTION_CREATE_TICKETDOMAIN = "createTicketDomain";
    private static final String ACTION_MODIFY_TICKETDOMAIN = "modifyTicketDomain";
    private static final String ACTION_REMOVE_TICKETDOMAIN = "removeTicketDomain";
    private static final String ACTION_CONFIRM_REMOVE_TICKETDOMAIN = "confirmRemoveTicketDomain";

    // Infos
    private static final String INFO_TICKETDOMAIN_CREATED = "ticketing.info.ticketdomain.created";
    private static final String INFO_TICKETDOMAIN_UPDATED = "ticketing.info.ticketdomain.updated";
    private static final String INFO_TICKETDOMAIN_REMOVED = "ticketing.info.ticketdomain.removed";
    private static final long serialVersionUID = 1L;

    //Messages
    private static final String MESSAGE_CAN_NOT_REMOVE_DOMAIN_CATEGORIES_ARE_ASSOCIATE = "ticketing.message.canNotRemoveDomainCategoriesAreAssociate";

    // Session variable to store working values
    private TicketDomain _ticketdomain;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETDOMAINS, defaultView = true )
    public String getManageTicketDomains( HttpServletRequest request )
    {
        _ticketdomain = null;

        List<TicketDomain> listTicketDomains = (List<TicketDomain>) TicketDomainHome.getTicketDomainsList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_TICKETDOMAIN_LIST, listTicketDomains,
                JSP_MANAGE_TICKETDOMAINS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETDOMAINS, TEMPLATE_MANAGE_TICKETDOMAINS, model );
    }

    /**
     * Returns the form to create a ticketdomain
     *
     * @param request The Http request
     * @return the html code of the ticketdomain form
     */
    @View( VIEW_CREATE_TICKETDOMAIN )
    public String getCreateTicketDomain( HttpServletRequest request )
    {
        _ticketdomain = ( _ticketdomain != null ) ? _ticketdomain : new TicketDomain(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKETDOMAIN, _ticketdomain );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKETDOMAIN, TEMPLATE_CREATE_TICKETDOMAIN, model );
    }

    /**
     * Process the data capture form of a new ticketdomain
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKETDOMAIN )
    public String doCreateTicketDomain( HttpServletRequest request )
    {
        populate( _ticketdomain, request );

        // Check constraints
        if ( !validateBean( _ticketdomain, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TICKETDOMAIN );
        }

        TicketDomainHome.create( _ticketdomain );
        addInfo( INFO_TICKETDOMAIN_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETDOMAINS );
    }

    /**
     * Manages the removal form of a ticketdomain whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKETDOMAIN )
    public String getConfirmRemoveTicketDomain( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETDOMAIN ) );

        if ( !TicketDomainHome.canRemove( nId ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_DOMAIN_CATEGORIES_ARE_ASSOCIATE,
                    AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKETDOMAIN ) );
        url.addParameter( PARAMETER_ID_TICKETDOMAIN, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKETDOMAIN,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a ticketdomain
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage ticketdomains
     */
    @Action( ACTION_REMOVE_TICKETDOMAIN )
    public String doRemoveTicketDomain( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETDOMAIN ) );
        TicketDomainHome.remove( nId );
        addInfo( INFO_TICKETDOMAIN_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETDOMAINS );
    }

    /**
     * Returns the form to update info about a ticketdomain
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKETDOMAIN )
    public String getModifyTicketDomain( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETDOMAIN ) );

        if ( ( _ticketdomain == null ) || ( _ticketdomain.getId(  ) != nId ) )
        {
            _ticketdomain = TicketDomainHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKETDOMAIN, _ticketdomain );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETDOMAIN, TEMPLATE_MODIFY_TICKETDOMAIN, model );
    }

    /**
     * Process the change form of a ticketdomain
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKETDOMAIN )
    public String doModifyTicketDomain( HttpServletRequest request )
    {
        populate( _ticketdomain, request );

        // Check constraints
        if ( !validateBean( _ticketdomain, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TICKETDOMAIN, PARAMETER_ID_TICKETDOMAIN, _ticketdomain.getId(  ) );
        }

        TicketDomainHome.update( _ticketdomain );
        addInfo( INFO_TICKETDOMAIN_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETDOMAINS );
    }
}
