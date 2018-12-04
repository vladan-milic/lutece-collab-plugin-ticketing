/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.paris.lutece.plugins.identitystore.web.rs.dto.AttributeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.AuthorDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityChangeDto;
import fr.paris.lutece.plugins.identitystore.web.rs.dto.IdentityDto;
import fr.paris.lutece.plugins.identitystore.web.service.AuthorType;
import fr.paris.lutece.plugins.identitystore.web.service.IdentityService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;

/**
 * Bean for saving personal data difference in identitystore
 */
public class PersonalDataApp extends MVCApplication
{
    private static final long     serialVersionUID               = 1L;

    // Session keys
    private static final String   SESSION_INIT_PERSONAL_DATA     = "ticketing.personal.data.init";
    private static final String   SESSION_DELTA_PERSONAL_DATA    = "ticketing.personal.data.delta";

    // map properties
    private static final String[] MAP_ATTRIBUTES_IDENTITY_TOSAVE = AppPropertiesService.getProperty( "ticketing.identity.attribute.tosave" ).split( "," );

    // IDS service
    private static final String   IDS_SERVICE_BEAN_NAME          = AppPropertiesService.getProperty( "ticketing.identity.service.beanname" );
    private IdentityService       _identityService;

    public PersonalDataApp( )
    {
        _identityService = SpringContextService.getBean( IDS_SERVICE_BEAN_NAME );
    }

    /**
     * Init personal data in session
     *
     * @param request
     *            request
     */
    public void doInitPersonalData( HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( ( user != null ) && StringUtils.isNotEmpty( user.getName( ) ) && ( request.getSession( ).getAttribute( SESSION_INIT_PERSONAL_DATA ) == null ) )
        {
            Map<String, String> mapAttributes = new HashMap<String, String>( );

            for ( String strAttrKeyToSave : MAP_ATTRIBUTES_IDENTITY_TOSAVE )
            {
                if ( request.getParameter( strAttrKeyToSave ) != null )
                {
                    mapAttributes.put( strAttrKeyToSave, request.getParameter( strAttrKeyToSave ) );
                }
            }
            request.getSession( ).setAttribute( SESSION_INIT_PERSONAL_DATA, mapAttributes );
            request.getSession( ).removeAttribute( SESSION_DELTA_PERSONAL_DATA );
        }
    }

    /**
     * Calculate personal data delta between init and the current Store the delta map in session
     *
     * @param request
     *            request
     */
    @SuppressWarnings( "unchecked" )
    public void doDeltaPersonalData( HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        Map<String, String> mapAttributes = new HashMap<String, String>( );

        if ( ( user != null ) && StringUtils.isNotEmpty( user.getName( ) ) )
        {
            try
            {
                Map<String, String> mapInitPersonalData = ( Map<String, String> ) request.getSession( ).getAttribute( SESSION_INIT_PERSONAL_DATA );
                if ( mapInitPersonalData != null )
                {
                    for ( String strAttrKeyToSave : MAP_ATTRIBUTES_IDENTITY_TOSAVE )
                    {
                        if ( StringUtils.isNotEmpty( request.getParameter( strAttrKeyToSave ) )
                                && !StringUtils.equals( request.getParameter( strAttrKeyToSave ), mapInitPersonalData.get( strAttrKeyToSave ) ) )
                        {
                            mapAttributes.put( strAttrKeyToSave, request.getParameter( strAttrKeyToSave ) );
                        }
                    }
                }
            } catch ( ClassCastException e )
            {
                // do nothing, object in session should be map<string,string>
                AppLogService.error( "error while convert session object " + SESSION_INIT_PERSONAL_DATA + " to map", e );
            }
        }
        request.getSession( ).setAttribute( SESSION_DELTA_PERSONAL_DATA, mapAttributes );
    }

    /**
     * Return a json {"delta":true} if there is a calculated delta in session, empty json elsewhere
     *
     * @param request
     *            request
     * @return json string
     */
    @SuppressWarnings( "unchecked" )
    public String getDeltaPersonalData( HttpServletRequest request )
    {
        ObjectNode jsonMap = JsonNodeFactory.instance.objectNode( );
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( ( user != null ) && StringUtils.isNotEmpty( user.getName( ) ) )
        {
            try
            {
                Map<String, String> mapDeltaPersonalData = ( Map<String, String> ) request.getSession( ).getAttribute( SESSION_DELTA_PERSONAL_DATA );
                if ( ( mapDeltaPersonalData != null ) && ( mapDeltaPersonalData.size( ) > 0 ) )
                {
                    jsonMap.put( "delta", true );
                }
            } catch ( ClassCastException e )
            {
                // do nothing, object in session should be map<string,string>
                AppLogService.error( "error while convert session object " + SESSION_DELTA_PERSONAL_DATA + " to map", e );
            }
        }
        return jsonMap.toString( );
    }

    /**
     * Save personal data difference in identitystore
     *
     * @param request
     *            request
     */
    @SuppressWarnings( "unchecked" )
    public void doSavePersonalData( HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( ( user != null ) && StringUtils.isNotEmpty( user.getName( ) ) )
        {
            Map<String, String> mapDeltaPersonalData = new HashMap<String, String>( );
            try
            {
                mapDeltaPersonalData = ( Map<String, String> ) request.getSession( ).getAttribute( SESSION_DELTA_PERSONAL_DATA );
            } catch ( ClassCastException e )
            {
                // do nothing, object in session should be map<string,string>
                AppLogService.error( "error while convert session object " + SESSION_DELTA_PERSONAL_DATA + " to map", e );
            }
            IdentityDto identityDto = new IdentityDto( );
            identityDto.setConnectionId( user.getName( ) );
            Map<String, AttributeDto> mapAttributes = new HashMap<String, AttributeDto>( );
            AttributeDto attribute;

            for ( String strAttrKeyToSave : MAP_ATTRIBUTES_IDENTITY_TOSAVE )
            {
                if ( ( mapDeltaPersonalData != null ) && mapDeltaPersonalData.containsKey( strAttrKeyToSave ) )
                {
                    attribute = new AttributeDto( );
                    attribute.setKey( strAttrKeyToSave );
                    attribute.setValue( mapDeltaPersonalData.get( strAttrKeyToSave ) );
                    mapAttributes.put( attribute.getKey( ), attribute );
                }
            }

            if ( mapAttributes.size( ) > 0 )
            {
                identityDto.setAttributes( mapAttributes );

                AuthorDto author = new AuthorDto( );
                // FIXME application code must be in conf
                author.setApplicationCode( TicketingConstants.APPLICATION_CODE );
                author.setType( AuthorType.TYPE_USER_OWNER.getTypeValue( ) );

                IdentityChangeDto identityChangeDto = new IdentityChangeDto( );
                identityChangeDto.setIdentity( identityDto );
                identityChangeDto.setAuthor( author );

                try
                {
                    _identityService.updateIdentity( identityChangeDto, null );
                    request.getSession( ).removeAttribute( SESSION_DELTA_PERSONAL_DATA );
                    request.getSession( ).removeAttribute( SESSION_INIT_PERSONAL_DATA );
                } catch ( Exception e )
                {
                    // do nothing, just log
                    AppLogService.error( "Error occur while save data to identityStore", e );
                }
            }
        }
    }
}
