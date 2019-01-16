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

package fr.paris.lutece.plugins.ticketing.business.form;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 */
public final class FormDAO implements IFormDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_form ) FROM ticketing_form";
    private static final String SQL_QUERY_SELECT = "SELECT id_form, title, message, button_label, connection FROM ticketing_form WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_form ( id_form, title, message, button_label, connection ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_form WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_form SET id_form = ?, title = ?, message = ?, button_label = ?, connection = ? WHERE id_form = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_form, title, message, button_label, connection FROM ticketing_form";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_form FROM ticketing_form";

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
    public void insert( Form form, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        form.setId( newPrimaryKey( plugin ) );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, form.getId( ) );
        daoUtil.setString( nIndex++, form.getTitle( ) );
        daoUtil.setString( nIndex++, form.getMessage( ) );
        daoUtil.setString( nIndex++, form.getButtonLabel( ) );
        daoUtil.setBoolean( nIndex++, form.isConnection( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Form load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        Form form = null;

        if ( daoUtil.next( ) )
        {
            form = new Form( );
            int nIndex = 1;

            form.setId( daoUtil.getInt( nIndex++ ) );
            form.setTitle( daoUtil.getString( nIndex++ ) );
            form.setMessage( daoUtil.getString( nIndex++ ) );
            form.setButtonLabel( daoUtil.getString( nIndex++ ) );
            form.setConnection( daoUtil.getBoolean( nIndex++ ) );
        }

        daoUtil.free( );
        return form;
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
    public void store( Form form, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, form.getId( ) );
        daoUtil.setString( nIndex++, form.getTitle( ) );
        daoUtil.setString( nIndex++, form.getMessage( ) );
        daoUtil.setString( nIndex++, form.getButtonLabel( ) );
        daoUtil.setBoolean( nIndex++, form.isConnection( ) );
        daoUtil.setInt( nIndex, form.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Form> selectFormsList( Plugin plugin )
    {
        List<Form> formList = new ArrayList<Form>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Form form = new Form( );
            int nIndex = 1;

            form.setId( daoUtil.getInt( nIndex++ ) );
            form.setTitle( daoUtil.getString( nIndex++ ) );
            form.setMessage( daoUtil.getString( nIndex++ ) );
            form.setButtonLabel( daoUtil.getString( nIndex++ ) );
            form.setConnection( daoUtil.getBoolean( nIndex++ ) );

            formList.add( form );
        }

        daoUtil.free( );
        return formList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdFormsList( Plugin plugin )
    {
        List<Integer> formList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            formList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return formList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectFormsReferenceList( Plugin plugin )
    {
        ReferenceList formList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            formList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );
        return formList;
    }
}
