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

import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.TicketFormHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage TicketCategory features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTicketCategorys.jsp", controllerPath = "jsp/admin/plugins/ticketing/", right = "TICKETING_MANAGEMENT" )
public class TicketCategoryJspBean extends ManageAdminTicketingJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_TICKETCATEGORYS = "/admin/plugins/ticketing/admin/manage_ticket_categories.html";
    private static final String TEMPLATE_CREATE_TICKETCATEGORY = "/admin/plugins/ticketing/admin/create_ticket_category.html";
    private static final String TEMPLATE_MODIFY_TICKETCATEGORY = "/admin/plugins/ticketing/admin/modify_ticket_category.html";

    // Parameters
    private static final String PARAMETER_ID_TICKETCATEGORY = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_TICKETCATEGORYS = "ticketing.manage_ticketcategories.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TICKETCATEGORY = "ticketing.modify_ticketcategory.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TICKETCATEGORY = "ticketing.create_ticketcategory.pageTitle";

    // Markers
    private static final String MARK_TICKETCATEGORY_LIST = "ticketcategory_list";
    private static final String MARK_TICKETCATEGORY = "ticketcategory";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_LIST_WORKFLOWS = "listWorkflows";
    private static final String MARK_LIST_UNITS = "units_list";
    private static final String MARK_TICKET_FORM_LIST = "ticketform_list";
    private static final String JSP_MANAGE_TICKETCATEGORYS = "jsp/admin/plugins/ticketing/ManageTicketCategorys.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_TICKETCATEGORY = "ticketing.message.confirmRemoveTicketCategory";
    private static final String MESSAGE_ERROR_CODE_ALREADY_EXISTS = "ticketing.message.errorTicketCategory.codeAlreadyExists";
    private static final String MESSAGE_ERROR_CODE_INVALID_FORMAT = "ticketing.message.errorTicketCategory.codeInvlidFormat";
    private static final String PROPERTY_DEFAULT_LIST_TICKETCATEGORY_PER_PAGE = "ticketing.listTicketCategorys.itemsPerPage";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.ticketcategory.attribute.";

    // Views
    private static final String VIEW_MANAGE_TICKETCATEGORYS = "manageTicketCategorys";
    private static final String VIEW_CREATE_TICKETCATEGORY = "createTicketCategory";
    private static final String VIEW_MODIFY_TICKETCATEGORY = "modifyTicketCategory";

    // Actions
    private static final String ACTION_CREATE_TICKETCATEGORY = "createTicketCategory";
    private static final String ACTION_MODIFY_TICKETCATEGORY = "modifyTicketCategory";
    private static final String ACTION_REMOVE_TICKETCATEGORY = "removeTicketCategory";
    private static final String ACTION_CONFIRM_REMOVE_TICKETCATEGORY = "confirmRemoveTicketCategory";

    // Infos
    private static final String INFO_TICKETCATEGORY_CREATED = "ticketing.info.ticketcategory.created";
    private static final String INFO_TICKETCATEGORY_UPDATED = "ticketing.info.ticketcategory.updated";
    private static final String INFO_TICKETCATEGORY_REMOVED = "ticketing.info.ticketcategory.removed";
    private static final String PATTERN_CATEGORY_CODE = "^[A-Z0-9]*";
    private static Pattern _pattern = Pattern.compile( PATTERN_CATEGORY_CODE );
    private static final long serialVersionUID = 1L;

    // Session variable to store working values
    private TicketCategory _category;

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TICKETCATEGORYS, defaultView = true )
    public String getManageTicketCategorys( HttpServletRequest request )
    {
        _category = null;

        List<TicketCategory> listTicketCategorys = (List<TicketCategory>) TicketCategoryHome.getTicketCategorysList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_TICKETCATEGORY_LIST, listTicketCategorys,
                JSP_MANAGE_TICKETCATEGORYS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_TICKETCATEGORYS, TEMPLATE_MANAGE_TICKETCATEGORYS, model );
    }

    /**
     * Returns the form to create a ticketcategory
     *
     * @param request The Http request
     * @return the html code of the ticketcategory form
     */
    @View( VIEW_CREATE_TICKETCATEGORY )
    public String getCreateTicketCategory( HttpServletRequest request )
    {
        _category = ( _category != null ) ? _category : new TicketCategory(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKETCATEGORY, _category );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_LIST_WORKFLOWS,
            WorkflowService.getInstance(  ).getWorkflowsEnabled( getUser(  ), getLocale(  ) ) );
        model.put( MARK_TICKET_FORM_LIST, TicketFormHome.getAvailableTicketFormsList(  ) );
        model.put( MARK_LIST_UNITS, getUnitsList(  ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TICKETCATEGORY, TEMPLATE_CREATE_TICKETCATEGORY, model );
    }

    /**
     * Process the data capture form of a new ticketcategory
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKETCATEGORY )
    public String doCreateTicketCategory( HttpServletRequest request )
    {
        populate( _category, request );

        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) || !validateCode( _category ) )
        {
            return redirectView( request, VIEW_CREATE_TICKETCATEGORY );
        }

        TicketCategoryHome.create( _category );
        addInfo( INFO_TICKETCATEGORY_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETCATEGORYS );
    }

    /**
     * Manages the removal form of a ticketcategory whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_TICKETCATEGORY )
    public String getConfirmRemoveTicketCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TICKETCATEGORY ) );
        url.addParameter( PARAMETER_ID_TICKETCATEGORY, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TICKETCATEGORY,
                url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a ticketcategory
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage ticketcategorys
     */
    @Action( ACTION_REMOVE_TICKETCATEGORY )
    public String doRemoveTicketCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );
        TicketCategoryHome.remove( nId );
        addInfo( INFO_TICKETCATEGORY_REMOVED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETCATEGORYS );
    }

    /**
     * Returns the form to update info about a ticketcategory
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_TICKETCATEGORY )
    public String getModifyTicketCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_TICKETCATEGORY ) );

        if ( ( _category == null ) || ( _category.getId(  ) != nId ) )
        {
            _category = TicketCategoryHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKETCATEGORY, _category );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_LIST_WORKFLOWS,
            WorkflowService.getInstance(  ).getWorkflowsEnabled( getUser(  ), getLocale(  ) ) );

        ReferenceList lstForms = TicketFormHome.getAvailableTicketFormsList(  );
        TicketForm form = TicketFormHome.findByPrimaryKey( _category.getIdTicketForm(  ) );

        if ( form != null )
        {
            lstForms.addItem( _category.getIdTicketForm(  ), form.getTitle(  ) );
        }

        model.put( MARK_TICKET_FORM_LIST, lstForms );
        model.put( MARK_LIST_UNITS, getUnitsList(  ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TICKETCATEGORY, TEMPLATE_MODIFY_TICKETCATEGORY, model );
    }

    /**
     * Process the change form of a ticketcategory
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TICKETCATEGORY )
    public String doModifyTicketCategory( HttpServletRequest request )
    {
        populate( _category, request );

        // Check constraints
        if ( !validateBean( _category, VALIDATION_ATTRIBUTES_PREFIX ) || !validateCode( _category ) )
        {
            return redirect( request, VIEW_MODIFY_TICKETCATEGORY, PARAMETER_ID_TICKETCATEGORY, _category.getId(  ) );
        }

        TicketCategoryHome.update( _category );
        addInfo( INFO_TICKETCATEGORY_UPDATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_TICKETCATEGORYS );
    }

    /**
     * Validate Code (uniqueness and format)
     * @param category The category
     * @return true if valid otherwise false
     */
    private boolean validateCode( TicketCategory category )
    {
        String strCode = category.getCode(  );

        if ( ( strCode != null ) && !strCode.equals( "" ) )
        {
            TicketCategory existingCategory = TicketCategoryHome.findByCode( strCode );

            if ( ( existingCategory != null ) && ( existingCategory.getId(  ) != category.getId(  ) ) )
            {
                Object[] args = { strCode, existingCategory.getLabel(  ) };
                String strMessage = I18nService.getLocalizedString( MESSAGE_ERROR_CODE_ALREADY_EXISTS, args,
                        getLocale(  ) );
                addError( strMessage );

                return false;
            }

            Matcher matcher = _pattern.matcher( strCode );

            if ( !matcher.matches(  ) )
            {
                addError( MESSAGE_ERROR_CODE_INVALID_FORMAT, getLocale(  ) );

                return false;
            }
        }

        return true;
    }
}
