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

import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PluginConfigurationService
{
    private static final String PROPERTY_PREFIX = "ticketing.configuration.";
    public static final String PROPERTY_TICKET_WORKFLOW_ID = PROPERTY_PREFIX + "workflow.id";
    public static final String PROPERTY_STATE_CLOSED_ID = PROPERTY_PREFIX + "state.id.closed";
    public static final String PROPERTY_STATES_SELECTED = PROPERTY_PREFIX + "states.selected";
    public static final String PROPERTY_STATES_SELECTED_FOR_ROLE_PREFIX = PROPERTY_PREFIX +
        "states.selected.for.role.";
    public static final String PROPERTY_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME = PROPERTY_PREFIX +
        "actions.filtered.when.assigned.to.me";
    public static final String PROPERTY_ADMINUSER_ID_FRONT = PROPERTY_PREFIX + "adminUser.id.front";
    public static final String PROPERTY_CHANNEL_ID_FRONT = PROPERTY_PREFIX + "channel.id.front";
    private static final String LIST_SEPARATOR = ";";

    public static void set( String strProperty, String strValue )
    {
        DatastoreService.setDataValue( strProperty, strValue );
    }

    public static void set( String strProperty, int nValue )
    {
        String strValue = ( TicketingConstants.PROPERTY_UNSET_INT == nValue ) ? null : String.valueOf( nValue );

        DatastoreService.setDataValue( strProperty, strValue );
    }

    public static void set( String strProperty, List<Integer> listValues )
    {
        String strValue = listToString( listValues );

        DatastoreService.setDataValue( strProperty, strValue );
    }

    public static String getString( String strProperty, String strDefaultValue )
    {
        String strValue = DatastoreService.getDataValue( strProperty, null );

        return ( strValue == null ) ? strDefaultValue : strValue;
    }

    public static List<String> getStringList( String strProperty, List<String> listDefaultValues )
    {
        String strValue = DatastoreService.getDataValue( strProperty, null );

        List<String> result = stringToStringList( strValue );

        return ( result == null ) ? listDefaultValues : result;
    }

    public static Map<String, List<String>> getStringListByPrefix( String strProperty,
        Map<String, List<String>> mapDefaultValues )
    {
        Map<String, List<String>> result = mapDefaultValues;
        ReferenceList referenceListValues = DatastoreService.getDataByPrefix( strProperty );

        if ( referenceListValues != null )
        {
            result = new HashMap<String, List<String>>(  );

            for ( ReferenceItem item : referenceListValues )
            {
                List<String> listValues = stringToStringList( item.getName(  ) );

                if ( listValues != null )
                {
                    result.put( item.getCode(  )
                                    .replace( PluginConfigurationService.PROPERTY_STATES_SELECTED_FOR_ROLE_PREFIX,
                            StringUtils.EMPTY ), listValues );
                }
            }
        }

        return result;
    }

    public static int getInt( String strProperty, int nDefaultValue )
    {
        String strValue = DatastoreService.getDataValue( strProperty, null );

        return ( strValue == null ) ? nDefaultValue : Integer.parseInt( strValue );
    }

    public static List<Integer> getIntegerList( String strProperty, List<Integer> listDefaultValues )
    {
        String strValue = DatastoreService.getDataValue( strProperty, null );

        List<Integer> result = stringToIntegerList( strValue );

        return ( result == null ) ? listDefaultValues : result;
    }

    public static Map<String, List<Integer>> getIntegerListByPrefix( String strProperty,
        Map<String, List<Integer>> mapDefaultValues )
    {
        Map<String, List<Integer>> result = mapDefaultValues;
        ReferenceList referenceListValues = DatastoreService.getDataByPrefix( strProperty );

        if ( referenceListValues != null )
        {
            result = new HashMap<String, List<Integer>>(  );

            for ( ReferenceItem item : referenceListValues )
            {
                List<Integer> listValues = stringToIntegerList( item.getName(  ) );

                if ( listValues != null )
                {
                    result.put( item.getCode(  )
                                    .replace( PluginConfigurationService.PROPERTY_STATES_SELECTED_FOR_ROLE_PREFIX,
                            StringUtils.EMPTY ), listValues );
                }
            }
        }

        return result;
    }

    public static void removeByPrefix( String strPropertyPrefix )
    {
        DatastoreService.removeDataByPrefix( strPropertyPrefix );
    }

    private static final String listToString( List<?extends Object> listValues )
    {
        if ( ( listValues == null ) || listValues.isEmpty(  ) )
        {
            return null;
        }

        StringBuilder sb = new StringBuilder(  );

        for ( Object objValue : listValues )
        {
            if ( ( objValue != null ) && !StringUtils.isEmpty( objValue.toString(  ) ) )
            {
                sb.append( objValue.toString(  ) ).append( LIST_SEPARATOR );
            }
        }

        if ( sb.length(  ) != 0 )
        {
            sb.deleteCharAt( sb.length(  ) - 1 );
        }

        return ( sb.length(  ) == 0 ) ? null : sb.toString(  );
    }

    private static List<String> stringToStringList( String strValue )
    {
        List<String> result = null;

        if ( strValue != null )
        {
            String[] listValues = strValue.split( LIST_SEPARATOR );

            result = Arrays.asList( listValues );
        }

        return result;
    }

    private static List<Integer> stringToIntegerList( String strValue )
    {
        List<Integer> result = null;

        if ( strValue != null )
        {
            String[] listValues = strValue.split( LIST_SEPARATOR );
            result = new ArrayList<Integer>( listValues.length );

            for ( String strValueItem : listValues )
            {
                result.add( Integer.parseInt( strValueItem ) );
            }
        }

        return result;
    }
}
