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
package fr.paris.lutece.plugins.ticketing.business.channel;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for
 * Channel objects
 */
public final class ChannelHome
{
    // Static variable pointed at the DAO instance
    private static IChannelDAO _dao = SpringContextService.getBean( "ticketing.channelDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ChannelHome(  )
    {
    }

    /**
     * Create an instance of the channel class
     *
     * @param channel
     *            The instance of the Channel which contains the
     *            informations to store
     */
    public static void create( Channel channel )
    {
        _dao.insert( channel, _plugin );
    }

    /**
     * Update of the channel which is specified in parameter
     *
     * @param channel
     *            The instance of the Channel which contains the data to
     *            store
     */
    public static void update( Channel channel )
    {
        _dao.store( channel, _plugin );
    }

    /**
     * Remove the channel whose identifier is specified in parameter
     *
     * @param nKey
     *            The channel Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a channel whose identifier is specified in
     * parameter
     *
     * @param nKey
     *            The channel primary key
     * @return an instance of Channel
     */
    public static Channel findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the channel objects and returns them in form of
     * a collection
     *
     * @return the collection which contains the data of all the channel
     *         objects
     */
    public static List<Channel> getChannelList(  )
    {
        return _dao.selectChannelList( _plugin );
    }

    /**
     * Load the id of all the channel objects and returns them in form of a
     * collection
     *
     * @return the collection which contains the id of all the channel
     *         objects
     */
    public static List<Integer> getIdChannelList(  )
    {
        return _dao.selectIdChannelList( _plugin );
    }

    /**
     * Load the data of all the channel objects and returns them as a
     * reference list
     *
     * @return The reference list
     */
    public static ReferenceList getReferenceList(  )
    {
        return _dao.selectReferenceList( _plugin );
    }
}
