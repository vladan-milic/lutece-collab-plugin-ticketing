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

import fr.paris.lutece.plugins.ticketing.business.form.Form;
import fr.paris.lutece.plugins.ticketing.business.form.FormHome;
import fr.paris.lutece.test.LuteceTestCase;


public class FormBusinessTest extends LuteceTestCase
{
    private final static String TITLE1 = "Title1";
    private final static String TITLE2 = "Title2";
    private final static String MESSAGE1 = "Message1";
    private final static String MESSAGE2 = "Message2";
    private final static String BUTTONLABEL1 = "ButtonLabel1";
    private final static String BUTTONLABEL2 = "ButtonLabel2";
	private final static boolean CONNECTION1 = true;
    private final static boolean CONNECTION2 = false;

    public void testBusiness(  )
    {
        // Initialize an object
        Form form = new Form();
        form.setTitle( TITLE1 );
        form.setMessage( MESSAGE1 );
        form.setButtonLabel( BUTTONLABEL1 );
        form.setConnection( CONNECTION1 );

        // Create test
        FormHome.create( form );
        Form formStored = FormHome.findByPrimaryKey( form.getId( ) );
        assertEquals( formStored.getTitle() , form.getTitle( ) );
        assertEquals( formStored.getMessage() , form.getMessage( ) );
        assertEquals( formStored.getButtonLabel() , form.getButtonLabel( ) );
        assertEquals( formStored.isConnection() , form.isConnection() );

        // Update test
        form.setTitle( TITLE2 );
        form.setMessage( MESSAGE2 );
        form.setButtonLabel( BUTTONLABEL2 );
        form.setConnection( CONNECTION2 );
        FormHome.update( form );
        formStored = FormHome.findByPrimaryKey( form.getId( ) );
        assertEquals( formStored.getTitle() , form.getTitle( ) );
        assertEquals( formStored.getMessage() , form.getMessage( ) );
        assertEquals( formStored.getButtonLabel() , form.getButtonLabel( ) );
        assertEquals( formStored.isConnection() , form.isConnection( ) );

        // List test
        FormHome.getFormsList();

        // Delete test
        FormHome.remove( form.getId( ) );
        formStored = FormHome.findByPrimaryKey( form.getId( ) );
        assertNull( formStored );
        
    }

}