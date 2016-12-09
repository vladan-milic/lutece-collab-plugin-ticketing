/*
 * Copyright (c) 2002-2016, Mairie de Paris
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
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticketform.ResponseRecap;
import fr.paris.lutece.plugins.ticketing.business.ticketform.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.ticketform.TicketFormHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.web.ticketfilter.TicketFilterHelper;
import fr.paris.lutece.plugins.ticketing.web.user.UserFactory;
import fr.paris.lutece.plugins.ticketing.web.util.FormValidator;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidatorFactory;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.prefs.AdminUserPreferencesService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * ManageTickets JSP Bean abstract class for JSP Bean
 */

/**
 * This class provides the user interface to manage Ticket features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTickets.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class ManageTicketsJspBean extends WorkflowCapableJspBean
{
    // Right
    public static final String RIGHT_MANAGETICKETS = "TICKETING_TICKETS_MANAGEMENT";
    private static final long serialVersionUID = 1L;

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETS = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH +
        "manage_tickets.html";
    private static final String TEMPLATE_CREATE_TICKET = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH +
        "create_ticket.html";
    private static final String TEMPLATE_MODIFY_TICKET = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH +
        "modify_ticket.html";
    private static final String TEMPLATE_RECAP_TICKET = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH +
        "recap_ticket.html";

    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_ticket_category";
    private static final String PARAMETER_USER_TITLE = "ut";
    private static final String PARAMETER_FIRSTNAME = "fn";
    private static final String PARAMETER_LASTNAME = "ln";
    private static final String PARAMETER_PHONE = "ph";
    private static final String PARAMETER_EMAIL = "em";
    private static final String PARAMETER_CATEGORY = "cat";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_SELECTED_TAB = "selected_tab";
    private static final String PARAMETER_NOMENCLATURE = "nom";

    // Properties for page titles
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETS = "ticketing.manage_tickets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKET = "ticketing.modify_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKET = "ticketing.create_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_TICKET = "ticketing.recap_ticket.pageTitle";

    // Markers
    private static final String MARK_TICKET_LIST = "ticket_list";
    private static final String MARK_USER_FACTORY = "user_factory";
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_TICKET_PRECISIONS_LIST = "ticket_precisions_list";
    private static final String MARK_NB_TICKET_AGENT = "nb_ticket_agent";
    private static final String MARK_NB_TICKET_GROUP = "nb_ticket_group";
    private static final String MARK_NB_TICKET_DOMAIN = "nb_ticket_domain";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    private static final String MARK_GUID = "guid";
    private static final String MARK_RESPONSE_RECAP_LIST = "response_recap_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_SELECTED_TAB = "selected_tab";
    private static final String JSP_MANAGE_TICKETS = TicketingConstants.ADMIN_CONTROLLLER_PATH + "ManageTickets.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKET = "ticketing.message.confirmRemoveTicket";

    // Views
    private static final String VIEW_MANAGE_TICKETS = "manageTickets";
    private static final String VIEW_CREATE_TICKET = "createTicket";
    private static final String VIEW_MODIFY_TICKET = "modifyTicket";
    private static final String VIEW_TICKET_FORM = "ticketForm";
    private static final String VIEW_RECAP_TICKET = "recapTicket";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";
    private static final String ACTION_MODIFY_TICKET = "modifyTicket";
    private static final String ACTION_REMOVE_TICKET = "removeTicket";
    private static final String ACTION_CONFIRM_REMOVE_TICKET = "confirmRemoveTicket";
    private static final String ACTION_RECAP_TICKET = "recapTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";
    private static final String INFO_TICKET_UPDATED = "ticketing.info.ticket.updated";
    private static final String INFO_TICKET_REMOVED = "ticketing.info.ticket.removed";

    // Other constants
    private static boolean _bAvatarAvailable = ( PluginService.getPlugin( TicketingConstants.PLUGIN_AVATAR ) != null );

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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        _ticketFormService.removeTicketFromSession( request.getSession(  ) );
        TicketAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION,
                TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( ( request.getParameter( TicketingConstants.PARAMETER_BACK ) != null ) &&
                StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }

        TicketFilter filter = TicketFilterHelper.getFilter( request, getUser(  ) );

        _strCurrentPageIndex = Paginator.getPageIndex( request, PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETS );
        String strUrl = url.getUrl(  );

        List<Ticket> listAgentTickets = new ArrayList<Ticket>(  );
        List<Ticket> listGroupTickets = new ArrayList<Ticket>(  );
        List<Ticket> listDomainTickets = new ArrayList<Ticket>(  );

        TicketUtils.setTicketsListByPerimeter( getUser(  ), filter, request, listAgentTickets, listGroupTickets,
            listDomainTickets );

        String strPreviousSelectedTab = ( request.getSession(  ).getAttribute( PARAMETER_SELECTED_TAB ) != null )
            ? (String) request.getSession(  ).getAttribute( PARAMETER_SELECTED_TAB ) : TabulationEnum.AGENT.getLabel(  );
        String strSelectedTab = getSelectedTab( request );

        if ( !strPreviousSelectedTab.equals( strSelectedTab ) )
        {
            //tab changes we reset index
            _strCurrentPageIndex = "1";
        }

        List<Ticket> listTickets = null;

        if ( strSelectedTab.equals( TabulationEnum.AGENT.getLabel(  ) ) )
        {
            listTickets = listAgentTickets;
        }
        else if ( strSelectedTab.equals( TabulationEnum.GROUP.getLabel(  ) ) )
        {
            listTickets = listGroupTickets;
        }
        else if ( strSelectedTab.equals( TabulationEnum.DOMAIN.getLabel(  ) ) )
        {
            listTickets = listDomainTickets;
        }

        //store list tickets for navigation in view details
        request.getSession(  ).setAttribute( TicketingConstants.SESSION_LIST_TICKETS_NAVIGATION, listTickets );

        // PAGINATORS
        LocalizedPaginator<Ticket> paginatorTickets = new LocalizedPaginator<Ticket>( listTickets, _nItemsPerPage,
                strUrl, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );
        setWorkflowAttributes( paginatorTickets );

        Map<String, Object> model = getModel(  );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_TICKET_LIST, paginatorTickets.getPageItems(  ) );
        model.put( MARK_PAGINATOR, paginatorTickets );
        model.put( MARK_NB_TICKET_AGENT, listAgentTickets.size(  ) );
        model.put( MARK_NB_TICKET_GROUP, listGroupTickets.size(  ) );
        model.put( MARK_NB_TICKET_DOMAIN, listDomainTickets.size(  ) );
        model.put( MARK_SELECTED_TAB, strSelectedTab );
        model.put( MARK_USER_FACTORY, UserFactory.getInstance(  ) );
        model.put( TicketingConstants.MARK_AVATAR_AVAILABLE, _bAvatarAvailable );
        TicketFilterHelper.setModel( model, filter, request, getUser(  ) );
        ModelUtils.storeTicketRights( model, getUser(  ) );

        String strCreationDateDisplay = AdminUserPreferencesService.instance(  )
                                                                   .get( String.valueOf( getUser(  ).getUserId(  ) ),
                TicketingConstants.USER_PREFERENCE_CREATION_DATE_DISPLAY, StringUtils.EMPTY );
        model.put( TicketingConstants.MARK_CREATION_DATE_AS_DATE,
            TicketingConstants.USER_PREFERENCE_CREATION_DATE_DISPLAY_DATE.equals( strCreationDateDisplay ) );

        String messageInfo = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION,
                TicketingConstants.ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO );

        if ( StringUtils.isNotEmpty( messageInfo ) )
        {
            addInfo( messageInfo );
            fillCommons( model );
        }

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETS, TEMPLATE_MANAGE_TICKETS, model );
    }

    /**
     * returns current selectedTab
     * @param request http request
     * @return selectedTab
     */
    private String getSelectedTab( HttpServletRequest request )
    {
        String strSelectedTab = TabulationEnum.AGENT.getLabel(  );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_SELECTED_TAB ) ) )
        {
            strSelectedTab = request.getParameter( PARAMETER_SELECTED_TAB );
            request.getSession(  ).setAttribute( PARAMETER_SELECTED_TAB, strSelectedTab );
        }
        else
        {
            if ( StringUtils.isNotEmpty( (String) request.getSession(  ).getAttribute( PARAMETER_SELECTED_TAB ) ) )
            {
                strSelectedTab = (String) request.getSession(  ).getAttribute( PARAMETER_SELECTED_TAB );
            }
        }

        return strSelectedTab;
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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        clearUploadFilesIfNeeded( request.getSession(  ) );

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        ticket = ( ticket != null ) ? ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        initTicketForm( request, ticket, model );

        TicketCategory ticketCategory = new TicketCategory(  );
        ticket.setTicketCategory( ticketCategory );

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
        String strGuid = request.getParameter( TicketingConstants.PARAMETER_GUID );
        String strIdCustomer = request.getParameter( TicketingConstants.PARAMETER_CUSTOMER_ID );
        String strIdUserTitle = request.getParameter( PARAMETER_USER_TITLE );
        String strFirstname = request.getParameter( PARAMETER_FIRSTNAME );
        String strLastname = request.getParameter( PARAMETER_LASTNAME );
        String strMobilePhoneNumber = request.getParameter( PARAMETER_PHONE );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        String strCategoryCode = request.getParameter( PARAMETER_CATEGORY );
        String strNomenclature = request.getParameter( PARAMETER_NOMENCLATURE );
        ticket.enrich( strIdUserTitle, strFirstname, strLastname, null, strMobilePhoneNumber, strEmail,
            strCategoryCode, null, null, null, strGuid, strIdCustomer, strNomenclature );

        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale(  ) ) );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put( MARK_TICKET_PRECISIONS_LIST, new ReferenceList(  ) );
        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList( request.getLocale(  ) ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_GUID, strGuid );
        ModelUtils.storeChannels( request, model );
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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        try
        {
            Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
            TicketHome.create( ticket );

            if ( ( ticket.getListResponse(  ) != null ) && !ticket.getListResponse(  ).isEmpty(  ) )
            {
                for ( Response response : ticket.getListResponse(  ) )
                {
                    ResponseHome.create( response );
                    TicketHome.insertTicketResponse( ticket.getId(  ), response.getIdResponse(  ) );
                }
            }

            doProcessNextWorkflowAction( ticket, request );
        }
        catch ( Exception e )
        {
            addError( TicketingConstants.ERROR_TICKET_CREATION_ABORTED, getLocale(  ) );
            AppLogService.error( e );

            return redirectView( request, VIEW_MANAGE_TICKETS );
        }

        return redirectAfterCreateAction( request );
    }

    /**
     * Computes redirection for creation action
     * @param request http request
     * @return redirect view
     */
    public String redirectAfterCreateAction( HttpServletRequest request )
    {
        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION,
                TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        int nId = Integer.parseInt( request.getParameter( TicketingConstants.PARAMETER_ID_TICKET ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKET ) );
        url.addParameter( TicketingConstants.PARAMETER_ID_TICKET, nId );

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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_DELETE, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        int nId = Integer.parseInt( request.getParameter( TicketingConstants.PARAMETER_ID_TICKET ) );

        doRemoveWorkFlowResource( nId );

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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_VIEW, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        clearUploadFilesIfNeeded( request.getSession(  ) );

        int nId = Integer.parseInt( request.getParameter( TicketingConstants.PARAMETER_ID_TICKET ) );

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        if ( ticket == null )
        {
            ticket = TicketHome.findByPrimaryKey( nId );
        }
        else
        {
            ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        }

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

        Map<String, Object> model = getModel(  );
        initTicketForm( request, ticket, model );

        _ticketFormService.saveTicketInSession( request.getSession(  ), ticket );

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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_MODIFY, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        boolean bIsFormValid = populateAndValidateFormTicket( ticket, request );

        if ( !bIsFormValid )
        {
            return redirect( request, VIEW_MODIFY_TICKET, TicketingConstants.PARAMETER_ID_TICKET, ticket.getId(  ) );
        }

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

        return redirectToViewDetails( request, ticket );
    }

    /**
     * Returns the form to recapitulate a ticket to create
     *
     * @param request
     *            The Http request
     * @return the html code of the ticket form
     */
    @View( VIEW_RECAP_TICKET )
    public String getRecapTicket( HttpServletRequest request )
    {
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        List<ResponseRecap> listResponseRecap = _ticketFormService.getListResponseRecap( ticket.getListResponse(  ) );

        Map<String, Object> model = getModel(  );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_RESPONSE_RECAP_LIST, listResponseRecap );

        return getPage( PROPERTY_PAGE_TITLE_RECAP_TICKET, TEMPLATE_RECAP_TICKET, model );
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
        //Check user rights
        if ( !RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_CREATE, getUser(  ) ) )
        {
            return redirect( request,
                AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        ticket = ( ticket != null ) ? ticket : new Ticket(  );

        TicketCategory ticketCategory = new TicketCategory(  );
        ticket.setTicketCategory( ticketCategory );

        boolean bIsFormValid = populateAndValidateFormTicket( ticket, request );

        if ( !bIsFormValid )
        {
            _ticketFormService.saveTicketInSession( request.getSession(  ), ticket );

            return redirectView( request, VIEW_CREATE_TICKET );
        }
        else
        {
            TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain(  ) );
            ticket.setTicketDomain( ticketDomain.getLabel(  ) );

            ticket.setTicketType( TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType(  ) ).getLabel(  ) );
            ticket.setContactMode( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode(  ) ).getCode(  ) );
            ticket.setUserTitle( UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle(  ) ).getLabel(  ) );
            ticket.setChannel( ChannelHome.findByPrimaryKey( ticket.getChannel(  ).getId(  ) ) );

            _ticketFormService.saveTicketInSession( request.getSession(  ), ticket );

            return redirectView( request, VIEW_RECAP_TICKET );
        }
    }

    /**
     * Populate the ticket from the request and validate the ticket form
     *
     * @param ticket
     *            The ticket to populate
     * @param request
     *            The Http Request
     * @return true if the ticket is valid else false
     */
    public boolean populateAndValidateFormTicket( Ticket ticket, HttpServletRequest request )
    {
        boolean bIsFormValid = true;
        populate( ticket, request );

        int nIdCategory = Integer.valueOf( request.getParameter( PARAMETER_ID_CATEGORY ) );
        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( nIdCategory );
        ticket.setTicketCategory( ticketCategory );

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );

        if ( ticket.getTicketCategory(  ).getId(  ) > 0 )
        {
            EntryFilter filter = new EntryFilter(  );
            TicketForm form = TicketFormHome.findByCategoryId( ticket.getTicketCategory(  ).getId(  ) );

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
        bIsFormValid = validateBean( ticket, TicketingConstants.VALIDATION_ATTRIBUTES_PREFIX );

        TicketValidator ticketValidator = TicketValidatorFactory.getInstance(  ).create( request.getLocale(  ) );
        List<String> listValidationErrors = ticketValidator.validate( ticket, false );

        FormValidator formValidator = new FormValidator( request );
        listValidationErrors.add( formValidator.isContactModeFilled(  ) );

        for ( String error : listValidationErrors )
        {
            if ( !StringUtils.isEmpty( error ) )
            {
                addError( error );
                bIsFormValid = false;
            }
        }

        if ( listFormErrors.size(  ) > 0 )
        {
            bIsFormValid = false;
        }

        return bIsFormValid;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectAfterWorkflowAction( HttpServletRequest request )
    {
        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectWorkflowActionCancelled( HttpServletRequest request )
    {
        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String defaultRedirectWorkflowAction( HttpServletRequest request )
    {
        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * Redirect to the view details for the ticket passed in parameter
     *
     * @param request The Http request
     * @param ticket The ticket
     * @return The Jsp URL of the process result
     */
    public String redirectToViewDetails( HttpServletRequest request, Ticket ticket )
    {
        UrlItem url = new UrlItem( TicketingConstants.JSP_VIEW_TICKET );
        url.addParameter( TicketingConstants.PARAMETER_ID_TICKET, ticket.getId(  ) );

        return redirect( request, url.getUrl(  ) );
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
