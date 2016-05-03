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
package fr.paris.lutece.plugins.ticketing.service.util;

import fr.paris.lutece.plugins.ticketing.business.config.IPluginConfigurationDAO;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import javax.inject.Inject;


public class PluginConfigurationService
{
    @Inject
    private static IPluginConfigurationDAO _pluginConfigurationDAO = SpringContextService.getBean( 
            "ticketing.pluginConfigurationDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    public static void set( String strProperty, String strValue )
    {
        _pluginConfigurationDAO.store( strProperty, strValue, _plugin );
    }

    public static void set( String strProperty, int nValue )
    {
        String strValue = ( TicketingConstants.PROPERTY_UNSET_INT == nValue ) ? null : String.valueOf( nValue );

        _pluginConfigurationDAO.store( strProperty, strValue, _plugin );
    }

    public static String getString( String strProperty, String strDefaultValue )
    {
        String strValue = _pluginConfigurationDAO.load( strProperty, _plugin );

        return ( strValue == null ) ? strDefaultValue : strValue;
    }

    public static int getInt( String strProperty, int nDefaultValue )
    {
        String strValue = _pluginConfigurationDAO.load( strProperty, _plugin );

        return ( strValue == null ) ? nDefaultValue : Integer.parseInt( strValue );
    }
}
