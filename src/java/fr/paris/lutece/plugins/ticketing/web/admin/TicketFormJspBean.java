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
package fr.paris.lutece.plugins.ticketing.web.admin;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.ticketing.business.ticketform.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.ticketform.TicketFormHome;
import fr.paris.lutece.plugins.ticketing.service.EntryService;
import fr.paris.lutece.plugins.ticketing.service.EntryTypeService;
import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class provides the user interface to manage TicketForm features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTicketForms.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = TicketFormJspBean.RIGHT_MANAGETICKETFORM )
public class TicketFormJspBean extends MVCAdminJspBean
{
    /**
     * Right to manage ticketing forms
     */
    public static final String RIGHT_MANAGETICKETFORM = "TICKETING_MANAGEMENT";
    private static final long serialVersionUID = 1L;

    // templates
    private static final String TEMPLATE_MANAGE_TICKETFORMS = TicketingConstants.TEMPLATE_ADMIN_TICKETFORM_FEATURE_PATH +
        "manage_ticketforms.html";
    private static final String TEMPLATE_CREATE_TICKETFORM = TicketingConstants.TEMPLATE_ADMIN_TICKETFORM_FEATURE_PATH +
        "create_ticketform.html";
    private static final String TEMPLATE_MODIFY_TICKETFORM = TicketingConstants.TEMPLATE_ADMIN_TICKETFORM_FEATURE_PATH +
        "modify_ticketform.html";
    private static final String TEMPLATE_ADVANCED_MODIFY_TICKETFORM = TicketingConstants.TEMPLATE_ADMIN_TICKETFORM_FEATURE_PATH +
        "modify_advanced_ticketform.html";
    private static final String TEMPLATE_MODIFY_TICKETFORM_GENATTR = TicketingConstants.TEMPLATE_ADMIN_TICKETFORM_FEATURE_PATH +
        "modify_form_ticketform_genattr.html";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESC = "description";
    private static final String PARAMETER_ID_CATEGORY = "idCategory";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_FORCE_RELOAD = "forceReload";
    private static final String PARAMETER_FORM_GENERICATTR = "form_genericatt";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETFORMS = "ticketing.manage_ticketforms.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETFORM = "ticketing.modify_ticketForm.titleAlterablesParameters";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKETFORM = "ticketing.create_ticketform.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETFORM_GENATTR = "ticketing.modify_ticketform.genattr.pageTitle";

    // Markers
    private static final String MARK_TICKETFORM_LIST = "ticketform_list";
    private static final String MARK_TICKETFORM = "ticketform";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ENTRY_TYPE_LIST = "entry_type_list";
    private static final String MARK_GROUP_ENTRY_LIST = "entry_group_list";
    private static final String MARK_LIST_ORDER_FIRST_LEVEL = "listOrderFirstLevel";
    private static final String MARK_LIST_WORKFLOWS = "listWorkflows";
    private static final String MARK_LOCALE = "language";
    private static final String MARK_LOCALE_TINY = "locale";
    private static final String MARK_TICKETING_RESOURCE_ENABLED = "isResourceInstalled";
    private static final String MARK_PAGE = "page";

    // Jsp
    private static final String JSP_MANAGE_TICKETFORMS = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH +
        "ManageTicketForms.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKETFORM = "ticketing.message.confirmRemoveTicketForm";
    private static final String PROPERTY_DEFAULT_LIST_TICKETFORM_PER_PAGE = "ticketing.listTicketForms.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticketform.attribute.";
    private static final String PROPERTY_MODULE_TICKETING_RESOURCE_NAME = "ticketing.moduleTicketResource.name";

    // Views
    private static final String VIEW_MANAGE_TICKETFORMS = "manageTicketForms";
    private static final String VIEW_CREATE_TICKETFORM = "createTicketForm";
    private static final String VIEW_MODIFY_TICKETFORM = "modifyTicketForm";
    private static final String VIEW_ADVANCED_MODIFY_TICKETFORM = "modifyTicketFormAdvanced";
    private static final String VIEW_MODIFY_FORM_MESSAGES = "modifyTicketFormMessages";

    // Actions
    private static final String ACTION_CREATE_TICKETFORM = "createTicketForm";
    private static final String ACTION_MODIFY_TICKETFORM = "modifyTicketForm";
    private static final String ACTION_REMOVE_TICKETFORM = "removeTicketForm";
    private static final String ACTION_CONFIRM_REMOVE_TICKETFORM = "confirmRemoveTicketForm";

    // Infos
    private static final String INFO_TICKETFORM_CREATED = "ticketing.info.ticketform.created";
    private static final String INFO_TICKETFORM_UPDATED = "ticketing.info.ticketform.updated";
    private static final String INFO_TICKETFORM_REMOVED = "ticketing.info.ticketform.removed";

    // Session variable to store working values
    private static final String SESSION_ATTRIBUTE_TICKETING_FORM = "ticketing.session.ticketForm";
    private static final String SESSION_CURRENT_PAGE_INDEX = "ticketing.session.ticketForm.currentPageIndex";
    private static final String SESSION_ITEMS_PER_PAGE = "ticketing.session.ticketForm.itemsPerPage";
    private static final String DEFAULT_CURRENT_PAGE = "1";

    // Local variables
    private final EntryService _entryService = EntryService.getService(  );
    private int _nDefaultItemsPerPage;

    /**
     * Default constructor
     */
    public TicketFormJspBean(  )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_TICKETFORM_PER_PAGE, 50 );
    }

    /**
     * Get the page to manage ticketing forms
     * @param request the request
     * @return The HTML content to display
     */
    @View( value = VIEW_MANAGE_TICKETFORMS, defaultView = true )
    public String getManageTicketForms( HttpServletRequest request )
    {
        String strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                (String) request.getSession(  ).getAttribute( SESSION_CURRENT_PAGE_INDEX ) );

        if ( strCurrentPageIndex == null )
        {
            strCurrentPageIndex = DEFAULT_CURRENT_PAGE;
        }

        request.getSession(  ).setAttribute( SESSION_CURRENT_PAGE_INDEX, strCurrentPageIndex );

        int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                getIntSessionAttribute( request.getSession(  ), SESSION_ITEMS_PER_PAGE ), _nDefaultItemsPerPage );
        request.getSession(  ).setAttribute( SESSION_ITEMS_PER_PAGE, nItemsPerPage );

        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETFORMS );
        String strUrl = url.getUrl(  );
        List<TicketForm> listTicketForms = TicketFormHome.getTicketFormsList(  );

        // PAGINATOR
        LocalizedPaginator<TicketForm> paginator = new LocalizedPaginator<TicketForm>( listTicketForms, nItemsPerPage,
                strUrl, PARAMETER_PAGE_INDEX, strCurrentPageIndex, getLocale(  ) );

        Map<String, Object> model = getModel(  );

        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( nItemsPerPage ) );
        model.put( MARK_PAGINATOR, paginator );

        model.put( MARK_TICKETFORM_LIST, paginator.getPageItems(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETFORMS, TEMPLATE_MANAGE_TICKETFORMS, model );
    }

    /**
     * Returns the form to create an ticketing form
     *
     * @param request The HTTP request
     * @return the HTML code of the ticketing form
     * @throws AccessDeniedException If the user is not authorized to create
     *             ticketing forms
     */
    @View( VIEW_CREATE_TICKETFORM )
    public String getCreateTicketForm( HttpServletRequest request )
        throws AccessDeniedException
    {
        TicketForm ticketForm = (TicketForm) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );

        if ( ( ticketForm == null ) || ( ticketForm.getIdForm(  ) > 0 ) )
        {
            ticketForm = new TicketForm(  );
            request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_TICKETING_FORM, ticketForm );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_LOCALE_TINY, getLocale(  ) );
        addElementsToModelForLeftColumn( request, ticketForm, getUser(  ), getLocale(  ), model );
        model.put( MARK_LOCALE, TicketingPlugin.getPluginLocale( getLocale(  ) ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKETFORM, TEMPLATE_CREATE_TICKETFORM, model );
    }

    /**
     * Process the data capture form of a new ticketing form
     * @param request The HTTP Request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException If the user is not authorized to create
     *             ticketing forms
     * @throws FileNotFoundException
     */
    @Action( ACTION_CREATE_TICKETFORM )
    public String doCreateTicketForm( HttpServletRequest request )
        throws AccessDeniedException, FileNotFoundException
    {
        TicketForm ticketForm = (TicketForm) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );

        if ( ticketForm == null )
        {
            ticketForm = new TicketForm(  );
        }

        populate( ticketForm, request );

        // Check constraints
        if ( !validateBean( ticketForm, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TICKETFORM );
        }

        TicketFormHome.create( ticketForm );

        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );
        addInfo( INFO_TICKETFORM_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETFORMS );
    }

    /**
     * Manages the removal form of a ticketing form whose identifier is in the
     * HTTP request
     * @param request The HTTP request
     * @return the HTML code to confirm
     * @throws AccessDeniedException If the user is not authorized to delete
     *             this ticketing form
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKETFORM )
    public String getConfirmRemoveTicketForm( HttpServletRequest request )
        throws AccessDeniedException
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKETFORM ) );
        url.addParameter( PARAMETER_ID_FORM, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKETFORM,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of an ticketing form
     * @param request The HTTP request
     * @return the JSP URL to display the form to manage ticketing forms
     * @throws AccessDeniedException If the user is not authorized to delete
     *             this ticketing form
     */
    @Action( ACTION_REMOVE_TICKETFORM )
    public String doRemoveTicketForm( HttpServletRequest request )
        throws AccessDeniedException
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );

        _entryService.removeEntriesByIdTicketForm( nId );

        TicketFormHome.remove( nId );
        addInfo( INFO_TICKETFORM_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETFORMS );
    }

    /**
     * Returns the form to update info about a ticketing form
     * @param request The HTTP request
     * @return The HTML form to update info
     * @throws AccessDeniedException If the user is not authorized to modify
     *             this ticketing form
     */
    @View( VIEW_MODIFY_TICKETFORM )
    public String getModifyTicketForm( HttpServletRequest request )
        throws AccessDeniedException
    {
        TicketForm ticketForm = (TicketForm) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );

        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        String strPage = request.getParameter( MARK_PAGE );

        if ( ( ticketForm == null ) || ( nIdForm != ticketForm.getIdForm(  ) ) ||
                Boolean.parseBoolean( request.getParameter( PARAMETER_FORCE_RELOAD ) ) )
        {
            ticketForm = TicketFormHome.findByPrimaryKey( nIdForm );
            request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_TICKETING_FORM, ticketForm );
        }

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( ticketForm.getIdForm(  ) );
        entryFilter.setResourceType( TicketForm.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( entryFilter );
        List<Entry> listEntry = new ArrayList<Entry>( listEntryFirstLevel.size(  ) );

        List<Integer> listOrderFirstLevel = new ArrayList<Integer>( listEntryFirstLevel.size(  ) );

        for ( Entry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );
            // If the entry is a group, we add entries associated with this group
            listOrderFirstLevel.add( listEntry.size(  ) );

            if ( entry.getEntryType(  ).getGroup(  ) )
            {
                entryFilter = new EntryFilter(  );
                entryFilter.setIdResource( ticketForm.getIdForm(  ) );
                entryFilter.setResourceType( TicketForm.RESOURCE_TYPE );
                entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                entryFilter.setIdEntryParent( entry.getIdEntry(  ) );

                List<Entry> listEntryGroup = EntryHome.getEntryList( entryFilter );
                entry.setChildren( listEntryGroup );
                listEntry.addAll( listEntryGroup );
            }
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_GROUP_ENTRY_LIST, getRefListGroups( ticketForm.getIdForm(  ) ) );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance(  ).getEntryTypeReferenceList(  ) );
        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_LOCALE_TINY, getLocale(  ) );
        model.put( MARK_LIST_ORDER_FIRST_LEVEL, listOrderFirstLevel );
        addElementsToModelForLeftColumn( request, ticketForm, getUser(  ), getLocale(  ), model );

        // model.put( MARK_MAP_CHILD, mapGroupItemsNumber );
        if ( ( strPage != null ) && strPage.equals( PARAMETER_FORM_GENERICATTR ) )
        {
            return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETFORM_GENATTR, TEMPLATE_MODIFY_TICKETFORM_GENATTR, model );
        }
        else
        {
            return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETFORM, TEMPLATE_MODIFY_TICKETFORM, model );
        }
    }

    /**
     * Returns the form to update info about a ticketing form
     * @param request The HTTP request
     * @return The HTML form to update info
     * @throws AccessDeniedException If the user is not authorized to modify
     *             this ticketing form
     */
    @View( VIEW_ADVANCED_MODIFY_TICKETFORM )
    public String getModifyTicketFormAdvanced( HttpServletRequest request )
        throws AccessDeniedException
    {
        TicketForm ticketForm = (TicketForm) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );

        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );

        if ( ( ticketForm == null ) || ( nIdForm != ticketForm.getIdForm(  ) ) ||
                Boolean.parseBoolean( request.getParameter( PARAMETER_FORCE_RELOAD ) ) )
        {
            ticketForm = TicketFormHome.findByPrimaryKey( nIdForm );
            request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_TICKETING_FORM, ticketForm );
        }

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( ticketForm.getIdForm(  ) );
        entryFilter.setResourceType( TicketForm.RESOURCE_TYPE );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( entryFilter );
        List<Entry> listEntry = new ArrayList<Entry>( listEntryFirstLevel.size(  ) );

        //        Map<Integer, Integer> mapGroupItemsNumber = new HashMap<Integer, Integer>( );
        List<Integer> listOrderFirstLevel = new ArrayList<Integer>( listEntryFirstLevel.size(  ) );

        for ( Entry entry : listEntryFirstLevel )
        {
            listEntry.add( entry );
            // If the entry is a group, we add entries associated with this group
            listOrderFirstLevel.add( listEntry.size(  ) );

            if ( entry.getEntryType(  ).getGroup(  ) )
            {
                entryFilter = new EntryFilter(  );
                entryFilter.setIdResource( ticketForm.getIdForm(  ) );
                entryFilter.setResourceType( TicketForm.RESOURCE_TYPE );
                entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                entryFilter.setIdEntryParent( entry.getIdEntry(  ) );

                List<Entry> listEntryGroup = EntryHome.getEntryList( entryFilter );
                entry.setChildren( listEntryGroup );
                //                mapGroupItemsNumber.put( entry.getIdEntry( ), listEntryGroup.size( ) );
                listEntry.addAll( listEntryGroup );
            }
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_GROUP_ENTRY_LIST, getRefListGroups( ticketForm.getIdForm(  ) ) );
        model.put( MARK_ENTRY_TYPE_LIST, EntryTypeService.getInstance(  ).getEntryTypeReferenceList(  ) );
        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_LOCALE, getLocale(  ) );
        model.put( MARK_LOCALE_TINY, getLocale(  ) );
        model.put( MARK_LIST_ORDER_FIRST_LEVEL, listOrderFirstLevel );
        model.put( MARK_PAGE, PARAMETER_FORM_GENERICATTR );
        addElementsToModelForLeftColumn( request, ticketForm, getUser(  ), getLocale(  ), model );

        //        model.put( MARK_MAP_CHILD, mapGroupItemsNumber );
        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETFORM, TEMPLATE_ADVANCED_MODIFY_TICKETFORM, model );
    }

    /**
     * Process the change form of a ticketing form
     * @param request The HTTP request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException If the user is not authorized to modify
     *             this ticketing form
     */
    @Action( ACTION_MODIFY_TICKETFORM )
    public String doModifyTicketForm( HttpServletRequest request )
        throws AccessDeniedException
    {
        TicketForm ticketForm = (TicketForm) request.getSession(  ).getAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strDesc = request.getParameter( PARAMETER_DESC );
        int nIdCategory = StringUtils.isEmpty( request.getParameter( PARAMETER_ID_CATEGORY ) ) ? 0
                                                                                               : Integer.parseInt( request.getParameter( 
                    PARAMETER_ID_CATEGORY ) );
        int nIdTicketForm = Integer.parseInt( strIdForm );

        if ( ( ticketForm == null ) || ( nIdTicketForm != ticketForm.getIdForm(  ) ) )
        {
            ticketForm = TicketFormHome.findByPrimaryKey( nIdTicketForm );
        }

        ticketForm.setIdCategory( nIdCategory );
        ticketForm.setDescription( strDesc );
        ticketForm.setTitle( strTitle );

        // Check constraints
        if ( !validateBean( ticketForm, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TICKETFORM, PARAMETER_ID_FORM, ticketForm.getIdForm(  ) );
        }

        TicketFormHome.update( ticketForm );

        request.getSession(  ).removeAttribute( SESSION_ATTRIBUTE_TICKETING_FORM );
        addInfo( INFO_TICKETFORM_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETFORMS );
    }

    /**
     * Get url manage ticketing form
     * @param request the request
     * @param strIdForm the id form
     * @return url manage ticketing form
     */
    public static String getURLManageTicketForm( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_TICKETFORMS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_FORM_MESSAGES );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

        return urlItem.getUrl(  );
    }

    /**
     * Get an integer attribute from the session
     * @param session The session
     * @param strSessionKey The session key of the item
     * @return The value of the attribute, or 0 if the key is not associated
     *         with any value
     */
    private int getIntSessionAttribute( HttpSession session, String strSessionKey )
    {
        Integer nAttr = (Integer) session.getAttribute( strSessionKey );

        if ( nAttr != null )
        {
            return nAttr;
        }

        return 0;
    }

    /**
     * Get the reference list of groups
     * @param nIdForm the id of the ticketing form
     * @return The reference list of groups of the given form
     */
    private static ReferenceList getRefListGroups( int nIdForm )
    {
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( TicketForm.RESOURCE_TYPE );
        entryFilter.setIdIsGroup( 1 );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );

        ReferenceList refListGroups = new ReferenceList(  );

        for ( Entry entry : listEntry )
        {
            refListGroups.addItem( entry.getIdEntry(  ), entry.getTitle(  ) );
        }

        return refListGroups;
    }

    /**
     * Get the URL to modify an ticketing form
     * @param request The request
     * @param nIdForm The id of the form to modify
     * @return The URL to modify the given Ticket form
     */
    public static String getURLModifyTicketForm( HttpServletRequest request, int nIdForm )
    {
        return getURLModifyTicketForm( request, Integer.toString( nIdForm ) );
    }

    /**
     * Get the URL to modify an ticketing form
     * @param request The request
     * @param strIdForm The id of the form to modify
     * @return The URL to modify the given Ticket form
     */
    public static String getURLModifyTicketForm( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_TICKETFORMS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_TICKETFORM );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );

        return urlItem.getUrl(  );
    }

    /**
     * Get the URL to modify advanced properties of a ticketing form
     *
     * @param request
     *            The request
     * @param strIdForm
     *            The id of the form to modify
     * @return The URL to modify the given Ticket form
     */
    public static String getURLModifyAdvancedTicketForm( HttpServletRequest request, String strIdForm )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_TICKETFORMS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_TICKETFORM );
        urlItem.addParameter( PARAMETER_ID_FORM, strIdForm );
        urlItem.addParameter( MVCUtils.PARAMETER_PAGE, PARAMETER_FORM_GENERICATTR );

        return urlItem.getUrl(  );
    }

    /**
     * Get the URL to modify advanced properties of a ticketing form
     *
     * @param request
     *            The request
     * @param nIdForm
     *            The id of the form to modify
     * @return The URL to modify the given Ticket form
     */
    public static String getURLModifyAdvancedTicketForm( HttpServletRequest request, int nIdForm )
    {
        return getURLModifyAdvancedTicketForm( request, Integer.toString( nIdForm ) );
    }

    /**
     * Get the URL to manage ticketing forms
     *
     * @param request
     *            The request
     * @return The URL to manage ticketing forms
     */
    public static String getURLManageTicketForms( HttpServletRequest request )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_TICKETFORMS );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_TICKETFORMS );

        return urlItem.getUrl(  );
    }

    /**
     * Add elements to the model to display the left column to modify an
     * ticketing form
     *
     * @param request
     *            The request to store the ticketing form in session
     * @param ticketForm
     *            The ticketing form
     * @param user
     *            The user
     * @param locale
     *            The locale
     * @param model
     *            the model to add elements in
     */
    public static void addElementsToModelForLeftColumn( HttpServletRequest request, TicketForm ticketForm,
        AdminUser user, Locale locale, Map<String, Object> model )
    {
        model.put( MARK_TICKETFORM, ticketForm );
        model.put( MARK_LIST_WORKFLOWS, WorkflowService.getInstance(  ).getWorkflowsEnabled( user, locale ) );

        Plugin pluginTicketResource = PluginService.getPlugin( AppPropertiesService.getProperty( 
                    PROPERTY_MODULE_TICKETING_RESOURCE_NAME ) );
        model.put( MARK_TICKETING_RESOURCE_ENABLED,
            ( pluginTicketResource != null ) && pluginTicketResource.isInstalled(  ) );
        request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_TICKETING_FORM, ticketForm );
    }
}
