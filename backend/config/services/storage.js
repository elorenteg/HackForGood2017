"use strict";

const path = require('path');

module.exports = {
  services: {
    storage: {
      provider: {
        uploads: path.resolve(__dirname, '../uploads')
      }
    }
  }
};
