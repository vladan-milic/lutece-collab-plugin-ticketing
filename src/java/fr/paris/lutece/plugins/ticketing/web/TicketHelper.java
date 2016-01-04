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
package fr.paris.lutece.plugins.ticketing.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;

public class TicketHelper
{

    /**
     * init the ticket from database and load generic attributes response
     * 
     * @param nId
     *            ticketId
     * @param request
     *            http request
     * @return ticket loaded from database and its response
     */
    public static Ticket getTicketWithGenAttrResp(int nId, HttpServletRequest request)
    {

        Ticket ticket = TicketHome.findByPrimaryKey( nId );

        if ( ticket != null )
        {
            List<Integer> listIdResponse = TicketHome.findListIdResponse( ticket.getId( ) );
            List<Response> listResponses = new ArrayList<Response>( listIdResponse.size( ) );

            if ( listIdResponse != null )
            {
                for ( int nIdResponse : listIdResponse )
                {
                    Response response = ResponseHome.findByPrimaryKey( nIdResponse );

                    if ( response.getField( ) != null )
                    {
                        response.setField( FieldHome.findByPrimaryKey( response.getField( )
                                .getIdField( ) ) );
                    }

                    if ( response.getFile( ) != null )
                    {
                        fr.paris.lutece.portal.business.file.File file = FileHome
                                .findByPrimaryKey( response.getFile( ).getIdFile( ) );
                        PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file
                                .getPhysicalFile( ).getIdPhysicalFile( ) );
                        file.setPhysicalFile( physicalFile );
                        response.setFile( file );

                        String strIdEntry = Integer.toString( response.getEntry( ).getIdEntry( ) );

                        FileItem fileItem = new GenAttFileItem( physicalFile.getValue( ),
                                file.getTitle( ), IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry,
                                response.getIdResponse( ) );
                        TicketAsynchronousUploadHandler.getHandler( )
                                .addFileItemToUploadedFilesList( fileItem,
                                        IEntryTypeService.PREFIX_ATTRIBUTE + strIdEntry, request );
                    }

                    listResponses.add( response );
                }
            }
            ticket.setListResponse( listResponses );
        }
        return ticket;
    }

    /**
     * remove a ticket and its form's responses
     *
     * @param nId
     *            ticketId
     */
    public static void removeTicket(int nId)
    {
        TicketHome.removeTicketResponse( nId );
        TicketHome.remove( nId );
    }

}
