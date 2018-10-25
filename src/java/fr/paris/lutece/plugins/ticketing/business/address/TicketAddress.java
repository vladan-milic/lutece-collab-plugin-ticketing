/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.business.address;

import java.io.Serializable;

import javax.validation.constraints.Size;

/**
 * This is the business class for the object TicketAddress
 */
public class TicketAddress implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    @Size( max = 255, message = "#i18n{ticketing.validation.ticketAddress.address.size}" )
    private String            _strAddress;
    @Size( max = 255, message = "#i18n{ticketing.validation.ticketAddress.addressDetail.size}" )
    private String            _strAddressDetail;
    @Size( max = 5, message = "#i18n{ticketing.validation.ticketAddress.postalCode.size}" )
    private String            _strPostalCode;
    @Size( max = 255, message = "#i18n{ticketing.validation.ticketAddress.city.size}" )
    private String            _strCity;

    /**
     * @return the address
     */
    public String getAddress( )
    {
        return _strAddress;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress( String address )
    {
        _strAddress = address;
    }

    /**
     * @return the addressDetail
     */
    public String getAddressDetail( )
    {
        return _strAddressDetail;
    }

    /**
     * @param addressDetail
     *            the addressDetail to set
     */
    public void setAddressDetail( String addressDetail )
    {
        _strAddressDetail = addressDetail;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode( )
    {
        return _strPostalCode;
    }

    /**
     * @param postalCode
     *            the postalCode to set
     */
    public void setPostalCode( String postalCode )
    {
        _strPostalCode = postalCode;
    }

    /**
     * @return the city
     */
    public String getCity( )
    {
        return _strCity;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity( String city )
    {
        _strCity = city;
    }

}
