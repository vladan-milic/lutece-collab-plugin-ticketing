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
package fr.paris.lutece.plugins.ticketing.web.admin;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage TicketCategory features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTicketCategories.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class TicketCategoryJspBean extends ManageAdminTicketingJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETCATEGORIES = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "manage_ticket_categories.html";
    private static final String TEMPLATE_CREATE_TICKETCATEGORY = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "create_ticket_category.html";
    private static final String TEMPLATE_MODIFY_TICKETCATEGORY = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "modify_ticket_category.html";
    private static final String TEMPLATE_MODIFY_TICKETCATEGORY_INPUTS = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH
            + "modify_ticket_category_inputs.html";

    // Parameters
    private static final String PARAMETER_ID_TICKETCATEGORY = "id";
    private static final String PARAMETER_ID_TICKETCATEGORY_INPUT = "id_input";
    private static final String PARAMETER_ID_UNIT = "idUnit";
    private static final String PARAMETER_TICKETCATEGORY_ORDER = "category_order";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETCATEGORIES = "ticketing.manage_ticketcategories.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETCATEGORY = "ticketing.modify_ticketcategory.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKETCATEGORY = "ticketing.create_ticketcategory.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETCATEGORY_INPUTS = "ticketing.modify_ticketcategory_inputs.pageTitle";

    // Markers
    private static final String MARK_TICKETCATEGORY_LIST = "ticketcategory_list";
    private static final String MARK_TICKETCATEGORY = "ticketcategory";
    private static final String MARK_TICKET_DOMAINS_REFLIST = "ticket_domains_list";
    private static final String MARK_TICKET_DOMAIN_LIST = "ticketdomain_list";
    private static final String MARK_LIST_WORKFLOWS = "listWorkflows";
    private static final String MARK_LIST_UNITS = "units_list";
    private static final String MARK_ALL_INPUTS_LIST = "inputs_list";
    private static final String MARK_LOCALE = "language";
    private static final String MARK_LOCALE_TINY = "locale";
    private static final String MARK_CATEGORY_INPUTS_LIST = "category_inputs_list";
    private static final String MARK_CATEGORY = "category";
    private static final String JSP_MANAGE_TICKETCATEGORYS = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH + "ManageTicketCategories.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKETCATEGORY = "ticketing.message.confirmRemoveTicketCategory";
    private static final String MESSAGE_CONFIRM_REMOVE_TICKETCATEGORY_INPUT = "ticketing.message.confirmRemoveTicketCategoryInput";
    private static final String MESSAGE_ERROR_CODE_ALREADY_EXISTS = "ticketing.message.errorTicketCategory.codeAlreadyExists";
    private static final String MESSAGE_ERROR_CODE_INVALID_FORMAT = "ticketing.message.errorTicketCategory.codeInvlidFormat";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticketcategory.attribute.";

    // Views
    private static final String VIEW_MANAGE_TICKETCATEGORYS = "manageTicketCategorys";
    private static final String VIEW_CREATE_TICKETCATEGORY = "createTicketCategory";
    private static final String VIEW_MODIFY_TICKETCATEGORY = "modifyTicketCategory";
    private static final String VIEW_MODIFY_TICKETCATEGORY_INPUTS = "modifyTicketCategoryInputs";

    // Actions
    private static final String ACTION_CREATE_TICKETCATEGORY = "createTicketCategory";
    private static final String ACTION_MODIFY_TICKETCATEGORY = "modifyTicketCategory";
    private static final String ACTION_REMOVE_TICKETCATEGORY = "removeTicketCategory";
    private static final String ACTION_CONFIRM_REMOVE_TICKETCATEGORY = "confirmRemoveTicketCategory";
    private static final String ACTION_ADD_TICKETCATEGORY_INPUT = "addTicketCategoryInput";
    private static final String ACTION_REMOVE_TICKETCATEGORY_INPUT = "removeTicketCategoryInput";
    private static final String ACTION_CONFIRM_REMOVE_TICKETCATEGORY_INPUT = "confirmRemoveTicketCategoryInput";
    private static final String ACTION_DO_MOVE_FIELD_UP = "doMoveFieldUp";
    private static final String ACTION_DO_MOVE_FIELD_DOWN = "doMoveFieldDown";
    private static final String ACTION_DO_MOVE_CATEGORY_UP = "doMoveCategoryUp";
    private static final String ACTION_DO_MOVE_CATEGORY_DOWN = "doMoveCategoryDown";

    // Infos
    private static final String INFO_TICKETCATEGORY_CREATED = "ticketing.info.ticketcategory.created";
    private static final String INFO_TICKETCATEGORY_UPDATED = "ticketing.info.ticketcategory.updated";
    private static final String INFO_TICKETCATEGORY_REMOVED = "ticketing.info.ticketcategory.removed";
    private static final String INFO_TICKETCATEGORY_INPUT_REMOVED = "ticketing.info.ticketcategory.input.removed";
    private static final String PATTERN_CATEGORY_CODE = "^[A-Z0-9]*";
    private static Pattern _pattern = Pattern.compile( PATTERN_CATEGORY_CODE );
    private static final long serialVersionUID = 1L;

    // Session variable to store working values
    private TicketCategory _category;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETCATEGORYS, defaultView = true )
    public String getManageTicketCategorys( HttpServletRequest request )
    {
        _category = null;

        // This List could be directly populated by DAO instead of loop
        List<TicketDomain> _ticketDomainList = TicketDomainHome.getTicketDomainsList( );
        for ( TicketDomain _ticketDomain : _ticketDomainList )
        {
            List<TicketCategory> listTicketCategories = (List<TicketCategory>) TicketCategoryHome.findByDomainId( _ticketDomain.getId( ) );
            if ( listTicketCategories != null )
            {
                _ticketDomain.setCategoryList( listTicketCategories );
            }
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_TICKET_DOMAIN_LIST, _ticketDomainList, JSP_MANAGE_TICKETCATEGORYS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETCATEGORIES, TEMPLATE_MANAGE_TICKETCATEGORIES, model );
    }

    /**
     * Returns the form to create a ticketcategory
     *
     * @param request
     *            The Http request
     * @return the html code of the ticketcategory form
     */
    @View( VIEW_CREATE_TICKETCATEGORY )
    public String getCreateTicketCategory( HttpServletRequest request )
    {
        _category = ( _category != null ) ? _category : new TicketCategory( );

        Map<String, Object> model = getModel( );
        model.put( MARK_TICKETCATEGORY, _category );
        model.put( MARK_TICKET_DOMAINS_REFLIST, TicketDomainHome.getReferenceList( ) );
        model.put( MARK_LIST_WORKFLOWS, WorkflowService.getInstance( ).getWorkflowsEnabled( getUser( ), getLocale( ) ) );
        model.put( MARK_LIST_UNITS, getUnitsList( ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKETCATEGORY, TEMPLATE_CREATE_TICKETCATEGORY, model );
    }

    /**
     * Process the data capture form of a new ticketcategory
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKETCATEGORY )
    public String doCreateTicketCategory( HttpServletRequest request )
    {
        populate( _category, request );

        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) || !validateCode( _category ) )
        {
            return redirectView( request, VIEW_CREATE_TICKETCATEGORY );
        }

        TicketCategoryHome.create( _category );
        addInfo( INFO_TICKETCATEGORY_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TICKETCATEGORYS );
    }

    /**
     * Manages the removal form of a ticketcategory whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKETCATEGORY )
    public String getConfirmRemoveTicketCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKETCATEGORY ) );
        url.addParameter( PARAMETER_ID_TICKETCATEGORY, nId );

        String strMessageUrl = AdminMessageService
                .getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKETCATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a ticketcategory
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage ticketcategorys
     */
    @Action( ACTION_REMOVE_TICKETCATEGORY )
    public String doRemoveTicketCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );
        TicketCategoryHome.remove( nId );

        addInfo( INFO_TICKETCATEGORY_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TICKETCATEGORYS );
    }

    /**
     * Returns the form to update info about a ticketcategory
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKETCATEGORY )
    public String getModifyTicketCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );

        if ( ( _category == null ) || ( _category.getId( ) != nId ) )
        {
            _category = TicketCategoryHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_TICKETCATEGORY, _category );
        model.put( MARK_TICKET_DOMAINS_REFLIST, TicketDomainHome.getReferenceList( ) );
        model.put( MARK_LIST_WORKFLOWS, WorkflowService.getInstance( ).getWorkflowsEnabled( getUser( ), getLocale( ) ) );

        model.put( MARK_LIST_UNITS, getUnitsList( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETCATEGORY, TEMPLATE_MODIFY_TICKETCATEGORY, model );
    }

    /**
     * Process the change form of a ticketcategory
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKETCATEGORY )
    public String doModifyTicketCategory( HttpServletRequest request )
    {
        populate( _category, request );

        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) || !validateCode( _category ) )
        {
            return redirect( request, VIEW_MODIFY_TICKETCATEGORY, PARAMETER_ID_TICKETCATEGORY, _category.getId( ) );
        }

        TicketCategoryHome.update( _category );
        addInfo( INFO_TICKETCATEGORY_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_TICKETCATEGORYS );
    }

    /**
     * Move a field up
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_CATEGORY_UP )
    public String doMoveCategoryUp( HttpServletRequest request )
    {
        return doMoveCategory( request, true );
    }

    /**
     * Move a field down
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_CATEGORY_DOWN )
    public String doMoveCategoryDown( HttpServletRequest request )
    {
        return doMoveCategory( request, false );
    }

    /**
     * Move a field up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the field up, false to move it down
     * @return The next URL to redirect to
     */
    private String doMoveCategory( HttpServletRequest request, boolean bMoveUp )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );

        // Update the Category with new Position
        TicketCategoryHome.updateCategoryOrder( nId, bMoveUp );

        return redirectView( request, VIEW_MANAGE_TICKETCATEGORYS );
    }

    /**
     * Returns the form to update inputs about a ticket category
     *
     * @param request
     *            The Http request
     * @return The HTML form to update inputs
     */
    @View( VIEW_MODIFY_TICKETCATEGORY_INPUTS )
    public String getModifyTicketCategoryInputs( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );

        _category = TicketCategoryHome.findByPrimaryKey( nId );

        List<Entry> listEntry = getCategoryEntryList( _category );

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORY, _category );
        model.put( MARK_ALL_INPUTS_LIST, getFilteredRefListInputs( _category ) );
        model.put( MARK_CATEGORY_INPUTS_LIST, listEntry );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_LOCALE_TINY, getLocale( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETCATEGORY_INPUTS, TEMPLATE_MODIFY_TICKETCATEGORY_INPUTS, model );
    }

    /**
     * Handles the add of input to a ticketcategory
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage ticketcategory inputs
     */
    @Action( ACTION_ADD_TICKETCATEGORY_INPUT )
    public String doAddTicketCategoryInput( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );

        if ( StringUtils.isNotBlank( request.getParameter( PARAMETER_ID_TICKETCATEGORY_INPUT ) ) )
        {
            int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY_INPUT ) );
            TicketCategoryHome.createLinkCategoryInputNextPos( nId, nIdInput );
            addInfo( INFO_TICKETCATEGORY_CREATED, getLocale( ) );
        }

        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_TICKETCATEGORY_INPUTS ) );
        url.addParameter( PARAMETER_ID_TICKETCATEGORY, nId );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Move a field up
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_FIELD_UP )
    public String doMoveFieldUp( HttpServletRequest request )
    {
        return doMoveField( request, true );
    }

    /**
     * Move a field down
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_FIELD_DOWN )
    public String doMoveFieldDown( HttpServletRequest request )
    {
        return doMoveField( request, false );
    }

    /**
     * Move a field up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the field up, false to move it down
     * @return The next URL to redirect to
     */
    public String doMoveField( HttpServletRequest request, boolean bMoveUp )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );
        int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY_INPUT ) );

        int nOldPosition = TicketCategoryHome.getCategoryInputPosition( nId, nIdInput );
        int nNewPosition = bMoveUp ? ( nOldPosition - 1 ) : ( nOldPosition + 1 );

        int nInputToInversePosition = TicketCategoryHome.getCategoryInputByPosition( nId, nNewPosition );

        // Update the Input with new Position
        TicketCategoryHome.updateCategoryInputPosition( nId, nIdInput, nNewPosition );

        // Update the Input that was on that position before
        TicketCategoryHome.updateCategoryInputPosition( nId, nInputToInversePosition, nOldPosition );

        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_TICKETCATEGORY_INPUTS ) );
        url.addParameter( PARAMETER_ID_TICKETCATEGORY, nId );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Manages the removal form of a ticketcategory whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( ACTION_CONFIRM_REMOVE_TICKETCATEGORY_INPUT )
    public String getConfirmRemoveTicketCategoryInput( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );
        int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY_INPUT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKETCATEGORY_INPUT ) );
        url.addParameter( PARAMETER_ID_TICKETCATEGORY, nId );
        url.addParameter( PARAMETER_ID_TICKETCATEGORY_INPUT, nIdInput );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKETCATEGORY_INPUT, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal of a ticketcategory input
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage ticketcategory inputs
     */
    @Action( ACTION_REMOVE_TICKETCATEGORY_INPUT )
    public String doRemoveTicketCategoryInput( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );
        int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY_INPUT ) );
        TicketCategoryHome.removeLinkCategoryInput( nId, nIdInput );
        reorganizeCategoryInputs( nId );
        addInfo( INFO_TICKETCATEGORY_INPUT_REMOVED, getLocale( ) );

        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_TICKETCATEGORY_INPUTS ) );
        url.addParameter( PARAMETER_ID_TICKETCATEGORY, nId );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Update the inputs within a given category with consecutive position indexes
     *
     * @param nId
     *            the category which inputs are being reordered
     */
    private void reorganizeCategoryInputs( int nId )
    {
        TicketCategory _category = TicketCategoryHome.findByPrimaryKey( nId );
        List<Integer> listInputs = _category.getListIdInput( );
        int i = 1;

        for ( Integer input : listInputs )
        {
            TicketCategoryHome.updateCategoryInputPosition( nId, input, i++ );
        }
    }

    /**
     * Get the reference list of inputs not already linked to a given Category
     *
     * @param category
     *            The ticket category
     * @return The reference list of inputs
     */
    private ReferenceList getFilteredRefListInputs( TicketCategory category )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listReferenceEntry = EntryHome.getEntryList( entryFilter );
        List<Entry> listExistingEntries = getCategoryEntryList( category );
        ReferenceList refListInputs = new ReferenceList( );

        for ( Entry entry : listReferenceEntry )
        {
            boolean b_found = false;

            for ( Entry existingEntry : listExistingEntries )
            {
                if ( existingEntry.getIdResource( ) == entry.getIdResource( ) )
                {
                    b_found = true;
                }
            }

            if ( !b_found )
            {
                refListInputs.addItem( entry.getIdResource( ), buildItemComboInput( entry ) );
            }
        }

        return refListInputs;
    }

    /**
     * Build item present in the inputs list combo for each input with the input title and the input type. Except for type comment having not title. For it, the
     * item combo is build with the technical id
     *
     * @param entry
     *            The current entry
     * @return The item present in the input list combo
     */
    private String buildItemComboInput( Entry entry )
    {
        StringBuilder itemComboInput;

        if ( entry.getEntryType( ).getComment( ) )
        {
            itemComboInput = new StringBuilder( entry.getCode( ) );
        }
        else
        {
            itemComboInput = new StringBuilder( entry.getTitle( ) );
        }

        itemComboInput.append( " (" ).append( entry.getEntryType( ).getTitle( ) ).append( ")" );

        return itemComboInput.toString( );
    }

    /**
     * Validate Code (uniqueness and format)
     * 
     * @param category
     *            The category
     * @return true if valid otherwise false
     */
    private boolean validateCode( TicketCategory category )
    {
        String strCode = category.getCode( );

        if ( ( strCode != null ) && !strCode.equals( "" ) )
        {
            TicketCategory existingCategory = TicketCategoryHome.findByCode( strCode );

            if ( ( existingCategory != null ) && ( existingCategory.getId( ) != category.getId( ) ) )
            {
                Object [ ] args = {
                        strCode, existingCategory.getLabel( )
                };
                String strMessage = I18nService.getLocalizedString( MESSAGE_ERROR_CODE_ALREADY_EXISTS, args, getLocale( ) );
                addError( strMessage );

                return false;
            }

            Matcher matcher = _pattern.matcher( strCode );

            if ( !matcher.matches( ) )
            {
                addError( MESSAGE_ERROR_CODE_INVALID_FORMAT, getLocale( ) );

                return false;
            }
        }

        return true;
    }

    /**
     * Populate the bean ticketCategory using parameters in http request
     * 
     * @param ticketCategory
     *            TicketCategory to populate
     * @param request
     *            http request
     */
    protected void populate( TicketCategory ticketCategory, HttpServletRequest request )
    {
        super.populate( ticketCategory, request );

        Unit unit = UnitHome.findByPrimaryKey( Integer.valueOf( request.getParameter( PARAMETER_ID_UNIT ) ) );
        AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
        ticketCategory.setAssigneeUnit( assigneeUnit );
    }

    /**
     * Return a list of Entries linked to a category
     * 
     * @param _category
     * @return
     */
    private List<Entry> getCategoryEntryList( TicketCategory _category )
    {
        List<Entry> listEntry = new ArrayList<Entry>( );

        if ( ( _category != null ) && ( _category.getListIdInput( ) != null ) )
        {
            EntryFilter entryFilter = new EntryFilter( );
            entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
            entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            for ( Integer nIdInput : _category.getListIdInput( ) )
            {
                entryFilter.setIdResource( nIdInput );

                List<Entry> listEntryFound = EntryHome.getEntryList( entryFilter );

                if ( ( listEntryFound != null ) && ( listEntryFound.size( ) >= 1 ) )
                {
                    listEntry.add( listEntryFound.get( 0 ) );
                }
            }
        }

        return listEntry;
    }
}
