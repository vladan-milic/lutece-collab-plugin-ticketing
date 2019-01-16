/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.web.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.Directory;

import fr.paris.lutece.plugins.ticketing.web.search.TicketSearchItemConstant;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Util class to manage an IndexWriter
 */
public class TicketIndexWriterUtil
{
    // Properties
    private static final String PROPERTY_WRITER_MERGE_FACTOR = "ticketing.internalIndexer.lucene.writer.mergeFactor";
    private static final String PROPERTY_WRITER_MAX_FIELD_LENGTH = "ticketing.internalIndexer.lucene.writer.maxSectorLength";

    // Default values
    private static final int DEFAULT_WRITER_MERGE_FACTOR = 20;
    private static final int DEFAULT_WRITER_MAX_FIELD_LENGTH = 1000000;

    // Variables
    public static final Map<String, Analyzer> _mapAnalyzerPerField = initPerFieldAnalyzerMap( );

    /**
     * Return the IndexWriterConfig for an IndexWriter
     *
     * @param analyzer
     *            the analyzer to use for the config
     * @return the IndexWriterConfig for an IndexWriter
     */
    public static IndexWriterConfig getIndexWriterConfig( Analyzer analyzer )
    {
        int nWriterMergeFactor = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MERGE_FACTOR, DEFAULT_WRITER_MERGE_FACTOR );
        int nWriterMaxSectorLength = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MAX_FIELD_LENGTH, DEFAULT_WRITER_MAX_FIELD_LENGTH );

        PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper( analyzer, getPerFieldAnalyzerMap( ) );
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig( new LimitTokenCountAnalyzer( perFieldAnalyzerWrapper, nWriterMaxSectorLength ) );

        LogMergePolicy mergePolicy = new LogDocMergePolicy( );
        mergePolicy.setMergeFactor( nWriterMergeFactor );

        indexWriterConfig.setMergePolicy( mergePolicy );
        indexWriterConfig.setOpenMode( OpenMode.CREATE_OR_APPEND );

        return indexWriterConfig;
    }

    /**
     * Close an IndexWriter
     *
     * @param indexWriter
     *            the indexWriter to close
     */
    public static void manageCloseWriter( IndexWriter indexWriter )
    {
        try
        {
            if ( indexWriter != null )
            {
                indexWriter.close( );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
    }

    /**
     * Method which tell if a directory exists or if it's necessary to create a new one and a new index
     *
     * @param directory
     *            the directory to test
     * @param bCreate
     *            the value used if the directory doesn't exist
     * @return is index exists
     * @throws IOException
     *             IOException
     */
    public static boolean isIndexExists( Directory directory, boolean bCreate ) throws IOException
    {
        return !DirectoryReader.indexExists( directory ) ? true : bCreate;
    }

    /**
     * Create the map which specify specific Analyzer to use for some field
     *
     * @return the map which associate an analyzer to the name of a field
     */
    public static final Map<String, Analyzer> initPerFieldAnalyzerMap( )
    {
        TicketSortAnalyzer ticketSortAnalyzer = new TicketSortAnalyzer( );
        Map<String, Analyzer> mapAnalyzerPerField = new LinkedHashMap<>( );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_LASTNAME, ticketSortAnalyzer );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_FIRSTNAME, ticketSortAnalyzer );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_CATEGORY, ticketSortAnalyzer );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_STATE, ticketSortAnalyzer );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_TICKET_NOMENCLATURE, ticketSortAnalyzer );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_CHANNEL_LABEL, ticketSortAnalyzer );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_NAME, ticketSortAnalyzer );
        mapAnalyzerPerField.put( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_LASTNAME, ticketSortAnalyzer );

        return mapAnalyzerPerField;
    }

    /**
     * Return the map which specify specific Analyzer to use for some field
     *
     * @return the map which specify specific Analyzer to use for some field
     */
    public static Map<String, Analyzer> getPerFieldAnalyzerMap( )
    {
        return _mapAnalyzerPerField;
    }
}
