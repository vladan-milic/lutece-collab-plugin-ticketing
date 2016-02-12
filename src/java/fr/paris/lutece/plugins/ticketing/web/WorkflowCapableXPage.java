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
import fr.paris.lutece.plugins.ticketing.service.TicketingPocGruService;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.portal.web.xpages.XPage;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents a XPage which can use workflow
 *
 */
public abstract class WorkflowCapableXPage extends MVCApplication
{
    // Properties
    public static final String PROPERTIES_WORKFLOW_STATE_WAITING_USER_REPLY = "ticketing.workflow.state.id.waitingUserReply";
    private static final String PROPERTY_ADMINUSER_FRONT_ID = "ticketing.adminUser.front.id";

    // Services
    private static WorkflowService _workflowService = WorkflowService.getInstance(  );

    // Other constants

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -249042695023346133L;

    /**
     * set workflow attributes for displayable tickets
     * @param request the request
     * @param paginator paginator of tickets
     */
    protected void setWorkflowAttributes( HttpServletRequest request, LocalizedPaginator<Ticket> paginator )
    {
        for ( Ticket ticket : paginator.getPageItems(  ) )
        {
            setWorkflowAttributes( request, ticket );
        }
    }

    /**
     * set workflow attributes for the specified ticket
     * @param request the request
     * @param ticket the Ticket
     */
    protected void setWorkflowAttributes( HttpServletRequest request, Ticket ticket )
    {
        registerDefaultAdminUser( request );

        if ( _workflowService.isAvailable(  ) )
        {
            TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
            int nIdWorkflow = ticketCategory.getIdWorkflow(  );

            StateFilter stateFilter = new StateFilter(  );
            stateFilter.setIdWorkflow( nIdWorkflow );

            State state = _workflowService.getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticketCategory.getId(  ) );

            if ( state != null )
            {
                ticket.setState( state );
            }

            if ( nIdWorkflow > 0 )
            {
                Collection<Action> listWorkflowActions = _workflowService.getActions( ticket.getId(  ),
                        Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, AdminUserService.getAdminUser( request ) );

                ticket.setListWorkflowActions( listWorkflowActions );
            }
        }
    }

    /**
     * Tests if the ticket is in the state specified by the properties passed as a parameter
     * @param ticket the ticket
     * @param strStateProperty the property corresponding to the state id
     * @return {@code true} if the ticket is in the specified state, {@code false} otherwise
     */
    protected boolean isInState( Ticket ticket, String strStateProperty )
    {
        int nStateIdInConf = AppPropertiesService.getPropertyInt( strStateProperty, -1 );
        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
        int nIdWorkflow = ticketCategory.getIdWorkflow(  );

        State state = _workflowService.getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                ticketCategory.getId(  ) );

        return state.getId(  ) == nStateIdInConf;
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
    @View( TicketingConstants.VIEW_WORKFLOW_ACTION_FORM )
    public XPage getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        registerDefaultAdminUser( request );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( _workflowService.isDisplayTasksForm( nIdAction, getLocale( request ) ) )
            {
                String strHtmlTasksForm = _workflowService.getDisplayTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE,
                        nIdAction, request, getLocale( request ) );

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

                model.put( TicketingConstants.MARK_FORM_ACTION,
                    getActionFullUrl( TicketingConstants.ACTION_DO_PROCESS_WORKFLOW_ACTION ) );
                model.put( TicketingConstants.MARK_PAGE, getXPageName(  ) );

                IActionService actionService = SpringContextService.getBean( TicketingConstants.BEAN_ACTION_SERVICE );
                Action action = actionService.findByPrimaryKey( nIdAction );
                model.put( TicketingConstants.MARK_WORKFLOW_ACTION, action );

                return getXPage( TicketingConstants.TEMPLATE_TASKS_FORM_WORKFLOW, getLocale( request ), model );
            }

            return doProcessWorkflowAction( request );
        }

        return defaultRedirectWorkflowAction( request );
    }

    /**
     * Do process a workflow action over a ticket
     *
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( TicketingConstants.ACTION_DO_PROCESS_WORKFLOW_ACTION )
    public XPage doProcessWorkflowAction( HttpServletRequest request )
    {
        String strError = null;
        String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        registerDefaultAdminUser( request );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( request.getParameter( TicketingConstants.PARAMETER_BACK ) == null )
            {
                Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );

                if ( _workflowService.isDisplayTasksForm( nIdAction, getLocale( request ) ) )
                {
                    strError = _workflowService.doSaveTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction,
                            ticketCategory.getId(  ), request, getLocale( request ) );

                    if ( strError != null )
                    {
                        return redirect( request, strError );
                    }
                }
                else
                {
                    _workflowService.doProcessAction( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction,
                        ticketCategory.getId(  ), request, getLocale( request ), false );
                }
            }
            else
            {
                return redirectWorkflowActionCancelled( request );
            }
        }

        return redirectAfterWorkflowAction( request );
    }

    /**
     * Do process a workflow automatic action over a ticket
     *
     * @param ticket
     *            The ticket
     */
    protected void doProcessWorkflowAutomaticAction( Ticket ticket )
    {
        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );

        // TODO After POC GRU, set this variable with
        // ticketCategory.getIdWorkflow( );
        int nIdWorkflow = TicketingPocGruService.getWorkflowId( ticket );

        if ( ( nIdWorkflow > 0 ) && _workflowService.isAvailable(  ) )
        {
            try
            {
                _workflowService.getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticketCategory.getId(  ) );
                _workflowService.executeActionAutomatic( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticketCategory.getId(  ) );
            }
            catch ( Exception e )
            {
                doRemoveWorkFlowResource( ticket.getId(  ) );
                TicketHome.remove( ticket.getId(  ) );
                throw e;
            }
        }
    }

    /**
     * Do remove a workflow resource
     * @param nTicketId the ticket id associated to the workflow resource
     */
    protected void doRemoveWorkFlowResource( int nTicketId )
    {
        if ( _workflowService.isAvailable(  ) )
        {
            _workflowService.doRemoveWorkFlowResource( nTicketId, Ticket.TICKET_RESOURCE_TYPE );
        }
    }

    /**
     * Registers the admin user for front office
     * @param request the request
     */
    private void registerDefaultAdminUser( HttpServletRequest request )
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
     * Redirects to the correct page after workflow action
     * @param request the request
     * @return the page to redirect to
     */
    protected abstract XPage redirectAfterWorkflowAction( HttpServletRequest request );

    /**
     * Redirects to the correct page after the cancellation of the workflow action
     * @param request the request
     * @return the page to redirect to
     */
    protected abstract XPage redirectWorkflowActionCancelled( HttpServletRequest request );

    /**
     * Redirects to the default page (if workflow not enabled, if action id not correct, etc.)
     * @param request the request
     * @return the page to redirect to
     */
    protected abstract XPage defaultRedirectWorkflowAction( HttpServletRequest request );
}
