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
package fr.paris.lutece.plugins.ticketing.business.contactmode;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for ContactMode objects
 */
public final class ContactModeDAO implements IContactModeDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_contact_mode ) FROM ticketing_contact_mode";
    private static final String SQL_QUERY_SELECT = "SELECT id_contact_mode, code, required_inputs, confirmation_msg FROM ticketing_contact_mode WHERE id_contact_mode = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_contact_mode ( id_contact_mode, code, required_inputs, confirmation_msg, inactive ) VALUES ( ?, ?, ?, ?, 0 ) ";
    private static final String SQL_QUERY_DELETE = "UPDATE ticketing_contact_mode SET inactive = 1 WHERE id_contact_mode = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_contact_mode SET id_contact_mode = ?, code = ?, required_inputs = ?, confirmation_msg = ? WHERE id_contact_mode = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_contact_mode, code, required_inputs, confirmation_msg FROM ticketing_contact_mode WHERE inactive <> 1 ";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_contact_mode FROM ticketing_contact_mode WHERE inactive <> 1 ";

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
    public void insert( ContactMode contactMode, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        contactMode.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, contactMode.getId( ) );
        daoUtil.setString( 2, contactMode.getCode( ) );
        daoUtil.setString( 3, contactMode.getRequiredInputs( ) );
        daoUtil.setString( 4, contactMode.getConfirmationMsg( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ContactMode load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        ContactMode contactMode = null;

        if ( daoUtil.next( ) )
        {
            contactMode = new ContactMode( );
            contactMode.setId( daoUtil.getInt( 1 ) );
            contactMode.setCode( daoUtil.getString( 2 ) );
            contactMode.setRequiredInputs( daoUtil.getString( 3 ) );
            contactMode.setConfirmationMsg( ( daoUtil.getString( 4 ) == null ) ? StringUtils.EMPTY : daoUtil.getString( 4 ) );
        }

        daoUtil.free( );

        return contactMode;
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
    public void store( ContactMode contactMode, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, contactMode.getId( ) );
        daoUtil.setString( 2, contactMode.getCode( ) );
        daoUtil.setString( 3, contactMode.getRequiredInputs( ) );
        daoUtil.setString( 4, contactMode.getConfirmationMsg( ) );
        daoUtil.setInt( 5, contactMode.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ContactMode> selectContactModesList( Plugin plugin )
    {
        List<ContactMode> contactModeList = new ArrayList<ContactMode>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ContactMode contactMode = new ContactMode( );

            contactMode.setId( daoUtil.getInt( 1 ) );
            contactMode.setCode( daoUtil.getString( 2 ) );
            contactMode.setRequiredInputs( daoUtil.getString( 3 ) );
            contactMode.setConfirmationMsg( ( daoUtil.getString( 4 ) == null ) ? StringUtils.EMPTY : daoUtil.getString( 4 ) );

            contactModeList.add( contactMode );
        }

        daoUtil.free( );

        return contactModeList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdContactModesList( Plugin plugin )
    {
        List<Integer> contactModeList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            contactModeList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return contactModeList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectReferenceList( Plugin plugin )
    {
        ReferenceList list = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            list.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return list;
    }
}
