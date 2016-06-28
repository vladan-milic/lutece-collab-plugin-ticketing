/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.ticketing.business.modelresponse.search;

import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

/**
 *
 * @author root
 */
public class CustomAnalyzer extends StopwordAnalyzerBase {
    
   public static final Version LUCENE_VERSION = Version.LUCENE_4_9;
   
   public CustomAnalyzer()
        {
            super( LUCENE_VERSION , StandardAnalyzer.STOP_WORDS_SET );
        }

        @Override
        protected Analyzer.TokenStreamComponents createComponents(String fieldName, Reader reader) 
        {
            final Tokenizer source = new StandardTokenizer( LUCENE_VERSION , reader);

            TokenStream tokenStream = source;
            tokenStream = new StandardFilter( LUCENE_VERSION, tokenStream);
            tokenStream = new LowerCaseFilter( LUCENE_VERSION, tokenStream);
            tokenStream = new StopFilter( LUCENE_VERSION, tokenStream, getStopwordSet());
          // tokenStream = new ASCIIFoldingFilter(tokenStream);
        
            return new Analyzer.TokenStreamComponents(source, tokenStream);
        }
    
}
