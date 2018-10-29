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
package fr.paris.lutece.plugins.ticketing.service.entrytype;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeFile;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractGenAttUploadHandler;
import fr.paris.lutece.plugins.ticketing.business.file.TicketFileHome;
import fr.paris.lutece.plugins.ticketing.service.authentication.RequestAuthenticationService;
import fr.paris.lutece.plugins.ticketing.service.download.TicketingFileServlet;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 *
 * class EntryTypeImage
 *
 */
public class EntryTypeFile extends AbstractEntryTypeFile
{
    /**
     * Name of the bean of this service
     */
    public static final String  BEAN_NAME                = "ticketing.entryTypeFile";

    // Templates
    private static final String TEMPLATE_CREATE          = "admin/plugins/ticketing/entries/create_entry_type_file.html";
    private static final String TEMPLATE_MODIFY          = "admin/plugins/ticketing/entries/modify_entry_type_file.html";
    private static final String TEMPLATE_HTML_CODE       = "skin/plugins/ticketing/entries/html_code_entry_type_file.html";
    private static final String TEMPLATE_HTML_CODE_ADMIN = "admin/plugins/ticketing/entries/html_code_entry_type_file.html";
    private static final String TEMPLATE_READ_ONLY_HTML  = "admin/plugins/ticketing/entries/read_only_entry_type_file.html";

    // Markers
    private static final String MARK_FILE_URL            = "file_url";
    private static final String MARK_DATE_ARCHIVED       = "date_archived";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        return bDisplayFront ? TEMPLATE_HTML_CODE : TEMPLATE_HTML_CODE_ADMIN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        Map<String, Object> model = EntryTypeUtils.initModel( entry, response );

        if ( ( response.getFile( ) != null ) && StringUtils.isNotBlank( response.getFile( ).getTitle( ) ) )
        {
            if ( response.getIdResponse( ) > 0 )
            {
                model.put( MARK_FILE_URL, getUrlDownloadFile( response.getIdResponse( ), AppPathService.getBaseUrl( request ) ) );
                model.put( MARK_DATE_ARCHIVED, TicketFileHome.getMigrationDate( response.getFile( ) ) );
            }
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_READ_ONLY_HTML, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkForImages( )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractGenAttUploadHandler getAsynchronousUploadHandler( )
    {
        return TicketAsynchronousUploadHandler.getHandler( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlDownloadFile( int nResponseId, String strBaseUrl )
    {
        List<String> listElements = new ArrayList<String>( );
        listElements.add( Integer.toString( nResponseId ) );

        String strTimestamp = Long.toString( new Date( ).getTime( ) );
        String strSignature = RequestAuthenticationService.getRequestAuthenticator( ).buildSignature( listElements, strTimestamp );

        UrlItem url = new UrlItem( strBaseUrl + TicketingFileServlet.URL_SERVLET );
        url.addParameter( TicketingFileServlet.PARAMETER_ID_RESPONSE, nResponseId );
        url.addParameter( TicketingConstants.PARAMETER_SIGNATURE, strSignature );
        url.addParameter( TicketingConstants.PARAMETER_TIMESTAMP, strTimestamp );

        return url.getUrl( );
    }
}
