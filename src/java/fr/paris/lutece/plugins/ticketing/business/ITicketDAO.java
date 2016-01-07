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

import fr.paris.lutece.portal.service.plugin.Plugin;



/**
 * ITicketDAO Interface
 */
public interface ITicketDAO
{
    /**
     * Insert a new record in the table.
     * @param ticket instance of the Ticket object to insert
     * @param plugin the Plugin
     */
    void insert( Ticket ticket, Plugin plugin );

    /**
     * Update the record in the table
     * @param ticket the reference of the Ticket
     * @param plugin the Plugin
     */
    void store( Ticket ticket, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nKey The identifier of the Ticket to delete
     * @param plugin the Plugin
     */
    void delete( int nKey, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * @param nKey The identifier of the ticket
     * @param plugin the Plugin
     * @return The instance of the ticket
     */
    Ticket load( int nKey, Plugin plugin );

    /**
     * Load the data of all the ticket objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the data of all the ticket objects
     */
    List<Ticket> selectTicketsList( Plugin plugin );

    /**
     * Load the data of all the ticket objects matching input filter and returns
     * them as a collection
     *
     * @param filter
     *            search filter
     * @param plugin
     *            the Plugin
     * @return The collection which contains the data of all the ticket objects
     */
    List<Ticket> selectTicketsList(TicketFilter filter, Plugin plugin);

    /**
     * Load the id of all the ticket objects and returns them as a collection
     * 
     * @param plugin
     *            the Plugin
     * @return The collection which contains the id of all the ticket objects
     */
    List<Integer> selectIdTicketsList( Plugin plugin );
    
    
    // ----------------------------------------
    // Ticket response management
    // ----------------------------------------

    /**
     * Associates a response to a ticket
     * @param nIdTicket The id of the ticket
     * @param nIdResponse The id of the response
     * @param plugin The plugin
     */
    void insertTicketResponse( int nIdTicket, int nIdResponse, Plugin plugin );

    /**
     * Get the list of id of responses associated with an ticket
     * @param nIdTicket the id of the ticket
     * @param plugin the plugin
     * @return the list of responses, or an empty list if no response was found
     */
    List<Integer> findListIdResponse( int nIdTicket, Plugin plugin );

    /**
     * Remove the association between an ticket and responses
     * @param nIdTicket The id of the ticket
     * @param plugin The plugin
     */
    void deleteTicketResponse( int nIdTicket, Plugin plugin );

     /**
     * Remove an ticket responses from the id of a response.
     * @param nIdResponse The id of the response
     * @param plugin The plugin
     */
    void removeTicketResponsesByIdResponse( int nIdTicket, Plugin plugin );
    /**
     * Find the id of the ticket associated with a given response
     * @param nIdResponse The id of the response
     * @param plugin The plugin
     * @return The id of the ticket, or 0 if no ticket is associated
     *         with he given response.
     */
    int findIdTicketByIdResponse( int nIdResponse, Plugin plugin );
}    
 