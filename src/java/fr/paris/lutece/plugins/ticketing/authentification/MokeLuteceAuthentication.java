/*
 *
 *  * Copyright (c) 2002-2013, Mairie de Paris
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions
 *  * are met:
 *  *
 *  *  1. Redistributions of source code must retain the above copyright notice
 *  *     and the following disclaimer.
 *  *
 *  *  2. Redistributions in binary form must reproduce the above copyright notice
 *  *     and the following disclaimer in the documentation and/or other materials
 *  *     provided with the distribution.
 *  *
 *  *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *  *     contributors may be used to endorse or promote products derived from
 *  *     this software without specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 *  * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  * POSSIBILITY OF SUCH DAMAGE.
 *  *
 *  * License 1.0
 *
 */
package fr.paris.lutece.plugins.ticketing.authentification;

import java.util.Collection;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;

/**
 *
 */
public class MokeLuteceAuthentication implements LuteceAuthentication
{
    /** Creates a new instance of MokeLuteceAuthentication */
    public MokeLuteceAuthentication( )
    {
    }

    @Override
    public String getAuthServiceName( )
    {
        return "MOKE AUTHENTICATION SERVICE";
    }

    @Override
    public String getAuthType( HttpServletRequest request )
    {
        return "mock";
    }

    @Override
    public LuteceUser login( final String strUserName, final String strUserPassword, HttpServletRequest request ) throws LoginException
    {
        LuteceUser user = new MokeLuteceUser( strUserName, this );
        user.setUserInfo( "user.id.customer", "12345" );
        user.setUserInfo( LuteceUser.GENDER, "M" );
        user.setUserInfo( LuteceUser.NAME_GIVEN, "Toto" );
        user.setUserInfo( LuteceUser.NAME_FAMILY, "Tata" );
        user.setUserInfo( LuteceUser.NAME_MIDDLE, "Titi" );
        user.setUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL, "toto@titi.com" );
        user.setUserInfo( LuteceUser.HOME_INFO_TELECOM_TELEPHONE_NUMBER, "0145455656" );
        user.setUserInfo( LuteceUser.HOME_INFO_TELECOM_MOBILE_NUMBER, "0645455656"  );
        return user;
    }

    public void logout( LuteceUser user )
    {
    }

    @Override
    public boolean findResetPassword( HttpServletRequest request, String strLogin )
    {
        return false;
    }

    @Override
    public LuteceUser getAnonymousUser( )
    {
        return null;
    }

    public boolean isUserInRole( LuteceUser user, HttpServletRequest request, String strRole )
    {
        return true;
    }

    public String[] getRolesByUser( LuteceUser user )
    {
        return null;
    }

    @Override
    public boolean isExternalAuthentication( )
    {
        return false;
    }

    @Override
    public LuteceUser getHttpAuthenticatedUser( HttpServletRequest request )
    {
        return null;
    }

    @Override
    public String getLoginPageUrl( )
    {
        return "jsp/site/Portal.jsp?page=mylutece&action=login";
    }

    @Override
    public String getDoLoginUrl( )
    {
        return "jsp/site/Portal.jsp?page=ticket&view=create";
    }

    @Override
    public String getDoLogoutUrl( )
    {
        return "jsp/site/Portal.jsp?page=mylutece&action=logout";
    }

    @Override
    public String getNewAccountPageUrl( )
    {
        return "";
    }

    @Override
    public String getViewAccountPageUrl( )
    {
        return "";
    }

    @Override
    public String getLostPasswordPageUrl( )
    {
        return null;
    }

    @Override
    public String getResetPasswordPageUrl( HttpServletRequest request )
    {
        return null;
    }

    @Override
    public String getAccessDeniedTemplate( )
    {
        return null;
    }

    @Override
    public String getAccessControledTemplate( )
    {
        return null;
    }

    @Override
    public boolean isUsersListAvailable( )
    {
        return false;
    }

    @Override
    public Collection<LuteceUser> getUsers( )
    {
        return null;
    }

    @Override
    public LuteceUser getUser( String strUserLogin )
    {
        return null;
    }

    @Override
    public boolean isDelegatedAuthentication( )
    {
        return false;
    }

    @Override
    public boolean isMultiAuthenticationSupported( )
    {
        return false;
    }

    @Override
    public String getIconUrl( )
    {
        return null;
    }

    @Override
    public String getName( )
    {
        return null;
    }

    @Override
    public String getPluginName( )
    {
        return null;
    }

    public void updateDateLastLogin( LuteceUser user, HttpServletRequest request )
    {
    }

    @Override
    public String getLostLoginPageUrl( )
    {
        return null;
    }
}
