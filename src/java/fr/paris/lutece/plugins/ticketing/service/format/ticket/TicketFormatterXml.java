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
package fr.paris.lutece.plugins.ticketing.service.format.ticket;

import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.ITicketingFormatter;
import fr.paris.lutece.util.xml.XmlUtil;

/**
 * XML formatter for ticket resource
 *
 */
public class TicketFormatterXml implements ITicketingFormatter<Ticket>
{
    @Override
    public String format( Ticket ticket )
    {
        StringBuffer sbXML = new StringBuffer( );

        if ( ticket != null )
        {
            sbXML.append( FormatConstants.XML_HEADER );
            add( sbXML, ticket );
        }

        return sbXML.toString( );
    }

    @Override
    public String format( List<Ticket> listTickets )
    {
        StringBuffer sbXML = new StringBuffer( );
        sbXML.append( FormatConstants.XML_HEADER );
        XmlUtil.beginElement( sbXML, FormatConstants.KEY_TICKETS );

        for ( Ticket ticket : listTickets )
        {
            add( sbXML, ticket );
        }

        XmlUtil.endElement( sbXML, FormatConstants.KEY_TICKETS );

        return sbXML.toString( );
    }

    @Override
    public String formatError( String arg0, String arg1 )
    {
        return null;
    }

    @Override
    public String formatResponse( Ticket ticket )
    {
        StringBuffer sbXML = new StringBuffer( );

        sbXML.append( FormatConstants.XML_HEADER );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_TICKET_REFERENCE, ticket.getReference( ) );

        return sbXML.toString( );
    }

    /**
     * Write a ticket into a buffer
     * 
     * @param sbXML
     *            The buffer
     * @param ticket
     *            The ticket
     */
    private void add( StringBuffer sbXML, Ticket ticket )
    {
        XmlUtil.beginElement( sbXML, FormatConstants.KEY_TICKET );

        XmlUtil.beginElement( sbXML, FormatConstants.KEY_USER );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_USER_TITLE_ID, ticket.getIdUserTitle( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_USER_FIRST_NAME, ticket.getFirstname( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_USER_LAST_NAME, ticket.getLastname( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_USER_EMAIL, ticket.getEmail( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_USER_FIXED_PHONE_NUMBER, ticket.getFixedPhoneNumber( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_USER_MOBILE_PHONE_NUMBER, ticket.getMobilePhoneNumber( ) );
        XmlUtil.endElement( sbXML, FormatConstants.KEY_USER );

        XmlUtil.addElement( sbXML, FormatConstants.KEY_TICKET_REFERENCE, ticket.getReference( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_TICKET_CATEGORY_CODE, ticket.getTicketThematic( ).getCode( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_TICKET_CONTACT_MODE_ID, ticket.getIdContactMode( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_TICKET_CHANNEL_ID, ticket.getChannel( ).getId( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_TICKET_COMMENT, ticket.getTicketComment( ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_TICKET_NOMENCLATURE, ticket.getNomenclature( ) );

        XmlUtil.endElement( sbXML, FormatConstants.KEY_TICKET );
    }
}
