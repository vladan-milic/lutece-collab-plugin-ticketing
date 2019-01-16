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
package fr.paris.lutece.plugins.ticketing.business.sphinx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
import fr.paris.lutece.plugins.ticketing.web.rs.SphinxRest;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public final class SphinxServices implements ISphinxServices
{

    // Header export
    private static final String HEADER_EMAIL = "email";
    private static final String HEADER_DOMAINE = "domaine";
    private static final String HEADER_THEMATIQUE = "thematique";
    private static final String HEADER_SOUS_THEMATIQUE = "sous-thematique";
    private static final String HEADER_LOCALISATION = "localisation";
    private static final String HEADER_DELAI = "delai (en jours)";

    private static final int DEPTH_DOMAINE = 1;
    private static final int DEPTH_THEMATIQUE = 2;
    private static final int DEPTH_SOUS_THEMATIQUE = 3;
    private static final int DEPTH_LOCALISATION = 4;

    private static final String ID_ACTION = "daemon.sphinxDaemon.idAction";

    private static File tempFile;

    /**
     * 
     */
    public SphinxServices( )
    {
        super( );
    }

    public synchronized String mailingToSphinx( )
    {

        int idAction = Integer.parseInt( AppPropertiesService.getProperty( ID_ACTION ) );

        StringBuffer sbLogs = new StringBuffer( );
        ISphinxDAO _dao = SpringContextService.getBean( "ticketing.sphinxDAO" );
        try
        {
            sbLogs.append( "\r\n<strong>SPHINX TREATMENT...\r\n" );

            Date start = new Date( );

            sbLogs.append( "\r\n<strong>Get Mailing List From DB : " );
            List<Mailing> mailingList = _dao.getNewMailingList( idAction, PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME ) );

            sbLogs.append( "\r\n<strong>Create CSV File" );
            createFile( mailingList );

            sbLogs.append( "\r\n<strong>Send Mailing to Sphinx API: " );
            SphinxRest.getHttpCon( );

            Date end = new Date( );
            sbLogs.append( "Duration of the treatment : " );
            sbLogs.append( end.getTime( ) - start.getTime( ) );
            sbLogs.append( " milliseconds\r\n" );
        }
        catch( Exception e )
        {
            sbLogs.append( " caught a " );
            sbLogs.append( e.getClass( ) );
            sbLogs.append( "\n with message: " );
            sbLogs.append( e.getMessage( ) );
            sbLogs.append( "\r\n" );
            AppLogService.error( "Sphinx Mailing error : " + e.getMessage( ), e );
        }
        finally
        {
            //
        }

        return sbLogs.toString( );
    }

    public static void createFile( List<Mailing> mailingList ) throws IOException
    {

        Date date = new Date( );
        String domaine = "";
        String thematique = "";
        String sous_thematique = "";
        String localisation = "";
        int duree = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        tempFile = File.createTempFile( "mailing_" + dateFormat.format( date ), ".csv" );

        CSVFormat csvFormat = CSVFormat.DEFAULT
                .withHeader( HEADER_EMAIL, HEADER_DOMAINE, HEADER_THEMATIQUE, HEADER_SOUS_THEMATIQUE, HEADER_LOCALISATION, HEADER_DELAI )
                .withRecordSeparator( '\n' ).withDelimiter( ';' );
        try( Writer writer = new OutputStreamWriter( new FileOutputStream( tempFile ), StandardCharsets.ISO_8859_1 ) ;

        CSVPrinter csvPrinter = new CSVPrinter( writer, csvFormat ) )
        {
            for ( Mailing mailing : mailingList )
            {

                String [ ] parts = mailing.getDomains( ).split( ";" );
                switch( parts.length )
                {
                    case DEPTH_DOMAINE:
                        domaine = parts [0];
                        break;
                    case DEPTH_THEMATIQUE:
                        thematique = parts [0];
                        domaine = parts [1];
                        break;
                    case DEPTH_SOUS_THEMATIQUE:
                        sous_thematique = parts [0];
                        thematique = parts [1];
                        domaine = parts [2];
                        break;
                    case DEPTH_LOCALISATION:
                        localisation = parts [0];
                        sous_thematique = parts [1];
                        thematique = parts [2];
                        domaine = parts [3];
                        break;
                }

                if ( Integer.parseInt( mailing.getDuration( ) ) == 0 )
                {
                    duree = 1;
                }
                else
                {
                    duree = Integer.parseInt( mailing.getDuration( ) );
                }

                csvPrinter.printRecord( mailing.getEMail( ), domaine, thematique, sous_thematique, localisation, duree );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( "Failed to export labels to file " + tempFile.getAbsolutePath( ) + " due to error: {}", e );
        }

    }

}
