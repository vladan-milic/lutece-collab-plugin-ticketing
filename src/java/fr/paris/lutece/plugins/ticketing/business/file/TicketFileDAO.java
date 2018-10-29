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

import java.sql.Timestamp;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Ticket file objects
 */
public final class TicketFileDAO implements ITicketFileDAO
{
    private static final String SQL_QUERY_NEW_PK                        = "SELECT max( id_file_blob ) FROM ticketing_file_blob";
    private static final String SQL_QUERY_INSERT                        = "INSERT INTO ticketing_file_blob ( id_file_blob, id_file, id_blob ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE                        = "DELETE FROM ticketing_file_blob where id_file = ? ";
    private static final String SQL_QUERY_FIND_BY_FILE_ID               = "SELECT id_blob FROM ticketing_file_blob WHERE id_file = ? ";
    private static final String SQL_QUERY_FIND_DATE_CREATION_BY_FILE_ID = "SELECT creation_date FROM ticketing_file_blob WHERE id_file = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( int nIdFile, String strIdBlob, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, newPrimaryKey( plugin ) );
        daoUtil.setInt( nIndex++, nIdFile );
        daoUtil.setString( nIndex++, strIdBlob );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findIdBlobByIdFile( int nIdFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_FILE_ID, plugin );
        daoUtil.setInt( 1, nIdFile );
        daoUtil.executeQuery( );

        String strIdBlob = null;

        if ( daoUtil.next( ) )
        {
            strIdBlob = daoUtil.getString( 1 );
        }

        daoUtil.free( );

        return strIdBlob;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFile );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );
        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );
        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp findCreationDateByIdFile( int nIdFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_DATE_CREATION_BY_FILE_ID, plugin );
        daoUtil.setInt( 1, nIdFile );
        daoUtil.executeQuery( );

        Timestamp creationDate = null;

        if ( daoUtil.next( ) )
        {
            creationDate = daoUtil.getTimestamp( 1 );
        }

        daoUtil.free( );

        return creationDate;
    }

}
