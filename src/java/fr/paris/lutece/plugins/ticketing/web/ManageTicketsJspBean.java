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
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.UserTitleHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;


/**
 * ManageTickets JSP Bean abstract class for JSP Bean
 */

/**
 * This class provides the user interface to manage Ticket features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTickets.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "TICKETING_TICKETS_MANAGEMENT" )
public class ManageTicketsJspBean extends MVCAdminJspBean
{
    // Right
    public static final String RIGHT_MANAGETICKETS = "TICKETING_TICKETS_MANAGEMENT";
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETS = "/admin/plugins/ticketing/manage_tickets.html";
    private static final String TEMPLATE_CREATE_TICKET = "/admin/plugins/ticketing/create_ticket.html";
    private static final String TEMPLATE_MODIFY_TICKET = "/admin/plugins/ticketing/modify_ticket.html";
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/ticketing/tasks_form_workflow.html";

    // Parameters
    private static final String PARAMETER_ID_TICKET = "id";
    private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_BACK = "back";
    private static final String PARAMETER_GUID = "guid";
    private static final String PARAMETER_FIRSTNAME = "fn";
    private static final String PARAMETER_LASTNAME = "ln";
    private static final String PARAMETER_PHONE = "ph";
    private static final String PARAMETER_EMAIL = "em";
    
    

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETS = "ticketing.manage_tickets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKET = "ticketing.modify_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKET = "ticketing.create_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "ticketing.taskFormWorkflow.pageTitle";

    // Markers
    private static final String MARK_TICKET_LIST = "ticket_list";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String JSP_MANAGE_TICKETS = "jsp/admin/plugins/ticketing/ManageTickets.jsp";
    private static final String MARK_GUID = "guid";
    private static final String MARK_FIRSTNAME = "firstname";
    private static final String MARK_LASTNAME = "lastname";
    private static final String MARK_PHONE = "phone";
    private static final String MARK_EMAIL = "email";
    
    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKET = "ticketing.message.confirmRemoveTicket";
    private static final String PROPERTY_DEFAULT_LIST_TICKET_PER_PAGE = "ticketing.listTickets.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticket.attribute.";

    // Views
    private static final String VIEW_MANAGE_TICKETS = "manageTickets";
    private static final String VIEW_CREATE_TICKET = "createTicket";
    private static final String VIEW_MODIFY_TICKET = "modifyTicket";
    private static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";
    private static final String ACTION_MODIFY_TICKET = "modifyTicket";
    private static final String ACTION_REMOVE_TICKET = "removeTicket";
    private static final String ACTION_CONFIRM_REMOVE_TICKET = "confirmRemoveTicket";
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";
    private static final String INFO_TICKET_UPDATED = "ticketing.info.ticket.updated";
    private static final String INFO_TICKET_REMOVED = "ticketing.info.ticket.removed";
    private static final long serialVersionUID = 1L;
    
    //services

  //@Inject
  //private IStateService _stateService;

   private final StateService _stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );

    // Errors
    public static final String ERROR_PHONE_NUMBER_MISSING = "ticketing.error.phonenumber.missing";


    // Session variable to store working values
    private Ticket _ticket;

    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;


    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETS, defaultView = true )
    public String getManageTickets( HttpServletRequest request )
    {
        _ticket = null;

        List<Ticket> listTickets = (List<Ticket>) TicketHome.getTicketsList(  );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETS );
        String strUrl = url.getUrl(  );

        // PAGINATOR
        LocalizedPaginator<Ticket> paginator = new LocalizedPaginator<Ticket>( listTickets, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale(  ) );
        
        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            for ( Ticket ticket : paginator.getPageItems(  ) )
            {
                
                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
                int nIdWorkflow = ticketCategory.getIdWorkflow(  );

                StateFilter stateFilter = new StateFilter(  );
                stateFilter.setIdWorkflow( nIdWorkflow );

                State state = _stateService.findByResource( ticket.getId( ),
                       Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow );

                if ( state != null )
                {
                    ticket.setState( state );
                }

                if ( nIdWorkflow > 0 )
                {
                    ticket.setListWorkflowActions(
                            WorkflowService.getInstance( ).getActions( ticket.getId( ),
                                    Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, getUser( ) ) );
                }
            }
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_TICKET_LIST, paginator.getPageItems(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETS, TEMPLATE_MANAGE_TICKETS, model );
    }

    /**
     * Returns the form to create a ticket
     *
     * @param request The Http request
     * @return the html code of the ticket form
     */
    @View( VIEW_CREATE_TICKET )
    public String getCreateTicket( HttpServletRequest request )
    {
    	String strGuid = request.getParameter( PARAMETER_GUID );
    	String strFirstname = request.getParameter( PARAMETER_FIRSTNAME );
    	String strLastname = request.getParameter( PARAMETER_LASTNAME );
    	String strPhone = request.getParameter( PARAMETER_PHONE );
    	String strEmail = request.getParameter(PARAMETER_EMAIL);
        _ticket = ( _ticket != null ) ? _ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList(  ) );
        // FIXME Dynamic filling
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put ( MARK_CONTACT_MODES_LIST,
                ContactModeHome.getReferenceList ( ) );
        model.put( MARK_TICKET, _ticket );
        model.put(MARK_GUID, strGuid);
        model.put(MARK_FIRSTNAME,strFirstname);
        model.put(MARK_LASTNAME, strLastname);
        model.put(MARK_PHONE, strPhone);
        model.put(MARK_EMAIL, strEmail);

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKET, TEMPLATE_CREATE_TICKET, model );
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKET )
    public String doCreateTicket( HttpServletRequest request )
    {
        populate( _ticket, request );

        // Check constraints
        if ( !validateBean( _ticket, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TICKET );
        }

        if ( _ticket.hasNoPhoneNumberFilled( ) )
        {
            addError( ERROR_PHONE_NUMBER_MISSING, getLocale( ) );
            return redirectView( request, VIEW_CREATE_TICKET );
        }

        TicketHome.create( _ticket );

        TicketCategory ticketCategory = TicketCategoryHome
                .findByPrimaryKey( _ticket.getIdTicketCategory( ) );
        int nIdWorkflow = ticketCategory.getIdWorkflow( );

        if ( nIdWorkflow > 0 )
        {
            WorkflowService.getInstance( ).getState( _ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE,
                    nIdWorkflow, ticketCategory.getId( ) );
            WorkflowService.getInstance( ).executeActionAutomatic( _ticket.getId( ),
                    Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );
        }

        addInfo( INFO_TICKET_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * Manages the removal form of a ticket whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKET )
    public String getConfirmRemoveTicket( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKET ) );
        url.addParameter( PARAMETER_ID_TICKET, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKET,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a ticket
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage tickets
     */
    @Action( ACTION_REMOVE_TICKET )
    public String doRemoveTicket( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );

        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            WorkflowService.getInstance( ).doRemoveWorkFlowResource( nId,
                    Ticket.TICKET_RESOURCE_TYPE );
        }

        TicketHome.remove( nId );
        addInfo( INFO_TICKET_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * Returns the form to update info about a ticket
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKET )
    public String getModifyTicket( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );

        if ( ( _ticket == null ) || ( _ticket.getId(  ) != nId ) )
        {
            _ticket = TicketHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList(  ) );
        // FIXME Dynamic filling
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put ( MARK_CONTACT_MODES_LIST,
                ContactModeHome.getReferenceList ( ) );
        model.put( MARK_TICKET, _ticket );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKET, TEMPLATE_MODIFY_TICKET, model );
    }

    /**
     * Process the change form of a ticket
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKET )
    public String doModifyTicket( HttpServletRequest request )
    {
        populate( _ticket, request );

        // Check constraints
        if ( !validateBean( _ticket, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TICKET, PARAMETER_ID_TICKET, _ticket.getId(  ) );
        }

        if ( _ticket.hasNoPhoneNumberFilled( ) )
        {
            addError( ERROR_PHONE_NUMBER_MISSING, getLocale( ) );
            return redirect( request, VIEW_MODIFY_TICKET, PARAMETER_ID_TICKET,
                    _ticket.getId( ) );
        }

        TicketHome.update( _ticket );
        addInfo( INFO_TICKET_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
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
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdTicket = request.getParameter( PARAMETER_ID_TICKET );

        if ( StringUtils.isNotEmpty( strIdAction )
                && StringUtils.isNumeric( strIdAction )
                && StringUtils.isNotEmpty( strIdTicket )
                && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( WorkflowService.getInstance( ).isDisplayTasksForm( nIdAction, getLocale( ) ) )
            {
                String strHtmlTasksForm = WorkflowService.getInstance( ).getDisplayTasksForm(
                        nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction, request,
                        getLocale( ) );

                Map<String, Object> model = new HashMap<String, Object>( );

                model.put( MARK_TASKS_FORM, strHtmlTasksForm );
                model.put( PARAMETER_ID_ACTION, nIdAction );
                model.put( PARAMETER_ID_TICKET, nIdTicket );

                return getPage( PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW,
                        TEMPLATE_TASKS_FORM_WORKFLOW, model );
            }

            return doProcessWorkflowAction( request );
        }

        return redirectView( request, VIEW_MANAGE_TICKETS );
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
        String strIdAction = request.getParameter( PARAMETER_ID_ACTION );
        String strIdTicket = request.getParameter( PARAMETER_ID_TICKET );

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction )
                && StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( request.getParameter( PARAMETER_BACK ) == null )
            {
                Ticket ticket = TicketHome.findByPrimaryKey( nIdTicket );
                TicketCategory ticketCategory = TicketCategoryHome
                        .findByPrimaryKey( ticket.getIdTicketCategory( ) );

                if ( WorkflowService.getInstance( ).isDisplayTasksForm( nIdAction, getLocale( ) ) )
                {
                    String strError = WorkflowService.getInstance( ).doSaveTasksForm( nIdTicket,
                            Ticket.TICKET_RESOURCE_TYPE, nIdAction, ticketCategory.getId( ),
                            request, getLocale( ) );

                    if ( strError != null )
                    {
                        return redirect( request, strError );
                    }
                } 
                else
                {
                    WorkflowService.getInstance( ).doProcessAction( nIdTicket,
                            Ticket.TICKET_RESOURCE_TYPE, nIdAction, ticketCategory.getId( ),
                            request, getLocale( ), false );
                }
            }
        }

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }
}
