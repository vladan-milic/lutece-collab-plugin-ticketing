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
    private static final String SQL_QUERY_SELECT               = "SELECT id_param, id_action, id_couleur, ordre, icone, id_groupe FROM ticketing_param_bouton_action where id_param=?";
    private static final String SQL_QUERY_SELECT_ORDER         = "select IFNULL(max(ordre)+1,1) from ticketing_param_bouton_action where id_groupe=?";
    private static final String SQL_QUERY_INSERT               = "INSERT INTO ticketing_param_bouton_action (id_action, id_couleur, ordre, icone, id_groupe) VALUES( ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE               = "UPDATE ticketing_groupe_action SET libelle_identifiant=?, cle=?, description=?, ordre=? WHERE id_groupe=?";
    private static final String SQL_QUERY_UPDATE_GROUP         = "UPDATE ticketing_param_bouton_action SET ordre=?, id_groupe=? WHERE id_param=?";
    private static final String SQL_QUERY_SELECT_BY_GROUP      = "select id_param, param.id_action, couleur, ordre, icone, id_groupe,  name, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from ticketing_param_bouton_action param join ticketing_couleur_bouton couleur on couleur.id_couleur = param.id_couleur join workflow_action act on act.id_action = param.id_action where id_groupe=?";
    private static final String SQL_QUERY_SELECT_WITHOUT_GROUP = "SELECT id_action, name, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from workflow_action act where id_action not in (select id_action FROM ticketing_param_bouton_action)";
    private static final String SQL_QUERY_SELECT_ACTION_STATE  = "select name, id_state_before, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from workflow_action where id_action=?";

    private static final int    ID_GROUPE_NON_CONFIGURE        = 1;
    private static final String DEFAULT_COLOR                  = "Bleu foncé";
    private static final String DEFAULT_ICONE                  = "fa fa-question-circle-o";

    private int getNewOrderGroup( int idGroupe, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ORDER, plugin );
        daoUtil.setInt( 1, idGroupe );
        daoUtil.executeQuery( );
        int newOrder = 1;

        if ( daoUtil.next( ) )
        {
            newOrder = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return newOrder;
    }

    @Override
    public void insert( ParamBouton paramBouton, Plugin plugin )
    {
        paramBouton.setOrdre( getNewOrderGroup( paramBouton.getIdGroupe( ), plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, paramBouton.getIdAction( ) );
        daoUtil.setString( nIndex++, paramBouton.getIdCouleur( ) );
        daoUtil.setInt( nIndex++, paramBouton.getOrdre( ) );
        daoUtil.setString( nIndex++, paramBouton.getIcone( ) );
        daoUtil.setInt( nIndex, paramBouton.getIdGroupe( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
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
            paramBouton.setIcone( DEFAULT_ICONE );
            paramBouton.setCouleur( DEFAULT_COLOR );

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

    @Override
    public ParamBouton load( int nKey, Plugin plugin )
    {
        ParamBouton paramBouton = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );

        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            paramBouton = new ParamBouton( );
            paramBouton.setIdparam( daoUtil.getInt( nIndex++ ) );
            paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
            paramBouton.setCouleur( daoUtil.getString( nIndex++ ) );
            paramBouton.setOrdre( daoUtil.getInt( nIndex++ ) );
            paramBouton.setIcone( daoUtil.getString( nIndex++ ) );
            paramBouton.setIdGroupe( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free( );

        return paramBouton;
    }

    @Override
    public void updateGroup( ParamBouton paramBouton, Plugin plugin )
    {
        paramBouton.setOrdre( getNewOrderGroup( paramBouton.getIdGroupe( ), plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_GROUP, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, paramBouton.getOrdre( ) );
        daoUtil.setInt( nIndex++, paramBouton.getIdGroupe( ) );
        daoUtil.setInt( nIndex, paramBouton.getIdparam( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
