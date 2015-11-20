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

import fr.paris.lutece.test.LuteceTestCase;


public class TicketBusinessTest extends LuteceTestCase
{
    private final static int IDUSERTITLE1 = 1;
    private final static int IDUSERTITLE2 = 2;
    private final static String USERTITLE1 = "UserTitle1";
    private final static String USERTITLE2 = "UserTitle2";
    private final static String FIRSTNAME1 = "Firstname1";
    private final static String FIRSTNAME2 = "Firstname2";
    private final static String LASTNAME1 = "Lastname1";
    private final static String LASTNAME2 = "Lastname2";
    private final static String EMAIL1 = "Email1";
    private final static String EMAIL2 = "Email2";
    private final static String PHONENUMBER1 = "PhoneNumber1";
    private final static String PHONENUMBER2 = "PhoneNumber2";
    private final static int IDTICKETTYPE1 = 1;
    private final static int IDTICKETTYPE2 = 2;
    private final static String TICKETTYPE1 = "TicketType1";
    private final static String TICKETTYPE2 = "TicketType2";
    private final static int IDTICKETDOMAIN1 = 1;
    private final static int IDTICKETDOMAIN2 = 2;
    private final static String TICKETDOMAIN1 = "TicketDomain1";
    private final static String TICKETDOMAIN2 = "TicketDomain2";
    private final static int IDTICKETCATEGORY1 = 1;
    private final static int IDTICKETCATEGORY2 = 2;
    private final static String TICKETCATEGORY1 = "TicketCategory1";
    private final static String TICKETCATEGORY2 = "TicketCategory2";
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
        ticket.setIdUserTitle( IDUSERTITLE1 );
        ticket.setUserTitle( USERTITLE1 );
        ticket.setFirstname( FIRSTNAME1 );
        ticket.setLastname( LASTNAME1 );
        ticket.setEmail( EMAIL1 );
        ticket.setPhoneNumber( PHONENUMBER1 );
        ticket.setIdTicketType( IDTICKETTYPE1 );
        ticket.setTicketType( TICKETTYPE1 );
        ticket.setIdTicketDomain( IDTICKETDOMAIN1 );
        ticket.setTicketDomain( TICKETDOMAIN1 );
        ticket.setIdTicketCategory( IDTICKETCATEGORY1 );
        ticket.setTicketCategory( TICKETCATEGORY1 );
        ticket.setTicketComment( TICKETCOMMENT1 );
        ticket.setTicketStatus( TICKETSTATUS1 );
        ticket.setTicketStatusText( TICKETSTATUSTEXT1 );

        // Create test
        TicketHome.create( ticket );

        Ticket ticketStored = TicketHome.findByPrimaryKey( ticket.getId(  ) );
        assertEquals( ticketStored.getIdUserTitle(  ), ticket.getIdUserTitle(  ) );
        assertEquals( ticketStored.getUserTitle(  ), ticket.getUserTitle(  ) );
        assertEquals( ticketStored.getFirstname(  ), ticket.getFirstname(  ) );
        assertEquals( ticketStored.getLastname(  ), ticket.getLastname(  ) );
        assertEquals( ticketStored.getEmail(  ), ticket.getEmail(  ) );
        assertEquals( ticketStored.getPhoneNumber(  ), ticket.getPhoneNumber(  ) );
        assertEquals( ticketStored.getIdTicketType(  ), ticket.getIdTicketType(  ) );
        assertEquals( ticketStored.getTicketType(  ), ticket.getTicketType(  ) );
        assertEquals( ticketStored.getIdTicketDomain(  ), ticket.getIdTicketDomain(  ) );
        assertEquals( ticketStored.getTicketDomain(  ), ticket.getTicketDomain(  ) );
        assertEquals( ticketStored.getIdTicketCategory(  ), ticket.getIdTicketCategory(  ) );
        assertEquals( ticketStored.getTicketCategory(  ), ticket.getTicketCategory(  ) );
        assertEquals( ticketStored.getTicketComment(  ), ticket.getTicketComment(  ) );
        assertEquals( ticketStored.getTicketStatus(  ), ticket.getTicketStatus(  ) );
        assertEquals( ticketStored.getTicketStatusText(  ), ticket.getTicketStatusText(  ) );

        // Update test
        ticket.setIdUserTitle( IDUSERTITLE2 );
        ticket.setUserTitle( USERTITLE2 );
        ticket.setFirstname( FIRSTNAME2 );
        ticket.setLastname( LASTNAME2 );
        ticket.setEmail( EMAIL2 );
        ticket.setPhoneNumber( PHONENUMBER2 );
        ticket.setIdTicketType( IDTICKETTYPE2 );
        ticket.setTicketType( TICKETTYPE2 );
        ticket.setIdTicketDomain( IDTICKETDOMAIN2 );
        ticket.setTicketDomain( TICKETDOMAIN2 );
        ticket.setIdTicketCategory( IDTICKETCATEGORY2 );
        ticket.setTicketCategory( TICKETCATEGORY2 );
        ticket.setTicketComment( TICKETCOMMENT2 );
        ticket.setTicketStatus( TICKETSTATUS2 );
        ticket.setTicketStatusText( TICKETSTATUSTEXT2 );
        TicketHome.update( ticket );
        ticketStored = TicketHome.findByPrimaryKey( ticket.getId(  ) );
        assertEquals( ticketStored.getIdUserTitle(  ), ticket.getIdUserTitle(  ) );
        assertEquals( ticketStored.getUserTitle(  ), ticket.getUserTitle(  ) );
        assertEquals( ticketStored.getFirstname(  ), ticket.getFirstname(  ) );
        assertEquals( ticketStored.getLastname(  ), ticket.getLastname(  ) );
        assertEquals( ticketStored.getEmail(  ), ticket.getEmail(  ) );
        assertEquals( ticketStored.getPhoneNumber(  ), ticket.getPhoneNumber(  ) );
        assertEquals( ticketStored.getIdTicketType(  ), ticket.getIdTicketType(  ) );
        assertEquals( ticketStored.getTicketType(  ), ticket.getTicketType(  ) );
        assertEquals( ticketStored.getIdTicketDomain(  ), ticket.getIdTicketDomain(  ) );
        assertEquals( ticketStored.getTicketDomain(  ), ticket.getTicketDomain(  ) );
        assertEquals( ticketStored.getIdTicketCategory(  ), ticket.getIdTicketCategory(  ) );
        assertEquals( ticketStored.getTicketCategory(  ), ticket.getTicketCategory(  ) );
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
