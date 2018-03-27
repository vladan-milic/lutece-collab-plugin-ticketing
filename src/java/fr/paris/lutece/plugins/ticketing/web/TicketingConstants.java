/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
    public static final String TEMPLATE_TASKS_FORM_WORKFLOW                                = "admin/plugins/ticketing/workflow/tasks_form_workflow.html";

    // Parameters
    public static final String PARAMETER_ID_TICKET                                         = "id";
    public static final String PARAMETER_GRU_CUSTOMER_ID                                   = "id_customer";
    public static final String PARAMETER_CUSTOMER_ID                                       = "cid";
    public static final String PARAMETER_GUID                                              = "guid";
    public static final String PARAMETER_WORKFLOW_ID_ACTION                                = "id_action";
    public static final String PARAMETER_BACK                                              = "back";
    public static final String PARAMETER_JSP_CONTROLLER                                    = "jsp";
    public static final String PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION                    = "redirect";
    public static final String PARAMETER_ID_CHANNEL                                        = "id_channel";
    public static final String PARAMETER_USER_SIGNATURE                                    = "user_signature";
    public static final String PARAMETER_SELECTABLE_ID_CHANNEL_LIST                        = "selectable_channels";
    public static final String PARAMETER_SELECTED_ID_CHANNEL                               = "selected_channel";
    public static final String PARAMETER_SIGNATURE                                         = "signature";
    public static final String PARAMETER_TIMESTAMP                                         = "timestamp";
    public static final String PARAMETER_FILTER_URGENCY                                    = "fltr_new_urgency";
    public static final String PARAMETER_FILTER_TYPE_ID                                    = "fltr_id_type";
    public static final String PARAMETER_FILTER_OPEN_SINCE                                 = "fltr_open_since";
    public static final String PARAMETER_FILTER_STATE_ID                                   = "fltr_state_ids";
    public static final String PARAMETER_TICKET_TYPE_ID                                    = "id_ticket_type";
    public static final String PARAMETER_TICKET_DOMAIN_ID                                  = "id_ticket_domain";
    public static final String PARAMETER_TICKET_PRECISION_ID                               = "id_ticket_precision";
    public static final String PARAMETER_TICKET_CATEGORY_ID                                = "id_ticket_category";
    public static final String PARAMETER_SELECTED_TICKETS                                  = "selected_tickets";
    public static final String PARAMETER_IS_MASS_ACTION                                    = "is_mass_action";
    public static final String PARAMETER_CATEGORY_ID                                       = "id_category_";
    public static final String PARAMETER_ID_FORM = "form";

    // Attributes
    public static final String ATTRIBUTE_HIDE_NEXT_STEP_BUTTON                             = "hide_next_button";
    public static final String ATTRIBUTE_RETURN_URL                                        = "return_url";
    public static final String ATTRIBUTE_REDIRECT_AFTER_WORKFLOW_ACTION                    = PARAMETER_REDIRECT_AFTER_WORKFLOW_ACTION;
    public static final String ATTRIBUTE_WORKFLOW_ACTION_MESSAGE_INFO                      = "ticketing.workflow.action.message.info";
    public static final String ATTRIBUTE_IS_DISPLAY_FRONT                                  = "bDisplayFront";
    public static final String ATTRIBUTE_IS_UNIT_CHANGED                                   = "bTicketingUnitChanged";
    public static final String ATTRIBUTE_BYPASS_ASSSIGN_TO_ME                              = "bBypassAssignToMe";
    public static final String ATTRIBUTE_MODIFY_ACTION_MESSAGE_INFO                        = "ticketing.modify.action.message.info";
    public static final String ATTRIBUTE_IS_PROCESS_CONTENT                                = "is_process_content";

    // Markers
    public static final String MARK_TICKET = "ticket";
    public static final String MARK_TICKET_REFERENCE = "reference";
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
    public static final String MARK_TICKET_EXPORT_RIGHT                                    = "ticket_export_right";
    public static final String MARK_POPIN_MODE = "popin_mode";
    public static final String MARK_JSP_CONTROLLER = "jsp_controller";
    public static final String MARK_CHANNELS_LIST = "channels_list";
    public static final String MARK_SELECTABLE_CHANNELS_LIST = "selectable_channels_list";
    public static final String MARK_PREFERRED_ID_CHANNEL = "preferred_id_channel";
    public static final String MARK_SELECTABLE_ID_CHANNEL_LIST = "selectable_id_channel_list";
    public static final String MARK_USER_SIGNATURE = "user_signature";
    public static final String MARK_AGENT_VIEW = "agent_view";
    public static final String MARK_CREATION_DATE_AS_DATE = "creation_date_as_date";
    public static final String MARK_NEXT_TICKET = "next_ticket";
    public static final String MARK_PREVIOUS_TICKET = "previous_ticket";
    public static final String MARK_AVATAR_AVAILABLE = "avatar_available";
    public static final String MARK_SELECTED_TICKETS = PARAMETER_SELECTED_TICKETS;
    public static final String MARK_TICKET_COMMENT = "ticket_processed_comment";
    public static final String MARK_TICKET_CATEGORIES_TREE = "categories_tree";
    public static final String MARK_TICKET_CATEGORIES_DEPTHS = "categories_depths";

    // Properties
    public static final String PROPERTY_POCGRU_URL_360                                     = "ticketing.pocgru.url.360View";
    public static final String PROPERTY_REDIRECT_PREFIX                                    = "ticketing.workflow.redirect.";
    public static final String VALIDATION_ATTRIBUTES_PREFIX                                = "ticketing.model.entity.ticket.attribute.";
    public static final String PROPERTIES_APPLICATION_CODE                                 = "ticketing.application.code";
    public static final String PROPERTIES_ATTRIBUTE_USER_GENDER                            = "ticketing.identity.attribute.user.gender";
    public static final String PROPERTIES_ATTRIBUTE_USER_NAME_GIVEN                        = "ticketing.identity.attribute.user.name.given";
    public static final String PROPERTIES_ATTRIBUTE_USER_PREFERRED_NAME                    = "ticketing.identity.attribute.user.name.family";
    public static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_ONLINE_EMAIL             = "ticketing.identity.attribute.user.home-info.online.email";
    public static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_TELEPHONE_NUMBER = "ticketing.identity.attribute.user.home-info.telecom.telephone.number";
    public static final String PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_MOBILE_NUMBER    = "ticketing.identity.attribute.user.home-info.telecom.mobile.number";
    public static final String PROPERTIES_CATEGORY_DEPTH_MIN                               = "ticketing.category.depth.min";
    public static final String PROPERTIES_CATEGORY_DEPTH_MAX                               = "ticketing.category.depth.max";
    public static final String PROPERTIES_CATEGORY_DEPTH_FILTER                            = "ticketing.category.depth.filter";
    public static final String PROPERTIES_CATEGORY_DEPTH_RBAC_RESOURCE                     = "ticketing.category.depth.rbac.resource";
    public static final String PROPERTIES_DOMAIN_DEPTH                                     = "ticketing.domain.depth";
    public static final String PROPERTIES_TYPE_DEPTH                                       = "ticketing.type.depth";
    public static final String PROPERTIES_THEMATIC_DEPTH                                   = "ticketing.thematic.depth";
    public static final String PROPERTIES_PRECISION_DEPTH                                  = "ticketing.precision.depth";

    // Session keys
    public static final String SESSION_NOT_VALIDATED_TICKET                                = "ticketing.ticketFormService.notValidatedTicket";
    public static final String SESSION_VALIDATED_TICKET_FORM                               = "ticketing.ticketFormService.validatedTicket";
    public static final String SESSION_TICKET_CONFIRM_MESSAGE                              = "ticketing.session.confirmMessage";
    public static final String SESSION_TICKET_FORM_ERRORS                                  = "ticketing.session.formErrors";
    public static final String SESSION_TICKET_FILTER                                       = "ticketing.ticket_filter";
    public static final String SESSION_INSTANTRESPONSE_FILTER                              = "ticketing.instantresponse_filter";
    public static final String SESSION_LIST_TICKETS_NAVIGATION                             = "ticketing.list.navigation.detail";

    // Views
    public static final String VIEW_WORKFLOW_ACTION_FORM                                   = "viewWorkflowActionForm";
    public static final String VIEW_WORKFLOW_MASS_ACTION_FORM                              = "viewWorkflowMassActionForm";
    // Actions
    public static final String ACTION_DO_PROCESS_WORKFLOW_ACTION                           = "doProcessWorkflowAction";

    // Beans
    public static final String BEAN_ACTION_SERVICE                                         = "workflow.actionService";

    // Errors
    public static final String ERROR_TICKET_CREATION_ABORTED                               = "ticketing.error.ticket.creation.aborted.backoffice";
    public static final String ERROR_INDEX_TICKET_FAILED_BACK                              = "ticketing.error.ticket.indexing.failed.backoffice";
    public static final String ERROR_INDEX_TICKET_FAILED_FRONT                             = "ticketing.error.ticket.indexing.failed.frontoffice";
    public static final String MESSAGE_ERROR_TICKET_CATEGORY_NOT_SELECTED                  = "ticketing.error.ticketCategory.notSelected";
    public static final String MESSAGE_ERROR_TICKET_CATEGORY_PRECISION_NOT_SELECTED        = "ticketing.error.ticketCategory.precision.notSelected";
    public static final String MESSAGE_ERROR_TICKET_NOT_EXISTS                             = "ticketing.error.ticket.notExist";
    public static final String ERROR_CATEGORY_UNKNOWN                                      = "ticketing.error.category.unknown";
    public static final String ERROR_CATEGORY_NOT_SELECTED                                 = "ticketing.error.category.notSelected";

    // User preferences
    public static final String USER_PREFERENCE_CREATION_DATE_DISPLAY                       = "creationDateDisplay";
    public static final String USER_PREFERENCE_CREATION_DATE_DISPLAY_DATE                  = "date";
    public static final String USER_PREFERENCE_SIGNATURE                                   = "ticketingUserSignature";
    public static final String USER_PREFERENCE_CHANNELS_LIST                               = "ticketingUserChannelsList";
    public static final String USER_PREFERENCE_PREFERRED_CHANNEL                           = "ticketingUserPreferredChannel";

    // Plugins
    public static final String PLUGIN_AVATAR                                               = "avatar";

    // Identity attributes
    public static final String ATTRIBUTE_IDENTITY_GENDER                                   = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_GENDER );
    public static final String ATTRIBUTE_IDENTITY_NAME_GIVEN                               = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_NAME_GIVEN );
    public static final String ATTRIBUTE_IDENTITY_NAME_PREFERRED_NAME                      = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_PREFERRED_NAME );
    public static final String ATTRIBUTE_IDENTITY_HOMEINFO_ONLINE_EMAIL                    = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_ONLINE_EMAIL );
    public static final String ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_TELEPHONE_NUMBER        = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_TELEPHONE_NUMBER );
    public static final String ATTRIBUTE_IDENTITY_HOMEINFO_TELECOM_MOBILE_NUMBER           = AppPropertiesService.getProperty( PROPERTIES_ATTRIBUTE_USER_HOMEINFO_TELECOM_MOBILE_NUMBER );

    // FIXME : the application code must be provided by the caller
    public static final String APPLICATION_CODE                                            = AppPropertiesService.getProperty( PROPERTIES_APPLICATION_CODE );

    // Other constants
    public static final String ADMIN_CONTROLLLER_PATH                                      = "jsp/admin/plugins/ticketing/";
    public static final String ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH                        = ADMIN_CONTROLLLER_PATH + "admin/";
    public static final String TEMPLATE_ADMIN_PATH                                         = "/admin/plugins/ticketing/";
    public static final String TEMPLATE_ADMIN_TICKET_FEATURE_PATH                          = TEMPLATE_ADMIN_PATH + "ticket/";
    public static final String TEMPLATE_ADMIN_INSTANTRESPONSE_FEATURE_PATH                 = TEMPLATE_ADMIN_PATH + "instantresponse/";
    public static final String TEMPLATE_ADMIN_ADMIN_FEATURE_PATH                           = TEMPLATE_ADMIN_PATH + "admin/";
    public static final String TEMPLATE_ADMIN_TICKETFORM_FEATURE_PATH                      = TEMPLATE_ADMIN_PATH + "ticketform/";
    public static final String TEMPLATE_ADMIN_TICKETINPUTS_FEATURE_PATH                    = TEMPLATE_ADMIN_PATH + "ticketinputs/";
    public static final String TEMPLATE_FRONT_PATH                                         = "/skin/plugins/ticketing/";
    public static final String TEMPLATE_FRONT_TICKET_FEATURE_PATH                          = TEMPLATE_FRONT_PATH + "ticket/";
    public static final String JSP_MANAGE_TICKETS                                          = "ManageTickets.jsp";
    public static final String JSP_VIEW_TICKET                                             = "TicketView.jsp";
    public static final String ROLE_GRU_ADMIN                                              = "gru_admin";
    public static final String ROLE_LEVEL_3                                                = "gru_level_3";
    public static final String FIELD_ID_SEPARATOR                                          = ",";
    public static final String EQUAL_SYMBOL                                                = "=";
    public static final int    NO_ID_CHANNEL                                               = 0;
    public static final int    TICKET_STATUS_IN_PROGRESS                                   = 0;
    public static final int    CONSTANT_ZERO                                               = 0;
    public static final int    TICKET_STATUS_CLOSED                                        = 1;
    public static final int    PROPERTY_UNSET_INT                                          = -1;
    public static final int    NO_PARENT_ID                                                = -1;
    public static final String NO_ID_STRING                                                = "-1";
    public static final String RESOURCE_TYPE_INPUT                                         = "TICKET_INPUT";
    public static final String HTML_BR_BALISE                                              = "<br>";
    public static final String REDIRECT_VIEW_LIST                                          = "list";
    public static final int    DEFAULT_MARKING                                             = 0;
    public static final String MESSAGE_MARK                                                = "<!-- MESSAGE-IN-WORKFLOW -->";

    public static final int    CATEGORY_DEPTH                                              = 3;
    public static final int    PRECISION_DEPTH                                             = 4;

    public static final int    CATEGORY_DEPTH_MIN                                          = Integer.parseInt( AppPropertiesService.getProperty( PROPERTIES_CATEGORY_DEPTH_MIN ) );
    public static final int    CATEGORY_DEPTH_MAX                                          = Integer.parseInt( AppPropertiesService.getProperty( PROPERTIES_CATEGORY_DEPTH_MAX ) );
    public static final int    CATEGORY_DEPTH_FILTER                                       = Integer.parseInt( AppPropertiesService.getProperty( PROPERTIES_CATEGORY_DEPTH_FILTER ) );
    public static final int    CATEGORY_DEPTH_RBAC_RESOURCE                                = Integer.parseInt( AppPropertiesService.getProperty( PROPERTIES_CATEGORY_DEPTH_RBAC_RESOURCE ) );
    public static final int    TYPE_DEPTH                                                  = Integer.parseInt( AppPropertiesService.getProperty( PROPERTIES_TYPE_DEPTH ) );
    public static final int    DOMAIN_DEPTH                                                = Integer.parseInt( AppPropertiesService.getProperty( PROPERTIES_DOMAIN_DEPTH ) );
    public static final int    THEMATIC_DEPTH                                              = Integer.parseInt( AppPropertiesService.getProperty( PROPERTIES_THEMATIC_DEPTH ) );

    /**
     * Default constructor
     */
    private TicketingConstants( )
    {
    }
}
