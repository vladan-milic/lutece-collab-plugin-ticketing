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


public class TicketCategoryBusinessTest extends LuteceTestCase
{
    private final static int IDTICKETDOMAIN1 = 1;
    private final static int IDTICKETDOMAIN2 = 2;
    private final static String LABEL1 = "Label1";
    private final static String LABEL2 = "Label2";

    public void testBusiness(  )
    {
        // Initialize an object
        TicketCategory ticketCategory = new TicketCategory(  );
        ticketCategory.setIdTicketDomain( IDTICKETDOMAIN1 );
        ticketCategory.setLabel( LABEL1 );

        // Create test
        TicketCategoryHome.create( ticketCategory );

        TicketCategory ticketCategoryStored = TicketCategoryHome.findByPrimaryKey( ticketCategory.getId(  ) );
        assertEquals( ticketCategoryStored.getIdTicketDomain(  ), ticketCategory.getIdTicketDomain(  ) );
        assertEquals( ticketCategoryStored.getLabel(  ), ticketCategory.getLabel(  ) );

        // Update test
        ticketCategory.setIdTicketDomain( IDTICKETDOMAIN2 );
        ticketCategory.setLabel( LABEL2 );
        TicketCategoryHome.update( ticketCategory );
        ticketCategoryStored = TicketCategoryHome.findByPrimaryKey( ticketCategory.getId(  ) );
        assertEquals( ticketCategoryStored.getIdTicketDomain(  ), ticketCategory.getIdTicketDomain(  ) );
        assertEquals( ticketCategoryStored.getLabel(  ), ticketCategory.getLabel(  ) );

        // List test
        TicketCategoryHome.getTicketCategorysList(  );

        // Delete test
        TicketCategoryHome.remove( ticketCategory.getId(  ) );
        ticketCategoryStored = TicketCategoryHome.findByPrimaryKey( ticketCategory.getId(  ) );
        assertNull( ticketCategoryStored );
    }
}
