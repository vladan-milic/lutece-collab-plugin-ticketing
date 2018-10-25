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
package fr.paris.lutece.plugins.ticketing.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.prefs.AdminUserPreferencesService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * Class providing utility methods for for Model
 *
 */
public final class ModelUtils
{
    /**
     * Default constructor
     */
    private ModelUtils( )
    {
    }

    /**
     * Completes the specified model for user rights
     * 
     * @param model
     *            the model to complete
     * @param adminUser
     *            user the user
     */
    public static void storeTicketRights( Map<String, Object> model, AdminUser adminUser )
    {
        if ( RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_CREATION_RIGHT, Boolean.TRUE );
        }

        if ( RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_DELETE, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_DELETION_RIGHT, Boolean.TRUE );
        }

        if ( RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_MODIFY, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_MODIFICATION_RIGHT, Boolean.TRUE );
        }
        if ( RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_EXPORT, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_EXPORT_RIGHT, Boolean.TRUE );
        }
    }

    /**
     * Completes the specified model for the read-only HTML of the ticket responses
     * 
     * @param request
     *            the request
     * @param model
     *            the model to complete
     * @param ticket
     *            the ticket
     */
    public static void storeReadOnlyHtmlResponses( HttpServletRequest request, Map<String, Object> model, Ticket ticket )
    {
        List<Response> listResponses = ticket.getListResponse( );
        List<String> listReadOnlyResponseHtml = new ArrayList<String>( listResponses.size( ) );

        for ( Response response : listResponses )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry( ) );
            listReadOnlyResponseHtml.add( entryTypeService.getResponseValueForRecap( response.getEntry( ), request, response, request.getLocale( ) ) );
        }

        model.put( TicketingConstants.MARK_LIST_READ_ONLY_HTML_RESPONSES, listReadOnlyResponseHtml );
    }

    /**
     * Completes the specified model for rich text
     * 
     * @param request
     *            the request
     * @param model
     *            the model to complete
     */
    public static void storeRichText( HttpServletRequest request, Map<String, Object> model )
    {
        model.put( TicketingConstants.MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( TicketingConstants.MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
    }

    /**
     * Completes the specified model for channels selection
     * 
     * @param request
     *            the request
     * @param model
     *            the model to complete
     */
    public static void storeChannels( HttpServletRequest request, Map<String, Object> model )
    {
        model.put( TicketingConstants.MARK_CHANNELS_LIST, ChannelHome.getReferenceList( ) );
        model.put( TicketingConstants.MARK_SELECTABLE_CHANNELS_LIST, TicketUtils.getSelectableChannelsList( request ) );

        String strPreferredIdChannel = AdminUserPreferencesService.instance( ).get( String.valueOf( AdminUserService.getAdminUser( request ).getUserId( ) ),
                TicketingConstants.USER_PREFERENCE_PREFERRED_CHANNEL, StringUtils.EMPTY );
        model.put( TicketingConstants.MARK_PREFERRED_ID_CHANNEL, strPreferredIdChannel );
    }

    /**
     * Completes the specified model for the user signature
     * 
     * @param request
     *            the request
     * @param model
     *            the model to complete
     */
    public static void storeUserSignature( HttpServletRequest request, Map<String, Object> model )
    {
        String strUserSignature = AdminUserPreferencesService.instance( ).get( String.valueOf( AdminUserService.getAdminUser( request ).getUserId( ) ), TicketingConstants.USER_PREFERENCE_SIGNATURE,
                StringUtils.EMPTY );

        model.put( TicketingConstants.MARK_USER_SIGNATURE, strUserSignature );
    }

    /**
     * Completes the specified model with the navigation between tickets.
     *
     * @param nIdCurrentTicket
     *            the id of the current ticket
     * @param listTickets
     *            the tickets list
     * @param model
     *            the model
     */
    public static void storeNavigationBetweenTickets( int nIdCurrentTicket, List<Integer> listTickets, Map<String, Object> model )
    {
        Integer nTicketNext = null;
        Integer nTicketPrevious = null;

        if ( listTickets != null )
        {
            int nIdxCurrent = listTickets.indexOf( nIdCurrentTicket );
            if ( nIdxCurrent > -1 )
            {
                if ( nIdxCurrent > 0 )
                {
                    nTicketPrevious = listTickets.get( nIdxCurrent - 1 );
                }
                if ( nIdxCurrent < ( listTickets.size( ) - 1 ) )
                {
                    nTicketNext = listTickets.get( nIdxCurrent + 1 );
                }
            }
        }

        model.put( TicketingConstants.MARK_NEXT_TICKET, nTicketNext );
        model.put( TicketingConstants.MARK_PREVIOUS_TICKET, nTicketPrevious );
    }
}
