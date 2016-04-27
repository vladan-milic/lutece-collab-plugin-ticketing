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
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketCriticality;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketPriority;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final String TEMPLATE_VIEW_TICKET_DETAILS = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH +
        "view_ticket_details.html";

    // Markers
    private static final String MARK_PRIORITY = "priority";
    private static final String MARK_CRITICALITY = "criticality";
    private static final String MARK_HISTORY = "history";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TICKET_DETAILS = "ticketing.view_ticket.pageTitle";

    // Views
    private static final String VIEW_DETAILS = "ticketDetails";

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

        //check user rights
        if ( !RBACService.isAuthorized( ticket, TicketResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

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
//navigation in authorized tickets : next <-> previous
        @SuppressWarnings( "unchecked" )
		List<Ticket> listTickets = ( List<Ticket> ) request.getSession( ).getAttribute( TicketingConstants.SESSION_LIST_TICKETS_NAVIGATION );
    	
    	setNavigationBetweenTickets( nIdTicket, listTickets, model );
       
     
//navigation in authorized tickets : next <-> previous
        
        
        
        model.put( TicketingConstants.MARK_LIST_READ_ONLY_HTML_RESPONSES, listReadOnlyResponseHtml );
        ModelUtils.storeTicketRights( model, getUser(  ) );

        model.put( TicketingConstants.MARK_JSP_CONTROLLER, getControllerJsp(  ) );

        String strHistory = getDisplayDocumentHistory( request, ticket );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_HISTORY, strHistory );

        _ticketFormService.removeTicketFromSession( request.getSession(  ) );

        String messageInfo = TicketUtils.getParameter( request,
                TicketingConstants.ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO );

        if ( StringUtils.isNotEmpty( messageInfo ) )
        {
            addInfo( messageInfo );
            fillCommons( model );
        }

        return getPage( PROPERTY_PAGE_TITLE_TICKET_DETAILS, TEMPLATE_VIEW_TICKET_DETAILS, model );
    }

    
    /**
     * Sets the navigation between tickets.
     *
     * @param nIdTicket the n id ticket
     * @param listTickets the list tickets
     * @param model the model
     */
    private void setNavigationBetweenTickets ( int nIdTicket, List<Ticket> listTickets, Map<String, Object> model )
    {    	
    	Ticket tNext = null, tPrevious = null, tcurrent = null;    	
    	Boolean bexistIdTicket = false;
    	if( listTickets !=null )
    	{
    		Iterator<Ticket> crunchifyIterator = listTickets.iterator();   	
    		while ( crunchifyIterator.hasNext ( ) ) 
    		{		
    			tcurrent = crunchifyIterator.next( );
    		
    			if( nIdTicket == tcurrent.getId( ) && crunchifyIterator.hasNext( ) )
    			{
    				bexistIdTicket=true;
    				tNext = crunchifyIterator.next( );
    				break;
    			}
    			else if( nIdTicket != tcurrent.getId( ) )
    			{
    				tPrevious = tcurrent;
    			}
    		}	
    		
    		if( !bexistIdTicket ) tPrevious = null;
    	}    		
		 model.put( TicketingConstants.MARK_TICKET_NAVIGATION_NEXT,  ( tNext == null )? TicketingConstants.TICKET_NO_NAVIGATION : tNext.getId( ) );
	     model.put( TicketingConstants.MARK_TICKET_NAVIGATION_PREVIOUS,  ( tPrevious == null )? TicketingConstants.TICKET_NO_NAVIGATION : tPrevious.getId( ) );
    }
    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectAfterWorkflowAction( HttpServletRequest request )
    {
        String strRedirect = ( request.getAttribute( TicketingConstants.PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION ) != null )
            ? (String) request.getAttribute( TicketingConstants.PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION )
            : request.getParameter( TicketingConstants.PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION );

        String strRedirectUrl = TicketUtils.getParameter( request, TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( StringUtils.isNotEmpty( strRedirect ) && StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }
        else
        {
            if ( StringUtils.isNotEmpty( strRedirect ) )
            {
                if ( _mapRedirectUrl.containsKey( strRedirect ) )
                {
                    return redirect( request, _mapRedirectUrl.get( strRedirect ) );
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
