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
package fr.paris.lutece.plugins.ticketing.web.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * TicketSearch
 */
@Controller( controllerJsp = "TicketSearch.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class TicketSearchJspBean extends MVCAdminJspBean
{
    // Templates
    private static final String TEMPLATE_SEARCH_TICKET = "/admin/plugins/ticketing/search/search_ticket.html";

    // Actions
    private static final String ACTION_SEARCH_TICKET = "search";

    // Other constants
    private static final long serialVersionUID = 1L;
    private String _strCurrentPageIndex;
    private int _nDefaultItemsPerPage;
    private int _nItemsPerPage;

    /**
     * Search tickets
     *
     * @param request
     *            The HTTP request
     * @return The view
     */
    @Action( value = ACTION_SEARCH_TICKET )
    @SuppressWarnings( {
        "rawtypes", "unchecked"
    } )
    public String searchTickets( HttpServletRequest request )
    {
        String strQuery = request.getParameter( SearchConstants.PARAMETER_QUERY );
        Map<String, Object> model = new HashMap<String, Object>( );

        if ( StringUtils.isNotEmpty( strQuery ) )
        {
            UrlItem url = new UrlItem( getActionUrl( ACTION_SEARCH_TICKET ) );
            url.addParameter( SearchConstants.PARAMETER_QUERY, strQuery );

            _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
            _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( SearchConstants.PROPERTY_DEFAULT_RESULT_PER_PAGE, 10 );
            _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

            TicketSearchEngine engine = (TicketSearchEngine) SpringContextService.getBean( SearchConstants.BEAN_SEARCH_ENGINE );
            List<Ticket> listResults;

            try
            {
                listResults = engine.searchTickets( strQuery, getUser( ), null );

                Paginator paginator = new Paginator( listResults, _nItemsPerPage, url.getUrl( ), SearchConstants.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

                model.put( SearchConstants.MARK_RESULT, paginator.getPageItems( ) );
                model.put( SearchConstants.MARK_QUERY, strQuery );
                model.put( SearchConstants.MARK_PAGINATOR, paginator );
                model.put( SearchConstants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
            }
            catch( ParseException pe )
            {
                AppLogService.error( "Error while parsing query " + strQuery, pe );
                addError( model, SearchConstants.MESSAGE_SEARCH_ERROR, getLocale( ) );
            }
        }
        else
        {
            addError( model, SearchConstants.MESSAGE_SEARCH_NO_INPUT, getLocale( ) );
        }

        return getPage( SearchConstants.PROPERTY_PAGE_TITLE_TICKET_SEARCH, TEMPLATE_SEARCH_TICKET, model );
    }

    /**
     * add error to model
     *
     * @param model
     *            model
     * @param strMessageKey
     *            message key
     * @param locale
     *            locale
     */
    @SuppressWarnings( "unchecked" )
    protected void addError( Map<String, Object> model, String strMessageKey, Locale locale )
    {
        if ( model.get( SearchConstants.MARK_ERRORS ) == null )
        {
            List<ErrorMessage> listErrors = new ArrayList<ErrorMessage>( );
            model.put( SearchConstants.MARK_ERRORS, listErrors );
        }

        ( (List<ErrorMessage>) model.get( SearchConstants.MARK_ERRORS ) ).add( new MVCMessage( I18nService.getLocalizedString( strMessageKey, locale ) ) );
    }
}
