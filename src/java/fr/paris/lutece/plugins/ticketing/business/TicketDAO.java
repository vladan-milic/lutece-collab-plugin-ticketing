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
package fr.paris.lutece.plugins.ticketing.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Ticket objects
 */
public final class TicketDAO implements ITicketDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_ticket ) FROM ticketing_ticket";
    private static final String SQL_QUERY_SELECT = "SELECT a.id_ticket, a.id_user_title, b.label, a.firstname, a.lastname, a.email, a.phone_number, c.id_ticket_type, c.label, d.id_ticket_domain, d.label, a.id_ticket_category, e.label, a.ticket_comment, a.ticket_status, a.ticket_status_text " +
        " FROM ticketing_ticket a, ticketing_user_title b, ticketing_ticket_type c, ticketing_ticket_domain d, ticketing_ticket_category e " +
        " WHERE a.id_ticket = ? AND a.id_user_title = b.id_user_title AND a.id_ticket_category = e.id_ticket_category AND e.id_ticket_domain = d.id_ticket_domain AND d.id_ticket_type = c.id_ticket_type";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket ( id_ticket, id_user_title, firstname, lastname, email, phone_number, id_ticket_category, ticket_comment, ticket_status, ticket_status_text ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_ticket WHERE id_ticket = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket SET id_ticket = ?, id_user_title = ?, firstname = ?, lastname = ?, email = ?, phone_number = ?, id_ticket_category = ?, ticket_comment = ?, ticket_status = ?, ticket_status_text = ? WHERE id_ticket = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_ticket, a.id_user_title, b.label, a.firstname, a.lastname, a.email, a.phone_number, c.id_ticket_type, c.label, d.id_ticket_domain, d.label, a.id_ticket_category, e.label, a.ticket_comment, a.ticket_status, a.ticket_status_text " +
        " FROM ticketing_ticket a, ticketing_user_title b, ticketing_ticket_type c, ticketing_ticket_domain d, ticketing_ticket_category e " +
        " WHERE a.id_user_title = b.id_user_title AND a.id_ticket_category = e.id_ticket_category AND e.id_ticket_domain = d.id_ticket_domain AND d.id_ticket_type = c.id_ticket_type";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket FROM ticketing_ticket";

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
    public void insert( Ticket ticket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        ticket.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, ticket.getId(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdUserTitle(  ) );
        daoUtil.setString( nIndex++, ticket.getFirstname(  ) );
        daoUtil.setString( nIndex++, ticket.getLastname(  ) );
        daoUtil.setString( nIndex++, ticket.getEmail(  ) );
        daoUtil.setString( nIndex++, ticket.getPhoneNumber(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdTicketCategory(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketComment(  ) );
        daoUtil.setInt( nIndex++, ticket.getTicketStatus(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketStatusText(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Ticket load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        Ticket ticket = null;

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            ticket = new Ticket(  );
            ticket.setId( daoUtil.getInt( nIndex++ ) );
            ticket.setIdUserTitle( daoUtil.getInt( nIndex++ ) );
            ticket.setUserTitle( daoUtil.getString( nIndex++ ) );
            ticket.setFirstname( daoUtil.getString( nIndex++ ) );
            ticket.setLastname( daoUtil.getString( nIndex++ ) );
            ticket.setEmail( daoUtil.getString( nIndex++ ) );
            ticket.setPhoneNumber( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketType( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketType( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketDomain( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketDomain( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketCategory( daoUtil.getString( nIndex++ ) );
            ticket.setTicketComment( daoUtil.getString( nIndex++ ) );
            ticket.setTicketStatus( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketStatusText( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free(  );

        return ticket;
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
    public void store( Ticket ticket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, ticket.getId(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdUserTitle(  ) );
        daoUtil.setString( nIndex++, ticket.getFirstname(  ) );
        daoUtil.setString( nIndex++, ticket.getLastname(  ) );
        daoUtil.setString( nIndex++, ticket.getEmail(  ) );
        daoUtil.setString( nIndex++, ticket.getPhoneNumber(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdTicketCategory(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketComment(  ) );
        daoUtil.setInt( nIndex++, ticket.getTicketStatus(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketStatusText(  ) );
        daoUtil.setInt( nIndex++, ticket.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Ticket> selectTicketsList( Plugin plugin )
    {
        List<Ticket> ticketList = new ArrayList<Ticket>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Ticket ticket = new Ticket(  );

            ticket.setId( daoUtil.getInt( 1 ) );
            ticket.setIdUserTitle( daoUtil.getInt( 2 ) );
            ticket.setUserTitle( daoUtil.getString( 3 ) );
            ticket.setFirstname( daoUtil.getString( 4 ) );
            ticket.setLastname( daoUtil.getString( 5 ) );
            ticket.setEmail( daoUtil.getString( 6 ) );
            ticket.setPhoneNumber( daoUtil.getString( 7 ) );
            ticket.setIdTicketType( daoUtil.getInt( 8 ) );
            ticket.setTicketType( daoUtil.getString( 9 ) );
            ticket.setIdTicketDomain( daoUtil.getInt( 10 ) );
            ticket.setTicketDomain( daoUtil.getString( 11 ) );
            ticket.setIdTicketCategory( daoUtil.getInt( 12 ) );
            ticket.setTicketCategory( daoUtil.getString( 13 ) );
            ticket.setTicketComment( daoUtil.getString( 14 ) );
            ticket.setTicketStatus( daoUtil.getInt( 15 ) );
            ticket.setTicketStatusText( daoUtil.getString( 16 ) );

            ticketList.add( ticket );
        }

        daoUtil.free(  );

        return ticketList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTicketsList( Plugin plugin )
    {
        List<Integer> ticketList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return ticketList;
    }
}
