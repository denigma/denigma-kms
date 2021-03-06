// Generated by CoffeeScript 1.6.3
/*
# MockSocket class #
Mock socket is needed for tests to simulate websocket behaviour
*/


(function() {
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; },
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  Batman.MockSocket = (function(_super) {
    __extends(MockSocket, _super);

    /*
    #Mock socket#
    Mock socket is needed for tests to simulate websocket behaviour
    */


    function MockSocket(url) {
      this.url = url;
      this.randomInt = __bind(this.randomInt, this);
      this.onclose = __bind(this.onclose, this);
      this.send = __bind(this.send, this);
      MockSocket.__super__.constructor.apply(this, arguments);
      this.onreceive = Batman.MockSocket.mockCallback(this);
      MockSocket.instance = this;
      this.onopen();
    }

    MockSocket.getInstance = function(url) {
      if (url == null) {
        url = "none";
      }
      if (Batman.MockSocket.instance != null) {
        return Batman.MockSocket.instance;
      } else {
        return new Batman.MockSocket(url);
      }
    };

    MockSocket.prototype.isMock = true;

    MockSocket.prototype.onreceive = function(event) {
      return event;
    };

    MockSocket.prototype.send = function(event) {
      return this.onreceive(event);
    };

    MockSocket.prototype.onopen = function() {
      /*
      Open event
      */

      return console.log("open");
    };

    MockSocket.prototype.onmessage = function(event) {
      /*
      On message
      */

      var data;
      return data = event.data;
    };

    MockSocket.prototype.onclose = function() {
      return console.log("close");
    };

    MockSocket.prototype.randomInt = function(min, max) {
      /*
        random int generating function
      */

      return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    MockSocket.mockCallback = function(mock) {
      /*
        this callback is needed to store data inside mock sockets and respond to read requests and other queirs
        NAPILNIK
      */

      return function(event) {
        var all, col, content, data, id, message, res;
        data = Batman.SocketEvent.fromString(event);
        switch (data.request) {
          case "save":
            /*
              if we received request to save something we answer to it with result
            */

            data.request = "push";
            if (data.content.id != null) {
              id = data.content.id;
              /*
                if we were give id we just give item with appropriate id
              */

              mock.set(id, data);
              all = mock.getOrSet(data.channel, function() {
                return new Batman.SimpleSet();
              });
              res = all.find(function(item) {
                return item.id === id;
              });
              if (res != null) {
                all.remove(res);
              }
              all.add(data.content);
              return mock.onmessage(data);
            }
            break;
          case "delete":
            if (data.content.id != null) {
              id = data.content.id;
              mock.unset(id);
              all = mock.getOrSet(data.channel, function() {
                return new Batman.SimpleSet();
              });
              res = all.find(function(item) {
                return item.id === id;
              });
              if (res != null) {
                return all.remove(res);
              }
            }
            break;
          case "read":
            if (data.content.id != null) {
              data = mock.get(data.content.id);
              data.request = "answer";
              return mock.onmessage(data);
            } else {
              if ((data.content.query != null) && data.content.query === "all") {
                col = mock.get(data.channel);
                if ((col != null) && col.length > 0) {
                  content = col.toArray();
                  message = new Batman.SocketEvent(content, data.channel, "readAll");
                  return mock.onmessage(message);
                } else {
                  return mock.onmessage(new Batman.SocketEvent("_nil_", data.channel, "readAll"));
                }
              }
            }
        }
      };
    };

    return MockSocket;

  }).call(this, Batman.Object);

}).call(this);

/*
//@ sourceMappingURL=mock_socket.map
*/
