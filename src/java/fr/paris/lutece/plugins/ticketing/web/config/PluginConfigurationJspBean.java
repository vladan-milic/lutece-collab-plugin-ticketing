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
package fr.paris.lutece.plugins.ticketing.web.config;

import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage the plugin configuration
 */
@Controller( controllerJsp = "ConfigurePlugin.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = PluginConfigurationJspBean.CONTROLLER_RIGHT )
public class PluginConfigurationJspBean extends MVCAdminJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    protected static final String CONTROLLER_RIGHT = "TICKETING_PLUGIN_CONFIGURATION";

    // templates
    private static final String TEMPLATE_CONFIGURE_PLUGIN = TicketingConstants.TEMPLATE_ADMIN_PATH +
        "config/configure_plugin.html";
    private static final String TEMPLATE_WORKFLOW_RELATED_PROPERTIES = TicketingConstants.TEMPLATE_ADMIN_PATH +
        "config/workflow_related_properties.html";

    // Parameters
    private static final String PARAMETER_WORKFLOW_ID = "id_workflow";
    private static final String PARAMETER_STATE_CLOSED_ID = "id_state_closed";

    // Properties for page titles

    // Marks
    private static final String MARK_WORKFLOW_ID = "id_workflow";
    private static final String MARK_LIST_WORKFLOWS = "list_workflows";
    private static final String MARK_WORKFLOW_RELATED_PROPERTIES = "workflow_related_properties";
    private static final String MARK_STATE_CLOSED_ID = "id_state_closed";
    private static final String MARK_LIST_STATES = "list_states";

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

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -1920398324341843326L;

    // Other constants
    private WorkflowService _workflowService = WorkflowService.getInstance(  );

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CONFIGURATION, defaultView = true )
    public String getManageConfiguration( HttpServletRequest request )
    {
        Map<String, Object> model = getModel(  );
        int nIdWorkflow = PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID,
                TicketingConstants.PROPERTY_UNSET_INT );

        model.put( MARK_WORKFLOW_ID, nIdWorkflow );

        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            ReferenceList listWorkflows = _workflowService.getWorkflowsEnabled( getUser(  ), getLocale(  ) );
            model.put( MARK_LIST_WORKFLOWS, listWorkflows );
            model.put( MARK_WORKFLOW_RELATED_PROPERTIES, buildWorkflowRelatedProperties( nIdWorkflow, getLocale(  ) ) );
        }

        return getPage( PROPERTY_PAGE_TITLE_CONFIGURE_PLUGIN, TEMPLATE_CONFIGURE_PLUGIN, model );
    }

    @Action( ACTION_MODIFIY_CONFIGURATION )
    public String doModifyConfiguration( HttpServletRequest request )
    {
        String strIdWorkflow = request.getParameter( PARAMETER_WORKFLOW_ID );
        String strIdStateClosed = request.getParameter( PARAMETER_STATE_CLOSED_ID );

        try
        {
            int nIdWorkflow = Integer.parseInt( strIdWorkflow );
            PluginConfigurationService.set( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID, nIdWorkflow );

            int nIdStateClosed = TicketingConstants.PROPERTY_UNSET_INT;

            if ( TicketingConstants.PROPERTY_UNSET_INT != nIdWorkflow )
            {
                nIdStateClosed = Integer.parseInt( strIdStateClosed );
            }

            PluginConfigurationService.set( PluginConfigurationService.PROPERTY_STATE_CLOSED_ID, nIdStateClosed );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( e );
            addError( ERROR_CONFIGURATION_SAVE_ABORTED, getLocale(  ) );

            return redirectView( request, VIEW_MANAGE_CONFIGURATION );
        }

        addInfo( INFO_CONFIGURATION_SAVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_CONFIGURATION );
    }

    @Action( ACTION_BUILD_WORKFLOW_RELATED_PROPERTIES )
    public String doBuildWorkflowRelatedProperties( HttpServletRequest request )
    {
        String strIdWorkflow = request.getParameter( PARAMETER_WORKFLOW_ID );
        String strResult = StringUtils.EMPTY;

        try
        {
            strResult = buildWorkflowRelatedProperties( Integer.parseInt( strIdWorkflow ), getLocale(  ) );
        }
        catch ( NumberFormatException e )
        {
            AppLogService.error( e );
        }

        return strResult;
    }

    private String buildWorkflowRelatedProperties( int nIdWorkflow, Locale locale )
    {
        String strResult = StringUtils.EMPTY;

        if ( TicketingConstants.PROPERTY_UNSET_INT != nIdWorkflow )
        {
            Map<String, Object> model = getModel(  );

            ReferenceItem referenceItemDefault = new ReferenceItem(  );
            referenceItemDefault.setCode( String.valueOf( TicketingConstants.PROPERTY_UNSET_INT ) );
            referenceItemDefault.setName( StringUtils.EMPTY );

            Collection<State> listStates = _workflowService.getAllStateByWorkflow( nIdWorkflow, getUser(  ) );
            ReferenceList referenceListStates = ReferenceList.convert( listStates, "id", "name", true );

            referenceListStates.add( 0, referenceItemDefault );

            model.put( MARK_LIST_STATES, referenceListStates );
            model.put( MARK_STATE_CLOSED_ID,
                PluginConfigurationService.getString( PluginConfigurationService.PROPERTY_STATE_CLOSED_ID, null ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_WORKFLOW_RELATED_PROPERTIES, locale, model );

            strResult = template.getHtml(  );
        }

        return strResult;
    }
}
