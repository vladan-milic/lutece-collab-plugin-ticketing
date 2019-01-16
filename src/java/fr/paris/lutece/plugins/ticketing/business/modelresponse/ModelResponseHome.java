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
package fr.paris.lutece.plugins.ticketing.business.modelresponse;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for TypeResponse objects
 */
public final class ModelResponseHome
{
    // Static variable pointed at the DAO instance
    private static IModelResponseDAO _dao = SpringContextService.getBean( "ticketing.modelResponseDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ModelResponseHome( )
    {
    }

    /**
     * Create an instance of the modelResponse class
     *
     * @param modelResponse
     *            The instance of the ModelResponse which contains the informations to store
     * @return The instance of modelResponse which has been created with its primary key.
     */
    public static ModelResponse create( ModelResponse modelResponse )
    {
        _dao.insert( modelResponse, _plugin );

        return modelResponse;
    }

    /**
     * Update of the modelResponse which is specified in parameter
     *
     * @param modelResponse
     *            The instance of the ModelResponse which contains the data to store
     * @return The instance of the modelResponse which has been updated
     */
    public static ModelResponse update( ModelResponse modelResponse )
    {
        _dao.store( modelResponse, _plugin );

        return modelResponse;
    }

    /**
     * Remove the modelResponse whose identifier is specified in parameter
     *
     * @param nKey
     *            The modelResponse Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a modelResponse whose identifier is specified in parameter
     *
     * @param nKey
     *            The modelResponse primary key
     * @return an instance of ModelResponse
     */
    public static ModelResponse findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the modelResponse objects and returns them as a list
     *
     * @return the list which contains the data of all the modelResponse objects
     */
    public static List<ModelResponse> getModelResponsesList( )
    {
        return _dao.selectModelResponsesList( _plugin );
    }

    /**
     * Load the data of all the modelResponse objects for a given domain label and returns them as a list
     *
     * @param sLabelDomain
     *            Label Domain
     *
     * @return the list which contains the data of all the modelResponse objects
     */
    public static List<ModelResponse> getModelResponsesListByDomain( String sLabelDomain )
    {
        return _dao.selectModelResponsesListByDomain( _plugin, sLabelDomain );
    }

    /**
     * Load the id of all the modelResponse objects and returns them as a list
     *
     * @return the list which contains the id of all the modelResponse objects
     */
    public static List<Integer> getIdTypeResponsesList( )
    {
        return _dao.selectIdModelResponsesList( _plugin );
    }

    /**
     * Load the data of all the modelResponse objects and returns them as a referenceList
     *
     * @return the referenceList which contains the data of all the modelResponse objects
     */
    public static ReferenceList getTypeResponsesReferenceList( )
    {
        return _dao.selectModelResponsesReferenceList( _plugin );
    }
}
