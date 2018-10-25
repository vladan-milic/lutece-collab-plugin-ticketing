/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import fr.paris.lutece.plugins.ticketing.business.form.FormEntry;
import fr.paris.lutece.plugins.ticketing.business.form.FormEntryHome;
import fr.paris.lutece.test.LuteceTestCase;

public class FormEntryBusinessTest extends LuteceTestCase
{
    private final static int     IDFORM1    = 1;
    private final static int     IDFORM2    = 2;
    private final static String  IDCHAMP1   = "IdChamp1";
    private final static String  IDCHAMP2   = "IdChamp2";
    private final static boolean HIDDEN1    = true;
    private final static boolean HIDDEN2    = false;
    private final static boolean MANDATORY1 = true;
    private final static boolean MANDATORY2 = false;
    private final static int     ORDER1     = 1;
    private final static int     ORDER2     = 2;

    public void testBusiness( )
    {
        // Initialize an object
        FormEntry formEntry = new FormEntry( );
        formEntry.setIdForm( IDFORM1 );
        formEntry.setIdChamp( IDCHAMP1 );
        formEntry.setHidden( HIDDEN1 );
        formEntry.setMandatory( MANDATORY1 );
        formEntry.setHierarchy( ORDER1 );

        // Create test
        FormEntryHome.create( formEntry );
        FormEntry formEntryStored = FormEntryHome.findByPrimaryKey( formEntry.getId( ) );
        assertEquals( formEntryStored.getIdForm( ), formEntry.getIdForm( ) );
        assertEquals( formEntryStored.getIdChamp( ), formEntry.getIdChamp( ) );
        assertEquals( formEntryStored.isHidden( ), formEntry.isHidden( ) );
        assertEquals( formEntryStored.isMandatory( ), formEntry.isMandatory( ) );
        assertEquals( formEntryStored.getHierarchy( ), formEntry.getHierarchy( ) );

        // Update test
        formEntry.setIdForm( IDFORM2 );
        formEntry.setIdChamp( IDCHAMP2 );
        formEntry.setHidden( HIDDEN2 );
        formEntry.setMandatory( MANDATORY2 );
        formEntry.setHierarchy( ORDER2 );
        FormEntryHome.update( formEntry );
        formEntryStored = FormEntryHome.findByPrimaryKey( formEntry.getId( ) );
        assertEquals( formEntryStored.getIdForm( ), formEntry.getIdForm( ) );
        assertEquals( formEntryStored.getIdChamp( ), formEntry.getIdChamp( ) );
        assertEquals( formEntryStored.isHidden( ), formEntry.isHidden( ) );
        assertEquals( formEntryStored.isMandatory( ), formEntry.isMandatory( ) );
        assertEquals( formEntryStored.getHierarchy( ), formEntry.getHierarchy( ) );

        // List test
        FormEntryHome.getFormEntrysList( );

        // Delete test
        FormEntryHome.remove( formEntry.getId( ) );
        formEntryStored = FormEntryHome.findByPrimaryKey( formEntry.getId( ) );
        assertNull( formEntryStored );

    }

}
