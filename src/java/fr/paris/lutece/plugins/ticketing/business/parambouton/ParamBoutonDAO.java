package fr.paris.lutece.plugins.ticketing.business.parambouton;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class ParamBoutonDAO implements IParamBoutonDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT               = "SELECT id_groupe, libelle_identifiant, cle, description, ordre FROM ticketing_groupe_action WHERE id_groupe = ?";
    private static final String SQL_QUERY_INSERT               = "INSERT INTO ticketing_groupe_action (libelle_identifiant, cle, description, ordre) VALUES(?,?,?,?)";
    private static final String SQL_QUERY_UPDATE               = "UPDATE ticketing_groupe_action SET libelle_identifiant=?, cle=?, description=?, ordre=? WHERE id_groupe=?";
    private static final String SQL_QUERY_SELECT_BY_GROUP      = "select id_param, param.id_action, couleur, ordre, icone, id_groupe,  name, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from ticketing_param_bouton_action param join ticketing_couleur_bouton couleur on couleur.id_couleur = param.id_couleur join workflow_action act on act.id_action = param.id_action where id_groupe=?";
    private static final String SQL_QUERY_SELECT_WITHOUT_GROUP = "SELECT id_action, name, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from workflow_action act where id_action not in (select id_action FROM ticketing_param_bouton_action)";
    private static final String SQL_QUERY_SELECT_ACTION_STATE  = "select name, id_state_before, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from workflow_action where id_action=?";

    private static final int    ID_GROUPE_NON_CONFIGURE        = 1;

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
            int nIndex = 1;
            ParamBouton paramBouton = new ParamBouton( );
            Action action = new Action( );
            State stateBefore = new State( );
            State stateAfter = new State( );

            paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
            action.setName( daoUtil.getString( nIndex++ ) );
            stateBefore.setName( daoUtil.getString( nIndex++ ) );
            stateAfter.setName( daoUtil.getString( nIndex ) );

            action.setStateAfter( stateAfter );
            action.setStateBefore( stateBefore );
            paramBouton.setAction( action );

            listParamBouton.add( paramBouton );
        }

        daoUtil.free( );

        // Récupération des actions affectées au groupe non configuré
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

        Action action = new Action( );
        State stateBefore = new State( );
        State stateAfter = new State( );

        paramBouton.setIdparam( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
        paramBouton.setCouleur( daoUtil.getString( nIndex++ ) );
        paramBouton.setOrdre( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIcone( daoUtil.getString( nIndex++ ) );
        paramBouton.setIdGroupe( daoUtil.getInt( nIndex++ ) );
        action.setName( daoUtil.getString( nIndex++ ) );
        stateBefore.setName( daoUtil.getString( nIndex++ ) );
        stateAfter.setName( daoUtil.getString( nIndex ) );

        action.setStateAfter( stateAfter );
        action.setStateBefore( stateBefore );
        paramBouton.setAction( action );
    }
}
