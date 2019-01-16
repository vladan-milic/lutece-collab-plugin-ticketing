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

import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;

public class TicketCategoryTypeService
{
    private static TicketCategoryTree _treeCategories;

    private static TicketCategoryTypeService _instance;

    private TicketCategoryTypeService( )
    {
    };

    /**
     * Get the instance of CategoryType service
     *
     * @param withInactives
     *            with Inactives
     *
     * @return the category type service
     */
    public static TicketCategoryTypeService getInstance( boolean withInactives )
    {
        if ( _instance == null )
        {
            _instance = new TicketCategoryTypeService( );
        }
        _treeCategories = TicketCategoryTreeCacheService.getInstance( withInactives ).getResource( );
        return _instance;
    }

    /**
     * Get the instance of CategoryType service
     *
     * @return the category type service
     */
    public static TicketCategoryTypeService getInstance( )
    {
        return getInstance( false );
    }

    /**
     * Get the category type corresponding to given depth number
     *
     * @param nDepth
     *            depth
     * @return the category type of given depth number
     */
    public TicketCategoryType findByDepthNumber( int nDepth )
    {
        return _treeCategories.findDepthByDepthNumber( nDepth );
    }

    public List<TicketCategory> getManageableCategories( )
    {
        return _treeCategories.getSortedNodes( ).stream( ).filter( category -> category.isManageable( ) ).collect( Collectors.toList( ) );
    }

}
