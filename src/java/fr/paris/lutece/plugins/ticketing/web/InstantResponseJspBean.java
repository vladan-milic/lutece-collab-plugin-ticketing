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

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.instantresponse.InstantResponse;
import fr.paris.lutece.plugins.ticketing.business.instantresponse.InstantResponseHome;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidatorResult;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage InstantResponse features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageInstantResponses.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_INSTANT_RESPONSE_MANAGEMENT" )
public class InstantResponseJspBean extends MVCAdminJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants

    private static final long serialVersionUID = 8473463434018604540L;

    // templates
    private static final String TEMPLATE_MANAGE_INSTANT_RESPONSES = TicketingConstants.TEMPLATE_ADMIN_INSTANTRESPONSE_FEATURE_PATH
            + "manage_instant_responses.html";
    private static final String TEMPLATE_CREATE_INSTANT_RESPONSE = TicketingConstants.TEMPLATE_ADMIN_INSTANTRESPONSE_FEATURE_PATH
            + "create_instant_response.html";
    private static final String TEMPLATE_MODIFY_INSTANT_RESPONSE = TicketingConstants.TEMPLATE_ADMIN_INSTANTRESPONSE_FEATURE_PATH
            + "modify_instant_response.html";

    // Parameters
    private static final String PARAMETER_ID_INSTANT_RESPONSE = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_INSTANT_RESPONSES = "ticketing.manage_instantresponses.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_INSTANT_RESPONSE = "ticketing.modify_instantresponse.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_INSTANT_RESPONSE = "ticketing.create_instantresponse.pageTitle";

    // Markers
    private static final String MARK_INSTANT_RESPONSE_LIST = "instantresponse_list";
    private static final String MARK_INSTANT_RESPONSE = "instantresponse";
    private static final String JSP_MANAGE_INSTANT_RESPONSES = TicketingConstants.ADMIN_CONTROLLLER_PATH + "ManageInstantResponses.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_INSTANT_RESPONSE = "ticketing.message.confirmRemoveInstantResponse";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.instantresponse.attribute.";

    // Views
    private static final String VIEW_MANAGE_INSTANT_RESPONSES = "manageInstantResponses";
    private static final String VIEW_CREATE_INSTANT_RESPONSE = "createInstantResponse";
    private static final String VIEW_MODIFY_INSTANT_RESPONSE = "modifyInstantResponse";

    // Actions
    private static final String ACTION_CREATE_INSTANT_RESPONSE = "createInstantResponse";
    private static final String ACTION_MODIFY_INSTANT_RESPONSE = "modifyInstantResponse";
    private static final String ACTION_REMOVE_INSTANT_RESPONSE = "removeInstantResponse";
    private static final String ACTION_CONFIRM_REMOVE_INSTANT_RESPONSE = "confirmRemoveInstantResponse";

    // Infos
    private static final String INFO_INSTANT_RESPONSE_CREATED = "ticketing.info.instantresponse.created";
    private static final String INFO_INSTANT_RESPONSE_UPDATED = "ticketing.info.instantresponse.updated";
    private static final String INFO_INSTANT_RESPONSE_REMOVED = "ticketing.info.instantresponse.removed";

    // Right
    public static final String RIGHT_MANAGEINSTANTRESPONSE = "TICKETING_INSTANT_RESPONSE_MANAGEMENT";
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static boolean _bAvatarAvailable = PluginService.isPluginEnable( TicketingConstants.PLUGIN_AVATAR );

    // Session variable to store working values
    private InstantResponse _instantresponse;

    // Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Return a model that contains the list and paginator infos
     * 
     * @param request
     *            The HTTP request
     * @param strBookmark
     *            The bookmark
     * @param listInstantResponse
     *            The list of item
     * @param strManageJsp
     *            The JSP
     * @return The model
     */
    protected Map<String, Object> getPaginatedListModel( HttpServletRequest request, String strBookmark, List<InstantResponse> listInstantResponse,
            String strManageJsp )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( strManageJsp );
        String strUrl = url.getUrl( );

        // PAGINATOR
        LocalizedPaginator<InstantResponse> paginator = new LocalizedPaginator<>( listInstantResponse, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        Map<String, Object> model = getModel( );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( strBookmark, paginator.getPageItems( ) );

        return model;
    }

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_INSTANT_RESPONSES, defaultView = true )
    public String getManageInstantResponses( HttpServletRequest request )
    {
        _instantresponse = null;

        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( ( request.getParameter( TicketingConstants.PARAMETER_BACK ) != null ) && StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }

        List<InstantResponse> listInstantResponses = InstantResponseHome.getInstantResponsesList( );
        Map<String, Object> model = getPaginatedListModel( request, MARK_INSTANT_RESPONSE_LIST, listInstantResponses, JSP_MANAGE_INSTANT_RESPONSES );

        model.put( TicketingConstants.MARK_AVATAR_AVAILABLE, _bAvatarAvailable );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_INSTANT_RESPONSES, TEMPLATE_MANAGE_INSTANT_RESPONSES, model );
    }

    /**
     * Returns the form to create a instantresponse
     *
     * @param request
     *            The Http request
     * @return the html code of the instantresponse form
     */
    @View( VIEW_CREATE_INSTANT_RESPONSE )
    public String getCreateInstantResponse( HttpServletRequest request )
    {
        _instantresponse = ( _instantresponse != null ) ? _instantresponse : new InstantResponse( );

        Map<String, Object> model = getModel( );
        model.put( MARK_INSTANT_RESPONSE, _instantresponse );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( ).getCategoriesTree( ).getDepths( ) );
        ModelUtils.storeChannels( request, model );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_INSTANT_RESPONSE, TEMPLATE_CREATE_INSTANT_RESPONSE, model );
    }

    /**
     * Process the data capture form of a new instantresponse
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_INSTANT_RESPONSE )
    public String doCreateInstantResponse( HttpServletRequest request )
    {
        populate( _instantresponse, request );

        // Validate the TicketCategory
        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request, getLocale() ).validateTicketCategory( );

        // Check constraints
        if ( !validateBean( _instantresponse, VALIDATION_ATTRIBUTES_PREFIX ) || !categoryValidatorResult.isTicketCategoryValid( ) )
        {
            return redirectView( request, VIEW_CREATE_INSTANT_RESPONSE);
        }

        int nUserId = getUser( ).getUserId( );
        _instantresponse.setIdAdminUser( nUserId );
        _instantresponse.setIdUnit( UnitHome.findByIdUser( nUserId ).get( 0 ).getIdUnit( ) );
        _instantresponse.setDateCreate( new Timestamp( ( new Date( ) ).getTime( ) ) );
        _instantresponse.setTicketCategory( categoryValidatorResult.getTicketCategory( ) );

        InstantResponseHome.create( _instantresponse );

        return redirectAfterCreateAction( request );
    }

    /**
     * Manages the removal form of a instantresponse whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_INSTANT_RESPONSE )
    public String getConfirmRemoveInstantResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_INSTANT_RESPONSE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_INSTANT_RESPONSE ) );
        url.addParameter( PARAMETER_ID_INSTANT_RESPONSE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_INSTANT_RESPONSE, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a instantresponse
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage instantresponses
     */
    @Action( ACTION_REMOVE_INSTANT_RESPONSE )
    public String doRemoveInstantResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_INSTANT_RESPONSE ) );
        InstantResponseHome.remove( nId );
        addInfo( INFO_INSTANT_RESPONSE_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_INSTANT_RESPONSES );
    }

    /**
     * Returns the form to update info about a instantresponse
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_INSTANT_RESPONSE )
    public String getModifyInstantResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_INSTANT_RESPONSE ) );

        if ( ( _instantresponse == null ) || ( _instantresponse.getId( ) != nId ) )
        {
            _instantresponse = InstantResponseHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_INSTANT_RESPONSE, _instantresponse );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getDepths( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_INSTANT_RESPONSE, TEMPLATE_MODIFY_INSTANT_RESPONSE, model );
    }

    /**
     * Process the change form of a instantresponse
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_INSTANT_RESPONSE )
    public String doModifyInstantResponse( HttpServletRequest request )
    {
        populate( _instantresponse, request );

        // Validate the TicketCategory
        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request, getLocale() ).validateTicketCategory( );

        // Check constraints
        if ( !validateBean( _instantresponse, VALIDATION_ATTRIBUTES_PREFIX ) || !categoryValidatorResult.isTicketCategoryValid( ) )
        {
            Map<String, Object> model = getModel( );
            model.put( PARAMETER_ID_INSTANT_RESPONSE, String.valueOf( _instantresponse.getId( ) ) );
            model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getTreeJSONObject( ) );
            model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getDepths( ) );

            return redirectView( request, VIEW_MODIFY_INSTANT_RESPONSE );
        }

        _instantresponse.setTicketCategory( categoryValidatorResult.getTicketCategory( ) );
        InstantResponseHome.update( _instantresponse );
        addInfo( INFO_INSTANT_RESPONSE_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_INSTANT_RESPONSES );
    }

    /**
     * Computes redirection for creation action
     * 
     * @param request
     *            http request
     * @return redirect view
     */
    private String redirectAfterCreateAction( HttpServletRequest request )
    {
        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }

        addInfo( INFO_INSTANT_RESPONSE_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_INSTANT_RESPONSES );
    }

}
