/*
 * Copyright (C) 2010 Paul Watts (paulcwatts@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.io.client.request;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import org.onebusaway.location.Location;

/**
 * Retrieve info about a specific route
 * {@link http://code.google.com/p/onebusaway/wiki/OneBusAwayRestApi_Route}
 *
 * @author Paul Watts (paulcwatts@gmail.com)
 */
public final class ObaRoutesForLocationRequest extends RequestBase
        implements Callable<ObaRoutesForLocationResponse> {

    protected ObaRoutesForLocationRequest(URI uri) {
        super(uri);
    }

    public static class Builder extends RequestBase.BuilderBase {

        public Builder(Location location) {
            super(BASE_PATH + "/routes-for-location.json");
            mBuilder.queryParam("lat", String.valueOf(location.getLatitude()));
            mBuilder.queryParam("lon", String.valueOf(location.getLongitude()));
        }

        /**
         * Sets the optional search radius.
         *
         * @param radius The search radius, in meters.
         */
        public Builder setRadius(int radius) {
            mBuilder.queryParam("radius", String.valueOf(radius));
            return this;
        }

        /**
         * An alternative to {@link #setRadius(int)} to set the search bounding box
         *
         * @param latSpan The latitude span of the bounding box.
         * @param lonSpan The longitude span of the bounding box.
         */
        public Builder setSpan(double latSpan, double lonSpan) {
            mBuilder.queryParam("latSpan", String.valueOf(latSpan));
            mBuilder.queryParam("lonSpan", String.valueOf(lonSpan));
            return this;
        }

        /**
         * An alternative to {@link #setRadius(int)} to set the search bounding box
         *
         * @param latSpan The latitude span of the bounding box in microdegrees.
         * @param lonSpan The longitude span of the bounding box in microdegrees.
         */
        public Builder setSpan(int latSpan, int lonSpan) {
            mBuilder.queryParam("latSpan", String.valueOf(latSpan / 1E6));
            mBuilder.queryParam("lonSpan", String.valueOf(lonSpan / 1E6));
            return this;
        }

        /**
         * A specific route short name to search for.
         *
         * @param query The short name query string.
         */
        public Builder setQuery(String query) {
            mBuilder.queryParam("query", query);
            return this;
        }

        public ObaRoutesForLocationRequest build() throws URISyntaxException {
            return new ObaRoutesForLocationRequest(buildUri());
        }
    }

    @Override
    public ObaRoutesForLocationResponse call() {
        return call(ObaRoutesForLocationResponse.class);
    }

    @Override
    public String toString() {
        return "ObaRoutesForLocationRequest [mUri=" + mUri + "]";
    }
}