package fr.paris.lutece.plugins.ticketing.business.parambouton;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class ParamBoutonDAO implements IParamBoutonDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT                    = "SELECT id_groupe, libelle_identifiant, cle, description, ordre FROM ticketing_groupe_action WHERE id_groupe = ?";
    private static final String SQL_QUERY_INSERT                    = "INSERT INTO ticketing_groupe_action (libelle_identifiant, cle, description, ordre) VALUES(?,?,?,?)";
    private static final String SQL_QUERY_UPDATE                    = "UPDATE ticketing_groupe_action SET libelle_identifiant=?, cle=?, description=?, ordre=? WHERE id_groupe=?";
    private static final String SQL_QUERY_SELECT_BY_GROUP           = "SELECT id_param, id_action, couleur, ordre, icone, id_groupe FROM ticketing_param_bouton_action param join ticketing_couleur_bouton couleur on couleur.id_couleur = param.id_couleur where id_groupe=?";
    private static final String SQL_QUERY_SELECT_WITHOUT_GROUP      = "SELECT id_action from workflow_action where id_action not in (select id_action FROM ticketing_param_bouton_action)";
    private static final String SQL_QUERY_SELECT_WITH_DEFAULT_GROUP = "SELECT id_action from workflow_action where id_action not in (select id_action FROM ticketing_param_bouton_action)";

    private static final int    ID_GROUPE_NON_CONFIGURE             = 1;

    @Override
    public void insert( ParamBouton paramBouton, Plugin plugin )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void store( ParamBouton paramBouton, Plugin plugin )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<ParamBouton> selectParamBoutonListByGroup( int idGroup, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_GROUP, plugin );
        daoUtil.setInt( 1, idGroup );
        daoUtil.executeQuery( );

        List<ParamBouton> listParamBouton = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            ParamBouton paramBouton = new ParamBouton( );
            fillParamBouton( daoUtil, paramBouton );
            listParamBouton.add( paramBouton );
        }

        daoUtil.free( );

        return listParamBouton;
    }

    @Override
    public List<ParamBouton> selectParamBoutonListWithoutGroup( Plugin plugin )
    {
        // Récupération des actions sans groupe
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WITHOUT_GROUP, plugin );
        daoUtil.executeQuery( );

        List<ParamBouton> listParamBouton = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            ParamBouton paramBouton = new ParamBouton( );
            paramBouton.setIdAction( daoUtil.getInt( 1 ) );
            listParamBouton.add( paramBouton );
        }

        daoUtil.free( );

        // Récupération des actions affectées au gruope non configuré
        daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_GROUP, plugin );
        daoUtil.setInt( 1, ID_GROUPE_NON_CONFIGURE );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ParamBouton paramBouton = new ParamBouton( );
            fillParamBouton( daoUtil, paramBouton );
            listParamBouton.add( paramBouton );
        }

        return listParamBouton;
    }

    private void fillParamBouton( DAOUtil daoUtil, ParamBouton paramBouton )
    {
        int nIndex = 1;

        paramBouton.setIdparam( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
        paramBouton.setCouleur( daoUtil.getString( nIndex++ ) );
        paramBouton.setOrdre( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIcone( daoUtil.getString( nIndex++ ) );
        paramBouton.setIdGroupe( daoUtil.getInt( nIndex ) );
    }
}
