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

import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

public class TicketCategoryTypeBusinessTest extends LuteceTestCase
{
    public void testBusiness( )
    {
        // Initialize an object
        TicketCategoryType newTicketCategoryType = new TicketCategoryType( );
        newTicketCategoryType.setLabel( "Nature" );
        newTicketCategoryType.setDepthNumber( 1 );

        // Create test
        TicketCategoryTypeHome.create( newTicketCategoryType );

        TicketCategoryType ticketCategoryTypeStored = TicketCategoryTypeHome.findByPrimaryKey( newTicketCategoryType.getId( ) );
        assertEquals( ticketCategoryTypeStored.getLabel( ), newTicketCategoryType.getLabel( ) );
        assertEquals( ticketCategoryTypeStored.getDepthNumber( ), newTicketCategoryType.getDepthNumber( ) );

        // Update test
        newTicketCategoryType.setLabel( "Type" );
        newTicketCategoryType.setDepthNumber( 2 );
        TicketCategoryTypeHome.update( newTicketCategoryType );

        ticketCategoryTypeStored = TicketCategoryTypeHome.findByPrimaryKey( newTicketCategoryType.getId( ) );
        assertEquals( ticketCategoryTypeStored.getLabel( ), newTicketCategoryType.getLabel( ) );
        assertEquals( ticketCategoryTypeStored.getDepthNumber( ), newTicketCategoryType.getDepthNumber( ) );
        assertEquals( ticketCategoryTypeStored.getLabel( ), "Type" );
        assertEquals( ticketCategoryTypeStored.getDepthNumber( ), 2 );

        newTicketCategoryType.setLabel( "Nature" );
        newTicketCategoryType.setDepthNumber( 1 );
        TicketCategoryTypeHome.update( newTicketCategoryType );

        // List test
        newTicketCategoryType = new TicketCategoryType( );
        newTicketCategoryType.setLabel( "Domaine" );
        newTicketCategoryType.setDepthNumber( 2 );
        TicketCategoryTypeHome.create( newTicketCategoryType );

        newTicketCategoryType.setLabel( "Problématique" );
        TicketCategoryTypeHome.createNewDepthCategoryType( newTicketCategoryType );

        ticketCategoryTypeStored = TicketCategoryTypeHome.findByDepth( 3 );
        assertEquals( ticketCategoryTypeStored.getLabel( ), "Problématique" );
        assertEquals( ticketCategoryTypeStored.getDepthNumber( ), 3 );

        List<Integer> listCategoryTypeId = TicketCategoryTypeHome.getIdCategoryTypesList( );
        assertEquals( listCategoryTypeId.size( ), 3 );
        listCategoryTypeId.forEach( categoryTypeId -> System.out.println( categoryTypeId ) );

        List<TicketCategoryType> listCategoryType = TicketCategoryTypeHome.getCategoryTypesList( );
        assertEquals( listCategoryType.size( ), 3 );
        listCategoryType.forEach( categoryType -> System.out.println( categoryType.getLabel( ) + " " + categoryType.getDepthNumber( ) ) );

        ReferenceList referenceList = TicketCategoryTypeHome.getCategoryTypesReferenceList( );
        assertEquals( referenceList.size( ), 3 );
        referenceList.forEach( reference -> System.out.println( reference.getCode( ) + " " + reference.getName( ) ) );

        // Delete test
        listCategoryType.forEach( categoryType -> TicketCategoryTypeHome.remove( categoryType.getId( ) ) );
        listCategoryType = TicketCategoryTypeHome.getCategoryTypesList( );
        assertEquals( listCategoryType.size( ), 0 );
    }
}
