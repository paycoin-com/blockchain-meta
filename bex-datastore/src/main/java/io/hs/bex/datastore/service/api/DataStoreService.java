package io.hs.bex.datastore.service.api;

import io.hs.bex.datastore.model.DataStoreType;
import io.hs.bex.datastore.model.FileInfo;

public interface DataStoreService
{
    String createDir( String path, boolean createParent );

    String getFileContent( String path, String fileName );

    String saveFile( boolean createDir, String path, String fileName, String jsonData );

    String createDirAsync( String path, boolean createParent );

    String saveFileAsync( boolean createDir, String path, String fileName, String jsonData );

    FileInfo getFileInfo( String path, String fileName );

    String publishNS( String key, String path, String fileName );

    String publishNS( String key, String nsValue );

    String publishNSAsync( String path, String fileName );

    FileInfo getFileInfo( DataStoreType dataStoreType, String path, String fileName );

    String createDir( DataStoreType dataStoreType, String path, boolean createParent );

    String saveFile( DataStoreType dataStoreType, boolean createDir, String path, String fileName, String jsonData );

    String getFileContent( DataStoreType dataStoreType, String path, String fileName );
}
