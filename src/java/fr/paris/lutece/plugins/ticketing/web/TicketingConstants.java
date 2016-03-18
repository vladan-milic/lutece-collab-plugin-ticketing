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
package fr.paris.lutece.plugins.ticketing.web;

import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * Class providing constants for Ticketing
 *
 */
public final class TicketingConstants
{
    // Templates
    public static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/ticketing/workflow/tasks_form_workflow.html";

    // Parameters
    public static final String PARAMETER_ID_TICKET = "id";
    public static final String PARAMETER_GRU_CUSTOMER_ID = "id_customer";
    public static final String PARAMETER_CUSTOMER_ID = "cid";
    public static final String PARAMETER_WORKFLOW_ID_ACTION = "id_action";
    public static final String PARAMETER_BACK = "back";
    public static final String PARAMETER_JSP_CONTROLLER = "jsp";
    public static final String PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION = "redirect";
    public static final String PARAMETER_ID_CHANNEL = "id_channel";
    public static final String PARAMETER_USER_SIGNATURE = "user_signature";
    public static final String PARAMETER_SELECTABLE_ID_CHANNEL_LIST = "selectable_channels";
    public static final String PARAMETER_SELECTED_ID_CHANNEL = "selected_channel";

    // Attributes
    public static final String ATTRIBUTE_HIDE_NEXT_STEP_BUTTON = "hide_next_button";
    public static final String ATTRIBUTE_RETURN_URL = "return_url";
    public static final String ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION = "redirect";
    public static final String ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO = "ticketing.workflow.action.message.info";

    // Markers
    public static final String MARK_TICKET = "ticket";
    public static final String MARK_ID_TICKET = PARAMETER_ID_TICKET;
    public static final String MARK_POCGRU_URL_360 = "url_poc_gru";
    public static final String MARK_LIST_READ_ONLY_HTML_RESPONSES = "read_only_reponses_html_list";
    public static final String MARK_WEBAPP_URL = "webapp_url";
    public static final String MARK_LOCALE = "locale";
    public static final String MARK_WORKFLOW_ACTION = "workflow_action";
    public static final String MARK_WORKFLOW_ID_ACTION = PARAMETER_WORKFLOW_ID_ACTION;
    public static final String MARK_HIDE_NEXT_STEP_BUTTON = ATTRIBUTE_HIDE_NEXT_STEP_BUTTON;
    public static final String MARK_TASKS_FORM = "tasks_form";
    public static final String MARK_FORM_ACTION = "form_action";
    public static final String MARK_PAGE = "page";
    public static final String MARK_TICKET_CREATION_RIGHT = "ticket_creation_right";
    public static final String MARK_TICKET_DELETION_RIGHT = "ticket_deletion_right";
    public static final String MARK_TICKET_MODIFICATION_RIGHT = "ticket_modification_right";
    public static final String MARK_POPIN_MODE = "popin_mode";
    public static final String MARK_JSP_CONTROLLER = "jsp_controller";
    public static final String MARK_CHANNELS_LIST = "channels_list";
    public static final String MARK_SELECTABLE_CHANNELS_LIST = "selectable_channels_list";
    public static final String MARK_PREFERRED_ID_CHANNEL = "preferred_id_channel";
    public static final String MARK_SELECTABLE_ID_CHANNEL_LIST = "selectable_id_channel_list";
    public static final String MARK_USER_SIGNATURE = "user_signature";
    public static final String MARK_AGENT_VIEW = "agent_view";

    // Properties
    public static final String PROPERTY_POCGRU_URL_360 = "ticketing.pocgru.url.360View";
    public static final String PROPERTY_REDIRECT_PREFIX = "ticketing.workflow.redirect.";
    public static final String PROPERTY_TICKET_CLOSE_ID = "ticketing.workflow.state.id.closed";
    public static final String PROPERTY_ADMINUSER_FRONT_ID = "ticketing.adminUser.front.id";
    private static final String PROPERTY_CHANNEL_ID = "ticketing.channel.webChannel.id";
    public static int WEB_ID_CHANNEL = 99;

    static
    {
        WEB_ID_CHANNEL = AppPropertiesService.getPropertyInt( PROPERTY_CHANNEL_ID, WEB_ID_CHANNEL );
    }

    // Session keys
    public static final String SESSION_NOT_VALIDATED_TICKET = "ticketing.ticketFormService.notValidatedTicket";
    public static final String SESSION_VALIDATED_TICKET_FORM = "ticketing.ticketFormService.validatedTicket";
    public static final String SESSION_TICKET_FORM_ERRORS = "ticketing.session.formErrors";
    public static final String SESSION_TICKET_FILTER = "ticketing.ticket_filter";
    public static final String SESSION_INSTANTRESPONSE_FILTER = "ticketing.instantresponse_filter";

    // Views
    public static final String VIEW_WORKFLOW_ACTION_FORM = "viewWorkflowActionForm";

    // Actions
    public static final String ACTION_DO_PROCESS_WORKFLOW_ACTION = "doProcessWorkflowAction";

    // Beans
    public static final String BEAN_ACTION_SERVICE = "workflow.actionService";

    // User preferences
    public static final String USER_PREFERENCE_SIGNATURE = "ticketingUserSignature";
    public static final String USER_PREFERENCE_CHANNELS_LIST = "ticketingUserChannelsList";
    public static final String USER_PREFERENCE_PREFERRED_CHANNEL = "ticketingUserPreferredChannel";

    // Other constants
    public static final String ADMIN_CONTROLLLER_PATH = "jsp/admin/plugins/ticketing/";
    public static final String JSP_MANAGE_TICKETS = "ManageTickets.jsp";
    public static final String JSP_VIEW_TICKET = "TicketView.jsp";
    public static final String ROLE_GRU_ADMIN = "gru_admin";
    public static final String ROLE_LEVEL_3 = "gru_level_3";
    public static final int NO_ID_CHANNEL = 0;
    public static final int TICKET_STATUS_IN_PROGRESS = 0;
    public static final int TICKET_STATUS_CLOSED = 1;


    /**
     * Default constructor
     */
    private TicketingConstants(  )
    {
    }
}
