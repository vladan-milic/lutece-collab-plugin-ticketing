package fr.paris.lutece.plugins.ticketing.web.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.marking.Marking;
import fr.paris.lutece.plugins.ticketing.business.marking.MarkingHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Marking features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageMarkings.jsp", controllerPath = "jsp/admin/plugins/ticketing/admin/", right = "TICKETING_MANAGEMENT_MARKING" )
public class MarkingJspBean extends MVCAdminJspBean {

	private static final long serialVersionUID = 8048541959673399437L;

	// Templates
    private static final String TEMPLATE_MANAGE_MARKINGS = "/admin/plugins/ticketing/admin/manage_markings.html";
    private static final String TEMPLATE_CREATE_MARKING = "/admin/plugins/ticketing/admin/create_marking.html";
    private static final String TEMPLATE_MODIFY_MARKING = "/admin/plugins/ticketing/admin/modify_marking.html";
    
    // Views
    private static final String VIEW_MANAGE_MARKINGS = "manageMarkings";
    private static final String VIEW_CREATE_MARKING = "createMarking";
    private static final String VIEW_MODIFY_MARKING = "modifyMarking";

    // Actions
    private static final String ACTION_CREATE_MARKING = "createMarking";
    private static final String ACTION_MODIFY_MARKING = "modifyMarking";
    private static final String ACTION_REMOVE_MARKING = "removeMarking";
    private static final String ACTION_CONFIRM_REMOVE_MARKING = "confirmRemoveMarking";
    
    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_MARKINGS = "ticketing.manage_marking.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_MARKING = "ticketing.modify_marking.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_MARKING = "ticketing.create_marking.pageTitle";
    
    // Properties
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";
    
    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_MARKING = "ticketing.message.confirmRemoveMarking";
    
    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.marking.attribute.";
  
    // Markers
    private static final String MARK_MARKING_LIST = "marking_list";
    private static final String MARK_MARKING = "marking";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String JSP_MANAGE_MARKINGS = "jsp/admin/plugins/ticketing/admin/ManageMarkings.jsp";
    
    // Infos
    private static final String INFO_MARKING_CREATED = "ticketing.info.marking.created";
    private static final String INFO_MARKING_UPDATED = "ticketing.info.marking.updated";
    private static final String INFO_MARKING_REMOVED = "ticketing.info.marking.removed";
    
    // Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    
    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_MARKING = "id";
    
    // Session variable to store working values
    private Marking _marking;
    
    /**
     * Return a model that contains the list and paginator infos
     * 
     * @param request
     *            The HTTP request
     * @param strBookmark
     *            The bookmark
     * @param list
     *            The list of item
     * @param strManageJsp
     *            The JSP
     * @return The model
     */
    protected Map<String, Object> getPaginatedListModel( HttpServletRequest request, String strBookmark, List<Marking> list, String strManageJsp )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( strManageJsp );
        String strUrl = url.getUrl( );

        // PAGINATOR
        LocalizedPaginator<Marking> paginator = new LocalizedPaginator<Marking>( list, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        Map<String, Object> model = getModel( );
        
        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( strBookmark, paginator.getPageItems( ) );

        return model;
    }
    
    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_MARKINGS, defaultView = true )
    public String getManageMarkings( HttpServletRequest request )
    {
    	List<Marking> listMarkings = new ArrayList<Marking>( );
    	listMarkings = MarkingHome.getMarkingsList( ); 
    	
    	Map<String, Object> model = getPaginatedListModel( request, MARK_MARKING_LIST, listMarkings, JSP_MANAGE_MARKINGS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_MARKINGS, TEMPLATE_MANAGE_MARKINGS, model );
    }
    
    /**
     * Returns the form to create a modelresponse
     *
     * @param request
     *            The Http request
     * @return the html code of the modelresponse form
     */
    @View( VIEW_CREATE_MARKING )
    public String getCreateMarking( HttpServletRequest request )
    {
        _marking = new Marking( );

        Map<String, Object> model = getModel( );
        model.put( MARK_MARKING, _marking );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_MARKING, TEMPLATE_CREATE_MARKING, model );
    }

    /**
     * Process the data capture form of a new modelresponse
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_MARKING )
    public String doCreateMarking( HttpServletRequest request )
    {
        populate( _marking, request );
        
        // Check constraints
        if ( !validateBean( _marking, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_MARKING );
        }

        MarkingHome.create( _marking );

        addInfo( INFO_MARKING_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_MARKINGS );
    }

    /**
     * Manages the removal form of a modelresponse whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_MARKING )
    public String getConfirmRemoveMarking( HttpServletRequest request )
    {
        int nIdMarking = Integer.parseInt( request.getParameter( PARAMETER_ID_MARKING ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_MARKING ) );
        url.addParameter( PARAMETER_ID_MARKING, nIdMarking );
        
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MARKING, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a modelresponse
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage modelresponses
     */
    @Action( ACTION_REMOVE_MARKING )
    public String doRemoveMarking( HttpServletRequest request )
    {
        int nIdMarking = Integer.parseInt( request.getParameter( PARAMETER_ID_MARKING ) );

        TicketHome.resetMarkingId( nIdMarking );
        MarkingHome.remove( nIdMarking );
        
        addInfo( INFO_MARKING_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_MARKINGS );
    }

    /**
     * Returns the form to update info about a modelresponse
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_MARKING )
    public String getModifyMarking( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_MARKING ) );

        if ( ( _marking == null ) || ( _marking.getId( ) != nId ) )
        {
            _marking = MarkingHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_MARKING, _marking );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_MARKING, TEMPLATE_MODIFY_MARKING, model );
    }

    /**
     * Process the change form of a modelresponse
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_MARKING )
    public String doModifyMarking( HttpServletRequest request )
    {
        populate( _marking, request );
        
        // Check constraints
        if ( !validateBean( _marking, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
        	return redirect( request, VIEW_MODIFY_MARKING, PARAMETER_ID_MARKING, _marking.getId( ) );
        }

        MarkingHome.update( _marking );
        addInfo( INFO_MARKING_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_MARKINGS );
    }

}
