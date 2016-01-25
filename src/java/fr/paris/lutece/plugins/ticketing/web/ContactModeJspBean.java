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

import fr.paris.lutece.plugins.ticketing.business.ContactMode;
import fr.paris.lutece.plugins.ticketing.business.ContactModeHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage ContactMode features (
 * manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageContactModes.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "TICKETING_MANAGEMENT" )
public class ContactModeJspBean extends ManageAdminTicketingJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_CONTACTMODES = "/admin/plugins/ticketing/admin/manage_contact_modes.html";
    private static final String TEMPLATE_CREATE_CONTACTMODE = "/admin/plugins/ticketing/admin/create_contact_mode.html";
    private static final String TEMPLATE_MODIFY_CONTACTMODE = "/admin/plugins/ticketing/admin/modify_contact_mode.html";
    private static final String TEMPLATE_FREEMARKER_LIST = "/admin/plugins/ticketing/admin/freemarker_list_ticket.html";

    // Parameters
    private static final String PARAMETER_ID_CONTACTMODE = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_CONTACTMODES = "ticketing.manage_contactmodes.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CONTACTMODE = "ticketing.modify_contactmode.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CONTACTMODE = "ticketing.create_contactmode.pageTitle";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_CONTACTMODE_LIST = "contactmode_list";
    private static final String MARK_CONTACTMODE = "contactmode";
    private static final String JSP_MANAGE_CONTACTMODES = "jsp/admin/plugins/ticketing/ManageContactModes.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CONTACTMODE = "ticketing.message.confirmRemoveContactMode";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.contactmode.attribute.";

    // Views
    private static final String VIEW_MANAGE_CONTACTMODES = "manageContactModes";
    private static final String VIEW_CREATE_CONTACTMODE = "createContactMode";
    private static final String VIEW_MODIFY_CONTACTMODE = "modifyContactMode";

    // Actions
    private static final String ACTION_CREATE_CONTACTMODE = "createContactMode";
    private static final String ACTION_MODIFY_CONTACTMODE = "modifyContactMode";
    private static final String ACTION_REMOVE_CONTACTMODE = "removeContactMode";
    private static final String ACTION_CONFIRM_REMOVE_CONTACTMODE = "confirmRemoveContactMode";

    // Infos
    private static final String INFO_CONTACTMODE_CREATED = "ticketing.info.contactmode.created";
    private static final String INFO_CONTACTMODE_UPDATED = "ticketing.info.contactmode.updated";
    private static final String INFO_CONTACTMODE_REMOVED = "ticketing.info.contactmode.removed";
    private static final long serialVersionUID = 1L;

    // Session variable to store working values
    private ContactMode _contactmode;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CONTACTMODES, defaultView = true )
    public String getManageContactModes( HttpServletRequest request )
    {
        _contactmode = null;

        List<ContactMode> listContactModes = (List<ContactMode>) ContactModeHome.getContactModesList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_CONTACTMODE_LIST, listContactModes,
                JSP_MANAGE_CONTACTMODES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_CONTACTMODES, TEMPLATE_MANAGE_CONTACTMODES, model );
    }

    /**
     * Returns the form to create a contactmode
     *
     * @param request
     *            The Http request
     * @return the html code of the contactmode form
     */
    @View( VIEW_CREATE_CONTACTMODE )
    public String getCreateContactMode( HttpServletRequest request )
    {
        _contactmode = ( _contactmode != null ) ? _contactmode : new ContactMode(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_CONTACTMODE, _contactmode );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CONTACTMODE, TEMPLATE_CREATE_CONTACTMODE, model );
    }

    /**
     * Process the data capture form of a new contactmode
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_CONTACTMODE )
    public String doCreateContactMode( HttpServletRequest request )
    {
        populate( _contactmode, request );

        // Check constraints
        if ( !validateBean( _contactmode, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CONTACTMODE );
        }

        ContactModeHome.create( _contactmode );
        addInfo( INFO_CONTACTMODE_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_CONTACTMODES );
    }

    /**
     * Manages the removal form of a contactmode whose identifier is in the http
     * request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_CONTACTMODE )
    public String getConfirmRemoveContactMode( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CONTACTMODE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CONTACTMODE ) );
        url.addParameter( PARAMETER_ID_CONTACTMODE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CONTACTMODE,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a contactmode
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage contactmodes
     */
    @Action( ACTION_REMOVE_CONTACTMODE )
    public String doRemoveContactMode( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CONTACTMODE ) );

        ContactModeHome.remove( nId );
        addInfo( INFO_CONTACTMODE_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_CONTACTMODES );
    }

    /**
     * Returns the form to update info about a contactmode
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_CONTACTMODE )
    public String getModifyContactMode( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_CONTACTMODE ) );

        if ( ( _contactmode == null ) || ( _contactmode.getId(  ) != nId ) )
        {
            _contactmode = ContactModeHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_CONTACTMODE, _contactmode );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CONTACTMODE, TEMPLATE_MODIFY_CONTACTMODE, model );
    }

    /**
     * Process the change form of a contactmode
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_CONTACTMODE )
    public String doModifyContactMode( HttpServletRequest request )
    {
        populate( _contactmode, request );

        // Check constraints
        if ( !validateBean( _contactmode, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_CONTACTMODE, PARAMETER_ID_CONTACTMODE, _contactmode.getId(  ) );
        }

        ContactModeHome.update( _contactmode );
        addInfo( INFO_CONTACTMODE_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_CONTACTMODES );
    }
}
