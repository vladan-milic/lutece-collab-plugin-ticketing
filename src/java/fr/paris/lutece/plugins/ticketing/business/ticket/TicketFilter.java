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
package fr.paris.lutece.plugins.ticketing.business.ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.OrderByFilter;

/**
 *
 * class TicketFilter
 *
 */
public class TicketFilter extends OrderByFilter
{
    /**
     * Value for boolean filters to represent Boolean.FALSE
     */
    public static final int FILTER_FALSE = 0;

    /**
     * Value for boolean filters to represent Boolean.TRUE
     */
    public static final int FILTER_TRUE = 1;
    public static final int CONSTANT_ID_NULL = -1;

    /**
     * Default order by
     */
    public static final String CONSTANT_DEFAULT_ORDER_BY = "date_create";
    public static final String CONSTANT_DEFAULT_ORDER_SORT = OrderSortAllowed.DESC.name( );
    private int _nIdTicket = CONSTANT_ID_NULL;
    private Date _dateLastUpdateDate;
    private Date _dateLastUpdateStartDate;
    private Date _dateLastUpdateEndDate;
    private Date _dateCreationDate;
    private Date _dateCreationStartDate;
    private Date _dateCreationEndDate;
    private Date _dateCloseDate;
    private int _nIdUser = CONSTANT_ID_NULL;
    private int _nIdChannel = CONSTANT_ID_NULL;
    private int _nOpenSincePeriod = CONSTANT_ID_NULL;
    private String _strStatus;
    private String _strEmail;
    private String _strLastName;
    private String _strFirstName;
    private String _strReference;
    private String _strFixedPhoneNumber;
    private String _strMobilePhoneNumber;
    private String _strNomenclature;
    private int _nUrgency = CONSTANT_ID_NULL;
    private List<Integer> _listIdWorkflowState = new ArrayList<Integer>( );
    private TicketFilterViewEnum _enumFilterView = TicketFilterViewEnum.ALL;
    private int _nFilterIdAdminUser = CONSTANT_ID_NULL;
    private Set<Integer> _setFilterIdAssigneeUnit = null;
    private Set<Integer> _setFilterIdAssignerUnit = null;
    private Set<String> _setAdminUserRoles = null;
    private int _nTicketsLimitStart = CONSTANT_ID_NULL;
    private int _nTicketsLimitCount = CONSTANT_ID_NULL;
    private Map<Integer, Integer> _mapCategoryId = new LinkedHashMap<Integer, Integer>( );
    private Set<String> _listMarkingsId = new HashSet<>( );

    /**
     * Check if this filter contains a idUser
     *
     * @return true if the filter contain an id of ticket
     */
    public boolean containsIdUser( )
    {
        return ( _nIdUser != CONSTANT_ID_NULL );
    }

    /**
     * @return the _nIdUser
     */
    public int getIdUser( )
    {
        return _nIdUser;
    }

    /**
     * @param nIdUser
     *            the nIdUser to set
     */
    public void setIdUser( int nIdUser )
    {
        _nIdUser = nIdUser;
    }

    /**
     * Check if this filter contains a idUser
     *
     * @return true if the filter contain an id of ticket
     */
    public boolean containsListIdWorkflowState( )
    {
        return ( ( _listIdWorkflowState != null ) && ( !_listIdWorkflowState.isEmpty( ) ) );
    }

    /**
     * @return the _nIdWorkflowState
     */
    public List<Integer> getListIdWorkflowState( )
    {
        return _listIdWorkflowState;
    }

    /**
     * @param lstIdWorkflowState
     *            the lstIdWorkflowState to set
     */
    public void setListIdWorkflowState( List<Integer> lstIdWorkflowState )
    {
        _listIdWorkflowState = lstIdWorkflowState;
    }

    /**
     * @param tabIdWorkflowState
     *            the tabIdWorkflowState to set
     */
    public void setListIdWorkflowState( String [ ] tabIdWorkflowState )
    {
        _listIdWorkflowState = new ArrayList<Integer>( );

        for ( String strId : tabIdWorkflowState )
        {
            _listIdWorkflowState.add( Integer.valueOf( strId ) );
        }
    }

    /**
     * @return the _nIdTicket
     */
    public int getIdTicket( )
    {
        return _nIdTicket;
    }

    /**
     * @param nIdTicket
     *            the nIdTicket to set
     */
    public void setIdTicket( int nIdTicket )
    {
        _nIdTicket = nIdTicket;
    }

    /**
     * Check if this filter contains a idTicket
     *
     * @return true if the filter contain an id of ticket
     */
    public boolean containsIdTicket( )
    {
        return ( _nIdTicket != CONSTANT_ID_NULL );
    }

    /**
     * @return the _dateLastUpdateDate
     */
    public Date getLastUpdateDate( )
    {
        return _dateLastUpdateDate;
    }

    /**
     * @param strLastUpdateDate
     *            the strLastUpdateDate to set
     */
    public void setLastUpdateDate( Date strLastUpdateDate )
    {
        _dateLastUpdateDate = strLastUpdateDate;
    }

    /**
     * Check if this filter contains a _dateLastUpdateDate
     *
     * @return true if the filter contain _dateLastUpdateDate
     */
    public boolean containsLastUpdateDate( )
    {
        return _dateLastUpdateDate != null;
    }

    /**
     * @return the _dateLastUpdateStartDate
     */
    public Date getLastUpdateStartDate( )
    {
        return _dateLastUpdateStartDate;
    }

    /**
     * @param strLastUpdateStartDate
     *            the strLastUpdateStartDate to set
     */
    public void setLastUpdateStartDate( Date strLastUpdateStartDate )
    {
        _dateLastUpdateStartDate = strLastUpdateStartDate;
    }

    /**
     * Check if this filter contains a _dateLastUpdateStartDate
     *
     * @return true if the filter contains _dateLastUpdateStartDate
     */
    public boolean containsLastUpdateStartDate( )
    {
        return _dateLastUpdateStartDate != null;
    }

    /**
     * @return the _dateLastUpdateEndDate
     */
    public Date getLastUpdateEndDate( )
    {
        return _dateLastUpdateEndDate;
    }

    /**
     * @param strLastUpdateEndDate
     *            the strLastUpdateEndDate to set
     */
    public void setLastUpdateEndDate( Date strLastUpdateEndDate )
    {
        _dateLastUpdateEndDate = strLastUpdateEndDate;
    }

    /**
     * Check if this filter contains a _dateLastUpdateEndDate
     *
     * @return true if the filter contains _dateLastUpdateEndDate
     */
    public boolean containsLastUpdateEndDate( )
    {
        return _dateLastUpdateEndDate != null;
    }

    /**
     * @return the _strCloseDate
     */
    public Date getCloseDate( )
    {
        return _dateCloseDate;
    }

    /**
     * @param strCloseDate
     *            the strCloseDate to set
     */
    public void setCloseDate( Date strCloseDate )
    {
        _dateCloseDate = strCloseDate;
    }

    /**
     * Check if this filter contains a _dateCloseDate
     *
     * @return true if the filter contain _dateCloseDate
     */
    public boolean containsCloseDate( )
    {
        return _dateCloseDate != null;
    }

    /**
     * @return the _strCreationDate
     */
    public Date getCreationDate( )
    {
        return _dateCreationDate;
    }

    /**
     * @param strCreationDate
     *            the strCreationDate to set
     */
    public void setCreationDate( Date strCreationDate )
    {
        _dateCreationDate = strCreationDate;
    }

    /**
     * Check if this filter contains a _dateCreationDate
     *
     * @return true if the filter contain _dateCreationDate
     */
    public boolean containsCreationDate( )
    {
        return _dateCreationDate != null;
    }

    /**
     * @return the _dateCreationStartDate
     */
    public Date getCreationStartDate( )
    {
        return _dateCreationStartDate;
    }

    /**
     * @param strCreationStartDate
     *            the strCreationStartDate to set
     */
    public void setCreationStartDate( Date strCreationStartDate )
    {
        _dateCreationStartDate = strCreationStartDate;
    }

    /**
     * Check if this filter contains a _dateCreationStartDate
     *
     * @return true if the filter contains _dateCreationStartDate
     */
    public boolean containsCreationStartDate( )
    {
        return _dateCreationStartDate != null;
    }

    /**
     * @return the _dateCreationEndDate
     */
    public Date getCreationEndDate( )
    {
        return _dateCreationEndDate;
    }

    /**
     * @param strCreationEndDate
     *            the strCreationEndDate to set
     */
    public void setCreationEndDate( Date strCreationEndDate )
    {
        _dateCreationEndDate = strCreationEndDate;
    }

    /**
     * Check if this filter contains a _dateCreationEndDate
     *
     * @return true if the filter contains _dateCreationEndDate
     */
    public boolean containsCreationEndDate( )
    {
        return _dateCreationEndDate != null;
    }

    /**
     * @return the _strStatus
     */
    public String getStatus( )
    {
        return _strStatus;
    }

    /**
     * @param strStatus
     *            the strStatus to set
     */
    public void setStatus( String strStatus )
    {
        _strStatus = strStatus;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of status
     */
    public boolean containsStatus( )
    {
        return StringUtils.isNotEmpty( _strStatus );
    }

    /**
     * @return the _strEmail
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * @param strEmail
     *            the strEmail to set
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of Email
     */
    public boolean containsEmail( )
    {
        return StringUtils.isNotEmpty( _strEmail );
    }

    /**
     * @return the _strLastName
     */
    public String getLastName( )
    {
        return _strLastName;
    }

    /**
     * @param strLastName
     *            the strLastName to set
     */
    public void setLastName( String strLastName )
    {
        _strLastName = strLastName;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of LastName
     */
    public boolean containsLastName( )
    {
        return StringUtils.isNotEmpty( _strLastName );
    }

    /**
     * @return the _strFirstName
     */
    public String getFirstName( )
    {
        return _strFirstName;
    }

    /**
     * @param strFirstName
     *            the strFirstName to set
     */
    public void setFirstName( String strFirstName )
    {
        _strFirstName = strFirstName;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of FirstName
     */
    public boolean containsFirstName( )
    {
        return StringUtils.isNotEmpty( _strFirstName );
    }

    /**
     * @return the _strFixedPhoneNumber
     */
    public String getFixedPhoneNumber( )
    {
        return _strFixedPhoneNumber;
    }

    /**
     * @param strFixedPhoneNumber
     *            the strFixedPhoneNumber to set
     */
    public void setFixedPhoneNumber( String strFixedPhoneNumber )
    {
        _strFixedPhoneNumber = strFixedPhoneNumber;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of FixedPhoneNumber
     */
    public boolean containsFixedPhoneNumber( )
    {
        return StringUtils.isNotEmpty( _strFixedPhoneNumber );
    }

    /**
     * @return the _strMobilePhoneNumber
     */
    public String getMobilePhoneNumber( )
    {
        return _strMobilePhoneNumber;
    }

    /**
     * @param strMobilePhoneNumber
     *            the strMobilePhoneNumber to set
     */
    public void setMobilePhoneNumber( String strMobilePhoneNumber )
    {
        _strMobilePhoneNumber = strMobilePhoneNumber;
    }

    /**
     * Check if this filter contains a MobilePhoneNumber
     *
     * @return true if the filter contain an id of MobilePhoneNumber
     */
    public boolean containsMobilePhoneNumber( )
    {
        return StringUtils.isNotEmpty( _strMobilePhoneNumber );
    }

    /**
     * @return the _nIdChannel
     */
    public int getIdChannel( )
    {
        return _nIdChannel;
    }

    /**
     * @param nIdChannel
     *            the nIdChannel to set
     */
    public void setIdChannel( int nIdChannel )
    {
        _nIdChannel = nIdChannel;
    }

    /**
     * Check if this filter contains a ChannelID
     *
     * @return true if the filter contain an id of Channel
     */
    public boolean containsChannel( )
    {
        return ( _nIdChannel != CONSTANT_ID_NULL );
    }

    /**
     * @return the _strReference
     */
    public String getReference( )
    {
        return _strReference;
    }

    /**
     * @param strReference
     *            the reference to set
     */
    public void setReference( String strReference )
    {
        _strReference = strReference;
    }

    /**
     * Check if this filter contains a reference
     *
     * @return true if the filter contains a reference
     */
    public boolean containsReference( )
    {
        return StringUtils.isNotEmpty( _strReference );
    }

    /**
     * @return the _strNomenclature
     */
    public String getNomenclature( )
    {
        return _strNomenclature;
    }

    /**
     * @param strNomenclature
     *            the Nomenclature to set
     */
    public void setNomenclature( String strNomenclature )
    {
        _strNomenclature = strNomenclature;
    }

    /**
     * Check if this filter contains a Nomenclature
     *
     * @return true if the filter contains a Nomenclature
     */
    public boolean containsNomenclature( )
    {
        return StringUtils.isNotEmpty( _strNomenclature );
    }

    /**
     * @return the _strOpenSincePeriod
     */
    public int getOpenSincePeriod( )
    {
        return _nOpenSincePeriod;
    }

    /**
     * @param nOpenSincePeriod
     *            the OpenSincePeriod to set
     */
    public void setOpenSincePeriod( int nOpenSincePeriod )
    {
        _nOpenSincePeriod = nOpenSincePeriod;
    }

    /**
     * Check if this filter contains a OpenSincePeriod
     *
     * @return true if the filter contains a OpenSincePeriod
     */
    public boolean containsOpenSincePeriod( )
    {
        return _nOpenSincePeriod != CONSTANT_ID_NULL;
    }

    /**
     * @return the _nUrgency
     */
    public int getUrgency( )
    {
        return _nUrgency;
    }

    /**
     * @param nUrgency
     *            the Urgency to set
     */
    public void setUrgency( int nUrgency )
    {
        _nUrgency = nUrgency;
    }

    /**
     * Check if this filter contains a Urgency
     *
     * @return true if the filter contains a Urgency
     */
    public boolean containsUrgency( )
    {
        return _nUrgency != CONSTANT_ID_NULL;
    }

    /**
     * @return the _enumFilterView
     */
    public TicketFilterViewEnum getFilterView( )
    {
        return _enumFilterView;
    }

    /**
     * @param enumFilterView
     *            the enumFilterView to set
     */
    public void setFilterView( TicketFilterViewEnum enumFilterView )
    {
        _enumFilterView = enumFilterView;
    }

    /**
     * Check if this filter contains a TicketFilterViewEnum
     *
     * @return true if the filter contains a TicketFilterViewEnum
     */
    public boolean containsFilterView( )
    {
        return ( ( _enumFilterView != null ) && ( _enumFilterView != TicketFilterViewEnum.ALL ) );
    }

    /**
     * @return the nFilterIdAdminUser
     */
    public int getFilterIdAdminUser( )
    {
        return _nFilterIdAdminUser;
    }

    /**
     * @param nFilterIdAdminUser
     *            the nFilterIdAdminUser to set
     */
    public void setFilterIdAdminUser( int nFilterIdAdminUser )
    {
        _nFilterIdAdminUser = nFilterIdAdminUser;
    }

    /**
     * @return the setFilterIdUAssigneeUnit
     */
    public Set<Integer> getFilterIdAssigneeUnit( )
    {
        return _setFilterIdAssigneeUnit;
    }

    /**
     * @param setFilterIdAssigneeUnit
     *            the setFilterIdAssigneeUnit to set
     */
    public void setFilterIdAssigneeUnit( Set<Integer> setFilterIdAssigneeUnit )
    {
        _setFilterIdAssigneeUnit = setFilterIdAssigneeUnit;
    }

    /**
     * @return the setFilterIdUAssignerUnit
     */
    public Set<Integer> getFilterIdAssignerUnit( )
    {
        return _setFilterIdAssignerUnit;
    }

    /**
     * @param setFilterIdAssignerUnit
     *            the setFilterIdAssignerUnit to set
     */
    public void setFilterIdAssignerUnit( Set<Integer> setFilterIdAssignerUnit )
    {
        _setFilterIdAssignerUnit = setFilterIdAssignerUnit;
    }

    /**
     * @return the setAdminUserRoles
     */
    public Set<String> getAdminUserRoles( )
    {
        return _setAdminUserRoles;
    }

    /**
     * @param setAdminUserRoles
     *            the setAdminUserRoles to set
     */
    public void setAdminUserRoles( Set<String> setAdminUserRoles )
    {
        _setAdminUserRoles = setAdminUserRoles;
    }

    /**
     *
     * @return true if query limit is enabled
     */
    public boolean containsLimit( )
    {
        return ( ( _nTicketsLimitStart > -1 ) && ( _nTicketsLimitCount > 0 ) );
    }

    /**
     * @return the nTicketsLimitStart
     */
    public int getTicketsLimitStart( )
    {
        return _nTicketsLimitStart;
    }

    /**
     * @param nTicketsLimitStart
     *            the nTicketsLimitStart to set
     */
    public void setTicketsLimitStart( int nTicketsLimitStart )
    {
        _nTicketsLimitStart = nTicketsLimitStart;
    }

    /**
     * @return the nTicketsLimitCount
     */
    public int getTicketsLimitCount( )
    {
        return _nTicketsLimitCount;
    }

    /**
     * @param nTicketsLimitCount
     *            the nTicketsLimitCount to set
     */
    public void setTicketsLimitCount( int nTicketsLimitCount )
    {
        _nTicketsLimitCount = nTicketsLimitCount;
    }

    /**
     * @return the _mapCategoryId
     */
    public Map<Integer, Integer> getMapCategoryId( )
    {
        return _mapCategoryId;
    }

    /**
     * @param mapCategoryId
     *            the mapCategoryId to set
     */
    public void setMapCategoryId( Map<Integer, Integer> mapCategoryId )
    {
        _mapCategoryId = mapCategoryId;
    }

    /**
     * @return the _listMarkingsId
     */
    public Set<String> getMarkingsId( )
    {
        return _listMarkingsId;
    }

    /**
     * @param _listMarkingsId the _listMarkingsId to set
     */
    public void setMarkingsId( Set<String> _listMarkingsId )
    {
        this._listMarkingsId = _listMarkingsId;
    }

    @Override
    protected void initOrderNameToColumnNameMap( )
    {
        _mapOrderNameToColumnName = new HashMap<String, List<String>>( );
        _mapOrderNameToColumnName.put( "reference", Arrays.asList( "a.ticket_reference", "a.id_ticket" ) );
        _mapOrderNameToColumnName.put( "ticket_status", Arrays.asList( "a.ticket_status" ) );
        _mapOrderNameToColumnName.put( "date_create", Arrays.asList( "a.date_create" ) );
        _mapOrderNameToColumnName.put( "date_update", Arrays.asList( "a.date_update" ) );
        _mapOrderNameToColumnName.put( "id_user", Arrays.asList( "g.id_user" ) ); // Tri sur id_user utile ?
        _mapOrderNameToColumnName.put( "channel", Arrays.asList( "x.label" ) );
        _mapOrderNameToColumnName.put( "email", Arrays.asList( "a.email" ) );
        _mapOrderNameToColumnName.put( "lastname", Arrays.asList( "a.lastname", "a.firstname", "a.email" ) );
        _mapOrderNameToColumnName.put( "date_close", Arrays.asList( "a.date_close" ) );
        _mapOrderNameToColumnName.put( "ticket_status_text", Arrays.asList( "a.ticket_status_text" ) );
        _mapOrderNameToColumnName.put( "assignee", Arrays.asList( "h.label", "g.last_name" ) );
        _mapOrderNameToColumnName.put( "state", Arrays.asList( "j.name" ) );
        _mapOrderNameToColumnName.put( "nomenclature", Arrays.asList( "a.nomenclature" ) );
    }

    @Override
    public List<String> getDefaultOrderBySqlColumns( )
    {
        return _mapOrderNameToColumnName.get( CONSTANT_DEFAULT_ORDER_BY );
    }

    @Override
    public String getDefaultOrderSort( )
    {
        return CONSTANT_DEFAULT_ORDER_SORT;
    }
}
