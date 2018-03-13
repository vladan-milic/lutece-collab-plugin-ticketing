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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.address.TicketAddress;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilterViewEnum;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.web.search.SearchConstants;
import fr.paris.lutece.plugins.ticketing.web.search.TicketSearchEngine;
import fr.paris.lutece.plugins.ticketing.web.ticketfilter.TicketFilterHelper;
import fr.paris.lutece.plugins.ticketing.web.user.UserFactory;
import fr.paris.lutece.plugins.ticketing.web.util.*;
import fr.paris.lutece.plugins.ticketing.web.workflow.TicketTaskException;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
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
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

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
    public static final  String RIGHT_MANAGETICKETS = "TICKETING_TICKETS_MANAGEMENT";
    private static final long   serialVersionUID    = 1L;

    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETS = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "manage_tickets.html";
    private static final String TEMPLATE_CREATE_TICKET  = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "create_ticket.html";
    private static final String TEMPLATE_MODIFY_TICKET  = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "modify_ticket.html";
    private static final String TEMPLATE_RECAP_TICKET   = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "recap_ticket.html";

    // Parameters
    private static final String PARAMETER_USER_TITLE   = "ut";
    private static final String PARAMETER_FIRSTNAME    = "fn";
    private static final String PARAMETER_FAMILYNAME   = "fan";
    private static final String PARAMETER_LASTNAME     = "ln";
    private static final String PARAMETER_FIXED_PHONE  = "fph";
    private static final String PARAMETER_MOBILE_PHONE = "mph";
    private static final String PARAMETER_EMAIL        = "em";
    private static final String PARAMETER_CATEGORY     = "cat";
    private static final String PARAMETER_PAGE_INDEX   = "page_index";
    private static final String PARAMETER_SELECTED_TAB = "selected_tab";
    private static final String PARAMETER_NOMENCLATURE = "nom";

    // Properties for page titles
    private static final String PROPERTY_PAGE_MANAGE_TITLE          = "ticketing.manage_tickets.title";
    private static final String PROPERTY_PAGE_SEARCH_TILE           = "ticketing.search_ticket.pageTitle";
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETS  = "ticketing.manage_tickets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKET   = "ticketing.modify_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKET   = "ticketing.create_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_RECAP_TICKET    = "ticketing.recap_ticket.pageTitle";

    // Markers
    private static final String MARK_TICKET_LIST = "ticket_list";
    private static final String MARK_USER_FACTORY = "user_factory";
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_MASS_ACTIONS_LIST = "mass_actions_list";
    private static final String MARK_NB_TICKET_AGENT = "nb_ticket_agent";
    private static final String MARK_NB_TICKET_GROUP = "nb_ticket_group";
    private static final String MARK_NB_TICKET_DOMAIN = "nb_ticket_domain";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    private static final String MARK_GUID = "guid";
    private static final String MARK_RESPONSE_RECAP_LIST = "response_recap_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_SELECTED_TAB = "selected_tab";
    private static final String MARK_USER_WITH_NO_UNIT = "user_with_no_unit";
    private static final String JSP_MANAGE_TICKETS = TicketingConstants.ADMIN_CONTROLLLER_PATH + "ManageTickets.jsp";
    private static final String MARK_MANAGE_PAGE_TITLE = "manage_ticket_page_title";
    private static final String MARK_CREATE_ASSIGN_RIGHT = "create_assign_right";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKET    = "ticketing.message.confirmRemoveTicket";
    private static final String MESSAGE_ERROR_COMMENT_VALIDATION = "ticketing.validation.ticket.TicketComment.size";

    // Views
    private static final String VIEW_MANAGE_TICKETS = "manageTickets";
    private static final String VIEW_TICKET_PAGE    = "ticketPage";
    private static final String VIEW_CREATE_TICKET  = "createTicket";
    private static final String VIEW_MODIFY_TICKET  = "modifyTicket";
    private static final String VIEW_RECAP_TICKET   = "recapTicket";

    // Actions
    private static final String ACTION_CREATE_TICKET         = "createTicket";
    private static final String ACTION_CREATE_ASSIGN_TICKET  = "createAssignTicket";
    private static final String ACTION_MODIFY_TICKET         = "modifyTicket";
    private static final String ACTION_REMOVE_TICKET         = "removeTicket";
    private static final String ACTION_CONFIRM_REMOVE_TICKET = "confirmRemoveTicket";
    private static final String ACTION_RECAP_TICKET          = "recapTicket";
    private static final String ACTION_EXPORT_TICKET         = "exportTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";
    private static final String INFO_TICKET_UPDATED = "ticketing.info.ticket.updated";
    private static final String INFO_TICKET_REMOVED = "ticketing.info.ticket.removed";

    // Other constants
    private static       boolean _bAvatarAvailable = ( PluginService.getPlugin( TicketingConstants.PLUGIN_AVATAR ) != null );
    private static final String  CONTENT_TYPE_CSV  = "application/csv";

    // Variables
    private int    _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int    _nItemsPerPage;
    private boolean _bSearchMode = false;
    private List<TicketCategory> _lstTicketDomain;
    private final TicketFormService  _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );
    private final TicketSearchEngine _engine            = ( TicketSearchEngine ) SpringContextService.getBean( SearchConstants.BEAN_SEARCH_ENGINE );

    //Header export
    private static final String HEADER_REFERENCE = "ticketing.export.header.reference"; 
    private static final String HEADER_CREATION_DATE = "ticketing.export.header.creation.date";
    private static final String HEADER_TIME_CREATION = "ticketing.export.header.time.creation";
    private static final String HEADER_NATURE_SOLICITATION = "ticketing.export.header.nature.solicitation";
    private static final String HEADER_DOMAIN_SOLICITATION = "ticketing.export.header.domain.solicitation";
    private static final String HEADER_PROBLEMATIC_SOLICITATION = "ticketing.export.header.problematic.solicitation";
    private static final String HEADER_SUB_PROBLEMS_SOLLICITATION = "ticketing.export.header.sub.problems.solicitation";
    private static final String HEADER_OBJECT_SOLICITATION = "ticketing.export.header.object.solicitation";
    private static final String HEADER_STATUS = "ticketing.export.header.status";
    private static final String HEADER_NOMENCLATURE = "ticketing.export.header.nomenclature";
    private static final String HEADER_CHANNEL = "ticketing.export.header.channel";
    private static final String HEADER_ASSIGNEMENT_ENTITY = "ticketing.export.header.assignement.entity";
    private static final String HEADER_ASSIGNEMENT_OFFICER = "ticketing.export.header.assignement.officer";

    /**
     * Build the Manage View
     *
     * @param request
     *         The HTTP request
     * @return The page
     */
    @View( value = VIEW_TICKET_PAGE, defaultView = true )
    public String getTicketPage( HttpServletRequest request )
    {
        // Check user rights
        AdminUser userCurrent = getUser( );
        if ( RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_VIEW, userCurrent ) )
        {
            return getManageTickets( request );
        } else if ( RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return getCreateTicket( request );
        }

        return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
    }

    /**
     * Export all the tickets found in the search
     *
     * @param request
     *         The HTTP request
     * @return The page
     */
    @Action( value = ACTION_EXPORT_TICKET )
    public String exportTickets( HttpServletRequest request )
    {
        // Check user rights
        AdminUser userCurrent = getUser( );
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_EXPORT, userCurrent ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        TicketFilter filter = null;
        try
        {
            filter = TicketFilterHelper.getFilter( request, userCurrent );
        } catch ( java.text.ParseException e )
        {
            AppLogService.error( "Error while parsing dates", e );
            addError( SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
        }
        TicketFilterHelper.setFilterUserAndUnitIds( filter, userCurrent );

        // Determine if we are in Search Ticket mode or in Manage Ticket mode
        String strQuery = request.getParameter( SearchConstants.PARAMETER_QUERY );

        if ( StringUtils.isBlank( strQuery ) )
        {
            strQuery = StringUtils.EMPTY;
        }

        //        Map<Integer, TicketDomain> mapIdDomainTicketDomain = new LinkedHashMap<>( );
        //        addTicketDomainToMapFromPermission( mapIdDomainTicketDomain, TicketDomainResourceIdService.PERMISSION_VIEW_LIST );
        //        addTicketDomainToMapFromPermission( mapIdDomainTicketDomain, TicketDomainResourceIdService.PERMISSION_VIEW_DETAIL );
        //
        //        _lstTicketDomain = new ArrayList<>( mapIdDomainTicketDomain.values( ) );

        if ( filter.getMapCategoryId( ) != null &&  filter.getMapCategoryId( ).get( TicketingConstants.DOMAIN_DEPTH ) != null && filter.getMapCategoryId( ).get( TicketingConstants.DOMAIN_DEPTH ) != -1)
        {
            TicketCategory ticketDomain = TicketCategoryService.getInstance( ).findCategoryById( filter.getMapCategoryId( ).get( TicketingConstants.DOMAIN_DEPTH ) );
            _lstTicketDomain.clear( );
            _lstTicketDomain.add( ticketDomain );
        } else
        {
            Map<Integer, TicketCategory> mapIdDomainTicketDomain = new LinkedHashMap<>( );
            addTicketDomainToMapFromPermission( mapIdDomainTicketDomain, TicketCategory.PERMISSION_VIEW_DETAIL );
            addTicketDomainToMapFromPermission( mapIdDomainTicketDomain, TicketCategory.PERMISSION_VIEW_LIST );

            _lstTicketDomain = new ArrayList<>( mapIdDomainTicketDomain.values( ) );
        }
        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        SimpleDateFormat sdf2 = new SimpleDateFormat( "HH:mm" );

        try
        {
            List<Ticket> listTickets = _engine.searchTickets( strQuery, _lstTicketDomain, filter );
            File tempFile = File.createTempFile( "ticketing", null );

            List<String> titlesUntranslated = new ArrayList<>();
            titlesUntranslated.add( HEADER_REFERENCE );
            titlesUntranslated.add( HEADER_CREATION_DATE );
            titlesUntranslated.add( HEADER_TIME_CREATION );
            // Insert here category types after being translated
            Integer indexWhereCategoryWillBeAdded = 2;
            titlesUntranslated.add( HEADER_NATURE_SOLICITATION );
            titlesUntranslated.add( HEADER_DOMAIN_SOLICITATION );
            titlesUntranslated.add( HEADER_PROBLEMATIC_SOLICITATION );
            titlesUntranslated.add( HEADER_SUB_PROBLEMS_SOLLICITATION );
            titlesUntranslated.add( HEADER_OBJECT_SOLICITATION );
            titlesUntranslated.add( HEADER_STATUS );
            titlesUntranslated.add( HEADER_NOMENCLATURE );
            titlesUntranslated.add( HEADER_CHANNEL );
            titlesUntranslated.add( HEADER_ASSIGNEMENT_ENTITY );
            titlesUntranslated.add( HEADER_ASSIGNEMENT_OFFICER );

            List<String> titlesTranslated = titlesUntranslated.stream( ).map( title -> I18nService.getLocalizedString( title, Locale.FRENCH ) ).collect( Collectors.toList( ) );

            for(TicketCategoryType category : TicketCategoryTypeHome.getCategoryTypesList( )) {
                titlesTranslated.add( indexWhereCategoryWillBeAdded + category.getDepthNumber( ), category.getLabel( ) );
            }

            // Write header in the temp file
            Writer w = new OutputStreamWriter( new FileOutputStream( tempFile ), StandardCharsets.ISO_8859_1 );

            // Write line in the temp file
            CSVUtils.writeLine(w, titlesTranslated);

            for ( Ticket ticket : listTickets )
            {
                // Data line
                List<String> line = new ArrayList<>( );
                line.add( ticket.getReference( ) );
                line.add( sdf.format( ticket.getDateCreate( ) ) );
                line.add( sdf2.format( ticket.getDateCreate( ) ) );
                for(TicketCategory category : ticket.getBranch( )) {
                    line.add( category.getLabel( ) );
                }
                line.add( ticket.getTicketComment( ) );
                line.add( ticket.getState( ).getName( ) );
                line.add( ticket.getNomenclature( ) );
                line.add( ticket.getChannel( ) != null ? ticket.getChannel( ).getLabel( ) : "" );
                line.add( ticket.getAssigneeUnit( ) != null ? ticket.getAssigneeUnit( ).getName( ) : "" );
                line.add( ticket.getAssigneeUser( ) != null ? ticket.getAssigneeUser( ).getFirstname( ) + " " + ticket.getAssigneeUser( ).getLastname( ) : "" );

                // Write line in the temp file
                CSVUtils.writeLine( w, line );

            }
            w.flush( );
            w.close( );
            // Send the the content of the file to the user
            download( Files.readAllBytes( tempFile.toPath( ) ), "sollicitations.csv", CONTENT_TYPE_CSV );

        } catch ( ParseException e )
        {
            AppLogService.error( "Error while parsing query " + strQuery, e );
            addError( SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
        } catch ( IOException e )
        {
            AppLogService.error( "Error while creating temporary file ", e );
        }

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * Build the Manage View
     *
     * @param request
     *         The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETS )
    public String getManageTickets( HttpServletRequest request )
    {
        // Check user rights
        AdminUser userCurrent = getUser( );
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_VIEW, userCurrent ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        _ticketFormService.removeTicketFromSession( request.getSession( ) );
        TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );

        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( ( request.getParameter( TicketingConstants.PARAMETER_BACK ) != null ) && StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }

        // Determine if we are in Search Ticket mode or in Manage Ticket mode
        String strQuery = request.getParameter( SearchConstants.PARAMETER_QUERY );
        _bSearchMode = StringUtils.isNotBlank( strQuery ) ? true : false;
        if ( StringUtils.isBlank( strQuery ) )
        {
            strQuery = StringUtils.EMPTY;
        }

        TicketFilter filter = null;
        try
        {
            filter = TicketFilterHelper.getFilter( request, userCurrent );
        } catch ( java.text.ParseException e )
        {
            AppLogService.error( "Error while parsing dates", e );
            addError( SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
        }
        TicketFilterHelper.setFilterUserAndUnitIds( filter, userCurrent );

        // Check if a user belong to a unit
        boolean bUserWithNoUnit = ( filter.getFilterIdAssigneeUnit( ) == null || filter.getFilterIdAssigneeUnit( ).isEmpty( ) || filter.getFilterIdAssignerUnit( ) == null || filter
                .getFilterIdAssignerUnit( ).isEmpty( ) ) ?
                        true :
                            false;

        _strCurrentPageIndex = Paginator.getPageIndex( request, PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETS );
        if ( StringUtils.isNotBlank( strQuery ) )
        {
            url.addParameter( SearchConstants.PARAMETER_QUERY, strQuery );
        }
        String strUrl = url.getUrl( );

        String strPreviousSelectedTab = ( request.getSession( ).getAttribute( PARAMETER_SELECTED_TAB ) != null ) ?
                ( String ) request.getSession( ).getAttribute( PARAMETER_SELECTED_TAB ) :
                    TabulationEnum.AGENT.getLabel( );
                String strSelectedTab = getSelectedTab( request );

                if ( !strPreviousSelectedTab.equals( strSelectedTab ) || StringUtils.isEmpty( _strCurrentPageIndex ) )
                {
                    // tab changes we reset index
                    _strCurrentPageIndex = "1";
                }
                int nCurrentPageIndex = Integer.parseInt( _strCurrentPageIndex );

                List<Ticket> listTickets = new ArrayList<>( );
                List<Integer> listIdTickets = new ArrayList<>( );

                if ( filter.getMapCategoryId( ) != null &&  filter.getMapCategoryId( ).get( TicketingConstants.DOMAIN_DEPTH ) != null && filter.getMapCategoryId( ).get( TicketingConstants.DOMAIN_DEPTH ) != -1)
                {
                    TicketCategory ticketDomain = TicketCategoryService.getInstance( ).findCategoryById( filter.getMapCategoryId( ).get( TicketingConstants.DOMAIN_DEPTH ) );
                    _lstTicketDomain.clear( );
                    _lstTicketDomain.add( ticketDomain );
                }
                else
                {
                    Map<Integer, TicketCategory> mapIdDomainTicketDomain = new LinkedHashMap<>( );
                    addTicketDomainToMapFromPermission( mapIdDomainTicketDomain, TicketCategory.PERMISSION_VIEW_DETAIL );
                    addTicketDomainToMapFromPermission( mapIdDomainTicketDomain, TicketCategory.PERMISSION_VIEW_LIST );

                    _lstTicketDomain = new ArrayList<>( mapIdDomainTicketDomain.values( ) );
                }

                // Set the limit for the number of ticket to display
                filter.setTicketsLimitStart( ( nCurrentPageIndex - 1 ) * _nItemsPerPage );
                filter.setTicketsLimitCount( _nItemsPerPage );

                // The number of tickets for AGENT and GROUP category
                int nAgentTickets = 0;
                int nGroupTickets = 0;

                // Get the seleted tab name
                String strUpperSelectedTab = StringUtils.isBlank( StringUtils.upperCase( strSelectedTab ) ) ? StringUtils.EMPTY : StringUtils.upperCase( strSelectedTab );
                try
                {
                    filter.setFilterView( TicketFilterViewEnum.valueOf( strUpperSelectedTab ) );
                } catch ( IllegalArgumentException e )
                {
                    // The default view
                    filter.setFilterView( TicketFilterViewEnum.AGENT );
                }

                try
                {
                    List<Ticket> ticketsUnrestricted = _engine.searchTickets( strQuery, _lstTicketDomain, filter );
                    
                    // Filter all tickets that are authorized
                    listTickets = ticketsUnrestricted.stream( ).filter( ticket -> TicketUtils.isAuthorized(ticket, TicketCategory.PERMISSION_VIEW_LIST, userCurrent) ).collect( Collectors.toList( ) );
                    
                    if ( listTickets != null && !listTickets.isEmpty( ) )
                    {
                        for ( Ticket ticket : listTickets )
                        {
                            listIdTickets.add( ticket.getId( ) );
                        }
                    }

                    // Compute the number of ticket for the AGENT and GROUP filter
                    if ( strUpperSelectedTab.equals( TicketFilterViewEnum.AGENT.toString( ) ) )
                    {
                        nAgentTickets = listIdTickets.size( );
                        filter.setFilterView( TicketFilterViewEnum.GROUP );
                        nGroupTickets = getNbTicketsWithLucene( strQuery, _lstTicketDomain, filter );
                    } else if ( strUpperSelectedTab.equals( TicketFilterViewEnum.GROUP.toString( ) ) )
                    {
                        nGroupTickets = listIdTickets.size( );
                        filter.setFilterView( TicketFilterViewEnum.AGENT );
                        nAgentTickets = getNbTicketsWithLucene( strQuery, _lstTicketDomain, filter );
                    } else
                    {
                        filter.setFilterView( TicketFilterViewEnum.AGENT );
                        nAgentTickets = getNbTicketsWithLucene( strQuery, _lstTicketDomain, filter );
                        filter.setFilterView( TicketFilterViewEnum.GROUP );
                        nGroupTickets = getNbTicketsWithLucene( strQuery, _lstTicketDomain, filter );
                    }
                } catch ( ParseException e )
                {
                    AppLogService.error( "Error while parsing query " + strQuery, e );
                    addError( SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
                }

                // Clean filter view for save in session
                filter.setTicketsLimitStart( TicketFilter.CONSTANT_ID_NULL );
                filter.setTicketsLimitCount( TicketFilter.CONSTANT_ID_NULL );
                filter.setFilterView( TicketFilterViewEnum.ALL );

                // Store list tickets for navigation in view details
                request.getSession( ).setAttribute( TicketingConstants.SESSION_LIST_TICKETS_NAVIGATION, listIdTickets );

                // PAGINATORS
                LocalizedPaginator<Ticket> paginatorTickets = new LocalizedPaginator<Ticket>( listTickets, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale( ) );

                Map<String, Object> model = getModel( );
                model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
                model.put( SearchConstants.MARK_QUERY, StringEscapeUtils.escapeHtml( strQuery ) );
                model.put( MARK_TICKET_LIST, paginatorTickets.getPageItems( ) );
                model.put( MARK_PAGINATOR, paginatorTickets );
                model.put( MARK_NB_TICKET_AGENT, nAgentTickets );
                model.put( MARK_NB_TICKET_GROUP, nGroupTickets );
                model.put( MARK_NB_TICKET_DOMAIN, strSelectedTab.equals( TabulationEnum.DOMAIN.getLabel( ) ) ? listIdTickets.size( ) : null );
                model.put( MARK_SELECTED_TAB, strSelectedTab );
                model.put( MARK_USER_WITH_NO_UNIT, bUserWithNoUnit );
                model.put( MARK_USER_FACTORY, UserFactory.getInstance( ) );
                model.put( TicketingConstants.MARK_AVATAR_AVAILABLE, _bAvatarAvailable );
                model.put( MARK_MANAGE_PAGE_TITLE, ( _bSearchMode ?
                        I18nService.getLocalizedString( PROPERTY_PAGE_SEARCH_TILE, getLocale( ) ) :
                            I18nService.getLocalizedString( PROPERTY_PAGE_MANAGE_TITLE, getLocale( ) ) ) );
                model.put( TicketingConstants.MARK_JSP_CONTROLLER, getControllerJsp( ) );
                TicketFilterHelper.setModel( model, filter, request, userCurrent );
                ModelUtils.storeTicketRights( model, userCurrent );

                String strCreationDateDisplay = AdminUserPreferencesService.instance( ).get( String.valueOf( userCurrent.getUserId( ) ), TicketingConstants.USER_PREFERENCE_CREATION_DATE_DISPLAY,
                        StringUtils.EMPTY );
                model.put( TicketingConstants.MARK_CREATION_DATE_AS_DATE, TicketingConstants.USER_PREFERENCE_CREATION_DATE_DISPLAY_DATE.equals( strCreationDateDisplay ) );
                model.put( MARK_MASS_ACTIONS_LIST, getListMassActions( userCurrent, filter ) );

                String messageInfo = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO );

                if ( StringUtils.isNotEmpty( messageInfo ) )
                {
                    addInfo( messageInfo );
                    fillCommons( model );
                }

                return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETS, TEMPLATE_MANAGE_TICKETS, model );
    }

    /**
     * returns current selectedTab
     *
     * @param request
     *         http request
     * @return selectedTab
     */
    private String getSelectedTab( HttpServletRequest request )
    {
        String strSelectedTab = TabulationEnum.AGENT.getLabel( );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_SELECTED_TAB ) ) )
        {
            strSelectedTab = request.getParameter( PARAMETER_SELECTED_TAB );
            request.getSession( ).setAttribute( PARAMETER_SELECTED_TAB, strSelectedTab );
        } else
        {
            if ( StringUtils.isNotEmpty( ( String ) request.getSession( ).getAttribute( PARAMETER_SELECTED_TAB ) ) )
            {
                strSelectedTab = ( String ) request.getSession( ).getAttribute( PARAMETER_SELECTED_TAB );
            }
        }

        return strSelectedTab;
    }

    /**
     * Returns the form to create a ticket
     *
     * @param request
     *         The Http request
     * @return the html code of the ticket form
     */
    @View( VIEW_CREATE_TICKET )
    public String getCreateTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        clearUploadFilesIfNeeded( request.getSession( ) );

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );

        if ( ticket == null )
        {
            ticket = new Ticket( );
        }

        Map<String, Object> model = getModel( );
        initTicketForm( request, ticket, model );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKET, TEMPLATE_CREATE_TICKET, model );
    }

    /**
     * ticket initialisation method
     *
     * @param request
     *         request
     * @param ticket
     *         ticket
     * @param model
     *         model
     */
    private void initTicketForm( HttpServletRequest request, Ticket ticket, Map<String, Object> model )
    {
        String strGuid = request.getParameter( TicketingConstants.PARAMETER_GUID );
        String strIdCustomer = request.getParameter( TicketingConstants.PARAMETER_CUSTOMER_ID );
        String strIdUserTitle = request.getParameter( PARAMETER_USER_TITLE );
        String strFirstname = request.getParameter( PARAMETER_FIRSTNAME );
        String strLastname = request.getParameter( PARAMETER_LASTNAME );
        String strFamilyname = request.getParameter( PARAMETER_FAMILYNAME );
        String strFixedPhoneNumber = request.getParameter( PARAMETER_FIXED_PHONE );
        String strMobilePhoneNumber = request.getParameter( PARAMETER_MOBILE_PHONE );
        String strEmail = request.getParameter( PARAMETER_EMAIL );
        String strCategoryCode = request.getParameter( PARAMETER_CATEGORY );
        String strNomenclature = request.getParameter( PARAMETER_NOMENCLATURE );

        if ( StringUtils.isEmpty( strLastname ) && StringUtils.isNotEmpty( strFamilyname ) )
        {
            strLastname = strFamilyname;
        }
        ticket.enrich( strIdUserTitle, strFirstname, strLastname, strFixedPhoneNumber, strMobilePhoneNumber, strEmail, strCategoryCode, null, null, null, strGuid, strIdCustomer, strNomenclature );

        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale( ) ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getDepths( ) );

        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList( request.getLocale( ) ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_GUID, strGuid );
        ModelUtils.storeChannels( request, model );
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request
     *         The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKET )
    public String doCreateTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        try
        {
            Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
            TicketHome.create( ticket );

            if ( ( ticket.getListResponse( ) != null ) && !ticket.getListResponse( ).isEmpty( ) )
            {
                for ( Response response : ticket.getListResponse( ) )
                {
                    ResponseHome.create( response );
                    TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
                }
            }
            request.setAttribute( TicketingConstants.ATTRIBUTE_BYPASS_ASSSIGN_TO_ME, true );

            doProcessNextWorkflowAction( ticket, request );

            // Immediate indexation of the Ticket
            immediateTicketIndexing( ticket.getId( ) );

            _ticketFormService.removeTicketFromSession( request.getSession( ) );
            return redirectAfterCreateAction( request );
        } catch ( Exception e )
        {
            addError( TicketingConstants.ERROR_TICKET_CREATION_ABORTED, getLocale( ) );
            AppLogService.error( e );

            return redirectView( request, VIEW_TICKET_PAGE );
        }
    }

    /**
     * Process the data capture form of a new ticket and assign it to the agent
     *
     * @param request
     *         The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_ASSIGN_TICKET )
    public String doCreateAssignTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        try
        {
            Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
            // Check user rights on domain
            if ( !RBACService.isAuthorized( TicketCategoryService.getInstance( ).getTicketCategoryRBACResource( ticket.getTicketCategory( ) ), TicketCategory.PERMISSION_VIEW_DETAIL, getUser( ) ) )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
            }

            TicketHome.create( ticket );

            if ( ( ticket.getListResponse( ) != null ) && !ticket.getListResponse( ).isEmpty( ) )
            {
                for ( Response response : ticket.getListResponse( ) )
                {
                    ResponseHome.create( response );
                    TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
                }
            }
            request.setAttribute( TicketingConstants.ATTRIBUTE_BYPASS_ASSSIGN_TO_ME, false );

            doProcessNextWorkflowAction( ticket, request );

            // Immediate indexation of the Ticket
            immediateTicketIndexing( ticket.getId( ) );

            _ticketFormService.removeTicketFromSession( request.getSession( ) );

            return redirectToViewDetails( request, ticket );
        } catch ( Exception e )
        {
            addError( TicketingConstants.ERROR_TICKET_CREATION_ABORTED, getLocale( ) );
            AppLogService.error( e );

            return redirectView( request, VIEW_TICKET_PAGE );
        }
    }

    /**
     * Computes redirection for creation action
     *
     * @param request
     *         http request
     * @return redirect view
     */
    public String redirectAfterCreateAction( HttpServletRequest request )
    {
        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }

        addInfo( INFO_TICKET_CREATED, getLocale( ) );

        return redirectView( request, VIEW_TICKET_PAGE );
    }

    /**
     * Manages the removal form of a ticket whose identifier is in the http request
     *
     * @param request
     *         The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKET )
    public String getConfirmRemoveTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_DELETE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        int nId = Integer.parseInt( request.getParameter( TicketingConstants.PARAMETER_ID_TICKET ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKET ) );
        url.addParameter( TicketingConstants.PARAMETER_ID_TICKET, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKET, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a ticket
     *
     * @param request
     *         The Http request
     * @return the jsp URL to display the form to manage tickets
     */
    @Action( ACTION_REMOVE_TICKET )
    public String doRemoveTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_DELETE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        int nId = Integer.parseInt( request.getParameter( TicketingConstants.PARAMETER_ID_TICKET ) );

        doRemoveWorkFlowResource( nId );

        IndexerActionHome.removeByIdTicket( nId );

        TicketHome.remove( nId );
        immediateRemoveTicketFromIndex( nId );
        addInfo( INFO_TICKET_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_TICKET_PAGE );
    }

    /**
     * Returns the form to update info about a ticket
     *
     * @param request
     *         The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKET )
    public String getModifyTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        int nId = Integer.parseInt( request.getParameter( TicketingConstants.PARAMETER_ID_TICKET ) );

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );

        if ( ticket == null )
        {
            ticket = TicketHome.findByPrimaryKey( nId );
        } else
        {
            ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
        }

        Map<String, Object> model = getModel( );
        initTicketForm( request, ticket, model );

        _ticketFormService.saveTicketInSession( request.getSession( ), ticket );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKET, TEMPLATE_MODIFY_TICKET, model );
    }

    /**
     * Process the change form of a ticket
     *
     * @param request
     *         The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKET )
    public String doModifyTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        int nId = Integer.parseInt( request.getParameter( TicketingConstants.PARAMETER_ID_TICKET ) );

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );

        if ( ticket == null )
        {
            ticket = TicketHome.findByPrimaryKey( nId );
        }

        boolean bIsFormValid = populateAndValidateModificationFormTicket( ticket, request );

        if ( !bIsFormValid )
        {
            return redirect( request, VIEW_MODIFY_TICKET, TicketingConstants.PARAMETER_ID_TICKET, ticket.getId( ) );
        }

        TicketHome.update( ticket );

        // Immediate indexation of the Ticket
        immediateTicketIndexing( ticket.getId( ) );

        String strMessage = I18nService.getLocalizedString( INFO_TICKET_UPDATED, Locale.FRENCH );
        RequestUtils.setParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_MODIFY_ACTION_MESSAGE_INFO, strMessage );

        return redirectToViewDetails( request, ticket );
    }

    /**
     * Returns the form to recapitulate a ticket to create
     *
     * @param request
     *         The Http request
     * @return the html code of the ticket form
     */
    @View( VIEW_RECAP_TICKET )
    public String getRecapTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
        List<ResponseRecap> listResponseRecap = _ticketFormService.getListResponseRecap( ticket.getListResponse( ) );

        Map<String, Object> model = getModel( );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_RESPONSE_RECAP_LIST, listResponseRecap );
        model.put( MARK_CREATE_ASSIGN_RIGHT, RBACService.isAuthorized( TicketCategoryService.getInstance( ).getTicketCategoryRBACResource( ticket.getTicketCategory( ) ), TicketCategory.PERMISSION_VIEW_DETAIL, getUser( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_RECAP_TICKET, TEMPLATE_RECAP_TICKET, model );
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request
     *         The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_RECAP_TICKET )
    public String doRecapTicket( HttpServletRequest request )
    {
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
        ticket = ( ticket != null ) ? ticket : new Ticket( );

        boolean bIsFormValid = populateAndValidateFormTicket( ticket, request );

        if ( !bIsFormValid )
        {
            _ticketFormService.saveTicketInSession( request.getSession( ), ticket );

            return redirectView( request, VIEW_CREATE_TICKET );
        } else
        {
            ticket.setContactMode( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode( ) ).getCode( ) );
            ticket.setUserTitle( UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle( ) ).getLabel( ) );
            ticket.setChannel( ChannelHome.findByPrimaryKey( ticket.getChannel( ).getId( ) ) );

            _ticketFormService.saveTicketInSession( request.getSession( ), ticket );

            return redirectView( request, VIEW_RECAP_TICKET );
        }
    }

    /**
     * Populate the ticket from the request and validate the ticket form
     *
     * @param ticket
     *         The ticket to populate
     * @param request
     *         The Http Request
     * @return true if the ticket is valid else false
     */
    public boolean populateAndValidateFormTicket( Ticket ticket, HttpServletRequest request )
    {
        boolean bIsFormValid = true;
        populate( ticket, request );

        TicketAddress ticketAddress = new TicketAddress( );
        populate( ticketAddress, request );
        ticket.setTicketAddress( ticketAddress );

        // Validate the TicketCategory
        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request ).validateTicketCategory( );
        if ( !categoryValidatorResult.isTicketCategoryValid( ) )
        {
            categoryValidatorResult.getListValidationErrors( ).stream( ).forEach( ( error ) -> addError( error ) );
            bIsFormValid = false;
        }
        else
        {
            ticket.setTicketCategory( categoryValidatorResult.getTicketCategory( ) );
        }

        // Validate the bean
        TicketValidator ticketValidator = TicketValidatorFactory.getInstance( ).create( request.getLocale( ) );
        List<String> listValidationErrors = ticketValidator.validateBean( ticket );
        for ( String error : listValidationErrors )
        {
            if ( !StringUtils.isEmpty( error ) )
            {
                addError( error );
                bIsFormValid = false;
            }
        }

        String errorModeContactFilled = new FormValidator( request ).isContactModeFilled( );
        if ( errorModeContactFilled != null )
        {
            addError( errorModeContactFilled );
            bIsFormValid = false;
        }

        // The validation for the ticket comment size is made here because the validation doesn't work for this field
        // Check constraints
        // Count the number of characters in the ticket comment
        int iNbCharcount = FormValidator.countCharTicketComment( ticket.getTicketComment( ) );
        if ( iNbCharcount > 5000 )
        {
            addError( MESSAGE_ERROR_COMMENT_VALIDATION, getLocale( ) );
            bIsFormValid = false;
        }

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );

        if ( categoryValidatorResult.isTicketCategoryValid( ) )
        {
            ticket.setListResponse( null );

            List<Entry> listEntry = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), null );

            for ( Entry entry : listEntry )
            {
                listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), getLocale( ), ticket ) );
            }
        }

        if ( listFormErrors.size( ) > 0 )
        {
            bIsFormValid = false;
        }

        return bIsFormValid;
    }

    /**
     * Populate the ticket from the request and validate the ticket form for modification
     *
     * @param ticket
     *         The ticket to populate
     * @param request
     *         The Http Request
     * @return true if the ticket is valid else false
     */
    public boolean populateAndValidateModificationFormTicket( Ticket ticket, HttpServletRequest request )
    {
        boolean bIsFormValid = true;
        populate( ticket, request );

        TicketAddress ticketAddress = new TicketAddress( );
        populate( ticketAddress, request );
        ticket.setTicketAddress( ticketAddress );

        TicketValidator ticketValidator = TicketValidatorFactory.getInstance( ).create( request.getLocale( ) );
        List<String> listValidationErrors = ticketValidator.validateBean( ticket );
        for ( String error : listValidationErrors )
        {
            if ( !StringUtils.isEmpty( error ) )
            {
                addError( error );
                bIsFormValid = false;
            }
        }

        String errorModeContactFilled = new FormValidator( request ).isContactModeFilled( );
        if ( errorModeContactFilled != null )
        {
            addError( errorModeContactFilled );
            bIsFormValid = false;
        }

        // The validation for the ticket comment size is made here because the validation doesn't work for this field
        // Check constraints
        // Count the number of characters in the ticket comment
        int iNbCharcount = FormValidator.countCharTicketComment( ticket.getTicketComment( ) );
        if ( iNbCharcount > 5000 )
        {
            addError( MESSAGE_ERROR_COMMENT_VALIDATION, getLocale( ) );
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
        return redirectView( request, VIEW_TICKET_PAGE );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String redirectWorkflowActionCancelled( HttpServletRequest request )
    {
        return redirectView( request, VIEW_TICKET_PAGE );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String defaultRedirectWorkflowAction( HttpServletRequest request )
    {
        return redirectView( request, VIEW_TICKET_PAGE );
    }

    /**
     * Redirect to the view details for the ticket passed in parameter
     *
     * @param request
     *         The Http request
     * @param ticket
     *         The ticket
     * @return The Jsp URL of the process result
     */
    public String redirectToViewDetails( HttpServletRequest request, Ticket ticket )
    {
        UrlItem url = new UrlItem( TicketingConstants.JSP_VIEW_TICKET );
        url.addParameter( TicketingConstants.PARAMETER_ID_TICKET, ticket.getId( ) );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Clear uploaded files if needed.
     *
     * @param session
     *         The session of the current user
     */
    private void clearUploadFilesIfNeeded( HttpSession session )
    {
        // If we do not reload an appointment, we clear uploaded files.
        if ( ( _ticketFormService.getTicketFromSession( session ) == null ) && ( _ticketFormService.getValidatedTicketFromSession( session ) == null ) )
        {
            TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( session.getId( ) );
        }
    }

    /**
     * Return the number of tickets from Lucene index with filter
     *
     * @param strQuery
     * @param listTicketDomain
     * @param filter
     * @return
     */
    private int getNbTicketsWithLucene( String strQuery, List<TicketCategory> listTicketDomain, TicketFilter filter )
    {
        try
        {
            return _engine.searchCountTickets( strQuery, listTicketDomain, filter );
        } catch ( ParseException e )
        {
            AppLogService.error( "Error while parsing query " + strQuery, e );
            addError( SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
        }
        return 0;
    }

    /**
     * Add the list of ticket domain associated to the given permission
     *
     * @param mapIntegerTicketDomain
     * @param permission
     */
    private void addTicketDomainToMapFromPermission( Map<Integer, TicketCategory> mapIntegerTicketDomain, String permission )
    {
        List<TicketCategory> listDomain = TicketCategoryService.getInstance( true ).getAuthorizedDomainsList( getUser( ), permission );

        if ( mapIntegerTicketDomain != null && listDomain != null && !listDomain.isEmpty( ) )
        {
            for ( TicketCategory ticketDomain : listDomain )
            {
                mapIntegerTicketDomain.put( ticketDomain.getId( ), ticketDomain );
            }
        }
    }

    /**
     * Set the ticket list in session and get the workflow action form before processing a mass action.
     *
     * @param request
     *         The request
     * @return The HTML content to display, or the next URL to redirect the user to
     */
    @View( TicketingConstants.VIEW_WORKFLOW_MASS_ACTION_FORM )
    public String getWorkflowMassActionForm( HttpServletRequest request )
    {
        request.getSession( ).setAttribute( TicketingConstants.PARAMETER_SELECTED_TICKETS, request.getParameterValues( TicketingConstants.PARAMETER_ID_TICKET ) );
        request.getSession( ).setAttribute( TicketingConstants.PARAMETER_IS_MASS_ACTION, true );
        return getWorkflowActionForm( request );
    }

    @Override
    protected boolean checkAccessToTicket( HttpServletRequest request, Ticket ticket )
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        List<Unit> unitsList = UnitHome.findByIdUser( user.getUserId( ) );

        // Check if user is in same unit tree as unit assigned to ticket
        Set<Integer> subUnits = unitsList.stream( ).map( unit -> unit.getIdUnit( ) ).collect( Collectors.toSet( ) );
        unitsList.stream( ).forEach( unit -> subUnits.addAll( UnitHome.getAllSubUnitsId( unit.getIdUnit( ) ) ) );
        boolean ticketInSubUnit = true;
        if ( ticket.getAssigneeUnit( ) != null )
        {
            ticketInSubUnit = subUnits.stream( ).anyMatch( unitId -> ticket.getAssigneeUnit( ).getUnitId( ) == unitId );
        }

        return ticketInSubUnit;
    }
}
