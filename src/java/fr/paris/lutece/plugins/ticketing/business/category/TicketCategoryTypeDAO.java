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

package fr.paris.lutece.plugins.ticketing.business.category;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for CategoryType objects
 */
public final class TicketCategoryTypeDAO implements ITicketCategoryTypeDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK          = "SELECT max( id_category_type ) FROM ticketing_category_type";
    private static final String SQL_QUERY_SELECT          = "SELECT id_category_type, label, depth FROM ticketing_category_type WHERE id_category_type = ?";
    private static final String SQL_QUERY_INSERT          = "INSERT INTO ticketing_category_type ( id_category_type, label, depth ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE          = "DELETE FROM ticketing_category_type WHERE id_category_type = ? ";
    private static final String SQL_QUERY_UPDATE          = "UPDATE ticketing_category_type SET id_category_type = ?, label = ?, depth = ? WHERE id_category_type = ?";
    private static final String SQL_QUERY_SELECTALL       = "SELECT id_category_type, label, depth FROM ticketing_category_type ORDER BY depth";
    private static final String SQL_QUERY_SELECTALL_ID    = "SELECT id_category_type FROM ticketing_category_type ORDER BY depth";
    private static final String SQL_QUERY_NEW_DEPTH       = "SELECT max( depth ) FROM ticketing_category_type";
    private static final String SQL_QUERY_SELECT_BY_DEPTH = "SELECT id_category_type, label, depth FROM ticketing_category_type WHERE depth = ?";

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
    public void insert( TicketCategoryType categoryType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        categoryType.setId( newPrimaryKey( plugin ) );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, categoryType.getId( ) );
        daoUtil.setString( nIndex++, categoryType.getLabel( ) );
        daoUtil.setInt( nIndex++, newDepth( plugin ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketCategoryType load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        TicketCategoryType categoryType = null;

        if ( daoUtil.next( ) )
        {
            categoryType = new TicketCategoryType( );
            int nIndex = 1;

            categoryType.setId( daoUtil.getInt( nIndex++ ) );
            categoryType.setLabel( daoUtil.getString( nIndex++ ) );
            categoryType.setDepthNumber( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );
        return categoryType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( TicketCategoryType categoryType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, categoryType.getId( ) );
        daoUtil.setString( nIndex++, categoryType.getLabel( ) );
        daoUtil.setInt( nIndex++, categoryType.getDepthNumber( ) );
        daoUtil.setInt( nIndex, categoryType.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategoryType> selectCategoryTypesList( Plugin plugin )
    {
        List<TicketCategoryType> categoryTypeList = new ArrayList<TicketCategoryType>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            TicketCategoryType categoryType = new TicketCategoryType( );
            int nIndex = 1;

            categoryType.setId( daoUtil.getInt( nIndex++ ) );
            categoryType.setLabel( daoUtil.getString( nIndex++ ) );
            categoryType.setDepthNumber( daoUtil.getInt( nIndex++ ) );

            categoryTypeList.add( categoryType );
        }

        daoUtil.free( );
        return categoryTypeList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdCategoryTypesList( Plugin plugin )
    {
        List<Integer> categoryTypeList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            categoryTypeList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return categoryTypeList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectCategoryTypesReferenceList( Plugin plugin )
    {
        ReferenceList categoryTypeList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            categoryTypeList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );
        return categoryTypeList;
    }

    /**
     * Return the new depth of categories types
     */
    private int newDepth( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_DEPTH, plugin );
        daoUtil.executeQuery( );
        int nDepth = 1;

        if ( daoUtil.next( ) )
        {
            nDepth = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );
        return nDepth;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insertNewDepthCategoryType( TicketCategoryType categoryType, Plugin plugin )
    {
        categoryType.setDepthNumber( newDepth( plugin ) );
        insert( categoryType, plugin );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketCategoryType loadByDepth( int nDepth, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DEPTH, plugin );
        daoUtil.setInt( 1, nDepth );
        daoUtil.executeQuery( );
        TicketCategoryType categoryType = null;

        if ( daoUtil.next( ) )
        {
            categoryType = new TicketCategoryType( );
            int nIndex = 1;

            categoryType.setId( daoUtil.getInt( nIndex++ ) );
            categoryType.setLabel( daoUtil.getString( nIndex++ ) );
            categoryType.setDepthNumber( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );
        return categoryType;
    }

}
