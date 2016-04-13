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
package fr.paris.lutece.plugins.ticketing.service.format.tickettype;

import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketType;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatter;
import fr.paris.lutece.util.ReferenceItem;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * JSON formatter for ticket category resource
 *
 */
public class TicketTypeFormatterJson implements IFormatter<TicketType>
{
    @Override
    public String format( TicketType ticketType )
    {
        JSONObject json = new JSONObject(  );
        String strJson = StringUtils.EMPTY;

        if ( ticketType != null )
        {
            add( json, ticketType );
            strJson = json.toString(  );
        }

        return strJson;
    }

    @Override
    public String format( List<TicketType> listTicketTypes )
    {
        JSONObject json = new JSONObject(  );
        JSONArray jsonTypes = new JSONArray(  );

        for ( TicketType ticketType : listTicketTypes )
        {
            JSONObject jsonType = new JSONObject(  );
            add( jsonType, ticketType );
            jsonTypes.add( jsonType );
        }

        json.accumulate( FormatConstants.KEY_TICKET_TYPES, jsonTypes );

        return json.toString(  );
    }

    @Override
    public String formatError( String arg0, String arg1 )
    {
        return null;
    }

    @Override
    public String formatResponse( TicketType ticketType )
    {
        return format( ticketType );
    }

    /**
     * Write a ticket type into a JSON Object
     * @param json The JSON Object
     * @param ticketType The ticket type
     */
    private void add( JSONObject json, TicketType ticketType )
    {
        json.accumulate( FormatConstants.KEY_ID, ticketType.getId(  ) );
        json.accumulate( FormatConstants.KEY_LABEL, ticketType.getLabel(  ) );

        JSONArray jsonDomains = new JSONArray(  );

        for ( ReferenceItem domain : TicketDomainHome.getReferenceListByType( ticketType.getId(  ) ) )
        {
            int nDomainId = Integer.parseInt( domain.getCode(  ) );
            JSONObject jsonDomain = new JSONObject(  );
            jsonDomain.accumulate( FormatConstants.KEY_ID, nDomainId );
            jsonDomain.accumulate( FormatConstants.KEY_LABEL, domain.getName(  ) );

            JSONArray jsonCategories = new JSONArray(  );

            for ( ReferenceItem category : TicketCategoryHome.getReferenceListByDomain( nDomainId ) )
            {
                int nCategoryId = Integer.parseInt( category.getCode(  ) );
                JSONObject jsonCategory = new JSONObject(  );
                jsonCategory.accumulate( FormatConstants.KEY_ID, nCategoryId );
                jsonCategory.accumulate( FormatConstants.KEY_LABEL, category.getName(  ) );
                jsonCategories.add( jsonCategory );
            }

            jsonDomain.accumulate( FormatConstants.KEY_TICKET_CATEGORIES, jsonCategories );
            jsonDomains.add( jsonDomain );
        }

        json.accumulate( FormatConstants.KEY_TICKET_DOMAINS, jsonDomains );
    }
}
