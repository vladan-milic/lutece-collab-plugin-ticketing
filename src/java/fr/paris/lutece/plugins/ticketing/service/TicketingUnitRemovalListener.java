package fr.paris.lutece.plugins.ticketing.service;

import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.plugins.unittree.service.UnitErrorException;
import fr.paris.lutece.plugins.unittree.service.unit.IUnitRemovalListener;

public class TicketingUnitRemovalListener implements IUnitRemovalListener
{
    private static final String MESSAGE_UNIT_NO_PARRENT_ERROR = "ticketing.removal.unit.listener.error.parent";

    /**
     * {@inheritDoc}
     */
    @Override
    public void notify( int nIdRemovedUnit ) throws UnitErrorException
    {
        Unit unit = UnitHome.findByPrimaryKey( nIdRemovedUnit );
        Unit unitParent = UnitHome.findByPrimaryKey( unit.getIdParent( ) );
        AssigneeUnit assignUnit = new AssigneeUnit( unitParent );

        if ( unit.getIdParent( ) != -1 )
        {
            List<Ticket> listTicket = TicketHome.findByUnitId( nIdRemovedUnit );

            for ( Ticket ticket : listTicket )
            {
                ticket.setAssigneeUnit( assignUnit );
                TicketHome.update( ticket );
            }

        }
        else
        {
            throw new UnitErrorException( MESSAGE_UNIT_NO_PARRENT_ERROR );
        }
    }

}
