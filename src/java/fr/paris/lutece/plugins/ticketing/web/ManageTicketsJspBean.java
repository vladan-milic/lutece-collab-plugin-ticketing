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
import fr.paris.lutece.plugins.ticketing.web.ticketfilter.TicketFilterHelper;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
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
    private static final String TEMPLATE_MANAGE_TICKETS = "/admin/plugins/ticketing/manage_tickets.html";
    private static final String TEMPLATE_CREATE_TICKET = "/admin/plugins/ticketing/create_ticket.html";
    private static final String TEMPLATE_MODIFY_TICKET = "/admin/plugins/ticketing/modify_ticket.html";
    private static final String TEMPLATE_RECAP_TICKET = "/admin/plugins/ticketing/recap_ticket.html";

    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_ticket_category";
    private static final String PARAMETER_GUID = "guid";
    private static final String PARAMETER_FIRSTNAME = "fn";
    private static final String PARAMETER_LASTNAME = "ln";
    private static final String PARAMETER_PHONE = "ph";
    private static final String PARAMETER_EMAIL = "em";
    private static final String PARAMETER_CATEGORY = "cat";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_SELECTED_TAB = "selected_tab";

    // Properties for page titles
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETS = "ticketing.manage_tickets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKET = "ticketing.modify_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKET = "ticketing.create_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_TICKET = "ticketing.recap_ticket.pageTitle";

    // Markers
    private static final String MARK_TICKET_LIST = "ticket_list";
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_NB_TICKET_AGENT = "nb_ticket_agent";
    private static final String MARK_NB_TICKET_GROUP = "nb_ticket_group";
    private static final String MARK_NB_TICKET_DOMAIN = "nb_ticket_domain";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    private static final String MARK_ADMIN_AVATAR = "adminAvatar";
    private static final String MARK_GUID = "guid";
    private static final String MARK_RESPONSE_RECAP_LIST = "response_recap_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_SELECTED_TAB = "selected_tab";
    private static final String JSP_MANAGE_TICKETS = "jsp/admin/plugins/ticketing/ManageTickets.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKET = "ticketing.message.confirmRemoveTicket";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticket.attribute.";

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

    // Errors
    private static final String ERROR_PHONE_NUMBER_MISSING = "ticketing.error.phonenumber.missing";
    private static final String ERROR_INCONSISTENT_CONTACT_MODE_WITH_PHONE_NUMBER_FILLED = "ticketing.error.contactmode.inconsistent";

    // Session keys
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

        TicketFilter filter = TicketFilterHelper.getFilter( request );

        _strCurrentPageIndex = Paginator.getPageIndex( request, PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETS );
        String strUrl = url.getUrl(  );

        List<Ticket> listAgentTickets = new ArrayList<Ticket>(  );
        List<Ticket> listGroupTickets = new ArrayList<Ticket>(  );
        List<Ticket> listDomainTickets = new ArrayList<Ticket>(  );

        TicketHelper.setTicketsListByPerimeter( getUser(  ), filter, request, listAgentTickets, listGroupTickets,
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
        model.put( MARK_ADMIN_AVATAR, _bAdminAvatar );
        TicketFilterHelper.setModel( model, filter, request );
        TicketHelper.storeTicketRightsIntoModel( model, getUser(  ) );

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
        clearUploadFilesIfNeeded( request.getSession(  ) );

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        ticket = ( ticket != null ) ? ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        initTicketForm( request, ticket, model );

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
        String strCustomerId = request.getParameter( TicketingConstants.PARAMETER_CUSTOMER_ID );
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

        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale(  ) ) );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList(  ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
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

        if ( ( ticket.getListResponse(  ) != null ) && !ticket.getListResponse(  ).isEmpty(  ) )
        {
            for ( Response response : ticket.getListResponse(  ) )
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( ticket.getId(  ), response.getIdResponse(  ) );
            }
        }

        doProcessWorkflowAutomaticAction( ticket );

        addInfo( INFO_TICKET_CREATED, getLocale(  ) );

        return redirectAfterCreateAction( request );
    }

    /**
     * compture redirection for creation action
     * @param request http request
     * @return redirect view
     */
    public String redirectAfterCreateAction( HttpServletRequest request )
    {
        if ( StringUtils.isNotEmpty( 
                    (String) request.getSession(  ).getAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL ) ) )
        {
            String strRedirectUrl = (String) request.getSession(  ).getAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL );
            //we remove redirect session attribute before leaving
            request.getSession(  ).removeAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL );

            return redirect( request, strRedirectUrl );
        }

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
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        ticket = ( ticket != null ) ? ticket : new Ticket(  );

        boolean bIsFormValid = populateAndValidateFormTicket( ticket, request );

        if ( !bIsFormValid )
        {
            _ticketFormService.saveTicketInSession( request.getSession(  ), ticket );

            return redirectView( request, VIEW_CREATE_TICKET );
        }
        else
        {
            TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
            TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory.getIdTicketDomain(  ) );
            ticket.setTicketCategory( ticketCategory.getLabel(  ) );
            ticket.setTicketDomain( ticketDomain.getLabel(  ) );

            ticket.setTicketType( TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType(  ) ).getLabel(  ) );
            ticket.setContactMode( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode(  ) ).getLabel(  ) );
            ticket.setUserTitle( UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle(  ) ).getLabel(  ) );

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

        if ( ticket.isInconsistentContactModeWithPhoneNumberFilled(  ) )
        {
            bIsFormValid = false;
            addError( ERROR_INCONSISTENT_CONTACT_MODE_WITH_PHONE_NUMBER_FILLED, getLocale(  ) );
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
