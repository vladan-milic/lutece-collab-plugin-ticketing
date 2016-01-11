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

import fr.paris.lutece.plugins.ticketing.service.TicketFormCacheService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for UserTitle objects
 */
public final class UserTitleHome
{
    // Static variable pointed at the DAO instance
    private static IUserTitleDAO _dao = SpringContextService.getBean( "ticketing.userTitleDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );
    private static TicketFormCacheService _cacheService = TicketFormCacheService.getInstance(  );

    /**
     * Private constructor - this class need not be instantiated
     */
    private UserTitleHome(  )
    {
    }

    /**
     * Create an instance of the userTitle class
     * @param userTitle The instance of the UserTitle which contains the informations to store
     */
    public static void create( UserTitle userTitle )
    {
        _dao.insert( userTitle, _plugin );

        if ( _cacheService.isCacheEnable(  ) )
        {
            _cacheService.putInCache( TicketFormCacheService.getUserTitleByIdCacheKey( userTitle.getId(  ) ),
                    userTitle.clone(  ) );
        }
    }

    /**
     * Update of the userTitle which is specified in parameter
     * @param userTitle The instance of the UserTitle which contains the data to store
     */
    public static void update( UserTitle userTitle )
    {
        _dao.store( userTitle, _plugin );

        if ( _cacheService.isCacheEnable(  ) )
        {
            _cacheService.putInCache( TicketFormCacheService.getUserTitleByIdCacheKey( userTitle.getId(  ) ),
                    userTitle.clone(  ) );
        }
    }

    /**
     * Remove the userTitle whose identifier is specified in parameter
     * @param nKey The userTitle Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
        
        if ( _cacheService.isCacheEnable(  ) )
        {
            _cacheService.removeKey( TicketFormCacheService.getUserTitleByIdCacheKey( nKey ) );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a userTitle whose identifier is specified in parameter
     * @param nKey The userTitle primary key
     * @return an instance of UserTitle
     */
    public static UserTitle findByPrimaryKey( int nKey )
    {
        String strCacheKey = TicketFormCacheService.getUserTitleByIdCacheKey( nKey );
        UserTitle userTitle = null;

        if ( _cacheService.isCacheEnable(  ) )
        {
            userTitle = (UserTitle) _cacheService.getFromCache( strCacheKey );
        }

        if ( userTitle == null )
        {
            userTitle = _dao.load( nKey, _plugin );

            if ( userTitle != null )
            {
                if ( _cacheService.isCacheEnable(  ) )
                {
                    _cacheService.putInCache( strCacheKey, userTitle.clone(  ) );
                }
            }
        }
        else
        {
            userTitle = (UserTitle) userTitle.clone(  );
        }

        return userTitle;
    }

    /**
     * Load the data of all the userTitle objects and returns them in form of a collection
     * @return the collection which contains the data of all the userTitle objects
     */
    public static List<UserTitle> getUserTitlesList(  )
    {
        return _dao.selectUserTitlesList( _plugin );
    }

    /**
     * Load the id of all the userTitle objects and returns them in form of a collection
     * @return the collection which contains the id of all the userTitle objects
     */
    public static List<Integer> getIdUserTitlesList(  )
    {
        return _dao.selectIdUserTitlesList( _plugin );
    }

    /**
     * returns referenceList
     * @return ReferenceList
     */
    public static ReferenceList getReferenceList(  )
    {
        return _dao.selectReferenceList( _plugin );
    }
}
