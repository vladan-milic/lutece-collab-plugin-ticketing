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
package fr.paris.lutece.plugins.ticketing.business.instantresponse;

import fr.paris.lutece.plugins.ticketing.business.OrderByFilter;

import java.util.HashMap;


/**
 *
 * class TicketFilter
 *
 */
public class InstantResponseFilter extends OrderByFilter
{
    public static final String CONSTANT_DEFAULT_ORDER_BY = "date_create";
    public static final String CONSTANT_DEFAULT_ORDER_SORT = OrderSortAllowed.DESC.name(  );

    /**
     *
     */
    public InstantResponseFilter(  )
    {
        super(  );
        initOrderNameToColumnNameMap(  );
    }

    @Override
    protected void initOrderNameToColumnNameMap(  )
    {
        _mapOrderNameToColumnName = new HashMap<String, String>(  );
        _mapOrderNameToColumnName.put( "id", "a.id_instant_response" );
        _mapOrderNameToColumnName.put( "category", "b.label " );
        _mapOrderNameToColumnName.put( "subject", "a.subject" );
        _mapOrderNameToColumnName.put( "date_create", "a.date_create" );
        _mapOrderNameToColumnName.put( "assignee", "f.label" );
    }

    @Override
    public String getDefaultOrderBySqlColumn(  )
    {
        return _mapOrderNameToColumnName.get( CONSTANT_DEFAULT_ORDER_BY );
    }

    @Override
    public String getDefaultOrderSort(  )
    {
        return CONSTANT_DEFAULT_ORDER_SORT;
    }
}