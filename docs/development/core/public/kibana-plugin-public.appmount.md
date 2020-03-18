<!-- Do not edit this file. It is automatically generated by API Documenter. -->

[Home](./index.md) &gt; [kibana-plugin-public](./kibana-plugin-public.md) &gt; [AppMount](./kibana-plugin-public.appmount.md)

## AppMount type

A mount function called when the user navigates to this app's route.

<b>Signature:</b>

```typescript
export declare type AppMount<HistoryLocationState = unknown> = (params: AppMountParameters<HistoryLocationState>) => AppUnmount | Promise<AppUnmount>;
```