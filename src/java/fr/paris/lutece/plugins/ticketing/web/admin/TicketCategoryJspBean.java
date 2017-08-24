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

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.tree.Tree;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryTreeCacheService;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Category features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageCategorys.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "ticketing_MANAGEMENT" )
public class TicketCategoryJspBean extends ManageAdminTicketingJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_CATEGORYS = "/admin/plugins/ticketing/manage_categorys.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "/admin/plugins/ticketing/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "/admin/plugins/ticketing/modify_category.html";

    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_ASSIGNEE_UNIT = "id_unit";
    private static final String PARAMETER_ID_PARENT_CATEGORY = "id_parent_category";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CATEGORYS = "ticketing.manage_categorys.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORY = "ticketing.modify_category.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORY = "ticketing.create_category.pageTitle";

    // Markers
    private static final String MARK_CATEGORY = "category";
    public static final String MARK_CATEGORIES_TREE = "tree";
    public static final String MARK_LIST_CATEGORY_TYPES = "category_types";
    public static final String MARK_ID_CATEGORY_TYPE = "id_category_type";
    public static final String MARK_ID_PARENT_CATEGORY = "id_parent_category";
    public static final String MARK_ID_CATEGORY = "id_category";
    public static final String MARK_ASSIGNEE_UNIT_LIST = "unit_list";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY = "ticketing.message.confirmRemoveCategory";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.category.attribute.";

    // Views
    private static final String VIEW_MANAGE_CATEGORYS = "manageCategorys";
    private static final String VIEW_CREATE_CATEGORY = "createCategory";
    private static final String VIEW_MODIFY_CATEGORY = "modifyCategory";

    // Actions
    private static final String ACTION_CREATE_CATEGORY = "createCategory";
    private static final String ACTION_MODIFY_CATEGORY = "modifyCategory";
    private static final String ACTION_REMOVE_CATEGORY = "removeCategory";
    private static final String ACTION_CONFIRM_REMOVE_CATEGORY = "confirmRemoveCategory";

    // Infos
    private static final String INFO_CATEGORY_CREATED = "ticketing.info.category.created";
    private static final String INFO_CATEGORY_UPDATED = "ticketing.info.category.updated";
    private static final String INFO_CATEGORY_REMOVED = "ticketing.info.category.removed";

    // Session variable to store working values
    private TicketCategory _category;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CATEGORYS, defaultView = true )
    public String getManageCategorys( HttpServletRequest request )
    {
        _category = null;

        Tree treeCategories = TicketCategoryService.getInstance( ).getCategoriesTree( );

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORIES_TREE, treeCategories );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CATEGORYS, TEMPLATE_MANAGE_CATEGORYS, model );
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
        _category = ( _category != null ) ? _category : new TicketCategory( );

        String strIdParentCategory = request.getParameter( PARAMETER_ID_PARENT_CATEGORY );

        if ( strIdParentCategory != null )
        {
            _category.setIdParent( Integer.parseInt( strIdParentCategory ) );
            _category.setParent( TicketCategoryService.getInstance( ).findById( _category.getIdParent( ) ) );
        }
        else
        {
            _category.setIdParent( -1 );
            _category.setParent( null );
        }

        Map<String, Object> model = getModel( );
        ReferenceList refListUnits = new ReferenceList( );
        for ( Unit unit : UnitHome.findAll( ) )
        {
            refListUnits.addItem( unit.getIdUnit( ), unit.getLabel( ) );
        }
        model.put( MARK_ASSIGNEE_UNIT_LIST, refListUnits );
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
                _category.setDefaultAssignUnit( unit );
            }
            else
            {
                Unit emptyUnit = new Unit( );
                emptyUnit.setIdUnit( -1 );
                _category.setDefaultAssignUnit( emptyUnit );
            }

        }

        TicketCategoryService.getInstance( ).createSubCategory( _category );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );

        addInfo( INFO_CATEGORY_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_CATEGORYS );
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
     * @return the jsp URL to display the form to manage categorys
     */
    @Action( ACTION_REMOVE_CATEGORY )
    public String doRemoveCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORY ) );

        Tree<TicketCategory, TicketCategoryType> treeCategories = TicketCategoryTreeCacheService.getInstance( ).getResource( );

        // Remove children
        treeCategories.getAllChildren( treeCategories.findNodeById( nId ) ).stream( ).forEach( category -> TicketCategoryHome.remove( category.getId( ) ) );

        TicketCategoryHome.remove( nId );
        addInfo( INFO_CATEGORY_REMOVED, getLocale( ) );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );

        return redirectView( request, VIEW_MANAGE_CATEGORYS );
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
            _category = TicketCategoryHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        ReferenceList refListUnits = new ReferenceList( );
        for ( Unit unit : UnitHome.findAll( ) )
        {
            refListUnits.addItem( unit.getIdUnit( ), unit.getLabel( ) );
        }
        model.put( MARK_ASSIGNEE_UNIT_LIST, refListUnits );
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
                _category.setDefaultAssignUnit( unit );
            }
            else
            {
                Unit emptyUnit = new Unit( );
                emptyUnit.setIdUnit( -1 );
                _category.setDefaultAssignUnit( emptyUnit );
            }
        }

        TicketCategoryHome.update( _category );
        addInfo( INFO_CATEGORY_UPDATED, getLocale( ) );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
        return redirectView( request, VIEW_MANAGE_CATEGORYS );
    }
}
