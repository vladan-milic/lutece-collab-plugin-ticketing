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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
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

    private static final String USERNAME_PROP        = "daemon.sphinxDaemon.username";
    private static final String PASSWORD_PROP        = "daemon.sphinxDaemon.password";
    private static final String URL_TOKEN            = "daemon.sphinxDaemon.token.url";
    private static final String URL_TOKEN_SPHINX     = AppPropertiesService.getProperty( URL_TOKEN );
    private static final String USERNAME             = AppPropertiesService.getProperty( USERNAME_PROP );
    private static final String PASSWORD             = AppPropertiesService.getProperty( PASSWORD_PROP );

    private static final String API_URL              = AppPropertiesService.getProperty( "daemon.sphinxDaemon.url" );
    private static final String SURVEY               = AppPropertiesService.getProperty( "ticketing.sphinx.survey" );

    private static final String ACCESS_TOKEN         = "access_token";

    private static final String COLUMN_EMAIL         = "email";
    private static final String COLUMN_CREATION_DATE = "Date_de_creation";
    private static final String COLUMN_CATEGORY_1    = "domaine";
    private static final String COLUMN_CATEGORY_2    = "thematique";
    private static final String COLUMN_CATEGORY_3    = "sous_thematique";
    private static final String COLUMN_CATEGORY_4    = "localisation";
    private static final String COLUMN_CHANNEL       = "Canal";
    private static final String COLUMN_ASSIGN_ENTITY = "Entite_d_assignation";
    private static final String COLUMN_CLOSE_DATE    = "Date_de_cloture";
    private static final String COLUMN_DAYS_OPENED   = "delai_en_jours";

    /**
     *
     * @param idTicket
     *            ticket
     * @return response
     */
    @GET
    @Path( Constants.ID_TICKET_PATH )
    public Response getToken( @PathParam( Constants.TICKET ) Integer idTicket )
    {
        try
        {
            Ticket ticket = TicketHome.findByPrimaryKey( idTicket );
            postTicketData( ticket );
        } catch ( Exception e )
        {
            return Response.ok( "ko" ).build( );
        }

        return Response.ok( "ok" ).build( );
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

        String rawData = httpAccess.doPost( URL_TOKEN_SPHINX, params, null, null, headersRequest );
        JsonObject dataJson = new JsonParser( ).parse( rawData ).getAsJsonObject( );
        return dataJson.get( ACCESS_TOKEN ).getAsString( );
    }

    public void post( String endpoint, String json ) throws HttpAccessException
    {
        HttpAccess httpAccess = new HttpAccess( );
        Map<String, String> headersRequest = new HashMap<String, String>( );
        headersRequest.put( "Authorization", "bearer " + getTokenAccess( ) );
        httpAccess.doPostJSON( API_URL + endpoint, json, headersRequest, null );
    }

    public void postTicketData( Ticket ticket ) throws HttpAccessException
    {
        JsonObject ticketJson = new JsonObject( );

        ticketJson.addProperty( COLUMN_EMAIL, ticket.getEmail( ) );

        String creationDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( ticket.getDateCreate( ) );
        ticketJson.addProperty( COLUMN_CREATION_DATE, creationDate );

        if ( ticket.getCategoryDepth( 0 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_1, ticket.getCategoryDepth( 0 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 1 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_2, ticket.getCategoryDepth( 1 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 2 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_3, ticket.getCategoryDepth( 2 ).getLabel( ) );
        }
        if ( ticket.getCategoryDepth( 3 ) != null )
        {
            ticketJson.addProperty( COLUMN_CATEGORY_4, ticket.getCategoryDepth( 3 ).getLabel( ) );
        }
        if ( ticket.getChannel( ) != null )
        {
            ticketJson.addProperty( COLUMN_CHANNEL, ticket.getChannel( ).getLabel( ) );
        }
        if ( ticket.getAssigneeUnit( ) != null )
        {
            ticketJson.addProperty( COLUMN_ASSIGN_ENTITY, ticket.getAssigneeUnit( ).getName( ) );
        }

        if ( ticket.getDateClose( ) != null )
        {
            String closeDate = new SimpleDateFormat( "dd/MM/yyyy" ).format( ticket.getDateClose( ) );
            ticketJson.addProperty( COLUMN_CLOSE_DATE, closeDate );

            ticketJson.addProperty( COLUMN_DAYS_OPENED, ( int ) ( ticket.getDateClose( ).getTime( ) - ticket.getDateCreate( ).getTime( ) ) / ( 24 * 60 * 60 * 1000 ) );
        }

        JsonArray ticketsJson = new JsonArray( );
        ticketsJson.add( ticketJson );

        post( "/api/v4.0/survey/" + SURVEY + "/data", ticketsJson.toString( ) );
    }

}
