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

import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryTree;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for Category objects
 */
public final class TicketCategoryHome
{
    // Static variable pointed at the DAO instance
    private static ITicketCategoryDAO _dao    = SpringContextService.getBean( "ticketing.ticketCategoryDAO" );
    private static Plugin             _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketCategoryHome( )
    {
    }

    /**
     * Create an instance of the category class
     *
     * @param category
     *            The instance of the Category which contains the informations to store
     * @return The instance of category which has been created with its primary key.
     */
    public static TicketCategory create( TicketCategory category )
    {
        _dao.insert( category, _plugin );

        return category;
    }

    /**
     * Update of the category which is specified in parameter
     *
     * @param category
     *            The instance of the Category which contains the data to store
     * @return The instance of the category which has been updated
     */
    public static TicketCategory update( TicketCategory category )
    {
        TicketCategory currentCategory = findByPrimaryKey( category.getId( ) );
        int nCurrentParentId = currentCategory.getIdParent( );
        int nCurrentOrder = currentCategory.getOrder( );
        int nTargetParentId = category.getIdParent( );

        if ( nCurrentParentId != nTargetParentId )
        {
            _dao.storeWithLastOrder( category, _plugin );
            _dao.rebuildCategoryOrders( nCurrentOrder, nCurrentParentId, _plugin );
        } else
        {
            _dao.store( category, _plugin );
        }

        return category;
    }

    /**
     * Remove the category whose identifier is specified in parameter
     *
     * @param nKey
     *            The category Id
     */
    public static void remove( int nKey )
    {
        // if ( canRemove( nKey ) )
        // {
        TicketCategory categoryToRemove = findByPrimaryKey( nKey );

        _dao.delete( nKey, _plugin );
        _dao.rebuildCategoryOrders( categoryToRemove.getOrder( ), categoryToRemove.getIdParent( ), _plugin );
        // }
        // else
        // {
        // throw new AppException( "TicketCategory cannot be removed for ID :" + nKey );
        // }
    }

    /**
     * return true if category can be removed false otherwise
     *
     * @param nKey
     *            The category Id
     * @return true if type can be removed false otherwise
     */
    public static boolean canRemove( int nKey )
    {
        return _dao.canRemoveCategory( nKey, _plugin );
    }

    /**
     * Returns an instance of a category whose identifier is specified in parameter
     *
     * @param nKey
     *            The category primary key
     * @return an instance of Category
     */
    public static TicketCategory findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    public static TicketCategory findByCode( String strCode )
    {
        return _dao.loadByCode( strCode, _plugin );
    }

    /**
     * Load the data of all the category objects and returns them as a list
     *
     * @return the list which contains the data of all the category objects
     */
    public static List<TicketCategory> getCategorysList( )
    {
        return _dao.selectCategorysList( _plugin );
    }

    /**
     * Load the list of categories (full, with java objects filled)
     *
     * @param withInactives
     *
     * @return list of categories
     */
    public static List<TicketCategory> getFullCategorysList( boolean withInactives )
    {
        return _dao.selectFullCategorysList( _plugin, withInactives );
    }

    /**
     * Load the id of all the category objects and returns them as a list
     *
     * @return the list which contains the id of all the category objects
     */
    public static List<Integer> getIdCategorysList( )
    {
        return _dao.selectIdCategorysList( _plugin );
    }

    /**
     * Load the data of all the category objects and returns them as a referenceList
     *
     * @return the referenceList which contains the data of all the category objects
     */
    public static ReferenceList getCategorysReferenceList( )
    {
        return _dao.selectCategorysReferenceList( _plugin );
    }

    /**
     * Change the position of a category
     *
     * @param nId
     *            the if of category to move
     * @param bMoveUp
     */
    public static void updateCategoryOrder( int nId, boolean bMoveUp )
    {
        TicketCategoryTree tree = TicketCategoryService.getInstance( ).getCategoriesTree( );
        TicketCategory sourceCategory = tree.findNodeById( nId );
        TicketCategory targetCategory = bMoveUp ? sourceCategory.getPreviousSibling( tree ) : sourceCategory.getNextSibling( tree );

        if ( targetCategory != null )
        {
            int targetOrder = targetCategory.getOrder( );
            int sourceOrder = sourceCategory.getOrder( );

            // If two categories have the same order, add one to the last element
            if ( targetCategory.getOrder( ) == sourceCategory.getOrder( ) )
            {
                if ( bMoveUp )
                {
                    sourceOrder++;
                } else
                {
                    targetOrder++;
                }
            }

            _dao.updateCategoryOrder( sourceCategory.getId( ), targetOrder, _plugin );
            _dao.updateCategoryOrder( targetCategory.getId( ), sourceOrder, _plugin );
        } else
        {
            AppLogService.error( "Could not move TicketCategory " + nId + " " + ( bMoveUp ? "up" : "down" ) + " : no TicketCategory to replace on destination " );
        }
    }

}
