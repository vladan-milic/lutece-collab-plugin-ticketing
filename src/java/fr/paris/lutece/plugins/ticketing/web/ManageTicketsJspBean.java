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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.ResponseRecap;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.TicketFormHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.OutputStream;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


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
    
    private static final long serialVersionUID = 1L;
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
    private static final String PARAMETER_GUID = "guid";
    private static final String PARAMETER_CUSTOMER_ID = "cid";
    private static final String PARAMETER_ID_RESPONSE = "idResponse";
    private static final String PARAMETER_FIRSTNAME = "fn";
    private static final String PARAMETER_LASTNAME = "ln";
    private static final String PARAMETER_PHONE = "ph";
    private static final String PARAMETER_EMAIL = "em";
    private static final String PARAMETER_CATEGORY = "cat";

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
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    private static final String MARK_TASKS_FORM = "tasks_form";
    private static final String MARK_ADMIN_AVATAR = "adminAvatar";
    private static final String JSP_MANAGE_TICKETS = "jsp/admin/plugins/ticketing/ManageTickets.jsp";
    private static final String MARK_GUID = "guid";
    private static final String MARK_RESPONSE_RECAP_LIST = "response_recap_list";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKET = "ticketing.message.confirmRemoveTicket";
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
    private static final String ERROR_FILTERING_INVALID_DATE = "ticketing.filter.error.date.invalid";

    // Session keys
    private static final String SESSION_ACTION_TYPE = "ticketing.session.actionType";
    private static boolean _bAdminAvatar = ( PluginService.getPlugin( "adminavatar" ) != null );

    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private final TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETS, defaultView = true )
    public String getManageTickets( HttpServletRequest request )
    {
        _ticketFormService.removeTicketFromSession( request.getSession(  ) );
        TicketAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

        TicketFilter filter = null;

        try
        {
            filter = TicketFilterHelper.getFilterFromRequest( request );
        }
        catch ( ParseException e )
        {
            addError( ERROR_FILTERING_INVALID_DATE, request.getLocale(  ) );

            return redirectView( request, VIEW_MANAGE_TICKETS );
        }

        List<Ticket> listTickets = (List<Ticket>) TicketHome.getTicketsList( filter );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETS );
        String strUrl = url.getUrl(  );

        // PAGINATOR
        LocalizedPaginator<Ticket> paginator = new LocalizedPaginator<Ticket>( listTickets, _nItemsPerPage, strUrl,
                PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            for ( Ticket ticket : paginator.getPageItems(  ) )
            {
                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
                int nIdWorkflow = ticketCategory.getIdWorkflow(  );

                StateFilter stateFilter = new StateFilter(  );
                stateFilter.setIdWorkflow( nIdWorkflow );

                State state = WorkflowService.getInstance(  )
                                             .getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                        ticketCategory.getId(  ) );

                if ( state != null )
                {
                    ticket.setState( state );
                }

                if ( nIdWorkflow > 0 )
                {
                    ticket.setListWorkflowActions( WorkflowService.getInstance(  )
                                                                  .getActions( ticket.getId(  ),
                            Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, getUser(  ) ) );
                }
            }
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_TICKET_LIST, paginator.getPageItems(  ) );
        model.put( MARK_ADMIN_AVATAR, _bAdminAvatar );

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
        clearUploadFilesIfNeeded( request.getSession(  ) );
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        ticket = ( ticket != null ) ? ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        initTicketForm( request, ticket, model );

        saveActionTypeInSession( request.getSession(  ), ACTION_CREATE_TICKET );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKET, TEMPLATE_CREATE_TICKET, model );
    }

    /**
     * ticket initialisation method
     * @param request request  
     * @param ticket ticket
     * @param model model
     */
    private void initTicketForm( HttpServletRequest request, Ticket ticket, Map<String, Object> model )
    {
        String strGuid = request.getParameter( PARAMETER_GUID );
        String strCustomerId = request.getParameter( PARAMETER_CUSTOMER_ID );
        String strFirstname = request.getParameter( PARAMETER_FIRSTNAME );
        String strLastname = request.getParameter( PARAMETER_LASTNAME );
        String strPhone = request.getParameter( PARAMETER_PHONE );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        String strCategory = request.getParameter( PARAMETER_CATEGORY );

        if ( !StringUtils.isEmpty( strFirstname ) && StringUtils.isEmpty( ticket.getFirstname(  ) ) )
        {
            ticket.setFirstname( strFirstname );
        }

        if ( !StringUtils.isEmpty( strLastname ) && StringUtils.isEmpty( ticket.getLastname(  ) ) )
        {
            ticket.setLastname( strLastname );
        }

        if ( !StringUtils.isEmpty( strPhone ) && StringUtils.isEmpty( ticket.getMobilePhoneNumber(  ) ) )
        {
            ticket.setMobilePhoneNumber( strPhone );
        }

        if ( !StringUtils.isEmpty( strEmail ) && StringUtils.isEmpty( ticket.getEmail(  ) ) )
        {
            ticket.setEmail( strEmail );
        }

        if ( !StringUtils.isEmpty( strCategory ) && ( ticket.getIdTicketCategory(  ) == 0 ) )
        {
            TicketCategory category = TicketCategoryHome.findByCode( strCategory );

            if ( category != null )
            {
                ticket.setIdTicketCategory( category.getId(  ) );
                ticket.setIdTicketDomain( category.getIdTicketDomain(  ) );
                ticket.setIdTicketType( category.getIdTicketType(  ) );
            }
        }

        if ( !StringUtils.isEmpty( strGuid ) && StringUtils.isEmpty( ticket.getGuid(  ) ) )
        {
            ticket.setGuid( strGuid );
        }

        if ( !StringUtils.isEmpty( strCustomerId ) && StringUtils.isEmpty( ticket.getCustomerId(  ) ) )
        {
            ticket.setCustomerId( strCustomerId );
        }

        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList(  ) );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList(  ) );
        model.put( MARK_TICKET, ticket );
        model.put( MARK_GUID, strGuid );
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
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        TicketHome.create( ticket );

        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
        int nIdWorkflow = ticketCategory.getIdWorkflow(  );

        if ( ( ticket.getListResponse(  ) != null ) && !ticket.getListResponse(  ).isEmpty(  ) )
        {
            for ( Response response : ticket.getListResponse(  ) )
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( ticket.getId(  ), response.getIdResponse(  ) );
            }
        }

        if ( ( nIdWorkflow > 0 ) && WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            try
            {
                WorkflowService.getInstance(  )
                               .getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticketCategory.getId(  ) );
                WorkflowService.getInstance(  )
                               .executeActionAutomatic( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticketCategory.getId(  ) );
            }
            catch ( Exception e )
            {
                WorkflowService.getInstance(  ).doRemoveWorkFlowResource( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE );
                TicketHome.remove( ticket.getId(  ) );
                throw e;
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

        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            WorkflowService.getInstance(  ).doRemoveWorkFlowResource( nId, Ticket.TICKET_RESOURCE_TYPE );
        }

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
    @View( VIEW_TICKET_FORM )
    public String getTicketForm( HttpServletRequest request )
    {
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        if ( !StringUtils.isEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
        {
            int nIdCategory = Integer.parseInt( strIdCategory );
            TicketForm form = TicketFormHome.findByCategoryId( nIdCategory );

            if ( form != null )
            {
                return _ticketFormService.getHtmlForm( ticket, form, getLocale(  ), false, request );
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
        clearUploadFilesIfNeeded( request.getSession(  ) );
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );

        if ( ( ticket == null ) || ( ticket.getId(  ) != nId ) )
        {
            ticket = TicketHome.findByPrimaryKey( nId );

            for ( Response response : ticket.getListResponse(  ) )
            {
                if ( response.getFile(  ) != null )
                {
                    String strIdEntry = Integer.toString( response.getEntry(  ).getIdEntry(  ) );

                    FileItem fileItem = new GenAttFileItem( response.getFile(  ).getPhysicalFile(  ).getValue(  ),
                            response.getFile(  ).getTitle(  ), IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry,
                            response.getIdResponse(  ) );
                    TicketAsynchronousUploadHandler.getHandler(  )
                                                   .addFileItemToUploadedFilesList( fileItem,
                        IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, request );
                }
            }
        }

        Map<String, Object> model = getModel(  );
        initTicketForm( request, ticket, model );

        saveActionTypeInSession( request.getSession(  ), ACTION_MODIFY_TICKET );

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
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        TicketHome.update( ticket );

        // remove and add generic attributes responses
        TicketHome.removeTicketResponse( ticket.getId(  ) );

        if ( ( ticket.getListResponse(  ) != null ) && !ticket.getListResponse(  ).isEmpty(  ) )
        {
            for ( Response response : ticket.getListResponse(  ) )
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( ticket.getId(  ), response.getIdResponse(  ) );
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

                model.put( MARK_TASKS_FORM, strHtmlTasksForm );
                model.put( PARAMETER_ID_ACTION, nIdAction );
                model.put( PARAMETER_ID_TICKET, nIdTicket );

                return getPage( PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW, TEMPLATE_TASKS_FORM_WORKFLOW, model );
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

        if ( StringUtils.isNotEmpty( strIdAction ) && StringUtils.isNumeric( strIdAction ) &&
                StringUtils.isNotEmpty( strIdTicket ) && StringUtils.isNumeric( strIdTicket ) )
        {
            int nIdAction = Integer.parseInt( strIdAction );
            int nIdTicket = Integer.parseInt( strIdTicket );

            if ( request.getParameter( PARAMETER_BACK ) == null )
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
        boolean bIsFormValid = true ;
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        ticket = ( ticket != null ) ? ticket : new Ticket(  );
        populate( ticket, request );

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );

        if ( ticket.getIdTicketCategory(  ) > 0 )
        {
            EntryFilter filter = new EntryFilter(  );
            TicketForm form = TicketFormHome.findByCategoryId( ticket.getIdTicketCategory(  ) );

            if ( form != null )
            {
                filter.setIdResource( form.getIdForm(  ) );
                filter.setResourceType( TicketForm.RESOURCE_TYPE );
                filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
                filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                filter.setIdIsComment( EntryFilter.FILTER_FALSE );
                ticket.setListResponse( null );

                List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

                for ( Entry entry : listEntryFirstLevel )
                {
                    listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry(  ),
                            getLocale(  ), ticket ) );
                }
            }
        }

        // Check constraints
        bIsFormValid = validateBean( ticket, VALIDATION_ATTRIBUTES_PREFIX );
        if ( ticket.hasNoPhoneNumberFilled(  ) )
        {
            bIsFormValid = false;
            addError( ERROR_PHONE_NUMBER_MISSING, getLocale(  ) );
        }

        if ( listFormErrors.size(  ) > 0 )
        {
            bIsFormValid = false ;
        }

        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
        TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory.getIdTicketDomain(  ) );
        ticket.setTicketCategory( ticketCategory.getLabel(  ) );
        ticket.setTicketDomain( ticketDomain.getLabel(  ) );

        ticket.setTicketType( TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType(  ) ).getLabel(  ) );
        ticket.setContactMode( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode(  ) ).getLabel(  ) );
        ticket.setUserTitle( UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle(  ) ).getLabel(  ) );
        ticket.setConfirmationMsg( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode(  ) ).getConfirmationMsg(  ) );

        _ticketFormService.saveTicketInSession( request.getSession(  ), ticket );
        
        if( !bIsFormValid ) 
        {
            return redirectAfterValidationKO( request, ticket );
        } 
        else 
        {
            return redirectView( request, VIEW_RECAP_TICKET );
        }
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
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        List<ResponseRecap> listResponseRecap = _ticketFormService.getListResponseRecap( ticket
                .getListResponse( ) );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKET_ACTION, getActionTypeFromSession( request.getSession(  ) ) );
        model.put( MARK_TICKET, ticket );
        model.put( MARK_RESPONSE_RECAP_LIST, listResponseRecap );

        removeActionTypeFromSession( request.getSession(  ) );

        return getPage( PROPERTY_PAGE_TITLE_RECAP_TICKET, TEMPLATE_RECAP_TICKET, model );
    }

    /**
     * redirection after validation form KO
     *
     * @param request
     *            HTTP request to validate
     * @param ticket ticket
     * @return view at the initiative of the validation (create or modify)
     */
    private String redirectAfterValidationKO( HttpServletRequest request, Ticket ticket )
    {
        _ticketFormService.saveTicketInSession( request.getSession(  ), ticket );
        if ( getActionTypeFromSession( request.getSession(  ) ).equals( ACTION_MODIFY_TICKET ) )
        {
            return redirect( request, VIEW_MODIFY_TICKET, PARAMETER_ID_TICKET, ticket.getId(  ) );
        }
        else// ACTION_CREATE_TICKET
        {
            return redirectView( request, VIEW_CREATE_TICKET );
        }
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

    /**
     * Do download a file from an appointment response
     *
     * @param request
     *            The request
     * @param httpResponse
     *            The response
     * @return nothing.
     * @throws AccessDeniedException
     *             If the user is not authorized to access this feature
     */
    public String getDownloadFile( HttpServletRequest request, HttpServletResponse httpResponse )
        throws AccessDeniedException
    {
        String strIdResponse = request.getParameter( PARAMETER_ID_RESPONSE );

        if ( StringUtils.isEmpty( strIdResponse ) || !StringUtils.isNumeric( strIdResponse ) )
        {
            return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
        }

        int nIdResponse = Integer.parseInt( strIdResponse );

        Response response = ResponseHome.findByPrimaryKey( nIdResponse );
        File file = FileHome.findByPrimaryKey( response.getFile(  ).getIdFile(  ) );
        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile(  ).getIdPhysicalFile(  ) );

        httpResponse.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getTitle(  ) + "\";" );
        httpResponse.setHeader( "Content-type", file.getMimeType(  ) );
        httpResponse.addHeader( "Content-Encoding", "UTF-8" );
        httpResponse.addHeader( "Pragma", "public" );
        httpResponse.addHeader( "Expires", "0" );
        httpResponse.addHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );

        try
        {
            OutputStream os = httpResponse.getOutputStream(  );
            os.write( physicalFile.getValue(  ) );
            // We do not close the output stream in finaly clause because it is
            // the response stream,
            // and an error message needs to be displayed if an exception occurs
            os.close(  );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getStackTrace(  ), e );
        }

        return StringUtils.EMPTY;
    }

    /**
     * Clear uploaded files if needed.
     *
     * @param session
     *            The session of the current user
     */
    private void clearUploadFilesIfNeeded( HttpSession session )
    {
        // If we do not reload an appointment, we clear uploaded files.
        if ( ( _ticketFormService.getTicketFromSession( session ) == null ) &&
                ( _ticketFormService.getValidatedTicketFromSession( session ) == null ) )
        {
            TicketAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( session.getId(  ) );
        }
    }

}