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
package fr.paris.lutece.plugins.ticketing.business.category;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for CategoryType objects
 */
public final class TicketCategoryTypeHome
{
    // Static variable pointed at the DAO instance
    private static ITicketCategoryTypeDAO _dao    = SpringContextService.getBean( "ticketing.ticketCategoryTypeDAO" );
    private static Plugin                 _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketCategoryTypeHome( )
    {
    }

    /**
     * Create an instance of the categoryType class
     * 
     * @param categoryType
     *            The instance of the CategoryType which contains the informations to store
     * @return The instance of categoryType which has been created with its primary key.
     */
    public static TicketCategoryType create( TicketCategoryType categoryType )
    {
        _dao.insert( categoryType, _plugin );

        return categoryType;
    }

    /**
     * Update of the categoryType which is specified in parameter
     * 
     * @param categoryType
     *            The instance of the CategoryType which contains the data to store
     * @return The instance of the categoryType which has been updated
     */
    public static TicketCategoryType update( TicketCategoryType categoryType )
    {
        _dao.store( categoryType, _plugin );

        return categoryType;
    }

    /**
     * Remove the categoryType whose identifier is specified in parameter
     * 
     * @param nKey
     *            The categoryType Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a categoryType whose identifier is specified in parameter
     * 
     * @param nKey
     *            The categoryType primary key
     * @return an instance of CategoryType
     */
    public static TicketCategoryType findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the categoryType objects and returns them as a list
     * 
     * @return the list which contains the data of all the categoryType objects
     */
    public static List<TicketCategoryType> getCategoryTypesList( )
    {
        return _dao.selectCategoryTypesList( _plugin );
    }

    /**
     * Load the id of all the categoryType objects and returns them as a list
     * 
     * @return the list which contains the id of all the categoryType objects
     */
    public static List<Integer> getIdCategoryTypesList( )
    {
        return _dao.selectIdCategoryTypesList( _plugin );
    }

    /**
     * Load the data of all the categoryType objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the categoryType objects
     */
    public static ReferenceList getCategoryTypesReferenceList( )
    {
        return _dao.selectCategoryTypesReferenceList( _plugin );
    }

    /**
     * Create a category type with a new depth ( previous depth + 1 )
     * 
     * @param categoryType
     * @return the category type
     */
    public static TicketCategoryType createNewDepthCategoryType( TicketCategoryType categoryType )
    {
        _dao.insertNewDepthCategoryType( categoryType, _plugin );
        return categoryType;
    }

    /**
     * Load a category type of the given depth
     * 
     * @param nDepth
     *            the depth
     * @return the Category type of the given depth
     */
    public static TicketCategoryType findByDepth( int nDepth )
    {
        return _dao.loadByDepth( nDepth, _plugin );
    }

}
