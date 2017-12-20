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

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.categoryinputs.TicketCategoryInputsHome;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Category objects
 */
public final class TicketCategoryDAO implements ITicketCategoryDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK                          = "SELECT max( id_category ) FROM ticketing_category";
    private static final String SQL_QUERY_SELECT                          = "SELECT id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, demand_id, help_message, is_manageable FROM ticketing_category WHERE id_category = ?";
    private static final String SQL_QUERY_SELECT_BY_CODE                  = "SELECT id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, demand_id, help_message, is_manageable FROM ticketing_category WHERE code = ?";
    private static final String SQL_QUERY_INSERT                          = "INSERT INTO ticketing_category ( id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, demand_id, help_message, is_manageable ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE                          = "UPDATE ticketing_category SET inactive = 1 WHERE id_category = ? ";
    private static final String SQL_QUERY_UPDATE                          = "UPDATE ticketing_category SET id_category = ?, id_parent = ?, label = ?, n_order = ?, code = ?, id_default_assignee_unit = ?, id_category_type = ?, demand_id = ?, help_message = ?, is_manageable = ? WHERE id_category = ?";
    private static final String SQL_QUERY_SELECTALL                       = "SELECT id_category, id_parent, label, n_order, code, id_default_assignee_unit, id_category_type, demand_id, help_message, is_manageable FROM ticketing_category WHERE inactive <> 1 ORDER BY id_parent, n_order";
    private static final String SQL_QUERY_SELECTALL_ID                    = "SELECT id_category FROM ticketing_category WHERE inactive <> 1 ";
    private static final String SQL_QUERY_MAX_CATEGORY_ORDER_BY_TYPE      = "SELECT max(n_order) FROM ticketing_category WHERE id_parent = ? AND inactive <> 1";
    private static final String SQL_QUERY_REBUILD_CATEGORY_ORDER_SEQUENCE = "UPDATE ticketing_category SET n_order = n_order - 1 WHERE n_order > ? AND id_parent = ? AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECT_CATEGORYID_BY_ORDER      = "SELECT id_category FROM ticketing_category WHERE id_parent = ? AND n_order = ? ";
    private static final String SQL_QUERY_UPDATE_CATEGORY_ORDER           = "UPDATE ticketing_category SET n_order = ? WHERE id_category = ? ";
    private static final String SQL_QUERY_COUNT_SUB_CATEGORY_BY_CATEGORY  = "SELECT COUNT(1) FROM ticketing_category WHERE id_parent = ? AND inactive <> 1 ";

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
        int nextOrder = newCategoryOrder( category.getIdParent( ), plugin );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        category.setId( newPrimaryKey( plugin ) );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, category.getId( ) );
        daoUtil.setInt( nIndex++, category.getIdParent( ) );
        daoUtil.setString( nIndex++, category.getLabel( ) );
        daoUtil.setInt( nIndex++, nextOrder );
        daoUtil.setString( nIndex++, category.getCode( ) );
        daoUtil.setInt( nIndex++, category.getDefaultAssignUnit( ).getUnitId( ) );
        daoUtil.setInt( nIndex++, category.getCategoryType( ).getId( ) );
        daoUtil.setInt( nIndex++, category.getDemandId( ) );
        daoUtil.setString( nIndex++, category.getHelpMessage( ) );
        daoUtil.setBoolean( nIndex++, category.isManageable( ) );

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
            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            if ( unit != null )
            {
                AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
                category.setDefaultAssignUnit( assigneeUnit );
            }
            category.getCategoryType( ).setId( daoUtil.getInt( nIndex++ ) );
            category.setDemandId( daoUtil.getInt( nIndex++ ) );
            category.setHelpMessage( daoUtil.getString( nIndex++ ) );
            category.setListIdInput( TicketCategoryInputsHome.getIdInputListByCategory( category.getId( ) ) );
            category.setManageable( daoUtil.getBoolean( nIndex++ ) );
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
            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            if ( unit != null )
            {
                AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
                category.setDefaultAssignUnit( assigneeUnit );
            }
            category.getCategoryType( ).setId( daoUtil.getInt( nIndex++ ) );
            category.setDemandId( daoUtil.getInt( nIndex++ ) );
            category.setHelpMessage( daoUtil.getString( nIndex++ ) );
            category.setManageable( daoUtil.getBoolean( nIndex++ ) );
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
        daoUtil.setInt( nIndex++, category.getDefaultAssignUnit( ).getUnitId( ) );
        daoUtil.setInt( nIndex++, category.getCategoryType( ).getId( ) );
        daoUtil.setInt( nIndex++, category.getDemandId( ) );
        daoUtil.setString( nIndex++, category.getHelpMessage( ) );
        daoUtil.setBoolean( nIndex++, category.isManageable( ) );
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
            category.getDefaultAssignUnit( ).setUnitId( daoUtil.getInt( nIndex++ ) );
            category.getCategoryType( ).setId( daoUtil.getInt( nIndex++ ) );
            category.setDemandId( daoUtil.getInt( nIndex++ ) );
            category.setHelpMessage( daoUtil.getString( nIndex++ ) );
            category.setManageable( daoUtil.getBoolean( nIndex++ ) );
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

            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            if ( unit != null )
            {
                AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
                category.setDefaultAssignUnit( assigneeUnit );
            }

            TicketCategoryType categoryType = TicketCategoryTypeHome.findByPrimaryKey( daoUtil.getInt( nIndex++ ) );
            if ( categoryType == null )
            {
                categoryType = new TicketCategoryType( );
                categoryType.setId( -1 );
                categoryType.setDepthNumber( -1 );
            }
            category.setCategoryType( categoryType );
            category.setListIdInput( TicketCategoryInputsHome.getIdInputListByCategory( category.getId( ) ) );
            category.setDemandId( daoUtil.getInt( nIndex++ ) );
            category.setHelpMessage( daoUtil.getString( nIndex++ ) );
            category.setManageable( daoUtil.getBoolean( nIndex++ ) );

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

    @Override
    public void updateCategoryOrder( int nId, int nNewPosition, Plugin _plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_CATEGORY_ORDER, _plugin );
        daoUtil.setInt( 1, nNewPosition );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    @Override
    public int selectCategoryIdByOrder( int nOrder, int nIdParent, Plugin _plugin )
    {
        int nTicketTypeId = -1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CATEGORYID_BY_ORDER, _plugin );
        daoUtil.setInt( 1, nIdParent );
        daoUtil.setInt( 2, nOrder );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            nTicketTypeId = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nTicketTypeId;
    }

    @Override
    public void rebuildCategoryOrders( int nFromOrder, int nIdParent, Plugin _plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REBUILD_CATEGORY_ORDER_SEQUENCE, _plugin );
        daoUtil.setInt( 1, nFromOrder );
        daoUtil.setInt( 2, nIdParent );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    private int newCategoryOrder( int nIdParent, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MAX_CATEGORY_ORDER_BY_TYPE, plugin );
        daoUtil.setInt( 1, nIdParent );
        daoUtil.executeQuery( );

        int nOrder = 1;

        if ( daoUtil.next( ) )
        {
            nOrder = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nOrder;
    }

    @Override
    public void storeWithLastOrder( TicketCategory category, Plugin _plugin )
    {
        int nNewDomainOrder = newCategoryOrder( category.getIdParent( ), _plugin );
        category.setOrder( nNewDomainOrder );
        store( category, _plugin );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canRemoveCategory( int nKey, Plugin plugin )
    {
        boolean bResult = false;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_SUB_CATEGORY_BY_CATEGORY, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            if ( daoUtil.getInt( 1 ) == 0 )
            {
                bResult = true;
            }
        }

        daoUtil.free( );

        return bResult;
    }
}
