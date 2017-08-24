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

import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Category objects
 */
public final class TicketCategoryDAO implements ITicketCategoryDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_category ) FROM ticketing_category";
    private static final String SQL_QUERY_SELECT = "SELECT id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, id_workflow FROM ticketing_category WHERE id_category = ?";
    private static final String SQL_QUERY_SELECT_BY_CODE = "SELECT id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, id_workflow FROM ticketing_category WHERE code = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_category ( id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, id_workflow ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_category WHERE id_category = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_category SET id_category = ?, id_parent = ?, label = ?, n_order = ?, code = ?, id_default_assignee_unit = ?, id_category_type = ?, id_workflow = ? WHERE id_category = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, id_workflow FROM ticketing_category";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_category FROM ticketing_category";

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
    public void insert( TicketCategory category, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        category.setId( newPrimaryKey( plugin ) );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, category.getId( ) );
        daoUtil.setInt( nIndex++, category.getIdParent( ) );
        daoUtil.setString( nIndex++, category.getLabel( ) );
        daoUtil.setInt( nIndex++, category.getOrder( ) );
        daoUtil.setString( nIndex++, category.getCode( ) );
        daoUtil.setInt( nIndex++, category.getDefaultAssignUnit( ).getIdUnit( ) );
        daoUtil.setInt( nIndex++, category.getCategoryType( ).getId( ) );
        daoUtil.setInt( nIndex++, category.getIdWorkflow( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketCategory load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        TicketCategory category = null;

        if ( daoUtil.next( ) )
        {
            category = new TicketCategory( );
            int nIndex = 1;

            category.setId( daoUtil.getInt( nIndex++ ) );
            category.setIdParent( daoUtil.getInt( nIndex++ ) );
            category.setLabel( daoUtil.getString( nIndex++ ) );
            category.setOrder( daoUtil.getInt( nIndex++ ) );
            category.setCode( daoUtil.getString( nIndex++ ) );
            category.getDefaultAssignUnit( ).setIdUnit( daoUtil.getInt( nIndex++ ) );
            category.getCategoryType( ).setId( daoUtil.getInt( nIndex++ ) );
            category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );
        return category;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketCategory loadByCode( String strCode, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CODE, plugin );
        daoUtil.setString( 1, strCode );
        daoUtil.executeQuery( );
        TicketCategory category = null;

        if ( daoUtil.next( ) )
        {
            category = new TicketCategory( );
            int nIndex = 1;

            category.setId( daoUtil.getInt( nIndex++ ) );
            category.setIdParent( daoUtil.getInt( nIndex++ ) );
            category.setLabel( daoUtil.getString( nIndex++ ) );
            category.setOrder( daoUtil.getInt( nIndex++ ) );
            category.setCode( daoUtil.getString( nIndex++ ) );
            category.getDefaultAssignUnit( ).setIdUnit( daoUtil.getInt( nIndex++ ) );
            category.getCategoryType( ).setId( daoUtil.getInt( nIndex++ ) );
            category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );
        return category;
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
    public void store( TicketCategory category, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, category.getId( ) );
        daoUtil.setInt( nIndex++, category.getIdParent( ) );
        daoUtil.setString( nIndex++, category.getLabel( ) );
        daoUtil.setInt( nIndex++, category.getOrder( ) );
        daoUtil.setString( nIndex++, category.getCode( ) );
        daoUtil.setInt( nIndex++, category.getDefaultAssignUnit( ).getIdUnit( ) );
        daoUtil.setInt( nIndex++, category.getCategoryType( ).getId( ) );
        daoUtil.setInt( nIndex++, category.getIdWorkflow( ) );
        daoUtil.setInt( nIndex, category.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> selectCategorysList( Plugin plugin )
    {
        List<TicketCategory> categoryList = new ArrayList<TicketCategory>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            TicketCategory category = new TicketCategory( );
            int nIndex = 1;

            category.setId( daoUtil.getInt( nIndex++ ) );
            category.setIdParent( daoUtil.getInt( nIndex++ ) );
            category.setLabel( daoUtil.getString( nIndex++ ) );
            category.setOrder( daoUtil.getInt( nIndex++ ) );
            category.setCode( daoUtil.getString( nIndex++ ) );
            category.getDefaultAssignUnit( ).setIdUnit( daoUtil.getInt( nIndex++ ) );
            category.getCategoryType( ).setId( daoUtil.getInt( nIndex++ ) );
            category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
            categoryList.add( category );
        }

        daoUtil.free( );
        return categoryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> selectFullCategorysList( Plugin plugin )
    {
        List<TicketCategory> categoryList = new ArrayList<TicketCategory>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            TicketCategory category = new TicketCategory( );
            int nIndex = 1;

            category.setId( daoUtil.getInt( nIndex++ ) );
            category.setIdParent( daoUtil.getInt( nIndex++ ) );
            category.setLabel( daoUtil.getString( nIndex++ ) );
            category.setOrder( daoUtil.getInt( nIndex++ ) );
            category.setCode( daoUtil.getString( nIndex++ ) );

            Unit unit = UnitHome.findByPrimaryKey( daoUtil.getInt( nIndex++ ) );
            if ( unit == null )
            {
                unit = new Unit( );
                unit.setIdUnit( -1 );
            }
            category.setDefaultAssignUnit( unit );

            TicketCategoryType categoryType = TicketCategoryTypeHome.findByPrimaryKey( daoUtil.getInt( nIndex++ ) );
            if ( categoryType == null )
            {
                categoryType = new TicketCategoryType( );
                categoryType.setId( -1 );
                categoryType.setDepth( -1 );
            }
            category.setCategoryType( categoryType );

            category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );

            categoryList.add( category );
        }

        daoUtil.free( );
        return categoryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdCategorysList( Plugin plugin )
    {
        List<Integer> categoryList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            categoryList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return categoryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectCategorysReferenceList( Plugin plugin )
    {
        ReferenceList categoryList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            categoryList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );
        return categoryList;
    }
}
