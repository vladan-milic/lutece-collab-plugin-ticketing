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
package fr.paris.lutece.plugins.ticketing.business.form;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for FormEntry objects
 */
public final class FormEntryHome
{
    // Static variable pointed at the DAO instance
    private static IFormEntryDAO _dao = SpringContextService.getBean( "ticketing.formEntryDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormEntryHome(  )
    {
    }

    public static List<FormEntry> findByFormId( Integer formId )
    {
        return _dao.loadByForm( formId, _plugin );
    }

    /**
     * Create an instance of the formEntry class
     * @param formEntry The instance of the FormEntry which contains the informations to store
     * @return The  instance of formEntry which has been created with its primary key.
     */
    public static FormEntry create( FormEntry formEntry )
    {
        _dao.insert( formEntry, _plugin );

        return formEntry;
    }

    /**
     * Update of the formEntry which is specified in parameter
     * @param formEntry The instance of the FormEntry which contains the data to store
     * @return The instance of the  formEntry which has been updated
     */
    public static FormEntry update( FormEntry formEntry )
    {
        _dao.store( formEntry, _plugin );

        return formEntry;
    }

    /**
     * Remove the formEntry whose identifier is specified in parameter
     * @param nKey The formEntry Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a formEntry whose identifier is specified in parameter
     * @param nKey The formEntry primary key
     * @return an instance of FormEntry
     */
    public static FormEntry findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin);
    }

    /**
     * Load the data of all the formEntry objects and returns them as a list
     * @return the list which contains the data of all the formEntry objects
     */
    public static List<FormEntry> getFormEntrysList( )
    {
        return _dao.selectFormEntrysList( _plugin );
    }
    
    /**
     * Load the id of all the formEntry objects and returns them as a list
     * @return the list which contains the id of all the formEntry objects
     */
    public static List<Integer> getIdFormEntrysList( )
    {
        return _dao.selectIdFormEntrysList( _plugin );
    }
    
    /**
     * Load the data of all the formEntry objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the formEntry objects
     */
    public static ReferenceList getFormEntrysReferenceList( )
    {
        return _dao.selectFormEntrysReferenceList(_plugin );
    }
}

