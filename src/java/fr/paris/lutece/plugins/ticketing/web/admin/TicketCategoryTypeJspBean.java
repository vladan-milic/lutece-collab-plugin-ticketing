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

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryTreeCacheService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage CategoryType features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageCategoryTypes.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "ticketing_MANAGEMENT" )
public class TicketCategoryTypeJspBean extends ManageAdminTicketingJspBean
{
    // Templates
    private static final String TEMPLATE_CREATE_CATEGORYTYPE = "/admin/plugins/ticketing/create_categorytype.html";
    private static final String TEMPLATE_MODIFY_CATEGORYTYPE = "/admin/plugins/ticketing/modify_categorytype.html";

    // Parameters
    private static final String PARAMETER_ID_CATEGORYTYPE = "id_category_type";

    // Properties for page title
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORYTYPE = "ticketing.modify_categorytype.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORYTYPE = "ticketing.create_categorytype.pageTitle";

    // Markers
    private static final String MARK_CATEGORYTYPE = "categorytype";

    private static final String JSP_MANAGE_CATEGORIES = "jsp/admin/plugins/ticketing/ManageCategorys.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORYTYPE = "ticketing.message.confirmRemoveCategoryType";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.categorytype.attribute.";

    // Views
    private static final String VIEW_CREATE_CATEGORYTYPE = "createCategoryType";
    private static final String VIEW_MODIFY_CATEGORYTYPE = "modifyCategoryType";

    // Actions
    private static final String ACTION_CREATE_CATEGORYTYPE = "createCategoryType";
    private static final String ACTION_MODIFY_CATEGORYTYPE = "modifyCategoryType";
    private static final String ACTION_REMOVE_CATEGORYTYPE = "removeCategoryType";
    private static final String ACTION_CONFIRM_REMOVE_CATEGORYTYPE = "confirmRemoveCategoryType";

    // Infos
    private static final String INFO_CATEGORYTYPE_CREATED = "ticketing.info.categorytype.created";
    private static final String INFO_CATEGORYTYPE_UPDATED = "ticketing.info.categorytype.updated";
    private static final String INFO_CATEGORYTYPE_REMOVED = "ticketing.info.categorytype.removed";

    // Session variable to store working values
    private TicketCategoryType _categorytype;

    /**
     * Returns the form to create a categorytype
     *
     * @param request
     *            The Http request
     * @return the html code of the categorytype form
     */
    @View( VIEW_CREATE_CATEGORYTYPE )
    public String getCreateCategoryType( HttpServletRequest request )
    {
        _categorytype = ( _categorytype != null ) ? _categorytype : new TicketCategoryType( );

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORYTYPE, _categorytype );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CATEGORYTYPE, TEMPLATE_CREATE_CATEGORYTYPE, model );
    }

    /**
     * Process the data capture form of a new categorytype
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_CATEGORYTYPE )
    public String doCreateCategoryType( HttpServletRequest request )
    {
        populate( _categorytype, request );

        // Check constraints
        if ( !validateBean( _categorytype, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CATEGORYTYPE );
        }

        TicketCategoryTypeHome.create( _categorytype );
        addInfo( INFO_CATEGORYTYPE_CREATED, getLocale( ) );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
        return redirect( request, AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORIES );
    }

    /**
     * Manages the removal form of a categorytype whose identifier is in the http request
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
     * Handles the removal form of a categorytype
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage categorytypes
     */
    @Action( ACTION_REMOVE_CATEGORYTYPE )
    public String doRemoveCategoryType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORYTYPE ) );
        TicketCategoryTypeHome.remove( nId );
        addInfo( INFO_CATEGORYTYPE_REMOVED, getLocale( ) );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
        return redirect( request, AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORIES );
    }

    /**
     * Returns the form to update info about a categorytype
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CATEGORYTYPE )
    public String getModifyCategoryType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CATEGORYTYPE ) );

        if ( _categorytype == null || ( _categorytype.getId( ) != nId ) )
        {
            _categorytype = TicketCategoryTypeHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORYTYPE, _categorytype );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CATEGORYTYPE, TEMPLATE_MODIFY_CATEGORYTYPE, model );
    }

    /**
     * Process the change form of a categorytype
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_CATEGORYTYPE )
    public String doModifyCategoryType( HttpServletRequest request )
    {
        populate( _categorytype, request );

        // Check constraints
        if ( !validateBean( _categorytype, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CATEGORYTYPE, PARAMETER_ID_CATEGORYTYPE, _categorytype.getId( ) );
        }

        TicketCategoryTypeHome.update( _categorytype );
        addInfo( INFO_CATEGORYTYPE_UPDATED, getLocale( ) );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
        return redirect( request, AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORIES );
    }
}
