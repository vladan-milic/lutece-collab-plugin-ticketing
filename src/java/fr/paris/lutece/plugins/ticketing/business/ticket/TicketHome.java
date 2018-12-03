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
package fr.paris.lutece.plugins.ticketing.business.ticket;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.marking.Marking;
import fr.paris.lutece.plugins.ticketing.business.marking.MarkingHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormCacheService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for Ticket objects
 */
public final class TicketHome
{
    // Static variable pointed at the DAO instance
    private static ITicketDAO             _dao          = SpringContextService.getBean( "ticketing.ticketDAO" );
    private static Plugin                 _plugin       = PluginService.getPlugin( "ticketing" );
    private static TicketFormCacheService _cacheService = TicketFormCacheService.getInstance( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketHome( )
    {
    }

    /**
     * Create an instance of the ticket class
     *
     * @param ticket
     *            The instance of the Ticket which contains the informations to store
     * @return The instance of ticket which has been created with its primary key.
     */
    public static Ticket create( Ticket ticket )
    {
        _dao.insert( ticket, _plugin );

        return ticket;
    }

    /**
     * Update of the ticket which is specified in parameter
     *
     * @param ticket
     *            The instance of the Ticket which contains the data to store
     * @return The instance of the ticket which has been updated
     */
    public static Ticket update( Ticket ticket )
    {
        _dao.store( ticket, _plugin );

        return ticket;
    }

    /**
     * Remove the ticket and its generic attributes responses whose identifier is specified in parameter
     *
     * @param nKey
     *            The ticket Id
     */
    public static void remove( int nKey )
    {
        removeTicketResponse( nKey );
        _dao.delete( nKey, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ticket whose identifier is specified in parameter generic attributes responses are also loaded
     *
     * @param nKey
     *            The ticket primary key
     * @return an instance of Ticket
     */
    public static Ticket findByPrimaryKey( int nKey )
    {
        Ticket ticket = _dao.load( nKey, _plugin );

        if ( ticket != null )
        {
            TicketCategory ticketCategory = TicketCategoryService.getInstance( true ).findCategoryById( ticket.getTicketCategory( ).getId( ) );
            ticket.setTicketCategory( ticketCategory );
        }

        if ( ticket != null )
        {
            // retrieving tickets generic attributes response
            List<Integer> listIdResponse = TicketHome.findListIdResponse( ticket.getId( ) );
            List<Response> listResponses = new ArrayList<Response>( listIdResponse.size( ) );

            if ( listIdResponse != null )
            {
                for ( int nIdResponse : listIdResponse )
                {
                    Response response = ResponseHome.findByPrimaryKey( nIdResponse );

                    if ( response != null )
                    {

                        if ( response.getField( ) != null )
                        {
                            response.setField( FieldHome.findByPrimaryKey( response.getField( ).getIdField( ) ) );
                        }

                        if ( response.getFile( ) != null )
                        {
                            fr.paris.lutece.portal.business.file.File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );
                            if ( file != null )
                            {
                                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );
                                if ( physicalFile != null )
                                {
                                    file.setPhysicalFile( physicalFile );
                                }
                                response.setFile( file );
                            }
                        }

                        listResponses.add( response );
                    }
                }
            }

            ticket.setListResponse( listResponses );
        }

        return ticket;
    }

    /**
     * Get the marking for the given ticket id
     *
     * @param ticket
     *            ticket
     *
     * @return the marking
     */
    public static Marking getTicketMarking( Ticket ticket )
    {
        Marking marking = null;
        int nIdTicketMarking = ticket.getIdTicketMarking( );

        if ( nIdTicketMarking > 0 )
        {
            marking = MarkingHome.loadMarkingFromCache( nIdTicketMarking );
        }

        return marking;
    }

    /**
     * Get the marking id for the given ticket
     *
     * @param nIdTicket
     *            the ticket
     * @return id for the given ticket
     */
    public static int getTicketMarkingId( int nIdTicket )
    {
        return _dao.loadTicketMarkingId( nIdTicket, _plugin );
    }

    /**
     * Store the marking id for the given ticket
     *
     * @param nIdTicket
     *            the ticket
     * @param nIdMarking
     *            the marking
     */
    public static void setTicketMarkingId( int nIdTicket, int nIdMarking )
    {
        _dao.storeTicketMarkingId( nIdTicket, nIdMarking, _plugin );
    }

    /**
     * Reset the marking id to default value for the given marking id
     *
     * @param nIdMarking
     *            the marking id
     */
    public static void resetMarkingId( int nIdMarking )
    {
        _dao.resetMarkingId( nIdMarking, _plugin );
    }

    /**
     * Reset the marking id to default value for the given ticket id
     *
     * @param nIdTicket
     *            the ticket id
     */
    public static void resetTicketMarkingId( int nIdTicket )
    {
        _dao.resetTicketMarkingId( nIdTicket, _plugin );
    }

    /**
     * Load the data of all the ticket objects and returns them in form of a collection be carefull generic attribute response are not loaded in this method
     *
     * @see #findByPrimaryKey for loading all attribute responses
     * @return the collection which contains the data of all the ticket objects
     */
    public static List<Ticket> getTicketsList( )
    {
        return _dao.selectTicketsList( _plugin );
    }

    /**
     * Load the data of all the ticket objects and returns them in form of a collection
     *
     * @param filter
     *            filter to apply to ticket search
     * @return the collection which contains the data of all the ticket objects
     */
    public static List<Ticket> getTicketsList( TicketFilter filter )
    {
        return _dao.selectTicketsList( filter, _plugin );
    }

    /**
     * Load the id of all the ticket objects and returns them in form of a collection
     *
     * @return the collection which contains the id of all the ticket objects
     */
    public static List<Integer> getIdTicketsList( )
    {
        return _dao.selectIdTicketsList( _plugin );
    }

    /**
     * Load the id of all the ticket objects and returns them in form of a collection
     *
     * @param filter
     *            filter to apply to ticket search
     *
     * @return the collection which contains the id of all the ticket objects
     */
    public static List<Integer> getIdTicketsList( TicketFilter filter )
    {
        return _dao.selectIdTicketsList( filter, _plugin );
    }

    // -----------------------------------------------
    // Ticket response management
    // -----------------------------------------------

    /**
     * Associates a response to a ticket
     *
     * @param nIdTicket
     *            The id of the ticket
     * @param nIdResponse
     *            The id of the response
     */
    public static void insertTicketResponse( int nIdTicket, int nIdResponse )
    {
        _dao.insertTicketResponse( nIdTicket, nIdResponse, _plugin );
        _cacheService.removeKey( _cacheService.getTicketResponseCacheKey( nIdTicket ) );
    }

    /**
     * Get the list of id of responses associated with an ticket
     *
     * @param nIdticket
     *            the id of the ticket
     * @return the list of responses, or an empty list if no response was found
     */
    @SuppressWarnings( "unchecked" )
    public static List<Integer> findListIdResponse( int nIdticket )
    {
        String strCacheKey = _cacheService.getTicketResponseCacheKey( nIdticket );
        List<Integer> listIdResponse = ( List<Integer> ) _cacheService.getFromCache( strCacheKey );

        if ( listIdResponse == null )
        {
            listIdResponse = _dao.findListIdResponse( nIdticket, _plugin );
            _cacheService.putInCache( strCacheKey, new ArrayList<Integer>( listIdResponse ) );
        } else
        {
            listIdResponse = new ArrayList<Integer>( listIdResponse );
        }

        return listIdResponse;
    }

    /**
     * Get the list of responses associated with an ticket
     *
     * @param nIdticket
     *            the id of the ticket
     * @return the list of responses, or an empty list if no response was found
     */
    public static List<Response> findListResponse( int nIdticket )
    {
        List<Integer> listIdResponse = findListIdResponse( nIdticket );
        List<Response> listResponse = new ArrayList<Response>( listIdResponse.size( ) );

        for ( Integer nIdResponse : listIdResponse )
        {
            listResponse.add( ResponseHome.findByPrimaryKey( nIdResponse ) );
        }

        return listResponse;
    }

    /**
     * Find the id of the ticket associated with a given response
     *
     * @param nIdResponse
     *            The id of the response
     * @return The id of the ticket, or 0 if no ticket is associated with he given response.
     */
    public static int findIdTicketByIdResponse( int nIdResponse )
    {
        return _dao.findIdTicketByIdResponse( nIdResponse, _plugin );
    }

    /**
     * Remove the association between an ticket and responses
     *
     * @param nIdTicket
     *            The id of the ticket
     */
    public static void removeTicketResponse( int nIdTicket )
    {
        _dao.deleteTicketResponse( nIdTicket, _plugin );
        _cacheService.removeKey( _cacheService.getTicketResponseCacheKey( nIdTicket ) );
    }

    /**
     * Remove the association between an ticket and a specific response
     *
     * @param nIdTicket
     *            The id of the ticket
     * @param nIdResponse
     *            The id of the response
     */
    public static void removeTicketResponse( int nIdTicket, int nIdResponse )
    {
        _dao.removeTicketResponsesByIdResponse( nIdResponse, _plugin );
        _cacheService.removeKey( _cacheService.getTicketResponseCacheKey( nIdTicket ) );
    }

    /**
     * Remove every ticket responses associated with a given entry.
     *
     * @param nIdEntry
     *            The id of the entry
     */
    public static void removeResponsesByIdEntry( int nIdEntry )
    {
        ResponseFilter filter = new ResponseFilter( );
        filter.setIdEntry( nIdEntry );

        List<Response> listResponses = ResponseHome.getResponseList( filter );

        for ( Response response : listResponses )
        {
            _dao.removeTicketResponsesByIdResponse( response.getIdResponse( ), _plugin );
            ResponseHome.remove( response.getIdResponse( ) );
        }

        _cacheService.resetCache( );
    }

    /**
     * Get every ticket associated with the given unit
     *
     * @param nIdUnit
     *            The id of the unit
     * @return The list of ticket associated
     */
    public static List<Ticket> findByUnitId( int nIdUnit )
    {
        return _dao.selectTicketsListByUnitId( nIdUnit, _plugin );
    }
}
