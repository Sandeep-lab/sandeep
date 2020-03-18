<!-- Do not edit this file. It is automatically generated by API Documenter. -->

[Home](./index.md) &gt; [kibana-plugin-server](./kibana-plugin-server.md) &gt; [SavedObjectsImportOptions](./kibana-plugin-server.savedobjectsimportoptions.md)

## SavedObjectsImportOptions interface

Options to control the import operation.

<b>Signature:</b>

```typescript
export interface SavedObjectsImportOptions 
```

## Properties

|  Property | Type | Description |
|  --- | --- | --- |
|  [namespace](./kibana-plugin-server.savedobjectsimportoptions.namespace.md) | <code>string</code> | if specified, will import in given namespace, else will import as global object |
|  [objectLimit](./kibana-plugin-server.savedobjectsimportoptions.objectlimit.md) | <code>number</code> | The maximum number of object to import |
|  [overwrite](./kibana-plugin-server.savedobjectsimportoptions.overwrite.md) | <code>boolean</code> | if true, will override existing object if present |
|  [readStream](./kibana-plugin-server.savedobjectsimportoptions.readstream.md) | <code>Readable</code> | The stream of [saved objects](./kibana-plugin-server.savedobject.md) to import |
|  [savedObjectsClient](./kibana-plugin-server.savedobjectsimportoptions.savedobjectsclient.md) | <code>SavedObjectsClientContract</code> | [client](./kibana-plugin-server.savedobjectsclientcontract.md) to use to perform the import operation |
|  [supportedTypes](./kibana-plugin-server.savedobjectsimportoptions.supportedtypes.md) | <code>string[]</code> | the list of allowed types to import |
