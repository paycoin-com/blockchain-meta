package io.hs.bex.datastore.service.api;

import java.io.IOException;

import io.hs.bex.datastore.model.FileInfo;

public interface DataStoreHandler
{
    String createDir( String pathStr, boolean createParent ) throws IOException;

    String getFileContent( String pathStr, String fileName ) throws IOException;

    String saveFile( boolean createDir, String pathStr, String fileName, String jsonData ) throws IOException;

    FileInfo getFileInfo( String pathStr, String fileName );

    String publishNS( String nsValue );

    String publishNS( String pathStr, String fileName );
}
