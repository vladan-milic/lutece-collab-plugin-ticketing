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
package fr.paris.lutece.plugins.ticketing.web.filter;

import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.ticketfilter.TicketFilterHelper;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPageApplicationEntry;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;

import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Filter which manages session for Ticketing plugin
 *
 */
public class SessionFilter implements Filter
{
    // Properties
    private static final String PROPERTY_RETURN_URL_PARAMETER_NAME = "ticketing.workflow.redirect.parameterName";

    // Parameters
    private static final String PARAM_RETURN_URL = AppPropertiesService.getProperty( PROPERTY_RETURN_URL_PARAMETER_NAME,
            "return_url" );
    private static final String PARAMETER_XPAGE = "page";

    @Override
    public void init( FilterConfig filterConfig ) throws ServletException
    {
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain filterChain )
        throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if ( isTicketingBackOfficeUrl( httpRequest ) || isTicketingFrontOfficeUrl( httpRequest ) )
        {
            if ( StringUtils.isNotEmpty( (String) request.getParameter( PARAM_RETURN_URL ) ) )
            {
                httpRequest.getSession( true )
                           .setAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL,
                    request.getParameter( PARAM_RETURN_URL ) );
            }
        }
        else
        {
            // Clean the session
            HttpSession session = httpRequest.getSession(  );
            session.removeAttribute( TicketingConstants.ATTRIBUTE_RETURN_URL );
            session.removeAttribute( TicketingConstants.ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO );
            session.removeAttribute( TicketingConstants.SESSION_NOT_VALIDATED_TICKET );
            session.removeAttribute( TicketingConstants.SESSION_VALIDATED_TICKET_FORM );
            session.removeAttribute( TicketingConstants.SESSION_TICKET_FORM_ERRORS );
            session.removeAttribute( TicketingConstants.SESSION_TICKET_FILTER );
            session.removeAttribute( TicketingConstants.SESSION_INSTANTRESPONSE_FILTER );
        }

        filterChain.doFilter( request, response );
    }

    @Override
    public void destroy(  )
    {
    }

    /**
     * Tests if the request is for the back office of Ticketing plugin
     * @param request the request
     * @return {@code true} if the request is for the back office of Ticketing, {@code false} otherwise
     */
    private static boolean isTicketingBackOfficeUrl( HttpServletRequest request )
    {
        String url = request.getRequestURL(  ).toString(  );

        return url.contains( TicketingConstants.ADMIN_CONTROLLLER_PATH );
    }

    /**
     * Tests if the request is for the front office of Ticketing plugin
     * @param request the request
     * @return {@code true} if the request is for the front office of Ticketing, {@code false} otherwise
     */
    private static boolean isTicketingFrontOfficeUrl( HttpServletRequest request )
    {
        boolean bFound = false;
        String strXPage = request.getParameter( PARAMETER_XPAGE );
        List<XPageApplicationEntry> listXPages = PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME ).getApplications(  );

        for ( XPageApplicationEntry entry : listXPages )
        {
            if ( entry.getId(  ).equals( strXPage ) )
            {
                bFound = true;

                break;
            }
        }

        return bFound;
    }
}
