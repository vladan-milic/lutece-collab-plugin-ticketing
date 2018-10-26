/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.business.file;

import fr.paris.lutece.plugins.blobstore.service.IBlobStoreService;
import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for Ticket file objects
 */
public final class TicketFileHome
{
    // Static variable pointed at the DAO instance
    private static ITicketFileDAO    _dao              = SpringContextService.getBean( "ticketing.ticketFileDAO" );
    private static Plugin            _plugin           = PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME );
    private static IBlobStoreService _blobStoreService = SpringContextService.getBean( "ticketing.blobStoreService" );

    /**
     * Migrate from database to filesystem file
     *
     * @param file
     *            File to migrate
     */
    public static void migrateToBlob( File file )
    {
        if ( ( file != null ) && ( file.getPhysicalFile( ) != null ) )
        {
            String strBlobId = _dao.findIdBlobByIdFile( file.getIdFile( ), _plugin );
            if ( strBlobId == null )
            {
                int idPhysicalFile = file.getPhysicalFile( ).getIdPhysicalFile( );
                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( idPhysicalFile );
                if ( physicalFile != null )
                {
                    String strIdBlob = _blobStoreService.store( physicalFile.getValue( ) );
                    if ( strIdBlob != null )
                    {
                        _dao.insert( file.getIdFile( ), strIdBlob, _plugin );
                        PhysicalFileHome.remove( idPhysicalFile );// TODO ajout un parametre boolean depuis properties
                    }
                }
            }
        }
    }

    /**
     * Find File from database or filesystem
     *
     * @param file
     *            File to find
     * @return physical file
     */
    public static PhysicalFile findPhysicalFile( File file )
    {
        PhysicalFile physicalFile = null;
        if ( ( file != null ) && ( file.getPhysicalFile( ) != null ) )
        {
            String strBlobId = _dao.findIdBlobByIdFile( file.getIdFile( ), _plugin );
            if ( strBlobId != null )
            {
                byte[] blob = _blobStoreService.getBlob( strBlobId );
                if ( blob != null )
                {
                    physicalFile = new PhysicalFile( );
                    physicalFile.setValue( blob );
                }
            } else
            {
                physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );
            }
        }
        return physicalFile;
    }

}
