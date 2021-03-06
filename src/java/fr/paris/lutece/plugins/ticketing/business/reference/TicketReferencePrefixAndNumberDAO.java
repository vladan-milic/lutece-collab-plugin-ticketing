/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.business.reference;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class accesses a ticket reference in the following format: {prefix}{sequence}
 *
 */
public class TicketReferencePrefixAndNumberDAO implements ITicketReferenceDAO
{
    // SQL QUERIES
    private static final String SQL_QUERY_SELECT_LAST_TICKET_REFERENCE = " SELECT max( substring( ticket_reference, ? ) ) FROM ticketing_ticket WHERE ticket_reference LIKE ? ";
    private static final String SQL_QUERY_SELECT_ALL_PREFIX_REFERENCE = " SELECT reference_prefix FROM ticketing_ticket_type";
    private static final String SQL_QUERY_SELECT_ID_TICKET_BY_REFERENCE = " SELECT id_ticket FROM ticketing_ticket WHERE ticket_reference = ?";
    private static final String SQL_LIKE_WILDCARD = "%";

    /**
     * {@inheritDoc}
     */
    @Override
    public String findLastTicketReference( String strPrefix )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LAST_TICKET_REFERENCE, PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME ) );
        daoUtil.setInt( 1, strPrefix.length( ) + 1 );
        daoUtil.setString( 2, strPrefix + SQL_LIKE_WILDCARD );
        daoUtil.executeQuery( );

        String lastTicketReference = null;

        if ( daoUtil.next( ) )
        {
            lastTicketReference = daoUtil.getString( 1 );
        }

        daoUtil.free( );

        return lastTicketReference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findAllPrefixReference( )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_PREFIX_REFERENCE, PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME ) );
        daoUtil.executeQuery( );

        List<String> listPrefixReference = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            listPrefixReference.add( daoUtil.getString( 1 ) );
        }

        daoUtil.free( );

        return listPrefixReference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer findIdTicketByReference( String strReference )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_TICKET_BY_REFERENCE, PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME ) );
        daoUtil.setString( 1, strReference );
        daoUtil.executeQuery( );

        Integer nIdTicket = null;

        if ( daoUtil.next( ) )
        {
            nIdTicket = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nIdTicket;
    }
}
