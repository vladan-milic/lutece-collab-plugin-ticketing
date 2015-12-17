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
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.TicketFormHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

/**
 * This class provides the user interface to manage Ticket xpages ( manage, create, modify, remove )
 */
 
@Controller( xpageName = "ticket" , pageTitleI18nKey = "ticketing.xpage.ticket.pageTitle" , pagePathI18nKey = "ticketing.xpage.ticket.pagePathLabel" )
public class TicketXPage extends MVCApplication
{
    // Templates
    private static final String TEMPLATE_CREATE_TICKET="/skin/plugins/ticketing/create_ticket.html";
    private static final String TEMPLATE_TICKET_FORM = "/skin/plugins/ticketing/ticket_form.html";
    
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_TICKET_FORM = "ticket_form";
    
    private static final String PARAMETER_ID_CATEGORY = "id_ticket_category";

    // Views
    private static final String VIEW_MANAGE_TICKETS = "manageTickets";
    private static final String VIEW_CREATE_TICKET = "createTicket";
    private static final String VIEW_TICKET_FORM = "ticketForm";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";
    
    // Session variable to store working values
    private Ticket _ticket;

    private final TicketFormService _ticketFormService = SpringContextService
            .getBean( TicketFormService.BEAN_NAME );
    
    /**
     * Returns the form to create a ticket
     *
     * @param request The Http request
     * @return the html code of the ticket form
     */
    @View( value = VIEW_CREATE_TICKET , defaultView = true )
    public XPage getCreateTicket( HttpServletRequest request )
    {
        _ticket = ( _ticket != null ) ? _ticket : new Ticket(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_USER_TITLES_LIST , UserTitleHome.getReferenceList() );
        model.put( MARK_TICKET, _ticket );
        return getXPage( TEMPLATE_CREATE_TICKET, request.getLocale( ), model );
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKET )
    public XPage doCreateTicket( HttpServletRequest request )
    {
        populate( _ticket, request );

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );
        if ( _ticket.getIdTicketCategory( ) > 0 )
        {
            EntryFilter filter = new EntryFilter( );

            filter.setIdResource( TicketFormHome.findByCategoryId( _ticket.getIdTicketCategory( ) )
                    .getIdForm( ) );
            filter.setResourceType( TicketForm.RESOURCE_TYPE );
            filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
            filter.setIdIsComment( EntryFilter.FILTER_FALSE );

            List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

            for ( Entry entry : listEntryFirstLevel )
            {
                listFormErrors.addAll( _ticketFormService.getResponseEntry( request,
                        entry.getIdEntry( ), getLocale( request ), _ticket ) );
            }

        }
        // If there is some errors, we redirect the user to the form page
        // Check constraints
        if ( !validateBean( _ticket, getLocale( request ) ) || listFormErrors.size( ) > 0 )
        {
            for ( GenericAttributeError error : listFormErrors )
            {
                if ( error.getIsDisplayableError( ) )
                    addError( error.getMessage( ) );
            }
            return redirectView( request, VIEW_CREATE_TICKET );
        }

        TicketHome.create( _ticket );

        if ( _ticket.getListResponse( ) != null && !_ticket.getListResponse( ).isEmpty( ) )
        {
            for ( Response response : _ticket.getListResponse( ) )
            {
                ResponseHome.create( response );
                TicketHome.insertTicketResponse( _ticket.getId( ), response.getIdResponse( ) );
            }
        }

        addInfo( INFO_TICKET_CREATED, getLocale( request ) );

        return redirectView( request, VIEW_MANAGE_TICKETS );
    }


    /**
     * Display ticket form linked to the category used for AJAX request ie.
     * standalone mode (no header/footer lutece)
     * 
     * @param request
     *            http request, id_ticket_category must be set
     * @return form to be displayed
     */
    @View(VIEW_TICKET_FORM)
    public XPage getTicketForm(HttpServletRequest request)
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        Map<String, Object> model = getModel( );
        if ( !StringUtils.isEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
        {
            int nIdCategory = Integer.parseInt( strIdCategory );
            TicketForm form = TicketFormHome.findByCategoryId( nIdCategory );
            if ( form != null ){
                model.put( MARK_TICKET_FORM,
                        _ticketFormService.getHtmlForm( form, request.getLocale( ), false, request ) );
            }
        }
        XPage page = getXPage( TEMPLATE_TICKET_FORM, request.getLocale( ), model );
        page.setStandalone( true );
        return page;
    }

}
