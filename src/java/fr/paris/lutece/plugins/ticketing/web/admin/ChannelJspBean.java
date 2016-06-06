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
package fr.paris.lutece.plugins.ticketing.web.admin;

import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage Channel features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageChannels.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class ChannelJspBean extends ManageAdminTicketingJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_CHANNELS = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH +
        "manage_channels.html";
    private static final String TEMPLATE_CREATE_CHANNEL = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH +
        "create_channel.html";
    private static final String TEMPLATE_MODIFY_CHANNEL = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH +
        "modify_channel.html";

    // Parameters
    private static final String PARAMETER_ID_CHANNEL = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CHANNELS = "ticketing.manage_channels.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CHANNEL = "ticketing.modify_channel.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CHANNEL = "ticketing.create_channel.pageTitle";

    // Markers
    private static final String MARK_CHANNEL_LIST = "channel_list";
    private static final String MARK_CHANNEL = "channel";
    private static final String JSP_MANAGE_CHANNELS = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH +
        "ManageChannels.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CHANNEL = "ticketing.message.confirmRemoveChannel";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.channel.attribute.";

    // Views
    private static final String VIEW_MANAGE_CHANNELS = "manageChannels";
    private static final String VIEW_CREATE_CHANNEL = "createChannel";
    private static final String VIEW_MODIFY_CHANNEL = "modifyChannel";

    // Actions
    private static final String ACTION_CREATE_CHANNEL = "createChannel";
    private static final String ACTION_MODIFY_CHANNEL = "modifyChannel";
    private static final String ACTION_REMOVE_CHANNEL = "removeChannel";
    private static final String ACTION_CONFIRM_REMOVE_CHANNEL = "confirmRemoveChannel";

    // Infos
    private static final String INFO_CHANNEL_CREATED = "ticketing.info.channel.created";
    private static final String INFO_CHANNEL_UPDATED = "ticketing.info.channel.updated";
    private static final String INFO_CHANNEL_REMOVED = "ticketing.info.channel.removed";
    private static final long serialVersionUID = 1L;

    // Session variable to store working values
    private Channel _channel;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CHANNELS, defaultView = true )
    public String getManageChannels( HttpServletRequest request )
    {
        _channel = null;

        List<Channel> listChannels = (List<Channel>) ChannelHome.getChannelList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_CHANNEL_LIST, listChannels, JSP_MANAGE_CHANNELS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CHANNELS, TEMPLATE_MANAGE_CHANNELS, model );
    }

    /**
     * Returns the form to create a channel
     *
     * @param request
     *            The Http request
     * @return the html code of the channel form
     */
    @View( VIEW_CREATE_CHANNEL )
    public String getCreateChannel( HttpServletRequest request )
    {
        _channel = ( _channel != null ) ? _channel : new Channel(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_CHANNEL, _channel );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CHANNEL, TEMPLATE_CREATE_CHANNEL, model );
    }

    /**
     * Process the data capture form of a new channel
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_CHANNEL )
    public String doCreateChannel( HttpServletRequest request )
    {
        populate( _channel, request );

        // Check constraints
        if ( !validateBean( _channel, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CHANNEL );
        }

        ChannelHome.create( _channel );
        addInfo( INFO_CHANNEL_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_CHANNELS );
    }

    /**
     * Manages the removal form of a channel whose identifier is in the http
     * request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CHANNEL )
    public String getConfirmRemoveChannel( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CHANNEL ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CHANNEL ) );
        url.addParameter( PARAMETER_ID_CHANNEL, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CHANNEL,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a channel
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage channels
     */
    @Action( ACTION_REMOVE_CHANNEL )
    public String doRemoveChannel( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CHANNEL ) );
        ChannelHome.remove( nId );

        int nIdChannelFront = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_CHANNEL_ID_FRONT,
                TicketingConstants.PROPERTY_UNSET_INT );

        if ( nId == nIdChannelFront )
        {
            PluginConfigurationService.set( PluginConfigurationService.PROPERTY_CHANNEL_ID_FRONT,
                TicketingConstants.PROPERTY_UNSET_INT );
        }

        addInfo( INFO_CHANNEL_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_CHANNELS );
    }

    /**
     * Returns the form to update info about a channel
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CHANNEL )
    public String getModifyChannel( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CHANNEL ) );

        if ( ( _channel == null ) || ( _channel.getId(  ) != nId ) )
        {
            _channel = ChannelHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_CHANNEL, _channel );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CHANNEL, TEMPLATE_MODIFY_CHANNEL, model );
    }

    /**
     * Process the change form of a channel
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_CHANNEL )
    public String doModifyChannel( HttpServletRequest request )
    {
        populate( _channel, request );

        // Check constraints
        if ( !validateBean( _channel, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CHANNEL, PARAMETER_ID_CHANNEL, _channel.getId(  ) );
        }

        ChannelHome.update( _channel );
        addInfo( INFO_CHANNEL_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_CHANNELS );
    }
}
