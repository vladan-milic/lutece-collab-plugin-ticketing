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
package fr.paris.lutece.plugins.ticketing.web.rs;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import fr.paris.lutece.plugins.ticketing.service.format.FormatterJsonFactory;
import fr.paris.lutece.plugins.ticketing.service.format.FormatterXmlFactory;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatterFactory;

/**
 * Common method used in REST services for ticketing resources
 *
 */
public class TicketingRest
{
    protected static final Map<String, IFormatterFactory> _formatterFactories = new HashMap<String, IFormatterFactory>( );

    static
    {
        _formatterFactories.put( MediaType.APPLICATION_JSON, new FormatterJsonFactory( ) );
        _formatterFactories.put( MediaType.APPLICATION_XML, new FormatterXmlFactory( ) );
    }

    /**
     * Default constructor
     */
    public TicketingRest( )
    {
    }

    /**
     * Gives the media type depending on the specified parameters
     * 
     * @param accept
     *            the accepted format
     * @param format
     *            the format
     * @return the media type
     */
    protected String getMediaType( String accept, String format )
    {
        String strMediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) || ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            strMediaType = MediaType.APPLICATION_JSON;
        } else
        {
            strMediaType = MediaType.APPLICATION_XML;
        }

        return strMediaType;
    }
}
