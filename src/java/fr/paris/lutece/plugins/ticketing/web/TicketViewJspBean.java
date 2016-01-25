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
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketingUtils;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * TicketViewJspBean
 */
@Controller( controllerJsp = "TicketView.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "TICKETING_TICKETS_MANAGEMENT" )
public class TicketViewJspBean extends MVCAdminJspBean
{
    private static final String TEMPLATE_VIEW_TICKET_DETAILS = "/admin/plugins/ticketing/ticket/view_ticket_details.html";
    private static final String TEMPLATE_VIEW_TICKET_HISTORY = "/admin/plugins/ticketing/ticket/view_ticket_history.html";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_PRIORITY = "priority";
    private static final String MARK_CRITICALITY = "criticality";
    private static final String MARK_HISTORY = "history";
    private static final String PARAMETER_ID_TICKET = "id";
    private static final String PROPERTY_PAGE_TITLE_TICKET_DETAILS = "ticketing.view_ticket.pageTitle";
    private static final String VIEW_DETAILS = "ticketDetails";
    private static final String VIEW_HISTORY = "ticketHistory";
    private static final long serialVersionUID = 1L;

    /**
     * Gets the Details tab of the Ticket View
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_DETAILS, defaultView = true )
    public String getTicketDetails( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( PARAMETER_ID_TICKET );
        int nIdTicket = Integer.parseInt( strIdTicket );

        Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKET, ticket );
        model.put( MARK_PRIORITY, TicketingUtils.getPriority( ticket, getLocale(  ) ) );
        model.put( MARK_CRITICALITY, TicketingUtils.getCriticality( ticket, getLocale(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_TICKET_DETAILS, TEMPLATE_VIEW_TICKET_DETAILS, model );
    }

    /**
     * Gets the History tab of the Ticket View
     * @param request The HTTP request
     * @return The view
     */
    @View( VIEW_HISTORY )
    public String getTicketHistory( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( PARAMETER_ID_TICKET );
        int nIdTicket = Integer.parseInt( strIdTicket );

        Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
        TicketCategory category = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
        int nWorkflowId = category.getIdWorkflow(  );
        String strHistory = WorkflowService.getInstance(  )
                                           .getDisplayDocumentHistory( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE,
                nWorkflowId, request, getLocale(  ) );
        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKET, ticket );
        model.put( MARK_HISTORY, strHistory );

        return getPage( PROPERTY_PAGE_TITLE_TICKET_DETAILS, TEMPLATE_VIEW_TICKET_HISTORY, model );
    }
}
