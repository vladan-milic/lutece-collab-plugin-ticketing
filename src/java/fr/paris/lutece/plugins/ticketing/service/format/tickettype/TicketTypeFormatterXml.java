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
package fr.paris.lutece.plugins.ticketing.service.format.tickettype;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatter;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.List;


/**
 * XML formatter for ticket type resource
 *
 */
public class TicketTypeFormatterXml implements IFormatter<TicketType>
{
    @Override
    public String format( TicketType ticketType )
    {
        StringBuffer sbXML = new StringBuffer(  );

        if ( ticketType != null )
        {
            sbXML.append( FormatConstants.XML_HEADER );
            add( sbXML, ticketType );
        }

        return sbXML.toString(  );
    }

    @Override
    public String format( List<TicketType> listTicketTypes )
    {
        StringBuffer sbXML = new StringBuffer(  );
        sbXML.append( FormatConstants.XML_HEADER );
        XmlUtil.beginElement( sbXML, FormatConstants.KEY_TICKET_TYPES );

        for ( TicketType ticketType : listTicketTypes )
        {
            add( sbXML, ticketType );
        }

        XmlUtil.endElement( sbXML, FormatConstants.KEY_TICKET_TYPES );

        return sbXML.toString(  );
    }

    @Override
    public String formatError( String arg0, String arg1 )
    {
        return null;
    }

    @Override
    public String formatResponse( TicketType ticketType )
    {
        return format( ticketType );
    }

    /**
     * Write a ticket category into a buffer
     * @param sbXML The buffer
     * @param ticketType The ticket type
     */
    private void add( StringBuffer sbXML, TicketType ticketType )
    {
        XmlUtil.beginElement( sbXML, FormatConstants.KEY_TICKET_TYPE );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_ID, ticketType.getId(  ) );
        XmlUtil.addElement( sbXML, FormatConstants.KEY_LABEL, ticketType.getLabel(  ) );

        for ( ReferenceItem domain : TicketDomainHome.getReferenceListByType( ticketType.getId(  ) ) )
        {
            XmlUtil.beginElement( sbXML, FormatConstants.KEY_TICKET_DOMAIN );

            int nDomainId = Integer.parseInt( domain.getCode(  ) );
            XmlUtil.addElement( sbXML, FormatConstants.KEY_ID, nDomainId );
            XmlUtil.addElement( sbXML, FormatConstants.KEY_LABEL, domain.getName(  ) );

            for ( ReferenceItem category : TicketCategoryHome.getReferenceListByDomain( nDomainId ) )
            {
                XmlUtil.beginElement( sbXML, FormatConstants.KEY_TICKET_CATEGORY );

                int nCategoryId = Integer.parseInt( category.getCode(  ) );
                XmlUtil.addElement( sbXML, FormatConstants.KEY_ID, nCategoryId );
                XmlUtil.addElement( sbXML, FormatConstants.KEY_LABEL, category.getName(  ) );
                XmlUtil.endElement( sbXML, FormatConstants.KEY_TICKET_CATEGORY );
            }

            XmlUtil.endElement( sbXML, FormatConstants.KEY_TICKET_DOMAIN );
        }

        XmlUtil.endElement( sbXML, FormatConstants.KEY_TICKET_TYPE );
    }
}
