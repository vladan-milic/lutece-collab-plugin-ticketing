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
package fr.paris.lutece.plugins.ticketing.web.ticketfilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.service.TicketResourceIdService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.business.rbac.AdminRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * Helper class used to manage TicketFilter
 *
 * @author s267533
 *
 */
public final class TicketFilterHelper
{
    // parameters
    private static final String PARAMETER_FILTER_ID_TICKET             = "fltr_id_ticket";
    private static final String PARAMETER_FILTER_OPEN_SINCE            = "fltr_open_since";
    private static final String PARAMETER_FILTER_OPEN_UNTIL            = "fltr_open_until";
    private static final String PARAMETER_FILTER_LASTUPDATE_DATE       = "fltr_lastupdate";
    private static final String PARAMETER_FILTER_START_LASTUPDATE_DATE = "fltr_start_lastupdate";
    private static final String PARAMETER_FILTER_END_LASTUPDATE_DATE   = "fltr_end_lastupdate";
    private static final String PARAMETER_FILTER_CREATION_DATE         = "fltr_creationdate";
    private static final String PARAMETER_FILTER_CLOSE_DATE            = "fltr_closedate";
    private static final String PARAMETER_FILTER_END_CREATION_DATE     = "fltr_end_creationdate";
    private static final String PARAMETER_FILTER_START_CREATION_DATE   = "fltr_start_creationdate";
    private static final String PARAMETER_FILTER_ID_USER               = "fltr_id_user";
    private static final String PARAMETER_FILTER_ID_CATEGORY           = "fltr_id_category";
    private static final String PARAMETER_FILTER_ID_DOMAIN             = "fltr_id_domain";
    private static final String PARAMETER_FILTER_ID_TYPE               = "fltr_id_type";
    private static final String PARAMETER_FILTER_ORDER_BY              = "fltr_order_by";
    private static final String PARAMETER_FILTER_EMAIL                 = "fltr_email";
    private static final String PARAMETER_FILTER_LASTNAME              = "fltr_lastname";
    private static final String PARAMETER_FILTER_FIRSTNAME             = "fltr_firstname";
    private static final String PARAMETER_FILTER_NOMENCLATURE          = "fltr_nomenclature";
    private static final String PARAMETER_FILTER_FIXED_PHONE_NUMBER    = "fltr_fixed_phone_number";
    private static final String PARAMETER_FILTER_MOBILE_PHONE_NUMBER   = "fltr_mobile_phone_number";
    private static final String PARAMETER_FILTER_URGENCY               = "fltr_urgency";
    private static final String PARAMETER_FILTER_NEW_URGENCY           = "fltr_new_urgency";
    private static final String PARAMETER_FILTER_REFERENCE             = "fltr_reference";
    private static final String PARAMETER_FILTER_ORDER_SORT            = "fltr_order_sort";
    private static final String PARAMETER_FILTER_SUBMITTED_FORM        = "submitted_form";
    private static final String PARAMETER_FILTER_WORKFLOW_STATE_IDS    = "fltr_state_ids";

    // Marks
    private static final String MARK_FULL_DOMAIN_LIST                  = "domain_list";
    private static final String MARK_FULL_TYPE_LIST                    = "type_list";
    private static final String MARK_FILTER_PERIOD_LIST                = "period_list";
    private static final String MARK_TICKET_FILTER                     = "ticket_filter";
    private static final String MARK_FULL_STATE_LIST                   = "state_list";
    private static final String DATE_FILTER_PATTERN                    = "yyyyMMdd";
    private static final String DATETIME_FILTER_PATTERN                = "yyyyMMdd hh:mm:ss";
    private static final String TIME_START_OF_DAY                      = " 00:00:00";
    private static final String TIME_END_OF_DAY                        = " 23:59:59";

    // Properties for page titles
    private static final String PROPERTY_TICKET_TYPE_LABEL             = "ticketing.model.entity.ticket.attribute.ticketType";
    private static final String PROPERTY_TICKET_DOMAIN_LABEL           = "ticketing.model.entity.ticket.attribute.ticketDomain";
    private static final String NO_SELECTED_FIELD_ID                   = "-1";

    /**
     * private constructor
     */
    private TicketFilterHelper( )
    {
        super( );
    }

    /**
     * returns a fltrFiltre instancied from a request
     *
     * @param request
     *            request
     * @return TicketFilter fltrFiltre initialized from request parameters
     * @throws ParseException
     *             if date is not well formated
     */
    private static TicketFilter getFilterFromRequest( HttpServletRequest request ) throws ParseException
    {
        TicketFilter fltrFiltre = new TicketFilter( );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy hh:mm:ss" );

        if ( ( request.getParameterValues( PARAMETER_FILTER_WORKFLOW_STATE_IDS ) != null ) && ( request.getParameterValues( PARAMETER_FILTER_WORKFLOW_STATE_IDS ).length > 0 ) )
        {
            fltrFiltre.setListIdWorkflowState( request.getParameterValues( PARAMETER_FILTER_WORKFLOW_STATE_IDS ) );
        } else
        {
            // no state selected => we put a dummy one
            fltrFiltre.setListIdWorkflowState( new String[] { NO_SELECTED_FIELD_ID } );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_DOMAIN ) ) && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_DOMAIN ) ) )
        {
            fltrFiltre.setIdDomain( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_DOMAIN ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ORDER_BY ) ) )
        {
            fltrFiltre.setOrderBy( request.getParameter( PARAMETER_FILTER_ORDER_BY ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ORDER_SORT ) ) )
        {
            fltrFiltre.setOrderSort( request.getParameter( PARAMETER_FILTER_ORDER_SORT ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_CATEGORY ) ) && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_CATEGORY ) ) )
        {
            fltrFiltre.setIdCategory( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_CATEGORY ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_USER ) ) && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_USER ) ) )
        {
            fltrFiltre.setIdUser( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_USER ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_TYPE ) ) && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_TYPE ) ) )
        {
            fltrFiltre.setIdType( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_TYPE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_TICKET ) ) && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_TICKET ) ) )
        {
            fltrFiltre.setIdTicket( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_TICKET ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_CREATION_DATE ) ) )
        {
            fltrFiltre.setCreationDate( getDateFromString( request.getParameter( PARAMETER_FILTER_CREATION_DATE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_END_CREATION_DATE ) ) )
        {
            fltrFiltre.setCreationEndDate( getDateFromString( request.getParameter( PARAMETER_FILTER_END_CREATION_DATE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_START_CREATION_DATE ) ) )
        {
            fltrFiltre.setCreationStartDate( getDateFromString( request.getParameter( PARAMETER_FILTER_START_CREATION_DATE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_START_LASTUPDATE_DATE ) ) )
        {
            fltrFiltre.setLastUpdateStartDate( getDateFromString( request.getParameter( PARAMETER_FILTER_START_LASTUPDATE_DATE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_END_LASTUPDATE_DATE ) ) )
        {
            fltrFiltre.setLastUpdateEndDate( getDateFromString( request.getParameter( PARAMETER_FILTER_END_LASTUPDATE_DATE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_LASTUPDATE_DATE ) ) )
        {
            fltrFiltre.setLastUpdateDate( getDateFromString( request.getParameter( PARAMETER_FILTER_LASTUPDATE_DATE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_CLOSE_DATE ) ) )
        {
            fltrFiltre.setCloseDate( getDateFromString( request.getParameter( PARAMETER_FILTER_CLOSE_DATE ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_EMAIL ) ) )
        {
            fltrFiltre.setEmail( request.getParameter( PARAMETER_FILTER_EMAIL ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_LASTNAME ) ) )
        {
            fltrFiltre.setLastName( request.getParameter( PARAMETER_FILTER_LASTNAME ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_NOMENCLATURE ) ) )
        {
            fltrFiltre.setNomenclature( request.getParameter( PARAMETER_FILTER_NOMENCLATURE ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_FIRSTNAME ) ) )
        {
            fltrFiltre.setFirstName( request.getParameter( PARAMETER_FILTER_FIRSTNAME ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_FIXED_PHONE_NUMBER ) ) )
        {
            fltrFiltre.setFixedPhoneNumber( request.getParameter( PARAMETER_FILTER_FIXED_PHONE_NUMBER ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_MOBILE_PHONE_NUMBER ) ) )
        {
            fltrFiltre.setMobilePhoneNumber( request.getParameter( PARAMETER_FILTER_MOBILE_PHONE_NUMBER ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_REFERENCE ) ) )
        {
            fltrFiltre.setReference( request.getParameter( PARAMETER_FILTER_REFERENCE ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_OPEN_SINCE ) ) )
        {
            Date dateStart = simpleDateFormat.parse( request.getParameter( PARAMETER_FILTER_OPEN_SINCE ) + TIME_START_OF_DAY );
            fltrFiltre.setCreationStartDate( dateStart );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_OPEN_UNTIL ) ) )
        {
            Date dateEnd = simpleDateFormat.parse( request.getParameter( PARAMETER_FILTER_OPEN_UNTIL ) + TIME_END_OF_DAY );
            fltrFiltre.setCreationEndDate( dateEnd );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_URGENCY ) ) )
        {
            fltrFiltre.setUrgency( Integer.parseInt( request.getParameter( PARAMETER_FILTER_URGENCY ) ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_NEW_URGENCY ) ) )
        {
            fltrFiltre.setUrgency( Integer.parseInt( request.getParameter( PARAMETER_FILTER_NEW_URGENCY ) ) );
        }

        return fltrFiltre;
    }

    /**
     * return filter from request / session
     *
     * @param request
     *            http servlet request
     * @param adminUser
     *            admin user
     * @return filter
     */
    public static TicketFilter getFilter( HttpServletRequest request, AdminUser adminUser ) throws ParseException
    {
        TicketFilter filter = null;

        if ( StringUtils.isEmpty( request.getParameter( PARAMETER_FILTER_SUBMITTED_FORM ) ) )
        {
            // filter form has not been submitted => we use the one stored in session if exists
            if ( request.getSession( ).getAttribute( TicketingConstants.SESSION_TICKET_FILTER ) != null )
            {
                filter = ( TicketFilter ) request.getSession( ).getAttribute( TicketingConstants.SESSION_TICKET_FILTER );
            } else
            {
                // set default value
                filter = getDefaultFilter( adminUser );
                request.getSession( ).setAttribute( TicketingConstants.SESSION_TICKET_FILTER, filter );
            }
        } else
        {
            filter = TicketFilterHelper.getFilterFromRequest( request );
            request.getSession( ).setAttribute( TicketingConstants.SESSION_TICKET_FILTER, filter );
        }

        return filter;
    }

    /**
     * returns default filter
     *
     * @param adminUser
     *            admin user
     * @return default filter
     */
    public static TicketFilter getDefaultFilter( AdminUser adminUser )
    {
        TicketFilter filter = new TicketFilter( );

        filter.setOrderBy( TicketFilter.CONSTANT_DEFAULT_ORDER_BY );
        filter.setOrderSort( filter.getDefaultOrderSort( ) );

        Map<String, AdminRole> listUserRole = AdminUserHome.getRolesListForUser( adminUser.getUserId( ) );

        // set default filtering for user from configuration
        List<Integer> lstIdWorkflowState = PluginConfigurationService.getIntegerList( PluginConfigurationService.PROPERTY_STATES_SELECTED, null );
        Map<String, List<Integer>> mapStatesForRoles = PluginConfigurationService.getIntegerListByPrefix( PluginConfigurationService.PROPERTY_STATES_SELECTED_FOR_ROLE_PREFIX, null );

        for ( String strRole : listUserRole.keySet( ) )
        {
            if ( ( mapStatesForRoles != null ) && mapStatesForRoles.containsKey( strRole ) )
            {
                lstIdWorkflowState = mapStatesForRoles.get( strRole );

                break;
            }
        }

        if ( ( lstIdWorkflowState != null ) && !lstIdWorkflowState.isEmpty( ) )
        {
            List<Integer> lstIdWorkflowStateTemp = new ArrayList<>( );
            for ( Integer idState : lstIdWorkflowState )
            {
                State state = new State( );
                state.setId( idState );
                if ( RBACService.isAuthorized( state, TicketResourceIdService.PERMISSION_VIEW, adminUser ) )
                {
                    lstIdWorkflowStateTemp.add( state.getId( ) );
                }
            }
            lstIdWorkflowState.clear( );
            lstIdWorkflowState.addAll( lstIdWorkflowStateTemp );
        }

        if ( ( lstIdWorkflowState == null ) || lstIdWorkflowState.isEmpty( ) )
        {
            lstIdWorkflowState = new ArrayList<>( );
            lstIdWorkflowState.add( Integer.valueOf( NO_SELECTED_FIELD_ID ) );
        }

        filter.setListIdWorkflowState( lstIdWorkflowState );

        return filter;
    }

    /**
     * set filter param to model
     *
     * @param mapModel
     *            model to update
     * @param fltrFilter
     *            filter attribute to set to model
     * @param request
     *            http request
     * @param user
     *            current admin user
     */
    public static void setModel( Map<String, Object> mapModel, TicketFilter fltrFilter, HttpServletRequest request, AdminUser user )
    {
        mapModel.put( MARK_FILTER_PERIOD_LIST, TicketFilterPeriod.getReferenceList( request.getLocale( ) ) );
        
        Map<String, String> mapTypes = new LinkedHashMap<String, String>( );
        mapTypes.put( StringUtils.EMPTY, I18nService.getLocalizedString( PROPERTY_TICKET_TYPE_LABEL, request.getLocale( ) ) );

        Map<String, String> mapDomains = new LinkedHashMap<String, String>( );
        mapDomains.put( StringUtils.EMPTY, I18nService.getLocalizedString( PROPERTY_TICKET_DOMAIN_LABEL, request.getLocale( ) ) );

        boolean isTypeAuthorized = false;

        for ( TicketCategory type : TicketCategoryService.getInstance( ).getTypeList(  ) )
        {
            isTypeAuthorized = false;

            for ( TicketCategory domain : type.getChildren( ) )
            {
                // Check user rights
                if ( RBACService.isAuthorized( domain, TicketCategory.PERMISSION_VIEW_LIST, user ) )
                {
                    isTypeAuthorized = true;
                    
                    if ( ( fltrFilter.getIdType( ) < 0 && !mapDomains.containsValue( domain.getLabel( ) ) )
                            || fltrFilter.getIdType( ) == type.getId( ) )
                    {
                        mapDomains.put( String.valueOf (domain.getId( ) ), domain.getLabel( ) );
                    }
                }
            }
            
            if ( isTypeAuthorized )
            {
                mapTypes.put( String.valueOf (type.getId( ) ), type.getLabel( ) );
            }
        }

        ReferenceList refListStates = new ReferenceList( );
        refListStates.addAll( getWorkflowStates( user, fltrFilter.getListIdWorkflowState( ) ) );

        mapModel.put( MARK_TICKET_FILTER, fltrFilter );
        mapModel.put( MARK_FULL_TYPE_LIST, ReferenceList.convert( mapTypes ) );
        mapModel.put( MARK_FULL_DOMAIN_LIST, ReferenceList.convert( mapDomains ) );
        mapModel.put( MARK_FULL_STATE_LIST, refListStates );
    }

    /**
     * returns workflow states for filtering
     *
     * @param user
     *            admin user
     * @param listSelectedStates
     *            list of states selected by user
     * @return ReferenceList filled with all states
     */
    public static ReferenceList getWorkflowStates( AdminUser user, List<Integer> listSelectedStates )
    {
        ReferenceList refList = new ReferenceList( );
        Collection<State> collState = WorkflowService.getInstance( )
                .getAllStateByWorkflow( PluginConfigurationService.getInt( PluginConfigurationService.PROPERTY_TICKET_WORKFLOW_ID, TicketingConstants.PROPERTY_UNSET_INT ), user );

        for ( State state : collState )
        {
            ReferenceItem item = new ReferenceItem( );
            item.setCode( String.valueOf( state.getId( ) ) );
            item.setName( state.getName( ) );
            item.setChecked( listSelectedStates.contains( Integer.valueOf( state.getId( ) ) ) );
            refList.add( item );
        }

        return refList;
    }

    /**
     * returns a Date object from a String date String has to respect yyyyMMdd format unless a parseException is thrown
     *
     * @param strDate
     *            yyyyMMdd date
     * @return date
     * @throws ParseException
     *             exception when error occurs while parsing date
     */
    private static Date getDateFromString( String strDate )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FILTER_PATTERN );
        Date date = null;

        try
        {
            date = sdf.parse( strDate );
        } catch ( ParseException pe )
        {
            AppLogService.info( "Invalid date : " + strDate + ". The expected pattern is : " + DATE_FILTER_PATTERN );
        }

        return date;
    }

    /**
     * returns a Date object from a String date String has to respect yyyyMMdd hh:mm:ss format unless a parseException is thrown
     *
     * @param strDate
     *            yyyyMMdd hh:mm:ss datetime
     * @return date
     * @throws ParseException
     *             exception when error occurs while parsing date
     */
    private static Date getDateTimeFromString( String strDate )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( DATETIME_FILTER_PATTERN );
        Date date = null;

        try
        {
            date = sdf.parse( strDate );
        } catch ( ParseException pe )
        {
            AppLogService.info( "Invalid date : " + strDate + ". The expected pattern is : " + DATETIME_FILTER_PATTERN );
        }

        return date;
    }

    /**
     * Update ids of user and units in a given filter
     *
     * @param filter
     * @param user
     */

    public static void setFilterUserAndUnitIds( TicketFilter filter, AdminUser user )
    {
        List<Unit> lstUserUnits = UnitHome.findByIdUser( user.getUserId( ) );
        Set<Integer> setAssigneeUnitId = new HashSet<>( );
        Set<Integer> setAssignerUnitId = new HashSet<>( );
        for ( Unit unit : lstUserUnits )
        {
            setAssignerUnitId.add( unit.getIdUnit( ) );
            setAssigneeUnitId.add( unit.getIdUnit( ) );
            setAssigneeUnitId.addAll( UnitHome.getAllSubUnitsId( unit.getIdUnit( ) ) );
        }

        filter.setFilterIdAdminUser( user.getUserId( ) );
        filter.setFilterIdAssigneeUnit( setAssigneeUnitId );
        filter.setFilterIdAssignerUnit( setAssignerUnitId );

        filter.setAdminUserRoles( user.getRoles( ).keySet( ) );
    }
}
