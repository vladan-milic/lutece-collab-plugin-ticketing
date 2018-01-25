package fr.paris.lutece.plugins.ticketing.business.form;

import java.util.Arrays;
import java.util.List;

public class FormEntryType
{
    private static final String USER_TITLE = "user_title";
    private static final String LAST_NAME = "last_name";
    private static final String FIRST_NAME = "first_name";
    private static final String EMAIL = "email";
    private static final String PHONE_NUMBERS = "phone_numbers";
    private static final String CONTACT_MODE = "contact_mode";
    private static final String COMMENT = "comment";
    private static final String CATEGORY = "category_level_";

    public List<String> entryTypes() {
        return Arrays.asList( USER_TITLE, LAST_NAME, FIRST_NAME, EMAIL, PHONE_NUMBERS, CONTACT_MODE, COMMENT );
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

}
