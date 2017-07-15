/**
 * Created by stylejy on 15/07/2017.
 */
if ( typeof at == "undefined" ) { at=new Object(); }
if ( typeof at.quelltextlich == "undefined" ) { at.quelltextlich=new Object(); }
at.quelltextlich.osm={



    // Version information of this script
    VERSION_NUMBER: "0.0.25",
    RELEASE_DATE: "2015-09-06",
    HOMEPAGE: "http://osm.quelltextlich.at/",
    LEGAL_INFO: "http://quelltextlich.at/impressum.html",



    // Adds the required JavaScript tags to the document head
    addJavaScript: function ( onLoadFunc )
    {
        var jsNode = document.createElement('script');
        jsNode.setAttribute('type', 'text/javascript');
        //jsNode.setAttribute('src', 'http://openlayers.org/api/OpenLayers.js');
        jsNode.setAttribute('src', 'http://openlayers.org/api/2.11/OpenLayers.js');
        if ( onLoadFunc )
        {
            jsNode.onreadystatechange= function () {
                if ( (this.readyState == 'loaded') || (this.readyState == 'complete') )
                {
                    onLoadFunc();
                }
            }
            jsNode.onload=onLoadFunc;
        }
        document.getElementsByTagName('head')[0].appendChild(jsNode);
    },



    // Adds the CSS Stylesheet href to the document head
    addStyleSheet: function ( href )
    {
        var cssNode = document.createElement('link');
        cssNode.setAttribute('rel', 'stylesheet');
        cssNode.setAttribute('type', 'text/css');
        cssNode.setAttribute('href', href);
        document.getElementsByTagName('head')[0].appendChild(cssNode);
    },



    // Yields the given text as valid, escaped xml
    encodeAsXml: function ( text )
    {
        return text.replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&apos;');
    },



    // Gets the width and height of the viewport
    getViewPortDimension: function ( )
    {
        var width;
        var height;

        try {
            width = window.innerWidth;
            height = window.innerHeight;
        } catch (e) { ; }

        if ( ( typeof width == "undefined" ) || ( width <= 0 ) )
        {
            try {
                width = document.documentElement.clientWidth;
                height = document.documentElement.clientHeight;
            } catch (e) { ; }
        }

        if ( ( typeof width == "undefined" ) || ( width <= 0 ) )
        {
            try {
                width = document.body.clientHeight;
                height = document.body.clientHeight;
            } catch (e) { ; }
        }

        if ( ( typeof width == "undefined" ) || ( width <= 0 ) )
        {
            width = 320;
            height = 256;
        }
        return { width: width, height: height };
    },



    // creates a div node with id divId.
    // If a node with the given id existed beforehand, it is replaced by the newly created one.
    // Otherwise, the newly created div node is inserted before siblingNode
    // The created div gets width width and height height (each in Pixels).
    replaceOrAddDiv: function ( divId, siblingNode, width, height )
    {
        var widthIsPercent = ( ( typeof width == "string" ) && ( width.substr( width.length-1, 1 ) == "%" ) );
        var heightIsPercent = ( ( typeof height == "string" ) && ( height.substr( height.length-1, 1 ) == "%" ) );

        if ( ( widthIsPercent ) || ( heightIsPercent ) )
        {
            var viewportDim=at.quelltextlich.osm.getViewPortDimension();

            if ( widthIsPercent )
            {
                width = viewportDim.width * width.substr( 0, width.length-1 ) / 100;
            }

            if ( heightIsPercent )
            {
                height =viewportDim.height * height.substr( 0, height.length-1 ) / 100;
            }
        }

        width = Math.max( width, 170 );
        height = Math.max( height, 170 );

        var divNode = document.createElement('div');
        divNode.setAttribute('id', divId );
        divNode.setAttribute('style', 'width: '+width+'px; height: '+height+'px;');

        var nodeToReplace = document.getElementById( divId );
        if ( nodeToReplace == null )
        {
            siblingNode.parentNode.insertBefore( divNode, siblingNode );
        } else {
            nodeToReplace.parentNode.replaceChild( divNode, nodeToReplace );
        }

        return divNode;
    },



    // Maps real world lat, lon to the coordinate system of the map
    worldToMap: function ( lat, lon )
    {
        var lonLat = new OpenLayers.LonLat( lon, lat );
        lonLat.transform(
            new OpenLayers.Projection("EPSG:4326"),
            new OpenLayers.Projection("EPSG:900913") /* This is map.getProjectionObject() */
        );
        return lonLat;
    },



    // Adds the maps once the OpenLayers JavaScript file is fully loaded
    getText: function ( label )
    {
        var i18n_text=null;
        try {
            i18n_text = at.quelltextlich.osm.vars['i18n'][ at.quelltextlich.osm.vars['i18n']['selectedLanguage'] ][ label ];
        } catch (e) { ; }

        if ( typeof i18n_text == "undefined" )
        {
            try {
                i18n_text = at.quelltextlich.osm.vars['i18n'][ at.quelltextlich.osm.vars['i18n']['defaultLanguage'] ][ label ];
            } catch (e) { ; }
        }

        if ( typeof i18n_text == "undefined" )
        {
            i18n_text = label;
        }
        return i18n_text;
    },



    // Sets up a basic map with controls.
    // Without setting the center.
    // Without markers.
    addRawMap: function ( div, useZoomBar )
    {

        var map = new OpenLayers.Map( div, { controls: [] } );

        var osmLayer = new OpenLayers.Layer.OSM();
        osmLayer.attribution=at.quelltextlich.osm.getText( "osm.Attribution" );
        map.addLayer( osmLayer );

        map.addControl(new OpenLayers.Control.Navigation());
        map.addControl(new OpenLayers.Control.KeyboardDefaults());
        //map.addControl(new OpenLayers.Control.LayerSwitcher());
        map.addControl(new OpenLayers.Control.ScaleLine());
        if ( useZoomBar )
        {
            map.addControl(new OpenLayers.Control.PanZoomBar());
        } else {
            map.addControl(new OpenLayers.Control.PanZoom());
        }
        map.addControl(new OpenLayers.Control.Attribution());

        // This fixup allows Opera to show KML Layers in a viewer that is
        // included via an <object> element.
        var containerDiv = document.getElementById(map.id+"_OpenLayers_Container");
        containerDiv.style.width="100%";
        containerDiv.style.height="100%";

        return map;
    },



    // Adds a marker (specified by conf) to the marking layer layer of the map map
    // The layer need not have been added to the map.
    addMarking: function ( layer, conf ) {
        var lon = 0;
        var lat = 0;
        if ( conf )
        {
            if ( conf.lon ) { lon = conf.lon; }
            if ( conf.lat ) { lat = conf.lat; }
        }
        var marker=at.quelltextlich.osm.createMarker( lat, lon );
        layer.addMarker( marker );
        return marker;
    },



    // Adds a marker (specified by conf) to the marking layer layer of the map map
    // The layer need not have been added to the map.
    getMapId: function ( map ) {
        // cut away �쐎sm_div_" from the div the map is rendered to
        return map.div.id.substr(8);
    },



    // Destroys a created popup for a given feature
    destroyPopup: function (feature) {
        feature.popup.destroy();
        feature.popup = null;
    },



    // Creates a popup for a given feature
    createPopup: function (feature) {
        if ( ( typeof feature != "undefined" ) && ( typeof feature.attributes != "undefined" ) && ( typeof feature.attributes.description != "undefined" ) )
        {
            var popupDescription = '';
            if ( typeof feature.attributes.name != "undefined" ) {
                popupDescription += '<div style="font-weight: bold">' + feature.attributes.name + '</div>';
            }
            if ( ( typeof feature.attributes.description == "string" ) && ( feature.attributes.description.replace(/\s/g, '') != '' ) ) {
                if ( popupDescription != '' ) {
                    popupDescription += '<hr/>';
                }
                popupDescription += '<div>' + feature.attributes.description + '</div>';
            }
            feature.popup = new OpenLayers.Popup.FramedCloud("pop",
                feature.geometry.getBounds().getCenterLonLat(),
                null,
                popupDescription,
                null,
                true,
                function() { at.quelltextlich.osm.destroyPopup( feature ); }
            );
            feature.layer.map.addPopup(feature.popup);
        }
    },



    // Collecting layer bounds, after a kml layer has been fully loaded
    kmlLayerLoadEnded: function ( eventObj )
    {
        if ( ( typeof eventObj != "undefined" ) && ( typeof eventObj.object != "undefined" ) && ( typeof eventObj.object.features != "undefined" ) )
        {
            var features = eventObj.object.features;
            if ( ( features.length > 0 ) && ( typeof eventObj.object.map != "undefined" ) && ( typeof eventObj.object.map != "undefined" ) )
            {
                var map = eventObj.object.map;
                var mapId = at.quelltextlich.osm.getMapId( map );
                var conf = at.quelltextlich.osm.vars['map'+mapId+'Conf'];
                var mergedLayersBounds=at.quelltextlich.osm.vars['mergedLayersBounds_'+mapId];

                var i;
                for ( i in features )
                {
                    var feature = features[i];
                    if ( ( typeof feature != "undefined" ) && ( feature ) &&
                        ( typeof feature.geometry != "undefined" ) && ( feature.geometry ) &&
                        ( typeof feature.geometry.bounds != "undefined" )  && ( feature.geometry.bounds ) )
                    {
                        mergedLayersBounds.extend( feature.geometry.bounds );
                    }

                }

                if ( typeof  conf.initialPosition == "undefined" )
                {
                    map.zoomToExtent( mergedLayersBounds );

                    // ZoomIn+ZoomOut is Workaround for Opera
                    map.zoomIn();
                    map.zoomOut();
                }
            }
        }
    },



    // Generates and adds a KML Layer based on the configuration
    // whose url has been successfully cached on the server
    addFullyCachedKMLLayer: function ( map, conf )
    {
        var attribution="<br>"+at.quelltextlich.osm.getText('layer.kml.attribution.preLink');
        attribution+="<a href=\""+at.quelltextlich.osm.encodeAsXml( conf.attribution.url )+"\">"+ at.quelltextlich.osm.encodeAsXml( conf.attribution.text ) +"</a>";
        if ( ( conf.license.url ) || ( conf.license.name ) ) {
            var addendum=at.quelltextlich.osm.getText('layer.kml.attribution.license');
            if ( conf.license.name ) {
                addendum=at.quelltextlich.osm.encodeAsXml( conf.license.name );
            }
            if ( conf.license.url ) {
                addendum="<a href=\""+at.quelltextlich.osm.encodeAsXml( conf.license.url )+"\">"+ addendum +"</a>";
            }
            attribution+=" ("+addendum+")";
        }
        attribution+=at.quelltextlich.osm.getText('layer.kml.attribution.postLink');

        var layer = new OpenLayers.Layer.Vector( "kml_"+conf.id, {
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: conf.url.cache,
                format: new OpenLayers.Format.KML({
                    extractStyles: true,
                    extractAttributes: true,
                    maxDepth: 4
                })
            }),
            eventListeners: {
                loadend: at.quelltextlich.osm.kmlLayerLoadEnded
            },
            attribution: attribution
        });

        map.addLayer( layer );

        var selectorControl = new OpenLayers.Control.SelectFeature( layer, {
            onSelect: at.quelltextlich.osm.createPopup,
            onUnselect: at.quelltextlich.osm.destroyPopup
        });
        map.addControl(selectorControl);
        selectorControl.activate();
    },



    // Adds the cache information to KML Configuration and adds the layer
    KMLFileCached: function( httpObj, params )
    {
        var errorCode=0;
        var errorMessage="";
        if ( httpObj.status == 200 )
        {
            var result;
            try {
                result = eval( "(" + httpObj.responseText + ")" );
            } catch ( e ) {
                errorCode=-2;
                errorMessage="Error interpreting the result of the caching service";
            }
            if ( ( typeof result != "undefined" ) && ( typeof result.errorCode != "undefined" ) )
            {
                errorCode=result.errorCode;
                errorMessage=result.errorMessage;
                if ( ( result.errorCode == 0 ) && ( typeof result.cacheUrl != "undefined" ) )
                {
                    params.conf.url.cache=result.cacheUrl;
                    at.quelltextlich.osm.addFullyCachedKMLLayer( params.map, params.conf );
                }
            }
        } else {
            errorCode = -1;
            errorMessage = "HTTP Error while communicating with cache service ("+httpObj.status+" - "+httpObj.statusText+")"
        }
        if ( errorCode != 0 )
        {
            if ( errorMessage )
            {
                alert( "File caching error:\n"+errorMessage+" (Error code: "+errorCode+")" );
            } else {
                alert( "Undefined file caching error" );
            }
        }
    },



    // Caches a URL on the Server
    cacheUrlOnServer: function( url, callback, params )
    {
        var httpObj = null;
        try {
            httpObj = new XMLHttpRequest();
        } catch(e) {
            try {
                httpObj  = new ActiveXObject("Microsoft.XMLHTTP");
            } catch(e) {
                try {
                    httpObj  = new ActiveXObject("Msxml2.XMLHTTP");
                } catch(e) {
                    httpObj  = null;
                }
            }
        }
        if (httpObj) {
            var cachingUrl=at.quelltextlich.osm.vars['const']['url_caching']+encodeURIComponent( url );
            httpObj.open('GET', cachingUrl, true);
            httpObj.onreadystatechange = function () {
                if (httpObj.readyState == 4) {
                    callback( httpObj, params );
                }
            };
            httpObj.send(null);
        }
    },



    // Adds a map, along with markers, ... and centers it
    addMap: function ( mapId )
    {
        var conf = at.quelltextlich.osm.vars['map'+mapId+'Conf'];

        var initialLat=48.8567;
        var initialLon=2.3517;
        var initialZoom=11;

        var divId="osm_div_"+mapId;

        // Dimension der Karte setzen
        var mapWidth=0;
        var mapHeight=0;
        if ( conf.dimension )
        {
            if ( conf.dimension.width ) { mapWidth=conf.dimension.width; }
            if ( conf.dimension.height ) { mapHeight=conf.dimension.height; }
        }
        var mapDiv = at.quelltextlich.osm.replaceOrAddDiv( divId, at.quelltextlich.osm.vars['map'+mapId+'OriginatingNode'], mapWidth, mapHeight );

        // Karte initialisieren
        var map = at.quelltextlich.osm.addRawMap( divId, ( mapHeight >= 370 ) );

        mapDiv.className = mapDiv.className+" "+divId;

        if ( ! at.quelltextlich.osm.vars['CSSAdded'] )
        {
            at.quelltextlich.osm.vars['CSSAdded']=true;
            at.quelltextlich.osm.addStyleSheet( at.quelltextlich.osm.HOMEPAGE + 'css/map.css' );
        }

        // Marker setzen
        var markerLayer;
        if ( conf.marker )
        {
            markerLayer = new OpenLayers.Layer.Markers( "Markers" );

            var markerConfId;
            for ( markerConfId in conf.marker )
            {
                var markerConf=conf.marker[markerConfId];
                at.quelltextlich.osm.addMarking( markerLayer, markerConf );
            }

            map.addLayer(markerLayer);
        }

        if ( typeof  conf.kmls != "undefined" )
        {
            at.quelltextlich.osm.vars['mergedLayersBounds_'+mapId] = new OpenLayers.Bounds();
            for ( kmlConfId in conf.kmls )
            {
                var kmlConf=conf.kmls[kmlConfId];

                kmlConf.id=kmlConfId;

                at.quelltextlich.osm.cacheUrlOnServer( kmlConf.url.real, at.quelltextlich.osm.KMLFileCached, { conf: kmlConf, map: map } );

            } //done adding a single kml layer

        } //done adding all kml layers

        // Ausgangsposition aus Konfiguration setzen
        if ( typeof  conf.initialPosition != "undefined" )
        {
            if ( conf.initialPosition.lon ) { initialLon=conf.initialPosition.lon; }
            if ( conf.initialPosition.lat ) { initialLat=conf.initialPosition.lat; }
            if ( conf.initialPosition.zoom ) { initialZoom=conf.initialPosition.zoom; }
        }
        map.setCenter( at.quelltextlich.osm.worldToMap( initialLat, initialLon ), initialZoom );

        try {
            conf.callbacks.mapAdded( mapId, map );
        } catch (e) { ; }
    },



    // Adds the maps once the OpenLayers JavaScript file is fully loaded
    loaded: function ()
    {
        if (! at.quelltextlich.osm.vars['OpenLayersFullyLoaded'])
        {
            at.quelltextlich.osm.vars['OpenLayersFullyLoaded']=true;

            // patch up the OpenLayers implementation towards Openstreetmap
            var origOnImageLoadErrorFunc = OpenLayers.Util.onImageLoadError
            OpenLayers.Util.onImageLoadError = function () {
                if ( /^http:\/\/tile\.openstreetmap\.org\/-?[0-9]+\/-?[0-9]+\/-?[0-9]+\.png$/.test(this.src) )
                {
                    this.src="http://www.openstreetmap.org/openlayers/img/404.png";
                } else {
                    origOnImageLoadErrorFunc();
                }
            }

            // add the prepared maps to the visible map
            var mapCount=at.quelltextlich.osm.vars['mapCount'];
            if ( mapCount )
            {
                var mapId;
                for ( mapId = 1; mapId <= mapCount; mapId++ )
                {
                    at.quelltextlich.osm.addMap( mapId );
                }
            }
        }
    },



    // Initializes the Internationalization for english
    addI18nString: function ( lang, label, translation ) {
        at.quelltextlich.osm.vars['i18n'][lang][label] = translation;
    },



    // Initializes the Internationalization for english
    initI18nEn: function () {
        var lang='en';
        at.quelltextlich.osm.vars['i18n'][lang] = new Object();
        addI18nString = at.quelltextlich.osm.addI18nString;

        addI18nString( lang, 'product.name', 'OSMTools Viewer' );
        addI18nString( lang, 'osm.Attribution', 'map by the <a href="'+at.quelltextlich.osm.vars['const']['url_osm']+'">OpenStreetMap</a> contributors (<a href="'+at.quelltextlich.osm.vars['const']['url_cc_by_sa_2_0']+'">CC-BY-SA 2.0</a>)' );
        addI18nString( lang, 'layer.kml.attribution.license', 'license' );
        addI18nString( lang, 'layer.kml.attribution.preLink', 'markings by ' );
        addI18nString( lang, 'layer.kml.attribution.postLink', '' );
        addI18nString( lang, 'ui.label.close', 'Close' );
        addI18nString( lang, 'ui.label.webservice', 'Webservice' );
        addI18nString( lang, 'ui.label.version', 'Version' );
        addI18nString( lang, 'ui.label.homepage', 'Homepage' );
        addI18nString( lang, 'ui.label.legal_info', 'Legal information' );
        addI18nString( lang, 'ui.label.map', 'Map' );
        addI18nString( lang, 'ui.label.attribution', 'Attribution' );
        addI18nString( lang, 'ui.label.kml_overlays', 'KML Overlays' );
        addI18nString( lang, 'ui.label.kml_overlay_nr', 'KML Overlay' );
        addI18nString( lang, 'ui.label.kml_overlay.attribution', 'Attribution' );
        addI18nString( lang, 'ui.label.kml_overlay.license', 'License' );
        addI18nString( lang, 'ui.label.back_to_map', 'Return to map' );
//	addI18nString( lang, '', '' );
    },



    // Initializes the Internationalization for german
    initI18nDe: function initI18nDe() {
        var lang='de';
        at.quelltextlich.osm.vars['i18n'][lang] = new Object();
        addI18nString = at.quelltextlich.osm.addI18nString;

        addI18nString( lang, 'product.name', 'OSMTools Betrachter' );
        addI18nString( lang, 'osm.Attribution', 'Karte von den <a href="'+at.quelltextlich.osm.vars['const']['url_osm']+'">OpenStreetMap</a> Mitwirkenden (<a href="'+at.quelltextlich.osm.vars['const']['url_cc_by_sa_2_0']+'">CC-BY-SA 2.0</a>)' );
        addI18nString( lang, 'layer.kml.attribution.license', 'Lizenz' );
        addI18nString( lang, 'layer.kml.attribution.preLink', 'Markierungen von ' );
        addI18nString( lang, 'layer.kml.attribution.postLink', '' );
        addI18nString( lang, 'ui.label.close', 'Schlie횩en' );
        addI18nString( lang, 'ui.label.webservice', 'Webdienst' );
        addI18nString( lang, 'ui.label.version', 'Version' );
        addI18nString( lang, 'ui.label.homepage', 'Webseite' );
        addI18nString( lang, 'ui.label.legal_info', 'Impressum' );
        addI18nString( lang, 'ui.label.map', 'Karte' );
        addI18nString( lang, 'ui.label.attribution', 'Quelle' );
        addI18nString( lang, 'ui.label.kml_overlays', 'KML Ebenen' );
        addI18nString( lang, 'ui.label.kml_overlay_nr', 'KML Ebene' );
        addI18nString( lang, 'ui.label.kml_overlay.attribution', 'Attributierung' );
        addI18nString( lang, 'ui.label.kml_overlay.license', 'Lizenz' );
        addI18nString( lang, 'ui.label.back_to_map', 'Zur체ck zur Karte' );
//	addI18nString( lang, '', '' );
    },



    // Initializes the Internationalization for esperanto
    initI18nEo: function () {
        var lang='eo';
        at.quelltextlich.osm.vars['i18n'][lang] = new Object();
        addI18nString = at.quelltextlich.osm.addI18nString;

        addI18nString( lang, 'product.name', 'OSMTools Rigardilo' );
        addI18nString( lang, 'osm.Attribution', 'mapo de la <a href="'+at.quelltextlich.osm.vars['const']['url_osm']+'">OpenStreetMap</a> kontribuantoj (<a href="'+at.quelltextlich.osm.vars['const']['url_cc_by_sa_2_0']+'">CC-BY-SA 2.0</a>)' );
        addI18nString( lang, 'layer.kml.attribution.license', 'permesilo' );
        addI18nString( lang, 'layer.kml.attribution.preLink', 'etikedoj de ' );
        addI18nString( lang, 'layer.kml.attribution.postLink', '' );
        addI18nString( lang, 'ui.label.close', 'Fermu' );
        addI18nString( lang, 'ui.label.webservice', 'Retservo' );
        addI18nString( lang, 'ui.label.version', 'Version' );
        addI18nString( lang, 'ui.label.homepage', 'TTT-ejo' );
        addI18nString( lang, 'ui.label.legal_info', 'Le휐a respondeco' );
        addI18nString( lang, 'ui.label.map', 'Mapo' );
        addI18nString( lang, 'ui.label.attribution', 'Fonto' );
        addI18nString( lang, 'ui.label.kml_overlays', 'KML tegoj' );
        addI18nString( lang, 'ui.label.kml_overlay_nr', 'KML tego' );
        addI18nString( lang, 'ui.label.kml_overlay.attribution', 'Atribuo' );
        addI18nString( lang, 'ui.label.kml_overlay.license', 'Permesilo' );
        addI18nString( lang, 'ui.label.back_to_map', 'Reiru al la mapo' );
//	addI18nString( lang, '', '' );
    },



    // Initializes the global setting and variables
    init: function ()
    {
        at.quelltextlich.osm.vars=new Object();
        at.quelltextlich.osm.vars['preparedMaps']=new Object();
        at.quelltextlich.osm.vars['OpenLayersFullyLoaded']=false;
        at.quelltextlich.osm.vars['CSSAdded']=false;
        at.quelltextlich.osm.vars['mapCount']=0;

        at.quelltextlich.osm.vars['const']=new Object();
        at.quelltextlich.osm.vars['const']['url_osm']="http://www.openstreetmap.org/";
        at.quelltextlich.osm.vars['const']['url_cc_by_2_0']="http://creativecommons.org/licenses/by/2.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_3_0']="http://creativecommons.org/licenses/by/3.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nc_2_0']="http://creativecommons.org/licenses/by-nc/2.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nc_3_0']="http://creativecommons.org/licenses/by-nc/3.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nc_nd_2_0']="http://creativecommons.org/licenses/by-nc-nd/2.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nc_nd_3_0']="http://creativecommons.org/licenses/by-nc-nd/3.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nc_sa_2_0']="http://creativecommons.org/licenses/by-nc-sa/2.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nc_sa_3_0']="http://creativecommons.org/licenses/by-nc-sa/3.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nd_2_0']="http://creativecommons.org/licenses/by-nd/2.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_nd_3_0']="http://creativecommons.org/licenses/by-nd/3.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_sa_2_0']="http://creativecommons.org/licenses/by-sa/2.0/";
        at.quelltextlich.osm.vars['const']['url_cc_by_sa_3_0']="http://creativecommons.org/licenses/by-sa/3.0/";
        at.quelltextlich.osm.vars['const']['url_cc0_1_0']="http://creativecommons.org/publicdomain/zero/1.0/";
        at.quelltextlich.osm.vars['const']['url_caching']=at.quelltextlich.osm.HOMEPAGE + "cache.json?url=";

        at.quelltextlich.osm.vars['i18n']=new Object();
        at.quelltextlich.osm.vars['i18n']['defaultLanguage']='en';
        at.quelltextlich.osm.initI18nEn();
        at.quelltextlich.osm.initI18nDe();
        at.quelltextlich.osm.initI18nEo();

        at.quelltextlich.osm.addJavaScript( at.quelltextlich.osm.loaded );

        if ( ! at.quelltextlich.osm.vars['i18n']['selectedLanguage'] )
        {
            try {
                at.quelltextlich.osm.vars['i18n']['selectedLanguage']=document.getElementsByTagName( 'html' )[0].lang;
            } catch (e) { ; }
        }

    },



    // Gathers the required information, for setting up the map, once all
    // JavaScript has been loaded
    prepareMap: function ( conf )
    {
        var mapId = at.quelltextlich.osm.vars['mapCount'];
        if ( mapId )
        {
            mapId++;
        } else {
            mapId=1;
        }

        at.quelltextlich.osm.vars['mapCount']=mapId;
        at.quelltextlich.osm.vars['map'+mapId+'Conf']=conf;


        var scriptNodes = document.getElementsByTagName( 'script' );
        var thisScriptNode = scriptNodes[ scriptNodes.length - 1 ];

        // Workaround for Opera Mobile, whose getElementsByTagName yields a too
        // short result for the first invocation. In this case, thisScriptNode is
        // one of the script tags from the <head> tag.
        if (thisScriptNode.parentNode.tagName.toUpperCase() == "HEAD")
        {
            // getElementsByTagName gave a script tag from within the head tag.
            // This behaviour occurs on Opera Mobile for the first call to
            // add a map. There, scriptNodes does /not/ contain all script
            // tags, although they occur in documents.all. Hence we iterating
            // manually, and thereby try to obtain the correct value for
            // thisScriptNode.

            var lastScriptNode = null;
            var i;
            for ( i = 0 ; i < document.all.length ; i++ )
            {
                node=document.all[i];
                if ( node.tagName.toUpperCase() == "SCRIPT" )
                {
                    lastScriptNode = node;
                }
            }
            if ( lastScriptNode.parentNode.tagName.toUpperCase() != "HEAD" )
            {
                thisScriptNode=lastScriptNode;
            }
        }
        at.quelltextlich.osm.vars['map'+mapId+'OriginatingNode']=thisScriptNode;

        // if the OpenLayers Javascript file has been fully loaded, we directly
        // start the actual addition of the map. Otherwise, the actual addition
        // is carried out in at.quelltextlich.osm.loaded.
        if ( at.quelltextlich.osm.vars['OpenLayersFullyLoaded'] )
        {
            at.quelltextlich.osm.addMap( mapId );
        }

    },



    /*
     ********************************************************************
     *
     * Published functions
     *
     ********************************************************************
     */



    // Adds a map specified by a at.quelltextlich.osm.MapConfiguration object
    embedMapPreconfigured: function ( conf )
    {
        at.quelltextlich.osm.prepareMap( conf.conf );
    },



    // Adds a map with a marker at a given position
    embedMapMarkedLocation: function ( lat, lon, zoom, width, height )
    {
        var conf = new at.quelltextlich.osm.MapConfiguration();

        conf.setMapDimension( width, height );
        conf.setInitialPosition( lat, lon, zoom );
        conf.addMarker( lat, lon );

        at.quelltextlich.osm.embedMapPreconfigured( conf );
    },



    // Adds a map with a KML Layer
    embedMapKML: function ( kmlUrl, width, height, kmlAttributionUrl, kmlAttribution, kmlLicenseName, kmlLicenseUrl )
    {
        var conf = new at.quelltextlich.osm.MapConfiguration();

        conf.setMapDimension( width, height );
        conf.addKML( kmlUrl, kmlAttributionUrl, kmlAttribution, kmlLicenseName, kmlLicenseUrl );

        at.quelltextlich.osm.embedMapPreconfigured( conf );
    },



    // Adds a plain map
    embedMapPlain: function ( lat, lon, zoom, width, height )
    {
        var conf = new at.quelltextlich.osm.MapConfiguration();

        conf.setMapDimension( width, height );
        conf.setInitialPosition( lat, lon, zoom );

        at.quelltextlich.osm.embedMapPreconfigured( conf );
    },



    // Creates a new marker, that can be added to marking layers
    createMarker: function ( lat, lon ) {
        var size = new OpenLayers.Size(17,31);
        var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
        var icon = new OpenLayers.Icon( at.quelltextlich.osm.HOMEPAGE + "pics/red.png", size, offset );

        return new OpenLayers.Marker( at.quelltextlich.osm.worldToMap( lat, lon ), icon);
    }



};

// Starting the initialization
at.quelltextlich.osm.init();

/*
 ********************************************************************
 *
 * MapConfiguration
 *
 ********************************************************************
 */

at.quelltextlich.osm.MapConfiguration=function () {

    // The Object accumulation all the configuration
    this.conf=new Object();

    /*
     ********************************************************************
     *
     * Published functions
     *
     ********************************************************************
     */

    // Sets the map's on-screen width and height
    this.setMapDimension=function ( width, height ) {
        if ( typeof this.conf.dimension == "undefined" ) { this.conf.dimension=new Object(); }
        this.conf.dimension.width = width;
        this.conf.dimension.height = height;
    };

    // Sets the default on-screet center of the map
    this.setInitialPosition=function ( lat, lon, zoom ) {
        if ( typeof this.conf.initialPosition == "undefined" ) { this.conf.initialPosition=new Object(); }
        this.conf.initialPosition.lat = lat;
        this.conf.initialPosition.lon = lon;
        this.conf.initialPosition.zoom = zoom;
    };

    // Adds a marker at the given position
    this.addMarker=function ( lat, lon ) {
        if ( typeof this.conf.marker == "undefined" ) { this.conf.marker=new Array(); }
        this.conf.marker.push( { lat: lat, lon: lon } );
    };

    // Adds a KML file to the map
    this.addKML=function ( url, attribution_url, attribution_text, license_name, license_url ) {
        if ( typeof this.conf.kmls == "undefined" ) { this.conf.kmls=new Array(); }

        if ( typeof attribution_url == "undefined" ) {
            attribution_url=url.replace(/^(http:\/\/[^\/]*\/).*$/,"\$1");
        }

        if ( typeof attribution_text == "undefined" ) {
            attribution_text=attribution_url;
        }

        if ( typeof license_name == "undefined" ) {
            license_name='';
        }

        if ( typeof license_url == "undefined" ) {
            license_key="url_"+license_name.replace(/[^a-zA-Z0-9]/g, "_").toLocaleLowerCase();
            if ( typeof at.quelltextlich.osm.vars['const'][license_key] == "undefined" ) {
                license_url='';
            } else {
                license_url=at.quelltextlich.osm.vars['const'][license_key];
            }
        }

        this.conf.kmls.push( {
            url: { real: url },
            attribution: { url: attribution_url, text: attribution_text },
            license: { name: license_name, url: license_url }
        } );
    };

    // Adds a KML file to the map
    this.setCallbackMapAdded=function ( func ) {
        if ( typeof this.conf.callbacks == "undefined" ) { this.conf.callbacks=new Object(); }
        this.conf.callbacks.mapAdded = func;
    };

};