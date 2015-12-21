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
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.TicketFormHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
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

    private static final long serialVersionUID = 1L;

    // Right
    public static final String RIGHT_MANAGETICKETS = "TICKETING_TICKETS_MANAGEMENT";
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_TICKET_ACTION = "ticket_action";

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETS = "/admin/plugins/ticketing/manage_tickets.html";
    private static final String TEMPLATE_CREATE_TICKET = "/admin/plugins/ticketing/create_ticket.html";
    private static final String TEMPLATE_MODIFY_TICKET = "/admin/plugins/ticketing/modify_ticket.html";
    private static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/ticketing/tasks_form_workflow.html";
    private static final String TEMPLATE_RECAP_TICKET = "/admin/plugins/ticketing/recap_ticket.html";

    // Parameters
    private static final String PARAMETER_ID_TICKET = "id";
    private static final String PARAMETER_ID_CATEGORY = "id_ticket_category";
        private static final String PARAMETER_ID_ACTION = "id_action";
    private static final String PARAMETER_BACK = "back";
    // private static final String PARAMETER_GUID = "guid";
    // private static final String PARAMETER_FIRSTNAME = "fn";
    // private static final String PARAMETER_LASTNAME = "ln";
    // private static final String PARAMETER_PHONE = "ph";
    // private static final String PARAMETER_EMAIL = "em";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETS = "ticketing.manage_tickets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKET = "ticketing.modify_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKET = "ticketing.create_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "ticketing.taskFormWorkflow.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_TICKET = "ticketing.recap_ticket.pageTitle";

    // Markers
    private static final String MARK_TICKET_LIST = "ticket_list";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_FORM_GENERIC_ATTRIBUTES_MAP_HTML = "generic_attributes_form_map_html";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String JSP_MANAGE_TICKETS = "jsp/admin/plugins/ticketing/ManageTickets.jsp";
    // private static final String MARK_GUID = "guid";
    // private static final String MARK_FIRSTNAME = "firstname";
    // private static final String MARK_LASTNAME = "lastname";
    // private static final String MARK_PHONE = "phone";
    // private static final String MARK_EMAIL = "email";
    
    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKET = "ticketing.message.confirmRemoveTicket";
    private static final String PROPERTY_DEFAULT_LIST_TICKET_PER_PAGE = "ticketing.listTickets.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticket.attribute.";

    // Views
    private static final String VIEW_MANAGE_TICKETS = "manageTickets";
    private static final String VIEW_CREATE_TICKET = "createTicket";
    private static final String VIEW_MODIFY_TICKET = "modifyTicket";
    private static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";
    private static final String VIEW_TICKET_FORM = "ticketForm";
    private static final String VIEW_RECAP_TICKET = "recapTicket";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";
    private static final String ACTION_MODIFY_TICKET = "modifyTicket";
    private static final String ACTION_REMOVE_TICKET = "removeTicket";
    private static final String ACTION_CONFIRM_REMOVE_TICKET = "confirmRemoveTicket";
    private static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";
    private static final String ACTION_RECAP_TICKET = "recapTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";
    private static final String INFO_TICKET_UPDATED = "ticketing.info.ticket.updated";
    private static final String INFO_TICKET_REMOVED = "ticketing.info.ticket.removed";
    
    // Errors
    private static final String ERROR_PHONE_NUMBER_MISSING = "ticketing.error.phonenumber.missing";

    // Session keys
    private static final String SESSION_NOT_VALIDATED_TICKET = "ticketing.session.notValidatedTicket";
    private static final String SESSION_ACTION_TYPE = "ticketing.session.actionType";


    // Session variable to store working values
    private Ticket _ticket;

    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    private final TicketFormService  _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETS, defaultView = true )
    public String getManageTickets( HttpServletRequest request )
    {
        _ticket = null;
        removeTicketFromSession( request.getSession( ) );

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
        
        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            for ( Ticket ticket : paginator.getPageItems( ) )
            {

                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory( ) );
                int nIdWorkflow = ticketCategory.getIdWorkflow( );

                StateFilter stateFilter = new StateFilter( );
                stateFilter.setIdWorkflow( nIdWorkflow );

                State state = WorkflowService.getInstance( ).getState( ticket.getId( ),
                        Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );

                if ( state != null )
                {
                    ticket.setState( state );
                }

                if ( nIdWorkflow > 0 )
                {
                    ticket.setListWorkflowActions( WorkflowService.getInstance( ).getActions( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, getUser( ) ) );
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
        _ticket = getTicketFromSession( request.getSession( ) );
        _ticket = ( _ticket != null ) ? _ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        initTicketForm( request, model );

        saveActionTypeInSession( request.getSession( ), ACTION_CREATE_TICKET );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKET, TEMPLATE_CREATE_TICKET, model );
    }

    private void initTicketForm(HttpServletRequest request, Map<String, Object> model)
    {
        // String strGuid = request.getParameter( PARAMETER_GUID );
        // String strFirstname = request.getParameter( PARAMETER_FIRSTNAME );
        // String strLastname = request.getParameter( PARAMETER_LASTNAME );
        // String strPhone = request.getParameter( PARAMETER_PHONE );
        // String strEmail = request.getParameter( PARAMETER_EMAIL );

        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList(  ) );
        // FIXME Dynamic filling
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put ( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList ( ) );
        model.put( MARK_TICKET, _ticket );
        // model.put(MARK_GUID, strGuid);
        // model.put(MARK_FIRSTNAME,strFirstname);
        // model.put(MARK_LASTNAME, strLastname);
        // model.put(MARK_PHONE, strPhone);
        // model.put(MARK_EMAIL, strEmail);
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
        _ticket = getTicketFromSession( request.getSession( ) );
        TicketHome.create( _ticket );

        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( _ticket
                .getIdTicketCategory( ) );
        int nIdWorkflow = ticketCategory.getIdWorkflow( );

        if ( nIdWorkflow > 0 )
        {
            WorkflowService.getInstance( ).getState( _ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE,
                    nIdWorkflow, ticketCategory.getId( ) );
            WorkflowService.getInstance( ).executeActionAutomatic( _ticket.getId( ),
                    Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, ticketCategory.getId( ) );
        }
        
		if ( _ticket.getListResponse( ) != null && !_ticket.getListResponse( ).isEmpty( ) )
        {
            for ( Response response : _ticket.getListResponse( ) )
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( _ticket.getId( ), response.getIdResponse( ) );
            }
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
        TicketHome.removeTicketResponse( nId );
        TicketHome.remove( nId );
        addInfo( INFO_TICKET_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * returns form linked to the selected category
     * 
     * @param request
     *            http request with id_ticket_category
     * @return ticket form
     */

    @View(VIEW_TICKET_FORM)
    public String getTicketForm(HttpServletRequest request)
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        if ( !StringUtils.isEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
        {
            int nIdCategory = Integer.parseInt( strIdCategory );
            TicketForm form = TicketFormHome.findByCategoryId( nIdCategory );
            if ( form != null )
            {
                return _ticketFormService.getHtmlForm( form, getLocale( ), false, request );
            }
        }
        return StringUtils.EMPTY;
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
        _ticket = getTicketFromSession( request.getSession( ) );
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );

        if ( ( _ticket == null ) || ( _ticket.getId(  ) != nId ) )
        {
            _ticket = TicketHome.findByPrimaryKey( nId );

            List<Integer> listIdResponse = TicketHome.findListIdResponse( _ticket.getId( ) );
            List<Response> listResponses = new ArrayList<Response>( listIdResponse.size( ) );

            for ( int nIdResponse : listIdResponse )
            {
                Response response = ResponseHome.findByPrimaryKey( nIdResponse );

                if ( response.getField( ) != null )
                {
                    response.setField( FieldHome.findByPrimaryKey( response.getField( ).getIdField( ) ) );
                }

                if ( response.getFile( ) != null )
                {
                    fr.paris.lutece.portal.business.file.File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );
                    PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );
                    file.setPhysicalFile( physicalFile );
                    response.setFile( file );

                    String strIdEntry = Integer.toString( response.getEntry( ).getIdEntry( ) );

                    FileItem fileItem = new GenAttFileItem( physicalFile.getValue( ), file.getTitle( ), IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, response.getIdResponse( ) );
                    TicketAsynchronousUploadHandler.getHandler( ).addFileItemToUploadedFilesList( fileItem, IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, request );
                }

                listResponses.add( response );
            }

            _ticket.setListResponse( listResponses );
        }

        Map<String, Object> model = getModel(  );
        initTicketForm( request, model );

        saveActionTypeInSession( request.getSession( ), ACTION_MODIFY_TICKET );

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
        _ticket = getTicketFromSession( request.getSession( ) );

        TicketHome.update( _ticket );
        // remove and add generic attributes responses
        TicketHome.removeTicketResponse( _ticket.getId( ) );
        if ( _ticket.getListResponse( ) != null && !_ticket.getListResponse( ).isEmpty( ) )
        {
            for ( Response response : _ticket.getListResponse( ) )
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( _ticket.getId( ), response.getIdResponse( ) );
            }
        }
        
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

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction )
                && StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( WorkflowService.getInstance( ).isDisplayTasksForm( nIdAction, getLocale( ) ) )
            {
                String strHtmlTasksForm = WorkflowService.getInstance( ).getDisplayTasksForm(
                        nIdTicket, Ticket.TICKET_RESOURCE_TYPE, nIdAction, request, getLocale( ) );

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
                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket
                        .getIdTicketCategory( ) );

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

    /**
     * Process the data capture form of a new ticket
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_RECAP_TICKET )
    public String doRecapTicket( HttpServletRequest request )
    {
        populate( _ticket, request );

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        if ( _ticket.getIdTicketCategory( ) >= 0 )
        {
            EntryFilter filter = new EntryFilter( );
            TicketForm form = TicketFormHome.findByCategoryId( _ticket.getIdTicketCategory( ) );
            if(form != null ){
                filter.setIdResource( form.getIdForm( ) );
                filter.setResourceType( TicketForm.RESOURCE_TYPE );
                filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
                filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                filter.setIdIsComment( EntryFilter.FILTER_FALSE );
				_ticket.setListResponse( null );
        
                List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );
        
                for ( Entry entry : listEntryFirstLevel )
                {
                    listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), getLocale( ), _ticket ) );
                }
            }
        }

        // Check constraints
        if ( !validateBean( _ticket, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            redirectAfterValidationKO(request);
        }

        if ( _ticket.hasNoPhoneNumberFilled( ) )
        {
            addError( ERROR_PHONE_NUMBER_MISSING, getLocale( ) );
            redirectAfterValidationKO(request);
        }

        if ( listFormErrors.size( ) > 0 )
        {
            for ( GenericAttributeError error : listFormErrors )
            {
                if ( error.getIsDisplayableError( ) )
                    addError( error.getMessage( ) );
            }
            redirectAfterValidationKO(request);
        }

        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( _ticket
                .getIdTicketCategory( ) );
        TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory
                .getIdTicketDomain( ) );
        _ticket.setTicketCategory( ticketCategory.getLabel( ) );
        _ticket.setTicketDomain( ticketDomain.getLabel( ) );

        _ticket.setTicketType( TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType( ) )
                .getLabel( ) );
        _ticket.setContactMode( ContactModeHome.findByPrimaryKey( _ticket.getIdContactMode( ) )
                .getLabel( ) );
        _ticket.setUserTitle( UserTitleHome.findByPrimaryKey( _ticket.getIdUserTitle( ) )
                .getLabel( ) );
        _ticket.setConfirmationMsg( ContactModeHome.findByPrimaryKey( _ticket.getIdContactMode( ) )
                .getConfirmationMsg( ) );

        saveTicketInSession( request.getSession( ), _ticket );

        return redirectView( request, VIEW_RECAP_TICKET );
    }

    /**
     * Returns the form to recapitulate a ticket to create or modify
     *
     * @param request
     *            The Http request
     * @return the html code of the ticket form
     */
    @View( VIEW_RECAP_TICKET )
    public String getRecapTicket( HttpServletRequest request )
    {
        _ticket = getTicketFromSession( request.getSession( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_TICKET_ACTION, getActionTypeFromSession( request.getSession( ) ) );
        model.put( MARK_TICKET, _ticket );

        removeActionTypeFromSession( request.getSession( ) );

        return getPage( PROPERTY_PAGE_TITLE_RECAP_TICKET, TEMPLATE_RECAP_TICKET, model );
    }

    /**
     * redirection after validation form KO
     * 
     * @param request
     *            HTTP request to validate
     * @return view at the initiative of the validation (create or modify)
     */
    private String redirectAfterValidationKO( HttpServletRequest request )
    {
		if ( getActionTypeFromSession( request.getSession( ) ).equals( ACTION_MODIFY_TICKET ) )
		{
			return redirect( request, VIEW_MODIFY_TICKET, PARAMETER_ID_TICKET, _ticket.getId( ) );
		}
		else
		// ACTION_CREATE_TICKET
		{
			return redirectView( request, VIEW_CREATE_TICKET );
		}
    }

    /**
     * Save an ticket form in the session of the user
     * 
     * @param session
     *            The session
     * @param ticket
     *            The ticket form to save
     */
    public void saveTicketInSession( HttpSession session, Ticket ticket )
    {
        session.setAttribute( SESSION_NOT_VALIDATED_TICKET, ticket );
    }

    /**
     * Get the current ticket form from the session
     * 
     * @param session
     *            The session of the user
     * @return The ticket form
     */
    public Ticket getTicketFromSession( HttpSession session )
    {
        return (Ticket) session.getAttribute( SESSION_NOT_VALIDATED_TICKET );
    }

    /**
     * Remove any ticket form stored in the session of the user
     * 
     * @param session
     *            The session
     */
    public void removeTicketFromSession( HttpSession session )
    {
        session.removeAttribute( SESSION_NOT_VALIDATED_TICKET );
    }

    /**
     * Save the current action type in the session of the user
     * 
     * @param session
     *            The session
     * @param actionType
     *            The action type to save
     */
    public void saveActionTypeInSession( HttpSession session, String actionType )
    {
        session.setAttribute( SESSION_ACTION_TYPE, actionType );
    }

    /**
     * Get the current actionType from the session
     * 
     * @param session
     *            The session of the user
     * @return The actionType
     */
    public String getActionTypeFromSession( HttpSession session )
    {
        return (String) session.getAttribute( SESSION_ACTION_TYPE );
    }

    /**
     * Remove any action type stored in the session of the user
     * 
     * @param session
     *            The session
     */
    public void removeActionTypeFromSession( HttpSession session )
    {
        session.removeAttribute( SESSION_ACTION_TYPE );
    }

}
