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
import fr.paris.lutece.plugins.ticketing.service.format.ITicketingFormatter;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.List;


/**
 * XML formatter for channel resource
 *
 */
public class ChannelFormatterXml implements ITicketingFormatter<Channel>
{
    @Override
    public String format( Channel channel )
    {
        StringBuffer sbXML = new StringBuffer(  );

        if ( channel != null )
        {
            sbXML.append( FormatConstants.XML_HEADER );
            add( sbXML, channel );
        }

        return sbXML.toString(  );
    }

    @Override
    public String format( List<Channel> listChannels )
    {
        StringBuffer sbXML = new StringBuffer(  );
        sbXML.append( FormatConstants.XML_HEADER );
        XmlUtil.beginElement( sbXML, FormatConstants.KEY_CHANNELS );

        for ( Channel channel : listChannels )
        {
            add( sbXML, channel );
        }

        XmlUtil.endElement( sbXML, FormatConstants.KEY_CHANNELS );

        return sbXML.toString(  );
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
     * Write a channel into a buffer
     * @param sbXML The buffer
     * @param channel The channel
     */
    private void add( StringBuffer sbXML, Channel channel )
    {
        XmlUtil.beginElement( sbXML, FormatConstants.KEY_CHANNEL );

        XmlUtil.addElement( sbXML, FormatConstants.KEY_ID, channel.getId(  ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_LABEL, channel.getLabel(  ) );

        XmlUtil.endElement( sbXML, FormatConstants.KEY_CHANNEL );
    }
}
