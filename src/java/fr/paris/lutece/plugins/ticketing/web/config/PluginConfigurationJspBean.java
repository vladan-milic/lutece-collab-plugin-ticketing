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
package fr.paris.lutece.plugins.ticketing.web.config;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class provides the user interface to manage the plugin configuration
 */
@Controller( controllerJsp = "ConfigurePlugin.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = PluginConfigurationJspBean.CONTROLLER_RIGHT )
public class PluginConfigurationJspBean extends MVCAdminJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants
    protected static final String CONTROLLER_RIGHT = "TICKETING_PLUGIN_CONFIGURATION";

    // templates
    private static final String TEMPLATE_CONFIGURE_PLUGIN = TicketingConstants.TEMPLATE_ADMIN_PATH + "config/configure_plugin.html";
    private static final String TEMPLATE_WORKFLOW_RELATED_PROPERTIES = TicketingConstants.TEMPLATE_ADMIN_PATH + "config/workflow_related_properties.html";

    // Parameters
    private static final String PARAMETER_WORKFLOW_ID = "id_workflow";
    private static final String PARAMETER_STATE_CLOSED_ID = "id_state_closed";
    private static final String PARAMETER_STATES_SELECTED = "states_selected";
    private static final String PARAMETER_STATES_SELECTED_FOR_ROLE_ROLE = "states_selected_for_role_role";
    private static final String PARAMETER_STATES_SELECTED_FOR_ROLE_STATES_PREFIX = "states_selected_for_role_states_";
    private static final String PARAMETER_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME = "actions_filtered_when_assigned_to_me";
    private static final String PARAMETER_ADMIN_USER_ID_FRONT = "admin_user_id_front";
    private static final String PARAMETER_CHANNEL_ID_FRONT = "channel_id_front";

    // Properties for page titles

    // Marks
    private static final String MARK_WORKFLOW_ID = PARAMETER_WORKFLOW_ID;
    private static final String MARK_LIST_WORKFLOWS = "list_workflows";
    private static final String MARK_WORKFLOW_RELATED_PROPERTIES = "workflow_related_properties";
    private static final String MARK_STATE_CLOSED_ID = PARAMETER_STATE_CLOSED_ID;
    private static final String MARK_STATES = "states";
    private static final String MARK_STATES_SELECTED = PARAMETER_STATES_SELECTED;
    private static final String MARK_STATES_SELECTED_FOR_ROLES = "states_selected_for_roles";
    private static final String MARK_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME = PARAMETER_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME;
    private static final String MARK_ACTIONS = "actions";
    private static final String MARK_ADMIN_USERS = "admin_users";
    private static final String MARK_ADMIN_USER_ID_FRONT = PARAMETER_ADMIN_USER_ID_FRONT;
    private static final String MARK_CHANNELS = "channels";
    private static final String MARK_CHANNEL_ID_FRONT = PARAMETER_CHANNEL_ID_FRONT;
    private static final String MARK_ID_ATTACHMENTS_ENTRY = "id_entry";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_CONFIGURE_PLUGIN = "ticketing.configure_plugin.pageTitle";

    // Views
    private static final String VIEW_MANAGE_CONFIGURATION = "getManageConfiguration";

    // Actions
    private static final String ACTION_MODIFIY_CONFIGURATION = "modifyConfiguration";
    private static final String ACTION_BUILD_WORKFLOW_RELATED_PROPERTIES = "buildWorkflowRelatedProperties";

    // Infos
    private static final String INFO_CONFIGURATION_SAVED = "ticketing.info.configuration.saved";

    // Errors
    private static final String ERROR_CONFIGURATION_SAVE_ABORTED = "ticketing.error.configuration.save.aborted";
    private static final String ERROR_CONFIGURATION_STATES_SELECTED_FOR_ROLES_DOUBLE = "ticketing.error.configuration.states.selected.fro.roles.double";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -1920398324341843326L;

    // Services
    private static WorkflowService _workflowService = WorkflowService.getInstance( );
    private static IActionService _actionService = SpringContextService.getBean( TicketingConstants.BEAN_ACTION_SERVICE );

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CONFIGURATION, defaultView = true )
    public String getManageConfiguration( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );

        manageWorkflowProperties( model );
        manageFrontOfficeProperties( model );
        manageBackOfficeProperties( model );

        return getPage( PROPERTY_PAGE_TITLE_CONFIGURE_PLUGIN, TEMPLATE_CONFIGURE_PLUGIN, model );
    }

    /**
     * Saves the modified configuration
     * 
     * @param request
     *            the request
     * @return the page to redirect when the modification is done
     */
    @Action( ACTION_MODIFIY_CONFIGURATION )
    public String doModifyConfiguration( HttpServletRequest request )
    {
        if ( !validate( request ) )
        {
            return redirectView( request, VIEW_MANAGE_CONFIGURATION );
        }

        try
        {
            saveWorkflowProperties( request );
            saveFrontOfficeProperties( request );
        }
        catch( Exception e )
        {
            AppLogService.error( e );
            addError( ERROR_CONFIGURATION_SAVE_ABORTED, getLocale( ) );

            return redirectView( request, VIEW_MANAGE_CONFIGURATION );
        }

        addInfo( INFO_CONFIGURATION_SAVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_CONFIGURATION );
    }

    /**
     * Builds the properties related to the workflow
     * 
     * @param request
     *            the request
     * @return the HTML for the properties
     */
    @Action( ACTION_BUILD_WORKFLOW_RELATED_PROPERTIES )
    public String doBuildWorkflowRelatedProperties( HttpServletRequest request )
    {
        String strIdWorkflow = request.getParameter( PARAMETER_WORKFLOW_ID );
        String strResult = StringUtils.EMPTY;

        try
        {
            strResult = buildWorkflowRelatedProperties( Integer.parseInt( strIdWorkflow ), getLocale( ) );
        }
        catch( NumberFormatException e )
        {
            AppLogService.error( e );
        }

        return strResult;
    }

    /**
     * Inserts the properties related to the workflow in the specified model
     * 
     * @param model
     *            the model
     */
    private void manageWorkflowProperties( Map<String, Object> model )
    {
        int nIdWorkflow = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID, TicketingConstants.PROPERTY_UNSET_INT );

        model.put( MARK_WORKFLOW_ID, nIdWorkflow );

        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            ReferenceList listWorkflows = _workflowService.getWorkflowsEnabled( getUser( ), getLocale( ) );
            model.put( MARK_LIST_WORKFLOWS, listWorkflows );
            model.put( MARK_WORKFLOW_RELATED_PROPERTIES, buildWorkflowRelatedProperties( nIdWorkflow, getLocale( ) ) );
        }
    }

    /**
     * Inserts the properties related to the front office in the specified model
     * 
     * @param model
     *            the model
     */
    private void manageFrontOfficeProperties( Map<String, Object> model )
    {
        // Admin user for front office
        int nIdAdminUser = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_ADMINUSER_ID_FRONT, TicketingConstants.PROPERTY_UNSET_INT );
        model.put( MARK_ADMIN_USER_ID_FRONT, nIdAdminUser );

        // All admin users
        Collection<AdminUser> adminUsers = AdminUserHome.findUserList( );
        ReferenceList referenceListAdminUser = buildReferenceList( adminUsers, "userId", "accessCode" );
        model.put( MARK_ADMIN_USERS, referenceListAdminUser );

        // Channel for front office
        int nIdChannel = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_CHANNEL_ID_FRONT, TicketingConstants.PROPERTY_UNSET_INT );
        model.put( MARK_CHANNEL_ID_FRONT, nIdChannel );

        // All channels
        ReferenceList referenceListChannel = buildReferenceList( ChannelHome.getReferenceList( ) );
        model.put( MARK_CHANNELS, referenceListChannel );
    }

    /**
     * Inserts the properties related to the back office in the specified model
     * 
     * @param model
     *            the model
     */
    private void manageBackOfficeProperties( Map<String, Object> model )
    {
        int nIdEntryAttachmentsReply = AppPropertiesService.getPropertyInt( TicketingConstants.PROPERTY_ENTRY_REPLY_ATTACHMENTS_ID,
                TicketingConstants.PROPERTY_UNSET_INT );
        model.put( MARK_ID_ATTACHMENTS_ENTRY, nIdEntryAttachmentsReply );
    }

    /**
     * Builds the properties related to the workflow
     * 
     * @param nIdWorkflow
     *            the workflow id
     * @param locale
     *            the locale
     * @return the HTML for the properties
     */
    private String buildWorkflowRelatedProperties( int nIdWorkflow, Locale locale )
    {
        String strResult = StringUtils.EMPTY;

        if ( TicketingConstants.PROPERTY_UNSET_INT != nIdWorkflow )
        {
            Map<String, Object> model = getModel( );

            // All states
            ReferenceList referenceListStates = buildReferenceList( _workflowService.getAllStateByWorkflow( nIdWorkflow, getUser( ) ), "id", "name" );
            model.put( MARK_STATES, referenceListStates );

            // Selected states
            model.put( MARK_STATES_SELECTED, PluginConfigurationService.getStringList( PluginConfigurationService.PROPERTY_STATES_SELECTED, null ) );

            // Selected states for roles
            Map<String, List<String>> mapStatesForRoles = PluginConfigurationService.getStringListByPrefix(
                    PluginConfigurationService.PROPERTY_STATES_SELECTED_FOR_ROLE_PREFIX, null );

            if ( mapStatesForRoles != null )
            {
                List<SelectedStatesForRole> listStatesSelectedForRole = new ArrayList<SelectedStatesForRole>( );

                for ( Map.Entry<String, List<String>> entry : mapStatesForRoles.entrySet( ) )
                {
                    SelectedStatesForRole selectedStatesFroRole = new SelectedStatesForRole( entry.getKey( ), entry.getValue( ) );

                    listStatesSelectedForRole.add( selectedStatesFroRole );
                }

                model.put( MARK_STATES_SELECTED_FOR_ROLES, listStatesSelectedForRole );
            }

            // Closed state
            model.put( MARK_STATE_CLOSED_ID, PluginConfigurationService.getString( PluginConfigurationService.PROPERTY_STATE_CLOSED_ID, null ) );

            // All actions
            ActionFilter actionFilter = new ActionFilter( );
            actionFilter.setIdWorkflow( nIdWorkflow );

            ReferenceList referenceListActions = buildReferenceList( _actionService.getListActionByFilter( actionFilter ), "id", "name" );
            model.put( MARK_ACTIONS, referenceListActions );

            // Filtered actions when assigned to me
            model.put( MARK_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME,
                    PluginConfigurationService.getStringList( PluginConfigurationService.PROPERTY_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME, null ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_WORKFLOW_RELATED_PROPERTIES, locale, model );

            strResult = template.getHtml( );
        }

        return strResult;
    }

    /**
     * Saves the properties related to the workflow
     * 
     * @param request
     *            the request
     */
    private static void saveWorkflowProperties( HttpServletRequest request )
    {
        int nIdWorkflow = TicketingConstants.PROPERTY_UNSET_INT;
        int nIdStateClosed = TicketingConstants.PROPERTY_UNSET_INT;
        List<Integer> listStateSelected = new ArrayList<Integer>( );
        List<Integer> listFilteredActionsWhenAssignedToMe = new ArrayList<Integer>( );

        PluginConfigurationService.removeByPrefix( PluginConfigurationService.PROPERTY_STATES_SELECTED_FOR_ROLE_PREFIX );

        String strIdWorkflow = request.getParameter( PARAMETER_WORKFLOW_ID );

        if ( strIdWorkflow != null )
        {
            nIdWorkflow = Integer.parseInt( strIdWorkflow );

            if ( TicketingConstants.PROPERTY_UNSET_INT != nIdWorkflow )
            {
                // Manage selected states
                listStateSelected = RequestUtils.extractIdList( request, PARAMETER_STATES_SELECTED );

                // Manage selected states for roles
                String [ ] listStateSelectedForRoleRoles = request.getParameterValues( PARAMETER_STATES_SELECTED_FOR_ROLE_ROLE );

                if ( listStateSelectedForRoleRoles != null )
                {
                    for ( String strRole : listStateSelectedForRoleRoles )
                    {
                        if ( !StringUtils.isEmpty( strRole ) )
                        {
                            List<Integer> listStateSelectedForRoleIds = RequestUtils.extractIdList( request, PARAMETER_STATES_SELECTED_FOR_ROLE_STATES_PREFIX
                                    + strRole );

                            PluginConfigurationService.set( PluginConfigurationService.PROPERTY_STATES_SELECTED_FOR_ROLE_PREFIX + strRole,
                                    listStateSelectedForRoleIds );
                        }
                    }
                }

                // Manage closed state
                nIdStateClosed = RequestUtils.extractId( request, PARAMETER_STATE_CLOSED_ID );

                // Manage filtered actions when assigned to me
                listFilteredActionsWhenAssignedToMe = RequestUtils.extractIdList( request, PARAMETER_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME );
            }
        }

        PluginConfigurationService.set( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID, nIdWorkflow );
        PluginConfigurationService.set( PluginConfigurationService.PROPERTY_STATE_CLOSED_ID, nIdStateClosed );
        PluginConfigurationService.set( PluginConfigurationService.PROPERTY_STATES_SELECTED, listStateSelected );
        PluginConfigurationService.set( PluginConfigurationService.PROPERTY_ACTIONS_FILTERED_WHEN_ASSIGNED_TO_ME, listFilteredActionsWhenAssignedToMe );
    }

    /**
     * Saves the properties related to the front office
     * 
     * @param request
     *            the request
     */
    private static void saveFrontOfficeProperties( HttpServletRequest request )
    {
        int nIdAdminUser = TicketingConstants.PROPERTY_UNSET_INT;
        String strIdAdminUser = request.getParameter( PARAMETER_ADMIN_USER_ID_FRONT );

        if ( strIdAdminUser != null )
        {
            nIdAdminUser = Integer.parseInt( strIdAdminUser );
        }

        PluginConfigurationService.set( PluginConfigurationService.PROPERTY_ADMINUSER_ID_FRONT, nIdAdminUser );

        int nIdChannel = TicketingConstants.PROPERTY_UNSET_INT;
        String strIdChannel = request.getParameter( PARAMETER_CHANNEL_ID_FRONT );

        if ( strIdChannel != null )
        {
            nIdChannel = Integer.parseInt( strIdChannel );
        }

        PluginConfigurationService.set( PluginConfigurationService.PROPERTY_CHANNEL_ID_FRONT, nIdChannel );
    }

    /**
     * Builds a ReferenceList from the specified Collection. The ReferenceList is built with an empty value in the first position.
     * 
     * @param collection
     *            the Collection used to build the ReferenceList
     * @param strCode
     *            the code of the ReferenceItem
     * @param strName
     *            the name of the ReferenceItem
     * @return the ReferenceList
     */
    @SuppressWarnings( "rawtypes" )
    private static ReferenceList buildReferenceList( Collection collection, String strCode, String strName )
    {
        ReferenceList referenceList = TicketUtils.createReferenceList( StringUtils.EMPTY, TicketingConstants.PROPERTY_UNSET_INT );

        referenceList.addAll( ReferenceList.convert( collection, strCode, strName, true ) );

        return referenceList;
    }

    /**
     * Builds a ReferenceList from the specified ReferenceList. The ReferenceList is built with an empty value in the first position.
     * 
     * @param referenceList
     *            the ReferenceList used to build the ReferenceList
     * @return the ReferenceList
     */
    private static ReferenceList buildReferenceList( ReferenceList referenceList )
    {
        ReferenceList referenceListOneItem = TicketUtils.createReferenceList( StringUtils.EMPTY, TicketingConstants.PROPERTY_UNSET_INT );

        referenceListOneItem.addAll( referenceList );

        return referenceListOneItem;
    }

    /**
     * Tests whether the form is valid or not.
     * 
     * @param request
     *            the request
     * @return {@code true} if the form is valid, {@code false} otherwise
     */
    private boolean validate( HttpServletRequest request )
    {
        boolean bIsValidated = true;

        String [ ] listStateSelectedForRoleRoles = request.getParameterValues( PARAMETER_STATES_SELECTED_FOR_ROLE_ROLE );

        if ( listStateSelectedForRoleRoles != null )
        {
            List<String> setRoles = new ArrayList<String>( );

            for ( String strRole : listStateSelectedForRoleRoles )
            {
                if ( !StringUtils.isEmpty( strRole ) )
                {
                    if ( setRoles.contains( strRole ) )
                    {
                        addError( ERROR_CONFIGURATION_STATES_SELECTED_FOR_ROLES_DOUBLE, getLocale( ) );
                        bIsValidated = false;

                        break;
                    }
                    else
                    {
                        setRoles.add( strRole );
                    }
                }
            }
        }

        return bIsValidated;
    }

    /**
     * This class represents a list of states for a role
     *
     */
    public static final class SelectedStatesForRole
    {
        private final String _strRole;
        private final List<String> _listState;

        /**
         * Constructor
         * 
         * @param strRole
         *            the role
         * @param listState
         *            the list of states
         */
        public SelectedStatesForRole( String strRole, List<String> listState )
        {
            _strRole = strRole;
            _listState = listState;
        }

        /**
         * Gets the role
         * 
         * @return the role
         */
        public String getRole( )
        {
            return _strRole;
        }

        /**
         * Gets the states
         * 
         * @return the states
         */
        public List<String> getStates( )
        {
            return _listState;
        }
    }
}
