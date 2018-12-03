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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.ws.rs.Path;

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * REST service for contact mode resource
 *
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.CONTACT_MODE_PATH )
public class SphinxRest
{

    private static final String URL         = "daemon.sphinxDaemon.url";
    private static final String URL_MAILING = "daemon.sphinxDaemon.url.mailing";
    private static final String URL_TOKEN   = "daemon.sphinxDaemon.token.url";
    private static final String USERNAME    = "daemon.sphinxDaemon.unsername";
    private static final String PASSWORD    = "daemon.sphinxDaemon.password";

    /**
     * Gives the contact modes
     *
     * @throws Exception
     */
    // @GET
    // @Path( Constants.ALL_PATH )
    // public Response getContactModes( @HeaderParam( HttpHeaders.ACCEPT ) String accept, @QueryParam( Constants.FORMAT_QUERY ) String format )
    // {
    // String strMediaType = getMediaType( accept, format );
    //
    // IFormatterFactory formatterFactory = _formatterFactories.get( strMediaType );
    //
    // List<ContactMode> listUserTitles = ContactModeHome.getContactModesList( );
    //
    // String strResponse = formatterFactory.createFormatter( ContactMode.class ).format( listUserTitles );
    //
    // return Response.ok( strResponse, strMediaType ).build( );
    // }
    public static void getHttpCon( ) throws Exception
    {

        String tokenUrl = AppPropertiesService.getProperty( URL_TOKEN );
        String username = AppPropertiesService.getProperty( USERNAME );
        String password = AppPropertiesService.getProperty( PASSWORD );

        String POST_PARAMS = "username=" + username + "&password=" + password + "&lang=fr&grant_type=password&client_id=sphinxapiclient";
        URL obj = new URL( tokenUrl );
        HttpURLConnection con = ( HttpURLConnection ) obj.openConnection( );
        con.setRequestMethod( "POST" );
        con.setRequestProperty( "Content-Type", "application/json;odata=verbose" );
        con.setRequestProperty( "Authorization", "Basic Base64_encoded_clientId:clientSecret" );
        con.setRequestProperty( "Accept", "application/x-www-form-urlencoded" );

        // For POST only - START
        con.setDoOutput( true );
        OutputStream os = con.getOutputStream( );
        os.write( POST_PARAMS.getBytes( ) );
        os.flush( );
        os.close( );
        // For POST only - END

        int responseCode = con.getResponseCode( );
        System.out.println( "POST Response Code :: " + responseCode );

        if ( responseCode == HttpURLConnection.HTTP_OK )
        { // success
            BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream( ) ) );
            String inputLine;
            StringBuffer response = new StringBuffer( );

            while ( ( inputLine = in.readLine( ) ) != null )
            {
                response.append( inputLine );
            }
            in.close( );

            // print result
            System.out.println( response.toString( ) );
        } else
        {
            System.out.println( "POST request not worked" );
        }
    }

    public static void main( String[] args ) throws Exception
    {
        getHttpCon( );
    }

}
