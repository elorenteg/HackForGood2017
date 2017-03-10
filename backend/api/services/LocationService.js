"use strict";

const location = require('sails-service-location');
const config = require('../../config/services/location');

module.exports = location('OpenStreetMap', config.services.location);
