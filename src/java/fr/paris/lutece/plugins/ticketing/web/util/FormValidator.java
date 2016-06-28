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
package fr.paris.lutece.plugins.ticketing.web.util;

import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactMode;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides utility methods to validatea form
 *
 */
public class FormValidator
{
    // Parameters
    private static final String PARAMETER_CONTACT_MODE_ID = "id_contact_mode";

    // Errors
    private static final String ERROR_CONTACT_MODE_NOT_FILLED = "ticketing.error.contactmode.not.filled";
    private HttpServletRequest _request;

    /**
     * Constructor
     * @param request the request containing the parameter to validate
     */
    public FormValidator( HttpServletRequest request )
    {
        _request = request;
    }

    /**
     * Tests if the contact mode is filled
     *
     * @return the localized error message if the contact mode is not filled, {@code null} otherwise
     */
    public String isContactModeFilled(  )
    {
        boolean bIsValid = false;
        String strError = null;

        String strContactMode = _request.getParameter( PARAMETER_CONTACT_MODE_ID );

        try
        {
            if ( !StringUtils.isEmpty( strContactMode ) )
            {
                ContactMode contactMode = ContactModeHome.findByPrimaryKey( Integer.parseInt( strContactMode ) );

                String strContactModeValue = _request.getParameter( contactMode.getCode(  ) );

                bIsValid = !StringUtils.isEmpty( strContactModeValue );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( "Unabled to retrieve contact mode" );
        }

        if ( !bIsValid )
        {
            strError = I18nService.getLocalizedString( ERROR_CONTACT_MODE_NOT_FILLED, _request.getLocale(  ) );
        }

        return strError;
    }
}