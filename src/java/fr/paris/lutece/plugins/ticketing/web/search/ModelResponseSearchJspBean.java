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
import fr.paris.lutece.plugins.ticketing.business.modelresponse.search.IModelResponseIndexer;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;

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
    
    // Messages
    private static final String MESSAGE_INFO_TOO_MANY_RESULTS = "ticketing.search_ticket.tooManyResults";

    // Other constants
    private static final long serialVersionUID = 1L;
    
    // Variables
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( SearchConstants.PROPERTY_DEFAULT_RESPONSE_MODEL_ITEM_PER_PAGE, 10 );
    private int _nMaxResponsePerQuery = AppPropertiesService.getPropertyInt( SearchConstants.PROPERTY_MODEL_RESPONSE_LIMIT_PER_QUERY, 100 );

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
            List<ModelResponse> listResults = new ArrayList<>( );
			IModelResponseIndexer modelResponseIndexer = SpringContextService.getBean( IModelResponseIndexer.BEAN_SERVICE );
            List<ModelResponse> listFullResults = modelResponseIndexer.searchResponses( strQuery, setIdDomain );

            // Add an info message if there are more response than maximum limit 
            if( listFullResults.size( ) > _nMaxResponsePerQuery ){
                listResults = listFullResults.subList( 0, _nMaxResponsePerQuery );
                
                Object [ ] args = {
                        _nMaxResponsePerQuery, listFullResults.size( )
                };
                List<ErrorMessage> listInfos = new ArrayList<ErrorMessage>(  );
                listInfos.add( new MVCMessage( I18nService.getLocalizedString( MESSAGE_INFO_TOO_MANY_RESULTS, args, getLocale( ) ) ) );
                model.put( SearchConstants.MARK_INFOS, listInfos );
            }
            else
            {
                listResults = listFullResults;
            }

            model.put( SearchConstants.MARK_RESULT, listResults );
            model.put( SearchConstants.MARK_QUERY, strQuery );
            
            // Add a Paginator for the list of the responses
            _strCurrentPageIndex = Paginator.getPageIndex( request, SearchConstants.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
            _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );
            
            LocalizedPaginator<ModelResponse> paginator = new LocalizedPaginator<ModelResponse>( listResults, _nItemsPerPage, StringUtils.EMPTY, SearchConstants.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale( ) );           
            model.put( SearchConstants.MARK_RESULT, paginator.getPageItems( ) );
            model.put( SearchConstants.MARK_QUERY, strQuery );
            model.put( SearchConstants.MARK_PAGINATOR, paginator );
            model.put( SearchConstants.MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
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
