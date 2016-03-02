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
package fr.paris.lutece.plugins.ticketing.web.digitalsafe;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentFilter;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.business.spaces.DocumentSpace;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.library.business.LibraryMapping;
import fr.paris.lutece.plugins.library.business.LibraryMappingHome;
import fr.paris.lutece.plugins.library.business.LibraryMedia;
import fr.paris.lutece.plugins.library.business.LibraryMediaHome;
import fr.paris.lutece.plugins.library.business.MediaAttributeHome;
import fr.paris.lutece.plugins.library.business.SelectedMedia;
import fr.paris.lutece.plugins.ticketing.web.TicketHelper;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * Controller for digital safe
 */
public class DigitalSafeServiceXPage implements XPageApplication
{
    /**
     * Generated serial id
     */
    private static final long serialVersionUID = 1570837733837434504L;

    // Templates
    private static final String TEMPLATE_MEDIA_SELECTOR = "admin/plugins/ticketing/digitalsafe/media_selector.html";

    // Parameters
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";
    private static final String PARAMETER_MEDIA_TYPE = "media_type";
    private static final String PARAMETER_SPACE_ID = "space_id";
    private static final String PARAMETER_INPUT = "input";
    private static final String PARAMETER_NB_ITEMS_PER_PAGE = "nb_items";
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // JSP
    private static final String JSP_MEDIA_TYPE_SELECTION = "jsp/admin/plugins/library/SelectMedia.jsp";

    // Marks
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String MARK_SELECTED_SPACE = "selected_space";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_SELECTED_MEDIAS = "selected_medias";
    private static final String MARK_ALL_DOCUMENTS = "all_documents";

    // Constants
    private static final String DEFAULT_RESULTS_PER_PAGE = "10";
    private static final String PROPERTY_RESULTS_PER_PAGE = "library.nbdocsperpage";
    private static final String SPACE_ID_SESSION = "spaceIdSession";
    private static final String DEFAULT_PAGE_INDEX = "1";
    private static final int APPROVED_DOCUMENT_STATE = 3;

    // Local session variables
    private AdminUser _user;
    private Plugin _plugin;
    private String _input;
    private int _nNbItemsPerPage;
    private String _strCurrentPageIndex;
    private List<SelectedMedia> _listSelectedMedia;

    /**
     * {@inheritDoc}
     */
    @Override
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws UserNotSignedException, SiteMessageException
    {
        XPage page = new XPage(  );

        TicketHelper.registerDefaultAdminUser( request );

        init( request );

        String strMediaType = request.getParameter( PARAMETER_MEDIA_TYPE );

        String strSpaceIdRequest = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );
        int nSpaceId = -1;

        if ( ( strSpaceIdRequest != null ) && ( strSpaceIdRequest.length(  ) > 0 ) )
        {
            nSpaceId = Integer.parseInt( strSpaceIdRequest );
            request.getSession(  ).setAttribute( SPACE_ID_SESSION, nSpaceId );
        }
        else
        {
            Integer nSpaceIdTmp = (Integer) request.getSession(  ).getAttribute( SPACE_ID_SESSION );

            if ( nSpaceIdTmp != null )
            {
                nSpaceId = nSpaceIdTmp.intValue(  );
            }
        }

        LibraryMedia mediaType = LibraryMediaHome.findByPrimaryKey( Integer.parseInt( strMediaType ), _plugin );
        mediaType.setMediaAttributeList( MediaAttributeHome.findAllAttributesForMedia( mediaType.getMediaId(  ), _plugin ) );

        Collection<LibraryMapping> allMappings = LibraryMappingHome.findAllMappingsByMedia( mediaType.getMediaId(  ),
                _plugin );
        Collection<DocumentSpace> spaces = DocumentSpacesService.getInstance(  ).getUserAllowedSpaces( _user );

        List<Pair<String, Document>> listDocuments = new ArrayList<Pair<String, Document>>(  );

        for ( LibraryMapping mapping : allMappings )
        {
            listDocuments.addAll( getDocumentsFromMapping( mapping, nSpaceId, spaces ) );
        }

        Paginator<Pair<String, Document>> paginator = getPaginator( request, listDocuments );
        HashMap<String, Object> model = getDefaultModel(  );
        model.put( MARK_ALL_DOCUMENTS, paginator.getPageItems(  ) );
        model.put( MARK_SPACES_BROWSER,
            DocumentSpacesService.getInstance(  ).getSpacesBrowser( request, _user, _user.getLocale(  ), true, true ) );
        model.put( MARK_SELECTED_SPACE, nSpaceId );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, Integer.toString( paginator.getItemsPerPage(  ) ) );
        model.put( MARK_SELECTED_MEDIAS, _listSelectedMedia );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MEDIA_SELECTOR, _user.getLocale(  ), model );

        page.setContent( template.getHtml(  ) );
        page.setStandalone( true );

        return page;
    }

    /**
     * Gives the paginator
     * @param request the request
     * @param list the document list
     * @return the paginator
     */
    private Paginator<Pair<String, Document>> getPaginator( HttpServletRequest request,
        List<Pair<String, Document>> list )
    {
        String strNbItemPerPage = request.getParameter( Paginator.PARAMETER_ITEMS_PER_PAGE );

        if ( StringUtils.isNotEmpty( strNbItemPerPage ) && StringUtils.isNumeric( strNbItemPerPage ) )
        {
            _nNbItemsPerPage = Integer.parseInt( strNbItemPerPage );
        }

        if ( _nNbItemsPerPage <= 0 )
        {
            String strDefaultNbItemPerPage = AppPropertiesService.getProperty( PROPERTY_RESULTS_PER_PAGE,
                    DEFAULT_RESULTS_PER_PAGE );
            _nNbItemsPerPage = Integer.parseInt( strDefaultNbItemPerPage );
        }

        String strCurrentPageIndex = request.getParameter( Paginator.PARAMETER_PAGE_INDEX );

        if ( StringUtils.isNotEmpty( strCurrentPageIndex ) && StringUtils.isNumeric( strCurrentPageIndex ) )
        {
            _strCurrentPageIndex = strCurrentPageIndex;
        }

        if ( StringUtils.isEmpty( _strCurrentPageIndex ) )
        {
            _strCurrentPageIndex = DEFAULT_PAGE_INDEX;
        }

        UrlItem url = new UrlItem( JSP_MEDIA_TYPE_SELECTION );
        url.addParameter( PARAMETER_INPUT, _input );
        url.addParameter( PARAMETER_PLUGIN_NAME, _plugin.getName(  ) );

        String strSpaceId = request.getParameter( PARAMETER_SPACE_ID );

        if ( strSpaceId != null )
        {
            url.addParameter( PARAMETER_SPACE_ID, strSpaceId );
        }

        url.addParameter( PARAMETER_MEDIA_TYPE, request.getParameter( PARAMETER_MEDIA_TYPE ) );
        url.addParameter( PARAMETER_NB_ITEMS_PER_PAGE, _nNbItemsPerPage );

        return new Paginator<Pair<String, Document>>( list, _nNbItemsPerPage, url.getUrl(  ), PARAMETER_PAGE_INDEX,
            _strCurrentPageIndex );
    }

    /**
     * Gives the list of documents from mapping
     * @param m the mapping
     * @param nSpaceId the space id
     * @param spaces the spaces
     * @return the list of documents
     */
    private List<Pair<String, Document>> getDocumentsFromMapping( LibraryMapping m, int nSpaceId,
        Collection<DocumentSpace> spaces )
    {
        DocumentFilter filter = new DocumentFilter(  );
        filter.setCodeDocumentType( m.getCodeDocumentType(  ) );
        filter.setIdState( APPROVED_DOCUMENT_STATE );

        List<Document> documents = null;
        List<Pair<String, Document>> result = new ArrayList<Pair<String, Document>>(  );

        if ( nSpaceId >= 0 )
        {
            if ( !DocumentSpacesService.getInstance(  ).isAuthorizedViewByRole( nSpaceId, _user ) ||
                    !DocumentSpacesService.getInstance(  ).isAuthorizedViewByWorkgroup( nSpaceId, _user ) )
            {
                return null;
            }

            filter.setIdSpace( nSpaceId );
            documents = DocumentHome.findByFilter( filter, _user.getLocale(  ) );
        }

        /* else
         {
             documents = new ArrayList<Document>(  );
        
             for ( DocumentSpace space : spaces )
             {
                 filter.setIdSpace( space.getId(  ) );
                 documents.addAll( DocumentHome.findByFilter( filter, _user.getLocale(  ) ) );
             }
         }*/
        if ( documents != null )
        {
            for ( Document document : documents )
            {
                DocumentHome.loadAttributes( document );
                result.add( new Pair<String, Document>( String.valueOf( m.getIdMapping(  ) ), document ) );
            }
        }

        return result;
    }

    /**
     * Initializes the controller
     * @param request the request
     */
    private void init( HttpServletRequest request )
    {
        String strPluginName = request.getParameter( PARAMETER_PLUGIN_NAME );
        _user = AdminUserService.getAdminUser( request );

        if ( StringUtils.isNotBlank( strPluginName ) )
        {
            _plugin = PluginService.getPlugin( strPluginName );
        }

        String strInput = request.getParameter( PARAMETER_INPUT );

        if ( StringUtils.isNotBlank( strInput ) )
        {
            _input = strInput;
        }
    }

    /**
     * Gives the default model
     * @return the default model
     */
    private HashMap<String, Object> getDefaultModel(  )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( PARAMETER_PLUGIN_NAME, _plugin.getName(  ) );
        model.put( PARAMETER_INPUT, _input );

        return model;
    }

    /**
     * This class represents a pair
     *
     * @param <X> the first value
     * @param <Y> the second value
     */
    public class Pair<X, Y>
    {
        private X _first;
        private Y _second;

        /**
         * Contructor
         * @param x the first value
         * @param y the second value
         */
        public Pair( X x, Y y )
        {
            _first = x;
            _second = y;
        }

        /**
         * Gets the first value
         * @return the first value
         */
        public X getFirst(  )
        {
            return _first;
        }

        /**
         * Gets the second value
         * @return the second value
         */
        public Y getSecond(  )
        {
            return _second;
        }
    }
}
