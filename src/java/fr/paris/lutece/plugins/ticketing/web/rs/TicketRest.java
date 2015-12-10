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

import net.sf.json.JSONObject;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.rest.util.json.JSONUtil;
import fr.paris.lutece.plugins.rest.util.xml.XMLUtil;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 * Page resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.TICKET_PATH )
public class TicketRest
{
    private static final String KEY_TICKETS = "tickets";
    private static final String KEY_TICKET = "ticket";
    private static final String KEY_ID = "id";
    private static final String KEY_ID_USER_TITLE = "id_user_title";
    private static final String KEY_USER_TITLE = "user_title";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIXED_PHONE_NUMBER = "fixed_phone_number";
    private static final String KEY_MOBILE_PHONE_NUMBER = "mobile_phone_number";
    private static final String KEY_ID_TICKET_TYPE = "id_ticket_type";
    private static final String KEY_TICKET_TYPE = "ticket_type";
    private static final String KEY_ID_TICKET_DOMAIN = "id_ticket_domain";
    private static final String KEY_TICKET_DOMAIN = "ticket_domain";
    private static final String KEY_ID_TICKET_CATEGORY = "id_ticket_category";
    private static final String KEY_TICKET_CATEGORY = "ticket_category";
    private static final String KEY_ID_CONTACT_MODE = "id_contact_mode";
    private static final String KEY_CONTACT_MODE = "contact_mode";
    private static final String KEY_TICKET_COMMENT = "ticket_comment";
    private static final String KEY_TICKET_STATUS = "ticket_status";
    private static final String KEY_TICKET_STATUS_TEXT = "ticket_status_text";

    @GET
    @Path( Constants.ALL_PATH )
    public Response getTickets( @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        String entity;
        String mediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) ||
                ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getTicketsJson(  );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getTicketsXml(  );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build(  );
    }

    /**
     * Gets all resources list in XML format
     * @return The list
     */
    public String getTicketsXml(  )
    {
        StringBuffer sbXML = new StringBuffer( XmlUtil.getXmlHeader(  ) );
        Collection<Ticket> list = TicketHome.getTicketsList(  );

        XmlUtil.beginElement( sbXML, KEY_TICKETS );

        for ( Ticket ticket : list )
        {
            addTicketXml( sbXML, ticket );
        }

        XmlUtil.endElement( sbXML, KEY_TICKETS );

        return sbXML.toString(  );
    }

    /**
     * Gets all resources list in JSON format
     * @return The list
     */
    public String getTicketsJson(  )
    {
        JSONObject jsonTicket = new JSONObject(  );
        JSONObject json = new JSONObject(  );

        Collection<Ticket> list = TicketHome.getTicketsList(  );

        for ( Ticket ticket : list )
        {
            addTicketJson( jsonTicket, ticket );
        }

        json.accumulate( KEY_TICKETS, jsonTicket );

        return json.toString(  );
    }

    @GET
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response getTicket( @PathParam( Constants.ID_PATH )
    String strId, @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        String entity;
        String mediaType;

        if ( ( ( accept != null ) && accept.contains( MediaType.APPLICATION_JSON ) ) ||
                ( ( format != null ) && format.equals( Constants.MEDIA_TYPE_JSON ) ) )
        {
            entity = getTicketJson( strId );
            mediaType = MediaType.APPLICATION_JSON;
        }
        else
        {
            entity = getTicketXml( strId );
            mediaType = MediaType.APPLICATION_XML;
        }

        return Response.ok( entity, mediaType ).build(  );
    }

    /**
     * Gets a resource in XML format
     * @param strId The resource ID
     * @return The XML output
     */
    public String getTicketXml( String strId )
    {
        StringBuffer sbXML = new StringBuffer(  );

        try
        {
            int nId = Integer.parseInt( strId );
            Ticket ticket = TicketHome.findByPrimaryKey( nId );

            if ( ticket != null )
            {
                sbXML.append( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" );
                addTicketXml( sbXML, ticket );
            }
        }
        catch ( NumberFormatException e )
        {
            sbXML.append( XMLUtil.formatError( "Invalid ticket number", 3 ) );
        }
        catch ( Exception e )
        {
            sbXML.append( XMLUtil.formatError( "Ticket not found", 1 ) );
        }

        return sbXML.toString(  );
    }

    /**
     * Gets a resource in JSON format
     * @param strId The resource ID
     * @return The JSON output
     */
    public String getTicketJson( String strId )
    {
        JSONObject json = new JSONObject(  );
        String strJson = "";

        try
        {
            int nId = Integer.parseInt( strId );
            Ticket ticket = TicketHome.findByPrimaryKey( nId );

            if ( ticket != null )
            {
                addTicketJson( json, ticket );
                strJson = json.toString(  );
            }
        }
        catch ( NumberFormatException e )
        {
            strJson = JSONUtil.formatError( "Invalid ticket number", 3 );
        }
        catch ( Exception e )
        {
            strJson = JSONUtil.formatError( "Ticket not found", 1 );
        }

        return strJson;
    }

    @DELETE
    @Path( "{" + Constants.ID_PATH + "}" )
    public Response deleteTicket( @PathParam( Constants.ID_PATH )
    String strId, @HeaderParam( HttpHeaders.ACCEPT )
    String accept, @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        try
        {
            int nId = Integer.parseInt( strId );

            if ( TicketHome.findByPrimaryKey( nId ) != null )
            {
                TicketHome.remove( nId );
            }
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( "Invalid ticket number" );
        }

        return getTickets( accept, format );
    }

    @POST
    public Response createTicket( @FormParam( KEY_ID ) String id,
            @FormParam( "id_user_title" ) String id_user_title,
            @FormParam( "user_title" ) String user_title,
            @FormParam( "firstname" ) String firstname,
            @FormParam( "lastname" ) String lastname,
            @FormParam( "email" ) String email,
            @FormParam( "fixed_phone_number" ) String fixed_phone_number,
            @FormParam( "mobile_phone_number" ) String mobile_phone_number,
            @FormParam( "id_ticket_type" ) String id_ticket_type,
            @FormParam( "ticket_type" ) String ticket_type,
            @FormParam( "id_ticket_domain" ) String id_ticket_domain,
            @FormParam( "ticket_domain" ) String ticket_domain,
            @FormParam( "id_ticket_category" ) String id_ticket_category,
            @FormParam( "ticket_category" ) String ticket_category,
            @FormParam( "id_contact_mode" ) String id_contact_mode,
            @FormParam( "contact_mode" ) String contact_mode,
            @FormParam( "ticket_comment" ) String ticket_comment,
            @FormParam( "ticket_status" ) String ticket_status,
            @FormParam( "ticket_status_text" ) String ticket_status_text,
            @HeaderParam( HttpHeaders.ACCEPT ) String accept,
            @QueryParam( Constants.FORMAT_QUERY )
    String format ) throws IOException
    {
        if ( id != null )
        {
            int nId = Integer.parseInt( KEY_ID );

            Ticket ticket = TicketHome.findByPrimaryKey( nId );

            if ( ticket != null )
            {
                ticket.setIdUserTitle( Integer.parseInt( id_user_title ) );
                ticket.setUserTitle( user_title );
                ticket.setFirstname( firstname );
                ticket.setLastname( lastname );
                ticket.setEmail( email );
                ticket.setFixedPhoneNumber ( fixed_phone_number );
                ticket.setMobilePhoneNumber ( mobile_phone_number );
                ticket.setIdTicketType( Integer.parseInt( id_ticket_type ) );
                ticket.setTicketType( ticket_type );
                ticket.setIdTicketDomain( Integer.parseInt( id_ticket_domain ) );
                ticket.setTicketDomain( ticket_domain );
                ticket.setIdTicketCategory( Integer.parseInt( id_ticket_category ) );
                ticket.setTicketCategory( ticket_category );
                ticket.setIdContactMode ( Integer.parseInt ( id_contact_mode ) );
                ticket.setContactMode ( contact_mode );
                ticket.setTicketComment( ticket_comment );
                ticket.setTicketStatus( Integer.parseInt( ticket_status ) );
                ticket.setTicketStatusText( ticket_status_text );
                TicketHome.update( ticket );
            }
        }
        else
        {
            Ticket ticket = new Ticket(  );

            ticket.setIdUserTitle( Integer.parseInt( id_user_title ) );
            ticket.setUserTitle( user_title );
            ticket.setFirstname( firstname );
            ticket.setLastname( lastname );
            ticket.setEmail( email );
            ticket.setFixedPhoneNumber ( fixed_phone_number );
            ticket.setMobilePhoneNumber ( mobile_phone_number );
            ticket.setIdTicketType( Integer.parseInt( id_ticket_type ) );
            ticket.setTicketType( ticket_type );
            ticket.setIdTicketDomain( Integer.parseInt( id_ticket_domain ) );
            ticket.setTicketDomain( ticket_domain );
            ticket.setIdTicketCategory( Integer.parseInt( id_ticket_category ) );
            ticket.setTicketCategory( ticket_category );
            ticket.setIdContactMode ( Integer.parseInt ( id_contact_mode ) );
            ticket.setContactMode ( contact_mode );
            ticket.setTicketComment( ticket_comment );
            ticket.setTicketStatus( Integer.parseInt( ticket_status ) );
            ticket.setTicketStatusText( ticket_status_text );
            TicketHome.create( ticket );
        }

        return getTickets( accept, format );
    }

    /**
     * Write a ticket into a buffer
     * @param sbXML The buffer
     * @param ticket The ticket
     */
    private void addTicketXml( StringBuffer sbXML, Ticket ticket )
    {
        XmlUtil.beginElement( sbXML, KEY_TICKET );
        XmlUtil.addElement( sbXML, KEY_ID, ticket.getId(  ) );
        XmlUtil.addElement( sbXML, KEY_ID_USER_TITLE, ticket.getIdUserTitle(  ) );
        XmlUtil.addElement( sbXML, KEY_USER_TITLE, ticket.getUserTitle(  ) );
        XmlUtil.addElement( sbXML, KEY_FIRSTNAME, ticket.getFirstname(  ) );
        XmlUtil.addElement( sbXML, KEY_LASTNAME, ticket.getLastname(  ) );
        XmlUtil.addElement( sbXML, KEY_EMAIL, ticket.getEmail(  ) );
        XmlUtil.addElement ( sbXML, KEY_FIXED_PHONE_NUMBER,
                ticket.getFixedPhoneNumber ( ) );
        XmlUtil.addElement ( sbXML, KEY_MOBILE_PHONE_NUMBER,
                ticket.getMobilePhoneNumber ( ) );
        XmlUtil.addElement( sbXML, KEY_ID_TICKET_TYPE, ticket.getIdTicketType(  ) );
        XmlUtil.addElement( sbXML, KEY_TICKET_TYPE, ticket.getTicketType(  ) );
        XmlUtil.addElement( sbXML, KEY_ID_TICKET_DOMAIN, ticket.getIdTicketDomain(  ) );
        XmlUtil.addElement( sbXML, KEY_TICKET_DOMAIN, ticket.getTicketDomain(  ) );
        XmlUtil.addElement( sbXML, KEY_ID_TICKET_CATEGORY, ticket.getIdTicketCategory(  ) );
        XmlUtil.addElement( sbXML, KEY_TICKET_CATEGORY, ticket.getTicketCategory(  ) );
        XmlUtil.addElement ( sbXML, KEY_ID_CONTACT_MODE,
                ticket.getIdContactMode ( ) );
        XmlUtil.addElement ( sbXML, KEY_CONTACT_MODE, ticket.getContactMode ( ) );
        XmlUtil.addElement ( sbXML, KEY_TICKET_COMMENT,
                ticket.getTicketComment ( ) );
        XmlUtil.addElement( sbXML, KEY_TICKET_STATUS, ticket.getTicketStatus(  ) );
        XmlUtil.addElement( sbXML, KEY_TICKET_STATUS_TEXT, ticket.getTicketStatusText(  ) );
        XmlUtil.endElement( sbXML, KEY_TICKET );
    }

    /**
     * Write a ticket into a JSON Object
     * @param json The JSON Object
     * @param ticket The ticket
     */
    private void addTicketJson( JSONObject json, Ticket ticket )
    {
        JSONObject jsonTicket = new JSONObject(  );
        jsonTicket.accumulate( KEY_ID, ticket.getId(  ) );
        jsonTicket.accumulate( KEY_ID_USER_TITLE, ticket.getIdUserTitle(  ) );
        jsonTicket.accumulate( KEY_USER_TITLE, ticket.getUserTitle(  ) );
        jsonTicket.accumulate( KEY_FIRSTNAME, ticket.getFirstname(  ) );
        jsonTicket.accumulate( KEY_LASTNAME, ticket.getLastname(  ) );
        jsonTicket.accumulate( KEY_EMAIL, ticket.getEmail(  ) );
        jsonTicket.accumulate ( KEY_FIXED_PHONE_NUMBER,
                ticket.getFixedPhoneNumber ( ) );
        jsonTicket.accumulate ( KEY_MOBILE_PHONE_NUMBER,
                ticket.getMobilePhoneNumber ( ) );
        jsonTicket.accumulate( KEY_ID_TICKET_TYPE, ticket.getIdTicketType(  ) );
        jsonTicket.accumulate( KEY_TICKET_TYPE, ticket.getTicketType(  ) );
        jsonTicket.accumulate( KEY_ID_TICKET_DOMAIN, ticket.getIdTicketDomain(  ) );
        jsonTicket.accumulate( KEY_TICKET_DOMAIN, ticket.getTicketDomain(  ) );
        jsonTicket.accumulate( KEY_ID_TICKET_CATEGORY, ticket.getIdTicketCategory(  ) );
        jsonTicket.accumulate( KEY_TICKET_CATEGORY, ticket.getTicketCategory(  ) );
        jsonTicket
                .accumulate ( KEY_ID_CONTACT_MODE, ticket.getIdContactMode ( ) );
        jsonTicket.accumulate ( KEY_CONTACT_MODE, ticket.getContactMode ( ) );
        jsonTicket.accumulate( KEY_TICKET_COMMENT, ticket.getTicketComment(  ) );
        jsonTicket.accumulate( KEY_TICKET_STATUS, ticket.getTicketStatus(  ) );
        jsonTicket.accumulate( KEY_TICKET_STATUS_TEXT, ticket.getTicketStatusText(  ) );
        json.accumulate( KEY_TICKET, jsonTicket );
    }
}
