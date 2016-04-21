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
package fr.paris.lutece.plugins.ticketing.web.search;

import fr.paris.lutece.portal.service.search.SearchResult;

import org.apache.lucene.queryparser.classic.ParseException;

import java.util.List;


/**
 * SearchEngine for tickets document
 * @author s267533
 *
 */
public interface ITicketSearchEngine
{
    int MAX_RESPONSE_NUMBER = 10;

    /**
     * search tickets which contains strQuery in lucene reference field
     * @param strTicketRef query to search
     * @return Results as a collection of SearchResult
     * @throws ParseException exception occurs while parsing input query
     */
    List<SearchResult> searchTicketFromReference( String strTicketRef )
        throws ParseException;

    /**
     * search tickets which contains strQuery in lucene category field
     * @param strTicketCategory query to search
     * @return Results as a collection of SearchResult
     * @throws ParseException exception occurs while parsing input query
     */
    List<SearchResult> searchTicketsFromCategory( String strTicketCategory )
        throws ParseException;

    /**
     * search tickets which contains strQuery in lucene contents field
     * @param strQuery query to search
     * @return Results as a collection of SearchResult
     * @throws ParseException exception occurs while parsing input query
     */
    List<SearchResult> searchTickets( String strQuery )
        throws ParseException;

    /**
     * Return response results matching input query
     * @param strQuery The search query
     * @param strCategory category of ticket
     * @return Results as a collection of SearchResult
     * @throws ParseException exception occurs while parsing input query
     */
    List<SearchResult> searchResponseResults( String strQuery, String strCategory )
        throws ParseException;
}
