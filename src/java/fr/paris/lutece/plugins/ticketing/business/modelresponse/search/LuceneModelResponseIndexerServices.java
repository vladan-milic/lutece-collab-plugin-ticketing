
package fr.paris.lutece.plugins.ticketing.business.modelresponse.search;

import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponse;
import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponseHome;
import fr.paris.lutece.plugins.ticketing.web.search.SearchConstants;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.analyzing.AnalyzingQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class LuceneModelResponseIndexerServices implements IModelResponseIndexer {

    private static LuceneModelResponseIndexerServices _singleton;
    private static final String BEAN_SERVICE = "ticketing.modelResponsesServices";
     private static final String PROPERTY_ANALYSER_CLASS_NAME = "ticketing.internalIndexer.lucene.analyser.className";

    private static Analyzer _analyzer;

     private LuceneModelResponseIndexerServices()
     {
                  
     }
   
    public static LuceneModelResponseIndexerServices instance() 
    {
        if (_singleton == null) {
            _singleton = SpringContextService.getBean(BEAN_SERVICE);
        }
        return _singleton;
    }

    @Override
    public void update(ModelResponse modelReponse) throws IOException
    {
        AppLogService.info("\n Ticketing - Model Response : " + modelReponse);
        try (IndexWriter writer = getIndexWriter(false))
        {
            Document doc = getDocument(modelReponse);
            writer.updateDocument(new Term(FIELD_MODEL_RESPONSE_INFOS, modelReponse.toString()), doc);
        }
    }

    private Document getDocument(ModelResponse modelReponse)
    {
        Document doc = new Document();
        doc.add(new IntField(FIELD_ID, modelReponse.getId(), Field.Store.YES));
        doc.add(new StringField(FIELD_TITLE, modelReponse.getTitle(), Field.Store.YES));
        doc.add(new StringField(FIELD_KEYWORD, modelReponse.getKeyword(), Field.Store.YES));
        doc.add(new StringField(FIELD_RESPONSE, modelReponse.getReponse(), Field.Store.YES));
        doc.add(new StringField(FIELD_MODEL_RESPONSE_INFOS, modelReponse.toString(), Field.Store.YES));
     
        doc.add(new TextField(FIELD_SEARCH_CONTENT, modelReponse.getKeyword(), Field.Store.NO));
        return doc;
    }

    @Override
    public void delete(ModelResponse modelReponse) throws IOException 
    {
        AppLogService.info("\n Ticketing - Model Response  : " + modelReponse);
        try (IndexWriter writer = getIndexWriter(false)) 
        {
            writer.deleteDocuments(new Term(FIELD_MODEL_RESPONSE_INFOS, modelReponse.toString()));
        }
    }

    @Override
    public void add(ModelResponse modelReponse) throws IOException
    {
        AppLogService.info("\n Ticketing - Model Response  : " + modelReponse);
        try (IndexWriter writer = getIndexWriter(false))
        {
            Document doc = getDocument(modelReponse);
            writer.addDocument(doc);
        }
    }

    @Override
    public String addAll() 
    {
        AppLogService.info("\n Ticketing - Model Response : Indexing All model response : \n");
        StringBuilder sbLogs = new StringBuilder();        
          
        try (IndexWriter writer = getIndexWriter(true))
        {
        
            List<ModelResponse> modelResponses = ModelResponseHome.getModelResponsesList();
            for (ModelResponse modelResponse : modelResponses)
            {
               Document doc = getDocument(modelResponse);
               writer.addDocument(doc);
            }
            sbLogs.append("\n Ticketing - Model Response : Indexed Model Responses : ").append(modelResponses.size());
           
        }
        catch (IOException ex)
        {
            AppLogService.error("\n Ticketing - Model Response : Error indexing model response : " + ex.getMessage(), ex);
        }

        AppLogService.info("\n Ticketing - Model Response : end Indexing All model response : \n");
        return sbLogs.toString();
    }
    
    private String customerParser(String strQuery)
    {    
        String strResult = QueryParser.escape(strQuery);
        
        strResult= strResult.replaceAll("@", "\\@");
        return strResult;
    }

    @Override
    public List<ModelResponse> searchResponses(String strQuery) 
    {
        List<ModelResponse> list = new ArrayList<>();
        int nMaxResponsePerQuery = AppPropertiesService.getPropertyInt( SearchConstants.PROPERTY_MODEL_RESPONSE_LIMIT_PER_QUERY, 5 );
        try {
            try (IndexReader reader = DirectoryReader.open(FSDirectory.open(getIndexPath()))) 
            {
                IndexSearcher searcher = new IndexSearcher(reader);
                AnalyzingQueryParser parser = new AnalyzingQueryParser(Version.LUCENE_4_9, FIELD_SEARCH_CONTENT, getAnalyzer());
                parser.setDefaultOperator(AnalyzingQueryParser.Operator.OR);   
                
               
               Query query = parser.parse( customerParser(strQuery));
                TopDocs results = searcher.search(query, nMaxResponsePerQuery );
                ScoreDoc[] hits = results.scoreDocs;

                AppLogService.info("\n Ticketing - Model Response  : query lucene " + hits.length + " \n");

                for (ScoreDoc hit : hits) 
                {
                    Document doc = searcher.doc(hit.doc);
                    ModelResponse modelResponse = new ModelResponse();                   
                    modelResponse.setId(Integer.parseInt(doc.get(FIELD_ID)));
                    modelResponse.setTitle(doc.get(FIELD_TITLE));
                    modelResponse.setReponse(doc.get(FIELD_RESPONSE));
                    modelResponse.setKeyword(doc.get(FIELD_KEYWORD));                    
                    list.add(modelResponse);

                }
            }
        } 
        catch (IOException | ParseException ex)
        {
            AppLogService.error("\n Ticketing - Model Response : Error searching model response : " + ex.getMessage(), ex);
        }
                return list;
    }

    private IndexWriter getIndexWriter(Boolean bcreate) throws IOException
    {

        Directory indexDir = FSDirectory.open(getIndexPath());
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, getAnalyzer());

        if (!DirectoryReader.indexExists(indexDir) || bcreate)
        {
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } 
        else
        {
            config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        }

        IndexWriter indexWriter = new IndexWriter(indexDir, config);
        return indexWriter;
    }

    private Analyzer getAnalyzer() 
    {
        
        if( _analyzer == null )
        {
         String strAnalyserClassName = AppPropertiesService.getProperty( PROPERTY_ANALYSER_CLASS_NAME );
          
       
        if ( ( strAnalyserClassName == null ) || ( strAnalyserClassName.equals( "" ) ) )
        {
            throw new AppException( "Analyser class name not found in ticketing.properties", null );
        }
        
          try
        {
            _analyzer = (Analyzer) Class.forName( strAnalyserClassName ).newInstance(  );
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
        
               
  
        return _analyzer;
    }

    private File getIndexPath() 
    {
        String strIndexPath = AppPathService.getAbsolutePathFromRelativePath(PATH_INDEX);
        return Paths.get(strIndexPath).toFile();
    }

}
