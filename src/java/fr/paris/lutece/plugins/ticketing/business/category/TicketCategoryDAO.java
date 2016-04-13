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
    private static final String SQL_QUERY_SELECT = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label,  a.id_workflow, b.label, c.label, a.id_ticket_form, c.id_ticket_type, a.category_code, a.id_unit " +
        " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c " +
        " WHERE id_ticket_category = ? AND a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type ";
    private static final String SQL_QUERY_SELECT_BY_CODE = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label,  a.id_workflow, b.label, c.label, a.id_ticket_form, c.id_ticket_type, a.category_code, a.id_unit " +
        " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c " +
        " WHERE category_code = ? AND a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type  AND a.inactive <> 1 AND  b.inactive <> 1 AND  c.inactive <> 1";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket_category ( id_ticket_category, id_ticket_domain, label, id_workflow, id_ticket_form, category_code, id_unit, inactive ) VALUES ( ?, ?, ?, ?, ?, ?, ?, 0 ) ";
    private static final String SQL_QUERY_DELETE = "UPDATE ticketing_ticket_category SET inactive = 1 WHERE id_ticket_category = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket_category SET id_ticket_category = ?, id_ticket_domain = ?, label = ?, id_workflow = ?, id_ticket_form = ?, category_code = ?, id_unit = ? WHERE id_ticket_category = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label, a.id_workflow, b.label, c.label, c.id_ticket_type, a.category_code, a.id_unit " +
        " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c " +
        " WHERE a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type  AND a.inactive <> 1 AND  b.inactive <> 1 AND  c.inactive <> 1";
    private static final String SQL_QUERY_SELECT_BY_DOMAIN = "SELECT id_ticket_category, label FROM ticketing_ticket_category WHERE id_ticket_domain = ?  AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket_category FROM ticketing_ticket_category AND inactive <> 1 ";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( TicketCategory ticketCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        ticketCategory.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, ticketCategory.getId(  ) );
        daoUtil.setInt( 2, ticketCategory.getIdTicketDomain(  ) );
        daoUtil.setString( 3, ticketCategory.getLabel(  ) );
        daoUtil.setInt( 4, ticketCategory.getIdWorkflow(  ) );
        daoUtil.setInt( 5, ticketCategory.getIdTicketForm(  ) );
        daoUtil.setString( 6, ticketCategory.getCode(  ) );
        daoUtil.setInt( 7,
            ( ticketCategory.getAssigneeUnit(  ) != null ) ? ticketCategory.getAssigneeUnit(  ).getUnitId(  ) : 0 );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketCategory load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        TicketCategory category = null;

        int nIndex = 1;

        if ( daoUtil.next(  ) )
        {
            category = new TicketCategory(  );
            category.setId( daoUtil.getInt( nIndex++ ) );
            category.setIdTicketDomain( daoUtil.getInt( nIndex++ ) );
            category.setLabel( daoUtil.getString( nIndex++ ) );
            category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
            category.setTicketDomain( daoUtil.getString( nIndex++ ) );
            category.setTicketType( daoUtil.getString( nIndex++ ) );
            category.setIdTicketForm( daoUtil.getInt( nIndex++ ) );
            category.setIdTicketType( daoUtil.getInt( nIndex++ ) );
            category.setCode( daoUtil.getString( nIndex++ ) );

            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
            category.setAssigneeUnit( assigneeUnit );
        }

        daoUtil.free(  );

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
        daoUtil.executeQuery(  );

        TicketCategory category = null;

        int nIndex = 1;

        if ( daoUtil.next(  ) )
        {
            category = new TicketCategory(  );
            category.setId( daoUtil.getInt( nIndex++ ) );
            category.setIdTicketDomain( daoUtil.getInt( nIndex++ ) );
            category.setLabel( daoUtil.getString( nIndex++ ) );
            category.setIdWorkflow( daoUtil.getInt( nIndex++ ) );
            category.setTicketDomain( daoUtil.getString( nIndex++ ) );
            category.setTicketType( daoUtil.getString( nIndex++ ) );
            category.setIdTicketForm( daoUtil.getInt( nIndex++ ) );
            category.setIdTicketType( daoUtil.getInt( nIndex++ ) );
            category.setCode( daoUtil.getString( nIndex++ ) );

            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
            category.setAssigneeUnit( assigneeUnit );
        }

        daoUtil.free(  );

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
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( TicketCategory ticketCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, ticketCategory.getId(  ) );
        daoUtil.setInt( 2, ticketCategory.getIdTicketDomain(  ) );
        daoUtil.setString( 3, ticketCategory.getLabel(  ) );
        daoUtil.setInt( 4, ticketCategory.getIdWorkflow(  ) );
        daoUtil.setInt( 5, ticketCategory.getIdTicketForm(  ) );
        daoUtil.setString( 6, ticketCategory.getCode(  ) );
        daoUtil.setInt( 7,
            ( ticketCategory.getAssigneeUnit(  ) != null ) ? ticketCategory.getAssigneeUnit(  ).getUnitId(  ) : 0 );
        daoUtil.setInt( 8, ticketCategory.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> selectTicketCategorysList( Plugin plugin )
    {
        List<TicketCategory> ticketCategoryList = new ArrayList<TicketCategory>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            TicketCategory category = new TicketCategory(  );

            int nIndex = 1;
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
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
            category.setAssigneeUnit( assigneeUnit );

            ticketCategoryList.add( category );
        }

        daoUtil.free(  );

        return ticketCategoryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTicketCategorysList( Plugin plugin )
    {
        List<Integer> ticketCategoryList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketCategoryList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return ticketCategoryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectReferenceListByDomain( int nDomainId, Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DOMAIN, plugin );
        daoUtil.setInt( 1, nDomainId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return list;
    }
}
