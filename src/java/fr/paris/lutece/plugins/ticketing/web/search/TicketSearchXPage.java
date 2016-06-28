
package fr.paris.lutece.plugins.ticketing.web.search;

import fr.paris.lutece.plugins.ticketing.business.typicalresponse.TypicalResponse;
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
        Map<String, Object> model = new HashMap<String, Object>(  );

        
        if ( StringUtils.isNotEmpty( strQuery ) )
        {
       
            
            List<TypicalResponse> listResults = LuceneModelResponseIndexerServices.instance().searchResponses(strQuery);
            
           
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
