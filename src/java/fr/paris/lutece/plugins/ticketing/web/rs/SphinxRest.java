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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * REST service for contact mode resource
 *
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.SPHINX_PATH )
public class SphinxRest
{

    private static final String USERNAME_PROP    = "daemon.sphinxDaemon.unsername";
    private static final String PASSWORD_PROP    = "daemon.sphinxDaemon.password";
    private static final String URL_TOKEN        = "daemon.sphinxDaemon.token.url";
    private static final String URL_TOKEN_SPHINX = AppPropertiesService.getProperty( URL_TOKEN );
    private static final String USERNAME         = AppPropertiesService.getProperty( USERNAME_PROP );
    private static final String PASSWORD         = AppPropertiesService.getProperty( PASSWORD_PROP );

    /**
     *
     * @return Response
     */
    @GET
    @Path( Constants.ALL_PATH )
    public Response getToken( )
    {
        String result = "";
        try
        {
            result = getTokenAccess( );
        } catch ( Exception e )
        {
            return Response.ok( "ko" ).build( );
        }

        return Response.ok( result ).build( );
    }

    public static String getTokenAccess( ) throws HttpAccessException
    {
        HttpAccess httpAccess = new HttpAccess( );
        Map<String, String> headersRequest = new HashMap<String, String>( );
        headersRequest.put( "Content-Type", "application/x-www-form-urlencoded" );
        Map<String, String> params = new HashMap<String, String>( );
        params.put( "username", USERNAME );
        params.put( "password", PASSWORD );
        params.put( "lang", "fr" );
        params.put( "grant_type", "password" );
        params.put( "client_id", "sphinxapiclient" );

        return httpAccess.doPost( URL_TOKEN_SPHINX, params, null, null, headersRequest );
    }

}
