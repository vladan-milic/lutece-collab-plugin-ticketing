/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
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
package fr.paris.lutece.plugins.ticketing.business.ticketform;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for TicketForm objects
 */
public final class TicketFormDAO implements ITicketFormDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_form ) FROM ticketing_ticket_form";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT form.id_form, form.title, form.description, cat.id_ticket_category, cat.label FROM ticketing_ticket_form AS form " +
        " LEFT JOIN ticketing_ticket_category AS cat ON cat.id_ticket_form = form.id_form ";
    private static final String SQL_QUERY_SELECTALL = SQL_QUERY_SELECT_COLUMNS + " ORDER BY title";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_WITHOUT_CATEGORY = SQL_QUERY_SELECT_COLUMNS +
        " WHERE form.id_form NOT IN  (SELECT id_ticket_form from ticketing_ticket_category where id_ticket_form > 0)";
    private static final String SQL_QUERY_SELECT_BY_CATEGORY = SQL_QUERY_SELECT_COLUMNS +
        " WHERE cat.id_ticket_category = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket_form ( id_form, title, description) VALUES (?, ?, ?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_ticket_form WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket_form SET title = ?, description = ? WHERE id_form = ?";

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
    public void insert( TicketForm ticketForm, Plugin plugin )
    {
        ticketForm.setIdForm( newPrimaryKey( plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, ticketForm.getIdForm(  ) );
        daoUtil.setString( nIndex++, ticketForm.getTitle(  ) );
        daoUtil.setString( nIndex++, ticketForm.getDescription(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketForm load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        TicketForm ticketForm = null;

        if ( daoUtil.next(  ) )
        {
            ticketForm = getTicketFormData( daoUtil );
        }

        daoUtil.free(  );

        return ticketForm;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nTicketFormId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nTicketFormId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( TicketForm ticketForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = 1;

        daoUtil.setString( nIndex++, ticketForm.getTitle(  ) );
        daoUtil.setString( nIndex++, ticketForm.getDescription(  ) );
        daoUtil.setInt( nIndex++, ticketForm.getIdForm(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketForm> selectTicketFormsList( Plugin plugin )
    {
        List<TicketForm> ticketFormList = new ArrayList<TicketForm>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketFormList.add( getTicketFormData( daoUtil ) );
        }

        daoUtil.free(  );

        return ticketFormList;
    }

    /**
     * Get data of an ticket form from a daoUtil
     *
     * @param daoUtil
     *            The daoUtil to get data from
     * @return The ticket form with data of the current row of the daoUtil
     */
    private TicketForm getTicketFormData( DAOUtil daoUtil )
    {
        TicketForm ticketForm = new TicketForm(  );

        int nIndex = 1;
        ticketForm.setIdForm( daoUtil.getInt( nIndex++ ) );
        ticketForm.setTitle( daoUtil.getString( nIndex++ ) );
        ticketForm.setDescription( daoUtil.getString( nIndex++ ) );
        ticketForm.setIdCategory( daoUtil.getInt( nIndex++ ) );

        String strCat = daoUtil.getString( nIndex++ );
        ticketForm.setTicketCategory( ( strCat == null ) ? org.apache.commons.lang.StringUtils.EMPTY : strCat );

        return ticketForm;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketForm loadFormCategoryId( int nCategoryId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CATEGORY, plugin );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery(  );

        TicketForm ticketForm = null;

        if ( daoUtil.next(  ) )
        {
            ticketForm = getTicketFormData( daoUtil );
        }

        daoUtil.free(  );

        return ticketForm;
    }

    /**
     * {@inheritDoc}
     */
    public List<TicketForm> getAvailableTicketForms( Plugin plugin )
    {
        List<TicketForm> ticketFormList = new ArrayList<TicketForm>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WITHOUT_CATEGORY, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketFormList.add( getTicketFormData( daoUtil ) );
        }

        daoUtil.free(  );

        return ticketFormList;
    }
}
