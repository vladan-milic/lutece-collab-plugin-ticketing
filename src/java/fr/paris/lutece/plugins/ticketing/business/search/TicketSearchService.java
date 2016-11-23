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
package fr.paris.lutece.plugins.ticketing.business.search;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.search.IndexSearcher;

//import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

import java.util.Date;
import java.util.List;


/**
 * TicketSearchService
 */
public final class TicketSearchService
{
    private static final String PATH_INDEX = "ticketing.internalIndexer.lucene.indexPath";
    private static final String PROPERTY_WRITER_MERGE_FACTOR = "ticketing.internalIndexer.lucene.writer.mergeFactor";
    private static final String PROPERTY_WRITER_MAX_FIELD_LENGTH = "ticketing.internalIndexer.lucene.writer.maxSectorLength";
    private static final String PROPERTY_ANALYSER_CLASS_NAME = "ticketing.internalIndexer.lucene.analyser.className";
    private static final String PROPERTY_MAX_SKIPPED_INDEXATION = "ticketing.indexer.maxSkipedIndexation";

    // Default values
    private static final int DEFAULT_WRITER_MERGE_FACTOR = 20;
    private static final int DEFAULT_WRITER_MAX_FIELD_LENGTH = 1000000;

    // Constants corresponding to the variables defined in the lutece.properties file
    private static volatile TicketSearchService _singleton;
    private int _nSkipedIndexations;
    private volatile String _strIndex;
    private Analyzer _analyzer;
    private ITicketSearchIndexer _indexer;
    private int _nWriterMergeFactor;
    private int _nWriterMaxSectorLength;

    /**
     * Creates a new instance of DirectorySearchService
     */
    private TicketSearchService(  )
    {
        // Read configuration properties
        String strIndex = getIndex(  );

        if ( StringUtils.isEmpty( strIndex ) )
        {
            throw new AppException( "Lucene index path not found in ticketing.properties", null );
        }

        _nWriterMergeFactor = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MERGE_FACTOR,
                DEFAULT_WRITER_MERGE_FACTOR );
        _nWriterMaxSectorLength = AppPropertiesService.getPropertyInt( PROPERTY_WRITER_MAX_FIELD_LENGTH,
                DEFAULT_WRITER_MAX_FIELD_LENGTH );

        String strAnalyserClassName = AppPropertiesService.getProperty( PROPERTY_ANALYSER_CLASS_NAME );

        if ( ( strAnalyserClassName == null ) || ( strAnalyserClassName.equals( "" ) ) )
        {
            throw new AppException( "Analyser class name not found in ticketing.properties", null );
        }

        _indexer = (ITicketSearchIndexer) SpringContextService.getBean( "ticketing.ticketIndexer" );

        try
        {
            @SuppressWarnings( {"unchecked",
                "rawtypes"
            } )
            java.lang.reflect.Constructor constructeur = Class.forName( strAnalyserClassName ).getConstructor( Version.class, String[].class  );
            _analyzer = (Analyzer) constructeur.newInstance( new Object[] { IndexationService.LUCENE_INDEX_VERSION, new String[]{} } );
        }

        catch ( InstantiationException ie )
        {
            @SuppressWarnings( "rawtypes" )
            Class classAnalyzer;

            try
            {
                classAnalyzer = Class.forName( strAnalyserClassName );

                @SuppressWarnings( {"unchecked",
                    "rawtypes"
                } )
                java.lang.reflect.Constructor constructeur = classAnalyzer.getConstructor( Version.class );
                _analyzer = (Analyzer) constructeur.newInstance( new Object[] { IndexationService.LUCENE_INDEX_VERSION } );
            }
            catch ( Exception e )
            {
                throw new AppException( "Failed to load Lucene Analyzer class", e );
            }
        }
        catch ( Exception e )
        {
            throw new AppException( "Failed to load Lucene Analyzer class", e );
        }
    }

    /**
     * Get the HelpdeskSearchService instance
     * @return The {@link TicketingSearchService}
     */
    public static TicketSearchService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new TicketSearchService(  );
        }

        return _singleton;
    }

    /**
     * return searcher
     * @return searcher
     */
    public IndexSearcher getSearcher(  )
    {
        IndexSearcher searcher = null;

        try
        {
            IndexReader ir = DirectoryReader.open( NIOFSDirectory.open( new File( getIndex(  ) ) ) );
            searcher = new IndexSearcher( ir );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return searcher;
    }

    /**
     * Process indexing
     * @param bCreate true for start full indexing
     *            false for begin incremental indexing
     * @return the log
     */
    public String processIndexing( boolean bCreate )
    {
        StringBuffer sbLogs = new StringBuffer(  );
        IndexWriter writer = null;
        boolean bCreateIndex = bCreate;

        try
        {
            sbLogs.append( "\r\nIndexing all contents ...\r\n" );

            Directory dir = NIOFSDirectory.open( new File( getIndex(  ) ) );

            // new
            if ( !DirectoryReader.indexExists( dir ) )
            { //init index
                bCreateIndex = true;
            }

            boolean bIsLocked = false;

            if ( IndexWriter.isLocked( dir ) )
            {
                _nSkipedIndexations++;

                if ( bCreate ||
                        ( _nSkipedIndexations >= AppPropertiesService.getPropertyInt( PROPERTY_MAX_SKIPPED_INDEXATION,
                            10 ) ) )
                {
                    IndexWriter.unlock( dir );
                    bIsLocked = false;
                }
                else
                {
                    bIsLocked = true;
                    _nSkipedIndexations = 0;
                }
            }

            if ( !bIsLocked )
            {
                Date start = new Date(  );

                IndexWriterConfig conf = new IndexWriterConfig( Version.LUCENE_46,
                        new LimitTokenCountAnalyzer( _analyzer, _nWriterMaxSectorLength ) );

                LogMergePolicy mergePolicy = new LogDocMergePolicy(  );
                mergePolicy.setMergeFactor( _nWriterMergeFactor );

                conf.setMergePolicy( mergePolicy );

                if ( bCreateIndex )
                {
                    conf.setOpenMode( OpenMode.CREATE );
                }
                else
                {
                    conf.setOpenMode( OpenMode.APPEND );
                }

                writer = new IndexWriter( dir, conf );

                sbLogs.append( "\r\n<strong>Indexer : " );
                sbLogs.append( _indexer.getName(  ) );
                sbLogs.append( " - " );
                sbLogs.append( _indexer.getDescription(  ) );
                sbLogs.append( "</strong>\r\n" );
                _indexer.processIndexing( writer, bCreateIndex, sbLogs );

                Date end = new Date(  );

                sbLogs.append( "Duration of the treatment : " );
                sbLogs.append( end.getTime(  ) - start.getTime(  ) );
                sbLogs.append( " milliseconds\r\n" );
            }
        }
        catch ( Exception e )
        {
            sbLogs.append( " caught a " );
            sbLogs.append( e.getClass(  ) );
            sbLogs.append( "\n with message: " );
            sbLogs.append( e.getMessage(  ) );
            sbLogs.append( "\r\n" );
            AppLogService.error( "Indexing error : " + e.getMessage(  ), e );
        }
        finally
        {
            try
            {
                if ( writer != null )
                {
                    writer.close(  );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return sbLogs.toString(  );
    }

    /**
     * Add Indexer Action to perform on a record
     * @param nIdTicket ticket id
     * @param nIdTask the key of the action to do
     * @param plugin the plugin
     */
    public void addIndexerAction( int nIdTicket, int nIdTask, Plugin plugin )
    {
        IndexerAction indexerAction = new IndexerAction(  );
        indexerAction.setIdTicket( nIdTicket );
        indexerAction.setIdTask( nIdTask );
        IndexerActionHome.create( indexerAction );
    }

    /**
     * Remove a Indexer Action
     * @param nIdAction the key of the action to remove
     * @param plugin the plugin
     */
    public void removeIndexerAction( int nIdAction, Plugin plugin )
    {
        IndexerActionHome.remove( nIdAction );
    }

    /**
     * return a list of IndexerAction by task key
     * @param nIdTask the task key
     * @param plugin the plugin
     * @return a list of IndexerAction
     */
    public List<IndexerAction> getAllIndexerActionByTask( int nIdTask, Plugin plugin )
    {
        IndexerActionFilter filter = new IndexerActionFilter(  );
        filter.setIdTask( nIdTask );

        return IndexerActionHome.getList( filter );
    }

    /**
     * Get the path to the index of the search service
     * @return The path to the index of the search service
     */
    private String getIndex(  )
    {
        if ( _strIndex == null )
        {
            _strIndex = AppPathService.getPath( PATH_INDEX );
        }

        return _strIndex;
    }

    /**
     * Get the analyzed of this search service
     * @return The analyzer of this search service
     */
    public Analyzer getAnalyzer(  )
    {
        return _analyzer;
    }
}
