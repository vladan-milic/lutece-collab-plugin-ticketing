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
package fr.paris.lutece.plugins.ticketing.service.format.channel;

import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.List;


/**
 * JSON formatter for channel resource
 *
 */
public class ChannelFormatterJson implements IFormatter<Channel>
{
    @Override
    public String format( Channel channel )
    {
        JSONObject json = new JSONObject(  );
        String strJson = StringUtils.EMPTY;

        if ( channel != null )
        {
            add( json, channel );
            strJson = json.toString(  );
        }

        return strJson;
    }

    @Override
    public String format( List<Channel> listChannels )
    {
        JSONObject json = new JSONObject(  );
        JSONArray jsonChannels = new JSONArray(  );

        for ( Channel channel : listChannels )
        {
            JSONObject jsonChannel = new JSONObject(  );
            add( jsonChannel, channel );
            jsonChannels.add( jsonChannel );
        }

        json.accumulate( FormatConstants.KEY_CHANNELS, jsonChannels );

        return json.toString(  );
    }

    @Override
    public String formatError( String arg0, String arg1 )
    {
        return null;
    }

    @Override
    public String formatResponse( Channel channel )
    {
        return format( channel );
    }

    /**
     * Write a channel into a JSON Object
     * @param json The JSON Object
     * @param channel The channel
     */
    private void add( JSONObject json, Channel channel )
    {
        json.accumulate( FormatConstants.KEY_ID, channel.getId(  ) );
        json.accumulate( FormatConstants.KEY_LABEL, channel.getLabel(  ) );
    }
}
