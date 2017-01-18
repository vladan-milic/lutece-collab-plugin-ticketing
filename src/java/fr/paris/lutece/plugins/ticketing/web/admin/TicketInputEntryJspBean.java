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
package fr.paris.lutece.plugins.ticketing.web.admin;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.EntryService;
import fr.paris.lutece.plugins.ticketing.service.EntryTypeService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

/**
 * JspBean to manage ticketing form entries
 */
@Controller( controllerJsp = "ManageTicketInputEntry.jsp", controllerPath = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH, right = TicketInputsJspBean.RIGHT_MANAGETICKETINPUTS )
public class TicketInputEntryJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -4951787792196104967L;

    // Parameters
    private static final String PARAMETER_ID_ENTRY_TYPE = "id_type";
    private static final String PARAMETER_ID_INPUT = "id_input";
    private static final String PARAMETER_ID_FIELD = "id_field";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ENTRY_CODE = "entry_code";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_ID_EXPRESSION = "id_expression";
    private static final String FIELD_ENTRY_CODE = "ticketing.createEntry.labelCode";

    // Urls
    private static final String JSP_URL_MANAGE_TICKETING_INPUT_ENTRY = TicketingConstants.ADMIN_ADMIN_FEATURE_CONTROLLLER_PATH + "ManageTicketInputEntry.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY = "ticketing.message.confirmRemoveEntry";
    private static final String MESSAGE_CANT_REMOVE_ENTRY = "advert.message.cantRemoveEntry";
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryField";
    private static final String PROPERTY_CREATE_ENTRY_TITLE = "ticketing.createEntry.titleInput";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "ticketing.modifyEntry.titleInput";
    private static final String PROPERTY_COPY_ENTRY_TITLE = "ticketing.copyEntry.title";
    private static final String MESSAGE_CANNOT_REMOVE_ENTRY = "ticketing.message.errorTicketCategory.cannotRemoveEntry";

    // Views
    private static final String VIEW_GET_CREATE_ENTRY = "getCreateEntry";
    private static final String VIEW_GET_MODIFY_ENTRY = "getModifyEntry";
    private static final String VIEW_CONFIRM_REMOVE_ENTRY = "confirmRemoveEntry";

    // Actions
    private static final String ACTION_DO_CREATE_ENTRY = "doCreateEntry";
    private static final String ACTION_DO_MODIFY_ENTRY = "doModifyEntry";
    private static final String ACTION_DO_REMOVE_ENTRY = "doRemoveEntry";
    private static final String ACTION_DO_COPY_ENTRY = "doCopyEntry";
    private static final String ACTION_DO_REMOVE_REGULAR_EXPRESSION = "doRemoveRegularExpression";
    private static final String ACTION_DO_INSERT_REGULAR_EXPRESSION = "doInsertRegularExpression";

    // Marks
    private static final String MARK_REGULAR_EXPRESSION_LIST_REF_LIST = "regular_expression_list";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_LIST = "list";
    private static final String MARK_ENTRY_TYPE_SERVICE = "entryTypeService";

    // Local variables
    private EntryService _entryService = EntryService.getService( );

    /**
     * Get the HTML code to create an entry
     * 
     * @param request
     *            The request
     * @return The HTML code to display or the next URL to redirect to
     */
    @View( value = VIEW_GET_CREATE_ENTRY )
    public String getCreateEntry( HttpServletRequest request )
    {
        String strIdType = request.getParameter( PARAMETER_ID_ENTRY_TYPE );

        if ( StringUtils.isEmpty( strIdType ) || !StringUtils.isNumeric( strIdType ) )
        {
            return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
        }

        int nIdType = Integer.parseInt( strIdType );

        Entry entry = new Entry( );
        entry.setEntryType( EntryTypeHome.findByPrimaryKey( nIdType ) );

        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( StringUtils.isNotEmpty( strIdField ) && StringUtils.isNumeric( strIdField ) )
        {
            nIdField = Integer.parseInt( strIdField );

            Field field = new Field( );
            field.setIdField( nIdField );
            entry.setFieldDepend( field );
        }

        entry.setIdResource( getNextIdInput( ) );
        entry.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );

        // Default Values
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_ENTRY, entry );

        ModelUtils.storeRichText( request, model );

        model.put( MARK_ENTRY_TYPE_SERVICE, EntryTypeServiceManager.getEntryTypeService( entry ) );

        String strTemplate = EntryTypeServiceManager.getEntryTypeService( entry ).getTemplateCreate( entry, false );

        if ( strTemplate == null )
        {
            return doCreateEntry( request );
        }

        return getPage( PROPERTY_CREATE_ENTRY_TITLE, strTemplate, model );
    }

    /**
     * Do create an entry
     * 
     * @param request
     *            the request
     * @return The HTML code to display or the next URL to redirect to
     */
    @Action( ACTION_DO_CREATE_ENTRY )
    public String doCreateEntry( HttpServletRequest request )
    {
        String strIdInput = request.getParameter( PARAMETER_ID_INPUT );

        if ( StringUtils.isEmpty( strIdInput ) || !StringUtils.isNumeric( strIdInput ) )
        {
            return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
        }

        String strIdType = request.getParameter( PARAMETER_ID_ENTRY_TYPE );

        int nIdInput = Integer.parseInt( strIdInput );

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) && StringUtils.isNotEmpty( strIdType ) && StringUtils.isNumeric( strIdType ) )
        {
            int nIdType = Integer.parseInt( strIdType );
            EntryType entryType = new EntryType( );
            entryType.setIdType( nIdType );

            Entry entry = new Entry( );
            entry.setEntryType( EntryTypeService.getInstance( ).getEntryType( nIdType ) );

            String strError = EntryTypeServiceManager.getEntryTypeService( entry ).getRequestData( entry, request, getLocale( ) );

            if ( strError != null )
            {
                return redirect( request, strError );
            }

            // entry code is mandatory for ticketing
            String strEntryCode = request.getParameter( PARAMETER_ENTRY_CODE );

            if ( StringUtils.isEmpty( strEntryCode ) )
            {
                String [ ] tabErr = new String [ ] {
                    I18nService.getLocalizedString( FIELD_ENTRY_CODE, getLocale( ) )
                };

                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabErr, AdminMessage.TYPE_STOP ) );
            }

            entry.setIdResource( nIdInput );
            entry.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
            entry.setIdEntry( EntryHome.create( entry ) );

            entry.setPosition( nIdInput );
            EntryHome.update( entry );

            if ( entry.getFields( ) != null )
            {
                for ( Field field : entry.getFields( ) )
                {
                    field.setParentEntry( entry );
                    FieldHome.create( field );
                }
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return redirect( request, VIEW_GET_MODIFY_ENTRY, PARAMETER_ID_ENTRY, entry.getIdEntry( ) );
            }
        }

        return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
    }

    /**
     * Gets the entry modification page
     * 
     * @param request
     *            The HTTP request
     * @return The entry modification page
     */
    @View( VIEW_GET_MODIFY_ENTRY )
    public String getModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );

            if ( nIdEntry <= 0 )
            {
                return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
            }

            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            List<Field> listField = new ArrayList<Field>( entry.getFields( ).size( ) );

            for ( Field field : entry.getFields( ) )
            {
                field = FieldHome.findByPrimaryKey( field.getIdField( ) );
                listField.add( field );
            }

            entry.setFields( listField );

            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

            Map<String, Object> model = new HashMap<String, Object>( );
            model.put( MARK_ENTRY, entry );

            UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + getViewUrl( VIEW_GET_MODIFY_ENTRY ) );
            urlItem.addParameter( PARAMETER_ID_ENTRY, strIdEntry );

            model.put( MARK_LIST, entry.getFields( ) );

            ReferenceList refListRegularExpression = entryTypeService.getReferenceListRegularExpression( entry, plugin );

            if ( refListRegularExpression != null )
            {
                model.put( MARK_REGULAR_EXPRESSION_LIST_REF_LIST, refListRegularExpression );
            }

            ModelUtils.storeRichText( request, model );

            model.put( MARK_ENTRY_TYPE_SERVICE, EntryTypeServiceManager.getEntryTypeService( entry ) );

            return getPage( PROPERTY_MODIFY_QUESTION_TITLE, entryTypeService.getTemplateModify( entry, false ), model );
        }

        return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
    }

    /**
     * Perform the entry modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_MODIFY_ENTRY )
    public String doModifyEntry( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );

            if ( nIdEntry <= 0 )
            {
                return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
            }

            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            if ( request.getParameter( PARAMETER_CANCEL ) == null )
            {
                String strError = EntryTypeServiceManager.getEntryTypeService( entry ).getRequestData( entry, request, getLocale( ) );

                // entry code is mandatory for ticketing
                String strEntryCode = request.getParameter( PARAMETER_ENTRY_CODE );

                if ( StringUtils.isEmpty( strEntryCode ) )
                {
                    String [ ] tabErr = new String [ ] {
                        I18nService.getLocalizedString( FIELD_ENTRY_CODE, getLocale( ) )
                    };

                    return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabErr, AdminMessage.TYPE_STOP ) );
                }

                if ( strError != null )
                {
                    return redirect( request, strError );
                }

                EntryHome.update( entry );

                if ( entry.getFields( ) != null )
                {
                    for ( Field field : entry.getFields( ) )
                    {
                        // Check if the field already exists in the database
                        Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField( ) );

                        if ( fieldStored != null )
                        {
                            // If it exists, update
                            FieldHome.update( field );
                        }
                        else
                        {
                            // If it does not exist, create
                            FieldHome.create( field );
                        }
                    }
                }
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return redirect( request, VIEW_GET_MODIFY_ENTRY, PARAMETER_ID_ENTRY, nIdEntry );
            }
        }

        return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
    }

    /**
     * Gets the confirmation page of delete entry
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( VIEW_CONFIRM_REMOVE_ENTRY )
    public String getConfirmRemoveEntry( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) && ( Integer.parseInt( strIdEntry ) > 0 ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );

            if ( checkIfEntryIsLinkedToACategory( nIdEntry ) )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CANNOT_REMOVE_ENTRY,
                        TicketInputsJspBean.getURLManageTicketInputs( request ), AdminMessage.TYPE_ERROR ) );
            }
            else
            {
                UrlItem url = new UrlItem( getActionUrl( ACTION_DO_REMOVE_ENTRY ) );
                url.addParameter( PARAMETER_ID_ENTRY, strIdEntry );

                return redirect( request,
                        AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ENTRY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
            }
        }
        else
        {
            return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
        }
    }

    /**
     * Perform the entry removal
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_REMOVE_ENTRY )
    public String doRemoveEntry( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );

            if ( nIdEntry <= 0 )
            {
                return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
            }

            List<String> listErrors = new ArrayList<String>( );

            if ( !_entryService.checkForRemoval( strIdEntry, listErrors, getLocale( ) ) )
            {
                String strCause = AdminMessageService.getFormattedList( listErrors, getLocale( ) );
                Object [ ] args = {
                    strCause
                };

                return AdminMessageService.getMessageUrl( request, MESSAGE_CANT_REMOVE_ENTRY, args, AdminMessage.TYPE_STOP );
            }

            TicketHome.removeResponsesByIdEntry( nIdEntry );

            // Remove entry
            EntryHome.remove( nIdEntry );
        }

        return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
    }

    /**
     * Copy the entry whose key is specified in the HTTP request
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_COPY_ENTRY )
    public String doCopyEntry( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );

            if ( nIdEntry == -1 )
            {
                return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
            }

            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            Object [ ] tabEntryTileCopy = {
                entry.getTitle( )
            };
            String strTitleCopyEntry = I18nService.getLocalizedString( PROPERTY_COPY_ENTRY_TITLE, tabEntryTileCopy, getLocale( ) );

            if ( strTitleCopyEntry != null )
            {
                entry.setTitle( strTitleCopyEntry );
            }

            EntryHome.copy( entry );

            Entry entryCopy = EntryHome.findByPrimaryKey( entry.getIdEntry( ) );
            entryCopy.setIdResource( getNextIdInput( ) );
            entryCopy.setPosition( getNextIdInput( ) );
            EntryHome.update( entryCopy );

            // If the entry has a parent
            if ( entry.getParent( ) != null )
            {
                // We reload the entry to get the copy and not he original entry
                // The id of the entry is the id of the copy. It has been set by the create method of EntryDAO
                entry = EntryHome.findByPrimaryKey( entry.getIdEntry( ) );

                Entry entryParent = EntryHome.findByPrimaryKey( entry.getParent( ).getIdEntry( ) );
                _entryService.moveUpEntryOrder( entryParent.getPosition( ) + entryParent.getChildren( ).size( ), entry );
            }
        }

        return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
    }

    /**
     * Delete the association between a field and and regular expression
     * 
     * @param request
     *            the HTTP Request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_REMOVE_REGULAR_EXPRESSION )
    public String doRemoveRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isNotEmpty( strIdExpression ) && StringUtils.isNotEmpty( strIdField ) && StringUtils.isNumeric( strIdExpression )
                && StringUtils.isNumeric( strIdField ) )
        {
            int nIdField = Integer.parseInt( strIdField );
            int nIdExpression = Integer.parseInt( strIdExpression );
            FieldHome.removeVerifyBy( nIdField, nIdExpression );

            Field field = FieldHome.findByPrimaryKey( nIdField );

            return redirect( request, VIEW_GET_MODIFY_ENTRY, PARAMETER_ID_ENTRY, field.getParentEntry( ).getIdEntry( ) );
        }

        return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
    }

    /**
     * Insert an association between a field and a regular expression
     * 
     * @param request
     *            the HTTP Request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_INSERT_REGULAR_EXPRESSION )
    public String doInsertRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isNotEmpty( strIdExpression ) && StringUtils.isNotEmpty( strIdField ) && StringUtils.isNumeric( strIdExpression )
                && StringUtils.isNumeric( strIdField ) )
        {
            int nIdField = Integer.parseInt( strIdField );
            int nIdExpression = Integer.parseInt( strIdExpression );

            FieldHome.createVerifyBy( nIdField, nIdExpression );

            Field field = FieldHome.findByPrimaryKey( nIdField );

            return redirect( request, VIEW_GET_MODIFY_ENTRY, PARAMETER_ID_ENTRY, field.getParentEntry( ).getIdEntry( ) );
        }

        return redirect( request, TicketInputsJspBean.getURLManageTicketInputs( request ) );
    }

    /**
     * Get the URL to modify an entry
     * 
     * @param request
     *            The request
     * @param nIdEntry
     *            The id of the entry
     * @return The URL to modify the given entry
     */
    public static String getURLModifyEntry( HttpServletRequest request, int nIdEntry )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_TICKETING_INPUT_ENTRY );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_MODIFY_ENTRY );
        urlItem.addParameter( PARAMETER_ID_ENTRY, nIdEntry );

        return urlItem.getUrl( );
    }

    /**
     * Get the next idResource of type TICKET_INPUT
     * 
     * @return The next id resource of type TICKET_INPUT
     */
    public int getNextIdInput( )
    {
        int nNextIdInput = 0;
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setResourceType( TicketingConstants.RESOURCE_TYPE_INPUT );
        entryFilter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        entryFilter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntry = EntryHome.getEntryList( entryFilter );
        ArrayList<Integer> listIdInput = new ArrayList<Integer>( );

        for ( Entry entry : listEntry )
        {
            listIdInput.add( entry.getIdResource( ) );
        }

        try
        {
            nNextIdInput = (Integer) Collections.max( listIdInput ) + 1;
        }
        catch( NoSuchElementException e )
        {
            nNextIdInput = 1;
        }

        return nNextIdInput;
    }

    /**
     * Check if an input is already used in a Category form
     *
     * @param nIdEntry
     *            the id of entry
     *
     * @return true if the Entry is linked to 1 or more Categories
     *
     */
    private boolean checkIfEntryIsLinkedToACategory( int nIdEntry )
    {
        boolean bIsUsedInput = false;
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        if ( ( entry != null ) && ( entry.getIdResource( ) >= 0 ) )
        {
            bIsUsedInput = TicketCategoryHome.checkIfInputIsUsedInCategories( entry.getIdResource( ) );
        }

        return bIsUsedInput;
    }
}
