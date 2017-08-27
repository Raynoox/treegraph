'use strict';

import rest from 'rest' ;
import defaultRequest from 'rest/interceptor/defaultRequest';
import mime from 'rest/interceptor/mime';
import errorCode from 'rest/interceptor/errorCode';
import baseRegistry from 'rest/mime/registry';

var registry = baseRegistry.child();

registry.register('application/json', require('rest/mime/type/application/json'));

var RestClient = rest
    // .wrap(mime, { registry: registry })
    // .wrap(errorCode)
    .wrap(defaultRequest, { headers: { 'Accept': 'application/json', 'Content-Type': 'application/json'}});

var request = function(method, path, successCallback, failureCallback, entity) {
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
}

export {request}