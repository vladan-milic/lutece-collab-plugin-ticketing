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

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for TicketCategory objects
 */
public final class TicketCategoryDAO implements ITicketCategoryDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_ticket_category ) FROM ticketing_ticket_category";
    private static final String SQL_QUERY_SELECT = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label,  a.id_workflow, b.label, c.label, c.id_ticket_type, a.category_code, a.id_unit, a.category_precision, a.help_message, a.category_order "
            + " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c "
            + " WHERE id_ticket_category = ? AND a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type ";
    private static final String SQL_QUERY_SELECT_BY_CODE = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label,  a.id_workflow, b.label, c.label, c.id_ticket_type, a.category_code, a.id_unit, a.category_precision, a.help_message, a.category_order "
            + " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c "
            + " WHERE category_code = ? AND a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type  AND a.inactive <> 1 AND  b.inactive <> 1 AND  c.inactive <> 1";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket_category ( id_ticket_category, id_ticket_domain, label, id_workflow, category_code, id_unit, inactive, category_precision, help_message, category_order ) VALUES ( ?, ?, ?, ?, ?, ?, 0, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "UPDATE ticketing_ticket_category SET inactive = 1, category_order = -1 WHERE id_ticket_category = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket_category SET id_ticket_category = ?, id_ticket_domain = ?, label = ?, id_workflow = ?, category_code = ?, id_unit = ?, category_precision = ?, help_message = ?, category_order = ? WHERE id_ticket_category = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label, a.id_workflow, b.label, c.label, c.id_ticket_type, a.category_code, a.id_unit, a.category_precision, a.help_message, a.category_order "
            + " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c "
            + " WHERE a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type  AND a.inactive <> 1 AND  b.inactive <> 1 AND  c.inactive <> 1"
            + " ORDER BY c.type_order, b.domain_order, a.category_order ASC";
    private static final String SQL_QUERY_SELECT_BY_DOMAIN = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label, a.id_workflow, b.label, c.label, c.id_ticket_type, a.category_code, a.id_unit, a.category_precision, a.help_message, a.category_order "
            + " FROM ticketing_ticket_category a, ticketing_ticket_domain b, ticketing_ticket_type c "
            + " WHERE a.id_ticket_domain = ?  AND a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type AND a.inactive <> 1 ORDER BY a.category_order ASC";
    private static final String SQL_QUERY_SELECT_BY_CATEGORY = "SELECT id_ticket_category, category_precision FROM ticketing_ticket_category WHERE id_ticket_domain = ? AND label = ?  AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket_category FROM ticketing_ticket_category AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECT_CATEGORYID_BY_ORDER = "SELECT id_ticket_category FROM ticketing_ticket_category WHERE category_order = ? AND id_ticket_domain = ? AND inactive <> 1";
    private static final String SQL_QUERY_UPDATE_CATEGORY_ORDER = "UPDATE ticketing_ticket_category SET category_order = ? WHERE id_ticket_category = ?";
    private static final String SQL_QUERY_SELECT_INPUTS_BY_CATEGORY = "SELECT id_input FROM ticketing_ticket_category_input WHERE id_ticket_category = ? ORDER BY pos";
    private static final String SQL_QUERY_INSERT_INPUT = "INSERT INTO ticketing_ticket_category_input ( id_ticket_category, id_input, pos ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE_INPUT = "DELETE FROM ticketing_ticket_category_input WHERE id_ticket_category = ? AND id_input = ?";
    private static final String SQL_QUERY_UPDATE_INPUT_POS = "UPDATE ticketing_ticket_category_input SET pos = ? WHERE id_ticket_category = ? AND id_input = ? ";
    private static final String SQL_QUERY_SELECT_MAX_INPUT_POS_FOR_CATEGORY = "SELECT MAX(pos) FROM ticketing_ticket_category_input WHERE id_ticket_category = ? ";
    private static final String SQL_QUERY_SELECT_INPUT_POS = "SELECT pos from ticketing_ticket_category_input WHERE id_ticket_category = ? AND id_input = ? ";
    private static final String SQL_QUERY_SELECT_INPUT_BY_POS = "SELECT id_input from ticketing_ticket_category_input WHERE id_ticket_category = ? AND pos = ? ";
    private static final String SQL_QUERY_SELECT_INPUT_IN_ALL_CATEGORIES = "SELECT id_ticket_category from ticketing_ticket_category_input WHERE id_input = ? ";
    private static final String SQL_QUERY_MAX_CATEGORY_ORDER = "SELECT max( category_order ) FROM ticketing_ticket_category WHERE inactive <> 1 AND id_ticket_domain = ?";
    private static final String SQL_QUERY_REBUILD_CATEGORY_ORDER_SEQUENCE = "UPDATE ticketing_ticket_category SET category_order = category_order - 1 WHERE category_order > ? AND inactive <> 1 AND id_ticket_domain = ?";

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
    public synchronized void insert( TicketCategory ticketCategory, Plugin plugin )
    {
        int nPrimaryKey = newPrimaryKey( plugin );
        int nOrder = newCategoryOrder( ticketCategory.getIdTicketDomain( ), plugin );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        ticketCategory.setId( nPrimaryKey );

        daoUtil.setInt( 1, ticketCategory.getId( ) );
        daoUtil.setInt( 2, ticketCategory.getIdTicketDomain( ) );
        daoUtil.setString( 3, ticketCategory.getLabel( ) );
        daoUtil.setInt( 4, ticketCategory.getIdWorkflow( ) );
        daoUtil.setString( 5, ticketCategory.getCode( ) );
        daoUtil.setInt( 6, ( ticketCategory.getAssigneeUnit( ) != null ) ? ticketCategory.getAssigneeUnit( ).getUnitId( ) : 0 );
        daoUtil.setString( 7, ticketCategory.getPrecision( ) );
        daoUtil.setString( 8, ticketCategory.getHelpMessage( ) );
        daoUtil.setInt( 9, nOrder );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Retrieve the last available order value for category_order within a TicketDomain
     * 
     * @param nTargetDomainId
     *            the identifier of the domain
     * @param _plugin
     * @return the next domain_order value for the given TicketType
     */
    private int newCategoryOrder( int nIdDomain, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MAX_CATEGORY_ORDER, plugin );
        daoUtil.setInt( 1, nIdDomain );
        daoUtil.executeQuery( );

        int nOrder = 1;

        if ( daoUtil.next( ) )
        {
            nOrder = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nOrder;
    }

    /**
     * Return the next available Position value for inputs linked to a category
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
    public TicketCategory load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        TicketCategory category = null;

        if ( daoUtil.next( ) )
        {
            category = dataToCategory( daoUtil );
        }

        daoUtil.free( );

        List<Integer> listIdInput = selectIdInputListByCategory( nKey, plugin );

        if ( category != null )
        {
            category.setListIdInput( listIdInput );
        }

        return category;
    }

    private TicketCategory dataToCategory( DAOUtil daoUtil )
    {
        int nIndex = 1;

        TicketCategory category = new TicketCategory( );
        category.setId( daoUtil.getInt( nIndex++ ) );
        category.setIdTicketDomain( daoUtil.getInt( nIndex++ ) );
        category.setLabel( daoUtil.getString( nIndex++ ) );
        category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
        category.setTicketDomain( daoUtil.getString( nIndex++ ) );
        category.setTicketType( daoUtil.getString( nIndex++ ) );
        category.setIdTicketType( daoUtil.getInt( nIndex++ ) );
        category.setCode( daoUtil.getString( nIndex++ ) );

        int nUnitId = daoUtil.getInt( nIndex++ );
        Unit unit = UnitHome.findByPrimaryKey( nUnitId );
        if ( unit != null )
        {
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
            category.setAssigneeUnit( assigneeUnit );
        }
        category.setPrecision( daoUtil.getString( nIndex++ ) );
        category.setHelpMessage( daoUtil.getString( nIndex++ ) );
        category.setOrder( daoUtil.getInt( nIndex++ ) );

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
            category = dataToCategory( daoUtil );
        }

        daoUtil.free( );

        return category;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> loadByDomainId( int nDomainId, Plugin plugin )
    {
        List<TicketCategory> ticketCategoryList = new ArrayList<TicketCategory>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DOMAIN, plugin );
        daoUtil.setInt( 1, nDomainId );
        daoUtil.executeQuery( );

        TicketCategory category = null;

        while ( daoUtil.next( ) )
        {
            category = dataToCategory( daoUtil );

            ticketCategoryList.add( category );
        }

        daoUtil.free( );

        return ticketCategoryList;
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
    public boolean checkIfInputIsUsedInCategories( int nIdResource, Plugin _plugin )
    {
        boolean InputIsUsedInCategories = false;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INPUT_IN_ALL_CATEGORIES, _plugin );
        daoUtil.setInt( 1, nIdResource );
        daoUtil.executeQuery( );

        InputIsUsedInCategories = daoUtil.next( );

        daoUtil.free( );

        return InputIsUsedInCategories;
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
    public void store( TicketCategory ticketCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, ticketCategory.getId( ) );
        daoUtil.setInt( 2, ticketCategory.getIdTicketDomain( ) );
        daoUtil.setString( 3, ticketCategory.getLabel( ) );
        daoUtil.setInt( 4, ticketCategory.getIdWorkflow( ) );
        daoUtil.setString( 5, ticketCategory.getCode( ) );
        daoUtil.setInt( 6, ( ticketCategory.getAssigneeUnit( ) != null ) ? ticketCategory.getAssigneeUnit( ).getUnitId( ) : 0 );
        daoUtil.setString( 7, ticketCategory.getPrecision( ) );
        daoUtil.setString( 8, ticketCategory.getHelpMessage( ) );
        daoUtil.setInt( 9, ticketCategory.getOrder( ) );
        daoUtil.setInt( 10, ticketCategory.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> selectTicketCategorysList( Plugin plugin )
    {
        List<TicketCategory> ticketCategoryList = new ArrayList<TicketCategory>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            TicketCategory category = dataToCategory( daoUtil );

            ticketCategoryList.add( category );
        }

        daoUtil.free( );

        return ticketCategoryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTicketCategorysList( Plugin plugin )
    {
        List<Integer> ticketCategoryList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ticketCategoryList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return ticketCategoryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectReferenceListByDomain( int nDomainId, Plugin plugin )
    {
        ReferenceList list = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DOMAIN, plugin );
        daoUtil.setInt( 1, nDomainId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return list;
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

    @Override
    public ReferenceList selectReferenceListByCategory( int nDomainId, String labelCategory, Plugin plugin )
    {
        ReferenceList list = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CATEGORY, plugin );
        daoUtil.setInt( 1, nDomainId );
        daoUtil.setString( 2, labelCategory );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return list;
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

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateCategoryOrder( int nIdCategory, int nNewPosition, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_CATEGORY_ORDER, plugin );
        daoUtil.setInt( 1, nNewPosition );
        daoUtil.setInt( 2, nIdCategory );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void rebuildCategoryOrders( int nfromOrder, int nDomainId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REBUILD_CATEGORY_ORDER_SEQUENCE, plugin );
        daoUtil.setInt( 1, nfromOrder );
        daoUtil.setInt( 2, nDomainId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int selectCategoryIdByOrder( int nOrder, int nDomainId, Plugin plugin )
    {
        int ticketCategoryId = -1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CATEGORYID_BY_ORDER, plugin );
        daoUtil.setInt( 1, nOrder );
        daoUtil.setInt( 2, nDomainId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ticketCategoryId = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return ticketCategoryId;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void storeWithLastOrder( TicketCategory ticketCategory, Plugin _plugin )
    {
        int nNewCategoryOrder = newCategoryOrder( ticketCategory.getIdTicketDomain( ), _plugin );
        ticketCategory.setOrder( nNewCategoryOrder );
        store( ticketCategory, _plugin );

    }

}
