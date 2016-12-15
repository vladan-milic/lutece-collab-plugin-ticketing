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

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.ITicketingFormatter;
import fr.paris.lutece.util.ReferenceItem;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * JSON formatter for ticket category resource
 *
 */
public class TicketTypeFormatterJson implements ITicketingFormatter<TicketType>
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
        ArrayList<String> listCategoryNames = new ArrayList<String>(  );

        for ( ReferenceItem refItemDomain : TicketDomainHome.getReferenceListByType( ticketType.getId(  ) ) )
        {
            int nDomainId = Integer.parseInt( refItemDomain.getCode(  ) );
            JSONObject jsonDomain = new JSONObject(  );
            jsonDomain.accumulate( FormatConstants.KEY_ID, nDomainId );
            jsonDomain.accumulate( FormatConstants.KEY_LABEL, refItemDomain.getName(  ) );

            Map<String, List<TicketCategory>> mapTicketCategories = new HashMap<String, List<TicketCategory>>(  );

            for ( TicketCategory ticketCategory : TicketCategoryHome.findByDomainId( nDomainId ) )
            {
                if ( mapTicketCategories.containsKey( ticketCategory.getLabel(  ) ) )
                { // Precision
                    mapTicketCategories.get( ticketCategory.getLabel(  ) ).add( ticketCategory );
                }
                else
                {
                    mapTicketCategories.put( ticketCategory.getLabel(  ), new ArrayList<TicketCategory>(  ) );
                    mapTicketCategories.get( ticketCategory.getLabel(  ) ).add( ticketCategory );
                }
            }

            JSONArray jsonCategories = new JSONArray(  );

            for ( Map.Entry<String, List<TicketCategory>> mapEntryCategoryByLabel : mapTicketCategories.entrySet(  ) )
            {
                String labelCategory = mapEntryCategoryByLabel.getKey(  );
                List<TicketCategory> listCategoryByLabel = mapEntryCategoryByLabel.getValue(  );
                Iterator itListCategoryByLabel = listCategoryByLabel.iterator(  );
                TicketCategory category = new TicketCategory(  );

                JSONObject jsonCategory = new JSONObject(  );
                JSONArray jsonPrecisions = new JSONArray(  );
                JSONObject jsonPrecision = new JSONObject(  );

                while ( itListCategoryByLabel.hasNext(  ) )
                {
                    category = null;
                    category = (TicketCategory) itListCategoryByLabel.next(  );
                    jsonCategory = new JSONObject(  );

                    if ( StringUtils.isNotEmpty( category.getPrecision(  ) ) )
                    {
                        jsonPrecision = new JSONObject(  );
                        jsonPrecision.accumulate( FormatConstants.KEY_ID, category.getId(  ) );
                        jsonPrecision.accumulate( FormatConstants.KEY_LABEL, category.getPrecision(  ) );

                        if ( StringUtils.isNotEmpty( category.getHelpMessage(  ) ) )
                        {
                            jsonPrecision.accumulate( FormatConstants.KEY_HELP, category.getHelpMessage(  ) );
                        }

                        jsonPrecisions.add( jsonPrecision );
                    }
                    else
                    {
                        jsonCategory.accumulate( FormatConstants.KEY_ID, category.getId(  ) );
                        jsonCategory.accumulate( FormatConstants.KEY_LABEL, category.getLabel(  ) );

                        if ( StringUtils.isNotEmpty( category.getHelpMessage(  ) ) )
                        {
                            jsonCategory.accumulate( FormatConstants.KEY_HELP, category.getHelpMessage(  ) );
                        }

                        jsonCategories.add( jsonCategory );
                    }
                }

                if ( !jsonPrecisions.isEmpty(  ) )
                {
                    jsonCategory.accumulate( FormatConstants.KEY_ID, category.getId(  ) );
                    jsonCategory.accumulate( FormatConstants.KEY_LABEL, category.getLabel(  ) );
                    jsonCategory.accumulate( FormatConstants.KEY_TICKET_PRECISIONS, jsonPrecisions );
                    jsonCategories.add( jsonCategory );
                }
            }

            jsonDomain.accumulate( FormatConstants.KEY_TICKET_CATEGORIES, jsonCategories );
            jsonDomains.add( jsonDomain );
        }

        json.accumulate( FormatConstants.KEY_TICKET_DOMAINS, jsonDomains );
    }
}
