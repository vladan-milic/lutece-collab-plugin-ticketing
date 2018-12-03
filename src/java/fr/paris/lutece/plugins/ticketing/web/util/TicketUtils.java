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
package fr.paris.lutece.plugins.ticketing.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.prefs.AdminUserPreferencesService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * Class providing helper for Ticket web management
 *
 */
public final class TicketUtils
{
    /**
     * Default constructor
     */
    private TicketUtils( )
    {
    }

    /**
     * Registers the admin user for front office
     *
     * @param request
     *            the request
     * @return admin user
     */
    public static AdminUser registerAdminUserFront( HttpServletRequest request )
    {
        AdminUser userFront = AdminUserHome.findByPrimaryKey( PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ADMINUSER_ID_FRONT, TicketingConstants.PROPERTY_UNSET_INT ) );

        try
        {
            AdminAuthenticationService.getInstance( ).registerUser( request, userFront );

            // Gets the user from request because registerUser initializes roles, etc.
            userFront = AdminAuthenticationService.getInstance( ).getRegisteredUser( request );
        } catch ( AccessDeniedException e )
        {
            AppLogService.error( e.getMessage( ), e );
        } catch ( UserNotSignedException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return userFront;
    }

    /**
     * Unregisters the admin user for front office
     *
     * @param request
     *            the request
     */
    public static void unregisterAdminUserFront( HttpServletRequest request )
    {
        AdminAuthenticationService.getInstance( ).unregisterUser( request );
    }

    /**
     * Creates a ReferenceList object initialized with one item
     *
     * @param strLabel
     *            the label for the item
     * @param nCode
     *            the int used as a code for the item
     * @return a ReferenceList object initialized with one item
     */
    public static ReferenceList createReferenceList( String strLabel, int nCode )
    {
        return createReferenceList( strLabel, String.valueOf( nCode ) );
    }

    /**
     * Creates a ReferenceList object initialized with one item
     *
     * @param strLabel
     *            the label for the item
     * @param strCode
     *            the code for the item
     * @return a ReferenceList object initialized with one item
     */
    public static ReferenceList createReferenceList( String strLabel, String strCode )
    {
        ReferenceItem refItemEmpty = new ReferenceItem( );
        refItemEmpty.setCode( strCode );
        refItemEmpty.setName( strLabel );

        ReferenceList listRefEmpty = new ReferenceList( );
        listRefEmpty.add( refItemEmpty );

        return listRefEmpty;
    }

    /**
     * returns true if ticket is assign to user's group
     *
     * @param ticket
     *            ticket
     * @param lstUserUnits
     *            user's units
     * @return true if ticket belongs to user's group
     */
    public static boolean isTicketAssignedToUserGroup( Ticket ticket, List<Unit> lstUserUnits )
    {
        boolean result = false;

        for ( Unit unit : lstUserUnits )
        {
            if ( ( ticket.getAssigneeUnit( ) != null ) && ( unit.getIdUnit( ) == ticket.getAssigneeUnit( ).getUnitId( ) ) )
            {
                result = true;

                break;
            }
        }

        return result;
    }

    /**
     * returns true if ticket is assigned to a child unit of user's group
     *
     * @param ticket
     *            ticket
     * @param lstUserUnits
     *            user's units
     * @return true if ticket is assigned to a child unit of user's group
     */
    public static boolean isTicketAssignedToChildUnitUserGroup( Ticket ticket, List<Unit> lstUserUnits )
    {
        boolean result = false;

        for ( Unit unit : lstUserUnits )
        {
            if ( ticket.getAssigneeUnit( ) != null )
            {
                Unit currentUnit = UnitHome.findByPrimaryKey( ticket.getAssigneeUnit( ).getUnitId( ) );

                result = isParentUnit( unit, currentUnit );

                if ( result )
                {
                    break;
                }
            }
        }

        return result;
    }

    /**
     * returns true if the current unit is the parent unit
     *
     * @param parentUnit
     *            parent unit
     * @param currentUnit
     *            current unit
     * @return true if the current unit is the parent unit
     */
    public static boolean isParentUnit( Unit parentUnit, Unit currentUnit )
    {
        boolean result = false;

        if ( ( parentUnit == null ) || ( currentUnit == null ) )
        {
            result = false;
        } else
        {
            if ( parentUnit.getIdUnit( ) == currentUnit.getIdParent( ) )
            {
                result = true;
            } else
            {
                if ( TicketingConstants.NO_PARENT_ID != currentUnit.getIdParent( ) )
                {
                    Unit currentParentUnit = UnitHome.findByPrimaryKey( currentUnit.getIdParent( ) );
                    result = isParentUnit( parentUnit, currentParentUnit );
                }
            }
        }

        return result;
    }

    /**
     * returns true if ticket is assign up from user's group
     *
     * @param ticket
     *            ticket
     * @param lstUserUnits
     *            user's units
     * @return true if ticket belongs to user's group
     */
    public static boolean isTicketAssignedUpFromUserGroup( Ticket ticket, List<Unit> lstUserUnits )
    {
        boolean result = false;

        for ( Unit unit : lstUserUnits )
        {
            if ( ( ticket.getAssignerUnit( ) != null ) && ( unit.getIdUnit( ) == ticket.getAssignerUnit( ).getUnitId( ) ) )
            {
                result = true;

                break;
            }
        }

        return result;
    }

    /**
     * get list of ticket id according to filter
     *
     * @param filter
     *            ticket filter
     * @return list of ticket id according to filter
     */
    public static List<Integer> getIdTickets( TicketFilter filter )
    {
        return TicketHome.getIdTicketsList( filter );
    }

    /**
     * get list of ticket according to filter
     *
     * @param filter
     *            ticket filter
     * @return list of ticket according to filter
     */
    public static List<Ticket> getTickets( TicketFilter filter )
    {
        return TicketHome.getTicketsList( filter );
    }

    /**
     * returns true if ticket is assign to user or group, false otherwise
     *
     * @param user
     *            adminUser
     * @param ticket
     *            ticket
     * @return true if ticket is assign to user or group
     */
    public static boolean isTicketAssignToUserOrGroup( AdminUser user, Ticket ticket )
    {
        boolean bAssignToUserOrGroup = false;

        if ( ( ticket.getAssigneeUser( ) != null ) && ( ticket.getAssigneeUser( ).getAdminUserId( ) == user.getUserId( ) ) )
        {
            // ticket assign to agent
            bAssignToUserOrGroup = true;
        } else if ( isTicketAssignedToUserGroup( ticket, UnitHome.findByIdUser( user.getUserId( ) ) ) )
        {
            // ticket assign to agent group
            bAssignToUserOrGroup = true;
        } else if ( isTicketAssignedToChildUnitUserGroup( ticket, UnitHome.findByIdUser( user.getUserId( ) ) ) )
        {
            // ticket assign to child unit of agent group
            bAssignToUserOrGroup = true;
        }

        return bAssignToUserOrGroup;
    }

    /**
     * Get list of id from a comma separated string
     *
     * @param strIdList
     *            list of the user
     * @return list of channel id of the user.
     */
    public static List<Integer> extractListIdFromString( String strIdList )
    {
        List<Integer> listId = new ArrayList<Integer>( );

        if ( StringUtils.isNotEmpty( strIdList ) )
        {
            StringTokenizer st = new StringTokenizer( strIdList, TicketingConstants.FIELD_ID_SEPARATOR );

            while ( st.hasMoreElements( ) )
            {
                listId.add( Integer.parseInt( st.nextToken( ) ) );
            }
        }

        return listId;
    }

    /**
     * Get list of selectable channels of the user
     *
     * @param request
     *            http request
     * @return list of selectable channels of the user.
     */
    public static ReferenceList getSelectableChannelsList( HttpServletRequest request )
    {
        ReferenceList channelList = ChannelHome.getReferenceList( );

        String strIdSelectableChannelList = AdminUserPreferencesService.instance( ).get( String.valueOf( AdminUserService.getAdminUser( request ).getUserId( ) ),
                TicketingConstants.USER_PREFERENCE_CHANNELS_LIST, StringUtils.EMPTY );

        List<Integer> idSelectableChannelList = extractListIdFromString( strIdSelectableChannelList );
        Map<String, String> selectableChannelsMap = new HashMap<String, String>( );
        Map<String, String> channelsMap = channelList.toMap( );

        for ( Integer channelId : idSelectableChannelList )
        {
            // check that channelId has not been removed and exists in channelList
            if ( channelsMap.get( String.valueOf( channelId ) ) != null )
            {
                selectableChannelsMap.put( String.valueOf( channelId ), channelsMap.get( String.valueOf( channelId ) ) );
            }
        }

        return ReferenceList.convert( selectableChannelsMap );
    }

    /**
     * Tests whether the id is set or not
     *
     * @param nId
     *            the id to test
     * @return {@code true} if the id is set, {@code false} otherwise
     */
    public static boolean isIdSet( int nId )
    {
        return ( nId != TicketingConstants.PROPERTY_UNSET_INT ) && ( nId != 0 );
    }

    /**
     * Tests if the specified ticket is assigned to the specified user
     *
     * @param ticket
     *            the ticket
     * @param user
     *            the user
     * @return {@code true} if the ticket is assigned to the user, {@code false} otherwise
     */
    public static boolean isAssignee( Ticket ticket, AdminUser user )
    {
        return ( ticket.getAssigneeUser( ) != null ) && ( ticket.getAssigneeUser( ).getAdminUserId( ) == user.getUserId( ) );
    }

    /**
     * Tests if the specified user is the assigner of the specified ticket
     *
     * @param ticket
     *            the ticket
     * @param user
     *            the user
     * @return {@code true} if the user is the assigner of the ticket, {@code false} otherwise
     */
    public static boolean isAssigner( Ticket ticket, AdminUser user )
    {
        return ( ticket.getAssignerUser( ) != null ) && ( ticket.getAssignerUser( ).getAdminUserId( ) == user.getUserId( ) );
    }

    /**
     * Return the parsing of the String into Integer or -1 if fails
     *
     * @param strStringToConvert
     * @param strMessageError
     *            the error message
     * @return the parsing of the String or -1 if fails
     */
    public static int manageIntegerParsingFromString( String strStringToConvert, String strMessageError )
    {
        try
        {
            return Integer.parseInt( strStringToConvert );
        } catch ( NumberFormatException e )
        {
            if ( StringUtils.isNotBlank( strMessageError ) )
            {
                AppLogService.error( strMessageError );
            }
            return TicketingConstants.PROPERTY_UNSET_INT;
        }
    }

    /**
     * Check if a ticket is authorized
     *
     * @param ticket
     * @param strPermission
     * @param user
     * @return boolean is authorized
     */
    public static boolean isAuthorized( Ticket ticket, String strPermission, AdminUser user )
    {
        boolean ticketAuthorized = RBACService.isAuthorized( ticket, TicketResourceIdService.PERMISSION_VIEW, user );
        boolean categoriesBranchAuthorized = TicketCategoryService.isAuthorizedBranch( ticket.getTicketCategory( ), user, strPermission );

        return ticketAuthorized && categoriesBranchAuthorized;
    }
}
