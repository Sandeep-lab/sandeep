/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

import React from 'react';
import { EuiInMemoryTable } from '@elastic/eui';
import { useCore } from '../../app_context';

export const TaskList: React.FunctionComponent = () => {
  const { i18n } = useCore();

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
