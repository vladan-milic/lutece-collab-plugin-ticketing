/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponse;
import fr.paris.lutece.plugins.ticketing.business.modelresponse.search.LuceneModelResponseIndexerServices;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * ModelResponseSearch
 */
@Controller( controllerJsp = "ModelResponseSearch.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_TICKETS_MANAGEMENT" )
public class ModelResponseSearchJspBean extends MVCAdminJspBean
{
    // Templates
    private static final String TEMPLATE_SEARCH_RESPONSE_RESULTS = "/admin/plugins/ticketing/search/search_response_results.html";

    // Actions
    private static final String ACTION_SEARCH_RESPONSE = "search_response";

    // Other constants
    private static final long serialVersionUID = 1L;

    /**
     * Search response for tickets
     * 
     * @param request
     *            The HTTP request
     * @return The view
     */
    @Action( value = ACTION_SEARCH_RESPONSE )
    public String searchResponse( HttpServletRequest request )
    {
        String strQuery = request.getParameter( SearchConstants.PARAMETER_QUERY );
        String strIdDomain = request.getParameter( SearchConstants.PARAMETER_DOMAIN );
        Map<String, Object> model = new HashMap<String, Object>( );

        // Create the set of id domain
        Set<String> setIdDomain = new LinkedHashSet<>( );
        String [ ] strSeqIdTickets = request.getParameterValues( TicketingConstants.PARAMETER_SELECTED_TICKETS );
        if ( strSeqIdTickets != null && strSeqIdTickets.length > 0 )
        {
            setIdDomain = getListIdDomain( strSeqIdTickets );
        }
        setIdDomain.add( strIdDomain );

        if ( StringUtils.isNotEmpty( strQuery ) )
        {
            List<ModelResponse> listResults = LuceneModelResponseIndexerServices.instance( ).searchResponses( strQuery, setIdDomain );

            model.put( SearchConstants.MARK_RESULT, listResults );
            model.put( SearchConstants.MARK_QUERY, strQuery );
        }
        else
        {
            addError( model, SearchConstants.MESSAGE_SEARCH_NO_INPUT, request.getLocale( ) );
        }
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEARCH_RESPONSE_RESULTS, getLocale( ), model );
        return template.getHtml( );
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

    /**
     * Return the set of id domain associated to the list of id ticket given in parameter
     * 
     * @param listIdTickets
     *            the list of id tickets
     * @return the set of id domain
     */
    private Set<String> getListIdDomain( String [ ] strIdTickets )
    {
        Set<String> setIdDomain = new LinkedHashSet<>( );
        List<String> listIdTickets = Arrays.asList( strIdTickets );
        if ( listIdTickets != null && !listIdTickets.isEmpty( ) )
        {
            for ( String idTicket : listIdTickets )
            {
                Ticket ticket = TicketHome.findByPrimaryKey( StringUtils.isNumeric( idTicket ) ? Integer.parseInt( idTicket ) : -1 );
                if ( ticket != null )
                {
                    setIdDomain.add( String.valueOf( ticket.getIdTicketDomain( ) ) );
                }
            }
        }
        return setIdDomain;
    }

}
