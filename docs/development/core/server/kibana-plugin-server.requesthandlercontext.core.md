<!-- Do not edit this file. It is automatically generated by API Documenter. -->

[Home](./index.md) &gt; [kibana-plugin-server](./kibana-plugin-server.md) &gt; [RequestHandlerContext](./kibana-plugin-server.requesthandlercontext.md) &gt; [core](./kibana-plugin-server.requesthandlercontext.core.md)

## RequestHandlerContext.core property

<b>Signature:</b>

```typescript
core: {
        rendering: IScopedRenderingClient;
        savedObjects: {
            client: SavedObjectsClientContract;
            typeRegistry: ISavedObjectTypeRegistry;
        };
        elasticsearch: {
            dataClient: IScopedClusterClient;
            adminClient: IScopedClusterClient;
        };
        uiSettings: {
            client: IUiSettingsClient;
        };
    };
```