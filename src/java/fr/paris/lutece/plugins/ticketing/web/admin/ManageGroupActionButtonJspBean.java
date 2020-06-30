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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.groupaction.GroupAction;
import fr.paris.lutece.plugins.ticketing.business.groupaction.GroupActionHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides the user interface to manage Category features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageGroupActionButton.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT_ACTION_BUTTON" )
public class ManageGroupActionButtonJspBean extends ManageAdminTicketingJspBean
{
    private static final long   serialVersionUID                         = 3581858333697237089L;

    // Templates
    private static final String TEMPLATE_MANAGE_GROUPS                   = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "manage_groups.html";
    private static final String TEMPLATE_CREATE_GROUP                    = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "create_group.html";
    private static final String TEMPLATE_EDIT_GROUP                      = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "edit_group.html";
    private static final String JSP_MANAGE_GROUP_ACTION_BUTTON           = "ManageGroupActionButton.jsp";

    // Parameters
    private static final String PARAMETER_GROUP_ID                       = "group_id";
    private static final String PARAMETER_ORDER_ID                       = "order_id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_ACTION_BUTTON = "ticketing.manage_actionButton.pageTitle";

    // Markers
    private static final String MARK_GROUPE_LIST                         = "groupe_list";
    private static final String MARK_GROUPE                              = "groupe";
    private static final String MARK_ORDER_LIST                          = "order_list";

    // Views
    private static final String VIEW_MANAGE_GROUP                        = "manageGroup";
    private static final String VIEW_CREATE_GROUP                        = "createGroup";
    private static final String VIEW_EDIT_GROUP                          = "editGroup";

    // Actions
    private static final String ACTION_CREATE_GROUPE                     = "createGroup";
    private static final String ACTION_EDIT_GROUPE                       = "editGroup";
    private static final String ACTION_CHANGE_ORDER                      = "changeOrder";

    // Properties
    private static final String MESSAGE_CONFIRM_DELETE                   = "ticketing.message.confirmDeleteGroup";

    // JSP
    private static final String JSP_REMOVE_GROUPS                        = "jsp/admin/plugins/ticketing/admin//DoRemoveGroup.jsp";

    private static final String REGEX_ID                                 = "^[\\d]+$";

    /**
     * Returns the Manage Groups page
     *
     * @param request
     *            The HTTP request
     * @return The HTML page
     */
    @View( value = VIEW_MANAGE_GROUP, defaultView = true )
    public String getManageGroups( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );

        // Récupération des groupes
        List<GroupAction> groupeList = GroupActionHome.getGroupActionList( );
        model.put( MARK_GROUPE_LIST, groupeList );

        ReferenceList listOrders = new ReferenceList( );
        IntStream.range( 0, groupeList.size( ) ).forEach( index -> listOrders.addItem( index + 1, Integer.toString( index + 1 ) ) );
        model.put( MARK_ORDER_LIST, listOrders );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ACTION_BUTTON, TEMPLATE_MANAGE_GROUPS, model );
    }

    /**
     * Gets the creates the group.
     *
     * @param request
     *            the request
     * @return the creates the group
     */
    @View( value = VIEW_CREATE_GROUP )
    public String getCreateGroup( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );

        List<GroupAction> groupeList = GroupActionHome.getGroupActionList( );

        ReferenceList listOrders = new ReferenceList( );
        IntStream.range( 0, groupeList.size( ) + 1 ).forEach( index -> listOrders.addItem( index + 1, Integer.toString( index + 1 ) ) );
        model.put( MARK_ORDER_LIST, listOrders );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ACTION_BUTTON, TEMPLATE_CREATE_GROUP, model );

    }

    /**
     * Do create groupe.
     *
     * @param request
     *            the request
     * @return the string
     */
    @Action( ACTION_CREATE_GROUPE )
    public String doCreateGroupe( HttpServletRequest request )
    {
        GroupAction groupAction = new GroupAction( );
        populate( groupAction, request );

        // Création du groupe
        GroupActionHome.create( groupAction );

        return redirectView( request, VIEW_MANAGE_GROUP );
    }

    /**
     * Gets the edits the group.
     *
     * @param request
     *            the request
     * @return the edits the group
     */
    @View( value = VIEW_EDIT_GROUP )
    public String getEditGroup( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );

        List<GroupAction> groupeList = GroupActionHome.getGroupActionList( );

        ReferenceList listOrders = new ReferenceList( );
        IntStream.range( 0, groupeList.size( ) ).forEach( index -> listOrders.addItem( index + 1, Integer.toString( index + 1 ) ) );
        model.put( MARK_ORDER_LIST, listOrders );

        String strGroupId = request.getParameter( PARAMETER_GROUP_ID );
        model.put( MARK_GROUPE, GroupActionHome.findByPrimaryKey( Integer.parseInt( strGroupId ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_ACTION_BUTTON, TEMPLATE_EDIT_GROUP, model );
    }

    /**
     * Do edit group.
     *
     * @param request
     *            the request
     * @return the string
     */
    @Action( ACTION_EDIT_GROUPE )
    public String doEditGroup( HttpServletRequest request )
    {
        GroupAction groupAction = new GroupAction( );
        populate( groupAction, request );
        GroupActionHome.update( groupAction );

        return redirectView( request, VIEW_MANAGE_GROUP );
    }

    /**
     * Returns the Remove page
     *
     * @param request
     *            The HTTP request
     * @return The HTML page
     */
    public String getRemoveGroup( HttpServletRequest request )
    {
        String strGroupId = request.getParameter( PARAMETER_GROUP_ID );

        String strUrl = JSP_REMOVE_GROUPS;
        Map<String, Object> parameters = new HashMap<>( );
        parameters.put( PARAMETER_GROUP_ID, strGroupId );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE, null, null, strUrl, "", AdminMessage.TYPE_CONFIRMATION, parameters );
    }

    /**
     * Remove the group
     *
     * @param request
     *            The HTTP request
     * @return The next URL to redirect after processing
     * @throws AccessDeniedException
     *             if the security token is invalid
     */
    public String doRemoveGroup( HttpServletRequest request ) throws AccessDeniedException
    {
        String strGroupId = request.getParameter( PARAMETER_GROUP_ID );
        GroupActionHome.remove( Integer.parseInt( strGroupId ) );

        return JSP_MANAGE_GROUP_ACTION_BUTTON + "?view=manageGroup";
    }

    @Action( ACTION_CHANGE_ORDER )
    public String doChangeOrder( HttpServletRequest request )
    {
        String strGroupId = request.getParameter( PARAMETER_GROUP_ID );
        String strOrderId = request.getParameter( PARAMETER_ORDER_ID );
        GroupAction groupAction = GroupActionHome.findByPrimaryKey( Integer.parseInt( strGroupId ) );

        if ( ( strOrderId != null ) && strOrderId.matches( REGEX_ID ) )
        {
            groupAction.setOrdre( Integer.parseInt( strOrderId ) );
        }

        GroupActionHome.update( groupAction );

        return redirectView( request, VIEW_MANAGE_GROUP );
    }
}
