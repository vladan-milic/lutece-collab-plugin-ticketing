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
package fr.paris.lutece.plugins.ticketing.service.download;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.service.authentication.RequestAuthenticationService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Servlet serving ticketing files
 */
public class TicketingFileServlet extends HttpServlet
{
    /**
     * Generated serial Id
     */
    private static final long   serialVersionUID            = -3589685443968252550L;

    // Parameters
    public static final String  PARAMETER_ID_RESPONSE       = "id_response";

    // Other constants
    public static final String  URL_SERVLET                 = "servlet/plugins/ticketing/file";
    private static final String LOG_UNKNOWN_ID_RESPONSE     = "Calling Ticketing file servlet with unknown id response : ";
    private static final String LOG_WRONG_ID_RESPONSE       = "Calling Ticketing file servlet with wrong format for parameter " + PARAMETER_ID_RESPONSE + " : ";
    private static final String LOG_UNAUTHENTICATED_REQUEST = "Calling Ticketing file servlet with unauthenticated request";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * 
     * @param request
     *            servlet request
     * @param httpResponse
     *            servlet response
     * @throws ServletException
     *             the servlet Exception
     * @throws IOException
     *             the io exception
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse httpResponse ) throws ServletException, IOException
    {
        String strIdResponse = request.getParameter( PARAMETER_ID_RESPONSE );

        if ( !StringUtils.isEmpty( strIdResponse ) && StringUtils.isNumeric( strIdResponse ) )
        {
            int nIdResponse = Integer.parseInt( strIdResponse );

            Response response = ResponseHome.findByPrimaryKey( nIdResponse );

            if ( response == null )
            {
                AppLogService.error( LOG_UNKNOWN_ID_RESPONSE + strIdResponse );
                throw new ServletException( LOG_UNKNOWN_ID_RESPONSE + strIdResponse );
            }

            if ( !isRequestAuthenticated( request ) )
            {
                AppLogService.error( LOG_UNAUTHENTICATED_REQUEST );
                throw new ServletException( LOG_UNAUTHENTICATED_REQUEST );
            }

            File file = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );
            PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );

            httpResponse.setHeader( "Content-Disposition", "attachment; filename=\"" + file.getTitle( ) + "\";" );
            httpResponse.setHeader( "Content-type", file.getMimeType( ) );
            httpResponse.addHeader( "Content-Encoding", "UTF-8" );
            httpResponse.addHeader( "Pragma", "public" );
            httpResponse.addHeader( "Expires", "0" );
            httpResponse.addHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );

            try
            {
                OutputStream os = httpResponse.getOutputStream( );
                os.write( physicalFile.getValue( ) );
                // We do not close the output stream in finaly clause because it is
                // the response stream,
                // and an error message needs to be displayed if an exception occurs
                os.close( );
            } catch ( IOException e )
            {
                AppLogService.error( e.getStackTrace( ), e );
            }
        } else
        {
            AppLogService.error( LOG_WRONG_ID_RESPONSE + strIdResponse );
            throw new ServletException( );
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             the servlet Exception
     * @throws IOException
     *             the io exception
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException
     *             the servlet Exception
     * @throws IOException
     *             the io exception
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return message
     */
    @Override
    public String getServletInfo( )
    {
        return "Servlet serving file content";
    }

    /**
     * Checks if the request is authenticated or not
     * 
     * @param request
     *            the HTTP request
     * @return {@code true} if the request is authenticated, {@code false} otherwise
     */
    private boolean isRequestAuthenticated( HttpServletRequest request )
    {
        return RequestAuthenticationService.getRequestAuthenticator( ).isRequestAuthenticated( request );
    }
}
