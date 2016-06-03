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

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.typicalResponse.TypicalResponse;
import fr.paris.lutece.plugins.ticketing.business.typicalResponse.TypicalResponseHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage TypeResponse features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTypicalResponses.jsp", controllerPath = "jsp/admin/plugins/ticketing/admin/", right = "TICKETING_MANAGEMENT_REPONSES_TYPE" )
public class TypicalResponseJspBean extends MVCAdminJspBean
{
    // Rights
    public static final String RIGHT_MANAGETICKETINGREPONSESTYPES = "TICKETING_MANAGEMENT_REPONSES_TYPE";

    // Templates
    private static final String TEMPLATE_MANAGE_TYPICALRESPONSES = "/admin/plugins/ticketing/admin/manage_typicalresponses.html";
    private static final String TEMPLATE_CREATE_TYPERESPONSE = "/admin/plugins/ticketing/admin/create_typicalresponse.html";
    private static final String TEMPLATE_MODIFY_TYPERESPONSE = "/admin/plugins/ticketing/admin/modify_typicalresponse.html";

    // Parameters
    private static final String PARAMETER_ID_TYPERESPONSE = "id";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TYPICALRESPONSES = "ticketing.manage_typeresponses.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TYPERESPONSE = "ticketing.modify_typeresponse.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TYPERESPONSE = "ticketing.create_typeresponse.pageTitle";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";

    // Markers
    private static final String MARK_TYPERESPONSE_LIST = "typeresponse_list";
    private static final String MARK_TYPERESPONSE = "typeresponse";

    // Markers
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String JSP_MANAGE_TYPICALRESPONSES = "jsp/admin/plugins/ticketing/admin/ManageTypicalResponses.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TYPERESPONSE = "ticketing.message.confirmRemoveTypeResponse";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.typeresponse.attribute.";

    // Views
    private static final String VIEW_MANAGE_TYPICALRESPONSES = "manageTypicalResponses";
    private static final String VIEW_CREATE_TYPERESPONSE = "createTypeResponse";
    private static final String VIEW_MODIFY_TYPERESPONSE = "modifyTypeResponse";

    // Actions
    private static final String ACTION_CREATE_TYPERESPONSE = "createTypeResponse";
    private static final String ACTION_MODIFY_TYPERESPONSE = "modifyTypeResponse";
    private static final String ACTION_REMOVE_TYPERESPONSE = "removeTypeResponse";
    private static final String ACTION_CONFIRM_REMOVE_TYPERESPONSE = "confirmRemoveTypeResponse";

    // Infos
    private static final String INFO_TYPERESPONSE_CREATED = "ticketing.info.typeresponse.created";
    private static final String INFO_TYPERESPONSE_UPDATED = "ticketing.info.typeresponse.updated";
    private static final String INFO_TYPERESPONSE_REMOVED = "ticketing.info.typeresponse.removed";

    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    // Session variable to store working values
    private TypicalResponse _typeresponse;

    /**
     * Return a model that contains the list and paginator infos
     * @param request The HTTP request
     * @param strBookmark The bookmark
     * @param list The list of item
     * @param strManageJsp The JSP
     * @return The model
     */
    protected Map<String, Object> getPaginatedListModel( HttpServletRequest request, String strBookmark, List list,
        String strManageJsp )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( strManageJsp );
        String strUrl = url.getUrl(  );

        // PAGINATOR
        LocalizedPaginator paginator = new LocalizedPaginator( list, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale(  ) );

        Map<String, Object> model = getModel(  );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( strBookmark, paginator.getPageItems(  ) );

        return model;
    }

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TYPICALRESPONSES, defaultView = true )
    public String getManageTypicalResponses( HttpServletRequest request )
    {
        _typeresponse = null;

        List<TypicalResponse> listTypicalResponses = TypicalResponseHome.getTypeResponsesList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_TYPERESPONSE_LIST, listTypicalResponses,
                JSP_MANAGE_TYPICALRESPONSES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TYPICALRESPONSES, TEMPLATE_MANAGE_TYPICALRESPONSES, model );
    }

    /**
     * Returns the form to create a typeresponse
     *
     * @param request The Http request
     * @return the html code of the typeresponse form
     */
    @View( VIEW_CREATE_TYPERESPONSE )
    public String getCreateTypeResponse( HttpServletRequest request )
    {
        _typeresponse = ( _typeresponse != null ) ? _typeresponse : new TypicalResponse(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TYPERESPONSE, _typeresponse );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TYPERESPONSE, TEMPLATE_CREATE_TYPERESPONSE, model );
    }

    /**
     * Process the data capture form of a new typeresponse
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TYPERESPONSE )
    public String doCreateTypeResponse( HttpServletRequest request )
    {
        populate( _typeresponse, request );

        // Check constraints
        if ( !validateBean( _typeresponse, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TYPERESPONSE );
        }

        TypicalResponseHome.create( _typeresponse );
        addInfo( INFO_TYPERESPONSE_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TYPICALRESPONSES );
    }

    /**
     * Manages the removal form of a typeresponse whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TYPERESPONSE )
    public String getConfirmRemoveTypeResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TYPERESPONSE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TYPERESPONSE ) );
        url.addParameter( PARAMETER_ID_TYPERESPONSE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TYPERESPONSE,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a typeresponse
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage typeresponses
     */
    @Action( ACTION_REMOVE_TYPERESPONSE )
    public String doRemoveTypeResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TYPERESPONSE ) );
        TypicalResponseHome.remove( nId );
        addInfo( INFO_TYPERESPONSE_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TYPICALRESPONSES );
    }

    /**
     * Returns the form to update info about a typeresponse
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TYPERESPONSE )
    public String getModifyTypeResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TYPERESPONSE ) );

        if ( ( _typeresponse == null ) || ( _typeresponse.getId(  ) != nId ) )
        {
            _typeresponse = TypicalResponseHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_TYPERESPONSE, _typeresponse );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TYPERESPONSE, TEMPLATE_MODIFY_TYPERESPONSE, model );
    }

    /**
     * Process the change form of a typeresponse
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TYPERESPONSE )
    public String doModifyTypeResponse( HttpServletRequest request )
    {
        populate( _typeresponse, request );

        // Check constraints
        if ( !validateBean( _typeresponse, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TYPERESPONSE, PARAMETER_ID_TYPERESPONSE, _typeresponse.getId(  ) );
        }

        TypicalResponseHome.update( _typeresponse );
        addInfo( INFO_TYPERESPONSE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TYPICALRESPONSES );
    }
}
