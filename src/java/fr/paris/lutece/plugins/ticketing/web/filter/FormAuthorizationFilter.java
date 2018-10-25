package fr.paris.lutece.plugins.ticketing.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.mylutece.service.security.AuthenticationFilterService;
import fr.paris.lutece.plugins.ticketing.business.form.Form;
import fr.paris.lutece.plugins.ticketing.business.form.FormHome;
import fr.paris.lutece.plugins.ticketing.web.TicketXPage;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.PortalJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;

public class FormAuthorizationFilter implements Filter
{
    private static final String URL_INTERROGATIVE    = "?";
    private static final String URL_AMPERSAND        = "&";
    private static final String URL_EQUAL            = "=";
    private static final String URL_STAR             = "*";

    private static final String PARAMETER_XPAGE      = "page";

    private static final String PARAMETER_CATEGORY_1 = "cat1";
    private static final String PARAMETER_CATEGORY_2 = "cat2";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( FilterConfig config ) throws ServletException
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy( )
    {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException
    {
        HttpServletRequest req = ( HttpServletRequest ) request;
        HttpServletResponse resp = ( HttpServletResponse ) response;

        if ( SecurityService.isAuthenticationEnable( ) && isPrivateUrl( req ) && isFormRestricted( req ) )
        {
            try
            {
                filterAccess( req );
            } catch ( UserNotSignedException e )
            {
                if ( SecurityService.getInstance( ).isExternalAuthentication( ) && !SecurityService.getInstance( ).isMultiAuthenticationSupported( ) )
                {
                    try
                    {
                        SiteMessageService.setMessage( req, Messages.MESSAGE_USER_NOT_AUTHENTICATED, null, Messages.MESSAGE_USER_NOT_AUTHENTICATED, null, "", SiteMessage.TYPE_STOP );
                    } catch ( SiteMessageException lme )
                    {
                        resp.sendRedirect( AppPathService.getSiteMessageUrl( req ) );
                    }
                } else
                {
                    Form form = FormHome.getFormFromRequest( req );

                    // if form exists and doesn't require connection, form is not restricted
                    if ( form != null )
                    {
                        String category1 = request.getParameter( PARAMETER_CATEGORY_1 );
                        if ( category1 != null && StringUtils.isNumeric( category1 ) )
                        {
                            category1 = URL_AMPERSAND + PARAMETER_CATEGORY_1 + URL_EQUAL + category1;
                        } else
                        {
                            category1 = "";
                        }

                        String category2 = request.getParameter( PARAMETER_CATEGORY_2 );
                        if ( category2 != null && StringUtils.isNumeric( category2 ) )
                        {
                            category2 = URL_AMPERSAND + PARAMETER_CATEGORY_2 + URL_EQUAL + category2;
                        } else
                        {
                            category2 = "";
                        }
                        resp.sendRedirect( AppPathService.getAbsoluteUrl( req, SecurityService.getInstance( ).getLoginPageUrl( ) + "&form=" + form.getId( ) + category1 + category2 ) );
                    } else
                    {
                        resp.sendRedirect( PortalJspBean.redirectLogin( req ) );
                    }
                }

                return;
            }
        }

        chain.doFilter( request, response );
    }

    /**
     * Check wether a given url is to be considered as private (ie that needs a successful authentication to be accessed) or public (ie that can be access without being authenticated)
     *
     * @param request
     *            the http request
     * @return true if the url needs to be authenticated, false otherwise
     *
     */
    private boolean isPrivateUrl( HttpServletRequest request )
    {
        return !( ( isInSiteMessageUrl( request ) || ( isInPublicUrlList( request ) ) ) );
    }

    /**
     * check that the access is granted
     *
     * @param request
     *            The HTTP request
     *
     * @throws UserNotSignedException
     *             If the user is not signed
     *
     **/
    private static void filterAccess( HttpServletRequest request ) throws UserNotSignedException
    {
        // Try to register the user in case of external authentication
        if ( SecurityService.getInstance( ).isExternalAuthentication( ) && !SecurityService.getInstance( ).isMultiAuthenticationSupported( ) )
        {
            // The authentication is external
            // Should register the user if it's not already done
            if ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
            {
                if ( ( SecurityService.getInstance( ).getRemoteUser( request ) == null ) && ( SecurityService.getInstance( ).isPortalAuthenticationRequired( ) ) )
                {
                    // Authentication is required to access to the portal
                    throw new UserNotSignedException( );
                }
            }
        } else
        {
            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

            // no checks are needed if the user is already registered
            if ( user == null )
            {
                // if multiauthentication is supported, then when have to
                // check remote user before other check
                if ( SecurityService.getInstance( ).isMultiAuthenticationSupported( ) )
                {
                    // getRemoteUser needs to be checked before any check so
                    // the user is registered
                    // getRemoteUser throws an exception if no user found,
                    // but here we have to bypass this exception to display
                    // login page.
                    user = SecurityService.getInstance( ).getRemoteUser( request );
                }

                // If portal authentication is enabled and user is null and
                // the requested URL
                // is not the login URL, user cannot access to Portal
                if ( user == null )
                {
                    // Authentication is required to access to the portal
                    throw new UserNotSignedException( );
                }
            }
        }
    }

    /**
     * Checks if the requested is the url of site message
     * 
     * @param request
     *            The HTTP request
     * @return true if the requested is the url of site message
     */
    private boolean isInSiteMessageUrl( HttpServletRequest request )
    {
        return matchUrl( request, AppPathService.getSiteMessageUrl( request ) );
    }

    /**
     * Checks if the requested is in the list of urls defined in Security service that shouldn't be protected
     *
     * @param request
     *            the http request
     * 
     * @return true if the url is in the list, false otherwise
     *
     */
    private boolean isInPublicUrlList( HttpServletRequest request )
    {
        for ( String strPubliUrl : AuthenticationFilterService.getInstance( ).getPublicUrls( ) )
        {
            if ( matchUrl( request, strPubliUrl ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * method to test if the URL matches the pattern
     * 
     * @param request
     *            the request
     * @param strUrlPatern
     *            the pattern
     * @return true if the URL matches the pattern
     */
    private boolean matchUrl( HttpServletRequest request, String strUrlPatern )
    {
        boolean bMatch = false;

        if ( strUrlPatern != null )
        {
            UrlItem url = new UrlItem( getResquestedUrl( request ) );

            if ( strUrlPatern.contains( URL_INTERROGATIVE ) )
            {
                for ( String strParamPatternValue : strUrlPatern.substring( strUrlPatern.indexOf( URL_INTERROGATIVE ) + 1 ).split( URL_AMPERSAND ) )
                {
                    String[] arrayPatternParamValue = strParamPatternValue.split( URL_EQUAL );

                    if ( ( arrayPatternParamValue != null ) && ( request.getParameter( arrayPatternParamValue[0] ) != null ) )
                    {
                        url.addParameter( arrayPatternParamValue[0], request.getParameter( arrayPatternParamValue[0] ) );
                    }
                }
            }

            if ( strUrlPatern.contains( URL_STAR ) )
            {
                String strUrlPaternLeftEnd = strUrlPatern.substring( 0, strUrlPatern.indexOf( URL_STAR ) );
                String strAbsoluteUrlPattern = getAbsoluteUrl( request, strUrlPaternLeftEnd );
                bMatch = url.getUrl( ).startsWith( strAbsoluteUrlPattern );
            } else
            {
                String strAbsoluteUrlPattern = getAbsoluteUrl( request, strUrlPatern );
                bMatch = url.getUrl( ).equals( strAbsoluteUrlPattern );
            }
        }

        return bMatch;
    }

    /**
     * Returns the absolute url corresponding to the given one, if the later was found to be relative. An url starting with "http://" is absolute. A relative url should be given relatively to the webapp root.
     *
     * @param request
     *            the http request (provides the base path if needed)
     * @param strUrl
     *            the url to transform
     * @return the corresonding absolute url
     *
     */
    private String getAbsoluteUrl( HttpServletRequest request, String strUrl )
    {
        if ( ( strUrl != null ) && !strUrl.startsWith( "http://" ) && !strUrl.startsWith( "https://" ) )
        {
            return AppPathService.getBaseUrl( request ) + strUrl;
        } else
        {
            return strUrl;
        }
    }

    /**
     * Return the absolute representation of the requested url
     *
     * @param request
     *            the http request (provides the base path if needed)
     * @return the requested url has a string
     *
     */
    private String getResquestedUrl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + request.getServletPath( ).substring( 1 );
    }

    public boolean isFormRestricted( HttpServletRequest request ) throws IOException, ServletException
    {
        String strXPage = request.getParameter( PARAMETER_XPAGE );

        // Only apply filter to ticket xpage
        if ( TicketXPage.TICKET_XPAGE_NAME.equals( strXPage ) )
        {
            Form form = FormHome.getFormFromRequest( request );

            // if form exists and doesn't require connection, form is not restricted
            if ( form != null && !form.isConnection( ) )
            {
                return false;
            }
        }

        return true;
    }
}
