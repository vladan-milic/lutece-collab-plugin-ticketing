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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceList;

/**
 * 
 * @author s267533
 * this enum represents support level
 */
public enum SupportLevel 
{
    LEVEL1( 1 ),
    LEVEL2( 2 ),
    LEVEL3( 3 );

    private static final String MESSAGE_PREFIX = "ticketing.supportentity.level";
    private static Map<Integer, SupportLevel> _mapSupportLevel = new HashMap<Integer, SupportLevel> ();
    static 
    {
        for ( SupportLevel enumSupportLevel : EnumSet.allOf( SupportLevel.class ) ) 
        {
            _mapSupportLevel.put( enumSupportLevel._nLevelValue, enumSupportLevel );
        }
    }
    
    private int _nLevelValue;
    
    /**
     * enum constructor
     * @param nLevelValue level value 
     */
    SupportLevel ( int nLevelValue ) 
    {
        _nLevelValue = nLevelValue;
    }

    /**
     * returns level value
     * @return levelValue
     */
    public int getLevelValue()
    {
        return _nLevelValue;
    }
    
    /**
     * returns SupportLevel enum for level value
     * @param nLevelValue level value
     * @return SupportLevel enum
     */
    public static SupportLevel valueOf( int nLevelValue ) 
    {
        return _mapSupportLevel.get( Integer.valueOf( nLevelValue ) );
    }
    
    /**
     * returns level label
     * @param locale the locale used to retrieve the localized messages
     * @return the message
     */
    public String getLocalizedMessage( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_PREFIX+_nLevelValue, locale );
    }
    
    /**
     * Builds a RefenrenceList object containing all the SupportLevel objects
     * @param locale the locale used to retrieve the localized messages
     * @return the ReferenceList object
     */
    public static ReferenceList getReferenceList( Locale locale )
    {
        ReferenceList refListLevel = new ReferenceList( );
        for ( SupportLevel supportLevel : SupportLevel.values( ) ) 
        {
            refListLevel.addItem( supportLevel.getLevelValue( ), supportLevel.getLocalizedMessage( locale ) );  
        }
        return refListLevel;
    }
}
  
