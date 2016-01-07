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
package fr.paris.lutece.plugins.ticketing.service;

import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketType;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.util.ReferenceItem;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 *
 * @author levy
 */
public class CategoriesService
{
    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String TYPES = "types";
    private static final int INDENT = 4;

    public static String getJsonCategories(  )
    {
        JSONObject json = new JSONObject(  );
        JSONArray jsonTypes = new JSONArray(  );

        for ( TicketType type : TicketTypeHome.getTicketTypesList(  ) )
        {
            JSONObject jsonType = new JSONObject(  );
            jsonType.accumulate( ID, type.getId(  ) );
            jsonType.accumulate( LABEL, type.getLabel(  ) );

            JSONArray jsonDomains = new JSONArray(  );

            for ( ReferenceItem domain : TicketDomainHome.getReferenceListByType( type.getId(  ) ) )
            {
                int nDomainId = Integer.parseInt( domain.getCode(  ) );
                JSONObject jsonDomain = new JSONObject(  );
                jsonDomain.accumulate( ID, nDomainId );
                jsonDomain.accumulate( LABEL, domain.getName(  ) );

                JSONArray jsonCategories = new JSONArray(  );

                for ( ReferenceItem category : TicketCategoryHome.getReferenceListByDomain( nDomainId ) )
                {
                    int nCategoryId = Integer.parseInt( category.getCode(  ) );
                    JSONObject jsonCategory = new JSONObject(  );
                    jsonCategory.accumulate( ID, nCategoryId );
                    jsonCategory.accumulate( LABEL, category.getName(  ) );
                    jsonCategories.add( jsonCategory );
                }

                jsonDomain.accumulate( "categories", jsonCategories );
                jsonDomains.add( jsonDomain );
            }

            jsonType.accumulate( "domains", jsonDomains );
            jsonTypes.add( jsonType );
        }

        json.accumulate( "types", jsonTypes );

        return json.toString( INDENT );
    }
}
