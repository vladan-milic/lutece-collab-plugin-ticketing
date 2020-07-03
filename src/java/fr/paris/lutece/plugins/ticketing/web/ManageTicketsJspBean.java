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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.BooleanUtils;
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
import fr.paris.lutece.plugins.ticketing.business.file.TicketFileHome;
import fr.paris.lutece.plugins.ticketing.business.marking.Marking;
import fr.paris.lutece.plugins.ticketing.business.marking.MarkingHome;
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
import fr.paris.lutece.plugins.ticketing.web.util.CSVUtils;
import fr.paris.lutece.plugins.ticketing.web.util.FormValidator;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.ResponseRecap;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidatorResult;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidatorFactory;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableJspBean;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.prefs.AdminUserPreferencesService;
import fr.paris.lutece.portal.service.prefs.IUserPreferencesService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

/*
 * ManageTickets JSP Bean abstract class for JSP Bean
 */

/**
 * This class provides the user interface to manage Ticket features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTickets.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class ManageTicketsJspBean extends WorkflowCapableJspBean
{
    // Right
    public static final String             RIGHT_MANAGETICKETS                  = "TICKETING_TICKETS_MANAGEMENT";
    private static final long              serialVersionUID                     = 1L;

    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String            TEMPLATE_MANAGE_TICKETS              = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "manage_tickets.html";
    private static final String            TEMPLATE_CREATE_TICKET               = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "create_ticket.html";
    private static final String            TEMPLATE_MODIFY_TICKET               = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "modify_ticket.html";
    private static final String            TEMPLATE_RECAP_TICKET                = TicketingConstants.TEMPLATE_ADMIN_TICKET_FEATURE_PATH + "recap_ticket.html";

    // Parameters
    private static final String            PARAMETER_USER_TITLE                 = "ut";
    private static final String            PARAMETER_FIRSTNAME                  = "fn";
    private static final String            PARAMETER_FAMILYNAME                 = "fan";
    private static final String            PARAMETER_LASTNAME                   = "ln";
    private static final String            PARAMETER_FIXED_PHONE                = "fph";
    private static final String            PARAMETER_MOBILE_PHONE               = "mph";
    private static final String            PARAMETER_EMAIL                      = "em";
    private static final String            PARAMETER_CATEGORY                   = "cat";
    private static final String            PARAMETER_PAGE_INDEX                 = "page_index";
    private static final String            PARAMETER_SELECTED_TAB               = "selected_tab";
    private static final String            PARAMETER_NOMENCLATURE               = "nom";

    // Properties for page titles
    private static final String            PROPERTY_PAGE_MANAGE_TITLE           = "ticketing.manage_tickets.title";
    private static final String            PROPERTY_PAGE_SEARCH_TILE            = "ticketing.search_ticket.pageTitle";
    private static final String            PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE  = "ticketing.listItems.itemsPerPage";
    private static final String            PROPERTY_PAGE_TITLE_MANAGE_TICKETS   = "ticketing.manage_tickets.pageTitle";
    private static final String            PROPERTY_PAGE_TITLE_MODIFY_TICKET    = "ticketing.modify_ticket.pageTitle";
    private static final String            PROPERTY_PAGE_TITLE_CREATE_TICKET    = "ticketing.create_ticket.pageTitle";
    private static final String            PROPERTY_PAGE_TITLE_RECAP_TICKET     = "ticketing.recap_ticket.pageTitle";

    // Markers
    private static final String            MARK_TICKET_LIST                     = "ticket_list";
    private static final String            MARK_USER_FACTORY                    = "user_factory";
    private static final String            MARK_USER_TITLES_LIST                = "user_titles_list";
    private static final String            MARK_MASS_ACTIONS_LIST               = "mass_actions_list";
    private static final String            MARK_NB_TICKET_AGENT                 = "nb_ticket_agent";
    private static final String            MARK_NB_TICKET_GROUP                 = "nb_ticket_group";
    private static final String            MARK_NB_TICKET_DOMAIN                = "nb_ticket_domain";
    private static final String            MARK_CONTACT_MODES_LIST              = "contact_modes_list";
    private static final String            MARK_GUID                            = "guid";
    private static final String            MARK_RESPONSE_RECAP_LIST             = "response_recap_list";
    private static final String            MARK_PAGINATOR                       = "paginator";
    private static final String            MARK_NB_ITEMS_PER_PAGE               = "nb_items_per_page";
    private static final String            MARK_SELECTED_TAB                    = "selected_tab";
    private static final String            MARK_USER_WITH_NO_UNIT               = "user_with_no_unit";
    private static final String            JSP_MANAGE_TICKETS                   = TicketingConstants.ADMIN_CONTROLLLER_PATH + "ManageTickets.jsp";
    private static final String            MARK_MANAGE_PAGE_TITLE               = "manage_ticket_page_title";
    private static final String            MARK_CREATE_ASSIGN_RIGHT             = "create_assign_right";
    private static final String            MARK_WARNING_COUNT                   = "warning_count";

    // Properties
    private static final String            MESSAGE_CONFIRM_REMOVE_TICKET        = "ticketing.message.confirmRemoveTicket";
    private static final String            MESSAGE_ERROR_COMMENT_VALIDATION     = "ticketing.validation.ticket.TicketComment.size";
    private static final String            MESSAGE_ERROR_FACIL_EMPTY_VALIDATION = "ticketing.validation.ticket.TicketFacilNumber.size";
    private static final String            MESSAGE_ERROR_FACIL_REGEX_VALIDATION = "ticketing.validation.ticket.TicketFacilNumber.regex";
    /**
     * Domaine facil'famille
     */
    private static final String            PROPERTY_ACCOUNT_NUMBER_DOMAIN_LABEL = "module.workflow.ticketingfacilfamilles.workflow.automatic_assignment.domainLabel";
    /**
     * facil'famille account number check
     */
    private static final String            PROPERTY_ACCOUNT_NUMBER_REGEXP       = "module.workflow.ticketingfacilfamilles.workflow.automatic_assignment.accountNumberRegexp";

    // Views
    private static final String            VIEW_MANAGE_TICKETS                  = "manageTickets";
    private static final String            VIEW_TICKET_PAGE                     = "ticketPage";
    private static final String            VIEW_CREATE_TICKET                   = "createTicket";
    private static final String            VIEW_MODIFY_TICKET                   = "modifyTicket";
    private static final String            VIEW_RECAP_TICKET                    = "recapTicket";

    // Actions
    private static final String            ACTION_CREATE_TICKET                 = "createTicket";
    private static final String            ACTION_CREATE_ASSIGN_TICKET          = "createAssignTicket";
    private static final String            ACTION_MODIFY_TICKET                 = "modifyTicket";
    private static final String            ACTION_REMOVE_TICKET                 = "removeTicket";
    private static final String            ACTION_CONFIRM_REMOVE_TICKET         = "confirmRemoveTicket";
    private static final String            ACTION_RECAP_TICKET                  = "recapTicket";
    private static final String            ACTION_EXPORT_TICKET                 = "exportTicket";

    // Infos
    private static final String            INFO_TICKET_CREATED                  = "ticketing.info.ticket.created";
    private static final String            INFO_TICKET_UPDATED                  = "ticketing.info.ticket.updated";
    private static final String            INFO_TICKET_REMOVED                  = "ticketing.info.ticket.removed";

    // Other constants
    private static boolean                 _bAvatarAvailable                    = ( PluginService.getPlugin( TicketingConstants.PLUGIN_AVATAR ) != null );
    private static final String            CONTENT_TYPE_CSV                     = "application/csv";
    private static final String            DISPALL_ALL_SOLLICITATION_COLUMN     = "displayAllColumn";

    // Variables
    private String                         _strCurrentPageIndex;
    private int                            _nItemsPerPage;
    private final TicketFormService        _ticketFormService                   = SpringContextService.getBean( TicketFormService.BEAN_NAME );
    private final TicketSearchEngine       _engine                              = ( TicketSearchEngine ) SpringContextService.getBean( SearchConstants.BEAN_SEARCH_ENGINE );
    private static IUserPreferencesService _userPreferencesService              = AdminUserPreferencesService.instance( );

    // Header export
    private static final String            HEADER_REFERENCE                     = "ticketing.export.header.reference";
    private static final String            HEADER_GUID                          = "ticketing.export.header.guid";
    private static final String            HEADER_CREATION_DATE                 = "ticketing.export.header.creation.date";
    private static final String            HEADER_TIME_CREATION                 = "ticketing.export.header.time.creation";
    private static final String            HEADER_OBJECT_SOLICITATION           = "ticketing.export.header.object.solicitation";
    private static final String            HEADER_STATUS                        = "ticketing.export.header.status";
    private static final String            HEADER_NOMENCLATURE                  = "ticketing.export.header.nomenclature";
    private static final String            HEADER_CHANNEL                       = "ticketing.export.header.channel";
    private static final String            HEADER_ASSIGNEMENT_ENTITY            = "ticketing.export.header.assignement.entity";
    private static final String            HEADER_ASSIGNEMENT_OFFICER           = "ticketing.export.header.assignement.officer";
    private static final String            HEADER_FINAL_RESPONSE_DATE           = "ticketing.export.header.final.response.date";
    private static final String            HEADER_NUM_FACIL_FAMILLE             = "ticketing.export.header.numeroFacilFamille";
    private static final String            HEADER_FINAL_RESPONSE_MESSAGE        = "ticketing.export.header.final.response.message";

    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
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
     *            The HTTP request
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
        String strQuery = ( String ) request.getSession( ).getAttribute( SearchConstants.PARAMETER_QUERY );

        if ( StringUtils.isBlank( strQuery ) )
        {
            strQuery = StringUtils.EMPTY;
        }

        try
        {
            List<Ticket> listTickets = _engine.searchTickets( strQuery, getUser( ), filter );
            return generateDownloadCsvFile( listTickets, request );

        } catch ( ParseException e )
        {
            AppLogService.error( "Error while parsing query " + strQuery, e );
            addError( SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_TICKETS );
        }

    }

    /**
     * Generate and download csv export file.
     * @param listTicketsToExport
     *         tickets to export
     * @param request
     *          The HTTP request
     * @return csv file.
     */
    private String generateDownloadCsvFile( List<Ticket> listTicketsToExport, HttpServletRequest request )
    {

        SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
        SimpleDateFormat sdf2 = new SimpleDateFormat( "HH:mm" );

        boolean bExportGuid = AppPropertiesService.getPropertyBoolean( TicketingConstants.PROPERTY_EXPORT_CSV_GUID, false );

        try
        {
            File tempFile = File.createTempFile( "ticketing", null );

            List<String> titlesUntranslated = new ArrayList<>( );
            if ( bExportGuid )
            {
                titlesUntranslated.add( HEADER_GUID );
            }
            titlesUntranslated.add( HEADER_REFERENCE );
            titlesUntranslated.add( HEADER_CREATION_DATE );
            titlesUntranslated.add( HEADER_TIME_CREATION );
            // Insert here category types after being translated
            Integer indexWhereCategoryWillBeAdded = 2;
            titlesUntranslated.add( HEADER_OBJECT_SOLICITATION );
            titlesUntranslated.add( HEADER_NUM_FACIL_FAMILLE );
            titlesUntranslated.add( HEADER_STATUS );
            titlesUntranslated.add( HEADER_NOMENCLATURE );
            titlesUntranslated.add( HEADER_CHANNEL );
            titlesUntranslated.add( HEADER_ASSIGNEMENT_ENTITY );
            titlesUntranslated.add( HEADER_ASSIGNEMENT_OFFICER );
            titlesUntranslated.add( HEADER_FINAL_RESPONSE_DATE );
            titlesUntranslated.add( HEADER_FINAL_RESPONSE_MESSAGE );

            List<String> titlesTranslated = titlesUntranslated.stream( ).map( title -> I18nService.getLocalizedString( title, Locale.FRENCH ) ).collect( Collectors.toList( ) );

            List<TicketCategoryType> categoryTypesList = TicketCategoryTypeHome.getCategoryTypesList( );
            for ( TicketCategoryType category : categoryTypesList )
            {
                titlesTranslated.add( indexWhereCategoryWillBeAdded + category.getDepthNumber( ), category.getLabel( ) );
            }

            // Write header in the temp file
            Writer w = new OutputStreamWriter( new FileOutputStream( tempFile ), StandardCharsets.ISO_8859_1 );

            // Write line in the temp file
            CSVUtils.writeLine( w, titlesTranslated );

            for ( Ticket ticket : listTicketsToExport )
            {
                //Write data
                List<String> line = writelineData( ticket, categoryTypesList, bExportGuid, sdf, sdf2 );
                CSVUtils.writeLine( w, line );

            }
            w.flush( );
            w.close( );
            // Send the the content of the file to the user
            download( Files.readAllBytes( tempFile.toPath( ) ), "sollicitations.csv", CONTENT_TYPE_CSV );
        } catch ( IOException e )
        {
            AppLogService.error( "Error while creating temporary file ", e );
            return redirectView( request, VIEW_MANAGE_TICKETS );
        }

        return null;
    }

    /**
     * Write csv data line.
     * @param ticket
     *         ticket to write
     * @param categoryTypesList
     *         list category type
     * @param bExportGuid
     *         boolean export guid true to export
     * @param sdfDate
     *         date format
     * @param sdfHeure
     *         hour format
     * @return csv line
     *
     */
    private List<String> writelineData ( Ticket ticket, List<TicketCategoryType> categoryTypesList,  boolean bExportGuid, SimpleDateFormat sdfDate, SimpleDateFormat sdfHour ) {

        // Data line
        List<String> line = new ArrayList<>( );
        if ( bExportGuid )
        {
            line.add( ticket.getGuid( ) );
        }
        line.add( ticket.getReference( ) );
        line.add( sdfDate.format( ticket.getDateCreate( ) ) );
        line.add( sdfHour.format( ticket.getDateCreate( ) ) );
        for ( TicketCategory category : ticket.getBranch( ) )
        {
            line.add( category.getLabel( ) );
        }
        for ( int index = 0; index < ( categoryTypesList.size( ) - ticket.getBranch( ).size( ) ); index++ )
        {
            line.add( "" );
        }
        line.add( ticket.getTicketComment( ) );
        line.add( ticket.getFacilFamilleNumber( ) != null ? "=\"" + ticket.getFacilFamilleNumber( ) + "\"" : "" );
        line.add( ticket.getState( ).getName( ) );
        line.add( ticket.getNomenclature( ) );
        line.add( ticket.getChannel( ) != null ? ticket.getChannel( ).getLabel( ) : "" );
        line.add( ticket.getAssigneeUnit( ) != null ? ticket.getAssigneeUnit( ).getName( ) : "" );
        line.add( ticket.getAssigneeUser( ) != null ? ticket.getAssigneeUser( ).getFirstname( ) + " " + ticket.getAssigneeUser( ).getLastname( ) : "" );
        if ( ticket.getTicketStatus( ) == TicketingConstants.TICKET_STATUS_CLOSED )
        {
            line.add( ticket.getDateClose( ) != null ? sdfDate.format( ticket.getDateClose( ) ) : "" );
            line.add( ticket.getUserMessage( ) != null ? ticket.getUserMessage( ) : "" );
        } else
        {
            line.add( "" );
            line.add( "" );
        }

        return line;

    }

    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
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
        String strQuery = ( String ) request.getSession( ).getAttribute( SearchConstants.PARAMETER_QUERY );
        String strQueryParam = request.getParameter( SearchConstants.PARAMETER_QUERY );
        strQuery = strQueryParam;
        request.getSession( ).setAttribute( SearchConstants.PARAMETER_QUERY, strQueryParam );

        boolean _bSearchMode = StringUtils.isNotBlank( strQuery ) ;
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
        boolean bUserWithNoUnit = ( filter.getFilterIdAssigneeUnit( ) == null ) || filter.getFilterIdAssigneeUnit( ).isEmpty( ) || ( filter.getFilterIdAssignerUnit( ) == null )
                || filter.getFilterIdAssignerUnit( ).isEmpty( );

        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        if (_userPreferencesService.existsKey( String.valueOf( userCurrent.getUserId( ) ), TicketingConstants.USER_PREFERENCE_NB_ITEMS_PER_PAGE )) {
            _nDefaultItemsPerPage = _userPreferencesService.getInt( String.valueOf( userCurrent.getUserId( ) ), TicketingConstants.USER_PREFERENCE_NB_ITEMS_PER_PAGE, _nDefaultItemsPerPage );
            _nItemsPerPage = _nDefaultItemsPerPage;
        }
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETS );
        if ( StringUtils.isNotBlank( strQuery ) )
        {
            url.addParameter( SearchConstants.PARAMETER_QUERY, strQuery );
        }
        String strUrl = url.getUrl( );

        String strPreviousSelectedTab = ( request.getSession( ).getAttribute( PARAMETER_SELECTED_TAB ) != null ) ? ( String ) request.getSession( ).getAttribute( PARAMETER_SELECTED_TAB )
                : TabulationEnum.AGENT.getLabel( );
        String strSelectedTab = getSelectedTab( request );

        if ( !strPreviousSelectedTab.equals( strSelectedTab ) || StringUtils.isEmpty( _strCurrentPageIndex ) )
        {
            // tab changes we reset index
            _strCurrentPageIndex = "1";
        }
        int nCurrentPageIndex = Integer.parseInt( _strCurrentPageIndex );

        List<Ticket> listTickets = new ArrayList<>( );
        List<Integer> listIdTickets = new ArrayList<>( );
        long warningCount = -1;
        Calendar warningDate = null;
        // Count warning tickets
        String warningPreference = _userPreferencesService.get( String.valueOf( userCurrent.getUserId( ) ), TicketingConstants.USER_PREFERENCE_WARNING_DAYS, StringUtils.EMPTY );
        if ( StringUtils.isNotEmpty( warningPreference ) )
        {
            warningDate = Calendar.getInstance( );
            warningDate.add( Calendar.DATE, -Integer.parseInt( warningPreference ) );
        }

        // Set the limit for the number of ticket to display
        filter.setTicketsLimitStart( ( nCurrentPageIndex - 1 ) * _nItemsPerPage );
        filter.setTicketsLimitCount( _nItemsPerPage );

        // The number of tickets for AGENT and GROUP category
        int nAgentTickets = 0;
        int nGroupTickets = 0;
        int nDomainTickets = 0;

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
            if ( TicketFilterViewEnum.WARNING.equals( filter.getFilterView( ) ) && StringUtils.isNotEmpty( warningPreference ) )
            {
                filter.setCreationStartDate( null );
                filter.setCreationEndDate( warningDate != null ? warningDate.getTime( ) : null );
                filter.setListIdWorkflowState( Arrays.asList( 303, 305, 307 ) );
            }

            listTickets = _engine.searchTickets( strQuery, getUser( ), filter );

            // O2T 76319, remise en place des id ticket
            if ( ( listTickets != null ) && !listTickets.isEmpty( ) )
            {
                for ( Ticket ticket : listTickets )
                {
                    listIdTickets.add( ticket.getId( ) );
                }
            }

            filter.setFilterView( TicketFilterViewEnum.AGENT );
            nAgentTickets = getNbTicketsWithLucene( strQuery, filter );
            filter.setFilterView( TicketFilterViewEnum.GROUP );
            nGroupTickets = getNbTicketsWithLucene( strQuery, filter );
            filter.setFilterView( TicketFilterViewEnum.DOMAIN );
            nDomainTickets = getNbTicketsWithLucene( strQuery, filter );

            // Count warning tickets
            if ( StringUtils.isNotEmpty( warningPreference ) )
            {
                TicketFilter filterWarning = new TicketFilter( );
                TicketFilterHelper.setFilterUserAndUnitIds( filterWarning, userCurrent );
                filterWarning.setFilterView( TicketFilterViewEnum.WARNING );
                filterWarning.setCreationStartDate( null );
                filterWarning.setCreationEndDate( warningDate != null ? warningDate.getTime( ) : null );
                filterWarning.setListIdWorkflowState( Arrays.asList( 303, 305, 307 ) );
                warningCount = getNbTicketsWithLucene( strQuery, filterWarning );
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
        LocalizedPaginator<Ticket> paginatorTickets = new LocalizedPaginator<>( listTickets, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( SearchConstants.MARK_QUERY, StringEscapeUtils.escapeHtml( strQuery ) );
        model.put( MARK_TICKET_LIST, paginatorTickets.getPageItems( ) );
        model.put( MARK_PAGINATOR, paginatorTickets );
        model.put( MARK_NB_TICKET_AGENT, nAgentTickets );
        model.put( MARK_NB_TICKET_GROUP, nGroupTickets );
        model.put( MARK_NB_TICKET_DOMAIN, nDomainTickets );
        model.put( MARK_WARNING_COUNT, warningCount );
        model.put( MARK_SELECTED_TAB, strSelectedTab );
        model.put( MARK_USER_WITH_NO_UNIT, bUserWithNoUnit );
        model.put( MARK_USER_FACTORY, UserFactory.getInstance( ) );
        model.put( TicketingConstants.MARK_AVATAR_AVAILABLE, _bAvatarAvailable );
        model.put( MARK_MANAGE_PAGE_TITLE,
                ( _bSearchMode ? I18nService.getLocalizedString( PROPERTY_PAGE_SEARCH_TILE, getLocale( ) ) : I18nService.getLocalizedString( PROPERTY_PAGE_MANAGE_TITLE, getLocale( ) ) ) );
        model.put( TicketingConstants.MARK_JSP_CONTROLLER, getControllerJsp( ) );
        TicketFilterHelper.setModel( model, filter, userCurrent );
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

        List<Marking> markings = MarkingHome.getMarkingsList( );
        model.put( "marking_list", markings );

        if (_userPreferencesService.existsKey(  String.valueOf( userCurrent.getUserId( ) ), TicketingConstants.USER_PREFERENCE_SOLLICITATION ))
        {
            String strTabSollicitation = _userPreferencesService.get( String.valueOf( userCurrent.getUserId( ) ) , TicketingConstants.USER_PREFERENCE_SOLLICITATION, StringUtils.EMPTY );
            model.put( TicketingConstants.MARK_TAB_SOLLICITATION, strTabSollicitation );
        }
        else {
            model.put( TicketingConstants.MARK_TAB_SOLLICITATION, DISPALL_ALL_SOLLICITATION_COLUMN );
        }

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETS, TEMPLATE_MANAGE_TICKETS, model );
    }

    /**
     * returns current selectedTab
     *
     * @param request
     *            http request
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
     *            The Http request
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

            // O2T 79721 delete previous values
            Enumeration<String> attributeNames = request.getSession( ).getAttributeNames( );
            while ( attributeNames.hasMoreElements( ) )
            {
                String strAttributeName = attributeNames.nextElement( );
                if ( ( strAttributeName != null ) && strAttributeName.startsWith( "attribute" ) )
                {
                    request.getSession( ).removeAttribute( strAttributeName );
                }
            }
        }

        Map<String, Object> model = getModel( );
        initTicketForm( request, ticket, model );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKET, TEMPLATE_CREATE_TICKET, model );
    }

    /**
     * ticket initialisation method
     *
     * @param request
     *            request
     * @param ticket
     *            ticket
     * @param model
     *            model
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
     *            The Http Request
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
                    if ( response.getFile( ) != null )
                    {
                        TicketFileHome.migrateToBlob( response.getFile( ) );
                    }
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
     *            The Http Request
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
                    if ( response.getFile( ) != null )
                    {
                        TicketFileHome.migrateToBlob( response.getFile( ) );
                    }
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
     *            http request
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
     *            The Http request
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
     *            The Http request
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
     *            The Http request
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
     *            The Http request
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
     *            The Http request
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
        model.put( MARK_CREATE_ASSIGN_RIGHT,
                RBACService.isAuthorized( TicketCategoryService.getInstance( ).getTicketCategoryRBACResource( ticket.getTicketCategory( ) ), TicketCategory.PERMISSION_VIEW_DETAIL, getUser( ) ) );

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
        // Check user rights
        if ( !RBACService.isAuthorized( new Ticket( ), TicketResourceIdService.PERMISSION_CREATE, getUser( ) ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP ) );
        }

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
        ticket = ( ticket != null ) ? ticket : new Ticket( );

        boolean bIsFormValid = populateAndValidateFormTicket( ticket, request );

        // O2T : recuperation des attributs complementaires (dont num facil'familles)
        for ( Map.Entry entry : request.getParameterMap( ).entrySet( ) )
        {
            if ( ( entry.getKey( ) != null ) && entry.getKey( ).toString( ).startsWith( "attribute" ) )
            {
                request.getSession( ).setAttribute( String.valueOf( entry.getKey( ) ), entry.getValue( ) );
            }
        }

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
     *            The ticket to populate
     * @param request
     *            The Http Request
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
        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request, getLocale( ) ).validateTicketCategory( );
        if ( !categoryValidatorResult.isTicketCategoryValid( ) )
        {
            categoryValidatorResult.getListValidationErrors( ).stream( ).forEach( error -> addError( error ) );
            bIsFormValid = false;
        } else
        {
            ticket.setTicketCategory( categoryValidatorResult.getTicketCategory( ) );
        }

        // Validate the bean
        TicketValidator ticketValidator = TicketValidatorFactory.getInstance( ).create( getLocale( ) );
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

        String errorFirstNameFilled = new FormValidator( request ).isFirstNameFilled( );
        if ( errorFirstNameFilled != null )
        {
            addError( errorFirstNameFilled );
            bIsFormValid = false;
        }

        String errorLastNameFilled = new FormValidator( request ).isLastNameFilled( );
        if ( errorLastNameFilled != null )
        {
            addError( errorLastNameFilled );
            bIsFormValid = false;
        }

        if ( BooleanUtils.isTrue( ChannelHome.findByPrimaryKey( ticket.getChannel( ).getId( ) ).getFlagMandatoryTicketComment( ) ) )
        {
            String errorCommentFilled = new FormValidator( request ).isCommentFilled( );
            if ( errorCommentFilled != null )
            {
                addError( errorCommentFilled );
                bIsFormValid = false;
            }
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

        List<GenericAttributeError> listFormErrors = new ArrayList<>( );

        if ( categoryValidatorResult.isTicketCategoryValid( ) )
        {
            ticket.setListResponse( null );

            List<Entry> listEntry = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), null );

            for ( Entry entry : listEntry )
            {
                listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), getLocale( ), ticket ) );
            }
        }

        if ( !listFormErrors.isEmpty( ) )
        {
            // TICKETING-2217 affichage du message d'erreur
            for ( GenericAttributeError error : listFormErrors )
            {
                addError( error.getMessage( ) );
            }
            bIsFormValid = false;
        }

        return bIsFormValid;
    }

    /**
     * Populate the ticket from the request and validate the ticket form for modification
     *
     * @param ticket
     *            The ticket to populate
     * @param request
     *            The Http Request
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

        // O2T 79251, contrôle facil'famille
        if ( ( ticket.getTicketDomain( ) != null ) && ticket.getTicketDomain( ).getLabel( ).equalsIgnoreCase( AppPropertiesService.getProperty( PROPERTY_ACCOUNT_NUMBER_DOMAIN_LABEL ) ) )
        {
            if ( ( ticket.getFacilFamilleNumber( ) == null ) || ticket.getFacilFamilleNumber( ).isEmpty( ) )
            {
                // null or empty
                bIsFormValid = false;
                addError( MESSAGE_ERROR_FACIL_EMPTY_VALIDATION, getLocale( ) );
            } else if ( !ticket.getFacilFamilleNumber( ).matches( AppPropertiesService.getProperty( PROPERTY_ACCOUNT_NUMBER_REGEXP ) ) )
            {
                // does not match facil'famille regex
                bIsFormValid = false;
                addError( MESSAGE_ERROR_FACIL_REGEX_VALIDATION, getLocale( ) );
            }
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
     *            The Http request
     * @param ticket
     *            The ticket
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
     *            The session of the current user
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
     * @param filter
     * @return
     */
    private int getNbTicketsWithLucene( String strQuery, TicketFilter filter )
    {
        try
        {
            return _engine.searchCountTickets( strQuery, getUser( ), filter );
        } catch ( ParseException e )
        {
            AppLogService.error( "Error while parsing query " + strQuery, e );
            addError( SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
        }
        return 0;
    }

    /**
     * Set the ticket list in session and get the workflow action form before processing a mass action.
     *
     * @param request
     *            The request
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
    protected boolean checkAccessToTicket( Ticket ticket )
    {
        AdminUser user = getUser( );
        List<Unit> unitsList = UnitHome.findByIdUser( user.getUserId( ) );

        // Check if user is in same unit tree as unit assigned to ticket
        Set<Integer> subUnits = unitsList.stream( ).map( Unit::getIdUnit).collect( Collectors.toSet( ) );
        unitsList.stream( ).forEach( unit -> subUnits.addAll( UnitHome.getAllSubUnitsId( unit.getIdUnit( ) ) ) );
        boolean ticketInSubUnit = true;
        if ( ticket.getAssigneeUnit( ) != null )
        {
            ticketInSubUnit = subUnits.stream( ).anyMatch( unitId -> ticket.getAssigneeUnit( ).getUnitId( ) == unitId );
        }

        return ticketInSubUnit;
    }
}
