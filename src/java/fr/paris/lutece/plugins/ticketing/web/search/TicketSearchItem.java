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
package fr.paris.lutece.plugins.ticketing.web.search;

import fr.paris.lutece.portal.service.search.SearchItem;

import org.apache.commons.lang.BooleanUtils;
import org.apache.lucene.document.Document;

/**
 * Ticket Search Item
 */
public class TicketSearchItem extends SearchItem
{
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRECISION = "precision";
    public static final String FIELD_DOMAIN = "domain";
    public static final String FIELD_REFERENCE = "reference";
    public static final String FIELD_TICKET_ID = "ticket_id";
    public static final String FIELD_DATE_CREATION = "ticket_creation_date";
    public static final String FIELD_RESPONSE = "ticket_response";
    public static final String FIELD_TXT_RESPONSE = "ticket_txt_response";
    public static final String FIELD_COMMENT = "ticket_comment";
    public static final String FIELD_TXT_COMMENT = "ticket_txt_comment";
    public static final String FIELD_TICKET_NOMENCLATURE = "ticket_nomenclature";
    public static final String FIELD_PRIORITY = "ticket_priority";
    public static final String FIELD_CRITICALITY = "ticket_criticality";
    public static final String FIELD_ID_STATUS = "ticket_id_status";
    public static final String FIELD_TICKET_TYPE = "ticket_type";
    public static final String FIELD_USER_TITLE = "user_title";
    public static final String FIELD_FIRSTNAME = "firstname";
    public static final String FIELD_LASTNAME = "lastname";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_MOBILE_PHONE_NUMBER = "mobile_phone_number";
    public static final String FIELD_FIXED_PHONE_NUMBER = "fixed_phone_number";
    public static final String FIELD_STATE = "ticket_state";
    public static final String FIELD_CHANNEL_ICONFONT = "ticket_channel_iconfont";
    public static final String FIELD_CHANNEL_LABEL = "ticket_channel_label";
    public static final String FIELD_ASSIGNEE_UNIT_NAME = "ticket_assignee_unit_name";
    public static final String FIELD_ASSIGNEE_USER_ADMIN_ID = "ticket_assignee_user_admin_id";
    public static final String FIELD_ASSIGNEE_USER_FIRSTNAME = "ticket_assignee_user_firstname";
    public static final String FIELD_ASSIGNEE_USER_LASTNAME = "ticket_assignee_user_lastname";
    public static final String FIELD_TICKET_READ = "ticket_read";

    // Variables declarations
    private String _strReference;
    private int _nTicketId;
    private String _strCategory;
    private String _strPrecision;
    private String _strResponse;
    private String _strComment;
    private String _strDomain;
    private String _strNomenclature;
    private long _lDateCreation;
    private int _nCriticality;
    private int _nPriority;
    private int _nTicketstatusId;
    private String _strTicketTypeLabel;
    private String _strUserTitle;
    private String _strFirstName;
    private String _strLastName;
    private String _strEmail;
    private String _strMobilePhoneNumber;
    private String _strFixedPhonenumber;
    private String _strStateName;
    private String _strChannelIconFont;
    private String _strChannelLabel;
    private String _strAssigneeUnitName;
    private int _nAssigneeUserAdminId;
    private String _strAssigneUserFirstName;
    private String _strAssigneUserLastName;
    private boolean _bRead;

    /**
     * Constructor
     *
     * @param document
     *            The document retrieved by the search
     */
    public TicketSearchItem( Document document )
    {
        super( document );
        _strReference = document.get( FIELD_REFERENCE );
        _nTicketId = document.getField( FIELD_TICKET_ID ).numericValue( ).intValue( );
        _strCategory = document.get( FIELD_CATEGORY );
        _strPrecision = document.get( FIELD_PRECISION );
        _strResponse = document.get( FIELD_RESPONSE );
        _strComment = document.get( FIELD_COMMENT );
        _strDomain = document.get( FIELD_DOMAIN );
        _strNomenclature = document.get( FIELD_TICKET_NOMENCLATURE );
        _lDateCreation = document.getField( FIELD_DATE_CREATION ).numericValue( ).longValue( );
        _nCriticality = document.getField( FIELD_CRITICALITY ).numericValue( ).intValue( );
        _nPriority = document.getField( FIELD_PRIORITY ).numericValue( ).intValue( );
        _nTicketstatusId = document.getField( FIELD_ID_STATUS ).numericValue( ).intValue( );
        _strTicketTypeLabel = document.get( FIELD_TICKET_TYPE );
        _strUserTitle = document.get( FIELD_USER_TITLE );
        _strFirstName = document.get( FIELD_FIRSTNAME );
        _strLastName = document.get( FIELD_LASTNAME );
        _strEmail = document.get( FIELD_EMAIL );
        _strMobilePhoneNumber = document.get( FIELD_MOBILE_PHONE_NUMBER );
        _strFixedPhonenumber = document.get( FIELD_FIXED_PHONE_NUMBER );
        _strStateName = document.get( FIELD_STATE );
        _strChannelIconFont = document.get( FIELD_CHANNEL_ICONFONT );
        _strChannelLabel = document.get( FIELD_CHANNEL_LABEL );
        _strAssigneeUnitName = document.get( FIELD_ASSIGNEE_UNIT_NAME );
        _nAssigneeUserAdminId = document.getField( FIELD_ASSIGNEE_USER_ADMIN_ID ).numericValue( ).intValue( );
        _strAssigneUserFirstName = document.get( FIELD_ASSIGNEE_USER_FIRSTNAME );
        _strAssigneUserLastName = document.get( FIELD_ASSIGNEE_USER_LASTNAME );
        _bRead = BooleanUtils.toBoolean( document.getField( FIELD_TICKET_READ ).numericValue( ).intValue( ) );
    }

    /**
     * @return the Reference
     */
    public String getReference( )
    {
        return _strReference;
    }

    /**
     * @param reference
     *            the reference to set
     */
    public void setReference( String reference )
    {
        this._strReference = reference;
    }

    /**
     * @return the TicketId
     */
    public int getTicketId( )
    {
        return _nTicketId;
    }

    /**
     * @param ticketId
     *            the ticketId to set
     */
    public void setTicketId( int ticketId )
    {
        this._nTicketId = ticketId;
    }

    /**
     * @return the Category
     */
    public String getCategory( )
    {
        return _strCategory;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory( String category )
    {
        this._strCategory = category;
    }

    /**
     * @return the Response
     */
    public String getResponse( )
    {
        return _strResponse;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse( String response )
    {
        this._strResponse = response;
    }

    /**
     * @return the Comment
     */
    public String getComment( )
    {
        return _strComment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment( String comment )
    {
        this._strComment = comment;
    }

    /**
     * @return the Domain
     */
    public String getDomain( )
    {
        return _strDomain;
    }

    /**
     * @param domain
     *            the domain to set
     */
    public void setDomain( String domain )
    {
        this._strDomain = domain;
    }

    /**
     * @return the _strNomenclature
     */
    public String getNomenclature( )
    {
        return _strNomenclature;
    }

    /**
     * @param nomenclature
     *            the nomenclature to set
     */
    public void setNomenclature( String nomenclature )
    {
        this._strNomenclature = nomenclature;
    }

    /**
     * @return the DateCreation
     */
    public long getDateCreation( )
    {
        return _lDateCreation;
    }

    /**
     * @param dateCreation
     *            the dateCreation to set
     */
    public void setDateCreation( long dateCreation )
    {
        this._lDateCreation = dateCreation;
    }

    /**
     * @return the Criticality
     */
    public int getCriticality( )
    {
        return _nCriticality;
    }

    /**
     * @param criticality
     *            the criticality to set
     */
    public void setCriticality( int criticality )
    {
        this._nCriticality = criticality;
    }

    /**
     * @return the Priority
     */
    public int getPriority( )
    {
        return _nPriority;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public void setPriority( int priority )
    {
        this._nPriority = priority;
    }

    /**
     * @return the TicketstatusId
     */
    public int getTicketstatusId( )
    {
        return _nTicketstatusId;
    }

    /**
     * @param ticketstatusId
     *            the ticketstatusId to set
     */
    public void setTicketstatusId( int ticketstatusId )
    {
        this._nTicketstatusId = ticketstatusId;
    }

    /**
     * @return the _strTicketTypeLabel
     */
    public String getTicketTypeLabel( )
    {
        return _strTicketTypeLabel;
    }

    /**
     * @param ticketTypeLabel
     *            the ticketTypeLabel to set
     */
    public void setTicketTypeLabel( String ticketTypeLabel )
    {
        this._strTicketTypeLabel = ticketTypeLabel;
    }

    /**
     * @return the UserTitle
     */
    public String getUserTitle( )
    {
        return _strUserTitle;
    }

    /**
     * @param userTitle
     *            the userTitle to set
     */
    public void setUserTitle( String userTitle )
    {
        this._strUserTitle = userTitle;
    }

    /**
     * @return the FirstName
     */
    public String getFirstName( )
    {
        return _strFirstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName( String firstName )
    {
        this._strFirstName = firstName;
    }

    /**
     * @return the LastName
     */
    public String getLastName( )
    {
        return _strLastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName( String lastName )
    {
        this._strLastName = lastName;
    }

    /**
     * @return the Email
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail( String email )
    {
        this._strEmail = email;
    }

    /**
     * @return the MobilePhoneNumber
     */
    public String getMobilePhoneNumber( )
    {
        return _strMobilePhoneNumber;
    }

    /**
     * @param mobilePhoneNumber
     *            the mobilePhoneNumber to set
     */
    public void setMobilePhoneNumber( String mobilePhoneNumber )
    {
        this._strMobilePhoneNumber = mobilePhoneNumber;
    }

    /**
     * @return the FixedPhonenumber
     */
    public String getFixedPhonenumber( )
    {
        return _strFixedPhonenumber;
    }

    /**
     * @param fixedPhonenumber
     *            the fixedPhonenumber to set
     */
    public void setFixedPhonenumber( String fixedPhonenumber )
    {
        this._strFixedPhonenumber = fixedPhonenumber;
    }

    /**
     * @return the StateName
     */
    public String getStateName( )
    {
        return _strStateName;
    }

    /**
     * @param stateName
     *            the stateName to set
     */
    public void setStateName( String stateName )
    {
        this._strStateName = stateName;
    }

    /**
     * @return the ChannelIconFont
     */
    public String getChannelIconFont( )
    {
        return _strChannelIconFont;
    }

    /**
     * @param channelIconFont
     *            the channelIconFont to set
     */
    public void setChannelIconFont( String channelIconFont )
    {
        this._strChannelIconFont = channelIconFont;
    }

    /**
     * @return the ChannelLabel
     */
    public String getChannelLabel( )
    {
        return _strChannelLabel;
    }

    /**
     * @param channelLabel
     *            the channelLabel to set
     */
    public void setChannelLabel( String channelLabel )
    {
        this._strChannelLabel = channelLabel;
    }

    /**
     * @return the AssigneeUnitName
     */
    public String getAssigneeUnitName( )
    {
        return _strAssigneeUnitName;
    }

    /**
     * @param assigneeUnitName
     *            the assigneeUnitName to set
     */
    public void setAssigneeUnitName( String assigneeUnitName )
    {
        this._strAssigneeUnitName = assigneeUnitName;
    }

    /**
     * @return the AssigneeUserAdminId
     */
    public int getAssigneeUserAdminId( )
    {
        return _nAssigneeUserAdminId;
    }

    /**
     * @param assigneeUserAdminId
     *            the assigneeUserAdminId to set
     */
    public void setAssigneeUserAdminId( int assigneeUserAdminId )
    {
        this._nAssigneeUserAdminId = assigneeUserAdminId;
    }

    /**
     * @return the AssigneUserFirstName
     */
    public String getAssigneUserFirstName( )
    {
        return _strAssigneUserFirstName;
    }

    /**
     * @param assigneUserFirstName
     *            the assigneUserFirstName to set
     */
    public void setAssigneUserFirstName( String assigneUserFirstName )
    {
        this._strAssigneUserFirstName = assigneUserFirstName;
    }

    /**
     * @return the AssigneUserLastName
     */
    public String getAssigneUserLastName( )
    {
        return _strAssigneUserLastName;
    }

    /**
     * @param assigneUserLastName
     *            the assigneUserLastName to set
     */
    public void setAssigneUserLastName( String assigneUserLastName )
    {
        this._strAssigneUserLastName = assigneUserLastName;
    }

    /**
     * @return the _strPrecision
     */
    public String getPrecision( )
    {
        return _strPrecision;
    }

    /**
     * @param strPrecision
     *            the strPrecision to set
     */
    public void setPrecision( String strPrecision )
    {
        this._strPrecision = strPrecision;
    }

    /**
     * @return the _bRead
     */
    public boolean isRead( )
    {
        return _bRead;
    }

    /**
     * @param read
     *            the _bRead to set
     */
    public void setRead( boolean read )
    {
        this._bRead = read;
    }
}
