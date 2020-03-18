/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

import React from 'react';
import { render, unmountComponentAtNode } from 'react-dom';
import { HashRouter } from 'react-router-dom';

import { AppDependencies } from './app_context';
import { AppProviders } from './app_providers';
// @ts-ignore
import { App } from './app.container';

const AppWithRouter = (props: { [key: string]: any }) => (
  <HashRouter>
    <App {...props} />
  </HashRouter>
);

export const renderApp = (element: Element, dependencies: AppDependencies) => {
  render(
    <AppProviders appDependencies={dependencies}>
      <AppWithRouter telemetry={dependencies.plugins.telemetry} />
    </AppProviders>,
    element
  );

  return () => {
    unmountComponentAtNode(element);
  };
};

export { AppDependencies };