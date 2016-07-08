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

import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponse;
import fr.paris.lutece.plugins.ticketing.business.modelresponse.search.LuceneModelResponseIndexerServices;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.ErrorMessage;

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
@Controller( xpageName = TicketSearchXPage.XPAGE_NAME, pageTitleI18nKey = TicketSearchXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = TicketSearchXPage.MESSAGE_PATH )
public class TicketSearchXPage extends MVCApplication
{
    protected static final String XPAGE_NAME = "ticketSearch";
    protected static final String MESSAGE_PAGE_TITLE = "ticketing.xpage.ticketsearch.pageTitle";
    protected static final String MESSAGE_PATH = "ticketing.xpage.ticketsearch.pagePathLabel";

    // Templates
    private static final String TEMPLATE_SEARCH_RESPONSE_RESULTS = "/admin/plugins/ticketing/search/search_response_results.html";

    // Actions
    private static final String ACTION_SEARCH_RESPONSE = "search_response";

    // Other constants
    private static final long serialVersionUID = 1L;

    /**
     * Search response for tickets
     * @param request The HTTP request
     * @return The view
     */
    @Action( value = ACTION_SEARCH_RESPONSE )
    @SuppressWarnings( {"rawtypes",
        "unchecked"
    } )
    public XPage searchResponse( HttpServletRequest request )
    {
        String strQuery = request.getParameter( SearchConstants.PARAMETER_QUERY );
        String strDomain = request.getParameter( SearchConstants.PARAMETER_DOMAIN );
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( StringUtils.isNotEmpty( strQuery ) )
        {
            List<ModelResponse> listResults = LuceneModelResponseIndexerServices.instance(  )
                                                                                .searchResponses( strQuery, strDomain );

            model.put( SearchConstants.MARK_RESULT, listResults );
            model.put( SearchConstants.MARK_QUERY, strQuery );
        }
        else
        {
            addError( model, SearchConstants.MESSAGE_SEARCH_NO_INPUT, request.getLocale(  ) );
        }

        XPage page = getXPage( TEMPLATE_SEARCH_RESPONSE_RESULTS, request.getLocale(  ), model );
        page.setStandalone( true );

        return page;
    }

    /**
     * add error to model
     * @param model model
     * @param strMessageKey message key
     * @param locale locale
     */
    @SuppressWarnings( "unchecked" )
    protected void addError( Map<String, Object> model, String strMessageKey, Locale locale )
    {
        if ( model.get( SearchConstants.MARK_ERRORS ) == null )
        {
            List<ErrorMessage> listErrors = new ArrayList<ErrorMessage>(  );
            model.put( SearchConstants.MARK_ERRORS, listErrors );
        }

        ( (List<ErrorMessage>) model.get( SearchConstants.MARK_ERRORS ) ).add( new MVCMessage( 
                I18nService.getLocalizedString( strMessageKey, locale ) ) );
    }
}
