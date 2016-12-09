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
package fr.paris.lutece.plugins.ticketing.business.category;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


/**
 * This class provides instances management methods (create, find, ...) for TicketCategory objects
 */
/**
 * @author a120274
 *
 */
public final class TicketCategoryHome
{
    // Static variable pointed at the DAO instance
    private static ITicketCategoryDAO _dao = SpringContextService.getBean( "ticketing.ticketCategoryDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketCategoryHome(  )
    {
    }

    /**
     * Create an instance of the ticketCategory class
     * @param ticketCategory The instance of the TicketCategory which contains the informations to store
     */
    public static void create( TicketCategory ticketCategory )
    {
        _dao.insert( ticketCategory, _plugin );
    }

    /**
     * Create a link between a category and an input
     * @param nIdCategory id Category
     * @param nIdInput id Input
     * @param nPos id Input position
     */
    public static void createLinkCategoryInput( int nIdCategory, int nIdInput, int nPos )
    {
        _dao.insertLinkCategoryInput( nIdCategory, nIdInput, nPos, _plugin );
    }
    
    
    /**
     * Create a link between a category and an input
     * @param nIdCategory id Category
     * @param nIdInput id Input
     * @param nPos id Input position
     */
    public static void createLinkCategoryInputNextPos( int nIdCategory, int nIdInput )
    {
        _dao.insertLinkCategoryInputNextPos( nIdCategory, nIdInput, _plugin );
    }

    /**
     * Update of the ticketCategory which is specified in parameter
     * @param ticketCategory The instance of the TicketCategory which contains the data to store
     */
    public static void update( TicketCategory ticketCategory )
    {
        _dao.store( ticketCategory, _plugin );
    }

    /**
     * Remove the ticketCategory whose identifier is specified in parameter
     * @param nKey The ticketCategory Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Remove a link between a category and an input
     * @param nIdCategory id Category
     * @param nIdInput id Input
     */
    public static void removeLinkCategoryInput( int nIdCategory, int nIdInput )
    {
        _dao.deleteLinkCategoryInput( nIdCategory, nIdInput, _plugin );
    }

    /**
     * Update the Position field in a link between a category and an input
     * @param nIdCategory id Category
     * @param nIdInput id Input
     * @param nPosition the position value
     */
    public static void updateCategoryInputPosition( int nIdCategory, int nIdInput, int nPosition )
    {
        _dao.updateLinkCategoryInputPos( nIdCategory, nIdInput, nPosition, _plugin);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ticketCategory whose identifier is specified in parameter
     * @param nKey The ticketCategory primary key
     * @return an instance of TicketCategory
     */
    public static TicketCategory findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Find a category by its code
     * @param strCode The code
     * @return The category
     */
    public static TicketCategory findByCode( String strCode )
    {
        return _dao.loadByCode( strCode, _plugin );
    }

    /**
     * Load the data of all the ticketCategory objects and returns them in form of a collection
     * @return the collection which contains the data of all the ticketCategory objects
     */
    public static List<TicketCategory> getTicketCategorysList(  )
    {
        return _dao.selectTicketCategorysList( _plugin );
    }

    /**
     * Load the id of all the ticketCategory objects and returns them in form of a collection
     * @return the collection which contains the id of all the ticketCategory objects
     */
    public static List<Integer> getIdTicketCategorysList(  )
    {
        return _dao.selectIdTicketCategorysList( _plugin );
    }

    /**
     * returns referenceList of input domainId
     * @param nDomainId id of domain
     * @return ReferenceList of domainId
     */
    public static ReferenceList getReferenceListByDomain( int nDomainId )
    {
        return _dao.selectReferenceListByDomain( nDomainId, _plugin );
    }

    /**
     * returns the position of an input for a given category
     * @param nId id of category
     * @param nIdInput id of input
     * @return the position as an integer
     */
	public static int getCategoryInputPosition( int nId, int nIdInput )
	{
		return _dao.selectCategoryInputPosition( nId, nIdInput, _plugin );

	}

    /**
     * returns the iD of an input for a given category and position
     * @param nId id of category
     * @param nPos the position
     * @return the input id
     */
	public static int getCategoryInputByPosition( int nId, int nPos )
	{
		return _dao.selectCategoryInputByPosition( nId, nPos, _plugin );
	}
	
}
