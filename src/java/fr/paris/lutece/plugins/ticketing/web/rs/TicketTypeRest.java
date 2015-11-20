/*
* Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.rest.util.json.JSONUtil;
import fr.paris.lutece.plugins.rest.util.xml.XMLUtil;
import fr.paris.lutece.plugins.ticketing.business.TicketType;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.xml.XmlUtil;

import net.sf.json.JSONObject;

import java.io.IOException;

import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Page resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.TICKETTYPE_PATH )
public class TicketTypeRest
{
    private static final String KEY_TICKETTYPES = "tickettypes";
    private static final String KEY_TICKETTYPE = "tickettype";
    private static final String KEY_ID = "id";
    private static final String KEY_LABEL = "label";

    @GET
    @Path( Constants.ALL_PATH )
    public Response getTicketTypes( @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        String entity;
        String mediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) ||
                ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getTicketTypesJson(  );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getTicketTypesXml(  );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build(  );
    }

    /**
     * Gets all resources list in XML format
     * @return The list
     */
    public String getTicketTypesXml(  )
    {
        StringBuffer sbXML = new StringBuffer( XmlUtil.getXmlHeader(  ) );
        Collection<TicketType> list = TicketTypeHome.getTicketTypesList(  );

        XmlUtil.beginElement( sbXML, KEY_TICKETTYPES );

        for ( TicketType tickettype : list )
        {
            addTicketTypeXml( sbXML, tickettype );
        }

        XmlUtil.endElement( sbXML, KEY_TICKETTYPES );

        return sbXML.toString(  );
    }

    /**
     * Gets all resources list in JSON format
     * @return The list
     */
    public String getTicketTypesJson(  )
    {
        JSONObject jsonTicketType = new JSONObject(  );
        JSONObject json = new JSONObject(  );

        Collection<TicketType> list = TicketTypeHome.getTicketTypesList(  );

        for ( TicketType tickettype : list )
        {
            addTicketTypeJson( jsonTicketType, tickettype );
        }

        json.accumulate( KEY_TICKETTYPES, jsonTicketType );

        return json.toString(  );
    }

    @GET
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response getTicketType( @PathParam( Constants.ID_PATH )
    String strId, @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        String entity;
        String mediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) ||
                ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getTicketTypeJson( strId );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getTicketTypeXml( strId );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build(  );
    }

    /**
     * Gets a resource in XML format
     * @param strId The resource ID
     * @return The XML output
     */
    public String getTicketTypeXml( String strId )
    {
        StringBuffer sbXML = new StringBuffer(  );

        try
        {
            int nId = Integer.parseInt( strId );
            TicketType tickettype = TicketTypeHome.findByPrimaryKey( nId );

            if ( tickettype != null )
            {
                sbXML.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" );
                addTicketTypeXml( sbXML, tickettype );
            }
        }
        catch ( NumberFormatException e )
        {
            sbXML.append( XMLUtil.formatError( "Invalid tickettype number", 3 ) );
        }
        catch ( Exception e )
        {
            sbXML.append( XMLUtil.formatError( "TicketType not found", 1 ) );
        }

        return sbXML.toString(  );
    }

    /**
     * Gets a resource in JSON format
     * @param strId The resource ID
     * @return The JSON output
     */
    public String getTicketTypeJson( String strId )
    {
        JSONObject json = new JSONObject(  );
        String strJson = "";

        try
        {
            int nId = Integer.parseInt( strId );
            TicketType tickettype = TicketTypeHome.findByPrimaryKey( nId );

            if ( tickettype != null )
            {
                addTicketTypeJson( json, tickettype );
                strJson = json.toString(  );
            }
        }
        catch ( NumberFormatException e )
        {
            strJson = JSONUtil.formatError( "Invalid tickettype number", 3 );
        }
        catch ( Exception e )
        {
            strJson = JSONUtil.formatError( "TicketType not found", 1 );
        }

        return strJson;
    }

    @DELETE
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response deleteTicketType( @PathParam( Constants.ID_PATH )
    String strId, @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        try
        {
            int nId = Integer.parseInt( strId );

            if ( TicketTypeHome.findByPrimaryKey( nId ) != null )
            {
                TicketTypeHome.remove( nId );
            }
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( "Invalid tickettype number" );
        }

        return getTicketTypes( accept, format );
    }

    @POST
    public Response createTicketType( @FormParam( KEY_ID )
    String id, @FormParam( "label" )
    String label, @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        if ( id != null )
        {
            int nId = Integer.parseInt( KEY_ID );

            TicketType tickettype = TicketTypeHome.findByPrimaryKey( nId );

            if ( tickettype != null )
            {
                tickettype.setLabel( label );
                TicketTypeHome.update( tickettype );
            }
        }
        else
        {
            TicketType tickettype = new TicketType(  );

            tickettype.setLabel( label );
            TicketTypeHome.create( tickettype );
        }

        return getTicketTypes( accept, format );
    }

    /**
     * Write a tickettype into a buffer
     * @param sbXML The buffer
     * @param tickettype The tickettype
     */
    private void addTicketTypeXml( StringBuffer sbXML, TicketType tickettype )
    {
        XmlUtil.beginElement( sbXML, KEY_TICKETTYPE );
        XmlUtil.addElement( sbXML, KEY_ID, tickettype.getId(  ) );
        XmlUtil.addElement( sbXML, KEY_LABEL, tickettype.getLabel(  ) );
        XmlUtil.endElement( sbXML, KEY_TICKETTYPE );
    }

    /**
     * Write a tickettype into a JSON Object
     * @param json The JSON Object
     * @param tickettype The tickettype
     */
    private void addTicketTypeJson( JSONObject json, TicketType tickettype )
    {
        JSONObject jsonTicketType = new JSONObject(  );
        jsonTicketType.accumulate( KEY_ID, tickettype.getId(  ) );
        jsonTicketType.accumulate( KEY_LABEL, tickettype.getLabel(  ) );
        json.accumulate( KEY_TICKETTYPE, jsonTicketType );
    }
}
