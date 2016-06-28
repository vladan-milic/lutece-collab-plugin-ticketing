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
import fr.paris.lutece.plugins.ticketing.business.typicalresponse.TypicalResponse;
import fr.paris.lutece.plugins.ticketing.business.typicalresponse.TypicalResponseHome;
import fr.paris.lutece.plugins.ticketing.business.modelresponse.search.LuceneModelResponseIndexerServices;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage TypicalResponse features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTypicalResponses.jsp", controllerPath = "jsp/admin/plugins/ticketing/admin/", right = "TICKETING_MANAGEMENT_TYPICAL_RESPONSE" )
public class TypicalResponseJspBean extends MVCAdminJspBean
{
    // Rights
    public static final String RIGHT_MANAGETICKETINGREPONSESTYPES = "TICKETING_MANAGEMENT_TYPICAL_RESPONSE";

    // Templates
    private static final String TEMPLATE_MANAGE_TYPICALRESPONSES = "/admin/plugins/ticketing/admin/manage_typicalresponses.html";
    private static final String TEMPLATE_CREATE_TYPICALRESPONSE = "/admin/plugins/ticketing/admin/create_typicalresponse.html";
    private static final String TEMPLATE_MODIFY_TYPICALRESPONSE = "/admin/plugins/ticketing/admin/modify_typicalresponse.html";

    // Parameters
    private static final String PARAMETER_ID_TYPICALRESPONSE = "id";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TYPICALRESPONSES = "ticketing.manage_typicalresponse.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TYPICALRESPONSE = "ticketing.modify_typicalresponse.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TYPICALRESPONSE = "ticketing.create_typicalresponse.pageTitle";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";

    // Markers
    private static final String MARK_TYPICALRESPONSE_LIST = "typicalresponse_list";
    private static final String MARK_TYPICALRESPONSE = "typicalresponse";

    // Markers
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String JSP_MANAGE_TYPICALRESPONSES = "jsp/admin/plugins/ticketing/admin/ManageTypicalResponses.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TYPICALRESPONSE = "ticketing.message.confirmRemoveTypicalResponse";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.typicalresponse.attribute.";

    // Views
    private static final String VIEW_MANAGE_TYPICALRESPONSES = "manageTypicalResponses";
    private static final String VIEW_CREATE_TYPICALRESPONSE = "createTypicalResponse";
    private static final String VIEW_MODIFY_TYPICALRESPONSE = "modifyTypicalResponse";

    // Actions
    private static final String ACTION_CREATE_TYPICALRESPONSE = "createTypicalResponse";
    private static final String ACTION_MODIFY_TYPICALRESPONSE = "modifyTypicalResponse";
    private static final String ACTION_REMOVE_TYPICALRESPONSE = "removeTypicalResponse";
    private static final String ACTION_CONFIRM_REMOVE_TYPICALRESPONSE = "confirmRemoveTypicalResponse";

    // Infos
    private static final String INFO_TYPICALRESPONSE_CREATED = "ticketing.info.typicalresponse.created";
    private static final String INFO_TYPICALRESPONSE_UPDATED = "ticketing.info.typicalresponse.updated";
    private static final String INFO_TYPICALRESPONSE_REMOVED = "ticketing.info.typicalresponse.removed";

    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    
   

    // Session variable to store working values
    private TypicalResponse _typicalResponse;

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
        _typicalResponse = null;

        List<TypicalResponse> listTypicalResponses = TypicalResponseHome.getTypicalResponsesList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_TYPICALRESPONSE_LIST, listTypicalResponses,
                JSP_MANAGE_TYPICALRESPONSES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TYPICALRESPONSES, TEMPLATE_MANAGE_TYPICALRESPONSES, model );
    }

    /**
     * Returns the form to create a typicalresponse
     *
     * @param request The Http request
     * @return the html code of the typicalresponse form
     */
    @View( VIEW_CREATE_TYPICALRESPONSE )
    public String getCreateTypicalResponse( HttpServletRequest request )
    {
        _typicalResponse = ( _typicalResponse != null ) ? _typicalResponse : new TypicalResponse(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TYPICALRESPONSE, _typicalResponse );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );

        ModelUtils.storeRichText( request, model );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TYPICALRESPONSE, TEMPLATE_CREATE_TYPICALRESPONSE, model );
    }

    /**
     * Process the data capture form of a new typicalresponse
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TYPICALRESPONSE )
    public String doCreateTypicalResponse( HttpServletRequest request )
    {
        populate( _typicalResponse, request );

        // Check constraints
        if ( !validateBean( _typicalResponse, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TYPICALRESPONSE );
        }

        TypicalResponseHome.create( _typicalResponse );
        
       
        try
        {
            LuceneModelResponseIndexerServices.instance().add(_typicalResponse);
        } 
        catch (IOException ex) 
        {
            AppLogService.error("\n Ticketing - TypicalResponseJspBean : can't add index odel response",ex );
        }
      
        
        addInfo( INFO_TYPICALRESPONSE_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TYPICALRESPONSES );
    }

    /**
     * Manages the removal form of a typicalresponse whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TYPICALRESPONSE )
    public String getConfirmRemoveTypicalResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TYPICALRESPONSE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TYPICALRESPONSE ) );
        url.addParameter( PARAMETER_ID_TYPICALRESPONSE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TYPICALRESPONSE,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a typicalresponse
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage typicalresponses
     */
    @Action( ACTION_REMOVE_TYPICALRESPONSE )
    public String doRemoveTypicalResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TYPICALRESPONSE ) );
        
        try {
            LuceneModelResponseIndexerServices.instance().delete(TypicalResponseHome.findByPrimaryKey(nId));
        }
        catch (IOException ex) 
        {
            AppLogService.error("\n Ticketing - TypicalResponseJspBean : can't remove index odel response",ex );
        }
        
        TypicalResponseHome.remove( nId );
        addInfo( INFO_TYPICALRESPONSE_REMOVED, getLocale(  ) );
        
        
       
        

        return redirectView( request, VIEW_MANAGE_TYPICALRESPONSES );
    }

    /**
     * Returns the form to update info about a typicalresponse
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TYPICALRESPONSE )
    public String getModifyTypicalResponse( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TYPICALRESPONSE ) );

        if ( ( _typicalResponse == null ) || ( _typicalResponse.getId(  ) != nId ) )
        {
            _typicalResponse = TypicalResponseHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_TYPICALRESPONSE, _typicalResponse );

        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );

        ModelUtils.storeRichText( request, model );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TYPICALRESPONSE, TEMPLATE_MODIFY_TYPICALRESPONSE, model );
    }

    /**
     * Process the change form of a typicalresponse
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TYPICALRESPONSE )
    public String doModifyTypicalResponse( HttpServletRequest request )
    {
        populate( _typicalResponse, request );

        // Check constraints
        if ( !validateBean( _typicalResponse, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TYPICALRESPONSE, PARAMETER_ID_TYPICALRESPONSE,
                _typicalResponse.getId(  ) );
        }

        TypicalResponseHome.update( _typicalResponse );
        
          try
        {
            LuceneModelResponseIndexerServices.instance().update(_typicalResponse);
        }
          catch (IOException ex) 
        {
            AppLogService.error("\n Ticketing - TypicalResponseJspBean : can't update index model response",ex );
        }
         
        addInfo( INFO_TYPICALRESPONSE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TYPICALRESPONSES );
    }
}
