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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.supportentity.SupportEntity;
import fr.paris.lutece.plugins.ticketing.business.supportentity.SupportEntityHome;
import fr.paris.lutece.plugins.ticketing.business.supportentity.SupportLevel;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidatorResult;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage SupportEntity features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageSupportEntities.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = "TICKETING_MANAGEMENT" )
public class SupportEntityJspBean extends ManageAdminTicketingJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants

    // templates
    private static final String TEMPLATE_MANAGE_SUPPORT_ENTITIES = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "manage_support_entities.html";
    private static final String TEMPLATE_CREATE_SUPPORT_ENTITY = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "create_support_entity.html";
    private static final String TEMPLATE_MODIFY_SUPPORT_ENTITY = TicketingConstants.TEMPLATE_ADMIN_ADMIN_FEATURE_PATH + "modify_support_entity.html";

    // Parameters
    private static final String PARAMETER_ID_SUPPORT_ENTITY = "id";
    private static final String PARAMETER_ID_TICKET_DOMAIN = "id_ticket_domain";
    private static final String PARAMETER_ID_UNIT = "id_unit";
    private static final String PARAMETER_LEVEL = "level";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_SUPPORT_ENTITIES = "ticketing.manage_supportentities.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_SUPPORT_ENTITY = "ticketing.modify_supportentity.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_SUPPORT_ENTITY = "ticketing.create_supportentity.pageTitle";

    // Markers
    private static final String MARK_SUPPORT_ENTITY_LIST = "supportentity_list";
    private static final String MARK_SUPPORT_ENTITY = "supportentity";
    private static final String MARK_UNIT_LIST = "unit_list";
    private static final String MARK_LEVEL_LIST = "level_list";
    private static final String JSP_MANAGE_SUPPORT_ENTITIES = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH + "ManageSupportEntities.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_SUPPORT_ENTITY = "ticketing.message.confirmRemoveSupportEntity";
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.supportentity.attribute.";

    // Views
    private static final String VIEW_MANAGE_SUPPORT_ENTITIES = "manageSupportEntities";
    private static final String VIEW_CREATE_SUPPORT_ENTITY = "createSupportEntity";
    private static final String VIEW_MODIFY_SUPPORT_ENTITY = "modifySupportEntity";

    // Actions
    private static final String ACTION_CREATE_SUPPORT_ENTITY = "createSupportEntity";
    private static final String ACTION_MODIFY_SUPPORT_ENTITY = "modifySupportEntity";
    private static final String ACTION_REMOVE_SUPPORT_ENTITY = "removeSupportEntity";
    private static final String ACTION_CONFIRM_REMOVE_SUPPORT_ENTITY = "confirmRemoveSupportEntity";

    // Infos
    private static final String INFO_SUPPORT_ENTITY_CREATED = "ticketing.info.supportentity.created";
    private static final String INFO_SUPPORT_ENTITY_UPDATED = "ticketing.info.supportentity.updated";
    private static final String INFO_SUPPORT_ENTITY_REMOVED = "ticketing.info.supportentity.removed";
    private static final long serialVersionUID = 1L;

    // Session variable to store working values
    private transient SupportEntity _supportEntity;

    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_SUPPORT_ENTITIES, defaultView = true )
    public String getManageSupportEntities( HttpServletRequest request )
    {
        _supportEntity = null;

        List<SupportEntity> listSupportEntities = SupportEntityHome.getSupportEntityList( );
        Map<String, Object> model = getPaginatedListModel( request, MARK_SUPPORT_ENTITY_LIST, listSupportEntities, JSP_MANAGE_SUPPORT_ENTITIES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_SUPPORT_ENTITIES, TEMPLATE_MANAGE_SUPPORT_ENTITIES, model );
    }

    /**
     * Returns the form to create a supportentity
     *
     * @param request
     *            The Http request
     * @return the html code of the supportentity form
     */
    @View( VIEW_CREATE_SUPPORT_ENTITY )
    public String getCreateSupportEntity( HttpServletRequest request )
    {
        _supportEntity = ( _supportEntity != null ) ? _supportEntity : new SupportEntity( );

        Map<String, Object> model = getModel( );
        model.put( MARK_SUPPORT_ENTITY, _supportEntity );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( ).getCategoriesTree( ).getDepths( ) );
        model.put( MARK_LEVEL_LIST, SupportLevel.getReferenceList( request.getLocale( ) ) );
        model.put( MARK_UNIT_LIST, getUnitsList( ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_SUPPORT_ENTITY, TEMPLATE_CREATE_SUPPORT_ENTITY, model );
    }

    /**
     * Process the data capture form of a new supportentity
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_SUPPORT_ENTITY )
    public String doCreateSupportEntity( HttpServletRequest request )
    {
        populate( _supportEntity, request );

        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request, getLocale( ) ).validateTicketCategory( );
        TicketCategory ticketCategory = categoryValidatorResult.getTicketCategory( );
        if ( ticketCategory == null )
        {
            ticketCategory = categoryValidatorResult.getTicketCategoryParent( );
        }
        _supportEntity.setTicketCategory( ticketCategory );

        // Check constraints
        if ( !validateBean( _supportEntity, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_SUPPORT_ENTITY );
        }

        SupportEntityHome.create( _supportEntity );
        addInfo( INFO_SUPPORT_ENTITY_CREATED, getLocale( ) );
        _supportEntity = null;

        return redirectView( request, VIEW_MANAGE_SUPPORT_ENTITIES );
    }

    @Override
    protected void populate( Object bean, HttpServletRequest request )
    {
        super.populate( bean, request );

        String strIdDomain = request.getParameter( PARAMETER_ID_TICKET_DOMAIN );

        if ( StringUtils.isNotEmpty( strIdDomain ) && StringUtils.isNumeric( strIdDomain ) )
        {
            TicketCategory ticketDomain = TicketCategoryService.getInstance( ).findCategoryById( ( Integer.parseInt( strIdDomain ) ) );
            _supportEntity.setTicketDomain( ticketDomain );
        }

        String strIdUnit = request.getParameter( PARAMETER_ID_UNIT );

        if ( StringUtils.isNotEmpty( strIdUnit ) && StringUtils.isNumeric( strIdUnit ) )
        {
            Unit unit = UnitHome.findByPrimaryKey( Integer.parseInt( strIdUnit ) );

            if ( unit != null )
            {
                _supportEntity.setUnit( new AssigneeUnit( unit ) );
            }
        }

        String strLevel = request.getParameter( PARAMETER_LEVEL );

        if ( StringUtils.isNotEmpty( strLevel ) && StringUtils.isNumeric( strLevel ) )
        {
            _supportEntity.setSupportLevel( SupportLevel.valueOf( Integer.parseInt( strLevel ) ) );
        }
    }

    /**
     * Manages the removal form of a supportentity whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_SUPPORT_ENTITY )
    public String getConfirmRemoveSupportEntity( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SUPPORT_ENTITY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_SUPPORT_ENTITY ) );
        url.addParameter( PARAMETER_ID_SUPPORT_ENTITY, nId );

        String strMessageUrl = AdminMessageService
                .getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUPPORT_ENTITY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a supportentity
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage supportentities
     */
    @Action( ACTION_REMOVE_SUPPORT_ENTITY )
    public String doRemoveSupportEntity( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SUPPORT_ENTITY ) );
        SupportEntityHome.remove( nId );
        addInfo( INFO_SUPPORT_ENTITY_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_SUPPORT_ENTITIES );
    }

    /**
     * Returns the form to update info about a supportentity
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_SUPPORT_ENTITY )
    public String getModifySupportEntity( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SUPPORT_ENTITY ) );

        if ( ( _supportEntity == null ) || ( _supportEntity.getId( ) != nId ) )
        {
            _supportEntity = SupportEntityHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_SUPPORT_ENTITY, _supportEntity );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( true ).getCategoriesTree( ).getDepths( ) );
        model.put( MARK_LEVEL_LIST, SupportLevel.getReferenceList( request.getLocale( ) ) );
        model.put( MARK_UNIT_LIST, getUnitsList( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_SUPPORT_ENTITY, TEMPLATE_MODIFY_SUPPORT_ENTITY, model );
    }

    /**
     * Process the change form of a supportentity
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_SUPPORT_ENTITY )
    public String doModifySupportEntity( HttpServletRequest request )
    {
        populate( _supportEntity, request );

        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request, getLocale( ) ).validateTicketCategory( );
        TicketCategory ticketCategory = categoryValidatorResult.getTicketCategory( );
        if ( ticketCategory == null )
        {
            ticketCategory = categoryValidatorResult.getTicketCategoryParent( );
        }
        _supportEntity.setTicketCategory( ticketCategory );

        // Check constraints
        if ( !validateBean( _supportEntity, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_SUPPORT_ENTITY, PARAMETER_ID_SUPPORT_ENTITY, _supportEntity.getId( ) );
        }

        SupportEntityHome.update( _supportEntity );
        addInfo( INFO_SUPPORT_ENTITY_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_SUPPORT_ENTITIES );
    }
}
