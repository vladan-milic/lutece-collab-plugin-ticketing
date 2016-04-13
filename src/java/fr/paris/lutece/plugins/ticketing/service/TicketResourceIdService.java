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
package fr.paris.lutece.plugins.ticketing.service;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;


/**
 *
 * TicketResourceIdService
 *
 */
public class TicketResourceIdService extends ResourceIdService
{
    /** Permission for creating a ticket */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for modifying a ticket */
    public static final String PERMISSION_MODIFY = "MODIFY";

    /** Permission for deleting a ticket */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for deleting a ticket */
    public static final String PERMISSION_VIEW = "VIEW";

    /** Permission for self assigning a ticket */
    public static final String PERMISSION_SELF_ASSIGN = "SELF_ASSIGN";

    /** Permission for assigning a ticket to a unit */
    public static final String PERMISSION_ASSIGN_TO_UNIT = "ASSIGN_TO_UNIT";

    /** Permission for assigning a ticket to a user */
    public static final String PERMISSION_ASSIGN_TO_USER = "ASSIGN_TO_USER";

    /** Permission for assigning up a ticket */
    public static final String PERMISSION_ASSIGN_UP_LEVEL = "ASSIGN_UP_LEVEL";

    /** Permission for qualifying a ticket */
    public static final String PERMISSION_QUALIFY = "QUALIFY";

    /** Permission for returnin a ticket to assigner */
    public static final String PERMISSION_RETURN_TO_ASSIGNER = "RETURN_TO_ASSIGNER";

    /** Permission for responding to assign up of a ticket */
    public static final String PERMISSION_RESPOND_ASSIGN_UP_LEVEL = "RESPOND_ASSIGN_UP_LEVEL";

    /** Permission for responding to a user for a ticket */
    public static final String PERMISSION_RESPOND_TO_USER = "RESPOND_TO_USER";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "ticketing.ticket.resourceType";
    private static final String PROPERTY_LABEL_CREATE = "ticketing.ticket.permission.label.create";
    private static final String PROPERTY_LABEL_MODIFY = "ticketing.ticket.permission.label.modify";
    private static final String PROPERTY_LABEL_DELETE = "ticketing.ticket.permission.label.delete";
    private static final String PROPERTY_LABEL_VIEW = "ticketing.ticket.permission.label.view";
    private static final String PROPERTY_LABEL_SELF_ASSIGN = "ticketing.ticket.permission.label.selfAssign";
    private static final String PROPERTY_LABEL_ASSIGN_TO_UNIT = "ticketing.ticket.permission.label.assignToUnit";
    private static final String PROPERTY_LABEL_ASSIGN_TO_USER = "ticketing.ticket.permission.label.assignToUser";
    private static final String PROPERTY_LABEL_ASSIGN_UP_LEVEL = "ticketing.ticket.permission.label.assignUpLevel";
    private static final String PROPERTY_LABEL_QUALIFY = "ticketing.ticket.permission.label.qualify";
    private static final String PROPERTY_LABEL_RETURN_TO_ASSIGNER = "ticketing.ticket.permission.label.returnToAssigner";
    private static final String PROPERTY_LABEL_RESPOND_TO_USER = "ticketing.ticket.permission.label.respondToUser";
    private static final String PROPERTY_LABEL_RESPOND_ASSIGN_UP_LEVEL = "ticketing.ticket.permission.label.respondAssignUpLevel";

    /**
     * Constructor
     */
    public TicketResourceIdService(  )
    {
        setPluginName( TicketingPlugin.PLUGIN_NAME );
    }

    /**
     * Initializes the service
     */
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( TicketResourceIdService.class.getName(  ) );
        rt.setPluginName( TicketingPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( Ticket.TICKET_RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission(  );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_VIEW );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_SELF_ASSIGN );
        p.setPermissionTitleKey( PROPERTY_LABEL_SELF_ASSIGN );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_ASSIGN_TO_UNIT );
        p.setPermissionTitleKey( PROPERTY_LABEL_ASSIGN_TO_UNIT );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_ASSIGN_TO_USER );
        p.setPermissionTitleKey( PROPERTY_LABEL_ASSIGN_TO_USER );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_ASSIGN_UP_LEVEL );
        p.setPermissionTitleKey( PROPERTY_LABEL_ASSIGN_UP_LEVEL );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_QUALIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_QUALIFY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_RETURN_TO_ASSIGNER );
        p.setPermissionTitleKey( PROPERTY_LABEL_RETURN_TO_ASSIGNER );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_RESPOND_TO_USER );
        p.setPermissionTitleKey( PROPERTY_LABEL_RESPOND_TO_USER );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_RESPOND_ASSIGN_UP_LEVEL );
        p.setPermissionTitleKey( PROPERTY_LABEL_RESPOND_ASSIGN_UP_LEVEL );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of resource ids
     * @param locale The current locale
     * @return A list of resource ids
     */
    public ReferenceList getResourceIdList( Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle( String strId, Locale locale )
    {
        return StringUtils.EMPTY;
    }
}
