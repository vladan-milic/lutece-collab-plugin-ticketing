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
package fr.paris.lutece.plugins.ticketing.business.domain;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for TicketDomain objects
 */
public final class TicketDomainDAO implements ITicketDomainDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_ticket_domain ) FROM ticketing_ticket_domain";
    private static final String SQL_QUERY_SELECT = "SELECT a.id_ticket_domain, a.id_ticket_type, a.label, b.label FROM ticketing_ticket_domain a, ticketing_ticket_type b " +
        " WHERE a.id_ticket_domain = ? AND a.id_ticket_type = b.id_ticket_type ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket_domain ( id_ticket_domain, id_ticket_type, label, inactive ) VALUES ( ?, ?, ?, 0 ) ";
    private static final String SQL_QUERY_DELETE = "UPDATE ticketing_ticket_domain SET inactive = 1 WHERE id_ticket_domain = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket_domain SET id_ticket_domain = ?, id_ticket_type = ?, label = ? WHERE id_ticket_domain = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_ticket_domain, a.id_ticket_type, a.label, b.label FROM ticketing_ticket_domain a, ticketing_ticket_type b " +
        " WHERE a.id_ticket_type = b.id_ticket_type  AND a.inactive <> 1 AND b.inactive <> 1 ";
    private static final String SQL_QUERY_SELECTALL_SIMPLE = "SELECT a.id_ticket_domain, a.label FROM ticketing_ticket_domain a  WHERE a.inactive <> 1";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket_domain FROM ticketing_ticket_domain  AND inactive <> 1 ";
    private static final String SQL_QUERY_SELECT_BY_TYPE = "SELECT id_ticket_domain , label FROM ticketing_ticket_domain WHERE id_ticket_type = ?  AND inactive <> 1 ORDER BY label ASC";
    private static final String SQL_QUERY_COUNT_CATEGORY_BY_DOMAIN = "SELECT COUNT(1) FROM ticketing_ticket_category WHERE id_ticket_domain = ? AND inactive <> 1 ";

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
    public void insert( TicketDomain ticketDomain, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        ticketDomain.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, ticketDomain.getId(  ) );
        daoUtil.setInt( 2, ticketDomain.getIdTicketType(  ) );
        daoUtil.setString( 3, ticketDomain.getLabel(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TicketDomain load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        TicketDomain ticketDomain = null;

        if ( daoUtil.next(  ) )
        {
            ticketDomain = new TicketDomain(  );
            ticketDomain.setId( daoUtil.getInt( 1 ) );
            ticketDomain.setIdTicketType( daoUtil.getInt( 2 ) );
            ticketDomain.setLabel( daoUtil.getString( 3 ) );
            ticketDomain.setTicketType( daoUtil.getString( 4 ) );
        }

        daoUtil.free(  );

        return ticketDomain;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canRemoveDomain( int nKey, Plugin plugin )
    {
        boolean bResult = false;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_CATEGORY_BY_DOMAIN, plugin );
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
    public void store( TicketDomain ticketDomain, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, ticketDomain.getId(  ) );
        daoUtil.setInt( 2, ticketDomain.getIdTicketType(  ) );
        daoUtil.setString( 3, ticketDomain.getLabel(  ) );
        daoUtil.setInt( 4, ticketDomain.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketDomain> selectTicketDomainsList( Plugin plugin )
    {
        List<TicketDomain> ticketDomainList = new ArrayList<TicketDomain>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            TicketDomain ticketDomain = new TicketDomain(  );

            ticketDomain.setId( daoUtil.getInt( 1 ) );
            ticketDomain.setIdTicketType( daoUtil.getInt( 2 ) );
            ticketDomain.setLabel( daoUtil.getString( 3 ) );
            ticketDomain.setTicketType( daoUtil.getString( 4 ) );

            ticketDomainList.add( ticketDomain );
        }

        daoUtil.free(  );

        return ticketDomainList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTicketDomainsList( Plugin plugin )
    {
        List<Integer> ticketDomainList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketDomainList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return ticketDomainList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectReferenceListByType( int nTicketTypeId, Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_TYPE, plugin );
        daoUtil.setInt( 1, nTicketTypeId );
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
    public ReferenceList selectReferenceList( Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 4 ) + " / " + daoUtil.getString( 3 ) );
        }

        daoUtil.free(  );

        return list;
    }

    /**
    * {@inheritDoc }
    */
    @Override
    public ReferenceList selectReferenceListSimple( Plugin plugin )
    {
        ReferenceList list = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_SIMPLE, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return list;
    }
}
