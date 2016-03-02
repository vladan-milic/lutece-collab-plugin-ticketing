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

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketDomainResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Class providing helper for Ticket web management
 *
 */
public final class TicketHelper
{
    // Properties
    private static final String PROPERTY_ADMINUSER_FRONT_ID = "ticketing.adminUser.front.id";

    /**
     * Default constructor
     */
    private TicketHelper(  )
    {
    }

    /**
     * store user rights in model
     * @param model model to store rights
     * @param adminUser user
     */
    public static void storeTicketRightsIntoModel( Map<String, Object> model, AdminUser adminUser )
    {
        if ( RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_CREATION_RIGHT, Boolean.TRUE );
        }

        if ( RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_DELETE, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_DELETION_RIGHT, Boolean.TRUE );
        }

        if ( RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_MODIFY, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_MODIFICATION_RIGHT, Boolean.TRUE );
        }
    }

    /**
     * Stores the read-only HTML of the ticket responses in the model
     * @param request the request
     * @param model the model
     * @param ticket the ticket
     */
    public static void storeReadOnlyHtmlResponsesIntoModel( HttpServletRequest request, Map<String, Object> model,
        Ticket ticket )
    {
        List<Response> listResponses = ticket.getListResponse(  );
        List<String> listReadOnlyResponseHtml = new ArrayList<String>( listResponses.size(  ) );

        for ( Response response : listResponses )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry(  ) );
            listReadOnlyResponseHtml.add( entryTypeService.getResponseValueForRecap( response.getEntry(  ), request,
                    response, request.getLocale(  ) ) );
        }

        model.put( TicketingConstants.MARK_LIST_READ_ONLY_HTML_RESPONSES, listReadOnlyResponseHtml );
    }

    /**
     * Completes the specified model for rich text
     * @param request the request
     * @param model the model to complete
     */
    public static void storeRichTextMarksIntoModel( HttpServletRequest request, Map<String, Object> model )
    {
        model.put( TicketingConstants.MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( TicketingConstants.MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );
    }

    /**
     * Registers the admin user for front office
     * @param request the request
     */
    public static void registerDefaultAdminUser( HttpServletRequest request )
    {
        AdminUser defaultUser = AdminUserHome.findByPrimaryKey( AppPropertiesService.getPropertyInt( 
                    PROPERTY_ADMINUSER_FRONT_ID, -1 ) );

        try
        {
            AdminAuthenticationService.getInstance(  ).registerUser( request, defaultUser );
        }
        catch ( AccessDeniedException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( UserNotSignedException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
    }

    /**
     * returns ReferenceList initialized with an Empty Item
     * @param strEmptyLabel label for empty value
     * @param strEmptyCode code for empty value
     * @return   listRefEmpty referenceList initialized with an Empty Item
     */
    public static ReferenceList getEmptyItemReferenceList( String strEmptyLabel, String strEmptyCode )
    {
        ReferenceItem refItemEmpty = new ReferenceItem(  );
        refItemEmpty.setCode( strEmptyCode );
        refItemEmpty.setName( strEmptyLabel );

        ReferenceList listRefEmpty = new ReferenceList(  );
        listRefEmpty.add( refItemEmpty );

        return listRefEmpty;
    }

    /**
     * returns true if ticket is assign to user's group
     * @param ticket ticket
     * @param lstUserUnits user's units
     * @return true if ticket belongs to user's group
     */
    public static boolean isTicketAssignedToUserGroup( Ticket ticket, List<Unit> lstUserUnits )
    {
        boolean result = false;

        for ( Unit unit : lstUserUnits )
        {
            if ( unit.getIdUnit(  ) == ticket.getAssigneeUnit(  ).getUnitId(  ) )
            {
                result = true;

                break;
            }
        }

        return result;
    }

    /**
     * @param user admin user
     * @param filter ticket filter
     * @param request http request
     * @param listAgentTickets user's ticket
     * @param listGroupTickets group's ticket
     * @param listDomainTickets domain's ticket
     */
    public static void setTicketsListByPerimeter( AdminUser user, TicketFilter filter, HttpServletRequest request,
        List<Ticket> listAgentTickets, List<Ticket> listGroupTickets, List<Ticket> listDomainTickets )
    {
        List<Ticket> listTickets = (List<Ticket>) TicketHome.getTicketsList( filter );

        List<Unit> lstUserUnits = UnitHome.findByIdUser( user.getUserId(  ) );

        //Filtering results
        for ( Ticket ticket : listTickets )
        {
            if ( RBACService.isAuthorized( ticket, TicketResourceIdService.PERMISSION_VIEW, user ) )
            {
                if ( ( ticket.getAssigneeUser(  ) != null ) &&
                        ( ticket.getAssigneeUser(  ).getAdminUserId(  ) == user.getUserId(  ) ) )
                {
                    //ticket assign to agent
                    listAgentTickets.add( ticket );
                }
                else if ( TicketHelper.isTicketAssignedToUserGroup( ticket, lstUserUnits ) )
                {
                    //ticket assign to agent group
                    listGroupTickets.add( ticket );
                }
                else
                {
                    TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain(  ) );

                    if ( RBACService.isAuthorized( ticketDomain, TicketDomainResourceIdService.PERMISSION_VIEW, user ) )
                    {
                        //ticket assign to domain
                        listDomainTickets.add( ticket );
                    }
                }
            }
        }
    }
}
