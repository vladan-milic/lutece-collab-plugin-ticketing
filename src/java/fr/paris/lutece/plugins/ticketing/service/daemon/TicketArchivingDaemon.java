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
package fr.paris.lutece.plugins.ticketing.service.daemon;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.file.TicketFileHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 *
 *
 * Daemon used to archiving Tickets
 */
public class TicketArchivingDaemon extends Daemon
{

    /**
     * Constructor
     */
    public TicketArchivingDaemon( )
    {
        super( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        StringJoiner sb = new StringJoiner( "\n\r" );

        sb.add( "Début de l'archivage" );
        archivage( sb );
        sb.add( "Fin de l'archivage" );

        sb.add( "Début de la purge" );
        purge( );
        sb.add( "Fin de la purge" );
        setLastRunLogs( sb.toString( ) );

    }

    private void purge( )
    {
        Date purgeDate = new Date( );
        Calendar calpurgeDate = Calendar.getInstance( );
        calpurgeDate.setTime( purgeDate );
        calpurgeDate.add( Calendar.YEAR, -AppPropertiesService.getPropertyInt( "ticketing.daemon.archiving.purge.delai.annee", 5 ) );
        purgeDate = calpurgeDate.getTime( );
        TicketFileHome.purgeFromDate( purgeDate );
    }

    private void archivage( StringJoiner sb )
    {
        TicketFilter filter = new TicketFilter( );
        filter.setStatus( "1" );

        Date date = new Date( );
        Calendar cal = Calendar.getInstance( );
        cal.setTime( date );
        cal.add( Calendar.MONTH, -AppPropertiesService.getPropertyInt( "ticketing.daemon.archiving.archivage.delai.mois", 3 ) );
        date = cal.getTime( );

        filter.setCloseDate( date );

        List<Integer> ticketsList = TicketHome.getIdTicketsList( filter );

        for ( Integer idTicket : ticketsList )
        {
            List<Response> listResponse = TicketHome.findListResponse( idTicket );

            if ( listResponse != null )
            {
                for ( Response response : listResponse )
                {
                    if ( response != null )
                    {
                        File file = response.getFile( );
                        if ( file != null )
                        {
                            file = FileHome.findByPrimaryKey( file.getIdFile( ) );
                            boolean bMoved = TicketFileHome.migrateToBlob( file );
                            if ( bMoved )
                            {
                                sb.add( "Id Ticket:" + idTicket + ", Fichier archivé: " + file.getTitle( ) );
                            }
                        }
                    }
                }
            }
        }
    }
}
