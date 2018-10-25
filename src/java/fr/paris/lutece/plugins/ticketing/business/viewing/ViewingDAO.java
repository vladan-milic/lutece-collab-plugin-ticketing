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
package fr.paris.lutece.plugins.ticketing.business.viewing;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Viewing objects
 */
public class ViewingDAO implements IViewingDAO
{

    // Constants
    private static final String SQL_QUERY_NEW_PK       = "SELECT max( id_viewing ) FROM ticketing_viewing";
    private static final String SQL_QUERY_SELECT       = "SELECT id_viewing, title, message, buton_label, channel, contact_mode, civility, domain, thematic, location FROM ticketing_viewing";
    private static final String SQL_QUERY_INSERT       = "INSERT INTO ticketing_viewing ( id_viewing, title, message, buton_label, channel, contact_mode, civility, domain, thematic, location ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_UPDATE       = "UPDATE ticketing_viewing tv , (SELECT MAX(id_viewing) as id FROM ticketing_viewing) AS tvid SET tv.title = ?, tv.message = ?, tv.buton_label = ?, tv.channel = ?, tv.contact_mode = ?, tv.civility = ?, tv.domain = ?, tv.thematic = ?, tv.location = ? WHERE tv.id_viewing = tvid.id";
    private static final String SQL_QUERY_SELECT_COUNT = "SELECT count(1) FROM ticketing_viewing";

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
     * {@inheritDoc }
     */
    @Override
    public void insert( Viewing viewing, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        viewing.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, viewing.getId( ) );
        daoUtil.setString( 2, viewing.getTitle( ) );
        daoUtil.setString( 3, viewing.getMessage( ) );
        daoUtil.setString( 4, viewing.getValidationButon( ) );
        daoUtil.setString( 5, viewing.getIsChannel( ) );
        daoUtil.setString( 6, viewing.getIsContactMode( ) );
        daoUtil.setString( 7, viewing.getIsCivility( ) );
        daoUtil.setString( 8, viewing.getIsDomain( ) );
        daoUtil.setString( 9, viewing.getIsThematic( ) );
        daoUtil.setString( 10, viewing.getIsLocation( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Viewing viewing, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, viewing.getTitle( ) );
        daoUtil.setString( 2, viewing.getMessage( ) );
        daoUtil.setString( 3, viewing.getValidationButon( ) );
        daoUtil.setString( 4, viewing.getIsChannel( ) );
        daoUtil.setString( 5, viewing.getIsContactMode( ) );
        daoUtil.setString( 6, viewing.getIsCivility( ) );
        daoUtil.setString( 7, viewing.getIsDomain( ) );
        daoUtil.setString( 8, viewing.getIsThematic( ) );
        daoUtil.setString( 9, viewing.getIsLocation( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Viewing selectViewing( Plugin plugin )
    {
        Viewing viewing = new Viewing( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            viewing.setId( daoUtil.getInt( 1 ) );
            viewing.setTitle( daoUtil.getString( 2 ) );
            viewing.setMessage( daoUtil.getString( 3 ) );
            viewing.setValidationButon( daoUtil.getString( 4 ) );
            viewing.setIsChannel( daoUtil.getString( 5 ) );
            viewing.setIsContactMode( daoUtil.getString( 6 ) );
            viewing.setIsCivility( daoUtil.getString( 7 ) );
            viewing.setIsDomain( daoUtil.getString( 8 ) );
            viewing.setIsThematic( daoUtil.getString( 9 ) );
            viewing.setIsLocation( daoUtil.getString( 10 ) );
        }
        daoUtil.free( );

        return viewing;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int selectCount( Plugin plugin )
    {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT, plugin );
        daoUtil.executeQuery( );
        int nbRow = 0;
        if ( daoUtil.next( ) )
        {
            nbRow = daoUtil.getInt( 1 );
        }
        daoUtil.free( );
        return nbRow;
    }

}
