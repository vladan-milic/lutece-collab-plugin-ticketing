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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.tree.Tree;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Category features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageCategories.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class CategoryJspBean extends ManageAdminTicketingJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_CATEGORIES                 = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "manage_categories.html";
    private static final String TEMPLATE_CREATE_CATEGORY                   = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY                   = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "modify_category.html";
    private static final String TEMPLATE_CREATE_CATEGORYTYPE               = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "create_categorytype.html";
    private static final String TEMPLATE_MODIFY_CATEGORYTYPE               = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "modify_categorytype.html";
    private static final String TEMPLATE_MODIFY_CATEGORY_INPUTS            = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "modify_category_inputs.html";

    // Parameters
    private static final String PARAMETER_ID_CATEGORY                      = "id_category";
    private static final String PARAMETER_ASSIGNEE_UNIT                    = "id_unit";
    private static final String PARAMETER_ID_PARENT_CATEGORY               = "id_parent_category";
    private static final String PARAMETER_ID_CATEGORYTYPE                  = "id_category_type";

    private static final String PARAMETER_ID_CATEGORY_INPUT                = "id_input";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CATEGORYS       = "ticketing.manage_categories.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORY        = "ticketing.modify_category.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORY        = "ticketing.create_category.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORYTYPE    = "ticketing.modify_categorytype.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORYTYPE    = "ticketing.create_categorytype.pageTitle";

    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORY_INPUTS = "ticketing.modify_category_inputs.pageTitle";

    // Markers
    private static final String MARK_CATEGORY                              = "category";
    public static final String  MARK_CATEGORIES_TREE                       = "tree";
    public static final String  MARK_LIST_CATEGORY_TYPES                   = "category_types";
    public static final String  MARK_ID_CATEGORY_TYPE                      = "id_category_type";
    public static final String  MARK_ID_PARENT_CATEGORY                    = "id_parent_category";
    public static final String  MARK_ID_CATEGORY                           = "id_category";
    public static final String  MARK_ASSIGNEE_UNIT_LIST                    = "unit_list";
    public static final String  MARK_FORM_LIST                             = "form_list";
    public static final String MARK_FORM_CATEGORY_LIST                     = "form_category_list";
    private static final String MARK_CATEGORYTYPE                          = "categorytype";

    private static final String MARK_ALL_INPUTS_LIST                       = "inputs_list";
    private static final String MARK_LOCALE                                = "language";
    private static final String MARK_LOCALE_TINY                           = "locale";
    private static final String MARK_CATEGORY_INPUTS_LIST                  = "category_inputs_list";
    private static final String MARK_CATEGORY_INPUTS_HERITED_LIST          = "category_inputs_herited_list";
    private static final String MARK_CATEGORY_INPUTS_BLOCKED_LIST          = "category_inputs_blocked_list";
    private static final String MARK_BRANCH_LABEL                          = "branch_label";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY            = "ticketing.message.confirmRemoveCategory";
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORYTYPE        = "ticketing.message.confirmRemoveCategoryType";

    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY_INPUT      = "ticketing.message.confirmRemoveCategoryInput";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX               = "ticketing.model.entity.category.attribute.";
    private static final String VALIDATION_ATTRIBUTES_TYPE_PREFIX          = "ticketing.model.entity.categorytype.attribute.";

    // Views
    private static final String VIEW_MANAGE_CATEGORIES                     = "manageCategories";
    private static final String VIEW_CREATE_CATEGORY                       = "createCategory";
    private static final String VIEW_MODIFY_CATEGORY                       = "modifyCategory";
    private static final String VIEW_CREATE_CATEGORYTYPE                   = "createCategoryType";
    private static final String VIEW_MODIFY_CATEGORYTYPE                   = "modifyCategoryType";
    private static final String VIEW_MODIFY_CATEGORY_INPUTS                = "modifyCategoryInputs";

    // Actions
    private static final String ACTION_CREATE_CATEGORY                     = "createCategory";
    private static final String ACTION_MODIFY_CATEGORY                     = "modifyCategory";
    private static final String ACTION_REMOVE_CATEGORY                     = "removeCategory";
    private static final String ACTION_CONFIRM_REMOVE_CATEGORY             = "confirmRemoveCategory";
    private static final String ACTION_CREATE_CATEGORYTYPE                 = "createCategoryType";
    private static final String ACTION_MODIFY_CATEGORYTYPE                 = "modifyCategoryType";
    private static final String ACTION_REMOVE_CATEGORYTYPE                 = "removeCategoryType";
    private static final String ACTION_CONFIRM_REMOVE_CATEGORYTYPE         = "confirmRemoveCategoryType";

    private static final String ACTION_ADD_CATEGORY_INPUT                  = "addCategoryInput";
    private static final String ACTION_REMOVE_CATEGORY_INPUT               = "removeCategoryInput";
    private static final String ACTION_CONFIRM_REMOVE_CATEGORY_INPUT       = "confirmRemoveCategoryInput";
    private static final String ACTION_DO_MOVE_FIELD_UP                    = "doMoveFieldUp";
    private static final String ACTION_DO_MOVE_FIELD_DOWN                  = "doMoveFieldDown";
    private static final String ACTION_DO_MOVE_CATEGORY_UP                 = "doMoveCategoryUp";
    private static final String ACTION_DO_MOVE_CATEGORY_DOWN               = "doMoveCategoryDown";

    // Infos
    private static final String INFO_CATEGORY_CREATED                      = "ticketing.info.category.created";
    private static final String INFO_CATEGORY_UPDATED                      = "ticketing.info.category.updated";
    private static final String INFO_CATEGORY_REMOVED                      = "ticketing.info.category.removed";
    private static final String INFO_CATEGORYTYPE_CREATED                  = "ticketing.info.categorytype.created";
    private static final String INFO_CATEGORYTYPE_UPDATED                  = "ticketing.info.categorytype.updated";
    private static final String INFO_CATEGORYTYPE_REMOVED                  = "ticketing.info.categorytype.removed";
    private static final String INFO_CATEGORY_INPUT_REMOVED                = "ticketing.info.category.input.removed";

    // Errors
    private static final String ERROR_CATEGORY_REFERENCED                  = "ticketing.error.category.referenced.in.categories";

    private static final String JSP_MANAGE_CATEGORIES                      = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH + "ManageCategories.jsp";

    private static final long   serialVersionUID                           = 1L;

    // Session variable to store working values
    private TicketCategory      _category;
    private TicketCategoryType  _categoryType;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CATEGORIES, defaultView = true )
    public String getManageCategories( HttpServletRequest request )
    {
        _category = null;

        Tree<TicketCategory, TicketCategoryType> treeCategories = TicketCategoryService.getInstance( ).getCategoriesTree( );

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORIES_TREE, treeCategories );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CATEGORYS, TEMPLATE_MANAGE_CATEGORIES, model );
    }

    /**
     * Returns the form to create a category
     *
     * @param request
     *            The Http request
     * @return the html code of the category form
     */
    @View( VIEW_CREATE_CATEGORY )
    public String getCreateCategory( HttpServletRequest request )
    {
        _category = new TicketCategory( );

        String strIdParentCategory = request.getParameter( PARAMETER_ID_PARENT_CATEGORY );

        if ( strIdParentCategory != null )
        {
            _category.setIdParent( Integer.parseInt( strIdParentCategory ) );
            TicketCategory categoryParent = TicketCategoryService.getInstance( ).findCategoryById( _category.getIdParent( ) );
            _category.setParent( categoryParent );
            _category.setDefaultAssignUnit( categoryParent.getDefaultAssignUnit( ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_ASSIGNEE_UNIT_LIST, getUnitsList( ) );
        model.put( MARK_CATEGORY, _category );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CATEGORY, TEMPLATE_CREATE_CATEGORY, model );
    }

    /**
     * Process the data capture form of a new category
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_CATEGORY )
    public String doCreateCategory( HttpServletRequest request )
    {
        populate( _category, request );

        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CATEGORY );
        }

        String strIdUnit = request.getParameter( PARAMETER_ASSIGNEE_UNIT );

        if ( strIdUnit != null )
        {
            Unit unit = UnitHome.findByPrimaryKey( Integer.parseInt( strIdUnit ) );
            if ( unit != null )
            {
                AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
                _category.setDefaultAssignUnit( assigneeUnit );
            } else
            {
                AssigneeUnit emptyUnit = new AssigneeUnit( );
                emptyUnit.setUnitId( -1 );
                _category.setDefaultAssignUnit( emptyUnit );
            }
        }

        TicketCategoryService.getInstance( ).createSubCategory( _category );

        addInfo( INFO_CATEGORY_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_CATEGORIES );
    }

    /**
     * Manages the removal form of a category whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CATEGORY )
    public String getConfirmRemoveCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CATEGORY ) );
        url.addParameter( PARAMETER_ID_CATEGORY, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a category
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage categories
     */
    @Action( ACTION_REMOVE_CATEGORY )
    public String doRemoveCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );

        Tree<TicketCategory, TicketCategoryType> treeCategories = TicketCategoryService.getInstance( ).getCategoriesTree( );

        // Remove children
        treeCategories.getAllChildren( treeCategories.findNodeById( nId ), true ).stream( ).forEach( category -> TicketCategoryService.getInstance( ).removeCategory( category.getId( ) ) );

        TicketCategoryService.getInstance( ).removeCategory( nId );
        addInfo( INFO_CATEGORY_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_CATEGORIES );
    }

    /**
     * Returns the form to update info about a category
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CATEGORY )
    public String getModifyCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );

        if ( _category == null || ( _category.getId( ) != nId ) )
        {
            _category = TicketCategoryService.getInstance( ).findCategoryById( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_ASSIGNEE_UNIT_LIST, getUnitsList( ) );
        model.put( MARK_FORM_LIST, getListForm( ) );
        model.put( MARK_FORM_CATEGORY_LIST, getListFormCategoryByIdCategory( _category.getId( ) ) );
        model.put( MARK_CATEGORY, _category );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CATEGORY, TEMPLATE_MODIFY_CATEGORY, model );
    }

    /**
     * Process the change form of a category
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_CATEGORY )
    public String doModifyCategory( HttpServletRequest request )
    {
        populate( _category, request );

        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CATEGORY, PARAMETER_ID_CATEGORY, _category.getId( ) );
        }

        String strIdUnit = request.getParameter( PARAMETER_ASSIGNEE_UNIT );

        if ( strIdUnit != null )
        {
            Unit unit = UnitHome.findByPrimaryKey( Integer.parseInt( strIdUnit ) );
            if ( unit != null )
            {
                AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
                _category.setDefaultAssignUnit( assigneeUnit );
            } else
            {
                AssigneeUnit emptyUnit = new AssigneeUnit( );
                emptyUnit.setUnitId( -1 );
                _category.setDefaultAssignUnit( emptyUnit );
            }
        }

        TicketCategoryService.getInstance( ).updateCategory( _category );
        addInfo( INFO_CATEGORY_UPDATED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_CATEGORIES );
    }

    /**
     * Handles the increment of position of a category
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage category
     */
    @Action( ACTION_DO_MOVE_CATEGORY_UP )
    public String doMoveUpTicketType( HttpServletRequest request )
    {
        return doMoveCategory( request, true );
    }

    /**
     * Handles the decrement of position of a category
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage category
     */
    @Action( ACTION_DO_MOVE_CATEGORY_DOWN )
    public String doMoveDownTicketType( HttpServletRequest request )
    {
        return doMoveCategory( request, false );
    }

    /**
     * Move a Category position up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the Category up, false to move it down
     * @return The next URL to redirect to
     */
    private String doMoveCategory( HttpServletRequest request, boolean bMoveUp )
    {
        try
        {
            int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );

            TicketCategoryService.getInstance( ).updateCategoryOrder( nId, bMoveUp );
        } catch ( NumberFormatException e )
        {
            AppLogService.debug( "Error while moving Category. " + e.getMessage( ) );
        }

        return redirectView( request, VIEW_MANAGE_CATEGORIES );
    }

    /**
     * Returns the form to create a categoryType
     *
     * @param request
     *            The Http request
     * @return the html code of the categoryType form
     */
    @View( VIEW_CREATE_CATEGORYTYPE )
    public String getCreateCategoryType( HttpServletRequest request )
    {
        _categoryType = new TicketCategoryType( );

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORYTYPE, _categoryType );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CATEGORYTYPE, TEMPLATE_CREATE_CATEGORYTYPE, model );
    }

    /**
     * Process the data capture form of a new categoryType
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_CATEGORYTYPE )
    public String doCreateCategoryType( HttpServletRequest request )
    {
        populate( _categoryType, request );

        // Check constraints
        if ( !validateBean( _categoryType, VALIDATION_ATTRIBUTES_TYPE_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CATEGORYTYPE );
        }

        TicketCategoryService.getInstance( ).createCategoryType( _categoryType );
        addInfo( INFO_CATEGORYTYPE_CREATED, getLocale( ) );
        return redirect( request, AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORIES );
    }

    /**
     * Manages the removal form of a categoryType whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CATEGORYTYPE )
    public String getConfirmRemoveCategoryType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORYTYPE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CATEGORYTYPE ) );
        url.addParameter( PARAMETER_ID_CATEGORYTYPE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORYTYPE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a categoryType
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage categoryType
     */
    @Action( ACTION_REMOVE_CATEGORYTYPE )
    public String doRemoveCategoryType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORYTYPE ) );

        if ( TicketCategoryService.getInstance( ).isCategoryTypeNotReferenced( nId ) )
        {
            TicketCategoryService.getInstance( ).removeCategoryTypeAndSubType( nId );
            addInfo( INFO_CATEGORYTYPE_REMOVED, getLocale( ) );
        } else
        {
            addError( ERROR_CATEGORY_REFERENCED, getLocale( ) );
        }

        return redirect( request, AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORIES );
    }

    /**
     * Returns the form to update info about a categoryType
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CATEGORYTYPE )
    public String getModifyCategoryType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORYTYPE ) );

        if ( _categoryType == null || ( _categoryType.getId( ) != nId ) )
        {
            _categoryType = TicketCategoryService.getInstance( ).findCategoryTypeById( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORYTYPE, _categoryType );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CATEGORYTYPE, TEMPLATE_MODIFY_CATEGORYTYPE, model );
    }

    /**
     * Process the change form of a categoryType
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_CATEGORYTYPE )
    public String doModifyCategoryType( HttpServletRequest request )
    {
        populate( _categoryType, request );

        // Check constraints
        if ( !validateBean( _categoryType, VALIDATION_ATTRIBUTES_TYPE_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CATEGORYTYPE, PARAMETER_ID_CATEGORYTYPE, _categoryType.getId( ) );
        }

        TicketCategoryService.getInstance( ).updateCategoryType( _categoryType );
        addInfo( INFO_CATEGORYTYPE_UPDATED, getLocale( ) );
        return redirect( request, AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORIES );
    }

    /**
     * Returns the form to update inputs about a category
     *
     * @param request
     *            The Http request
     * @return The HTML form to update inputs
     */
    @View( VIEW_MODIFY_CATEGORY_INPUTS )
    public String getModifyCategoryInputs( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );

        _category = TicketCategoryService.getInstance( ).findCategoryById( nId );

        List<Entry> listEntry = TicketCategoryService.getInstance( ).getCategoryEntryList( _category );
        List<Entry> listHeritedEntry = TicketCategoryService.getInstance( ).getCategoryEntryHeritedList( _category, getLocale( ) );
        List<Entry> listBlockedEntry = TicketCategoryService.getInstance( ).getCategoryEntryBlockedList( _category, getLocale( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORY, _category );
        model.put( MARK_ALL_INPUTS_LIST, TicketCategoryService.getInstance( ).getFilteredRefListInputs( _category ) );
        model.put( MARK_CATEGORY_INPUTS_LIST, listEntry );
        model.put( MARK_CATEGORY_INPUTS_HERITED_LIST, listHeritedEntry );
        model.put( MARK_CATEGORY_INPUTS_BLOCKED_LIST, listBlockedEntry );

        model.put( MARK_BRANCH_LABEL, TicketCategoryService.getInstance( ).getBranchLabel( _category, " / " ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_LOCALE_TINY, getLocale( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CATEGORY_INPUTS, TEMPLATE_MODIFY_CATEGORY_INPUTS, model );
    }

    /**
     * Handles the add of input to a category
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage category inputs
     */
    @Action( ACTION_ADD_CATEGORY_INPUT )
    public String doAddCategoryInput( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );

        if ( StringUtils.isNotBlank( request.getParameter( PARAMETER_ID_CATEGORY_INPUT ) ) )
        {
            int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY_INPUT ) );
            TicketCategoryService.getInstance( ).createLinkCategoryInput( nId, nIdInput );
            addInfo( INFO_CATEGORY_CREATED, getLocale( ) );
        }

        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_CATEGORY_INPUTS ) );
        url.addParameter( PARAMETER_ID_CATEGORY, nId );

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
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );
        int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY_INPUT ) );

        TicketCategoryService.getInstance( ).updateCategoryInputPosition( nId, nIdInput, bMoveUp );

        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_CATEGORY_INPUTS ) );
        url.addParameter( PARAMETER_ID_CATEGORY, nId );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Manages the removal form of a category whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( ACTION_CONFIRM_REMOVE_CATEGORY_INPUT )
    public String getConfirmRemoveCategoryInput( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );
        int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY_INPUT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CATEGORY_INPUT ) );
        url.addParameter( PARAMETER_ID_CATEGORY, nId );
        url.addParameter( PARAMETER_ID_CATEGORY_INPUT, nIdInput );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY_INPUT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal of a category input
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage category inputs
     */
    @Action( ACTION_REMOVE_CATEGORY_INPUT )
    public String doRemoveCategoryInput( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );
        int nIdInput = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY_INPUT ) );
        TicketCategoryService.getInstance( ).removeLinkCategoryInput( nId, nIdInput );
        addInfo( INFO_CATEGORY_INPUT_REMOVED, getLocale( ) );

        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_CATEGORY_INPUTS ) );
        url.addParameter( PARAMETER_ID_CATEGORY, nId );

        return redirect( request, url.getUrl( ) );
    }

}
