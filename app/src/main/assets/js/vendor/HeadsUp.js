/*
* HeadsUp 1.5.6
* @author Kyle Foster (@hkfoster)
* @license MIT
*/
;(function( window, document, undefined ) {

  'use strict';

  // Extend function
  function extend( a, b ) {
    for( var key in b ) {
      if( b.hasOwnProperty( key ) ) {
        a[ key ] = b[ key ];
      }
    }
    return a;
  }

  // Throttle function (http://bit.ly/1eJxOqL)
  function throttle( fn, threshhold, scope ) {
    threshhold || ( threshhold = 250 );
    var previous, deferTimer;
    return function () {
      var context = scope || this,
          current = Date.now(),
          args    = arguments;
      if ( previous && current < previous + threshhold ) {
        clearTimeout( deferTimer );
        deferTimer = setTimeout( function () {
        previous   = current;
        fn.apply( context, args );
        }, threshhold );
      } else {
        previous = current;
        fn.apply( context, args );
      }
    };
  }

  // Class management functions
  function classReg( className ) {
    return new RegExp( '(^|\\s+)' + className + '(\\s+|$)' );
  }

  function hasClass( el, cl ) {
    return classReg( cl ).test( el.className );
  }

  function addClass( el, cl ) {
    if ( !hasClass( el, cl ) ) {
      el.className = el.className + ' ' + cl;
    }
  }

  function removeClass( el, cl ) {
    el.className = el.className.replace( classReg( cl ), ' ' );
  }

  // Main function definition
  function headsUp( selector, options ) {
    this.selector = document.querySelector( selector );
    this.options  = extend( this.defaults, options );
    this.init();
  }

  // Overridable defaults
  headsUp.prototype = {
    defaults : {
      delay       : 300,
      sensitivity : 20
    },

    // Init function
    init : function( selector ) {

      var self         = this,
          options      = self.options,
          selector     = self.selector,
          oldScrollY   = 0, 
          winHeight;

      // Resize handler function
      function resizeHandler() {
        winHeight = window.innerHeight;
        return winHeight;
      }

      // Scroll handler function
      function scrollHandler() {

        // Scoped variables
        var newScrollY = window.pageYOffset,
            docHeight  = document.body.scrollHeight,
            pastDelay  = newScrollY > options.delay,
            goingDown  = newScrollY > oldScrollY,
            fastEnough = newScrollY < oldScrollY - options.sensitivity,
            rockBottom = newScrollY < 0 || newScrollY + winHeight >= docHeight;

        // Where the magic happens
        if ( pastDelay && goingDown ) {
          addClass( selector, 'heads-up' );
        } else if ( !goingDown && fastEnough && !rockBottom || !pastDelay ) {
          removeClass( selector, 'heads-up' );
        }

        // Keep on keeping on
        oldScrollY = newScrollY;
      }

      // Attach listeners
      if ( selector ) {
        
        // Trigger initial resize
        resizeHandler();

        // Resize function listener
        window.addEventListener( 'resize', throttle( resizeHandler ), false );

        // Scroll function listener
        window.addEventListener( 'scroll', throttle( scrollHandler, 100 ), false );
      }
    }
  };

  window.headsUp = headsUp;

})( window, document );

// Instantiate HeadsUp
new headsUp( '#toolbar' );