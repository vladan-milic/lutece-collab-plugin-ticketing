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

import fr.paris.lutece.plugins.ticketing.business.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketFilter;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketDomainResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
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
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;


/**
 * Class providing helper for Ticket web management
 *
 */
public final class TicketUtils
{
    // Properties
    private static final String PROPERTY_ADMINUSER_FRONT_ID = "ticketing.adminUser.front.id";

    /**
     * Default constructor
     */
    private TicketUtils(  )
    {
    }

    /**
     * Registers the admin user for front office
     * @param request the request
     */
    public static void registerDefaultAdminUser( HttpServletRequest request )
    {
        AdminUser defaultUser = AdminUserHome.findByPrimaryKey( AppPropertiesService.getPropertyInt( 
                    PROPERTY_ADMINUSER_FRONT_ID, -1 ) );

        try
        {
            AdminAuthenticationService.getInstance(  ).registerUser( request, defaultUser );
        }
        catch ( AccessDeniedException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( UserNotSignedException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
    }

    /**
     * returns ReferenceList initialized with an Empty Item
     * @param strEmptyLabel label for empty value
     * @param strEmptyCode code for empty value
     * @return   listRefEmpty referenceList initialized with an Empty Item
     */
    public static ReferenceList getEmptyItemReferenceList( String strEmptyLabel, String strEmptyCode )
    {
        ReferenceItem refItemEmpty = new ReferenceItem(  );
        refItemEmpty.setCode( strEmptyCode );
        refItemEmpty.setName( strEmptyLabel );

        ReferenceList listRefEmpty = new ReferenceList(  );
        listRefEmpty.add( refItemEmpty );

        return listRefEmpty;
    }

    /**
     * returns true if ticket is assign to user's group
     * @param ticket ticket
     * @param lstUserUnits user's units
     * @return true if ticket belongs to user's group
     */
    public static boolean isTicketAssignedToUserGroup( Ticket ticket, List<Unit> lstUserUnits )
    {
        boolean result = false;

        for ( Unit unit : lstUserUnits )
        {
            if ( ( ticket.getAssigneeUnit(  ) != null ) &&
                    ( unit.getIdUnit(  ) == ticket.getAssigneeUnit(  ).getUnitId(  ) ) )
            {
                result = true;

                break;
            }
        }

        return result;
    }

    /**
     * returns true if ticket is assign up from user's group
     * @param ticket ticket
     * @param lstUserUnits user's units
     * @return true if ticket belongs to user's group
     */
    public static boolean isTicketAssignedUpFromUserGroup( Ticket ticket, List<Unit> lstUserUnits )
    {
        boolean result = false;

        for ( Unit unit : lstUserUnits )
        {
            if ( ( ticket.getAssignerUnit(  ) != null ) &&
                    ( unit.getIdUnit(  ) == ticket.getAssignerUnit(  ).getUnitId(  ) ) )
            {
                result = true;

                break;
            }
        }

        return result;
    }

    /**
     * @param user admin user
     * @param filter ticket filter
     * @param request http request
     * @param listAgentTickets user's ticket
     * @param listGroupTickets group's ticket
     * @param listDomainTickets domain's ticket
     */
    public static void setTicketsListByPerimeter( AdminUser user, TicketFilter filter, HttpServletRequest request,
        List<Ticket> listAgentTickets, List<Ticket> listGroupTickets, List<Ticket> listDomainTickets )
    {
        List<Ticket> listTickets = (List<Ticket>) TicketHome.getTicketsList( filter );

        List<Unit> lstUserUnits = UnitHome.findByIdUser( user.getUserId(  ) );

        //Filtering results
        for ( Ticket ticket : listTickets )
        {
            if ( RBACService.isAuthorized( ticket, TicketResourceIdService.PERMISSION_VIEW, user ) )
            {
                if ( ( ( ticket.getAssigneeUser(  ) != null ) &&
                        ( ticket.getAssigneeUser(  ).getAdminUserId(  ) == user.getUserId(  ) ) ) ||
                        ( ( ticket.getAssignerUser(  ) != null ) &&
                        ( ticket.getAssignerUser(  ).getAdminUserId(  ) == user.getUserId(  ) ) ) )
                {
                    //ticket assign to agent
                    listAgentTickets.add( ticket );
                }
                else if ( isTicketAssignedToUserGroup( ticket, lstUserUnits ) ||
                        isTicketAssignedUpFromUserGroup( ticket, lstUserUnits ) )
                {
                    //ticket assign to agent group
                    listGroupTickets.add( ticket );
                }
                else
                {
                    TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain(  ) );

                    if ( RBACService.isAuthorized( ticketDomain, TicketDomainResourceIdService.PERMISSION_VIEW, user ) )
                    {
                        //ticket assign to domain
                        listDomainTickets.add( ticket );
                    }
                }
            }
        }
    }

    /**
     * returns true if ticket is assign to user or group, false otherwise
     * @param user adminUser
     * @param ticket ticket
     * @return true if ticket is assign to user or group
     */
    public static boolean isTicketAssignToUserOrGroup( AdminUser user, Ticket ticket )
    {
        boolean bAssignToUserOrGroup = false;

        if ( ( ticket.getAssigneeUser(  ) != null ) &&
                ( ticket.getAssigneeUser(  ).getAdminUserId(  ) == user.getUserId(  ) ) )
        {
            //ticket assign to agent
            bAssignToUserOrGroup = true;
        }
        else if ( isTicketAssignedToUserGroup( ticket, UnitHome.findByIdUser( user.getUserId(  ) ) ) )
        {
            //ticket assign to agent group
            bAssignToUserOrGroup = true;
        }

        return bAssignToUserOrGroup;
    }

    /**
     * Sets the parameter with the specified value
     * @param request the request
     * @param strParameter the parameter
     * @param strValue the value
     */
    public static void setParameter( HttpServletRequest request, String strParameter, String strValue )
    {
        request.getSession(  ).setAttribute( strParameter, strValue );
    }

    /**
     * Gives the value of the specified parameter
     * @param request the request
     * @param strParameter the parameter
     * @return the parameter value
     */
    public static String getParameter( HttpServletRequest request, String strParameter )
    {
        String strRedirectUrl = (String) request.getSession(  ).getAttribute( strParameter );

        // we remove session attribute after consuming it
        request.getSession(  ).removeAttribute( strParameter );

        return strRedirectUrl;
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
        List<Integer> listId = new ArrayList<Integer>(  );
        StringTokenizer st = new StringTokenizer( strIdList, TicketingConstants.FIELD_ID_SEPARATOR );

        while ( st.hasMoreElements(  ) )
        {
            listId.add( Integer.parseInt( st.nextToken(  ) ) );
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
        ReferenceList channelList = ChannelHome.getReferenceList(  );

        String strIdSelectableChannelList = AdminUserPreferencesService.instance(  )
                                                                       .get( String.valueOf( 
                    AdminUserService.getAdminUser( request ).getUserId(  ) ),
                TicketingConstants.USER_PREFERENCE_CHANNELS_LIST, StringUtils.EMPTY );

        List<Integer> idSelectableChannelList = extractListIdFromString( strIdSelectableChannelList );
        Map<String, String> selectableChannelsMap = new HashMap<String, String>(  );
        Map<String, String> channelsMap = channelList.toMap(  );

        for ( Integer channelId : idSelectableChannelList )
        {
            selectableChannelsMap.put( String.valueOf( channelId ), channelsMap.get( channelId ) );
        }

        return ReferenceList.convert( selectableChannelsMap );
    }
}
