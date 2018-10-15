package io.hs.bex.blocknode.service.api;

import io.hs.bex.blocknode.model.Node;
import io.hs.bex.blocknode.model.NodeProvider;
import io.hs.bex.blocknode.model.NodeStatus;

public interface GenericNodeHandler
{
    Node init( NodeProvider nodeProvider );

    Node init( NodeProvider nodeProvider , boolean fullVerificationMode );
    
    Node start() throws Exception;

    NodeStatus getStatus();

    Node stop();

    Node getNode();

}
