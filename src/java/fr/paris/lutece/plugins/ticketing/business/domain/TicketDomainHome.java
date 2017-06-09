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

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for TicketDomain objects
 */
public final class TicketDomainHome
{
    // Static variable pointed at the DAO instance
    private static ITicketDomainDAO _dao = SpringContextService.getBean( "ticketing.ticketDomainDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketDomainHome( )
    {
    }

    /**
     * Create an instance of the ticketDomain class
     * 
     * @param ticketDomain
     *            The instance of the TicketDomain which contains the informations to store
     */
    public static void create( TicketDomain ticketDomain )
    {
        _dao.insert( ticketDomain, _plugin );
    }

    /**
     * Update of the ticketDomain which is specified in parameter
     * 
     * @param ticketDomain
     *            The instance of the TicketDomain which contains the data to store
     */
    public static void update( TicketDomain ticketDomain )
    {
        TicketDomain currentTicketDomain = findByPrimaryKey( ticketDomain.getId( ) );
        int nCurrentTypeId = currentTicketDomain.getIdTicketType( );
        int nCurrentOrder = currentTicketDomain.getOrder( );
        int nTargetTypeId = ticketDomain.getIdTicketType( );

        if ( nCurrentTypeId != nTargetTypeId )
        {
            _dao.storeWithLastOrder( ticketDomain, _plugin );
            _dao.rebuildDomainOrdersByType( nCurrentOrder, nCurrentTypeId, _plugin );
        }
        else
        {
            _dao.store( ticketDomain, _plugin );
        }
    }

    /**
     * Remove the ticketDomain whose identifier is specified in parameter
     * 
     * @param nKey
     *            The ticketDomain Id
     */
    public static void remove( int nKey )
    {
        if ( canRemove( nKey ) )
        {
            TicketDomain ticketDomainToRemove = findByPrimaryKey( nKey );

            _dao.delete( nKey, _plugin );
            _dao.rebuildDomainOrdersByType( ticketDomainToRemove.getOrder( ), ticketDomainToRemove.getIdTicketType( ), _plugin );
        }
        else
        {
            throw new AppException( "TicketDomain cannot be removed for ID :" + nKey );
        }
    }

    /**
     * Returns the ticketType Id of a TicketDomain whose identifier is specified in parameter
     * 
     * @param nKey
     *            The ticketDomain Id
     */
    public static int getTypeId( int nKey )
    {

        TicketDomain _ticketDomain = _dao.load( nKey, _plugin );
        return _ticketDomain.getIdTicketType( );
    }

    /**
     * return true if domain can be removed false otherwise
     * 
     * @param nKey
     *            The ticketDomain Id
     * @return true if type can be removed false otherwise
     */
    public static boolean canRemove( int nKey )
    {
        return _dao.canRemoveDomain( nKey, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ticketDomain whose identifier is specified in parameter
     * 
     * @param nKey
     *            The ticketDomain primary key
     * @return an instance of TicketDomain
     */
    public static TicketDomain findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the ticketDomain objects and returns them in form of a collection
     * 
     * @param bInactiveDomain
     *            Boolean for inactive domain inclusion
     * @return the collection which contains the data of all the ticketDomain objects
     */
    public static List<TicketDomain> getTicketDomainsList( boolean bInactiveDomain )
    {
        return _dao.selectTicketDomainsList( _plugin, bInactiveDomain );
    }

    /**
     * Load the data of all the ticketDomain objects for a given ticketType and returns them in form of a collection
     * 
     * @param nTypeId
     *            The ticketType id
     * @return the collection which contains the data of all the ticketDomain objects
     */
    public static List<TicketDomain> getTicketDomainsListbyType( int nTypeId )
    {
        return _dao.selectDomainListByTypeId( nTypeId, _plugin );
    }

    /**
     * Load the data of all the ticketDomain objects for a label and returns them in form of a collection
     * 
     * @param sDomainLabel
     *            The ticketDomain label
     * @return the collection which contains the data of all the ticketDomain objects
     */
    public static List<TicketDomain> getTicketDomainsListByLabel( String sDomainLabel )
    {
        return _dao.selectDomainsByLabel( sDomainLabel, _plugin );
    }

    /**
     * Load the id of all the ticketDomain objects and returns them in form of a collection
     * 
     * @return the collection which contains the id of all the ticketDomain objects
     */
    public static List<Integer> getIdTicketDomainsList( )
    {
        return _dao.selectIdTicketDomainsList( _plugin );
    }

    /**
     * returns referenceList by type of input typeId
     * 
     * @param nTicketTypeId
     *            id of type
     * @param bInactiveDomain
     *            Boolean for inactive domain inclusion
     * @return ReferenceList of typeId
     */
    public static ReferenceList getReferenceListByType( int nTicketTypeId, boolean bInactiveDomain )
    {
        return _dao.selectReferenceListByType( nTicketTypeId, bInactiveDomain, _plugin );
    }

    /**
     * returns referenceList
     * 
     * @return ReferenceList
     */
    public static ReferenceList getReferenceList( )
    {
        return _dao.selectReferenceList( _plugin );
    }

    /**
     * returns referenceList
     * 
     * @return ReferenceList
     */
    public static ReferenceList getReferenceListSimple( )
    {
        return _dao.selectReferenceListSimple( _plugin );
    }

    /**
     * returns ticketDomains allowed for an admin user according to RBAC provided permission
     * 
     * @param adminUser
     *            admin user
     * @param strPermission
     *            TicketDomainResourceIdService permission
     * @return domains list filtered by RBAC permission
     */
    public static List<TicketDomain> getTicketDomainsList( AdminUser adminUser, String strPermission )
    {
        List<TicketDomain> listDomains = new ArrayList<TicketDomain>( );

        for ( TicketDomain domain : getTicketDomainsList( true ) )
        {
            if ( RBACService.isAuthorized( domain, strPermission, adminUser ) )
            {
                listDomains.add( domain );
            }
        }

        return listDomains;
    }

    /**
     * Returns the ReferenceList of all domains with the concatenation of the name of the type and the domain
     * 
     * @return the ReferenceList of all domains with the concatenation of the name of the type and the domain
     */
    public static ReferenceList getReferenceListModelResponse( )
    {
        return _dao.selectReferenceListModelResponse( _plugin );
    }

    /**
     * Change the position of a TicketDomain
     *
     * @param nId
     *            the if of domain to move
     * @param nCurrentPostion
     *            the current position of the Type
     * 
     * @param nNewPosition
     *            the target position of the Type
     */
    public static void updateDomainOrder( int nId, boolean moveUp )
    {
        TicketDomain ticketDomainToRemove = findByPrimaryKey( nId );
        int nCurrentOrder = ticketDomainToRemove.getOrder( );
        int nIdTicketType = ticketDomainToRemove.getIdTicketType( );
        int nTargetOrder = moveUp ? ( nCurrentOrder - 1 ) : ( nCurrentOrder + 1 );

        int nIdDomainWhichPlaceIsTaken = _dao.selectDomainIdByOrder( nTargetOrder, nIdTicketType, _plugin );

        if ( nIdDomainWhichPlaceIsTaken != -1 )
        {
            _dao.updateDomainOrder( nId, nTargetOrder, _plugin );
            _dao.updateDomainOrder( nIdDomainWhichPlaceIsTaken, nCurrentOrder, _plugin );
        }
        else
        {
            AppLogService
                    .error( "Could not move TicketDomain " + nId + " to position " + nTargetOrder + " : no Domain to replace on position " + nCurrentOrder );
        }
    }

}
