/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

import React, { useState } from 'react';
import {
  EuiButton,
  EuiInMemoryTable,
  EuiLink,
  Query,
  EuiLoadingSpinner,
  EuiToolTip,
  EuiButtonIcon,
} from '@elastic/eui';

export const TasksHome: React.FunctionComponent<Props> = ({}) => {
  const {
    core: { i18n },
  } = useAppDependencies();
  const { FormattedMessage } = i18n;
  const { trackUiMetric } = uiMetricService;

  const columns = [
    {
      field: 'tasks',
      name: i18n.translate('xpack.tasksList.table.tasksColumnTitle', {
        defaultMessage: 'Task ID',
      }),
      truncateText: true,
      sortable: true,
    },
  ];

  return <EuiInMemoryTable columns={columns} />;
};
