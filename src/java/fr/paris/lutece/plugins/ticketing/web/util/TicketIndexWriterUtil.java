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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.util.Version;

import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Util class to manage an IndexWriter
 */
public class TicketIndexWriterUtil
{
    
    /**
     * Return the IndexWriterConfig for an IndexWriter
     * 
     * @param bCreateIndex the boolean for the creation of the index
     * @return
     */
    public static IndexWriterConfig getIndexWriterConfig( Analyzer analyzer, int maxTokenCount, int mergeFactor,  boolean bCreateIndex )
    {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig( Version.LUCENE_4_9, new LimitTokenCountAnalyzer( analyzer, maxTokenCount ) );

        LogMergePolicy mergePolicy = new LogDocMergePolicy( );
        mergePolicy.setMergeFactor( mergeFactor );

        indexWriterConfig.setMergePolicy( mergePolicy );

        if ( bCreateIndex )
        {
            indexWriterConfig.setOpenMode( OpenMode.CREATE );
        }
        else
        {
            indexWriterConfig.setOpenMode( OpenMode.APPEND );
        }
        
        return indexWriterConfig;
    }
    
    /**
     * Close an IndexWriter
     * 
     * @param indexWriter the indexWriter to close
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
     * @param directory the directory to test
     * @param bCreate the value used if the directory doesn't exist
     * @return
     * @throws IOException
     */
    public static boolean isIndexExists( Directory directory, boolean bCreate ) throws IOException
    {
        return !DirectoryReader.indexExists( directory ) ? true : bCreate;
    }

}
