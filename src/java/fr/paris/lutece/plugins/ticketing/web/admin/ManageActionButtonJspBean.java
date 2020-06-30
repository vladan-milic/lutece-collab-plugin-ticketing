/*
 * Copyright (c) 2002-2020, Mairie de Paris
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.groupaction.GroupAction;
import fr.paris.lutece.plugins.ticketing.business.groupaction.GroupActionHome;
import fr.paris.lutece.plugins.ticketing.business.parambouton.ParamBouton;
import fr.paris.lutece.plugins.ticketing.business.parambouton.ParamBoutonHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * This class provides the user interface to manage Category features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageActionButton.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT_ACTION_BUTTON" )
public class ManageActionButtonJspBean extends ManageAdminTicketingJspBean
{
    private static final long   serialVersionUID                         = 981626411759447723L;

    // Templates
    private static final String TEMPLATE_MANAGE_ACTION_BUTTON            = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "manage_action_button.html";
    private static final String TEMPLATE_CREATE_GROUP                    = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "create_group.html";
    private static final String TEMPLATE_EDIT_GROUP                      = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "edit_group.html";
    private static final String JSP_MANAGE_ACTION_BUTTON                 = "ManageActionButton.jsp";

    // Parameters
    private static final String PARAMETER_ID                             = "param_id";
    private static final String PARAMETER_GROUP_ID                       = "group_id";
    private static final String PARAMETER_ORDER_ID                       = "order_id";
    private static final String PARAMETER_ACTION_ID                      = "action_id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ACTION_BUTTON = "ticketing.manage_actionButton.pageTitle";

    // Markers
    private static final String MARK_GROUPE_LIST                         = "groupe_list";
    private static final String MARK_GROUPE                              = "groupe";
    private static final String MARK_PARAM_BOUTON_LIST                   = "param_bouton_list";
    private static final String MARK_ORDER_LIST                          = "order_list";

    // Views
    private static final String VIEW_MANAGE_ACTION_BUTTON                = "manageActionButton";
    private static final String VIEW_EDIT_ICON                           = "editIcon";

    // Actions
    private static final String ACTION_CHANGE_GROUP                      = "changeGroup";
    private static final String ACTION_CHANGE_ORDER                      = "changeOrder";
    private static final String ACTION_CHANGE_COLOR                      = "changeColor";
    private static final String ACTION_SAVE_ICON                         = "saveIcon";

    private static final String REGEX_ID                                 = "^[\\d]+$";
    private static final int    ID_GROUPE_NON_CONFIGURE                  = 1;

    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_ACTION_BUTTON, defaultView = true )
    public String getManageActionButton( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );

        Collection<HashMap<String, Object>> colGroupMap = new ArrayList<>( );

        // Récupération des groupes
        Collection<GroupAction> groupActionList = GroupActionHome.getGroupActionList( );

        for ( GroupAction groupAction : groupActionList )
        {
            // Récupération des paramètres de bouton pour chaque groupe
            HashMap<String, Object> groupMap = new HashMap<>( );
            groupMap.put( MARK_GROUPE, groupAction );
            colGroupMap.add( groupMap );

            // Groupe non configuré
            if ( groupAction.getIdGroup( ) == 1 )
            {
                List<ParamBouton> paramBoutonList = ParamBoutonHome.selectParamBoutonListWithoutGroup( );
                groupMap.put( MARK_PARAM_BOUTON_LIST, paramBoutonList );
            }
            else
            {
                groupMap.put( MARK_PARAM_BOUTON_LIST, ParamBoutonHome.selectParamBoutonListByGroup( groupAction.getIdGroup( ) ) );
            }

        }
        model.put( MARK_GROUPE_LIST, colGroupMap );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ACTION_BUTTON, TEMPLATE_MANAGE_ACTION_BUTTON, model );
    }

    @Action( ACTION_CHANGE_GROUP )
    public String doChangeGroup( HttpServletRequest request )
    {
        String strGroupId = request.getParameter( PARAMETER_GROUP_ID );
        String strParameterId = request.getParameter( PARAMETER_ID );
        String strActionId = request.getParameter( PARAMETER_ACTION_ID );

        // FeatureGroup featureGroup = FeatureGroupHome.findByPrimaryKey( strGroupId );

        // FeatureGroupHome.update( featureGroup );

        return redirectView( request, VIEW_MANAGE_ACTION_BUTTON );
    }

}
