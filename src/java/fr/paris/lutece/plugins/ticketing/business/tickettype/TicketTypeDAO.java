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
package fr.paris.lutece.plugins.ticketing.business.tickettype;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for TicketType objects
 */
public final class TicketTypeDAO implements ITicketTypeDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_ticket_type ) FROM ticketing_ticket_type";
    private static final String SQL_QUERY_SELECT = "SELECT id_ticket_type, label, reference_prefix FROM ticketing_ticket_type WHERE id_ticket_type = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket_type ( id_ticket_type, label, reference_prefix, inactive ) VALUES ( ?, ?, ?, 0 ) ";
    private static final String SQL_QUERY_DELETE = "UPDATE ticketing_ticket_type SET inactive = 1 WHERE id_ticket_type = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket_type SET id_ticket_type = ?, label = ?, reference_prefix = ? WHERE id_ticket_type = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_ticket_type, label, reference_prefix FROM ticketing_ticket_type WHERE inactive <> 1 ";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket_type FROM ticketing_ticket_type WHERE inactive <> 1 ";
    private static final String SQL_QUERY_COUNT_DOMAIN_BY_TYPE = "SELECT COUNT(1) FROM ticketing_ticket_domain WHERE id_ticket_type = ? AND inactive <> 1 ";

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
    public void insert( TicketType ticketType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        ticketType.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, ticketType.getId(  ) );
        daoUtil.setString( 2, ticketType.getLabel(  ) );
        daoUtil.setString( 3, ticketType.getReferencePrefix(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketType load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        TicketType ticketType = null;

        if ( daoUtil.next(  ) )
        {
            ticketType = new TicketType(  );
            ticketType.setId( daoUtil.getInt( 1 ) );
            ticketType.setLabel( daoUtil.getString( 2 ) );
            ticketType.setReferencePrefix( daoUtil.getString( 3 ) );
        }

        daoUtil.free(  );

        return ticketType;
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
    public void store( TicketType ticketType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, ticketType.getId(  ) );
        daoUtil.setString( 2, ticketType.getLabel(  ) );
        daoUtil.setString( 3, ticketType.getReferencePrefix(  ) );
        daoUtil.setInt( 4, ticketType.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketType> selectTicketTypesList( Plugin plugin )
    {
        List<TicketType> ticketTypeList = new ArrayList<TicketType>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            TicketType ticketType = new TicketType(  );

            ticketType.setId( daoUtil.getInt( 1 ) );
            ticketType.setLabel( daoUtil.getString( 2 ) );
            ticketType.setReferencePrefix( daoUtil.getString( 3 ) );

            ticketTypeList.add( ticketType );
        }

        daoUtil.free(  );

        return ticketTypeList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTicketTypesList( Plugin plugin )
    {
        List<Integer> ticketTypeList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketTypeList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return ticketTypeList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectReferenceList( Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
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
    public boolean canRemoveType( int nKey, Plugin plugin )
    {
        boolean bResult = false;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_DOMAIN_BY_TYPE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            if ( daoUtil.getInt( 1 ) == 0 )
            {
                bResult = true;
            }
        }

        daoUtil.free(  );

        return bResult;
    }
}