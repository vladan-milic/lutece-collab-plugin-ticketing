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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.address.TicketAddress;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.marking.Marking;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * This is the business class for the object Ticket
 */
public class Ticket implements Serializable, RBACResource
{
    public static final String           TICKET_RESOURCE_TYPE = "ticket";
    private static final long            serialVersionUID     = 1L;
    private static final String          SEPARATOR            = " ";
    private static final String          PHONE_NUMBER_REGEX   = "(^$|[0-9]{10})";

    // Variables declarations
    private int                          _nId;
    private String                       _strReference;
    private int                          _nIdUserTitle;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.UserTitle.size}" )
    private String                       _strUserTitle;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.Firstname.size}" )
    private String                       _strFirstname;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.Lastname.size}" )
    private String                       _strLastname;
    @Email( message = "#i18n{ticketing.validation.ticket.Email.badFormat}" )
    @Size( max = 255, message = "#i18n{ticketing.validation.ticket.Email.size}" )
    private String                       _strEmail;
    @Pattern( regexp = PHONE_NUMBER_REGEX, message = "#i18n{ticketing.validation.ticket.FixedPhoneNumber.format}" )
    private String                       _strFixedPhoneNumber;
    @Pattern( regexp = PHONE_NUMBER_REGEX, message = "#i18n{ticketing.validation.ticket.MobilePhoneNumber.format}" )
    private String                       _strMobilePhoneNumber;
    private TicketCategory               _ticketCategory;
    private int                          _nIdContactMode;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.ContactMode.size}" )
    private String                       _strContactMode;
    private TicketAddress                _ticketAddress;
    private String                       _strTicketComment;
    private String                       _strConfirmationMsg;
    private String                       _strGuid;
    private int                          _nTicketStatus;
    @Size( max = 255, message = "#i18n{ticketing.validation.ticket.TicketStatusText.size}" )
    private String                       _strTicketStatusText;
    private transient State              _state;
    private Timestamp                    _dDateCreate;
    private Timestamp                    _dDateUpdate;
    private Timestamp                    _dDateClose;
    private transient Collection<Action> _listWorkflowActions;
    private List<Response>               _listResponse;
    private int                          _nCriticality;
    private int                          _nPriority;
    private String                       _strCustomerId;
    private transient AssigneeUser       _user;
    private transient AssigneeUnit       _unit;
    private transient AssigneeUser       _assignerUser;
    private transient AssigneeUnit       _assignerUnit;
    private String                       _strUserMessage;
    private String                       _strUrl;
    private Channel                      _channel             = new Channel( );
    private String                       _strNomenclature;
    private int                          _nIdticketMarking    = -1;
    private int 						 _nIdDemand = -1;

    /**
     * Enriches empty ticket attributes with specified values
     * 
     * @param strIdUserTitle
     *            the user title id as String
     * @param strFirstname
     *            the first name
     * @param strLastname
     *            the last name
     * @param strFixedPhoneNumber
     *            the fixed phone number
     * @param strMobilePhoneNumber
     *            the mobile phone number
     * @param strEmail
     *            the email
     * @param strCategoryCode
     *            the categoryCode
     * @param strIdContactMode
     *            the contact mode id
     * @param strIdChannel
     *            the channel id
     * @param strComment
     *            the comment
     * @param strGuid
     *            the guid
     * @param strCustomerId
     *            the customer id
     */
    public void enrich( String strIdUserTitle, String strFirstname, String strLastname, String strFixedPhoneNumber, String strMobilePhoneNumber, String strEmail, String strCategoryCode,
            String strIdContactMode, String strIdChannel, String strComment, String strGuid, String strCustomerId, String strNomenclature )
    {
        if ( !StringUtils.isEmpty( strIdUserTitle ) )
        {
            setIdUserTitle( Integer.parseInt( strIdUserTitle ) );
        }

        if ( !StringUtils.isEmpty( strFirstname ) && StringUtils.isEmpty( getFirstname( ) ) )
        {
            setFirstname( strFirstname );
        }

        if ( !StringUtils.isEmpty( strLastname ) && StringUtils.isEmpty( getLastname( ) ) )
        {
            setLastname( strLastname );
        }

        if ( !StringUtils.isEmpty( strFixedPhoneNumber ) && StringUtils.isEmpty( getFixedPhoneNumber( ) ) )
        {
            setFixedPhoneNumber( strFixedPhoneNumber );
        }

        if ( !StringUtils.isEmpty( strMobilePhoneNumber ) && StringUtils.isEmpty( getMobilePhoneNumber( ) ) )
        {
            setMobilePhoneNumber( strMobilePhoneNumber );
        }

        if ( !StringUtils.isEmpty( strEmail ) && StringUtils.isEmpty( getEmail( ) ) )
        {
            setEmail( strEmail );
        }

        if ( !StringUtils.isEmpty( strCategoryCode ) && ( ( _ticketCategory == null ) || ( _ticketCategory.getId( ) == 0 ) ) )
        {
            TicketCategory category = TicketCategoryHome.findByCode( strCategoryCode );

            if ( category != null )
            {
                setTicketCategory( category );
            }
        }

        if ( !StringUtils.isEmpty( strIdContactMode ) )
        {
            setIdContactMode( Integer.parseInt( strIdContactMode ) );
        }

        if ( !StringUtils.isEmpty( strIdChannel ) )
        {
            try
            {
                setChannel( ChannelHome.findByPrimaryKey( Integer.parseInt( strIdChannel ) ) );
            } catch ( NumberFormatException e )
            {
            }
        }

        if ( !StringUtils.isEmpty( strNomenclature ) )
        {
            setNomenclature( strNomenclature );
        }

        if ( !StringUtils.isEmpty( strComment ) && StringUtils.isEmpty( getTicketComment( ) ) )
        {
            setTicketComment( strComment );
        }

        if ( !StringUtils.isEmpty( strGuid ) && StringUtils.isEmpty( getGuid( ) ) )
        {
            setGuid( strGuid );
        }

        if ( !StringUtils.isEmpty( strCustomerId ) && StringUtils.isEmpty( getCustomerId( ) ) )
        {
            setCustomerId( strCustomerId );
        }
    }

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the IdUserTitle
     * 
     * @return The IdUserTitle
     */
    public int getIdUserTitle( )
    {
        return _nIdUserTitle;
    }

    /**
     * Sets the IdUserTitle
     * 
     * @param nIdUserTitle
     *            The IdUserTitle
     */
    public void setIdUserTitle( int nIdUserTitle )
    {
        _nIdUserTitle = nIdUserTitle;
    }

    /**
     * Returns the Reference
     * 
     * @return The Reference
     */
    public String getReference( )
    {
        return _strReference;
    }

    /**
     * Sets the Reference
     * 
     * @param strReference
     *            The Reference
     */
    public void setReference( String strReference )
    {
        _strReference = strReference;
    }

    /**
     * Returns the UserTitle
     * 
     * @return The UserTitle
     */
    public String getUserTitle( )
    {
        return _strUserTitle;
    }

    /**
     * Sets the UserTitle
     * 
     * @param strUserTitle
     *            The UserTitle
     */
    public void setUserTitle( String strUserTitle )
    {
        _strUserTitle = strUserTitle;
    }

    /**
     * Returns the Firstname
     * 
     * @return The Firstname
     */
    public String getFirstname( )
    {
        return _strFirstname;
    }

    /**
     * Sets the Firstname
     * 
     * @param strFirstname
     *            The Firstname
     */
    public void setFirstname( String strFirstname )
    {
        _strFirstname = strFirstname;
    }

    /**
     * Returns the Lastname
     * 
     * @return The Lastname
     */
    public String getLastname( )
    {
        return _strLastname;
    }

    /**
     * Sets the Lastname
     * 
     * @param strLastname
     *            The Lastname
     */
    public void setLastname( String strLastname )
    {
        _strLastname = strLastname;
    }

    /**
     * Returns the Email
     * 
     * @return The Email
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * Sets the Email
     * 
     * @param strEmail
     *            The Email
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
    public String getFixedPhoneNumber( )
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
    public String getMobilePhoneNumber( )
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
     * Returns the TicketType
     * 
     * @return The TicketType
     */
    public TicketCategory getTicketType( )
    {
        return TicketCategoryService.getInstance( true ).getType( _ticketCategory );
    }

    /**
     * Returns the TicketDomain
     * 
     * @return The TicketDomain
     */
    public TicketCategory getTicketDomain( )
    {
        return TicketCategoryService.getInstance( true ).getDomain( _ticketCategory );
    }

    /**
     * Returns the TicketCategory
     * 
     * @return The TicketCategory
     */
    public TicketCategory getTicketCategory( )
    {
        return _ticketCategory;
    }

    /**
     * Returns the TicketThematic
     * 
     * @return The TicketThematic
     */
    public TicketCategory getTicketThematic( )
    {
        return TicketCategoryService.getInstance( true ).getThematic( _ticketCategory );
    }

    /**
     * Get a JSON Object of the branch
     * 
     * @return the JSON Object of the branch
     */
    public String getBranchJSONObject( )
    {
        JSONArray jsonBranchCategories = new JSONArray( );

        for ( TicketCategory ticketCategory : TicketCategoryService.getInstance( true ).getCategoriesTree( ).getBranch( _ticketCategory ) )
        {
            JSONObject jsonTicketCategory = new JSONObject( );
            jsonTicketCategory.accumulate( FormatConstants.KEY_ID, ticketCategory.getId( ) );
            jsonTicketCategory.accumulate( FormatConstants.KEY_DEPTH_NUMBER, ticketCategory.getDepth( ).getDepthNumber( ) );
            jsonTicketCategory.accumulate( FormatConstants.KEY_INACTIVE, ticketCategory.isInactive( ) );
            jsonBranchCategories.add( jsonTicketCategory );
        }

        return jsonBranchCategories.toString( );
    }

    /**
     * Get the branch of the ticketCategory
     * 
     * @return the TicketCategory list corresponding to the branch
     */
    public List<TicketCategory> getBranch( )
    {
        return TicketCategoryService.getInstance( true ).getCategoriesTree( ).getBranch( _ticketCategory );
    }

    public TicketCategory getCategoryOfDepth( int depth )
    {
        if(_ticketCategory.getDepth().getDepthNumber() >= depth )
        {
            return _ticketCategory.getBranch( ).get( depth - 1 );
        }else
        {
            return new TicketCategory();
        }

    }

    /**
     * Sets the TicketCategory
     * 
     * @param ticketCategory
     *            The TicketCategory
     */
    public void setTicketCategory( TicketCategory ticketCategory )
    {
        _ticketCategory = ticketCategory;
    }

    /**
     * Returns the TicketCategory precision
     * 
     * @return The TicketCategory precision
     */
    public TicketCategory getTicketPrecision( )
    {
        return TicketCategoryService.getInstance( ).getPrecision( _ticketCategory );
    }

    /**
     * Returns the IdContactMode
     *
     * @return The IdContactMode
     */
    public int getIdContactMode( )
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
    public String getContactMode( )
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
    public String getTicketComment( )
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
    public String getConfirmationMsg( )
    {
        return _strConfirmationMsg;
    }

    /**
     * Gets the create date
     * 
     * @return the create date
     */
    public Timestamp getDateCreate( )
    {
        return _dDateCreate;
    }

    /**
     * Sets the create date
     * 
     * @param dDateCreate
     *            the create date
     */
    public void setDateCreate( Timestamp dDateCreate )
    {
        _dDateCreate = dDateCreate;
    }

    /**
     * Gets the update date
     * 
     * @return the update date
     */
    public Timestamp getDateUpdate( )
    {
        return _dDateUpdate;
    }

    /**
     * Sets the update date
     * 
     * @param dDateUpdate
     *            the update date
     */
    public void setDateUpdate( Timestamp dDateUpdate )
    {
        _dDateUpdate = dDateUpdate;
    }

    /**
     * Gets the close date
     * 
     * @return the close date
     */
    public Timestamp getDateClose( )
    {
        return _dDateClose;
    }

    /**
     * Sets the close date
     * 
     * @param dDateClose
     *            The close date
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
    public int getTicketStatus( )
    {
        return _nTicketStatus;
    }

    /**
     * Sets the TicketStatus
     * 
     * @param nTicketStatus
     *            The TicketStatus
     */
    public void setTicketStatus( int nTicketStatus )
    {
        _nTicketStatus = nTicketStatus;
    }

    /**
     * Returns the TicketStatusText
     * 
     * @return The TicketStatusText
     */
    public String getTicketStatusText( )
    {
        return _strTicketStatusText;
    }

    /**
     * Sets the TicketStatusText
     * 
     * @param strTicketStatusText
     *            The TicketStatusText
     */
    public void setTicketStatusText( String strTicketStatusText )
    {
        _strTicketStatusText = strTicketStatusText;
    }

    /**
     * Returns the State
     * 
     * @return The State
     */
    public State getState( )
    {
        return _state;
    }

    /**
     * Sets the State
     * 
     * @param state
     *            The state
     */
    public void setState( State state )
    {
        _state = state;
    }

    /**
     * Get the list of workflow actions available for this ticket. Workflow actions are NOT loaded by default, so check that they have been set before calling this method.
     * 
     * @return The list of workflow actions available for this ticket.
     */
    public Collection<Action> getListWorkflowActions( )
    {
        return _listWorkflowActions;
    }

    /**
     * Set the list of workflow actions available for this ticket.
     * 
     * @param listWorkflowActions
     *            The list of workflow actions available for this ticket.
     */
    public void setListWorkflowActions( Collection<Action> listWorkflowActions )
    {
        this._listWorkflowActions = listWorkflowActions;
    }

    /**
     * Get the list of response of this ticket
     * 
     * @return the list of response of this ticket
     */
    public List<Response> getListResponse( )
    {
        return _listResponse;
    }

    /**
     * Set the list of responses of this ticket
     * 
     * @param listResponse
     *            The list of responses
     */
    public void setListResponse( List<Response> listResponse )
    {
        _listResponse = listResponse;
    }

    /**
     *
     * @return the _strGuid
     */
    public String getGuid( )
    {
        return _strGuid;
    }

    /**
     * Sets the GUID
     * 
     * @param strGuid
     *            the Guid to set
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
    public int getCriticality( )
    {
        return _nCriticality;
    }

    /**
     * Sets the Criticality
     * 
     * @param nCriticality
     *            The Criticality
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
    public int getPriority( )
    {
        return _nPriority;
    }

    /**
     * Sets the Priority
     * 
     * @param nPriority
     *            The Priority
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
    public String getCustomerId( )
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
     * 
     * @return The AssigneeUser
     */
    public AssigneeUser getAssigneeUser( )
    {
        return _user;
    }

    /**
     * Sets the AssigneeUser
     * 
     * @param assigneeUser
     *            The AssigneeUser
     */
    public void setAssigneeUser( AssigneeUser assigneeUser )
    {
        _user = assigneeUser;
    }

    /**
     * Returns the AssigneeUnit
     * 
     * @return The AssigneeUnit
     */
    public AssigneeUnit getAssigneeUnit( )
    {
        return _unit;
    }

    /**
     * Sets the assigneeUnit
     * 
     * @param assigneeUnit
     *            The assigneeUnit
     */
    public void setAssigneeUnit( AssigneeUnit assigneeUnit )
    {
        _unit = assigneeUnit;
    }

    /**
     * Returns the user from assign up
     * 
     * @return The user from assign up
     */
    public AssigneeUser getAssignerUser( )
    {
        return _assignerUser;
    }

    /**
     * Sets the user from assign up
     * 
     * @param assignerUser
     *            The user from assign up
     */
    public void setAssignerUser( AssigneeUser assignerUser )
    {
        _assignerUser = assignerUser;
    }

    /**
     * Returns the unit from assign up
     * 
     * @return The unit from assign up
     */
    public AssigneeUnit getAssignerUnit( )
    {
        return _assignerUnit;
    }

    /**
     * Sets the unit from assign up
     * 
     * @param assignerUnit
     *            The unit from assign up
     */
    public void setAssignerUnit( AssigneeUnit assignerUnit )
    {
        _assignerUnit = assignerUnit;
    }

    @Override
    public String getResourceId( )
    {
        return String.valueOf( _nId );
    }

    @Override
    public String getResourceTypeCode( )
    {
        return TICKET_RESOURCE_TYPE;
    }

    /**
     * Returns the user message
     *
     * @return The user message
     */
    public String getUserMessage( )
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
    public String getUrl( )
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
     * Sets the channel
     * 
     * @param channel
     *            The channel
     */
    public void setChannel( Channel channel )
    {
        _channel = channel;
    }

    /**
     * Returns the channel
     * 
     * @return The channel
     */
    public Channel getChannel( )
    {
        return _channel;
    }

    /**
     * Returns the nomenclature
     * 
     * @return The string of the nomenclature
     */
    public String getNomenclature( )
    {
        return _strNomenclature;
    }

    /**
     * Sets the Nomenclature
     * 
     * @param String
     */
    public void setNomenclature( String _strNomenclature )
    {
        this._strNomenclature = _strNomenclature;
    }

    /**
     * Returns urgency, it s computed from max of criticty and priority value
     * 
     * @return urgency
     */
    public int getUrgency( )
    {
        return ( _nCriticality >= _nPriority ) ? _nCriticality : _nPriority;
    }

    /**
     * {@inheritDoc}
     */
    public String toString( )
    {
        StringBuilder sb = new StringBuilder( );
        sb.append( _nId );

        if ( StringUtils.isNotEmpty( _strReference ) )
        {
            sb.append( SEPARATOR ).append( _strReference );
        }

        return sb.toString( );
    }

    /**
     * @return the ticketAddress
     */
    public TicketAddress getTicketAddress( )
    {
        return _ticketAddress;
    }

    /**
     * @param _ticketAddress
     *            the ticket Address to set
     */
    public void setTicketAddress( TicketAddress _ticketAddress )
    {
        this._ticketAddress = _ticketAddress;
    }

    /**
     * @return the ticket marking
     */
    public int getIdTicketMarking( )
    {
        return _nIdticketMarking;
    }

    /**
     * @param _ticketMarking
     *            the Marking to set
     */
    public void setIdTicketMarking( int _nIdticketMarking )
    {
        this._nIdticketMarking = _nIdticketMarking;
    }

    /**
     * @return load and return the ticket marking
     */
    public Marking getMarking( )
    {
        return TicketHome.getTicketMarking( this );
    }
    
    
    /**
     * @return load and return the ticket demand id
     */
	public int getDemandId( ) 
	{
		return _nIdDemand;
	}

	public void setDemandId( int _nIdDemand ) 
	{
		this._nIdDemand = _nIdDemand;
	}
    
    
}
