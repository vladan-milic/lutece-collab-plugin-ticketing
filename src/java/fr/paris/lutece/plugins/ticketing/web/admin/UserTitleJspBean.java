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

import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitle;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
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
 * This class provides the user interface to manage UserTitle features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageUserTitles.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class UserTitleJspBean extends ManageAdminTicketingJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_USERTITLES = "/admin/plugins/ticketing/admin/manage_user_titles.html";
    private static final String TEMPLATE_CREATE_USERTITLE = "/admin/plugins/ticketing/admin/create_user_title.html";
    private static final String TEMPLATE_MODIFY_USERTITLE = "/admin/plugins/ticketing/admin/modify_user_title.html";

    // Parameters
    private static final String PARAMETER_ID_USERTITLE = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_USERTITLES = "ticketing.manage_usertitles.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_USERTITLE = "ticketing.modify_usertitle.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_USERTITLE = "ticketing.create_usertitle.pageTitle";

    // Markers
    private static final String MARK_USERTITLE_LIST = "usertitle_list";
    private static final String MARK_USERTITLE = "usertitle";
    private static final String JSP_MANAGE_USERTITLES = "jsp/admin/plugins/ticketing/ManageUserTitles.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_USERTITLE = "ticketing.message.confirmRemoveUserTitle";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.usertitle.attribute.";

    // Views
    private static final String VIEW_MANAGE_USERTITLES = "manageUserTitles";
    private static final String VIEW_CREATE_USERTITLE = "createUserTitle";
    private static final String VIEW_MODIFY_USERTITLE = "modifyUserTitle";

    // Actions
    private static final String ACTION_CREATE_USERTITLE = "createUserTitle";
    private static final String ACTION_MODIFY_USERTITLE = "modifyUserTitle";
    private static final String ACTION_REMOVE_USERTITLE = "removeUserTitle";
    private static final String ACTION_CONFIRM_REMOVE_USERTITLE = "confirmRemoveUserTitle";

    // Infos
    private static final String INFO_USERTITLE_CREATED = "ticketing.info.usertitle.created";
    private static final String INFO_USERTITLE_UPDATED = "ticketing.info.usertitle.updated";
    private static final String INFO_USERTITLE_REMOVED = "ticketing.info.usertitle.removed";
    private static final long serialVersionUID = 1L;

    // Session variable to store working values
    private UserTitle _usertitle;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_USERTITLES, defaultView = true )
    public String getManageUserTitles( HttpServletRequest request )
    {
        _usertitle = null;

        List<UserTitle> listUserTitles = (List<UserTitle>) UserTitleHome.getUserTitlesList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_USERTITLE_LIST, listUserTitles,
                JSP_MANAGE_USERTITLES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_USERTITLES, TEMPLATE_MANAGE_USERTITLES, model );
    }

    /**
     * Returns the form to create a usertitle
     *
     * @param request The Http request
     * @return the html code of the usertitle form
     */
    @View( VIEW_CREATE_USERTITLE )
    public String getCreateUserTitle( HttpServletRequest request )
    {
        _usertitle = ( _usertitle != null ) ? _usertitle : new UserTitle(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_USERTITLE, _usertitle );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_USERTITLE, TEMPLATE_CREATE_USERTITLE, model );
    }

    /**
     * Process the data capture form of a new usertitle
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_USERTITLE )
    public String doCreateUserTitle( HttpServletRequest request )
    {
        populate( _usertitle, request );

        // Check constraints
        if ( !validateBean( _usertitle, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_USERTITLE );
        }

        UserTitleHome.create( _usertitle );
        addInfo( INFO_USERTITLE_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_USERTITLES );
    }

    /**
     * Manages the removal form of a usertitle whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_USERTITLE )
    public String getConfirmRemoveUserTitle( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_USERTITLE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_USERTITLE ) );
        url.addParameter( PARAMETER_ID_USERTITLE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_USERTITLE,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a usertitle
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage usertitles
     */
    @Action( ACTION_REMOVE_USERTITLE )
    public String doRemoveUserTitle( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_USERTITLE ) );
        UserTitleHome.remove( nId );
        addInfo( INFO_USERTITLE_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_USERTITLES );
    }

    /**
     * Returns the form to update info about a usertitle
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_USERTITLE )
    public String getModifyUserTitle( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_USERTITLE ) );

        if ( ( _usertitle == null ) || ( _usertitle.getId(  ) != nId ) )
        {
            _usertitle = UserTitleHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_USERTITLE, _usertitle );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_USERTITLE, TEMPLATE_MODIFY_USERTITLE, model );
    }

    /**
     * Process the change form of a usertitle
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_USERTITLE )
    public String doModifyUserTitle( HttpServletRequest request )
    {
        populate( _usertitle, request );

        // Check constraints
        if ( !validateBean( _usertitle, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_USERTITLE, PARAMETER_ID_USERTITLE, _usertitle.getId(  ) );
        }

        UserTitleHome.update( _usertitle );
        addInfo( INFO_USERTITLE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_USERTITLES );
    }
}
