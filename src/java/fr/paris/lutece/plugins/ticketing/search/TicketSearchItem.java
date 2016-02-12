/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.search;

import fr.paris.lutece.portal.service.search.SearchItem;

import org.apache.lucene.document.Document;


/**
 * Ticket Search Item
 */
public class TicketSearchItem extends SearchItem
{
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_REFERENCE = "reference";
    public static final String FIELD_TICKET_ID = "ticket_id";

    // Variables declarations
    private String _strReference;
    private String _strTicketId;
    private String _strCategory;

    /**
     * Constructor
     * @param document The document retrieved by the search
     */
    public TicketSearchItem( Document document )
    {
        super( document );
        _strReference = document.get( FIELD_REFERENCE );
        _strTicketId = document.get( FIELD_TICKET_ID );
        _strCategory = document.get( FIELD_CATEGORY );
    }

    /**
     * @return the _strReference
     */
    public String getReference(  )
    {
        return _strReference;
    }

    /**
     * @param strReference the strReference to set
     */
    public void setReference( String strReference )
    {
        this._strReference = strReference;
    }

    /**
     * @return the _strTicketId
     */
    public String getTicketId(  )
    {
        return _strTicketId;
    }

    /**
     * @param strTicketId the strTicketId to set
     */
    public void setTicketId( String strTicketId )
    {
        this._strTicketId = strTicketId;
    }

    /**
     * @return the _strCategory
     */
    public String getCategory(  )
    {
        return _strCategory;
    }

    /**
     * @param strCategory the strCategory to set
     */
    public void setCategory( String strCategory )
    {
        this._strCategory = strCategory;
    }
}
