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
package fr.paris.lutece.plugins.ticketing.business.categoryinputs;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for TicketCategory objects
 */
public final class TicketCategoryInputsDAO implements ITicketCategoryInputsDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT_INPUTS_BY_CATEGORY = "SELECT id_input FROM ticketing_category_input WHERE id_category = ? ORDER BY pos";
    private static final String SQL_QUERY_INSERT_INPUT = "INSERT INTO ticketing_category_input ( id_category, id_input, pos ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE_INPUT = "DELETE FROM ticketing_category_input WHERE id_category = ? AND id_input = ?";
    private static final String SQL_QUERY_DELETE_ALL_INPUT = "DELETE FROM ticketing_category_input WHERE id_category = ?";
    private static final String SQL_QUERY_UPDATE_INPUT_POS = "UPDATE ticketing_category_input SET pos = ? WHERE id_category = ? AND id_input = ? ";
    private static final String SQL_QUERY_SELECT_MAX_INPUT_POS_FOR_CATEGORY = "SELECT MAX(pos) FROM ticketing_category_input WHERE id_category = ? ";
    private static final String SQL_QUERY_SELECT_INPUT_POS = "SELECT pos from ticketing_category_input WHERE id_category = ? AND id_input = ? ";
    private static final String SQL_QUERY_SELECT_INPUT_BY_POS = "SELECT id_input from ticketing_category_input WHERE id_category = ? AND pos = ? ";
    private static final String SQL_QUERY_SELECT_INPUT_IN_ALL_CATEGORIES = "SELECT id_category from ticketing_category_input WHERE id_input = ? ";

    /**
     * Return the next available Position value for inputs linked to a category
     *
     * @param nIdCategory
     *            Id Category
     *
     * @param plugin
     *            The Plugin
     * @return The new primary key
     */
    public int getNextPositionForCategoryInputs( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_INPUT_POS_FOR_CATEGORY, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeQuery( );

        int nPos = 1;

        if ( daoUtil.next( ) )
        {
            nPos = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nPos;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insertLinkCategoryInput( int nIdCategory, int nIdInput, int nPos, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_INPUT, plugin );

        daoUtil.setInt( 1, nIdCategory );
        daoUtil.setInt( 2, nIdInput );
        daoUtil.setInt( 3, nPos );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insertLinkCategoryInputNextPos( int nIdCategory, int nIdInput, Plugin plugin )
    {
        int nPos = getNextPositionForCategoryInputs( nIdCategory, plugin );
        insertLinkCategoryInput( nIdCategory, nIdInput, nPos, plugin );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateLinkCategoryInputPos( int nIdCategory, int nIdInput, int nPos, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_INPUT_POS, plugin );
        daoUtil.setInt( 1, nPos );
        daoUtil.setInt( 2, nIdCategory );
        daoUtil.setInt( 3, nIdInput );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean checkIfInputIsUsedInCategories( int nIdResource, Plugin _plugin )
    {
        boolean isInputUsedInCategories = false;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INPUT_IN_ALL_CATEGORIES, _plugin );
        daoUtil.setInt( 1, nIdResource );
        daoUtil.executeQuery( );

        isInputUsedInCategories = daoUtil.next( );

        daoUtil.free( );

        return isInputUsedInCategories;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteLinkCategoryInput( int nIdCategory, int nIdInput, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_INPUT, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.setInt( 2, nIdInput );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteAllLinksCategoryInput( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL_INPUT, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int selectCategoryInputPosition( int nId, int nIdInput, Plugin plugin )
    {
        int nPosition = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INPUT_POS, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.setInt( 2, nIdInput );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nPosition = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nPosition;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int selectCategoryInputByPosition( int nId, int nPos, Plugin plugin )
    {
        int nIdInput = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INPUT_BY_POS, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.setInt( 2, nPos );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nIdInput = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nIdInput;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdInputListByCategory( int nCategoryId, Plugin plugin )
    {
        List<Integer> ticketInputList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INPUTS_BY_CATEGORY, plugin );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ticketInputList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return ticketInputList;
    }

}
