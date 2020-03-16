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

/**
 * @name Vis
 *
 * @description This class consists of aggs, params, listeners, title, and type.
 *  - Aggs: Instances of IAggConfig.
 *  - Params: The settings in the Options tab.
 *
 * Not to be confused with vislib/vis.js.
 */

import { EventEmitter } from 'events';
import _ from 'lodash';
import { PersistedState } from '../../../../../../../src/plugins/visualizations/public';
import { updateVisualizationConfig } from './legacy/vis_update';
import { getTypes, getAggs } from './services';

class VisImpl extends EventEmitter {
  constructor(indexPattern, visState) {
    super();
    visState = visState || {};

    if (_.isString(visState)) {
      visState = {
        type: visState,
      };
    }

    this.indexPattern = indexPattern;
    this._setUiState(new PersistedState());
    this.setCurrentState(visState);
    this.setState(this.getCurrentState(), false);

    // Session state is for storing information that is transitory, and will not be saved with the visualization.
    // For instance, map bounds, which depends on the view port, browser window size, etc.
    this.sessionState = {};

    this.API = {
      events: {
        filter: data => this.eventsSubject.next({ name: 'filterBucket', data }),
        brush: data => this.eventsSubject.next({ name: 'brush', data }),
      },
    };
  }

  initializeDefaultsFromSchemas(configStates, schemas) {
    // Set the defaults for any schema which has them. If the defaults
    // for some reason has more then the max only set the max number
    // of defaults (not sure why a someone define more...
    // but whatever). Also if a schema.name is already set then don't
    // set anything.
    const newConfigs = [...configStates];
    schemas
      .filter(schema => Array.isArray(schema.defaults) && schema.defaults.length > 0)
      .filter(schema => !configStates.find(agg => agg.schema && agg.schema === schema.name))
      .forEach(schema => {
        const defaults = schema.defaults.slice(0, schema.max);
        defaults.forEach(d => newConfigs.push(d));
      });
    return newConfigs;
  }

  setCurrentState(state) {
    this.title = state.title || '';
    const type = state.type || this.type;
    if (_.isString(type)) {
      this.type = getTypes().get(type);
      if (!this.type) {
        throw new Error(`Invalid type "${type}"`);
      }
    } else {
      this.type = type;
    }

    this.params = _.defaults(
      {},
      _.cloneDeep(state.params || {}),
      _.cloneDeep(this.type.visConfig.defaults || {})
    );

    updateVisualizationConfig(state.params, this.params);

    if (state.aggs || !this.aggs) {
      let configStates = state.aggs ? state.aggs.aggs || state.aggs : [];
      configStates = this.initializeDefaultsFromSchemas(configStates, this.type.schemas.all || []);
      this.aggs = getAggs().createAggConfigs(this.indexPattern, configStates);
    }
  }

  setState(state, updateCurrentState = true) {
    this._state = _.cloneDeep(state);
    if (updateCurrentState) {
      this.setCurrentState(this._state);
    }
  }

  setVisType(type) {
    this.type.type = type;
  }

  updateState() {
    this.setState(this.getCurrentState(true));
    this.emit('update');
  }

  forceReload() {
    this.emit('reload');
  }

  getCurrentState(includeDisabled) {
    return {
      title: this.title,
      type: this.type.name,
      params: _.cloneDeep(this.params),
      aggs: this.aggs.aggs
        .map(agg => agg.toJSON())
        .filter(agg => includeDisabled || agg.enabled)
        .filter(Boolean),
    };
  }

  copyCurrentState(includeDisabled = false) {
    const state = this.getCurrentState(includeDisabled);
    state.aggs = getAggs().createAggConfigs(
      this.indexPattern,
      state.aggs.aggs || state.aggs,
      this.type.schemas.all
    );
    return state;
  }

  getStateInternal(includeDisabled) {
    return {
      title: this._state.title,
      type: this._state.type,
      params: this._state.params,
      aggs: this._state.aggs.filter(agg => includeDisabled || agg.enabled),
    };
  }

  getEnabledState() {
    return this.getStateInternal(false);
  }

  getAggConfig() {
    return this.aggs.clone({ enabledOnly: true });
  }

  getState() {
    return this.getStateInternal(true);
  }

  isHierarchical() {
    if (_.isFunction(this.type.hierarchicalData)) {
      return !!this.type.hierarchicalData(this);
    } else {
      return !!this.type.hierarchicalData;
    }
  }

  hasSchemaAgg(schemaName, aggTypeName) {
    const aggs = this.aggs.bySchemaName(schemaName) || [];
    return aggs.some(function(agg) {
      if (!agg.type || !agg.type.name) return false;
      return agg.type.name === aggTypeName;
    });
  }

  hasUiState() {
    return !!this.__uiState;
  }

  /***
   * this should not be used outside of visualize
   * @param uiState
   * @private
   */
  _setUiState(uiState) {
    if (uiState instanceof PersistedState) {
      this.__uiState = uiState;
    }
  }

  getUiState() {
    return this.__uiState;
  }

  /**
   * Currently this is only used to extract map-specific information
   * (e.g. mapZoom, mapCenter).
   */
  uiStateVal(key, val) {
    if (this.hasUiState()) {
      if (_.isUndefined(val)) {
        return this.__uiState.get(key);
      }
      return this.__uiState.set(key, val);
    }
    return val;
  }
}

VisImpl.prototype.type = 'histogram';

export { VisImpl };