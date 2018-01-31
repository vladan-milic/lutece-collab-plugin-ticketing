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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for FormEntry objects
 */
public final class FormEntryDAO implements IFormEntryDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_formentry ) FROM ticketing_formentry";
    private static final String SQL_QUERY_SELECT = "SELECT id_formentry, id_form, id_champ, hidden, mandatory, hierarchy, default_value FROM ticketing_formentry WHERE id_formentry = ?";
    private static final String SQL_QUERY_SELECT_BY_FORM = "SELECT id_formentry, id_form, id_champ, hidden, mandatory, hierarchy, default_value FROM ticketing_formentry WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_formentry ( id_formentry, id_form, id_champ, hidden, mandatory, hierarchy, default_value ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_formentry WHERE id_formentry = ? ";
    private static final String SQL_QUERY_DELETE_BY_ID_FORM = "DELETE FROM ticketing_formentry WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_formentry SET id_formentry = ?, id_form = ?, id_champ = ?, hidden = ?, mandatory = ?, hierarchy = ?, default_value = ? WHERE id_formentry = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_formentry, id_form, id_champ, hidden, mandatory, hierarchy, default_value FROM ticketing_formentry";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_formentry FROM ticketing_formentry";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin)
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK , plugin  );
        daoUtil.executeQuery( );
        int nKey = 1;

        if( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free();
        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormEntry formEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        formEntry.setId( newPrimaryKey( plugin ) );
        int nIndex = 1;

        daoUtil.setInt( nIndex++ , formEntry.getId( ) );
        daoUtil.setInt( nIndex++ , formEntry.getIdForm( ) );
        daoUtil.setString( nIndex++ , formEntry.getIdChamp( ) );
        daoUtil.setBoolean( nIndex++ , formEntry.isHidden( ) );
        daoUtil.setBoolean( nIndex++ , formEntry.isMandatory( ) );
        daoUtil.setInt( nIndex++ , formEntry.getHierarchy( ) );
        daoUtil.setString( nIndex++, formEntry.getDefaultValue( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormEntry> loadByForm( int nKey, Plugin plugin )
    {
        List<FormEntry> formEntryList = new ArrayList<FormEntry>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FORM, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        while ( daoUtil.next(  ) )
        {
            FormEntry formEntry = new FormEntry(  );
            int nIndex = 1;

            formEntry.setId( daoUtil.getInt( nIndex++ ) );
            formEntry.setIdForm( daoUtil.getInt( nIndex++ ) );
            formEntry.setIdChamp( daoUtil.getString( nIndex++ ) );
            formEntry.setHidden( daoUtil.getBoolean( nIndex++ ) );
            formEntry.setMandatory( daoUtil.getBoolean( nIndex++ ) );
            formEntry.setHierarchy( daoUtil.getInt( nIndex++ ) );
            formEntry.setDefaultValue( daoUtil.getString( nIndex++ ) );

            formEntryList.add( formEntry );
        }

        daoUtil.free( );
        return formEntryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormEntry load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeQuery( );
        FormEntry formEntry = null;

        if ( daoUtil.next( ) )
        {
            formEntry = new FormEntry();
            int nIndex = 1;

            formEntry.setId( daoUtil.getInt( nIndex++ ) );
            formEntry.setIdForm( daoUtil.getInt( nIndex++ ) );
            formEntry.setIdChamp( daoUtil.getString( nIndex++ ) );
            formEntry.setHidden( daoUtil.getBoolean( nIndex++ ) );
            formEntry.setMandatory( daoUtil.getBoolean( nIndex++ ) );
            formEntry.setHierarchy( daoUtil.getInt( nIndex++ ) );
            formEntry.setDefaultValue( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free( );
        return formEntry;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByIdForm(int nKey, Plugin plugin) {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_FORM, plugin );
        daoUtil.setInt( 1 , nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );	
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( FormEntry formEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++ , formEntry.getId( ) );
        daoUtil.setInt( nIndex++ , formEntry.getIdForm( ) );
        daoUtil.setString( nIndex++ , formEntry.getIdChamp( ) );
        daoUtil.setBoolean( nIndex++ , formEntry.isHidden( ) );
        daoUtil.setBoolean( nIndex++ , formEntry.isMandatory( ) );
        daoUtil.setInt( nIndex++ , formEntry.getHierarchy( ) );
        daoUtil.setString( nIndex++, formEntry.getDefaultValue( ) );
        daoUtil.setInt( nIndex , formEntry.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormEntry> selectFormEntrysList( Plugin plugin )
    {
        List<FormEntry> formEntryList = new ArrayList<FormEntry>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            FormEntry formEntry = new FormEntry(  );
            int nIndex = 1;

            formEntry.setId( daoUtil.getInt( nIndex++ ) );
            formEntry.setIdForm( daoUtil.getInt( nIndex++ ) );
            formEntry.setIdChamp( daoUtil.getString( nIndex++ ) );
            formEntry.setHidden( daoUtil.getBoolean( nIndex++ ) );
            formEntry.setMandatory( daoUtil.getBoolean( nIndex++ ) );
            formEntry.setHierarchy( daoUtil.getInt( nIndex++ ) );
            formEntry.setDefaultValue( daoUtil.getString( nIndex++ ) );

            formEntryList.add( formEntry );
        }

        daoUtil.free( );
        return formEntryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdFormEntrysList( Plugin plugin )
    {
        List<Integer> formEntryList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            formEntryList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return formEntryList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectFormEntrysReferenceList( Plugin plugin )
    {
        ReferenceList formEntryList = new ReferenceList();
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            formEntryList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
        }

        daoUtil.free( );
        return formEntryList;
    }
}