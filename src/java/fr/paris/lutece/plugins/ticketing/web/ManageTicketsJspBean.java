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
package fr.paris.lutece.plugins.ticketing.web;

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.UserTitleHome;
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
 * ManageTickets JSP Bean abstract class for JSP Bean
 */

/**
 * This class provides the user interface to manage Ticket features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTickets.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "TICKETING_TICKETS_MANAGEMENT" )
public class ManageTicketsJspBean extends MVCAdminJspBean
{
    // Right
    public static final String RIGHT_MANAGETICKETS = "TICKETING_TICKETS_MANAGEMENT";
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETS = "/admin/plugins/ticketing/manage_tickets.html";
    private static final String TEMPLATE_CREATE_TICKET = "/admin/plugins/ticketing/create_ticket.html";
    private static final String TEMPLATE_MODIFY_TICKET = "/admin/plugins/ticketing/modify_ticket.html";

    // Parameters
    private static final String PARAMETER_ID_TICKET = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETS = "ticketing.manage_tickets.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKET = "ticketing.modify_ticket.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKET = "ticketing.create_ticket.pageTitle";

    // Markers
    private static final String MARK_TICKET_LIST = "ticket_list";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String JSP_MANAGE_TICKETS = "jsp/admin/plugins/ticketing/ManageTickets.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKET = "ticketing.message.confirmRemoveTicket";
    private static final String PROPERTY_DEFAULT_LIST_TICKET_PER_PAGE = "ticketing.listTickets.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticket.attribute.";

    // Views
    private static final String VIEW_MANAGE_TICKETS = "manageTickets";
    private static final String VIEW_CREATE_TICKET = "createTicket";
    private static final String VIEW_MODIFY_TICKET = "modifyTicket";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";
    private static final String ACTION_MODIFY_TICKET = "modifyTicket";
    private static final String ACTION_REMOVE_TICKET = "removeTicket";
    private static final String ACTION_CONFIRM_REMOVE_TICKET = "confirmRemoveTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";
    private static final String INFO_TICKET_UPDATED = "ticketing.info.ticket.updated";
    private static final String INFO_TICKET_REMOVED = "ticketing.info.ticket.removed";
    private static final long serialVersionUID = 1L;

    // Session variable to store working values
    private Ticket _ticket;

    //Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;


    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETS, defaultView = true )
    public String getManageTickets( HttpServletRequest request )
    {
        _ticket = null;

        List<Ticket> listTickets = (List<Ticket>) TicketHome.getTicketsList(  );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( JSP_MANAGE_TICKETS );
        String strUrl = url.getUrl(  );

        // PAGINATOR
        LocalizedPaginator paginator = new LocalizedPaginator( listTickets, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale(  ) );

        Map<String, Object> model = getModel(  );
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_TICKET_LIST, paginator.getPageItems(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETS, TEMPLATE_MANAGE_TICKETS, model );
    }

    /**
     * Returns the form to create a ticket
     *
     * @param request The Http request
     * @return the html code of the ticket form
     */
    @View( VIEW_CREATE_TICKET )
    public String getCreateTicket( HttpServletRequest request )
    {
        _ticket = ( _ticket != null ) ? _ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList(  ) );
        // FIXME Dynamic filling
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put( MARK_TICKET, _ticket );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKET, TEMPLATE_CREATE_TICKET, model );
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKET )
    public String doCreateTicket( HttpServletRequest request )
    {
        populate( _ticket, request );

        // Check constraints
        if ( !validateBean( _ticket, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_TICKET );
        }

        TicketHome.create( _ticket );
        addInfo( INFO_TICKET_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * Manages the removal form of a ticket whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKET )
    public String getConfirmRemoveTicket( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKET ) );
        url.addParameter( PARAMETER_ID_TICKET, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKET,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a ticket
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage tickets
     */
    @Action( ACTION_REMOVE_TICKET )
    public String doRemoveTicket( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );
        TicketHome.remove( nId );
        addInfo( INFO_TICKET_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }

    /**
     * Returns the form to update info about a ticket
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKET )
    public String getModifyTicket( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKET ) );

        if ( ( _ticket == null ) || ( _ticket.getId(  ) != nId ) )
        {
            _ticket = TicketHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList(  ) );
        // FIXME Dynamic filling
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put( MARK_TICKET, _ticket );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKET, TEMPLATE_MODIFY_TICKET, model );
    }

    /**
     * Process the change form of a ticket
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKET )
    public String doModifyTicket( HttpServletRequest request )
    {
        populate( _ticket, request );

        // Check constraints
        if ( !validateBean( _ticket, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_TICKET, PARAMETER_ID_TICKET, _ticket.getId(  ) );
        }

        TicketHome.update( _ticket );
        addInfo( INFO_TICKET_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }
}
