/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.ticketing.service.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;

public abstract class Tree<Node extends AbstractNode, Depth extends AbstractDepth>
{
    protected List<Node>  _rootNodes;

    protected List<Node>  _leaves;

    protected List<Node>  _nodes;

    protected List<Depth> _depths;

    public Tree( Tree<Node, Depth> treeSource )
    {
        setDepths( treeSource.getDepths( ) );
        setLeaves( treeSource.getLeaves( ) );
        setNodes( treeSource.getNodes( ) );
        setRootElements( treeSource.getRootElements( ) );
    }

    /**
     * The main constructor of the tree
     *
     * @param listNode
     *            list node
     * @param listDepth
     *            list depth
     */
    public Tree( List<Node> listNode, List<Depth> listDepth )
    {
        _depths = listDepth;
        for ( AbstractNode Node : listNode )
        {

            // Set the parent
            if ( Node.getIdParent( ) != -1 )
            {
                for ( AbstractNode nodeParent : listNode )
                {
                    if ( nodeParent.getId( ) == Node.getIdParent( ) )
                    {
                        Node.setParent( nodeParent );
                        Node.setDepth( findDepthByDepthNumber( nodeParent.getDepth( ).getDepthNumber( ) + 1 ) );
                        break;
                    }
                }
            } else
            {
                // Set the depth of the node
                Node.setParent( null );
                Node.setDepth( findDepthByDepthNumber( 1 ) );
            }

            // Set the children
            List<AbstractNode> listChildren = new ArrayList<>( );

            listNode.stream( ).filter( ( childNode ) -> ( childNode.getIdParent( ) == Node.getId( ) ) ).forEach( ( childNode ) ->
            {
                listChildren.add( childNode );
            } );
            if ( listChildren.isEmpty( ) )
            {
                Node.setLeaf( true );
            } else
            {
                Node.setLeaf( false );
            }
            Node.setChildren( listChildren );
        }
        _nodes = new ArrayList<Node>( listNode );
        _rootNodes = _nodes.stream( ).filter( node -> ( node.getIdParent( ) == -1 ) ).collect( Collectors.toList( ) );
        _leaves = _nodes.stream( ).filter( node -> ( node.getLeaf( ) == true ) ).collect( Collectors.toList( ) );
        _nodes.stream( ).forEach( ( node ) ->
        {
            node.setLeaves( getLeavesOf( node ) );
        } );
    }

    /**
     * Get the root elements of the tree
     *
     * @return the list of root elements of the tree
     */
    public List<Node> getRootElements( )
    {
        return _rootNodes;
    }

    /**
     * Set the root elements of the tree
     *
     * @param rootNodes
     *            the list of root elements of the tree
     */
    public void setRootElements( List<Node> rootNodes )
    {
        _rootNodes = rootNodes;
    }

    /**
     * Get the list of nodes of the tree
     *
     * @return the list of node of the tree.
     */
    public List<Node> getNodes( )
    {
        return _nodes;
    }

    private void addChildrenToList( Node root, List<Node> list )
    {
        list.add( root );
        root.getChildren( ).stream( ).forEach( node ->
        {
            node.setParent( root );
            addChildrenToList( ( Node ) node, list );
        } );
    }

    public List<Node> getSortedNodes( )
    {
        List<Node> nodes = new ArrayList<>( );

        _rootNodes.stream( ).forEach( root -> addChildrenToList( root, nodes ) );

        return nodes;
    }

    /**
     * Set the list of nodes of the tree
     *
     * @param nodes
     *            the list of nodes of the tree
     */
    public void setNodes( List<Node> nodes )
    {
        _nodes = nodes;
    }

    /**
     * Get the depths java obj of the tree
     *
     * @return depths java obj of the tree
     */
    public List<Depth> getDepths( )
    {
        return _depths;
    }

    /**
     * Set the depths java obj of the tree
     *
     * @param depths
     *            the Depths of the tree
     */
    public void setDepths( List<Depth> depths )
    {
        _depths = depths;
    }

    /**
     * Get the leaves of the tree
     *
     * @return the leaves nodes of the tree
     */
    public List<Node> getLeaves( )
    {
        return _leaves;
    }

    /**
     * Set the leaves node of the tree
     *
     * @param leaves
     *            the leaves node of the tree
     */
    public void setLeaves( List<Node> leaves )
    {
        _leaves = leaves;
    }

    /**
     * Get the leaves nodes of a given node
     *
     * @param node
     * @return the list of leaves node of the given node
     */
    private List<AbstractNode> getLeavesOf( AbstractNode node )
    {
        List<AbstractNode> listLeaves = new ArrayList<>( );
        if ( node.getLeaf( ) )
        {
            listLeaves.add( node );
        } else
        {
            for ( AbstractNode child : node.getChildren( ) )
            {
                listLeaves.addAll( getLeavesOf( child ) );
            }
        }
        return listLeaves;
    }

    /**
     * Get all children nodes of the given node (recursively)
     *
     * @param node
     *            node
     * @param isFirstCall
     *            true if first recursive call
     * @return the list of all the children of the given node (recursively).
     */
    public List<AbstractNode> getAllChildren( AbstractNode node, boolean isFirstCall )
    {
        List<AbstractNode> listAllChildren = new ArrayList<>( );
        if ( !node.getLeaf( ) )
        {
            for ( AbstractNode nodeChild : node.getChildren( ) )
            {
                listAllChildren.addAll( getAllChildren( nodeChild, false ) );
            }
        }
        if ( !isFirstCall )
        {
            listAllChildren.add( node );
        }
        return listAllChildren;
    }

    /**
     * Find a node with given id
     *
     * @param nId
     *            id
     * @return the node of given id.
     */
    public Node findNodeById( int nId )
    {
        for ( Node node : _nodes )
        {
            if ( node.getId( ) == nId )
            {
                return node;
            }
        }
        return null;
    }

    /**
     * Find the Depth obj with given depth number
     *
     * @param nDepth
     *            id depth
     * @return the Depth obj
     */
    public Depth findDepthByDepthNumber( int nDepth )
    {
        for ( Depth depth : _depths )
        {
            if ( depth.getDepthNumber( ) == nDepth )
            {
                return depth;
            }
        }
        // Default depth
        TicketCategoryType defaultDepth = new TicketCategoryType( );
        defaultDepth.setDepthNumber( nDepth );
        defaultDepth.setLabel( StringUtils.EMPTY );
        return ( Depth ) defaultDepth;
    }

    /**
     * Find the Depth obj with given label
     *
     * @param strLabel
     *            label
     * @return the Depth obj
     */
    public Depth findDepthByLabel( String strLabel )
    {
        for ( Depth depth : _depths )
        {
            if ( depth.getLabel( ) == strLabel )
            {
                return depth;
            }
        }
        return null;
    }

    /**
     * Get the max depth of the tree
     *
     * @return the max depth of the tree;
     */
    public int getMaxDepthNumber( )
    {
        int max = -1;
        for ( AbstractDepth depth : _depths )
        {
            if ( depth.getDepthNumber( ) > max )
            {
                max = depth.getDepthNumber( );
            }
        }
        return max;
    }

    /**
     * Get the list of nodes parents of given node ( from root to node)
     *
     * @param node
     *            Node
     * @return the list of nodes parents of given node ( from root to node)
     */
    public List<Node> getBranch( Node node )
    {
        List<Node> listNodes = new ArrayList<>( );
        if ( node != null )
        {
            listNodes.add( node );
            while ( node.getParent( ) != null )
            {
                node = ( Node ) node.getParent( );
                listNodes.add( node );
            }
            Collections.reverse( listNodes );
        }
        return listNodes;
    }

    /**
     * Get the list of nodes of a given depth
     *
     * @param depth
     *            Depth
     * @return the list of nodes of the given depth
     */
    public List<Node> getListNodesOfDepth( Depth depth )
    {
        List<Node> listNodes = new ArrayList<>( );
        for ( Node node : getNodes( ) )
        {
            if ( node.getDepth( ).getDepthNumber( ) == depth.getDepthNumber( ) )
            {
                listNodes.add( node );
            }
        }
        return listNodes;
    }

}
