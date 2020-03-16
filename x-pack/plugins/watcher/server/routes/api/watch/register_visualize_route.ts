/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

import { schema } from '@kbn/config-schema';
import { IScopedClusterClient } from 'kibana/server';
import { isEsError } from '../../../lib/is_es_error';
import { RouteDependencies } from '../../../types';
import { licensePreRoutingFactory } from '../../../lib/license_pre_routing_factory';

// @ts-ignore
import { Watch } from '../../../models/watch/index';
// @ts-ignore
import { VisualizeOptions } from '../../../models/visualize_options/index';

const bodySchema = schema.object({
  watch: schema.object({}, { allowUnknowns: true }),
  options: schema.object({}, { allowUnknowns: true }),
});

function fetchVisualizeData(dataClient: IScopedClusterClient, index: any, body: any) {
  const params = {
    index,
    body,
    ignoreUnavailable: true,
    allowNoIndices: true,
    ignore: [404],
  };

  return dataClient.callAsCurrentUser('search', params);
}

export function registerVisualizeRoute(deps: RouteDependencies) {
  deps.router.post(
    {
      path: '/api/watcher/watch/visualize',
      validate: {
        body: bodySchema,
      },
    },
    licensePreRoutingFactory(deps, async (ctx, request, response) => {
      const watch = Watch.fromDownstreamJson(request.body.watch);
      const options = VisualizeOptions.fromDownstreamJson(request.body.options);
      const body = watch.getVisualizeQuery(options);

      try {
        const hits = await fetchVisualizeData(ctx.watcher!.client, watch.index, body);
        const visualizeData = watch.formatVisualizeData(hits);

        return response.ok({
          body: {
            visualizeData,
          },
        });
      } catch (e) {
        // Case: Error from Elasticsearch JS client
        if (isEsError(e)) {
          return response.customError({ statusCode: e.statusCode, body: e });
        }

        // Case: default
        return response.internalError({ body: e });
      }
    })
  );
}