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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableXPage;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

/**
 * This class provides the user interface to view a ticket
 *
 */
@Controller( xpageName = TicketViewXPage.XPAGE_NAME, pageTitleI18nKey = TicketViewXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = TicketViewXPage.MESSAGE_PATH )
public class TicketViewXPage extends WorkflowCapableXPage
{
    protected static final String XPAGE_NAME = "ticketView";

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "ticketing.xpage.ticket.view.pageTitle";
    protected static final String MESSAGE_PATH = "ticketing.xpage.ticket.view.pagePathLabel";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -5182134645557350678L;

    // Templates
    private static final String TEMPLATE_VIEW_TICKET_DETAILS = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "view_ticket_details.html";

    // Views
    private static final String VIEW_DETAILS = "ticketDetails";

    /**
     * Gets the Ticket details view
     * 
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_DETAILS, defaultView = true )
    public XPage getTicketDetails( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );
        int nIdTicket = Integer.parseInt( strIdTicket );

        Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
        setWorkflowAttributes( request, ticket );

        Map<String, Object> model = getModel( );
        model.put( TicketingConstants.MARK_TICKET, ticket );

        ModelUtils.storeReadOnlyHtmlResponses( request, model, ticket );

        return getXPage( TEMPLATE_VIEW_TICKET_DETAILS, request.getLocale( ), model );
    }

    /**
     * Redirects to the default page
     * 
     * @param request
     *            the request
     * @return the default page
     */
    private XPage defaultRedirect( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        Map<String, String> mapParams = new HashMap<String, String>( );
        mapParams.put( TicketingConstants.PARAMETER_ID_TICKET, strIdTicket );

        return redirect( request, VIEW_DETAILS, mapParams );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected XPage redirectAfterWorkflowAction( HttpServletRequest request )
    {
        return defaultRedirect( request );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected XPage redirectWorkflowActionCancelled( HttpServletRequest request )
    {
        return defaultRedirect( request );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected XPage defaultRedirectWorkflowAction( HttpServletRequest request )
    {
        return defaultRedirect( request );
    }
}
