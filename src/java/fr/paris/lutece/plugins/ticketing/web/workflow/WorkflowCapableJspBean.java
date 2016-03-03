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

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketingPocGruService;
import fr.paris.lutece.plugins.ticketing.web.TicketHelper;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class represents a JSP bean which can use workflow
 *
 */
public abstract class WorkflowCapableJspBean extends MVCAdminJspBean
{
    /** redirection map */
    protected static Map<String, String> _mapRedirectUrl;

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "ticketing.taskFormWorkflow.pageTitle";
    private static final String PROPERTY_WORKFLOW_ACTION_ID_ASSIGN_ME = "ticketing.workflow.action.id.assignMe";
    private static final String TEMPLATE_RESOURCE_HISTORY = "admin/plugins/ticketing/workflow/ticket_history.html";

    static
    {
        _mapRedirectUrl = new HashMap<String, String>(  );

        Enumeration<?> enumPropNames = AppPropertiesService.getProperties(  ).propertyNames(  );

        while ( enumPropNames.hasMoreElements(  ) )
        {
            String key = (String) enumPropNames.nextElement(  );

            if ( key.startsWith( TicketingConstants.PROPERTY_REDIRECT_PREFIX ) )
            {
                _mapRedirectUrl.put( ( key ).substring( TicketingConstants.PROPERTY_REDIRECT_PREFIX.length(  ) ),
                    AppPropertiesService.getProperty( key ) );
            }
        }
    }

    // Services
    private static WorkflowService _workflowService = WorkflowService.getInstance(  );

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -249042695023346133L;

    /**
     * set workflow attributes for displayable tickets
     * @param paginator paginator of tickets
     */
    protected void setWorkflowAttributes( LocalizedPaginator<Ticket> paginator )
    {
        for ( Ticket ticket : paginator.getPageItems(  ) )
        {
            setWorkflowAttributes( ticket );
        }
    }

    /**
     * set workflow attributes for the specified ticket
     * @param ticket the Ticket
     */
    protected void setWorkflowAttributes( Ticket ticket )
    {
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
                Collection<Action> fullListWorkflowActions = getActions( ticket.getId(  ), nIdWorkflow );
                Collection<Action> filteredListWorkflowActions = new ArrayList<Action>( fullListWorkflowActions );

                if ( TicketHelper.isTicketAssignToUserOrGroup( getUser(  ), ticket ) ||
                        RBACService.isUserInRole( getUser(  ), TicketingConstants.ROLE_GRU_ADMIN ) )
                {
                    //if ticket is assign to agent or to its group 
                    for ( Action action : fullListWorkflowActions )
                    {
                        if ( ( action.getId(  ) == AppPropertiesService.getPropertyInt( 
                                    PROPERTY_WORKFLOW_ACTION_ID_ASSIGN_ME, -1 ) ) &&
                                ( ticket.getAssigneeUser(  ) != null ) &&
                                ( getUser(  ).getUserId(  ) == ticket.getAssigneeUser(  ).getAdminUserId(  ) ) )
                        {
                            //self assign action for a ticket already assign to the agent => removing action from list
                            filteredListWorkflowActions.remove( action );
                        }
                    }
                }
                else
                {
                    //ticket is not assign to agent or its group => no Workflow actions shown
                    filteredListWorkflowActions = new ArrayList<Action>(  );
                }

                ticket.setListWorkflowActions( filteredListWorkflowActions );
            }
        }
    }

    /**
     * Gives a list of actions possible for a given ticket based on its status in the workflow and the user role
     * @param nTicketId the ticket id
     * @param nIdWorkflow the workflow id
     * @return the list of possible actions
     */
    private Collection<Action> getActions( int nTicketId, int nIdWorkflow )
    {
        Collection<Action> listTicketingActions;

        if ( nIdWorkflow > 0 )
        {
            listTicketingActions = new ArrayList<Action>(  );

            Collection<Action> listWorkflowActions = _workflowService.getActions( nTicketId,
                    Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, getUser(  ) );

            for ( Action workflowAction : listWorkflowActions )
            {
                Action ticketingAction = new TicketingAction( workflowAction,
                        _workflowService.isDisplayTasksForm( workflowAction.getId(  ), getLocale(  ) ) );
                listTicketingActions.add( ticketingAction );
            }
        }
        else
        {
            listTicketingActions = Collections.emptyList(  );
        }

        return listTicketingActions;
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
    public String getWorkflowActionForm( HttpServletRequest request )
    {
        String strIdAction = request.getParameter( TicketingConstants.PARAMETER_WORKFLOW_ID_ACTION );
        String strIdTicket = request.getParameter( TicketingConstants.PARAMETER_ID_TICKET );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( _workflowService.isDisplayTasksForm( nIdAction, getLocale(  ) ) )
            {
                String strHtmlTasksForm = _workflowService.getDisplayTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE,
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

                return getPage( PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW,
                    TicketingConstants.TEMPLATE_TASKS_FORM_WORKFLOW, model );
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

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( request.getParameter( TicketingConstants.PARAMETER_BACK ) == null )
            {
                Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );

                if ( _workflowService.isDisplayTasksForm( nIdAction, getLocale(  ) ) )
                {
                    strError = _workflowService.doSaveTasksForm( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction,
                            ticketCategory.getId(  ), request, getLocale(  ) );

                    if ( strError != null )
                    {
                        return redirect( request, strError );
                    }
                }
                else
                {
                    _workflowService.doProcessAction( nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction,
                        ticketCategory.getId(  ), request, getLocale(  ), false );
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
     * returns the actions history performed on the specified ticket
     * @param request the request
     * @param ticket the ticket
     * @return the actions history
     */
    protected String getDisplayDocumentHistory( HttpServletRequest request, Ticket ticket )
    {
        TicketCategory category = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
        int nWorkflowId = category.getIdWorkflow(  );

        return _workflowService.getDisplayDocumentHistory( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nWorkflowId,
            request, getLocale(  ), TEMPLATE_RESOURCE_HISTORY );
    }

    /**
     * Redirects to the correct page after workflow action
     * @param request the request
     * @return the page to redirect to
     */
    protected abstract String redirectAfterWorkflowAction( HttpServletRequest request );

    /**
     * Redirects to the correct page after the cancellation of the workflow action
     * @param request the request
     * @return the page to redirect to
     */
    protected abstract String redirectWorkflowActionCancelled( HttpServletRequest request );

    /**
     * Redirects to the default page (if workflow not enabled, if action id not correct, etc.)
     * @param request the request
     * @return the page to redirect to
     */
    protected abstract String defaultRedirectWorkflowAction( HttpServletRequest request );
}
