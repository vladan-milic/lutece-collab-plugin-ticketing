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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.ticketform.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.ticketform.TicketFormHome;
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
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * JspBean to manage ticketing form entries
 */
@Controller( controllerJsp = "ManageTicketFormEntries.jsp", controllerPath = TicketingConstants.ADMIN_CONTROLLLER_PATH, right = TicketFormJspBean.RIGHT_MANAGETICKETFORM )
public class TicketFormEntryJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -4951787792196104967L;

    // Parameters
    private static final String PARAMETER_ID_ENTRY_TYPE = "id_type";
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PARAMETER_ID_FIELD = "id_field";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ENTRY_CODE = "entry_code";
    private static final String PARAMETER_FIELD_CODE = "field_code";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_ORDER_ID = "order_id_";
    private static final String PARAMETER_ADD_TO_GROUP = "add_to_group";
    private static final String PARAMETER_ID_ENTRY_GROUP = "id_entry_group";
    private static final String PARAMETER_ENTRY_ID_MOVE = "entry_id_move";
    private static final String PARAMETER_ID_EXPRESSION = "id_expression";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_NO_DISPLAY_TITLE = "no_display_title";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_FORM_GENERICATTR = "form_genericatt";
    private static final String FIELD_ENTRY_CODE = "ticketing.createEntry.labelCode";
    private static final String FIELD_TITLE_FIELD = "ticketing.createField.labelTitle";
    private static final String FIELD_CODE_FIELD = "ticketing.createField.labelCode";
    private static final String FIELD_VALUE_FIELD = "ticketing.createField.labelValue";

    // Urls
    private static final String JSP_URL_MANAGE_TICKETING_FORM_ENTRIES = "jsp/admin/plugins/ticketing/ManageTicketFormEntries.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_ENTRY = "ticketing.message.confirmRemoveEntry";
    private static final String MESSAGE_CANT_REMOVE_ENTRY = "advert.message.cantRemoveEntry";
    private static final String MESSAGE_MANDATORY_FIELD = "portal.util.message.mandatoryField";
    private static final String MESSAGE_FIELD_VALUE_FIELD = "ticketing.message.error.field_value_field";
    private static final String PROPERTY_CREATE_ENTRY_TITLE = "ticketing.createEntry.titleQuestion";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "ticketing.modifyEntry.titleQuestion";
    private static final String PROPERTY_COPY_ENTRY_TITLE = "ticketing.copyEntry.title";

    // Views
    private static final String VIEW_GET_CREATE_ENTRY = "getCreateEntry";
    private static final String VIEW_GET_MODIFY_ENTRY = "getModifyEntry";
    private static final String VIEW_CONFIRM_REMOVE_ENTRY = "confirmRemoveEntry";

    // Actions
    private static final String ACTION_DO_CREATE_ENTRY = "doCreateEntry";
    private static final String ACTION_DO_MODIFY_ENTRY = "doModifyEntry";
    private static final String ACTION_DO_REMOVE_ENTRY = "doRemoveEntry";
    private static final String ACTION_DO_COPY_ENTRY = "doCopyEntry";
    private static final String ACTION_DO_CHANGE_ORDER_ENTRY = "doChangeOrderEntry";
    private static final String ACTION_DO_MOVE_OUT_ENTRY = "doMoveOutEntry";
    private static final String ACTION_DO_MOVE_UP_ENTRY_CONDITIONAL = "doMoveUpEntryConditional";
    private static final String ACTION_DO_MOVE_DOWN_ENTRY_CONDITIONAL = "doMoveDownEntryConditional";
    private static final String ACTION_DO_REMOVE_REGULAR_EXPRESSION = "doRemoveRegularExpression";
    private static final String ACTION_DO_INSERT_REGULAR_EXPRESSION = "doInsertRegularExpression";

    // Marks
    private static final String MARK_REGULAR_EXPRESSION_LIST_REF_LIST = "regular_expression_list";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_LIST = "list";
    private static final String MARK_FORM = "form";
    private static final String MARK_ENTRY_TYPE_SERVICE = "entryTypeService";
    private static final String ENTRY_TYPE_LUTECE_USER_BEAN_NAME = "ticketing.entryTypeMyLuteceUser";

    // Local variables
    private EntryService _entryService = EntryService.getService(  );

    /**
     * Get the HTML code to create an entry
     * @param request The request
     * @return The HTML code to display or the next URL to redirect to
     */
    @View( value = VIEW_GET_CREATE_ENTRY )
    public String getCreateEntry( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) )
        {
            return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
        }

        String strIdType = request.getParameter( PARAMETER_ID_ENTRY_TYPE );

        if ( StringUtils.isEmpty( strIdType ) || !StringUtils.isNumeric( strIdType ) )
        {
            return redirect( request, TicketFormJspBean.getURLModifyAdvancedTicketForm( request, strIdForm ) );
        }

        int nIdForm = Integer.parseInt( strIdForm );
        int nIdType = Integer.parseInt( strIdType );

        Entry entry = new Entry(  );
        entry.setEntryType( EntryTypeHome.findByPrimaryKey( nIdType ) );

        String strIdField = request.getParameter( PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( StringUtils.isNotEmpty( strIdField ) && StringUtils.isNumeric( strIdField ) )
        {
            nIdField = Integer.parseInt( strIdField );

            Field field = new Field(  );
            field.setIdField( nIdField );
            entry.setFieldDepend( field );
        }

        entry.setIdResource( nIdForm );
        entry.setResourceType( TicketForm.RESOURCE_TYPE );

        TicketForm ticketForm = TicketFormHome.findByPrimaryKey( nIdForm );

        // Default Values
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ENTRY, entry );
        model.put( MARK_FORM, ticketForm );

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
     * @param request the request
     * @return The HTML code to display or the next URL to redirect to
     */
    @Action( ACTION_DO_CREATE_ENTRY )
    public String doCreateEntry( HttpServletRequest request )
    {
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );

        if ( StringUtils.isEmpty( strIdForm ) || !StringUtils.isNumeric( strIdForm ) )
        {
            return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
        }

        String strIdType = request.getParameter( PARAMETER_ID_ENTRY_TYPE );

        int nIdForm = Integer.parseInt( strIdForm );
        Field fieldDepend = null;

        if ( ( request.getParameter( PARAMETER_CANCEL ) == null ) && StringUtils.isNotEmpty( strIdType ) &&
                StringUtils.isNumeric( strIdType ) )
        {
            int nIdType = Integer.parseInt( strIdType );
            EntryType entryType = new EntryType(  );
            entryType.setIdType( nIdType );

            Entry entry = new Entry(  );
            entry.setEntryType( EntryTypeService.getInstance(  ).getEntryType( nIdType ) );

            String strIdField = request.getParameter( PARAMETER_ID_FIELD );
            int nIdField = -1;

            if ( StringUtils.isNotEmpty( strIdField ) && StringUtils.isNumeric( strIdField ) )
            {
                nIdField = Integer.parseInt( strIdField );

                fieldDepend = new Field(  );
                fieldDepend.setIdField( nIdField );
                entry.setFieldDepend( fieldDepend );
            }

            String strError = EntryTypeServiceManager.getEntryTypeService( entry )
                                                     .getRequestData( entry, request, getLocale(  ) );

            if ( strError != null )
            {
                return redirect( request, strError );
            }

            // entry code is mandatory for ticketing
            String strEntryCode = request.getParameter( PARAMETER_ENTRY_CODE );

            if ( StringUtils.isEmpty( strEntryCode ) &&
                    !entry.getEntryType(  ).getBeanName(  ).equals( ENTRY_TYPE_LUTECE_USER_BEAN_NAME ) )
            {
                String[] tabErr = new String[] { I18nService.getLocalizedString( FIELD_ENTRY_CODE, getLocale(  ) ) };

                return redirect( request,
                    AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabErr, AdminMessage.TYPE_STOP ) );
            }

            entry.setIdResource( nIdForm );
            entry.setResourceType( TicketForm.RESOURCE_TYPE );
            entry.setIdEntry( EntryHome.create( entry ) );

            if ( entry.getFields(  ) != null )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    field.setParentEntry( entry );
                    FieldHome.create( field );
                }
            }

            if ( request.getParameter( PARAMETER_APPLY ) != null )
            {
                return redirect( request, VIEW_GET_MODIFY_ENTRY, PARAMETER_ID_ENTRY, entry.getIdEntry(  ) );
            }
        }

        if ( fieldDepend != null )
        {
            return redirect( request, TicketFormFieldJspBean.getUrlModifyField( request, fieldDepend.getIdField(  ) ) );
        }

        return redirect( request, TicketFormJspBean.getURLModifyAdvancedTicketForm( request, strIdForm ) );
    }

    /**
     * Gets the entry modification page
     * @param request The HTTP request
     * @return The entry modification page
     */
    @View( VIEW_GET_MODIFY_ENTRY )
    public String getModifyEntry( HttpServletRequest request )
    {
        Plugin plugin = getPlugin(  );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );

            if ( nIdEntry <= 0 )
            {
                return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
            }

            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            List<Field> listField = new ArrayList<Field>( entry.getFields(  ).size(  ) );

            for ( Field field : entry.getFields(  ) )
            {
                field = FieldHome.findByPrimaryKey( field.getIdField(  ) );
                listField.add( field );
            }

            entry.setFields( listField );

            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ENTRY, entry );
            model.put( MARK_FORM, TicketFormHome.findByPrimaryKey( entry.getIdResource(  ) ) );

            UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + getViewUrl( VIEW_GET_MODIFY_ENTRY ) );
            urlItem.addParameter( PARAMETER_ID_ENTRY, strIdEntry );

            model.put( MARK_LIST, entry.getFields(  ) );

            ReferenceList refListRegularExpression = entryTypeService.getReferenceListRegularExpression( entry, plugin );

            if ( refListRegularExpression != null )
            {
                model.put( MARK_REGULAR_EXPRESSION_LIST_REF_LIST, refListRegularExpression );
            }

            ModelUtils.storeRichText( request, model );

            model.put( MARK_ENTRY_TYPE_SERVICE, EntryTypeServiceManager.getEntryTypeService( entry ) );

            return getPage( PROPERTY_MODIFY_QUESTION_TITLE, entryTypeService.getTemplateModify( entry, false ), model );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the
     * field specified in parameter. return null if there is no error or else
     * return the error page URL
     * @param request the request
     * @param field field
     * @return null if there is no error or else return the error page URL
     */
    private String getFieldData( HttpServletRequest request, Field field )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strCode = request.getParameter( PARAMETER_FIELD_CODE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strNoDisplayTitle = request.getParameter( PARAMETER_NO_DISPLAY_TITLE );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        String strFieldError = null;

        if ( StringUtils.isEmpty( strTitle ) )
        {
            strFieldError = FIELD_TITLE_FIELD;
        }
        else if ( StringUtils.isEmpty( strCode ) )
        {
            strFieldError = FIELD_CODE_FIELD;
        }
        else if ( StringUtils.isEmpty( strValue ) )
        {
            strFieldError = FIELD_VALUE_FIELD;
        }
        else if ( !StringUtil.checkCodeKey( strValue ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_FIELD_VALUE_FIELD, AdminMessage.TYPE_STOP );
        }

        if ( strFieldError != null )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, getLocale(  ) ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        field.setTitle( strTitle );
        field.setValue( strValue );
        field.setComment( strComment );
        field.setCode( strCode );

        field.setDefaultValue( strDefaultValue != null );
        field.setNoDisplayTitle( strNoDisplayTitle != null );

        return null; // No error
    }

    /**
     * Perform the entry modification
     * @param request The HTTP request
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
                return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
            }

            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            if ( request.getParameter( PARAMETER_CANCEL ) == null )
            {
                String strError = EntryTypeServiceManager.getEntryTypeService( entry )
                                                         .getRequestData( entry, request, getLocale(  ) );

                // entry code is mandatory for ticketing
                String strEntryCode = request.getParameter( PARAMETER_ENTRY_CODE );

                if ( StringUtils.isEmpty( strEntryCode ) )
                {
                    String[] tabErr = new String[] { I18nService.getLocalizedString( FIELD_ENTRY_CODE, getLocale(  ) ) };

                    return redirect( request,
                        AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabErr,
                            AdminMessage.TYPE_STOP ) );
                }

                if ( strError != null )
                {
                    return redirect( request, strError );
                }

                EntryHome.update( entry );

                if ( entry.getFields(  ) != null )
                {
                    for ( Field field : entry.getFields(  ) )
                    {
                        // Check if the field already exists in the database
                        Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField(  ) );

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

            String strUrl;

            if ( entry.getFieldDepend(  ) != null )
            {
                strUrl = TicketFormFieldJspBean.getUrlModifyField( request, entry.getFieldDepend(  ).getIdField(  ) );
            }
            else
            {
                strUrl = TicketFormJspBean.getURLModifyAdvancedTicketForm( request,
                        Integer.toString( entry.getIdResource(  ) ) );
            }

            return redirect( request, strUrl );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Gets the confirmation page of delete entry
     * @param request The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( VIEW_CONFIRM_REMOVE_ENTRY )
    public String getConfirmRemoveEntry( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        UrlItem url = new UrlItem( getActionUrl( ACTION_DO_REMOVE_ENTRY ) );
        url.addParameter( PARAMETER_ID_ENTRY, strIdEntry );

        return redirect( request,
            AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ENTRY, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Perform the entry removal
     * @param request The HTTP request
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
                return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
            }

            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            List<String> listErrors = new ArrayList<String>(  );

            if ( !_entryService.checkForRemoval( strIdEntry, listErrors, getLocale(  ) ) )
            {
                String strCause = AdminMessageService.getFormattedList( listErrors, getLocale(  ) );
                Object[] args = { strCause };

                return AdminMessageService.getMessageUrl( request, MESSAGE_CANT_REMOVE_ENTRY, args,
                    AdminMessage.TYPE_STOP );
            }

            // Update order
            List<Entry> listEntry;
            EntryFilter filter = new EntryFilter(  );
            filter.setIdResource( entry.getIdResource(  ) );
            filter.setResourceType( TicketForm.RESOURCE_TYPE );
            listEntry = EntryHome.getEntryList( filter );

            if ( entry.getFieldDepend(  ) == null )
            {
                _entryService.moveDownEntryOrder( listEntry.size(  ), entry );
            }
            else
            {
                //conditional questions
                EntryHome.decrementOrderByOne( entry.getPosition(  ), entry.getFieldDepend(  ).getIdField(  ),
                    entry.getIdResource(  ), entry.getResourceType(  ) );
            }

            TicketHome.removeResponsesByIdEntry( nIdEntry );

            // Remove entry
            EntryHome.remove( nIdEntry );

            if ( entry.getFieldDepend(  ) != null )
            {
                return redirect( request,
                    TicketFormFieldJspBean.getUrlModifyField( request, entry.getFieldDepend(  ).getIdField(  ) ) );
            }

            return redirect( request,
                TicketFormJspBean.getURLModifyAdvancedTicketForm( request, entry.getIdResource(  ) ) );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Do move up an conditional entry of a field
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_UP_ENTRY_CONDITIONAL )
    public String doMoveUpEntryConditional( HttpServletRequest request )
    {
        return doMoveEntryConditional( request, true );
    }

    /**
     * Do move down an conditional entry of a field
     * @param request The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_DOWN_ENTRY_CONDITIONAL )
    public String doMoveDownEntryConditional( HttpServletRequest request )
    {
        return doMoveEntryConditional( request, false );
    }

    /**
     * Do move up or down an conditional entry of a field
     * @param request The request
     * @param bMoveUp True to move the entry up, false to move it down
     * @return The next URL to redirect to
     */
    private String doMoveEntryConditional( HttpServletRequest request, boolean bMoveUp )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );
            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );
            int nNewPosition = bMoveUp ? ( entry.getPosition(  ) - 1 ) : ( entry.getPosition(  ) + 1 );

            if ( nNewPosition > 0 )
            {
                Entry entryToMove = EntryHome.findByOrderAndIdFieldAndIdResource( nNewPosition,
                        entry.getFieldDepend(  ).getIdField(  ), entry.getIdResource(  ), entry.getResourceType(  ) );

                if ( entryToMove != null )
                {
                    entryToMove.setPosition( entry.getPosition(  ) );
                    EntryHome.update( entryToMove );
                    entry.setPosition( nNewPosition );
                    EntryHome.update( entry );
                }
            }

            return redirect( request,
                TicketFormFieldJspBean.getUrlModifyField( request, entry.getFieldDepend(  ).getIdField(  ) ) );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Copy the entry whose key is specified in the HTTP request
     * @param request The HTTP request
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
                return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
            }

            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            Object[] tabEntryTileCopy = { entry.getTitle(  ) };
            String strTitleCopyEntry = I18nService.getLocalizedString( PROPERTY_COPY_ENTRY_TITLE, tabEntryTileCopy,
                    getLocale(  ) );

            if ( strTitleCopyEntry != null )
            {
                entry.setTitle( strTitleCopyEntry );
            }

            EntryHome.copy( entry );

            // If the entry has a parent
            if ( entry.getParent(  ) != null )
            {
                // We reload the entry to get the copy and not he original entry
                // The id of the entry is the id of the copy. It has been set by the create method of EntryDAO 
                entry = EntryHome.findByPrimaryKey( entry.getIdEntry(  ) );

                Entry entryParent = EntryHome.findByPrimaryKey( entry.getParent(  ).getIdEntry(  ) );
                _entryService.moveUpEntryOrder( entryParent.getPosition(  ) + entryParent.getChildren(  ).size(  ),
                    entry );
            }

            if ( entry.getFieldDepend(  ) != null )
            {
                return redirect( request,
                    TicketFormFieldJspBean.getUrlModifyField( request, entry.getFieldDepend(  ).getIdField(  ) ) );
            }

            return redirect( request,
                TicketFormJspBean.getURLModifyAdvancedTicketForm( request, entry.getIdResource(  ) ) );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Change the attribute's order (move up or move down in the list)
     * @param request the request
     * @return The URL of the form management page
     */
    @Action( ACTION_DO_CHANGE_ORDER_ENTRY )
    public String doChangeOrderEntry( HttpServletRequest request )
    {
        //gets the entry which needs to be changed (order)
        String strIdForm = request.getParameter( PARAMETER_ID_FORM );
        int nIdForm = Integer.parseInt( strIdForm );

        // If the parameter move.x has been set, then we have to add entries to a group
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_ADD_TO_GROUP ) ) )
        {
            String strIdEntryGroup = request.getParameter( PARAMETER_ID_ENTRY_GROUP );

            if ( StringUtils.isNotEmpty( strIdEntryGroup ) && StringUtils.isNumeric( strIdEntryGroup ) )
            {
                int nIdEntryGroup = Integer.parseInt( strIdEntryGroup );
                Entry entryParent = EntryHome.findByPrimaryKey( nIdEntryGroup );
                String[] strArrayIdEntries = request.getParameterValues( PARAMETER_ENTRY_ID_MOVE );

                if ( ( strArrayIdEntries != null ) && ( strArrayIdEntries.length > 0 ) )
                {
                    for ( String strIdEntry : strArrayIdEntries )
                    {
                        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
                        {
                            int nIdEntry = Integer.parseInt( strIdEntry );
                            _entryService.moveEntryIntoGroup( EntryHome.findByPrimaryKey( nIdEntry ), entryParent );
                        }
                    }
                }
            }
        }
        else
        {
            Integer nEntryId = Integer.parseInt( request.getParameter( PARAMETER_ID_ENTRY ) );
            Integer nOrderToSet = Integer.parseInt( request.getParameter( PARAMETER_ORDER_ID +
                        request.getParameter( PARAMETER_ID_ENTRY ) ) );

            Entry entryToChangeOrder = EntryHome.findByPrimaryKey( nEntryId );
            int nActualOrder = entryToChangeOrder.getPosition(  );

            // does nothing if the order to set is equal to the actual order
            if ( nOrderToSet != nActualOrder )
            {
                // entry goes up in the list 
                if ( nOrderToSet < entryToChangeOrder.getPosition(  ) )
                {
                    _entryService.moveUpEntryOrder( nOrderToSet, entryToChangeOrder );
                }

                // entry goes down in the list
                else
                {
                    _entryService.moveDownEntryOrder( nOrderToSet, entryToChangeOrder );
                }
            }
        }

        return redirect( request, TicketFormJspBean.getURLModifyAdvancedTicketForm( request, nIdForm ) );
    }

    /**
     * Remove an entry from a group
     * @param request The request
     * @return The newt URL to redirect to
     */
    @Action( ACTION_DO_MOVE_OUT_ENTRY )
    public String doMoveOutEntry( HttpServletRequest request )
    {
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );

        if ( StringUtils.isNotEmpty( strIdEntry ) && StringUtils.isNumeric( strIdEntry ) )
        {
            int nIdEntry = Integer.parseInt( strIdEntry );
            Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

            if ( entry.getParent(  ) != null )
            {
                _entryService.moveOutEntryFromGroup( entry );
            }

            return redirect( request,
                TicketFormJspBean.getURLModifyAdvancedTicketForm( request, entry.getIdResource(  ) ) );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Delete the association between a field and and regular expression
     * @param request the HTTP Request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_REMOVE_REGULAR_EXPRESSION )
    public String doRemoveRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isNotEmpty( strIdExpression ) && StringUtils.isNotEmpty( strIdField ) &&
                StringUtils.isNumeric( strIdExpression ) && StringUtils.isNumeric( strIdField ) )
        {
            int nIdField = Integer.parseInt( strIdField );
            int nIdExpression = Integer.parseInt( strIdExpression );
            FieldHome.removeVerifyBy( nIdField, nIdExpression );

            Field field = FieldHome.findByPrimaryKey( nIdField );

            return redirect( request, VIEW_GET_MODIFY_ENTRY, PARAMETER_ID_ENTRY, field.getParentEntry(  ).getIdEntry(  ) );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Insert an association between a field and a regular expression
     * @param request the HTTP Request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_DO_INSERT_REGULAR_EXPRESSION )
    public String doInsertRegularExpression( HttpServletRequest request )
    {
        String strIdExpression = request.getParameter( PARAMETER_ID_EXPRESSION );
        String strIdField = request.getParameter( PARAMETER_ID_FIELD );

        if ( StringUtils.isNotEmpty( strIdExpression ) && StringUtils.isNotEmpty( strIdField ) &&
                StringUtils.isNumeric( strIdExpression ) && StringUtils.isNumeric( strIdField ) )
        {
            int nIdField = Integer.parseInt( strIdField );
            int nIdExpression = Integer.parseInt( strIdExpression );

            FieldHome.createVerifyBy( nIdField, nIdExpression );

            Field field = FieldHome.findByPrimaryKey( nIdField );

            return redirect( request, VIEW_GET_MODIFY_ENTRY, PARAMETER_ID_ENTRY, field.getParentEntry(  ).getIdEntry(  ) );
        }

        return redirect( request, TicketFormJspBean.getURLManageTicketForms( request ) );
    }

    /**
     * Get the URL to modify an entry
     * @param request The request
     * @param nIdEntry The id of the entry
     * @return The URL to modify the given entry
     */
    public static String getURLModifyEntry( HttpServletRequest request, int nIdEntry )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_TICKETING_FORM_ENTRIES );
        urlItem.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_GET_MODIFY_ENTRY );
        urlItem.addParameter( PARAMETER_ID_ENTRY, nIdEntry );
        urlItem.addParameter( MVCUtils.PARAMETER_PAGE, PARAMETER_FORM_GENERICATTR );

        return urlItem.getUrl(  );
    }
}
