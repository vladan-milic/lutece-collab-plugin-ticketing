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

import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketSearchService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
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

            // Get results documents
            TopDocs topDocs = searcher.search( query, LuceneSearchEngine.MAX_RESPONSES );
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

        return convertList( listResults );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult> searchTicketFromReference( String strTicketRef )
        throws ParseException
    {
        BooleanQuery mainQuery = new BooleanQuery(  );
        QueryParser parser = new QueryParser( IndexationService.LUCENE_INDEX_VERSION, TicketSearchItem.FIELD_REFERENCE,
                TicketSearchService.getInstance(  ).getAnalyzer(  ) );
        Query queryTicketRef = parser.parse( QueryParser.escape( strTicketRef ) );
        mainQuery.add( queryTicketRef, BooleanClause.Occur.MUST );
        addQueryTypeClause( mainQuery );

        return search( mainQuery );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult> searchTicketsFromCategory( String strTicketCategory )
        throws ParseException
    {
        BooleanQuery mainQuery = new BooleanQuery(  );
        QueryParser qp = new QueryParser( IndexationService.LUCENE_INDEX_VERSION, TicketSearchItem.FIELD_CATEGORY,
                TicketSearchService.getInstance(  ).getAnalyzer(  ) );
        Query queryTicketCat = qp.parse( QueryParser.escape( strTicketCategory ) );
        mainQuery.add( queryTicketCat, BooleanClause.Occur.MUST );
        addQueryTypeClause( mainQuery );

        return search( mainQuery );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult> searchTickets( String strQuery )
        throws ParseException
    {
        BooleanQuery mainQuery = new BooleanQuery(  );
        QueryParser qp = new QueryParser( IndexationService.LUCENE_INDEX_VERSION, TicketSearchItem.FIELD_CONTENTS,
                TicketSearchService.getInstance(  ).getAnalyzer(  ) );
        Query queryTicket = qp.parse( QueryParser.escape( strQuery ) );
        mainQuery.add( queryTicket, BooleanClause.Occur.MUST );
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
     * {@inheritDoc}
     */
    @Override
    public List<SearchResult> searchResponseResults( String strQuery, String strCategory )
    {
        ArrayList<TicketSearchItem> listResults = new ArrayList<TicketSearchItem>(  );
        IndexSearcher searcher;

        try
        {
            searcher = TicketSearchService.getInstance(  ).getSearcher(  );

            BooleanQuery mainQuery = new BooleanQuery(  );

            QueryParser qpComment = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                    TicketSearchItem.FIELD_TXT_COMMENT, TicketSearchService.getInstance(  ).getAnalyzer(  ) );
            Query queryTicketComment = qpComment.parse( strQuery );

            mainQuery.add( queryTicketComment, BooleanClause.Occur.SHOULD );

            QueryParser qpResp = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                    TicketSearchItem.FIELD_TXT_RESPONSE, TicketSearchService.getInstance(  ).getAnalyzer(  ) );
            Query queryTicketResponse = qpResp.parse( strQuery );

            mainQuery.add( queryTicketResponse, BooleanClause.Occur.SHOULD );

            QueryParser qpCategory = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                    TicketSearchItem.FIELD_CATEGORY, IndexationService.getAnalyser(  ) );
            mainQuery.add( qpCategory.parse( strCategory ), BooleanClause.Occur.MUST );

            // Type
            addQueryTypeClause( mainQuery );

            // Get results documents
            TopDocs topDocs = searcher.search( mainQuery, MAX_RESPONSE_NUMBER );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( int i = 0; i < hits.length; i++ )
            {
                int docId = hits[i].doc;
                Document document = searcher.doc( docId );
                TicketSearchItem si = new TicketSearchItem( document );

                if ( StringUtils.isEmpty( si.getResponse(  ) ) )
                {
                    continue;
                }

                listResults.add( si );
            }
        }
        catch ( org.apache.lucene.queryparser.classic.ParseException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return convertList( listResults );
    }
}
