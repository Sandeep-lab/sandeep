/*
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { SavedObjectsManagementActionRegistry } from './action_registry';
import { SavedObjectsManagementAction } from './action_types';

class DummyAction extends SavedObjectsManagementAction {
  constructor(public id: string) {
    super();
  }

  public euiAction = {
    name: 'name',
    description: 'description',
    icon: 'icon',
    type: 'type',
  };

  public render = () => '';
}

describe('SavedObjectsManagementActionRegistry', () => {
  let registry: SavedObjectsManagementActionRegistry;

  const createAction = (id: string): SavedObjectsManagementAction => {
    return new DummyAction(id);
  };

  beforeEach(() => {
    registry = new SavedObjectsManagementActionRegistry();
  });

  describe('#register', () => {
    it('allows actions to be registered and retrieved', () => {
      const action = createAction('foo');
      registry.register(action);
      expect(registry.getAll()).toContain(action);
    });

    it('does not allow actions with duplicate ids to be registered', () => {
      const action = createAction('my-action');
      registry.register(action);
      expect(() => registry.register(action)).toThrowErrorMatchingInlineSnapshot(
        `"Saved Objects Management Action with id 'my-action' already exists"`
      );
    });
  });

  describe('#has', () => {
    it('returns true when an action with a matching ID exists', () => {
      const action = createAction('existing-action');
      registry.register(action);
      expect(registry.has('existing-action')).toEqual(true);
    });

    it(`returns false when an action doesn't exist`, () => {
      expect(registry.has('missing-action')).toEqual(false);
    });
  });
});