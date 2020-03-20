<!-- Do not edit this file. It is automatically generated by API Documenter. -->

[Home](./index.md) &gt; [kibana-plugin-core-public](./kibana-plugin-core-public.md) &gt; [App](./kibana-plugin-core-public.app.md)

## App interface

Extension of [common app properties](./kibana-plugin-core-public.appbase.md) with the mount function.

<b>Signature:</b>

```typescript
export interface App<HistoryLocationState = unknown> extends AppBase 
```

## Properties

|  Property | Type | Description |
|  --- | --- | --- |
|  [appRoute](./kibana-plugin-core-public.app.approute.md) | <code>string</code> | Override the application's routing path from <code>/app/${id}</code>. Must be unique across registered applications. Should not include the base path from HTTP. |
|  [chromeless](./kibana-plugin-core-public.app.chromeless.md) | <code>boolean</code> | Hide the UI chrome when the application is mounted. Defaults to <code>false</code>. Takes precedence over chrome service visibility settings. |
|  [mount](./kibana-plugin-core-public.app.mount.md) | <code>AppMount&lt;HistoryLocationState&gt; &#124; AppMountDeprecated&lt;HistoryLocationState&gt;</code> | A mount function called when the user navigates to this app's route. May have signature of [AppMount](./kibana-plugin-core-public.appmount.md) or [AppMountDeprecated](./kibana-plugin-core-public.appmountdeprecated.md)<!-- -->. |
