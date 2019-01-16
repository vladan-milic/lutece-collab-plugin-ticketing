package fr.paris.lutece.plugins.ticketing.business.sphinx;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class SphinxDAO implements ISphinxDAO
{

    // SQL Queries
    // private static final String SQL_SELECT_MAILING_LIST= "SELECT * FROM ( "
    // + " SELECT @z := ticketing_ticket.id_ticket_category AS id,"
    // + " ticketing_ticket.email, "
    // + " CONCAT( "
    // + " TIMESTAMPDIFF(day,date_create,date_close) , ' jours '"
    // + " ) AS 'temps de traitement ' "
    // + " FROM ticketing_ticket "
    // + " WHERE DATE(date_close) = CURDATE() "
    // + " ) AS X "
    // + " CROSS JOIN ( "
    // + " SELECT GROUP_CONCAT(T2.label SEPARATOR ';') AS 'Domaines' "
    // + " FROM ( "
    // + " SELECT @r AS _id, "
    // + " (SELECT @r := id_parent "
    // + " FROM ticketing_category "
    // + " WHERE id_category = _id) AS parent_id, "
    // + " @l := @l + 1 AS lvl "
    // + " FROM ( SELECT @r := @z, @l := 0) vars "
    // + " CROSS JOIN ticketing_category m "
    // + " WHERE @r <> 0 "
    // + " ) T1 "
    // + " JOIN ticketing_category T2 "
    // + " ON T1._id = T2.id_category "
    // + " ) AS Y";
    private static final String SQL_SELECT_MAILING_LIST = "		SELECT tt.id_ticket_category ,  tt.email,  Timestampdiff( day, tt.date_create, tt.date_close ) AS 'temps de traitement ' "
            + "		FROM ticketing_ticket tt "
            + "		JOIN workflow_resource_history  wrh ON wrh.id_resource = tt.id_ticket "
            + "		WHERE wrh.id_action = ? "
            + "		AND Date(tt.date_close) = CURDATE() ";

    private static final String SQL_SELECT_DOMAINS_LIST = "	SELECT Group_concat(T2.label SEPARATOR ';') AS 'Domaines' " + "	FROM( SELECT @r AS _id, " + "	("
            + "				SELECT @r := id_parent " + "				FROM ticketing_category " + "				WHERE id_category = _id " + "			) AS parent_id, "
            + "			@l := @l + 1 AS lvl " + "		FROM( SELECT @r := ?, @l := 0) vars CROSS " + "		JOIN ticketing_category m " + "		WHERE @r <> 0 " + "	) T1 "
            + "JOIN ticketing_category T2 ON T1._id = T2.id_category";

    @Override
    public List<Mailing> getNewMailingList( int idAction, Plugin plugin )
    {
        List<Mailing> mailingList = new ArrayList<>( );
        DAOUtil daoUtil = new DAOUtil( SQL_SELECT_MAILING_LIST, plugin );
        DAOUtil daoUtil2 = new DAOUtil( SQL_SELECT_DOMAINS_LIST, plugin );

        daoUtil.setInt( 1, idAction );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Mailing mailing = dataToMailing( daoUtil );

            mailingList.add( mailing );
        }

        daoUtil.free( );

        for ( Mailing mailing : mailingList )
        {
            daoUtil2.setInt( 1, mailing.getId( ) );
            daoUtil2.executeQuery( );
            while ( daoUtil2.next( ) )
            {
                domainsToMailing( daoUtil2, mailing );
            }
        }

        daoUtil2.free( );
        return mailingList;
    }

    /**
     * Creates a Ticket object from data
     * 
     * @param daoUtil
     *            the data
     * @return the Ticket object
     */
    private static Mailing dataToMailing( DAOUtil daoUtil )
    {
        Mailing mailing = new Mailing( );

        int nIndex = 1;

        mailing.setId( daoUtil.getInt( nIndex++ ) );
        mailing.setEMail( daoUtil.getString( nIndex++ ) );
        mailing.setDuration( daoUtil.getString( nIndex++ ) );

        return mailing;
    }

    private static void domainsToMailing( DAOUtil daoUtil, Mailing mailing )
    {
        int nIndex = 1;
        mailing.setDomains( daoUtil.getString( nIndex++ ) );
    }

}
