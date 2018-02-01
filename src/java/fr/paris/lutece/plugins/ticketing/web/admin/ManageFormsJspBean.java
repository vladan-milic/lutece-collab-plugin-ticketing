package fr.paris.lutece.plugins.ticketing.web.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.form.Form;
import fr.paris.lutece.plugins.ticketing.business.form.FormEntry;
import fr.paris.lutece.plugins.ticketing.business.form.FormEntryHome;
import fr.paris.lutece.plugins.ticketing.business.form.FormEntryType;
import fr.paris.lutece.plugins.ticketing.business.form.FormHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.ModelUtils;
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
 * This class provides the user interface to manage forms ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageForms.jsp", controllerPath = "jsp/admin/plugins/ticketing/admin/", right = "TICKETING_TICKETS_MANAGEMENT_FORMS" )
public class ManageFormsJspBean extends MVCAdminJspBean {

    /**
     * 
     */
    private static final long serialVersionUID = 3672494350924238271L;

    // Templates
    private static final String TEMPLATE_MANAGE_FORMS = "/admin/plugins/ticketing/admin/manage_forms.html";
    //    private static final String TEMPLATE_CREATE_FORM = "/admin/plugins/ticketing/admin/create_form.html";
    //    private static final String TEMPLATE_MODIFY_FORM = "/admin/plugins/ticketing/admin/modify_form.html";
    private static final String TEMPLATE_EDIT_FORM = "/admin/plugins/ticketing/admin/edit_form.html";
    // Views
    private static final String VIEW_MANAGE_FORMS = "manageForms";
    private static final String VIEW_CREATE_FORM = "createForm";
    private static final String VIEW_MODIFY_FORM = "modifyForm";

    // Actions
    private static final String ACTION_CREATE_FORM = "createForm";
    private static final String ACTION_MODIFY_FORM = "modifyForm";
    private static final String ACTION_REMOVE_FORM = "removeForm";
    private static final String ACTION_CONFIRM_REMOVE_FORM = "confirmRemoveForm";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FORMS = "ticketing.manage_form.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORM = "ticketing.modify_form.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FORM = "ticketing.create_form.pageTitle";

    // Properties
    private static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "ticketing.listItems.itemsPerPage";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_FORM = "ticketing.message.confirmRemoveForm";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "ticketing.model.entity.form.attribute.";

    // Markers
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM = "form";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_FORMENTRYTYPE = "formEntryType";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String JSP_MANAGE_FORMS = "jsp/admin/plugins/ticketing/admin/ManageForms.jsp";

    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    // Infos
    private static final String INFO_FORM_CREATED = "ticketing.info.form.created";
    private static final String INFO_FORM_UPDATED = "ticketing.info.form.updated";
    private static final String INFO_FORM_REMOVED = "ticketing.info.form.removed";

    // Variables
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_FORM = "id";
    private static final String PARAMETER_ROLE = "roles";

    // Session variable to store working values
    private Form _form;
    private FormEntryType _formEntryType;
    private FormEntry _formEntry;

    /**
     * Return a model that contains the list and paginator infos
     * 
     * @param request
     *            The HTTP request
     * @param strBookmark
     *            The bookmark
     * @param list
     *            The list of item
     * @param strFormJsp
     *            The JSP
     * @return The model
     */
    protected Map<String, Object> getPaginatedListModel( HttpServletRequest request, String strBookmark, List<Form> list, String strFormJsp )
    {
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        UrlItem url = new UrlItem( strFormJsp );
        String strUrl = url.getUrl( );

        // PAGINATOR
        LocalizedPaginator<Form> paginator = new LocalizedPaginator<Form>( list, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, _strCurrentPageIndex,
                getLocale( ) );

        Map<String, Object> model = getModel( );

        model.put( MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( MARK_PAGINATOR, paginator );
        model.put( strBookmark, paginator.getPageItems( ) );

        return model;    	
    }

    /**
     * Build the Form View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_FORMS, defaultView = true )
    public String getManageForms( HttpServletRequest request )
    {
        List<Form> listForms = new ArrayList<Form>( );
        listForms = FormHome.getFormsList();

        Map<String, Object> model = getPaginatedListModel( request, MARK_FORM_LIST, listForms, JSP_MANAGE_FORMS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_FORMS, TEMPLATE_MANAGE_FORMS, model );
    }

    /**
     * Returns the form to create a modelresponse
     *
     * @param request
     *            The Http request
     * @return the html code of the modelresponse form
     */
    @View( VIEW_CREATE_FORM )
    public String getCreateForm( HttpServletRequest request )
    {
        _form = new Form( );
        _formEntryType = new FormEntryType();

        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, _form );
        model.put( MARK_FORMENTRYTYPE, _formEntryType );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( ).getCategoriesTree( ).getDepths( ) );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale( ) ) );
        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList( request.getLocale( ) ) );
        model.put( MARK_TICKET, createFakeTicketFromForm( _form ) );
        ModelUtils.storeRichText( request, model );
        return getPage( PROPERTY_PAGE_TITLE_CREATE_FORM, TEMPLATE_EDIT_FORM, model );
    }

    /**
     * Process the data capture form of a new modelresponse
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws IOException 
     */
    @Action( ACTION_CREATE_FORM )
    public String doCreateForm( HttpServletRequest request ) throws IOException
    {
        populate( _form, request );

        // Check constraints
        if ( !validateBean( _form, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_FORM );
        }      

        FormHome.create( _form );

        updateFormEntries( request );

        addInfo( INFO_FORM_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    private void updateFormEntries( HttpServletRequest request )
    {
        FormEntryHome.removeByIdForm( _form.getId( ) );
        FormEntry formEntry;
        for ( String entryType : _formEntryType.entryTypesWithCategories( ) )
        {
            formEntry = new FormEntry( );

            formEntry.setIdChamp( entryType );
            formEntry.setIdForm( _form.getId( ) );

            // get parameters
            String strHidden = request.getParameter( "formEntry." + entryType + ".hidden" );
            String strMandatory = request.getParameter( "formEntry." + entryType + ".mandatory" );
            String strDefaultValue = request.getParameter( "formEntry." + entryType + ".defaultValue" );

            formEntry.setHidden( strHidden != null );
            formEntry.setMandatory( strMandatory != null );
            formEntry.setDefaultValue( strDefaultValue );

            FormEntryHome.create( formEntry );
        }
    }

    /**
     * Manages the removal form of a modelresponse whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_FORM )
    public String getConfirmRemoveForm( HttpServletRequest request )
    {
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FORM ) );
        url.addParameter( PARAMETER_ID_FORM, nIdForm );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FORM, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a modelresponse
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage modelresponses
     */
    @Action( ACTION_REMOVE_FORM )
    public String doRemoveForm( HttpServletRequest request )
    {
        int nIdForm = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );

        FormEntryHome.removeByIdForm( nIdForm );
        FormHome.remove( nIdForm );

        addInfo( INFO_FORM_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    /**
     * Returns the form to update info about a modelresponse
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_FORM )
    public String getModifyForm( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );

        _form = FormHome.findByPrimaryKey( nId );
        _formEntryType = new FormEntryType();
        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, _form );
        model.put( MARK_FORMENTRYTYPE, _formEntryType );
        model.put( MARK_TICKET, createFakeTicketFromForm( _form ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( ).getCategoriesTree( ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( ).getCategoriesTree( ).getDepths( ) );

        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale( ) ) );
        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList( request.getLocale( ) ) );

        ModelUtils.storeRichText( request, model );
        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_EDIT_FORM, model );
    }

    // Method used to create a fake ticket to have category choice
    private Ticket createFakeTicketFromForm( Form form )
    {
        Ticket fakeTicket = new Ticket( );
        TicketCategory parentTicketCategory = null;
        for ( TicketCategoryType categoryType : TicketCategoryTypeHome.getCategoryTypesList( ) )
        {
            FormEntry formEntry = form.getEntry( _formEntryType.getCategory( ) + categoryType.getDepthNumber( ) );
            if ( StringUtils.isNotEmpty( formEntry.getDefaultValue( ) ) )
            {
                TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( Integer.parseInt( formEntry.getDefaultValue( ) ) );
                if ( ticketCategory != null )
                {
                    ticketCategory.setParent( parentTicketCategory );
                    ticketCategory.setDepth( categoryType );
                    parentTicketCategory = ticketCategory;

                    fakeTicket.setTicketCategory( ticketCategory );
                }
            }
        }

        return fakeTicket;
    }

    /**
     * Process the change form of a modelresponse
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_FORM )
    public String doModifyForm( HttpServletRequest request )
    {
        populate( _form, request );
        _form.setFormEntries(_form.getFormEntries());
        // Check constraints
        if ( !validateBean( _form, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_FORM, PARAMETER_ID_FORM, _form.getId( ) );
        }


        updateFormEntries(request);

        FormHome.update( _form );

        addInfo( INFO_FORM_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }


}
