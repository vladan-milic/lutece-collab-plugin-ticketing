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
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage TicketDomain features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTicketDomains.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class TicketDomainJspBean extends ManageAdminTicketingJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETDOMAINS = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "manage_ticket_domains.html";
    private static final String TEMPLATE_CREATE_TICKETDOMAIN = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "create_ticket_domain.html";
    private static final String TEMPLATE_MODIFY_TICKETDOMAIN = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "modify_ticket_domain.html";

    // Parameters
    private static final String PARAMETER_ID_TICKETDOMAIN = "id";
    private static final String PARAMETER_ORDER_TICKETDOMAIN = "domain_order";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETDOMAINS = "ticketing.manage_ticketdomains.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETDOMAIN = "ticketing.modify_ticketdomain.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKETDOMAIN = "ticketing.create_ticketdomain.pageTitle";

    // Markers
    private static final String MARK_TICKETDOMAIN_LIST = "ticketdomain_list";
    private static final String MARK_TICKETDOMAIN = "ticketdomain";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String JSP_MANAGE_TICKETDOMAINS = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH + "ManageTicketDomains.jsp";

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
    private static final String ACTION_MOVEUP_TICKETDOMAIN = "doMoveDomainUp";
    private static final String ACTION_MOVEDOWN_TICKETDOMAIN = "doMoveDomainDown";

    // Infos
    private static final String INFO_TICKETDOMAIN_CREATED = "ticketing.info.ticketdomain.created";
    private static final String INFO_TICKETDOMAIN_UPDATED = "ticketing.info.ticketdomain.updated";
    private static final String INFO_TICKETDOMAIN_REMOVED = "ticketing.info.ticketdomain.removed";
    private static final long serialVersionUID = 1L;

    // Messages
    private static final String MESSAGE_CAN_NOT_REMOVE_DOMAIN_CATEGORIES_ARE_ASSOCIATE = "ticketing.message.canNotRemoveDomainCategoriesAreAssociate";
    private static final String ERROR_TICKETDOMAIN_REMOVED = "ticketing.error.ticketdomain.removed";

    // Session variable to store working values
    private TicketDomain _ticketdomain;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETDOMAINS, defaultView = true )
    public String getManageTicketDomains( HttpServletRequest request )
    {
        _ticketdomain = null;

        // This List could be directly populated by DAO instead of loop
        List<TicketType> _ticketTypeList = TicketTypeHome.getTicketTypesList( );
        for ( TicketType _ticketType : _ticketTypeList )
        {
            List<TicketDomain> _ticketDomainListById = TicketDomainHome.getTicketDomainsListbyType( _ticketType.getId( ) );
            if ( _ticketDomainListById != null )
            {
                _ticketType.setDomainList( _ticketDomainListById );
            }
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_TICKET_TYPES_LIST, _ticketTypeList, JSP_MANAGE_TICKETDOMAINS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETDOMAINS, TEMPLATE_MANAGE_TICKETDOMAINS, model );
    }

    /**
     * Returns the form to create a ticketdomain
     *
     * @param request
     *            The Http request
     * @return the html code of the ticketdomain form
     */
    @View( VIEW_CREATE_TICKETDOMAIN )
    public String getCreateTicketDomain( HttpServletRequest request )
    {
        _ticketdomain = ( _ticketdomain != null ) ? _ticketdomain : new TicketDomain( );

        Map<String, Object> model = getModel( );
        model.put( MARK_TICKETDOMAIN, _ticketdomain );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList( ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKETDOMAIN, TEMPLATE_CREATE_TICKETDOMAIN, model );
    }

    /**
     * Process the data capture form of a new ticketdomain
     *
     * @param request
     *            The Http Request
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
        addInfo( INFO_TICKETDOMAIN_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TICKETDOMAINS );
    }

    /**
     * Manages the removal form of a ticketdomain whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKETDOMAIN )
    public String getConfirmRemoveTicketDomain( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETDOMAIN ) );

        if ( !TicketDomainHome.canRemove( nId ) )
        {
            return redirect( request,
                    AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_DOMAIN_CATEGORIES_ARE_ASSOCIATE, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKETDOMAIN ) );
        url.addParameter( PARAMETER_ID_TICKETDOMAIN, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKETDOMAIN, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a ticketdomain
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage ticketdomains
     */
    @Action( ACTION_REMOVE_TICKETDOMAIN )
    public String doRemoveTicketDomain( HttpServletRequest request )
    {
        try
        {
            int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETDOMAIN ) );

            TicketDomainHome.remove( nId );

            addInfo( INFO_TICKETDOMAIN_REMOVED, getLocale( ) );
        }
        catch( NumberFormatException | AppException e )
        {
            AppLogService.debug( "Error while removing TicketType " + e.getMessage( ) );
            addError( ERROR_TICKETDOMAIN_REMOVED, getLocale( ) );
        }
        return redirectView( request, VIEW_MANAGE_TICKETDOMAINS );

    }

    /**
     * Returns the form to update info about a ticketdomain
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKETDOMAIN )
    public String getModifyTicketDomain( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETDOMAIN ) );

        if ( ( _ticketdomain == null ) || ( _ticketdomain.getId( ) != nId ) )
        {
            _ticketdomain = TicketDomainHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_TICKETDOMAIN, _ticketdomain );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETDOMAIN, TEMPLATE_MODIFY_TICKETDOMAIN, model );
    }

    /**
     * Process the change form of a ticketdomain
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKETDOMAIN )
    public String doModifyTicketDomain( HttpServletRequest request )
    {
        populate( _ticketdomain, request );

        // Check constraints
        if ( !validateBean( _ticketdomain, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TICKETDOMAIN, PARAMETER_ID_TICKETDOMAIN, _ticketdomain.getId( ) );
        }

        TicketDomainHome.update( _ticketdomain );
        addInfo( INFO_TICKETDOMAIN_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TICKETDOMAINS );
    }

    /**
     * Handles the increment of position of a tickettype
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage ticketDomains
     */
    @Action( ACTION_MOVEUP_TICKETDOMAIN )
    public String doMoveUpTicketDomain( HttpServletRequest request )
    {
        return doMoveTicketDomain( request, true );
    }

    /**
     * Handles the decrement of position of a ticketDomain
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage ticketDomains
     */
    @Action( ACTION_MOVEDOWN_TICKETDOMAIN )
    public String doMoveDownTicketDomain( HttpServletRequest request )
    {
        return doMoveTicketDomain( request, false );
    }

    /**
     * Move a ticketDomain position up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the ticketDomain up, false to move it down
     * @return The next URL to redirect to
     */
    private String doMoveTicketDomain( HttpServletRequest request, boolean bMoveUp )
    {
        try
        {

            int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETDOMAIN ) );

            TicketDomainHome.updateDomainOrder( nId, bMoveUp );
        }
        catch( NumberFormatException e )
        {
            AppLogService.debug( "Error while moving TicketDomain. " + e.getMessage( ) );
        }

        return redirectView( request, VIEW_MANAGE_TICKETDOMAINS );
    }

}
