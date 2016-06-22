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

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.ITicketingFormatter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * JSON formatter for ticket resource
 *
 */
public class TicketFormatterJson implements ITicketingFormatter<Ticket>
{
    @Override
    public String format( Ticket ticket )
    {
        JSONObject json = new JSONObject(  );
        String strJson = StringUtils.EMPTY;

        if ( ticket != null )
        {
            add( json, ticket );
            strJson = json.toString(  );
        }

        return strJson;
    }

    @Override
    public String format( List<Ticket> listTickets )
    {
        JSONObject json = new JSONObject(  );
        JSONArray jsonTickets = new JSONArray(  );

        for ( Ticket ticket : listTickets )
        {
            JSONObject jsonTicket = new JSONObject(  );
            add( jsonTicket, ticket );
            jsonTickets.add( jsonTicket );
        }

        json.accumulate( FormatConstants.KEY_TICKETS, jsonTickets );

        return json.toString(  );
    }

    @Override
    public String formatError( String arg0, String arg1 )
    {
        return null;
    }

    @Override
    public String formatResponse( Ticket ticket )
    {
        JSONObject jsonTicket = new JSONObject(  );
        jsonTicket.accumulate( FormatConstants.KEY_TICKET_REFERENCE, ticket.getReference(  ) );

        return jsonTicket.toString(  );
    }

    /**
     * Write a ticket into a JSON Object
     * @param json The JSON Object
     * @param ticket The ticket
     */
    private void add( JSONObject json, Ticket ticket )
    {
        JSONObject jsonUser = new JSONObject(  );
        jsonUser.accumulate( FormatConstants.KEY_USER_TITLE_ID, ticket.getIdUserTitle(  ) );
        jsonUser.accumulate( FormatConstants.KEY_USER_FIRST_NAME, ticket.getFirstname(  ) );
        jsonUser.accumulate( FormatConstants.KEY_USER_LAST_NAME, ticket.getLastname(  ) );
        jsonUser.accumulate( FormatConstants.KEY_USER_EMAIL, ticket.getEmail(  ) );
        jsonUser.accumulate( FormatConstants.KEY_USER_FIXED_PHONE_NUMBER, ticket.getFixedPhoneNumber(  ) );
        jsonUser.accumulate( FormatConstants.KEY_USER_MOBILE_PHONE_NUMBER, ticket.getMobilePhoneNumber(  ) );
        json.accumulate( FormatConstants.KEY_USER, jsonUser );

        json.accumulate( FormatConstants.KEY_TICKET_REFERENCE, ticket.getReference(  ) );
        json.accumulate( FormatConstants.KEY_TICKET_CATEGORY_CODE, ticket.getIdTicketCategory(  ) );
        json.accumulate( FormatConstants.KEY_TICKET_CONTACT_MODE_ID, ticket.getIdContactMode(  ) );
        json.accumulate( FormatConstants.KEY_TICKET_CHANNEL_ID, ticket.getChannel(  ).getId(  ) );
        json.accumulate( FormatConstants.KEY_TICKET_COMMENT, ticket.getTicketComment(  ) );
    }
}
