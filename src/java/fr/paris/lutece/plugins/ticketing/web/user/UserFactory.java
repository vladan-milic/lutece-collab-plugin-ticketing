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
package fr.paris.lutece.plugins.ticketing.web.user;

import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;

/**
 * Factory of User. Designed as a singleton
 *
 */
public final class UserFactory
{
    private static UserFactory _instance;

    /**
     * Default constructor
     */
    private UserFactory( )
    {
    }

    /**
     * Creates a User
     *
     * @param nIdUser
     *            the idUser used to select the correct AdminUser
     * @return the User
     */
    public User create( int nIdUser )
    {
        User user = new User( );

        AdminUser adminUser = AdminUserHome.findByPrimaryKey( nIdUser );

        if ( adminUser == null )
        {
            return null;
        }

        adminUser.setRoles( AdminUserHome.getRolesListForUser( adminUser.getUserId( ) ) );

        List<TicketCategory> listDomains = TicketCategoryService.getInstance( ).getAuthorizedCategoryList( TicketingConstants.CATEGORY_DEPTH_RBAC_RESOURCE,
                adminUser, TicketCategory.PERMISSION_BELONG_TO );

        List<Unit> listUnits = UnitHome.findByIdUser( nIdUser );

        user.setIdUser( adminUser.getUserId( ) );

        user.setFirstName( adminUser.getFirstName( ) );

        user.setLastName( adminUser.getLastName( ) );

        user.setEmail( adminUser.getEmail( ) );

        user.setUnit( listUnits );

        user.setDomains( listDomains );

        return user;
    }

    /**
     * Creates a UserFactory
     *
     * @return The UserFactory
     */
    public static UserFactory getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new UserFactory( );
        }

        return _instance;
    }
}
