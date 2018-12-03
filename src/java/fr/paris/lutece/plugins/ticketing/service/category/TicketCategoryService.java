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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.plugins.ticketing.business.categoryinputs.TicketCategoryInputsHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.util.ReferenceList;

public class TicketCategoryService
{

    private final String                 KEY_DEFAULT_CATEGORY_TYPE_LABEL = "ticketing.create_categorytype.default.label";

    private final String                 KEY_HERITED_MESSAGE             = "ticketing.modify_category_inputs.herited";
    private final String                 KEY_BLOCKED_MESSAGE             = "ticketing.modify_category_inputs.blocked";
    private static TicketCategoryTree    _treeCategories;

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
        return getInstance( false );
    }

    /**
     * Get the instance of CategoryService
     *
     * @param withInactives
     *            boolean with inactives
     *
     * @return the instance of categoryService
     */
    public static TicketCategoryService getInstance( boolean withInactives )
    {
        if ( _instance == null )
        {
            _instance = new TicketCategoryService( );
        }
        _treeCategories = TicketCategoryTreeCacheService.getInstance( withInactives ).getResource( );
        return _instance;
    }

    /**
     * Return the category tree
     *
     * @param restrictedCategoriesId
     *            restricted Categories Id
     *
     * @return the category tree
     */
    public TicketCategoryTree getCategoriesTree( List<Integer> restrictedCategoriesId )
    {
        return new TicketCategoryTree( _treeCategories, restrictedCategoriesId );
    }

    /**
     * Return the category tree
     *
     * @return the category tree
     */
    public TicketCategoryTree getCategoriesTree( )
    {
        return getCategoriesTree( null );
    }

    /**
     * Find a category by code in the category tree
     *
     * @param strCode
     *            the category code
     * @return the category coresponding to given code
     */
    public TicketCategory findCategoryByCode( String strCode )
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
    public TicketCategory findCategoryById( int nId )
    {
        return _treeCategories.findNodeById( nId );
    }

    /**
     * Find a category type by id in the list of depth of the category tree
     *
     * @param nId
     *            the category type id
     * @return the category type corresponding to given id
     */
    public TicketCategoryType findCategoryTypeById( int nId )
    {
        return _treeCategories.findDepthByDepthNumber( nId );
    }

    /**
     * Find the assignee unit of given category (recursively found in parent)
     *
     * @param category
     *            the category
     * @return the assignee unit
     */
    public AssigneeUnit findAssigneeUnit( TicketCategory category )
    {
        AssigneeUnit unit = category.getDefaultAssignUnit( );
        if ( unit.getUnitId( ) != -1 )
        {
            return unit;
        }
        while ( ( unit.getUnitId( ) == -1 ) && ( category.getCategoryType( ).getDepthNumber( ) > 0 ) )
        {
            category = category.getParent( );
            unit = category.getDefaultAssignUnit( );
            if ( unit.getUnitId( ) != -1 )
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
            TicketCategoryType childCategoryType = TicketCategoryTypeService.getInstance( ).findByDepthNumber( 1 );
            if ( childCategoryType == null )
            {
                TicketCategoryType newCategoryType = new TicketCategoryType( );
                newCategoryType.setDepthNumber( 1 );
                newCategoryType.setLabel( I18nService.getLocalizedString( KEY_DEFAULT_CATEGORY_TYPE_LABEL, Locale.getDefault( ) ) );
                TicketCategoryTypeHome.create( newCategoryType );
                subCategory.setCategoryType( newCategoryType );
            } else
            {
                subCategory.setCategoryType( childCategoryType );
            }
        } else
        {
            // Try to get child category type
            TicketCategory parentCategory = subCategory.getParent( );
            TicketCategoryType categoryTypeParent = parentCategory.getCategoryType( );
            TicketCategoryType childCategoryType = TicketCategoryTypeService.getInstance( ).findByDepthNumber( categoryTypeParent.getDepthNumber( ) + 1 );

            if ( childCategoryType == null )
            {
                TicketCategoryType newCategoryType = new TicketCategoryType( );
                newCategoryType.setDepthNumber( categoryTypeParent.getDepthNumber( ) + 1 );
                newCategoryType.setLabel( I18nService.getLocalizedString( KEY_DEFAULT_CATEGORY_TYPE_LABEL, Locale.getDefault( ) ) );
                TicketCategoryTypeHome.create( newCategoryType );
                subCategory.setCategoryType( newCategoryType );
            } else
            {
                subCategory.setCategoryType( childCategoryType );
            }
        }
        TicketCategoryHome.create( subCategory );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );

        return subCategory;
    }

    /**
     * Remove the category
     *
     * @param nIdCategory
     *            category the id of the category to remove
     */
    public void removeCategory( int nIdCategory )
    {
        TicketCategoryInputsHome.removeAllLinksCategoryInput( nIdCategory );
        TicketCategoryHome.remove( nIdCategory );

        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Update the category
     *
     * @param ticketCategory
     *            the category to update
     */
    public void updateCategory( TicketCategory ticketCategory )
    {
        TicketCategoryHome.update( ticketCategory );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Update order the category
     *
     * @param bMoveUp
     *            boolean move up
     *
     * @param nIdCategory
     *            category the id of the category to order
     */
    public void updateCategoryOrder( int nIdCategory, boolean bMoveUp )
    {
        TicketCategoryHome.updateCategoryOrder( nIdCategory, bMoveUp );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Create the category type
     *
     * @param ticketCategoryType
     *            the category type to create
     */
    public void createCategoryType( TicketCategoryType ticketCategoryType )
    {
        TicketCategoryTypeHome.create( ticketCategoryType );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Remove the category type
     *
     * @param nIdCategoryType
     *            category type the id of the category type to remove
     */
    public void removeCategoryTypeAndSubType( int nIdCategoryType )
    {
        List<TicketCategoryType> listType = TicketCategoryTypeHome.getCategoryTypesList( );
        TicketCategoryType ticketCategoryType = TicketCategoryTypeHome.findByPrimaryKey( nIdCategoryType );

        listType.stream( ).filter( ( type ) -> ( type.getDepthNumber( ) >= ticketCategoryType.getDepthNumber( ) ) ).forEach( ( type ) ->
        {
            TicketCategoryTypeHome.remove( type.getId( ) );
        } );

        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Update the category type
     *
     * @param ticketCategoryType
     *            the category type to update
     */
    public void updateCategoryType( TicketCategoryType ticketCategoryType )
    {
        TicketCategoryTypeHome.update( ticketCategoryType );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Create the link of category input
     *
     * @param nIdCategory
     *            id ticket category
     * @param nIdInput
     *            id input
     */
    public void createLinkCategoryInput( int nIdCategory, int nIdInput )
    {
        TicketCategoryInputsHome.createLinkCategoryInputNextPos( nIdCategory, nIdInput );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Remove the link of category input
     *
     * @param nIdCategory
     *            id ticket category
     * @param nIdInput
     *            id input
     */
    public void removeLinkCategoryInput( int nIdCategory, int nIdInput )
    {
        TicketCategoryInputsHome.removeLinkCategoryInput( nIdCategory, nIdInput );
        reorganizeCategoryInputs( nIdCategory );
        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Update the inputs within a given category with consecutive position indexes
     *
     * @param nId
     *            the category which inputs are being reordered
     */
    private void reorganizeCategoryInputs( int nId )
    {
        TicketCategory _category = TicketCategoryService.getInstance( ).findCategoryById( nId );

        List<Integer> listInputs = _category.getListIdInput( );
        int i = 1;

        for ( Integer input : listInputs )
        {
            TicketCategoryInputsHome.updateCategoryInputPosition( nId, input, i++ );
        }
    }

    /**
     * Update the category input position
     *
     * @param nIdCategory
     *            id ticket category
     * @param nIdInput
     *            id input
     * @param bMoveUp
     *            boolean true if up otherwise false
     */
    public void updateCategoryInputPosition( int nIdCategory, int nIdInput, boolean bMoveUp )
    {
        int nOldPosition = TicketCategoryInputsHome.getCategoryInputPosition( nIdCategory, nIdInput );
        int nNewPosition = bMoveUp ? ( nOldPosition - 1 ) : ( nOldPosition + 1 );

        int nInputToInversePosition = TicketCategoryInputsHome.getCategoryInputByPosition( nIdCategory, nNewPosition );

        // Update the Input with new Position
        TicketCategoryInputsHome.updateCategoryInputPosition( nIdCategory, nIdInput, nNewPosition );

        // Update the Input that was on that position before
        TicketCategoryInputsHome.updateCategoryInputPosition( nIdCategory, nInputToInversePosition, nOldPosition );

        TicketCategoryTreeCacheService.getInstance( ).reloadResource( );
    }

    /**
     * Get the branch of a category (from root to category)
     *
     * @param category
     *            category
     * @return the branch of categories from root to category
     */
    public List<TicketCategory> getBranchOfCategory( TicketCategory category )
    {
        return _treeCategories.getBranch( category );
    }

    /**
     * Get the branch of a category id (from root to category)
     *
     * @param nId
     *            id
     * @return the branch of categories from root to category
     */
    public List<TicketCategory> getBranchOfCategoryId( int nId )
    {
        return _treeCategories.getBranch( findCategoryById( nId ) );
    }

    /**
     * Get the category list corresponding to the depth
     *
     * @param depth
     *            depth
     * @return the category list
     */
    public List<TicketCategory> getCategoryListOfDepth( TicketCategoryType depth )
    {
        return _treeCategories.getListNodesOfDepth( depth );
    }

    /**
     * Get the referenceList of a ticket category list
     *
     * @param ticketCategoryList
     *            ticket category list
     * @return ReferenceList
     */
    public ReferenceList getReferenceList( List<TicketCategory> ticketCategoryList )
    {
        ReferenceList list = new ReferenceList( );

        ticketCategoryList.forEach( ticketCategory -> list.addItem( ticketCategory.getId( ), ticketCategory.getLabel( ) ) );

        return list;
    }

    /**
     * Check if the given category type is not referenced in category
     *
     * @param nIdCategoryType
     *            Id Category Type
     * @return true if referenced, false otherwise
     */
    public boolean isCategoryTypeNotReferenced( int nIdCategoryType )
    {
        return TicketCategoryTreeCacheService.getInstance( ).getResource( ).getListNodesOfDepth( TicketCategoryTypeHome.findByPrimaryKey( nIdCategoryType ) ).size( ) == 0;
    }

    ////////
    // FOR TICKETING DOMAIN/TYPE/CATEGORY

    public TicketCategory getDepth( TicketCategory ticketCategory, int depth )
    {
        try
        {
            TicketCategory domainCategory = ticketCategory.getBranch( ).get( depth );
            return domainCategory;
        } catch ( IndexOutOfBoundsException e )
        {
            return null;
        }
    }

    /**
     * Return the ticket type
     *
     * @param ticketCategory
     *            the category of a ticket
     * @return the type of a ticket
     */
    public TicketCategory getType( TicketCategory ticketCategory )
    {
        return getDepth( ticketCategory, TicketingConstants.TYPE_DEPTH - 1 );
    }

    /**
     * Return the ticket domain
     *
     * @param ticketCategory
     *            the category of a ticket
     * @return the ticket domain
     */
    public TicketCategory getDomain( TicketCategory ticketCategory )
    {
        return getDepth( ticketCategory, TicketingConstants.DOMAIN_DEPTH - 1 );
    }

    /**
     * Return the ticket thematic
     *
     * @param ticketCategory
     *            the thematic of a ticket
     * @return the thematic of a ticket
     */
    public TicketCategory getThematic( TicketCategory ticketCategory )
    {
        try
        {
            TicketCategory categoryCategory = ticketCategory.getBranch( ).get( TicketingConstants.THEMATIC_DEPTH - 1 );
            return categoryCategory;
        } catch ( IndexOutOfBoundsException e )
        {
            return null;
        }
    }

    /**
     * Return the ticket category precision
     *
     * @param ticketCategory
     *            the category precision of a ticket
     * @return the ticket category precision
     */
    public TicketCategory getPrecision( TicketCategory ticketCategory )
    {
        try
        {
            TicketCategory precisionCategory = ticketCategory.getBranch( ).get( TicketingConstants.PRECISION_DEPTH - 1 );
            return precisionCategory;
        } catch ( IndexOutOfBoundsException e )
        {
            return null;
        }
    }

    /**
     * Check if given category is Authorized for given user and given permission
     *
     * @param category
     *            category
     * @param user
     *            user
     * @param strPermission
     *            permission
     * @return true if authorized, false otherwise
     */
    public static boolean isAuthorizedBranch( TicketCategory category, AdminUser user, String strPermission )
    {
        List<TicketCategory> listTicketCategory = TicketCategoryService.getInstance( ).getBranchOfCategory( category );
        return listTicketCategory.stream( ).allMatch( cat -> isAuthorizedCategory( cat, user, strPermission ) );
    }

    /**
     * Check if given category is Authorized for given user and given permission
     *
     * @param category
     *            category
     * @param user
     *            user
     * @param strPermission
     *            permission
     * @return true if authorized, false otherwise
     */
    public static boolean isAuthorizedCategory( TicketCategory category, AdminUser user, String strPermission )
    {
        return !category.isManageable( ) || RBACService.isAuthorized( category, strPermission, user );
    }

    /**
     * Get the children category list corresponding to the category and allowed for an admin user according to RBAC provided permission
     *
     * @param ticketCategories
     *            ticket Categories
     * @param user
     *            admin user
     * @param strPermission
     *            TicketCategory permission
     * @return the category list filtered by RBAC permission
     */
    public List<TicketCategory> filterAuthorizedCategoryList( List<TicketCategory> ticketCategories, AdminUser user, String strPermission )
    {
        return ticketCategories.stream( ).filter( cat -> isAuthorizedCategory( cat, user, strPermission ) ).collect( Collectors.toList( ) );
    }

    /**
     * Get the children category list corresponding to the category and allowed for an admin user according to RBAC provided permission
     *
     * @param ticketCategory
     *            ticket Category
     * @param adminUser
     *            admin user
     * @param strPermission
     *            TicketCategory permission
     * @return the category list filtered by RBAC permission
     */
    public List<TicketCategory> getAuthorizedCategoryList( TicketCategory ticketCategory, AdminUser adminUser, String strPermission )
    {
        return filterAuthorizedCategoryList( ticketCategory.getChildren( ), adminUser, strPermission );
    }

    /**
     * Get the category list corresponding to the depth and allowed for an admin user according to RBAC provided permission
     *
     * @param nDepth
     *            depth
     *
     * @param adminUser
     *            admin user
     * @param strPermission
     *            TicketCategory permission
     * @return the category list filtered by RBAC permission
     */
    public List<TicketCategory> getAuthorizedCategoryList( int nDepth, AdminUser adminUser, String strPermission )
    {
        List<TicketCategory> listCategories = _treeCategories.getListNodesOfDepth( TicketCategoryTypeService.getInstance( true ).findByDepthNumber( nDepth ) );

        return filterAuthorizedCategoryList( listCategories, adminUser, strPermission );
    }

    /**
     * Get the reference list of inputs not already linked to a given Category
     *
     * @param category
     *            The ticket category
     * @return The reference list of inputs
     */
    public ReferenceList getFilteredRefListInputs( TicketCategory category )
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listReferenceEntry = EntryHome.getEntryList( entryFilter );
        Set<Entry> listExistingEntries = new HashSet<>( );
        listExistingEntries.addAll( getCategoryEntryList( category ) );
        listExistingEntries.addAll( getCategoryEntryHeritedList( category, Locale.getDefault( ) ) );
        listExistingEntries.addAll( getCategoryEntryBlockedList( category, Locale.getDefault( ) ) );
        ReferenceList refListInputs = new ReferenceList( );

        for ( Entry entry : listReferenceEntry )
        {
            boolean b_found = false;

            for ( Entry existingEntry : listExistingEntries )
            {
                if ( existingEntry.getIdResource( ) == entry.getIdResource( ) )
                {
                    b_found = true;
                }
            }

            if ( !b_found )
            {
                refListInputs.addItem( entry.getIdResource( ), buildItemComboInput( entry ) );
            }
        }

        return refListInputs;
    }

    /**
     * Build item present in the inputs list combo for each input with the input title and the input type. Except for type comment having not title. For it, the item combo is build with the technical id
     *
     * @param entry
     *            The current entry
     * @return The item present in the input list combo
     */
    private String buildItemComboInput( Entry entry )
    {
        StringBuilder itemComboInput;

        if ( entry.getEntryType( ).getComment( ) )
        {
            itemComboInput = new StringBuilder( entry.getCode( ) );
        } else
        {
            itemComboInput = new StringBuilder( entry.getTitle( ) );
        }

        itemComboInput.append( " (" ).append( entry.getEntryType( ).getTitle( ) ).append( ")" );

        return itemComboInput.toString( );
    }

    /**
     * Return a list of Entries linked to a category
     *
     * @param _category
     *            category
     * @return list of Entries linked to a category
     */
    public List<Entry> getCategoryEntryList( TicketCategory _category )
    {
        List<Entry> listEntry = new ArrayList<Entry>( );

        if ( ( _category != null ) && ( _category.getListIdInput( ) != null ) )
        {
            EntryFilter entryFilter = new EntryFilter( );
            entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
            entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
            entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

            for ( Integer nIdInput : _category.getListIdInput( ) )
            {
                entryFilter.setIdResource( nIdInput );

                List<Entry> listEntryFound = EntryHome.getEntryList( entryFilter );

                if ( ( listEntryFound != null ) && ( listEntryFound.size( ) >= 1 ) )
                {
                    listEntry.add( listEntryFound.get( 0 ) );
                }
            }
        }

        return listEntry;
    }

    /**
     * Return a list of Entries herited from parent categories
     *
     * @param _category
     *            category
     * @param locale
     *            locale
     * @return list of Entries herited from parent categories
     */
    public List<Entry> getCategoryEntryHeritedList( TicketCategory _category, Locale locale )
    {
        List<Entry> listEntry = new ArrayList<Entry>( );

        if ( _category != null )
        {
            _category.getBranch( ).stream( ).forEach( cat ->
            {
                if ( cat.getId( ) != _category.getId( ) )
                {
                    List<Entry> listEntryOfCategory = getCategoryEntryList( cat );
                    listEntryOfCategory.stream( ).forEach( entry -> entry.setErrorMessage( I18nService.getLocalizedString( KEY_HERITED_MESSAGE, locale ) + getBranchLabel( cat, " -> " ) ) );

                    listEntry.addAll( listEntryOfCategory );

                }
            } );
        }

        return listEntry;
    }

    /**
     * Return a list of Entries bocked by children categories
     *
     * @param _category
     *            category
     * @param locale
     *            locale
     * @return ist of Entries bocked by children categories
     */
    public List<Entry> getCategoryEntryBlockedList( TicketCategory _category, Locale locale )
    {
        List<Entry> listEntry = new ArrayList<Entry>( );

        if ( _category != null )
        {
            _category.getChildren( ).stream( ).forEach( cat ->
            {
                List<Entry> listEntryOfCategory = getCategoryEntryList( cat );
                listEntryOfCategory.stream( ).forEach( entry -> entry.setErrorMessage( I18nService.getLocalizedString( KEY_BLOCKED_MESSAGE, locale ) + getBranchLabel( cat, " -> " ) ) );
                listEntryOfCategory.addAll( getCategoryEntryBlockedList( cat, locale ) );

                listEntry.addAll( listEntryOfCategory );

            } );
        }

        return listEntry;
    }

    /**
     * Get the type list
     *
     * @return the type list
     */
    public List<TicketCategory> getTypeList( )
    {
        return _treeCategories.getListNodesOfDepth( TicketCategoryTypeService.getInstance( ).findByDepthNumber( TicketingConstants.TYPE_DEPTH ) );
    }

    /**
     * Get the domain list
     *
     * @return the domain list
     */
    public List<TicketCategory> getDomainList( )
    {
        return _treeCategories.getListNodesOfDepth( TicketCategoryTypeService.getInstance( true ).findByDepthNumber( TicketingConstants.DOMAIN_DEPTH ) );
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
    public List<TicketCategory> getAuthorizedDomainsList( AdminUser adminUser, String strPermission )
    {
        return getAuthorizedCategoryList( TicketingConstants.DOMAIN_DEPTH, adminUser, strPermission );
    }

    /**
     * Get the branch label of the category
     *
     * @param category
     *            admin user
     * @param separator
     *            separator
     * @return the branch label
     */
    public String getBranchLabel( TicketCategory category, String separator )
    {
        List<TicketCategory> branch = _treeCategories.getBranch( category );
        StringBuilder branchLabel = new StringBuilder( );
        branch.stream( ).forEach( ( node ) -> branchLabel.append( node.getLabel( ) ).append( separator ) );

        return branchLabel.substring( 0, branchLabel.length( ) - separator.length( ) );
    }

    /**
     * Returns the TicketCategory used as RBACResource
     *
     * @param ticketCategory
     *            ticket Category
     *
     * @return The TicketCategory used as RBACResource
     */
    public TicketCategory getTicketCategoryRBACResource( TicketCategory ticketCategory )
    {
        try
        {
            TicketCategory ticketCategoryRBACResource = ticketCategory.getBranch( ).get( TicketingConstants.CATEGORY_DEPTH_RBAC_RESOURCE - 1 );
            return ticketCategoryRBACResource;
        } catch ( IndexOutOfBoundsException e )
        {
            return null;
        }
    }

    public List<TicketCategory> getAllCategories( )
    {
        return _treeCategories.getNodes( );
    }
}
