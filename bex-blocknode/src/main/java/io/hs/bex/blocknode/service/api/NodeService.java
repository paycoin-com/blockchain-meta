package io.hs.bex.blocknode.service.api;

import java.util.List;

import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeProvider;
import io.hs.bex.blocknode.model.NodeStatus;

public interface NodeService
{
    List<Node> getNodes();

    Node startNode( int nodeId );

    Node stopNode( int nodeId );

    Node getNode( int nodeId );

    NodeStatus getNodeStatus( int nodeId );

    Node getNode( NodeProvider nodeProvider );

}
