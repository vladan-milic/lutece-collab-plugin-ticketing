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

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

public class TicketCategoryBusinessTest extends LuteceTestCase
{
    public void testBusiness( )
    {
        // Initialize objects
        List<TicketCategoryType> listCategoryType = TicketCategoryTypeHome.getCategoryTypesList( );
        listCategoryType.forEach( categoryType -> TicketCategoryTypeHome.remove( categoryType.getId( ) ) );
        TicketCategoryType newTicketCategoryType = new TicketCategoryType( );
        newTicketCategoryType.setLabel( "Nature" );
        TicketCategoryTypeHome.createNewDepthCategoryType( newTicketCategoryType );
        newTicketCategoryType = new TicketCategoryType( );
        newTicketCategoryType.setLabel( "Domaine" );
        TicketCategoryTypeHome.createNewDepthCategoryType( newTicketCategoryType );
        newTicketCategoryType.setLabel( "Problématique" );
        TicketCategoryTypeHome.createNewDepthCategoryType( newTicketCategoryType );
        newTicketCategoryType.setLabel( "Précision" );
        TicketCategoryTypeHome.createNewDepthCategoryType( newTicketCategoryType );

        // Create test
        TicketCategory newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Réclamations" );
        newTicketCategory.setCode( "RCLs" );

        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 1 ) );
        System.out.println( "Type newTicketCategory=" + newTicketCategory.getCategoryType( ).getLabel( ) + " "
                + newTicketCategory.getCategoryType( ).getDepthNumber( ) );

        TicketCategoryHome.create( newTicketCategory );

        TicketCategory ticketCategoryStored = TicketCategoryHome.findByCode( "RCLs" );
        assertEquals( ticketCategoryStored.getLabel( ), newTicketCategory.getLabel( ) );
        assertEquals( ticketCategoryStored.getCode( ), newTicketCategory.getCode( ) );
        assertEquals( ticketCategoryStored.getCode( ), "RCLs" );
        // assertEquals( ticketCategoryStored.getCategoryType( ).getLabel( ), newTicketCategory.getCategoryType( ).getLabel( ) );
        // assertEquals( ticketCategoryStored.getCategoryType( ).getNbDepth( ), newTicketCategory.getCategoryType( ).getNbDepth( ) );

        // Update test
        newTicketCategory.setLabel( "Réclamation" );
        newTicketCategory.setCode( "RCL" );
        TicketCategoryHome.update( newTicketCategory );
        ticketCategoryStored = TicketCategoryHome.findByPrimaryKey( 1 );
        assertEquals( ticketCategoryStored.getLabel( ), newTicketCategory.getLabel( ) );
        assertEquals( ticketCategoryStored.getCode( ), newTicketCategory.getCode( ) );
        assertEquals( ticketCategoryStored.getCode( ), "RCL" );
        assertEquals( ticketCategoryStored.getLabel( ), "Réclamation" );

        ticketCategoryStored = TicketCategoryHome.findByCode( "RCLs" );
        assertEquals( ticketCategoryStored, null );
        ticketCategoryStored = TicketCategoryHome.findByCode( "RCL" );
        assertEquals( ticketCategoryStored.getCode( ), "RCL" );
        assertEquals( ticketCategoryStored.getLabel( ), "Réclamation" );

        // List test
        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Demande d'information" );
        newTicketCategory.setCode( "INF" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 1 ) );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Facil'Familles" );
        newTicketCategory.setCode( "FFRCL" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 2 ) );
        newTicketCategory.setIdParent( 1 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Stationnement" );
        newTicketCategory.setCode( "STA" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 2 ) );
        newTicketCategory.setIdParent( 1 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Facil'Familles" );
        newTicketCategory.setCode( "FFINF" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 2 ) );
        newTicketCategory.setIdParent( 2 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Mairie" );
        newTicketCategory.setCode( "MDP" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 2 ) );
        newTicketCategory.setIdParent( 2 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Pb Petite Enfance" );
        newTicketCategory.setCode( "PPE" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 3 ) );
        newTicketCategory.setIdParent( 3 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Pb Périscolaire" );
        newTicketCategory.setCode( "PSCO" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 3 ) );
        newTicketCategory.setIdParent( 3 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Horaires" );
        newTicketCategory.setCode( "HOS" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 3 ) );
        newTicketCategory.setIdParent( 4 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Obtention info" );
        newTicketCategory.setCode( "OBT" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 3 ) );
        newTicketCategory.setIdParent( 5 );
        TicketCategoryHome.create( newTicketCategory );

        newTicketCategory = new TicketCategory( );
        newTicketCategory.setLabel( "Poubelles" );
        newTicketCategory.setCode( "POU" );
        newTicketCategory.setCategoryType( TicketCategoryTypeHome.findByDepth( 3 ) );
        newTicketCategory.setIdParent( 6 );
        TicketCategoryHome.create( newTicketCategory );

        ReferenceList referenceList = TicketCategoryHome.getCategorysReferenceList( );
        assertEquals( referenceList.size( ), 11 );
        referenceList.forEach( reference -> System.out.println( reference.getCode( ) + " " + reference.getName( ) ) );

        List<Integer> listCategoryId = TicketCategoryHome.getIdCategorysList( );
        assertEquals( listCategoryId.size( ), 11 );
        listCategoryId.forEach( categoryId -> System.out.println( categoryId ) );

        List<TicketCategory> listCategory = TicketCategoryHome.getFullCategorysList( false );
        assertEquals( listCategory.size( ), 11 );
        listCategory
                .forEach( category -> System.out.println( category.getLabel( ) + "  " + category.getCode( ) + " " + category.getCategoryType( ).getLabel( ) ) );

        listCategory = TicketCategoryHome.getCategorysList( );
        assertEquals( listCategory.size( ), 11 );
        listCategory
                .forEach( category -> System.out.println( category.getLabel( ) + "  " + category.getCode( ) + " " + category.getCategoryType( ).getLabel( ) ) );

        // // Delete test
        // listCategoryType.forEach(categoryType -> TicketCategoryTypeHome.remove( categoryType.getId( ) ) );
        // listCategoryType = TicketCategoryTypeHome.getCategoryTypesList( );
        // assertEquals( listCategoryType.size( ), 0 );
    }
}
