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

import fr.paris.lutece.plugins.ticketing.service.tree.Tree;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;

public class TicketCategoryService
{

    private final String KEY_DEFAULT_CATEGORY_TYPE_LABEL = "ticketing.create_categorytype.default.label";
    private static TicketCategoryTree _treeCategories;

    private static TicketCategoryService _instance;

    private TicketCategoryService( )
    {
    };

    /**
     * Get the instance of CategoryService
     * 
     * @return the instance of categoryService
     */
    public static TicketCategoryService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new TicketCategoryService( );
        }
        _treeCategories = TicketCategoryTreeCacheService.getInstance( ).getResource( );
        return _instance;
    }

    /**
     * Return the category tree
     * 
     * @return the category tree
     */
    public Tree<TicketCategory, TicketCategoryType> getCategoriesTree( )
    {
        return _treeCategories;
    }

    /**
     * Find a category by code in the category tree
     * 
     * @param strCode
     *            the category code
     * @return the category coresponding to given code
     */
    public TicketCategory findByCode( String strCode )
    {
        return _treeCategories.findByCode( strCode );
    }

    /**
     * Find a category by id in the category tree
     * 
     * @param nId
     *            the category id
     * @return the category corresponding to given id
     */
    public TicketCategory findById( int nId )
    {
        return _treeCategories.findNodeById( nId );
    }

    /**
     * Find the assignee unit of given category (recursively found in parent)
     * 
     * @param category
     *            the category
     * @return the assignee unit
     */
    public Unit findAssigneeUnit( TicketCategory category )
    {
        Unit unit = category.getDefaultAssignUnit( );
        if ( unit.getIdUnit( ) != -1 )
        {
            return unit;
        }
        while ( unit.getIdUnit( ) == -1 && category.getCategoryType( ).getNbDepth( ) > 0 )
        {
            category = category.getParent( );
            unit = category.getDefaultAssignUnit( );
            if ( unit.getIdUnit( ) != -1 )
            {
                return unit;
            }
        }
        return null;
    }

    /**
     * Create a sub category (based on his parent id)
     * 
     * @param subCategory
     *            the subcategory to create
     * @return the created category
     */
    public TicketCategory createSubCategory( TicketCategory subCategory )
    {
        if ( subCategory.getParent( ) == null )
        {
            // Try to get root category type
            TicketCategoryType childCategoryType = TicketCategoryTypeService.getInstance( ).findByNbDepth( 0 );
            if ( childCategoryType == null )
            {
                TicketCategoryType newCategoryType = new TicketCategoryType( );
                newCategoryType.setDepth( 1 );
                newCategoryType.setLabel( I18nService.getLocalizedString( KEY_DEFAULT_CATEGORY_TYPE_LABEL, Locale.getDefault( ) ) );
                TicketCategoryTypeHome.create( childCategoryType );
                subCategory.setCategoryType( newCategoryType );
            }
            else
            {
                subCategory.setCategoryType( childCategoryType );
            }
        }
        else
        {
            // Try to get child category type
            TicketCategory parentCategory = subCategory.getParent( );
            TicketCategoryType categoryTypeParent = parentCategory.getCategoryType( );
            TicketCategoryType childCategoryType = TicketCategoryTypeService.getInstance( ).findByNbDepth( categoryTypeParent.getDepth( ) + 1 );

            if ( childCategoryType == null )
            {
                TicketCategoryType newCategoryType = new TicketCategoryType( );
                newCategoryType.setDepth( categoryTypeParent.getDepth( ) + 1 );
                newCategoryType.setLabel( I18nService.getLocalizedString( KEY_DEFAULT_CATEGORY_TYPE_LABEL, Locale.getDefault( ) ) );
                TicketCategoryTypeHome.create( newCategoryType );
                subCategory.setCategoryType( newCategoryType );
            }
            else
            {
                subCategory.setCategoryType( childCategoryType );
            }
        }
        TicketCategoryHome.create( subCategory );

        return subCategory;
    }

    /**
     * Get the branch of a category (from root to category)
     * 
     * @param category
     * @return the branch of categories from root to category
     */
    public List<TicketCategory> getBranchOfCategory( TicketCategory category )
    {
        return _treeCategories.getBranch( category );
    }
    
    /**
     * Check if given category is Authorized for given user and given permission
     * @param category
     * @param strPermission
     * @param user
     * @return true if authorized, false otherwise
     */
    public static boolean isAutorized ( TicketCategory category, String strPermission,  AdminUser user )
    {
        List<TicketCategory> listCategory = TicketCategoryService.getInstance( ).getBranchOfCategory(TicketCategoryService.getInstance( ).findById( category.getId( ) ) );
            for ( TicketCategory categoryBranch : listCategory )
            {
                if ( RBACService.isAuthorized( categoryBranch, strPermission, user ) )
                {
                    return true;
                }
            }
        return false;
    }
    
    ////////
    // FOR TICKETING DOMAIN/TYPE/PRECISION
    
    /**
     * Return the ticket domain
     * @param ticketCategory the category of a ticket
     * @return the ticket domain
     */
    public static TicketCategory getDomain ( TicketCategory ticketCategory )
    {
        try
        {
           TicketCategory domainCategory = ticketCategory.getBranch( ).get( 0 ); 
           return domainCategory;
        }
        catch ( IndexOutOfBoundsException e )
        {
            TicketCategory emptyCategory = new TicketCategory( );
            emptyCategory.setLabel( StringUtils.EMPTY );
            return emptyCategory;
        }
    }
    
    /**
     * Return the ticket type
     * @param ticketCategory the category of a ticket
     * @return the type of a ticket
     */
    public static TicketCategory getType ( TicketCategory ticketCategory )
    {
        try
        {
           TicketCategory typeCategory = ticketCategory.getBranch( ).get( 1 ); 
           return typeCategory;
        }
        catch ( IndexOutOfBoundsException e )
        {
            TicketCategory emptyCategory = new TicketCategory( );
            emptyCategory.setLabel( StringUtils.EMPTY );
            return emptyCategory;
        }
    }
    
    /**
     * Return the ticket precision
     * @param ticketCategory the category of a ticket
     * @return the precision of a ticket
     */
    public static TicketCategory getPrecision ( TicketCategory ticketCategory )
    {
        try
        {
           TicketCategory precisionCategory = ticketCategory.getBranch( ).get( 2 ); 
           return precisionCategory;
        }
        catch ( IndexOutOfBoundsException e )
        {
            TicketCategory emptyCategory = new TicketCategory( );
            emptyCategory.setLabel( StringUtils.EMPTY );
            return emptyCategory;
        }
    }
}
