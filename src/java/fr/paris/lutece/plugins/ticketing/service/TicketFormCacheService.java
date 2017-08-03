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
package fr.paris.lutece.plugins.ticketing.service;

import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

/**
 * Get the instance of the cache service
 */
public final class TicketFormCacheService extends AbstractCacheableService
{
    private static final String SERVICE_NAME = "ticketing.ticketFormCacheService";
    private static final String CACHE_KEY_FORM = "ticketing.ticketForm.";
    private static final String CACHE_KEY_TICKET_RESPONSE = "ticketing.ticketResponse";
    private static final String CACHE_KEY_FORM_BY_CATEGORY = "ticketing.ticketForm.category";
    private static final String CACHE_KEY_TICKET_CATEGORY = "ticketing.ticketCategory";
    private static final String CACHE_KEY_TICKET_DOMAIN = "ticketing.ticketDomain";
    private static final String CACHE_KEY_TICKET_TYPE = "ticketing.ticketType";
    private static final String CACHE_KEY_TICKET_USER_TITLE = "ticketing.ticketUserTitle";
    private static final String CACHE_KEY_TICKET_CONTACT_MODE = "ticketing.ticketContactMode";

    private static TicketFormCacheService _instance = new TicketFormCacheService( );

    /**
     * Private constructor
     */
    private TicketFormCacheService( )
    {
        initCache( );
    }

    /**
     * Get the instance of the cache service
     * 
     * @return The instance of the service
     */
    public static TicketFormCacheService getInstance( )
    {
        return _instance;
    }

    /**
     * Get the cache key for a given form
     * 
     * @param nIdForm
     *            The id of the form
     * @return The cache key for the form
     */
    public static String getFormCacheKey( int nIdForm )
    {
        return CACHE_KEY_FORM + nIdForm;
    }

    /**
     * Get the cache key form ticket responses
     *
     * @param nIdTicket
     *            The id of the ticket
     * @return The cache key for the given ticket
     */
    public String getTicketResponseCacheKey( int nIdTicket )
    {
        return CACHE_KEY_TICKET_RESPONSE + nIdTicket;
    }

    /**
     * Get the category cache key for a given form
     *
     * @param nIdCategory
     *            The id of the category linked to the form
     * @return The cache key for the form
     */
    public static String getFormByCategoryCacheKey( int nIdCategory )
    {
        return CACHE_KEY_FORM_BY_CATEGORY + nIdCategory;
    }

    /**
     * Get the category cache key for a given id category
     *
     * @param nIdCategory
     *            The id of the category linked to the category
     * @return The cache key for the category
     */
    public static String getCategoryByIdCacheKey( int nIdCategory )
    {
        return CACHE_KEY_TICKET_CATEGORY + nIdCategory;
    }

    /**
     * Get the domain cache key for a given id domain
     *
     * @param nIdDomain
     *            The id of the domain linked to the domain
     * @return The cache key for the domain
     */
    public static String getDomainByIdCacheKey( int nIdDomain )
    {
        return CACHE_KEY_TICKET_DOMAIN + nIdDomain;
    }

    /**
     * Get the type cache key for a given id type
     *
     * @param nIdType
     *            The id of the type linked to the type
     * @return The cache key for the type
     */
    public static String getTypeByIdCacheKey( int nIdType )
    {
        return CACHE_KEY_TICKET_TYPE + nIdType;
    }

    /**
     * Get the user title cache key for a given id user title
     *
     * @param nIdUserTitle
     *            The id of the user title linked to the user title
     * @return The cache key for the user title
     */
    public static String getUserTitleByIdCacheKey( int nIdUserTitle )
    {
        return CACHE_KEY_TICKET_USER_TITLE + nIdUserTitle;
    }

    /**
     * Get the contact mode cache key for a given id contact mode
     *
     * @param nIdContactMode
     *            The id of the contact mode linked to the contact mode
     * @return The cache key for the contact mode
     */
    public static String getContactModeByIdCacheKey( int nIdContactMode )
    {
        return CACHE_KEY_TICKET_CONTACT_MODE + nIdContactMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }
}
