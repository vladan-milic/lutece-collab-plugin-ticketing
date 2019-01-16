package fr.paris.lutece.plugins.ticketing.business.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;

public class FormEntryType
{
    private static final String USER_TITLE = "user_title";
    private static final String LAST_NAME = "last_name";
    private static final String FIRST_NAME = "first_name";
    private static final String EMAIL = "email";
    private static final String PHONE_NUMBERS = "phone_numbers";
    private static final String CONTACT_MODE = "contact_mode";
    private static final String CHANNEL = "channel";
    private static final String COMMENT = "comment";
    private static final String CATEGORY = "category_level_";

    public List<String> entryTypes( )
    {
        return Arrays.asList( USER_TITLE, LAST_NAME, FIRST_NAME, EMAIL, PHONE_NUMBERS, CONTACT_MODE, CHANNEL, COMMENT );
    }

    public List<String> entryTypesWithCategories( )
    {
        List<String> entryTypes = new ArrayList<>( entryTypes( ) );

        for ( TicketCategoryType categoryType : TicketCategoryTypeHome.getCategoryTypesList( ) )
        {
            entryTypes.add( CATEGORY + categoryType.getDepthNumber( ) );
        }

        return entryTypes;
    }

    public String getUserTitle( )
    {
        return USER_TITLE;
    }

    public String getLastName( )
    {
        return LAST_NAME;
    }

    public String getFirstName( )
    {
        return FIRST_NAME;
    }

    public String getEmail( )
    {
        return EMAIL;
    }

    public String getPhoneNumbers( )
    {
        return PHONE_NUMBERS;
    }

    public String getContactMode( )
    {
        return CONTACT_MODE;
    }

    public String getComment( )
    {
        return COMMENT;
    }

    public String getCategory( )
    {
        return CATEGORY;
    }

    public String getChannel( )
    {
        return CHANNEL;
    }

    public boolean isForcedMandatory( String entryType )
    {
        return Arrays.asList( EMAIL, FIRST_NAME, LAST_NAME, CHANNEL ).contains( entryType );
    }

    public boolean isForcedHidden( String entryType )
    {
        return Arrays.asList( CHANNEL ).contains( entryType );
    }

}
