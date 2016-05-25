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

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.test.LuteceTestCase;


public class TicketBusinessTest extends LuteceTestCase
{
    private final static String REFERENCE1 = "INF20000100001";
    private final static String REFERENCE2 = "INF20000100002";
    private final static String GUID1 = "1234567";
    private final static String GUID2 = "9876543";
    private final static int IDUSERTITLE1 = 1;
    private final static int IDUSERTITLE2 = 2;
    private final static String USERTITLE2 = "UserTitle2";
    private final static String FIRSTNAME1 = "Firstname1";
    private final static String FIRSTNAME2 = "Firstname2";
    private final static String LASTNAME1 = "Lastname1";
    private final static String LASTNAME2 = "Lastname2";
    private final static String EMAIL1 = "Email1";
    private final static String EMAIL2 = "Email2";
    private final static String FIXEDPHONENUMBER1 = "0123456789";
    private final static String FIXEDPHONENUMBER2 = "0198765432";
    private final static String MOBILEPHONENUMBER1 = "0623456789";
    private final static String MOBILEPHONENUMBER2 = "0698765432";
    private final static int IDTICKETCATEGORY1 = 1;
    private final static int IDTICKETCATEGORY2 = 2;
    private final static int CONTACTMODE1 = 1;
    private final static int CONTACTMODE2 = 2;
    private final static String TICKETCOMMENT1 = "TicketComment1";
    private final static String TICKETCOMMENT2 = "TicketComment2";
    private final static int TICKETSTATUS1 = 1;
    private final static int TICKETSTATUS2 = 2;
    private final static String TICKETSTATUSTEXT1 = "TicketStatusText1";
    private final static String TICKETSTATUSTEXT2 = "TicketStatusText2";

    public void testBusiness(  )
    {
        // Initialize an object
        Ticket ticket = new Ticket(  );
        ticket.setReference( REFERENCE1 );
        ticket.setGuid( GUID1 );
        ticket.setIdUserTitle( IDUSERTITLE1 );
        ticket.setFirstname( FIRSTNAME1 );
        ticket.setLastname( LASTNAME1 );
        ticket.setEmail( EMAIL1 );
        ticket.setFixedPhoneNumber( FIXEDPHONENUMBER1 );
        ticket.setMobilePhoneNumber( MOBILEPHONENUMBER1 );
        ticket.setIdTicketCategory( IDTICKETCATEGORY1 );
        ticket.setIdContactMode( CONTACTMODE1 );
        ticket.setTicketComment( TICKETCOMMENT1 );
        ticket.setTicketStatus( TICKETSTATUS1 );
        ticket.setTicketStatusText( TICKETSTATUSTEXT1 );

        // Create test
        TicketHome.create( ticket );

        Ticket ticketStored = TicketHome.findByPrimaryKey( ticket.getId(  ) );
        assertEquals( ticketStored.getReference(  ), ticket.getReference(  ) );
        assertEquals( ticketStored.getGuid(  ), ticket.getGuid(  ) );
        assertEquals( ticketStored.getIdUserTitle(  ), ticket.getIdUserTitle(  ) );
        assertEquals( ticketStored.getFirstname(  ), ticket.getFirstname(  ) );
        assertEquals( ticketStored.getLastname(  ), ticket.getLastname(  ) );
        assertEquals( ticketStored.getEmail(  ), ticket.getEmail(  ) );
        assertEquals( ticketStored.getFixedPhoneNumber(  ), ticket.getFixedPhoneNumber(  ) );
        assertEquals( ticketStored.getMobilePhoneNumber(  ), ticket.getMobilePhoneNumber(  ) );
        assertEquals( ticketStored.getIdTicketCategory(  ), ticket.getIdTicketCategory(  ) );
        assertEquals( ticketStored.getIdContactMode(  ), ticket.getIdContactMode(  ) );
        assertEquals( ticketStored.getTicketComment(  ), ticket.getTicketComment(  ) );
        assertEquals( ticketStored.getTicketStatus(  ), ticket.getTicketStatus(  ) );
        assertEquals( ticketStored.getTicketStatusText(  ), ticket.getTicketStatusText(  ) );

        // Update test
        ticket.setIdUserTitle( IDUSERTITLE2 );
        ticket.setReference( REFERENCE2 );
        ticket.setGuid( GUID2 );
        ticket.setUserTitle( USERTITLE2 );
        ticket.setFirstname( FIRSTNAME2 );
        ticket.setLastname( LASTNAME2 );
        ticket.setEmail( EMAIL2 );
        ticket.setFixedPhoneNumber( FIXEDPHONENUMBER2 );
        ticket.setMobilePhoneNumber( MOBILEPHONENUMBER2 );
        ticket.setIdTicketCategory( IDTICKETCATEGORY2 );
        ticket.setIdContactMode( CONTACTMODE2 );
        ticket.setTicketComment( TICKETCOMMENT2 );
        ticket.setTicketStatus( TICKETSTATUS2 );
        ticket.setTicketStatusText( TICKETSTATUSTEXT2 );
        TicketHome.update( ticket );
        ticketStored = TicketHome.findByPrimaryKey( ticket.getId(  ) );
        assertEquals( ticketStored.getReference(  ), ticket.getReference(  ) );
        assertEquals( ticketStored.getGuid(  ), ticket.getGuid(  ) );
        assertEquals( ticketStored.getIdUserTitle(  ), ticket.getIdUserTitle(  ) );
        assertEquals( ticketStored.getFirstname(  ), ticket.getFirstname(  ) );
        assertEquals( ticketStored.getLastname(  ), ticket.getLastname(  ) );
        assertEquals( ticketStored.getEmail(  ), ticket.getEmail(  ) );
        assertEquals( ticketStored.getFixedPhoneNumber(  ), ticket.getFixedPhoneNumber(  ) );
        assertEquals( ticketStored.getMobilePhoneNumber(  ), ticket.getMobilePhoneNumber(  ) );
        assertEquals( ticketStored.getIdTicketCategory(  ), ticket.getIdTicketCategory(  ) );
        assertEquals( ticketStored.getIdContactMode(  ), ticket.getIdContactMode(  ) );
        assertEquals( ticketStored.getTicketComment(  ), ticket.getTicketComment(  ) );
        assertEquals( ticketStored.getTicketStatus(  ), ticket.getTicketStatus(  ) );
        assertEquals( ticketStored.getTicketStatusText(  ), ticket.getTicketStatusText(  ) );

        // List test
        TicketHome.getTicketsList(  );

        // Delete test
        TicketHome.remove( ticket.getId(  ) );
        ticketStored = TicketHome.findByPrimaryKey( ticket.getId(  ) );
        assertNull( ticketStored );
    }
}
