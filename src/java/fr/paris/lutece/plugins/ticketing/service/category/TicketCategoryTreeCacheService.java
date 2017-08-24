/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.service.category;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

public class TicketCategoryTreeCacheService extends AbstractCacheableService
{
    private static final String SERVICE_NAME = "TREE_CAT_CACHE_SERVICE";
    private static final String KEY_CACHE_TREE = "TREE_CAT";

    private static TicketCategoryTreeCacheService _instance;

    /**
     * Private constructor
     */
    private TicketCategoryTreeCacheService( )
    {
        initCache( );
    }

    /**
     * Return the instance of CategoryTreeCacheService
     * 
     * @return the CategoryTreeCacheService
     */
    public static TicketCategoryTreeCacheService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new TicketCategoryTreeCacheService( );
        }
        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }

    /**
     * The category tree found in cache or a new category tree otherwise
     * 
     * @return the category tree
     */
    public TicketCategoryTree getResource( )
    {
        TicketCategoryTree categoryTree = (TicketCategoryTree) getFromCache( KEY_CACHE_TREE );
        if ( categoryTree == null )
        {
            categoryTree = new TicketCategoryTree( TicketCategoryHome.getFullCategorysList( ), TicketCategoryTypeHome.getCategoryTypesList( ) );
            putInCache( KEY_CACHE_TREE, categoryTree );
        }
        return categoryTree;
    }

    /**
     * Put a new category tree in the cache
     */
    public void reloadResource( )
    {
        putInCache(KEY_CACHE_TREE, new TicketCategoryTree( TicketCategoryHome.getFullCategorysList( ), TicketCategoryTypeHome.getCategoryTypesList( ) ) );
    }
}
