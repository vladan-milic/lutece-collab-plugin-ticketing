/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketSearchService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * TicketSearchEngine
 */
public class TicketSearchEngine implements ITicketSearchEngine
{
    /**
     * Convert a list of Lucene items into a list of generic search items
     *
     * @param listSource
     *            The list of Lucene items
     * @return A list of generic search items
     */
    private List<SearchResult> convertList( List<TicketSearchItem> listSource )
    {
        List<SearchResult> listDest = new ArrayList<SearchResult>(  );

        for ( TicketSearchItem item : listSource )
        {
            TicketSearchResult result = new TicketSearchResult(  );
            result.setId( item.getTicketId(  ) );

            try
            {
                if ( StringUtils.isNotEmpty( item.getDate(  ) ) )
                {
                    result.setDate( DateTools.stringToDate( item.getDate(  ) ) );
                }
            }
            catch ( java.text.ParseException e )
            {
                AppLogService.error( "Bad Date Format for indexed item \"" + item.getTitle(  ) + "\" : " +
                    e.getMessage(  ) );
            }

            result.setUrl( item.getUrl(  ) );
            result.setTitle( item.getTitle(  ) );
            result.setSummary( item.getSummary(  ) );
            result.setType( item.getType(  ) );
            result.setComment( item.getComment(  ) );
            result.setResponse( item.getResponse(  ) );
            listDest.add( (SearchResult) result );
        }

        return listDest;
    }

    /**
     * process search
     *
     * @param query
     *            lucene query
     * @return list of search results matching query
     */
    private List<SearchResult> search( Query query )
    {
        List<TicketSearchItem> listResults = new ArrayList<TicketSearchItem>(  );

        try
        {
            IndexSearcher searcher = TicketSearchService.getInstance(  ).getSearcher(  );

            //hook because of strange behaviour with native query (using toString seems to fix issue)
            String strQuery = query.toString(  );
            Query queryToLaunch = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                    TicketSearchItem.FIELD_CONTENTS, TicketSearchService.getInstance(  ).getAnalyzer(  ) ).parse( strQuery );

            // Get results documents
            TopDocs topDocs = searcher.search( queryToLaunch, LuceneSearchEngine.MAX_RESPONSES );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( int i = 0; i < hits.length; i++ )
            {
                int docId = hits[i].doc;
                Document document = searcher.doc( docId );
                TicketSearchItem si = new TicketSearchItem( document );

                listResults.add( si );
            }
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( ParseException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return convertList( listResults );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult> searchTickets( String strQuery, List<TicketDomain> listTicketDomain )
        throws ParseException
    {
        BooleanQuery mainQuery = new BooleanQuery(  );
        TermQuery queryTicket = new TermQuery( new Term( TicketSearchItem.FIELD_CONTENTS, strQuery ) );
        mainQuery.add( queryTicket, BooleanClause.Occur.MUST );
        addQueryDomainClause( mainQuery, listTicketDomain );
        addQueryTypeClause( mainQuery );

        return search( mainQuery );
    }

    /**
     * add ticket type clause
     * @param booleanQuery input query
     * @throws ParseException exception while parsing  document type
     */
    private void addQueryTypeClause( BooleanQuery booleanQuery )
        throws ParseException
    {
        QueryParser qpt = new QueryParser( IndexationService.LUCENE_INDEX_VERSION, TicketSearchItem.FIELD_TYPE,
                TicketSearchService.getInstance(  ).getAnalyzer(  ) );
        Query queryType = qpt.parse( TicketIndexer.getDocumentType(  ) );
        booleanQuery.add( queryType, BooleanClause.Occur.MUST );
    }

    /**
     * add ticket domain clause
     * @param booleanQuery input query
     * @param listUserDomain list domains authorized for admin user
     * @throws ParseException exception while parsing  document type
     */
    private void addQueryDomainClause( BooleanQuery booleanQuery, List<TicketDomain> listUserDomain )
        throws ParseException
    {
        BooleanQuery domainsQuery = new BooleanQuery(  );

        for ( TicketDomain domain : listUserDomain )
        {
            TermQuery domQuery = new TermQuery( new Term( TicketSearchItem.FIELD_DOMAIN,
                        QueryParser.escape( domain.getLabel(  ) ) ) );
            domainsQuery.add( new BooleanClause( domQuery, BooleanClause.Occur.SHOULD ) );
        }

        booleanQuery.add( new BooleanClause( domainsQuery, BooleanClause.Occur.MUST ) );
    }
}
