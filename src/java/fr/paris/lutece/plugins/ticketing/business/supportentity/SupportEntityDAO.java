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
package fr.paris.lutece.plugins.ticketing.business.supportentity;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for SupportEntity objects
 */
public final class SupportEntityDAO implements ISupportEntityDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_support_entity ) FROM ticketing_support_entity";
    private static final String SQL_QUERY_SELECT = "SELECT a.id_support_entity, a.name, a.level, a.id_unit, a.id_admin_user, a.id_domain  FROM ticketing_support_entity a "
            + " WHERE a.id_support_entity = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_support_entity ( id_support_entity, name, level, id_unit, id_admin_user, id_domain ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_support_entity  WHERE id_support_entity = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_support_entity SET id_support_entity = ?,  name = ?, level = ?, id_unit = ?, id_admin_user = ?, id_domain = ?  WHERE id_support_entity = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_support_entity, a.name, a.level, a.id_unit, a.id_admin_user, a.id_domain  FROM ticketing_support_entity a ";

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
    public void insert( SupportEntity supportEntity, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        supportEntity.setId( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, supportEntity.getId( ) );
        daoUtil.setString( 2, supportEntity.getName( ) );
        daoUtil.setInt( 3, supportEntity.getSupportLevel( ).getLevelValue( ) );
        daoUtil.setInt( 4, ( supportEntity.getUnit( ) != null ) ? supportEntity.getUnit( ).getUnitId( ) : ( -1 ) );
        daoUtil.setInt( 5, ( supportEntity.getUser( ) != null ) ? supportEntity.getUser( ).getAdminUserId( ) : ( -1 ) );
        daoUtil.setInt( 6, ( supportEntity.getTicketDomain( ) != null ) ? supportEntity.getTicketDomain( ).getId( ) : ( -1 ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SupportEntity load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        SupportEntity supportEntity = null;

        if ( daoUtil.next( ) )
        {
            supportEntity = getSupportEntity( daoUtil );
        }

        daoUtil.free( );

        return supportEntity;
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
    public void store( SupportEntity supportEntity, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, supportEntity.getId( ) );
        daoUtil.setString( 2, supportEntity.getName( ) );
        daoUtil.setInt( 3, supportEntity.getSupportLevel( ).getLevelValue( ) );
        daoUtil.setInt( 4, ( supportEntity.getUnit( ) != null ) ? supportEntity.getUnit( ).getUnitId( ) : ( -1 ) );
        daoUtil.setInt( 5, ( supportEntity.getUser( ) != null ) ? supportEntity.getUser( ).getAdminUserId( ) : ( -1 ) );
        daoUtil.setInt( 6, ( supportEntity.getTicketDomain( ) != null ) ? supportEntity.getTicketDomain( ).getId( ) : ( -1 ) );
        daoUtil.setInt( 7, supportEntity.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<SupportEntity> selectSupportEntityList( Plugin plugin )
    {
        List<SupportEntity> supportEntityList = new ArrayList<SupportEntity>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            SupportEntity supportEntity = getSupportEntity( daoUtil );
            supportEntityList.add( supportEntity );
        }

        daoUtil.free( );

        return supportEntityList;
    }

    /**
     * get SupportEntity from daoUtil
     * 
     * @param daoUtil
     *            daoUtil
     * @return SupportEntity instance load from daoUtil object
     */
    private SupportEntity getSupportEntity( DAOUtil daoUtil )
    {
        SupportEntity supportEntity = new SupportEntity( );
        supportEntity.setId( daoUtil.getInt( 1 ) );
        supportEntity.setName( daoUtil.getString( 2 ) );
        supportEntity.setSupportLevel( SupportLevel.valueOf( daoUtil.getInt( 3 ) ) );

        Unit unit = UnitHome.findByPrimaryKey( daoUtil.getInt( 4 ) );

        if ( unit != null )
        {
            supportEntity.setUnit( new AssigneeUnit( unit ) );
        }

        AdminUser adminUser = AdminUserHome.findByPrimaryKey( daoUtil.getInt( 5 ) );

        if ( adminUser != null )
        {
            supportEntity.setUser( new AssigneeUser( adminUser ) );
        }

        TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( daoUtil.getInt( 6 ) );

        if ( ticketDomain != null )
        {
            supportEntity.setTicketDomain( ticketDomain );
        }

        return supportEntity;
    }
}
