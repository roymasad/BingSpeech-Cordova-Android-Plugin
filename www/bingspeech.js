
var exec = require('cordova/exec');

var PLUGIN_NAME = 'BingSpeechPlugin';

var BingSpeechPlugin =  function() {

  this.oninit = null;
  this.onstart = null;
  this.onstop = null;
  this.onerror = null;

};

BingSpeechPlugin.prototype.init = function(arg) {

  var that = this;

  var successCallback = function(event) {
    that.oninit(event);
  }

  var errorCallback = function(err) {
    that.onerror(err);
  };


  exec(successCallback, errorCallback, PLUGIN_NAME, 'init', [arg]);
};

BingSpeechPlugin.prototype.start = function(arg) {

  var that = this;

  var successCallback = function(event) {
    that.onstart(event);
  }

  var errorCallback = function(err) {
    that.onerror(err);
  };


  exec(successCallback, errorCallback, PLUGIN_NAME, 'start', [arg]);
};

BingSpeechPlugin.prototype.stop = function(arg) {

  var that = this;

  var successCallback = function(event) {
    that.onstop(event);
  }

  var errorCallback = function(err) {
    that.onerror(err);
  };


  exec(successCallback, errorCallback, PLUGIN_NAME, 'stop', [arg]);
};


module.exports = BingSpeechPlugin;
