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
package fr.paris.lutece.plugins.ticketing.business;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;


/**
 * This is the business class for the object Ticket
 */
public class Ticket implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String EMPTY = "";

    public static final String TICKET_RESOURCE_TYPE = "ticket";

    // Variables declarations 
    private int _nId;
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
    @Email( message = "#i18n{portal.validation.message.email}" )
    @Size( max = 255, message = "#i18n{ticketing.validation.ticket.Email.size}" )
    private String _strEmail;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.FixedPhoneNumber.size}" )
    private String _strFixedPhoneNumber;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.MobilePhoneNumber.size}" )
    private String _strMobilePhoneNumber;
    private int _nIdTicketType;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.TicketType.size}" )
    private String _strTicketType;
    private int _nIdTicketDomain;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.TicketDomain.size}" )
    private String _strTicketDomain;
    private int _nIdTicketCategory;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.TicketCategory.size}" )
    private String _strTicketCategory;
    private int _nIdContactMode;
    @Size( max = 50, message = "#i18n{ticketing.validation.ticket.ContactMode.size}" )
    private String _strContactMode;
    private String _strTicketComment;
    private String _strConfirmationMsg;
    private int _nTicketStatus;
    @Size( max = 255, message = "#i18n{ticketing.validation.ticket.TicketStatusText.size}" )
    private String _strTicketStatusText;
    private State _state;
    private Timestamp _dDateCreate;
    private Timestamp _dDateUpdate;
    private transient Collection<Action> _listWorkflowActions;
    private List<Response> _listResponse;   

    
    /**
     * @return */
    public Timestamp getDateCreate() {
        return _dDateCreate;
    }

     /**
     * @param dDateCreate*/
    public void setDateCreate(Timestamp dDateCreate) {
        this._dDateCreate = dDateCreate;
    }

     /**
     * @return */
    public Timestamp getDateUpdate() {
        return _dDateUpdate;
    }

     /**
     * @param dDateUpdate*/
    public void setDateUpdate(Timestamp dDateUpdate) {
        this._dDateUpdate = dDateUpdate;
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
     * Returns true if the fixed phone number and the mobile phone number are
     * not filled, and else returns false
     * 
     * @return boolean result
     */
    public boolean hasNoPhoneNumberFilled( )
    {
        return EMPTY.equals( this.getFixedPhoneNumber( ).trim( ) )
                && EMPTY.equals( this.getMobilePhoneNumber( ).trim( ) );
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
        this._listResponse = listResponse;
    }
}
