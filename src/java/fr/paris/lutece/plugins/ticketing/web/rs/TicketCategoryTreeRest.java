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
package fr.paris.lutece.plugins.ticketing.web.rs;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryTree;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatterFactory;

/**
 * REST service for ticket type resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.TYPE_PATH )
public class TicketCategoryTreeRest extends TicketingRest
{
    /**
     * Gives the categories tree
     * 
     * @param accept
     *            the accepted format
     * @param format
     *            the format
     * @return the categories tree
     */
    @GET
    @Path( Constants.ALL_PATH )
    public Response getCategoriesTree( @HeaderParam( HttpHeaders.ACCEPT ) String accept, @QueryParam( Constants.FORMAT_QUERY ) String format )
    {
        String strMediaType = getMediaType( accept, format );

        IFormatterFactory formatterFactory = _formatterFactories.get( strMediaType );

        TicketCategoryTree ticketCategoryTree = TicketCategoryService.getInstance( ).getCategoriesTree( );

        String strResponse = formatterFactory.createFormatter( TicketCategoryTree.class ).format( ticketCategoryTree );

        return Response.ok( strResponse, strMediaType ).build( );
    }
}
