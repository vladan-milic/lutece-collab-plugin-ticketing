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
package fr.paris.lutece.plugins.ticketing.business.modelresponse.search;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponse;
import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponseHome;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * The Class LuceneModelResponseIndexerServices.
 */
public class LuceneModelResponseIndexerServices implements IModelResponseIndexer
{
    /** Constant for lucene */
    private final String FIELD_MODEL_RESPONSE_INFOS = "model_responses";
    private final String FIELD_ID = "id";
    private final String FIELD_TITLE = "title";
    private final String FIELD_RESPONSE = "response";
    private final String FIELD_KEYWORD = "keyword";
    private final String FIELD_DOMAIN_ID = "id_domain";
    private final String FIELD_DOMAIN_LABEL = "domain";
    private final String FIELD_SEARCH_CONTENT = "content";

    /** The _analyzer. */
    private Analyzer _analyzer;

    /** property index path */
    private String _strIndexPath;
    /** property index in webapp */
    private Boolean _bIndexInWebapp;

    /**
     * Instantiates a new lucene model response indexer services.
     */
    public LuceneModelResponseIndexerServices( String strIndexPath, String strClassAnalyzer, Boolean bIndexInWebapp )
    {
        super( );
        _strIndexPath = strIndexPath;
        _bIndexInWebapp = bIndexInWebapp;
        setAnalyzer( strClassAnalyzer );
    }

    public LuceneModelResponseIndexerServices( String strIndexPath, String strClassAnalyzer )
    {
        this( strIndexPath, strClassAnalyzer, true );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.plugins.ticketing.business.modelresponse.search.IModelResponseIndexer#update(fr.paris.lutece.plugins.ticketing.business.modelresponse
     * .ModelResponse)
     */
    @Override
    public void update( ModelResponse modelReponse ) throws IOException
    {
        AppLogService.debug( "\n Ticketing - Model Response : " + modelReponse );

        IndexWriter writer = getIndexWriter( false );
        Document doc = getDocument( modelReponse );
        writer.updateDocument( new Term( FIELD_MODEL_RESPONSE_INFOS, modelReponse.toString( ) ), doc );
        writer.close( );
    }

    /**
     * Gets the document.
     *
     * @param modelReponse
     *            the model reponse
     * @return the document
     */
    private Document getDocument( ModelResponse modelReponse )
    {
        Document doc = new Document( );
        doc.add( new IntField( FIELD_ID, modelReponse.getId( ), Field.Store.YES ) );
        doc.add( new StringField( FIELD_TITLE, modelReponse.getTitle( ), Field.Store.YES ) );
        doc.add( new StringField( FIELD_KEYWORD, modelReponse.getKeyword( ), Field.Store.YES ) );
        doc.add( new StringField( FIELD_RESPONSE, modelReponse.getReponse( ), Field.Store.YES ) );
        doc.add( new StringField( FIELD_DOMAIN_ID, String.valueOf( modelReponse.getIdDomain( ) ), Field.Store.YES ) );
        doc.add( new TextField( FIELD_DOMAIN_LABEL, modelReponse.getDomain( ), Field.Store.YES ) );
        doc.add( new StringField( FIELD_MODEL_RESPONSE_INFOS, modelReponse.toString( ), Field.Store.YES ) );

        List<String> tKeywords = new ArrayList<String>( );
        tKeywords.addAll( Arrays.asList( modelReponse.getKeyword( ).split( "," ) ) );

        // add title to index
        tKeywords.addAll( Arrays.asList( modelReponse.getTitle( ).split( " " ) ) );

        String strIndexWords = StringUtils.join( tKeywords, " " );
        doc.add( new TextField( FIELD_SEARCH_CONTENT, strIndexWords, Field.Store.NO ) );

        return doc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.plugins.ticketing.business.modelresponse.search.IModelResponseIndexer#delete(fr.paris.lutece.plugins.ticketing.business.modelresponse
     * .ModelResponse)
     */
    @Override
    public void delete( ModelResponse modelReponse ) throws IOException
    {
        AppLogService.debug( "\n Ticketing - Model Response  : " + modelReponse );

        IndexWriter writer = getIndexWriter( false );
        writer.deleteDocuments( new Term( FIELD_MODEL_RESPONSE_INFOS, modelReponse.toString( ) ) );
        writer.close( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.ticketing.business.modelresponse.search.IModelResponseIndexer#add(fr.paris.lutece.plugins.ticketing.business.modelresponse.
     * ModelResponse)
     */
    @Override
    public void add( ModelResponse modelReponse ) throws IOException
    {
        AppLogService.debug( "\n Ticketing - Model Response  : " + modelReponse );

        IndexWriter writer = getIndexWriter( false );
        Document doc = getDocument( modelReponse );
        writer.addDocument( doc );
        writer.close( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.ticketing.business.modelresponse.search.IModelResponseIndexer#addAll()
     */
    @Override
    public String addAll( )
    {
        AppLogService.debug( "\n Ticketing - Model Response : Indexing All model response : \n" );

        StringBuilder sbLogs = new StringBuilder( );

        try
        {
            IndexWriter writer = getIndexWriter( true );
            List<ModelResponse> modelResponses = ModelResponseHome.getModelResponsesList( );

            for ( ModelResponse modelResponse : modelResponses )
            {
                Document doc = getDocument( modelResponse );
                writer.addDocument( doc );
            }

            writer.close( );
            sbLogs.append( "\n Ticketing - Model Response : Indexed Model Responses : " ).append( modelResponses.size( ) );
        }
        catch( IOException ex )
        {
            AppLogService.error( "\n Ticketing - Model Response : Error indexing model response : " + ex.getMessage( ), ex );
        }

        AppLogService.debug( "\n Ticketing - Model Response : end Indexing All model response : \n" );

        return sbLogs.toString( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.ticketing.business.modelresponse.search.IModelResponseIndexer#searchResponses(java.lang.String, java.lang.String)
     */
    @Override
    public List<ModelResponse> searchResponses( String strQuery, Set<String> setIdDomain )
    {
        List<ModelResponse> list = new ArrayList<ModelResponse>( );

        try
        {
            IndexReader reader = DirectoryReader.open( FSDirectory.open( getIndexPath( ) ) );

            IndexSearcher searcher = new IndexSearcher( reader );

            BooleanQuery booleanQueryMain = new BooleanQuery( );
            if ( setIdDomain != null && !setIdDomain.isEmpty( ) )
            {
                BooleanQuery booleanIdDomainQuery = new BooleanQuery( );
                for ( String strIdDomain : setIdDomain )
                {
                    TermQuery termQueryDomainId = new TermQuery( new Term( FIELD_DOMAIN_ID, strIdDomain ) );
                    booleanIdDomainQuery.add( new BooleanClause( termQueryDomainId, Occur.SHOULD ) );
                }
                booleanQueryMain.add( new BooleanClause( booleanIdDomainQuery, Occur.MUST ) );
            }

            Query query = new QueryParser( Version.LUCENE_4_9, FIELD_SEARCH_CONTENT, _analyzer ).parse( strQuery );
            booleanQueryMain.add( new BooleanClause( query, Occur.MUST ) );

            TopDocs results = searcher.search( booleanQueryMain, Short.MAX_VALUE );
            ScoreDoc [ ] hits = results.scoreDocs;

            AppLogService.debug( "\n Ticketing - Model Response  : query lucene " + hits.length + " \n" );

            for ( ScoreDoc hit : hits )
            {
                Document doc = searcher.doc( hit.doc );
                ModelResponse modelResponse = new ModelResponse( );
                modelResponse.setId( Integer.parseInt( doc.get( FIELD_ID ) ) );
                modelResponse.setTitle( doc.get( FIELD_TITLE ) );
                modelResponse.setReponse( doc.get( FIELD_RESPONSE ) );
                modelResponse.setDomain( doc.get( FIELD_DOMAIN_LABEL ) );
                modelResponse.setIdDomain( Integer.parseInt( doc.get( FIELD_DOMAIN_ID ) ) );
                modelResponse.setKeyword( doc.get( FIELD_KEYWORD ) );
                list.add( modelResponse );
            }

            reader.close( );
        }
        catch( IOException ex )
        {
            AppLogService.error( "\n Ticketing - Model Response : Error searching model response : " + ex.getMessage( ), ex );
        }
        catch( ParseException ex )
        {
            AppLogService.error( "\n Ticketing - Model Response : Error searching model response : " + ex.getMessage( ), ex );
        }

        return list;
    }

    /**
     * Gets the index writer.
     *
     * @param bcreate
     *            the bcreate
     * @return the index writer
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private IndexWriter getIndexWriter( Boolean bcreate ) throws IOException
    {
        Directory indexDir = FSDirectory.open( getIndexPath( ) );
        IndexWriterConfig config = new IndexWriterConfig( Version.LUCENE_4_9, _analyzer );

        if ( !DirectoryReader.indexExists( indexDir ) || bcreate )
        {
            config.setOpenMode( IndexWriterConfig.OpenMode.CREATE );
        }
        else
        {
            config.setOpenMode( IndexWriterConfig.OpenMode.APPEND );
        }

        IndexWriter indexWriter = new IndexWriter( indexDir, config );

        return indexWriter;
    }

    /**
     * Sets the analyzer.
     *
     * @param strClassAnalyzer
     *            the class to use
     */
    private void setAnalyzer( String strAnalyserClassName )
    {
        if ( ( strAnalyserClassName == null ) || ( strAnalyserClassName.equals( "" ) ) )
        {
            throw new AppException( "Analyser class name not found in context", null );
        }

        try
        {
            @SuppressWarnings( {
                "rawtypes"
            } )
            java.lang.reflect.Constructor constructeur = Class.forName( strAnalyserClassName ).getConstructor( Version.class, String [ ].class );
            _analyzer = (Analyzer) constructeur.newInstance( new Object [ ] {
                    IndexationService.LUCENE_INDEX_VERSION, new String [ ] { }
            } );
        }
        catch( InstantiationException ie )
        {
            @SuppressWarnings( "rawtypes" )
            Class classAnalyzer;

            try
            {
                classAnalyzer = Class.forName( strAnalyserClassName );

                @SuppressWarnings( {
                        "unchecked", "rawtypes"
                } )
                java.lang.reflect.Constructor constructeur = classAnalyzer.getConstructor( Version.class );
                _analyzer = (Analyzer) constructeur.newInstance( new Object [ ] {
                    IndexationService.LUCENE_INDEX_VERSION
                } );
            }
            catch( Exception e )
            {
                throw new AppException( "Failed to load Lucene Analyzer class", e );
            }
        }
        catch( Exception e )
        {
            throw new AppException( "Failed to load Lucene Analyzer class", e );
        }
    }

    /**
     * Gets the index path.
     *
     * @return the index path
     */
    private File getIndexPath( )
    {
        String strIndexPath = _strIndexPath;

        if ( _bIndexInWebapp )
        {
            strIndexPath = AppPathService.getAbsolutePathFromRelativePath( _strIndexPath );
        }

        return Paths.get( strIndexPath ).toFile( );
    }
}
