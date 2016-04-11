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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for UserTitle objects
 */
public final class UserTitleDAO implements IUserTitleDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_user_title ) FROM ticketing_user_title";
    private static final String SQL_QUERY_SELECT = "SELECT id_user_title, label FROM ticketing_user_title WHERE id_user_title = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_user_title ( id_user_title, label, inactive ) VALUES ( ?, ?, 0 ) ";
    private static final String SQL_QUERY_DELETE = "UPDATE ticketing_user_title SET inactive = 1 WHERE id_user_title = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_user_title SET id_user_title = ?, label = ? WHERE id_user_title = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_user_title, label FROM ticketing_user_title WHERE inactive <> 1 ";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_user_title FROM ticketing_user_title WHERE inactive <> 1 ";

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
    public void insert( UserTitle userTitle, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        userTitle.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, userTitle.getId(  ) );
        daoUtil.setString( 2, userTitle.getLabel(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public UserTitle load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        UserTitle userTitle = null;

        if ( daoUtil.next(  ) )
        {
            userTitle = new UserTitle(  );
            userTitle.setId( daoUtil.getInt( 1 ) );
            userTitle.setLabel( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return userTitle;
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
    public void store( UserTitle userTitle, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, userTitle.getId(  ) );
        daoUtil.setString( 2, userTitle.getLabel(  ) );
        daoUtil.setInt( 3, userTitle.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<UserTitle> selectUserTitlesList( Plugin plugin )
    {
        List<UserTitle> userTitleList = new ArrayList<UserTitle>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            UserTitle userTitle = new UserTitle(  );

            userTitle.setId( daoUtil.getInt( 1 ) );
            userTitle.setLabel( daoUtil.getString( 2 ) );

            userTitleList.add( userTitle );
        }

        daoUtil.free(  );

        return userTitleList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdUserTitlesList( Plugin plugin )
    {
        List<Integer> userTitleList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            userTitleList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return userTitleList;
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
}
