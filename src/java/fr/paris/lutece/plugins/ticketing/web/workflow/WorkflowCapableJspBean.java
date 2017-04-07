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
package fr.paris.lutece.plugins.ticketing.web.workflow;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.resourcehistory.IResourceHistoryInformationService;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexerException;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.user.UserFactory;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexerActionUtil;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * This class represents a JSP bean which can use workflow
 *
 */
public abstract class WorkflowCapableJspBean extends MVCAdminJspBean
{
    /** redirection map */
    protected static final Map<String, String> _mapRedirectUrl;

    // MARKS
    private static final String MARK_RESOURCE_HISTORY_CHANNEL = "resource_history_channel";
    private static final String MARK_USER_FACTORY = "user_factory";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "ticketing.taskFormWorkflow.pageTitle";

    // Bean
    private static final String BEAN_RESOURCE_HISTORY_INFORMATION_SERVICE = "workflow-ticketing.resourceHistoryService";

    // Templates
    private static final String TEMPLATE_RESOURCE_HISTORY = "admin/plugins/ticketing/workflow/ticket_history.html";

    // Infos
    private static final String INFO_WORKFLOW_ACTION_EXECUTED = "ticketing.info.workflow.action.executed";

    // Errors
    private static final String ERROR_WORKFLOW_ACTION_ABORTED = "ticketing.error.workflow.action.aborted.backoffice";

    static
    {
        _mapRedirectUrl = new HashMap<String, String>( );

        Enumeration<?> enumPropNames = AppPropertiesService.getProperties( ).propertyNames( );

        while ( enumPropNames.hasMoreElements( ) )
        {
            String key = (String) enumPropNames.nextElement( );

            if ( key.startsWith( TicketingConstants.PROPERTY_REDIRECT_PREFIX ) )
            {
                _mapRedirectUrl.put( ( key ).substring( TicketingConstants.PROPERTY_REDIRECT_PREFIX.length( ) ), AppPropertiesService.getProperty( key ) );
            }
        }
    }

    // Services
    private static WorkflowService _workflowService = WorkflowService.getInstance( );
    private static IResourceHistoryInformationService _resourceHistoryTicketingInformationService = SpringContextService
            .getBean( BEAN_RESOURCE_HISTORY_INFORMATION_SERVICE );
    private static StateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -249042695023346133L;

    /**
     * set workflow attributes needed for display a list of ticket
     * 
     * 
     * @param list
     *            of ticket to update
     */
    protected void setWorkflowAttributes( List<Ticket> listTicket )
    {
        if ( _workflowService.isAvailable( ) )
        {
            for ( Ticket ticket : listTicket )
            {
                TicketCategory ticketCategory = ticket.getTicketCategory( );
                int nIdWorkflow = ticketCategory.getIdWorkflow( );

                StateFilter stateFilter = new StateFilter( );
                stateFilter.setIdWorkflow( nIdWorkflow );

                State state = _workflowService.getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );

                if ( state != null )
                {
                    ticket.setState( state );
                }
            }
        }
    }

    /**
     * set workflow attributes for the specified ticket
     * 
     * @param ticket
     *            the Ticket
     */
    protected void setWorkflowAttributes( Ticket ticket )
    {
        if ( _workflowService.isAvailable( ) )
        {
            TicketCategory ticketCategory = ticket.getTicketCategory( );
            int nIdWorkflow = ticketCategory.getIdWorkflow( );

            StateFilter stateFilter = new StateFilter( );
            stateFilter.setIdWorkflow( nIdWorkflow );

            State state = _workflowService.getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );

            if ( state != null )
            {
                ticket.setState( state );
            }

            if ( nIdWorkflow > 0 )
            {
                Collection<Action> fullListWorkflowActions = getActions( ticket.getId( ), nIdWorkflow );
                Collection<Action> filteredListWorkflowActions = new ArrayList<Action>( fullListWorkflowActions );

                if ( TicketUtils.isTicketAssignToUserOrGroup( getUser( ), ticket ) || RBACService.isUserInRole( getUser( ), TicketingConstants.ROLE_GRU_ADMIN ) )
                {
                    // if ticket is assign to agent or to its group
                    for ( Action action : fullListWorkflowActions )
                    {
                        List<Integer> listActionFiltered = PluginConfigurationService.getIntegerList(
                                PluginConfigurationService.PROPERTY_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME, new ArrayList<Integer>( ) );

                        for ( Integer nActionId : listActionFiltered )
                        {
                            if ( ( action.getId( ) == nActionId ) && TicketUtils.isAssignee( ticket, getUser( ) ) )
                            {
                                // ticket already assign to the agent => removing action from list
                                filteredListWorkflowActions.remove( action );
                            }
                        }
                    }
                }
                else
                {
                    // ticket is not assign to agent or its group => no Workflow actions shown
                    filteredListWorkflowActions = new ArrayList<Action>( );
                }

                ticket.setListWorkflowActions( filteredListWorkflowActions );
            }
        }
    }

    /**
     * Gives a list of actions possible for a given ticket based on its status in the workflow and the user role
     * 
     * @param nTicketId
     *            the ticket id
     * @param nIdWorkflow
     *            the workflow id
     * @return the list of possible actions
     */
    private Collection<Action> getActions( int nTicketId, int nIdWorkflow )
    {
        Collection<Action> listTicketingActions;

        if ( nIdWorkflow > 0 )
        {
            listTicketingActions = new ArrayList<Action>( );

            Collection<Action> listWorkflowActions = _workflowService.getActions( nTicketId, Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, getUser( ) );

            for ( Action workflowAction : listWorkflowActions )
            {
                Action ticketingAction = new TicketingAction( workflowAction, _workflowService.isDisplayTasksForm( workflowAction.getId( ), getLocale( ) ) );
                listTicketingActions.add( ticketingAction );
            }
        }
        else
        {
            listTicketingActions = Collections.emptyList( );
        }

        return listTicketingActions;
    }

    /**
     * Get the workflow action form before processing the action. If the action does not need to display any form, then redirect the user to the workflow action
     * processing page.
     *
     * @param request
     *            The request
     * @return The HTML content to display, or the next URL to redirect the user to
     */
    @View( TicketingConstants.VIEW_WORKFLOW_ACTION_FORM )
    public String getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) && StringUtils.isNotEmpty( strIdTicket )
                && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( _workflowService.isDisplayTasksForm( nIdAction, getLocale( ) ) )
            {
                String strHtmlTasksForm = _workflowService.getDisplayTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction, request, getLocale( ) );

                Map<String, Object> model = new HashMap<String, Object>( );

                model.put( TicketingConstants.MARK_TASKS_FORM, strHtmlTasksForm );
                model.put( TicketingConstants.MARK_WORKFLOW_ID_ACTION, nIdAction );
                model.put( TicketingConstants.MARK_ID_TICKET, nIdTicket );

                // used to hide next button (if an error occured in html tasks form generation)
                if ( request.getAttribute( TicketingConstants.ATTRIBUTE_HIDE_NEXT_STEP_BUTTON ) != null )
                {
                    model.put( TicketingConstants.MARK_HIDE_NEXT_STEP_BUTTON, request.getAttribute( TicketingConstants.ATTRIBUTE_HIDE_NEXT_STEP_BUTTON ) );
                }

                String strFormAction = getActionUrl( TicketingConstants.ACTION_DO_PROCESS_WORKFLOW_ACTION );
                String jspController = request.getParameter( TicketingConstants.PARAMETER_JSP_CONTROLLER );

                if ( jspController != null )
                {
                    strFormAction = TicketingConstants.ADMIN_CONTROLLLER_PATH + jspController;
                }

                model.put( TicketingConstants.MARK_FORM_ACTION, strFormAction );
                model.put( TicketingConstants.MARK_POPIN_MODE, jspController != null );

                IActionService actionService = SpringContextService.getBean( TicketingConstants.BEAN_ACTION_SERVICE );
                Action action = actionService.findByPrimaryKey( nIdAction );
                model.put( TicketingConstants.MARK_WORKFLOW_ACTION, action );

                return getPage( PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW, TicketingConstants.TEMPLATE_TASKS_FORM_WORKFLOW, model );
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
    public String doProcessWorkflowAction( HttpServletRequest request )
    {
        String strError = null;
        String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) && StringUtils.isNotEmpty( strIdTicket )
                && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( request.getParameter( TicketingConstants.PARAMETER_BACK ) == null )
            {
                try
                {
                    Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
                    TicketCategory ticketCategory = ticket.getTicketCategory( );

                    if ( _workflowService.isDisplayTasksForm( nIdAction, getLocale( ) ) )
                    {
                        strError = _workflowService.doSaveTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction, ticketCategory.getId( ), request,
                                getLocale( ) );

                        if ( strError != null )
                        {
                            return redirect( request, strError );
                        }

                        addInfoWorkflowAction( request, nIdAction );
                    }
                    else
                    {
                        _workflowService.doProcessAction( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction, ticketCategory.getId( ), request, getLocale( ),
                                false );

                        addInfoWorkflowAction( request, nIdAction );
                    }

                    // Immediate indexation of the Ticket
                    immediateTicketIndexing( ticket.getId( ) );
                }
                catch( Exception e )
                {
                    addErrorWorkflowAction( request, nIdAction );
                    AppLogService.error( e );

                    return redirectWorkflowActionCancelled( request );
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
        TicketCategory ticketCategory = ticket.getTicketCategory( );

        int nIdWorkflow = ticketCategory.getIdWorkflow( );

        if ( ( nIdWorkflow > 0 ) && _workflowService.isAvailable( ) )
        {
            try
            {
                _workflowService.getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );
                _workflowService.executeActionAutomatic( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );
            }
            catch( Exception e )
            {
                doRemoveWorkFlowResource( ticket.getId( ) );
                TicketHome.remove( ticket.getId( ) );
                throw e;
            }
        }
    }

    /**
     * Do process a workflow action over a ticket
     *
     * @param ticket
     *            The ticket
     * @param request
     *            http request
     */
    protected void doProcessNextWorkflowAction( Ticket ticket, HttpServletRequest request )
    {
        TicketCategory ticketCategory = ticket.getTicketCategory( );

        int nIdWorkflow = ticketCategory.getIdWorkflow( );

        if ( ( nIdWorkflow > 0 ) && _workflowService.isAvailable( ) )
        {
            try
            {
                _workflowService.getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );

                Collection<Action> actions = _workflowService.getActions( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, getUser( ) );

                if ( actions.size( ) == 1 )
                {
                    Action action = actions.iterator( ).next( );
                    _workflowService.doProcessAction( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, action.getId( ), ticketCategory.getId( ), request,
                            request.getLocale( ), false );
                }
                else
                {
                    // multiple actions or no action => ambiguous case
                    // TODO throw an exception
                }
            }
            catch( Exception e )
            {
                doRemoveWorkFlowResource( ticket.getId( ) );
                TicketHome.remove( ticket.getId( ) );
                throw e;
            }
        }
    }

    /**
     * Do remove a workflow resource
     * 
     * @param nTicketId
     *            the ticket id associated to the workflow resource
     */
    protected void doRemoveWorkFlowResource( int nTicketId )
    {
        if ( _workflowService.isAvailable( ) )
        {
            _resourceHistoryTicketingInformationService.removeByResource( nTicketId, Ticket.TICKET_RESOURCE_TYPE );

            _workflowService.doRemoveWorkFlowResource( nTicketId, Ticket.TICKET_RESOURCE_TYPE );
        }
    }

    /**
     * returns the actions history performed on the specified ticket
     * 
     * @param request
     *            the request
     * @param ticket
     *            the ticket
     * @return the actions history
     */
    protected String getDisplayDocumentHistory( HttpServletRequest request, Ticket ticket )
    {
        TicketCategory category = ticket.getTicketCategory( );
        int nWorkflowId = category.getIdWorkflow( );

        Map<String, Channel> mapHistoryChannel = _resourceHistoryTicketingInformationService.getChannelHistoryMap( ticket.getId( ),
                Ticket.TICKET_RESOURCE_TYPE, nWorkflowId );

        Map<String, Object> modelToAdd = new HashMap<String, Object>( );

        modelToAdd.put( MARK_RESOURCE_HISTORY_CHANNEL, mapHistoryChannel );
        modelToAdd.put( MARK_USER_FACTORY, UserFactory.getInstance( ) );

        return _workflowService.getDisplayDocumentHistory( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nWorkflowId, request, getLocale( ), modelToAdd,
                TEMPLATE_RESOURCE_HISTORY );
    }

    /**
     * Immediate indexation of a Ticket for the Backoffice
     * 
     * @param idTicket
     *            the id of the Ticket to index
     */
    protected void immediateTicketIndexing( int idTicket )
    {
        Ticket ticket = TicketHome.findByPrimaryKey( idTicket );
        if ( ticket != null )
        {
            try
            {
                TicketIndexer ticketIndexer = new TicketIndexer( );
                ticketIndexer.indexTicket( ticket );
            }
            catch( TicketIndexerException ticketIndexerException )
            {
                addError( TicketingConstants.ERROR_INDEX_TICKET_FAILED_BACK, getLocale( ) );

                // The indexation of the Ticket fail, we will store the Ticket in the table for the daemon
                IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
            }
        }
    }

    /**
     * Adds information message for workflow action
     * 
     * @param request
     *            the request
     * @param nIdAction
     *            the action id
     */
    private void addInfoWorkflowAction( HttpServletRequest request, int nIdAction )
    {
        IActionService actionService = SpringContextService.getBean( TicketingConstants.BEAN_ACTION_SERVICE );
        Action action = actionService.findByPrimaryKey( nIdAction );
        String strMessage = MessageFormat.format( I18nService.getLocalizedString( INFO_WORKFLOW_ACTION_EXECUTED, Locale.FRENCH ), action.getName( ) );
        RequestUtils.setParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO, strMessage );
    }

    /**
     * Adds error message for workflow action
     * 
     * @param request
     *            the request
     * @param nIdAction
     *            the action id
     */
    private void addErrorWorkflowAction( HttpServletRequest request, int nIdAction )
    {
        IActionService actionService = SpringContextService.getBean( TicketingConstants.BEAN_ACTION_SERVICE );
        Action action = actionService.findByPrimaryKey( nIdAction );
        String strError = MessageFormat.format( I18nService.getLocalizedString( ERROR_WORKFLOW_ACTION_ABORTED, Locale.FRENCH ), action.getName( ) );
        addError( strError );
    }

    /**
     * Return the list of all mass actions associated to the enabled workflow for the user filter by the selected task. If more than one task has been selected
     * the list returned will be empty
     * 
     * @param user
     * @param filter
     * @return the list of all mass actions associated to the enabled workflow for the user
     */
    protected List<Action> getListMassActions( AdminUser user, TicketFilter filter )
    {
        List<Action> listMassActions = new ArrayList<>( );
        // We get the list of mass actions only for one task
        if ( filter.getListIdWorkflowState( ).size( ) == 1 )
        {
            Integer nStateIdFilter = filter.getListIdWorkflowState( ).get( 0 );
            State stateSelected = _stateService.findByPrimaryKey( nStateIdFilter );

            if ( stateSelected != null && stateSelected.getWorkflow( ) != null )
            {
                List<Action> listWorkflowMassActions = _workflowService.getMassActions( stateSelected.getWorkflow( ).getId( ) );

                if ( listWorkflowMassActions != null && !listWorkflowMassActions.isEmpty( ) )
                {
                    Map<Integer, Action> mapLibelleAction = new TreeMap<>( );
                    // We sort the list of actions by order
                    for ( Action workflowActionMass : listWorkflowMassActions )
                    {
                        if ( workflowActionMass.getStateBefore( ) != null && nStateIdFilter == workflowActionMass.getStateBefore( ).getId( ) )
                        {
                            mapLibelleAction.put( workflowActionMass.getOrder( ), workflowActionMass );
                        }
                    }
                    listMassActions.addAll( mapLibelleAction.values( ) );
                }
            }
        }
        return listMassActions;
    }

    /**
     * Redirects to the correct page after workflow action
     * 
     * @param request
     *            the request
     * @return the page to redirect to
     */
    protected abstract String redirectAfterWorkflowAction( HttpServletRequest request );

    /**
     * Redirects to the correct page after the cancellation of the workflow action
     * 
     * @param request
     *            the request
     * @return the page to redirect to
     */
    protected abstract String redirectWorkflowActionCancelled( HttpServletRequest request );

    /**
     * Redirects to the default page (if workflow not enabled, if action id not correct, etc.)
     * 
     * @param request
     *            the request
     * @return the page to redirect to
     */
    protected abstract String defaultRedirectWorkflowAction( HttpServletRequest request );
}
