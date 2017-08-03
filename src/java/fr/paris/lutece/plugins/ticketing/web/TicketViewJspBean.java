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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexerException;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketCriticality;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketPriority;
import fr.paris.lutece.plugins.ticketing.service.TicketDomainResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.reference.ITicketReferenceService;
import fr.paris.lutece.plugins.ticketing.web.filter.SessionFilter;
import fr.paris.lutece.plugins.ticketing.web.user.UserFactory;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexerActionUtil;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.portal.service.content.ContentPostProcessor;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;

/**
 * TicketViewJspBean
 */
@Controller( controllerJsp = "TicketView.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class TicketViewJspBean extends WorkflowCapableJspBean
{
    // Templates
    private static final String TEMPLATE_VIEW_TICKET_DETAILS = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "view_ticket_details.html";

    // Markers
    private static final String MARK_PRIORITY = "priority";
    private static final String MARK_CRITICALITY = "criticality";
    private static final String MARK_HISTORY = "history";
    private static final String MARK_USER_FACTORY = "user_factory";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TICKET_DETAILS = "ticketing.view_ticket.pageTitle";

    // Views
    private static final String VIEW_DETAILS = "ticketDetails";

    // Actions
    private static final String ACTION_DETAILS_FROM_REFERENCE = "ticketReference";

    // Other constants
    private static final long serialVersionUID = 1L;
    private static final String TICKET_NOT_EXIST_REDIRECT_URL = TicketingConstants.ADMIN_CONTROLLLER_PATH + TicketingConstants.JSP_MANAGE_TICKETS;
    private static final String CONTENT_POST_PROCESSORS_LIST_BEAN_NAME = "workflow.commentContentPostProcessors.list";

    // Session keys
    private static boolean _bAvatarAvailable = ( PluginService.getPlugin( TicketingConstants.PLUGIN_AVATAR ) != null );

    // Variable
    private final TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );
    private final ITicketReferenceService _ticketReferenceService = SpringContextService.getBean( ITicketReferenceService.BEAN_NAME );
    private static final List<ContentPostProcessor> _listContentPostProcessors = SpringContextService.getBean( CONTENT_POST_PROCESSORS_LIST_BEAN_NAME );

    /**
     * Gets the Details tab of the Ticket View
     * 
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_DETAILS, defaultView = true )
    public String getTicketDetails( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        // Determine the url of the caller in the header of the request
        String strRedirectUrl = request.getQueryString( );
        if ( StringUtils.isBlank( strRedirectUrl ) )
        {
            strRedirectUrl = TICKET_NOT_EXIST_REDIRECT_URL;
        }
        else
        {
            String [ ] strArraySplitQuery = strRedirectUrl.split( SessionFilter.PARAM_RETURN_URL + TicketingConstants.EQUAL_SYMBOL );
            if ( strArraySplitQuery.length > 1 )
            {
                strRedirectUrl = strArraySplitQuery [1];
            }
            else
            {
                String strRedirectSessionUrl = RequestUtils.getParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_RETURN_URL );
                strRedirectUrl = ( StringUtils.isNotBlank( strRedirectSessionUrl ) ) ? strRedirectSessionUrl : TICKET_NOT_EXIST_REDIRECT_URL;
            }
        }

        // Check if the id is an integer
        int nIdTicket = -1;
        try
        {
            nIdTicket = Integer.parseInt( strIdTicket );
        }
        catch( NumberFormatException exception )
        {
            return redirect( request,
                    AdminMessageService.getMessageUrl( request, TicketingConstants.MESSAGE_ERROR_TICKET_NOT_EXISTS, strRedirectUrl, AdminMessage.TYPE_STOP ) );
        }

        // Check if the ticket is present in the database
        Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
        if ( ticket == null )
        {
            return redirect( request,
                    AdminMessageService.getMessageUrl( request, TicketingConstants.MESSAGE_ERROR_TICKET_NOT_EXISTS, strRedirectUrl, AdminMessage.TYPE_STOP ) );
        }

        TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain( ) );

        // check user rights
        if ( !RBACService.isAuthorized( ticket, TicketResourceIdService.PERMISSION_VIEW, getUser( ) )
                || !RBACService.isAuthorized( ticketDomain, TicketDomainResourceIdService.PERMISSION_VIEW_DETAIL, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        setWorkflowAttributes( ticket );

        String strCustomerId = request.getParameter( TicketingConstants.PARAMETER_CUSTOMER_ID );

        if ( !StringUtils.isEmpty( strCustomerId ) && StringUtils.isEmpty( ticket.getCustomerId( ) ) )
        {
            ticket.setCustomerId( strCustomerId );
        }

        Map<String, Object> model = getModel( );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_PRIORITY, TicketPriority.valueOf( ticket.getPriority( ) ).getLocalizedMessage( getLocale( ) ) );
        model.put( MARK_CRITICALITY, TicketCriticality.valueOf( ticket.getCriticality( ) ).getLocalizedMessage( getLocale( ) ) );

        List<Response> listResponses = ticket.getListResponse( );
        List<String> listReadOnlyResponseHtml = new ArrayList<String>( listResponses.size( ) );

        for ( Response response : listResponses )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( response.getEntry( ) );
            listReadOnlyResponseHtml.add( entryTypeService.getResponseValueForRecap( response.getEntry( ), request, response, getLocale( ) ) );
        }

        if ( StringUtils.isNotEmpty( ticket.getCustomerId( ) )
                && StringUtils.isNotEmpty( AppPropertiesService.getProperty( TicketingConstants.PROPERTY_POCGRU_URL_360 ) ) )
        {
            UrlItem url = new UrlItem( AppPropertiesService.getProperty( TicketingConstants.PROPERTY_POCGRU_URL_360 ) );
            url.addParameter( TicketingConstants.PARAMETER_GRU_CUSTOMER_ID, ticket.getCustomerId( ) );
            model.put( TicketingConstants.MARK_POCGRU_URL_360, url.getUrl( ) );
        }

        // navigation in authorized tickets : next <-> previous
        @SuppressWarnings( "unchecked" )
        List<Integer> listTickets = (List<Integer>) request.getSession( ).getAttribute( TicketingConstants.SESSION_LIST_TICKETS_NAVIGATION );

        ModelUtils.storeNavigationBetweenTickets( nIdTicket, listTickets, model );

        // navigation in authorized tickets : next <-> previous
        model.put( TicketingConstants.MARK_LIST_READ_ONLY_HTML_RESPONSES, listReadOnlyResponseHtml );
        ModelUtils.storeTicketRights( model, getUser( ) );

        model.put( TicketingConstants.MARK_JSP_CONTROLLER, getControllerJsp( ) );

        // Add link to all reference present in the ticket comment
        String strTicketComment = ticket.getTicketComment( );
        String strProcessResult = strTicketComment.replaceAll( System.lineSeparator( ), TicketingConstants.HTML_BR_BALISE );
        if ( _listContentPostProcessors != null && !_listContentPostProcessors.isEmpty( ) )
        {
            for ( ContentPostProcessor contentPostProcessor : _listContentPostProcessors )
            {
                strProcessResult = contentPostProcessor.process( request, strProcessResult );
            }
        }
        ticket.setTicketComment( strProcessResult );

        String strHistory = getDisplayDocumentHistory( request, ticket );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_HISTORY, strHistory );
        model.put( TicketingConstants.MARK_AVATAR_AVAILABLE, _bAvatarAvailable );
        model.put( MARK_USER_FACTORY, UserFactory.getInstance( ) );
        _ticketFormService.removeTicketFromSession( request.getSession( ) );

        // Validate if precision has been selected if the selected category has precisions
        if ( ticket.getTicketCategory( ).getId( ) != TicketingConstants.PROPERTY_UNSET_INT )
        {
            List<TicketCategory> listTicketCategory = TicketCategoryHome.findByDomainId( ticket.getIdTicketDomain( ) );
            for ( TicketCategory ticketCategoryByDomain : listTicketCategory )
            {
                if ( ticketCategoryByDomain.getLabel( ).equals( ticket.getTicketCategory( ).getLabel( ) )
                        && StringUtils.isNotBlank( ticketCategoryByDomain.getPrecision( ) )
                        && ( StringUtils.isNotBlank( request.getParameter( TicketingConstants.PARAMETER_TICKET_PRECISION_ID ) ) && request.getParameter(
                                TicketingConstants.PARAMETER_TICKET_PRECISION_ID ).equals( TicketingConstants.NO_ID_STRING ) ) )
                {
                    addError( TicketingConstants.MESSAGE_ERROR_TICKET_CATEGORY_PRECISION_NOT_SELECTED, getLocale( ) );
                    ticket.getTicketCategory( ).setPrecision( TicketingConstants.NO_ID_STRING );
                    break;
                }
            }
        }

        if ( TicketUtils.isAssignee( ticket, getUser( ) ) )
        {
            TicketHome.resetTicketMarkingId( ticket.getId( ) );
            try
            {
                TicketIndexer ticketIndexer = new TicketIndexer( );

                // We will not index the ticket comment with the link
                ticket.setTicketComment( strTicketComment );

                ticketIndexer.indexTicket( ticket );
            }
            catch( TicketIndexerException ticketIndexerException )
            {
                addError( TicketingConstants.ERROR_INDEX_TICKET_FAILED_BACK, getLocale( ) );

                // The indexation of the Ticket fail, we will store the Ticket in the table for the daemon
                IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
            }

        }

        String messageInfo = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO );

        if ( StringUtils.isNotEmpty( messageInfo ) )
        {
            addInfo( messageInfo );
            fillCommons( model );
        }
        else
        {
            messageInfo = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_MODIFY_ACTION_MESSAGE_INFO );
            if ( StringUtils.isNotEmpty( messageInfo ) )
            {
                addInfo( messageInfo );
                fillCommons( model );
            }
        }

        return getPage( PROPERTY_PAGE_TITLE_TICKET_DETAILS, TEMPLATE_VIEW_TICKET_DETAILS, model );
    }

    /**
     * Access the details of a ticket by its reference
     * 
     * @param request
     *            The HttpServletRequest
     * @return redirect to the details page of the ticket
     */
    @Action( ACTION_DETAILS_FROM_REFERENCE )
    public String getTicketDetailsByReference( HttpServletRequest request )
    {
        String strTicketReference = request.getParameter( TicketingConstants.MARK_TICKET_REFERENCE );

        Integer nIdTicket = null;
        if ( StringUtils.isNotBlank( strTicketReference ) )
        {
            nIdTicket = _ticketReferenceService.findIdTicketByReference( strTicketReference );
        }

        Map<String, String> mapParams = new HashMap<String, String>( );
        mapParams.put( TicketingConstants.PARAMETER_ID_TICKET, ( nIdTicket == null ) ? null : String.valueOf( nIdTicket ) );

        return redirect( request, VIEW_DETAILS, mapParams );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectAfterWorkflowAction( HttpServletRequest request )
    {
        String strRedirect = ( request.getAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION ) != null ) ? (String) request
                .getAttribute( TicketingConstants.ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION ) : request
                .getParameter( TicketingConstants.PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION );

        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_RETURN_URL );

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
     * 
     * @param request
     *            the request
     * @return the default page
     */
    private String defaultRedirect( HttpServletRequest request )
    {
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        Map<String, String> mapParams = new HashMap<String, String>( );
        mapParams.put( TicketingConstants.PARAMETER_ID_TICKET, strIdTicket );

        return redirect( request, VIEW_DETAILS, mapParams );
    }
}
