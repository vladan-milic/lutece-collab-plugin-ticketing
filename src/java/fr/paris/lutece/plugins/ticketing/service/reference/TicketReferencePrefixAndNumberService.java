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
package fr.paris.lutece.plugins.ticketing.service.reference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import fr.paris.lutece.plugins.ticketing.business.reference.ITicketReferenceDAO;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class manages ticket reference in the following format : <prefix><date><sequence>
 *
 */
public class TicketReferencePrefixAndNumberService implements ITicketReferenceService
{
    private static final int SEQUENCE_INITIAL_VALUE = 0;
    private static final String REFERENCE_FORMAT = "%s%05d";
    private static final String DATE_FORMAT = "yyMM";
    private static final String PATTERN_REFERENCE_PREFIX = "^[A-Z]{3}$";
    private static final String PATTERN_REFERENCE = "\\b[A-Z]{3}\\d{9}\\b";

    private static final String MARK_URL_REFERENCE = "reference_url";

    private static final String TEMPLATE_COMMENT_URL = "/admin/plugins/ticketing/ticket/comment_url_reference_ticket.html";

    private static Pattern _patternReferencePrefix = Pattern.compile( PATTERN_REFERENCE_PREFIX );
    private SimpleDateFormat _simpleDateFormat = new SimpleDateFormat( DATE_FORMAT );
    private static List<String> _listPrefixReference = new ArrayList<>( );
    private ITicketReferenceDAO _dao;

    /**
     * Constructor of a TicketReferencePrefixAndNumberService
     * 
     * @param ticketReferenceDAO
     *            the dao to access the ticket reference
     */
    public TicketReferencePrefixAndNumberService( ITicketReferenceDAO ticketReferenceDAO )
    {
        _dao = ticketReferenceDAO;
        if ( _dao != null )
        {
            _listPrefixReference = _dao.findAllPrefixReference( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateReference( Ticket ticket )
    {
        TicketType ticketType = TicketTypeHome.findByPrimaryKey( ticket.getIdTicketType( ) );

        Date dateToday = new Date( );
        String strPrefixWithDate = ticketType.getReferencePrefix( ) + _simpleDateFormat.format( dateToday );

        String strSequence = _dao.findLastTicketReference( strPrefixWithDate );
        int nSequence = SEQUENCE_INITIAL_VALUE;

        if ( !StringUtil.isBlank( strSequence ) )
        {
            nSequence = Integer.parseInt( strSequence );
        }

        nSequence++;

        return String.format( REFERENCE_FORMAT, strPrefixWithDate, nSequence );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pattern getReferencePrefixPattern( )
    {
        return _patternReferencePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return TicketingPlugin.PLUGIN_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String process( HttpServletRequest request, String strContent )
    {
        if ( StringUtils.isNotBlank( strContent ) )
        {
            String strResult = strContent;

            // Detect if there is a reference in the given string
            Pattern patternReference = Pattern.compile( PATTERN_REFERENCE );
            Matcher matcherReference = patternReference.matcher( strContent );
            while ( matcherReference.find( ) )
            {
                String currentReference = matcherReference.group( );
                if ( StringUtils.isNotBlank( currentReference ) )
                {
                    // Detect if the prefix used in the current reference is a valid prefix
                    for ( String strPrefix : _listPrefixReference )
                    {
                        if ( currentReference.contains( strPrefix ) )
                        {
                            Map<String, Object> model = new HashMap<String, Object>( );
                            model.put( MARK_URL_REFERENCE, currentReference );

                            HtmlTemplate templateLink = AppTemplateService.getTemplate( TEMPLATE_COMMENT_URL, request.getLocale( ), model );
                            if ( templateLink != null )
                            {
                                strResult = strResult.replace( currentReference, templateLink.getHtml( ) );
                            }
                        }
                    }
                }
            }
            return strResult;
        }
        return strContent;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer findIdTicketByReference( String strReference )
    {
        return _dao.findIdTicketByReference( strReference );
    }
}
