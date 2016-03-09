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
package fr.paris.lutece.plugins.ticketing.web.search;

import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * TicketSearchEngine
 */
public class TicketSearchEngine implements SearchEngine
{
    /**
     * Return search results
     * @param strQuery The search query
     * @param request The HTTP request
     * @return Results as a collection of SearchResult
     */

    //@Override
    public List<SearchResult> getSearchResults( String strQuery, HttpServletRequest request )
    {
        ArrayList<TicketSearchItem> listResults = new ArrayList<TicketSearchItem>(  );
        IndexSearcher searcher;
        String strSearchedField = request.getParameter( SearchConstants.PARAMETER_SEARCH_FIELD );

        try
        {
            IndexReader ir = DirectoryReader.open( IndexationService.getDirectoryIndex(  ) );
            searcher = new IndexSearcher( ir );

            BooleanQuery query = new BooleanQuery(  );

            // Contents
            if ( StringUtils.isNotEmpty( strQuery ) )
            {
                if ( StringUtils.isNotEmpty( strSearchedField ) &&
                        strSearchedField.equals( TicketSearchItem.FIELD_CONTENTS ) )
                {
                    QueryParser parser = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                            TicketSearchItem.FIELD_CONTENTS, IndexationService.getAnalyser(  ) );
                    query.add( parser.parse( strQuery ), BooleanClause.Occur.MUST );
                }
                else if ( StringUtils.isNotEmpty( strSearchedField ) &&
                        strSearchedField.equals( TicketSearchItem.FIELD_CATEGORY ) )
                {
                    QueryParser parserCategory = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                            TicketSearchItem.FIELD_CATEGORY, IndexationService.getAnalyser(  ) );
                    query.add( parserCategory.parse( strQuery ), BooleanClause.Occur.MUST );
                }
                else if ( StringUtils.isNotEmpty( strSearchedField ) &&
                        strSearchedField.equals( TicketSearchItem.FIELD_REFERENCE ) )
                {
                    QueryParser parserCategory = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                            TicketSearchItem.FIELD_REFERENCE, IndexationService.getAnalyser(  ) );
                    query.add( parserCategory.parse( strQuery ), BooleanClause.Occur.MUST );
                }
            }

            if ( StringUtils.isNotEmpty( strSearchedField ) &&
                    strSearchedField.equals( TicketSearchItem.FIELD_RESPONSE ) )
            {
                setSearchResponseQuery( query, strQuery, request );
            }

            // Type
            Query queryType = new TermQuery( new Term( TicketSearchItem.FIELD_TYPE, TicketIndexer.getDocumentType(  ) ) );
            query.add( queryType, BooleanClause.Occur.MUST );

            // Get results documents
            TopDocs topDocs = searcher.search( query, LuceneSearchEngine.MAX_RESPONSES );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( int i = 0; i < hits.length; i++ )
            {
                int docId = hits[i].doc;
                Document document = searcher.doc( docId );
                TicketSearchItem si = new TicketSearchItem( document );

                if ( strSearchedField.equals( TicketSearchItem.FIELD_RESPONSE ) &&
                        StringUtils.isEmpty( si.getResponse(  ) ) )
                {
                    continue;
                }

                listResults.add( si );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return convertList( listResults );
    }

    /**
     * Convert a list of Lucene items into a list of generic search items
     * @param listSource The list of Lucene items
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
            catch ( ParseException e )
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
     *
     * @param query Query object to populate
     * @param strQuery query string
     * @param request request
     * @throws org.apache.lucene.queryparser.classic.ParseException when parse exception occurs
     */
    private void setSearchResponseQuery( BooleanQuery query, String strQuery, HttpServletRequest request )
        throws org.apache.lucene.queryparser.classic.ParseException
    {
        if ( StringUtils.isNotEmpty( strQuery ) )
        {
            QueryParser parserResponse = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                    TicketSearchItem.FIELD_TXT_RESPONSE, IndexationService.getAnalyser(  ) );
            parserResponse.setAllowLeadingWildcard( true );
            query.add( parserResponse.parse( "*" + strQuery.replaceAll( " ", "*" ) + "*" ), BooleanClause.Occur.SHOULD );
       
            QueryParser parserComment = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                    TicketSearchItem.FIELD_COMMENT, IndexationService.getAnalyser(  ) );
            parserComment.setAllowLeadingWildcard( true );
            query.add( parserComment.parse( "*" + strQuery.replaceAll( " ", "*" ) + "*" ), BooleanClause.Occur.SHOULD );
        }

        QueryParser parserCategory = new QueryParser( IndexationService.LUCENE_INDEX_VERSION,
                TicketSearchItem.FIELD_CATEGORY, IndexationService.getAnalyser(  ) );
        query.add( parserCategory.parse( request.getParameter( SearchConstants.PARAMETER_CATEGORY ) ),
            BooleanClause.Occur.MUST );
    }
}
