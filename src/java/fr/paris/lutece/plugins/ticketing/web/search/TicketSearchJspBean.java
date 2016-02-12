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
package fr.paris.lutece.plugins.ticketing.web.search;

import fr.paris.lutece.plugins.ticketing.search.TicketSearchItem;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * TicketSearch
 */
@Controller( controllerJsp = "TicketSearch.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class TicketSearchJspBean extends MVCAdminJspBean
{
    // Templates
    private static final String TEMPLATE_SEARCH_TICKET = "/admin/plugins/ticketing/search/search_ticket.html";

    // Markers
    private static final String MARK_QUERY = "query";
    private static final String MARK_RESULT = "result";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_SEARCH_FIELDS_LIST = "search_fields_list";
    private static final String MARK_SEARCH_FIELD = "searched_field";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ERRORS = "errors";
    private static final String MARK_INFOS = "infos";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_TICKET_SEARCH = "ticketing.search_ticket.pageTitle";
    private static final String PROPERTY_DEFAULT_RESULT_PER_PAGE = "ticketing.search_ticket.labelItemsPerPag";
    private static final String PROPERTY_SEARCH_REFERENCE_CRITERIA = "ticketing.search_ticket.criteria.reference";
    private static final String PROPERTY_SEARCH_CONTENT_CRITERIA = "ticketing.search_ticket.criteria.content";
    private static final String PROPERTY_SEARCH_CATEGORY_CRITERIA = "ticketing.search_ticket.criteria.category";
    private static final String PROPERTY_SEARCH_NO_INPUT = "ticketing.search_ticket.noInput";

    // Parameters
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_SEARCH_FIELD = "searched_field";

    // Views
    private static final String ACTION_SEARCH = "search";

    // for search purpose
    private static final String BEAN_SEARCH_ENGINE = "ticketing.ticketSearchEngine";

    // Other constants
    private static final long serialVersionUID = 1L;
    private String _strCurrentPageIndex;
    private int _nDefaultItemsPerPage;
    private int _nItemsPerPage;

    /**
     * Search tickets
     * @param request The HTTP request
     * @return The view
     */
    @Action( value = ACTION_SEARCH )
    @SuppressWarnings( {"rawtypes",
        "unchecked"
    } )
    public String searchTickets( HttpServletRequest request )
    {
        String strQuery = request.getParameter( PARAMETER_QUERY );
        Map<String, Object> model = new HashMap<String, Object>(  );

        String strSearchField = request.getParameter( PARAMETER_SEARCH_FIELD );

        if ( StringUtils.isEmpty( strSearchField ) )
        {
            strSearchField = TicketSearchItem.FIELD_CONTENTS;
        }

        if ( StringUtils.isNotEmpty( strQuery ) )
        {
            UrlItem url = new UrlItem( getActionUrl( ACTION_SEARCH ) );
            url.addParameter( PARAMETER_QUERY, strQuery );

            _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
            _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_RESULT_PER_PAGE, 10 );
            _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                    _nDefaultItemsPerPage );

            SearchEngine engine = (SearchEngine) SpringContextService.getBean( BEAN_SEARCH_ENGINE );
            List<SearchResult> listResults = engine.getSearchResults( strQuery, request );

            Paginator paginator = new Paginator( listResults, _nItemsPerPage, url.getUrl(  ), PARAMETER_PAGE_INDEX,
                    _strCurrentPageIndex );

            model.put( MARK_RESULT, paginator.getPageItems(  ) );
            model.put( MARK_QUERY, strQuery );
            model.put( MARK_PAGINATOR, paginator );
            model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        }
        else
        {
            addError( model, PROPERTY_SEARCH_NO_INPUT, getLocale(  ) );
        }

        model.put( MARK_SEARCH_FIELDS_LIST, getCriteriaReferenceList( request.getLocale(  ) ) );
        model.put( MARK_SEARCH_FIELD, strSearchField );

        return getPage( PROPERTY_PAGE_TITLE_TICKET_SEARCH, TEMPLATE_SEARCH_TICKET, model );
    }

    /**
     * returns criteria reference list
     * @param locale the locale used to retrieve the localized messages
     * @return the ReferenceList object
     */
    private static ReferenceList getCriteriaReferenceList( Locale locale )
    {
        ReferenceList listCriterias = new ReferenceList(  );

        listCriterias.addItem( TicketSearchItem.FIELD_CONTENTS,
            I18nService.getLocalizedString( PROPERTY_SEARCH_CONTENT_CRITERIA, locale ) );
        listCriterias.addItem( TicketSearchItem.FIELD_REFERENCE,
            I18nService.getLocalizedString( PROPERTY_SEARCH_REFERENCE_CRITERIA, locale ) );
        listCriterias.addItem( TicketSearchItem.FIELD_CATEGORY,
            I18nService.getLocalizedString( PROPERTY_SEARCH_CATEGORY_CRITERIA, locale ) );

        return listCriterias;
    }

    /**
     * add error to model
     * @param model model
     * @param strMessageKey message key
     * @param locale locale
     */
    protected void addError( Map<String, Object> model, String strMessageKey, Locale locale )
    {
        if ( model.get( MARK_ERRORS ) == null )
        {
            List<ErrorMessage> listErrors = new ArrayList<ErrorMessage>(  );
            model.put( MARK_ERRORS, listErrors );
        }

        ( (List<ErrorMessage>) model.get( MARK_ERRORS ) ).add( new MVCMessage( I18nService.getLocalizedString( 
                    strMessageKey, locale ) ) );
    }
}
