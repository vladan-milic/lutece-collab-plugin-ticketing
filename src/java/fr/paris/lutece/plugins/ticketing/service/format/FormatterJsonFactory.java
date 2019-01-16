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
package fr.paris.lutece.plugins.ticketing.service.format;

import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactMode;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitle;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryTree;
import fr.paris.lutece.plugins.ticketing.service.format.channel.ChannelFormatterJson;
import fr.paris.lutece.plugins.ticketing.service.format.contactmode.ContactModeFormatterJson;
import fr.paris.lutece.plugins.ticketing.service.format.ticket.TicketFormatterJson;
import fr.paris.lutece.plugins.ticketing.service.format.ticketcategory.TicketCategoryTreeFormatterJson;
import fr.paris.lutece.plugins.ticketing.service.format.usertitle.UserTitleFormatterJson;

/**
 * Factory for the JSON formatters
 */
public class FormatterJsonFactory implements IFormatterFactory
{
    private Map<Class<?>, ITicketingFormatter<?>> _formatters;
    private IRestFormatter _restFormatter;

    /**
     * Default constructor
     */
    public FormatterJsonFactory( )
    {
        _formatters = new HashMap<Class<?>, ITicketingFormatter<?>>( );
        _formatters.put( Ticket.class, new TicketFormatterJson( ) );
        _formatters.put( TicketCategoryTree.class, new TicketCategoryTreeFormatterJson( ) );
        _formatters.put( Channel.class, new ChannelFormatterJson( ) );
        _formatters.put( ContactMode.class, new ContactModeFormatterJson( ) );
        _formatters.put( UserTitle.class, new UserTitleFormatterJson( ) );
        _restFormatter = new RestFormatterJson( );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> ITicketingFormatter<T> createFormatter( Class<T> clazz )
    {
        return (ITicketingFormatter<T>) _formatters.get( clazz );
    }

    @Override
    public IRestFormatter createRestFormatter( )
    {
        return _restFormatter;
    }
}
