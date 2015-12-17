/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *         and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *         and the following disclaimer in the documentation and/or other materials
 *         provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *         contributors may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.service.TicketFormCacheService;
import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


/**
 * This class provides instances management methods (create, find, ...) for
 * TicketForm objects
 */
public final class TicketFormHome
{
    // Static variable pointed at the DAO instance
    private static ITicketFormDAO _dao = SpringContextService.getBean ( "ticketing.ticketFormDAO" );
    private static Plugin _plugin = PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME );
    private static TicketFormCacheService _cacheService = TicketFormCacheService.getInstance ( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketFormHome(  )
    {
    }

    /**
     * Create an ticket form
     * 
     * @param ticketForm
     *            The instance of the TicketForm which contains the informations
     *            to store
     */
    public static void create(TicketForm ticketForm)
    {
        _dao.insert ( ticketForm, _plugin );
        if ( _cacheService.isCacheEnable( ) )
        {
            _cacheService.putInCache(
                    TicketFormCacheService.getFormCacheKey( ticketForm.getIdForm( ) ),
                    ticketForm.clone( ) );
            _cacheService.putInCache( TicketFormCacheService.getFormByCategoryCacheKey( ticketForm
                            .getIdCategory( ) ), ticketForm.clone( ) );
        }
    }

    /**
     * Update of the ticketForm which is specified in parameter
     * 
     * @param ticketForm
     *            The instance of the TicketForm which contains the data to
     *            store
     */
    public static void update(TicketForm ticketForm)
    {
        _dao.store ( ticketForm, _plugin );
        if ( _cacheService.isCacheEnable( ) )
        {
            _cacheService.putInCache(
                    TicketFormCacheService.getFormCacheKey( ticketForm.getIdForm( ) ),
                    ticketForm.clone( ) );
            _cacheService.putInCache( TicketFormCacheService.getFormByCategoryCacheKey( ticketForm
                            .getIdCategory( ) ), ticketForm.clone( ) );
        }
    }

    /**
     * Remove an ticket form by its primary key. <b>Warning, please check that
     * there is no ticket associated with the form BEFORE removing it!</b>
     * 
     * @param nTicketFormId
     *            The ticketForm Id
     */
    public static void remove(int nTicketFormId)
    {
        _dao.delete ( nTicketFormId, _plugin );
        if ( _cacheService.isCacheEnable( ) )
        {
            _cacheService
                    .removeKey( TicketFormCacheService
                            .getFormByCategoryCacheKey( ( (TicketForm) _cacheService
                                    .getFromCache( TicketFormCacheService
                                            .getFormCacheKey( nTicketFormId ) ) ).getIdCategory( ) ) );
            _cacheService.removeKey( TicketFormCacheService.getFormCacheKey( nTicketFormId ) );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ticketForm whose identifier is specified in
     * parameter
     * 
     * @param nTicketFormId
     *            The ticketForm primary key
     * @return an instance of TicketForm
     */
    public static TicketForm findByPrimaryKey(int nTicketFormId)
    {
        String strCacheKey = TicketFormCacheService.getFormCacheKey ( nTicketFormId );
        TicketForm form = null;
        if ( _cacheService.isCacheEnable( ) )
        {
            form = (TicketForm) _cacheService.getFromCache( strCacheKey );
        }

        if ( form == null )
        {
            form = _dao.load ( nTicketFormId, _plugin );

            if ( form != null )
            {
                if ( _cacheService.isCacheEnable( ) )
                {
                    _cacheService.putInCache( strCacheKey, form.clone( ) );
                }
            }
        }
        else
        {
            form = (TicketForm) form.clone ( );
        }

        return form;
    }

    /**
     * Returns an instance of a ticketForm whose identifier is specified in
     * parameter
     * 
     * @param nTicketFormId
     *            The ticketForm primary key
     * @return an instance of TicketForm
     */
    public static TicketForm findByCategoryId(int nIdCategory)
    {

        String strCacheKey = TicketFormCacheService.getFormByCategoryCacheKey( nIdCategory );
        TicketForm form = null;
        if ( _cacheService.isCacheEnable( ) )
        {
            form = (TicketForm) _cacheService.getFromCache( strCacheKey );
        }

        if ( form == null )
        {
            form = _dao.loadFormCategoryId( nIdCategory, _plugin );

            if ( form != null )
            {
                if ( _cacheService.isCacheEnable( ) )
                {
                    _cacheService.putInCache( strCacheKey, form.clone( ) );
                }
            }
        } else
        {
            form = (TicketForm) form.clone( );
        }

        return form;
    }

    /**
     * Load the data of all the ticketForm objects and returns them in form of a
     * collection
     * 
     * @return the list which contains the data of all the ticketForm objects
     */
    public static List<TicketForm> getTicketFormsList()
    {
        return _dao.selectTicketFormsList ( _plugin );
    }

    /**
     * Load the data of all the ticketForm objects which are not linked to a
     * category and returns them in form of a collection
     * 
     * @return the list which contains the data of all the ticketForm objects
     */
    public static ReferenceList getAvailableTicketFormsList()
    {
        List<TicketForm> lstForms = _dao.getAvailableTicketForms( _plugin );
        ReferenceList lstRef = new ReferenceList( lstForms.size( ) );
        lstRef.addItem( 0, StringUtils.EMPTY );
        for ( TicketForm form : lstForms )
        {
            lstRef.addItem( form.getIdForm( ), form.getTitle( ) );
        }
        return lstRef;
    }
}
