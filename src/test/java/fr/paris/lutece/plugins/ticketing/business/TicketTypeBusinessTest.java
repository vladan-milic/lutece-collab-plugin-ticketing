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
package fr.paris.lutece.plugins.ticketing.business;

import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.test.LuteceTestCase;

public class TicketTypeBusinessTest extends LuteceTestCase
{
    private final static String LABEL1 = "Label1";
    private final static String LABEL2 = "Label2";
    private final static String REFERENCE1 = "RF1";
    private final static String REFERENCE2 = "RF2";

    public void testBusiness( )
    {
        // Initialize an object
        TicketType ticketType = new TicketType( );
        ticketType.setLabel( LABEL1 );
        ticketType.setReferencePrefix( REFERENCE1 );

        // Create test
        TicketTypeHome.create( ticketType );

        TicketType ticketTypeStored = TicketTypeHome.findByPrimaryKey( ticketType.getId( ) );
        assertEquals( ticketTypeStored.getLabel( ), ticketType.getLabel( ) );
        assertEquals( ticketTypeStored.getReferencePrefix( ), ticketType.getReferencePrefix( ) );

        // Update test
        ticketType.setLabel( LABEL2 );
        ticketType.setReferencePrefix( REFERENCE2 );
        TicketTypeHome.update( ticketType );
        ticketTypeStored = TicketTypeHome.findByPrimaryKey( ticketType.getId( ) );
        assertEquals( ticketTypeStored.getLabel( ), ticketType.getLabel( ) );
        assertEquals( ticketTypeStored.getReferencePrefix( ), ticketType.getReferencePrefix( ) );

        // List test
        TicketTypeHome.getTicketTypesList( );

        // Delete test
        TicketTypeHome.remove( ticketType.getId( ) );
        ticketTypeStored = TicketTypeHome.findByPrimaryKey( ticketType.getId( ) );
        assertNotNull( ticketTypeStored );
    }
}
