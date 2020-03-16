<!-- Do not edit this file. It is automatically generated by API Documenter. -->

[Home](./index.md) &gt; [kibana-plugin-core-server](./kibana-plugin-core-server.md) &gt; [SavedObjectsRepository](./kibana-plugin-core-server.savedobjectsrepository.md)

## SavedObjectsRepository class


<b>Signature:</b>

```typescript
export declare class SavedObjectsRepository 
```

## Methods

|  Method | Modifiers | Description |
|  --- | --- | --- |
|  [bulkCreate(objects, options)](./kibana-plugin-core-server.savedobjectsrepository.bulkcreate.md) |  | Creates multiple documents at once |
|  [bulkGet(objects, options)](./kibana-plugin-core-server.savedobjectsrepository.bulkget.md) |  | Returns an array of objects by id |
|  [bulkUpdate(objects, options)](./kibana-plugin-core-server.savedobjectsrepository.bulkupdate.md) |  | Updates multiple objects in bulk |
|  [create(type, attributes, options)](./kibana-plugin-core-server.savedobjectsrepository.create.md) |  | Persists an object |
|  [delete(type, id, options)](./kibana-plugin-core-server.savedobjectsrepository.delete.md) |  | Deletes an object |
|  [deleteByNamespace(namespace, options)](./kibana-plugin-core-server.savedobjectsrepository.deletebynamespace.md) |  | Deletes all objects from the provided namespace. |
|  [find({ search, defaultSearchOperator, searchFields, hasReference, page, perPage, sortField, sortOrder, fields, namespace, type, filter, })](./kibana-plugin-core-server.savedobjectsrepository.find.md) |  |  |
|  [get(type, id, options)](./kibana-plugin-core-server.savedobjectsrepository.get.md) |  | Gets a single object |
|  [incrementCounter(type, id, counterFieldName, options)](./kibana-plugin-core-server.savedobjectsrepository.incrementcounter.md) |  | Increases a counter field by one. Creates the document if one doesn't exist for the given id. |
|  [update(type, id, attributes, options)](./kibana-plugin-core-server.savedobjectsrepository.update.md) |  | Updates an object |
