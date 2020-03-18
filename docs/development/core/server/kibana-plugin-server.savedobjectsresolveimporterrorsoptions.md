<!-- Do not edit this file. It is automatically generated by API Documenter. -->

[Home](./index.md) &gt; [kibana-plugin-server](./kibana-plugin-server.md) &gt; [SavedObjectsResolveImportErrorsOptions](./kibana-plugin-server.savedobjectsresolveimporterrorsoptions.md)

## SavedObjectsResolveImportErrorsOptions interface

Options to control the "resolve import" operation.

<b>Signature:</b>

```typescript
export interface SavedObjectsResolveImportErrorsOptions 
```

## Properties

|  Property | Type | Description |
|  --- | --- | --- |
|  [namespace](./kibana-plugin-server.savedobjectsresolveimporterrorsoptions.namespace.md) | <code>string</code> | if specified, will import in given namespace |
|  [objectLimit](./kibana-plugin-server.savedobjectsresolveimporterrorsoptions.objectlimit.md) | <code>number</code> | The maximum number of object to import |
|  [readStream](./kibana-plugin-server.savedobjectsresolveimporterrorsoptions.readstream.md) | <code>Readable</code> | The stream of [saved objects](./kibana-plugin-server.savedobject.md) to resolve errors from |
|  [retries](./kibana-plugin-server.savedobjectsresolveimporterrorsoptions.retries.md) | <code>SavedObjectsImportRetry[]</code> | saved object import references to retry |
|  [savedObjectsClient](./kibana-plugin-server.savedobjectsresolveimporterrorsoptions.savedobjectsclient.md) | <code>SavedObjectsClientContract</code> | client to use to perform the import operation |
|  [supportedTypes](./kibana-plugin-server.savedobjectsresolveimporterrorsoptions.supportedtypes.md) | <code>string[]</code> | the list of allowed types to import |
