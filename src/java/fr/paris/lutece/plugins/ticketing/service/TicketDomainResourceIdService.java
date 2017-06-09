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
package fr.paris.lutece.plugins.ticketing.service;

import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Locale;

/**
 *
 * TicketDomainResourceIdService
 *
 */
public class TicketDomainResourceIdService extends ResourceIdService
{
    /** Permission for viewing a ticket domain */
    public static final String PERMISSION_VIEW_LIST = "VIEW_LIST";
    public static final String PERMISSION_VIEW_DETAIL = "VIEW_DETAIL";
    public static final String PERMISSION_BELONG_TO = "BELONG_TO";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "ticketing.ticketdomain.resourceType";
    private static final String PROPERTY_LABEL_VIEW_LIST = "ticketing.ticketdomain.permission.label.viewList";
    private static final String PROPERTY_LABEL_VIEW_DETAIL = "ticketing.ticketdomain.permission.label.viewDetail";
    private static final String PROPERTY_LABEL_BELONG_TO = "ticketing.ticketdomain.permission.label.belongTo";

    /** Constants */
    private static final String SEPARATOR = " - ";

    /**
     * Constructor
     */
    public TicketDomainResourceIdService( )
    {
        setPluginName( TicketingPlugin.PLUGIN_NAME );
    }

    /**
     * Initializes the service
     */
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( TicketDomainResourceIdService.class.getName( ) );
        rt.setPluginName( TicketingPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( TicketDomain.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERMISSION_VIEW_LIST );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW_LIST );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_VIEW_DETAIL );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW_DETAIL );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_BELONG_TO );
        p.setPermissionTitleKey( PROPERTY_LABEL_BELONG_TO );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of resource ids
     * 
     * @param locale
     *            The current locale
     * @return A list of resource ids
     */
    public ReferenceList getResourceIdList( Locale locale )
    {
        ReferenceList list = new ReferenceList( );

        for ( TicketDomain domain : TicketDomainHome.getTicketDomainsList( true ) )
        {
            list.addItem( domain.getId( ), ( domain.getTicketType( ) + SEPARATOR + domain.getLabel( ) ) );
        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle( String strId, Locale locale )
    {
        TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( Integer.parseInt( strId ) );

        return ticketDomain.getLabel( );
    }
}
