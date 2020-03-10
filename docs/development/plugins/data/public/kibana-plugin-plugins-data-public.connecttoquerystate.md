<!-- Do not edit this file. It is automatically generated by API Documenter. -->

[Home](./index.md) &gt; [kibana-plugin-plugins-data-public](./kibana-plugin-plugins-data-public.md) &gt; [connectToQueryState](./kibana-plugin-plugins-data-public.connecttoquerystate.md)

## connectToQueryState variable

Helper to setup two-way syncing of global data and a state container

<b>Signature:</b>

```typescript
connectToQueryState: <S extends QueryState>({ timefilter: { timefilter }, filterManager, state$, }: Pick<{
    filterManager: import("..").FilterManager;
    timefilter: import("..").TimefilterSetup;
    state$: import("rxjs").Observable<{
        changes: QueryStateChange;
        state: QueryState;
    }>;
    savedQueries: import("..").SavedQueryService;
} | {
    filterManager: import("..").FilterManager;
    timefilter: import("..").TimefilterSetup;
    state$: import("rxjs").Observable<{
        changes: QueryStateChange;
        state: QueryState;
    }>;
}, "state$" | "timefilter" | "filterManager">, stateContainer: BaseStateContainer<S>, syncConfig: {
    time?: boolean | undefined;
    refreshInterval?: boolean | undefined;
    filters?: boolean | FilterStateStore | undefined;
}) => () => void
```