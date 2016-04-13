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
package fr.paris.lutece.plugins.ticketing.service.format.contactmode;

import fr.paris.lutece.plugins.ticketing.business.ContactMode;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * JSON formatter for contact mode resource
 *
 */
public class ContactModeFormatterJson implements IFormatter<ContactMode>
{
    @Override
    public String format( ContactMode contactMode )
    {
        JSONObject json = new JSONObject(  );
        String strJson = StringUtils.EMPTY;

        if ( contactMode != null )
        {
            add( json, contactMode );
            strJson = json.toString(  );
        }

        return strJson;
    }

    @Override
    public String format( List<ContactMode> listContactModes )
    {
        JSONObject json = new JSONObject(  );
        JSONArray jsonContactModes = new JSONArray(  );

        for ( ContactMode contactMode : listContactModes )
        {
            JSONObject jsonContactMode = new JSONObject(  );
            add( jsonContactMode, contactMode );
            jsonContactModes.add( jsonContactMode );
        }

        json.accumulate( FormatConstants.KEY_CONTACT_MODES, jsonContactModes );

        return json.toString(  );
    }

    @Override
    public String formatError( String arg0, String arg1 )
    {
        return null;
    }

    @Override
    public String formatResponse( ContactMode contactMode )
    {
        return format( contactMode );
    }

    /**
     * Write a contact mode into a JSON Object
     * @param json The JSON Object
     * @param contactMode The contact mode
     */
    private void add( JSONObject json, ContactMode contactMode )
    {
        json.accumulate( FormatConstants.KEY_ID, contactMode.getId(  ) );
        json.accumulate( FormatConstants.KEY_LABEL, contactMode.getLabel(  ) );
    }
}
