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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;

/**
 * This class provides the user interface to manage Channel features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageWorkflow.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class WorkflowJspBean extends ManageAdminTicketingJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_WORKFLOW            = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "manage_workflow.html";

    // Parameters
    private static final String PARAMETER_ID_WORKFLOW               = "id_workflow";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_WORKFLOW = "ticketing.manage_workflow.pageTitle";

    // Markers
    private static final String MARK_WORKFLOW_LIST                  = "workflow_list";
    private static final String MARK_WORKFLOW                       = "workflow_id";
    // Views
    private static final String VIEW_MANAGE_WORKFLOW                = "manageWorkflow";

    // Actions
    private static final String ACTION_MODIFY_WORKFLOW              = "modifyWorkflow";

    private static final String INFO_WORKFLOW_UPDATED               = "ticketing.info.workflow.updated";

    private static final long   serialVersionUID                    = 1L;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_WORKFLOW, defaultView = true )
    public String getManageWorkflow( HttpServletRequest request )
    {
        List<ReferenceItem> listChannels = WorkflowService.getInstance( ).getWorkflowsEnabled( getUser( ), getLocale( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_WORKFLOW_LIST, listChannels );
        model.put( MARK_WORKFLOW, Integer.parseInt( DatastoreService.getDataValue( TicketingConstants.PROPERTY_GLOBAL_WORKFLOW_ID, TicketingConstants.DEFAULT_GLOBAL_WORKFLOW_ID ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_WORKFLOW, TEMPLATE_MANAGE_WORKFLOW, model );
    }

    /**
     * Process the change form of a channel
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_WORKFLOW )
    public String doModifyWorkflow( HttpServletRequest request )
    {
        String idWorkflow = request.getParameter( PARAMETER_ID_WORKFLOW );
        DatastoreService.setDataValue( TicketingConstants.PROPERTY_GLOBAL_WORKFLOW_ID, idWorkflow );

        addInfo( INFO_WORKFLOW_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_WORKFLOW );
    }
}
