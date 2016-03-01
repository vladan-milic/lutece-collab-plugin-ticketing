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
import fr.paris.lutece.plugins.ticketing.business.TicketCriticality;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketPriority;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
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
@Controller( controllerJsp = "TicketView.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class TicketViewJspBean extends WorkflowCapableJspBean
{
    // Templates
    private static final String TEMPLATE_VIEW_TICKET_DETAILS = "/admin/plugins/ticketing/ticket/view_ticket_details.html";
    private static final String TEMPLATE_VIEW_TICKET_HISTORY = "/admin/plugins/ticketing/ticket/view_ticket_history.html";

    // Markers
    private static final String MARK_PRIORITY = "priority";
    private static final String MARK_CRITICALITY = "criticality";
    private static final String MARK_HISTORY = "history";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TICKET_DETAILS = "ticketing.view_ticket.pageTitle";

    // Parameters
    private static final String PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION = "redirect";

    // Views
    private static final String VIEW_DETAILS = "ticketDetails";
    private static final String VIEW_HISTORY = "ticketHistory";

    // Other constants
    private static final long serialVersionUID = 1L;

    // Variable
    private final TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );
    
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
        setWorkflowAttributes( ticket );

        String strCustomerId = request.getParameter( TicketingConstants.PARAMETER_CUSTOMER_ID );

        if ( !StringUtils.isEmpty( strCustomerId ) && StringUtils.isEmpty( ticket.getCustomerId(  ) ) )
        {
            ticket.setCustomerId( strCustomerId );
        }

        Map<String, Object> model = getModel(  );
        model.put( TicketingConstants.MARK_TICKET, ticket );
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

        model.put( TicketingConstants.MARK_LIST_READ_ONLY_HTML_RESPONSES, listReadOnlyResponseHtml );
        TicketHelper.storeTicketRightsIntoModel( model, getUser(  ) );

        model.put( TicketingConstants.MARK_JSP_CONTROLLER, getControllerJsp(  ) );
        
        _ticketFormService.removeTicketFromSession( request.getSession(  ) );

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

        String strHistory = getDisplayDocumentHistory( request, ticket );

        Map<String, Object> model = getModel(  );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_HISTORY, strHistory );

        return getPage( PROPERTY_PAGE_TITLE_TICKET_DETAILS, TEMPLATE_VIEW_TICKET_HISTORY, model );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectAfterWorkflowAction( HttpServletRequest request )
    {
        if ( StringUtils.isNotEmpty( 
                    (String) request.getSession(  ).getAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL ) ) )
        {
            String strRedirectUrl = (String) request.getSession(  ).getAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL ); 
            //we remove redirect session attribute before leaving
            request.getSession(  ).removeAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL );

            return redirect( request, strRedirectUrl );
        }
        else
        {
            if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION ) ) )
            {
                if ( _mapRedirectUrl.containsKey( request.getParameter( PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION ) ) )
                {
                    return redirect( request,
                        _mapRedirectUrl.get( request.getParameter( PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION ) ) );
                }
            }
        }

        return defaultRedirect( request );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectWorkflowActionCancelled( HttpServletRequest request )
    {
        return defaultRedirect( request );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String defaultRedirectWorkflowAction( HttpServletRequest request )
    {
        return defaultRedirect( request );
    }

    /**
     * Redirects to the default page
     * @param request the request
     * @return the default page
     */
    private String defaultRedirect( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        Map<String, String> mapParams = new HashMap<String, String>(  );
        mapParams.put( TicketingConstants.PARAMETER_ID_TICKET, strIdTicket );

        return redirect( request, VIEW_DETAILS, mapParams );
    }
}
