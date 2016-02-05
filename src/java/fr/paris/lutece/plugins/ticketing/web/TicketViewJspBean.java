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
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketCriticality;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketPriority;
import fr.paris.lutece.plugins.ticketing.service.TicketingUtils;
import fr.paris.lutece.plugins.ticketing.service.download.TicketingFileServlet;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * TicketViewJspBean
 */
@Controller( controllerJsp = "TicketView.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "TICKETING_TICKETS_MANAGEMENT" )
public class TicketViewJspBean extends MVCAdminJspBean
{
    // Templates
    private static final String TEMPLATE_VIEW_TICKET_DETAILS = "/admin/plugins/ticketing/ticket/view_ticket_details.html";
    private static final String TEMPLATE_VIEW_TICKET_HISTORY = "/admin/plugins/ticketing/ticket/view_ticket_history.html";

    // Markers
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_PRIORITY = "priority";
    private static final String MARK_CRITICALITY = "criticality";
    private static final String MARK_HISTORY = "history";
    private static final String MARK_LIST_READ_ONLY_HTML_RESPONSES = "read_only_reponses_html_list";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TICKET_DETAILS = "ticketing.view_ticket.pageTitle";

    // Views
    private static final String VIEW_DETAILS = "ticketDetails";
    private static final String VIEW_HISTORY = "ticketHistory";
    private static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";

    // Actions
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";

    // Other constants
    private static final long serialVersionUID = 1L;

    /**
     * Gets the Details tab of the Ticket View
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_DETAILS, defaultView = true )
    public String getTicketDetails( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );
        int nIdTicket = Integer.parseInt( strIdTicket );

        Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
        TicketingUtils.setWorkflowAttributes( ticket, getUser(  ) );

        String strCustomerId = request.getParameter( TicketingConstants.PARAMETER_CUSTOMER_ID );

        if ( !StringUtils.isEmpty( strCustomerId ) && StringUtils.isEmpty( ticket.getCustomerId(  ) ) )
        {
            ticket.setCustomerId( strCustomerId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKET, ticket );
        model.put( MARK_PRIORITY, TicketPriority.valueOf( ticket.getPriority(  ) ).getLocalizedMessage( getLocale(  ) ) );
        model.put( MARK_CRITICALITY,
            TicketCriticality.valueOf( ticket.getCriticality(  ) ).getLocalizedMessage( getLocale(  ) ) );

        List<Response> listResponses = ticket.getListResponse(  );
        List<String> listReadOnlyResponseHtml = new ArrayList<String>( listResponses.size(  ) );

        for ( Response response : listResponses )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry(  ) );
            listReadOnlyResponseHtml.add( entryTypeService.getResponseValueForRecap( response.getEntry(  ), request,
                    response, getLocale(  ) ) );
        }

        if ( StringUtils.isNotEmpty( ticket.getCustomerId(  ) ) &&
                StringUtils.isNotEmpty( AppPropertiesService.getProperty( TicketingConstants.PROPERTY_POCGRU_URL_360 ) ) )
        {
            UrlItem url = new UrlItem( AppPropertiesService.getProperty( TicketingConstants.PROPERTY_POCGRU_URL_360 ) );
            url.addParameter( TicketingConstants.PARAMETER_GRU_CUSTOMER_ID, ticket.getCustomerId(  ) );
            model.put( TicketingConstants.MARK_POCGRU_URL_360, url.getUrl(  ) );
        }

        model.put( MARK_LIST_READ_ONLY_HTML_RESPONSES, listReadOnlyResponseHtml );

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
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );
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

    /**
     * Get the workflow action form before processing the action. If the action
     * does not need to display any form, then redirect the user to the workflow
     * action processing page.
     *
     * @param request
     *            The request
     * @return The HTML content to display, or the next URL to redirect the user
     *         to
     */
    @View( VIEW_WORKFLOW_ACTION_FORM )
    public String getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
            {
                String strHtmlTasksForm = WorkflowService.getInstance(  )
                                                         .getDisplayTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE,
                        nIdAction, request, getLocale(  ) );

                Map<String, Object> model = new HashMap<String, Object>(  );

                model.put( TicketingConstants.MARK_TASKS_FORM, strHtmlTasksForm );
                model.put( TicketingConstants.MARK_WORKFLOW_ID_ACTION, nIdAction );
                model.put( TicketingConstants.MARK_ID_TICKET, nIdTicket );

                //used to hide next button (if an error occured in html tasks form generation)
                if ( request.getAttribute( TicketingConstants.ATTRIBUTE_HIDE_NEXT_STEP_BUTTON ) != null )
                {
                    model.put( TicketingConstants.MARK_HIDE_NEXT_STEP_BUTTON,
                        request.getAttribute( TicketingConstants.ATTRIBUTE_HIDE_NEXT_STEP_BUTTON ) );
                }

                return getPage( TicketingConstants.PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW,
                    TicketingConstants.TEMPLATE_TASKS_FORM_WORKFLOW, model );
            }

            return doProcessWorkflowAction( request );
        }

        return redirectView( request, VIEW_DETAILS );
    }

    /**
     * Do process a workflow action over a ticket
     *
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_PROCESS_WORKFLOW_ACTION )
    public String doProcessWorkflowAction( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( request.getParameter( TicketingConstants.PARAMETER_BACK ) == null )
            {
                Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );

                if ( WorkflowService.getInstance(  ).isDisplayTasksForm( nIdAction, getLocale(  ) ) )
                {
                    String strError = WorkflowService.getInstance(  )
                                                     .doSaveTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE,
                            nIdAction, ticketCategory.getId(  ), request, getLocale(  ) );

                    if ( strError != null )
                    {
                        return redirect( request, strError );
                    }
                }
                else
                {
                    WorkflowService.getInstance(  )
                                   .doProcessAction( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction,
                        ticketCategory.getId(  ), request, getLocale(  ), false );
                }
            }
        }

        return redirectView( request, VIEW_DETAILS );
    }
}
