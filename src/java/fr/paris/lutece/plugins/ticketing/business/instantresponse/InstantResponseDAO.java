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
package fr.paris.lutece.plugins.ticketing.business.instantresponse;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for InstantResponse objects
 */
public final class InstantResponseDAO implements IInstantResponseDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_instant_response ) FROM ticketing_instant_response";
    private static final String SQL_QUERY_SELECT = "SELECT r.id_instant_response, r.id_ticket_category, c.label, c.category_precision, r.subject, r.id_admin_user, r.id_channel FROM ticketing_instant_response r JOIN ticketing_ticket_category c ON r.id_ticket_category = c.id_ticket_category WHERE id_instant_response = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_instant_response ( id_instant_response, id_ticket_category, subject, id_admin_user, id_unit, date_create, id_channel ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_instant_response WHERE id_instant_response = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_instant_response SET id_instant_response = ?, id_ticket_category = ?, subject = ?, id_admin_user = ?, id_unit = ? WHERE id_instant_response = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_instant_response, a.id_ticket_category, b.label, b.category_precision, c.label, d.label, a.subject, a.date_create, a.id_admin_user , e.first_name , e.last_name, a.id_unit, f.label "
            + " FROM ticketing_instant_response a, ticketing_ticket_category b, ticketing_ticket_domain c , ticketing_ticket_type d, core_admin_user e, unittree_unit f "
            + " WHERE a.id_ticket_category = b.id_ticket_category AND b.id_ticket_domain = c.id_ticket_domain AND c.id_ticket_type = d.id_ticket_type AND a.id_admin_user = e.id_user AND a.id_unit = f.id_unit";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_instant_response FROM ticketing_instant_response";

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
    public synchronized void insert( InstantResponse instantResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        instantResponse.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, instantResponse.getId( ) );

        daoUtil.setInt( nIndex++, instantResponse.getIdTicketCategory( ) );
        daoUtil.setString( nIndex++, instantResponse.getSubject( ) );
        daoUtil.setInt( nIndex++, instantResponse.getIdAdminUser( ) );
        daoUtil.setInt( nIndex++, instantResponse.getIdUnit( ) );
        daoUtil.setTimestamp( nIndex++, instantResponse.getDateCreate( ) );
        daoUtil.setInt( nIndex++, instantResponse.getIdChannel( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InstantResponse load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        InstantResponse instantResponse = null;

        if ( daoUtil.next( ) )
        {
            instantResponse = new InstantResponse( );

            int nIndex = 1;
            instantResponse.setId( daoUtil.getInt( nIndex++ ) );

            instantResponse.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            instantResponse.setCategory( daoUtil.getString( nIndex++ ) );
            instantResponse.setTicketCategoryPrecision( daoUtil.getString( nIndex++ ) );

            instantResponse.setSubject( daoUtil.getString( nIndex++ ) );
            instantResponse.setIdAdminUser( daoUtil.getInt( nIndex++ ) );
            instantResponse.setIdChannel( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );

        return instantResponse;
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
    public void store( InstantResponse instantResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, instantResponse.getId( ) );
        daoUtil.setInt( nIndex++, instantResponse.getIdTicketCategory( ) );
        daoUtil.setString( nIndex++, instantResponse.getSubject( ) );
        daoUtil.setInt( nIndex++, instantResponse.getIdAdminUser( ) );
        daoUtil.setInt( nIndex++, instantResponse.getIdUnit( ) );
        daoUtil.setInt( nIndex, instantResponse.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<InstantResponse> selectInstantResponsesList( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        List<InstantResponse> instantResponseList = getInstantResponsesFromQuery( daoUtil );
        daoUtil.free( );

        return instantResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdInstantResponsesList( Plugin plugin )
    {
        List<Integer> instantResponseList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            instantResponseList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return instantResponseList;
    }

    /**
     * returns instantResponseList
     * 
     * @param daoUtil
     *            daoUtil which has executed query and still contain element
     * @return list of instant responses
     */
    private List<InstantResponse> getInstantResponsesFromQuery( DAOUtil daoUtil )
    {
        List<InstantResponse> instantResponseList = new ArrayList<InstantResponse>( );

        if ( daoUtil != null )
        {
            while ( daoUtil.next( ) )
            {
                instantResponseList.add( dataToInstantResponse( daoUtil ) );
            }
        }

        return instantResponseList;
    }

    /**
     * Create an InstantResponse object from the daoUtil data
     * 
     * @param daoUtil
     *            The daoUtil
     * @return the InstantResponse object created from the daoUtil
     */
    private InstantResponse dataToInstantResponse( DAOUtil daoUtil )
    {
        InstantResponse instantResponse = new InstantResponse( );

        int nIndex = 1;
        instantResponse.setId( daoUtil.getInt( nIndex++ ) );

        instantResponse.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
        instantResponse.setCategory( daoUtil.getString( nIndex++ ) );
        instantResponse.setTicketCategoryPrecision( daoUtil.getString( nIndex++ ) );

        instantResponse.setDomain( daoUtil.getString( nIndex++ ) );
        instantResponse.setType( daoUtil.getString( nIndex++ ) );
        instantResponse.setSubject( daoUtil.getString( nIndex++ ) );
        instantResponse.setDateCreate( daoUtil.getTimestamp( nIndex++ ) );
        instantResponse.setIdAdminUser( daoUtil.getInt( nIndex++ ) );
        instantResponse.setUserFirstname( daoUtil.getString( nIndex++ ) );
        instantResponse.setUserLastname( daoUtil.getString( nIndex++ ) );
        instantResponse.setIdUnit( daoUtil.getInt( nIndex++ ) );
        instantResponse.setUnit( daoUtil.getString( nIndex++ ) );

        return instantResponse;
    }
}
