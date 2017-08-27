'use strict';

import rest from 'rest' ;
import defaultRequest from 'rest/interceptor/defaultRequest';
import baseRegistry from 'rest/mime/registry';

const registry = baseRegistry.child();

registry.register('application/json', require('rest/mime/type/application/json'));

const RestClient = rest
    .wrap(defaultRequest, {headers: {'Accept': 'application/json', 'Content-Type': 'application/json'}});

let request = function(method, path, successCallback, failureCallback, entity) {
    RestClient({
        path: path,
        method: method,
        entity: JSON.stringify(entity)
    }).then(function (response) {
        if(response.status.code === 200) {
            successCallback(response);
        } else {
            failureCallback(response);
        }
    });
};

export {request}