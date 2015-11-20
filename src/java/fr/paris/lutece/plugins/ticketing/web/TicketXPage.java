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

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.UserTitleHome;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage Ticket xpages ( manage, create, modify, remove )
 */
@Controller( xpageName = "ticket", pageTitleI18nKey = "ticketing.xpage.ticket.pageTitle", pagePathI18nKey = "ticketing.xpage.ticket.pagePathLabel" )
public class TicketXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_CREATE_TICKET = "/skin/plugins/ticketing/create_ticket.html";
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";

    // Views
    private static final String VIEW_MANAGE_TICKETS = "manageTickets";
    private static final String VIEW_CREATE_TICKET = "createTicket";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";

    // Session variable to store working values
    private Ticket _ticket;

    /**
     * Returns the form to create a ticket
     *
     * @param request The Http request
     * @return the html code of the ticket form
     */
    @View( value = VIEW_CREATE_TICKET, defaultView = true )
    public XPage getCreateTicket( HttpServletRequest request )
    {
        _ticket = ( _ticket != null ) ? _ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList(  ) );
        model.put( MARK_TICKET, _ticket );

        // FIXME Dynamic filling
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );

        return getXPage( TEMPLATE_CREATE_TICKET, request.getLocale(  ), model );
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKET )
    public XPage doCreateTicket( HttpServletRequest request )
    {
        populate( _ticket, request );

        // Check constraints
        if ( !validateBean( _ticket, getLocale( request ) ) )
        {
            return redirectView( request, VIEW_CREATE_TICKET );
        }

        TicketHome.create( _ticket );
        addInfo( INFO_TICKET_CREATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }
}
