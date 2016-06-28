
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
    public void update(ModelResponse typicalReponse) throws IOException
    {
        AppLogService.info("\n Ticketing - Model Response : " + typicalReponse);
        try (IndexWriter writer = getIndexWriter())
        {
            Document doc = getDocument(typicalReponse);
            writer.updateDocument(new Term(FIELD_TYPICAL_RESPONSE_INFOS, typicalReponse.toString()), doc);
        }
    }

    private Document getDocument(ModelResponse typicalReponse)
    {
        Document doc = new Document();
        doc.add(new IntField(FIELD_ID, typicalReponse.getId(), Field.Store.YES));
        doc.add(new StringField(FIELD_TITLE, typicalReponse.getTitle(), Field.Store.YES));
        doc.add(new StringField(FIELD_KEYWORD, typicalReponse.getKeyword(), Field.Store.YES));
        doc.add(new StringField(FIELD_RESPONSE, typicalReponse.getReponse(), Field.Store.YES));
        doc.add(new StringField(FIELD_TYPICAL_RESPONSE_INFOS, typicalReponse.toString(), Field.Store.YES));
     
        doc.add(new TextField(FIELD_SEARCH_CONTENT, typicalReponse.getKeyword(), Field.Store.NO));
        return doc;
    }

    @Override
    public void delete(ModelResponse typicalReponse) throws IOException 
    {
        AppLogService.info("\n Ticketing - Model Response  : " + typicalReponse);
        try (IndexWriter writer = getIndexWriter()) 
        {
            writer.deleteDocuments(new Term("typical", typicalReponse.toString()));
        }
    }

    @Override
    public void add(ModelResponse typicalReponse) throws IOException
    {
        AppLogService.info("\n Ticketing - Model Response  : " + typicalReponse);
        try (IndexWriter writer = getIndexWriter())
        {
            Document doc = getDocument(typicalReponse);
            writer.addDocument(doc);
        }
    }

    @Override
    public String addAll() 
    {
        AppLogService.info("\n Ticketing - Model Response : Indexing All model response : \n");
        StringBuilder sbLogs = new StringBuilder();        
        try 
        {
            List<ModelResponse> typicalResponses = ModelResponseHome.getModelResponsesList();
            for (ModelResponse typicalResponse : typicalResponses)
            {
                add(typicalResponse);
            }
            sbLogs.append("\n Ticketing - Model Response : Indexed Model Responses : ").append(typicalResponses.size());
           
        }
        catch (IOException ex)
        {
            AppLogService.error("\n Ticketing - Model Response : Error indexing customer : " + ex.getMessage(), ex);
        }

        AppLogService.info("\n Ticketing - Model Response : end Indexing All model response : \n");
        return sbLogs.toString();
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

                AnalyzingQueryParser parser = new AnalyzingQueryParser(Version.LUCENE_4_9, "content", getAnalyzer());
                parser.setDefaultOperator(AnalyzingQueryParser.Operator.OR);
                Query query = parser.parse(strQuery);
                TopDocs results = searcher.search(query, nMaxResponsePerQuery );
                ScoreDoc[] hits = results.scoreDocs;

                AppLogService.info("\n Ticketing - Model Response  : query lucene " + hits.length + " \n");

                for (ScoreDoc hit : hits) 
                {
                    Document doc = searcher.doc(hit.doc);
                    ModelResponse typicalResponse = new ModelResponse();
                    //typicalResponse.setId(doc.get( "id" ) );
                    typicalResponse.setId(Integer.parseInt(doc.get(FIELD_ID)));
                    typicalResponse.setTitle(doc.get(FIELD_TITLE));
                    typicalResponse.setReponse(doc.get(FIELD_RESPONSE));
                    typicalResponse.setKeyword(doc.get(FIELD_KEYWORD));                    
                    list.add(typicalResponse);

                }
            }
        } 
        catch (IOException | ParseException ex)
        {
            AppLogService.error("\n Ticketing - Model Response : Error searching model response : " + ex.getMessage(), ex);
        }
                return list;
    }

    private IndexWriter getIndexWriter() throws IOException
    {

        Directory indexDir = FSDirectory.open(getIndexPath());
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, getAnalyzer());

        if (!DirectoryReader.indexExists(indexDir))
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
          String strAnalyserClassName = AppPropertiesService.getProperty( PROPERTY_ANALYSER_CLASS_NAME );
          
     /*      try
        {
            _analyzer = (Analyzer) Class.forName( strAnalyserClassName ).newInstance(  );           
            @SuppressWarnings( "rawtypes" )
            Class classAnalyzer;           
                classAnalyzer = Class.forName( strAnalyserClassName );
                @SuppressWarnings( {"unchecked",
                    "rawtypes"
                } )
                java.lang.reflect.Constructor constructeur = classAnalyzer.getConstructor( Version.class );
                _analyzer = (Analyzer) constructeur.newInstance( new Object[] { IndexationService.LUCENE_INDEX_VERSION } );
            }
            catch ( Exception e )
            {
                throw new AppException( "\n Ticketing - Model Response : Failed to load Lucene Analyzer class", e );
            } 
          * */
                  
       if (_analyzer == null)
        {
            _analyzer = new CustomAnalyzer();
        }  
        return _analyzer;
    }

    private File getIndexPath() 
    {
        String strIndexPath = AppPathService.getAbsolutePathFromRelativePath(PATH_INDEX);
        return Paths.get(strIndexPath).toFile();
    }

}
