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
package fr.paris.lutece.plugins.ticketing.business.ticket;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.rbac.RBACResource;

import org.apache.commons.lang.StringUtils;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import org.jsoup.Jsoup;

import java.io.Serializable;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 * This is the business class for the object Ticket
 */
public class Ticket implements Serializable, RBACResource
{
    public static final String TICKET_RESOURCE_TYPE = "ticket";
    private static final long serialVersionUID = 1L;
    private static final String SEPARATOR = " ";
    private static final String PHONE_NUMBER_REGEX = "(^$|[0-9]{10})";

    // Variables declarations 
    private int _nId;
    private String _strReference;
    private int _nIdUserTitle;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.UserTitle.size}" )
    private String _strUserTitle;
    @NotEmpty( message = "#i18n{ticketing.validation.ticket.Firstname.notEmpty}" )
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.Firstname.size}" )
    private String _strFirstname;
    @NotEmpty( message = "#i18n{ticketing.validation.ticket.Lastname.notEmpty}" )
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.Lastname.size}" )
    private String _strLastname;
    @NotEmpty( message = "#i18n{ticketing.validation.ticket.Email.notEmpty}" )
    @Email( message = "#i18n{ticketing.validation.ticket.Email.badFormat}" )
    @Size( max = 255, message = "#i18n{ticketing.validation.ticket.Email.size}" )
    private String _strEmail;
    @Pattern( regexp = PHONE_NUMBER_REGEX, message = "#i18n{ticketing.validation.ticket.FixedPhoneNumber.format}" )
    private String _strFixedPhoneNumber;
    @Pattern( regexp = PHONE_NUMBER_REGEX, message = "#i18n{ticketing.validation.ticket.MobilePhoneNumber.format}" )
    private String _strMobilePhoneNumber;
    @Min( value = 1, message = "#i18n{ticketing.validation.ticket.TicketType.mandatory}" )
    private int _nIdTicketType;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.TicketType.size}" )
    private String _strTicketType;
    @Min( value = 1, message = "#i18n{ticketing.validation.ticket.TicketDomain.mandatory}" )
    private int _nIdTicketDomain;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.TicketDomain.size}" )
    private String _strTicketDomain;
    @Min( value = 1, message = "#i18n{ticketing.validation.ticket.TicketCategory.mandatory}" )
    private int _nIdTicketCategory;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.TicketCategory.size}" )
    private String _strTicketCategory;
    private int _nIdContactMode;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.ContactMode.size}" )
    private String _strContactMode;
    private String _strTicketComment;
    private String _strConfirmationMsg;
    private String _strGuid;
    private int _nTicketStatus;
    @Size( max = 255, message = "#i18n{ticketing.validation.ticket.TicketStatusText.size}" )
    private String _strTicketStatusText;
    private transient State _state;
    private Timestamp _dDateCreate;
    private Timestamp _dDateUpdate;
    private Timestamp _dDateClose;
    private transient Collection<Action> _listWorkflowActions;
    private List<Response> _listResponse;
    private int _nCriticality;
    private int _nPriority;
    private String _strCustomerId;
    private transient AssigneeUser _user;
    private transient AssigneeUnit _unit;
    private transient AssigneeUser _assignerUser;
    private transient AssigneeUnit _assignerUnit;
    private String _strUserMessage;
    private String _strUrl;
    private int _nIdChannel;
    private String _strChannel;

    /**
     * Enriches empty ticket attributes with specified values
     * @param strIdUserTitle the user title id as String
     * @param strFirstname the first name
     * @param strLastname the last name
     * @param strFixedPhoneNumber the fixed phone number
     * @param strMobilePhoneNumber the mobile phone number
     * @param strEmail the email
     * @param strCategoryCode the categoryCode
     * @param strIdContactMode the contact mode id
     * @param strIdChannel the channel id
     * @param strComment the comment
     * @param strGuid the guid
     * @param strCustomerId the customer id
     */
    public void enrich( String strIdUserTitle, String strFirstname, String strLastname, String strFixedPhoneNumber,
        String strMobilePhoneNumber, String strEmail, String strCategoryCode, String strIdContactMode,
        String strIdChannel, String strComment, String strGuid, String strCustomerId )
    {
        if ( !StringUtils.isEmpty( strIdUserTitle ) )
        {
            setIdUserTitle( Integer.parseInt( strIdUserTitle ) );
        }

        if ( !StringUtils.isEmpty( strFirstname ) && StringUtils.isEmpty( getFirstname(  ) ) )
        {
            setFirstname( strFirstname );
        }

        if ( !StringUtils.isEmpty( strLastname ) && StringUtils.isEmpty( getLastname(  ) ) )
        {
            setLastname( strLastname );
        }

        if ( !StringUtils.isEmpty( strFixedPhoneNumber ) && StringUtils.isEmpty( getFixedPhoneNumber(  ) ) )
        {
            setFixedPhoneNumber( strFixedPhoneNumber );
        }

        if ( !StringUtils.isEmpty( strMobilePhoneNumber ) && StringUtils.isEmpty( getMobilePhoneNumber(  ) ) )
        {
            setMobilePhoneNumber( strMobilePhoneNumber );
        }

        if ( !StringUtils.isEmpty( strEmail ) && StringUtils.isEmpty( getEmail(  ) ) )
        {
            setEmail( strEmail );
        }

        if ( !StringUtils.isEmpty( strCategoryCode ) && ( getIdTicketCategory(  ) == 0 ) )
        {
            TicketCategory category = TicketCategoryHome.findByCode( strCategoryCode );

            if ( category != null )
            {
                setIdTicketCategory( category.getId(  ) );
                setIdTicketDomain( category.getIdTicketDomain(  ) );
                setIdTicketType( category.getIdTicketType(  ) );
            }
        }

        if ( !StringUtils.isEmpty( strIdContactMode ) )
        {
            setIdContactMode( Integer.parseInt( strIdContactMode ) );
        }

        if ( !StringUtils.isEmpty( strIdChannel ) )
        {
            setIdChannel( Integer.parseInt( strIdChannel ) );
        }

        if ( !StringUtils.isEmpty( strComment ) && StringUtils.isEmpty( getTicketComment(  ) ) )
        {
            setTicketComment( strComment );
        }

        if ( !StringUtils.isEmpty( strGuid ) && StringUtils.isEmpty( getGuid(  ) ) )
        {
            setGuid( strGuid );
        }

        if ( !StringUtils.isEmpty( strCustomerId ) && StringUtils.isEmpty( getCustomerId(  ) ) )
        {
            setCustomerId( strCustomerId );
        }
    }

    /**
     * Returns the Id
     * @return The Id
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the IdUserTitle
     * @return The IdUserTitle
     */
    public int getIdUserTitle(  )
    {
        return _nIdUserTitle;
    }

    /**
     * Sets the IdUserTitle
     * @param nIdUserTitle The IdUserTitle
     */
    public void setIdUserTitle( int nIdUserTitle )
    {
        _nIdUserTitle = nIdUserTitle;
    }

    /**
     * Returns the Reference
     * @return The Reference
     */
    public String getReference(  )
    {
        return _strReference;
    }

    /**
     * Sets the Reference
     * @param strReference The Reference
     */
    public void setReference( String strReference )
    {
        _strReference = strReference;
    }

    /**
     * Returns the UserTitle
     * @return The UserTitle
     */
    public String getUserTitle(  )
    {
        return _strUserTitle;
    }

    /**
     * Sets the UserTitle
     * @param strUserTitle The UserTitle
     */
    public void setUserTitle( String strUserTitle )
    {
        _strUserTitle = strUserTitle;
    }

    /**
     * Returns the Firstname
     * @return The Firstname
     */
    public String getFirstname(  )
    {
        return _strFirstname;
    }

    /**
     * Sets the Firstname
     * @param strFirstname The Firstname
     */
    public void setFirstname( String strFirstname )
    {
        _strFirstname = strFirstname;
    }

    /**
     * Returns the Lastname
     * @return The Lastname
     */
    public String getLastname(  )
    {
        return _strLastname;
    }

    /**
     * Sets the Lastname
     * @param strLastname The Lastname
     */
    public void setLastname( String strLastname )
    {
        _strLastname = strLastname;
    }

    /**
     * Returns the Email
     * @return The Email
     */
    public String getEmail(  )
    {
        return _strEmail;
    }

    /**
     * Sets the Email
     * @param strEmail The Email
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * Returns the FixedPhoneNumber
     *
     * @return The FixedPhoneNumber
     */
    public String getFixedPhoneNumber(  )
    {
        return _strFixedPhoneNumber;
    }

    /**
     * Sets the FixedPhoneNumber
     *
     * @param strFixedPhoneNumber
     *            The FixedPhoneNumber
     */
    public void setFixedPhoneNumber( String strFixedPhoneNumber )
    {
        _strFixedPhoneNumber = strFixedPhoneNumber;
    }

    /**
     * Returns the MobilePhoneNumber
     *
     * @return The MobilePhoneNumber
     */
    public String getMobilePhoneNumber(  )
    {
        return _strMobilePhoneNumber;
    }

    /**
     * Sets the MobilePhoneNumber
     *
     * @param strMobilePhoneNumber
     *            The MobilePhoneNumber
     */
    public void setMobilePhoneNumber( String strMobilePhoneNumber )
    {
        _strMobilePhoneNumber = strMobilePhoneNumber;
    }

    /**
     * Returns the IdTicketType
     * @return The IdTicketType
     */
    public int getIdTicketType(  )
    {
        return _nIdTicketType;
    }

    /**
     * Sets the IdTicketType
     * @param nIdTicketType The IdTicketType
     */
    public void setIdTicketType( int nIdTicketType )
    {
        _nIdTicketType = nIdTicketType;
    }

    /**
     * Returns the TicketType
     * @return The TicketType
     */
    public String getTicketType(  )
    {
        return _strTicketType;
    }

    /**
     * Sets the TicketType
     * @param strTicketType The TicketType
     */
    public void setTicketType( String strTicketType )
    {
        _strTicketType = strTicketType;
    }

    /**
     * Returns the IdTicketDomain
     * @return The IdTicketDomain
     */
    public int getIdTicketDomain(  )
    {
        return _nIdTicketDomain;
    }

    /**
     * Sets the IdTicketDomain
     * @param nIdTicketDomain The IdTicketDomain
     */
    public void setIdTicketDomain( int nIdTicketDomain )
    {
        _nIdTicketDomain = nIdTicketDomain;
    }

    /**
     * Returns the TicketDomain
     * @return The TicketDomain
     */
    public String getTicketDomain(  )
    {
        return _strTicketDomain;
    }

    /**
     * Sets the TicketDomain
     * @param strTicketDomain The TicketDomain
     */
    public void setTicketDomain( String strTicketDomain )
    {
        _strTicketDomain = strTicketDomain;
    }

    /**
     * Returns the IdTicketCategory
     * @return The IdTicketCategory
     */
    public int getIdTicketCategory(  )
    {
        return _nIdTicketCategory;
    }

    /**
     * Sets the IdTicketCategory
     * @param nIdTicketCategory The IdTicketCategory
     */
    public void setIdTicketCategory( int nIdTicketCategory )
    {
        _nIdTicketCategory = nIdTicketCategory;
    }

    /**
     * Returns the TicketCategory
     * @return The TicketCategory
     */
    public String getTicketCategory(  )
    {
        return _strTicketCategory;
    }

    /**
     * Sets the TicketCategory
     * @param strTicketCategory The TicketCategory
     */
    public void setTicketCategory( String strTicketCategory )
    {
        _strTicketCategory = strTicketCategory;
    }

    /**
     * Returns the IdContactMode
     *
     * @return The IdContactMode
     */
    public int getIdContactMode(  )
    {
        return _nIdContactMode;
    }

    /**
     * Sets the IdContactMode
     *
     * @param nIdContactMode
     *            The IdContactMode
     */
    public void setIdContactMode( int nIdContactMode )
    {
        _nIdContactMode = nIdContactMode;
    }

    /**
     * Returns the ContactMode
     *
     * @return The ContactMode
     */
    public String getContactMode(  )
    {
        return _strContactMode;
    }

    /**
     * Sets the ContactMode
     *
     * @param strContactMode
     *            The ContactMode
     */
    public void setContactMode( String strContactMode )
    {
        _strContactMode = strContactMode;
    }

    /**
     * Returns the TicketComment
     *
     * @return The TicketComment
     */
    public String getTicketComment(  )
    {
        return _strTicketComment;
    }

    /**
     * Sets the TicketComment
     *
     * @param strTicketComment
     *            The TicketComment
     */
    public void setTicketComment( String strTicketComment )
    {
        _strTicketComment = strTicketComment;
    }

    /**
     * Returns the ConfirmationMsg
     *
     * @return The ConfirmationMsg
     */
    public String getConfirmationMsg(  )
    {
        return _strConfirmationMsg;
    }

    /**
     * Gets the create date
     * @return the create date
     */
    public Timestamp getDateCreate(  )
    {
        return _dDateCreate;
    }

    /**
     * Sets the create date
     * @param dDateCreate the create date
     */
    public void setDateCreate( Timestamp dDateCreate )
    {
        _dDateCreate = dDateCreate;
    }

    /**
     * Gets the update date
     * @return the update date
     */
    public Timestamp getDateUpdate(  )
    {
        return _dDateUpdate;
    }

    /**
     * Sets the update date
     * @param dDateUpdate the update date
     */
    public void setDateUpdate( Timestamp dDateUpdate )
    {
        _dDateUpdate = dDateUpdate;
    }

    /**
     * Gets the close date
     * @return the close date
     */
    public Timestamp getDateClose(  )
    {
        return _dDateClose;
    }

    /**
     * Sets the close date
     * @param dDateClose The close date
     */
    public void setDateClose( Timestamp dDateClose )
    {
        _dDateClose = dDateClose;
    }

    /**
     * Sets the ConfirmationMsg
     *
     * @param strConfirmationMsg
     *            The ConfirmationMsg
     */
    public void setConfirmationMsg( String strConfirmationMsg )
    {
        _strConfirmationMsg = strConfirmationMsg;
    }

    /**
     * Returns the TicketStatus
     *
     * @return The TicketStatus
     */
    public int getTicketStatus(  )
    {
        return _nTicketStatus;
    }

    /**
     * Sets the TicketStatus
     * @param nTicketStatus The TicketStatus
     */
    public void setTicketStatus( int nTicketStatus )
    {
        _nTicketStatus = nTicketStatus;
    }

    /**
     * Returns the TicketStatusText
     * @return The TicketStatusText
     */
    public String getTicketStatusText(  )
    {
        return _strTicketStatusText;
    }

    /**
     * Sets the TicketStatusText
     * @param strTicketStatusText The TicketStatusText
     */
    public void setTicketStatusText( String strTicketStatusText )
    {
        _strTicketStatusText = strTicketStatusText;
    }

    /**
     * Returns the State
     * @return The State
     */
    public State getState(  )
    {
        return _state;
    }

    /**
     * Sets the State
     * @param state The state
     */
    public void setState( State state )
    {
        _state = state;
    }

    /**
     * Get the list of workflow actions available for this ticket. Workflow
     * actions are NOT loaded by default, so check that they have been set
     * before calling this method.
     * @return The list of workflow actions available for this ticket.
     */
    public Collection<Action> getListWorkflowActions(  )
    {
        return _listWorkflowActions;
    }

    /**
     * Set the list of workflow actions available for this ticket.
     * @param listWorkflowActions The list of workflow actions available for
     *            this
     *            ticket.
     */
    public void setListWorkflowActions( Collection<Action> listWorkflowActions )
    {
        this._listWorkflowActions = listWorkflowActions;
    }

    /**
     * Get the list of response of this ticket
     * @return the list of response of this ticket
     */
    public List<Response> getListResponse(  )
    {
        return _listResponse;
    }

    /**
     * Set the list of responses of this ticket
     * @param listResponse The list of responses
     */
    public void setListResponse( List<Response> listResponse )
    {
        _listResponse = listResponse;
    }

    /**
     *
     * @return the _strGuid
     */
    public String getGuid(  )
    {
        return _strGuid;
    }

    /**
     * Sets the GUID
     * @param strGuid the Guid to set
     */
    public void setGuid( String strGuid )
    {
        _strGuid = strGuid;
    }

    /**
     * Returns the Criticality
     *
     * @return The Criticality
     */
    public int getCriticality(  )
    {
        return _nCriticality;
    }

    /**
     * Sets the Criticality
     * @param nCriticality The Criticality
     */
    public void setCriticality( int nCriticality )
    {
        _nCriticality = nCriticality;
    }

    /**
     * Returns the Priority
     *
     * @return The Priority
     */
    public int getPriority(  )
    {
        return _nPriority;
    }

    /**
     * Sets the Priority
     * @param nPriority The Priority
     */
    public void setPriority( int nPriority )
    {
        _nPriority = nPriority;
    }

    /**
     * Returns the CustomerId
     *
     * @return The CustomerId
     */
    public String getCustomerId(  )
    {
        return _strCustomerId;
    }

    /**
     * Sets the CustomerId
     *
     * @param strCustomerId
     *            The CustomerId
     */
    public void setCustomerId( String strCustomerId )
    {
        _strCustomerId = strCustomerId;
    }

    /**
    * Returns the AssigneeUser
    * @return The AssigneeUser
    */
    public AssigneeUser getAssigneeUser(  )
    {
        return _user;
    }

    /**
     * Sets the AssigneeUser
     * @param assigneeUser The AssigneeUser
     */
    public void setAssigneeUser( AssigneeUser assigneeUser )
    {
        _user = assigneeUser;
    }

    /**
     * Returns the AssigneeUnit
     * @return The AssigneeUnit
     */
    public AssigneeUnit getAssigneeUnit(  )
    {
        return _unit;
    }

    /**
     * Sets the assigneeUnit
     * @param assigneeUnit The assigneeUnit
     */
    public void setAssigneeUnit( AssigneeUnit assigneeUnit )
    {
        _unit = assigneeUnit;
    }

    /**
    * Returns the user from assign up
    * @return The user from assign up
    */
    public AssigneeUser getAssignerUser(  )
    {
        return _assignerUser;
    }

    /**
     * Sets the user from assign up
     * @param userFromAssignUp The user from assign up
     */
    public void setAssignerUser( AssigneeUser assignerUser )
    {
        _assignerUser = assignerUser;
    }

    /**
     * Returns the unit from assign up
     * @return The unit from assign up
     */
    public AssigneeUnit getAssignerUnit(  )
    {
        return _assignerUnit;
    }

    /**
     * Sets the unit from assign up
     * @param assignerUnit The unit from assign up
     */
    public void setAssignerUnit( AssigneeUnit assignerUnit )
    {
        _assignerUnit = assignerUnit;
    }

    @Override
    public String getResourceId(  )
    {
        return String.valueOf( _nId );
    }

    @Override
    public String getResourceTypeCode(  )
    {
        return TICKET_RESOURCE_TYPE;
    }

    /**
     * Returns the user message
     *
     * @return The user message
     */
    public String getUserMessage(  )
    {
        return _strUserMessage;
    }

    /**
     * Sets the user message
     *
     * @param strUserMessage
     *            The user message
     */
    public void setUserMessage( String strUserMessage )
    {
        _strUserMessage = strUserMessage;
    }

    /**
     * Returns the url
     *
     * @return The url
     */
    public String getUrl(  )
    {
        return _strUrl;
    }

    /**
     * Sets the url
     *
     * @param strUrl
     *            The url
     */
    public void setUrl( String strUrl )
    {
        _strUrl = strUrl;
    }

    /**
     * Returns the IdChannel
     * @return The IdChannel
     */
    public int getIdChannel(  )
    {
        return _nIdChannel;
    }

    /**
     * Sets the IdChannel
     * @param nIdChannel The IdChannel
     */
    public void setIdChannel( int nIdChannel )
    {
        _nIdChannel = nIdChannel;
    }

    /**
     * Returns the Channel
     *
     * @return The Channel
     */
    public String getChannel(  )
    {
        return _strChannel;
    }

    /**
     * Sets the Channel
     *
     * @param strChannel
     *            The Channel
     */
    public void setChannel( String strChannel )
    {
        _strChannel = strChannel;
    }

    /**
     * Returns urgency, it s computed from max of criticty and priority value
     * @return urgency
     */
    public int getUrgency(  )
    {
        return ( _nCriticality >= _nPriority ) ? _nCriticality : _nPriority;
    }

    /**
     * returns a string with content fields to be indexed
     * @return string with content fields to be indexed
     */
    public String getContentForIndexer(  )
    {
        StringBuilder sb = new StringBuilder(  );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy hh:mm" );
        sb.append( _nId ).append( SEPARATOR );

        if ( StringUtils.isNotEmpty( _strReference ) )
        {
            sb.append( _strReference ).append( SEPARATOR );
        }

        if ( _nIdUserTitle > 0 )
        {
            sb.append( _nIdUserTitle ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strUserTitle ) )
        {
            sb.append( _strUserTitle ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strFirstname ) )
        {
            sb.append( _strFirstname ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strLastname ) )
        {
            sb.append( _strLastname ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strEmail ) )
        {
            sb.append( _strEmail ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strFixedPhoneNumber ) )
        {
            sb.append( _strFixedPhoneNumber ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strMobilePhoneNumber ) )
        {
            sb.append( _strMobilePhoneNumber ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketType ) )
        {
            sb.append( _strTicketType ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketDomain ) )
        {
            sb.append( _strTicketDomain ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketCategory ) )
        {
            sb.append( _strTicketCategory ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strContactMode ) )
        {
            sb.append( _strContactMode ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketComment ) )
        {
            sb.append( _strTicketComment ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strConfirmationMsg ) )
        {
            sb.append( _strConfirmationMsg ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strGuid ) )
        {
            sb.append( _strGuid ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketStatusText ) )
        {
            sb.append( _strTicketStatusText ).append( SEPARATOR );
        }

        if ( _state != null )
        {
            sb.append( _state.getDescription(  ) ).append( SEPARATOR );
        }

        if ( _dDateCreate != null )
        {
            sb.append( simpleDateFormat.format( _dDateCreate.getTime(  ) ) ).append( SEPARATOR );
        }

        if ( _dDateUpdate != null )
        {
            sb.append( simpleDateFormat.format( _dDateUpdate.getTime(  ) ) ).append( SEPARATOR );
        }

        if ( _dDateClose != null )
        {
            sb.append( simpleDateFormat.format( _dDateClose.getTime(  ) ) ).append( SEPARATOR );
        }

        if ( _listResponse != null )
        {
            for ( Response response : _listResponse )
            { //TODO

                if ( response.getResponseValue(  ) != null )
                {
                    sb.append( response.getResponseValue(  ) ).append( SEPARATOR );
                }
            }
        }

        if ( _user != null )
        {
            sb.append( _user.getFirstname(  ) ).append( SEPARATOR ).append( _user.getLastname(  ) ).append( SEPARATOR );
        }

        if ( _unit != null )
        {
            sb.append( _unit.getName(  ) ).append( SEPARATOR );
        }

        if ( _strUserMessage != null )
        {
            sb.append( Jsoup.parse( _strUserMessage ).text(  ) ).append( SEPARATOR );
        }

        if ( _nCriticality > 0 )
        {
            sb.append( TicketCriticality.valueOf( _nCriticality ).toString(  ) ).append( SEPARATOR );
        }

        if ( _nPriority > 0 )
        {
            sb.append( TicketCriticality.valueOf( _nPriority ).toString(  ) ).append( SEPARATOR );
        }

        return sb.toString(  );
    }

    /**
     * @return summary of ticket
     * returns summary of ticket to be displayed
     */
    public String getDisplaySummary(  )
    {
        StringBuilder sb = new StringBuilder(  );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy hh:mm" );

        if ( _dDateCreate != null )
        {
            sb.append( simpleDateFormat.format( _dDateCreate.getTime(  ) ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strReference ) )
        {
            sb.append( _strReference ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strUserTitle ) )
        {
            sb.append( _strUserTitle ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strFirstname ) )
        {
            sb.append( _strFirstname ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strLastname ) )
        {
            sb.append( _strLastname ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketType ) )
        {
            sb.append( _strTicketType ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketDomain ) )
        {
            sb.append( _strTicketDomain ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketCategory ) )
        {
            sb.append( _strTicketCategory ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketComment ) )
        {
            sb.append( _strTicketComment ).append( SEPARATOR );
        }

        return sb.toString(  );
    }

    /**
     * @return title of ticket
     * returns title of ticket to be displayed
     */
    public String getDisplayTitle(  )
    {
        StringBuilder sb = new StringBuilder(  );

        if ( StringUtils.isNotEmpty( _strReference ) )
        {
            sb.append( _strReference ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strFirstname ) )
        {
            sb.append( _strFirstname ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strLastname ) )
        {
            sb.append( _strLastname ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( _strTicketCategory ) )
        {
            sb.append( _strTicketCategory ).append( SEPARATOR );
        }

        return sb.toString(  );
    }

    /**
     * {@inheritDoc}
     */
    public String toString(  )
    {
        StringBuilder sb = new StringBuilder(  );
        sb.append( _nId );

        if ( StringUtils.isNotEmpty( _strReference ) )
        {
            sb.append( SEPARATOR ).append( _strReference );
        }

        return sb.toString(  );
    }
}