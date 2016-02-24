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
package fr.paris.lutece.plugins.ticketing.web;

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.Map;


/**
 * Class providing helper for Ticket web management
 *
 */
public final class TicketHelper
{
    /**
     * Default constructor
     */
    private TicketHelper(  )
    {
    }

    /**
     * store user rights in model
     * @param model model to store rights
     * @param adminUser user
     */
    public static void storeTicketRightsIntoModel( Map<String, Object> model, AdminUser adminUser )
    {
        if ( RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_CREATION_RIGHT, Boolean.TRUE );
        }

        if ( RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_DELETE, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_DELETION_RIGHT, Boolean.TRUE );
        }

        if ( RBACService.isAuthorized( new Ticket(  ), TicketResourceIdService.PERMISSION_MODIFY, adminUser ) )
        {
            model.put( TicketingConstants.MARK_TICKET_MODIFICATION_RIGHT, Boolean.TRUE );
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
}
