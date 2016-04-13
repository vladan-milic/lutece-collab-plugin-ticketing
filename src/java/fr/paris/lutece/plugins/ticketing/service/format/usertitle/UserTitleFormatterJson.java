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
package fr.paris.lutece.plugins.ticketing.service.format.usertitle;

import fr.paris.lutece.plugins.ticketing.business.UserTitle;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * JSON formatter for user title resource
 *
 */
public class UserTitleFormatterJson implements IFormatter<UserTitle>
{
    @Override
    public String format( UserTitle userTitle )
    {
        JSONObject json = new JSONObject(  );
        String strJson = StringUtils.EMPTY;

        if ( userTitle != null )
        {
            add( json, userTitle );
            strJson = json.toString(  );
        }

        return strJson;
    }

    @Override
    public String format( List<UserTitle> listUserTitles )
    {
        JSONObject json = new JSONObject(  );
        JSONArray jsonUserTitles = new JSONArray(  );

        for ( UserTitle userTitle : listUserTitles )
        {
            JSONObject jsonUserTitle = new JSONObject(  );
            add( jsonUserTitle, userTitle );
            jsonUserTitles.add( jsonUserTitle );
        }

        json.accumulate( FormatConstants.KEY_USER_TITLES, jsonUserTitles );

        return json.toString(  );
    }

    @Override
    public String formatError( String arg0, String arg1 )
    {
        return null;
    }

    @Override
    public String formatResponse( UserTitle userTitle )
    {
        return format( userTitle );
    }

    /**
     * Write a user title into a JSON Object
     * @param json The JSON Object
     * @param userTitle The user title
     */
    private void add( JSONObject json, UserTitle userTitle )
    {
        json.accumulate( FormatConstants.KEY_ID, userTitle.getId(  ) );
        json.accumulate( FormatConstants.KEY_LABEL, userTitle.getLabel(  ) );
    }
}
