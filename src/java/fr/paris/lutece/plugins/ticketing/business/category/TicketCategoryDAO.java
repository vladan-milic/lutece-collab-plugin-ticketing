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
    private static final String SQL_QUERY_SELECT = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label,  a.id_workflow, b.label, c.label, a.id_ticket_form, c.id_ticket_type, a.category_code, a.id_unit, a.category_precision, a.help_message " +
        " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c " +
        " WHERE id_ticket_category = ? AND a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type ";
    private static final String SQL_QUERY_SELECT_BY_CODE = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label,  a.id_workflow, b.label, c.label, a.id_ticket_form, c.id_ticket_type, a.category_code, a.id_unit, a.category_precision, a.help_message " +
        " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c " +
        " WHERE category_code = ? AND a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type  AND a.inactive <> 1 AND  b.inactive <> 1 AND  c.inactive <> 1";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket_category ( id_ticket_category, id_ticket_domain, label, id_workflow, id_ticket_form, category_code, id_unit, inactive, category_precision, help_message ) VALUES ( ?, ?, ?, ?, ?, ?, ?, 0, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "UPDATE ticketing_ticket_category SET inactive = 1 WHERE id_ticket_category = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket_category SET id_ticket_category = ?, id_ticket_domain = ?, label = ?, id_workflow = ?, id_ticket_form = ?, category_code = ?, id_unit = ?, category_precision = ?, help_message = ? WHERE id_ticket_category = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_ticket_category, a.id_ticket_domain, a.label, a.id_workflow, b.label, c.label, c.id_ticket_type, a.category_code, a.id_unit, a.category_precision, a.help_message " +
        " FROM ticketing_ticket_category a, ticketing_ticket_domain b , ticketing_ticket_type c " +
        " WHERE a.id_ticket_domain = b.id_ticket_domain AND b.id_ticket_type = c.id_ticket_type  AND a.inactive <> 1 AND  b.inactive <> 1 AND  c.inactive <> 1";
    private static final String SQL_QUERY_SELECT_BY_DOMAIN = "SELECT id_ticket_category, label FROM ticketing_ticket_category WHERE id_ticket_domain = ?  AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECT_BY_CATEGORY = "SELECT id_ticket_category, category_precision FROM ticketing_ticket_category WHERE id_ticket_domain = ? AND label = ?  AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket_category FROM ticketing_ticket_category AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECT_INPUTS_BY_CATEGORY = "SELECT id_input FROM ticketing_ticket_category_input WHERE id_ticket_category = ? ORDER BY pos";
    private static final String SQL_QUERY_INSERT_INPUT = "INSERT INTO ticketing_ticket_category_input ( id_ticket_category, id_input, pos ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE_INPUT = "DELETE FROM ticketing_ticket_category_input WHERE id_ticket_category = ? AND id_input = ?";
    private static final String SQL_QUERY_UPDATE_INPUT_POS = "UPDATE ticketing_ticket_category_input SET pos = ? WHERE id_ticket_category = ? AND id_input = ? ";
    private static final String SQL_QUERY_SELECT_MAX_INPUT_POS_FOR_CATEGORY = "SELECT MAX(pos) FROM ticketing_ticket_category_input WHERE id_ticket_category = ? ";
	private static final String SQL_QUERY_SELECT_INPUT_POS = "SELECT pos from ticketing_ticket_category_input WHERE id_ticket_category = ? AND id_input = ? ";
	private static final String SQL_QUERY_SELECT_INPUT_BY_POS = "SELECT id_input from ticketing_ticket_category_input WHERE id_ticket_category = ? AND pos = ? ";
    private static final String SQL_QUERY_SELECT_HELP_MESSAGE = "SELECT help_message FROM ticketing_ticket_category WHERE id_ticket_category = ? AND help_message IS NOT NULL AND help_message != '' AND inactive <> 1 ";


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
        daoUtil.setString( 8, ticketCategory.getPrecision(  ) );
        daoUtil.setString( 9, ticketCategory.getHelpMessage(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Return the next available Position value for inputs linked to a category
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int getNextPositionForCategoryInputs( int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_INPUT_POS_FOR_CATEGORY, plugin );
        daoUtil.setInt( 1,  nIdCategory );
        daoUtil.executeQuery(  );

        int nPos = 1;

        if ( daoUtil.next(  ) )
        {
        	nPos = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

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

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
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
            category.setPrecision( daoUtil.getString( nIndex++ ) );
            category.setHelpMessage( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free(  );
        
        List<Integer> listIdInput = selectIdInputListByCategory( nKey, plugin );

        if ( category != null )
        {
            category.setListIdInput( listIdInput );
        }

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
            category.setPrecision( daoUtil.getString( nIndex++ ) );
            category.setHelpMessage( daoUtil.getString( nIndex++ ) );
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
    public void deleteLinkCategoryInput( int nIdCategory, int nIdInput, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_INPUT, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.setInt( 2, nIdInput );
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
        daoUtil.setString( 8, ticketCategory.getPrecision(  ) );
        daoUtil.setString( 9, ticketCategory.getHelpMessage(  ) );
        daoUtil.setInt( 10, ticketCategory.getId(  ) );

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
            category.setPrecision( daoUtil.getString( nIndex++ ) );
            category.setHelpMessage( daoUtil.getString( nIndex++ ) );
            
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
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
        	nPosition = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );
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
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
        	nIdInput = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );
        return nIdInput;
	}
	
    @Override
    public ReferenceList selectReferenceListByCategory( int nDomainId, String labelCategory, Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CATEGORY, plugin );
        daoUtil.setInt( 1, nDomainId );
        daoUtil.setString( 2, labelCategory );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return list;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String selectHelpMessageByCategory( int nCategoryId, Plugin plugin )
    {
    	String help_message = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_HELP_MESSAGE, plugin );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery(  );
        if(daoUtil.next()){
        	help_message = daoUtil.getString( 1 );
        }
        
        daoUtil.free(  ); 

        return help_message;
    }
    
	/**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdInputListByCategory( int nCategoryId, Plugin plugin )
    {
        List<Integer> ticketInputList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INPUTS_BY_CATEGORY, plugin );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketInputList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return ticketInputList;
    }    
}